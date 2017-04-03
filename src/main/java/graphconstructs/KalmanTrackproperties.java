package graphconstructs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class KalmanTrackproperties extends AbstractEuclideanSpace implements RealLocalizable, Comparable<KalmanTrackproperties> {

	
	/*
	 * FIELDS
	 */

	public static AtomicInteger IDcounter = new AtomicInteger( -1 );

	/** Store the individual features, and their values. */
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();

	/** A user-supplied name for this spot. */
	private String name;

	/** This spot ID. */
	private final int ID;
	
	/**
	 * @param thirdDimension
	 *            the current frame
	 * 
	 * @param Label
	 *            the label of the MT
	 * @param currentpoint
	 *            the co-ordinates of the new point of the end of MT.
	 * @param newslope
	 *            the newslope of the MT line.
	 * @param newintercept
	 *            the newintercept of the MT line.
	 * @param originalslope
	 *            the original slope of the MT line.
	 * @param originalintercept
	 *            the original intercept of the MT line.
	 * @param seedlabel
	 *            the seedlabel of the MT line.  
	 * @param originalpoint
	 *            the original point of the MT line.
	 * @param originalds
	 *            the original magnitude of the ds vector determined.
	 */
	
	public final int thirdDimension;
	public final int Label;
	public final double size;
	public final double[] currentpoint;
	public final double[] originalpoint;
	public final double newslope;
	public final double newintercept;
	public final double originalslope;
	public final double originalintercept;
	public final int seedlabel;
	public final double[] originalds;
	
	
	/*
	 * CONSTRUCTORS
	 */
	
	public KalmanTrackproperties(final int thirdDimension, final int Label,  final double size,
			 final double[] currentpoint,final double[] originalpoint, final double newslope, final double newintercept, 
			final double originalslope, final double originalintercept, final int seedlabel, final double[] originalds ) {
		super( 3 );
		this.ID = IDcounter.incrementAndGet();
		putFeature( FRAME, Double.valueOf( thirdDimension ) );
		putFeature( LABEL, Double.valueOf( Label ) );
		
		putFeature( CurrentXPOSITION, Double.valueOf( currentpoint[0] ) );
		putFeature( CurrentYPOSITION, Double.valueOf( currentpoint[1] ) );
		
		putFeature( OriginalXPOSITION, Double.valueOf( originalpoint[0] ) );
		putFeature( OriginalYPOSITION, Double.valueOf( originalpoint[1] ) );
		
		putFeature( NEWSLOPE, Double.valueOf( newslope ) );
		putFeature( ORIGINALSLOPE, Double.valueOf( originalslope ) );
		
		this.Label = Label;
		this.thirdDimension = thirdDimension;
		this.size = size;
		this.currentpoint = currentpoint;
		this.originalpoint = originalpoint;
		this.newslope = newslope;
		this.newintercept = newintercept;
		this.originalslope = originalslope;
		this.originalintercept = originalintercept;
		this.seedlabel = seedlabel;
		this.originalds = originalds;
		
		

	}
	
	

	
	@Override
	public int compareTo(KalmanTrackproperties o) {

		return hashCode() - o.hashCode();
	}

	@Override
	public void localize(float[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);

	}

	@Override
	public void localize(double[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getDoublePosition(d);
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) getDoublePosition(d);
	}

	@Override
	public double getDoublePosition(int d) {
		return getDoublePosition(d);
	}

	@Override
	public int numDimensions() {

		return currentpoint.length;
	}

	
	
	/*
	 * STATIC KEYS
	 */

	



	/** The name of the blob X position feature. */
	public static final String CurrentXPOSITION = "CurrentXPOSITION";

	/** The name of the blob Y position feature. */
	public static final String CurrentYPOSITION = "CurrentYPOSITION";
	
	/** The name of the blob X position feature. */
	public static final String OriginalXPOSITION = "OriginalXPOSITION";

	/** The name of the blob Y position feature. */
	public static final String OriginalYPOSITION = "OriginalYPOSITION";
	
	/** The name of the blob X position feature. */
	public static final String ORIGINALSLOPE = "OLDSLOPE";

	/** The name of the blob Y position feature. */
	public static final String NEWSLOPE = "NEWSLOPE";
	
	
	/** The label of the blob position feature. */
	public static final String LABEL = "LABEL";

	/** The name of the frame feature. */
	public static final String FRAME = "FRAME";
	public final Double getFeature( final String feature )
	{
		return features.get( feature );
	}

	/**
	 * Stores the specified feature value for this spot.
	 *
	 * @param feature
	 *            the name of the feature to store, as a {@link String}.
	 * @param value
	 *            the value to store, as a {@link Double}. Using
	 *            <code>null</code> will have unpredicted outcomes.
	 */
	public final void putFeature( final String feature, final Double value )
	{
		features.put( feature, value );
	}

	/**
	 * Returns the difference between the location of two blobs, this operation
	 * returns ( <code>A.diffTo(B) = - B.diffTo(A)</code>)
	 *
	 * @param target
	 *            the Blob to compare to.
	 * @param int
	 *            n n = 0 for X- coordinate, n = 1 for Y- coordinate
	 * @return the difference in co-ordinate specified.
	 */
	public double diffTo(final KalmanTrackproperties target, int n) {

		final double thisMTlocation = currentpoint[n];
		final double targetMTlocation = target.currentpoint[n];
		return thisMTlocation - targetMTlocation;
	}


	/**
	 * Returns the squared distance between two blobs.
	 *
	 * @param target
	 *            the MT location to compare to.
	 *
	 * @return the distance to the current MT to target MT specified.
	 */

	public double squareDistanceTo(KalmanTrackproperties target) {
		// Returns squared distance between the source Blob and the target Blob.

		final double[] sourceLocation = this.currentpoint;
		final double[] targetLocation = target.currentpoint;

		
    final double sourceslope =	this.originalslope;
    final double sourceintercept = this.originalintercept;
 
    
    double distance = 0;
		
		
		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

		return distance;
	
	}
}
