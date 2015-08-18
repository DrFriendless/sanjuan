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
import java.io.*;
import java.util.*;
import java.util.List;
import java.net.URL;
import javax.imageio.ImageIO;


/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class PlayedCard extends Card {
    private Image smallGood;
    private Image largeGood;
    private Card good;
    private List<Card> chapelCards = new ArrayList<Card>();
    public static final Comparator<PlayedCard> GOOD_VALUE_COMPARATOR = new Comparator<PlayedCard>() {
        public int compare(PlayedCard playedCard, PlayedCard playedCard1) {
            int pi1 = playedCard.getProductionIndex();
            int pi2 = playedCard1.getProductionIndex();
            return pi2 - pi1;
        }
    };

    PlayedCard(Card card) {
        super(card.getName(), card.getKey(), card.getVPs(), card.getCost(), card.getProductionIndex() + 1, card.isMonument());
        try {
            smallGood = ImageIO.read(new URL(SimpleDisplayable.getImagesLangDir().toString() + "/Small/Good.BMP"));
            largeGood = ImageIO.read(new URL(SimpleDisplayable.getImagesLangDir().toString() + "/Large/Good.BMP"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addGood(Card card) {
        assert isProduction();
        if (good != null) throw new RuntimeException("Already got a good");
        good = card;
        fireCardChanged();
    }

    Card useGood() {
        assert isProduction();
        if (good == null) throw new RuntimeException("Don't have a good");
        Card result = good;
        good = null;
        fireCardChanged();
        return result;
    }

    boolean hasGood() {
        assert isProduction();
        return good != null;
    }

    int getChapelCount() {
        assert isChapel();
        return chapelCards.size();
    }

    void putUnderChapel(Card card) {
        assert isChapel();
        chapelCards.add(card);
    }

    public Image getSmallOverlayImage() {
        if (good != null) return smallGood;
        return null;
    }

    public Image getLargeOverlayImage() {
        if (good != null) return largeGood;
        return null;
    }
}
