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

package de.walware.eutils.yaml.internal.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.preferences.PreferencesUtil;

import de.walware.eutils.yaml.core.IYamlCoreAccess;
import de.walware.eutils.yaml.core.YamlCoreAccess;
import de.walware.eutils.yaml.internal.core.model.YamlModelManager;


public class YamlCorePlugin extends Plugin {
	
	
	/** The shared instance */
	private static YamlCorePlugin instance;
	
	/**
	 * Returns the shared plug-in instance
	 *
	 * @return the shared instance
	 */
	public static YamlCorePlugin getInstance() {
		return instance;
	}
	
	public static final void log(final IStatus status) {
		final Plugin plugin= getInstance();
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}
	
	
	private boolean started;
	
	private YamlModelManager yamlModelManager;
	
	private volatile IYamlCoreAccess workbenchAccess;
	private volatile IYamlCoreAccess defaultsAccess;
	
	
	public YamlCorePlugin() {
	}
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		instance= this;
		
		this.yamlModelManager= new YamlModelManager();
		
		synchronized (this) {
			this.started= true;
		}
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			synchronized (this) {
				this.started= false;
			}
			if (this.yamlModelManager != null) {
				this.yamlModelManager.dispose();
				this.yamlModelManager= null;
			}
		}
		finally {
			instance= null;
			super.stop(context);
		}
	}
	
	
	private void checkStarted() {
		if (!this.started) {
			throw new IllegalStateException("Plug-in is not started.");
		}
	}
	
	public YamlModelManager getYamlModelManager() {
		return this.yamlModelManager;
	}
	
	public IYamlCoreAccess getWorkbenchAccess() {
		IYamlCoreAccess access= this.workbenchAccess;
		if (access == null) {
			synchronized (this) {
				access= this.workbenchAccess;
				if (access == null) {
					checkStarted();
					access= this.workbenchAccess= new YamlCoreAccess(
							PreferencesUtil.getInstancePrefs() );
				}
			}
		}
		return access;
	}
	
	public IYamlCoreAccess getDefaultsAccess() {
		IYamlCoreAccess access= this.defaultsAccess;
		if (access == null) {
			synchronized (this) {
				access= this.defaultsAccess;
				if (access == null) {
					checkStarted();
					access= this.defaultsAccess= new YamlCoreAccess(
							PreferencesUtil.getDefaultPrefs() );
				}
			}
		}
		return access;
	}
	
}
