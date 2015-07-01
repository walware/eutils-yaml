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

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor2OutlinePage;
import de.walware.ecommons.ltk.ui.util.ViewerDragSupport;
import de.walware.ecommons.ltk.ui.util.ViewerDropSupport;
import de.walware.ecommons.ui.actions.HandlerCollection;
import de.walware.ecommons.ui.util.DialogUtil;

import de.walware.eutils.yaml.core.model.YamlModel;
import de.walware.eutils.yaml.core.refactoring.YamlRefactoring;
import de.walware.eutils.yaml.internal.ui.YamlUIPlugin;
import de.walware.eutils.yaml.ui.YamlLabelProvider;


/**
 * Outline page for YAML sources.
 */
public class YamlOutlinePage extends SourceEditor2OutlinePage {
	
	private class ContentFilter implements IModelElement.Filter {
		
		public ContentFilter() {
		}
		
		@Override
		public boolean include(final IModelElement element) {
			switch (element.getElementType()) {
			default:
				return true;
			}
		};
		
	}
	
	
	private final ContentFilter filter= new ContentFilter();
	
	
	public YamlOutlinePage(final SourceEditor1 editor) {
		super(editor, YamlModel.YAML_TYPE_ID, YamlRefactoring.getYamlFactory(),
				"de.walware.eutils.yaml.menus.YamlOutlineViewContextMenu"); //$NON-NLS-1$
	}
	
	
	@Override
	protected IDialogSettings getDialogSettings() {
		return DialogUtil.getDialogSettings(YamlUIPlugin.getInstance(), "YamlOutlineView"); //$NON-NLS-1$
	}
	
	@Override
	protected IModelElement.Filter getContentFilter() {
		return this.filter;
	}
	
	@Override
	protected void configureViewer(final TreeViewer viewer) {
		super.configureViewer(viewer);
		
		viewer.setLabelProvider(new YamlLabelProvider());
		
		final ViewerDropSupport drop= new ViewerDropSupport(viewer, this,
				getRefactoringFactory() );
		drop.init();
		final ViewerDragSupport drag= new ViewerDragSupport(viewer);
		drag.init();
	}
	
	@Override
	protected void contributeToActionBars(final IServiceLocator serviceLocator,
			final IActionBars actionBars, final HandlerCollection handlers) {
		super.contributeToActionBars(serviceLocator, actionBars, handlers);
		
//		final IToolBarManager toolBarManager= actionBars.getToolBarManager();
//		
//		toolBarManager.appendToGroup(ECommonsUI.VIEW_SORT_MENU_ID,
//				new AlphaSortAction());
//		toolBarManager.appendToGroup(ECommonsUI.VIEW_FILTER_MENU_ID,
//				new FilterCommonVariables());
//		toolBarManager.appendToGroup(ECommonsUI.VIEW_FILTER_MENU_ID,
//				new FilterLocalDefinitions());
	}
	
}
