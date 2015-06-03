package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

public class ForwardSearchAction extends Action {
	MainWindow window;

	public ForwardSearchAction(MainWindow w) {
		window = w;
		setText(Resource.getString("find.next"));
		this.setAccelerator(SWT.CTRL | SWT.ARROW_DOWN);
		//setToolTipText("Exit the application");
		try {
			URL url = Resource.getResourceURL(Resource.SEARCH_FORWARD_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading search forward image", e);
		}
	}

	@Override
	public void run() {
		window.getSelectedTailTab().search(window.getSelectedTailTab().getSearchText(), true);
	}
}
