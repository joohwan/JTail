package jtail.ui.menu;

import java.io.File;

public final class OpenedFileMenuItemData {
	private final File file;
	
	public OpenedFileMenuItemData(File file){
		this.file = file;
	}
	
	public File getFile(){
		return file;
	}
}
