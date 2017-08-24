
package listeners;

import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import updateListeners.DefaultModel;
import updateListeners.FinalPoint;


public class SegMethodListener implements ActionListener {

	
	
	final Interactive_MTDoubleChannel parent;
	final JComboBox<String> choice;
	
	public SegMethodListener(final Interactive_MTDoubleChannel parent, final JComboBox<String> choice){
		
		this.parent = parent;
		this.choice = choice;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedindex = choice.getSelectedIndex();
		
		parent.controlnext.setVisible(true);
		
          if (selectedindex == 0){
			
			
			parent.FindLinesViaMSER = true;
			parent.doMserSegmentation = true;
			
			
			FinalPoint finalpoint = new FinalPoint(parent);
			finalpoint.FinalizeEnds();
			
			parent.thirdDimension = parent.starttime;

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
			parent.UpdateMser();


			

		}
		
		
		if (selectedindex == 1){
			
			
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

			
			
		}
		
		
		
		parent.controlprevious.removeAll();
		parent.controlnext.removeAll();
		parent.controlnext.add(new JButton(new AbstractAction("\u22b2Prev") {

			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) parent.panelCont.getLayout();

				cl.previous(parent.panelCont);
			}
		}));
	 
		parent.controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

			
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
		
		parent.panelThird.add(parent.panelNext,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	
		parent.panelThird.validate();
		parent.Cardframe.pack();
		
		
		
		
		
		
	}

}
