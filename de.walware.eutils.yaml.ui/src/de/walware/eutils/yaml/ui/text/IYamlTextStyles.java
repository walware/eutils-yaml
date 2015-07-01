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

package de.walware.eutils.yaml.ui.text;

import de.walware.eutils.yaml.ui.YamlUI;


public class IYamlTextStyles {
	
	
	public static final String YAML_TEXTSTYLE_CONFIG_QUALIFIER= YamlUI.PLUGIN_ID + "/textstyle/Yaml"; //$NON-NLS-1$
	
	
	public static final String PREFIX= "yaml_ts_"; //$NON-NLS-1$
	
	public static final String TS_DEFAULT= PREFIX + "Default"; //$NON-NLS-1$
	
	public static final String TS_DOCUMENT_SEPARATOR= PREFIX + "DocumentSeparator"; //$NON-NLS-1$
	public static final String TS_DIRECTIVE= PREFIX + "Directive"; //$NON-NLS-1$
	
	public static final String TS_INDICATOR= PREFIX + "Indicator"; //$NON-NLS-1$
	public static final String TS_BRACKET= PREFIX + "Indicator.Bracket"; //$NON-NLS-1$
	
	public static final String TS_COMMENT= PREFIX + "Comment"; //$NON-NLS-1$
//	public static final String TS_TASK_TAG= PREFIX + "TaskTag"; //$NON-NLS-1$
	
	public static final String TS_KEY= PREFIX + "Key"; //$NON-NLS-1$
//	public static final String TS_QUOTED_KEY= PREFIX + "Key.Quoted"; //$NON-NLS-1$
	public static final String TS_TAG= PREFIX + "Tag"; //$NON-NLS-1$
//	public static final String TS_VALUE= PREFIX + "Scalar"; //$NON-NLS-1$
//	public static final String TS_QUOTED_VALUE= PREFIX + "Scalar.Quoted"; //$NON-NLS-1$
	
}
