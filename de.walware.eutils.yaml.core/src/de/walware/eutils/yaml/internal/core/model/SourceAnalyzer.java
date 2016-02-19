/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.core.model;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.core.model.ISourceUnit;

import de.walware.eutils.yaml.core.ast.YamlAstNode;


public class SourceAnalyzer {
	
	
	public YamlSourceUnitModelInfo createModel(final ISourceUnit sourceUnit, final String text, final AstInfo ast) {
		final YamlSourceElement unitElement= new YamlSourceElement.SourceContainer(
				sourceUnit, (YamlAstNode) ast.root);
		
		final YamlSourceUnitModelInfo modelInfo= new YamlSourceUnitModelInfo(ast, unitElement, null);
		return modelInfo;
	}
	
}
