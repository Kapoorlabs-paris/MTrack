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
package interactiveMT;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

public class OverlayKymo {
	
	
	
	public static void overlay(final RandomAccessibleInterval<FloatType> kymoimg){
		
		
		
		
		
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		new ImageJ();
		
		ImagePlus kymoimp = new Opener().openImage("/Users/varunkapoor/Documents/20170229/Video4/Kymograph4-1.tif");
		
		RandomAccessibleInterval<FloatType> kymoimg = ImageJFunctions.convertFloat(kymoimp);
		
		
		ArrayList<double[]> lengthtimestart = new ArrayList<double[]>();
		
		
		
		 FileReader fr = new FileReader("/Users/varunkapoor/Documents/20170229/Video4/MT4porcineVKKymoVarun-start0.txt");
		 BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
       int count = 0;
       
       
		while((line = br.readLine())!=null){
			
			
			count++;
			
			if (count > 1){
				
		   String[] numbers = line.split("\\s");
			System.out.println(Double.parseDouble(numbers[0]));
				
			
			}
			
		}
		fr.close();
		br.close();
		
	}

}
