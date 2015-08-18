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
 * OriginalAI stolen from the VB.
 *
 * @author John Farrell (friendless@optushome.com.au)
 */
public class OriginalAI implements AI {
    private int numPlayers;
    private ComputerPlayer me;
    private AbstractPlayer[] players;

    OriginalAI(int numPlayers, ComputerPlayer me, AbstractPlayer[] players) {
        this.me = me;
        this.numPlayers = numPlayers;
        this.players = players;
    }

    // couldn't figure out the VB to see where roundsLeft came from, so I made this up.
    private int getRoundsLeft() {
        int roundsLeft = 12;
        for (int i=0; i<players.length; i++) {
            if (12 - players[i].getBuildingCount() < roundsLeft) {
                roundsLeft = 12 - players[i].getBuildingCount();
            }
        }
        return roundsLeft;
    }

    public double getAIValue(Card card, TurnDetails turn, int roundsLeft) {
        double val = 0.0;
        // not allowed to build 2 of the same violet
        if (card.isViolet() && me.has(card.getName())) return 0.0;
        int rolesPerRound = (numPlayers == 2 ? 3 : numPlayers);
        double tradeMult = roundsLeft * rolesPerRound / 5.0;
        double mayorMult = roundsLeft * rolesPerRound / 5.0;
        if (roundsLeft < 8) {
            val = card.getVPs();
        } else {
            val = card.getVPs() * ((11.0 - roundsLeft) / roundsLeft);
        }
        val = val + getCardBonus(card, tradeMult, mayorMult, roundsLeft);
        int costToBuild = card.getCost() - me.getModel().getBuildDiscount(turn) - me.getModel().getBuildDiscountFor(card);
        if (costToBuild < 0) costToBuild = 0;
        if (roundsLeft > 3) val -= costToBuild;
        return val;
    }

    private double getCardBonus(Card card, double tradeMult, double mayorMult, int roundsLeft) {
        String name = card.getName();
        if (name.equals("Smithy")) return getSmithyBonus(roundsLeft);
        if (name.equals("Archive")) return getArchiveBonus(roundsLeft);
        if (name.equals("Gold Mine")) return getGoldMineBonus(roundsLeft);
        if (name.equals("Market Stand")) return getMarketStandBonus(tradeMult);
        if (name.equals("Black Market")) return getBlackMarketBonus(roundsLeft);
        if (name.equals("Poor House")) return getPoorHouseBonus(roundsLeft);
        if (name.equals("Well")) return getWellBonus(tradeMult);
        if (name.equals("Trading Post")) return getTradingPostBonus(tradeMult);
        if (name.equals("Crane")) return getCraneBonus();
        if (name.equals("Tower")) return getTowerBonus(roundsLeft);
        if (name.equals("Aqueduct")) return getAqueductBonus(tradeMult);
        if (name.equals("Prefecture")) return getPrefectureBonus(mayorMult);
        if (name.equals("Carpenter")) return getCarpenterBonus(roundsLeft);
        if (name.equals("Chapel")) return getChapelBonus(roundsLeft);
        if (name.equals("Market Hall")) return getMarketHallBonus(tradeMult);
        if (name.equals("Quarry")) return getQuarryBonus(roundsLeft);
        if (name.equals("Library")) return getLibraryBonus(tradeMult, roundsLeft);
        if (name.equals("City Hall")) return getCityHallBonus(roundsLeft);
        if (name.equals("Guild Hall")) return getGuildHallBonus(roundsLeft);
        if (name.equals("Palace")) return getPalaceBonus(roundsLeft);
        if (name.equals("Triumphal Arch")) return getTriumphalArchBonus();
        if (name.equals("Statue") || name.equals("Hero") || name.equals("Victory Column")) return getMonumentBonus(card, roundsLeft);
        if (card.isProduction()) return getProductionBonus(card, tradeMult, roundsLeft);
        throw new RuntimeException(card.getName());
    }

    private double getProductionBonus(Card card, double tradeMult, int roundsLeft) {
        double val = 0.0;
        if (me.has("Guild Hall")) val += 2.0;
        boolean library = me.hasLibrary();
        boolean aqueduct = me.has("Aqueduct");
        boolean tradingPost = me.has("Trading Post");
        int maxProduce = 2 + (library ? 1 : 0) + (aqueduct ? 1 : 0);
        int maxTrade = 2 + (library ? 1 : 0) + (tradingPost ? 1 : 0);
        int prodBuilt = me.getProductionBuildingCount();
        double averageTradeWorth = card.getAverageTradeWorth();
        if (prodBuilt < maxProduce - 1) {
            if (prodBuilt < maxTrade - 1) {
                val += (averageTradeWorth - 1.0) * tradeMult;
            } else if (prodBuilt < maxTrade) {
                val += (averageTradeWorth - 1.0) * tradeMult / 2.0;
            }
        } else if (prodBuilt < maxProduce) {
            if (prodBuilt < maxTrade - 1) {
                val += (averageTradeWorth - 1.0) * tradeMult / 2.0;
            } else if (prodBuilt < maxTrade) {
                val += (averageTradeWorth - 1.0) * tradeMult / 4.0;
            }
        } else {
            List<PlayedCard> prodBuildings = me.getProductionBuildings().getCards();
            Collections.sort(prodBuildings, new Comparator<Card>() {
                public int compare(Card c1, Card c2) {
                    return -Double.compare(c1.getAverageTradeWorth(), c2.getAverageTradeWorth());
                }
            });
            if (card.getAverageTradeWorth() > prodBuildings.get(0).getAverageTradeWorth()) {
                double rltm = roundsLeft * tradeMult;
                double awd = card.getAverageTradeWorth() - prodBuildings.get(0).getAverageTradeWorth();
                val = val + awd * rltm;
            }
        }
        return val;
    }

    private double getMonumentBonus(Card card, int roundsLeft) {
        List<String> monuments = new ArrayList<String>();
        monuments.add("Statue");
        monuments.add("Hero");
        monuments.add("Victory Column");
        monuments.remove(card.getName());
        int count = 0;
        if (me.has(monuments.get(0))) count++;
        if (me.has(monuments.get(1))) count++;
        // this code looks dodgy to me - seems to give big bonus for no monuments at all
        if (me.has("Triumphal Arch")) {
            return (count > 0) ? 2.0 : 4.0;
        } else if (roundsLeft > 3 || me.holds("Triumphal Arch")) {
            return (count > 0) ? 1.0 : 2.0;
        } else {
            return 0.0;
        }
    }

    private double getTriumphalArchBonus() {
        int count = 0;
        if (me.has("Statue")) count++;
        if (me.has("Victory Column")) count++;
        if (me.has("Hero")) count++;
        return (new int[] { 0, 4, 6, 8 })[count];
    }

    private double getPalaceBonus(int roundsLeft) {
        return me.getModel().getPoints() / 4.0 + roundsLeft * 0.75;
    }

    private double getGuildHallBonus(int roundsLeft) {
        return me.getProductionBuildingCount() * 2.0 + roundsLeft * 0.8;
    }

    private double getCityHallBonus(int roundsLeft) {
        double val = me.getVioletBuildingCount();
        val += roundsLeft * 0.9;
        return val;
    }

    private double getLibraryBonus(double tradeMult, int roundsLeft) {
        double val = roundsLeft * numPlayers * 0.8;
        if (me.has("Trading Post")) {
            if (me.has("Aqueduct")) {
                val -= tradeMult / 2.0;
            } else {
                val -= tradeMult * 0.35;
            }
        } else if (me.has("Aqueduct")) {
            val -= tradeMult * 0.35;
        }
        return val;
    }

    private double getQuarryBonus(int roundsLeft) {
        double result = 0.0;
        if (me.has("City Hall")) {
            result += roundsLeft;
        } else {
            result += roundsLeft / 2.0;
        }
        if (me.has("Carpenter")) {
            result += roundsLeft / 10.0;
        }
        return result;
    }

    private double getMarketHallBonus(double tradeMult) {
        return tradeMult;
    }

    private double getChapelBonus(int roundsLeft) {
        if (roundsLeft < 8) {
            return roundsLeft * 0.6;
        } else {
            return 0.0;
        }
    }

    private double getCarpenterBonus(int roundsLeft) {
        double result = 0.0;
        if (me.has("City Hall")) {
            result += roundsLeft;
        } else {
            result += roundsLeft / 2.0;
        }
        if (me.has("Quarry")) {
            result += roundsLeft / 10.0;
        }
        return result;
    }

    private double getPrefectureBonus(double mayorMult) {
        return mayorMult;
    }

    private double getAqueductBonus(double tradeMult) {
        if (me.hasLibrary()) {
            return tradeMult / 4.0;
        } else if (me.has("Market Stand")) {
            if (me.has("Trading Post")) {
                return tradeMult;
            } else {
                return tradeMult / 2.0;
            }
        } else {
            return tradeMult / 4.0;
        }
    }

    private double getTowerBonus(int roundsLeft) {
        return roundsLeft / 10.0;
    }

    private double getCraneBonus() {
        int extraBuildings = 100;
        int pointsBehind = 0;
        for (AbstractPlayer player : players) {
            if (player == me) continue;
            if (me.getBuildingCount() - player.getBuildingCount() < extraBuildings) {
                extraBuildings = me.getBuildingCount() - player.getBuildingCount();
            }
            if (player.getModel().getPoints() - me.getModel().getPoints() > pointsBehind) {
                pointsBehind = player.getModel().getPoints() - me.getModel().getPoints();
            }
        }
        double craneAdv = extraBuildings * 2.0;
        if (craneAdv > pointsBehind + 1.0) {
            craneAdv = pointsBehind + 1.0;
        }
        return craneAdv;
    }

    private double getTradingPostBonus(double tradeMult) {
        if (me.hasLibrary()) {
            return tradeMult / 4.0;
        } if (me.has("Market Stand")) {
            if (me.has("Aqueduct")) {
                return tradeMult;
            } else {
                return tradeMult / 2.0;
            }
        } else {
            return tradeMult / 4.0;
        }
    }

    private double getWellBonus(double tradeMult) {
        if (me.has("Aqueduct") || me.hasLibrary()) {
            return tradeMult;
        } else {
            return tradeMult / numPlayers;
        }
    }

    private double getPoorHouseBonus(int roundsLeft) {
        return roundsLeft / 4.0;
    }

    private double getBlackMarketBonus(int roundsLeft) {
        return roundsLeft / 4.0;
    }

    private double getMarketStandBonus(double tradeMult) {
        if (me.has("Trading Post") || me.hasLibrary()) {
            if (me.has("Aqueduct") || me.hasLibrary()) {
                return tradeMult;
            } else {
                return tradeMult * 0.5;
            }
        } else {
            return tradeMult * 0.35;
        }
    }

    private double getGoldMineBonus(int roundsLeft) {
        return roundsLeft / 5.0;
    }

    private double getArchiveBonus(int roundsLeft) {
        return roundsLeft / 5.0;
    }

    private double getSmithyBonus(int roundsLeft) {
        if (me.has("Guild Hall")) {
            return roundsLeft;
        } else if (me.getProductionBuildingCount() < 3) {
            return 3.0 - me.getProductionBuildingCount();
        } else {
            return 0.0;
        }
    }

    public Card chooseCardToKeep(Cards<Card> toChooseFrom, TurnDetails turn) {
        int roundsLeft = getRoundsLeft();
        List<Card> cards = orderByAIValue(toChooseFrom.getCards(), turn, roundsLeft);
        Card result = cards.get(0);
        return result;
    }

    public Cards<Card> chooseCardsToKeep(Cards<Card> toChooseFrom, TurnDetails turn, int count) {
        int roundsLeft = getRoundsLeft();
        assert count <= toChooseFrom.size();
        List<Card> cards = orderByAIValue(toChooseFrom.getCards(), turn, roundsLeft);
        List<Card> result = cards.subList(0, count);
        return new Cards<Card>(result);
    }

    Cards<Card> getBuildableCards(TurnDetails turn) {
        Cards<Card> cards = me.getHand();
        return me.getBuildableCards(cards, me.getModel().getBuildDiscount(turn));
    }

    public Card chooseCardToBuild(TurnDetails turn) {
        int roundsLeft = getRoundsLeft();
        Cards<Card> canBuild = getBuildableCards(turn);
        if (canBuild.size() == 0) return null;
        if (roundsLeft == 11) {
            if (canBuild.get("Library") != null) return canBuild.get("Library");
            if (canBuild.get("Coffee Roaster") != null) return canBuild.get("Coffee Roaster");
        }
        List<Card> ordered = orderByAIValue(canBuild.getCards(), turn, roundsLeft);
        return ordered.get(0);
    }

    private List<Card> orderByAIValue(List<Card> cards, TurnDetails turn, int roundsLeft) {
        final Map<Card, Double> values = new HashMap<Card, Double>();
        for (Card card : cards) {
            double aiValue = getAIValue(card, turn, roundsLeft);
            values.put(card, aiValue);
        }
        Collections.sort(cards, new Comparator<Card>() {
            public int compare(Card o1, Card o2) {
                double v1 = values.get(o1);
                double v2 = values.get(o2);
                if (v1 < v2) return 1;
                if (v2 < v1) return -1;
                return 0;
            }
        });
        return cards;
    }

    public Cards<PlayedCard> chooseCardsToTrade(Cards<PlayedCard> buildingsWithGoods, int count) {
        if (buildingsWithGoods.size() <= count) return buildingsWithGoods;
        List<PlayedCard> toTrade = buildingsWithGoods.getCards();
        Collections.sort(toTrade, PlayedCard.GOOD_VALUE_COMPARATOR);
        return new Cards<PlayedCard>(toTrade);
    }

    public Role chooseRole(Set<Role> available, boolean usedLibraryPrivilege) {
        List<Role> roles = new ArrayList<Role>(available);
        Role best = null;
        double a = -Double.MAX_VALUE;
        boolean builderAvailable = available.contains(Role.BUILDER);
        for (int i = 0; i < roles.size(); i++) {
            double advantage = calculateRoleAdvantage(roles.get(i), builderAvailable, usedLibraryPrivilege);
            if (advantage > a) {
                a = advantage;
                best = roles.get(i);
            }
        }
        return best;
    }

    private double calculateRoleAdvantage(Role role, boolean builderAvailable, boolean usedLibraryPrivilege) {
        int roundsLeft = getRoundsLeft();
        if (role == Role.COUNCILLOR) return calculateCouncillorAdvantage(builderAvailable, roundsLeft, usedLibraryPrivilege);
        if (role == Role.BUILDER) return calculateBuilderAdvantage(roundsLeft, usedLibraryPrivilege);
        if (role == Role.PRODUCER) return calculateProducerAdvantage(usedLibraryPrivilege);
        if (role == Role.TRADER) return calculateTraderAdvantage(usedLibraryPrivilege);
        if (role == Role.PROSPECTOR) return calculateProspectorAdvantage(usedLibraryPrivilege);
        throw new IllegalArgumentException();
    }

    private double calculateProspectorAdvantage(boolean usedLibraryPrivilege) {
        boolean library = me.hasLibrary() && !usedLibraryPrivilege;
        double adv = library ? 2.0 : 1.0;
        if (me.has("Gold Mine")) adv += 0.2;
        return adv;
    }

    private double calculateTraderAdvantage(boolean usedLibraryPrivilege) {
        TurnDetails traderTurn = new TurnDetails(Role.TRADER, true, usedLibraryPrivilege);
        int tradeCount = me.getModel().getTradeCount(traderTurn);
        List<PlayedCard> buildingsWithGoods = me.getModel().getFullProductionBuildings().getCards();
        Collections.sort(buildingsWithGoods, PlayedCard.GOOD_VALUE_COMPARATOR);
        double adv = 0.0;
        for (int i=0; i<tradeCount && i<buildingsWithGoods.size(); i++) {
            adv += buildingsWithGoods.get(i).getAverageTradeWorth();
        }
        if (adv + me.getHand().size() > me.getModel().getHandLimit()) {
            adv = adv + me.getHand().size() - me.getModel().getHandLimit();
        }
        return adv;
    }

    private double calculateProducerAdvantage(boolean usedLibraryPrivilege) {
        TurnDetails ifITakeProducer = new TurnDetails(Role.PRODUCER, true, usedLibraryPrivilege);
        TurnDetails ifSomeoneElseTakesProducer = new TurnDetails(Role.PRODUCER, false, usedLibraryPrivilege);
        int pcme = me.getModel().getProduceCount(ifITakeProducer, false);
        int pcse = me.getModel().getProduceCount(ifSomeoneElseTakesProducer, false);
        List<PlayedCard> pbs = me.getModel().getEmptyProductionBuildings().getCards();
        Collections.sort(pbs, PlayedCard.GOOD_VALUE_COMPARATOR);
        double me = 0;
        double se = 0;
        for (int i=0; i<pbs.size(); i++) {
            if (i < pcse) se += pbs.get(i).getAverageTradeWorth();
            if (i < pcme) {
                me += pbs.get(i).getAverageTradeWorth();
            } else {
                break;
            }
        }
        return me - se;
    }

    private double calculateBuilderAdvantage(int roundsLeft, boolean usedLibraryPrivilege) {
        boolean library = me.hasLibrary() && !usedLibraryPrivilege;
        double adv = library ? 2.0 : 1.0;
        if (me.getHand().size() > me.getModel().getHandLimit()) adv += me.getHand().size() - me.getModel().getHandLimit();
        TurnDetails builderTurn = new TurnDetails(Role.BUILDER, true, usedLibraryPrivilege);
        Card toBuild = chooseCardToBuild(builderTurn);
        if (toBuild == null) {
            adv = -999.0;
        } else if (roundsLeft <= 5) {
            double waste = (5.0 - getAIValue(toBuild, builderTurn, roundsLeft)) / 4.0;
            if (waste > 0.0) adv -= waste;
        }
        return adv;
    }

    private double calculateCouncillorAdvantage(boolean builderAvailable, int roundsLeft, boolean usedLibraryPrivilege) {
        int cardsAllowed = me.getModel().getHandLimit() - me.getHand().size();
        boolean library = me.hasLibrary() && !usedLibraryPrivilege;
        double adv = library ? 0.3 : 0.2;
        if (me.has("Prefecture")) adv += 1.0;
        if (roundsLeft <= 5) {
            if (me.getHand().size() >= 5 && builderAvailable) {
                // what could I achieve if I took builder instead?
                TurnDetails builderTurn = new TurnDetails(Role.BUILDER, true, usedLibraryPrivilege);
                Cards<Card> buildable = getBuildableCards(builderTurn);
                double waste;
                List<Card> bc = orderByAIValue(buildable.getCards(), builderTurn, roundsLeft);
                if (buildable.size() == 0) {
                    waste = 5.0;
                } else {
                    waste = (5.0 - getAIValue(bc.get(0), builderTurn, roundsLeft)) / 4.0;
                }
                if (waste > 0) adv += waste;
            }
        } else if (adv > (double) cardsAllowed) {
            adv = (double) cardsAllowed;
        }
        return adv;
    }

    public Card chooseCardToBury(int positionNextRound) {
        // guess that one of the first two turns next round will be the builder
        TurnDetails builderTurn = new TurnDetails(Role.BUILDER, positionNextRound <= 2, false);
        Card wantToBuild = chooseCardToBuild(builderTurn);
        List<Card> cards = me.getHand().getCards();
        if (wantToBuild == null) {
            // nothing we can build, better save up
            return null;
        } else {
            Cards<PlayedCard> indigoes = me.getModel().getFullProductionBuildings();
            for (PlayedCard pc : indigoes.getCards()) {
                if (pc.getProductionIndex() != 0) indigoes.remove(pc);
            }
            int cost = me.getModel().getCost(wantToBuild, null, me.has("Black Market") ? indigoes : new Cards<PlayedCard>(), builderTurn);
            cards.remove(wantToBuild);
            if (cards.size() <= cost) return null;
            orderByAIValue(cards, builderTurn, getRoundsLeft());
            return cards.get(cards.size() - 1);
        }
    }

    public String getBaseName() {
        return Translation.inDefaultLanguage("name.robot");
    }
}
