package updateListeners;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTDoubleChannel;
import listeners.AcceptResultsListener;
import listeners.CheckResultsListener;
import listeners.SkipFramesandTrackendsListener;
import listeners.TrackendsListener;

public class DeterchoiceListener implements ItemListener {
	
final Interactive_MTDoubleChannel parent;
	
	
	public DeterchoiceListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.showDeterministic = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			Deterministic();
		}

	}
	
	public void Deterministic() {

		parent.showDeterministic = true;
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;

		parent.panelFifth.removeAll();

		final Label Step5 = new Label("Step 5", Label.CENTER);
		parent.panelFifth.setLayout(layout);
		parent.panelFifth.add(Step5, c);
		final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
		final Button SkipframeandTrackEndPoints = new Button("TrackEndPoint (User specified first and last frame)");
		final Button CheckResults = new Button("Check Results (then click next)");
		final Checkbox RoughResults = new Checkbox("Rates and Statistical Analysis");

		final Label Checkres = new Label("The tracker now performs an internal check on the results");
		Checkres.setBackground(new Color(1, 0, 1));
		Checkres.setForeground(new Color(255, 255, 255));
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		parent.panelFifth.add(TrackEndPoints, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		parent.panelFifth.add(SkipframeandTrackEndPoints, c);
		if (parent.analyzekymo && parent.Kymoimg != null) {
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 0);
			parent.panelFifth.add(Checkres, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelFifth.add(CheckResults, c);
		}
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 175);
		parent.panelFifth.add(RoughResults, c);

		TrackEndPoints.addActionListener(new TrackendsListener(parent));
		SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener(parent));
		CheckResults.addActionListener(new CheckResultsListener(parent));
		RoughResults.addItemListener(new AcceptResultsListener(parent));
		parent.panelFifth.repaint();
		parent.panelFifth.validate();
		parent.Cardframe.pack();

	}
	
	
	
}
