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


public class Label extends YamlAstNode {
	
	
	static final class Anchor extends Label {
		
		Anchor(final YamlAstNode parent, final int beginOffset, final int endOffset,
				final String label) {
			super(parent, beginOffset, endOffset, label);
		}
		
		
		@Override
		public char getOperator() {
			return '*';
		}
		
	}
	
	static final class Reference extends Label {
		
		Reference(final YamlAstNode parent, final int beginOffset, final int endOffset,
				final String label) {
			super(parent, beginOffset, endOffset, label);
		}
		
		
		@Override
		public char getOperator() {
			return '&';
		}
		
	}
	
	
	private final String label;
	
	
	private Label(final YamlAstNode parent, final int beginOffset, final int endOffset,
			final String label) {
		super(parent, beginOffset, endOffset);
		
		this.label= label;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.LABEL;
	}
	
	@Override
	public String getText() {
		return this.label;
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
