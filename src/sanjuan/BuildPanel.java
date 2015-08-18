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
import java.awt.event.*;
import javax.swing.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class BuildPanel extends JPanel {
    private PlayedCard planningToBuildOver;
    private Cards<PlayedCard> blackMarketCards = new Cards<PlayedCard>();
    private PlayerModel model;
    private JButton dontBuild, ok;
    private SmallCardsDisplay<Card> paymentDisplay, buildDisplay;
    private SmallCardsDisplay<PlayedCard> craneDisplay, blackMarketDisplay;
    private LargeCardDisplay<Card> lcd;
    private Card planningToBuild;
    private JLabel paymentLabel;
    private TurnDetails turn;
    private int cardWidth;
    private HumanPlayer player;

    public BuildPanel(HumanPlayer player, TurnDetails turn) {
        this.model = player.getModel();
        this.player = player;
        this.turn = turn;
        setLayout(new BorderLayout(Constants.GAP, Constants.GAP));
        setBackground(Colours.SUGAR);
        setOpaque(true);
        setBorder(Constants.EMPTY_BORDER);
        add(BorderLayout.EAST, lcd = new LargeCardDisplay<Card>(false));
        lcd.setOpaque(false);
        boolean useBlackMarket = (model.has("Black Market") && model.getGoodsCount() > 0);
        boolean useCrane = model.has("Crane");
        int tiers = 2 + (useBlackMarket ? 1 : 0) + (useCrane ? 1 : 0);
        JPanel content = new JPanel(new GridLayout(tiers, 1, Constants.GAP, Constants.GAP));
        content.setBackground(getBackground());
        content.add(chooseBuildingPanel(lcd));
        if (useBlackMarket) {
            content.add(buildBlackMarketPanel());
        }
        content.add(choosePaymentPanel());
        if (useCrane) {
            content.add(buildCranePanel());
        }
        add(BorderLayout.CENTER, content);
        JPanel buttons = new JPanel(new HCodeLayout("", Constants.GAP));
        buttons.setBackground(getBackground());
        add(BorderLayout.SOUTH, buttons);
        buttons.add(dontBuild = new JButton(player.getString("button.nobuild")));
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        buttons.add(panel, "x"); // padding
        buttons.add(ok = new JButton(player.getString("button.build")));
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Window window = (Window) getTopLevelAncestor();
                window.dispose();
                synchronized (window) {
                    window.notifyAll();
                }
            }
        });
        dontBuild.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                planningToBuild = null;
                Window window = (Window) getTopLevelAncestor();
                window.dispose();
                synchronized (window) {
                    window.notifyAll();
                }
            }
        });
        if (cardWidth < 4) cardWidth = 4;
        buildDisplay.setDimension(1, cardWidth);
        paymentDisplay.setDimension(1, cardWidth);
        if (craneDisplay != null) craneDisplay.setDimension(1, cardWidth);
        if (blackMarketDisplay != null) blackMarketDisplay.setDimension(1, cardWidth);
        fixButtonStates();
    }

    private CardListener fix = new CardListener() {
        public void cardChanged(Displayable card) {
            fixButtonStates();
        }
    };

    private JPanel buildCranePanel() {
        Cards<PlayedCard> toBuildOver = model.getPlayedCards();
        craneDisplay = new SmallCardsDisplay<PlayedCard>(1, toBuildOver.size(), true);
        craneDisplay.setCards(toBuildOver);
        if (cardWidth < toBuildOver.size()) cardWidth = toBuildOver.size();
        craneDisplay.setSingleSelection(true);
        craneDisplay.addSelectListener(fix);
        return createSubPanel(craneDisplay, player.getString("message.crane"));
    }

    private JPanel choosePaymentPanel() {
        paymentDisplay = new SmallCardsDisplay<Card>(1, model.getHand().size(), true);
        paymentDisplay.setCards(model.getHand());
        if (cardWidth < model.getHand().size()) cardWidth = model.getHand().size();
        paymentDisplay.addSelectListener(fix);
        return createSubPanel(paymentDisplay, player.getString("message.pay"));
    }

    private JPanel buildBlackMarketPanel() {
        Cards<PlayedCard> goods = model.getBuildingsWithGoods();
        blackMarketDisplay = new SmallCardsDisplay<PlayedCard>(1, goods.size(), true);
        blackMarketDisplay.setCards(goods);
        if (cardWidth < goods.size()) cardWidth = goods.size();
        blackMarketDisplay.addSelectListener(fix);
        return createSubPanel(blackMarketDisplay, player.getString("message.blackmarket"));
    }

    private JPanel chooseBuildingPanel(LargeCardDisplay<Card> lcd) {
        Cards<Card> buildable = new Cards<Card>(model.getHand());
        lcd.setCard(buildable.chooseRandom());
        Cards<PlayedCard> built = model.getPlayedCards();
        for (PlayedCard pc : built.getCards()) {
            if (pc.isViolet()) buildable.removeAny(pc);
        }
        buildDisplay = new SmallCardsDisplay<Card>(1, buildable.size(), true);
        buildDisplay.setCards(buildable);
        buildDisplay.addCardOverListener(lcd);
        if (cardWidth < buildable.size()) cardWidth = buildable.size();
        buildDisplay.addSelectListener(fix);
        buildDisplay.setSingleSelection(true);
        return createSubPanel(buildDisplay, player.getString("message.building"));
    }

    private JPanel createSubPanel(SmallCardsDisplay<?> cards, String mesg) {
        JPanel panel = new JPanel(new BorderLayout(Constants.GAP, Constants.GAP));
        panel.setBackground(getBackground());
        panel.setOpaque(false);
        panel.add(cards, BorderLayout.CENTER);
        cards.setBackground(getBackground());
        JLabel label = new JLabel(mesg);
        label.setBackground(getBackground());
        if (cards == paymentDisplay) paymentLabel = label;
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    public Card getBuilding() {
        return planningToBuild;
    }

    public PlayedCard getBuildOver() {
        return planningToBuildOver;
    }

    public Cards<PlayedCard> getBlackMarketCards() {
        return blackMarketCards;
    }

    public Cards<Card> getPayment() {
        return paymentDisplay.getSelectedCards();
    }

    private void fixButtonStates() {
        dontBuild.setEnabled(true);
        blackMarketCards = blackMarketDisplay == null ? new Cards<PlayedCard>() : blackMarketDisplay.getSelectedCards();
        // if you're planning to build it, you can't spend it
        planningToBuild = buildDisplay.getSelectedCard();
        Cards<Card> buildCards = new Cards<Card>();
        if (planningToBuild != null) buildCards.add(planningToBuild);
        paymentDisplay.setDisabled(buildCards);
        // crane?
        planningToBuildOver = (craneDisplay != null) ? craneDisplay.getSelectedCard() : null;
        // have the paid the right amount
        if (planningToBuild == null) {
            paymentLabel.setText(player.getString("message.pay"));
            ok.setEnabled(false);
        } else if (blackMarketCards != null && blackMarketCards.size() > 2) {
            ok.setEnabled(false);
        } else {
            // pretend there are no black market cards being used, so that if the player is paying too much
            // they can be told to pay less.
            int cost = model.getCost(planningToBuild, planningToBuildOver, new Cards<PlayedCard>(), turn);
            String comment = player.getString("payment.ok");
            int paidSoFar = paymentDisplay.getSelectedCards().size() + blackMarketCards.size();
            if (paidSoFar < cost) {
                comment = player.format("payment.more", cost - paidSoFar);
            } else if (paidSoFar > cost) {
                comment = player.format("payment.less", paidSoFar - cost);
            }
            paymentLabel.setText(player.format("message.payment", cost, comment));
            boolean buildOK = planningToBuild != null;
            boolean paymentOK = (planningToBuild == null) || paidSoFar == cost;
            boolean okToBuild = paymentOK && buildOK;
            ok.setEnabled(okToBuild);
        }
    }
}
