package swingClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import MTObjects.ResultsMT;
import drawandOverlay.DisplayGraph;
import drawandOverlay.DisplayGraphKalman;
import graphconstructs.KalmanTrackproperties;
import graphconstructs.Trackproperties;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import lineFinder.FindlinesVia;
import lineFinder.LinefinderInteractiveHFHough;
import lineFinder.LinefinderInteractiveHFMSER;
import lineFinder.LinefinderInteractiveHFMSERwHough;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import trackerType.KFsearch;
import trackerType.TrackModel;
import updateListeners.FinalPoint;
import velocityanalyser.Trackend;
import velocityanalyser.Trackstart;

public class ProgressTrack extends SwingWorker<Void, Void> {

final Interactive_MTDoubleChannel parent;
final int starttime;
final int endtime;
	
	public ProgressTrack(final Interactive_MTDoubleChannel parent, final int starttime, final int endtime){
	
		this.parent = parent;
		this.starttime = starttime;
		this.endtime = endtime;
	}
	
	
	
	
	@Override
	protected Void doInBackground() throws Exception {

		int next = 2;
		
		Track newtrack = new Track(parent);
		newtrack.Trackobject(next);

		return null;
	}
	@Override
	protected void done() {
		try {
			
			
			
			
			parent.jpb.setIndeterminate(false);
			get();
			parent.frame.dispose();
			IJ.log("Tracking Done and track files written in the chosen folder");

		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	

	

}