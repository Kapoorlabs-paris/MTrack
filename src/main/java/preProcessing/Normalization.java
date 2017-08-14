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
		String path = "/Volumes/Varun_disk/AnalysisFilesTif";
		String file = "2017-05-24_trops_cy5bovineseeds_cy3_9uM2nd.tif";
		String filepath = "/Volumes/Varun_disk/AnalysisFilesTif/2017-05-24_trops_cy5bovineseeds_cy3_9uM2nd.tif";
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
