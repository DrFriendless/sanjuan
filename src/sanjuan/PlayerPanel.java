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
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class PlayerPanel extends JPanel {
    private SmallCardsDisplay smallCards;
    private JLabel roleLabel, cardsLabel;

    PlayerPanel(final PlayerModel playerModel, final LargeCardDisplay<Card> lcd) {
        super(new BorderLayout(Constants.GAP, Constants.GAP));
        setBackground(Colours.SILVER);
        Border emptyBorder = Constants.EMPTY_BORDER;
        Border lineBorder = BorderFactory.createLineBorder(Colours.COFFEE);
        setBorder(BorderFactory.createCompoundBorder(emptyBorder, BorderFactory.createCompoundBorder(lineBorder, emptyBorder)));
        JPanel title;
        add(title = new JPanel(new HCodeLayout("f", Constants.GAP)), BorderLayout.NORTH);
        title.setBackground(Colours.SUGAR);
        title.add("", new JLabel(playerModel.getName()));
        title.add("x", new JLabel());
        title.add("", cardsLabel = new JLabel(MessageFormat.format(Translation.inDefaultLanguage("label.cardcount"), playerModel.getHand().size())));
        title.add("x", new JLabel());
        title.add("", roleLabel = new JLabel());
        roleLabel.setForeground(new Color(0, 128, 0));
        Font f = roleLabel.getFont();
        roleLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize()));
        add(smallCards = new SmallCardsDisplay<PlayedCard>(2, 6, false), BorderLayout.CENTER);
        smallCards.setCards(playerModel.getPlayedCards());
        smallCards.setBackground(getBackground());
        repopulate();
        playerModel.getHand().addCardListener(new CardsListener() {
            public void cardsChanged() {
                cardsLabel.setText(MessageFormat.format(Translation.inDefaultLanguage("label.cardcount"), playerModel.getHand().size()));
            }
        });
        playerModel.getPlayedCards().addCardListener(new CardsListener() {
            public void cardsChanged() {
                repopulate();
            }
        });
        smallCards.addCardOverListener(new CardListener() {
            public void cardChanged(Displayable card) {
                lcd.setCard((Card) card);
            }
        });
    }

    private void repopulate() {
        smallCards.repopulate();
    }

    void updateTurn(Role role) {
        roleLabel.setText(role == null ? "" : role.toString());
    }
}
