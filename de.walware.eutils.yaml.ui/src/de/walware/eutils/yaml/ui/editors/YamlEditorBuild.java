/*=============================================================================#
 # Copyright (c) 2011-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.ui.editors;

import de.walware.ecommons.preferences.core.Preference.BooleanPref;

import de.walware.eutils.yaml.ui.sourceediting.YamlEditingSettings;


public class YamlEditorBuild {
	
	
	public static final String GROUP_ID= "Yaml/editor/build"; //$NON-NLS-1$
	
	
	public static final BooleanPref PROBLEMCHECKING_ENABLED_PREF= new BooleanPref(
			YamlEditingSettings.EDITOR_OPTIONS_QUALIFIER, "ProblemChecking.enabled"); //$NON-NLS-1$
	
	
	public static final String ERROR_ANNOTATION_TYPE= "de.walware.eutils.yaml.ui.error"; //$NON-NLS-1$
	public static final String WARNING_ANNOTATION_TYPE= "de.walware.eutils.yaml.ui.warning"; //$NON-NLS-1$
	public static final String INFO_ANNOTATION_TYPE= "de.walware.eutils.yaml.ui.info"; //$NON-NLS-1$
	
}
