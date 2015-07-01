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

package de.walware.eutils.yaml.core.refactoring;

import de.walware.ecommons.ltk.core.refactoring.CommonPasteCodeProcessor;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;
import de.walware.ecommons.ltk.core.refactoring.RefactoringDestination;


public class PasteYamlCodeProcessor extends CommonPasteCodeProcessor {
	
	
	public PasteYamlCodeProcessor(final String code,
			final RefactoringDestination destination, final RefactoringAdapter adapter) {
		super(code, destination, adapter);
	}
	
	
	@Override
	public String getIdentifier() {
		return YamlRefactoring.PASTE_YAML_CODE_PROCESSOR_ID;
	}
	
	@Override
	protected String getRefactoringIdentifier() {
		return YamlRefactoring.PASTE_YAML_CODE_REFACTORING_ID;
	}
	
}
