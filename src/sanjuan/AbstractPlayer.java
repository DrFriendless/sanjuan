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
public abstract class AbstractPlayer {
    private PlayerModel model;

    AbstractPlayer() {
    }

    public PlayerModel getModel() {
        return model;
    }

    public void setModel(PlayerModel model) {
        this.model = model;
    }

    public Cards<Card> getHand() {
        return model.getHand();
    }

    public void autoplayCard(Card card) {
        PlayedCard pc = new PlayedCard(card);
        getPlayedCards().add(pc);
    }



    public final void draw(Deck deck) {
        getHand().add(deck.drawTop());
    }

    public abstract Role chooseRole(Set<Role> available, boolean usedLibraryPrivilege);

    public abstract void builderPhase(TurnDetails turn, Deck deck);

    public abstract void producerPhase(TurnDetails turn, Deck deck);

    public abstract void traderPhase(TurnDetails turn, Deck deck, SalePrice salePrice);

    /** If this invoked, then you definitely have a chapel */
    public abstract void chapelReminder(int positionNextRound);

    public abstract Cards<Card> discardDueToHandLimit(int count);

    public void councillorPhase(TurnDetails turn, Deck deck) {
        int draw = turn.hasPrivilege() ? 5 : 2;
        if (hasLibrary() && turn.hasPrivilege() && !turn.usedLibraryPrivilege()) {
            Statistics.use("Library", getName());
            draw += 3;
            turn.useLibraryPrivilege();
        }
        int keep = 1;
        if (has("Prefecture")) {
            Statistics.use("Prefecture", getName());
            keep = 2;
        }
        if (has("Archive")) {
            Statistics.use("Archive", getName());
            keep = keep + getHand().size();
            Cards<Card> drawn = deck.draw(draw);
            drawn.add(getHand());
            Cards<Card> chosen = chooseSomeToKeepForCouncillorWithArchive(drawn, keep, turn);
            drawn.remove(chosen);
            model.replaceHand(chosen);
            deck.discard(drawn);
        } else {
            Cards<Card> drawn = deck.draw(draw);
            Cards<Card> chosen = chooseSomeToKeepForCouncillorWithoutArchive(drawn, keep, turn);
            getHand().add(chosen);
            drawn.remove(chosen);
            deck.discard(drawn);
        }
    }

    protected abstract Cards<Card> chooseSomeToKeepForCouncillorWithoutArchive(Cards<Card> drawn, int keep, TurnDetails turn);

    protected abstract Cards<Card> chooseSomeToKeepForCouncillorWithArchive(Cards<Card> drawn, int keep, TurnDetails turn);

    protected abstract Cards<Card> chooseSomeToKeepForGoldMine(Cards<Card> drawn, TurnDetails turn);

    public void prospectorPhase(TurnDetails turn, Deck deck, Runnable ifSomethingHappens) {
        if (turn.hasPrivilege()) {
            draw(deck);
            if (hasLibrary() && !turn.usedLibraryPrivilege()) {
                Statistics.use("Library", getName());
                draw(deck);
                turn.useLibraryPrivilege();
            }
        }
        if (has("Gold Mine")) {
            Statistics.use("Gold Mine", getName());
            Cards<Card> drawn = deck.draw(4);
            if (drawn.allDistinctCosts()) {
                ifSomethingHappens.run();
                Cards<Card> chosen = chooseSomeToKeepForGoldMine(drawn, turn);
                getHand().add(chosen);
                drawn.remove(chosen);
                deck.discard(drawn);
            } else {
                deck.discard(drawn);
            }
        }
    }

    public abstract String getBaseName();

    protected final void produce(Cards<PlayedCard> toProduce, Deck deck) {
        for (PlayedCard playedCard : toProduce.getCards()) {
            playedCard.addGood(deck.drawTop());
            Statistics.use(playedCard.getName(), getName());
        }
        if (toProduce.size() >= 2 && has("Well")) {
            draw(deck);
            Statistics.use("Well", getName());
        }
    }

    protected final void bury(Cards<Card> toBury) {
        assert toBury.size() <= 1;
        for (Card card : toBury.getCards()) {
            getModel().get("Chapel").putUnderChapel(card);
            getHand().remove(card);
            Statistics.use("Chapel", getName());
        }
    }

    public void build(Card building, PlayedCard buildOver, Cards<PlayedCard> blackMarketCards, Cards<Card> payment,
            Deck deck) {
        assert building != null;
        assert building.isProduction() || !has(building.getName());
        boolean hasCarpenter = has("Carpenter");
        boolean hasPoorHouse = has("Poor House");
        if (building.isViolet() && has("Quarry")) Statistics.use("Quarry", getName());
        if (building.isProduction() && has("Smithy")) Statistics.use("Smithy", getName());
        if (buildOver != null) {
            getPlayedCards().remove(buildOver);
            Statistics.use("Crane", getName());
        }
        getPlayedCards().add(new PlayedCard(building));
        if (blackMarketCards != null) {
            for (PlayedCard playedCard : blackMarketCards.getCards()) {
                deck.discard(playedCard.useGood());
                Statistics.use("Black Market", getName());
            }
        }
        getHand().remove(building);
        Statistics.build(building.getName(), getName());
        getHand().remove(payment);
        deck.discard(payment);
        if (building.isViolet() && hasCarpenter) {
            getHand().add(deck.drawTop());
            Statistics.use("Carpenter", getName());
        }
        if (hasPoorHouse && getHand().size() <= 1) {
            getHand().add(deck.drawTop());
            Statistics.use("Poor House", getName());
        }
    }

    public int getVioletBuildingCount() {
        return getModel().getVioletBuildingCount();
    }

    public int getProductionBuildingCount() {
        return getModel().getProductionBuildingCount();
    }

    public int getCardCount() {
        return getHand().size() + getPlayedCards().size() + model.getFullProductionBuildings().size() + model.getChapelPoints();
    }

    public boolean hasLibrary() {
        return model.hasLibrary();
    }

    public final boolean has(String building) {
        return model.has(building);
    }

    public int getBuildingCount() {
        return getPlayedCards().size();
    }

    public String getName() {
        return model.getName();
    }

    public Cards<PlayedCard> getPlayedCards() {
        return model.getPlayedCards();
    }

    public int getScore() {
        int bps = model.getBuildingPoints();
        int cps = model.getChapelPoints();
        int sixps = model.getSixPoints();
        int subtotal = bps + cps + sixps;
        int pps = 0;
        if (model.hasPalace()) pps = subtotal / 4;
        int total = subtotal + pps;
        return total;
    }

    public int getTieBreaker() {
        return getHand().size() + model.getFullProductionBuildings().size();
    }
}
