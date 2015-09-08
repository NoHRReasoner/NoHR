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

public interface QueryResult {
	/**
	 * Gets the total number of this result.
	 *
	 * @return The total number of this result.
	 */
	public long getNum();

	/**
	 * Moves to the next element in the result set.
	 *
	 * @return False when reaching the end of the result set, otherwise true.
	 */
	public boolean next();
}