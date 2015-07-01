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

package de.walware.eutils.yaml.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import de.walware.eutils.yaml.internal.ui.YamlUIPlugin;


public class YamlUIResources {
	
	
	private static final String NS= "de.walware.eutils.yaml"; //$NON-NLS-1$
	
	
	public static final String OBJ_LABEL_IMAGE_ID= NS + "/image/obj/Label"; //$NON-NLS-1$
	
	
	public static final YamlUIResources INSTANCE= new YamlUIResources();
	
	
	private final ImageRegistry registry;
	
	
	private YamlUIResources() {
		this.registry= YamlUIPlugin.getInstance().getImageRegistry();
	}
	
	public ImageDescriptor getImageDescriptor(final String id) {
		return this.registry.getDescriptor(id);
	}
	
	public Image getImage(final String id) {
		return this.registry.get(id);
	}
	
}
