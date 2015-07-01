/*=============================================================================#
 # Copyright (c) 2008-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.core.model;

import org.eclipse.core.resources.IFile;

import de.walware.ecommons.ltk.core.impl.AbstractFilePersistenceSourceUnitFactory;
import de.walware.ecommons.ltk.core.model.ISourceUnit;

import de.walware.eutils.yaml.core.model.YamlSourceUnit;


/**
 * Factory for common YAML files
 */
public class YamlSourceUnitFactory extends AbstractFilePersistenceSourceUnitFactory {
	
	
	@Override
	protected ISourceUnit createSourceUnit(final String id, final IFile file) {
		return new YamlSourceUnit(id, file);
	}
	
}
