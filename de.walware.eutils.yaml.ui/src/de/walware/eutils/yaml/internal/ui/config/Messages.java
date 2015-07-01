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

package de.walware.eutils.yaml.internal.ui.config;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String TextStyles_DefaultCodeCategory_label;
	public static String TextStyles_DefaultCodeCategory_short;
	public static String TextStyles_Default_label;
	public static String TextStyles_Default_description;
	public static String TextStyles_Indicators_label;
	public static String TextStyles_Indicators_description;
	public static String TextStyles_SeqMapBrackets_label;
	public static String TextStyles_SeqMapBrackets_description;
	public static String TextStyles_Keys_label;
	public static String TextStyles_Keys_description;
	public static String TextStyles_Tags_label;
	public static String TextStyles_Tags_description;
	
	public static String TextStyles_ProcessorCategory_label;
	public static String TextStyles_ProcessorCategory_short;
	public static String TextStyles_DocumentSeparators_label;
	public static String TextStyles_DocumentSeparators_description;
	public static String TextStyles_Directives_label;
	public static String TextStyles_Directives_description;
	
	public static String TextStyles_CommentCategory_label;
	public static String TextStyles_Comment_label;
	public static String TextStyles_Comment_description;
	public static String TextStyles_TaskTag_label;
	public static String TextStyles_TaskTag_description;
	
	public static String EditorOptions_SmartInsert_label;
	public static String EditorOptions_SmartInsert_AsDefault_label;
	public static String EditorOptions_SmartInsert_description;
	public static String EditorOptions_SmartInsert_TabAction_label;
	public static String EditorOptions_SmartInsert_CloseAuto_label;
	public static String EditorOptions_SmartInsert_CloseBrackets_label;
	public static String EditorOptions_SmartInsert_CloseQuotes_label;
	
	public static String EditorOptions_Folding_Enable_label;
	public static String EditorOptions_Folding_RestoreState_Enable_label;
	public static String EditorOptions_MarkOccurrences_Enable_label;
	public static String EditorOptions_ProblemChecking_Enable_label;
	public static String EditorOptions_AnnotationAppearance_info;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
