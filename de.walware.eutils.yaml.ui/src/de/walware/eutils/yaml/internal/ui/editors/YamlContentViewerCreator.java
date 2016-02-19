/*=============================================================================#
 # Copyright (c) 2008-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.ui.editors;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import de.walware.ecommons.ltk.ui.compare.CompareTextViewer;

import de.walware.eutils.yaml.ui.sourceediting.YamlSourceViewerConfiguration;
import de.walware.eutils.yaml.ui.sourceediting.YamlSourceViewerConfigurator;


public class YamlContentViewerCreator implements IViewerCreator {
	
	
	public YamlContentViewerCreator() {
	}
	
	
	@Override
	public Viewer createViewer(final Composite parent, final CompareConfiguration config) {
		final YamlSourceViewerConfigurator viewerConfigurator=
				new YamlSourceViewerConfigurator(null, new YamlSourceViewerConfiguration());
		return new CompareTextViewer(parent, config, viewerConfigurator);
	}
	
}
