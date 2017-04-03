package MTObjects;

	
	public final class Objprop {

        public  int Label;
		public  double diameter;
		public double[] sigma;
		public  double totalintensity;
		public double[] location;
		public double corr;
		public double noise;
		public double Circularity;
	

		public Objprop(final int Label, final double diameter,final double totalintensity, final double Circularity) {
			this.Label = Label;
			this.diameter = diameter;
			this.totalintensity = totalintensity;
			this.Circularity = Circularity;
			

		}
		
		public Objprop(final int Label, final double diameter, final double[] location, final double[] sigma, 
				final double corr, final double noise,
				final double totalintensity, final double Circularity){
			
			this.Label = Label;
			this.sigma = sigma;
			this.totalintensity = totalintensity;
			this.location = location;
			this.diameter = diameter;
			this.corr = corr;
			this.noise = noise;
			this.Circularity = Circularity;
			
			
			
		}
		
		
		
	}



