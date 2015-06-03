package jtail.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jtail.logic.filter.Filter;
import jtail.logic.filter.FilterHandler;
import jtail.resources.Resource;
import jtail.ui.tab.TailTab;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class UseFiltersDialog extends Dialog {
	private TailTab tailTab;
	private org.eclipse.swt.widgets.List availableList;
	private org.eclipse.swt.widgets.List selectedList;
		
	public UseFiltersDialog(Shell parentShell, TailTab tailTab) {
		super(parentShell);
		this.tailTab = tailTab;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		Shell shell = this.getShell();
		shell.setText(Resource.getString("use.filters"));

		GridDataFactory gdf = createTopLayout(comp);
		addAvailableFiltersPart(comp, gdf);
		addArrowButtonsPart(comp, gdf);
		addSelectedFiltersPart(comp, gdf);
		
		populateFiltersList();

		return comp;
	}
	
	private void populateFiltersList() {
		List<Filter> usedFilterList = tailTab.getFilterList();
		List<Filter> availableFilterList = FilterHandler.getAvailableFilterList(usedFilterList);		
		
		populateAvailableList(availableFilterList);
		populateSelectedList(usedFilterList);		
	}

	private void populateSelectedList(List<Filter> usedFilterList) {
		this.selectedList.removeAll();
		for (Filter filter : usedFilterList){
			this.selectedList.add(filter.getFilterName());
		}			
	}

	private void populateAvailableList(List<Filter> availableFilterList) {
		this.availableList.removeAll();
		for (Filter filter : availableFilterList){
			this.availableList.add(filter.getFilterName());
		}		
	}

	private GridDataFactory createTopLayout(Composite comp) {
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 3;
		topLayout.makeColumnsEqualWidth = false;
		comp.setLayout(topLayout);

		GridDataFactory gdf = GridDataFactory.fillDefaults();
		return gdf;
	}

	private void addAvailableFiltersPart(Composite comp, GridDataFactory gdf) {
		Composite availableFiltersComposite = new Composite(comp, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		availableFiltersComposite.setLayout(layout);
		
		addLabel(availableFiltersComposite, gdf, Resource.getString("available.filters"));
		addAvailableFiltersList(availableFiltersComposite, gdf);		
	}
	
	private void addArrowButtonsPart(Composite parent, GridDataFactory gdf) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		comp.setLayout(layout);
		
		addEmptyLabel(comp, gdf);	
		addToRightButton(comp, gdf);	
		addToLeftButton(comp, gdf);	
		addEmptyLabel(comp, gdf);	
		addToRightAllButton(comp, gdf);	
		addToLeftAllButton(comp, gdf);	
		
	}
	
	private void addSelectedFiltersPart(Composite parent, GridDataFactory gdf) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		comp.setLayout(layout);
		
		addLabel(comp, gdf, Resource.getString("selected.filters"));
		addSelectedFiltersList(comp, gdf);		
	}

	private void addSelectedFiltersList(Composite comp, GridDataFactory gdf) {
		selectedList = new org.eclipse.swt.widgets.List(comp, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gdf.copy().span(1, 5).applyTo(selectedList);
		
	}

	private void addEmptyLabel(Composite comp, GridDataFactory gdf) {
		Label label = new Label(comp, SWT.NULL);
		gdf.copy().span(1, 1).applyTo(label);		
	}

	private void addToRightButton(Composite comp, GridDataFactory gdf) {
		Button button = new Button(comp, SWT.PUSH);
		button.setText(">");
		gdf.copy().span(1, 1).applyTo(button);
		button.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveHighlightedAvailableFiltersToSelectedFilterList();
			}
		});		
	}
	
	
	private void addToLeftButton(Composite comp, GridDataFactory gdf) {
		Button button = new Button(comp, SWT.PUSH);
		button.setText("<");
		gdf.copy().span(1, 1).applyTo(button);
		button.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveHighlightedSelectedFiltersToAvailableFilterList();
			}
		});	
	}
	
	private void addToRightAllButton(Composite comp, GridDataFactory gdf) {
		Button button = new Button(comp, SWT.PUSH);
		button.setText(">>");
		gdf.copy().span(1, 1).applyTo(button);
		button.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveAllAvailableFiltersToSelectedFilterList();
			}
		});	
	}
	
	private void addToLeftAllButton(Composite comp, GridDataFactory gdf) {
		Button button = new Button(comp, SWT.PUSH);
		button.setText("<<");
		gdf.copy().span(1, 1).applyTo(button);
		button.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveAllSelectedFiltersToAvailableFilterList();
			}
		});	
	}
		
	private void addLabel(Composite comp, GridDataFactory gdf, String text) {
		Label label = new Label(comp, SWT.NULL);
		label.setText(text);
		gdf.copy().span(1, 1).applyTo(label);		
	}
	
	private void addAvailableFiltersList(Composite availableFiltersComposite, GridDataFactory gdf) {
		availableList = new org.eclipse.swt.widgets.List(availableFiltersComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gdf.copy().span(1, 5).applyTo(availableList);		
	}

	
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		Button okButton = createButton(parent, IDialogConstants.PROCEED_ID, Resource.getString("ok.button.label"), true);
		Button saveButton = createButton(parent, IDialogConstants.YES_ID, Resource.getString("save"), false);
		saveButton.setToolTipText(Resource.getString("useFilterDialog.save.tooltip"));
		Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, Resource.getString("cancel.button.label"), false);
		
		okButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				okButtonPressed();				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				okButtonPressed();	
			}
		});	
		
		saveButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveButtonPressed();				
			}			
		});
	}
	
	private void okButtonPressed() {
		tailTab.setFilterList(FilterHandler.getFilterList(this.selectedList.getItems()));
		tailTab.refreshTail();
		close();
	}
	
	private void saveButtonPressed() {
		saveSelectedFilters();
		tailTab.setFilterList(FilterHandler.getFilterList(this.selectedList.getItems()));
		tailTab.refreshTail();
		close();
	}
	
	private void moveHighlightedAvailableFiltersToSelectedFilterList(){
		moveHighlighted(this.availableList, this.selectedList);			
	}
	
	private void moveHighlightedSelectedFiltersToAvailableFilterList(){
		moveHighlighted(this.selectedList, this.availableList);			
	}
	
	private void moveAllAvailableFiltersToSelectedFilterList(){
		this.availableList.selectAll();
		moveHighlighted(this.availableList, this.selectedList);			
	}
	
	private void moveAllSelectedFiltersToAvailableFilterList(){
		this.selectedList.selectAll();
		moveHighlighted(this.selectedList, this.availableList);			
	}

	private void moveHighlighted(org.eclipse.swt.widgets.List sourceList,
			org.eclipse.swt.widgets.List targetList) {		
		int[] selectedIndices = sourceList.getSelectionIndices();
		if (selectedIndices.length == 0){
			return;
		}
		
		Arrays.sort(selectedIndices);
		List<String> highlightedList = new ArrayList<String>();
		for (int i = 0; i < selectedIndices.length; i++){
			highlightedList.add(sourceList.getItem(selectedIndices[i]));
		}
		
		sourceList.remove(selectedIndices);
		for (String highlighted : highlightedList){
			targetList.add(highlighted);
		}			
	}
	
	private void saveSelectedFilters(){
		FilterHandler.saveFiltersForFile(this.selectedList.getItems(), tailTab.getFile());		
	}
}
