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
