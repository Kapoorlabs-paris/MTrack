package singleListeners;

import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import interactiveMT.Interactive_MTSingleChannel;
import listeners.StarttimeListener;
import updateListeners.BatchModeListener;
import updateListeners.DefaultModelHF;
import updateListeners.FinalPoint;
import updateListeners.SingleDefaultModelHF;

public class SingleThirdPanel {

	
	final  Interactive_MTSingleChannel parent;
	
	
	
	public SingleThirdPanel(final  Interactive_MTSingleChannel parent){
		
		this.parent = parent;
		
	}
	
	
	private JPanel Deselect = new JPanel();
	private JPanel Timeselect = new JPanel();
	private JPanel Segselect = new JPanel();
	public void Paint(){
		
		
		// Panel Third
		
		
		
		Deselect.setLayout(parent.layout);
		Timeselect.setLayout(parent.layout);
		Segselect.setLayout(parent.layout);
		
		final JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, parent.thirdDimensionsliderInit, 10, 0, 10 + parent.scrollbarSize);
		
		final JScrollBar starttimeslider = new JScrollBar(Scrollbar.HORIZONTAL, parent.starttime, 10, 0, 10 + parent.scrollbarSize);
		
		final JScrollBar endtimeslider = new JScrollBar(Scrollbar.HORIZONTAL, parent.endtime, 10, 0, 10 + parent.scrollbarSize);
	
		parent.thirdDimension = parent.computeScrollbarPositionFromValue(parent.thirdDimensionsliderInit, parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize);
		
		
		
		
		final String timestring = "Time point for choosing ends ";
		final String starttimestring = "Start time for tracking ";
		final String endtimestring = "End time for tracking ";
		final Label timeText = new Label("Current time point = " + parent.thirdDimension , Label.CENTER);
		final Label timeTextstart = new Label("Start time for tracking = " + parent.starttime, Label.CENTER);
		final Label timeTextend = new Label("End time for tracking = " + parent.endtime, Label.CENTER);
		
		
		
		
		Border selectborder = new CompoundBorder(new TitledBorder("Option 1"),
				new EmptyBorder(parent.c.insets));
		
		Border timeborder = new CompoundBorder(new TitledBorder("Option 2"),
				new EmptyBorder(parent.c.insets));
		
		Border batchborder = new CompoundBorder(new TitledBorder("Option 3"),
				new EmptyBorder(parent.c.insets));
		
		
		
		final Label LeftClick = new Label(
				"Left click deselects an end");
		final Label SLeftClick = new Label( "Shift +  left click reselects a deselected end");
		final Label SALeftClick = new Label( "Shift + Alt + left click marks a user defined seed.");
		
		
		
		String[] SegMethod = {  "Do MSER based segmentation", "Do Watershed +  MSER based segmentation " };
		JComboBox<String> ChooseMethod = new JComboBox<String>(SegMethod);
		
		
		
		
		
		Deselect.add(LeftClick,new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Deselect.add(SLeftClick,new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Deselect.add(SALeftClick,new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		
		Deselect.add(timeText,new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Deselect.add(timeslider,new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Deselect.setBorder(selectborder);
		
		parent.panelThird.add(Deselect, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		

		
		
		Timeselect.add(timeTextstart,new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Timeselect.add(starttimeslider,new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Timeselect.add(timeTextend,new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Timeselect.add(endtimeslider,new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
	
		Timeselect.setBorder(timeborder);

		parent.panelThird.add(Timeselect, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		
		
		
		Segselect.add(ChooseMethod, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0) );
		
		
		
		Segselect.setBorder(batchborder);
		
		parent.panelThird.add(Segselect, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				
				
				// Load default optimizer options 
				SingleDefaultModelHF loaddefaultHF = new SingleDefaultModelHF(parent);
				loaddefaultHF.LoadDefault();

			

				parent.Cardframe.addWindowListener(new FrameListener(parent.Cardframe));

				timeslider.addAdjustmentListener(new SingleTimeListener(parent, timeText,timestring, parent.thirdDimensionsliderInit , parent.thirdDimensionSize, parent.scrollbarSize,timeslider));
				starttimeslider.addAdjustmentListener(new SingleStarttimeListener(parent, timeTextstart, starttimestring, parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize,starttimeslider));
				endtimeslider.addAdjustmentListener(new SingleEndtimeListener(parent, timeTextend, endtimestring, parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize,endtimeslider));
			
				ChooseMethod.addActionListener(new SingleSegMethodListener(parent, ChooseMethod));
				
			
				// Previous button for the third panel
				
				parent.controlprevious.add(new JButton(new AbstractAction("\u22b2Prev") {

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
				
				parent.panelPrevious.add(parent.controlprevious,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
				parent.controlprevious.setVisible(true);
				
				parent.panelThird.add(parent.panelPrevious,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			 
				

				parent.panelThird.validate();
				
				
				parent.Cardframe.pack();
	
		
	}
	
	

	
	protected class FinishedButtonListener implements ActionListener {
		final Frame parentB;
		final boolean cancel;

		public FinishedButtonListener(Frame parentB, final boolean cancel) {
			this.parentB = parentB;
			this.cancel = cancel;
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			parent.wasCanceled = cancel;
			parent.close(parentB, parent.sliceObserver, parent.roiListener);
		}
	}

	protected class FrameListener extends WindowAdapter {
		final Frame parentB;

		public FrameListener(Frame parentB) {
			super();
			this.parentB = parentB;
		}

		@Override
		public void windowClosing(WindowEvent e) {
			parent.close(parentB, parent.sliceObserver, parent.preprocessedimp, parent.roiListener);
		}
	}

	
}
