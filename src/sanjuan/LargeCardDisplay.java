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
import java.awt.event.*;
import javax.swing.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class LargeCardDisplay <E extends Displayable> extends JPanel implements CardListener {
    static final Dimension LARGE_DIMENSION = new Dimension(150, 224);
    private E card;
    private boolean grayed, selected, selectable;
    private CardListener redraw = new CardListener() {
        public void cardChanged(Displayable card) {
            repaint();
        }
    };

    public LargeCardDisplay(boolean selectable) {
        this.selectable = selectable;
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!LargeCardDisplay.this.selectable) return;
                click();
            }
        });
    }

    public Dimension getPreferredSize() {
        return LARGE_DIMENSION;
    }

    public void setCard(E card) {
        if (this.card != null) this.card.removeCardListener(redraw);
        this.card = card;
        setToolTipText(card == null ? "" : card.toString());
        if (this.card != null) this.card.addCardListener(redraw);
        repaint();
    }

    public void cardChanged(Displayable card) {
        setCard((E) card);
    }

    protected void paintComponent(Graphics g) {
        if (card != null) {
            Image use;
            if (grayed) {
                use = card.getGrayedImage();
            } else {
                use = card.getLargeImage();
            }
            g.drawImage(use, 0, 0, this);
            Image overlay = card.getLargeOverlayImage();
            if (overlay != null) {
                int w = use.getWidth(this);
                int h = use.getHeight(this);
                g.drawImage(overlay, w / 4, h / 4, this);
            }
            if (card instanceof Selectable) {
                ((Selectable) card).drawSelected(g, selected);
            }
        } else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public E getCard() {
        return card;
    }

    public void setGrayed(boolean grayed) {
        this.grayed = grayed;
        if (grayed) selectable = false;
        repaint();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public void click() {
    }
}
