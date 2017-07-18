package LineModels;

public class NumericalDerivatives extends GaussianSplinethirdorder {

	
public static double numdiffstart(double[] x, double[] a, int dim, double[] b) {
		
		double [] newa = new double[a.length];
		final int ndims = x.length;
		double epsilon = 1.0E-1;
		double f1 = 0;
		double f2 = 0;
		double diff = 0;
		
		
		do{
			
			
		for (int i = 0; i < a.length; ++i){
			newa[i] = a[i];
			if (i == dim)
			newa[i] = a[i] + epsilon;
		}
		f1 = (Estart(x, newa, b) - Estart(x, a, b))/ epsilon ;
		
		epsilon/=2;
		
		for (int i = 0; i < a.length; ++i){
			newa[i] = a[i];
			if (i == dim)
			newa[i] = a[i] + epsilon;
		}
		
		f2 = (Estart(x, newa, b) - Estart(x, a, b))/ epsilon ;
		
		diff =  Math.abs(f2 - f1) ;
		
		f1 = f2;
		}while(diff> 1.0E-7);
		
		return a[2 * ndims + 3] *f2;
		
	}

public static double numdiffend(double[] x, double[] a, int dim, double[] b) {
	
	double [] newa = new double[a.length];
	final int ndims = x.length;
	double epsilon = 1.0E-1;
	double f1 = 0;
	double f2 = 0;
	double diff = 0;
	
	
	do{
		
		
	for (int i = 0; i < a.length; ++i){
		newa[i] = a[i];
		if (i == dim)
		newa[i] = a[i] + epsilon;
	}
	f1 = (Eend(x, newa, b) - Eend(x, a, b))/ epsilon ;
	
	epsilon/=2;
	
	for (int i = 0; i < a.length; ++i){
		newa[i] = a[i];
		if (i == dim)
		newa[i] = a[i] + epsilon;
	}
	
	f2 = (Eend(x, newa, b) - Eend(x, a, b))/ epsilon ;
	
	diff =  Math.abs(f2 - f1) ;
	
	f1 = f2;
	}while(diff> 1.0E-7);
	
	return a[2 * ndims + 3] *f2;
	
}


public static double numdiff(double[] x, double[] a, int dim, double[] b) {
	
	double [] newa = new double[a.length];
	final int ndims = x.length;
	double epsilon = 0.001;
	double f1 = 0;
	double f2 = 0;
	double diff = 0;
	
	
	do{
		
		
	for (int i = 0; i < a.length; ++i){
		newa[i] = a[i];
		if (i == dim)
		newa[i] = a[i] + epsilon;
	}
	f1 = (Etotal(x, newa, b) - Etotal(x, a, b) )/ epsilon ;
	
	epsilon/=2;
	
	for (int i = 0; i < a.length; ++i){
		newa[i] = a[i];
		if (i == dim)
		newa[i] = a[i] + epsilon;
	}
	
	f2 =  (Etotal(x, newa, b) - Etotal(x, a, b) )/ epsilon ;
	
	diff =  Math.abs(f2 - f1) ;
	
	f1 = f2;
	}while(diff> 1.0E-2);
	
	return a[2 * ndims + 3] *f2;
	
}

	
}
