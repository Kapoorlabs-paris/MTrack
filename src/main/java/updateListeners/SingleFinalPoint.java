/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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

import ij.IJ;
import interactiveMT.BatchMode;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import interactiveMT.Interactive_MTSingleChannel.WhichendSingle;
import listeners.SkipFramesandTrackendsListener;
import interactiveMT.Interactive_MTSingleChannelBasic;
import interactiveMT.SingleBatchMode;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import singleListeners.SingleSkipFramesandTrackendsListener;

public class SingleFinalPoint implements ActionListener {

	final Interactive_MTSingleChannel parent;

	final Interactive_MTSingleChannelBasic child;

	final SingleBatchMode batch;
	
	public SingleFinalPoint(final Interactive_MTSingleChannel parent, final Interactive_MTSingleChannelBasic child) {

		this.parent = parent;
		this.child = child;
		this.batch = null;
	}

	public SingleFinalPoint(final Interactive_MTSingleChannel parent) {

		this.parent = parent;
		this.child = null;
		this.batch = null;
	}
	
	public SingleFinalPoint(final SingleBatchMode batch) {

		this.parent = null;
		this.child = null;
		this.batch = batch;
	}
	

	@Override
	public void actionPerformed(ActionEvent arg0)  {
	
			FinalizeEnds();


			
			
			
			
			
		

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

				mindistA = util.Boundingboxes.Distance(parent.ClickedPoints.get(index).getA(), endAmap.get(i));
				mindistB = util.Boundingboxes.Distance(parent.ClickedPoints.get(index).getA(), endBmap.get(i));

				if (mindistA <= 1 && parent.seedmap.get(i) != WhichendSingle.end) {

					parent.seedmap.put(i, WhichendSingle.start);

					int seedid = i;
					double[] seedpos = parent.PrevFrameparam.getA().get(count).fixedpos;
					Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);
					parent.IDALL.add(seedpair);
				}

				else if (mindistB <= 1 && parent.seedmap.get(i) != WhichendSingle.start) {

					parent.seedmap.put(i, WhichendSingle.end);

					int seedid = i;
					double[] seedpos = parent.PrevFrameparam.getB().get(count).fixedpos;
					Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);
					parent.IDALL.add(seedpair);

				}

				else if (parent.seedmap.get(i) == WhichendSingle.start && mindistB <= 1) {
					parent.seedmap.put(i, WhichendSingle.both);

				}

				else if (parent.seedmap.get(i) == WhichendSingle.end && mindistA <= 1) {
					parent.seedmap.put(i, WhichendSingle.both);
				}

				else if (parent.seedmap.get(i) == null) {
					parent.seedmap.put(i, WhichendSingle.none);
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
			
			SingleSkipFramesandTrackendsListener track =  new SingleSkipFramesandTrackendsListener(parent, child, parent.starttime, parent.endtime);
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

			
			
				batch.seedmap.put(i, WhichendSingle.both);
				

		}

		for (int index = 0; index < batch.Userframe.size(); ++index) {

			int seedid = batch.Userframe.get(index).seedLabel;
			double[] seedpos = batch.Userframe.get(index).fixedpos;
			Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);

			batch.IDALL.add(seedpair);

		}

	
	}
	
	
	
	
}
