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

import java.util.List;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1OutlinePage;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfigurator;

import de.walware.eutils.yaml.core.YamlCore;
import de.walware.eutils.yaml.core.model.YamlModel;
import de.walware.eutils.yaml.core.source.YamlDocumentContentInfo;
import de.walware.eutils.yaml.internal.ui.YamlUIPlugin;
import de.walware.eutils.yaml.ui.sourceediting.YamlEditingSettings;
import de.walware.eutils.yaml.ui.sourceediting.YamlSourceViewerConfiguration;
import de.walware.eutils.yaml.ui.sourceediting.YamlSourceViewerConfigurator;


public class YamlEditor extends SourceEditor1 {
	
	
	private YamlSourceViewerConfigurator yamlConfig;
	
	
	public YamlEditor() {
		super(YamlCore.YAML_CONTENT_TYPE);
	}
	
	
	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		
		setEditorContextMenuId("de.walware.eutils.yaml.menus.YamlEditorContextMenu"); //$NON-NLS-1$
	}
	
	@Override
	protected SourceEditorViewerConfigurator createConfiguration() {
		setDocumentProvider(YamlUIPlugin.getInstance().getYamlDocumentProvider());
		
		enableStructuralFeatures(YamlModel.getYamlModelManager(),
				YamlEditingSettings.FOLDING_ENABLED_PREF,
				YamlEditingSettings.MARKOCCURRENCES_ENABLED_PREF );
		
		this.yamlConfig= new YamlSourceViewerConfigurator(null,
				new YamlSourceViewerConfiguration(YamlDocumentContentInfo.INSTANCE, this,
						null, null, null ));
		return this.yamlConfig;
	}
	
	
//	@Override
//	protected ISourceEditorAddon createCodeFoldingProvider() {
//		return new FoldingEditorAddon(new YamlDefaultFoldingProvider());
//	}
	
	
	@Override
	protected void handlePreferenceStoreChanged(final org.eclipse.jface.util.PropertyChangeEvent event) {
		if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH.equals(event.getProperty())
				|| AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS.equals(event.getProperty())) {
			return;
		}
		super.handlePreferenceStoreChanged(event);
	}
	
	
	@Override
	protected boolean isTabsToSpacesConversionEnabled() {
		return false;
	}
	
	
	@Override
	protected void collectContextMenuPreferencePages(final List<String> pageIds) {
		super.collectContextMenuPreferencePages(pageIds);
		pageIds.add("de.walware.eutils.yaml.preferencePages.YamlEditor"); //$NON-NLS-1$
		pageIds.add("de.walware.eutils.yaml.preferencePages.YamlTextStyles"); //$NON-NLS-1$
		pageIds.add("de.walware.eutils.yaml.preferencePages.YamlEditorTemplates"); //$NON-NLS-1$
		pageIds.add("de.walware.eutils.yaml.preferencePages.YamlCodeStyle"); //$NON-NLS-1$
	}
	
	@Override
	protected SourceEditor1OutlinePage createOutlinePage() {
		return new YamlOutlinePage(this);
	}
	
	
	@Override
	public String[] getShowInTargetIds() {
		return new String[] { IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.ID_OUTLINE };
	}
	
}
