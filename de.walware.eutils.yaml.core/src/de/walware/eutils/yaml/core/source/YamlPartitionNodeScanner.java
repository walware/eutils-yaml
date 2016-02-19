/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.core.source;

import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.CScannerImpl;
import org.yaml.snakeyaml.tokens.Token;

import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScan;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScanner;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeType;


public class YamlPartitionNodeScanner implements ITreePartitionNodeScanner {
	
	
	private static class Pos {
		
		private final int beginOffset;
		private final int endOffset;
		
		public Pos(final int beginOffset, final int endOffset) {
			this.beginOffset= beginOffset;
			this.endOffset= endOffset;
		}
		
		public int getBeginOffset() {
			return this.beginOffset;
		}
		
		public int getEndOffset() {
			return this.endOffset;
		}
		
	}
	
	
	private final CScannerImpl scanner= new CScannerImpl(new StreamReader(""), false, false, false) { //$NON-NLS-1$
		
		@Override
		protected void handleComment(final int startIndex, final int endIndex) {
			YamlPartitionNodeScanner.this.comments.addLast(new Pos(startIndex, endIndex));
		}
		
	};
	
	private final Deque<Pos> comments= new ArrayDeque<>();
	
	private ITreePartitionNodeScan scan;
	
	/** The current node */
	private ITreePartitionNode node;
	/** The current node type */
	private YamlPartitionNodeType type;
	
	
	public YamlPartitionNodeScanner() {
	}
	
	
	@Override
	public int getRestartOffset(ITreePartitionNode node, final IDocument document, int offset)
			throws BadLocationException {
		final YamlPartitionNodeType rootType= getRootType();
		ITreePartitionNode parent= node.getParent();
		if (parent != null) {
			while (parent.getType() != rootType) {
				node= parent;
				parent= node.getParent();
			}
			
			// start at line start, but never inside a child
			int idx= parent.indexOfChild(node);
			while (true) {
				final int line= document.getLineOfOffset(node.getOffset());
				offset= document.getLineOffset(line);
				if (idx > 0) {
					node= parent.getChild(--idx);
					if (offset < node.getOffset() + node.getLength()) {
						continue;
					}
				}
				break;
			}
		}
		return offset;
	}
	
	@Override
	public YamlPartitionNodeType getRootType() {
		return YamlPartitionNodeType.DEFAULT_ROOT;
	}
	
	@Override
	public void execute(final ITreePartitionNodeScan scan) {
		this.scan= scan;
		
		setRange(scan.getBeginOffset(), scan.getEndOffset());
		
		this.node= null;
		this.comments.clear();
		
		init();
		
		process();
	}
	
	protected ITreePartitionNodeScan getScan() {
		return this.scan;
	}
	
	protected void setRange(final int beginOffset, final int endOffset) {
		try {
			final String s= getScan().getDocument().get(beginOffset, endOffset - beginOffset);
			this.scanner.reset(s, beginOffset);
		}
		catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void init() {
		final ITreePartitionNode beginNode= getScan().getBeginNode();
		if (beginNode.getType() instanceof YamlPartitionNodeType) {
			this.node= beginNode;
			this.type= (YamlPartitionNodeType) beginNode.getType();
		}
		else {
			this.node= beginNode;
			addNode(getRootType(), getScan().getBeginOffset());
		}
	}
	
	
	protected final void initNode(final ITreePartitionNode node, final YamlPartitionNodeType type) {
		if (this.node != null) {
			throw new IllegalStateException();
		}
		this.node= node;
		this.type= type;
	}
	
	protected final void addNode(final YamlPartitionNodeType type, final int offset) {
		checkComment(offset);
		this.node= this.scan.add(type, this.node, offset);
		this.type= type;
	}
	
	protected final ITreePartitionNode getNode() {
		return this.node;
	}
	
	protected final void exitNode(final int offset) {
		checkComment(offset);
		this.scan.expand(this.node, offset, true);
		this.node= this.node.getParent();
		this.type= (YamlPartitionNodeType) this.node.getType();
	}
	
	protected final boolean exitNode(final YamlPartitionNodeType type, final YamlPartitionNodeType typeAlt,
			final int offset) {
		int n= 1;
		ITreePartitionNode aNode= this.node;
		while (aNode != null) {
			final ITreePartitionNodeType aType= aNode.getType();
			if (aType == type || aType == typeAlt) {
				while (n > 0) {
					exitNode(offset);
					n--;
				}
				return true;
			}
			if (!(aType instanceof YamlPartitionNodeType)) {
				return false;
			}
			aNode= aNode.getParent();
			n++;
		}
		return false;
	}
	
//	protected final void exitNode() {
//		this.node= this.node.getParent();
//		this.type= (YamlPartitionNodeType) this.node.getType();
//	}
	
	protected final void exitToRoot(final int offset) {
		final YamlPartitionNodeType rootType= getRootType();
		while (this.type != rootType) {
			exitNode(offset);
		}
		checkComment(offset);
	}
	
	
	private void process() {
		boolean key= false;
		while (true) {
			final Token token= this.scanner.nextToken();
			if (token == null) {
				handleEOF(this.type);
				this.scan.expand(this.node, this.scan.getEndOffset(), true);
				return;
			}
			
			switch (token.getTokenId()) {
			case StreamStart:
			case StreamEnd:
			case DocumentStart:
			case DocumentEnd:
				exitToRoot(token.getStartMark().getIndex());
				continue;
			case Directive:
				addNode(YamlPartitionNodeType.DIRECTIVE, token.getStartMark().getIndex());
				exitNode(token.getEndMark().getIndex());
				continue;
			case BlockMappingStart:
				addNode(YamlPartitionNodeType.BLOCK_MAPPING, token.getStartMark().getIndex());
				continue;
			case BlockSequenceStart:
				addNode(YamlPartitionNodeType.BLOCK_SEQUENCE, token.getStartMark().getIndex());
				continue;
			case BlockEntry:
				continue;
			case BlockEnd:
				exitNode(YamlPartitionNodeType.BLOCK_MAPPING, YamlPartitionNodeType.BLOCK_SEQUENCE,
						token.getEndMark().getIndex() );
				continue;
			case FlowMappingStart:
				addNode(YamlPartitionNodeType.FLOAT_MAPPING, token.getStartMark().getIndex());
				continue;
			case FlowSequenceStart:
				addNode(YamlPartitionNodeType.FLOAT_SEQUENCE, token.getStartMark().getIndex());
				continue;
			case FlowEntry:
				continue;
			case FlowMappingEnd:
				exitNode(YamlPartitionNodeType.FLOAT_MAPPING, null,
						token.getEndMark().getIndex() );
				continue;
			case FlowSequenceEnd:
				exitNode(YamlPartitionNodeType.FLOAT_SEQUENCE, null,
						token.getEndMark().getIndex() );
				continue;
			case Key:
				key= true;
//				addNode(YamlPartitionNodeType.KEY, token.getStartMark().getIndex());
//				if (checkToken(Token.ID.Scalar)) {
//					token= nextToken();
//				}
//				exitNode(token.getEndMark().getIndex());
				continue;
			case Tag:
				addNode(YamlPartitionNodeType.TAG, token.getStartMark().getIndex());
				exitNode(token.getEndMark().getIndex());
				continue;
			case Anchor:
			case Alias:
				continue;
			case Value:
				key= false;
//				addNode(YamlPartitionNodeType.VALUE, token.getStartMark().getIndex());
//				if (checkToken(Token.ID.Scalar)) {
//					token= nextToken();
//				}
//				exitNode(token.getEndMark().getIndex());
				continue;
			case Scalar:
				addNode((key) ? YamlPartitionNodeType.KEY : YamlPartitionNodeType.VALUE,
						token.getStartMark().getIndex() );
				exitNode(token.getEndMark().getIndex());
				continue;
			}
		}
	}
	
	private void checkComment(final int offset) {
		while (!this.comments.isEmpty()) {
			final Pos pos= this.comments.getFirst();
			if (pos.getBeginOffset() >= offset) {
				break;
			}
			this.comments.removeFirst();
			this.scan.add(YamlPartitionNodeType.COMMENT_LINE, this.node,
					pos.getBeginOffset(), pos.getEndOffset() - pos.getBeginOffset() );
		}
	}
	
	protected void handleEOF(final YamlPartitionNodeType type) {
	}
	
}
