package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_PSFAnalyze;
import mpicbg.imglib.multithreading.SimpleMultiThreading;

public class Unstability_ScoreListener implements AdjustmentListener {
	final Label label;
	final float min, max;
	final int scrollbarSize;
	Interactive_MTDoubleChannel parent;
	final Scrollbar Unstability_ScoreScrollbar;

	public Unstability_ScoreListener(final Interactive_MTDoubleChannel parent, final Label label, final float min, final float max, final int scrollbarSize,
			final Scrollbar Unstability_ScoreScrollbar) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.parent = parent;
		this.scrollbarSize = scrollbarSize;
		this.Unstability_ScoreScrollbar = Unstability_ScoreScrollbar;
		if (parent.FindLinesViaHOUGH){
			
			Unstability_ScoreScrollbar.addMouseListener( new StandardMouseListener( parent,ValueChange.SHOWHOUGH ) );
			Unstability_ScoreScrollbar.addMouseListener( new StandardMouseListener( parent,ValueChange.SHOWMSERinHough ) );
				
			}
		else if (parent.FindLinesViaMSER || parent.FindLinesViaMSERwHOUGH)
		Unstability_ScoreScrollbar.addMouseListener( new StandardMouseListener( parent, ValueChange.SHOWMSER ) );
		else{
			
			Unstability_ScoreScrollbar.addMouseListener( new StandardMouseListener( parent,ValueChange.SHOWHOUGH ) );
			Unstability_ScoreScrollbar.addMouseListener( new StandardMouseListener( parent,ValueChange.SHOWMSERinHough ) );
			
		}
	}
	
	
	

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		parent.Unstability_Score = (parent.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize));

		Unstability_ScoreScrollbar.setValue(parent.computeScrollbarPositionFromValue((float) parent.Unstability_Score, min, max, scrollbarSize));

		label.setText("Unstability Score = " + parent.Unstability_Score);

		
	}
}
