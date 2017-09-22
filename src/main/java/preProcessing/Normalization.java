package preProcessing;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Normalization {

	
	
	public static void main(String[] args){
		
		
		new ImageJ();
		String path = "/Users/varunkapoor/Google Drive/Fast_Movies/CheaterMovies";
		String file = "2017-06-07_laevis_cy5bovineseeds_cy3_9uM2nd_Shrunk.tif";
		String filepath = "/Users/varunkapoor/Google Drive/Fast_Movies/CheaterMovies/2017-06-07_laevis_cy5bovineseeds_cy3_9uM2nd_Shrunk.tif";
		ImagePlus impA = new ImagePlus( filepath );
		RandomAccessibleInterval<FloatType> img = ImageJFunctions.convertFloat(impA);
		new Normalize();
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(img), minval, maxval); 
		
		ImageJFunctions.show(img);
	  FileSaver fs = new FileSaver(ImageJFunctions.show(img));
		fs.saveAsTiffStack(path+file);
		
	}
	
}
