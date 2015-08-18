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

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class TurnDetails {
    private Role role;
    private boolean privilege;
    private boolean usedLibraryPrivilege;

    TurnDetails(Role role, boolean privilege, boolean usedLibraryPrivilege) {
        this.role = role;
        this.privilege = privilege;
        this.usedLibraryPrivilege = usedLibraryPrivilege;
    }

    public boolean isBuilder() {
        return role.isBuilder();
    }

    public boolean hasPrivilege() {
        return privilege;
    }

    public String getText() {
        return role.getName();
    }

    public boolean usedLibraryPrivilege() {
        return usedLibraryPrivilege;
    }

    public void useLibraryPrivilege() {
        this.usedLibraryPrivilege = true;
    }
}
