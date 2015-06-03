package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

public class BackwardSearchAction extends Action {
	MainWindow window;

	public BackwardSearchAction(MainWindow w) {
		window = w;
		setText(Resource.getString("find.previous"));
		this.setAccelerator(SWT.CTRL | SWT.ARROW_UP);
		//setToolTipText("Exit the application");
		try {
			URL url = Resource.getResourceURL(Resource.SEARCH_BACKWARD_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading search backward image", e);
		}
	}

	@Override
	public void run() {
		window.getSelectedTailTab().search(window.getSelectedTailTab().getSearchText(), false);
	}
}