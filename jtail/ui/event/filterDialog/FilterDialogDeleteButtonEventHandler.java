package jtail.ui.event.filterDialog;

import java.util.SortedSet;

import jtail.config.Config;
import jtail.logic.filter.Filter;
import jtail.logic.filter.FilterHandler;
import jtail.ui.dialog.DialogUtil;
import jtail.ui.dialog.FilterDialog;
import jtail.ui.event.SaveMode;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class FilterDialogDeleteButtonEventHandler extends SelectionAdapter {
	private FilterDialog filterDialog;
	
	public FilterDialogDeleteButtonEventHandler(FilterDialog filterDialog){
		this.filterDialog = filterDialog;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (this.filterDialog.getSaveMode() != SaveMode.UPDATE){
			DialogUtil.displayNoDataToDeleteDialog(this.filterDialog.getShell());
			return;
		}
		
		SortedSet<Filter> filterSet = FilterHandler.getAllSavedFilterSet();
		Filter currentFilter = this.filterDialog.getCurrentFilter();
		filterSet.remove(currentFilter);
		Config.saveBean();		
		
		DialogUtil.displayDeleteSuccessDialog(filterDialog.getShell());
		this.filterDialog.setNewMode();
	}

}
