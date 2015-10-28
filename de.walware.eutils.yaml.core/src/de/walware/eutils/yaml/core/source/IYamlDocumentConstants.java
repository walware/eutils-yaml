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

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.text.core.IPartitionConstraint;


public interface IYamlDocumentConstants {
	
	
	/**
	 * The id of partitioning of YAML documents.
	 */
	String YAML_PARTITIONING= "Yaml_walware"; //$NON-NLS-1$
	
	String YAML_DEFAULT_CONTENT_TYPE= "Yaml.Default"; //$NON-NLS-1$
	String YAML_COMMENT_CONTENT_TYPE= "Yaml.Comment"; //$NON-NLS-1$
	String YAML_DIRECTIVE_CONTENT_TYPE= "Yaml.Directive"; //$NON-NLS-1$
	String YAML_KEY_CONTENT_TYPE= "Yaml.Key"; //$NON-NLS-1$
	String YAML_TAG_CONTENT_TYPE= "Yaml.Tag"; //$NON-NLS-1$
	String YAML_VALUE_CONTENT_TYPE= "Yaml.Value"; //$NON-NLS-1$
	
	
	/**
	 * List with all partition content types of YAML documents.
	 */
	ImList<String> YAML_CONTENT_TYPES= ImCollections.newList(
			YAML_DEFAULT_CONTENT_TYPE,
			YAML_COMMENT_CONTENT_TYPE,
			YAML_DIRECTIVE_CONTENT_TYPE,
			YAML_KEY_CONTENT_TYPE,
			YAML_TAG_CONTENT_TYPE,
			YAML_VALUE_CONTENT_TYPE );
	
	
	IPartitionConstraint YAML_DEFAULT_CONTENT_CONSTRAINT= new IPartitionConstraint() {
		@Override
		public boolean matches(final String partitionType) {
			return (partitionType == YAML_DEFAULT_CONTENT_TYPE);
		}
	};
	
	IPartitionConstraint YAML_ANY_CONTENT_CONSTRAINT= new IPartitionConstraint() {
		@Override
		public boolean matches(final String partitionType) {
			return (partitionType.startsWith("Yaml.")); //$NON-NLS-1$
		}
	};
	
}
