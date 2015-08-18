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
import java.awt.event.*;
import java.awt.*;
import java.text.MessageFormat;
import javax.swing.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class HumanPlayer extends AbstractPlayer {
    private JFrame frame;
    private static Point roleWindowLocation = new Point(0,0);
    private static Point buildWindowLocation = new Point(0,0);
    private static Point chooseWindowLocation = new Point(0,0);
    private Locale locale;
    private String name;

    HumanPlayer(JFrame frame, String name, Locale locale) {
        this.frame = frame;
        this.locale = locale;
        this.name = name;
    }

    protected String getString(String key) {
        return Translation.getResource(locale, key);
    }

    public Role chooseRole(Set<Role> available, boolean usedLibraryPrivilege) {
        JFrame dialog = new JFrame(format("title.choose_role", name));
        dialog.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                roleWindowLocation = e.getComponent().getLocation();
            }
        });
        dialog.setLocation(roleWindowLocation);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        ChooseRolePanel chooseRole = new ChooseRolePanel(available, this);
        dialog.getContentPane().add(chooseRole);
        display(dialog);
        return chooseRole.getSelected();
    }

    public void builderPhase(TurnDetails turn, Deck deck) {
        JFrame dialog = new JFrame(format("title.builder", name));
        dialog.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                buildWindowLocation = e.getComponent().getLocation();
            }
        });
        dialog.setLocation(buildWindowLocation);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        BuildPanel build = new BuildPanel(this, turn);
        dialog.getContentPane().add(build);
        display(dialog);
        if (build.getBuilding() != null) {
            build(build.getBuilding(), build.getBuildOver(), build.getBlackMarketCards(), build.getPayment(), deck);
        }
    }

    private void display(JFrame dialog) {
        dialog.pack();
        dialog.setVisible(true);
        synchronized (dialog) {
            try {
                dialog.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void producerPhase(TurnDetails turn, Deck deck) {
        int produceCount = getModel().getProduceCount(turn, true);
        Cards<PlayedCard> canProduce = getModel().getEmptyProductionBuildings();
        if (canProduce.size() > 0) {
            String message = format("message.producer", produceCount);
            JFrame dialog = buildChooseWindow(format("title.producer", name));
            ChooseCardsPanel<PlayedCard> chooseCards = new ChooseCardsPanel<PlayedCard>(canProduce, 0, produceCount, message, this);
            dialog.getContentPane().add(chooseCards);
            display(dialog);
            Cards<PlayedCard> toProduce = chooseCards.getSelectedCards();
            produce(toProduce, deck);
        }
    }

    public void traderPhase(TurnDetails turn, Deck deck, SalePrice salePrice) {
        if (getModel().getFullProductionBuildings().size() > 0) {
            JFrame dialog = buildChooseWindow(format("title.trader", name));
            TradePanel trade = new TradePanel(this, turn, salePrice);
            dialog.getContentPane().add(trade);
            display(dialog);
            Cards<PlayedCard> toTrade = trade.getTradeCards();
            getModel().trade(toTrade, deck, salePrice);
        }
    }

    private JFrame buildChooseWindow(String title) {
        JFrame dialog = new JFrame(title);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                chooseWindowLocation = e.getComponent().getLocation();
            }
        });
        dialog.setLocation(chooseWindowLocation);
        return dialog;
    }

    public void chapelReminder(int positionNExtRound) {
        JFrame dialog = buildChooseWindow(format("title.governor", name));
        if (has("Chapel") && getHand().size() > 0) {
            ChooseCardsPanel<Card> chooseCards = new ChooseCardsPanel<Card>(getHand(), 0, 1, getString("message.chapel"), this);
            dialog.getContentPane().add(chooseCards);
            display(dialog);
            Cards<Card> toBury = chooseCards.getSelectedCards();
            bury(toBury);
        }
    }

    String format(String key, Object ... arguments) {
        return MessageFormat.format(getString(key), arguments);
    }

    public Cards<Card> discardDueToHandLimit(int count) {
        int toKeep = getModel().getHand().size() - count;
        String title = format("title.handlimit", name);
        String message = format("message.handlimit", toKeep);
        Cards<Card> keep = chooseSomeToKeep(getModel().getHand(), toKeep, toKeep, title, null, true, message);
        return getModel().getHand().except(keep);
    }

    protected Cards<Card> chooseSomeToKeepForCouncillorWithoutArchive(Cards<Card> drawn, int keep, TurnDetails turn) {
        String message = format("message.noarchive", keep);
        return chooseSomeToKeep(drawn, keep, keep, format("title.councillor", name), turn, false, message);
    }

    protected Cards<Card> chooseSomeToKeepForCouncillorWithArchive(Cards<Card> drawn, int keep, TurnDetails turn) {
        String message = format("message.archive", keep);
        return chooseSomeToKeep(drawn, keep, keep, format("title.councillor", name), turn, true, message);
    }

    protected Cards<Card> chooseSomeToKeepForGoldMine(Cards<Card> drawn, TurnDetails turn) {
        return chooseSomeToKeep(drawn, 1, 1, format("title.goldmine", name), turn, false, getString("message.goldmine"));
    }

    public Cards<Card> chooseSomeToKeep(Cards<Card> toChooseFrom, int min, int max, String title, TurnDetails turn,
            boolean allSelected, String message) {
        if (min == toChooseFrom.size()) return new Cards<Card>(toChooseFrom);
        JDialog dialog = new JDialog(frame, title, true);
        dialog.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                chooseWindowLocation = e.getComponent().getLocation();
            }
        });
        dialog.setLocation(chooseWindowLocation);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        ChooseCardsPanel<Card> chooseCards = new ChooseCardsPanel<Card>(toChooseFrom, min, max, message, this);
        if (allSelected) chooseCards.setAllSelected();
        dialog.getContentPane().add(chooseCards);
        dialog.pack();
        dialog.setVisible(true);
        return chooseCards.getSelectedCards();
    }

    public String getBaseName() {
        return name;
    }
}
