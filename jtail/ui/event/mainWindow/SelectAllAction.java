package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class SelectAllAction extends Action {
	MainWindow window;

	public SelectAllAction(MainWindow w) {
		window = w;
		setText(Resource.getString("selectall.action.label"));
		this.setAccelerator(SWT.CTRL | 'A');

		try {
			URL url = Resource.getResourceURL(Resource.SELECT_ALL_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading select all image", e);
		}
	}

	@Override
	public void run() {
		Control focusControl = Display.getDefault().getFocusControl();
		if (focusControl != null) {
			if (focusControl instanceof Text) {
				((Text) focusControl).selectAll();
				//MyLogger.log("Text control's text was copied:" + focusControl);
			} else if (focusControl instanceof StyledText) {
				((StyledText) focusControl).selectAll();
				//MyLogger.log("StyledText control's text was copied:" + focusControl);
			}
		}
	}
}
