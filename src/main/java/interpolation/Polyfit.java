/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
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
package interpolation;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import Jama.Matrix;
import Jama.QRDecomposition;
import mt.Tracking;
import mt.Util;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class Polyfit {

	private final int degree;
	private final int Npoints;
	private final Matrix Coefficients;
	 private double SSE;
	    private double SST;
	    
	public Polyfit(double[] x, double[] y, int degree){
		
		
		this.degree = degree;
		Npoints = x.length;
		
		// Vandermonde matrix 
		double[][] vandermonde = new double[Npoints][degree+1];
        for (int i = 0; i < Npoints; i++) {
            for (int j = 0; j <= degree; j++) {
                vandermonde[i][j] = Math.pow(x[i], j);
            }
        }
        Matrix X = new Matrix(vandermonde);

        // create matrix from vector
        Matrix Y = new Matrix(y, Npoints);
        
     // find least squares solution
        QRDecomposition qr = new QRDecomposition(X);
        Coefficients = qr.solve(Y);
        
        
        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < Npoints; i++)
            sum += y[i];
        double mean = sum / Npoints;
        
        
        // total variation to be accounted for
        for (int i = 0; i < Npoints; i++) {
            double dev = y[i] - mean;
            SST += dev*dev;
        }

        // variation not accounted for
        Matrix residuals = X.times(Coefficients).minus(Y);
        SSE = residuals.norm2() * residuals.norm2();
        
		
	}
	public double GetCoefficients(int j) {
        return Coefficients.get(j, 0);
    }

    public int degree() {
        return degree;
    }

    public double R2() {
        return 1.0 - SSE/SST;
    }

    // Horner's method to get y values correspoing to x
    public double predict(double x) {
        // horner's method
        double y = 0.0;
        for (int j = degree; j >= 0; j--)
            y = GetCoefficients(j) + (x * y);
        return y;
    }
    
    // Horner's method to get y values correspoing to x
    public double predictderivative(double x) {
        // horner's method for derivative of a function
        double y = 0;
        for (int j = degree - 1; j >= 0; j--)
            y = (j + 1) * GetCoefficients(j + 1) + (x * y);
        return y;
    }
    
    // Horner's method to get y values correspoing to x
    public double predictsecderivative(double x) {
        // horner's method for derivative of a function
        double y = 0;
        for (int j = degree - 2; j >= 0; j--)
            y = j * (j + 1) * GetCoefficients(j + 1) + (x * y);
        System.out.println(y);
        return y;
    }
    
    public static ArrayList< Pair< Integer, Double > > loadsimple( final File file )
	{
		final ArrayList< Pair< Integer, Double > > points = new ArrayList< Pair< Integer, Double > >();
		final ArrayList< Pair< Integer, Double > > normalpoints = new ArrayList< Pair< Integer, Double > >();
		try
		{
			BufferedReader in = Util.openFileRead( file );

			while( in.ready() )
			{
				String line = in.readLine().trim();

				while ( line.contains( "\t\t" ) )
					line = line.replaceAll( "\t\t", "\t" );

				if ( line.length() >= 3 && line.matches( "[0-9].*" ) )
				{
					final String[] split = line.trim().split( "\t" );

					final int frame = (int)Double.parseDouble( split[ 0 ] );
					final double length = Double.parseDouble( split[ 1 ] );

					points.add( new ValuePair< Integer, Double >( frame, length ) );
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return null;
		}

		double maxlength = Double.MIN_VALUE;
		for( Pair< Integer, Double> point:points){
			
			double length = point.getB();
			if ( length > maxlength)
				maxlength = length;
		}
		for( Pair< Integer, Double> point:points){
			Pair< Integer, Double> newpoint = new ValuePair< Integer, Double >(point.getA() , point.getB()  );
			normalpoints.add(newpoint);
		}
		
		
		return normalpoints;
	}
    public static void main(String[] args) {
    
    	final ArrayList< Pair< Integer, Double > > mts = loadsimple( new File( "/Users/varunkapoor/Documents/Ines_Fourier/Cell39.txt" ) );

    	final ArrayList< Pair< Integer, Double > > mtspoly = new ArrayList< Pair< Integer, Double > >();
    	
        double[] x = new double[mts.size()];
        double[] y = new double[mts.size()];
        
        int i = 0;
        for ( Pair< Integer, Double > point: mts){
        	
        	x[i] = point.getA();
        	y[i] = point.getB();
        	i++;
        }
        int degree = 20;
        Polyfit regression = new Polyfit(x, y, degree);
        for ( double t = x[0]; t <=x[x.length - 1] ; ++t )
		{
          double poly = regression.predict(t);
          
          mtspoly.add(new ValuePair< Integer, Double>((int)t, poly));
		}
        
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(Tracking.drawPoints(mtspoly, new double[]{1,1,1}, "Function fit"));
        dataset.addSeries(Tracking.drawPoints(mts, new double[]{1,1,1} ,"Original Data"));
     
        
       JFreeChart  chart = Tracking.makeChart(dataset);
       Tracking.display(chart, new Dimension(500, 400));
   	Tracking.setColor(chart, i, new Color(255, 0, 0));
	Tracking.setStroke(chart, i, 0.5f);
       
       for(int j = degree; j >=0; --j)
        System.out.println(regression.GetCoefficients(j)  + " *x power  " + j );
    }
	
}
