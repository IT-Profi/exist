/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-04,  Wolfgang M. Meier (wolfgang@exist-db.org)
 *  and others (see http://exist-db.org)
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 *  $Id$
 */
package org.exist.dom;

import org.exist.xquery.Constants;

/**
 * Used to track fulltext matches throughout the query.
 * 
 * {@link org.exist.storage.TextSearchEngine} will add a
 * match object to every {@link org.exist.dom.NodeProxy}
 * that triggered a fulltext match for every term matched. The 
 * Match object contains the nodeId of the text node that triggered the
 * match, the string value of the matching term and a frequency count,
 * indicating the frequency of the matching term string within the corresponding
 * single text node.
 * 
 * All path operations copy existing match objects, i.e. the match objects
 * are copied to the selected descendant or child nodes. This means that
 * every NodeProxy being the direct or indirect result of a fulltext
 * selection will have one or more match objects, indicating which text nodes
 * among its descendant nodes contained a fulltext match.
 * 
 * @author wolf
 */
public class Match implements Comparable {
	
    public final static class Offset implements Comparable {
        private int offset;
        private int length;
        
        private Offset(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public void setOffset(int offset) {
            this.offset = offset;
        }
        
        public int getLength() {
            return length;
        }
        
        public int compareTo(Object other) {
            final int otherOffset = ((Offset) other).offset;
            return offset == otherOffset ? Constants.EQUAL : (offset < otherOffset ? Constants.INFERIOR : Constants.SUPERIOR);
        }
    }
    
	private long nodeId;
    private String matchTerm;
    
    private int[] offsets;
    private int[] lengths;
    
	private int currentOffset = 0;
    
	protected Match nextMatch = null;
	protected Match prevMatch = null;
	
    public Match(long nodeId, String matchTerm) {
        this( nodeId, matchTerm, 1);
    }
    
	public Match(long nodeId, String matchTerm, int frequency) {
		this.nodeId = nodeId;
        this.matchTerm = matchTerm;
		this.offsets = new int[frequency];
        this.lengths = new int[frequency];
	}
	
	public Match(Match match) {
		this.nodeId = match.nodeId;
        this.matchTerm = match.matchTerm;
		this.offsets = match.offsets;
        this.lengths = match.lengths;
        this.currentOffset = match.currentOffset;
	}
	
	public long getNodeId() {
		return nodeId;
	}
	
	public int getFrequency() {
		return currentOffset;
	}
	
    public void addOffset(int offset, int length) {
        if (currentOffset == offsets.length) {
            int noffsets[] = new int[currentOffset + 1];
            System.arraycopy(offsets, 0, noffsets, 0, currentOffset);
            offsets = noffsets;
            
            int nlengths[] = new int[currentOffset + 1];
            System.arraycopy(lengths, 0, nlengths, 0, currentOffset);
            lengths = nlengths;
        }
        offsets[currentOffset] = offset;
        lengths[currentOffset++] = length;
    }
    
    public Offset getOffset(int pos) {
        return new Offset(offsets[pos], lengths[pos]);
    }
    
	public Match getNextMatch() {
		return nextMatch;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof Match))
			return false;
        return ((Match)other).matchTerm.equals(matchTerm) &&
            ((Match)other).nodeId == nodeId;
	}
	
	/**
	 * Used to sort matches. Terms are compared by their string 
	 * length to have the longest string first.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		Match other = (Match)o;
        return matchTerm.compareTo(other.matchTerm);
	}
}
