/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.ui.editors;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;

import de.walware.ecommons.text.TextUtil;
import de.walware.ecommons.text.ui.BracketLevel;


public class YamlBracketLevel extends BracketLevel {
	
	
	public static final class SquareBracketPosition extends InBracketPosition {
		
		public SquareBracketPosition(final IDocument document, final int offset, final int length,
				final int sequence) {
			super(document, offset, length, sequence);
		}
		
		@Override
		public char getOpenChar() {
			return '[';
		}
		
		@Override
		public char getCloseChar() {
			return ']';
		}
		
	}
	
	public static final class CurlyBracketPosition extends InBracketPosition {
		
		public CurlyBracketPosition(final IDocument document, final int offset, final int length,
				final int sequence) {
			super(document, offset, length, sequence);
		}
		
		@Override
		public char getOpenChar() {
			return '{';
		}
		
		@Override
		public char getCloseChar() {
			return '}';
		}
		
	}
	
	public final static class QuotedDPosition extends InBracketPosition {
		
		public QuotedDPosition(final IDocument doc, final int offset, final int length,
				final int sequence) {
			super(doc, offset, length, sequence);
		}
		
		@Override
		public char getOpenChar() {
			return '"';
		}
		
		@Override
		public char getCloseChar() {
			return '"';
		}
		
		@Override
		public boolean insertCR(final int charOffset) {
			return false;
		}
		
		@Override
		protected boolean isEscaped(final int offset) throws BadLocationException {
			return (TextUtil.countBackward(getDocument(), offset, '\\') % 2 == 1);
		}
		
	}
	
	public final static class QuotedSPosition extends InBracketPosition {
		
		public QuotedSPosition(final IDocument doc, final int offset, final int length,
				final int sequence) {
			super(doc, offset, length, sequence);
		}
		
		@Override
		public char getOpenChar() {
			return '\'';
		}
		
		@Override
		public char getCloseChar() {
			return '\'';
		}
		
		@Override
		public boolean insertCR(final int charOffset) {
			return false;
		}
		
		@Override
		protected boolean isEscaped(final int offset) throws BadLocationException {
			final IDocument document= getDocument();
			return ((TextUtil.countBackward(document, offset, '\'') % 2 == 1)
					|| (offset + 1 < document.getLength() && document.getChar(offset + 1) == '\'') );
		}
		
	}
	
	
	public static InBracketPosition createPosition(final char c, final IDocument document,
			final int offset, final int length, final int sequence) {
		switch(c) {
		case '{':
			return new CurlyBracketPosition(document, offset, length, sequence);
		case '[':
			return new SquareBracketPosition(document, offset, length, sequence);
		case '"':
			return new QuotedDPosition(document, offset, length, sequence);
		case '\'':
			return new QuotedSPosition(document, offset, length, sequence);
		default:
			throw new IllegalArgumentException("Invalid position type: " + c); //$NON-NLS-1$
		}
	}
	
	
	public YamlBracketLevel(final IDocument doc, final String partitioning,
			final List<LinkedPosition> positions, final int mode) {
		super(doc, partitioning, positions, mode);
	}
	
}
