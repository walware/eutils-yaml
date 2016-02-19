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

package de.walware.eutils.yaml.internal.ui.config;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;

import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfiguration;
import de.walware.ecommons.ltk.ui.util.CombinedPreferenceStore;
import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;
import de.walware.ecommons.text.ui.presentation.AbstractTextStylesConfigurationBlock;
import de.walware.ecommons.text.ui.settings.TextStyleManager;

import de.walware.eutils.yaml.core.YamlCore;
import de.walware.eutils.yaml.core.source.YamlDocumentContentInfo;
import de.walware.eutils.yaml.core.source.YamlDocumentSetupParticipant;
import de.walware.eutils.yaml.internal.ui.YamlUIPlugin;
import de.walware.eutils.yaml.ui.sourceediting.YamlSourceViewerConfiguration;
import de.walware.eutils.yaml.ui.text.IYamlTextStyles;


public class YamlTextStylesPreferencePage extends ConfigurationBlockPreferencePage {
	
	
	public YamlTextStylesPreferencePage() {
		setPreferenceStore(YamlUIPlugin.getInstance().getPreferenceStore());
	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() throws CoreException {
		return new YamlTextStylesBlock();
	}
	
}


class YamlTextStylesBlock extends AbstractTextStylesConfigurationBlock {
	
	
	public YamlTextStylesBlock() {
	}
	
	
	@Override
	protected String getSettingsGroup() {
		return IYamlTextStyles.YAML_TEXTSTYLE_CONFIG_QUALIFIER;
	}
	
	@Override
	protected SyntaxNode[] createItems() {
		return new SyntaxNode[] {
			new CategoryNode(Messages.TextStyles_DefaultCodeCategory_label, new SyntaxNode[] {
				new StyleNode(Messages.TextStyles_Default_label, Messages.TextStyles_Default_description,
						IYamlTextStyles.TS_DEFAULT, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
				new StyleNode(Messages.TextStyles_Indicators_label, Messages.TextStyles_Indicators_description,
						IYamlTextStyles.TS_INDICATOR, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, new StyleNode[] {
					new StyleNode(Messages.TextStyles_SeqMapBrackets_label, Messages.TextStyles_SeqMapBrackets_description,
							IYamlTextStyles.TS_BRACKET, new SyntaxNode.UseStyle[] {
								SyntaxNode.createUseNoExtraStyle(IYamlTextStyles.TS_INDICATOR),
								SyntaxNode.createUseCustomStyle()
							}, null ),
				}),
				new StyleNode(Messages.TextStyles_Keys_label, Messages.TextStyles_Keys_description,
						IYamlTextStyles.TS_KEY, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle(),
							SyntaxNode.createUseOtherStyle(IYamlTextStyles.TS_DEFAULT, Messages.TextStyles_Default_label),
						}, null ),
				new StyleNode(Messages.TextStyles_Tags_label, Messages.TextStyles_Tags_description,
						IYamlTextStyles.TS_TAG, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle(),
							SyntaxNode.createUseOtherStyle(IYamlTextStyles.TS_DEFAULT, Messages.TextStyles_Default_label),
						}, null ),
			}),
			new CategoryNode(Messages.TextStyles_ProcessorCategory_label, new SyntaxNode[] {
				new StyleNode(Messages.TextStyles_DocumentSeparators_label, Messages.TextStyles_DocumentSeparators_description,
						IYamlTextStyles.TS_DOCUMENT_SEPARATOR, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
				new StyleNode(Messages.TextStyles_Directives_label, Messages.TextStyles_Directives_description,
						IYamlTextStyles.TS_DIRECTIVE, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
			}),
			new CategoryNode(Messages.TextStyles_CommentCategory_label, new SyntaxNode[] {
				new StyleNode(Messages.TextStyles_Comment_label, Messages.TextStyles_Comment_description,
						IYamlTextStyles.TS_COMMENT, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
//				new StyleNode(Messages.TextStyles_TaskTag_label, Messages.TextStyles_TaskTag_description,
//						IYamlTextStyles.TS_TASK_TAG, new SyntaxNode.UseStyle[] {
//							SyntaxNode.createUseCustomStyle()
//						}, null ),
			}),
		};
	}
	
	@Override
	protected String getPreviewFileName() {
		return "YamlTextStylesPreviewCode.txt"; //$NON-NLS-1$
	}
	
	@Override
	protected IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new YamlDocumentSetupParticipant();
	}
	
	@Override
	protected SourceEditorViewerConfiguration getSourceEditorViewerConfiguration(
			final IPreferenceStore preferenceStore, final TextStyleManager textStyles) {
		return new YamlSourceViewerConfiguration(YamlDocumentContentInfo.INSTANCE, null,
				YamlCore.getDefaultsAccess(),
				CombinedPreferenceStore.createStore(
						preferenceStore,
						EditorsUI.getPreferenceStore() ),
				textStyles );
	}
	
}
