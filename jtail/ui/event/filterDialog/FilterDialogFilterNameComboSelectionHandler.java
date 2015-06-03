package jtail.ui.event.filterDialog;

import jtail.logic.filter.Filter;
import jtail.logic.filter.FilterHandler;
import jtail.ui.dialog.FilterDialog;
import jtail.util.Value;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;

public class FilterDialogFilterNameComboSelectionHandler extends SelectionAdapter {
	private Combo comboWidget;
	private FilterDialog filterDialog;

	public FilterDialogFilterNameComboSelectionHandler(FilterDialog filterDialog, Combo widget) {
		this.filterDialog = filterDialog;
		this.comboWidget = widget;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		String filterName = this.comboWidget.getText();
		if (Value.isEmptyOrNull(filterName)){
			return;
		}
		
		Filter filter = FilterHandler.getFilterByFilterName(filterName);
		this.filterDialog.displayFilter(filter);
		this.filterDialog.setCurrentFilter(filter);
		this.comboWidget.traverse(SWT.TRAVERSE_TAB_NEXT);
	}
	
}