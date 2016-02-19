/*=============================================================================#
 # Copyright (c) 2005-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.ui.config;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.templates.TemplateContextType;

import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfigurator;
import de.walware.ecommons.ltk.ui.templates.AbstractTemplatePreferencePage;
import de.walware.ecommons.templates.TemplateVariableProcessor;
import de.walware.ecommons.text.core.treepartitioner.TreePartitioner;

import de.walware.eutils.yaml.core.source.YamlPartitionNodeType;
import de.walware.eutils.yaml.internal.ui.YamlUIPlugin;
import de.walware.eutils.yaml.ui.sourceediting.YamlTemplateSourceViewerConfigurator;


public class YamlEditorTemplatesPreferencePage extends AbstractTemplatePreferencePage {
	
	
	public YamlEditorTemplatesPreferencePage() {
		setPreferenceStore(YamlUIPlugin.getInstance().getPreferenceStore());
		setTemplateStore(YamlUIPlugin.getInstance().getYamlEditorTemplateStore());
		setContextTypeRegistry(YamlUIPlugin.getInstance().getYamlEditorTemplateContextTypeRegistry());
	}
	
	@Override
	protected SourceEditorViewerConfigurator createSourceViewerConfigurator(
			final TemplateVariableProcessor templateProcessor) {
		return new YamlTemplateSourceViewerConfigurator(null, templateProcessor);
	}
	
	
	@Override
	protected void configureContext(final AbstractDocument document,
			final TemplateContextType contextType, final SourceEditorViewerConfigurator configurator) {
		final String partitioning= configurator.getDocumentContentInfo().getPartitioning();
		final TreePartitioner partitioner= (TreePartitioner) document.getDocumentPartitioner(partitioning);
//		if (contextType.getId().equals(YamlEditorContextType.)) {
//			partitioner.setStartType(YamlPartitionNodeType.);
//		}
//		else {
			partitioner.setStartType(YamlPartitionNodeType.DEFAULT_ROOT);
//		}
		partitioner.disconnect();
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioning, partitioner);
	}
	
}
