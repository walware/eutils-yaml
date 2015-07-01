/*=============================================================================#
 # Copyright (c) 2008-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.ui.editors;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.templates.TemplateContextType;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.TemplatesCompletionComputer;

import de.walware.eutils.yaml.internal.ui.YamlUIPlugin;


public class YamlEditorTemplatesCompletionComputer extends TemplatesCompletionComputer {
	
	
	public YamlEditorTemplatesCompletionComputer() {
		super(YamlUIPlugin.getInstance().getYamlEditorTemplateStore(),
				YamlUIPlugin.getInstance().getYamlEditorTemplateContextTypeRegistry() );
	}
	
	
	@Override
	protected TemplateContextType getContextType(final AssistInvocationContext context, final IRegion region) {
		try {
			final ISourceEditor editor= context.getEditor();
			final AbstractDocument document= (AbstractDocument) context.getSourceViewer().getDocument();
			final ITypedRegion partition= document.getPartition(
					editor.getDocumentContentInfo().getPartitioning(), region.getOffset(), true );
			
			return getTypeRegistry().getContextType(YamlEditorContextType.DEFAULT_CONTEXT_TYPE_ID);
		}
		catch (final BadPartitioningException e) {} 
		catch (final BadLocationException e) {}
		return null;
	}
	
}
