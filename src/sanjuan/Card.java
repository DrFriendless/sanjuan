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


/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class Card extends SimpleDisplayable {
    private Vector<CardListener> listeners = new Vector<CardListener>();
    private String name;
    private int vps, cost, production;
    private boolean monument;

    Card(String name, String key, int vps, int cost, int production, boolean monument) {
        super(name, key);
        this.name = name;
        this.vps = vps;
        this.cost = cost;
        this.production = production;
        this.monument = monument;
    }

    String getName() {
        return name;
    }

    int getCost() {
        return cost;
    }

    int getVPs() {
        return vps;
    }

    boolean isProduction() {
        return production > 0;
    }

    int getProductionIndex() {
        return production - 1;
    }

    boolean isViolet() {
        return !isProduction();
    }

    boolean isMonument() {
        return monument;
    }

    boolean isChapel() {
        return name.equals("Chapel");
    }

    public String toString() {
        return Translation.inDefaultLanguage("card." + key);
    }

    public double getAverageTradeWorth() {
        return (new double[] { 0.0, 1.0, 1.4, 1.8, 2.2, 2.6 })[production];
    }

    public Image getSmallOverlayImage() {
        return null;
    }

    public Image getLargeOverlayImage() {
        return null;
    }

    public void addCardListener(CardListener listener) {
        listeners.add(listener);
    }

    public void removeCardListener(CardListener listener) {
        listeners.remove(listener);
    }

    protected void fireCardChanged() {
        java.util.List<CardListener> copy;
        synchronized (listeners) {
            copy = new ArrayList<CardListener>(listeners);
        }
        for (CardListener cardListener : copy) {
            cardListener.cardChanged(this);
        }
    }
}
