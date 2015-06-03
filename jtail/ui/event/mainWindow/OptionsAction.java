package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.execute.GuiExecutor;
import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;
import jtail.ui.dialog.DialogUtil;
import jtail.ui.dialog.OptionsDialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class OptionsAction  extends Action {
	MainWindow window;

	public OptionsAction(MainWindow w) {
		window = w;
		setText(Resource.getString("options.action.label"));
		this.setAccelerator(SWT.CTRL | 'T');
		//setToolTipText(Resource.getString("filter.action.tooltip"));
		try {
			URL url = Resource.getResourceURL(Resource.OPTIONS_IMAGE_PATH);
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
				OptionsDialog optionsDialog = new OptionsDialog(shell);				
				optionsDialog.open();
			}
		});
	}
}