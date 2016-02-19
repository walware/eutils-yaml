/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import de.walware.eutils.yaml.internal.core.YamlCorePlugin;


public class YamlCore {
	
	public static final String PLUGIN_ID= "de.walware.eutils.yaml.core"; //$NON-NLS-1$
	
	
	public static final String YAML_CONTENT_ID= "de.walware.eutils.yaml.contentTypes.Yaml"; //$NON-NLS-1$
	
	public static final IContentType YAML_CONTENT_TYPE;
	
	static {
		final IContentTypeManager contentTypeManager= Platform.getContentTypeManager();
		YAML_CONTENT_TYPE= contentTypeManager.getContentType(YAML_CONTENT_ID);
	}
	
	
	public static IYamlCoreAccess getWorkbenchAccess() {
		return YamlCorePlugin.getInstance().getWorkbenchAccess();
	}
	
	public static IYamlCoreAccess getDefaultsAccess() {
		return YamlCorePlugin.getInstance().getDefaultsAccess();
	}
	
}
