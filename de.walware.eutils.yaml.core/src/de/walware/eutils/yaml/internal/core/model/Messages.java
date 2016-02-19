/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.core.model;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String Syntax_GenEscapeSequenceInvalid_messsage;
	public static String Syntax_GenTokenUnexpected_message;
	public static String Syntax_GenTokenUnknown_message;
	
	public static String Syntax_FlowSeqNotClosed_message;
	public static String Syntax_FlowMapNotClosed_message;
	
	public static String Syntax_QuotedScalarNotClosed_message;
	public static String Syntax_QuotedScalarEscapeSequenceInvalid_messsage;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
