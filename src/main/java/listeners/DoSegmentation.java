package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import updateListeners.FinalPoint;
import updateListeners.UpdateHoughListener;

public class DoSegmentation implements ItemListener {

	
final Interactive_MTDoubleChannel parent;
	
	
	public DoSegmentation(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.DESELECTED){
			parent.FindLinesViaHOUGH = false;
		}

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.FindLinesViaHOUGH = true;
			parent.doSegmentation = true;
			
			parent.thirdDimension = parent.starttime;
			FinalPoint finalpoint = new FinalPoint(parent);
			finalpoint.FinalizeEnds();

			if (parent.thirdDimension > parent.thirdDimensionSize) {
				IJ.log("Max frame number exceeded, moving to last frame instead");
				parent.thirdDimension = parent.thirdDimensionSize;
				parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension,
						parent.thirdDimensionSize);
				parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg,
						parent.thirdDimension, parent.thirdDimensionSize);
			} else {

				parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension,
						parent.thirdDimensionSize);
				parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg,
						parent.thirdDimension, parent.thirdDimensionSize);
			}
			parent.updatePreview(ValueChange.THIRDDIM);
			parent.UpdateHough();

			parent.controlnext.setVisible(true);

		}

	}

}
