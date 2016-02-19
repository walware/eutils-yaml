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

package de.walware.eutils.yaml.core.refactoring;

import de.walware.ecommons.ltk.core.ElementSet;
import de.walware.ecommons.ltk.core.refactoring.CommonDeleteProcessor;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;


public class DeleteYamlProcessor extends CommonDeleteProcessor {
	
	
	public DeleteYamlProcessor(final ElementSet elements, final RefactoringAdapter adapter) {
		super(elements, adapter);
	}
	
	
	@Override
	public String getIdentifier() {
		return YamlRefactoring.DELETE_YAML_ELEMENTS_PROCESSOR_ID;
	}
	
	@Override
	protected String getRefactoringIdentifier() {
		return YamlRefactoring.DELETE_YAML_ELEMENTS_REFACTORING_ID;
	}
	
}
