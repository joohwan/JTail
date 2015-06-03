package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class ExitAction extends Action {
	MainWindow window;

	public ExitAction(MainWindow w) {
		window = w;
		setText(Resource.getString("exit.action.label"));
		// this.setAccelerator(SWT.CTRL | 'W');
		setToolTipText(Resource.getString("exit.action.tooltip"));
		try {
			URL url = Resource.getResourceURL(Resource.EXIT_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading exit image", e);
		}
	}

	@Override
	public void run() {
		window.stopAllRunningTail();
		window.close();
	}
}

