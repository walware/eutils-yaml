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

import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_BOLD_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_COLOR_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_ITALIC_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_STRIKETHROUGH_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_UNDERLINE_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_USE_SUFFIX;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;

import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.TabAction;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.text.ui.settings.AssistPreferences;

import de.walware.workbench.ui.IWaThemeConstants;
import de.walware.workbench.ui.util.ThemeUtil;

import de.walware.eutils.yaml.ui.YamlUI;
import de.walware.eutils.yaml.ui.editors.YamlEditorBuild;
import de.walware.eutils.yaml.ui.sourceediting.YamlEditingSettings;
import de.walware.eutils.yaml.ui.text.IYamlTextStyles;


public class YamlUIPreferenceInitializer extends AbstractPreferenceInitializer {
	
	
	public YamlUIPreferenceInitializer() {
	}
	
	
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store= YamlUIPlugin.getInstance().getPreferenceStore();
		final IScopeContext scope= DefaultScope.INSTANCE;
		final IEclipsePreferences pref= scope.getNode(YamlUI.PLUGIN_ID);
		final ThemeUtil theme= new ThemeUtil();
		
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		
		pref.put(IYamlTextStyles.TS_DEFAULT + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_DEFAULT_COLOR));
		pref.putBoolean(IYamlTextStyles.TS_DEFAULT + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_DEFAULT + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_DEFAULT + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_DEFAULT + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(IYamlTextStyles.TS_DOCUMENT_SEPARATOR + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_UNDEFINED_COLOR));
		pref.putBoolean(IYamlTextStyles.TS_DOCUMENT_SEPARATOR + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_DOCUMENT_SEPARATOR + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_DOCUMENT_SEPARATOR + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_DOCUMENT_SEPARATOR + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(IYamlTextStyles.TS_DIRECTIVE + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_PREPROCESSOR_COLOR));
		pref.putBoolean(IYamlTextStyles.TS_DIRECTIVE + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_DIRECTIVE + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_DIRECTIVE + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_DIRECTIVE + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(IYamlTextStyles.TS_INDICATOR + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_OPERATOR_COLOR));
		pref.putBoolean(IYamlTextStyles.TS_INDICATOR + TEXTSTYLE_BOLD_SUFFIX, true);
		pref.putBoolean(IYamlTextStyles.TS_INDICATOR + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_INDICATOR + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_INDICATOR + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(IYamlTextStyles.TS_BRACKET + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_DEFAULT_COLOR));
		pref.putBoolean(IYamlTextStyles.TS_BRACKET + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_BRACKET + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_BRACKET + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_BRACKET + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		pref.put(IYamlTextStyles.TS_BRACKET + TEXTSTYLE_USE_SUFFIX, IYamlTextStyles.TS_INDICATOR);
		
		pref.put(IYamlTextStyles.TS_KEY + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_STRING_COLOR));
		pref.putBoolean(IYamlTextStyles.TS_KEY + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_KEY + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_KEY + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_KEY + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(IYamlTextStyles.TS_TAG + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_DOCU_TAG_COLOR));
		pref.putBoolean(IYamlTextStyles.TS_TAG + TEXTSTYLE_BOLD_SUFFIX, true);
		pref.putBoolean(IYamlTextStyles.TS_TAG + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_TAG + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_TAG + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(IYamlTextStyles.TS_COMMENT + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_COMMENT_COLOR));
		pref.putBoolean(IYamlTextStyles.TS_COMMENT + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_COMMENT + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_COMMENT + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(IYamlTextStyles.TS_COMMENT + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
//		pref.put(IYamlTextStyles.TS_TASK_TAG + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_COMMENT_TASKTAG_COLOR));
//		pref.putBoolean(IYamlTextStyles.TS_TASK_TAG + TEXTSTYLE_BOLD_SUFFIX, true);
//		pref.putBoolean(IYamlTextStyles.TS_TASK_TAG + TEXTSTYLE_ITALIC_SUFFIX, false);
//		pref.putBoolean(IYamlTextStyles.TS_TASK_TAG + TEXTSTYLE_UNDERLINE_SUFFIX, false);
//		pref.putBoolean(IYamlTextStyles.TS_TASK_TAG + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		{	final IEclipsePreferences node= scope.getNode(YamlEditingSettings.ASSIST_YAML_PREF_QUALIFIER);
			node.put(ContentAssistComputerRegistry.CIRCLING_ORDERED, "yaml-elements:false,templates:true,paths:true"); //$NON-NLS-1$
			node.put(ContentAssistComputerRegistry.DEFAULT_DISABLED, ""); //$NON-NLS-1$
		}
		// EditorPreferences
		{	final AssistPreferences assistPrefs= YamlEditingSettings.getAssistPrefences();
			PreferencesUtil.setPrefValue(scope, assistPrefs.getAutoActivationEnabledPref(), Boolean.TRUE);
			PreferencesUtil.setPrefValue(scope, assistPrefs.getAutoInsertSinglePref(), Boolean.FALSE);
			PreferencesUtil.setPrefValue(scope, assistPrefs.getAutoInsertPrefixPref(), Boolean.FALSE);
		}
		PreferencesUtil.setPrefValue(scope, YamlEditingSettings.FOLDING_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, YamlEditingSettings.FOLDING_RESTORE_STATE_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, YamlEditingSettings.MARKOCCURRENCES_ENABLED_PREF, Boolean.TRUE);
		
		PreferencesUtil.setPrefValue(scope, YamlEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, YamlEditingSettings.SMARTINSERT_TAB_ACTION_PREF, TabAction.INSERT_INDENT_LEVEL);
		PreferencesUtil.setPrefValue(scope, YamlEditingSettings.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, YamlEditingSettings.SMARTINSERT_CLOSEQUOTES_ENABLED_PREF, Boolean.TRUE);
		
		PreferencesUtil.setPrefValue(scope, YamlEditorBuild.PROBLEMCHECKING_ENABLED_PREF, Boolean.TRUE);
		
	}
	
}
