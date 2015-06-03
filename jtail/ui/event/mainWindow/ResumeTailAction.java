package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

public class ResumeTailAction extends Action {
	MainWindow window;

	public ResumeTailAction(MainWindow w) {
		window = w;
		setText(Resource.getString("resume.tail"));
		this.setAccelerator(SWT.CTRL | 'R');
		//setToolTipText(Resource.getString("resume.tail"));
		try {
			URL url = Resource.getResourceURL(Resource.RESUME_TAIL_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Error:", e);
		}
	}

	@Override
	public void run() {
		window.getSelectedTailTab().resumeTail();
	}
}