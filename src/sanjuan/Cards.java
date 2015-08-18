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

import java.util.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class Cards <E extends Card> {
    private static Random rng = new Random();
    private List<E> cards = new ArrayList<E>();
    private Vector<CardsListener> listeners = new Vector<CardsListener>();

    Cards() {
    }

    Cards(Cards<E> copy) {
        add(copy);
    }

    Cards(List<E> cards) {
        this.cards.addAll(cards);
    }

    void add(E card) {
        assert card != null;
        cards.add(card);
        fireListeners();
    }

    void add(Cards<E> cards) {
        assert cards != null;
        this.cards.addAll(cards.cards);
        fireListeners();
    }

    void replace(int i, E card) {
        if (i + 1 >= cards.size()) {
            if (card == null) return;
            while (cards.size() <= i) cards.add(null);
        }
        cards.set(i, card);
        fireListeners();
    }

    List<E> getCards() {
        return new ArrayList<E>(cards);
    }

    void addCardListener(CardsListener listener) {
        listeners.add(listener);
    }

    void removeCardListener(CardsListener listener) {
        listeners.remove(listener);
    }

    private void fireListeners() {
        List<CardsListener> copy;
        synchronized (listeners) {
            copy = new ArrayList<CardsListener>(listeners);
        }
        for (CardsListener cardsListener : copy) {
            cardsListener.cardsChanged();
        }
    }

    public boolean has(String cardName) {
        for (E card : cards) {
            if (card == null) continue;
            if (card.getName().equals(cardName)) return true;
        }
        return false;
    }

    public E get(String cardName) {
        for (E card : cards) {
            if (card == null) continue;
            if (card.getName().equals(cardName)) return card;
        }
        return null;
    }

    public E removeFirst() {
        E result = cards.get(0);
        cards.remove(0);
        fireListeners();
        return result;
    }

    public Card remove(String name) {
        Card result = null;
        for (Iterator iterator = cards.iterator(); iterator.hasNext();) {
            Card card = (Card) iterator.next();
            if (card.getName().equals(name)) {
                result = card;
                iterator.remove();
                fireListeners();
                break;
            }
        }
        return result;
    }

    public boolean remove(E chosen) {
        for (Iterator<E> iterator = cards.iterator(); iterator.hasNext();) {
            E card = (E) iterator.next();
            if (card == chosen) {
                iterator.remove();
                fireListeners();
                return true;
            }
        }
        return false;
    }

    public void remove(Cards<E> chosen) {
        for (E card : chosen.cards) remove(card);
    }

    public int size() {
        return cards.size();
    }

    public boolean allDistinctCosts() {
        boolean[] costs = new boolean[7];
        for (E card : cards) {
            if (costs[card.getCost()]) return false;
            costs[card.getCost()] = true;
        }
        return true;
    }

    public String toString() {
        return "Cards[" + cards + "]";
    }

    public E chooseRandom() {
        if (cards.size() == 0) return null;
        return cards.get(rng.nextInt(cards.size()));
    }

    public void replace(Cards<E> chosen) {
        cards.clear();
        cards.addAll(chosen.getCards());
        fireListeners();
    }

    public boolean contains(Card card) {
        return cards.contains(card);
    }

    // remove any cards which are the same as this
    public void removeAny(Card toRemove) {
        int removeCount = 0;
        for (Iterator<E> iterator = cards.iterator(); iterator.hasNext();) {
            E card = (E) iterator.next();
            if (card.getName().equals(toRemove.getName())) {
                iterator.remove();
                removeCount++;
            }
        }
        if (removeCount > 0) fireListeners();
    }

    public Cards<E> except(Cards<E> others) {
        Cards<E> cards = new Cards<E>(this);
        cards.remove(others);
        return cards;
    }
}
