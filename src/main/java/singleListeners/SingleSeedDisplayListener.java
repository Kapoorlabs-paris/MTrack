/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
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
package singleListeners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

public class SingleSeedDisplayListener implements ActionListener {

	final JComboBox<String> cb;
	final RandomAccessibleInterval<FloatType> seedimg;
	final Interactive_MTSingleChannel parent;
	
	public SingleSeedDisplayListener(JComboBox<String> cb, RandomAccessibleInterval<FloatType> seedimg, final Interactive_MTSingleChannel parent) {

		this.cb = cb;
		this.seedimg = seedimg;
		this.parent = parent;

	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.displayselectedSeed = cb.getSelectedIndex();

		ImagePlus displayimp;

		displayimp = ImageJFunctions.show(seedimg);
		displayimp.setTitle("Display Tracks");

		Overlay o = displayimp.getOverlay();

		if (displayimp.getOverlay() == null) {
			o = new Overlay();
			displayimp.setOverlay(o);
		}

		o.clear();

		for (int index = 0; index < parent.IDALL.size(); ++index) {

			Line newellipse = new Line(parent.IDALL.get(index).getB()[0], parent.IDALL.get(index).getB()[1],
					parent.IDALL.get(index).getB()[0], parent.IDALL.get(index).getB()[1]);

			if (parent.displayselectedSeed == 0) {
				newellipse.setStrokeColor(Color.WHITE);
				newellipse.setStrokeWidth(1);
				newellipse.setName("TrackID: " + parent.IDALL.get(index).getA());

				o.add(newellipse);

				o.drawLabels(true);

				o.drawNames(true);
			} else if (parent.displayselectedSeed == index + 1) {

				newellipse.setStrokeColor(Color.WHITE);
				newellipse.setStrokeWidth(1);
				newellipse.setName("TrackID: " + parent.IDALL.get(index).getA());

				o.add(newellipse);

				o.drawLabels(true);

				o.drawNames(true);

			}

		}

	}

}


