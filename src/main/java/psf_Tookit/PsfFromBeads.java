package psf_Tookit;

import houghandWatershed.WatershedDistimg;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import preProcessing.GetLocalmaxmin;
import preProcessing.GlobalThresholding;

public class PsfFromBeads {

	private final RandomAccessibleInterval<FloatType> inputimg;
	private final int ndims;
	int Maxlabel;
	RandomAccessibleInterval<IntType> intimg;
	public PsfFromBeads(final RandomAccessibleInterval<FloatType> inputimg){
		
		this.inputimg = inputimg;
		this.ndims = inputimg.numDimensions();
		
	}
	
	
	public void  ExtractPSF(){
		
		final Float ThresholdValue = GlobalThresholding.AutomaticThresholding(inputimg);
		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(inputimg, new BitType());
		GetLocalmaxmin.ThresholdingBit(inputimg, bitimg, ThresholdValue);
		
		WatershedDistimg<FloatType> WaterafterDisttransform = new WatershedDistimg<FloatType>(inputimg, bitimg);
		WaterafterDisttransform.checkInput();
		WaterafterDisttransform.process();
		intimg = WaterafterDisttransform.getResult();
		Maxlabel = WaterafterDisttransform.GetMaxlabelsseeded(intimg);
		
		
		
		
	}
	
	
	
}
