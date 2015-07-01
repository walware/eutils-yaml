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

package de.walware.eutils.yaml.internal.ui.editors;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.source.IAnnotationModel;

import de.walware.ecommons.IDisposable;
import de.walware.ecommons.ltk.IProblem;
import de.walware.ecommons.ltk.LTK;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.ui.sourceediting.SourceAnnotationModel;
import de.walware.ecommons.ltk.ui.sourceediting.SourceDocumentProvider;
import de.walware.ecommons.ltk.ui.sourceediting.SourceProblemAnnotation;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.preferences.SettingsChangeNotifier;
import de.walware.ecommons.preferences.SettingsChangeNotifier.ChangeListener;

import de.walware.eutils.yaml.core.model.YamlModel;
import de.walware.eutils.yaml.core.source.YamlDocumentSetupParticipant;
import de.walware.eutils.yaml.ui.editors.YamlEditorBuild;


public class YamlDocumentProvider extends SourceDocumentProvider<ISourceUnit>
		implements IDisposable {
	
	
	private class ThisAnnotationModel extends SourceAnnotationModel {
		
		public ThisAnnotationModel(final IResource resource) {
			super(resource);
		}
		
		@Override
		protected boolean isHandlingTemporaryProblems() {
			return YamlDocumentProvider.this.handleTemporaryProblems;
		}
		
		@Override
		protected SourceProblemAnnotation createAnnotation(final IProblem problem) {
			if (problem.getCategoryId() == YamlModel.YAML_TYPE_ID) {
				switch (problem.getSeverity()) {
				case IProblem.SEVERITY_ERROR:
					return new SourceProblemAnnotation(YamlEditorBuild.ERROR_ANNOTATION_TYPE, problem,
							SourceProblemAnnotation.ERROR_CONFIG );
				case IProblem.SEVERITY_WARNING:
					return new SourceProblemAnnotation(YamlEditorBuild.WARNING_ANNOTATION_TYPE, problem,
							SourceProblemAnnotation.WARNING_CONFIG );
				default:
					return new SourceProblemAnnotation(YamlEditorBuild.INFO_ANNOTATION_TYPE, problem,
							SourceProblemAnnotation.INFO_CONFIG );
				}
			}
			return null;
		}
		
	}
	
	
	private ChangeListener editorPrefListener;
	
	private boolean handleTemporaryProblems;
	
	
	public YamlDocumentProvider() {
		super(YamlModel.YAML_TYPE_ID, new YamlDocumentSetupParticipant());
		
		this.editorPrefListener= new SettingsChangeNotifier.ChangeListener() {
			@Override
			public void settingsChanged(final Set<String> groupIds) {
				if (groupIds.contains(YamlEditorBuild.GROUP_ID)) {
					updateEditorPrefs();
				}
			}
		};
		PreferencesUtil.getSettingsChangeNotifier().addChangeListener(this.editorPrefListener);
		final IPreferenceAccess access= PreferencesUtil.getInstancePrefs();
		this.handleTemporaryProblems= access.getPreferenceValue(YamlEditorBuild.PROBLEMCHECKING_ENABLED_PREF);
	}
	
	
	@Override
	public void dispose() {
		if (this.editorPrefListener != null) {
			PreferencesUtil.getSettingsChangeNotifier().removeChangeListener(this.editorPrefListener);
			this.editorPrefListener= null;
		}
	}
	
	private void updateEditorPrefs() {
		final IPreferenceAccess access= PreferencesUtil.getInstancePrefs();
		final boolean newHandleTemporaryProblems= access.getPreferenceValue(YamlEditorBuild.PROBLEMCHECKING_ENABLED_PREF);
		if (this.handleTemporaryProblems != newHandleTemporaryProblems) {
			this.handleTemporaryProblems= newHandleTemporaryProblems;
			if (this.handleTemporaryProblems) {
				YamlModel.getYamlModelManager().refresh(LTK.EDITOR_CONTEXT);
			}
			else {
				final List<? extends ISourceUnit> sus= LTK.getSourceUnitManager().getOpenSourceUnits(
						YamlModel.YAML_TYPE_ID, LTK.EDITOR_CONTEXT );
				for (final ISourceUnit su : sus) {
					final IAnnotationModel model= getAnnotationModel(su);
					if (model instanceof ThisAnnotationModel) {
						((ThisAnnotationModel) model).clearProblems(null);
					}
				}
			}
		}
	}
	
	@Override
	protected IAnnotationModel createAnnotationModel(final IFile file) {
		return new ThisAnnotationModel(file);
	}
	
}
