/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.core.model;

import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;

import de.walware.eutils.yaml.internal.core.YamlCorePlugin;


public final class YamlModel {
	
	
	public static final String YAML_TYPE_ID= "Yaml"; //$NON-NLS-1$
	
	
	public static IYamlModelManager getYamlModelManager() {
		return YamlCorePlugin.getInstance().getYamlModelManager();
	}
	
	public static IYamlModelInfo getYamlModelInfo(final ISourceUnitModelInfo modelInfo) {
		if (modelInfo != null) {
			if (modelInfo instanceof IYamlModelInfo) {
				return (IYamlModelInfo) modelInfo;
			}
			for (final Object aAttachment : modelInfo.getAttachments()) {
				if (aAttachment instanceof IYamlModelInfo) {
					return (IYamlModelInfo) aAttachment;
				}
			}
		}
		return null;
	}
	
	
	private YamlModel() {}
	
}
