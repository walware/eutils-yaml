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

package de.walware.eutils.yaml.internal.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ltk.IElementName;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;

import de.walware.eutils.yaml.core.ast.YamlAstNode;
import de.walware.eutils.yaml.core.model.IYamlSourceElement;
import de.walware.eutils.yaml.core.model.YamlElementName;
import de.walware.eutils.yaml.core.model.YamlModel;


public abstract class YamlSourceElement implements IYamlSourceElement {
	
	
	private static final ImList<YamlSourceElement> NO_CHILDREN= ImCollections.emptyList();
	
	static final List<? extends IYamlSourceElement> getChildren(
			final List<? extends IYamlSourceElement> children, final IModelElement.Filter filter) {
		if (filter == null) {
			return children;
		}
		else {
			final ArrayList<IYamlSourceElement> filtered= new ArrayList<>(children.size());
			for (final IYamlSourceElement child : children) {
				if (filter.include(child)) {
					filtered.add(child);
				}
			}
			return filtered;
		}
	}
	
	static final boolean hasChildren(
			final List<? extends IYamlSourceElement> children, final IModelElement.Filter filter) {
		if (filter == null) {
			return (!children.isEmpty());
		}
		else {
			for (final IYamlSourceElement child : children) {
				if (filter.include(child)) {
					return true;
				}
			}
			return false;
		}
	}
	
	
	public abstract static class Container extends YamlSourceElement {
		
		
		List<YamlSourceElement> children= NO_CHILDREN;
		IRegion nameRegion;
		
		private final YamlAstNode astNode;
		
		
		public Container(final YamlAstNode astNode) {
			super();
			this.astNode= astNode;
		}
		
		
		@Override
		public IRegion getNameSourceRange() {
			return this.nameRegion;
		}
		
		@Override
		public IRegion getSourceRange() {
			return this.astNode;
		}
		
		@Override
		public boolean hasSourceChildren(final Filter filter) {
			return hasChildren(this.children, filter);
		}
		
		@Override
		public List<? extends IYamlSourceElement> getSourceChildren(final Filter filter) {
			return getChildren(this.children, filter);
		}
		
		@Override
		public abstract Container getModelParent();
		
		@Override
		public boolean hasModelChildren(final Filter filter) {
			return hasChildren(this.children, filter);
		}
		
		@Override
		public List<? extends IModelElement> getModelChildren(final Filter filter) {
			return getChildren(this.children, filter);
		}
		
		@Override
		public Object getAdapter(final Class required) {
			if (IAstNode.class.equals(required)) {
				return this.astNode;
			}
			return super.getAdapter(required);
		}
		
	}
	
	public static class SourceContainer extends Container {
		
		
		private final ISourceUnit sourceUnit;
		
		
		public SourceContainer(final ISourceUnit su, final YamlAstNode astNode) {
			super(astNode);
			this.sourceUnit= su;
		}
		
		
		@Override
		public int getElementType() {
			return IYamlSourceElement.C2_SOURCE_FILE;
		}
		
		@Override
		public String getId() {
			return this.sourceUnit.getId();
		}
		
		@Override
		public YamlElementName getElementName() {
			final IElementName elementName= this.sourceUnit.getElementName();
			if (elementName instanceof YamlElementName) {
				return (YamlElementName) elementName;
			}
			return YamlElementName.create(YamlElementName.RESOURCE, elementName.getSegmentName());
		}
		
		@Override
		public ISourceUnit getSourceUnit() {
			return this.sourceUnit;
		}
		
		@Override
		public boolean exists() {
			final ISourceUnitModelInfo modelInfo= getSourceUnit().getModelInfo(YamlModel.YAML_TYPE_ID, 0, null);
			return (modelInfo != null && modelInfo.getSourceElement() == this);
		}
		
		@Override
		public boolean isReadOnly() {
			return this.sourceUnit.isReadOnly();
		}
		
		@Override
		public ISourceStructElement getSourceParent() {
			return null;
		}
		
		@Override
		public Container getModelParent() {
			return null;
		}
		
	}
	
	public static class StructContainer extends Container {
		
		
		private final int type;
		
		private final Container parent;
		
		
		public StructContainer(final int type, final Container parent, final YamlAstNode astNode) {
			super(astNode);
			this.type= type;
			this.parent= parent;
		}
		
		
		@Override
		public final int getElementType() {
			return this.type;
		}
		
		@Override
		public ISourceUnit getSourceUnit() {
			return this.parent.getSourceUnit();
		}
		
		@Override
		public boolean exists() {
			return this.parent.exists();
		}
		
		@Override
		public boolean isReadOnly() {
			return this.parent.isReadOnly();
		}
		
		@Override
		public ISourceStructElement getSourceParent() {
			return this.parent;
		}
		
		@Override
		public Container getModelParent() {
			return this.parent;
		}
		
	}
	
	
	protected YamlElementName name;
	protected int occurrenceCount;
	
	
	protected YamlSourceElement() {
	}
	
	
	@Override
	public final String getModelTypeId() {
		return YamlModel.YAML_TYPE_ID;
	}
	
	@Override
	public String getId() {
		final String name= getElementName().getDisplayName();
		final StringBuilder sb= new StringBuilder(name.length() + 16);
		sb.append(Integer.toHexString(getElementType() & MASK_C2));
		sb.append(':');
		sb.append(name);
		sb.append('#');
		sb.append(this.occurrenceCount);
		return sb.toString();
	}
	
	@Override
	public IElementName getElementName() {
		return this.name;
	}
	
	@Override
	public IRegion getDocumentationRange() {
		return null;
	}
	
	
	@Override
	public Object getAdapter(final Class adapter) {
		return null;
	}
	
	
	@Override
	public int hashCode() {
		return (getElementType() & MASK_C2) * getElementName().hashCode() + this.occurrenceCount;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof YamlSourceElement)) {
			return false;
		}
		final YamlSourceElement other= (YamlSourceElement) obj;
		return ( (getElementType() & MASK_C2) == (getElementType() & MASK_C2))
				&& (this.occurrenceCount == other.occurrenceCount)
				&& ( ((getElementType() & MASK_C1) == C1_SOURCE) || (getSourceParent().equals(other.getSourceParent())) )
				&& (getElementName().equals(other.getElementName()) );
	}
	
}
