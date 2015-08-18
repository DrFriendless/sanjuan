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
 * Created by IntelliJ IDEA.
 * User: john
 * Date: Apr 7, 2005
 * Time: 6:33:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class SanJuanPanel extends JPanel {
    private ControlPanel controlPanel;
    private PlayersPanel playersPanel;

    SanJuanPanel(AbstractPlayer[] players) {
        super(new BorderLayout(Constants.GAP, Constants.GAP));
        int humanCount = 0;
        HumanPlayer human = null;
        setOpaque(true);
        setBackground(Colours.SILVER);
        PlayerModel[] models = new PlayerModel[players.length];
        for (int i = 0; i < players.length; i++) {
            if (players[i] instanceof HumanPlayer) {
                human = (HumanPlayer) players[i];
                humanCount++;
            }
            models[i] = players[i].getModel();
        }
        if (humanCount > 1) human = null;
        controlPanel = new ControlPanel(models, human);
        add(controlPanel, BorderLayout.SOUTH);
        controlPanel.setBorder(Constants.EMPTY_BORDER);
        add(playersPanel = new PlayersPanel(players, controlPanel.getLargeCardDisplay()), BorderLayout.CENTER);
    }

    public void refresh() {
        controlPanel.refresh();
    }

    public void playerChoseRole(AbstractPlayer chooser, Role role) {
        playersPanel.playerChoseRole(chooser, role);
    }

    public void setHumanPlayer(HumanPlayer player) {
        controlPanel.setHumanPlayer(player);
    }
}
