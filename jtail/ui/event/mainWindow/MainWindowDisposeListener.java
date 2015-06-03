package jtail.ui.event.mainWindow;

import jtail.config.Config;
import jtail.ui.MainWindow;
import jtail.ui.tab.TailTab;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabItem;

public class MainWindowDisposeListener implements DisposeListener {
	private MainWindow window;
	
	public MainWindowDisposeListener(MainWindow window) {
		this.window = window;
	}
	public void widgetDisposed(DisposeEvent e) {
		/*
		 * Iterate all tabs and cancel all pending tail tasks.
		 */
		TabItem[] tabItems = window.getTabFolder().getItems();
		for (TabItem item : tabItems) {
			TailTab tab = (TailTab) item.getControl();
			tab.dispose();
		}

		/*
		 * save x, y, width and height of the main window when main window closed and restore it
		 * when main window opens next time.
		 */
		Composite parent = (Composite) e.getSource();
		Rectangle lastPosition = parent.getBounds();
		Config.bean.setLastPosition(new java.awt.Rectangle(lastPosition.x, lastPosition.y,
				lastPosition.width, lastPosition.height));

		Config.saveBean();
	}
}
