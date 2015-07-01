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

import java.lang.reflect.InvocationTargetException;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;

import de.walware.eutils.yaml.core.ast.YamlAst.NodeType;


public abstract class YamlAstNode implements IAstNode {
	
	
	protected static final YamlAstNode[] NO_CHILDREN= new YamlAstNode[0];
	
	private static final ImList<Object> NO_ATTACHMENT= ImCollections.emptyList();
	
	
	int status;
	
	YamlAstNode yamlParent;
	
	int startOffset;
	int stopOffset;
	
	private volatile ImList<Object> attachments= NO_ATTACHMENT;
	
	
	YamlAstNode() {
	}
	
	YamlAstNode(final YamlAstNode parent, final int startOffset, final int stopOffset) {
		this.yamlParent= parent;
		
		this.startOffset= startOffset;
		this.stopOffset= stopOffset;
	}
	
	
	public abstract NodeType getNodeType();
	
	public char getOperator() {
		return 0;
	}
	
	@Override
	public final int getStatusCode() {
		return this.status;
	}
	
	public final YamlAstNode getYamlParent() {
		return this.yamlParent;
	}
	
	@Override
	public IAstNode getParent() {
		return this.yamlParent;
	}
	
	@Override
	public final IAstNode getRoot() {
		IAstNode candidate= this;
		IAstNode p;
		while ((p= candidate.getParent()) != null) {
			candidate= p;
		}
		return candidate;
	}
	
	@Override
	public final int getOffset() {
		return this.startOffset;
	}
	
	@Override
	public final int getStopOffset() {
		return this.stopOffset;
	}
	
	@Override
	public final int getLength() {
		return this.stopOffset - this.startOffset;
	}
	
	public String getText() {
		return null;
	}
	
	
	@Override
	public abstract YamlAstNode getChild(final int index);
	
	@Override
	public final void accept(final ICommonAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	public abstract void acceptInYaml(YamlAstVisitor visitor) throws InvocationTargetException;
	
	public abstract void acceptInYamlChildren(YamlAstVisitor visitor) throws InvocationTargetException;
	
	
	@Override
	public synchronized void addAttachment(final Object data) {
		this.attachments= ImCollections.addElement(this.attachments, data);
	}
	
	@Override
	public synchronized void removeAttachment(final Object data) {
		this.attachments= ImCollections.removeElement(this.attachments, data);
	}
	
	@Override
	public ImList<Object> getAttachments() {
		return this.attachments;
	}
	
}
