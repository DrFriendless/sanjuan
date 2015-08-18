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

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public interface AI {
    public Cards<Card> chooseCardsToKeep(Cards<Card> toChooseFrom, TurnDetails turn, int count);

    public Card chooseCardToKeep(Cards<Card> toChooseFrom, TurnDetails turn);

    public Card chooseCardToBuild(TurnDetails turn);

    public Cards<PlayedCard> chooseCardsToTrade(Cards<PlayedCard> buildingsWithGoods, int count);

    public Role chooseRole(Set<Role> available, boolean usedLibraryPrivilege);

    public Card chooseCardToBury(int positionNextRound);

    public String getBaseName();
}
