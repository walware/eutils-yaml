/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.core.ast;


public interface IYamlAstStatusConstants {
	
	
	int STATUS_MASK_1=                                      0x0_0000_ff00;
	int STATUS_MASK_2=                                      0x0_000f_fff0;
	int STATUS_MASK_3=                                      0x0_0000_000f;
	int STATUS_MASK_123=                                    0x0_000f_ffff;
	
	int STATUSFLAG_REAL_ERROR=                              0x0_0001_0000;
	int STATUSFLAG_SUBSEQUENT=                              0x0_0010_0000;
	
	int STATUS_OK=                                          0x0_0000_0000;
	
	int STATUS1_SYNTAX_INCORRECT_TOKEN=                     0x0_0000_1100;
	int STATUS2_SYNTAX_TOKEN_NOT_CLOSED=                    0x0_0000_1110 | STATUSFLAG_REAL_ERROR;
	int STATUS2_SYNTAX_ESCAPE_INVALID=                      0x0_0000_1120 | STATUSFLAG_REAL_ERROR;
	int STATUS2_SYNTAX_CHAR_INVALID=                        0x0_0000_1130 | STATUSFLAG_REAL_ERROR;
	int STATUS2_SYNTAX_TOKEN_UNKNOWN=                       0x0_0000_1150 | STATUSFLAG_REAL_ERROR;
	int STATUS2_SYNTAX_TOKEN_UNEXPECTED=                    0x0_0000_1160 | STATUSFLAG_REAL_ERROR;
	
	/** The node represents a missing token */
	int STATUS1_SYNTAX_MISSING_TOKEN=                       0x0_0000_1300 | STATUSFLAG_REAL_ERROR;
	int STATUS1_SYNTAX_MISSING_INDICATOR=                   0x0_0000_1310 | STATUSFLAG_REAL_ERROR;
	
	/** The statement is incomplete */
	int STATUS2_SYNTAX_INCOMPLETE_CC=                       0x0_0000_1500 | STATUSFLAG_REAL_ERROR;
	int STATUS2_SYNTAX_COLLECTION_NOT_CLOSED=               0x0_0000_1510 | STATUSFLAG_REAL_ERROR;
	
	int STATUS3_FLOW_SEQ=                                   0x0_0000_0003;
	int STATUS3_FLOW_MAP=                                   0x0_0000_0004;
	int STATUS3_BLOCK_SEQ=                                  0x0_0000_0005;
	int STATUS3_BLOCK_MAP=                                  0x0_0000_0006;
	int STATUS3_SEQ_ENTRY=                                  0x0_0000_0007;
	int STATUS3_MAP_KEY=                                    0x0_0000_0008;
	int STATUS3_MAP_VALUE=                                  0x0_0000_0009;
	
}
