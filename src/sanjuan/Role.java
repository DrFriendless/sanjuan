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
public class Role {
    public static final Role GOVERNOR = new Role("Governor");
    public static final Role BUILDER = new Role("Builder");
    public static final Role COUNCILLOR = new Role("Councillor");
    public static final Role PROSPECTOR = new Role("Prospector");
    public static final Role PRODUCER = new Role("Producer");
    public static final Role TRADER = new Role("Trader");

    public static final Role[] ALL_ROLES = { BUILDER, COUNCILLOR, PROSPECTOR, PRODUCER, TRADER };

    private String name;

    private Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isBuilder() {
        return this == BUILDER;
    }

    public String toString() {
        return Translation.inDefaultLanguage("name." + name.toLowerCase());
    }
}
