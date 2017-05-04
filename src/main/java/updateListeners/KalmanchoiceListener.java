package updateListeners;

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
import listeners.AcceptResultsListener;
import listeners.CheckResultsListener;
import listeners.MaxSearchradiusListener;
import listeners.MissedFrameListener;
import listeners.SearchradiusListener;
import listeners.SkipFramesandTrackendsListener;
import listeners.TrackendsListener;
import trackerType.KFsearch;

public class KalmanchoiceListener implements ItemListener {

	
final Interactive_MTDoubleChannel parent;
	
	
	public KalmanchoiceListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.showKalman = false;

		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.showKalman = true;
			Kalman();
		}

	}
	
	public void Kalman() {

		parent.showKalman = true;
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
		final Scrollbar rad = new Scrollbar(Scrollbar.HORIZONTAL, parent.initialSearchradiusInit, 10, 0, 10 + parent.scrollbarSize);
		parent.initialSearchradius = parent.computeValueFromScrollbarPosition(parent.initialSearchradiusInit, parent.initialSearchradiusMin,
				parent.initialSearchradiusMax, parent.scrollbarSize);

		final Label SearchText = new Label("Initial Search Radius: " + parent.initialSearchradius, Label.CENTER);

		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelFifth.add(SearchText, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelFifth.add(rad, c);

		final Scrollbar Maxrad = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxSearchradiusInit, 10, 0, 10 + parent.scrollbarSize);
		parent.maxSearchradius = parent.computeValueFromScrollbarPosition(parent.maxSearchradiusInit, parent.maxSearchradiusMin, parent.maxSearchradiusMax,
				parent.scrollbarSize);
		final Label MaxMovText = new Label("Max Movment of Objects per frame: " + parent.maxSearchradius, Label.CENTER);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelFifth.add(MaxMovText, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelFifth.add(Maxrad, c);

		final Scrollbar Miss = new Scrollbar(Scrollbar.HORIZONTAL, parent.missedframesInit, 10, 0, 10 + parent.scrollbarSize);
		Miss.setBlockIncrement(1);
		parent.missedframes = (int) parent.computeValueFromScrollbarPosition(parent.missedframesInit, parent.missedframesMin, parent.missedframesMax,
				parent.scrollbarSize);
		final Label LostText = new Label("Objects allowed to be lost for #frames" + parent.missedframes, Label.CENTER);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelFifth.add(LostText, c);
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 50);
		parent.panelFifth.add(Miss, c);

		// final Checkbox Costfunc = new Checkbox("Squared Distance Cost
		// Function");
		// ++c.gridy;
		// c.insets = new Insets(10, 10, 0, 50);
		// parent.panelFifth.add(Costfunc, c);

		rad.addAdjustmentListener(new SearchradiusListener(parent, SearchText, parent.initialSearchradiusMin, parent.initialSearchradiusMax));
		Maxrad.addAdjustmentListener(new MaxSearchradiusListener(parent,MaxMovText, parent.maxSearchradiusMin, parent.maxSearchradiusMax));
		Miss.addAdjustmentListener(new MissedFrameListener(parent, LostText, parent.missedframesMin, parent.missedframesMax));

		// Costfunc.addItemListener(new CostfunctionListener());

		parent.MTtrackerstart = new KFsearch(parent.AllstartKalman, parent.UserchosenCostFunction, parent.maxSearchradius, parent.initialSearchradius,
				parent.thirdDimension, parent.thirdDimensionSize, parent.missedframes);

		parent.MTtrackerend = new KFsearch(parent.AllendKalman, parent.UserchosenCostFunction, parent.maxSearchradius, parent.initialSearchradius,
				parent.thirdDimension, parent.thirdDimensionSize, parent.missedframes);

		final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
		final Button SkipframeandTrackEndPoints = new Button("TrackEndPoint (User specified first and last frame)");
		final Button CheckResults = new Button("Check Results (then click next)");
		final Checkbox RoughResults = new Checkbox("Analyze Rates");
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
		c.insets = new Insets(10, 175, 0, 175);
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
