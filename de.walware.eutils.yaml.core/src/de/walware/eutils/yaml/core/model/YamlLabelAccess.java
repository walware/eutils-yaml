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

package de.walware.eutils.yaml.core.model;

import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;

import de.walware.eutils.yaml.core.ast.YamlAstNode;


public abstract class YamlLabelAccess extends YamlElementName {
	
	
	public static final Comparator<YamlLabelAccess> NAME_POSITION_COMPARATOR= 
			new Comparator<YamlLabelAccess>() {
				@Override
				public int compare(final YamlLabelAccess o1, final YamlLabelAccess o2) {
					return (o1.getNameNode().getOffset() - o2.getNameNode().getOffset()); 
			}
	};
	
	
	public static Position getTextPosition(final YamlAstNode node) {
		return new Position(node.getOffset(), node.getLength());
	}
	
	public static IRegion getTextRegion(final YamlAstNode node) {
		return new Region(node.getOffset(), node.getLength());
	}
	
	
	protected YamlLabelAccess() {
	}
	
	
	public abstract YamlAstNode getNode();
	
	public abstract YamlAstNode getNameNode();
	
	public abstract List<? extends YamlLabelAccess> getAllInUnit();
	
	
	public abstract boolean isWriteAccess();
	
}
