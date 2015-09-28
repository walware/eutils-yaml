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


abstract class NContainer extends YamlAstNode {
	
	
	YamlAstNode[] children= NO_CHILDREN;
	
	
	NContainer() {
	}
	
	NContainer(final YamlAstNode parent, final int beginOffset, final int endOffset) {
		super(parent, beginOffset, endOffset);
	}
	
	
	@Override
	public final boolean hasChildren() {
		return (this.children.length > 0);
	}
	
	@Override
	public final int getChildCount() {
		return this.children.length;
	}
	
	@Override
	public final YamlAstNode getChild(final int index) {
		return this.children[index];
	}
	
	@Override
	public final int getChildIndex(final IAstNode element) {
		for (int i= 0; i < this.children.length; i++) {
			if (this.children[i] == element) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public final void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
		for (final YamlAstNode child : this.children) {
			visitor.visit(child);
		}
	}
	
	
	@Override
	public final void acceptInYamlChildren(final YamlAstVisitor visitor) throws InvocationTargetException {
		for (final YamlAstNode child : this.children) {
			child.acceptInYaml(visitor);
		}
	}
	
}
