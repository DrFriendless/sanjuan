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

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class RoleCard extends SimpleDisplayable implements Selectable {
    private Role role;

    public RoleCard(Role role) {
        super(role.getName(), role.getName().toLowerCase());
        this.role = role;
    }

    Role getRole() {
        return role;
    }

    public void drawSelected(Graphics g, boolean selected) {
        if (!selected) {
            g.setColor(Color.LIGHT_GRAY);
            for (int i=0; i<5; i++) {
                g.drawRect(i, i, 149 - i * 2, 223 - i * 2);
            }
        }
    }

    public String toString() {
        return role.toString();
    }
}
