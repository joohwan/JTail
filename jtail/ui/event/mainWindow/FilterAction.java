package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.execute.GuiExecutor;
import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;
import jtail.ui.dialog.DialogUtil;
import jtail.ui.dialog.FilterDialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class FilterAction extends Action {
	MainWindow window;

	public FilterAction(MainWindow w) {
		window = w;
		setText(Resource.getString("filter.action.label"));
		this.setAccelerator(SWT.CTRL | 'F');
		setToolTipText(Resource.getString("filter.action.tooltip"));
		try {
			URL url = Resource.getResourceURL(Resource.FILTER_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading filter image", e);
		}
	}

	@Override
	public void run() {
		GuiExecutor.getInstance().execute(new Runnable() {
			public void run() {
				Shell shell = DialogUtil.getActiveShell();				
				if (shell.isDisposed()) return;
				FilterDialog filterDialog = new FilterDialog(shell);
				filterDialog.open();
			}
		});
	}
}
