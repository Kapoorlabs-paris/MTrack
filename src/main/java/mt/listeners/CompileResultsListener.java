package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import ij.measure.ResultsTable;
import mpicbg.models.Point;
import mt.Averagerate;
import mt.Rateobject;
import mt.Tracking;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class CompileResultsListener implements ActionListener {

	final InteractiveRANSAC parent;

	public CompileResultsListener(final InteractiveRANSAC parent) {
		this.parent = parent;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		compileresults();

	}

	
	public void compileresults() {



		try {
			File ratesfile = new File(parent.inputdirectory + "//"  + "AllRates" + ".txt");
			File frequfile = new File(parent.inputdirectory + "//"  + "AllAverages" + ".txt");

			FileWriter fw = new FileWriter(ratesfile);

			BufferedWriter bw = new BufferedWriter(fw);

			FileWriter fwfrequ = new FileWriter(frequfile);

			BufferedWriter bwfrequ = new BufferedWriter(fwfrequ);
			
			parent.AllMoviesB.add(parent.inputfile);
			bw.write("\tStartTime (real)\tEndTime(real)\tLinearRateSlope(real)\tFileName\n");
			bwfrequ.write(
					"\tAverageGrowthrate(real)\tAverageShrinkrate(real)\tCatastropheFrequency(real)\tRescueFrequency(real)"
					+ "\tGrowth events \tShrink events\tRescue events\tCatastrophe events"
					+ "\tFileName\n");
			

			for (Map.Entry<Integer, ArrayList<Rateobject>>  allrates: parent.Compilepositiverates.entrySet()){
				
				
				int key = allrates.getKey();
				ArrayList<Rateobject> rateobject = allrates.getValue();
				String File = parent.inputfiles[key].getName();
			
				for (int index = 0; index < rateobject.size(); ++index){
				
                        	  int startX = rateobject.get(index).starttime;
                        	  int endX = rateobject.get(index).endtime;
                        	  double linearrate =  rateobject.get(index).rate;
                        	  
                        	  
                        	 
                        	  
                        	
							bw.write("\t" + new DecimalFormat("#.####").format(startX) + "\t" + "\t" + new DecimalFormat("#.####").format(endX) + "\t"
									+ "\t" + new DecimalFormat("#.####").format(linearrate) + "\t" + "\t" + File   + "\t" + "\t" + "\n");
							
							
							
							

						}
				
			
				
				
				
			}
			
				
			for (Map.Entry<Integer, Averagerate>  allaverage: parent.Compileaverage.entrySet()){
				
				Averagerate rateobject = allaverage.getValue();
				String File = rateobject.file.getName();
			
				
				double averagegrowth = rateobject.averagegrowth;
				double averageshrink = rateobject.averageshrink;
				double catfrequ = rateobject.catfrequ;
				double resfrequ = rateobject.resfrequ;
				int growevent = rateobject.growthevent;
				int shrinkevent = rateobject.shrinkevent;
				int resevent = rateobject.resevent;
				int catevent = rateobject.catevent;
			

			bwfrequ.write("\t" + new DecimalFormat("#.####").format(averagegrowth) + "\t" + "\t" + "\t" + "\t"
					+ new DecimalFormat("#.####").format(averageshrink) + "\t" + "\t" + "\t" + new DecimalFormat("#.####").format(catfrequ) + "\t" + "\t"
					+ "\t" + new DecimalFormat("#.####").format(resfrequ) + "\t" + "\t"
					+ new DecimalFormat("#.####").format(growevent)  + "\t" + "\t" + new DecimalFormat("#.####").format(shrinkevent) + "\t" + "\t"
					+ "\t" + new DecimalFormat("#.####").format(resevent) + "\t" + "\t" + new DecimalFormat("#.####").format(catevent) + "\t" + "\t"
							+ File + "\t" + "\t"

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
