package listeners;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;

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

			parent.controlnext.removeAll();
			parent.controlnext.add(new JButton(new AbstractAction("\u22b2Prev") {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					CardLayout cl = (CardLayout) parent.panelCont.getLayout();

					cl.previous(parent.panelCont);
				}
			}));
		 
			parent.controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					CardLayout cl = (CardLayout) parent.panelCont.getLayout();
					cl.next(parent.panelCont);
				}
			}));

			parent.panelNext.add(parent.controlnext,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			parent.controlnext.setVisible(true);
			
			parent.panelThird.add(parent.panelNext,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			parent.panelThird.validate();

		}

	}

}
