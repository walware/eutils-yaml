/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.core;

import de.walware.ecommons.preferences.core.IPreferenceAccess;


public class YamlCoreAccess implements IYamlCoreAccess {
	
	
	private final IPreferenceAccess prefs;
	
	private final YamlCodeStyleSettings codeStyle;
	
	
	public YamlCoreAccess(final IPreferenceAccess prefs) {
		this.prefs= prefs;
		this.codeStyle= new YamlCodeStyleSettings(1);
	}
	
	
	@Override
	public IPreferenceAccess getPrefs() {
		return this.prefs;
	}
	
	@Override
	public YamlCodeStyleSettings getYamlCodeStyle() {
		return this.codeStyle;
	}
	
}
