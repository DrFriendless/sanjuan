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
public class SmallCardDisplay <E extends Card> extends JPanel {
    public static final Dimension SMALL_DIMENSION = new Dimension(64, 98);
    private E card;
    private Object id;
    private boolean selected;
    private boolean disabled;
    private boolean selectable;
    private MediaTracker tracker = new MediaTracker(this);
    private CardListener redraw = new CardListener() {
        public void cardChanged(Displayable card) {
            repaint();
        }
    };

    SmallCardDisplay(Object id, boolean selectable) {
        this.id = id;
        this.selectable = selectable;
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!SmallCardDisplay.this.selectable) return;
                click();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                mouseOver();
            }
        });
    }

    public Dimension getPreferredSize() {
        return SMALL_DIMENSION;
    }

    public synchronized void setCard(E card) {
        if (this.card != null) this.card.removeCardListener(redraw);
        this.card = card;
        setToolTipText(card == null ? "" : card.toString());
        if (this.card != null) this.card.addCardListener(redraw);
        repaint();
    }

    protected synchronized void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (card != null) {
            Image image = disabled ? card.getSmallGrayedImage() : card.getSmallImage();
            tracker.addImage(image, 0);
            g.drawImage(image, 0, 0, this);
            try {
                tracker.waitForAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tracker.removeImage(image);
            Image overlay = card.getSmallOverlayImage();
            if (overlay != null) {
                int w = image.getWidth(this);
                int h = image.getHeight(this);
                tracker.addImage(overlay, 1);
                try {
                    tracker.waitForAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                g.drawImage(overlay, w/4, h/4, this);
                tracker.removeImage(overlay);
            }
            if (selected) {
                g.setColor(Color.black);
                g.drawRect(0, 0, image.getWidth(this) - 1, image.getHeight(this) - 1);
                g.drawRect(1, 1, image.getWidth(this) - 3, image.getHeight(this) - 3);
            }
        }
    }

    Object getId() {
        return id;
    }

    synchronized E getCard() {
        return card;
    }

    public void toggleSelected() {
        selected = !selected;
        repaint();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void click() {
        // override me
    }

    public void mouseOver() {
        // override me
    }
}
