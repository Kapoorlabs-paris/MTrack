package comboListeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.DecimalFormat;

import javax.swing.JScrollBar;

import mt.listeners.InteractiveRANSAC;
import mt.listeners.StandardMouseListener;


public class MaxDistListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveRANSAC parent;
	final float min;
	final int scrollbarSize;

	float max;
	final JScrollBar deltaScrollbar;

	public MaxDistListener(final InteractiveRANSAC parent, final Label label, final String string, final float min, float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
	
		this.scrollbarSize = scrollbarSize;

		deltaScrollbar.addMouseListener( new StandardMouseListener( parent ) );
		this.deltaScrollbar = deltaScrollbar;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		max =  parent.MAX_Gap;
		
		
		parent.maxDist = (int) Math.round(utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize));

	
		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.maxDist, min, max, scrollbarSize));

		label.setText(string +  " = "  + parent.df.format(parent.maxDist) + "      ");
		if(e.getValueIsAdjusting())
		parent.maxGapField.setText(Float.toString((parent.maxDist)));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
	
		
	}
	
}


