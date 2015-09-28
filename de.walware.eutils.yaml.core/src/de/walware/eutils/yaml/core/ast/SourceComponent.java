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

import de.walware.eutils.yaml.core.ast.YamlAst.NodeType;


public final class SourceComponent extends NContainer {
	
	
	private final IAstNode parent;
	
	
	public SourceComponent(final IAstNode parent, final int beginOffset, final int endOffset) {
		super(null, beginOffset, endOffset);
		
		this.parent= parent;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.SOURCELINES;
	}
	
	@Override
	public IAstNode getParent() {
		return this.parent;
	}
	
	
	@Override
	public void acceptInYaml(final YamlAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
}
