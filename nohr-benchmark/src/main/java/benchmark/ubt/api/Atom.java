package benchmark.ubt.api;

/*
 * #%L
 * nohr-benchmark
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */


/**
 * by Yuanbo Guo Semantic Web and Agent Technology Lab, CSE Department, Lehigh University, USA Copyright (C) 2004 This program is free software; you
 * can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version
 * 2 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

public class Atom {
	/** the predicate */
	private final String predicate_;
	/** the arguments */
	private final String[] arguments_;

	/**
	 * Constructor.
	 * 
	 * @param predicate
	 *            Name of the predicate
	 * @param arguments
	 *            List of the arguments
	 */
	public Atom(String predicate, String[] arguments) {
		predicate_ = predicate;
		arguments_ = arguments;
	}

	/**
	 * Gets the arguments.
	 * 
	 * @return List of the arguments
	 */
	public String[] getArguments() {
		return arguments_;
	}

	/**
	 * Gets the predicate.
	 * 
	 * @return Name of the predicate
	 */
	public String getPredicate() {
		return predicate_;
	}
}