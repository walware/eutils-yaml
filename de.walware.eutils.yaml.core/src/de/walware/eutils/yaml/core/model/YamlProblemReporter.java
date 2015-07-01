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

package de.walware.eutils.yaml.core.model;

import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS1_SYNTAX_MISSING_INDICATOR;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS2_SYNTAX_COLLECTION_NOT_CLOSED;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS2_SYNTAX_ESCAPE_INVALID;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS2_SYNTAX_TOKEN_NOT_CLOSED;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS3_FLOW_MAP;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS3_FLOW_SEQ;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS3_MAP_VALUE;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUSFLAG_SUBSEQUENT;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS_MASK_123;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS_MASK_2;
import static de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants.STATUS_OK;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import de.walware.ecommons.MessageBuilder;
import de.walware.ecommons.ltk.IProblem;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.StatusDetail;
import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.ltk.core.impl.Problem;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;
import de.walware.ecommons.ltk.core.model.ISourceUnit;

import de.walware.eutils.yaml.core.ast.Collection;
import de.walware.eutils.yaml.core.ast.Dummy;
import de.walware.eutils.yaml.core.ast.IYamlAstStatusConstants;
import de.walware.eutils.yaml.core.ast.Scalar;
import de.walware.eutils.yaml.core.ast.Tuple;
import de.walware.eutils.yaml.core.ast.YamlAstNode;
import de.walware.eutils.yaml.core.ast.YamlAstVisitor;
import de.walware.eutils.yaml.internal.core.model.Messages;


public class YamlProblemReporter extends YamlAstVisitor {
	
	
	private static final int BUFFER_SIZE= 64;
	
	private static final int START_TEXT_LIMIT= 25;
	
	private static final int MASK= 0x0_00ff_ffff;
	
	
	private boolean reportSubsequent= false;
	
	private ISourceUnit sourceUnit;
	private SourceContent sourceContent;
	private IProblemRequestor problemRequestor;
	
	private final StringBuilder sBuilder= new StringBuilder();
	private final MessageBuilder messageBuilder= new MessageBuilder();
	private final List<IProblem> problemBuffer= new ArrayList<>(BUFFER_SIZE);
	
	
	public YamlProblemReporter() {
	}
	
	
	public void run(final IYamlModelInfo model, final SourceContent content,
			final IProblemRequestor requestor, final int level, final IProgressMonitor monitor) {
		try {
			final ISourceStructElement root= model.getSourceElement();
			this.sourceUnit= root.getSourceUnit();
			this.sourceContent= content;
			this.problemRequestor= requestor;
			
			if (root instanceof IYamlCompositeSourceElement) {
				for (final ISourceStructElement chunk : ((IYamlCompositeSourceElement) root).getCompositeElements()) {
					check((IAstNode) chunk.getAdapter(IAstNode.class));
				}
			}
			else {
				check((IAstNode) root.getAdapter(IAstNode.class));
			}
			
			if (!this.problemBuffer.isEmpty()) {
				this.problemRequestor.acceptProblems(YamlModel.YAML_TYPE_ID, this.problemBuffer);
			}
		}
		catch (final OperationCanceledException | InvocationTargetException e) {}
		finally {
			this.sourceUnit= null;
			this.sourceContent= null;
			this.problemRequestor= null;
			this.problemBuffer.clear();
		}
	}
	
	private void check(final IAstNode node) throws InvocationTargetException {
		if (node instanceof YamlAstNode) {
			((YamlAstNode) node).acceptInYaml(this);
		}
	}
	
	
	private boolean requiredCheck(final int code) {
		return code != STATUS_OK &&
				(this.reportSubsequent || ((code & STATUSFLAG_SUBSEQUENT) == 0));
	}
	
	protected final void addProblem(final int severity, final int code, final String message,
			int startOffset, int stopOffset) {
		if (startOffset < this.sourceContent.getBeginOffset()) {
			startOffset= this.sourceContent.getBeginOffset();
		}
		if (stopOffset < startOffset) {
			stopOffset= startOffset;
		}
		else if (stopOffset > this.sourceContent.getEndOffset()) {
			stopOffset= this.sourceContent.getEndOffset();
		}
		
		this.problemBuffer.add(new Problem(YamlModel.YAML_TYPE_ID, severity, code, message,
				this.sourceUnit, startOffset, stopOffset ));
		
		if (this.problemBuffer.size() >= BUFFER_SIZE) {
			this.problemRequestor.acceptProblems(YamlModel.YAML_TYPE_ID, this.problemBuffer);
			this.problemBuffer.clear();
		}
	}
	
	
	protected final StringBuilder getStringBuilder() {
		this.sBuilder.setLength(0);
		return this.sBuilder;
	}
	
	protected String getStartText(final YamlAstNode node, final int offset)
			throws BadLocationException {
		final String text= node.getText();
		if (text != null) {
			if (text.length() > START_TEXT_LIMIT) {
				final StringBuilder sb= getStringBuilder();
				sb.append(text, 0, START_TEXT_LIMIT);
				sb.append('…');
				return sb.toString();
			}
			else {
				return text;
			}
		}
		else {
			if (node.getLength() - offset > START_TEXT_LIMIT) {
				final StringBuilder sb= getStringBuilder();
				sb.append(this.sourceContent.getText(), 
						node.getOffset() + offset, node.getOffset() + offset + START_TEXT_LIMIT);
				sb.append('…');
				return sb.toString();
			}
			else {
				return this.sourceContent.getText().substring(
						node.getOffset() + offset, node.getStopOffset() + offset );
			}
		}
	}
	
	
	protected void handleCommonCodes(final YamlAstNode node, final int code)
			throws BadLocationException, InvocationTargetException {
		switch ((code & STATUS_MASK_2)) {
		case IYamlAstStatusConstants.STATUS2_SYNTAX_TOKEN_UNKNOWN:
			addProblem(IProblem.SEVERITY_ERROR, code, this.messageBuilder.bind(
					Messages.Syntax_GenTokenUnknown_message,
					getStartText(node, 0) ),
					node.getOffset(), node.getStopOffset() );
			return;
			
		case IYamlAstStatusConstants.STATUS2_SYNTAX_TOKEN_UNEXPECTED:
			addProblem(IProblem.SEVERITY_ERROR, code, this.messageBuilder.bind(
					Messages.Syntax_GenTokenUnexpected_message,
					getStartText(node, 0) ),
					node.getOffset(), node.getStopOffset() );
			return;
		
		default:
			// TODO
			return;
		}
	}
	
	
	@Override
	public void visit(final Collection node) throws InvocationTargetException {
		final int code= (node.getStatusCode() & MASK);
		if (requiredCheck(code)) {
			try {
				STATUS: switch ((code & STATUS_MASK_123)) {
				case STATUS2_SYNTAX_COLLECTION_NOT_CLOSED | STATUS3_FLOW_SEQ:
					addProblem(IProblem.SEVERITY_ERROR, code,
							Messages.Syntax_FlowSeqNotClosed_message,
							node.getOffset(), node.getOffset() + 1 );
					break STATUS;
				case STATUS2_SYNTAX_COLLECTION_NOT_CLOSED | STATUS3_FLOW_MAP:
					addProblem(IProblem.SEVERITY_ERROR, code,
							Messages.Syntax_FlowMapNotClosed_message,
							node.getOffset(), node.getOffset() + 1 );
					break STATUS;
				default:
					handleCommonCodes(node, code);
					break STATUS;
				}
			}
			catch (final BadLocationException e) {
				throw new InvocationTargetException(e);
			}
		}
		node.acceptInYamlChildren(this);
	}
	
	@Override
	public void visit(final Tuple node) throws InvocationTargetException {
		final int code= (node.getStatusCode() & MASK);
		if (requiredCheck(code)) {
			try {
				STATUS: switch ((code & STATUS_MASK_123)) {
				case STATUS1_SYNTAX_MISSING_INDICATOR | STATUS3_MAP_VALUE:
					addProblem(IProblem.SEVERITY_ERROR, code,
							Messages.Syntax_FlowSeqNotClosed_message,
							node.getKeyNode().getStopOffset() - 1, node.getKeyNode().getStopOffset() + 1 );
					break STATUS;
				default:
					handleCommonCodes(node, code);
					break STATUS;
				}
			}
			catch (final BadLocationException e) {
				throw new InvocationTargetException(e);
			}
		}
		node.acceptInYamlChildren(this);
	}
	
	@Override
	public void visit(final Scalar node) throws InvocationTargetException {
		final int code= (node.getStatusCode() & MASK);
		if (requiredCheck(code)) {
			try {
				StatusDetail detail;
				STATUS: switch ((code & STATUS_MASK_2)) {
				case STATUS2_SYNTAX_TOKEN_NOT_CLOSED:
					addProblem(IProblem.SEVERITY_ERROR, code, this.messageBuilder.bind(
							Messages.Syntax_QuotedScalarNotClosed_message,
							getStartText(node, 1), String.valueOf(node.getOperator()) ),
							node.getStopOffset() - 1, node.getStopOffset() + 1 );
					break STATUS;
				case STATUS2_SYNTAX_ESCAPE_INVALID:
					detail= StatusDetail.getStatusDetail(node);
					if (detail != null) {
						addProblem(IProblem.SEVERITY_ERROR, code, this.messageBuilder.bind(
								Messages.Syntax_QuotedScalarEscapeSequenceInvalid_messsage,
								detail.getText() ),
								detail.getOffset(), detail.getOffset() + detail.getLength() );
					}
					else {
						addProblem(IProblem.SEVERITY_ERROR, code, this.messageBuilder.bind(
								Messages.Syntax_GenEscapeSequenceInvalid_messsage,
								getStartText(node, 0) ),
								node.getOffset(), node.getStopOffset() );
					}
					break STATUS;
				default:
					handleCommonCodes(node, code);
					break STATUS;
				}
			}
			catch (final BadLocationException e) {
				throw new InvocationTargetException(e);
			}
		}
	}
	
	@Override
	public void visit(final Dummy node) throws InvocationTargetException {
		final int code= (node.getStatusCode() & MASK);
		if (requiredCheck(code)) {
			try {
//				STATUS: switch ((code & STATUS_MASK_2)) {
//				default:
					handleCommonCodes(node, code);
//					break STATUS;
//				}
			}
			catch (final BadLocationException e) {
				throw new InvocationTargetException(e);
			}
		}
	}
	
}
