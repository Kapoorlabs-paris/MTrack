package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import mpicbg.models.Point;
import mt.Tracking;
import net.imglib2.util.Pair;

public class WriteRatesListener implements ActionListener {
	
	
	final InteractiveRANSAC parent;
	
	public WriteRatesListener( final InteractiveRANSAC parent)
	{
		this.parent = parent;
	}

	
	@Override
	public void actionPerformed( final ActionEvent arg0 )
	{
		

		String inputfile = parent.inputfile.getName().replaceFirst("[.][^.]+$", "");
		try {
		File ratesfile = new File(parent.inputdirectory + "//" + inputfile + "Rates" + ".txt");
		
		
		FileWriter fw = new FileWriter(ratesfile);
		
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("\tStartTime (px)\tEndTime(px)\tLinearRate(px)\n");
		for ( final Pair< AbstractFunction2D, ArrayList< PointFunctionMatch > > result : parent.segments )
		{
			
				final Pair< Double, Double > minMax = Tracking.fromTo( result.getB() );
		
				double startX = minMax.getA();
				double endX = minMax.getB();
				Polynomial< ?, Point > polynomial = (Polynomial)result.getA();
				double startY = polynomial.predict(startX);
				double endY = polynomial.predict(endX);
				
				double linearrate = (endY - startY) / (endX - startX);
		
				
		
			
			
			
			
			
			bw.write("\t" + parent.nf.format(startX) + "\t" + "\t"
			              + parent.nf.format(endX)+ "\t" + "\t"
			              + parent.nf.format(linearrate) + "\n");
		
			
	
	}
		
		
		
		
		bw.close();
		fw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
}
