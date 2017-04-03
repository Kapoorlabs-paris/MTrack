package trackerType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.imglib2.RealPoint;
import snakes.SnakeObject;

import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import blobObjects.FramedBlob;
import blobObjects.Subgraphs;
import costMatrix.CostFunction;
import costMatrix.JaqamanLinkingCostMatrixCreator;
import graphconstructs.JaqamanLinker;
import graphconstructs.Logger;


public class KFsearch implements BlobTracker {

	private static final double ALTERNATIVE_COST_FACTOR = 1.05d;

	private static final double PERCENTILE = 1d;

	private static final String BASE_ERROR_MSG = "[KalmanTracker] ";

	private final ArrayList<ArrayList<SnakeObject>> Allblobs;

	private final double maxsearchRadius;
	private final double initialsearchRadius;
	private final CostFunction<SnakeObject, SnakeObject> UserchosenCostFunction;
	private final int maxframe;
	private final int maxframeGap;

	private SimpleWeightedGraph<SnakeObject, DefaultWeightedEdge> graph;
	private ArrayList<FramedBlob> Allmeasured;
	private ArrayList<Subgraphs> Framedgraph;

	protected Logger logger = Logger.DEFAULT_LOGGER;
	protected String errorMessage;

	public KFsearch(final ArrayList<ArrayList<SnakeObject>> Allblobs,
			final CostFunction<SnakeObject, SnakeObject> UserchosenCostFunction, final double maxsearchRadius,
			final double initialsearchRadius, final int maxframe, final int maxframeGap) {
		this.Allblobs = Allblobs;
		this.UserchosenCostFunction = UserchosenCostFunction;
		this.initialsearchRadius = initialsearchRadius;
		this.maxsearchRadius = maxsearchRadius;
		this.maxframe = maxframe;
		this.maxframeGap = maxframeGap;

	}

	@Override
	public SimpleWeightedGraph<SnakeObject, DefaultWeightedEdge> getResult() {
		return graph;
	}

	public ArrayList<FramedBlob> getFramelist() {

		return Allmeasured;
	}

	public ArrayList<Subgraphs> getFramedgraph() {

		return Framedgraph;
	}

	@Override
	public boolean checkInput() {
		return true;
	}

	@Override
	public boolean process() {

		/*
		 * Outputs
		 */

		graph = new SimpleWeightedGraph<SnakeObject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Allmeasured = new ArrayList<FramedBlob>();
		Framedgraph = new ArrayList<Subgraphs>();

		// Find first two non-zero frames containing blobs

		int Firstframe = 0;
		int Secondframe = 0;

		for (int frame = 0; frame < maxframe; ++frame) {

			if (Allblobs.get(frame).size() > 0) {
				Firstframe = frame;
				break;
			}
		}

		for (int frame = Firstframe + 1; frame < maxframe; ++frame) {

			if (Allblobs.get(frame).size() > 0) {
				Secondframe = frame;
				break;
			}
		}

		Collection<SnakeObject> Firstorphan = Allblobs.get(Firstframe);

		Collection<SnakeObject> Secondorphan = Allblobs.get(Secondframe);

		// Max KF search cost.
		final double maxCost = maxsearchRadius * maxsearchRadius;

		// Max cost to nucleate KFs.
		final double maxInitialCost = initialsearchRadius * initialsearchRadius;

		/*
		 * Estimate Kalman filter variances.
		 *
		 * The search radius is used to derive an estimate of the noise that
		 * affects position and velocity. The two are linked: if we need a large
		 * search radius, then the fluoctuations over predicted states are
		 * large.
		 */
		final double positionProcessStd = maxsearchRadius / 3d;
		final double velocityProcessStd = maxsearchRadius / 3d;

		
		double meanSpotRadius = 0d;
		for (final SnakeObject Blob : Secondorphan) {
			
			
			
			
				if (Blob.roi!= null)
			meanSpotRadius += Blob.roi.getLength() ;
				else
			meanSpotRadius += Blob.size ;
			
		}
		meanSpotRadius /= Secondorphan.size();
		final double positionMeasurementStd = meanSpotRadius / 10d;

		final Map<CVMKalmanFilter, SnakeObject> kalmanFiltersMap = new HashMap<CVMKalmanFilter, SnakeObject>(
				Secondorphan.size());

		// Loop from the second frame to the last frame and build
		// KalmanFilterMap

		for (int frame = Secondframe; frame < maxframe; frame++) {

			SimpleWeightedGraph<SnakeObject, DefaultWeightedEdge> subgraph = new SimpleWeightedGraph<SnakeObject, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);

			List<SnakeObject> measurements = Allblobs.get(frame);

			System.out.println("Doing KF search in frame number: " + frame + " " + "Number of blobs:"
					+ Allblobs.get(frame).size());

			// Make the preditiction map
			final Map<ComparableRealPoint, CVMKalmanFilter> predictionMap = new HashMap<ComparableRealPoint, CVMKalmanFilter>(
					kalmanFiltersMap.size());

			for (final CVMKalmanFilter kf : kalmanFiltersMap.keySet()) {
				final double[] X = kf.predict();
				final ComparableRealPoint point = new ComparableRealPoint(X);
				predictionMap.put(point, kf);

			}
			final List<ComparableRealPoint> predictions = new ArrayList<ComparableRealPoint>(predictionMap.keySet());

			// Orphans are dealt with later
			final Collection<CVMKalmanFilter> childlessKFs = new HashSet<CVMKalmanFilter>(kalmanFiltersMap.keySet());

			/*
			 * Here we simply link based on minimizing the squared distances to
			 * get an initial starting point, more advanced Kalman filter costs
			 * will be built in the next step
			 */

			if (!predictions.isEmpty() && !measurements.isEmpty()) {
				// Only link measurements to predictions if we have predictions.

				final JaqamanLinkingCostMatrixCreator<ComparableRealPoint, SnakeObject> crm = new JaqamanLinkingCostMatrixCreator<ComparableRealPoint, SnakeObject>(
						predictions, measurements, DistanceBasedcost, maxCost, ALTERNATIVE_COST_FACTOR, PERCENTILE);

				final JaqamanLinker<ComparableRealPoint, SnakeObject> linker = new JaqamanLinker<ComparableRealPoint, SnakeObject>(
						crm);
				if (!linker.checkInput() || !linker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking candidates in frame " + frame + ": "
							+ linker.getErrorMessage();
					return false;
				}
				final Map<ComparableRealPoint, SnakeObject> agnts = linker.getResult();
				final Map<ComparableRealPoint, Double> costs = linker.getAssignmentCosts();

				// Deal with found links.
				Secondorphan = new HashSet<SnakeObject>(measurements);
				for (final ComparableRealPoint cm : agnts.keySet()) {
					final CVMKalmanFilter kf = predictionMap.get(cm);

					// Create links for found match.
					final SnakeObject source = kalmanFiltersMap.get(kf);
					final SnakeObject target = agnts.get(cm);

					graph.addVertex(source);
					graph.addVertex(target);
					final DefaultWeightedEdge edge = graph.addEdge(source, target);
					final double cost = costs.get(cm);
					graph.setEdgeWeight(edge, cost);

					subgraph.addVertex(source);
					subgraph.addVertex(target);
					final DefaultWeightedEdge subedge = subgraph.addEdge(source, target);
					subgraph.setEdgeWeight(subedge, cost);

					Subgraphs currentframegraph = new Subgraphs(frame - 1, frame, subgraph);

					Framedgraph.add(currentframegraph);
					final FramedBlob prevframedBlob = new FramedBlob(frame - 1, source);

					Allmeasured.add(prevframedBlob);

					final FramedBlob newframedBlob = new FramedBlob(frame, target);

					Allmeasured.add(newframedBlob);

					// Update Kalman filter
					kf.update(MeasureBlob(target));

					// Update Kalman track SnakeObject
					kalmanFiltersMap.put(kf, target);

					// Remove from orphan set
					Secondorphan.remove(target);

					// Remove from childless KF set
					childlessKFs.remove(kf);
				}
			}

			// Deal with orphans from the previous frame.
			// Here is the real linking with the actual cost function

			if (!Firstorphan.isEmpty() && !Secondorphan.isEmpty()) {

				// Trying to link orphans with unlinked candidates.

				final JaqamanLinkingCostMatrixCreator<SnakeObject, SnakeObject> ic = new JaqamanLinkingCostMatrixCreator<SnakeObject, SnakeObject>(
						Firstorphan, Secondorphan, UserchosenCostFunction, maxInitialCost, ALTERNATIVE_COST_FACTOR,
						PERCENTILE);
				final JaqamanLinker<SnakeObject, SnakeObject> newLinker = new JaqamanLinker<SnakeObject, SnakeObject>(
						ic);
				if (!newLinker.checkInput() || !newLinker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking Blobs from frame " + (frame - 1) + " to frame "
							+ frame + ": " + newLinker.getErrorMessage();
					return false;
				}
				final Map<SnakeObject, SnakeObject> newAssignments = newLinker.getResult();
				final Map<SnakeObject, Double> assignmentCosts = newLinker.getAssignmentCosts();

				// Build links and new KFs from these links.
				for (final SnakeObject source : newAssignments.keySet()) {
					final SnakeObject target = newAssignments.get(source);

					// Remove from orphan collection.
					Secondorphan.remove(target);

					// Derive initial state and create Kalman filter.
					final double[] XP = estimateInitialState(source, target);
					final CVMKalmanFilter kt = new CVMKalmanFilter(XP, Double.MIN_NORMAL, positionProcessStd,
							velocityProcessStd, positionMeasurementStd);
					// We trust the initial state a lot.

					// Store filter and source
					kalmanFiltersMap.put(kt, target);

					// Add edge to the graph.
					graph.addVertex(source);
					graph.addVertex(target);
					final DefaultWeightedEdge edge = graph.addEdge(source, target);
					final double cost = assignmentCosts.get(source);
					graph.setEdgeWeight(edge, cost);

					subgraph.addVertex(source);
					subgraph.addVertex(target);
					final DefaultWeightedEdge subedge = subgraph.addEdge(source, target);
					subgraph.setEdgeWeight(subedge, cost);

					Subgraphs currentframegraph = new Subgraphs(frame - 1, frame, subgraph);

					Framedgraph.add(currentframegraph);

					final FramedBlob prevframedBlob = new FramedBlob(frame - 1, source);

					Allmeasured.add(prevframedBlob);

					final FramedBlob newframedBlob = new FramedBlob(frame, target);

					Allmeasured.add(newframedBlob);

				}
			}

			Firstorphan = Secondorphan;
			// Deal with childless KFs.
			for (final CVMKalmanFilter kf : childlessKFs) {
				// Echo we missed a measurement
				kf.update(null);

				// We can bridge a limited number of gaps. If too much, we die.
				// If not, we will use predicted state next time.
				if (kf.getNOcclusion() > maxframeGap) {
					kalmanFiltersMap.remove(kf);
				}
			}

		}

		return true;
	}

	@Override
	public void setLogger(final Logger logger) {
		this.logger = logger;

	}
	@Override
	public void reset() {
		graph = new SimpleWeightedGraph<SnakeObject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		final Iterator<SnakeObject> it = Allblobs.get(0).iterator();
		while (it.hasNext()) {
			graph.addVertex(it.next());
		}
	}
	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	private static final class ComparableRealPoint extends RealPoint implements Comparable<ComparableRealPoint> {
		public ComparableRealPoint(final double[] A) {
			// Wrap array.
			super(A, false);
		}

		/**
		 * Sort based on X, Y
		 */
		@Override
		public int compareTo(final ComparableRealPoint o) {
			int i = 0;
			while (i < n) {
				if (getDoublePosition(i) != o.getDoublePosition(i)) {
					return (int) Math.signum(getDoublePosition(i) - o.getDoublePosition(i));
				}
				i++;
			}
			return hashCode() - o.hashCode();
		}
	}
	
	

	private static final double[] MeasureBlob(final SnakeObject target) {
		final double[] location = new double[] { target.centreofMass[0], target.centreofMass[1], target.centreofMass[2] };
		return location;
	}


	private static final double[] estimateInitialState(final SnakeObject first, final SnakeObject second) {
		final double[] xp = new double[] { second.centreofMass[0], second.centreofMass[1], second.centreofMass[2], second.diffTo(first, 0),
				second.diffTo(first, 1), second.diffTo(first, 2) };
		return xp;
	}

	/**
	 * 
	 * Implementations of various cost functions, starting with the simplest
	 * one, based on minimizing the distances between the links, followed by
	 * minimizing cost function based on intensity differences between the
	 * links.
	 *
	 * Cost function that returns the square distance between a KF state and a
	 * Blob.
	 */
	private static final CostFunction<ComparableRealPoint, SnakeObject> DistanceBasedcost = new CostFunction<ComparableRealPoint, SnakeObject>() {

		@Override
		public double linkingCost(final ComparableRealPoint state, final SnakeObject Blob) {
			final double dx = state.getDoublePosition(0) - Blob.centreofMass[0];
			final double dy = state.getDoublePosition(1) - Blob.centreofMass[1];
			final double dz = state.getDoublePosition(2) - Blob.centreofMass[2];
			return dx * dx + dy * dy + dz * dz + Double.MIN_NORMAL;
			// So that it's never 0
		}
	};

}