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
import java.util.Locale;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public interface Displayable {
    public Image getSmallImage();

    public Image getLargeImage();

    public Image getGrayedImage();

    public Image getSmallGrayedImage();

    public Image getSmallOverlayImage();

    public Image getLargeOverlayImage();

    public void addCardListener(CardListener listener);

    public void removeCardListener(CardListener listener);

    public String getTooltip(Locale locale);
}
