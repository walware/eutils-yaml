/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.ui.sourceediting;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SearchPattern;

import de.walware.ecommons.ltk.core.model.IModelElement.Filter;
import de.walware.ecommons.ltk.ui.sourceediting.QuickOutlineInformationControl;
import de.walware.ecommons.ltk.ui.sourceediting.actions.OpenDeclaration;
import de.walware.ecommons.ui.content.ITextElementFilter;
import de.walware.ecommons.ui.content.TextElementFilter;

import de.walware.eutils.yaml.core.model.YamlModel;
import de.walware.eutils.yaml.ui.YamlLabelProvider;
import de.walware.eutils.yaml.ui.util.YamlNameSearchPattern;


public class YamlQuickOutlineInformationControl extends QuickOutlineInformationControl {
	
	
	public YamlQuickOutlineInformationControl(final Shell parent, final String commandId) {
		super(parent, commandId, 1, new OpenDeclaration());
	}
	
	
	@Override
	public String getModelTypeId() {
		return YamlModel.YAML_TYPE_ID;
	}
	
	@Override
	protected Filter getContentFilter() {
		return null;
	}
	
	@Override
	protected ITextElementFilter createNameFilter() {
		return new TextElementFilter() {
			@Override
			protected SearchPattern createSearchPattern() {
				return new YamlNameSearchPattern();
			}
		};
	}
	
	@Override
	protected void configureViewer(final TreeViewer viewer) {
		super.configureViewer(viewer);
		
		viewer.setLabelProvider(new YamlLabelProvider());
	}
	
}
