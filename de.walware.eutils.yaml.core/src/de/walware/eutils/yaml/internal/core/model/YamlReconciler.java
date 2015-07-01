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

package de.walware.eutils.yaml.internal.core.model;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.ltk.core.impl.SourceModelStamp;
import de.walware.ecommons.ltk.core.model.ISourceUnit;

import de.walware.eutils.yaml.core.ast.SourceComponent;
import de.walware.eutils.yaml.core.ast.YamlParser;
import de.walware.eutils.yaml.core.model.IYamlModelInfo;
import de.walware.eutils.yaml.core.model.YamlProblemReporter;
import de.walware.eutils.yaml.core.model.YamlSuModelContainer;


public class YamlReconciler {
	
	
	protected static class Data {
		
		public final YamlSuModelContainer<?> adapter;
		public final SourceContent content;
		
		public int parseOffset;
		
		public AstInfo ast;
		
		public IYamlModelInfo oldModel;
		public IYamlModelInfo newModel;
		
		public Data(final YamlSuModelContainer<?> adapter, final IProgressMonitor monitor) {
			this.adapter= adapter;
			this.content= adapter.getParseContent(monitor);
		}
		
	}
	
	
	private final YamlModelManager yamlManager;
	protected boolean stop= false;
	
	private final Object f1AstLock= new Object();
	private final YamlParser f1Parser= new YamlParser();
	
	private final Object f2ModelLock= new Object();
	private final SourceAnalyzer f2SourceAnalyzer= new SourceAnalyzer();
	
	private final Object f3ReportLock= new Object();
	private final YamlProblemReporter f3ProblemReporter= new YamlProblemReporter();
	
	
	public YamlReconciler(final YamlModelManager manager) {
		this.yamlManager= manager;
	}
	
	
	public void reconcile(final YamlSuModelContainer<?> adapter, final int flags,
			final IProgressMonitor monitor) {
		final ISourceUnit su= adapter.getSourceUnit();
		final Data data= new Data(adapter, monitor);
		if (data.content == null) {
			return;
		}
		
		synchronized (this.f1AstLock) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			updateAst(data, flags, monitor);
		}
		
		if (this.stop || monitor.isCanceled()
				|| (flags & 0xf) < IModelManager.MODEL_FILE) {
			return;
		}
		
		synchronized (this.f2ModelLock) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			final boolean updated= updateModel(data, flags, monitor);
			
			if (updated) {
				this.yamlManager.getEventJob().addUpdate(su, data.oldModel, data.newModel);
			}
		}
		
		if ((flags & IModelManager.RECONCILE) != 0 && data.newModel != null) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			
			IProblemRequestor problemRequestor= null;
			synchronized (this.f3ReportLock) {
				if (!this.stop && !monitor.isCanceled()
						&& data.newModel == adapter.getCurrentModel() ) {
					problemRequestor= adapter.createProblemRequestor();
					if (problemRequestor != null) {
						this.f3ProblemReporter.run(data.newModel, data.content,
								problemRequestor, flags, monitor);
					}
				}
				if (problemRequestor != null) {
					problemRequestor.finish();
				}
			}
		}
	}
	
	protected final void updateAst(final Data data, final int flags,
			final IProgressMonitor monitor) {
		final SourceModelStamp stamp= new SourceModelStamp(data.content.getStamp());
		
		data.ast= data.adapter.getCurrentAst();
		if (data.ast != null && !stamp.equals(data.ast.getStamp())) {
			data.ast= null;
		}
		
		if (data.ast == null) {
			final SourceComponent sourceNode;
			
			sourceNode= this.f1Parser.parse(data.content.getText());
			
			data.ast= new AstInfo(1, stamp, sourceNode);
			
			synchronized (data.adapter) {
				data.adapter.setAst(data.ast);
			}
		}
	}
	
	protected final boolean updateModel(final Data data, final int flags,
			final IProgressMonitor monitor) {
		data.newModel= data.adapter.getCurrentModel();
		if (data.newModel != null && !data.ast.getStamp().equals(data.newModel.getStamp())) {
			data.newModel= null;
		}
		
		if (data.newModel == null) {
			final YamlSourceUnitModelInfo model= this.f2SourceAnalyzer.createModel(data.adapter.getSourceUnit(),
					data.content.getText(), data.ast );
			final boolean isOK= (model != null);
			
			if (isOK) {
				synchronized (data.adapter) {
					data.oldModel= data.adapter.getCurrentModel();
					data.adapter.setModel(model);
				}
				data.newModel= model;
				return true;
			}
		}
		return false;
	}
	
	void stop() {
		this.stop= true;
	}
	
}
