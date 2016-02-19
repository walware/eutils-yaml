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


public class YamlAstVisitor {
	
	
	public void visit(final SourceComponent node) throws InvocationTargetException {
		node.acceptInYamlChildren(this);
	}
	
	public void visit(final Directive directive) {
	}
	
	public void visit(final DocContent node) throws InvocationTargetException {
		node.acceptInYamlChildren(this);
	}
	
	public void visit(final Instruction instruction) throws InvocationTargetException {
	}
	
	public void visit(final Collection node) throws InvocationTargetException {
		node.acceptInYamlChildren(this);
	}
	
	public void visit(final Tuple node) throws InvocationTargetException {
		node.acceptInYamlChildren(this);
	}
	
	public void visit(final Label node) throws InvocationTargetException {
	}
	
	public void visit(final Tag node) throws InvocationTargetException {
	}
	
	public void visit(final Scalar node) throws InvocationTargetException {
	}
	
	public void visit(final Comment node) throws InvocationTargetException {
	}
	
	public void visit(final Dummy node) throws InvocationTargetException {
	}

}
