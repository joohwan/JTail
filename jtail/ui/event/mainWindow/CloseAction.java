package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;
import jtail.ui.tab.TailTab;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;

public class CloseAction extends Action {
	MainWindow window;

	public CloseAction(MainWindow w) {
		window = w;
		setText(Resource.getString("close.action.label"));
		this.setAccelerator(SWT.CTRL | 'W');
		setToolTipText(Resource.getString("close.action.tooltip"));
		try {
			URL url = Resource.getResourceURL(Resource.CLOSE_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading close image", e);
		}
	}

	@Override
	public void run() {
		// clean up tab
		TailTab tab = this.window.getSelectedTailTab();
		if (tab != null) {
			tab.pauseTail();
			tab.dispose();
			this.window.removeFromOpenedTailTabList(tab);

			// close tab from the tab folder.
			TabItem tabItem = this.window.getSelectedTabItem();
			tabItem.dispose();
		}
	}
}
