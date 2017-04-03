package labeledObjects;

import java.util.ArrayList;

// Objects containing the label and the correspoing rho and theta information
	public  final class Lineobjects {
		public final int Label;
		
        public ArrayList<double[]> slopeandintercept;  		
		public final long [] boxmin;
		public final long [] boxmax;
		public double[] singleslopeandintercept;
		

		public Lineobjects(
				final int Label,
				final ArrayList<double[]> slopeandintercept,
				
				final long[] minCorner, 
				final long[] maxCorner
				) {
			this.Label = Label;
		    this.slopeandintercept = slopeandintercept;
			this.boxmin = minCorner;
			this.boxmax = maxCorner;
			
			
		}
		
		public Lineobjects(
				final int Label,
				double[] singleslopeandintercept,
				
				final long[] minCorner, 
				final long[] maxCorner
				) {
			this.Label = Label;
		    this.singleslopeandintercept = singleslopeandintercept;
			this.boxmin = minCorner;
			this.boxmax = maxCorner;
			
			
		}
		
	}
