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

import java.io.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class Statistics {
    private static PrintWriter out;
    private static FileWriter fileWriter;

    static void init() {
        File dir = new File("runs");
        if (dir.exists()) {
            int i = 1;
            File f = new File(dir, "run" + i + ".csv");
            while (f.exists()) {
                i++;
                f = new File(dir, "run" + i + ".csv");
            }
            try {
                fileWriter = new FileWriter(f);
                out = new PrintWriter(fileWriter, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void use(String card, String player) {
        if (out != null) out.printf("USE,%s,%s\n", card, player);
    }

    public static void build(String card, String player) {
        if (out != null) out.printf("BUILD,%s,%s\n", card, player);
    }

    public static void score(String player, int score, int tieBreaker) {
        if (out != null) out.printf("SCORE,%s,%d,%d\n", player, score, tieBreaker);
    }

    public static void close() {
        if (out != null) {
            out.flush();
            out.close();
            out = null;
        }
    }
}
