package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;
import jtail.ui.tab.TailTab;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

public class ReloadAction extends Action {
	MainWindow window;

	public ReloadAction(MainWindow w) {
		window = w;
		setText(Resource.getString("reload.action.label"));
		this.setAccelerator(SWT.CTRL | 'D');
		setToolTipText(Resource.getString("reload.action.tooltip"));
		try {
			URL url = Resource.getResourceURL(Resource.RELOAD_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading reload image", e);
		}
	}

	@Override
	public void run() {
		// get current tailtab
		int index = window.getTabFolder().getSelectionIndex();
		if (index >= 0){
			TailTab tailTab = (TailTab) window.getTabFolder().getItem(index).getControl();

			// reload tailed file from the start of the file.
			tailTab.reloadDocument();	
		}		
	}
}
