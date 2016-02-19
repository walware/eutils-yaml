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

import org.eclipse.ui.editors.text.IFoldingCommandIds;

import de.walware.ecommons.ui.actions.TogglePreferenceEnablementHandler;

import de.walware.eutils.yaml.ui.sourceediting.YamlEditingSettings;


/**
 * Toggles enablement of code folding in YAML editors.
 */
public class YamlToggleFoldingHandler extends TogglePreferenceEnablementHandler {
	
	
	public YamlToggleFoldingHandler() {
		super(YamlEditingSettings.FOLDING_ENABLED_PREF, IFoldingCommandIds.FOLDING_TOGGLE);
	}
	
}
