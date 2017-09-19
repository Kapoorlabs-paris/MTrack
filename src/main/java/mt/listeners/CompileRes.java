package mt.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import mt.Averagerate;
import mt.Rateobject;

public class CompileRes {


	final InteractiveRANSAC parent;

	public CompileRes(final InteractiveRANSAC parent) {
		this.parent = parent;
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
			bw.write("\tStartTime \tEndTime\tLinearRateSlope\tFileName\n");
			bwfrequ.write(
					"\tAverageGrowthrate\tAverageShrinkrate\tCatastropheFrequency\tRescueFrequency"
					+ "\tGrowth events\tShrink events\tCatastrophe events\tRescue events"
					+ "\tFileName\n");
			

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
				
			
				
				
				
			}
			
				
			for (Map.Entry<Integer, Averagerate>  allaverage: parent.Compileaverage.entrySet()){
				
				int key = allaverage.getKey();
				Averagerate rateobject = allaverage.getValue();
				String File = parent.inputfiles[key].getName();
			
				
			
				double averagegrowth = rateobject.averagegrowth;
				double averageshrink = rateobject.averageshrink;
				double catfrequ = rateobject.catfrequ;
				double resfrequ = rateobject.resfrequ;
				int growthevent = rateobject.growthevent;
				int shrinkevent = rateobject.shrinkevent;
				int catevent = rateobject.catevent;
				int resevent = rateobject.resevent;
			

			bwfrequ.write("\t" + parent.nf.format(averagegrowth) + "\t" + "\t" + "\t" + "\t"
					+ parent.nf.format(averageshrink) + "\t" + "\t" + "\t" + parent.nf.format(catfrequ) + "\t" + "\t"
					+ "\t" + parent.nf.format(resfrequ) + "\t" + "\t"
					+ parent.nf.format(growthevent) + "\t" + "\t" + "\t" + parent.nf.format(shrinkevent) + "\t" + "\t"
					+ "\t" + parent.nf.format(catevent) + "\t" + "\t"+ parent.nf.format(resevent) + "\t" + "\t"
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
