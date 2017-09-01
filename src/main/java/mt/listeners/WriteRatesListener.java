package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import ij.measure.ResultsTable;
import mpicbg.models.Point;
import mt.Tracking;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class WriteRatesListener implements ActionListener {

	final InteractiveRANSAC parent;

	public WriteRatesListener(final InteractiveRANSAC parent) {
		this.parent = parent;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		writeratestofile();

	}

	public double leastX() {

		// Ignore the event starting from zero time
		double minstartX = Double.MAX_VALUE;

		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : parent.segments) {

			final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

			double startX = minMax.getB();

			if (startX <= minstartX) {

				minstartX = startX;

			}

		}

		return minstartX;

	}

	public double leastStart() {

		double minstartY = Double.MAX_VALUE;

		double minstartX = leastX();

		for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : parent.segments) {

			final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

			double startX = minMax.getA();
			Polynomial<?, Point> polynomial = (Polynomial) result.getA();
			double startY = polynomial.predict(startX);

			if (startY <= minstartY && startX != 0) {

				minstartY = startY;

			}

		}

		return minstartY;

	}

	public void writeratestofile() {

		double lifetime = 0;
		String file = parent.inputfile.getName().replaceFirst("[.][^.]+$", "");

		try {
			File ratesfile = new File(parent.inputdirectory + "//" + file + "Rates" + ".txt");
			File frequfile = new File(parent.inputdirectory + "//" + file + "Averages" + ".txt");

			FileWriter fw = new FileWriter(ratesfile);

			BufferedWriter bw = new BufferedWriter(fw);

			FileWriter fwfrequ = new FileWriter(frequfile);

			BufferedWriter bwfrequ = new BufferedWriter(fwfrequ);
			parent.AllMovies.add(parent.inputfile);

			bw.write("\tStartTime (px)\tEndTime(px)\tLinearRateSlope(px)\n");
			bwfrequ.write(
					"\tAverageGrowthrate(px)\tAverageShrinkrate(px)\tCatastropheFrequency(px)\tRescueFrequency(px)\n");
			

			
                          for (int index =  0; index < parent.allrates.size(); ++index){
                        	  
                        	  int startX = parent.allrates.get(index).starttime;
                        	  int endX = parent.allrates.get(index).endtime;
                        	  double linearrate =  parent.allrates.get(index).rate;
                        	  
                        	  
							bw.write("\t" + parent.nf.format(startX) + "\t" + "\t" + parent.nf.format(endX) + "\t"
									+ "\t" + parent.nf.format(linearrate) + "\t" + "\t" + "\t" + "\t" + "\n");
							

						}

				

				
			for (int index = 0; index < parent.averagerates.size(); ++index){

				double averagegrowth = parent.averagerates.get(index).averagegrowth;
				double averageshrink = parent.averagerates.get(index).averageshrink;
				double catfrequ = parent.averagerates.get(index).catfrequ;
				double resfrequ = parent.averagerates.get(index).resfrequ;
			

			bwfrequ.write("\t" + parent.nf.format(averagegrowth) + "\t" + "\t" + "\t" + "\t"
					+ parent.nf.format(averageshrink) + "\t" + "\t" + "\t" + parent.nf.format(catfrequ) + "\t" + "\t"
					+ "\t" + parent.nf.format(resfrequ)

					+ "\n" + "\n");
			}
			bw.close();
			fw.close();

			bwfrequ.close();
			fwfrequ.close();
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
