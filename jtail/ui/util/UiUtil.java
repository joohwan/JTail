package jtail.ui.util;

import jtail.resources.Resource;
import jtail.util.Value;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class UiUtil {
	public static Label addLabel(Composite parent, int style, String text, GridDataFactory gdf) {
		Label label = new Label(parent, style);
		label.setText(text);
		gdf.copy().span(1, 1).applyTo(label);
		return label;
	}
	
	public static Label createLabel(Composite parent, int style, String text) {
		Label label = new Label(parent, style);
		label.setText(text);		
		return label;
	}
	
	public static String buildMultilineTooltip(String resourceKeyPrefix, int size) {
		StringBuilder sb = new StringBuilder("");
		for (int i = 1; i <= size; i++){
			sb.append(Resource.getString(resourceKeyPrefix+i));
			if (i < size){
				sb.append(Value.NL);	
			}			
		}
		return sb.toString();
	}
}
