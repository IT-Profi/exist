/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-04 The eXist Team
 *
 *  http://exist-db.org
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  
 *  $Id$
 */
package org.exist.storage;

import java.util.Arrays;

import org.exist.xquery.value.Type;

public abstract class RangeIndexSpec {

	/*
	 * Constants to define the type of the index.
	 * These constants are used to encode the
	 * index type in the storage pointer.
	 */
	
	/** No index specified **/
	public static final int NO_INDEX = 0;
	public static final int STRING = 1;
	public static final int INTEGER = 2;
	public static final int DOUBLE = 3;
	public static final int FLOAT = 4;
	public static final int BOOLEAN = 5;
	public static final int MIXED_CONTENT = 0x40;
	public static final int TEXT = 0x80;
	public static final int RANGE_INDEX_MASK = 0x3F;
	
	private static final int[] xpathTypes = {
            Type.ITEM,
            Type.STRING,
            Type.INTEGER,
            Type.DOUBLE,
            Type.FLOAT,
            Type.BOOLEAN
	};
	
	protected static final int[] indexTypes = new int[64];
	static {
        Arrays.fill(indexTypes, NO_INDEX);
        indexTypes[Type.STRING] = STRING;
        indexTypes[Type.INTEGER] = INTEGER;
        indexTypes[Type.DOUBLE] = DOUBLE;
        indexTypes[Type.FLOAT] = FLOAT;
        indexTypes[Type.BOOLEAN] = BOOLEAN;
    }

	/**
	 * For a given index type bit, return the corresponding
	 * atomic XPath type (as defined in {@link org.exist.xquery.value.Type}).
	 * 
	 * @param type a bit set indicating the type
	 * @return
	 */
	public static final int indexTypeToXPath(int type) {
	    return xpathTypes[type & RANGE_INDEX_MASK];
	}

	/**
	 * Returns true if the index type specifier has the fulltext index flag
	 * set.
	 * 
	 * @param type a bit set indicating the type
	 * @return
	 */
	public static final boolean hasFulltextIndex(int type) {
	    return (type & TEXT) != 0;
	}

	/**
	 * Returns true if the index type specifier has the mixed content
	 * flag set.
	 * 
	 * @param type a bit set indicating the type
	 * @return
	 */
	public static final boolean hasMixedContent(int type) {
	    return (type & MIXED_CONTENT) != 0;
	}

	/**
	 * Returns the index type bit mask corresponding to a given
	 * XPath type (as defined in {@link org.exist.xquery.value.Type}).
	 * 
	 * @param type
	 * @return
	 */
	public static final int xpathTypeToIndex(int type) {
	    return indexTypes[type];
	}

	/**
	 * Returns true if the index type bit mask has a range index
	 * bit set.
	 * 
	 * @param type a bit set indicating the type
	 * @return
	 */
	public static final boolean hasRangeIndex(int type) {
		return (type & RANGE_INDEX_MASK) > 0;
	}

	protected int type;

	protected RangeIndexSpec() {
	}
	
	/**
	 * Returns the XPath type code for this index
	 * (as defined in {@link org.exist.xquery.value.Type}).
	 * 
	 * @return
	 */
	public int getType() {
	    return type;
	}

	/**
	 * Returns the index type for this index, corresponding
	 * to the constants defined in this class.
	 * 
	 * @return
	 */
	public int getIndexType() {
	    return indexTypes[type];
	}

}
