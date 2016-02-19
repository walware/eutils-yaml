/*=============================================================================#
 # Copyright (c) 2008-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.core.model;

import java.util.Map;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.core.impl.AbstractSourceModelInfo;

import de.walware.eutils.yaml.core.model.IYamlModelInfo;
import de.walware.eutils.yaml.core.model.IYamlSourceElement;


public class YamlSourceUnitModelInfo extends AbstractSourceModelInfo implements IYamlModelInfo {
	
	
	private final IYamlSourceElement sourceElement;
	
	
	YamlSourceUnitModelInfo(final AstInfo ast, final IYamlSourceElement unitElement,
			final Map<String, LabelAccess.Shared> labels) {
		super(ast);
		this.sourceElement= unitElement;
	}
	
	
	@Override
	public IYamlSourceElement getSourceElement() {
		return this.sourceElement;
	}
	
	
}
