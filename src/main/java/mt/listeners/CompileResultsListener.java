package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
		//	File ratesfile = new File(parent.inputdirectory + "//"  + "AllRates" + ".txt");
			File frequfile = new File(parent.inputdirectory + "//"  + "AllAverages" + ".txt");

		//	FileWriter fw = new FileWriter(ratesfile);

		//	BufferedWriter bw = new BufferedWriter(fw);

			FileWriter fwfrequ = new FileWriter(frequfile);

			BufferedWriter bwfrequ = new BufferedWriter(fwfrequ);
			
            
		//	bw.write("\tStartTime (px)\tEndTime(px)\tLinearRateSlope(px)\tFileName\n");
			bwfrequ.write(
					"\tAverageGrowthrate(px)\tAverageShrinkrate(px)\tCatastropheFrequency(px)\tRescueFrequency(px)\tFileName\n");
			/*

			for (Map.Entry<Integer, ArrayList<Rateobject>>  allrates: parent.Compilepositiverates.entrySet()){
				
				
				int key = allrates.getKey();
				ArrayList<Rateobject> rateobject = allrates.getValue();
				String File = parent.inputfiles[key].getName();
			
				for (int index = 0; index < rateobject.size(); ++index){
				
                        	  int startX = rateobject.get(index).starttime;
                        	  int endX = rateobject.get(index).endtime;
                        	  double linearrate =  rateobject.get(index).rate;
                        	  
                        	  
                        	 
                        	  
                        	
							bw.write("\t" + parent.nf.format(startX) + "\t" + "\t" + parent.nf.format(endX) + "\t"
									+ "\t" + parent.nf.format(linearrate) + "\t" + "\t" + File   + "\t" + "\t" + "\n");
							
							
							
							

						}
				
				 if (parent.Compilenegativerates.get(key)!=null){
              		  
					ArrayList<Rateobject> newrateobject = parent.Compilenegativerates.get(key);
					for (int index = 0; index < rateobject.size(); ++index){
					
						String newFile = parent.inputfiles[key].getName();
					
		                        	  int newstartX = newrateobject.get(index).starttime;
		                        	  int newendX = newrateobject.get(index).endtime;
		                        	  double newlinearrate =  newrateobject.get(index).rate;
		                        	  
		                        	  
		                        	 
		                        	  
		                        	
									bw.write("\t" + parent.nf.format(newstartX) + "\t" + "\t" + parent.nf.format(newendX) + "\t"
											+ "\t" + parent.nf.format(newlinearrate) + "\t" + "\t" + newFile   + "\t" + "\t" + "\n");  
        		  
					}
        	  }
				
				
				
			}
			*/
				
			for (Map.Entry<Integer, Averagerate>  allaverage: parent.Compileaverage.entrySet()){
				
				int key = allaverage.getKey();
				Averagerate rateobject = allaverage.getValue();
				String File = parent.inputfiles[key].getName();
			
				
			
				double averagegrowth = rateobject.averagegrowth;
				double averageshrink = rateobject.averageshrink;
				double catfrequ = rateobject.catfrequ;
				double resfrequ = rateobject.resfrequ;
			

			bwfrequ.write("\t" + parent.nf.format(averagegrowth) + "\t" + "\t" + "\t" + "\t"
					+ parent.nf.format(averageshrink) + "\t" + "\t" + "\t" + parent.nf.format(catfrequ) + "\t" + "\t"
					+ "\t" + parent.nf.format(resfrequ) + "\t" + "\t"
							+ File + "\t" + "\t"

					+ "\n" + "\n");
			}
		//	bw.close();
       	//		fw.close();

			bwfrequ.close();
			fwfrequ.close();
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
