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
import java.awt.image.*;
import java.io.*;
import java.util.Locale;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class SimpleDisplayable implements Displayable {
    private Image smallImage, largeImage, gray, smallGray;
    protected static final ImageFilter grayer = new GrayFilter(true, 25);
    protected String key;

    SimpleDisplayable(String name, String key) {
        assert name != null;
        this.key = key;
        URL langDir = getImagesLangDir();
        try {
            try {
                smallImage = ImageIO.read(new URL(langDir.toString() + "/Small/" + name + ".BMP"));
                smallGray = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(smallImage.getSource(), grayer));
            } catch (IOException e) {
                // doesn't have a small image
            }
            largeImage = ImageIO.read(new URL(langDir.toString() + "/Large/" + name + ".BMP"));
            gray = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(largeImage.getSource(), grayer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static URL getImagesLangDir() {
//            Locale loc = Translation.getDefaultLocale();
//            File f = new File("Images/" + loc.getLanguage());
//            if (f.exists()) return f.toURL();
        return SimpleDisplayable.class.getClassLoader().getResource("Images/en");
    }

    public Image getSmallImage() {
        return smallImage;
    }

    public Image getSmallGrayedImage() {
        return smallGray;
    }

    public Image getLargeImage() {
        return largeImage;
    }

    public Image getGrayedImage() {
        return gray;
    }

    public Image getSmallOverlayImage() {
        return null;
    }

    public Image getLargeOverlayImage() {
        return null;
    }

    public void addCardListener(CardListener listener) {
    }

    public void removeCardListener(CardListener listener) {
    }

    public String getTooltip(Locale locale) {
        return Translation.getResource(locale, "name." + key);
    }

    String getKey() {
        return key;
    }
}
