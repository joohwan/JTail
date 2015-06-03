package jtail.ui.event.mainWindow;

import java.net.URL;

import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.MainWindow;
import jtail.ui.tab.TailTab;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

public class ClearAction extends Action {
	MainWindow window;

	public ClearAction(MainWindow w) {
		window = w;
		setText(Resource.getString("clear.action.label"));
		this.setAccelerator(SWT.CTRL | 'L');
		setToolTipText(Resource.getString("clear.action.tooltip"));
		try {
			URL url = Resource.getResourceURL(Resource.CLEAR_IMAGE_PATH);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			MyLogger.debug("Exception while reading clear image", e);
		}
	}

	@Override
	public void run() {
		int index = window.getTabFolder().getSelectionIndex();
		TailTab tailTab = (TailTab) window.getTabFolder().getItem(index).getControl();
		tailTab.clearViewport();
		if (tailTab.isTailPaused()){
			//tailTab.checkStopTailButton(false);
			tailTab.resumeTail();
		}		
	}
}
