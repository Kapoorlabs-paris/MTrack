package trackerType;



import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.TraversalListenerAdapter;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.AsUnweightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import snakes.SnakeObject;



public class TrackModel
{
/**
 * A component of {@link Model} specialized for tracks.
 *
 * @author Jean-Yves Tinevez
 */


	/**
	 * The mother graph, from which all subsequent fields are calculated. This
	 * graph is not made accessible to the outside world. Editing it must be
	 * trough the model methods {@link #addEdge(SnakeObject, SnakeObject, double)},
	 * {@link #removeEdge(DefaultWeightedEdge)}, {@link #removeEdge(SnakeObject, SnakeObject)}
	 * .
	 */
	private ListenableUndirectedGraph< SnakeObject, DefaultWeightedEdge > graph;

	private final MyGraphListener mgl;

	/*
	 * TRANSACTION FIELDS
	 */

	/**
	 * The edges that have been added to this model by
	 * {@link #addEdge(SnakeObject, SnakeObject, double)}.
	 * <p>
	 * It is the parent instance responsibility to clear this field when it is
	 * fit to do so.
	 */
	final Set< DefaultWeightedEdge > edgesAdded = new HashSet< DefaultWeightedEdge >();

	/**
	 * The edges that have removed from this model by
	 * {@link #removeEdge(DefaultWeightedEdge)} or
	 * {@link #removeEdge(SnakeObject, SnakeObject)}.
	 * <p>
	 * It is the parent instance responsibility to clear this field when it is
	 * fit to do so.
	 */
	final Set< DefaultWeightedEdge > edgesRemoved = new HashSet< DefaultWeightedEdge >();

	/**
	 * The edges that have been modified in this model by changing its cost
	 * using {@link #setEdgeWeight(DefaultWeightedEdge, double)} or modifying
	 * the SnakeObjects it links elsewhere.
	 * <p>
	 * It is the parent instance responsibility to clear this field when it is
	 * fit to do so.
	 */
	final Set< DefaultWeightedEdge > edgesModified = new HashSet< DefaultWeightedEdge >();

	/**
	 * The track IDs that have been modified, updated or created, <b>solely</b>
	 * by removing or adding an edge. Possibly after the removal of a SnakeObject.
	 * Tracks having edges that are <b>modified</b>, for instance by modifying a
	 * SnakeObject it contains, will not be listed here, but must be sought from the
	 * {@link #edgesModified} field.
	 * <p>
	 * It is the parent instance responsibility to clear this field when it is
	 * fit to do so.
	 */
	final Set< Integer > tracksUpdated = new HashSet< Integer >();

	private static final Boolean DEFAULT_VISIBILITY = Boolean.TRUE;

	// ~ Instance fields
	// --------------------------------------------------------

	private int IDcounter = 0;

	private Map< Integer, Set< DefaultWeightedEdge > > connectedEdgeSets;

	Map< DefaultWeightedEdge, Integer > edgeToID;

	private Map< Integer, HashSet< SnakeObject > > connectedVertexSets;

	Map< SnakeObject, Integer > vertexToID;

	private Map< Integer, Boolean > visibility;

	private Map< Integer, String > names;

	private final Iterator< String > nameGenerator = new DefaultNameGenerator();

	/*
	 * Constructors -----------------------------------------------------------
	 */

	public TrackModel()
	{
		this( new SimpleWeightedGraph< SnakeObject, DefaultWeightedEdge >( DefaultWeightedEdge.class ) );
	}

	public TrackModel( final SimpleWeightedGraph< SnakeObject, DefaultWeightedEdge > graph )
	{
		this.mgl = new MyGraphListener();
		setGraph( graph );
	}

	/*
	 * CREATION METHODS Methods that wipes the current content and replace it in
	 * bulk.
	 */

	/**
	 * Clears the content of this model and replace it by the tracks found by
	 * inspecting the specified graph. All new tracks found will be made visible
	 * and will be given a default name.
	 *
	 * @param graph
	 *            the graph to parse for tracks.
	 */
	void setGraph( final SimpleWeightedGraph< SnakeObject, DefaultWeightedEdge > graph )
	{
		if ( null != this.graph )
		{
			this.graph.removeGraphListener( mgl );
		}
		this.graph = new ListenableUndirectedGraph< SnakeObject, DefaultWeightedEdge >( graph );
		this.graph.addGraphListener( mgl );
		init( graph );
	}

	/**
	 * Removes all the tracks from this model.
	 */
	void clear()
	{
		setGraph( new SimpleWeightedGraph< SnakeObject, DefaultWeightedEdge >( DefaultWeightedEdge.class ) );
	}

	/**
	 * This method is meant to help building a model from a serialized source,
	 * such as a saved file. It allows specifying the exact mapping of track IDs
	 * to the connected sets. The model content is completely replaced by the
	 * specified parameters, including the global graph, its connected
	 * components (both in SnakeObjects and edges), visibility and naming.
	 * <p>
	 * It is the caller responsibility to ensure that the graph and provided
	 * component are coherent. Unexpected behavior might result otherwise.
	 *
	 * @param graph
	 *            the mother graph for the model.
	 * @param trackSnakeObjects
	 *            the mapping of track IDs vs the connected components as sets
	 *            of SnakeObjects.
	 * @param trackEdges
	 *            the mapping of track IDs vs the connected components as sets
	 *            of edges.
	 * @param trackVisibility
	 *            the track visibility.
	 * @param trackNames
	 *            the track names.
	 */
	public void from( final SimpleWeightedGraph< SnakeObject, DefaultWeightedEdge > graph, 
			final Map<Integer, HashSet<SnakeObject>> trackSnakeObjects, final Map< Integer, Set< DefaultWeightedEdge > > trackEdges, final Map< Integer, Boolean > trackVisibility, final Map< Integer, String > trackNames )
	{

		if ( null != this.graph )
		{
			this.graph.removeGraphListener( mgl );
		}
		this.graph = new ListenableUndirectedGraph< SnakeObject, DefaultWeightedEdge >( graph );
		this.graph.addGraphListener( mgl );

		edgesAdded.clear();
		edgesModified.clear();
		edgesRemoved.clear();
		tracksUpdated.clear();

		visibility = trackVisibility;
		names = trackNames;
		connectedVertexSets = trackSnakeObjects;
		connectedEdgeSets = trackEdges;

		// Rebuild the id maps
		IDcounter = 0;
		vertexToID = new HashMap< SnakeObject, Integer >();
		for ( final Integer id : trackSnakeObjects.keySet() )
		{
			for ( final SnakeObject SnakeObject : trackSnakeObjects.get( id ) )
			{
				vertexToID.put( SnakeObject, id );
			}
			if ( id > IDcounter )
			{
				IDcounter = id;
			}
		}
		IDcounter++;

		edgeToID = new HashMap< DefaultWeightedEdge, Integer >();
		for ( final Integer id : trackEdges.keySet() )
		{
			for ( final DefaultWeightedEdge edge : trackEdges.get( id ) )
			{
				edgeToID.put( edge, id );
			}
		}

	}

	/*
	 * DEFAULT VISIBILIT METHODS made to be called from the mother model.
	 */

	void addSnakeObject( final SnakeObject SnakeObjectToAdd )
	{
		graph.addVertex( SnakeObjectToAdd );
	}

	void removeSnakeObject( final SnakeObject SnakeObjectToRemove )
	{
		graph.removeVertex( SnakeObjectToRemove );
	}

	DefaultWeightedEdge addEdge( final SnakeObject source, final SnakeObject target, final double weight )
	{
		if ( !graph.containsVertex( source ) )
		{
			graph.addVertex( source );
		}
		if ( !graph.containsVertex( target ) )
		{
			graph.addVertex( target );
		}
		final DefaultWeightedEdge edge = graph.addEdge( source, target );
		graph.setEdgeWeight( edge, weight );
		return edge;
	}

	DefaultWeightedEdge removeEdge( final SnakeObject source, final SnakeObject target )
	{
		return graph.removeEdge( source, target );
	}

	boolean removeEdge( final DefaultWeightedEdge edge )
	{
		return graph.removeEdge( edge );
	}

	void setEdgeWeight( final DefaultWeightedEdge edge, final double weight )
	{
		graph.setEdgeWeight( edge, weight );
		edgesModified.add( edge );
	}

	Boolean setVisibility( final Integer trackID, final boolean visible )
	{
		return visibility.put( trackID, Boolean.valueOf( visible ) );
	}

	/*
	 * PUBLIC METHODS
	 */

	/*
	 * GRAPH
	 */

	/**
	 * Returns a new graph with the same structure as the one wrapped here, and
	 * with vertices generated by the given {@link Function1}. Edges are copied
	 * in direction and weight.
	 *
	 * @param factory
	 *            the vertex factory used to instantiate new vertices in the new
	 *            graph
	 * @param function
	 *            the function used to set values of a new vertex in the new
	 *            graph, from the matching SnakeObject
	 * @param mappings
	 *            a map that will receive mappings from {@link SnakeObject} to the new
	 *            vertices. Can be <code>null</code> if you do not want to get
	 *            the mappings
	 * @param <V>
	 *            the type of the vertices.
	 * @return a new {@link SimpleDirectedWeightedGraph}.
	 */
	public < V > SimpleDirectedWeightedGraph< V, DefaultWeightedEdge > copy( final VertexFactory< V > factory, final Function1< SnakeObject, V > function, final Map< SnakeObject, V > mappings )
	{
		final SimpleDirectedWeightedGraph< V, DefaultWeightedEdge > copy = new SimpleDirectedWeightedGraph< V, DefaultWeightedEdge >( DefaultWeightedEdge.class );
		final Set< SnakeObject > SnakeObjects = graph.vertexSet();
		// To store mapping of old graph vs new graph
		Map< SnakeObject, V > map;
		if ( null == mappings )
		{
			map = new HashMap< SnakeObject, V >( SnakeObjects.size() );
		}
		else
		{
			map = mappings;
		}

		// Generate new vertices
		for ( final SnakeObject SnakeObject : Collections.unmodifiableCollection( SnakeObjects ) )
		{
			final V vertex = factory.createVertex();
			function.compute( SnakeObject, vertex );
			map.put( SnakeObject, vertex );
			copy.addVertex( vertex );
		}

		// Generate new edges
		for ( final DefaultWeightedEdge edge : graph.edgeSet() )
		{
			final DefaultWeightedEdge newEdge = copy.addEdge( map.get( graph.getEdgeSource( edge ) ), map.get( graph.getEdgeTarget( edge ) ) );
			copy.setEdgeWeight( newEdge, graph.getEdgeWeight( edge ) );
		}

		return copy;
	}

	/**
	 * Returns <code>true</code> if this model contains the specified edge.
	 * 
	 * @param source
	 *            the edge source vertex.
	 * @param target
	 *            the edge target vertex.
	 * @return <code>true</code> if this model contains the specified edge.
	 * 
	 * @see org.jgrapht.Graph#containsEdge(Object, Object)
	 */
	public boolean containsEdge( final SnakeObject source, final SnakeObject target )
	{
		return graph.containsEdge( source, target );
	}

	/**
	 * Returns the specified edge.
	 * 
	 * @param source
	 *            the edge source vertex.
	 * @param target
	 *            the edge target vertex.
	 * @return the specified edge, or <code>null</code> if it does not exist.
	 * 
	 * @see org.jgrapht.Graph#getEdge(Object, Object)
	 */
	public DefaultWeightedEdge getEdge( final SnakeObject source, final SnakeObject target )
	{
		return graph.getEdge( source, target );
	}

	/**
	 * Returns the set of edges of a SnakeObject.
	 * 
	 * @param SnakeObject
	 *            the SnakeObject.
	 * @return the set of edges connected to this SnakeObject. Can be empty if the SnakeObject
	 *         does not have any edge.
	 * 
	 * @see org.jgrapht.Graph#edgesOf(Object)
	 */
	public Set< DefaultWeightedEdge > edgesOf( final SnakeObject SnakeObject )
	{
		if ( graph.containsVertex( SnakeObject ) )
		{
			return graph.edgesOf( SnakeObject );
		}
		else
		{
			return Collections.emptySet();
		}
	}

	/**
	 * Returns a set of the edges contained in this model. The set is backed by
	 * the graph, so changes to the graph are reflected in the set. If the graph
	 * is modified while an iteration over the set is in progress, the results
	 * of the iteration are undefined.
	 * 
	 * @return the edges of this model.
	 * 
	 * @see org.jgrapht.Graph#edgeSet()
	 */
	public Set< DefaultWeightedEdge > edgeSet()
	{
		return graph.edgeSet();
	}

	/**
	 * Returns a set of the vertices contained in this graph. The set is backed
	 * by the graph, so changes to the graph are reflected in the set. If the
	 * graph is modified while an iteration over the set is in progress, the
	 * results of the iteration are undefined.
	 * 
	 * @return the vertices of this model.
	 * 
	 * @see org.jgrapht.Graph#vertexSet()
	 */
	public Set< SnakeObject > vertexSet()
	{
		return graph.vertexSet();
	}

	/**
	 * Returns the source SnakeObject of the specified edge.
	 * 
	 * @param e
	 *            the edge.
	 * @return the source SnakeObject of this edge.
	 * 
	 * @see org.jgrapht.Graph#getEdgeSource(Object)
	 */
	public SnakeObject getEdgeSource( final DefaultWeightedEdge e )
	{
		return graph.getEdgeSource( e );
	}

	/**
	 * Returns the target SnakeObject of the specified edge.
	 * 
	 * @param e
	 *            the edge.
	 * @return the target SnakeObject of this edge.
	 * 
	 * @see org.jgrapht.Graph#getEdgeTarget(Object)
	 */
	public SnakeObject getEdgeTarget( final DefaultWeightedEdge e )
	{
		return graph.getEdgeTarget( e );
	}

	/**
	 * Returns the weight assigned to the specified edge. Its physical meaning
	 * depend on the particle-linking algorithm used to generate the edge.
	 * 
	 * @param edge
	 *            the edge.
	 * @return the edge weight.
	 * 
	 * @see org.jgrapht.Graph#getEdgeWeight(Object)
	 */
	public double getEdgeWeight( final DefaultWeightedEdge edge )
	{
		return graph.getEdgeWeight( edge );
	}

	/*
	 * TRACKS
	 */

	/**
	 * Returns <code>true</code> if the track with the specified ID is marked as
	 * visible, <code>false</code> otherwise. Throws a
	 * {@link NullPointerException} if the track ID is unknown to this model.
	 *
	 * @param ID
	 *            the track ID.
	 * @return the track visibility.
	 */
	public boolean isVisible( final Integer ID )
	{
		return visibility.get( ID );
	}
	/**
	 * Return a new map sorted by its values. Adapted from
	 * http://stackoverflow.com
	 * /questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
	 */
	public static < K, V extends Comparable< ? super V > > Map< K, V > sortByValue( final Map< K, V > map, final Comparator< V > comparator )
	{
		final List< Map.Entry< K, V > > list = new LinkedList< Map.Entry< K, V > >( map.entrySet() );
		Collections.sort( list, new Comparator< Map.Entry< K, V > >()
		{
			@Override
			public int compare( final Map.Entry< K, V > o1, final Map.Entry< K, V > o2 )
			{
				return comparator.compare( o1.getValue(), o2.getValue() );
			}
		} );

		final LinkedHashMap< K, V > result = new LinkedHashMap< K, V >();
		for ( final Map.Entry< K, V > entry : list )
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
	/**
	 * Returns the set of track IDs managed by this model, ordered by track
	 * names (alpha-numerically sorted).
	 *
	 * @param visibleOnly
	 *            if <code>true</code>, only visible track IDs will be returned.
	 * @return a new set of track IDs.
	 */
	public Set< Integer > trackIDs( final boolean visibleOnly )
	{
		final Set< Integer > ids = sortByValue( names, AlphanumComparator.instance ).keySet();
		if ( !visibleOnly )
		{
			return ids;
		}
		else
		{
			final Set< Integer > vids = new LinkedHashSet< Integer >( ids.size() );
			for ( final Integer id : ids )
			{
				if ( visibility.get( id ) )
				{
					vids.add( id );
				}
			}
			return vids;
		}
	}

	/**
	 * Returns the set of track IDs managed by this model, unsorted. This method
	 * exists to provide better performance for callers that do not need the IDs
	 * to be sorted.
	 * 
	 * @param visibleOnly
	 *            if <code>true</code>, only visible track IDs will be returned.
	 * @return the set of track IDs.
	 */
	public Set< Integer > unsortedTrackIDs( final boolean visibleOnly )
	{
		if ( !visibleOnly )
		{
			return visibility.keySet();
		}
		else
		{
			final Set< Integer > vids = new LinkedHashSet< Integer >( visibility.size() );
			for ( final Integer id : visibility.keySet() )
			{
				if ( visibility.get( id ) )
				{
					vids.add( id );
				}
			}
			return vids;
		}
	}

	/**
	 * Returns the name of the track with the specified ID.
	 *
	 * @param id
	 *            the track ID.
	 * @return the track name.
	 */
	public String name( final Integer id )
	{
		return names.get( id );
	}

	/**
	 * Sets the name of the track with the specified ID.
	 *
	 * @param id
	 *            the track ID.
	 * @param name
	 *            the name for the track.
	 */
	public void setName( final Integer id, final String name )
	{
		names.put( id, name );
	}

	/**
	 * Returns the edges of the track with the specified ID.
	 *
	 * @param trackID
	 *            the track ID.
	 * @return the set of edges.
	 */
	public Set< DefaultWeightedEdge > trackEdges( final Integer trackID )
	{
		return connectedEdgeSets.get( trackID );
	}

	/**
	 * Returns the SnakeObjects of the track with the specified ID.
	 *
	 * @param trackID
	 *            the track ID.
	 * @return the set of SnakeObjects.
	 */
	public HashSet< SnakeObject > trackSnakeObjects( final Integer trackID )
	{
		return connectedVertexSets.get( trackID );
	}

	public int nTracks( final boolean visibleOnly )
	{
		if ( !visibleOnly )
		{
			return connectedEdgeSets.size();
		}
		else
		{
			int ntracks = 0;
			for ( final Boolean visible : visibility.values() )
			{
				if ( visible )
				{
					ntracks++;
				}
			}
			return ntracks;
		}
	}

	/**
	 * Returns the track ID the specified edge belong to, or <code>null</code>
	 * if the specified edge cannot be found in this model.
	 *
	 * @param edge
	 *            the edge to search for.
	 * @return the track ID it belongs to.
	 */
	public Integer trackIDOf( final DefaultWeightedEdge edge )
	{
		return edgeToID.get( edge );
	}

	/**
	 * Returns the track ID the specified SnakeObject belong to, or <code>null</code>
	 * if the specified SnakeObject cannot be found in this model.
	 * 
	 * @param SnakeObject
	 *            the SnakeObject to search for.
	 *
	 * @return the track ID it belongs to.
	 */
	public Integer trackIDOf( final SnakeObject SnakeObject )
	{
		return vertexToID.get( SnakeObject );
	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * Generates initial connected sets in bulk, from a graph. All sets are
	 * created visible, and are give a default name.
	 * 
	 * @param graph
	 *            the graph to read edges and vertices from.
	 */
	private void init( final UndirectedGraph< SnakeObject, DefaultWeightedEdge > graph )
	{
		vertexToID = new HashMap< SnakeObject, Integer >();
		edgeToID = new HashMap< DefaultWeightedEdge, Integer >();
		IDcounter = 0;
		visibility = new HashMap< Integer, Boolean >();
		names = new HashMap< Integer, String >();
		connectedVertexSets = new HashMap< Integer, HashSet< SnakeObject > >();
		connectedEdgeSets = new HashMap< Integer, Set< DefaultWeightedEdge > >();

		edgesAdded.clear();
		edgesModified.clear();
		edgesRemoved.clear();
		tracksUpdated.clear();

		final Set< SnakeObject > vertexSet = graph.vertexSet();
		if ( vertexSet.size() > 0 )
		{
			final BreadthFirstIterator< SnakeObject, DefaultWeightedEdge > i = new BreadthFirstIterator< SnakeObject, DefaultWeightedEdge >( graph, null );
			i.addTraversalListener( new MyTraversalListener() );

			while ( i.hasNext() )
			{
				i.next();
			}
		}
	}

	/*
	 * UTILS
	 */

	public String echo()
	{
		if ( null == connectedVertexSets ) { return "Uninitialized.\n"; }

		final StringBuilder str = new StringBuilder();
		final Set< Integer > vid = connectedVertexSets.keySet();
		final HashSet< Integer > eid = new HashSet< Integer >( connectedEdgeSets.keySet() );

		for ( final Integer id : vid )
		{
			str.append( id + ":\n" );
			str.append( " - " + connectedVertexSets.get( id ) + "\n" );
			final Set< DefaultWeightedEdge > es = connectedEdgeSets.get( id );
			if ( es == null )
			{
				str.append( " - no matching edges!\n" );
			}
			else
			{
				str.append( " - " + es + "\n" );
			}
			eid.remove( id );
		}

		if ( eid.isEmpty() )
		{
			str.append( "No remaining edges ID.\n" );
		}
		else
		{
			str.append( "Found non-matching edge IDs!\n" );
			for ( final Integer id : eid )
			{
				str.append( id + ":\n" );
				str.append( " - " + connectedEdgeSets.get( id ) + "\n" );
			}
		}

		return str.toString();
	}

	/*
	 * ITERATORS
	 */

	/**
	 * Returns a new depth first iterator over the SnakeObjects connected by links in
	 * this model. A boolean flag allow to set whether the returned iterator
	 * does take into account the edge direction. If true, the iterator will not
	 * be able to iterate backward in time.
	 *
	 * @param start
	 *            the SnakeObject to start iteration with. Can be <code>null</code>,
	 *            then the start will be taken randomly and will traverse all
	 *            the links.
	 * @param directed
	 *            if true returns a directed iterator, undirected if false.
	 * @return a new depth-first iterator.
	 */
	public GraphIterator< SnakeObject, DefaultWeightedEdge > getDepthFirstIterator( final SnakeObject start, final boolean directed )
	{
		if ( directed )
		{
			return new TimeDirectedDepthFirstIterator( graph, start );
		}
		else
		{
			return new DepthFirstIterator< SnakeObject, DefaultWeightedEdge >( graph, start );
		}
	}

	/**
	 * Returns a new depth first iterator over the SnakeObjects connected by links in
	 * this model. This iterator is sorted: when branching, it chooses the next
	 * vertex according to a specified comparator. A boolean flag allow to set
	 * whether the returned iterator does take into account the edge direction.
	 * If true, the iterator will not be able to iterate backward in time.
	 *
	 * @param start
	 *            the SnakeObject to start iteration with. Can be <code>null</code>,
	 *            then the start will be taken randomly and will traverse all
	 *            the links.
	 * @param directed
	 *            if true returns a directed iterator, undirected if false.
	 * @param comparator
	 *            the comparator to use to pick children in order when
	 *            branching.
	 * @return a new depth-first iterator.
	 */
	public SortedDepthFirstIterator< SnakeObject, DefaultWeightedEdge > getSortedDepthFirstIterator( final SnakeObject start, final Comparator< SnakeObject > comparator, final boolean directed )
	{
		if ( directed )
		{
			return new TimeDirectedSortedDepthFirstIterator( graph, start, comparator );
		}
		else
		{
			return new SortedDepthFirstIterator< SnakeObject, DefaultWeightedEdge >( graph, start, comparator );
		}
	}

	public TimeDirectedNeighborIndex getDirectedNeighborIndex()
	{
		return new TimeDirectedNeighborIndex( graph );
	}

	/**
	 * Returns the shortest path between two connected SnakeObject, using Dijkstra's
	 * algorithm. The edge weights, if any, are ignored here, meaning that the
	 * returned path is the shortest in terms of number of edges.
	 * <p>
	 * Returns <code>null</code> if the two SnakeObjects are not connected by a track,
	 * or if one of the SnakeObject do not belong to the graph, or if the graph field
	 * is <code>null</code>.
	 *
	 * @param source
	 *            the SnakeObject to start the path with
	 * @param target
	 *            the SnakeObject to stop the path with
	 * @return the shortest path, as a list of edges.
	 */
	public List< DefaultWeightedEdge > dijkstraShortestPath( final SnakeObject source, final SnakeObject target )
	{
		if ( null == graph ) { return null; }
		final AsUnweightedGraph< SnakeObject, DefaultWeightedEdge > unWeightedGrah = new AsUnweightedGraph< SnakeObject, DefaultWeightedEdge >( graph );
		final DijkstraShortestPath< SnakeObject, DefaultWeightedEdge > pathFinder = new DijkstraShortestPath< SnakeObject, DefaultWeightedEdge >( unWeightedGrah, source, target );
		final List< DefaultWeightedEdge > path = pathFinder.getPathEdgeList();
		return path;
	}

	/*
	 * Inner Classes
	 */

	private class MyTraversalListener implements TraversalListener< SnakeObject, DefaultWeightedEdge >
	{
		private HashSet<SnakeObject> currentConnectedVertexSet;

		private Set< DefaultWeightedEdge > currentConnectedEdgeSet;

		private Integer ID;

		/**
		 * Called when after traversing a connected set. Stores it, gives it
		 * default visibility, and a default name. Discard sets made of 1
		 * vertices or 0 edges.
		 */
		@Override
		public void connectedComponentFinished( final ConnectedComponentTraversalEvent event )
		{
			if ( currentConnectedVertexSet.size() <= 1 || currentConnectedEdgeSet.size() == 0 )
			{
				// Forget them
				for ( final DefaultWeightedEdge e : currentConnectedEdgeSet )
				{
					edgeToID.remove( e );
				}
				for ( final SnakeObject v : currentConnectedVertexSet )
				{
					vertexToID.remove( v );
				}
				return;
			}
			// Adds them
			connectedVertexSets.put( ID, currentConnectedVertexSet );
			connectedEdgeSets.put( ID, currentConnectedEdgeSet );
			visibility.put( ID, DEFAULT_VISIBILITY );
			names.put( ID, nameGenerator.next() );
		}

		/**
		 * @see TraversalListenerAdapter#connectedComponentStarted(ConnectedComponentTraversalEvent)
		 */
		@Override
		public void connectedComponentStarted( final ConnectedComponentTraversalEvent e )
		{
			currentConnectedVertexSet = new HashSet< SnakeObject >();
			currentConnectedEdgeSet = new HashSet< DefaultWeightedEdge >();
			ID = IDcounter++;
		}

		/**
		 * @see TraversalListenerAdapter#vertexTraversed(VertexTraversalEvent)
		 */
		@Override
		public void vertexTraversed( final VertexTraversalEvent< SnakeObject > event )
		{
			final SnakeObject v = event.getVertex();
			currentConnectedVertexSet.add( v );
			vertexToID.put( v, ID );
		}

		@Override
		public void edgeTraversed( final EdgeTraversalEvent< SnakeObject, DefaultWeightedEdge > event )
		{
			final DefaultWeightedEdge e = event.getEdge();
			currentConnectedEdgeSet.add( e );
			edgeToID.put( e, ID );
		}

		@Override
		public void vertexFinished( final VertexTraversalEvent< SnakeObject > e )
		{}
	}

	/**
	 * This listener class is made to deal with complex changes in the track
	 * graph.
	 * <p>
	 * By complex change, we mean the changes occurring in the graph caused by
	 * another change that was initiated manually by the user. For instance,
	 * imagine we have a simple track branch made of 5 SnakeObjects that link linearly,
	 * like this:
	 *
	 * <pre>
	 * S1 - S2 - S3 - S4 - S5
	 * </pre>
	 *
	 * The user might want to remove the S3 SnakeObject, in the middle of the track. On
	 * top of the track rearrangement, that is dealt with elsewhere in the model
	 * class, this SnakeObject removal also triggers 2 edges removal: the links S2-S3
	 * and S3-S4 disappear. The only way for the {@link TrackModel} to be aware
	 * of that, and to forward these events to its listener, is to listen itself
	 * to the {@link #graph} that store links.
	 * <p>
	 * This is done through this class. This class is notified every time a
	 * change occur in the {@link #graph}:
	 * <ul>
	 * <li>It ignores events triggered by SnakeObjects being added or removed, because
	 * they can't be triggered automatically, and are dealt with in the
	 * {@link TrackModel#addSnakeObjectTo(SnakeObject, Integer)} and
	 * {@link TrackModel#removeSnakeObject(SnakeObject, Integer)} methods.
	 * <li>It catches all events triggered by a link being added or removed in
	 * the graph, whether they are triggered manually through a call to a model
	 * method such as {@link TrackModel#addEdge(SnakeObject, SnakeObject, double)}, or
	 * triggered by another call. They are used to build the
	 * {@link TrackModel#edgesAdded} and {@link TrackModel#edgesRemoved} fields,
	 * that will be used to notify listeners of the model.
	 *
	 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt; Aug 12, 2011
	 *
	 */
	private class MyGraphListener implements GraphListener< SnakeObject, DefaultWeightedEdge >
	{

		@Override
		public void vertexAdded( final GraphVertexChangeEvent< SnakeObject > event )
		{}

		@Override
		public void vertexRemoved( final GraphVertexChangeEvent< SnakeObject > event )
		{
			if ( null == connectedEdgeSets ) { return; }

			final SnakeObject v = event.getVertex();
			vertexToID.remove( v );
			final Integer id = vertexToID.get( v );
			if ( id != null )
			{
				final Set< SnakeObject > set = connectedVertexSets.get( id );
				if ( null == set ) { return; // it was removed when removing the
												// last edge of a track, most
												// likely.
				}
				set.remove( v );

				if ( set.isEmpty() )
				{
					connectedEdgeSets.remove( id );
					connectedVertexSets.remove( id );
					names.remove( id );
					visibility.remove( id );
				}
			}
		}

		@Override
		public void edgeAdded( final GraphEdgeChangeEvent< SnakeObject, DefaultWeightedEdge > event )
		{
			// To signal to ModelChangeListener
			edgesAdded.add( event.getEdge() );

			// To maintain connected sets coherence:
			/*
			 * This is the tricky part: when we add an edge to our set model,
			 * first we need to find to what existing set it has been added.
			 * Then a new edge sometime come with 1 or 2 vertices that might be
			 * new or belonging to an existing set.
			 */
			final DefaultWeightedEdge e = event.getEdge();

			// Was it added to known tracks?
			final SnakeObject sv = graph.getEdgeSource( e );
			final Integer sid = vertexToID.get( sv );
			final SnakeObject tv = graph.getEdgeTarget( e );
			final Integer tid = vertexToID.get( tv );

			if ( null != tid && null != sid )
			{
				// Case 1: it was added between two existing sets. We connect
				// them, therefore
				// and take the id of the largest one. The other id, disappear,
				// unless they
				// belonged to the same set.

				// Did they come from the same set?
				if ( tid.equals( sid ) )
				{
					// They come from the same set (equals ID). Not much to do.
					final Set< DefaultWeightedEdge > ses = connectedEdgeSets.get( sid );
					ses.add( e );
					edgeToID.put( e, sid );

				}
				else
				{
					// They come from different sets.

					// Edges:
					final Set< DefaultWeightedEdge > ses = connectedEdgeSets.get( sid );
					final Set< DefaultWeightedEdge > tes = connectedEdgeSets.get( tid );
					final HashSet< DefaultWeightedEdge > nes = new HashSet< DefaultWeightedEdge >( ses.size() + tes.size() + 1 );
					nes.addAll( ses );
					nes.addAll( tes );
					nes.add( e );

					// Vertices:
					final Set< SnakeObject > svs = connectedVertexSets.get( sid );
					final Set< SnakeObject > tvs = connectedVertexSets.get( tid );
					final HashSet< SnakeObject > nvs = new HashSet< SnakeObject >( ses.size() + tes.size() );
					nvs.addAll( svs );
					nvs.addAll( tvs );

					Integer nid, rid;
					if ( nvs.size() > tvs.size() )
					{
						nid = sid;
						rid = tid;
						for ( final SnakeObject v : tvs )
						{
							// Vertices of target set change id
							vertexToID.put( v, nid );
						}
						for ( final DefaultWeightedEdge te : tes )
						{
							edgeToID.put( te, nid );
						}
					}
					else
					{
						nid = tid;
						rid = sid;
						for ( final SnakeObject v : svs )
						{
							// Vertices of source set change id
							vertexToID.put( v, nid );
						}
						for ( final DefaultWeightedEdge se : ses )
						{
							edgeToID.put( se, nid );
						}
					}
					edgeToID.put( e, nid );
					connectedVertexSets.put( nid, nvs );
					connectedVertexSets.remove( rid );
					connectedEdgeSets.put( nid, nes );
					connectedEdgeSets.remove( rid );

					// Transaction: we signal that the large id is to be
					// updated, and forget about the small one
					tracksUpdated.add( nid );
					tracksUpdated.remove( rid );

					// Visibility: if at least one is visible, the new set is
					// made visible.
					final Boolean targetVisibility = visibility.get( sid ) || visibility.get( tid );
					visibility.put( nid, targetVisibility );
					visibility.remove( rid );

					// Name: the new set gets the name of the largest one.
					names.remove( rid ); // 'nid' already has the right name.
				}

			}
			else if ( null == sid && null == tid )
			{
				// Case 4: the edge was added between two lonely vertices.
				// Create a new set id from this
				final HashSet< SnakeObject > nvs = new HashSet< SnakeObject >( 2 );
				nvs.add( graph.getEdgeSource( e ) );
				nvs.add( graph.getEdgeTarget( e ) );

				final HashSet< DefaultWeightedEdge > nes = new HashSet< DefaultWeightedEdge >( 1 );
				nes.add( e );

				final int nid = IDcounter++;
				connectedEdgeSets.put( nid, nes );
				connectedVertexSets.put( nid, nvs );
				vertexToID.put( sv, nid );
				vertexToID.put( tv, nid );
				edgeToID.put( e, nid );

				// Give it visibility
				visibility.put( nid, Boolean.TRUE );
				// and a default name.
				names.put( nid, nameGenerator.next() );
				// Transaction: we mark the new track as updated
				tracksUpdated.add( nid );

			}
			else if ( null == sid )
			{
				// Case 2: the edge was added to the target set. No source set,
				// but there is a source vertex.
				// Add it, with the source vertex, to the target id.
				connectedEdgeSets.get( tid ).add( e );
				edgeToID.put( e, tid );
				connectedVertexSets.get( tid ).add( sv );
				vertexToID.put( sv, tid );
				// We do not change the visibility, nor the name.
				// Transaction: we mark the mother track as updated
				tracksUpdated.add( tid );

			}
			else if ( null == tid )
			{
				// Case 3: the edge was added to the source set. No target set,
				// but there is a target vertex.
				// Add it, with the target vertex, to the source id.
				connectedEdgeSets.get( sid ).add( e );
				edgeToID.put( e, sid );
				connectedVertexSets.get( sid ).add( tv );
				vertexToID.put( tv, sid );
				// We do not change the visibility, nor the name.
				// Transaction: we mark the mother track as updated
				tracksUpdated.add( sid );

			}

		}

		@Override
		public void edgeRemoved( final GraphEdgeChangeEvent< SnakeObject, DefaultWeightedEdge > event )
		{
			// To signal to ModelChangeListeners
			edgesRemoved.add( event.getEdge() );

			// To maintain connected sets coherence

			final DefaultWeightedEdge e = event.getEdge();
			final Integer id = edgeToID.get( e );
			if ( null == id ) { throw new RuntimeException( "Edge is unkown to this model: " + e ); }
			final Set< DefaultWeightedEdge > set = connectedEdgeSets.get( id );
			if ( null == set ) { throw new RuntimeException( "Unknown set ID: " + id ); }

			// Remove edge from set.
			final boolean removed = set.remove( e );
			if ( !removed ) { throw new RuntimeException( "Could not removed edge " + e + " from set with ID: " + id ); }
			// Forget about edge.
			edgeToID.remove( e );

			/*
			 * Ok the trouble is that now we might be left with 2 sets if the
			 * edge "was in the middle". Or 1 if it was in the end. Or 0 if it
			 * was the last edge of the set.
			 */

			if ( set.size() == 0 )
			{
				// The set is empty, remove it from the map.
				connectedEdgeSets.remove( id );
				names.remove( id );
				visibility.remove( id );
				/* We need to remove also the vertices */
				final Set< SnakeObject > vertexSet = connectedVertexSets.get( id );
				// Forget the vertices were in a set
				for ( final SnakeObject SnakeObject : vertexSet )
				{
					vertexToID.remove( SnakeObject );
				}
				// Forget the vertex set
				connectedVertexSets.remove( id );
				/*
				 * We do not mark it as a track to update, for it disappeared.
				 * On the other hand, it might *have been* marked as a track to
				 * update. Since it just joined oblivion, we remove it from the
				 * list of tracks to update.
				 */
				tracksUpdated.remove( id );

			}
			else
			{
				// So there are some edges remaining in the set.
				// Look at the connected component of its source and target.
				// Source
				final HashSet< SnakeObject > sourceVCS = new HashSet< SnakeObject >();
				final HashSet< DefaultWeightedEdge > sourceECS = new HashSet< DefaultWeightedEdge >();
				{
					final SnakeObject source = graph.getEdgeSource( e );
					// Get its connected set
					final BreadthFirstIterator< SnakeObject, DefaultWeightedEdge > i = new BreadthFirstIterator< SnakeObject, DefaultWeightedEdge >( graph, source );
					while ( i.hasNext() )
					{
						final SnakeObject sv = i.next();
						sourceVCS.add( sv );
						sourceECS.addAll( graph.edgesOf( sv ) );
					}
				}
				// Target
				final HashSet< SnakeObject > targetVCS = new HashSet< SnakeObject >();
				final HashSet< DefaultWeightedEdge > targetECS = new HashSet< DefaultWeightedEdge >();
				{
					final SnakeObject target = graph.getEdgeTarget( e );
					// Get its connected set
					final BreadthFirstIterator< SnakeObject, DefaultWeightedEdge > i = new BreadthFirstIterator< SnakeObject, DefaultWeightedEdge >( graph, target );
					while ( i.hasNext() )
					{
						final SnakeObject sv = i.next();
						targetVCS.add( sv );
						targetECS.addAll( graph.edgesOf( sv ) );
					}
				}

				/*
				 * If the two connected components are the same, it means that
				 * the edge was an "internal" edge: Because there is another
				 * path that connect its source and target, removing it did NOT
				 * split the track in 2. We therefore need not to re-attribute
				 * it.
				 */
				if ( targetVCS.equals( sourceVCS ) )
				{
					tracksUpdated.add( id );
					connectedEdgeSets.get( id ).remove( e );
					return;
				}

				/*
				 * Re-attribute the found connected sets to the model. The
				 * largest one (in vertices) gets the original id, the other
				 * gets a new id. As for names: the largest one keeps its name,
				 * the small one gets a new name.
				 */

				if ( targetVCS.size() > sourceVCS.size() )
				{

					connectedEdgeSets.put( id, targetECS );
					connectedVertexSets.put( id, targetVCS ); // they already
																// have the
																// right id in
																// #vertexToId
					tracksUpdated.add( id ); // old track has changed

					if ( sourceECS.size() > 0 )
					{
						// the smaller part is still a track
						final int newid = IDcounter++;
						connectedEdgeSets.put( newid, sourceECS ); // otherwise
																	// forget it
						for ( final DefaultWeightedEdge te : sourceECS )
						{
							edgeToID.put( te, newid );
						}
						connectedVertexSets.put( newid, sourceVCS );
						for ( final SnakeObject tv : sourceVCS )
						{
							vertexToID.put( tv, newid );
						}
						final Boolean targetVisibility = visibility.get( id );
						visibility.put( newid, targetVisibility );
						names.put( newid, nameGenerator.next() );
						// Transaction: both children tracks are marked for
						// update.
						tracksUpdated.add( newid );

					}
					else
					{
						/*
						 * Nothing remains from the smallest part. The remaining
						 * solitary vertex has no right to be called a track.
						 */
						final SnakeObject solitary = sourceVCS.iterator().next();
						vertexToID.remove( solitary );
					}

				}
				else
				{

					if ( sourceECS.size() > 0 )
					{
						// There are still some pieces left. It is worth noting
						// it.
						connectedEdgeSets.put( id, sourceECS );
						connectedVertexSets.put( id, sourceVCS );
						tracksUpdated.add( id );

						if ( targetECS.size() > 0 )
						{
							// the small part is still a track
							final int newid = IDcounter++;
							connectedEdgeSets.put( newid, targetECS );
							for ( final DefaultWeightedEdge te : targetECS )
							{
								edgeToID.put( te, newid );
							}
							connectedVertexSets.put( newid, targetVCS );
							for ( final SnakeObject v : targetVCS )
							{
								vertexToID.put( v, newid );
							}
							final Boolean targetVisibility = visibility.get( id );
							visibility.put( newid, targetVisibility );
							names.put( newid, nameGenerator.next() );
							// Transaction: both children tracks are marked for
							// update.
							tracksUpdated.add( newid );
						}
						else
						{
							/*
							 * Nothing remains from the smallest part. The
							 * remaining solitary vertex has no right to be
							 * called a track.
							 */
							final SnakeObject solitary = targetVCS.iterator().next();
							vertexToID.remove( solitary );
						}

					}
					else
					{
						// Nothing remains (maybe a solitary vertex) -> forget
						// about it all.
						connectedEdgeSets.remove( id );
						connectedVertexSets.remove( id );
						names.remove( id );
						visibility.remove( id );
						tracksUpdated.remove( id );
					}

				}
			}
		}

	}

	private static class DefaultNameGenerator implements Iterator< String >
	{

		private int nameID = 0;

		@Override
		public boolean hasNext()
		{
			return true;
		}

		@Override
		public String next()
		{
			return "Track_" + nameID++;
		}

		@Override
		public void remove()
		{}

	}

	
}
