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
import java.util.*;
import javax.swing.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class PlayersPanel extends JPanel {
    private Map<AbstractPlayer, PlayerPanel> playerPanels = new HashMap<AbstractPlayer, PlayerPanel>();

    PlayersPanel(AbstractPlayer[] players, LargeCardDisplay<Card> lcd) {
        setOpaque(false);
        if (players.length == 2) {
            setLayout(new GridLayout(1, 2, 0, 0));
        } else {
            setLayout(new GridLayout(2, 2, 0, 0));
        }
        for (int i=0; i< players.length; i++) {
            PlayerPanel pp = new PlayerPanel(players[i].getModel(), lcd);
            playerPanels.put(players[i], pp);
            add(pp);
        }
    }

    public void playerChoseRole(AbstractPlayer chooser, Role role) {
        PlayerPanel panel = playerPanels.get(chooser);
        panel.updateTurn(role);
    }
}
