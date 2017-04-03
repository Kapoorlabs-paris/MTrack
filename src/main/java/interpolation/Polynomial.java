package interpolation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import Jama.Matrix;
import Jama.QRDecomposition;
import mpicbg.models.IllDefinedDataPointsException;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;

public class Polynomial extends AbstractFunction<Polynomial> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5010369758205651325L;
	final int minNumPoints;
	int degree;
	
	private double SSE;
	private double SST;
	public final double[] coeff;

	public Polynomial(final int degree) {

		this.degree = degree;
		this.minNumPoints = degree + 1;
		this.coeff = new double[degree + 1];
	}

	/**
	 * @return - the coefficients of the polynomial in x
	 */
	public double getCoefficients(final int j) {
		return coeff[j];
	}

	@Override
	public int getMinNumPoints() {
		return minNumPoints;
	}

	/*
	 * 
	 * This is a fit function for the polynomial of user chosen degree
	 * 
	 */
	public void fitFunction(final Collection<Point> points) throws NotEnoughDataPointsException {
		Matrix coefficients;
		final int Npoints = points.size();
		if (Npoints < minNumPoints)
			throw new NotEnoughDataPointsException("Not enough points, at least " + minNumPoints + " are necessary.");
		double[] y = new double[Npoints];
		double[] x = new double[Npoints];

		int count = 0;
		for (final Point p : points) {
			x[count] = p.getW()[0];
			y[count] = p.getW()[1];
			count++;
		}

		// Vandermonde matrix
		double[][] vandermonde = new double[Npoints][degree + 1];
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
		coefficients = qr.solve(Y);

		// mean of y[] values
		double sum = 0.0;
		for (int i = 0; i < Npoints; i++)
			sum += y[i];
		double mean = sum / Npoints;

		// total variation to be accounted for
		for (int i = 0; i < Npoints; i++) {
			double dev = y[i] - mean;
			SST += dev * dev;
		}

		// variation not accounted for
		Matrix residuals = X.times(coefficients).minus(Y);
		SSE = residuals.norm2() * residuals.norm2();

		for (int j = degree; j >= 0; j--) {
			this.coeff[j] = coefficients.get(j, 0);

		}

	}

	// Distance of a point from a polynomial
	@Override
	public double distanceTo(final Point point) {
		final double x1 = point.getW()[0];
		final double y1 = point.getW()[1];

		
		if (degree == 1)
			return Math.abs(y1 - coeff[1]*x1 - coeff[0]) /(Math.sqrt( 1 + coeff[1] * coeff[1]));
	
		else{
		// Initial guesses for Newton Raphson
		final Random rndx = new Random();
		double xc = rndx.nextFloat();

		double polyfunc = 0;
		double polyfuncdiff = 0;
		double delpolyfuncdiff = 0;
		double Dmin = 0;
		double Dmindiff = 0;
		double xcNew = rndx.nextFloat() * rndx.nextFloat()  ;
		double secdelpolyfuncdiff = 0;
		double Dminsecdiff = 0;

		/**
		 * Newton Raphson routine to get the shortest distance of a point from a
		 * curve.
		 */
		for (int j = degree; j >= 0; j--) 

				polyfunc += coeff[j] * Math.pow(xc, j);

			
			for (int j = degree; j >= 1; j--) 

				polyfuncdiff += j * coeff[j] * Math.pow(xc, j - 1);

			
			
			for (int j = degree; j >= 2; j--)
				delpolyfuncdiff += j * (j - 1) * coeff[j] * Math.pow(xc, j - 2);
			
			for (int j = degree; j >= 3; j--)
				secdelpolyfuncdiff += j * (j - 1) * (j - 2) * coeff[j] * Math.pow(xc, j - 3);
		
		do {

			xc = xcNew;
			
			Dmin = (polyfunc - y1) * polyfuncdiff + (xc - x1);

			Dmindiff = polyfuncdiff * polyfuncdiff +  (polyfunc - y1)* delpolyfuncdiff + 1;
			
			Dminsecdiff = (polyfunc - y1)*secdelpolyfuncdiff + delpolyfuncdiff * polyfuncdiff + 2 * polyfuncdiff * delpolyfuncdiff ;
			
			
			
			// Compute the first iteration of the new point
			xcNew =  NewtonRaphson(xc, Dmin, Dmindiff, Dminsecdiff);
			
			if (xcNew == Double.NaN)
				xcNew = xc;
			
			
			System.out.println(xcNew);

			// Compute the functions and the required derivates at the new point
			delpolyfuncdiff = 0;
			polyfunc = 0;
			polyfuncdiff = 0;
			secdelpolyfuncdiff = 0;
			for (int j = degree; j >= 0; j--) {

				polyfunc += coeff[j] * Math.pow(xcNew, j);

			}
			for (int j = degree; j >= 1; j--) {

				polyfuncdiff += j * coeff[j] * Math.pow(xcNew, j - 1);

			}

			for (int j = degree; j >= 2; j--)
				delpolyfuncdiff += j * (j - 1) * coeff[j] * Math.pow(xcNew, j - 2);
			
			for (int j = degree; j >= 3; j--)
				secdelpolyfuncdiff += j * (j - 1) * (j - 2) * coeff[j] * Math.pow(xcNew, j - 3);
			
		
		
			
			
			

		} while (Math.abs((xcNew - xc)) > 1.0E-3);

		// After the solution is found compute the y co-oordinate of the point
		// on the curve
		polyfunc = 0;
		for (int j = degree; j >= 0; j--) {

			polyfunc += coeff[j] * Math.pow(xc, j);

		}

		// Get the distance of (x1, y1) point from the curve and return the
		// value

		double returndist = util.Boundingboxes.Distance(new double[] { x1, y1 }, new double[] { xc, polyfunc });

		return returndist;
		}
	}

	public double NewtonRaphson(final double oldpoint, final double Function, final double Functionderiv, final double Functionsecderiv) {

		
		double secondordertermA = -(Functionderiv - oldpoint*Functionsecderiv) ;
		double secondordertermSQRT = Math.pow((Functionderiv - oldpoint*Functionsecderiv),2) - 2 * Functionsecderiv * (Function + Functionsecderiv*0.5*oldpoint*oldpoint - Functionderiv*oldpoint) ;
		double newpoint = 0 ;
		if (secondordertermSQRT > 0 && Functionsecderiv!= 0)
		newpoint = (secondordertermA + Math.sqrt(secondordertermSQRT)) / (Functionsecderiv);
		else
		newpoint = 	oldpoint -  Function / Functionderiv;
		
		return newpoint;

	}

	public static int i = 0;

	@Override
	public void set(final Polynomial p) {

		for (int j = degree; j >= 0; j--) {

			this.coeff[j] = p.getCoefficients(j);
		}

		this.setCost(p.getCost());
	}

	@Override
	public Polynomial copy() {
		Polynomial c = new Polynomial(degree);

		for (int j = degree; j >= 0; j--) {
			c.coeff[j] = getCoefficients(j);
		}

		c.setCost(getCost());

		return c;
	}

	public int degree() {
		return degree;
	}

	public double R2() {
		return 1.0 - SSE / SST;
	}

	// Horner's method to get y values correspoing to x
	public double predict(double x) {
		// horner's method
		double y = 0.0;
		for (int j = degree; j >= 0; j--)
			y = getCoefficients(j) + (x * y);
		return y;
	}

	public static void main(String[] args) throws NotEnoughDataPointsException, IllDefinedDataPointsException {
		final ArrayList<Point> points = new ArrayList<Point>();

		points.add(new Point(new double[] { 1f, -3.95132f }));
		points.add(new Point(new double[] { 2f, 6.51205f }));
		points.add(new Point(new double[] { 3f, 18.03612f }));
		points.add(new Point(new double[] { 4f, 28.65245f }));
		points.add(new Point(new double[] { 5f, 42.05581f }));
		points.add(new Point(new double[] { 6f, 54.01327f }));
		points.add(new Point(new double[] { 7f, 64.58747f }));
		points.add(new Point(new double[] { 8f, 76.48754f }));
		points.add(new Point(new double[] { 9f, 89.00033f }));

		final ArrayList<PointFunctionMatch> candidates = new ArrayList<PointFunctionMatch>();
		final ArrayList<PointFunctionMatch> inliersPoly = new ArrayList<PointFunctionMatch>();

		for (final Point p : points)
			candidates.add(new PointFunctionMatch(p));

		final int degree = 2;
		// Using the polynomial model to do the fitting
		final Polynomial regression = new Polynomial(degree);

		regression.ransac(candidates, inliersPoly, 100, 0.1, 0.5);

		System.out.println("inliers: " + inliersPoly.size());

		regression.fit(inliersPoly);
		System.out.println(" y = "  );
		for (int i = degree; i >= 0; --i)
			System.out.println(regression.getCoefficients(i) + "  " + "x" + " X to the power of "  + i );
		

	}

}
