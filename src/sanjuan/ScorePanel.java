// Copyright (C) 2005  John Farrell
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package sanjuan;

import java.awt.*;
import javax.swing.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class ScorePanel extends JPanel {
    private static final Color FRAME_COLOUR = Colours.COFFEE;
    private static final Color WRITING_COLOUR = Colours.WRITING;
    private static final String[] LABELS = {
        "score.name", "score.buildings", "score.chapel", "score.6", "score.subtotal", "score.palace", "score.total"
    };
    private static final int SPACE = 6;
    private static final int COL_WIDTH = 75;
    private FontMetrics metrics;
    private PlayerModel[] playerModels;
    private static Font labels = new Font("Serif", Font.PLAIN, 14);
    private static Font writing = new Font("Serif", Font.ITALIC, 14);

    ScorePanel(PlayerModel[] playerModels) {
        this.playerModels = playerModels;
        metrics = getFontMetrics(labels);
    }

    public Dimension getPreferredSize() {
        Dimension d = LargeCardDisplay.LARGE_DIMENSION;
        return new Dimension(COL_WIDTH * (playerModels.length + 1), d.height);
    }

    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(FRAME_COLOUR);
        g.setFont(labels);
        String title = Translation.inDefaultLanguage("score.title");
        int top = getHeight() / 8;
        int r = getWidth() - 1;
        g.drawRoundRect(0, top, r, getHeight() - 1 - top, 8, 8);
        int w = metrics.stringWidth(title);
        int textUp = (top - getFont().getSize()) / 2;
        g.drawString(title, (getWidth() - w) / 2, top - textUp);
        for (int i=0; i<LABELS.length; i++) {
            int y = (i + 2) * getHeight() / 8;
            if (i != 6) {
                g.drawLine(0, y, r, y);
            }
            g.drawString(Translation.inDefaultLanguage(LABELS[i]), SPACE, y - textUp);
        }
        g.setFont(writing);
        for (int i=0; i<playerModels.length; i++) {
            g.setColor(FRAME_COLOUR);
            g.drawLine(COL_WIDTH * (i + 1), top, COL_WIDTH * (i + 1), getHeight() - 1);
            int bps = playerModels[i].getBuildingPoints();
            int cps = playerModels[i].getChapelPoints();
            int sixps = playerModels[i].getSixPoints();
            int subtotal = bps + cps + sixps;
            int pps = 0;
            if (playerModels[i].hasPalace()) pps = subtotal / 4;
            int total = subtotal + pps;
            String[] values = { playerModels[i].getName(), "" + bps, "" + cps, "" + sixps, "" + subtotal, "" + pps, "" + total };
            for (int j = 0; j < LABELS.length; j++) {
                int y = (j + 2) * getHeight() / 8 - textUp;
                g.setColor(WRITING_COLOUR);
                g.drawString(values[j], COL_WIDTH * (i + 1) + SPACE, y);
            }
        }
    }

    public void refresh() {
        repaint();
    }
}
