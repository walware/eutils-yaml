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

package de.walware.eutils.yaml.ui.sourceediting;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

import de.walware.ecommons.ltk.ui.LTKUIPreferences;
import de.walware.ecommons.ltk.ui.sourceediting.EcoReconciler2;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditorAddon;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewer;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfiguration;
import de.walware.ecommons.ltk.ui.sourceediting.SourceUnitReconcilingStrategy;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistProcessor;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.text.ICharPairMatcher;
import de.walware.ecommons.text.IIndentSettings;
import de.walware.ecommons.text.core.sections.IDocContentSections;
import de.walware.ecommons.text.ui.presentation.SingleTokenScanner;
import de.walware.ecommons.text.ui.settings.TextStyleManager;

import de.walware.eutils.yaml.core.IYamlCoreAccess;
import de.walware.eutils.yaml.core.YamlCore;
import de.walware.eutils.yaml.core.source.IYamlDocumentConstants;
import de.walware.eutils.yaml.core.source.YamlBracketPairMatcher;
import de.walware.eutils.yaml.core.source.YamlDocumentContentInfo;
import de.walware.eutils.yaml.core.source.YamlHeuristicTokenScanner;
import de.walware.eutils.yaml.internal.ui.YamlUIPlugin;
import de.walware.eutils.yaml.internal.ui.sourceediting.YamlAutoEditStrategy;
import de.walware.eutils.yaml.internal.ui.sourceediting.YamlQuickOutlineInformationProvider;
import de.walware.eutils.yaml.ui.text.IYamlTextStyles;
import de.walware.eutils.yaml.ui.text.YamlDefaultTextStyleScanner;
import de.walware.eutils.yaml.ui.text.YamlDoubleClickStrategy;


/**
 * Configuration for YAML source editors.
 */
public class YamlSourceViewerConfiguration extends SourceEditorViewerConfiguration {
	
	
	private static final String[] CONTENT_TYPES= IYamlDocumentConstants.YAML_CONTENT_TYPES.toArray(
			new String[IYamlDocumentConstants.YAML_CONTENT_TYPES.size()] );
	
	
	protected ITextDoubleClickStrategy doubleClickStrategy;
	
	private YamlAutoEditStrategy autoEditStrategy;
	
	private IYamlCoreAccess coreAccess;
	
	
	public YamlSourceViewerConfiguration() {
		this(YamlDocumentContentInfo.INSTANCE, null, null, null, null);
	}
	
	public YamlSourceViewerConfiguration(final IDocContentSections documentContentInfo,
			final ISourceEditor editor,
			final IYamlCoreAccess access,
			final IPreferenceStore preferenceStore, final TextStyleManager textStyles) {
		super(documentContentInfo, editor);
		setCoreAccess(access);
		
		setup((preferenceStore != null) ? preferenceStore : YamlUIPlugin.getInstance().getEditorPreferenceStore(),
				LTKUIPreferences.getEditorDecorationPreferences(),
				YamlEditingSettings.getAssistPrefences() );
		setTextStyles(textStyles);
	}
	
	protected void setCoreAccess(final IYamlCoreAccess access) {
		this.coreAccess= (access != null) ? access : YamlCore.getWorkbenchAccess();
	}
	
	
	@Override
	protected void initTextStyles() {
		setTextStyles(YamlUIPlugin.getInstance().getYamlTextStyles());
	}
	
	@Override
	protected void initScanners() {
		final TextStyleManager textStyles= getTextStyles();
		
		addScanner(IYamlDocumentConstants.YAML_DEFAULT_CONTENT_TYPE,
				new YamlDefaultTextStyleScanner(textStyles) );
		addScanner(IYamlDocumentConstants.YAML_DIRECTIVE_CONTENT_TYPE,
				new SingleTokenScanner(textStyles, IYamlTextStyles.TS_DIRECTIVE) );
		addScanner(IYamlDocumentConstants.YAML_COMMENT_CONTENT_TYPE,
				new SingleTokenScanner(textStyles, IYamlTextStyles.TS_COMMENT) );
		addScanner(IYamlDocumentConstants.YAML_KEY_CONTENT_TYPE,
				new SingleTokenScanner(textStyles, IYamlTextStyles.TS_KEY) );
		addScanner(IYamlDocumentConstants.YAML_TAG_CONTENT_TYPE,
				new SingleTokenScanner(textStyles, IYamlTextStyles.TS_TAG) );
		addScanner(IYamlDocumentConstants.YAML_VALUE_CONTENT_TYPE,
				new SingleTokenScanner(textStyles, IYamlTextStyles.TS_DEFAULT) );
	}
	
	
	@Override
	public List<ISourceEditorAddon> getAddOns() {
		final List<ISourceEditorAddon> addons= super.getAddOns();
		if (this.autoEditStrategy != null) {
			addons.add(this.autoEditStrategy);
		}
		return addons;
	}
	
	@Override
	public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
		super.handleSettingsChanged(groupIds, options);
		if (this.autoEditStrategy != null) {
			this.autoEditStrategy.getSettings().handleSettingsChanged(groupIds, options);
		}
	}
	
	
	@Override
	public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer) {
		return CONTENT_TYPES;
	}
	
	
	@Override
	public ICharPairMatcher createPairMatcher() {
		return new YamlBracketPairMatcher(getDocumentContentInfo());
	}
	
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(final ISourceViewer sourceViewer, final String contentType) {
		if (this.doubleClickStrategy == null) {
			this.doubleClickStrategy= new YamlDoubleClickStrategy(
					YamlHeuristicTokenScanner.create(getDocumentContentInfo()) );
		}
		return this.doubleClickStrategy;
	}
	
	
	@Override
	protected IIndentSettings getIndentSettings() {
		return this.coreAccess.getYamlCodeStyle();
	}
	
	@Override
	public String[] getDefaultPrefixes(final ISourceViewer sourceViewer, final String contentType) {
		return new String[] { "#", "" }; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	@Override
	public boolean isSmartInsertSupported() {
		return true;
	}
	
	@Override
	public boolean isSmartInsertByDefault() {
		return PreferencesUtil.getInstancePrefs().getPreferenceValue(
				YamlEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF );
	}
	
	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(final ISourceViewer sourceViewer, final String contentType) {
		if (getSourceEditor() == null) {
			return super.getAutoEditStrategies(sourceViewer, contentType);
		}
		if (this.autoEditStrategy == null) {
			this.autoEditStrategy= createYamlAutoEditStrategy();
		}
		return new IAutoEditStrategy[] { this.autoEditStrategy };
	}
	
	protected YamlAutoEditStrategy createYamlAutoEditStrategy() {
		return new YamlAutoEditStrategy(this.coreAccess, getSourceEditor());
	}
	
	
	@Override
	public IReconciler getReconciler(final ISourceViewer sourceViewer) {
		final ISourceEditor editor= getSourceEditor();
		if (!(editor instanceof SourceEditor1)) {
			return null;
		}
		final EcoReconciler2 reconciler= new EcoReconciler2(editor);
		reconciler.setDelay(500);
		reconciler.addReconcilingStrategy(new SourceUnitReconcilingStrategy());
		
//		final IReconcilingStrategy spellingStrategy= getSpellingStrategy(sourceViewer);
//		if (spellingStrategy != null) {
//			reconciler.addReconcilingStrategy(spellingStrategy);
//		}
		
		return reconciler;
	}
	
	
	@Override
	public void initContentAssist(final ContentAssist assistant) {
		final ContentAssistComputerRegistry registry= YamlUIPlugin.getInstance().getYamlEditorContentAssistRegistry();
		
		{	final ContentAssistProcessor processor= new ContentAssistProcessor(assistant,
					IYamlDocumentConstants.YAML_DEFAULT_CONTENT_TYPE, registry, getSourceEditor());
			assistant.setContentAssistProcessor(processor, IYamlDocumentConstants.YAML_DEFAULT_CONTENT_TYPE);
		}
	}
	
	@Override
	protected void collectHyperlinkDetectorTargets(final Map<String, IAdaptable> targets,
			final ISourceViewer sourceViewer) {
		targets.put("de.walware.eutils.yaml.editorHyperlinks.YamlEditorTarget", getSourceEditor()); //$NON-NLS-1$
	}
	
	
	@Override
	protected IInformationProvider getQuickInformationProvider(final ISourceViewer sourceViewer,
			final int operation) {
		final ISourceEditor editor= getSourceEditor();
		if (editor == null) {
			return null;
		}
		switch (operation) {
		case SourceEditorViewer.SHOW_SOURCE_OUTLINE:
			return new YamlQuickOutlineInformationProvider(editor, operation);
		default:
			return null;
		}
	}
	
}
