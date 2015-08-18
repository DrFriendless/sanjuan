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
 * This is a player which is controlled by an AI.
 * This class implements AbstractPlayer and provides helper methods for the AI to find out about the state of the game.
 *
 * @author John Farrell (friendless@optushome.com.au)
 */
public class ComputerPlayer extends AbstractPlayer {
    private AI ai;

    ComputerPlayer() {
    }

    void setAI(AI ai) {
        this.ai = ai;
    }

    public Role chooseRole(Set<Role> available, boolean usedLibraryPrivilege) {
        return ai.chooseRole(available, usedLibraryPrivilege);
    }

    private Cards<Card> getPayment(TurnDetails turn, int amount, Card planningToBuild) {
        Cards<Card> payment = new Cards<Card>();
        Cards<Card> uselessCards = getUselessCards(turn);
        Cards<Card> toPayWith = new Cards<Card>(getModel().getHand());
        toPayWith.remove(planningToBuild);
        assert !(uselessCards.contains(planningToBuild));
        if (amount <= uselessCards.size()) {
            while (payment.size() < amount) {
                Card card = uselessCards.chooseRandom();
                uselessCards.remove(card);
                payment.add(card);
                toPayWith.remove(card);
            }
        }
        if (amount - payment.size() > toPayWith.size()) {
            return payment;
        }
        if (payment.size() < amount) {
            Cards<Card> toKeep = ai.chooseCardsToKeep(toPayWith, turn, toPayWith.size() - amount + payment.size());
            for (Card card : toPayWith.getCards()) {
                if (!toKeep.contains(card)) {
                    toPayWith.remove(card);
                    payment.add(card);
                }
            }
        }
        return payment;
    }

    public void builderPhase(TurnDetails turn, Deck deck) {
        Card planningToBuild = ai.chooseCardToBuild(turn);
        if (planningToBuild == null) return;
        Cards<PlayedCard> blackMarketCards = new Cards<PlayedCard>();
        PlayedCard buildOver = null;
        int cost = getModel().getCost(planningToBuild, buildOver, blackMarketCards, turn);
        Cards<Card> payment = getPayment(turn, cost, planningToBuild);
        if (planningToBuild.getCost() == getModel().getBuildDiscount(turn) + getModel().getBuildDiscountFor(planningToBuild) + payment.size()) {
            build(planningToBuild, buildOver, blackMarketCards, payment, deck);
        } else if (payment.size() > 0 || blackMarketCards.size() > 0) {
            System.out.println("Paid wrong amount " + planningToBuild + " " + planningToBuild.getCost() + " " +
                    getModel().getBuildDiscount(turn) + " " + getModel().getBuildDiscountFor(planningToBuild) + " " + payment);
        }
    }

    public void producerPhase(TurnDetails turn, Deck deck) {
        List<PlayedCard> buildings = getModel().getProductionBuildingsWithoutGoods().getCards();
        Collections.sort(buildings, PlayedCard.GOOD_VALUE_COMPARATOR);
        int produceCount = getModel().getProduceCount(turn, true);
        if (produceCount > buildings.size()) produceCount = buildings.size();
        produce(new Cards<PlayedCard>(buildings.subList(0, produceCount)), deck);
    }

    public void traderPhase(TurnDetails turn, Deck deck, SalePrice salePrice) {
        Cards<PlayedCard> buildingsWithGoods = getModel().getFullProductionBuildings();
        int max = getModel().getTradeCount(turn);
        Cards<PlayedCard> toTrade = ai.chooseCardsToTrade(buildingsWithGoods, max);
        getModel().trade(toTrade, deck, salePrice);
    }

    public void chapelReminder(int positionNextRound) {
        Card toBury = ai.chooseCardToBury(positionNextRound);
        if (toBury != null) {
            Cards<Card> cards = new Cards<Card>();
            cards.add(toBury);
            bury(cards);
        }
    }

    public Cards<Card> discardDueToHandLimit(int count) {
        int toKeep = getModel().getHand().size() - count;
        Cards<Card> keep = chooseSomeToKeep(getModel().getHand(), toKeep, toKeep, null);
        return getHand().except(keep);
    }

    protected Cards<Card> chooseSomeToKeepForCouncillorWithoutArchive(Cards<Card> drawn, int keep, TurnDetails turn) {
        return chooseSomeToKeep(drawn, keep, keep, turn);
    }

    protected Cards<Card> chooseSomeToKeepForCouncillorWithArchive(Cards<Card> drawn, int keep, TurnDetails turn) {
        return chooseSomeToKeep(drawn, keep, keep, turn);
    }

    protected Cards<Card> chooseSomeToKeepForGoldMine(Cards<Card> drawn, TurnDetails turn) {
        return chooseSomeToKeep(drawn, 1, 1, turn);
    }

    public Cards<Card> chooseSomeToKeep(Cards<Card> toChooseFrom, int min, int max, TurnDetails turn) {
        toChooseFrom = new Cards<Card>(toChooseFrom);
        if (min == toChooseFrom.size()) return toChooseFrom;
        Cards<Card> cards = new Cards<Card>();
        while (cards.size() < min) {
            Card card = ai.chooseCardToKeep(toChooseFrom, turn);
            toChooseFrom.remove(card);
            cards.add(card);
        }
        return cards;
    }

    public boolean holds(String cardName) {
        return getHand().has(cardName);
    }

    public double getAverageTradeWorth() {
        int count = 0;
        double total = 0.0;
        for (Card card : getPlayedCards().getCards()) {
            if (card.isProduction()) {
                count++;
                total += card.getAverageTradeWorth();
            }
        }
        return total / (double) count;
    }

    public Cards<PlayedCard> getProductionBuildings() {
        Cards<PlayedCard> result = new Cards<PlayedCard>();
        for (PlayedCard c : getPlayedCards().getCards()) {
            if (c.isProduction()) result.add(c);
        }
        return result;
    }

    Cards<Card> getBuildableCards(Cards<Card> choices, int privilegeDiscount) {
        Cards<Card> canBuild = new Cards<Card>();
        int toSpend = choices.getCards().size() - 1;
        for (Card card : choices.getCards()) {
            if (card.isViolet() && has(card.getName())) continue;
            if (card.getCost() <= privilegeDiscount + toSpend + getModel().getBuildDiscountFor(card)) {
                canBuild.add(card);
            }
        }
        return canBuild;
    }

    Cards<Card> getUselessCards(TurnDetails turn) {
        Cards<Card> cards = new Cards<Card>(getHand());
        Cards<Card> buildable = getBuildableCards(cards, getModel().getBuildDiscount(turn));
        cards.remove(buildable);
        // don't quite follow this logic, but that's what it says
        for (Iterator iter = cards.getCards().iterator(); iter.hasNext();) {
            Card card = (Card) iter.next();
            int count = 0;
            for (Card card2 : cards.getCards()) {
                if (card.getName().equals(card2.getName())) count++;
            }
            if (count < 2) iter.remove();
        }
        return new Cards<Card>(cards);
    }

    public String getBaseName() {
        return ai.getBaseName();
    }
}
