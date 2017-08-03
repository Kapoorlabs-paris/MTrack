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

	
	
	public static void displayseeds(IntervalView<FloatType> seedimg, ArrayList<Pair<Integer, double[]>> IDALL){
		

		ImagePlus displayimp;

		displayimp = ImageJFunctions.show(seedimg);
		displayimp.setTitle("Display Tracks");

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
