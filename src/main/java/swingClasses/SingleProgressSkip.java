package swingClasses;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import ij.IJ;
import interactiveMT.Interactive_MTSingleChannel;
import interactiveMT.Interactive_MTSingleChannelBasic;
import mt.listeners.InteractiveRANSAC;
import updateListeners.FinalPoint;

public class SingleProgressSkip extends SwingWorker<Void, Void> {

final Interactive_MTSingleChannel parent;
final Interactive_MTSingleChannelBasic child;

	
	public SingleProgressSkip(final Interactive_MTSingleChannel parent, final Interactive_MTSingleChannelBasic child){
	
		this.parent = parent;
		this.child = child;
	}
	public SingleProgressSkip(final Interactive_MTSingleChannel parent){
		
		this.parent = parent;
		this.child = null;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {

		
		
       

		int next = parent.starttime;

		if (next < 2)
			next = 2;

		
		
		SingleTrack newtrack = new SingleTrack(parent);
		newtrack.Trackobject(next);
		

		return null;

	}

	@Override
	protected void done() {
		try {
			parent.jpb.setIndeterminate(false);
			get();
			parent.frame.dispose();
			
              if (child!=null){
				
				child.panelNext.removeAll();
				child.controlnext.removeAll();
				child.controlnext.add(new JButton(new AbstractAction("\u22b2Prev") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						CardLayout cl = (CardLayout) child.panelCont.getLayout();

						cl.previous(child.panelCont);
					}
				}));
				
				child.controlnext.add(new JButton(new AbstractAction("Enter RANSAC stage\u22b3") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						new InteractiveRANSAC().run(null);
						
					}
				}));

				child.panelNext.add(child.controlnext, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				child.controlnext.setVisible(true);

				child.panelSecond.add(child.panelNext, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			
				child.panelSecond.validate();
				
			}
              
              
              
              if (child == null){
  				parent.controlprevious.removeAll();
  				parent.panelPrevious.removeAll();
  				
  				parent.panelPrevious.add(new JButton(new AbstractAction("\u22b2Prev") {

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
  				
  				parent.controlprevious.add(new JButton(new AbstractAction("Enter RANSAC stage\u22b3") {

  					/**
  					 * 
  					 */
  					private static final long serialVersionUID = 1L;

  					@Override
  					public void actionPerformed(ActionEvent e) {
  						new InteractiveRANSAC().run(null);
  						
  					}
  				}));

  				parent.panelPrevious.add(parent.controlprevious,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
  						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
  			
  				
  				parent.panelFourth.add(parent.panelPrevious,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
  						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
  				parent.panelFourth.validate();
  				
  			}
              
			
			IJ.log("Tracking Done and track files written in the chosen folder");

		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
}
