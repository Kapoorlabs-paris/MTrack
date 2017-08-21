package listeners;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import interactiveMT.Interactive_MTDoubleChannel;



public class CheckResultsListener implements ActionListener {

	 final Interactive_MTDoubleChannel parent;
		
		
		public CheckResultsListener(final Interactive_MTDoubleChannel parent){
		
			this.parent = parent;
		}
		
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		if (parent.redo) {
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
			JLabel warning = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));

			final Label RedotextA = new Label("MTtracker redo = ", Label.CENTER);
			JTextArea textArea = new JTextArea(
					"On average the result was " + (float) parent.netdeltad
							+ "(pixels) away from the Kymo (your cutoff was " + parent.deltadcutoff + "(pixels) ) "
							+ " When MT is very small the optimizer identifies the nearest bright spot as part of the line, adding a wrong length for that frame "
							+ " this causes the wrong number to be added over all frames causing a deviation from Kymo (which does not affects the rates) "
							+ " If However the mistake was in Kymograph, then ignore this step and go next to compute rates.",
					6, 20);
			textArea.setFont(new Font("Serif", Font.PLAIN, 16));
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setOpaque(false);
			textArea.setEditable(false);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 200);
			parent.panelEighth.add(warning, c);

			++c.gridy;
			c.insets = new Insets(10, 10, 0, 200);
			parent.panelEighth.add(textArea, c);

			final Checkbox AcceptResults = new Checkbox("Accept Results");
			final Button RefuseResults = new Button("Refuse Results, do over");
			++c.gridy;
			c.insets = new Insets(10, 175, 0, 200);
			parent.panelEighth.add(AcceptResults, c);
			++c.gridy;
			c.insets = new Insets(10, 175, 0, 200);
			parent.panelEighth.add(RefuseResults, c);

			AcceptResults.addItemListener(new AcceptResultsListener(parent));
			RefuseResults.addActionListener(new RefuseResultsListener(parent));

			RedotextA.setBackground(new Color(1, 0, 1));
			RedotextA.setForeground(new Color(255, 255, 255));
			parent.panelEighth.validate();
			parent.panelEighth.repaint();

		}

		else {
			final GridBagLayout layout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			parent.panelEighth.removeAll();

			parent.panelEighth.setLayout(layout);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 4;
			c.weighty = 1.5;

			final Label SuccessA = new Label(" Congratulations: that is quite close to the Kymograph, + ",
					Label.CENTER);

			final Label SuccessB = new Label(" Now you can compute rates, choose start and end frame: ",
					Label.CENTER);

			final Label Done = new Label("The results have been compiled and stored, you can now exit",
					Label.CENTER);

			final Button Analyze = new Button("Do Rough Analysis");

			final JScrollBar startS = new JScrollBar(Scrollbar.HORIZONTAL, parent.thirdDimensionsliderInit, 10, 0,
					10 + parent.scrollbarSize);
			final JScrollBar endS = new JScrollBar(Scrollbar.HORIZONTAL, parent.thirdDimensionsliderInit, 10, 0,
					10 + parent.scrollbarSize);
			parent.starttime = (int) parent.computeValueFromScrollbarPosition(parent.thirdDimensionsliderInit, 0, parent.thirdDimensionSize,
					parent.scrollbarSize);
			parent.endtime = (int) parent.computeValueFromScrollbarPosition(parent.thirdDimensionsliderInit, 0, parent.thirdDimensionSize,
					parent.scrollbarSize);
			final Label startText = new Label("startFrame = ", Label.CENTER);
			final Label endText = new Label("endFrame = ", Label.CENTER);

			SuccessA.setBackground(new Color(1, 0, 1));
			SuccessA.setForeground(new Color(255, 255, 255));

			SuccessB.setBackground(new Color(1, 0, 1));
			SuccessB.setForeground(new Color(255, 255, 255));
			if (parent.analyzekymo && parent.Kymoimg != null) {
				++c.gridy;
				c.insets = new Insets(10, 10, 0, 50);
				parent.panelEighth.add(SuccessA, c);
			}
			++c.gridy;
			c.insets = new Insets(10, 10, 0, 50);
			parent.panelEighth.add(SuccessB, c);

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

			startS.addAdjustmentListener(new StarttimeListener(parent, startText, startText.getName(), parent.thirdDimensionsliderInit,
					parent.thirdDimensionSize, parent.scrollbarSize, startS));
			endS.addAdjustmentListener(new EndtimeListener(parent, endText, endText.getName(), parent.thirdDimensionsliderInit, parent.thirdDimensionSize,
					parent.scrollbarSize, endS));
			Analyze.addActionListener(new AnalyzeListener(parent));

			parent.panelEighth.validate();
			parent.panelEighth.repaint();
			parent.Cardframe.pack();
		}

	}

}
