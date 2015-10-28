/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
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

import de.walware.jcommons.collections.ImCollections;

import de.walware.eutils.yaml.core.ast.YamlAstNode;
import de.walware.eutils.yaml.core.model.YamlElementName;
import de.walware.eutils.yaml.core.model.YamlLabelAccess;


public class LabelAccess extends YamlLabelAccess {
	
	
	public final static int A_READ=                       0x00000000;
	public final static int A_WRITE=                      0x00000002;
	
	
	static class Shared {
		
		
		private final String label;
		
		private List<YamlLabelAccess> all;
		
		
		public Shared(final String label) {
			this.label= label;
			this.all= new ArrayList<>(8);
		}
		
		
		public void finish() {
			this.all= ImCollections.toList(this.all);
		}
		
		public List<YamlLabelAccess> getAll() {
			return this.all;
		}
		
	}
	
	
	private final Shared shared;
	
	private final YamlAstNode node;
	private final YamlAstNode nameNode;
	
	int flags;
	
	
	protected LabelAccess(final Shared shared, final YamlAstNode node, final YamlAstNode labelNode) {
		this.shared= shared;
		shared.all.add(this);
		this.node= node;
		this.nameNode= labelNode;
	}
	
	
	@Override
	public int getType() {
		return LABEL;
	}
	
	@Override
	public String getSegmentName() {
		return this.shared.label;
	}
	
	@Override
	public String getDisplayName() {
		return this.shared.label;
	}
	
	@Override
	public YamlElementName getNextSegment() {
		return null;
	}
	
	
	@Override
	public YamlAstNode getNode() {
		return this.node;
	}
	
	@Override
	public YamlAstNode getNameNode() {
		return this.nameNode;
	}
	
	@Override
	public List<? extends YamlLabelAccess> getAllInUnit() {
		return this.shared.all;
	}
	
	
	@Override
	public boolean isWriteAccess() {
		return ((this.flags & A_WRITE) != 0);
	}
	
}
