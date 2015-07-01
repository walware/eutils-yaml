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

package de.walware.eutils.yaml.core.source;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.text.core.sections.AbstractDocContentSections;


public class YamlDocumentContentInfo extends AbstractDocContentSections {
	
	
	public static final String YAML=                        IYamlDocumentConstants.YAML_PARTITIONING;
	
	
	public static final YamlDocumentContentInfo INSTANCE= new YamlDocumentContentInfo();
	
	
	public YamlDocumentContentInfo() {
		super(IYamlDocumentConstants.YAML_PARTITIONING, YAML,
				ImCollections.newList(YAML) );
	}
	
	
	@Override
	public String getTypeByPartition(final String contentType) {
		return YAML;
	}
	
}
