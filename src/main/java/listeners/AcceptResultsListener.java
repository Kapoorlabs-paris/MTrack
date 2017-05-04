package listeners;

import java.awt.Button;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import interactiveMT.Interactive_MTDoubleChannel;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;

public class AcceptResultsListener implements ItemListener {

	 final Interactive_MTDoubleChannel parent;
		
		
		public AcceptResultsListener(final Interactive_MTDoubleChannel parent){
		
			this.parent = parent;
		}
		
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.redoAccept = false;

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
			final Button Analyze = new Button("Do Rough Analysis");

			final Label Optional = new Label("Do rate analysis for MT of your choosing (optional)", Label.CENTER);
			Optional.setBackground(new Color(1, 0, 1));
			Optional.setForeground(new Color(255, 255, 255));

			final Scrollbar startS = new Scrollbar(Scrollbar.HORIZONTAL, parent.thirdDimensionsliderInit, 10, 0,
					10 + parent.scrollbarSize);
			final Scrollbar endS = new Scrollbar(Scrollbar.HORIZONTAL, parent.thirdDimensionsliderInit, 10, 0,
					10 + parent.scrollbarSize);
			parent.starttime = (int) parent.computeValueFromScrollbarPosition(parent.thirdDimensionsliderInit, 0, parent.thirdDimensionSize,
					parent.scrollbarSize);
			parent.endtime = (int) parent.computeValueFromScrollbarPosition(parent.thirdDimensionsliderInit, 0, parent.thirdDimensionSize,
					parent.scrollbarSize);
			final Label startText = new Label("startFrame = ", Label.CENTER);
			final Label endText = new Label("endFrame = ", Label.CENTER);
			final Label Done = new Label("Proceed to Statistical analysis", Label.CENTER);
			Done.setBackground(new Color(1, 0, 1));
			Done.setForeground(new Color(255, 255, 255));

			final Button Stats = new Button("Do Statistical analysis");
			JLabel lbl = new JLabel("Select the seedID of the MT for analysis");

			String[] choices = new String[parent.IDALL.size()];

			JLabel lbltrack = new JLabel("Select the seedID of the MT for displaying tracks");

			String[] choicestrack = new String[parent.IDALL.size() + 1];
			choicestrack[0] = "Display All";
			Comparator<Pair<Integer, double[]>> seedIDcomparison = new Comparator<Pair<Integer, double[]>>() {

				@Override
				public int compare(final Pair<Integer, double[]> A, final Pair<Integer, double[]> B) {

					return A.getA() - B.getA();

				}

			};

			Collections.sort(parent.IDALL, seedIDcomparison);
			for (int index = 0; index < parent.IDALL.size(); ++index) {

				String currentseed = Double.toString(parent.IDALL.get(index).getA());

				choices[index] = "Seed " + currentseed;
				choicestrack[index + 1] = "Seed " + currentseed;
			}

			JComboBox<String> cb = new JComboBox<String>(choices);

			JComboBox<String> cbtrack = new JComboBox<String>(choicestrack);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(Optional, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(lbl, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(cb, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(lbltrack, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(cbtrack, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(startText, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(startS, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(endText, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(endS, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(Analyze, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(Done, c);

			// ++c.gridy;
			// c.insets = new Insets(10, 10, 0, 50);
			// parent.panelEighth.add(Stats, c);

			startS.addAdjustmentListener(new StarttimeListener(parent, startText, parent.thirdDimensionsliderInit,
					parent.thirdDimensionSize, parent.scrollbarSize, startS));
			endS.addAdjustmentListener(new EndtimeListener(parent, endText, parent.thirdDimensionsliderInit, parent.thirdDimensionSize,
					parent.scrollbarSize, endS));
			Analyze.addActionListener(new AnalyzeListener(parent));
			Stats.addActionListener(new StatsAnalyzeListener(parent));
			cb.addActionListener(new SeedchoiceListener(cb, parent));

			cbtrack.addActionListener(new SeedDisplayListener(cbtrack, Views.hyperSlice(parent.originalimg, 2, 1),parent));
			parent.panelEighth.validate();
			parent.panelEighth.repaint();
			parent.Cardframe.pack();
			// Stat Analysis

			parent.panelNinth.removeAll();

			parent.panelNinth.setLayout(layout);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;
			final Label Step9 = new Label("Step 9", Label.CENTER);
			parent.panelNinth.add(Step9, c);
			final Button Nlength = new Button("Time averaged MT lengths");

			final Button NlengthMax = new Button("Get length distribution");

			parent.inputMaxdpixel = new JLabel("Enter maxLength of MT (pixel units): ");
			parent.Maxdpixel = new TextField();
			parent.Maxdpixel.setColumns(10);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelNinth.add(Nlength, c);
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelNinth.add(parent.inputMaxdpixel, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelNinth.add(parent.Maxdpixel, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelNinth.add(NlengthMax, c);
			++c.gridy;

			Nlength.addActionListener(new NlengthListener(parent));

			NlengthMax.addActionListener(new NlengthMaxListener(parent));
			parent.panelNinth.validate();
			parent.panelNinth.repaint();
			parent.Cardframe.pack();
		}
	}

}


