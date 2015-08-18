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
import java.util.List;
import java.awt.event.*;
import java.awt.*;
import java.text.MessageFormat;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: john
 * Date: Apr 7, 2005
 * Time: 6:32:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class Main implements Constants {
    private static SanJuanPanel sanJuanPanel;
    private static List<SalePrice> salePrices;
    private static int salePriceIndex = 0;
    private static final Random rng = new Random();
    private static int humanPlayerCount = 0;
    private static HumanPlayer lastPlayer = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("San Juan");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        Statistics.init();
        Translation.setDefaultLocale(Locale.ENGLISH);
        NumberOfPlayersDialog dialog = new NumberOfPlayersDialog(frame);
        dialog.pack();
        centreWindow(dialog);
        dialog.setVisible(true);
        Translation.setDefaultLocale(dialog.getDefaultLocale());
        AbstractPlayer[] players = dialog.getPlayers();
        for (AbstractPlayer player : players) {
            if (player instanceof ComputerPlayer) {
                ((ComputerPlayer) player).setAI(new OriginalAI(players.length, (ComputerPlayer) player, players));
            } else if (player instanceof HumanPlayer) {
                humanPlayerCount++;
            }
        }
        Deck deck = initPlayers(players);
        sanJuanPanel = new SanJuanPanel(players);
        frame.getContentPane().add(sanJuanPanel);
        frame.pack();
        centreWindow(frame);
        frame.setVisible(true);
        salePrices = new ArrayList<SalePrice>();
        salePrices.add(new SalePrice(new int[]{1, 1, 2, 2, 3}));
        salePrices.add(new SalePrice(new int[]{1, 2, 2, 2, 3}));
        salePrices.add(new SalePrice(new int[]{1, 1, 1, 2, 2}));
        salePrices.add(new SalePrice(new int[]{1, 2, 2, 3, 3}));
        salePrices.add(new SalePrice(new int[]{1, 1, 2, 2, 2}));
        shuffle(salePrices);
        play(players, deck, frame);
        Statistics.close();
    }

    private static <T> void shuffle(List<T> items) {
        List<T> shuffled = new ArrayList<T>(items.size());
        while (items.size() > 0) {
            shuffled.add(items.remove(rng.nextInt(items.size())));
        }
        items.clear();
        items.addAll(shuffled);
    }

    private static void play(AbstractPlayer[] players, Deck deck, JFrame frame) {
        int governorIndex = 0;
        while (true) {
            Set<Role> roles = governorPhase(players, governorIndex, deck);
            int chooserIndex = governorIndex;
            boolean finished = false;
            int rolesPerTurn = players.length;
            if (rolesPerTurn == 2) rolesPerTurn = 3;
            boolean[] usedLibraryPrivilege = new boolean[players.length];
            for (int j=0; j<rolesPerTurn; j++) {
                AbstractPlayer chooser = players[chooserIndex];
                waitForHuman(chooser);
                Role role = chooser.chooseRole(Collections.unmodifiableSet(roles), usedLibraryPrivilege[chooserIndex]);
                assert roles.contains(role);
                roles.remove(role);
                sanJuanPanel.playerChoseRole(chooser, role);
                if (role == Role.BUILDER) {
                    for (int i=chooserIndex; i<chooserIndex+ players.length; i++) {
                        int pi = i % players.length;
                        TurnDetails turn = new TurnDetails(role, i == chooserIndex, usedLibraryPrivilege[pi]);
                        waitForHuman(players[pi]);
                        players[pi].builderPhase(turn, deck);
                        if (turn.usedLibraryPrivilege()) usedLibraryPrivilege[pi] = true;
                    }
                } else if (role == Role.COUNCILLOR) {
                    for (int i = chooserIndex; i < chooserIndex + players.length; i++) {
                        int pi = i % players.length;
                        TurnDetails turn = new TurnDetails(role, i == chooserIndex, usedLibraryPrivilege[pi]);
                        waitForHuman(players[pi]);
                        players[pi].councillorPhase(turn, deck);
                        if (turn.usedLibraryPrivilege()) usedLibraryPrivilege[pi] = true;
                    }
                } else if (role == Role.PROSPECTOR) {
                    for (int i = chooserIndex; i < chooserIndex + players.length; i++) {
                        int pi = i % players.length;
                        TurnDetails turn = new TurnDetails(role, i == chooserIndex, usedLibraryPrivilege[pi]);
                        final AbstractPlayer player = players[pi];
                        player.prospectorPhase(turn, deck, new Runnable() {
                            public void run() {
                                waitForHuman(player);
                            }
                        });
                        if (turn.usedLibraryPrivilege()) usedLibraryPrivilege[pi] = true;
                    }
                } else if (role == Role.PRODUCER) {
                    for (int i = chooserIndex; i < chooserIndex + players.length; i++) {
                        int pi = i % players.length;
                        TurnDetails turn = new TurnDetails(role, i == chooserIndex, usedLibraryPrivilege[pi]);
                        waitForHuman(players[pi]);
                        players[pi].producerPhase(turn, deck);
                        if (turn.usedLibraryPrivilege()) usedLibraryPrivilege[pi] = true;
                    }
                } else {
                    for (int i = chooserIndex; i < chooserIndex + players.length; i++) {
                        int pi = i % players.length;
                        TurnDetails turn = new TurnDetails(role, i == chooserIndex, usedLibraryPrivilege[pi]);
                        waitForHuman(players[pi]);
                        players[pi].traderPhase(turn, deck, salePrices.get(salePriceIndex));
                        salePriceIndex = (salePriceIndex + 1) % salePrices.size();
                        if (turn.usedLibraryPrivilege()) usedLibraryPrivilege[pi] = true;
                    }
                }
                sanJuanPanel.refresh();
                if (finished = checkForFinished(players)) break;
                chooserIndex++;
                if (chooserIndex == players.length) chooserIndex = 0;
                countCards(players, deck);
            }
            if (finished) break;
            governorIndex++;
            if (governorIndex == players.length) governorIndex = 0;
        }
        determineWinner(players, frame);
    }

    private static void countCards(AbstractPlayer[] players, Deck deck) {
        int count = deck.size();
        for (AbstractPlayer player : players) {
            count += player.getCardCount();
        }
        if (count != 110) System.out.println("I can find " + count + " cards");
    }

    private static void determineWinner(AbstractPlayer[] players, JFrame frame) {
        for (AbstractPlayer player : players) {
            Statistics.score(player.getName(), player.getScore(), player.getTieBreaker());
        }
        List<AbstractPlayer> winners = determineWinners(players);
        String title;
        String message;
        if (winners.size() == 1) {
            message = Translation.formatInDefaultLanguage("message.winner", winners.get(0).getName());
        } else {
            AbstractPlayer first = winners.remove(0);
            List<AbstractPlayer> others = winners;
            StringBuffer buf = new StringBuffer();
            for (AbstractPlayer other : others) {
                if (buf.length() > 0) buf.append(", ");
                buf.append(other.getName());
            }
            message = Translation.formatInDefaultLanguage("message.tie", buf.toString(), first.getName());
        }
        title = Translation.inDefaultLanguage("title.gameover");
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private static List<AbstractPlayer> determineWinners(AbstractPlayer[] players) {
        int topScore = 0;
        int topTieBreaker = 0;
        List<AbstractPlayer> winners = new ArrayList<AbstractPlayer>();
        for (AbstractPlayer player : players) {
            if (player.getScore() > topScore || (player.getScore() == topScore && player.getTieBreaker() > topTieBreaker)) {
                winners.clear();
                winners.add(player);
                topScore = player.getScore();
                topTieBreaker = player.getTieBreaker();
            } else if (player.getScore() == topScore && player.getTieBreaker() == topTieBreaker) {
                winners.add(player);
            }
        }
        return winners;
    }

    private static boolean checkForFinished(AbstractPlayer[] players) {
        for (AbstractPlayer player : players) {
            if (player.getPlayedCards().size() == 12) return true;
        }
        return false;
    }

    private static Set<Role> governorPhase(AbstractPlayer[] players, int nextGovernorIndex, Deck deck) {
        // all roles unclaimed now
        for (AbstractPlayer p : players) sanJuanPanel.playerChoseRole(p, null);
        for (int i = 0; i<players.length; i++) {
            AbstractPlayer player = players[i];
            if (player.has("Chapel")) {
                waitForHuman(players[i]);
                player.chapelReminder((i + players.length - nextGovernorIndex) % players.length + 1);
            }
        }
        sanJuanPanel.refresh();
        for (AbstractPlayer player : players) {
            checkHandLimit(player, deck);
        }
        return new HashSet<Role>(Arrays.asList(Role.ALL_ROLES));
    }

    private static void checkHandLimit(AbstractPlayer player, Deck deck) {
        int handLimit = player.getModel().getHandLimit();
        if (handLimit == 12 && player.getHand().size() > 7) {
            // we used the tower for something
            Statistics.use("Tower", player.getName());
        }
        if (player.getHand().size() > handLimit) {
            waitForHuman(player);
            Cards<Card> toDiscard = player.discardDueToHandLimit(player.getHand().size() - handLimit);
            assert toDiscard.size() == player.getHand().size() - handLimit;
            player.getHand().remove(toDiscard);
            deck.discard(toDiscard);
        }
    }

    private static void waitForHuman(AbstractPlayer player) {
        if (player instanceof HumanPlayer && humanPlayerCount > 1) {
            if (player == lastPlayer) return;
            sanJuanPanel.setHumanPlayer(null);
            JOptionPane.showMessageDialog(sanJuanPanel, "Waiting for " + player.getName(), "Waiting", JOptionPane.OK_OPTION);
            sanJuanPanel.setHumanPlayer((HumanPlayer) player);
            lastPlayer = (HumanPlayer) player;
        }
    }

    private static Deck initPlayers(AbstractPlayer[] players) {
        Deck deck = new Deck();
        for (int i = 0; i < players.length; i++) {
            PlayerModel model = new PlayerModel(MessageFormat.format(players[i].getBaseName(), (i + 1)));
            players[i].setModel(model);
            players[i].autoplayCard(deck.find("Indigo Plant"));
        }
        deck.shuffle();
        for (AbstractPlayer player : players) {
            player.getHand().add(deck.drawTop());
            player.getHand().add(deck.drawTop());
            player.getHand().add(deck.drawTop());
            player.getHand().add(deck.drawTop());
        }
        return deck;
    }

    public static void centreWindow(Window w) {
        Dimension d = w.getSize();
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension sr = Toolkit.getDefaultToolkit().getScreenSize();
        if (s.width > s.height * 4/3) s.width = s.height * 3/4;
        Point p = new Point((s.width - d.width) / 2, (s.height - d.height) / 2);
        if (p.x + d.width > sr.width) p.x = sr.width - d.width;
        if (p.y + d.height > sr.height) p.y = sr.height - d.height;
        if (p.x < 0) p.x = 0;
        if (p.y < 0) p.y = 0;
        w.setLocation(p);
    }
}
