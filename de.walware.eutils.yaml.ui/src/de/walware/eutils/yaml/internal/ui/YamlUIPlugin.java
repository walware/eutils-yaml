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

package de.walware.eutils.yaml.internal.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.ltk.ui.templates.WaContributionContextTypeRegistry;
import de.walware.ecommons.ltk.ui.util.CombinedPreferenceStore;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.text.ui.settings.TextStyleManager;
import de.walware.ecommons.ui.SharedUIResources;

import de.walware.eutils.yaml.core.YamlCore;
import de.walware.eutils.yaml.internal.ui.editors.YamlDocumentProvider;
import de.walware.eutils.yaml.ui.YamlUI;
import de.walware.eutils.yaml.ui.sourceediting.YamlEditingSettings;
import de.walware.eutils.yaml.ui.text.IYamlTextStyles;


public class YamlUIPlugin extends AbstractUIPlugin {
	
	
	/** The shared instance */
	private static YamlUIPlugin instance;
	
	/**
	 * Returns the shared plug-in instance
	 *
	 * @return the shared instance
	 */
	public static YamlUIPlugin getInstance() {
		return instance;
	}
	
	
	private boolean started;
	
	private final List<IDisposable> disposables= new ArrayList<>();
	
	private IPreferenceStore editorPreferenceStore;
	
	private YamlDocumentProvider yamlDocumentProvider;
	
	private TextStyleManager yamlTextStyles;
	
	private ContextTypeRegistry yamlEditorTemplateContextTypeRegistry;
	private TemplateStore yamlEditorTemplateStore;
	
	private ContentAssistComputerRegistry yamlEditorContentAssistRegistry;
	
	
	public YamlUIPlugin() {
	}
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		instance= this;
		
		this.started= true;
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			synchronized (this) {
				this.started= false;
				
				this.editorPreferenceStore= null;
				
				this.yamlDocumentProvider= null;
			}
			
			for (final IDisposable listener : this.disposables) {
				try {
					listener.dispose();
				}
				catch (final Throwable e) {
					getLog().log(new Status(IStatus.ERROR, YamlUI.PLUGIN_ID, ICommonStatusConstants.INTERNAL_PLUGGED_IN, "Error occured when dispose module", e)); 
				}
			}
			this.disposables.clear();
		}
		finally {
			instance= null;
			super.stop(context);
		}
	}
	
	
	public void addStoppingListener(final IDisposable listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.disposables.add(listener);
		}
	}
	
	
//	@Override
//	protected void initializeImageRegistry(final ImageRegistry reg) {
//		if (!this.started) {
//			throw new IllegalStateException("Plug-in is not started.");
//		}
//		final ImageRegistryUtil util= new ImageRegistryUtil(this);
//		
//		util.register(YamlUIResources.OBJ_LABEL_IMAGE_ID, ImageRegistryUtil.T_OBJ, "label.png");
//	}
	
	
	public synchronized IPreferenceStore getEditorPreferenceStore() {
		if (this.editorPreferenceStore == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.editorPreferenceStore= CombinedPreferenceStore.createStore(
					getPreferenceStore(),
					EditorsUI.getPreferenceStore() );
		}
		return this.editorPreferenceStore;
	}
	
	public synchronized YamlDocumentProvider getYamlDocumentProvider() {
		if (this.yamlDocumentProvider == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.yamlDocumentProvider= new YamlDocumentProvider();
			this.disposables.add(this.yamlDocumentProvider);
		}
		return this.yamlDocumentProvider;
	}
	
	public synchronized TextStyleManager getYamlTextStyles() {
		if (this.yamlTextStyles == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.yamlTextStyles= new TextStyleManager(SharedUIResources.getColors(),
					getPreferenceStore(),
					IYamlTextStyles.YAML_TEXTSTYLE_CONFIG_QUALIFIER );
			PreferencesUtil.getSettingsChangeNotifier().addManageListener(this.yamlTextStyles);
		}
		return this.yamlTextStyles;
	}
	
	public synchronized ContextTypeRegistry getYamlEditorTemplateContextTypeRegistry() {
		if (this.yamlEditorTemplateContextTypeRegistry == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.yamlEditorTemplateContextTypeRegistry= new WaContributionContextTypeRegistry(
					"de.walware.eutils.yaml.templates.YamlEditor" ); //$NON-NLS-1$;
		}
		return this.yamlEditorTemplateContextTypeRegistry;
	}
	
	public synchronized TemplateStore getYamlEditorTemplateStore() {
		if (this.yamlEditorTemplateStore == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.yamlEditorTemplateStore= new ContributionTemplateStore(
					getYamlEditorTemplateContextTypeRegistry(), getPreferenceStore(),
					"editor/assist/Yaml/EditorTemplates.store" );
			try {
				this.yamlEditorTemplateStore.load();
			}
			catch (final IOException e) {
				getLog().log(new Status(IStatus.ERROR, YamlUI.PLUGIN_ID, ICommonStatusConstants.IO_ERROR,
						"An error occured when loading 'YAML Editor' template store.", e)); 
			}
		}
		return this.yamlEditorTemplateStore;
	}
	
	public synchronized ContentAssistComputerRegistry getYamlEditorContentAssistRegistry() {
		if (this.yamlEditorContentAssistRegistry == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.yamlEditorContentAssistRegistry= new ContentAssistComputerRegistry(
					YamlCore.YAML_CONTENT_ID, 
					YamlEditingSettings.ASSIST_YAML_PREF_QUALIFIER ); 
			this.disposables.add(this.yamlEditorContentAssistRegistry);
		}
		return this.yamlEditorContentAssistRegistry;
	}
	
}
