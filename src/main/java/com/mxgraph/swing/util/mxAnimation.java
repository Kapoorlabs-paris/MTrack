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
/**
 * Copyright (c) 2007, Gaudenz Alder
 */
package com.mxgraph.swing.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

/**
 * Baseclass for all timer-based animations. Fires mxEvent.DONE when the
 * stopAnimation method is called. Implement updateAnimation for the
 * actual animation or listen to mxEvent.EXECUTE.
 */
public class mxAnimation extends mxEventSource
{
	/**
	 * Specifies the default delay for animations in ms. Default is 20.
	 */
	public static int DEFAULT_DELAY = 20;

	/**
	 * Default is DEFAULT_DELAY.
	 */
	protected int delay;

	/**
	 * Time instance that is used for timing the animation.
	 */
	protected Timer timer;

	/**
	 * Constructs a new animation instance with the given repaint delay.
	 */
	public mxAnimation()
	{
		this(DEFAULT_DELAY);
	}

	/**
	 * Constructs a new animation instance with the given repaint delay.
	 */
	public mxAnimation(int delay)
	{
		this.delay = delay;
	}

	/**
	 * Returns the delay for the animation.
	 */
	public int getDelay()
	{
		return delay;
	}

	/**
	 * Sets the delay for the animation.
	 */
	public void setDelay(int value)
	{
		delay = value;
	}
	
	/**
	 * Returns true if the animation is running.
	 */
	public boolean isRunning()
	{
		return timer != null;
	}

	/**
	 * Starts the animation by repeatedly invoking updateAnimation.
	 */
	public void startAnimation()
	{
		if (timer == null)
		{
			timer = new Timer(delay, new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					updateAnimation();
				}

			});

			timer.start();
		}
	}

	/**
	 * Hook for subclassers to implement the animation. Invoke stopAnimation
	 * when finished, startAnimation to resume. This is called whenever the
	 * timer fires and fires an mxEvent.EXECUTE event with no properties.
	 */
	public void updateAnimation()
	{
		fireEvent(new mxEventObject(mxEvent.EXECUTE));
	}

	/**
	 * Stops the animation by deleting the timer and fires mxEvent.DONE.
	 */
	public void stopAnimation()
	{
		if (timer != null)
		{
			timer.stop();
			timer = null;
			fireEvent(new mxEventObject(mxEvent.DONE));
		}
	}

}
