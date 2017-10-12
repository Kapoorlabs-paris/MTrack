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
