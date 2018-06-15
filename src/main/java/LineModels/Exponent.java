package LineModels;

public class Exponent {

	public static double exp(double x) {
		double sum = 1.0f; // initialize sum of series
		int  n = 10;
	    for (int i = n - 1; i > 0; --i )
	        sum = 1 + x * sum / i;
	 
	    return sum;
	}
	
}
