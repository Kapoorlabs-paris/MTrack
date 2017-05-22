package updateListeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannelBasic;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_MTDoubleChannel.Whichend;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class FinalPoint implements ItemListener {

final Interactive_MTDoubleChannel parent;
	
final Interactive_MTDoubleChannelBasic child;

	public FinalPoint(final Interactive_MTDoubleChannel parent, final Interactive_MTDoubleChannelBasic child ){
	
		this.parent = parent;
		this.child = child;
	}
	public FinalPoint(final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
		this.child = null;
	}
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.finalpoint = false;

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.finalpoint = true;

		FinalizeEnds();

	}

}

	
public void FinalizeEnds(){
	

	parent.preprocessedimp.getCanvas().removeMouseListener(parent.removeml);
	parent.preprocessedimp.getCanvas().removeMouseListener(parent.ml);

	HashMap<Integer, double[]> endAmap = new HashMap<Integer, double[]>();

	HashMap<Integer, double[]> endBmap = new HashMap<Integer, double[]>();

	

	Collections.sort(parent.PrevFrameparam.getA(), parent.Seedcompare);
	Collections.sort(parent.PrevFrameparam.getB(), parent.Seedcompare);

	int minSeed = parent.PrevFrameparam.getA().get(0).seedLabel;
	int maxSeed = parent.PrevFrameparam.getA().get(parent.PrevFrameparam.getA().size() - 1).seedLabel;

	for (int i = 0; i < parent.PrevFrameparam.getA().size(); ++i) {

		endAmap.put(parent.PrevFrameparam.getA().get(i).seedLabel, parent.PrevFrameparam.getA().get(i).fixedpos);

	}

	for (int i = 0; i < parent.PrevFrameparam.getB().size(); ++i) {

		endBmap.put(parent.PrevFrameparam.getB().get(i).seedLabel, parent.PrevFrameparam.getB().get(i).fixedpos);

	}

	
	for (int i = minSeed; i < maxSeed + 1; ++i) {

		
		for (int index = 0; index < parent.ClickedPoints.size(); ++index) {

			double mindistA = 0;
			double mindistB = 0;

			mindistA = util.Boundingboxes.Distance(parent.ClickedPoints.get(index).getA(), endAmap.get(i));
			mindistB = util.Boundingboxes.Distance(parent.ClickedPoints.get(index).getA(), endBmap.get(i));

			if (mindistA <= 1 && parent.seedmap.get(i) != Whichend.end) {

				
				parent.seedmap.put(i, Whichend.start);
				
				int seedid = i;
				double[] seedpos = parent.ClickedPoints.get(index).getA();
				Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);
				parent.IDALL.add(seedpair);
			}

			else if (mindistB <= 1 && parent.seedmap.get(i) != Whichend.start) {

				parent.seedmap.put(i, Whichend.end);
				
				int seedid = i;
				double[] seedpos = parent.ClickedPoints.get(index).getA();
				Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);
				parent.IDALL.add(seedpair);
				
			}

			else if (parent.seedmap.get(i) == Whichend.start && mindistB <= 1){
				parent.seedmap.put(i, Whichend.both);
				
				
				
			}

			else if (parent.seedmap.get(i) == Whichend.end && mindistA <= 1){
				parent.seedmap.put(i, Whichend.both);
			}

			else if (parent.seedmap.get(i) == null){
				parent.seedmap.put(i, Whichend.none);
			}

		}

	}
	

	for (int index = 0 ; index < parent.Userframe.size(); ++index){
		
		int seedid = parent.Userframe.get(index).seedLabel;
		double[] seedpos = parent.Userframe.get(index).fixedpos;
		Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(seedid, seedpos);

		
	parent.IDALL.add(seedpair);
	
	}

	parent.ShowMser = true;

	parent.updatePreview(ValueChange.SHOWMSER);
	
	if (child!=null)
	child.DeterministicSimple();
	
	
}
}


