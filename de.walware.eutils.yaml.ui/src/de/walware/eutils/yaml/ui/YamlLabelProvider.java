/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.ui.IElementLabelProvider;

import de.walware.eutils.yaml.core.model.YamlLabelAccess;


public class YamlLabelProvider extends StyledCellLabelProvider implements IElementLabelProvider, ILabelProvider {
	
	
	private YamlUIResources yamlResources;
	
	
	public YamlLabelProvider() {
		this.yamlResources= YamlUIResources.INSTANCE;
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
		this.yamlResources= null;
	}
	
	
	@Override
	public Image getImage(final Object element) {
		if (element instanceof IModelElement) {
			return getImage((IModelElement) element);
		}
		if (element instanceof YamlLabelAccess) {
			return getImage((YamlLabelAccess) element);
		}
		return null;
	}
	
	@Override
	public Image getImage(final IModelElement element) {
		switch (element.getElementType() & IModelElement.MASK_C3) {
		default:
			return null;
		}
	}
	
	public Image getImage(final YamlLabelAccess access) {
		if (access.getType() == YamlLabelAccess.LABEL) {
			return this.yamlResources.getImage(YamlUIResources.OBJ_LABEL_IMAGE_ID);
		}
		return null;
	}
	
	@Override
	public String getText(final Object element) {
		if (element instanceof IModelElement) {
			return getText((IModelElement) element);
		}
		if (element instanceof YamlLabelAccess) {
			return getText((YamlLabelAccess) element);
		}
		return null;
	}
	
	@Override
	public String getText(final IModelElement element) {
		return element.getElementName().getDisplayName();
	}
	
	public String getText(final YamlLabelAccess access) {
		return access.getDisplayName();
	}
	
	@Override
	public StyledString getStyledText(final IModelElement element) {
		return new StyledString(element.getElementName().getDisplayName());
	}
	
	@Override
	public void update(final ViewerCell cell) {
		final Object cellElement= cell.getElement();
		if (cellElement instanceof IModelElement) {
			final IModelElement element= (IModelElement) cellElement;
			cell.setImage(getImage(element));
			final StyledString styledText= getStyledText(element);
			cell.setText(styledText.getString());
			cell.setStyleRanges(styledText.getStyleRanges());
			super.update(cell);
		}
		else {
			cell.setImage(null);
			cell.setText(cellElement.toString());
			cell.setStyleRanges(null);
			super.update(cell);
		}
	}
	
}
