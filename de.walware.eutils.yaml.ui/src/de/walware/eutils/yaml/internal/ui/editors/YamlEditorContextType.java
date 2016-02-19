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

package de.walware.eutils.yaml.internal.ui.editors;

import de.walware.ecommons.ltk.ui.templates.SourceEditorContextType;


public class YamlEditorContextType extends SourceEditorContextType {
	
	
	//-- Context Types --// (starts with TEMPLATES_ID)
	
	public static final String DEFAULT_CONTEXT_TYPE_ID= "de.walware.eutils.yaml.templates.YamlEditorDefaultContextType"; //$NON-NLS-1$
	
	
	public YamlEditorContextType() {
		super();
		
		addCommonVariables();
		addEditorVariables();
	}
	
}
