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

package de.walware.eutils.yaml.internal.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ltk.core.model.ISourceUnit;

import de.walware.eutils.yaml.core.model.IYamlCompositeSourceElement;
import de.walware.eutils.yaml.core.model.IYamlSourceElement;


public class CompositeSourceElement extends YamlSourceElement.SourceContainer
		implements IYamlCompositeSourceElement {
	
	
	private final ImList<? extends IYamlSourceElement> compositeElements;
	
	private final IRegion sourceRange;
	
	private volatile List<IYamlSourceElement> allSourceChildren;
	
	
	public CompositeSourceElement(final List<? extends IYamlSourceElement> elements,
			final ISourceUnit su, final IRegion sourceRange) {
		super(su, null);
		
		this.compositeElements= ImCollections.toList(elements);
		this.sourceRange= sourceRange;
	}
	
	
	@Override
	public ImList<? extends IYamlSourceElement> getCompositeElements() {
		return this.compositeElements;
	}
	
	@Override
	public IRegion getSourceRange() {
		return this.sourceRange;
	}
	
	@Override
	public boolean hasSourceChildren(final Filter filter) {
		for (final IYamlSourceElement element : this.compositeElements) {
			if (element.hasSourceChildren(filter)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<IYamlSourceElement> getSourceChildren(final Filter filter) {
		if (filter == null) {
			List<IYamlSourceElement> children= this.allSourceChildren;
			if (children == null) {
				final List<? extends IYamlSourceElement>[] compositeLists= new List[this.compositeElements.size()];
				for (int i= 0; i < compositeLists.length; i++) {
					compositeLists[i]= this.compositeElements.get(i).getSourceChildren(null);
				}
				children= this.allSourceChildren= ImCollections.concatList(compositeLists);
			}
			return children;
		}
		else {
			final List<IYamlSourceElement> children= new ArrayList<>();
			for (final IYamlSourceElement element : this.compositeElements) {
				final List<? extends IYamlSourceElement> list= element.getSourceChildren(null);
				for (final IYamlSourceElement child : list) {
					if (filter.include(child)) {
						children.add(child);
					}
				}
			}
			return children;
		}
	}
	
}
