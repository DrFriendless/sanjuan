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
public class SalePriceIcon implements Icon {
    private static final Color[] COLOURS = {
        Colours.INDIGO, Colours.SUGAR, Colours.TOBACCO, Colours.COFFEE, Colours.SILVER
    };
    private static final Font FONT = new Font("Serif", Font.BOLD, 20);
    private SalePrice prices;

    SalePriceIcon(SalePrice prices) {
        this.prices = prices;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Colours.TOBACCO);
        g.fillRect(x, y, 40, 168);
        for (int i=0; i<5; i++) {
            int yb = y + 32 * i;
            g.setColor(COLOURS[i]);
            g.fillRect(x+4, yb+4, 32, 32);
            g.setColor(Colours.WRITING);
            g.setFont(FONT);
            g.drawString(Integer.toString(prices.getPrice(i)), x+14, yb+26);
        }
        g.setColor(Color.black);
        g.drawRoundRect(x+4, y+4, 32, 160, 8, 8);
    }

    public int getIconWidth() {
        return 40;
    }

    public int getIconHeight() {
        return 168;
    }
}
