package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import mpicbg.models.Point;
import mt.Tracking;
import net.imglib2.util.Pair;

public class WriteRatesListener implements ActionListener {

	final InteractiveRANSAC parent;

	public WriteRatesListener(final InteractiveRANSAC parent) {
		this.parent = parent;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		String inputfile = parent.inputfile.getName().replaceFirst("[.][^.]+$", "");
		try {
			File ratesfile = new File(parent.inputdirectory + "//" + inputfile + "Rates" + ".txt");

			FileWriter fw = new FileWriter(ratesfile);

			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\tStartTime (px)\tEndTime(px)\tLinearRateSlope(px)\tLinearRateRegPolynomial(px)\tLinearityFraction(1 = Fully Linear)\n");
			
			int count = 0;
			double timediff = 0;
			
			for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : parent.segments) {

				final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

				double startX = minMax.getA();
				double endX = minMax.getB();
				
				Polynomial<?, Point> polynomial = (Polynomial) result.getA();
				double startY = polynomial.predict(startX);
				double endY = polynomial.predict(endX);

				
				
				
				double linearrate = (endY - startY) / (endX - startX);
				
				
				if (linearrate > 0){
					
					
					count++;
					timediff += endX - startX;
				}
				
				
				

				bw.write("\t" + parent.nf.format(startX) + "\t" + "\t" + parent.nf.format(endX) + "\t" + "\t"
						+ parent.nf.format(linearrate) + "\t" + "\t" + "\t" + "\t"
						
						+ parent.nf.format(polynomial.getCoefficient(1)) + "\t" + "\t" + "\t" + "\t"
						
                        + parent.nf.format(parent.lambda) + "\t" + "\t"
						
						+ "\n" + "\n");

			}
			
			if (count > 0){
			bw.write("\tCatastrophe Frequency (px)");
			bw.write("\t" + (count/timediff)+ "\n" + "\n");
			}
			
			
			bw.write("\tPolynomial\n");
			for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : parent.segments) {

				Polynomial<?, Point> polynomial = (Polynomial) result.getA();
				int degree = polynomial.degree();
				for (int i = degree; i >= 0; --i) {


						bw.write("\t" + parent.nf.format(polynomial.getCoefficient(i)) + "X to the power of " + i);

				}
				bw.write("\n");
			}
			
		

			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
