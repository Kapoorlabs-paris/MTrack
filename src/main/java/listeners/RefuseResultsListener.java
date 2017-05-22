package listeners;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactiveMT.Interactive_MTDoubleChannel;

import trackerType.KFsearch;

public class RefuseResultsListener implements ActionListener  {

    final Interactive_MTDoubleChannel parent;
	
	
	public RefuseResultsListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		parent.redoAccept = true;

		parent.Allstart.clear();
		parent.Allend.clear();
		
		parent.lengthtimestart.clear();
		parent.lengthtimeend.clear();
		parent.deltad.clear();
		parent.deltadstart.clear();
		parent.deltadend.clear();
		parent.Accountedframes.clear();
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		parent.panelEighth.removeAll();
		final Label Step8 = new Label("Step 8", Label.CENTER);
		parent.panelEighth.setLayout(layout);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 4;
		c.weighty = 1.5;

		parent.panelEighth.add(Step8, c);

		if (parent.showDeterministic) {
			final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
			final Button SkipframeandTrackEndPoints = new Button(
					"TrackEndPoint (User specified first and last frame)");
			final Button CheckResults = new Button("Check Results (then click next)");

		//	++c.gridy;
		//	c.insets = new Insets(10, 10, 0, 175);
		//	parent.panelEighth.add(TrackEndPoints, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 10, 0);
			parent.panelEighth.add(parent.inputLabelX, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 10, 0);
			parent.panelEighth.add(parent.inputFieldX, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 10, 0);
			parent.panelEighth.add(parent.inputLabelY, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 10, 0);
			parent.panelEighth.add(parent.inputFieldY, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 175);
			parent.panelEighth.add(SkipframeandTrackEndPoints, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelEighth.add(CheckResults, c);

			TrackEndPoints.addActionListener(new TrackendsListener(parent, (int)Float.parseFloat(parent.inputFieldX.getText()), (int)Float.parseFloat(parent.inputFieldY.getText())));
			SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener(parent, (int)Float.parseFloat(parent.inputFieldX.getText()), (int)Float.parseFloat(parent.inputFieldY.getText())));
			CheckResults.addActionListener(new CheckResultsListener(parent));

			parent.panelEighth.validate();
			parent.panelEighth.repaint();

			parent.Cardframe.pack();
		}

	
	}
}
