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

package de.walware.eutils.yaml.core.source;

import org.eclipse.jface.text.IDocument;

import de.walware.ecommons.text.core.treepartitioner.AbstractPartitionNodeType;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;


public abstract class YamlPartitionNodeType extends AbstractPartitionNodeType {
	
	
	public static final YamlPartitionNodeType DEFAULT_ROOT= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_DEFAULT_CONTENT_TYPE;
		}
		
		@Override
		public boolean prefereAtBegin(final ITreePartitionNode node, final IDocument document) {
			return true;
		}
		
		@Override
		public boolean prefereAtEnd(final ITreePartitionNode node, final IDocument document) {
			return true;
		}
		
	};
	
	public static final YamlPartitionNodeType DIRECTIVE= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_DIRECTIVE_CONTENT_TYPE;
		}
		
	};
	
	public static final YamlPartitionNodeType FLOAT_MAPPING= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_DEFAULT_CONTENT_TYPE;
		}
		
	};
	
	public static final YamlPartitionNodeType FLOAT_SEQUENCE= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_DEFAULT_CONTENT_TYPE;
		}
		
	};
	
	public static final YamlPartitionNodeType BLOCK_MAPPING= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_DEFAULT_CONTENT_TYPE;
		}
		
	};
	
	public static final YamlPartitionNodeType BLOCK_SEQUENCE= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_DEFAULT_CONTENT_TYPE;
		}
		
	};
	
	public static final YamlPartitionNodeType KEY= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_KEY_CONTENT_TYPE;
		}
		
	};
	
	public static final YamlPartitionNodeType TAG= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_TAG_CONTENT_TYPE;
		}
		
	};
	
	public static final YamlPartitionNodeType VALUE= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_VALUE_CONTENT_TYPE;
		}
		
	};
	
	public static final YamlPartitionNodeType COMMENT_LINE= new YamlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IYamlDocumentConstants.YAML_COMMENT_CONTENT_TYPE;
		}
		
	};
	
	
	protected YamlPartitionNodeType() {
	}
	
	
}
