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

import java.util.Set;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class ChooseRolePanel extends JPanel {
    private Role selected;
    private JButton ok;
    private LargeCardDisplay previous;

    public ChooseRolePanel(final Set available, HumanPlayer player) {
        super(new BorderLayout(Constants.GAP, Constants.GAP));
        setBorder(Constants.EMPTY_BORDER);
        setBackground(Colours.INDIGO);
        Role[] all = Role.ALL_ROLES;
        JPanel cards = new JPanel(new GridLayout(1, all.length, Constants.GAP, Constants.GAP));
        cards.setOpaque(false);
        for (Role role : all) {
            LargeCardDisplay<RoleCard> lcd = new LargeCardDisplay<RoleCard>(true) {
                public void click() {
                    select(this);
                }
            };
            lcd.setBackground(getBackground());
            lcd.setCard(new RoleCard(role));
            lcd.setGrayed(!(available.contains(role)));
            cards.add(lcd);
        }
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        ok = new JButton(player.getString("button.role"));
        buttons.add(ok);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame dialog = (JFrame) getTopLevelAncestor();
                dialog.dispose();
                synchronized (dialog) {
                    dialog.notifyAll();
                }
            }
        });
        ok.setEnabled(false);
        add(cards, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void select(LargeCardDisplay<RoleCard> lcd) {
        Role thisRole = ((RoleCard) ((LargeCardDisplay) lcd).getCard()).getRole();
        selected = thisRole;
        ok.setEnabled(selected != null);
        if (previous != null) previous.setSelected(false);
        previous = (LargeCardDisplay) lcd;
        previous.setSelected(true);
    }

    Role getSelected() {
        return selected;
    }
}
