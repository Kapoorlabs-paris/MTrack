package listeners;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactiveMT.Interactive_MTDoubleChannel;

import net.imglib2.img.display.imagej.ImageJFunctions;
import trackerType.KFsearch;

public class AnalyzekymoListener implements ItemListener {
	
final Interactive_MTDoubleChannel parent;
	
	
	public AnalyzekymoListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	

	@Override
	public void itemStateChanged(ItemEvent arg0) {

		Kymo();

	}



public void Kymo() {

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
	if (parent.Kymoimg != null)
		parent.Kymoimp = ImageJFunctions.show(parent.Kymoimg);
	final Label Select = new Label(
			"Make Segmented Line selection (Generates a file containing time (row 1) and length (row 2))");
	final Button ExtractKymo = new Button("Extract Mask Co-ordinates :");
	Select.setBackground(new Color(1, 0, 1));
	Select.setForeground(new Color(255, 255, 255));

	final Label Checkres = new Label("The tracker now performs an internal check on the results");
	Checkres.setBackground(new Color(1, 0, 1));
	Checkres.setForeground(new Color(255, 255, 255));

	if (parent.analyzekymo) {

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		parent.panelFifth.add(Select, c);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 200);
		parent.panelFifth.add(ExtractKymo, c);

		ExtractKymo.addActionListener(new GetCords(parent));

	}

	if (parent.showDeterministic) {
		final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
		final Button SkipframeandTrackEndPoints = new Button("TrackEndPoint (User specified first and last frame)");
		final Button CheckResults = new Button("Check Results (then click next)");

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

		TrackEndPoints.addActionListener(new TrackendsListener(parent));
		SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener(parent));
		CheckResults.addActionListener(new CheckResultsListener(parent));

	}

	
	parent.panelFifth.repaint();
	parent.panelFifth.validate();
	parent.Cardframe.pack();
	
}
}
