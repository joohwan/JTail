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

public class CopyAction extends Action {
	MainWindow window;

	public CopyAction(MainWindow w) {
		window = w;
		setText(Resource.getString("copy.action.label"));
		this.setAccelerator(SWT.CTRL | 'C');

		try {
			URL url = Resource.getResourceURL(Resource.COPY_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading copy image", e);
		}
	}

	@Override
	public void run() {
		Control focusControl = Display.getDefault().getFocusControl();
		if (focusControl != null) {
			if (focusControl instanceof Text) {
				((Text) focusControl).copy();
				//MyLogger.log("Text control's text was copied:" + focusControl);
			} else if (focusControl instanceof StyledText) {
				((StyledText) focusControl).copy();
				//MyLogger.log("StyledText control's text was copied:" + focusControl);
			}
		}
	}
}
