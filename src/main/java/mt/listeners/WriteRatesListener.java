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
			File frequfile = new File(parent.inputdirectory + "//" + inputfile + "Frequency" + ".txt");
			
			FileWriter fw = new FileWriter(ratesfile);

			BufferedWriter bw = new BufferedWriter(fw);
			
			
			FileWriter fwfrequ = new FileWriter(frequfile);

			BufferedWriter bwfrequ = new BufferedWriter(fwfrequ);
			bwfrequ.write("\tCatastropheFrequency(px)\n");
			
			bw.write("\tStartTime (px)\tEndTime(px)\tLinearRateSlope(px)\n");
			
			int count = 0;
			double timediff = 0;
		
			for (final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> result : parent.segments) {

				final Pair<Double, Double> minMax = Tracking.fromTo(result.getB());

				double startX = minMax.getA();
				double endX = minMax.getB();
				
				Polynomial<?, Point> polynomial = (Polynomial) result.getA();
				
                LinearFunction linear = new LinearFunction();
				LinearFunction.slopeFits( result.getB(), linear, parent.minSlope, parent.maxSlope ) ;
				
				
				double linearrate = linear.getCoefficient(1); 
				
				
				if (linearrate > 0){
					
					
					count++;
					timediff += endX - startX;
				}
				
				
				

				bw.write("\t" + parent.nf.format(startX) + "\t" + "\t" + parent.nf.format(endX) + "\t" + "\t"
						+ parent.nf.format(linearrate) + "\t" + "\t" + "\t" + "\t"
						+ "\n");

			}
			
			if (count > 0){
			bwfrequ.write("\t" + parent.nf.format(count/timediff)+ "\n" + "\n");
			}
			
			
		
			
		

			bw.close();
			fw.close();
			
			bwfrequ.close();
			fwfrequ.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
