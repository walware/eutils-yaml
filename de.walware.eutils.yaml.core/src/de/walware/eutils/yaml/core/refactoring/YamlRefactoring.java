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

package de.walware.eutils.yaml.core.refactoring;

import de.walware.ecommons.ltk.core.refactoring.CommonRefactoringFactory;


public class YamlRefactoring {
	
	
	private static final CommonRefactoringFactory YAML_FACTORY= new YamlRefactoringFactory();
	
	public static CommonRefactoringFactory getYamlFactory() {
		return YAML_FACTORY;
	}
	
	
	public static final String DELETE_YAML_ELEMENTS_REFACTORING_ID= "de.walware.eutils.yaml.refactoring.DeleteYamlElementsOperation"; //$NON-NLS-1$
	
	public static final String MOVE_YAML_ELEMENTS_REFACTORING_ID= "de.walware.eutils.yaml.refactoring.MoveYamlElementsOperation"; //$NON-NLS-1$
	
	public static final String COPY_YAML_ELEMENTS_REFACTORING_ID= "de.walware.eutils.yaml.refactoring.CopyYamlElementsOperation"; //$NON-NLS-1$
	
	public static final String PASTE_YAML_CODE_REFACTORING_ID= "de.walware.eutils.yaml.refactoring.PasteYamlCodeOperation"; //$NON-NLS-1$
	
	
	public static final String DELETE_YAML_ELEMENTS_PROCESSOR_ID= "de.walware.eutils.yaml.refactoring.DeleteYamlElementsProcessor"; //$NON-NLS-1$
	
	public static final String MOVE_YAML_ELEMENTS_PROCESSOR_ID= "de.walware.eutils.yaml.refactoring.MoveYamlElementsProcessor"; //$NON-NLS-1$
	
	public static final String COPY_YAML_ELEMENTS_PROCESSOR_ID= "de.walware.eutils.yaml.refactoring.CopyYamlElementsProcessor"; //$NON-NLS-1$
	
	public static final String PASTE_YAML_CODE_PROCESSOR_ID= "de.walware.eutils.yaml.refactoring.PasteYamlCodeProcessor"; //$NON-NLS-1$
	
}
