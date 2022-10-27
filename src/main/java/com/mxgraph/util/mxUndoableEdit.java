/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
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
 * Copyright (c) 2007-2010, Gaudenz Alder, David Benson
 */
package com.mxgraph.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a 2-dimensional rectangle with double precision coordinates.
 */
public class mxUndoableEdit
{

	/**
	 * Defines the requirements for an undoable change.
	 */
	public interface mxUndoableChange
	{

		/**
		 * Undoes or redoes the change depending on its undo state.
		 */
		void execute();

	}

	/**
	 * Holds the source of the undoable edit.
	 */
	protected Object source;

	/**
	 * Holds the list of changes that make up this undoable edit.
	 */
	protected List<mxUndoableChange> changes = new ArrayList<mxUndoableChange>();

	/**
	 * Specifies this undoable edit is significant. Default is true.
	 */
	protected boolean significant = true;

	/**
	 * Specifies the state of the undoable edit.
	 */
	protected boolean undone, redone;

	/**
	 * Constructs a new undoable edit for the given source.
	 */
	public mxUndoableEdit(Object source)
	{
		this(source, true);
	}

	/**
	 * Constructs a new undoable edit for the given source.
	 */
	public mxUndoableEdit(Object source, boolean significant)
	{
		this.source = source;
		this.significant = significant;
	}

	/**
	 * Hook to notify any listeners of the changes after an undo or redo
	 * has been carried out. This implementation is empty.
	 */
	public void dispatch()
	{
		// empty
	}

	/**
	 * Hook to free resources after the edit has been removed from the command
	 * history. This implementation is empty.
	 */
	public void die()
	{
		// empty
	}

	/**
	 * @return the source
	 */
	public Object getSource()
	{
		return source;
	}

	/**
	 * @return the changes
	 */
	public List<mxUndoableChange> getChanges()
	{
		return changes;
	}

	/**
	 * @return the significant
	 */
	public boolean isSignificant()
	{
		return significant;
	}

	/**
	 * @return the undone
	 */
	public boolean isUndone()
	{
		return undone;
	}

	/**
	 * @return the redone
	 */
	public boolean isRedone()
	{
		return redone;
	}

	/**
	 * Returns true if the this edit contains no changes.
	 */
	public boolean isEmpty()
	{
		return changes.isEmpty();
	}

	/**
	 * Adds the specified change to this edit. The change is an object that is
	 * expected to either have an undo and redo, or an execute function.
	 */
	public void add(mxUndoableChange change)
	{
		changes.add(change);
	}

	/**
	 * 
	 */
	public void undo()
	{
		if (!undone)
		{
			int count = changes.size();

			for (int i = count - 1; i >= 0; i--)
			{
				mxUndoableChange change = changes.get(i);
				change.execute();
			}

			undone = true;
			redone = false;
		}

		dispatch();
	}

	/**
	 * 
	 */
	public void redo()
	{
		if (!redone)
		{
			int count = changes.size();

			for (int i = 0; i < count; i++)
			{
				mxUndoableChange change = changes.get(i);
				change.execute();
			}

			undone = false;
			redone = true;
		}

		dispatch();
	}

}
