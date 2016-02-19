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

package de.walware.eutils.yaml.core.ast;

import java.lang.reflect.InvocationTargetException;

import de.walware.eutils.yaml.core.ast.YamlAst.NodeType;


public abstract class Collection extends NContainer {
	
	
	static abstract class FlowCollection extends Collection {
		
		
		int closeIndicatorOffset= NA_OFFSET;
		
		
		FlowCollection(final YamlAstNode parent, final int offset, final int stopOffset) {
			super(parent, offset, stopOffset);
		}
		
		
		@Override
		public int getOpenIndicatorOffset() {
			return this.beginOffset;
		}
		
		@Override
		public int getCloseIndicatorOffset() {
			return this.closeIndicatorOffset;
		}
		
		
	}
	
	static final class FlowSeq extends FlowCollection {
		
		
		FlowSeq(final YamlAstNode parent, final int offset, final int stopOffset) {
			super(parent, offset, stopOffset);
		}
		
		
		@Override
		public NodeType getNodeType() {
			return NodeType.SEQUENCE;
		}
		
		@Override
		public char getOperator() {
			return '[';
		}
		
	}
	
	static final class FlowMap extends FlowCollection {
		
		
		FlowMap(final YamlAstNode parent, final int offset, final int stopOffset) {
			super(parent, offset, stopOffset);
		}
		
		
		@Override
		public NodeType getNodeType() {
			return NodeType.MAP;
		}
		
		@Override
		public char getOperator() {
			return '{';
		}
		
	}
	
	static abstract class BlockCollection extends Collection {
		
		
		BlockCollection(final YamlAstNode parent, final int offset, final int stopOffset) {
			super(parent, offset, stopOffset);
		}
		
		
		@Override
		public int getOpenIndicatorOffset() {
			return NA_OFFSET;
		}
		
		@Override
		public int getCloseIndicatorOffset() {
			return NA_OFFSET;
		}
		
		
	}
	
	static final class BlockSeq extends BlockCollection {
		
		
		BlockSeq(final YamlAstNode parent, final int offset, final int stopOffset) {
			super(parent, offset, stopOffset);
		}
		
		
		@Override
		public NodeType getNodeType() {
			return NodeType.SEQUENCE;
		}
		
		@Override
		public char getOperator() {
			return '-';
		}
		
	}
	
	static final class BlockMap extends BlockCollection {
		
		
		BlockMap(final YamlAstNode parent, final int offset, final int stopOffset) {
			super(parent, offset, stopOffset);
		}
		
		
		@Override
		public NodeType getNodeType() {
			return NodeType.MAP;
		}
		
		@Override
		public char getOperator() {
			return ':'; // symbolic
		}
		
	}
	
	
	private Collection(final YamlAstNode parent, final int offset, final int stopOffset) {
		super(parent, offset, stopOffset);
	}
	
	
	@Override
	public void acceptInYaml(final YamlAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	
	public abstract int getOpenIndicatorOffset();
	
	public abstract int getCloseIndicatorOffset();
	
	
}
