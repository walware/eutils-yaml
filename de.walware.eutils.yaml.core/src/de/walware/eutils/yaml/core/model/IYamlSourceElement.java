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

package de.walware.eutils.yaml.core.model;

import java.util.List;

import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;


public interface IYamlSourceElement extends IYamlElement, ISourceStructElement {
	
	
	@Override
	IYamlElement getModelParent();
	
	
	@Override
	boolean hasSourceChildren(IModelElement.Filter filter);
	@Override
	List<? extends IYamlSourceElement> getSourceChildren(IModelElement.Filter filter);
	
}
