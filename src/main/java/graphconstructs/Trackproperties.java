package graphconstructs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class Trackproperties extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Trackproperties> {

	
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
	 * @param Framenumber
	 *            the current frame
	 * 
	 * @param Label
	 *            the label of the MT
	 * @param oldpoint
	 *            the co-ordinates of the old point of the end of MT.
	 * @param newpoint
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
	
	public final int Framenumber;
	public final int Label;
	public final double[] oldpoint;
	public final double[] newpoint;
	public final double newslope;
	public final double newintercept;
	public final double originalslope;
	public final double originalintercept;
	public final int seedlabel;
	public final double[] originalpoint;
	public final double[] originalds;
	
	
	/*
	 * CONSTRUCTORS
	 */
	
	public Trackproperties(final int Framenumber, final int Label, 
			final double[] oldpoint, final double[] newpoint, final double newslope, final double newintercept, 
			final double originalslope, final double originalintercept, final int seedlabel, final double[] originalpoint, final double[] originalds ) {
		super( 3 );
		this.ID = IDcounter.incrementAndGet();
		putFeature( FRAME, Double.valueOf( Framenumber ) );
		putFeature( LABEL, Double.valueOf( Label ) );
		putFeature( OLDXPOSITION, Double.valueOf( oldpoint[0] ) );
		putFeature( OLDYPOSITION, Double.valueOf( oldpoint[1] ) );
		putFeature( NEWXPOSITION, Double.valueOf( newpoint[0] ) );
		putFeature( NEWYPOSITION, Double.valueOf( newpoint[1] ) );
		putFeature( NEWSLOPE, Double.valueOf( newslope ) );
		putFeature( ORIGINALSLOPE, Double.valueOf( originalslope ) );
		
		this.Label = Label;
		this.Framenumber = Framenumber;
		this.oldpoint = oldpoint;
		this.newpoint = newpoint;
		this.newslope = newslope;
		this.newintercept = newintercept;
		this.originalslope = originalslope;
		this.originalintercept = originalintercept;
		this.seedlabel = seedlabel;
		this.originalpoint = originalpoint;
		this.originalds = originalds;
		
		

	}
	
	

	
	@Override
	public int compareTo(Trackproperties o) {

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

		return oldpoint.length;
	}

	
	
	/*
	 * STATIC KEYS
	 */

	



	/** The name of the blob X position feature. */
	public static final String OLDXPOSITION = "OLDXPOSITION";

	/** The name of the blob Y position feature. */
	public static final String OLDYPOSITION = "OLDYPOSITION";
	
	/** The name of the blob X position feature. */
	public static final String ORIGINALSLOPE = "OLDSLOPE";

	/** The name of the blob Y position feature. */
	public static final String NEWSLOPE = "NEWSLOPE";
	
	/** The name of the blob X position feature. */
	public static final String NEWXPOSITION = "NEWXPOSITION";

	/** The name of the blob Y position feature. */
	public static final String NEWYPOSITION = "NEWYPOSITION";
	
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




	
}
