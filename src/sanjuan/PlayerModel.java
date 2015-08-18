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
public class PlayerModel {
    private final Cards<PlayedCard> playedCards = new Cards<PlayedCard>();
    private final Cards<Card> hand = new Cards<Card>();
    private String name;

    PlayerModel(String name) {
        this.name = name;
    }

    Cards<PlayedCard> getPlayedCards() {
        return playedCards;
    }

    Cards<Card> getHand() {
        return hand;
    }

    int getBuildingPoints() {
        int total = 0;
        List<PlayedCard> cs = playedCards.getCards();
        for (Card card : cs) {
            if (card == null) break;
            total += card.getVPs();
        }
        return total;
    }

    public String getName() {
        return name;
    }

    public boolean has(String cardName) {
        return playedCards.has(cardName);
    }

    public int getHandLimit() {
        int handLimit = 7;
        if (has("Tower")) {
            handLimit = 12;
        }
        return handLimit;
    }

    public boolean hasPalace() {
        return has("Palace");
    }

    public boolean hasLibrary() {
        return has("Library");
    }

    public int getSixPoints() {
        int total = 0;
        if (has("Guild Hall")) {
            total += 2 * getProductionBuildingCount();
        }
        if (has("City Hall")) {
            total += getVioletBuildingCount();
        }
        if (has("Triumphal Arch")) {
            List<PlayedCard> cs = playedCards.getCards();
            int count = 0;
            for (Card card : cs) {
                if (card.isMonument()) count++;
            }
            if (count == 1) {
                total += 4;
            } else if (count == 2) {
                total += 6;
            } else if (count == 3) {
                total += 8;
            }
        }
        return total;
    }

    public int getChapelPoints() {
        PlayedCard chapel = playedCards.get("Chapel");
        if (chapel == null) return 0;
        return chapel.getChapelCount();
    }

    public int getVioletBuildingCount() {
        int count = 0;
        for (Card card : playedCards.getCards()) {
            if (card.isViolet()) count++;
        }
        return count;
    }

    public int getProductionBuildingCount() {
        int count = 0;
        for (Card card : playedCards.getCards()) {
            if (card.isProduction()) count++;
        }
        return count;
    }

    public void replaceHand(Cards<Card> chosen) {
        hand.replace(chosen);
    }

    public int getGoodsCount() {
        int count = 0;
        for (PlayedCard card : playedCards.getCards()) {
            if (card.isProduction() && card.hasGood()) count++;
        }
        return count;
    }

    public Cards<PlayedCard> getBuildingsWithGoods() {
        Cards<PlayedCard> goods = new Cards<PlayedCard>();
        for (PlayedCard card : playedCards.getCards()) {
            if (card.isProduction() && card.hasGood()) goods.add(card);
        }
        return goods;
    }

    public int getBuildDiscountFor(Card planningToBuild) {
        int discount = 0;
        if (planningToBuild.isViolet() && has("Quarry")) discount++;
        if (planningToBuild.isProduction() && has("Smithy")) discount++;
        return discount;
    }

    public int getBuildDiscount(TurnDetails turn) {
        if (turn != null && turn.isBuilder() && turn.hasPrivilege()) {
            return (hasLibrary() && !turn.usedLibraryPrivilege()) ? 2 : 1;
        } else {
            return 0;
        }
    }

    public int getCost(Card planningToBuild, PlayedCard planningToBuildOver, Cards<PlayedCard> blackMarketCards,
                               TurnDetails turn) {
        assert planningToBuild != null;
        int cost = planningToBuild.getCost();
        cost -= getBuildDiscountFor(planningToBuild);
        if (planningToBuildOver != null) {
            cost -= planningToBuildOver.getCost();
        }
        cost -= blackMarketCards.size();
        cost -= getBuildDiscount(turn);
        if (cost < 0) cost = 0;
        return cost;
    }

    public Cards<PlayedCard> getEmptyProductionBuildings() {
        Cards<PlayedCard> result = new Cards<PlayedCard>();
        for (PlayedCard playedCard : playedCards.getCards()) {
            if (playedCard.isProduction() && !playedCard.hasGood()) {
                result.add(playedCard);
            }
        }
        return result;
    }

    public Cards<PlayedCard> getFullProductionBuildings() {
        Cards<PlayedCard> result = new Cards<PlayedCard>();
        for (PlayedCard playedCard : playedCards.getCards()) {
            if (playedCard.isProduction() && playedCard.hasGood()) {
                result.add(playedCard);
            }
        }
        return result;
    }

    /**
     * @return how many goods could be produced
     */
    public int getProduceCount(TurnDetails turn, boolean recordStats) {
        int count = 1;
        if (turn.hasPrivilege()) {
            count++;
            if (hasLibrary() && !turn.usedLibraryPrivilege()) {
                if (recordStats) Statistics.use("Library", name);
                count++;
            }
        }
        if (has("Aqueduct")) {
            count++;
            if (recordStats) Statistics.use("Aqueduct", name);
        }
        return count;
    }

    /**
     * @return how many goods could be traded
     */
    public int getTradeCount(TurnDetails turn) {
        int count = 1;
        if (turn.hasPrivilege()) {
            count++;
            if (hasLibrary() && !turn.usedLibraryPrivilege()) count++;
        }
        if (has("Trading Post")) count++;
        return count;
    }

    public Cards<PlayedCard> getProductionBuildingsWithoutGoods() {
        Cards<PlayedCard> result = new Cards<PlayedCard>();
        for (PlayedCard playedCard : playedCards.getCards()) {
            if (playedCard.isProduction() && !playedCard.hasGood()) {
                result.add(playedCard);
            }
        }
        return result;
    }

    public void trade(Cards<PlayedCard> toTrade, Deck deck, SalePrice salePrice) {
        for (PlayedCard playedCard : toTrade.getCards()) {
            deck.discard(playedCard.useGood());
            int value = salePrice.getPrice(playedCard.getProductionIndex());
            hand.add(deck.draw(value));
        }
        if (has("Market Stand") && toTrade.size() >= 2) {
            Statistics.use("Market Stand", name);
            hand.add(deck.drawTop());
        }
        if (has("Market Hall") && toTrade.size() >= 1) {
            Statistics.use("Market Hall", name);
            hand.add(deck.drawTop());
        }
    }

    public PlayedCard get(String cardName) {
        return playedCards.get(cardName);
    }

    public int countVioletBuildings() {
        int count = 0;
        for (Card c : playedCards.getCards()) {
            if (c.isViolet()) count++;
        }
        return count;
    }

    public int getPoints() {
        int bps = getBuildingPoints();
        int cps = getChapelPoints();
        int sixps = getSixPoints();
        int subtotal = bps + cps + sixps;
        int pps = 0;
        if (hasPalace()) pps = subtotal / 4;
        int total = subtotal + pps;
        return total;
    }
}
