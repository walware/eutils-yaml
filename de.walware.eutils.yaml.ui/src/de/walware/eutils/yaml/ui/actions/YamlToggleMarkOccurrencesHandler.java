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

package de.walware.eutils.yaml.ui.actions;

import de.walware.ecommons.ui.actions.TogglePreferenceEnablementHandler;

import de.walware.eutils.yaml.ui.sourceediting.YamlEditingSettings;


/**
 * Toggles enablement of mark occurrences in YAML editors.
 */
public class YamlToggleMarkOccurrencesHandler extends TogglePreferenceEnablementHandler {
	
	
	public YamlToggleMarkOccurrencesHandler() {
		super(	YamlEditingSettings.MARKOCCURRENCES_ENABLED_PREF,
				"org.eclipse.jdt.ui.edit.text.java.toggleMarkOccurrences"); //$NON-NLS-1$
	}
	
}
