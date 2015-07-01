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

package de.walware.eutils.yaml.core.refactoring;

import org.eclipse.jface.text.ITypedRegion;

import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;

import de.walware.eutils.yaml.core.YamlCore;
import de.walware.eutils.yaml.core.model.YamlModel;
import de.walware.eutils.yaml.core.source.IYamlDocumentConstants;
import de.walware.eutils.yaml.core.source.YamlHeuristicTokenScanner;


public class YamlRefactoringAdapter extends RefactoringAdapter {
	
	
	public YamlRefactoringAdapter() {
		super(YamlModel.YAML_TYPE_ID);
	}
	
	
	@Override
	public String getPluginIdentifier() {
		return YamlCore.PLUGIN_ID;
	}
	
	@Override
	public YamlHeuristicTokenScanner getScanner(final ISourceUnit su) {
		return YamlHeuristicTokenScanner.create(su.getDocumentContentInfo());
	}
	
	@Override
	public boolean isCommentContent(final ITypedRegion partition) {
		return (partition != null
				&& partition.getType() == IYamlDocumentConstants.YAML_COMMENT_CONTENT_TYPE );
	}
	
}
