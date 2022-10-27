/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package mt;
import java.awt.*;
import java.awt.event.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class ScrollablePicture extends JLabel implements Scrollable {

    private int maxUnitIncrement = 1;

    public ScrollablePicture(ImageIcon i, int m) {
	super(i);
	maxUnitIncrement = m;
    }

    public Dimension getPreferredScrollableViewportSize() {
	return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

	int currentPosition = 0;
	if (orientation == SwingConstants.HORIZONTAL)
	    currentPosition = visibleRect.x;
	else
	    currentPosition = visibleRect.y;

	if (direction < 0) {
	    int newPosition = currentPosition - (currentPosition / maxUnitIncrement) * maxUnitIncrement;
	    return (newPosition == 0) ? maxUnitIncrement : newPosition;
	} else {
	    return ((currentPosition / maxUnitIncrement) + 1) * maxUnitIncrement - currentPosition;
	}
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
	if (orientation == SwingConstants.HORIZONTAL)
	    return visibleRect.width - maxUnitIncrement;
	else
	    return visibleRect.height - maxUnitIncrement;
    }

    public boolean getScrollableTracksViewportWidth() {
	return false;
    }

    public boolean getScrollableTracksViewportHeight() {
	return false;
    }

    public void setMaxUnitIncrement(int pixels) {
	maxUnitIncrement = pixels;
    }
}
