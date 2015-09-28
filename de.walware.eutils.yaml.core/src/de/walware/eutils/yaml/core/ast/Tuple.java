/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
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


public class Tuple extends YamlAstNode {
	
	
	int keyIndicatorOffset= Integer.MAX_VALUE;
	
	YamlAstNode keyNode;
	
	int valueIndicatorOffset= Integer.MIN_VALUE;
	
	YamlAstNode valueNode;
	
	
	Tuple(final YamlAstNode parent, final int beginOffset, final int endOffset) {
		super(parent, beginOffset, endOffset);
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.MAP_ENTRY;
	}
	
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public int getChildCount() {
		return 2;
	}
	
	public int getKeyIndicatorOffset() {
		return this.keyIndicatorOffset;
	}
	
	public YamlAstNode getKeyNode() {
		return this.keyNode;
	}
	
	public int getValueIndicatorOffset() {
		return this.valueIndicatorOffset;
	}
	
	public YamlAstNode getValueNode() {
		return this.valueNode;
	}
	
	@Override
	public YamlAstNode getChild(final int index) {
		switch (index) {
		case 0:
			return this.keyNode;
		case 1:
			return this.valueNode;
		default:
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
	}
	
	@Override
	public int getChildIndex(final IAstNode element) {
		if (element == this.keyNode) {
			return 0;
		}
		if (element == this.valueNode) {
			return 1;
		}
		return -1;
	}
	
	
	@Override
	public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this.keyNode);
		visitor.visit(this.valueNode);
	}
	
	@Override
	public void acceptInYaml(final YamlAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptInYamlChildren(final YamlAstVisitor visitor) throws InvocationTargetException {
		this.keyNode.acceptInYaml(visitor);
		this.valueNode.acceptInYaml(visitor);
	}
	
}
