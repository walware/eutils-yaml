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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.jcommons.collections.ImCollections;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.IModelElementDelta;
import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.core.impl.AbstractModelEventJob;
import de.walware.ecommons.ltk.core.impl.AbstractModelManager;
import de.walware.ecommons.ltk.core.impl.SourceUnitModelContainer;
import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;

import de.walware.eutils.yaml.core.model.IYamlModelInfo;
import de.walware.eutils.yaml.core.model.IYamlModelManager;
import de.walware.eutils.yaml.core.model.YamlChunkElement;
import de.walware.eutils.yaml.core.model.YamlModel;
import de.walware.eutils.yaml.core.model.YamlSuModelContainer;


public class YamlModelManager extends AbstractModelManager implements IYamlModelManager {
	
	
	private static class ModelDelta implements IModelElementDelta {
		
		private final int level;
		private final IModelElement element;
		private final ISourceUnitModelInfo oldInfo;
		private final AstInfo oldAst;
		private final ISourceUnitModelInfo newInfo;
		private final AstInfo newAst;
		
		
		public ModelDelta(final IModelElement element,
				final ISourceUnitModelInfo oldInfo, final ISourceUnitModelInfo newInfo) {
			this.level= IModelManager.MODEL_FILE;
			this.element= element;
			this.oldInfo= oldInfo;
			this.oldAst= (oldInfo != null) ? oldInfo.getAst() : null;
			this.newInfo= newInfo;
			this.newAst= (newInfo != null) ? newInfo.getAst() : null;
		}
		
		
		@Override
		public IModelElement getModelElement() {
			return this.element;
		}
		
		@Override
		public AstInfo getOldAst() {
			return this.oldAst;
		}
		
		@Override
		public AstInfo getNewAst() {
			return this.newAst;
		}
		
	}
	
	protected static class EventJob extends AbstractModelEventJob<ISourceUnit, IYamlModelInfo> {
		
		public EventJob(final YamlModelManager manager) {
			super(manager);
		}
		
		@Override
		protected IModelElementDelta createDelta(final Task task) {
			return new ModelDelta(task.getElement(), task.getOldInfo(), task.getNewInfo());
		}
		
		@Override
		protected void dispose() {
			super.dispose();
		}
		
	}
	
	
	private final EventJob eventJob= new EventJob(this);
	
	private final YamlReconciler reconciler= new YamlReconciler(this);
	
	
	public YamlModelManager() {
		super(YamlModel.YAML_TYPE_ID);
	}
	
	
	public void dispose() {
		this.eventJob.dispose();
	}
	
	
	public EventJob getEventJob() {
		return this.eventJob;
	}
	
	@Override
	public void reconcile(final SourceUnitModelContainer<?, ?> adapter,
			final int level, final IProgressMonitor monitor) {
		if (adapter instanceof YamlSuModelContainer) {
			this.reconciler.reconcile((YamlSuModelContainer<?>) adapter, level, monitor);
		}
	}
	
	@Override
	public IYamlModelInfo reconcile(final ISourceUnit sourceUnit, final ISourceUnitModelInfo modelInfo,
			List<? extends YamlChunkElement> chunks,
			final int level, final IProgressMonitor monitor) {
		if (sourceUnit == null) {
			throw new NullPointerException("sourceUnit"); //$NON-NLS-1$
		}
		if (chunks == null) {
			chunks= ImCollections.emptyList();
		}
		
		final CompositeSourceElement unitElement= new CompositeSourceElement(chunks, sourceUnit,
				modelInfo.getSourceElement().getSourceRange() );
		return new YamlSourceUnitModelInfo(modelInfo.getAst(), unitElement, null);
	}
	
}
