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
 * Created by IntelliJ IDEA.
 * User: john
 * Date: Apr 7, 2005
 * Time: 6:36:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class ControlPanel extends JPanel {
    private LargeCardDisplay<Card> lcd;
    private ScorePanel score;
    private SmallCardsDisplay<Card> hand;
    private PlayerModel[] players;

    ControlPanel(PlayerModel[] players, HumanPlayer human) {
        super(new BorderLayout(Constants.GAP, Constants.GAP));
        this.players = players;
        setOpaque(true);
        setBackground(Colours.SILVER);
        add(BorderLayout.EAST, lcd = new LargeCardDisplay<Card>(true));
        lcd.setBackground(getBackground());
        add(BorderLayout.WEST, score = new ScorePanel(players));
        score.setBackground(getBackground());
        add(BorderLayout.CENTER, hand = new SmallCardsDisplay<Card>(2, 6, false));
        setHumanPlayer(human);
        hand.setBackground(getBackground());
        hand.setBorder(Constants.EMPTY_BORDER);
        hand.addCardOverListener(new CardListener() {
            public void cardChanged(Displayable card) {
                lcd.setCard((Card) card);
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("e = " + e);
            }
        });
    }

    LargeCardDisplay<Card> getLargeCardDisplay() {
        return lcd;
    }

    public void refresh() {
        score.refresh();
    }

    void setHumanPlayer(HumanPlayer player) {
        if (player == null) {
            hand.setCards(null);
        } else {
            hand.setCards(player.getHand());
        }
    }
}
