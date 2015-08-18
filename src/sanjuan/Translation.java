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
import java.text.MessageFormat;


/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class Translation {
    private static Map languages = new HashMap();
    private static Locale defaultLocale;

    public static void setDefaultLocale(Locale locale) {
        defaultLocale = locale;
    }

    public static String getResource(Locale locale, String key) {
        ResourceBundle lang = (ResourceBundle) languages.get(locale);
        if (lang == null) {
            lang = ResourceBundle.getBundle("Resources", locale);
            languages.put(locale, lang);
        }
        return lang.getString(key);
    }

    public static String formatInDefaultLanguage(String key, Object ... params) {
        return MessageFormat.format(inDefaultLanguage(key), params);
    }

    public static String inDefaultLanguage(String key) {
        assert defaultLocale != null;
        return getResource(defaultLocale, key);
    }

    static Locale getDefaultLocale() {
        return defaultLocale;
    }
}
