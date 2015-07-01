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


public class Instruction extends YamlAstNode {
	
	
	static final class DocStart extends Instruction {
		
		DocStart(final SourceComponent parent, final int startOffset, final int stopOffset) {
			super(parent, startOffset, stopOffset);
		}
		
		
		@Override
		public String getText() {
			return "---"; //$NON-NLS-1$
		}
		
	}
	
	static final class DocEnd extends Instruction {
		
		DocEnd(final SourceComponent parent, final int startOffset, final int stopOffset) {
			super(parent, startOffset, stopOffset);
		}
		
		
		@Override
		public String getText() {
			return "..."; //$NON-NLS-1$
		}
		
	}
	
	
	Instruction(final YamlAstNode parent, final int startOffset, final int stopOffset) {
		super(parent, startOffset, stopOffset);
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.INSTRUCTION;
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
