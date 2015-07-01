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

package de.walware.eutils.yaml.ui.sourceediting;

import static de.walware.ecommons.text.ui.BracketLevel.AUTODELETE;
import static de.walware.eutils.yaml.core.source.IYamlDocumentConstants.YAML_ANY_CONTENT_CONSTRAINT;
import static de.walware.eutils.yaml.core.source.IYamlDocumentConstants.YAML_DEFAULT_CONTENT_CONSTRAINT;
import static de.walware.eutils.yaml.core.source.YamlHeuristicTokenScanner.CURLY_BRACKET_TYPE;
import static de.walware.eutils.yaml.core.source.YamlHeuristicTokenScanner.SQUARE_BRACKET_TYPE;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.texteditor.ITextEditorExtension3;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.TabAction;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditorAddon;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.text.ITokenScanner;
import de.walware.ecommons.text.IndentUtil;
import de.walware.ecommons.text.core.sections.IDocContentSections;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.TreePartition;
import de.walware.ecommons.ui.ISettingsChangedHandler;
import de.walware.ecommons.ui.util.UIAccess;

import de.walware.eutils.yaml.core.IYamlCoreAccess;
import de.walware.eutils.yaml.core.YamlCodeStyleSettings;
import de.walware.eutils.yaml.core.source.IYamlDocumentConstants;
import de.walware.eutils.yaml.core.source.YamlHeuristicTokenScanner;
import de.walware.eutils.yaml.core.source.YamlPartitionNodeType;
import de.walware.eutils.yaml.internal.ui.editors.YamlBracketLevel;
import de.walware.eutils.yaml.ui.YamlUI;


/**
 * Auto edit strategy for YAML code
 */
public class YamlAutoEditStrategy extends DefaultIndentLineAutoEditStrategy
		implements ISourceEditorAddon {
	
	
	protected static class SmartInsertSettings implements ISettingsChangedHandler {
		
		private final IYamlCoreAccess coreAccess;
		
		private boolean byDefaultEnabled;
		private TabAction tabAction;
		private boolean closeBrackets;
		private boolean closeQuotes;
		
		
		public SmartInsertSettings(final IYamlCoreAccess coreAccess) {
			this.coreAccess= coreAccess;
		}
		
		
		@Override
		public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
			if (groupIds.contains(YamlEditingSettings.SMARTINSERT_GROUP_ID)) {
				updateSettings();
			}
		}
		
		protected void updateSettings() {
			final IPreferenceAccess prefs= this.coreAccess.getPrefs();
			this.byDefaultEnabled= prefs.getPreferenceValue(YamlEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF);
			this.tabAction= prefs.getPreferenceValue(YamlEditingSettings.SMARTINSERT_TAB_ACTION_PREF);
			this.closeBrackets= prefs.getPreferenceValue(YamlEditingSettings.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF);
			this.closeQuotes= prefs.getPreferenceValue(YamlEditingSettings.SMARTINSERT_CLOSEQUOTES_ENABLED_PREF);
		}
		
	}
	
	
	private class RealTypeListener implements VerifyKeyListener {
		@Override
		public void verifyKey(final VerifyEvent event) {
			if (!event.doit) {
				return;
			}
			switch (event.character) {
			case '[':
			case '{':
			case '"':
			case '\'':
				event.doit= !customizeKeyPressed(event.character);
				return;
			case '\t':
				if (event.stateMask == 0) {
					event.doit= !customizeKeyPressed(event.character);
				}
				return;
			case 0x0A:
			case 0x0D:
				if (YamlAutoEditStrategy.this.editor3 != null) {
					event.doit= !customizeKeyPressed('\n');
				}
				return;
			default:
				return;
			}
		}
	};
	
	
	private final ISourceEditor editor;
	private final ITextEditorExtension3 editor3;
	private final IDocContentSections documentContentInfo;
	private final SourceViewer viewer;
	private final RealTypeListener typeListener;
	
	private final IYamlCoreAccess yamlCoreAccess;
	final SmartInsertSettings settings;
	
	private AbstractDocument document;
	private IRegion validRange;
	private YamlHeuristicTokenScanner scanner;
	private YamlCodeStyleSettings codeStyle;
	
	private boolean ignoreCommands;
	
	
	public YamlAutoEditStrategy(final IYamlCoreAccess coreAccess, final ISourceEditor editor) {
		assert (coreAccess != null);
		assert (editor != null);
		
		this.yamlCoreAccess= coreAccess;
		this.editor= editor;
		this.documentContentInfo= editor.getDocumentContentInfo();
		
		this.settings= new SmartInsertSettings(coreAccess);
//		assert (fOptions != null);
		
		this.viewer= this.editor.getViewer();
		this.editor3= (editor instanceof SourceEditor1) ? (SourceEditor1) editor : null;
		this.typeListener= new RealTypeListener();
	}
	
	
	@Override
	public void install(final ISourceEditor editor) {
		assert (editor.getViewer() == this.viewer);
		this.viewer.prependVerifyKeyListener(this.typeListener);
		this.settings.updateSettings();
	}
	
	@Override
	public void uninstall() {
		this.viewer.removeVerifyKeyListener(this.typeListener);
	}
	
	
	private final TreePartition initCustomization(final int offset, final int c)
			throws BadLocationException, BadPartitioningException {
		assert(this.document != null);
		if (this.scanner == null) {
			this.scanner= createScanner();
		}
		this.codeStyle= this.yamlCoreAccess.getYamlCodeStyle();
		
		final TreePartition partition= (TreePartition) this.document.getPartition(
				this.scanner.getDocumentPartitioning(), offset, true );
		this.validRange= getValidRange(offset, partition, c);
		return (this.validRange != null) ? partition : null;
	}
	
	protected YamlHeuristicTokenScanner createScanner() {
		return YamlHeuristicTokenScanner.create(this.documentContentInfo);
	}
	
	protected IRegion getValidRange(final int offset, final TreePartition partition, final int c) {
		ITreePartitionNode node= partition.getTreeNode();
		if (node.getType() instanceof YamlPartitionNodeType) {
			if (this.documentContentInfo.getPrimaryType() == IYamlDocumentConstants.YAML_PARTITIONING) {
				return new Region(0, this.document.getLength());
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
	
	protected final IDocument getDocument() {
		return this.document;
	}
	
	private final void quitCustomization() {
		this.document= null;
		this.codeStyle= null;
	}
	
	
	private final boolean isSmartInsertEnabled() {
		return ((this.editor3 != null) ?
				(this.editor3.getInsertMode() == ITextEditorExtension3.SMART_INSERT) :
				this.settings.byDefaultEnabled);
	}
	
	private final boolean isBlockSelection() {
		final StyledText textWidget= this.viewer.getTextWidget();
		return (textWidget.getBlockSelection() && textWidget.getSelectionRanges().length > 2);
	}
	
	private final boolean isClosedBracket(final int backwardOffset, final int forwardOffset,
			final String currentPartition, final int searchType) {
		int[] balance= new int[3];
		balance[searchType]++;
		this.scanner.configure(this.document, currentPartition);
		balance= this.scanner.computeBracketBalance(backwardOffset, forwardOffset, balance, searchType);
		return (balance[searchType] <= 0);
	}
	
	private final boolean isClosedQuotedD(int offset, final int end, final boolean endVirtual) {
		this.scanner.configure(this.document);
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
	
	private boolean isCharAt(final int offset, final char c) throws BadLocationException {
		return (offset >= this.validRange.getOffset() && offset < this.validRange.getOffset()+this.validRange.getLength()
				&& this.document.getChar(offset) == c);
	}
	
	private boolean isValueChar(final int offset) throws BadLocationException {
		if (offset >= this.validRange.getOffset() && offset < this.validRange.getOffset()+this.validRange.getLength()) {
			final int c= this.document.getChar(offset);
			return (Character.isLetterOrDigit(c));
		}
		return false;
	}
	
	
	@Override
	public void customizeDocumentCommand(final IDocument d, final DocumentCommand c) {
		if (this.ignoreCommands || c.doit == false || c.text == null) {
			return;
		}
		if (!isSmartInsertEnabled() || isBlockSelection()) {
			super.customizeDocumentCommand(d, c);
			return;
		}
		
		try {
			this.document= (AbstractDocument) d;
			final TreePartition partition= initCustomization(c.offset, -1);
			if (partition == null) {
				return;
			}
			final String contentType= partition.getType();
			
			if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
				if (c.length == 0 && TextUtilities.equals(d.getLegalLineDelimiters(), c.text) != -1) {
					smartIndentOnNewLine(c, contentType);
				}
			}
		}
		catch (final Exception e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, YamlUI.PLUGIN_ID, -1,
					"An error occurred when customizing action for document command in YAML auto edit strategy.",
					e )); 
		}
		finally {
			quitCustomization();
		}
	}
	
	/**
	 * Second main entry method for real single key presses.
	 * 
	 * @return <code>true</code>, if key was processed by method
	 */
	private boolean customizeKeyPressed(final char c) {
		if (!isSmartInsertEnabled() || !UIAccess.isOkToUse(this.viewer) || isBlockSelection()) {
			return false;
		}
		
		try {
			this.document= (AbstractDocument) this.viewer.getDocument();
			ITextSelection selection= (ITextSelection) this.viewer.getSelection();
			final TreePartition partition= initCustomization(selection.getOffset(), c);
			if (partition == null) {
				return false;
			}
			final String contentType= partition.getType();
			this.ignoreCommands= true;
			
			final DocumentCommand command= new DocumentCommand() {};
			command.offset= selection.getOffset();
			command.length= selection.getLength();
			command.doit= true;
			command.shiftsCaret= true;
			command.caretOffset= -1;
			int linkedModeType= -1;
			int linkedModeOffset= -1;
			final int cEnd= command.offset+command.length;
			
			KEY: switch (c) {
			case '\t':
				if (YAML_ANY_CONTENT_CONSTRAINT.matches(contentType)) {
					// Indentation rules are complicated, better use spaces for all partition types
					if (command.length == 0 || this.document.getLineOfOffset(command.offset) == this.document.getLineOfOffset(cEnd)) {
						command.text= "\t"; //$NON-NLS-1$
						switch (smartIndentOnTab(command)) {
						case -1:
							return false;
						case 0:
							break;
						case 1:
							break KEY;
						}
					}
					
					if (this.codeStyle.getReplaceOtherTabsWithSpaces()) {
						final IndentUtil indent= new IndentUtil(this.document, this.codeStyle);
						command.text= indent.createTabSpacesCompletionString(indent.getColumnAtOffset(command.offset));
						break KEY;
					}
				}
				return false;
			case '[':
				if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
					command.text= "["; //$NON-NLS-1$
					if (this.settings.closeBrackets && !isValueChar(cEnd)) {
						if (!isClosedBracket(command.offset, cEnd, contentType, SQUARE_BRACKET_TYPE)) {
							command.text= "[]"; //$NON-NLS-1$
							linkedModeType= 2 | AUTODELETE;
						}
						else if (isCharAt(cEnd, ']')) {
							linkedModeType= 2;
						}
					}
					break KEY;
				}
				return false;
			case '{':
				if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
					command.text= "{"; //$NON-NLS-1$
					if (this.settings.closeBrackets && !isValueChar(cEnd)) {
						if (!isClosedBracket(command.offset, cEnd, contentType, CURLY_BRACKET_TYPE)) {
							command.text= "{}"; //$NON-NLS-1$
							linkedModeType= 2 | AUTODELETE;
						}
						else if (isCharAt(cEnd, '}')) {
							linkedModeType= 2;
						}
					}
					break KEY;
				}
				return false;
			case '"':
				if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
						&& this.settings.closeQuotes
						&& !isValueChar(cEnd) && !isValueChar(command.offset - 1) ) {
					final IRegion line= this.document.getLineInformationOfOffset(cEnd);
					if (!isClosedQuotedD(cEnd, line.getOffset() + line.getLength(), false)) {
						command.text= "\"\""; //$NON-NLS-1$
						linkedModeType= 2 | AUTODELETE;
						break KEY;
					}
					break KEY;
				}
				return false;
			case '\'':
				if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
						&& this.settings.closeQuotes
						&& !isValueChar(cEnd) && !isValueChar(command.offset - 1) ) {
					final IRegion line= this.document.getLineInformationOfOffset(cEnd);
					if (!isClosedQuotedD(cEnd, line.getOffset() + line.getLength(), false)) {
						command.text= "\'\'"; //$NON-NLS-1$
						linkedModeType= 2 | AUTODELETE;
						break KEY;
					}
					break KEY;
				}
				return false;
			case '\n':
				if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
						|| (contentType == IYamlDocumentConstants.YAML_COMMENT_CONTENT_TYPE
								&& YAML_DEFAULT_CONTENT_CONSTRAINT.matches(
										partition.getTreeNode().getParent().getType().getPartitionType()) )) {
					command.text= TextUtilities.getDefaultLineDelimiter(this.document);
					smartIndentOnNewLine(command, contentType);
					break KEY;
				}
				return false;
			default:
				assert (false);
				return false;
			}
			
			if (command.text.length() > 0 && this.editor.isEditable(true)) {
				this.viewer.getTextWidget().setRedraw(false);
				try {
					this.document.replace(command.offset, command.length, command.text);
					final int cursor= (command.caretOffset >= 0) ? command.caretOffset :
							command.offset+command.text.length();
					selection= new TextSelection(this.document, cursor, 0);
					this.viewer.setSelection(selection, true);
					
					if (linkedModeType >= 0) {
						if (linkedModeOffset < 0) {
							linkedModeOffset= command.offset;
						}
						createLinkedMode(linkedModeOffset, c, linkedModeType).enter();
					}
				}
				finally {
					this.viewer.getTextWidget().setRedraw(true);
				}
			}
			return true;
		}
		catch (final Exception e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, YamlUI.PLUGIN_ID, -1,
					"An error occurred when customizing action for pressed key in YAML auto edit strategy.", //$NON-NLS-1$
					e ));
		}
		finally {
			this.ignoreCommands= false;
			quitCustomization();
		}
		return false;
	}
	
	
	private int smartIndentOnTab(final DocumentCommand c) throws BadLocationException {
		final IRegion line= this.document.getLineInformation(this.document.getLineOfOffset(c.offset));
		int first;
		this.scanner.configure(this.document);
		first= this.scanner.findAnyNonBlankBackward(c.offset, line.getOffset()-1, false);
		if (first != ITokenScanner.NOT_FOUND) { // not first char
			return 0;
		}
		final IndentUtil indentation= new IndentUtil(this.document, this.codeStyle);
		final int column= indentation.getColumnAtOffset(c.offset);
		if (this.settings.tabAction != ISmartInsertSettings.TabAction.INSERT_TAB_CHAR) {
			c.text= indentation.createIndentCompletionString(column);
		}
		return 1;
	}
	
	private void smartIndentOnNewLine(final DocumentCommand c, final String partitionType)
			throws BadLocationException, BadPartitioningException, CoreException {
		final int before= c.offset - 1;
		final int behind= c.offset + c.length;
		final String lineDelimiter= c.text;
		final char cBefore;
		if (YAML_DEFAULT_CONTENT_CONSTRAINT.matches(partitionType)
				&& before >= 0 && behind < this.validRange.getOffset() + this.validRange.getLength()
				&& (((cBefore= this.document.getChar(before)) == '[' && this.document.getChar(behind) == ']')
						|| (cBefore == '{' && this.document.getChar(behind) == '}') )) {
			c.text= c.text + c.text;
		}
		smartIndentAfterNewLine1(c, lineDelimiter);
	}
	
	private void smartIndentAfterNewLine1(final DocumentCommand c, final String lineDelimiter)
			throws BadLocationException, BadPartitioningException, CoreException {
		final StringBuilder sb= new StringBuilder(c.text);
		int nlIndex= lineDelimiter.length();
		final int line= this.document.getLineOfOffset(c.offset);
		int checkOffset= Math.max(0, c.offset);
		
		final ITypedRegion partition= this.document.getPartition(
				this.scanner.getDocumentPartitioning(), checkOffset, true );
		if (partition.getType() == IYamlDocumentConstants.YAML_COMMENT_CONTENT_TYPE) {
			checkOffset= partition.getOffset();
		}
		
		final IndentUtil util= new IndentUtil(this.document, this.codeStyle);
		final int column= util.getLineIndent(line, false)[IndentUtil.COLUMN_IDX];
		
		if (checkOffset > 0) {
			// new block?:
			this.scanner.configure(this.document);
			final int match= this.scanner.findAnyNonBlankBackward(checkOffset, this.document.getLineOffset(line) - 1, false);
			final char cBefore;
			if (match >= 0 && ((cBefore= this.document.getChar(match)) == '[' || cBefore == '{')) {
				final String indent= util.createIndentString(util.getNextLevelColumn(column, 1));
				sb.insert(nlIndex, indent);
				nlIndex+= indent.length() + lineDelimiter.length();
			}
		}
		
		if (nlIndex <= sb.length()) {
			sb.insert(nlIndex, util.createIndentString(column));
		}
		c.text= sb.toString();
	}
	
	
	private LinkedModeUI createLinkedMode(final int offset, final char type, final int mode)
			throws BadLocationException {
		final LinkedModeModel model= new LinkedModeModel();
		int pos= 0;
		
		final LinkedPositionGroup group= new LinkedPositionGroup();
		final LinkedPosition position= YamlBracketLevel.createPosition(type, this.document,
				offset + 1, 0, pos++ );
		group.addPosition(position);
		model.addGroup(group);
		
		model.forceInstall();
		
		final YamlBracketLevel level= new YamlBracketLevel(this.document,
				this.scanner.getDocumentPartitioning(),
				ImCollections.newList(position), (mode & 0xffff0000) );
		
		/* create UI */
		final LinkedModeUI ui= new LinkedModeUI(model, this.viewer);
		ui.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
		ui.setExitPosition(this.viewer, offset + (mode & 0xff), 0, pos);
		ui.setSimpleMode(true);
		ui.setExitPolicy(level);
		return ui;
	}
	
}
