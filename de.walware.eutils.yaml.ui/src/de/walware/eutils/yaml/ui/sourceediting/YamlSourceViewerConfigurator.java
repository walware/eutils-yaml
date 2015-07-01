/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.ui.sourceediting;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfigurator;
import de.walware.ecommons.preferences.IPreferenceAccess;

import de.walware.eutils.yaml.core.IYamlCoreAccess;
import de.walware.eutils.yaml.core.YamlCodeStyleSettings;
import de.walware.eutils.yaml.core.YamlCore;
import de.walware.eutils.yaml.core.source.YamlDocumentSetupParticipant;


/**
 * Configurator for YAML code source viewers.
 */
public class YamlSourceViewerConfigurator extends SourceEditorViewerConfigurator
		implements IYamlCoreAccess, PropertyChangeListener {
	
	
	private static final Set<String> RESET_GROUP_IDS= new HashSet<>(Arrays.asList(new String[] {
			YamlCodeStyleSettings.INDENT_GROUP_ID,
//			TaskTagsPreferences.GROUP_ID,
	}));
	
	
	private IYamlCoreAccess sourceCoreAccess;
	
	private final YamlCodeStyleSettings yamlCodeStyleCopy;
	
	
	public YamlSourceViewerConfigurator(final IYamlCoreAccess coreAccess,
			final YamlSourceViewerConfiguration config) {
		super(config);
		this.yamlCodeStyleCopy= new YamlCodeStyleSettings(1);
		config.setCoreAccess(this);
		setSource(coreAccess);
		
		this.yamlCodeStyleCopy.load(this.sourceCoreAccess.getYamlCodeStyle());
		this.yamlCodeStyleCopy.resetDirty();
		this.yamlCodeStyleCopy.addPropertyChangeListener(this);
	}
	
	
	@Override
	public IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new YamlDocumentSetupParticipant();
	}
	
	@Override
	protected Set<String> getResetGroupIds() {
		return RESET_GROUP_IDS;
	}
	
	
	public void setSource(IYamlCoreAccess newAccess) {
		if (newAccess == null) {
			newAccess= YamlCore.getWorkbenchAccess();
		}
		if (this.sourceCoreAccess != newAccess) {
			this.sourceCoreAccess= newAccess;
			handleSettingsChanged(null, null);
		}
	}
	
	
	@Override
	public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
		super.handleSettingsChanged(groupIds, options);
		
		this.yamlCodeStyleCopy.resetDirty();
	}
	
	@Override
	protected void checkSettingsChanges(final Set<String> groupIds, final Map<String, Object> options) {
		super.checkSettingsChanges(groupIds, options);
		
		if (groupIds.contains(YamlCodeStyleSettings.INDENT_GROUP_ID)) {
			this.yamlCodeStyleCopy.load(this.sourceCoreAccess.getYamlCodeStyle());
		}
		if (groupIds.contains(YamlEditingSettings.EDITOR_OPTIONS_QUALIFIER)) {
			this.fUpdateCompleteConfig= true;
		}
	}
	
	
	@Override
	public IPreferenceAccess getPrefs() {
		return this.sourceCoreAccess.getPrefs();
	}
	
	@Override
	public YamlCodeStyleSettings getYamlCodeStyle() {
		return this.yamlCodeStyleCopy;
	}
	
}
