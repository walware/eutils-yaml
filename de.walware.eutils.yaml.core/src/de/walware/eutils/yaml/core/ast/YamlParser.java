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

package de.walware.eutils.yaml.core.ast;

import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS1_SYNTAX_MISSING_INDICATOR;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS2_SYNTAX_CHAR_INVALID;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS2_SYNTAX_COLLECTION_NOT_CLOSED;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS2_SYNTAX_ESCAPE_INVALID;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS2_SYNTAX_TOKEN_NOT_CLOSED;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS2_SYNTAX_TOKEN_UNEXPECTED;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS2_SYNTAX_TOKEN_UNKNOWN;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS3_FLOW_MAP;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS3_FLOW_SEQ;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS3_MAP_KEY;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS3_MAP_VALUE;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS3_SEQ_ENTRY;

import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.CScannerConstants;
import org.yaml.snakeyaml.scanner.CScannerImpl;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;

import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.StatusDetail;

import de.walware.eutils.yaml.core.ast.YamlAst.NodeType;


public class YamlParser {
	
	
	private static class ProblemInfo {
		
		//private final byte context;
		private final Mark contextMark;
		private final byte problem;
		private final Mark problemMark;
		private final String problemText;
		
		public ProblemInfo(final byte context, final Mark contextMark,
				final byte problem, final Mark problemMark, final String problemText) {
			//this.context= context;
			this.contextMark= contextMark;
			this.problem= problem;
			this.problemMark= problemMark;
			this.problemText= problemText;
		}
		
	}
	
	
	private final CScannerImpl scanner= new CScannerImpl(new StreamReader(""), true, true, false) { //$NON-NLS-1$
		@Override
		protected void handleComment(final int startIndex, final int endIndex) {
		}
		@Override
		protected void handleSyntaxProblem(final byte context, final Mark contextMark,
				final byte problem, final Mark problemMark, final String problemText, final String arg2) {
			YamlParser.this.handleScannerProblem(new ProblemInfo(context, contextMark,
					problem, problemMark, problemText ));
		}
	};
	
	
	private YamlAstNode currentNode;
	
	private int depth;
	private final List<List<YamlAstNode>> childrenStack= new ArrayList<>();
	
	private final List<ProblemInfo> problemInfos= new ArrayList<>();
	
	
	public YamlParser() {
	}
	
	
	public void setScalarText(final boolean create) {
		this.scanner.setCreateScalarText(create);
	}
	
	public SourceComponent parse(final String text) {
		return parse(text, null, 0);
	}
	
	public SourceComponent parse(final String text, final IAstNode parent, final int offset) {
		try {
			this.depth= -1;
			this.scanner.reset(text, offset);
			
			final SourceComponent sourceComponent= new SourceComponent(parent, offset, text.length());
			enterNode(sourceComponent);
			
			processTokens();
			
			while (this.depth >= 0) {
				exit();
			}
			
			return sourceComponent;
		}
		finally {
			while (this.depth >= 0) {
				final List<YamlAstNode> list= this.childrenStack.get(this.depth);
				list.clear();
				this.depth--;
			}
			this.problemInfos.clear();
		}
	}
	
	
	private void addChildTerm(final YamlAstNode node) {
		addChild(node);
		
		checkExit();
	}
	
	private void addChild(final YamlAstNode node) {
		if (this.currentNode.getNodeType() == NodeType.MAP_ENTRY) {
			final Tuple entry= (Tuple) this.currentNode;
			if (entry.keyNode == null && entry.valueIndicatorOffset == Integer.MIN_VALUE) {
				entry.keyNode= node;
			}
			else {
				entry.valueNode= node;
			}
		}
		else {
			final List<YamlAstNode> children= this.childrenStack.get(this.depth);
			children.add(node);
			
			if (this.depth == 0) {
				clearProblems(node.getOffset());
			}
		}
	}
	
	private void finish(final int stopOffset) {
		switch (this.currentNode.getNodeType()) {
		case MAP_ENTRY: {
			final Tuple entry= (Tuple) this.currentNode;
			if (entry.keyNode == null) {
				entry.keyNode= new Dummy(0, entry, // empty node
						(entry.keyIndicatorOffset != Integer.MIN_VALUE) ?
								entry.keyIndicatorOffset : entry.beginOffset );
			}
			if (entry.status == 0 && entry.valueIndicatorOffset == Integer.MAX_VALUE) {
				entry.status= STATUS1_SYNTAX_MISSING_INDICATOR | STATUS3_MAP_VALUE;
			}
			if (entry.valueNode == null) {
				entry.valueNode= new Dummy(0, entry, // empty node
						(entry.valueIndicatorOffset != Integer.MIN_VALUE) ?
								entry.valueIndicatorOffset : entry.keyNode.endOffset );
			}
			final int min= entry.valueNode.endOffset;
			if (entry.endOffset < min) {
				entry.endOffset= min;
			}
			return;
		}
		case MAP: {
			final Collection collection= (Collection) this.currentNode;
			switch (this.currentNode.getOperator()) {
			case '[':
				if (collection.getCloseIndicatorOffset() == Integer.MIN_VALUE) {
					collection.status= STATUS2_SYNTAX_COLLECTION_NOT_CLOSED | STATUS3_FLOW_SEQ;
				}
				break;
			case '{':
				if (collection.getCloseIndicatorOffset() == Integer.MIN_VALUE) {
					collection.status= STATUS2_SYNTAX_COLLECTION_NOT_CLOSED | STATUS3_FLOW_MAP;
				}
				break;
			}
			break; // -> NContainer
		}
		default:
			break; // -> NContainer
		}
		
		{	final NContainer container= (NContainer) this.currentNode;
			if (stopOffset != Integer.MIN_VALUE) {
				container.endOffset= stopOffset;
			}
			
			final List<YamlAstNode> children= this.childrenStack.get(this.depth);
			if (!children.isEmpty()) {
				container.children= children.toArray(new YamlAstNode[children.size()]);
				children.clear();
				
				final int min= container.children[container.children.length - 1].getEndOffset();
				if (container.endOffset < min) {
					container.endOffset= min;
				}
			}
		}
	}
	
	private void enterNode(final NContainer node) {
		if (this.depth >= 0) {
			addChild(node);
		}
		
		this.depth++;
		this.currentNode= node;
		
		while (this.depth >= this.childrenStack.size()) {
			this.childrenStack.add(new ArrayList<YamlAstNode>());
		}
	}
	
	private void enterNode(final Tuple node) {
		addChild(node);
		
		this.depth++;
		this.currentNode= node;
	}
	
	private boolean exitTo(final Class<?> type1) {
		while (this.depth > 1) {
			if (this.currentNode.getClass() == type1) {
				return true;
			}
			exit();
		}
		return false;
	}
	
	private boolean exitTo(final Class<?> type1, final Class<?> type2) {
		while (this.depth > 1) {
			if (this.currentNode.getClass() == type1 || this.currentNode.getClass() == type2) {
				return true;
			}
			exit();
		}
		return false;
	}
	
	private boolean exitTo1(final Class<?> type1) {
		while (this.depth > 1) {
			if (this.currentNode.getClass() == type1) {
				return true;
			}
			if (this.currentNode.getNodeType() == NodeType.MAP_ENTRY) {
				exit();
			}
			break;
		}
		int checkDepth= this.depth - 1;
		YamlAstNode node= this.currentNode.getYamlParent();
		while (checkDepth > 1 && node != null) {
			if (node.getClass() == type1) {
				while (this.depth > checkDepth) {
					exit();
				}
				return true;
			}
			checkDepth--;
			node= node.getYamlParent();
		}
		return false;
	}
	
	private boolean exitTo1(final Class<?> type1, final Class<?> type2) {
		while (this.depth > 1) {
			if (this.currentNode.getClass() == type1 || this.currentNode.getClass() == type2) {
				return true;
			}
			if (this.currentNode.getNodeType() == NodeType.MAP_ENTRY) {
				exit();
			}
			break;
		}
		int checkDepth= this.depth - 1;
		YamlAstNode node= this.currentNode.getYamlParent();
		while (checkDepth > 1 && node != null) {
			if (node.getClass() == type1 || node.getClass() == type2) {
				while (this.depth > checkDepth) {
					exit();
				}
				return true;
			}
			checkDepth--;
			node= node.getYamlParent();
		}
		return false;
	}
	
	private boolean exitTo(final NodeType type1) {
		while (this.depth > 1) {
			if (this.currentNode.getNodeType() == type1) {
				return true;
			}
			exit();
		}
		return false;
	}
	
	private boolean exitTo(final NodeType type1, final NodeType type2) {
		while (this.depth > 1) {
			if (this.currentNode.getNodeType() == type1 || this.currentNode.getNodeType() == type2) {
				return true;
			}
			exit();
		}
		return false;
	}
	
	private void exitToSourceComponent(final int offset) {
		while (this.depth > 1) {
			exit();
		}
		if (this.depth > 0) {
			exit(offset);
		}
	}
	
	private void exit(final int offset) {
		finish(offset);
		this.currentNode= this.currentNode.yamlParent;
		this.depth--;
		
		checkExit();
	}
	
	private void exit() {
		finish(Integer.MIN_VALUE);
		this.currentNode= this.currentNode.yamlParent;
		this.depth--;
		
		checkExit();
	}
	
	private void checkExit() {
		if (this.depth > 0 && this.currentNode.getNodeType() == NodeType.MAP_ENTRY
				&& ((Tuple) this.currentNode).valueNode != null) {
			exit();
		}
	}
	
	
	private void processTokens() {
		while (true) {
			final Token token= this.scanner.nextToken();
			if (token == null) {
				break;
			}
			
			switch (token.getTokenId()) {
			case StreamStart:
			case StreamEnd:
				continue;
			
			case Directive: {
				exitToSourceComponent(token.getStartMark().getIndex());
				final Directive node= new Directive((SourceComponent) this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex() );
				addChildTerm(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			case DocumentStart: {
				exitToSourceComponent(token.getStartMark().getIndex());
				final Instruction.DocStart node= new Instruction.DocStart((SourceComponent) this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex() );
				addChildTerm(node);
				checkForProblem(token.getStartMark(), node);
				enterNode(new DocContent((SourceComponent) this.currentNode,
						token.getStartMark().getIndex() ));
				continue;
			}
			case DocumentEnd: {
				exitToSourceComponent(token.getStartMark().getIndex());
				final Instruction.DocEnd node= new Instruction.DocEnd((SourceComponent) this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex() );
				addChildTerm(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			
			case BlockSequenceStart: {
				final Collection.BlockSeq node= new Collection.BlockSeq(this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex() );
				enterNode(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			case BlockMappingStart: {
				final Collection.BlockMap node= new Collection.BlockMap(this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex() );
				enterNode(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			case BlockEnd: {
				final boolean found= exitTo(Collection.BlockSeq.class, Collection.BlockMap.class);
				if (found) {
					exit(token.getEndMark().getIndex());
				}
				continue;
			}
			case FlowSequenceStart: {
				final Collection.FlowCollection node= new Collection.FlowSeq(this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex() );
				enterNode(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			case FlowSequenceEnd: {
				final boolean found= exitTo(Collection.FlowSeq.class);
				final Collection.FlowSeq collection;
				if (found) {
					collection= (Collection.FlowSeq) this.currentNode;
					collection.closeIndicatorOffset= token.getStartMark().getIndex();
					exit(token.getEndMark().getIndex());
				}
				else {
					addChildTerm(new Dummy(STATUS2_SYNTAX_TOKEN_UNEXPECTED, this.currentNode,
							token.getStartMark().getIndex(), token.getEndMark().getIndex() ));
				}
				continue;
			}
			case FlowMappingStart: {
				final Collection.FlowCollection node= new Collection.FlowMap(this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex() );
				enterNode(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			case FlowMappingEnd: {
				final boolean found= exitTo(Collection.FlowMap.class);
				final Collection.FlowMap node;
				if (found) {
					node= (Collection.FlowMap) this.currentNode;
					node.closeIndicatorOffset= token.getStartMark().getIndex();
					exit(token.getEndMark().getIndex());
				}
				else {
					addChildTerm(new Dummy(STATUS2_SYNTAX_TOKEN_UNEXPECTED, this.currentNode,
							token.getStartMark().getIndex(), token.getEndMark().getIndex() ));
				}
				continue;
			}
			
			case BlockEntry: {
				final boolean found= exitTo1(Collection.BlockSeq.class);
				final Collection.BlockSeq node;
				if (found) {
					node= (Collection.BlockSeq) this.currentNode;
				}
				else {
					node= new Collection.BlockSeq(this.currentNode,
							token.getStartMark().getIndex(), token.getEndMark().getIndex() );
					enterNode(node);
					checkForProblem(token.getStartMark(), node);
				}
				continue;
			}
			case FlowEntry: {
				final boolean found= exitTo1(Collection.FlowSeq.class, Collection.FlowMap.class);
				if (!found) {
					addChildTerm(new Dummy(STATUS2_SYNTAX_TOKEN_UNEXPECTED | STATUS3_SEQ_ENTRY,
							this.currentNode,
							token.getStartMark().getIndex(), token.getEndMark().getIndex() ));
				}
				continue;
			}
			
			case Anchor: {
				final Label node= new Label.Anchor(this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex(),
						((AliasToken) token).getValue() );
				addChildTerm(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			case Alias: {
				final Label node= new Label.Reference(this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex(),
						((AliasToken) token).getValue() );
				addChildTerm(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			
			case Key: {
				final boolean found= exitTo(NodeType.MAP);
				final Tuple node= new Tuple(this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex() );
				if (!found) {
					node.status|= STATUS2_SYNTAX_TOKEN_UNEXPECTED | STATUS3_MAP_KEY;
				}
				enterNode(node);
				checkForProblem(token.getStartMark(), node);
				if (node.getLength() > 0) { // explicite
					node.keyIndicatorOffset= node.beginOffset;
				}
				continue;
			}
			case Value: {
				final boolean found= exitTo(NodeType.MAP_ENTRY, NodeType.MAP);
				final Tuple node;
				if (this.currentNode.getNodeType() == NodeType.MAP_ENTRY) {
					node= (Tuple) this.currentNode;
				}
				else {
					node= new Tuple(this.currentNode,
							token.getStartMark().getIndex(), token.getEndMark().getIndex() );
					if (!found) {
						node.status|= STATUS2_SYNTAX_TOKEN_UNEXPECTED | STATUS3_MAP_VALUE;
					}
					enterNode(node);
				}
				checkForProblem(token.getStartMark(), node);
				node.valueIndicatorOffset= token.getStartMark().getIndex();
				node.endOffset= token.getEndMark().getIndex();
				continue;
			}
			
			case Tag: {
				final TagTuple tagTuple= ((TagToken) token).getValue();
				final Tag node= new Tag(this.currentNode,
						token.getStartMark().getIndex(), token.getEndMark().getIndex(),
						tagTuple.getHandle(), tagTuple.getSuffix() );
				addChildTerm(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			
			case Scalar: {
				final ScalarToken scalarToken= (ScalarToken) token;
				final Scalar node;
				switch (scalarToken.getStyle()) {
				case '\"':
					node= new Scalar.DQuoted(this.currentNode,
							token.getStartMark().getIndex(), token.getEndMark().getIndex(),
							scalarToken.getValue() );
					break;
				case '\'':
					node= new Scalar.SQuoated(this.currentNode,
							token.getStartMark().getIndex(), token.getEndMark().getIndex(),
							scalarToken.getValue() );
					break;
				default:
					node= new Scalar.Plain(this.currentNode,
							token.getStartMark().getIndex(), token.getEndMark().getIndex(),
							scalarToken.getValue() );
					break;
				}
				addChildTerm(node);
				checkForProblem(token.getStartMark(), node);
				continue;
			}
			
			default:
				continue;
			}
		}
	}
	
	private void handleScannerProblem(final ProblemInfo p) {
		if (p.contextMark == null) {
			if (p.problemMark != null && p.problemText != null) {
				switch ((p.problemText.length() == 1) ? p.problemText.charAt(0) : 0) {
				case ':':
				case ',':
				case '%':
				case '-':
				case '.':
				case '?':
				case '|':
				case '>':
					addChildTerm(new Dummy(STATUS2_SYNTAX_TOKEN_UNEXPECTED, this.currentNode,
							p.problemMark.getIndex(), p.problemMark.getIndex() + p.problemText.length() ));
					break;
				default:
					addChildTerm(new Dummy(STATUS2_SYNTAX_TOKEN_UNKNOWN, this.currentNode,
							p.problemMark.getIndex(), p.problemMark.getIndex() + p.problemText.length() ));
				}
			}
		}
		else {
			keepProblem(p);
		}
	}
	
	private void keepProblem(final ProblemInfo p) {
		final int contextIndex= p.contextMark.getIndex();
		int idx= this.problemInfos.size();
		while (idx > 0) {
			if (this.problemInfos.get(idx - 1).contextMark.getIndex() <= contextIndex) {
				break;
			}
			idx--;
		}
		this.problemInfos.add(idx, p);
	}
	
	private void clearProblems(final int contextIndex) {
		int idx= 0;
		while (idx < this.problemInfos.size()) {
			if (this.problemInfos.get(idx).contextMark.getIndex() < contextIndex) {
				idx++;
			}
			else {
				break;
			}
		}
		if (idx > 1) {
			if (idx == 2) {
				this.problemInfos.remove(0);
			}
			else {
				this.problemInfos.subList(0, idx - 1).clear();
			}
		}
	}
	
	private void checkForProblem(final Mark startMark, final YamlAstNode node) {
		final int contextIndex= startMark.getIndex();
		for (int idx= 0; idx < this.problemInfos.size(); ) {
			final ProblemInfo p= this.problemInfos.get(idx);
			if (p.contextMark == startMark) {
				if (attachProblem(node, p)) {
					this.problemInfos.remove(idx);
					continue;
				}
			}
			if (p.contextMark.getIndex() > contextIndex) {
				break;
			}
			idx++;
		}
	}
	
	private boolean attachProblem(final YamlAstNode node, final ProblemInfo p) {
		if (node.getStatusCode() != 0) {
			return false;
		}
		switch (p.problem) {
		case CScannerConstants.UNEXPECTED_CHAR:
		case CScannerConstants.UNEXPECTED_CHAR_2:
			if (p.problemMark != null && p.problemMark.getIndex() >= node.getOffset()
					&& p.problemMark.getIndex() < node.getEndOffset()
					&& p.problemText != null) {
				node.status= STATUS2_SYNTAX_CHAR_INVALID;
				node.addAttachment(new StatusDetail(
						p.problemMark.getIndex(), p.problemText.length(),
						p.problemText ));
				return true;
			}
			break;
		case CScannerConstants.UNEXPECTED_ESCAPE_SEQUENCE:
			if (node.getNodeType() == NodeType.SCALAR) {
				node.status= STATUS2_SYNTAX_ESCAPE_INVALID;
				if (p.problemMark != null && p.problemText != null) {
					node.addAttachment(new StatusDetail(
							p.problemMark.getIndex() - 1, p.problemText.length() + 1,
							'\\' + p.problemText ));
				}
				return true;
			}
			break;
		case CScannerConstants.NOT_CLOSED:
			if (node.getNodeType() == NodeType.SCALAR) {
				node.status= STATUS2_SYNTAX_TOKEN_NOT_CLOSED;
				return true;
			}
			break;
		case CScannerConstants.MISSING_DIRECTIVE_NAME:
		case CScannerConstants.UNEXPECTED_CHAR_FOR_VERSION_NUMBER:
		case CScannerConstants.MISSING_URI:
		case CScannerConstants.MISSING_ANCHOR_NAME:
		case CScannerConstants.MISSING_MAP_COLON:
		case CScannerConstants.UNEXPECTED_BLOCK_SEQ_ENTRY:
			if (node.getClass() == Collection.BlockSeq.class) {
				node.status= STATUS2_SYNTAX_TOKEN_UNEXPECTED | STATUS3_SEQ_ENTRY;
				return true;
			}
			break;
		case CScannerConstants.UNEXPECTED_MAP_KEY:
			if (node.getNodeType() == NodeType.MAP_ENTRY) {
				node.status= STATUS2_SYNTAX_TOKEN_UNEXPECTED | STATUS3_MAP_KEY;
				return true;
			}
			if (node.getYamlParent() != null
					&& node.getYamlParent().getNodeType() == NodeType.MAP_ENTRY
					&& node.getYamlParent().getStatusCode() == 0) {
				node.getYamlParent().status= STATUS2_SYNTAX_TOKEN_UNEXPECTED | STATUS3_MAP_KEY;
				return true;
			}
			break;
		case CScannerConstants.UNEXPECTED_MAP_VALUE:
			if (node.getNodeType() == NodeType.MAP_ENTRY) {
				node.status= STATUS2_SYNTAX_TOKEN_UNEXPECTED | STATUS3_MAP_VALUE;
				return true;
			}
			if (node.getYamlParent() != null
					&& node.getYamlParent().getNodeType() == NodeType.MAP_ENTRY
					&& node.getYamlParent().getStatusCode() == 0) {
				node.getYamlParent().status= STATUS2_SYNTAX_TOKEN_UNEXPECTED | STATUS3_MAP_VALUE;
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}
	
}
