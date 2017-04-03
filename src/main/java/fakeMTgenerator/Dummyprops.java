package fakeMTgenerator;

public class Dummyprops {

	


		
		public final int frame;
		public final double[] originalpoint;
		public final double[] newpoint;
		public final double originalslope;
		public final double originalintercept;
		
		
		
		
		public Dummyprops(final int frame, final double[] originalpoint,
				final double[] newpoint,  final double originalslope, final double originalintercept ) {
			this.frame = frame;
			this.newpoint = newpoint;
			this.originalslope = originalslope;
			this.originalintercept = originalintercept;
			this.originalpoint = originalpoint;
			

		}
}
