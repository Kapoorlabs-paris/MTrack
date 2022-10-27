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
package swingClasses;

import java.awt.Color;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class DisplayID {

	
	
	public static void displayseeds(String name, IntervalView<FloatType> seedimg, ArrayList<Pair<Integer, double[]>> IDALL){
		

		ImagePlus displayimp;

		displayimp = ImageJFunctions.show(seedimg);
		displayimp.setTitle(name + "Display Tracks");

		Overlay o = displayimp.getOverlay();

		if (displayimp.getOverlay() == null) {
			o = new Overlay();
			displayimp.setOverlay(o);
		}

		o.clear();

		for (int index = 0; index < IDALL.size(); ++index) {

			Line newellipse = new Line(IDALL.get(index).getB()[0], IDALL.get(index).getB()[1],
					IDALL.get(index).getB()[0], IDALL.get(index).getB()[1]);

				newellipse.setStrokeColor(Color.WHITE);
				newellipse.setStrokeWidth(1);
				newellipse.setName("TrackID: " + IDALL.get(index).getA());

				o.add(newellipse);

				o.drawLabels(true);

				o.drawNames(true);
			

		
		}
		
		
	}
	
}
