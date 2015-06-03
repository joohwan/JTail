package jtail.ui.event.filterDialog;

import java.util.List;
import java.util.SortedSet;

import jtail.logic.filter.Filter;
import jtail.logic.filter.FilterHandler;
import jtail.ui.dialog.DialogUtil;
import jtail.ui.dialog.FilterDialog;
import jtail.ui.event.SaveMode;
import jtail.util.RegexUtil;
import jtail.util.Value;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class FilterDialogSaveButtonEventHandler extends SelectionAdapter {
	private FilterDialog filterDialog;
	
	public FilterDialogSaveButtonEventHandler(FilterDialog filterDialog){
		this.filterDialog = filterDialog;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		List<String> missedFieldsList = filterDialog.checkMandatoryInput();
		if (!Value.isEmpty(missedFieldsList)) {
			DialogUtil.displayMissedInputDialog(filterDialog.getShell(), missedFieldsList);
			return;
		}
		
		String regexp = filterDialog.getRegexp();
		String regexpCompileErrorMessage = RegexUtil.getCompileErrorMessage(regexp);
		if (!Value.isEmptyOrNull(regexpCompileErrorMessage)) {
			DialogUtil.displayValidationErrorDialog(filterDialog.getShell(), regexpCompileErrorMessage);
			return;
		}
		
		SortedSet<Filter> filterSet = FilterHandler.getAllSavedFilterSet();
		
		if (this.filterDialog.getSaveMode() == SaveMode.NEW){
			if (sameFilterNameExists(filterDialog.getFilterName())){
				DialogUtil.displaySameFilterNameErrorDialog(filterDialog.getShell());
				return;
			}
			Filter filter = new Filter(filterDialog.getFilterName(), filterDialog.getFilterType(),
					filterDialog.getRegexp(), filterDialog.getRegExpOptionList());			
			filterSet.add(filter);				
		} else { // update
			Filter currentFilter = this.filterDialog.getCurrentFilter();
			for (Filter filter : filterSet){
				if (filter.equals(currentFilter)){
					filter.setFilterName(this.filterDialog.getFilterName());
					filter.setFilterType(this.filterDialog.getFilterType());
					filter.setRegExp(this.filterDialog.getRegexp());
					filter.setRegExpOptionList(this.filterDialog.getRegExpOptionList());
				}
			}
		}
		
		FilterHandler.saveFilterSet(filterSet);
		
		DialogUtil.displaySaveSuccessDialog(filterDialog.getShell());
		
	}

	private boolean sameFilterNameExists(String filterName) {
		SortedSet<Filter> filterSet = FilterHandler.getAllSavedFilterSet();
		for (Filter filter : filterSet){
			if (filter != null && filter.getFilterName().equals(filterName)) {
				return true;
			}
		}
		return false;
	}	
	
}