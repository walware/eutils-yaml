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
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;

import de.walware.eutils.yaml.core.ast.YamlAst.NodeType;


public abstract class Scalar extends YamlAstNode {
	
	
	static class DQuoted extends Scalar {
		
		DQuoted(final YamlAstNode parent, final int beginOffset, final int endOffset, final String value) {
			super(parent, beginOffset, endOffset, value);
		}
		
		@Override
		public char getOperator() {
			return '\"';
		}
		
	}
	
	static class SQuoated extends Scalar {
		
		SQuoated(final YamlAstNode parent, final int beginOffset, final int endOffset, final String value) {
			super(parent, beginOffset, endOffset, value);
		}
		
		@Override
		public char getOperator() {
			return '\'';
		}
		
	}
	
	static class Plain extends Scalar {
		
		Plain(final YamlAstNode parent, final int beginOffset, final int endOffset, final String value) {
			super(parent, beginOffset, endOffset, value);
		}
		
		@Override
		public char getOperator() {
			return 0;
		}
		
	}
	
	
	String value;
	
	
	Scalar(final YamlAstNode parent, final int beginOffset, final int endOffset,
			final String value) {
		super(parent, beginOffset, endOffset);
		
		this.value= value;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.SCALAR;
	}
	
	
	@Override
	public String getText() {
		return this.value;
	}
	
	
	@Override
	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public int getChildCount() {
		return 0;
	}
	
	@Override
	public YamlAstNode getChild(final int index) {
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public int getChildIndex(final IAstNode element) {
		return -1;
	}
	
	@Override
	public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
	}
	
	@Override
	public void acceptInYaml(final YamlAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptInYamlChildren(final YamlAstVisitor visitor) throws InvocationTargetException {
	}
	
}
