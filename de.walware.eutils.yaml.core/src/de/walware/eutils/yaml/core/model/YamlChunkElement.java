/*=============================================================================#
 # Copyright (c) 2011-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.core.model;

import de.walware.ecommons.ltk.core.model.ISourceStructElement;
import de.walware.ecommons.ltk.core.model.ISourceUnit;

import de.walware.eutils.yaml.core.ast.YamlAstNode;
import de.walware.eutils.yaml.internal.core.model.YamlSourceElement;


public class YamlChunkElement extends YamlSourceElement.Container
		implements IYamlSourceElement {
	
	
	private final ISourceStructElement parent;
	
	
	public YamlChunkElement(final ISourceStructElement parent, final YamlAstNode astNode,
			final YamlElementName name, final int occurrenceCount) {
		super(astNode);
		this.parent= parent;
		this.name= name;
		this.occurrenceCount= occurrenceCount;
	}
	
	
	@Override
	public int getElementType() {
		return IYamlElement.C2_SOURCE_CHUNK;
	}
	
	@Override
	public ISourceUnit getSourceUnit() {
		return this.parent.getSourceUnit();
	}
	
	@Override
	public boolean isReadOnly() {
		return this.parent.isReadOnly();
	}
	
	@Override
	public boolean exists() {
		return this.parent.exists();
	}
	
	@Override
	public Container getModelParent() {
		return null;
	}
	
	@Override
	public ISourceStructElement getSourceParent() {
		return this.parent;
	}
	
}
