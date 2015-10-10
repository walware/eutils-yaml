/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.ui.sourceediting;

import static de.walware.ecommons.text.ui.BracketLevel.AUTODELETE;
import static de.walware.eutils.yaml.core.source.IYamlDocumentConstants.YAML_ANY_CONTENT_CONSTRAINT;
import static de.walware.eutils.yaml.core.source.IYamlDocumentConstants.YAML_DEFAULT_CONTENT_CONSTRAINT;
import static de.walware.eutils.yaml.core.source.YamlHeuristicTokenScanner.CURLY_BRACKET_TYPE;
import static de.walware.eutils.yaml.core.source.YamlHeuristicTokenScanner.SQUARE_BRACKET_TYPE;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.events.KeyEvent;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.ltk.ui.sourceediting.AbstractAutoEditStrategy;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.text.IIndentSettings;
import de.walware.ecommons.text.IndentUtil;
import de.walware.ecommons.text.core.ITextRegion;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.TreePartition;
import de.walware.ecommons.ui.ISettingsChangedHandler;

import de.walware.eutils.yaml.core.IYamlCoreAccess;
import de.walware.eutils.yaml.core.YamlCodeStyleSettings;
import de.walware.eutils.yaml.core.source.IYamlDocumentConstants;
import de.walware.eutils.yaml.core.source.YamlHeuristicTokenScanner;
import de.walware.eutils.yaml.core.source.YamlPartitionNodeType;
import de.walware.eutils.yaml.internal.ui.editors.YamlBracketLevel;
import de.walware.eutils.yaml.ui.sourceediting.YamlEditingSettings;


/**
 * Auto edit strategy for YAML code
 */
public class YamlAutoEditStrategy extends AbstractAutoEditStrategy {
	
	
	public static class Settings implements ISmartInsertSettings, ISettingsChangedHandler {
		
		private final IYamlCoreAccess coreAccess;
		
		private boolean enabledByDefault;
		private TabAction tabAction;
		private boolean closeBrackets;
		private boolean closeQuotes;
		
		
		public Settings(final IYamlCoreAccess coreAccess) {
			this.coreAccess= coreAccess;
			updateSettings();
		}
		
		
		@Override
		public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
			if (groupIds == null || groupIds.contains(YamlEditingSettings.SMARTINSERT_GROUP_ID)) {
				updateSettings();
			}
		}
		
		private void updateSettings() {
			final IPreferenceAccess prefs= this.coreAccess.getPrefs();
			this.enabledByDefault= prefs.getPreferenceValue(YamlEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF);
			this.tabAction= prefs.getPreferenceValue(YamlEditingSettings.SMARTINSERT_TAB_ACTION_PREF);
			this.closeBrackets= prefs.getPreferenceValue(YamlEditingSettings.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF);
			this.closeQuotes= prefs.getPreferenceValue(YamlEditingSettings.SMARTINSERT_CLOSEQUOTES_ENABLED_PREF);
		}
		
		@Override
		public boolean isSmartInsertEnabledByDefault() {
			return this.enabledByDefault;
		}
		
		@Override
		public TabAction getSmartInsertTabAction() {
			return this.tabAction;
		}
		
	}
	
	
	private final IYamlCoreAccess yamlCoreAccess;
	private final Settings settings;
	
	private YamlHeuristicTokenScanner scanner;
	private YamlCodeStyleSettings codeStyle;
	
	
	public YamlAutoEditStrategy(final IYamlCoreAccess coreAccess, final ISourceEditor editor) {
		super(editor);
		assert (coreAccess != null);
		
		this.yamlCoreAccess= coreAccess;
		this.settings= new Settings(coreAccess);
	}
	
	
	@Override
	public Settings getSettings() {
		return this.settings;
	}
	
	@Override
	protected IIndentSettings getCodeStyleSettings() {
		return this.codeStyle;
	}
	
	
	@Override
	protected final TreePartition initCustomization(final int offset, final int ch)
			throws BadLocationException, BadPartitioningException {
		if (this.scanner == null) {
			this.scanner= createScanner();
		}
		this.codeStyle= this.yamlCoreAccess.getYamlCodeStyle();
		
		return super.initCustomization(offset, ch);
	}
	
	protected YamlHeuristicTokenScanner createScanner() {
		return YamlHeuristicTokenScanner.create(getDocumentContentInfo());
	}
	
	@Override
	protected ITextRegion computeValidRange(final int offset, final TreePartition partition, final int ch) {
		ITreePartitionNode node= partition.getTreeNode();
		if (node.getType() instanceof YamlPartitionNodeType) {
			if (getDocumentContentInfo().getPrimaryType() == IYamlDocumentConstants.YAML_PARTITIONING) {
				return super.computeValidRange(offset, partition, ch);
			}
			else {
				ITreePartitionNode parent;
				while ((parent= node.getParent()) != null
						&& parent instanceof YamlPartitionNodeType) {
					node= parent;
				}
				return node;
			}
		}
		return null;
	}
	
	@Override
	protected YamlHeuristicTokenScanner getScanner() {
		return this.scanner;
	}
	
	@Override
	protected final void quitCustomization() {
		super.quitCustomization();
		
		this.codeStyle= null;
	}
	
	
	private final boolean isClosedBracket(final int backwardOffset, final int forwardOffset,
			final String currentPartition, final int searchType) {
		int[] balance= new int[3];
		balance[searchType]++;
		this.scanner.configure(getDocument(), currentPartition);
		balance= this.scanner.computeBracketBalance(backwardOffset, forwardOffset, balance, searchType);
		return (balance[searchType] <= 0);
	}
	
	private final boolean isClosedQuotedD(int offset, final int end, final boolean endVirtual) {
		this.scanner.configure(getDocument());
		boolean in= true; // we start always inside after a sep
		final char[] chars= new char[] { '"', '\\' };
		while (offset < end) {
			offset= this.scanner.scanForward(offset, end, chars);
			if (offset == YamlHeuristicTokenScanner.NOT_FOUND) {
				offset= end;
				break;
			}
			offset++;
			if (this.scanner.getChar() == '\\') {
				offset++;
			}
			else {
				in= !in;
			}
		}
		return (offset == end) && (!in ^ endVirtual);
	}
	
	private boolean isValueChar(final int offset) throws BadLocationException {
		final int ch= getChar(offset);
		return (ch != -1 && Character.isLetterOrDigit(ch));
	}
	
	
	@Override
	protected char isCustomizeKey(final KeyEvent event) {
		switch (event.character) {
		case '[':
		case '{':
		case '"':
		case '\'':
			return event.character;
		case '\t':
			if (event.stateMask == 0) {
				return '\t';
			}
			break;
		case 0x0A:
		case 0x0D:
			if (getEditor3() != null) {
				return '\n';
			}
			break;
		default:
			break;
		}
		return 0;
	}
	
	@Override
	protected void doCustomizeKeyCommand(final char ch, final DocumentCommand command,
			final TreePartition partition) throws Exception {
		final String contentType= partition.getType();
		final int cEnd= command.offset+command.length;
		int linkedModeType= -1;
		int linkedModeOffset= -1;
		
		KEY: switch (ch) {
		case '\t':
			if (YAML_ANY_CONTENT_CONSTRAINT.matches(contentType)
					&& isRegularTabCommand(command)) {
				command.text= "\t"; //$NON-NLS-1$
				smartInsertOnTab(command, true);
				break KEY;
			}
			return;
		case '[':
			if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
				command.text= "["; //$NON-NLS-1$
				if (this.settings.closeBrackets && !isValueChar(cEnd)) {
					if (!isClosedBracket(command.offset, cEnd, contentType, SQUARE_BRACKET_TYPE)) {
						command.text= "[]"; //$NON-NLS-1$
						linkedModeType= 2 | AUTODELETE;
					}
					else if (getChar(cEnd) == ']') {
						linkedModeType= 2;
					}
				}
				break KEY;
			}
			return;
		case '{':
			if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
				command.text= "{"; //$NON-NLS-1$
				if (this.settings.closeBrackets && !isValueChar(cEnd)) {
					if (!isClosedBracket(command.offset, cEnd, contentType, CURLY_BRACKET_TYPE)) {
						command.text= "{}"; //$NON-NLS-1$
						linkedModeType= 2 | AUTODELETE;
					}
					else if (getChar(cEnd) == '}') {
						linkedModeType= 2;
					}
				}
				break KEY;
			}
			return;
		case '"':
			if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
					&& this.settings.closeQuotes
					&& !isValueChar(cEnd) && !isValueChar(command.offset - 1) ) {
				final IRegion line= getDocument().getLineInformationOfOffset(cEnd);
				if (!isClosedQuotedD(cEnd, line.getOffset() + line.getLength(), false)) {
					command.text= "\"\""; //$NON-NLS-1$
					linkedModeType= 2 | AUTODELETE;
					break KEY;
				}
				break KEY;
			}
			return;
		case '\'':
			if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
					&& this.settings.closeQuotes
					&& !isValueChar(cEnd) && !isValueChar(command.offset - 1) ) {
				final IRegion line= getDocument().getLineInformationOfOffset(cEnd);
				if (!isClosedQuotedD(cEnd, line.getOffset() + line.getLength(), false)) {
					command.text= "\'\'"; //$NON-NLS-1$
					linkedModeType= 2 | AUTODELETE;
					break KEY;
				}
				break KEY;
			}
			return;
		case '\n':
			if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
					|| (contentType == IYamlDocumentConstants.YAML_COMMENT_CONTENT_TYPE
							&& YAML_DEFAULT_CONTENT_CONSTRAINT.matches(
									partition.getTreeNode().getParent().getType().getPartitionType()) )) {
				command.text= TextUtilities.getDefaultLineDelimiter(getDocument());
				smartIndentOnNewLine(command, contentType);
				break KEY;
			}
			return;
		default:
			assert (false);
			return;
		}
		
		if (command.doit && command.text.length() > 0 && getEditor().isEditable(true)) {
			getViewer().getTextWidget().setRedraw(false);
			try {
				applyCommand(command);
				updateSelection(command);
				
				if (linkedModeType >= 0) {
					if (linkedModeOffset < 0) {
						linkedModeOffset= command.offset;
					}
					createLinkedMode(linkedModeOffset, ch, linkedModeType).enter();
				}
			}
			finally {
				getViewer().getTextWidget().setRedraw(true);
			}
		}
	}
	
	@Override
	protected void doCustomizeOtherCommand(final DocumentCommand command, final TreePartition partition)
			throws Exception {
		final String contentType= partition.getType();
		
		if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
			if (command.length == 0 && TextUtilities.equals(getDocument().getLegalLineDelimiters(), command.text) != -1) {
				smartIndentOnNewLine(command, contentType);
			}
		}
	}
	
	
	private void smartIndentOnNewLine(final DocumentCommand command, final String partitionType)
			throws Exception {
		final String lineDelimiter= command.text;
		final int cBefore;
		if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(partitionType)
				&& (((cBefore= getChar(command.offset - 1)) == '[' && getChar(command.offset + command.length) == ']')
						|| (cBefore == '{' && getChar(command.offset + command.length) == '}') )) {
			command.text= command.text + command.text;
		}
		smartIndentAfterNewLine1(command, lineDelimiter);
	}
	
	private void smartIndentAfterNewLine1(final DocumentCommand command, final String lineDelimiter)
			throws BadLocationException, BadPartitioningException, CoreException {
		final AbstractDocument doc= getDocument();
		final StringBuilder sb= new StringBuilder(command.text);
		int nlIndex= lineDelimiter.length();
		final int line= doc.getLineOfOffset(command.offset);
		int checkOffset= Math.max(0, command.offset);
		
		final ITypedRegion partition= doc.getPartition(
				this.scanner.getDocumentPartitioning(), checkOffset, true );
		if (partition.getType() == IYamlDocumentConstants.YAML_COMMENT_CONTENT_TYPE) {
			checkOffset= partition.getOffset();
		}
		
		final IndentUtil util= new IndentUtil(doc, this.codeStyle);
		final int column= util.getLineIndent(line, false)[IndentUtil.COLUMN_IDX];
		
		if (checkOffset > 0) {
			// new block?:
			this.scanner.configure(doc);
			final int match= this.scanner.findAnyNonBlankBackward(checkOffset, doc.getLineOffset(line) - 1, false);
			final char cBefore;
			if (match >= 0 && ((cBefore= doc.getChar(match)) == '[' || cBefore == '{')) {
				final String indent= util.createIndentString(util.getNextLevelColumn(column, 1));
				sb.insert(nlIndex, indent);
				nlIndex+= indent.length() + lineDelimiter.length();
			}
		}
		
		if (nlIndex <= sb.length()) {
			sb.insert(nlIndex, util.createIndentString(column));
		}
		command.text= sb.toString();
	}
	
	
	private LinkedModeUI createLinkedMode(final int offset, final char type, final int mode)
			throws BadLocationException {
		final LinkedModeModel model= new LinkedModeModel();
		int pos= 0;
		
		final LinkedPositionGroup group= new LinkedPositionGroup();
		final LinkedPosition position= YamlBracketLevel.createPosition(type, getDocument(),
				offset + 1, 0, pos++ );
		group.addPosition(position);
		model.addGroup(group);
		
		model.forceInstall();
		
		final YamlBracketLevel level= new YamlBracketLevel(
				getDocument(), getDocumentContentInfo().getPartitioning(),
				ImCollections.newList(position), (mode & 0xffff0000) );
		
		/* create UI */
		final LinkedModeUI ui= new LinkedModeUI(model, getViewer());
		ui.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
		ui.setExitPosition(getViewer(), offset + (mode & 0xff), 0, pos);
		ui.setSimpleMode(true);
		ui.setExitPolicy(level);
		return ui;
	}
	
}
