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

package de.walware.eutils.yaml.ui.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextUtilities;

import de.walware.ecommons.text.PairMatcher;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.TreePartition;

import de.walware.eutils.yaml.core.source.IYamlDocumentConstants;
import de.walware.eutils.yaml.core.source.YamlBracketPairMatcher;
import de.walware.eutils.yaml.core.source.YamlHeuristicTokenScanner;


/**
 * If matching pairs found, selection of content inside matching brackets,
 * otherwise default word selection.
 */
public class YamlDoubleClickStrategy extends DefaultTextDoubleClickStrategy {
	
	
	private final YamlHeuristicTokenScanner scanner;
	private final PairMatcher pairMatcher;
	
	
	public YamlDoubleClickStrategy(final YamlHeuristicTokenScanner scanner) {
		this.scanner= scanner;
		this.pairMatcher= new YamlBracketPairMatcher(this.scanner);
	}
	
	
	@Override
	public void doubleClicked(final ITextViewer textViewer) {
		final int offset= textViewer.getSelectedRange().x;
		
		if (offset < 0) {
			return;
		}
		
		final IDocument document= textViewer.getDocument();
		try {
			TreePartition partition= (TreePartition) TextUtilities.getPartition(document,
					this.scanner.getDocumentPartitioning(), offset, true );
			String type= partition.getType();
			
			// Bracket-Pair-Matching in Code-Partitions
			if (type == IYamlDocumentConstants.YAML_DEFAULT_CONTENT_TYPE) {
				final IRegion region= this.pairMatcher.match(document, offset);
				if (region != null && region.getLength() >= 2) {
					textViewer.setSelectedRange(region.getOffset() + 1, region.getLength() - 2);
					return;
				}
			}
			
			// For other partitions, use prefere new partitions (instead opened)
			partition= (TreePartition) TextUtilities.getPartition(document,
					this.scanner.getDocumentPartitioning(), offset, false );
			type= partition.getType();
			final ITreePartitionNode treeNode= partition.getTreeNode();
			
			switch (type) {
			case IYamlDocumentConstants.YAML_KEY_CONTENT_TYPE:
			case IYamlDocumentConstants.YAML_TAG_CONTENT_TYPE:
			case IYamlDocumentConstants.YAML_VALUE_CONTENT_TYPE:
				if (offset == treeNode.getOffset()) {
					textViewer.setSelectedRange(treeNode.getOffset(), treeNode.getLength());
					return;
				}
				break;
			case IYamlDocumentConstants.YAML_COMMENT_CONTENT_TYPE:
				if (offset == treeNode.getOffset() || offset == treeNode.getOffset() + 1) {
					textViewer.setSelectedRange(treeNode.getOffset(), treeNode.getLength());
					return;
				}
				break;
			default:
				break;
			}
			
			super.doubleClicked(textViewer);
			return;
		}
		catch (final BadLocationException e) {
		}
		catch (final NullPointerException e) {
		}
		// else
		textViewer.setSelectedRange(offset, 0);
	}
	
}
