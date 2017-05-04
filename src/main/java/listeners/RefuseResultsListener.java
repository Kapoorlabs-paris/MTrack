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
		parent.AllstartKalman.clear();
		parent.AllendKalman.clear();
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

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 175);
			parent.panelEighth.add(TrackEndPoints, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 175);
			parent.panelEighth.add(SkipframeandTrackEndPoints, c);

			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelEighth.add(CheckResults, c);

			TrackEndPoints.addActionListener(new TrackendsListener(parent));
			SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener(parent));
			CheckResults.addActionListener(new CheckResultsListener(parent));

			parent.panelEighth.validate();
			parent.panelEighth.repaint();

			parent.Cardframe.pack();
		}

		if (parent.showKalman) {

			final Scrollbar rad = new Scrollbar(Scrollbar.HORIZONTAL, parent.initialSearchradiusInit, 10, 0,
					10 + parent.scrollbarSize);
			parent.initialSearchradius = parent.computeValueFromScrollbarPosition(parent.initialSearchradiusInit, parent.initialSearchradiusMin,
					parent.initialSearchradiusMax, parent.scrollbarSize);

			final Label SearchText = new Label("Initial Search Radius: " + parent.initialSearchradius, Label.CENTER);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(SearchText, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(rad, c);

			final Scrollbar Maxrad = new Scrollbar(Scrollbar.HORIZONTAL, parent.maxSearchradiusInit, 10, 0,
					10 + parent.scrollbarSize);
			parent.maxSearchradius = parent.computeValueFromScrollbarPosition(parent.maxSearchradiusInit, parent.maxSearchradiusMin,
					parent.maxSearchradiusMax, parent.scrollbarSize);
			final Label MaxMovText = new Label("Max Movment of Objects per frame: " + parent.maxSearchradius,
					Label.CENTER);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(MaxMovText, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(Maxrad, c);

			final Scrollbar Miss = new Scrollbar(Scrollbar.HORIZONTAL, parent.missedframesInit, 10, 0, 10 + parent.scrollbarSize);
			Miss.setBlockIncrement(1);
			parent.missedframes = (int) parent.computeValueFromScrollbarPosition(parent.missedframesInit, parent.missedframesMin,
					parent.missedframesMax, parent.scrollbarSize);
			final Label LostText = new Label("Objects allowed to be lost for #frames" + parent.missedframes, Label.CENTER);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(LostText, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(Miss, c);

			final Checkbox Costfunc = new Checkbox("Squared Distance Cost Function");
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(Costfunc, c);

			rad.addAdjustmentListener(
					new SearchradiusListener(parent, SearchText, parent.initialSearchradiusMin, parent.initialSearchradiusMax));
			Maxrad.addAdjustmentListener(
					new MaxSearchradiusListener(parent, MaxMovText, parent.maxSearchradiusMin, parent.maxSearchradiusMax));
			Miss.addAdjustmentListener(new MissedFrameListener(parent, LostText, parent.missedframesMin, parent.missedframesMax));

			// Costfunc.addItemListener(new CostfunctionListener());

			parent.MTtrackerstart = new KFsearch(parent.AllstartKalman, parent.UserchosenCostFunction, parent.maxSearchradius,
					parent.initialSearchradius, parent.thirdDimension, parent.thirdDimensionSize, parent.missedframes);

			parent.MTtrackerend = new KFsearch(parent.AllendKalman, parent.UserchosenCostFunction, parent.maxSearchradius, parent.initialSearchradius,
					parent.thirdDimension, parent.thirdDimensionSize, parent.missedframes);

			final Button TrackEndPoints = new Button("Track EndPoints (From first to a chosen last frame)");
			final Button SkipframeandTrackEndPoints = new Button(
					"TrackEndPoint (User specified first and last frame)");
			final Button CheckResults = new Button("Check Results (then click next)");
			final Checkbox AcceptResults = new Checkbox("Accept Results");
			final Button RefuseResults = new Button("Refuse Results");
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 175);
			parent.panelEighth.add(TrackEndPoints, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 175);
			parent.panelEighth.add(SkipframeandTrackEndPoints, c);
			++c.gridy;
			c.insets = new Insets(10, 175, 0, 175);
			parent.panelEighth.add(CheckResults, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 200);
			parent.panelEighth.add(AcceptResults, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 200);
			parent.panelEighth.add(RefuseResults, c);

			TrackEndPoints.addActionListener(new TrackendsListener(parent));
			SkipframeandTrackEndPoints.addActionListener(new SkipFramesandTrackendsListener(parent));
			CheckResults.addActionListener(new CheckResultsListener(parent));
			AcceptResults.addItemListener(new AcceptResultsListener(parent));

			RefuseResults.addActionListener(new RefuseResultsListener(parent));

			parent.panelEighth.validate();
			parent.panelEighth.repaint();
			parent.Cardframe.pack();

		}
	}
}
