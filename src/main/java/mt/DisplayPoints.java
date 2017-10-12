/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package mt;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class DisplayPoints {
	public static JFrame display( final JFreeChart chart ) { return display( chart, new Dimension( 800, 500 ) ); }
	public static JFrame display( final JFreeChart chart, final Dimension d )
	{
		final JPanel panel = new JPanel();
		final ChartPanel chartPanel = new ChartPanel(
				chart,
				d.width - 10,
				d.height - 35,
				ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH,
				ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT,
				ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH,
				ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT,
				ChartPanel.DEFAULT_BUFFER_USED,
				true,  // properties
				true,  // save
				true,  // print
				true,  // zoom
				true   // tooltips
				);
		panel.add( chartPanel );

		final JFrame frame = new JFrame();
		frame.setContentPane( panel );
		frame.validate();
		frame.setSize( d );

		frame.setVisible( true );
		return frame;
	}
}
