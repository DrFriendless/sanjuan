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
public class Deck {
    private static final String[] NAMES = new String[]{
        "Aqueduct", "Archive", "Black Market", "Carpenter", "Chapel", "City Hall", "Coffee Roaster",
        "Crane", "Gold Mine", "Guild Hall", "Hero", "Indigo Plant", "Library", "Market Hall",
        "Market Stand", "Palace", "Poor House", "Prefecture", "Quarry", "Silver Smelter", "Smithy",
        "Statue", "Sugar Mill", "Tobacco Storage", "Tower", "Trading Post", "Triumphal Arch",
        "Victory Column", "Well"
    };
    private static final int[] VICTORY_POINTS = new int[]{
        2, 1, 1, 2, 2, 0, 2, 1, 1, 0, 5, 1, 3, 2, 1, 0, 1, 2, 2, 3, 1, 3, 2, 2, 2, 1, 0, 4, 1
    };
    private static final int[] COST = new int[]{
        3, 1, 2, 3, 3, 6, 4, 2, 1, 6, 5, 1, 5, 4, 2, 6, 2, 3, 4, 5, 1, 3, 2, 3, 3, 2, 6, 4, 2
    };
    private static final int[] PRODUCTION = new int[]{
        0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 2, 3, 0, 0, 0, 0, 0
    };
    private static final int[] MONUMENT = new int[]{
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0
    };
    private static final int[] COUNT = new int[]{
        3, 3, 3, 3, 3, 2, 8, 3, 3, 2, 3, 10, 3, 4, 3, 2, 3, 3, 2, 8, 3, 3, 8, 8, 3, 3, 2, 3, 3
    };
    private static final Random rng = new Random();

    private Cards<Card> deck;
    private Cards<Card> discardPile;

    private static String getKey(String name) {
        String s = name.replace(" ", "");
        s = s.substring(0, 1).toLowerCase() + s.substring(1);
        return s;
    }

    Deck() {
        deck = new Cards<Card>();
        discardPile = new Cards<Card>();
        for (int i = 0; i < PRODUCTION.length; i++) {
            for (int j=0; j<COUNT[i]; j++) {
                deck.add(new Card(NAMES[i], getKey(NAMES[i]), VICTORY_POINTS[i], COST[i], PRODUCTION[i], MONUMENT[i] > 0));
            }
        }
    }

    public Card drawTop() {
        if (deck.size() == 0) {
            deck = discardPile;
            discardPile = new Cards<Card>();
            shuffle();
        }
        return deck.removeFirst();
    }

    public Card find(String name) {
        return deck.remove(name);
    }

    public Cards<Card> draw(int count) {
        Cards<Card> drawn = new Cards<Card>();
        for (int i=0; i<count; i++) {
            drawn.add(drawTop());
        }
        return drawn;
    }

    public void discard(Card chosen) {
        if (chosen == null) throw new IllegalArgumentException();
        discardPile.add(chosen);
    }

    public void discard(Cards<Card> chosen) {
        if (chosen.contains(null)) throw new IllegalArgumentException(chosen.toString());
        discardPile.add(chosen);
    }

    public void shuffle() {
        Cards<Card> shuffled = new Cards<Card>();
        List<Card> cards = deck.getCards();
        while (cards.size() > 0) {
            shuffled.add((Card) cards.remove(rng.nextInt(cards.size())));
        }
        deck = shuffled;
    }

    public int size() {
        return deck.size() + discardPile.size();
    }
}
