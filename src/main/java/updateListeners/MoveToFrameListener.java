package updateListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class MoveToFrameListener implements ActionListener {
	
final Interactive_MTDoubleChannel parent;
	
	
	public MoveToFrameListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.moveDialogue();

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max frame number exceeded, moving to last frame instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension, parent.thirdDimensionSize);
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg, parent.thirdDimension,
					parent.thirdDimensionSize);
		} else {

			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension, parent.thirdDimensionSize);
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg, parent.thirdDimension,
					parent.thirdDimensionSize);
		}

		parent.updatePreview(ValueChange.THIRDDIM);
		Markends newends = new Markends(parent);
		newends.markend();

		if (parent.doSegmentation){
			
			
			parent.UpdateHough();
		}
			

		else{

			
			parent.UpdateMser();
			
		}

	}

}
