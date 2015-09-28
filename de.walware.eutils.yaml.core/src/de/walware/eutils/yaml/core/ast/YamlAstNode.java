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

import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.core.impl.AbstractAstNode;

import de.walware.eutils.yaml.core.ast.YamlAst.NodeType;


public abstract class YamlAstNode extends AbstractAstNode
		implements IAstNode {
	
	
	protected static final YamlAstNode[] NO_CHILDREN= new YamlAstNode[0];
	
	
	int status;
	
	YamlAstNode yamlParent;
	
	int beginOffset;
	int endOffset;
	
	
	YamlAstNode() {
	}
	
	YamlAstNode(final YamlAstNode parent, final int beginOffset, final int endOffset) {
		this.yamlParent= parent;
		
		this.beginOffset= beginOffset;
		this.endOffset= endOffset;
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
	public final int getOffset() {
		return this.beginOffset;
	}
	
	@Override
	public final int getEndOffset() {
		return this.endOffset;
	}
	
	@Override
	public final int getLength() {
		return this.endOffset - this.beginOffset;
	}
	
	
	@Override
	public abstract YamlAstNode getChild(final int index);
	
	
	public abstract void acceptInYaml(YamlAstVisitor visitor) throws InvocationTargetException;
	
	public abstract void acceptInYamlChildren(YamlAstVisitor visitor) throws InvocationTargetException;
	
	
}
