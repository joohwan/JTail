package jtail.ui.viewer;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class SearchHistoryShell extends Shell {
	public SearchHistoryShell(Shell parent){
		super(parent);		
		buildControls();
		this.pack();
		this.open();
	}
	
	private void buildControls() {
		this.setLayout(new GridLayout(10, false));
		GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, false);
		
		Button button = new Button(this, SWT.PUSH);
		button.setText("SearchHistoryViewer");
		gdf.copy().span(5, 1).grab(false, false).align(SWT.CENTER, SWT.CENTER).applyTo(button);
	
	}
}
