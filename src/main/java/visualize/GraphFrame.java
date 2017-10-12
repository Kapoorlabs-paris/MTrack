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
package visualize;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import ij.gui.GUI;

/**
 * @author Stephan Preibisch
 */
public class GraphFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;

	JFreeChart chart = null;

	ChartPanel chartPanel = null;
	JPanel mainPanel;

	public GraphFrame( final JFreeChart chart  )
	{
		super();

		mainPanel = new JPanel();
		mainPanel.setLayout( new BorderLayout() );

		updateWithNewChart( chart, true );

		JPanel buttonsPanel = new JPanel();
		mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

		setContentPane( mainPanel );
		validate();
		GUI.center( this );
	}

	synchronized public void updateWithNewChart( JFreeChart c, boolean setSize )
	{
		if ( chartPanel != null )
			remove( chartPanel );
		chartPanel = null;
		this.chart = c;
		chartPanel = new ChartPanel( c );

		chartPanel.setMouseWheelEnabled( true );
		chartPanel.setHorizontalAxisTrace( true );
		mainPanel.add( chartPanel, BorderLayout.CENTER );
		
		/*
		// add extra items
		final JPopupMenu menu = chartPanel.getPopupMenu();

		if ( extraMenuItems != null )
			for ( final FileOpenMenuEntry m : extraMenuItems )
			{
				m.setChartPanel( chartPanel );
				menu.add( new JMenuItem( m ) );
			}
		*/

		//menu.get
		validate();
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
	}
}
