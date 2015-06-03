package jtail.ui.event.mainWindow;

import java.io.File;
import java.net.URL;

import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;
import jtail.ui.dialog.DialogUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

public class OpenAction extends Action {
	MainWindow window;

	public OpenAction(MainWindow w) {
		window = w;
		setText(Resource.getString("open.action.label"));
		this.setAccelerator(SWT.CTRL | 'O');
		setToolTipText(Resource.getString("open.action.tooltip"));
		try {
			URL url = Resource.getResourceURL(Resource.OPEN_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading open image", e);
		}
	}

	@Override
	public void run() {
		File file = DialogUtil.displayFileOpenDialog(window.getShell());
		MyLogger.debug("file from OpenFileDialog:" + (file == null ? "" : file.getAbsolutePath()));

		// if user did not select any file, return.
		if (file == null) {
			return;
		}

		/*
		 * check if same file is already opened. if it is, show informational message. if it is
		 * not, display selected file.
		 */
		if (window.isFileOpened(file)) {
			// DialogUtil.displayMessageDialog(window.getShell(), "File already opened",
			// "Same file has already been opened.", MessageDialog.INFORMATION,
			// DialogUtil.OK_BUTTON);
			window.selectTailTab(file);
		} else {
			window.createTailTab(file);
			window.updateOpenedFileMenu();
		}
	}
}
