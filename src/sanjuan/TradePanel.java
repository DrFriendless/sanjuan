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
import java.awt.event.*;
import javax.swing.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class TradePanel extends JPanel {
    private PlayerModel model;
    private JButton ok;
    private SmallCardsDisplay<PlayedCard> tradeDisplay;

    public TradePanel(HumanPlayer player, TurnDetails turn, SalePrice salePrice) {
        this.model = player.getModel();
        setBackground(Colours.TOBACCO);
        setLayout(new BorderLayout(Constants.GAP, Constants.GAP));
        setBorder(Constants.EMPTY_BORDER);
        int count = model.getFullProductionBuildings().size();
        int rows = (count + 3)/4;
        final int max = model.getTradeCount(turn);
        tradeDisplay = new SmallCardsDisplay<PlayedCard>(rows, 4, true);
        tradeDisplay.setCards(model.getFullProductionBuildings());
        tradeDisplay.setBackground(getBackground());
        tradeDisplay.addSelectListener(new CardListener() {
            public void cardChanged(Displayable card) {
                ok.setEnabled(tradeDisplay.getSelectedCards().size() <= max);
            }
        });
        final Icon priceIcon = new SalePriceIcon(salePrice);
        JPanel iconPanel = new JPanel() {
            public Dimension getPreferredSize() {
                return new Dimension(priceIcon.getIconWidth(), priceIcon.getIconHeight());
            }

            protected void paintComponent(Graphics g) {
                priceIcon.paintIcon(this, g, 0, 0);
            }
        };
        add(BorderLayout.EAST, iconPanel);
        add(BorderLayout.CENTER, tradeDisplay);
        add(BorderLayout.NORTH, new JLabel(player.format("message.trader", max)));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, Constants.GAP, Constants.GAP));
        buttons.setBackground(getBackground());
        add(BorderLayout.SOUTH, buttons);
        buttons.add(ok = new JButton(player.getString("button.trade")));
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Window window = (Window) getTopLevelAncestor();
                window.dispose();
                synchronized (window) {
                    window.notifyAll();
                }
            }
        });
    }

    public Cards<PlayedCard> getTradeCards() {
        return tradeDisplay.getSelectedCards();
    }
}
