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

package de.walware.eutils.yaml.ui.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import de.walware.ecommons.text.core.input.DocumentParserInput;
import de.walware.ecommons.text.ui.settings.TextStyleManager;


public class YamlDefaultTextStyleScanner extends DocumentParserInput implements ITokenScanner {
	
	
	private static final byte ST_EOF= 0;
	private static final byte ST_DOCUMENT_SEPARATOR= 1;
	private static final byte ST_OTHER_INDICATOR= 2;
	private static final byte ST_BRACKET_INDICATOR= 3;
	
	private static final byte LAST_OTHER= 0;
	private static final byte LAST_EOF= 1;
	private static final byte LAST_EOL= 2;
	
	
	private final TextStyleManager textStyles;
	private final IToken[] tokens;
	private final IToken defaultToken;
	
	private int currentOffset;
	private int currentLength;
	
	private byte nextType;
	private int nextLength;
	
	private byte lastChar;
	
	
	public YamlDefaultTextStyleScanner(final TextStyleManager textStyles) {
		this.textStyles= textStyles;
		
		this.defaultToken= getToken(IYamlTextStyles.TS_DEFAULT);
		this.tokens= new IToken[4];
		this.tokens[ST_EOF]= Token.EOF;
		this.tokens[ST_DOCUMENT_SEPARATOR]= getToken(IYamlTextStyles.TS_DOCUMENT_SEPARATOR);
		this.tokens[ST_OTHER_INDICATOR]= getToken(IYamlTextStyles.TS_INDICATOR);
		this.tokens[ST_BRACKET_INDICATOR]= getToken(IYamlTextStyles.TS_BRACKET);
	}
	
	
	protected IToken getToken(final String key) {
		return this.textStyles.getToken(key);
	}
	
	
	@Override
	public void setRange(final IDocument document, final int offset, final int length) {
		reset(document);
		init(offset, offset + length);
		
		this.currentOffset= offset;
		this.currentLength= 0;
		this.nextLength= -1;
		
		try {
			if (offset > 0) {
				switch (document.getChar(offset - 1)) {
				case '\r':
				case '\n':
					this.lastChar= LAST_EOL;
					break;
				default:
					this.lastChar= LAST_OTHER;
				}
			}
			else {
				this.lastChar= LAST_EOL;
			}
		}
		catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public IToken nextToken() {
		this.currentOffset += this.currentLength;
		this.currentLength= this.nextLength;
		if (this.currentLength >= 0) {
			this.nextLength= -1;
			return this.tokens[this.nextType];
		}
		readNext();
		if (this.currentLength > 0) {
			return this.defaultToken;
		}
		else {
			this.currentLength= this.nextLength;
			this.nextLength= -1;
			return this.tokens[this.nextType];
		}
	}
	
	@Override
	public int getTokenOffset() {
		return this.currentOffset;
	}
	
	@Override
	public int getTokenLength() {
		return this.currentLength;
	}
	
	
	private void readNext() {
		int offset= 0;
		boolean nl= (this.lastChar == LAST_EOL);
		for (; true; offset++) {
			final int c= get(offset);
			switch (c) {
			case EOF:
				newToken(offset, ST_EOF, 0);
				this.lastChar= LAST_EOF;
				return;
			case '\n':
			case '\r':
				nl= true;
				continue;
			case '-':
				if (nl) {
					if (matches(1, '-', '-')) {
						newToken(offset, ST_DOCUMENT_SEPARATOR, 3);
						this.lastChar= LAST_OTHER;
						return;
					}
				}
				newToken(offset, ST_OTHER_INDICATOR, 1);
				this.lastChar= LAST_OTHER;
				return;
			case '?':
			case ':':
				newToken(offset, ST_OTHER_INDICATOR, 1);
				this.lastChar= LAST_OTHER;
				return;
			case '.':
				if (nl) {
					if (matches(1, '.', '.')) {
						int length= 3;
						while (get(length) == '.') {
							length++;
						}
						newToken(offset, ST_DOCUMENT_SEPARATOR, length);
						this.lastChar= LAST_OTHER;
						return;
					}
					nl= false;
				}
				continue;
			case '[':
			case ']':
			case '{':
			case '}':
				newToken(offset, ST_BRACKET_INDICATOR, 1);
				this.lastChar= LAST_OTHER;
				return;
//			case '&':
//			case '*':
//				TODO anchor
			default:
				nl= false;
				continue;
			}
		}
	}
	
	private void newToken(final int offset, final byte type, final int length) {
		this.currentLength= getLengthInSource(offset);
		consume(offset);
		
		this.nextType= type;
		this.nextLength= getLengthInSource(length);
		consume(length);
	}
	
}
