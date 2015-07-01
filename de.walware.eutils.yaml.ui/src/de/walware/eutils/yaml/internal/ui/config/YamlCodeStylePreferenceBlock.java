/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.eutils.yaml.internal.ui.config;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.text.IIndentSettings.IndentationType;
import de.walware.ecommons.text.ui.settings.IndentSettingsUI;
import de.walware.ecommons.ui.CombineStatusChangeListener;
import de.walware.ecommons.ui.util.LayoutUtil;

import de.walware.eutils.yaml.core.YamlCodeStyleSettings;


/**
 * A PreferenceBlock for YamlCodeStyleSettings (code formatting preferences).
 */
public class YamlCodeStylePreferenceBlock extends ManagedConfigurationBlock {
	
	
	private YamlCodeStyleSettings model;
	
	private IndentSettingsUI stdIndentSettings;
//	private Text indentBlockDepthControl;
	
	private final CombineStatusChangeListener statusListener;
	
	
	public YamlCodeStylePreferenceBlock(final IProject project, final IStatusChangeListener statusListener) {
		super(project);
		this.statusListener= new CombineStatusChangeListener(statusListener);
		setStatusListener(this.statusListener);
	}
	
	
	@Override
	protected void createBlockArea(final Composite pageComposite) {
		final Map<Preference<?>, String> prefs= new HashMap<>();
		
		prefs.put(YamlCodeStyleSettings.TAB_SIZE_PREF, YamlCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(YamlCodeStyleSettings.INDENT_DEFAULT_TYPE_PREF, YamlCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(YamlCodeStyleSettings.INDENT_SPACES_COUNT_PREF, YamlCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(YamlCodeStyleSettings.REPLACE_CONVERSATIVE_PREF, YamlCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(YamlCodeStyleSettings.REPLACE_TABS_WITH_SPACES_PREF, YamlCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(YamlCodeStyleSettings.INDENT_BLOCK_DEPTH_PREF, YamlCodeStyleSettings.INDENT_GROUP_ID);
		
		setupPreferenceManager(prefs);
		
		this.model= new YamlCodeStyleSettings(0);
		this.stdIndentSettings= new IndentSettingsUI() {
			@Override
			protected IndentationType[] getAvailableIndentationTypes() {
				return new IndentationType[] { IndentationType.SPACES };
			}
		};
		
		final Composite mainComposite= new Composite(pageComposite, SWT.NONE);
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainComposite.setLayout(LayoutUtil.createCompositeGrid(2));
		
		final TabFolder folder= new TabFolder(mainComposite, SWT.NONE);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		{	final TabItem item= new TabItem(folder, SWT.NONE);
			item.setText(this.stdIndentSettings.getGroupLabel());
			item.setControl(createIndentControls(folder));
		}
//		{	final TabItem item= new TabItem(folder, SWT.NONE);
//			item.setText("&Line Wrapping");
//			item.setControl(createLineControls(folder));
//		}
		
		initBindings();
		updateControls();
	}
	
	private Control createIndentControls(final Composite parent) {
		final Composite composite= new Composite(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.createTabGrid(2));
		
		this.stdIndentSettings.createControls(composite);
		LayoutUtil.addSmallFiller(composite, false);
		
//		final Composite depthComposite= new Composite(composite, SWT.NONE);
//		depthComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
//		depthComposite.setLayout(LayoutUtil.createCompositeGrid(4));
//		this.indentBlockDepthControl= createIndentDepthLine(depthComposite, Messages.CodeStyle_Indent_IndentInBlocks_label);
//		
//		LayoutUtil.addSmallFiller(depthComposite, false);
		
		LayoutUtil.addSmallFiller(composite, false);
		return composite;
	}
	
	private Text createIndentDepthLine(final Composite composite, final String label) {
		final Label labelControl= new Label(composite, SWT.LEFT);
		labelControl.setText(label);
		labelControl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		final Text textControl= new Text(composite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		final GridData gd= new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.widthHint= LayoutUtil.hintWidth(textControl, 2);
		textControl.setLayoutData(gd);
		final Label typeControl= new Label(composite, SWT.LEFT);
		typeControl.setText(this.stdIndentSettings.getLevelUnitLabel());
		typeControl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		LayoutUtil.addGDDummy(composite);
		
		return textControl;
	}
	
	private Control createLineControls(final Composite parent) {
		final Composite composite= new Composite(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.createTabGrid(2));
		
		this.stdIndentSettings.addLineWidth(composite);
		
		return composite;
	}
	
	@Override
	protected void addBindings(final DataBindingSupport db) {
		this.stdIndentSettings.addBindings(db, this.model);
		
//		db.getContext().bindValue(
//				SWTObservables.observeText(this.indentBlockDepthControl, SWT.Modify),
//				BeansObservables.observeValue(db.getRealm(), this.model, YamlCodeStyleSettings.INDENT_BLOCK_DEPTH_PROP),
//				new UpdateValueStrategy().setAfterGetValidator(new IntegerValidator(1, 10, Messages.CodeStyle_Indent_IndentInBlocks_error_message)),
//				null );
	}
	
	@Override
	protected void updateControls() {
		this.model.load(this);
		this.model.resetDirty();
		getDataBinding().getContext().updateTargets();  // required for invalid target values
	}
	
	@Override
	protected void updatePreferences() {
		if (this.model.isDirty()) {
			this.model.resetDirty();
			setPrefValues(this.model.toPreferencesMap());
		}
	}
	
}
