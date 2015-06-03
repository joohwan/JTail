package jtail.ui.event;

import jtail.util.Value;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;



public class OpenFileDialogHandler implements Listener {	
	final private Shell parentShell;
	final private Text filePathText;
	final private Button fileSearchButton;
	final private String fileExtension;
	public OpenFileDialogHandler(Shell parentShell, Text text, Button button, String fileExtension){
		this.parentShell = parentShell;
		this.filePathText = text;
		this.fileSearchButton = button;
		this.fileSearchButton.addListener(SWT.KeyDown, this);
		this.fileSearchButton.addListener(SWT.MouseDown, this);
		this.fileExtension = fileExtension;
	}
	
	public void handleEvent(Event e){
		switch (e.type){
			case SWT.KeyDown:
			case SWT.MouseDown:
				FileDialog fd = new FileDialog(parentShell, SWT.OPEN);
				fd.setFilterExtensions(new String[]{this.fileExtension});
				String filePath = fd.open();
				if (filePath != null) {
					// if user clicked on OK button, populate text control.
					filePathText.setText(Value.toUnixPath(filePath));				
				}
				break;
		}
	}
}