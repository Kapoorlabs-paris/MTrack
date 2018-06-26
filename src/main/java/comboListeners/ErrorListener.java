package comboListeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.DecimalFormat;

import javax.swing.JScrollBar;

import mt.listeners.InteractiveRANSAC;
import mt.listeners.StandardMouseListener;


public class ErrorListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveRANSAC parent;
	final float min;
	final int scrollbarSize;

	float max;
	final JScrollBar deltaScrollbar;

	public ErrorListener(final InteractiveRANSAC parent, final Label label, final String string, final float min, float max,
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
		
		max =  parent.MAX_ERROR;
		
		
		parent.maxError = utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);

	
		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.maxError, min, max, scrollbarSize));

		label.setText(string +  " = "  + parent.df.format(parent.maxError) + "      ");
		if(e.getValueIsAdjusting())
		parent.maxErrorField.setText(parent.df.format(parent.maxError));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
	
		
	}
	
}


