/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.core.source;

import de.walware.ecommons.text.BasicHeuristicTokenScanner;
import de.walware.ecommons.text.core.sections.IDocContentSections;


public class YamlHeuristicTokenScanner extends BasicHeuristicTokenScanner {
	
	
	public static final int CURLY_BRACKET_TYPE= 0;
	public static final int SQUARE_BRACKET_TYPE= 1;
	
	
	public static int getBracketType(final char c) {
		switch (c) {
		case '{':
		case '}':
			return CURLY_BRACKET_TYPE;
		case '[':
		case ']':
			return SQUARE_BRACKET_TYPE;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public static YamlHeuristicTokenScanner create(final IDocContentSections documentContentInfo) {
		return (documentContentInfo.getPrimaryType() == IYamlDocumentConstants.YAML_PARTITIONING) ?
				new YamlHeuristicTokenScanner(documentContentInfo) :
				new YamlChunkHeuristicTokenScanner(documentContentInfo);
	}
	
	
	private class BracketBalanceCondition extends PartitionBasedCondition {
		
		private int type;
		private boolean open;
		
		@Override
		protected boolean matchesChar() {
			switch (YamlHeuristicTokenScanner.this.fChar) {
			case '{':
				this.type= CURLY_BRACKET_TYPE;
				this.open= true;
				return true;
			case '}':
				this.type= CURLY_BRACKET_TYPE;
				this.open= false;
				return true;
			case '[':
				this.type= SQUARE_BRACKET_TYPE;
				this.open= true;
				return true;
			case ']':
				this.type= SQUARE_BRACKET_TYPE;
				this.open= false;
				return true;
			}
			return false;
		}
		
	};
	
	
	protected YamlHeuristicTokenScanner(final IDocContentSections documentContentInfo) {
		super(documentContentInfo, IYamlDocumentConstants.YAML_DEFAULT_CONTENT_CONSTRAINT);
	}
	
	
	/**
	 * Computes bracket balance
	 * 
	 * @param backwardOffset searching backward before this offset
	 * @param forwardOffset searching forward after (including) this offset
	 * @param initial initial balance (e.g. known or not yet inserted between backward and forward offset)
	 * @param searchType interesting bracket type
	 * @return
	 * @see #getBracketType(char)
	 */
	public int[] computeBracketBalance(int backwardOffset, int forwardOffset, final int[] initial, final int searchType) {
		final int[] compute= new int[3];
		final BracketBalanceCondition condition= new BracketBalanceCondition();
		int breakType= -1;
		ITER_BACKWARD : while (--backwardOffset >= 0) {
			backwardOffset= scanBackward(backwardOffset, -1, condition);
			if (backwardOffset != NOT_FOUND) {
				if (condition.open) {
					compute[condition.type]++;
					if (condition.type != searchType && compute[condition.type] > 0) {
						breakType= condition.type;
						break ITER_BACKWARD;
					}
				}
				else {
					compute[condition.type]--;
				}
			}
			else {
				break ITER_BACKWARD;
			}
		}
		final int bound= this.fDocument.getLength();
		for (int i= 0; i < compute.length; i++) {
			if (compute[i] < 0) {
				compute[i]= 0;
			}
			compute[i]= compute[i]+initial[i];
		}
		ITER_FORWARD : while (forwardOffset < bound) {
			forwardOffset= scanForward(forwardOffset, bound, condition);
			if (forwardOffset != NOT_FOUND) {
				if (condition.open) {
					compute[condition.type]++;
				}
				else {
					compute[condition.type]--;
				}
				if (breakType >= 0 && compute[breakType] == 0) {
					break ITER_FORWARD;
				}
				forwardOffset++;
			}
			else {
				break ITER_FORWARD;
			}
		}
		return compute;
	}
	
}
