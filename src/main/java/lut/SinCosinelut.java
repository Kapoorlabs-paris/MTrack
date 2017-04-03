package lut;

public class SinCosinelut {
	
	    static final int precision = 1000; 
	    
	    static final int length = 361*precision;
		static float[] cos = new float[length];
		static float[] sin = new float[length];
		// Theta in degrees
		static float[] theta = new float[length];
		private static SinCosinelut table = new SinCosinelut();
		
		private SinCosinelut() {
		    for (int i = 0; i < length; i++) {
		    	theta[i] = i/precision;
		        cos[i] = (float) Math.cos(Math.toRadians(theta[i]));
		        sin[i] = (float) Math.sin(Math.toRadians(theta[i]));
		    }
		    System.out.println("Computed SinCosine Table");
		}
		private static float sinLookup(int a) {
		    return sin[a];
		}
		private static float cosLookup(int a) {
		    return cos[a];
		}
		public float getSine(double angle) {
		    return sinLookup((int)Math.round(angle * precision ));
		}

		public float getCos(double angle) {
		    return cosLookup((int)Math.round(angle * precision ));
		}

		public static SinCosinelut getTable() {
		    return table;
		}
		}

