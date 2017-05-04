package updateListeners;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import ij.IJ;
import ij.gui.ImageCanvas;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public  class MoveNextListener implements ActionListener {
	
	
final Interactive_MTDoubleChannel parent;
	
	
	public MoveNextListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max frame number exceeded, moving to last frame instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension, parent.thirdDimensionSize);
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg, parent.thirdDimension,
					parent.thirdDimensionSize);
		} else {

			parent.thirdDimension = parent.thirdDimension + 1;
			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension, parent.thirdDimensionSize);
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg, parent.thirdDimension,
					parent.thirdDimensionSize);

		}

		parent.updatePreview(ValueChange.THIRDDIM);

		
		Markends newends = new Markends(parent);
		newends.markend();
		if (parent.doSegmentation){
			
			
			UpdateHoughListener newhough = new UpdateHoughListener(parent);
			newhough.UpdateHough();
			
		}

		else{

			UpdateMserListener newmser = new UpdateMserListener(parent);
			newmser.UpdateMser();
			
		}

	}
	
	
	
}
