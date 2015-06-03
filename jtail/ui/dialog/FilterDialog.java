/*
 * File: FilterDialog.java
 *
 * Version:    01.00.00
 *
 *   (c) Copyright 2009 - TELUS Health Solutions
 *                      ALL RIGHTS RESERVED
 *       
 *   PROPRIETARY RIGHTS NOTICE All rights reserved.  This material
 *   contains the valuable properties and trade secrets of TELUS Health Solutions, 
 *   embodying substantial creative efforts and confidential information, ideas 
 *   and expressions, no part of which may be reproduced, distributed or transmitted 
 *   in any form or by any means electronic, mechanical or otherwise, including
 *   photocopying and recording or in conjunction with any information storage 
 *   or retrieval system without the express written permission of TELUS Health Solutions.
 *   
 *   Description:
 *       Users input search regular expression and save it to use it later.
 *
 * Change Control:
 *    Date         Ver            Who          Revision History
 * ----------- ----------- ------------------- --------------------------------------------- 
 * 23-Apr-2009                 Joohwan Oh        Class is created. 
 *  
 */

package jtail.ui.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jtail.logic.filter.Filter;
import jtail.logic.filter.FilterType;
import jtail.logic.filter.RegExpOption;
import jtail.resources.Resource;
import jtail.ui.event.SaveMode;
import jtail.ui.event.filterDialog.FilterDialogDeleteButtonEventHandler;
import jtail.ui.event.filterDialog.FilterDialogFilterNameComboFocusHandler;
import jtail.ui.event.filterDialog.FilterDialogFilterNameComboSelectionHandler;
import jtail.ui.event.filterDialog.FilterDialogNewButtonEventHandler;
import jtail.ui.event.filterDialog.FilterDialogSaveButtonEventHandler;
import jtail.ui.util.UiUtil;
import jtail.util.FileUtil;
import jtail.util.Value;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * The Class FilterDialog.
 * 
 * @author Joohwan Oh, 2009-04-22
 */
public class FilterDialog extends Dialog {
	private GridLayout filterGroupLayout;
	private TextViewer regexpText;
	private TextViewer testRegexpText;
	private Combo filterNameCombo;
	private Button includeRadio;
	private Button excludeRadio;
	
	private Button multiLineOptionButton;
	private Button dotAllOptionButton;
	private Button caseInsensitiveOptionButton;
	private Button literalOptionButton;
	private Button unixLinesOptionButton;
	private Button commentsOptionButton;
	private Button unicodeCaseOptionButton;
	private Button canonEqOptionButton;	
	
	private SaveMode saveMode;
	private Filter currentFilter;
		
	/**
	 * Instantiates a new define regex dialog.
	 * 
	 * @param parentShell the parent shell
	 */
	public FilterDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		Shell shell = this.getShell();
		shell.setText(Resource.getString("filter"));
		
		GridDataFactory gdf = GridDataFactory.fillDefaults();
		
		createTopLayout(comp);		
		
		Group filterGroup = createFilterGroup(comp, gdf);		
		
		addFilterTypePart(gdf, filterGroup);
		
		addRegularExpressionTextPart(gdf, filterGroup);
		
		addRegularExpressionOptionCheckboxPart(filterGroup, gdf);
		
		addTestRegularExpressionPart(filterGroup, gdf);
				
		addNamePart(gdf, filterGroup);
				
		setNewMode(); //this.setInitialFocus();

		return comp;
	}

	

	private void addRegularExpressionTextPart(GridDataFactory gdf, Group filterGroup) {
		UiUtil.addLabel(filterGroup, SWT.NULL, Resource.getString("regular.expression")+": ", gdf);		
		addRegularExpressionTextViewer(filterGroup, gdf);
	}

	private void addNamePart(GridDataFactory gdf, Group filterGroup) {
		UiUtil.addLabel(filterGroup, SWT.NULL, Resource.getString("name")+": ", gdf);		
		addFilterNameCombo(filterGroup, gdf);
	}

	private void addFilterTypePart(GridDataFactory gdf, Group filterGroup) {
		UiUtil.addLabel(filterGroup, SWT.NULL, Resource.getString("filter.type")+": ", gdf);		
		addIncludeRadioButton(filterGroup, gdf);		
		addExcludeRadioButton(filterGroup, gdf);
	}

	private void createTopLayout(Composite parent) {
		GridLayout topLayout = (GridLayout) parent.getLayout();
		topLayout.numColumns = 1;
		topLayout.makeColumnsEqualWidth = true;
	}

	private Group createFilterGroup(Composite parent, GridDataFactory gdf) {
		Group filterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		filterGroup.setText(Resource.getString("filter"));
		filterGroupLayout = new GridLayout();		
		filterGroupLayout.numColumns = 5;
		filterGroupLayout.makeColumnsEqualWidth = false;
		filterGroup.setLayout(filterGroupLayout);
		gdf.copy().span(1, 1).grab(true, true).applyTo(filterGroup);
		return filterGroup;
	}
		
	private void addRegularExpressionOptionCheckboxPart(Composite parent, GridDataFactory gdf) {
		UiUtil.addLabel(parent, SWT.NULL, "", gdf);
		addMultiLineCheckbox(parent, gdf);
		
		UiUtil.addLabel(parent, SWT.NULL, "", gdf);
		addDotAllCheckbox(parent, gdf);
		
		UiUtil.addLabel(parent, SWT.NULL, "", gdf);
		addCaseInsensitiveCheckbox(parent, gdf);
		
		UiUtil.addLabel(parent, SWT.NULL, "", gdf);
		addLiteralCheckbox(parent, gdf);
		
		UiUtil.addLabel(parent, SWT.NULL, "", gdf);
		addUnixLinesCheckbox(parent, gdf);
		
		UiUtil.addLabel(parent, SWT.NULL, "", gdf);
		addCommentsCheckbox(parent, gdf);
		
		UiUtil.addLabel(parent, SWT.NULL, "", gdf);
		addUnicodeCaseCheckbox(parent, gdf);
		
		UiUtil.addLabel(parent, SWT.NULL, "", gdf);
		addCanonEqOptionButtonCheckbox(parent, gdf);
	}

	private void addFilterNameCombo(Composite parent, GridDataFactory gdf) {
		filterNameCombo = new Combo(parent, SWT.DROP_DOWN);
		filterNameCombo.setVisibleItemCount(1000);
		gdf.copy().span(4, 1).hint(300, SWT.DEFAULT).grab(true, true).applyTo(filterNameCombo);
		//saveAsCombo.addKeyListener(new SearchComboKeyListener(searchCombo));
		filterNameCombo.addFocusListener(new FilterDialogFilterNameComboFocusHandler(filterNameCombo));
		filterNameCombo.addSelectionListener(new FilterDialogFilterNameComboSelectionHandler(this, filterNameCombo));
	}

	private void addRegularExpressionTextViewer(Composite parent, GridDataFactory gdf) {
		regexpText = new TextViewer(parent, SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		//text = new TextViewer(defineRegexGroup, SWT.MULTI | SWT.V_SCROLL);
		regexpText.setDocument(new Document());
		StyledText styledText = regexpText.getTextWidget();
		gdf.copy().span(4,1).hint(300, 100).grab(true, true).applyTo(styledText);
	}
	
	private void addMultiLineCheckbox(Composite parent, GridDataFactory gdf) {
		multiLineOptionButton = new Button(parent, SWT.CHECK);
		multiLineOptionButton.setText(Resource.getString("multiline"));
		multiLineOptionButton.setToolTipText(UiUtil.buildMultilineTooltip("multiline.tooltip", 5));
		gdf.copy().span(filterGroupLayout.numColumns - 1, 1).applyTo(multiLineOptionButton);
	}
		
	private void addDotAllCheckbox(Composite parent, GridDataFactory gdf) {
		dotAllOptionButton = new Button(parent, SWT.CHECK);
		dotAllOptionButton.setText(Resource.getString("dotall"));
		dotAllOptionButton.setToolTipText(UiUtil.buildMultilineTooltip("dotall.tooltip", 5));
		gdf.copy().span(filterGroupLayout.numColumns - 1, 1).applyTo(dotAllOptionButton);
	}
	
	private void addCaseInsensitiveCheckbox(Composite parent, GridDataFactory gdf) {
		caseInsensitiveOptionButton = new Button(parent, SWT.CHECK);
		caseInsensitiveOptionButton.setText(Resource.getString("caseinsensitive"));
		caseInsensitiveOptionButton.setToolTipText(UiUtil.buildMultilineTooltip("caseinsensitive.tooltip", 5));
		gdf.copy().span(filterGroupLayout.numColumns - 1, 1).applyTo(caseInsensitiveOptionButton);
	}
	
	private void addLiteralCheckbox(Composite parent, GridDataFactory gdf) {
		literalOptionButton = new Button(parent, SWT.CHECK);
		literalOptionButton.setText(Resource.getString("literal"));
		literalOptionButton.setToolTipText(UiUtil.buildMultilineTooltip("literal.tooltip", 5));
		gdf.copy().span(filterGroupLayout.numColumns - 1, 1).applyTo(literalOptionButton);
	}
	
	private void addUnixLinesCheckbox(Composite parent, GridDataFactory gdf) {
		unixLinesOptionButton = new Button(parent, SWT.CHECK);
		unixLinesOptionButton.setText(Resource.getString("unixlines"));
		unixLinesOptionButton.setToolTipText(UiUtil.buildMultilineTooltip("unixlines.tooltip", 3));
		gdf.copy().span(filterGroupLayout.numColumns - 1, 1).applyTo(unixLinesOptionButton);
	}
	
	private void addCommentsCheckbox(Composite parent, GridDataFactory gdf) {
		commentsOptionButton = new Button(parent, SWT.CHECK);
		commentsOptionButton.setText(Resource.getString("comments"));
		commentsOptionButton.setToolTipText(UiUtil.buildMultilineTooltip("comments.tooltip", 3));
		gdf.copy().span(filterGroupLayout.numColumns - 1, 1).applyTo(commentsOptionButton);
	}	
	
	private void addUnicodeCaseCheckbox(Composite parent, GridDataFactory gdf) {
		unicodeCaseOptionButton = new Button(parent, SWT.CHECK);
		unicodeCaseOptionButton.setText(Resource.getString("unicodecase"));
		unicodeCaseOptionButton.setToolTipText(UiUtil.buildMultilineTooltip("unicodecase.tooltip", 6));
		gdf.copy().span(filterGroupLayout.numColumns - 1, 1).applyTo(unicodeCaseOptionButton);
	}
	
	private void addCanonEqOptionButtonCheckbox(Composite parent, GridDataFactory gdf) {
		canonEqOptionButton = new Button(parent, SWT.CHECK);
		canonEqOptionButton.setText(Resource.getString("canonEq"));
		canonEqOptionButton.setToolTipText(UiUtil.buildMultilineTooltip("canonEq.tooltip", 6));
		gdf.copy().span(filterGroupLayout.numColumns - 1, 1).applyTo(canonEqOptionButton);
	}

	private void addExcludeRadioButton(Composite parent, GridDataFactory gdf) {
		excludeRadio = new Button(parent, SWT.RADIO);
		excludeRadio.setText(Resource.getString("exclude"));
		gdf.copy().span(3, 1).applyTo(excludeRadio);
	}

	private void addIncludeRadioButton(Composite parent, GridDataFactory gdf) {
		includeRadio = new Button(parent, SWT.RADIO);
		includeRadio.setText(Resource.getString("include"));
		includeRadio.setSelection(true);
		gdf.copy().span(1, 1).applyTo(includeRadio);
	}
	
	private void addTestRegularExpressionPart(Group filterGroup, GridDataFactory gdf) {
		UiUtil.addLabel(filterGroup, SWT.NULL, Resource.getString("Test regular expression")+": ", gdf);		
		addTestRegularExpressionTextViewer(filterGroup, gdf);
		addTestRegularExpressionButtons(filterGroup, gdf);
	}	
		
	private void addTestRegularExpressionTextViewer(Composite parent, GridDataFactory gdf) {
		testRegexpText = new TextViewer(parent, SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		testRegexpText.setDocument(new Document());
		StyledText styledText = testRegexpText.getTextWidget();
		gdf.copy().span(4,1).hint(300, 100).grab(true, true).applyTo(styledText);
	}
	
	private void addTestRegularExpressionButtons(Composite parent, GridDataFactory gdf) {
		UiUtil.addLabel(parent, SWT.NULL, "", gdf);
		
		Button openButton = new Button(parent, SWT.PUSH);
		openButton.setText(Resource.getString("open"));
		gdf.copy().span(1,1).applyTo(openButton);
		openButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				openTestRegExpFile();
			}				
		});	
		
		Button testButton = new Button(parent, SWT.PUSH);
		testButton.setText(Resource.getString("test"));
		gdf.copy().span(1, 1).applyTo(testButton);
		
		Label label = UiUtil.createLabel(parent, SWT.NULL, "");
		gdf.copy().span(2, 1).applyTo(label);
	}
	
	private void openTestRegExpFile() {
		File file = DialogUtil.displayFileOpenDialog(getShell());
		
		// if user did not select any file, return.
		if (file == null) {
			return;
		}

		this.testRegexpText.getDocument().set(FileUtil.readFile(file));		
	}	
		
	public String getFilterName(){
		return this.filterNameCombo.getText().trim();
	}
	
	public FilterType getFilterType(){
		return (this.includeRadio.getSelection() ? FilterType.INCLUDE : FilterType.EXCLUDE);
	}
	
	public String getRegexp(){
		return this.regexpText.getDocument().get().trim();
	}
	
	
	/**
	 * Adds buttons to this dialog's button bar.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method adds
	 * standard ok and cancel buttons using the <code>createButton</code>
	 * framework method. These standard buttons will be accessible from
	 * <code>getCancelButton</code>, and <code>getOKButton</code>.
	 * Subclasses may override.
	 * </p>
	 * 
	 * @param parent
	 *            the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		Button newButton = createButton(parent, IDialogConstants.YES_ID, Resource.getString("new"), false);
		Button saveButton = createButton(parent, IDialogConstants.YES_ID, Resource.getString("save"), false);
		Button deleteButton = createButton(parent, IDialogConstants.YES_ID, Resource.getString("delete"), false);
		Button closeButton = createButton(parent, IDialogConstants.CANCEL_ID, Resource.getString("close"), true);
		
		newButton.addSelectionListener(new FilterDialogNewButtonEventHandler(this));
		saveButton.addSelectionListener(new FilterDialogSaveButtonEventHandler(this));
		deleteButton.addSelectionListener(new FilterDialogDeleteButtonEventHandler(this));
	}
	
	public List<String> checkMandatoryInput() {
		List<String> missedInputFieldNameList = new ArrayList<String>();
		if (this.regexpText.getDocument().getLength() == 0){
			missedInputFieldNameList.add(Resource.getString("regular.expression"));
		}
		if (Value.isEmptyOrNull(this.filterNameCombo.getText())) {
			missedInputFieldNameList.add(Resource.getString("name"));
		}
		return missedInputFieldNameList;
	}

	public void displayFilter(Filter filter) {
		if (filter == null){
			return;
		}
		
		if (filter.getFilterType() == FilterType.INCLUDE){
			this.includeRadio.setSelection(true);
			this.excludeRadio.setSelection(false);
		} else {
			this.includeRadio.setSelection(false);
			this.excludeRadio.setSelection(true);
		}
		
		this.regexpText.getDocument().set(filter.getRegExp());
		
		displayRegExpOptions(filter.getRegExpOptionList());
		
		String filterName = filter.getFilterName();
		String[] filterNames = this.filterNameCombo.getItems();
		for (int i = 0; i < filterNames.length; i++){
			if (!Value.isEmptyOrNull(filterNames[i])
					&& filterNames[i].equals(filterName)) {
				this.filterNameCombo.select(i);
				break;
			}
		}		
	}
		
	public void selectIncludeRadioButton(){
		this.includeRadio.setSelection(true);
		this.excludeRadio.setSelection(false);
	}
	
	public void selectExcludeRadioButton(){
		this.includeRadio.setSelection(false);
		this.excludeRadio.setSelection(true);
	}
		
	public void selectDefaultRadioButton(){
		this.selectIncludeRadioButton();
	}
	
	public void clearRegexp(){
		this.regexpText.getDocument().set("");		
	}
	
	public void clearFilterName(){
		this.filterNameCombo.deselectAll();
	}
	
	public void setInitialFocus(){
		this.regexpText.getTextWidget().setFocus();
	}
	
	public void setNewMode(){
		selectDefaultRadioButton();
		clearRegexp();
		clearFilterName();
		setInitialFocus();
		
		setSaveMode(SaveMode.NEW);
	}

	public void setSaveMode(SaveMode saveMode) {
		this.saveMode = saveMode;		
	}
	
	public SaveMode getSaveMode() {
		return this.saveMode;		
	}

	public Filter getCurrentFilter() {
		return this.currentFilter;
	}

	public void setCurrentFilter(Filter filter) {
		this.currentFilter = filter;
		this.setSaveMode(SaveMode.UPDATE);
	}
	
	public List<RegExpOption> getRegExpOptionList(){
		List<RegExpOption> list = new ArrayList<RegExpOption>();
		if (this.canonEqOptionButton.getSelection()){
			list.add(RegExpOption.CANON_EQ);
		}
		if (this.caseInsensitiveOptionButton.getSelection()){
			list.add(RegExpOption.CASE_INSENSITIVE);
		}
		if (this.commentsOptionButton.getSelection()){
			list.add(RegExpOption.COMMENTS);
		}
		if (this.dotAllOptionButton.getSelection()){
			list.add(RegExpOption.DOTALL);
		}
		if (this.multiLineOptionButton.getSelection()){
			list.add(RegExpOption.MULTILINE);
		}
		if (this.unicodeCaseOptionButton.getSelection()){
			list.add(RegExpOption.UNICODE_CASE);
		}
		if (this.unixLinesOptionButton.getSelection()){
			list.add(RegExpOption.UNIX_LINES);
		}
		if (this.literalOptionButton.getSelection()){
			list.add(RegExpOption.LITERAL);
		}
		return list;
	}
	
	private void displayRegExpOptions(List<RegExpOption> regExpOptionList) {
		clearRegExpOptions();
		
		for (RegExpOption option : regExpOptionList){
			if (option == RegExpOption.CANON_EQ){
				this.canonEqOptionButton.setSelection(true);
			}
			if (option == RegExpOption.CASE_INSENSITIVE){
				this.caseInsensitiveOptionButton.setSelection(true);
			}
			if (option == RegExpOption.COMMENTS){
				this.commentsOptionButton.setSelection(true);
			}
			if (option == RegExpOption.DOTALL){
				this.dotAllOptionButton.setSelection(true);
			}
			if (option == RegExpOption.MULTILINE){
				this.multiLineOptionButton.setSelection(true);
			}
			if (option == RegExpOption.UNICODE_CASE){
				this.unicodeCaseOptionButton.setSelection(true);
			}
			if (option == RegExpOption.UNIX_LINES){
				this.unixLinesOptionButton.setSelection(true);
			}
			if (option == RegExpOption.LITERAL){
				this.literalOptionButton.setSelection(true);
			}
		}		
	}

	private void clearRegExpOptions() {
		this.canonEqOptionButton.setSelection(false);
		this.caseInsensitiveOptionButton.setSelection(false);
		this.commentsOptionButton.setSelection(false);
		this.dotAllOptionButton.setSelection(false);
		this.multiLineOptionButton.setSelection(false);
		this.unicodeCaseOptionButton.setSelection(false);
		this.unixLinesOptionButton.setSelection(false);
		this.literalOptionButton.setSelection(false);		
	}

}
