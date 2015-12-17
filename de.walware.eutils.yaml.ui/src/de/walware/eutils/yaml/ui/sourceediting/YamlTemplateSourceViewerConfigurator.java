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

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.source.ISourceViewer;

import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.templates.TemplateVariableProcessor;

import de.walware.eutils.yaml.core.IYamlCoreAccess;
import de.walware.eutils.yaml.core.source.YamlDocumentSetupParticipant;


public class YamlTemplateSourceViewerConfigurator extends YamlSourceViewerConfigurator {
	
	
	private static class YamlTemplateSourceViewerConfiguration extends YamlSourceViewerConfiguration {
		
		
		private final TemplateVariableProcessor processor;
		
		
		public YamlTemplateSourceViewerConfiguration(
				final TemplateVariableProcessor variableProcessor) {
			super();
			this.processor= variableProcessor;
		}
		
		
		@Override
		protected ContentAssist createContentAssistant(final ISourceViewer sourceViewer) {
			return createTemplateVariableContentAssistant(sourceViewer, this.processor);
		}
		
		@Override
		public int[] getConfiguredTextHoverStateMasks(final ISourceViewer sourceViewer, final String contentType) {
			return new int[] { ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK };
		}
		
		@Override
		public ITextHover getTextHover(final ISourceViewer sourceViewer, final String contentType, final int stateMask) {
			return new TemplateVariableTextHover(this.processor);
		}
		
	}
	
	
	public YamlTemplateSourceViewerConfigurator(final IYamlCoreAccess coreAccess,
			final TemplateVariableProcessor processor) {
		super(coreAccess, new YamlTemplateSourceViewerConfiguration(processor));
	}
	
	
	@Override
	public IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new YamlDocumentSetupParticipant();
	}
	
}
