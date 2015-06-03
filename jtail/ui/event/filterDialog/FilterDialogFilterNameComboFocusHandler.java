package jtail.ui.event.filterDialog;

import java.util.SortedSet;
import java.util.TreeSet;

import jtail.logic.filter.Filter;
import jtail.logic.filter.FilterHandler;
import jtail.util.Value;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Combo;

public class FilterDialogFilterNameComboFocusHandler extends FocusAdapter {
	private Combo comboWidget;

	public FilterDialogFilterNameComboFocusHandler(Combo widget) {
		this.comboWidget = widget;
	}

	public void focusGained(FocusEvent e) {
		SortedSet<Filter> filterSet = FilterHandler.getAllSavedFilterSet();
		SortedSet<String> filterNameSet = new TreeSet<String>();		
		if (!Value.isEmpty(filterSet)){
			for (Filter filter : filterSet){
				filterNameSet.add(filter.getFilterName());
			}
		} else {
			filterNameSet.add("");
		}
		String[] items = filterNameSet.toArray(new String[] {});
		this.comboWidget.setItems(items);
	}

	public void focusLost(FocusEvent e) {
	}

}
