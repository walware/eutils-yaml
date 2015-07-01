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

package de.walware.eutils.yaml.core.model;

import org.eclipse.core.resources.IFile;

import de.walware.ecommons.ltk.core.impl.GenericResourceSourceUnit;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.text.core.sections.IDocContentSections;

import de.walware.eutils.yaml.core.source.YamlDocumentContentInfo;


public class YamlSourceUnit extends GenericResourceSourceUnit implements ISourceUnit {
	
	
	public YamlSourceUnit(final String id, final IFile file) {
		super(id, file);
	}
	
	
	@Override
	public String getModelTypeId() {
		return YamlModel.YAML_TYPE_ID;
	}
	
	@Override
	public IDocContentSections getDocumentContentInfo() {
		return YamlDocumentContentInfo.INSTANCE;
	}
	
}
