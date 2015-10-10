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

package de.walware.eutils.yaml.core.source;

import org.eclipse.jface.text.IDocumentPartitioner;

import de.walware.ecommons.text.PartitionerDocumentSetupParticipant;
import de.walware.ecommons.text.core.treepartitioner.TreePartitioner;


/**
 * The document setup participant for YAML.
 */
public class YamlDocumentSetupParticipant extends PartitionerDocumentSetupParticipant {
	
	
	public YamlDocumentSetupParticipant() {
	}
	
	
	@Override
	public String getPartitioningId() {
		return IYamlDocumentConstants.YAML_PARTITIONING;
	}
	
	@Override
	protected IDocumentPartitioner createDocumentPartitioner() {
		return new TreePartitioner(getPartitioningId(),
				new YamlPartitionNodeScanner(),
				IYamlDocumentConstants.YAML_CONTENT_TYPES );
	}
	
}
