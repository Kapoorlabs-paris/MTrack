package interactiveMT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class ExtractKymo {

	
	
	public static void ReadFromKymo(final RandomAccessibleInterval<UnsignedByteType> Kymo, File fichier) throws IOException{
		
		
		
            ArrayList<double[]> pointlist = new ArrayList<double[]>();		
			FileWriter fw = new FileWriter(fichier);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(
					"\tDeltaFramenumber\tDeltaLength\n");

			
			Cursor<UnsignedByteType> cursor = Views.iterable(Kymo).localizingCursor();
		
			while(cursor.hasNext()){

				cursor.fwd();
				
				
				
				if (cursor.get().get() > 0){
					final double[] pos = new double[Kymo.numDimensions()];
					
					 pos[0] = cursor.getIntPosition(1);
					 pos[1] = cursor.getDoublePosition(0);
					

					 pointlist.add(pos);
							
							
				}
				
				
				
				}
			
			
			 /********
			 * The part below removes the duplicate entries in the sorted array
			 * (keeps the point with minimum value in other direction)
			 ********/
				
				int j = 0;

				for (int i = 0; i < pointlist.size(); ++i) {
					j = i + 1;
					while (j < pointlist.size()) {

						if (pointlist.get(i)[0] == pointlist.get(j)[0]) {

							pointlist.remove(i);
						}

						else {
							++j;
							
						}

					}

				
			
				
			}
			
			
			for (int index = 0; index < pointlist.size(); ++index){
			bw.write("\t" + (pointlist.get(index)[0]) + "\t" + (pointlist.get(index)[1] - 37) + "\n");
			}
				
			bw.close();
			fw.close();
			
				
	}
			
			
			
			
		
		
		
		
	
	
	public static Img<UnsignedByteType> copytoByteImage(final RandomAccessibleInterval<FloatType> input) {
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it
		// implements
		// the Interval interface
		Normalize.normalize(Views.iterable(input), new FloatType(0), new FloatType(255));
		final UnsignedByteType type = new UnsignedByteType();
		final ImgFactory<UnsignedByteType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
		final Img<UnsignedByteType> output = factory.create(input, type);
		// create a cursor for both images
		RandomAccess<FloatType> ranac = input.randomAccess();
		Cursor<UnsignedByteType> cursorOutput = output.cursor();

		// iterate over the input
		while (cursorOutput.hasNext()) {
			// move both cursors forward by one pixel
			cursorOutput.fwd();

			ranac.setPosition(cursorOutput);
			
			// set the value of this pixel of the output image to the same as
			// the input,
			// every Type supports T.set( T type )
			cursorOutput.get().set((int) ranac.get().get());
		}

		// return the copy
		return output;
	}
	
	public static void main(String[] args) throws IOException{
		new ImageJ();
		String usefolder = IJ.getDirectory("imagej");
		String addToName = "DeltaLKymo2";
		
		RandomAccessibleInterval<FloatType> img = util.ImgLib2Util
				.openAs32Bit(new File("/Users/varunkapoor/Documents/MTAnalysis/20170210/Kymograph4.tif"), new ArrayImgFactory<FloatType>());
		
		
		
		RandomAccessibleInterval<UnsignedByteType> newimg = copytoByteImage(img);
		
		File fichier = new File(usefolder + "//" + addToName + "ID" + 0 + ".txt");
		
		 ReadFromKymo(newimg, fichier);
		
		ImageJFunctions.show(newimg);
		
	}
	
	
}
