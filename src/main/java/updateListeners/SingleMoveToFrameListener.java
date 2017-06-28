package updateListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;

import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;

public class SingleMoveToFrameListener implements ActionListener {
	
final Interactive_MTSingleChannel parent;
	
	
	public SingleMoveToFrameListener(final Interactive_MTSingleChannel parent){
	
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
		SingleMarkends newends = new SingleMarkends(parent);
		newends.markend();

		

	}

}
