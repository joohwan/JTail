package jtail.ui.event.optionsDialog;

import jtail.config.Config;
import jtail.ui.MainWindow;
import jtail.ui.dialog.DialogUtil;
import jtail.ui.dialog.OptionsDialog;
import jtail.ui.tab.TailTab;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class OptionsDialogSaveButtonEventHandler extends SelectionAdapter {
	private OptionsDialog optionsDialog;
	
	public OptionsDialogSaveButtonEventHandler(OptionsDialog optionsDialog){
		this.optionsDialog = optionsDialog;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		int size = Config.bean.setDisplayBlockSizeInKb(optionsDialog.getBlockSize());
		optionsDialog.setBlockSize(size);
		
		size = Config.bean.setMaximumNumberOfBlocksToBeRead(optionsDialog.getMaximumNumberOfBlocksToBeRead());
		optionsDialog.setMaximumNumberOfBlocksToBeRead(size);		
		
		TailTab selectedTailTab = MainWindow.getMainWindow().getSelectedTailTab();
		if (selectedTailTab != null){
			selectedTailTab.reloadDocument();
		}
		//DialogUtil.displaySaveSuccessDialog(optionsDialog.getShell());
		optionsDialog.close();
	}
}
