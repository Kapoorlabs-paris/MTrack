package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import fit.AbstractFunction2D;
import fit.PointFunctionMatch;
import fit.polynomial.LinearFunction;
import fit.polynomial.Polynomial;
import ij.measure.ResultsTable;
import mpicbg.models.Point;
import mt.Averagerate;
import mt.Rateobject;
import mt.Tracking;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class AutoCompileResultsListener implements ActionListener {

	final InteractiveRANSAC parent;
	final int index;
	

	public AutoCompileResultsListener(final InteractiveRANSAC parent, final int index) {
		this.parent = parent;
		this.index = index;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		
		
		int nThreads = 1;
		// set up executor service
		final ExecutorService taskexecutor = Executors.newFixedThreadPool(nThreads);
		 List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (int trackindex = index; trackindex < parent.inputfiles.length; ++trackindex){
			
			tasks.add(Executors.callable(new Split(parent, trackindex)));
			
		}
		try {
			taskexecutor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		

	}

	

}
