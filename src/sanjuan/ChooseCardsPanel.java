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
public class ChooseCardsPanel <E extends Card> extends JPanel {
    private JButton ok;
    private LargeCardDisplay<E> detail;
    private SmallCardsDisplay<E> choose;

    public ChooseCardsPanel(Cards<E> toChooseFrom, final int min, final int max, String text, HumanPlayer player) {
        super(new BorderLayout(Constants.GAP, Constants.GAP));
        assert text != null;
        setBorder(Constants.EMPTY_BORDER);
        setOpaque(true);
        setBackground(Colours.COFFEE);
        add(new JLabel(text), BorderLayout.NORTH);
        detail = new LargeCardDisplay<E>(false);
        detail.setCard(toChooseFrom.chooseRandom());
        add(detail, BorderLayout.EAST);
        if (toChooseFrom.size() <= 8) {
            choose = new SmallCardsDisplay<E>(2, 4, true);
        } else if (toChooseFrom.size() <= 12) {
            choose = new SmallCardsDisplay<E>(2, 6, true);
        } else if (toChooseFrom.size() <= 18) {
            choose = new SmallCardsDisplay<E>(3, 6, true);
        } else {
            // this should fix them!
            choose = new SmallCardsDisplay<E>(4, 8, true);
        }
        choose.setCards(toChooseFrom);
        choose.setBackground(getBackground());
        choose.setSingleSelection(min == 1 && max == 1);
        add(choose, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(getBackground());
        ok = new JButton(player.getString("button.choose"));
        buttons.add(ok);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Window window = (Window) getTopLevelAncestor();
                window.dispose();
                synchronized (window) {
                    window.notifyAll();
                }
            }
        });
        setOK(min, max);
        add(buttons, BorderLayout.SOUTH);
        choose.addCardOverListener(detail);
        choose.addSelectListener(new CardListener() {
            public void cardChanged(Displayable card) {
                setOK(min, max);
            }
        });
    }

    private void setOK(final int min, final int max) {
        Cards selected = getSelectedCards();
        ok.setEnabled(selected.size() >= min && selected.size() <= max);
    }

    public Cards<E> getSelectedCards() {
        return choose.getSelectedCards();
    }

    public void setAllSelected() {
        choose.setAllSelected();
    }
}
