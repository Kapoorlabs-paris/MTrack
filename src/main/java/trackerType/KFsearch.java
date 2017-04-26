package trackerType;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.imglib2.RealPoint;

import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import MTObjects.FramedMT;
import costMatrix.CostFunction;
import costMatrix.JaqamanLinkingCostMatrixCreator;
import graphconstructs.JaqamanLinker;
import graphconstructs.KalmanTrackproperties;
import graphconstructs.Logger;
import labeledObjects.SubgraphsKalman;


public class KFsearch implements MTTracker {

	private static final double ALTERNATIVE_COST_FACTOR = 1.05d;

	private static final double PERCENTILE = 1d;

	private static final String BASE_ERROR_MSG = "[KalmanTracker] ";

	private final ArrayList<ArrayList<KalmanTrackproperties>> AllMT;

	private final double maxsearchRadius;
	private final double initialsearchRadius;
	private final CostFunction<KalmanTrackproperties, KalmanTrackproperties> UserchosenCostFunction;
	private final int maxframe;
	private final int firstframe;
	private final int maxframeGap;

	private SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graph;
	private ArrayList<FramedMT> Allmeasured;
	private ArrayList<SubgraphsKalman> Framedgraph;

	protected Logger logger = Logger.DEFAULT_LOGGER;
	protected String errorMessage;

	public KFsearch(final ArrayList<ArrayList<KalmanTrackproperties>> AllMT,
			final CostFunction<KalmanTrackproperties, KalmanTrackproperties> UserchosenCostFunction, final double maxsearchRadius,
			final double initialsearchRadius,final int firstframe, final int maxframe, final int missedframes) {
		this.AllMT = AllMT;
		this.UserchosenCostFunction = UserchosenCostFunction;
		this.initialsearchRadius = initialsearchRadius;
		this.maxsearchRadius = maxsearchRadius;
		this.maxframe = maxframe;
		this.firstframe = firstframe;
		this.maxframeGap = missedframes;

	}

	@Override
	public SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> getResult() {
		return graph;
	}

	public ArrayList<FramedMT> getFramelist() {

		return Allmeasured;
	}
	@Override
	public ArrayList<SubgraphsKalman> getFramedgraph() {

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

		graph = new SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Allmeasured = new ArrayList<FramedMT>();
		Framedgraph = new ArrayList<SubgraphsKalman>();

		// Find first two non-zero frames containing blobs
		int Firstframe = 0;
		int Secondframe = 1;

		for (int frame = 0; frame < maxframe; ++frame) {

			if (AllMT.get(frame).size() > 0) {
				Firstframe = frame;
				break;
			}
		}

		for (int frame = Secondframe; frame < maxframe; ++frame) {

			if (AllMT.get(frame).size() > 0) {
				Secondframe = frame;
				break;
			}
		}

		Collection<KalmanTrackproperties> Firstorphan = AllMT.get(Firstframe);

		Collection<KalmanTrackproperties> Secondorphan = AllMT.get(Secondframe);

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
		final double positionProcessStd = maxsearchRadius / 2d;
		final double velocityProcessStd = maxsearchRadius / 2d;

		
		double meanSpotRadius = 0d;
		for (final KalmanTrackproperties MT : Secondorphan) {
			
			
			meanSpotRadius += MT.size  ;
			
		}
		meanSpotRadius /= Secondorphan.size();
		final double positionMeasurementStd =  meanSpotRadius ;

		final Map<CVMKalmanFilter, KalmanTrackproperties> kalmanFiltersMap = new HashMap<CVMKalmanFilter, KalmanTrackproperties>(
				Secondorphan.size());

		// Loop from the second frame to the last frame and build
		// KalmanFilterMap

		for (int frame = Secondframe; frame < AllMT.size() ; frame++) {

			SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> subgraph = new SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);

			List<KalmanTrackproperties> measurements = AllMT.get(frame);

			
			System.out.println("Doing KF search in frame number: " + (frame + Firstframe) );

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
				final JaqamanLinkingCostMatrixCreator<ComparableRealPoint, KalmanTrackproperties> crm = new JaqamanLinkingCostMatrixCreator<ComparableRealPoint, KalmanTrackproperties>(
						predictions, measurements, DistanceBasedcost, maxCost, ALTERNATIVE_COST_FACTOR, PERCENTILE);

				final JaqamanLinker<ComparableRealPoint, KalmanTrackproperties> linker = new JaqamanLinker<ComparableRealPoint, KalmanTrackproperties>(
						crm);
				if (!linker.checkInput() || !linker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking candidates in frame " + frame + ": "
							+ linker.getErrorMessage();
					return false;
				}
				final Map<ComparableRealPoint, KalmanTrackproperties> agnts = linker.getResult();
				final Map<ComparableRealPoint, Double> costs = linker.getAssignmentCosts();

				// Deal with found links.
				Secondorphan = new HashSet<KalmanTrackproperties>(measurements);
				for (final ComparableRealPoint cm : agnts.keySet()) {
					final CVMKalmanFilter kf = predictionMap.get(cm);

					// Create links for found match.
					final KalmanTrackproperties source = kalmanFiltersMap.get(kf);
					final KalmanTrackproperties target = agnts.get(cm);
					graph.addVertex(source);
					graph.addVertex(target);
					final DefaultWeightedEdge edge = graph.addEdge(source, target);
					final double cost = costs.get(cm);
					graph.setEdgeWeight(edge, cost);

					subgraph.addVertex(source);
					subgraph.addVertex(target);
					final DefaultWeightedEdge subedge = subgraph.addEdge(source, target);
					subgraph.setEdgeWeight(subedge, cost);

					SubgraphsKalman currentframegraph = new SubgraphsKalman(frame - 1, frame, subgraph);

					Framedgraph.add(currentframegraph);
					final FramedMT prevframedMT = new FramedMT(frame - 1, source);

					Allmeasured.add(prevframedMT);

					final FramedMT newframedMT = new FramedMT(frame, target);

					Allmeasured.add(newframedMT);

					// Update Kalman filter
					kf.update(MeasureMT(target));

					// Update Kalman track KalmanTrackproperties
					kalmanFiltersMap.put(kf, target);

				
					Secondorphan.remove(target);

					// Remove from childless KF set
					childlessKFs.remove(kf);
				}
			}

			// Deal with orphans from the previous frame.
			// Here is the real linking with the actual cost function

			if (!Firstorphan.isEmpty() && !Secondorphan.isEmpty()) {

				// Trying to link orphans with unlinked candidates.

				final JaqamanLinkingCostMatrixCreator<KalmanTrackproperties, KalmanTrackproperties> ic = new JaqamanLinkingCostMatrixCreator<KalmanTrackproperties, KalmanTrackproperties>(
						Firstorphan, Secondorphan, UserchosenCostFunction, maxInitialCost, ALTERNATIVE_COST_FACTOR,
						PERCENTILE);
				final JaqamanLinker<KalmanTrackproperties, KalmanTrackproperties> newLinker = new JaqamanLinker<KalmanTrackproperties, KalmanTrackproperties>(
						ic);
				if (!newLinker.checkInput() || !newLinker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking MT from frame " + (frame - 1) + " to frame "
							+ frame + ": " + newLinker.getErrorMessage();
					return false;
				}
				final Map<KalmanTrackproperties, KalmanTrackproperties> newAssignments = newLinker.getResult();
				final Map<KalmanTrackproperties, Double> assignmentCosts = newLinker.getAssignmentCosts();

				// Build links and new KFs from these links.
				for (final KalmanTrackproperties source : newAssignments.keySet()) {
					final KalmanTrackproperties target = newAssignments.get(source);

					// Remove from orphan collection.

					// Derive initial state and create Kalman filter.
					final double[] XP = estimateInitialState(source, target);
					
					final CVMKalmanFilter kt = new CVMKalmanFilter(XP, Double.MIN_NORMAL, positionProcessStd,
							velocityProcessStd, positionMeasurementStd);
					// We trust the initial state a lot.

					// Store filter and source
					kalmanFiltersMap.put(kt, target);

					// Add edge to the graph.
					synchronized (graph) {
					graph.addVertex(source);
					graph.addVertex(target);
					final DefaultWeightedEdge edge = graph.addEdge(source, target);
					final double cost = assignmentCosts.get(source);
					System.out.println(cost + " " + source.Label);
					graph.setEdgeWeight(edge, cost);
					
					subgraph.addVertex(source);
					subgraph.addVertex(target);
					final DefaultWeightedEdge subedge = subgraph.addEdge(source, target);
					subgraph.setEdgeWeight(subedge, cost);

					SubgraphsKalman currentframegraph = new SubgraphsKalman(frame - 1, frame, subgraph);
					
					Framedgraph.add(currentframegraph);
					}
					final FramedMT prevframedMT = new FramedMT(frame - 1, source);

					Allmeasured.add(prevframedMT);

					final FramedMT newframedMT = new FramedMT(frame, target);

					Allmeasured.add(newframedMT);

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
		graph = new SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		final Iterator<KalmanTrackproperties> it = AllMT.get(0).iterator();
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
	
	

	private static final double[] MeasureMT(final KalmanTrackproperties target) {
		final double[] location = new double[] { target.currentpoint[0], target.currentpoint[1] };
		return location;
	}


	private static final double[] estimateInitialState(final KalmanTrackproperties first, final KalmanTrackproperties second) {
		final double[] xp = new double[] { second.currentpoint[0], second.currentpoint[1], second.diffTo(first, 0),
				second.diffTo(first, 1)};
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
	private static final CostFunction<ComparableRealPoint, KalmanTrackproperties> DistanceBasedcost = new CostFunction<ComparableRealPoint, KalmanTrackproperties>() {

		@Override
		public double linkingCost(final ComparableRealPoint state, final KalmanTrackproperties MT) {
			final double dx = state.getDoublePosition(0) - MT.currentpoint[0];
			final double dy = state.getDoublePosition(1) - MT.currentpoint[1];
			return dx * dx + dy * dy  + Double.MIN_NORMAL;
			// So that it's never 0
		}
	};

}