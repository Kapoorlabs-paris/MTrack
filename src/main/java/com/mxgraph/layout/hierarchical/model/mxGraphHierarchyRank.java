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
/*
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package com.mxgraph.layout.hierarchical.model;

import java.util.LinkedHashSet;

/**
 * An abstraction of a rank in the hierarchy layout. Should be ordered, perform
 * remove in constant time and contains in constant time
 */
public class mxGraphHierarchyRank extends LinkedHashSet<mxGraphAbstractHierarchyCell>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2781491210687143878L;
}
