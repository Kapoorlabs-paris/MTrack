package updateListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ij.gui.Overlay;
import interactiveMT.BatchMode;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_MTDoubleChannel.Whichend;
import listeners.SkipFramesandTrackendsListener;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class FinalPoint implements ActionListener {

	final Interactive_MTDoubleChannel parent;

	final Interactive_MTDoubleChannelBasic child;

	final BatchMode batch;
	 int starttime;
    int endtime;
	
	public FinalPoint(final Interactive_MTDoubleChannel parent, final Interactive_MTDoubleChannelBasic child, final int starttime, final int endtime) {

		this.parent = parent;
		this.child = child;
		this.batch = null;
		this.starttime = starttime;
		this.endtime = endtime;
	}

	public FinalPoint(final Interactive_MTDoubleChannel parent) {

		this.parent = parent;
		this.child = null;
		this.batch = null;
		
	}
	
	public FinalPoint(final BatchMode batch) {

		this.parent = null;
		this.child = null;
		this.batch = batch;
	}
	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		

			FinalizeEnds();

		

	}

	public double Distance(final double[] cordone, final double[] cordtwo) {

		double distance = 0;

		int ndims = cordone.length;
		for (int d = 0; d < ndims; ++d) {

			distance += Math.pow((cordone[d] - cordtwo[d]), 2);

		}
		return Math.sqrt(distance);
	}


	public void FinalizeEnds() {

		parent.preprocessedimp.getCanvas().removeMouseListener(parent.removeml);
		parent.preprocessedimp.getCanvas().removeMouseListener(parent.ml);

		HashMap<Integer, double[]> endAmap = new HashMap<Integer, double[]>();

		HashMap<Integer, double[]> endBmap = new HashMap<Integer, double[]>();

		Collections.sort(parent.PrevFrameparam.getA(), parent.Seedcompare);
		Collections.sort(parent.PrevFrameparam.getB(), parent.Seedcompare);

	

		Set<Integer> Actualseed = new HashSet<Integer>();
		
		for (int i = 0; i < parent.PrevFrameparam.getA().size(); ++i) {

			if (parent.PrevFrameparam.getA().get(i).fixedpos!=null){
			endAmap.put(parent.PrevFrameparam.getA().get(i).seedLabel, parent.PrevFrameparam.getA().get(i).fixedpos);
			Actualseed.add(parent.PrevFrameparam.getA().get(i).seedLabel);
			}

		}

		for (int i = 0; i < parent.PrevFrameparam.getB().size(); ++i) {

			if (parent.PrevFrameparam.getB().get(i).fixedpos!=null){
			endBmap.put(parent.PrevFrameparam.getB().get(i).seedLabel, parent.PrevFrameparam.getB().get(i).fixedpos);
			Actualseed.add(parent.PrevFrameparam.getB().get(i).seedLabel);
			}

		}
	
		
		
		Iterator<Integer> iter = Actualseed.iterator();
		int count = 0;
		while(iter.hasNext()){
			
			int i = iter.next();
			
			for (int index = 0; index < parent.ClickedPoints.size(); ++index) {

				double mindistA = 0;
				double mindistB = 0;

				double cutdist = 1;
				mindistA = util.Boundingboxes.Distance(parent.ClickedPoints.get(index).getA(), endAmap.get(i));
				mindistB = util.Boundingboxes.Distance(parent.ClickedPoints.get(index).getA(), endBmap.get(i));

				if ( mindistA <= cutdist  && parent.seedmap.get(i) != Whichend.end) {

					parent.seedmap.put(i, Whichend.start);

					int seedid = i;
					double[] seedpos = parent.PrevFrameparam.getA().get(count).fixedpos;
					Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);
					parent.IDALL.add(seedpair);
				}

				else if (mindistB <= cutdist  && parent.seedmap.get(i) != Whichend.start) {

					parent.seedmap.put(i, Whichend.end);

					int seedid = i;
					double[] seedpos = parent.PrevFrameparam.getB().get(count).fixedpos;
					Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);
					parent.IDALL.add(seedpair);

				}

				else if (parent.seedmap.get(i) == Whichend.start && mindistB <= cutdist  ) {
					parent.seedmap.put(i, Whichend.both);

				}

				else if (parent.seedmap.get(i) == Whichend.end && mindistA <= cutdist ) {
					parent.seedmap.put(i, Whichend.both);
				}

				else if (parent.seedmap.get(i) == null) {
					parent.seedmap.put(i, Whichend.none);
                  
				}
			
			
		}
		
			count++;

			

			

		}

		for (int index = 0; index < parent.Userframe.size(); ++index) {

			int seedid = parent.Userframe.get(index).seedLabel;
			double[] seedpos = parent.Userframe.get(index).fixedpos;
			Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);

			parent.IDALL.add(seedpair);

		}

		parent.ShowMser = true;

		parent.updatePreview(ValueChange.SHOWMSER);

		if (child != null){
			
			SkipFramesandTrackendsListener track =  new SkipFramesandTrackendsListener(parent, starttime, endtime);
			track.goSkip();
			
		}
			

	}
	
	public void BatchFinalizeEnds() {

		

		HashMap<Integer, double[]> endAmap = new HashMap<Integer, double[]>();

		HashMap<Integer, double[]> endBmap = new HashMap<Integer, double[]>();

		Collections.sort(batch.PrevFrameparam.getA(), batch.Seedcompare);
		Collections.sort(batch.PrevFrameparam.getB(), batch.Seedcompare);

		int minSeed = batch.PrevFrameparam.getA().get(0).seedLabel;
		int maxSeed = batch.PrevFrameparam.getA().get(batch.PrevFrameparam.getA().size() - 1).seedLabel;

		for (int i = 0; i < batch.PrevFrameparam.getA().size(); ++i) {

			endAmap.put(batch.PrevFrameparam.getA().get(i).seedLabel, batch.PrevFrameparam.getA().get(i).fixedpos);

		}

		for (int i = 0; i < batch.PrevFrameparam.getB().size(); ++i) {

			endBmap.put(batch.PrevFrameparam.getB().get(i).seedLabel, batch.PrevFrameparam.getB().get(i).fixedpos);

		}

		for (int i = minSeed; i < maxSeed + 1; ++i) {

			
			
				batch.seedmap.put(i, Whichend.both);
				

		}

		for (int index = 0; index < batch.Userframe.size(); ++index) {

			int seedid = batch.Userframe.get(index).seedLabel;
			double[] seedpos = batch.Userframe.get(index).fixedpos;
			Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);

			batch.IDALL.add(seedpair);

		}

	
	}
	
	
	
	
}
