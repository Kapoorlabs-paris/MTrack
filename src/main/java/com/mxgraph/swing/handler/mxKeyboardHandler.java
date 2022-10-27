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
 * Copyright (c) 2008, Gaudenz Alder
 */
package com.mxgraph.swing.handler;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;

/**
 * @author Administrator
 * 
 */
public class mxKeyboardHandler
{

	/**
	 * 
	 * @param graphComponent
	 */
	public mxKeyboardHandler(mxGraphComponent graphComponent)
	{
		installKeyboardActions(graphComponent);
	}

	/**
	 * Invoked as part from the boilerplate install block.
	 */
	protected void installKeyboardActions(mxGraphComponent graphComponent)
	{
		InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		SwingUtilities.replaceUIInputMap(graphComponent,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);

		inputMap = getInputMap(JComponent.WHEN_FOCUSED);
		SwingUtilities.replaceUIInputMap(graphComponent,
				JComponent.WHEN_FOCUSED, inputMap);
		SwingUtilities.replaceUIActionMap(graphComponent, createActionMap());
	}

	/**
	 * Return JTree's input map.
	 */
	protected InputMap getInputMap(int condition)
	{
		InputMap map = null;

		if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		{
			map = (InputMap) UIManager.get("ScrollPane.ancestorInputMap");
		}
		else if (condition == JComponent.WHEN_FOCUSED)
		{
			map = new InputMap();

			map.put(KeyStroke.getKeyStroke("F2"), "edit");
			map.put(KeyStroke.getKeyStroke("DELETE"), "delete");
			map.put(KeyStroke.getKeyStroke("UP"), "selectParent");
			map.put(KeyStroke.getKeyStroke("DOWN"), "selectChild");
			map.put(KeyStroke.getKeyStroke("RIGHT"), "selectNext");
			map.put(KeyStroke.getKeyStroke("LEFT"), "selectPrevious");
			map.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "enterGroup");
			map.put(KeyStroke.getKeyStroke("PAGE_UP"), "exitGroup");
			map.put(KeyStroke.getKeyStroke("HOME"), "home");
			map.put(KeyStroke.getKeyStroke("ENTER"), "expand");
			map.put(KeyStroke.getKeyStroke("BACK_SPACE"), "collapse");
			map.put(KeyStroke.getKeyStroke("control A"), "selectAll");
			map.put(KeyStroke.getKeyStroke("control D"), "selectNone");
			map.put(KeyStroke.getKeyStroke("control X"), "cut");
			map.put(KeyStroke.getKeyStroke("CUT"), "cut");
			map.put(KeyStroke.getKeyStroke("control C"), "copy");
			map.put(KeyStroke.getKeyStroke("COPY"), "copy");
			map.put(KeyStroke.getKeyStroke("control V"), "paste");
			map.put(KeyStroke.getKeyStroke("PASTE"), "paste");
			map.put(KeyStroke.getKeyStroke("control G"), "group");
			map.put(KeyStroke.getKeyStroke("control U"), "ungroup");
			map.put(KeyStroke.getKeyStroke("control ADD"), "zoomIn");
			map.put(KeyStroke.getKeyStroke("control SUBTRACT"), "zoomOut");
		}

		return map;
	}

	/**
	 * Return the mapping between JTree's input map and JGraph's actions.
	 */
	protected ActionMap createActionMap()
	{
		ActionMap map = (ActionMap) UIManager.get("ScrollPane.actionMap");

		map.put("edit", mxGraphActions.getEditAction());
		map.put("delete", mxGraphActions.getDeleteAction());
		map.put("home", mxGraphActions.getHomeAction());
		map.put("enterGroup", mxGraphActions.getEnterGroupAction());
		map.put("exitGroup", mxGraphActions.getExitGroupAction());
		map.put("collapse", mxGraphActions.getCollapseAction());
		map.put("expand", mxGraphActions.getExpandAction());
		map.put("toBack", mxGraphActions.getToBackAction());
		map.put("toFront", mxGraphActions.getToFrontAction());
		map.put("selectNone", mxGraphActions.getSelectNoneAction());
		map.put("selectAll", mxGraphActions.getSelectAllAction());
		map.put("selectNext", mxGraphActions.getSelectNextAction());
		map.put("selectPrevious", mxGraphActions.getSelectPreviousAction());
		map.put("selectParent", mxGraphActions.getSelectParentAction());
		map.put("selectChild", mxGraphActions.getSelectChildAction());
		map.put("cut", TransferHandler.getCutAction());
		map.put("copy", TransferHandler.getCopyAction());
		map.put("paste", TransferHandler.getPasteAction());
		map.put("group", mxGraphActions.getGroupAction());
		map.put("ungroup", mxGraphActions.getUngroupAction());
		map.put("zoomIn", mxGraphActions.getZoomInAction());
		map.put("zoomOut", mxGraphActions.getZoomOutAction());

		return map;
	}

}
