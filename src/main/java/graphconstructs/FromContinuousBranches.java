package graphconstructs;



import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class FromContinuousBranches implements OutputAlgorithm< SimpleWeightedGraph< Staticproperties, DefaultWeightedEdge > >, Benchmark
{

	private static final String BASE_ERROR_MSG = "[FromContinuousBranches] ";

	private long processingTime;

	private final Collection< List< Staticproperties >> branches;

	private final Collection< List< Staticproperties >> links;

	private String errorMessage;

	private SimpleWeightedGraph< Staticproperties, DefaultWeightedEdge > graph;

	public FromContinuousBranches( final Collection< List< Staticproperties >> branches, final Collection< List< Staticproperties >> links )
	{
		this.branches = branches;
		this.links = links;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public boolean checkInput()
	{
		final long start = System.currentTimeMillis();
		if ( null == branches )
		{
			errorMessage = BASE_ERROR_MSG + "branches are null.";
			return false;
		}
		if ( null == links )
		{
			errorMessage = BASE_ERROR_MSG + "links are null.";
			return false;
		}
		for ( final List< Staticproperties > link : links )
		{
			if ( link.size() != 2 )
			{
				errorMessage = BASE_ERROR_MSG + "A link is not made of two Staticpropertiess.";
				return false;
			}
			if ( !checkIfInBranches( link.get( 0 ) ) )
			{
				errorMessage = BASE_ERROR_MSG + "A Staticproperties in a link is not present in the branch collection: " + link.get( 0 ) + " in the link " + link.get( 0 ) + "-" + link.get( 1 ) + ".";
				return false;
			}
			if ( !checkIfInBranches( link.get( 1 ) ) )
			{
				errorMessage = BASE_ERROR_MSG + "A Staticproperties in a link is not present in the branch collection: " + link.get( 1 ) + " in the link " + link.get( 0 ) + "-" + link.get( 1 ) + ".";
				return false;
			}
		}
		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public boolean process()
	{
		final long start = System.currentTimeMillis();

		graph = new SimpleWeightedGraph< Staticproperties, DefaultWeightedEdge >( DefaultWeightedEdge.class );
		for ( final List< Staticproperties > branch : branches )
		{
			for ( final Staticproperties Staticproperties : branch )
			{
				graph.addVertex( Staticproperties );
			}
		}

		for ( final List< Staticproperties > branch : branches )
		{
			final Iterator< Staticproperties > it = branch.iterator();
			Staticproperties previous = it.next();
			while ( it.hasNext() )
			{
				final Staticproperties Staticproperties = it.next();
				graph.addEdge( previous, Staticproperties );
				previous = Staticproperties;
			}
		}

		for ( final List< Staticproperties > link : links )
		{
			graph.addEdge( link.get( 0 ), link.get( 1 ) );
		}

		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public SimpleWeightedGraph< Staticproperties, DefaultWeightedEdge > getResult()
	{
		return graph;
	}

	private final boolean checkIfInBranches( final Staticproperties Staticproperties )
	{
		for ( final List< Staticproperties > branch : branches )
		{
			if ( branch.contains( Staticproperties ) ) { return true; }
		}
		return false;
	}

}
