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
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class SmallCardsDisplay <E extends Card> extends JPanel {
    private Cards<E> cards;
    private SmallCardDisplay<E>[] displays;
    private Vector<CardListener> overListeners = new Vector<CardListener>();
    private Vector<CardListener> selectListeners = new Vector<CardListener>();
    private boolean selectable;
    private boolean singleSelection;
    private Cards<E> disabled = new Cards<E>();
    private CardsListener listener = new CardsListener() {
        public void cardsChanged() {
            repopulate();
        }
    };

    SmallCardsDisplay(int rows, int columns, boolean selectable) {
        setOpaque(false);
        this.selectable = selectable;
        setDimension(rows, columns);
    }

    void setCards(Cards<E> cards) {
        if (this.cards != null) {
            this.cards.removeCardListener(listener);
        }
        this.cards = cards;
        if (this.cards != null) {
            this.cards.addCardListener(listener);
        }
        repopulate();
    }

    private void mouseOver(SmallCardDisplay<E> smallCardDisplay) {
        E card = smallCardDisplay.getCard();
        if (card != null) fireCardOver(card);
    }

    private void select(SmallCardDisplay<E> smallCardDisplay) {
        if (disabled.contains(smallCardDisplay.getCard())) return;
        if (singleSelection) {
            for (int i = 0; i < displays.length; i++) {
                SmallCardDisplay display = displays[i];
                if (display != smallCardDisplay && display.isSelected()) {
                    display.setSelected(false);
                }
            }
            smallCardDisplay.toggleSelected();
        } else {
            smallCardDisplay.toggleSelected();
        }
        fireSelect(smallCardDisplay.getCard());
    }

    public void setDimension(int rows, int columns) {
        setLayout(new GridLayout(rows, columns));
        removeAll();
        this.displays = (SmallCardDisplay<E>[]) new SmallCardDisplay<?>[rows * columns];
        for (int i = 0; i < displays.length; i++) {
            displays[i] = new SmallCardDisplay<E>(new Integer(i), selectable) {
                public void click() {
                    select(this);
                }

                public void mouseOver() {
                    SmallCardsDisplay.this.mouseOver(this);
                }
            };
            add(displays[i]);
            displays[i].setBackground(getBackground());
        }
        repopulate();
    }

    public void setSingleSelection(boolean singleSelection) {
        this.singleSelection = singleSelection;
    }

    public boolean isSingleSelection() {
        return singleSelection;
    }

    void addCardOverListener(CardListener listener) {
        overListeners.add(listener);
    }

    void removeCardOverListener(CardListener listener) {
        overListeners.remove(listener);
    }

    void addSelectListener(CardListener listener) {
        selectListeners.add(listener);
    }

    void removeSelectListener(CardListener listener) {
        selectListeners.remove(listener);
    }

    private void fireCardOver(E card) {
        List<CardListener> copy;
        synchronized (overListeners) {
            copy = new ArrayList<CardListener>(overListeners);
        }
        for (CardListener cardListener : copy) {
            cardListener.cardChanged(card);
        }
    }

    private void fireSelect(E card) {
        List<CardListener> copy;
        synchronized (selectListeners) {
            copy = new ArrayList<CardListener>(selectListeners);
        }
        for (CardListener cardListener : copy) {
            cardListener.cardChanged(card);
        }
    }

    public void setDisabled(Cards<E> disabled) {
        this.disabled = new Cards<E>(disabled);
        repopulate();
    }

    void repopulate() {
        List<E> cs = (cards == null) ? new ArrayList<E>() : cards.getCards();
        for (int i = 0; i < displays.length; i++) {
            SmallCardDisplay<E> display = displays[i];
            if (i >= cs.size()) {
                display.setCard(null);
            } else {
                E card = cs.get(i);
                display.setCard(card);
                display.setDisabled(disabled.contains(card));
                if (disabled.contains(card)) display.setSelected(false);
            }
        }
    }

    public Cards<E> getSelectedCards() {
        Cards<E> cards = new Cards<E>();
        for (int i = 0; i < displays.length; i++) {
            SmallCardDisplay<E> display = displays[i];
            if (display.getCard() != null && display.isSelected()) cards.add(display.getCard());
        }
        return cards;
    }

    public E getSelectedCard() {
        assert singleSelection;
        for (int i = 0; i < displays.length; i++) {
            SmallCardDisplay<E> display = displays[i];
            if (display.getCard() != null && display.isSelected()) return display.getCard();
        }
        return null;
    }

    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (displays == null) return;
        for (int i = 0; i < displays.length; i++) {
            SmallCardDisplay<E> display = displays[i];
            display.setBackground(bg);
        }
    }

    public void setAllSelected() {
        assert selectable;
        for (int i = 0; i < displays.length; i++) {
            SmallCardDisplay<E> display = displays[i];
            if (display.getCard() != null) display.setSelected(true);
        }
    }
}
