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

package de.walware.eutils.yaml.internal.ui;

import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.model.IWorkspaceSourceUnit;
import de.walware.ecommons.ltk.ui.GenericEditorWorkspaceSourceUnitWorkingCopy2;

import de.walware.eutils.yaml.core.model.YamlSuModelContainer;


public final class YamlEditorWorkingCopy
		extends GenericEditorWorkspaceSourceUnitWorkingCopy2<YamlSuModelContainer<ISourceUnit>> {
	
	
	public YamlEditorWorkingCopy(final IWorkspaceSourceUnit from) {
		super(from);
	}
	
	@Override
	protected YamlSuModelContainer<ISourceUnit> createModelContainer() {
		return new YamlSuModelContainer<ISourceUnit>(this);
	}
	
	
}
