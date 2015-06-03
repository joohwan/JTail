package jtail.ui.dialog;

import java.io.File;
import java.util.List;

import jtail.execute.GuiExecutor;
import jtail.resources.Resource;
import jtail.util.MessageBuilder;
import jtail.util.RegexUtil;
import jtail.util.Value;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;


public class DialogUtil {
	public static final String[] OK_BUTTON = { Resource.getString("ok.button.label") };
	
	public static final String[] OK_CANCEL_BUTTON = { Resource.getString("ok.button.label"),
		Resource.getString("cancel.button.label")};
	
	private final static IInputValidator inputNameValidator = new IInputValidator() {
		public String isValid(String text) { // return an error message,
			if (text.trim().length() == 0) return "Please input a name for this setting.";
			else if (!text.matches(RegexUtil.NAME_REGEX)) {
				return "Please input a name with correct format.";
			} else return null; // pass validation.
		}
	};
	

	public static String getMissedInputMessage(List<String> fieldNameList) {
		StringBuffer sb = new StringBuffer(Resource.getString("please.input.value") + Value.NL + Value.NL);
		for (String fieldName : fieldNameList) {
			sb.append(Value.TAB + Value.DASH + " " + fieldName + Value.NL);
		}
		return sb.toString();
	}

	public static String getNoDataToDeleteMessage() {
		return Resource.getString("no.data.to.delete");
	}
	
	public static void displayMessageDialog(final Shell shell, final String title,
			final String message, final int icon, final String[] buttons) {
		Display display = shell.getDisplay();		
		display.syncExec(new Runnable() {
			public void run() {
				MessageDialog dialog = new MessageDialog(shell, title, null, message, // text to be displayed
					icon, // dialog type
					buttons, // button labels
					0);
				dialog.open();
			}});
	}
	
	public static int showDialog(final Shell shell, final String title,
			final String message, final int icon, final String[] buttons) {
		MessageDialog dialog = new MessageDialog(shell, title, null, message, // text to be displayed
					icon, // dialog type
					buttons, // button labels
					0);
		int buttonId = dialog.open();
		return buttonId;		
	}
	

	public static void displayMissedInputDialog(Shell settingWidnowShell, List<String> fieldNameList) {
		String missedInputMessage = getMissedInputMessage(fieldNameList);
		displayMessageDialog(settingWidnowShell, Resource.getString("validation.error"),
				missedInputMessage, MessageDialog.ERROR, OK_BUTTON);
	}

	public static void displayValidationErrorDialog(Shell parentShell, String message) {
		String errorMessage = Resource.getString("validation.error.message")
			+ Value.NL + Value.NL + message;
		displayMessageDialog(parentShell, Resource.getString("validation.error"),
				errorMessage, MessageDialog.ERROR, OK_BUTTON);
	}

	public static void displayNoDataToDeleteDialog(Shell settingWidnowShell) {
		displayMessageDialog(settingWidnowShell, Resource.getString("delete.failure"),
				getNoDataToDeleteMessage(), MessageDialog.ERROR, OK_BUTTON);
	}	
	
	public static void displaySaveSuccessDialog(Shell settingWindowShell) {
		displayMessageDialog(settingWindowShell, Resource.getString("confirmation"),
				MessageBuilder.SAVE_SUCCESS_MSG, MessageDialog.INFORMATION, OK_BUTTON);		
	}

	public static void displayDeleteSuccessDialog(Shell settingWindowShell) {
		displayMessageDialog(settingWindowShell, Resource.getString("confirmation"),
				MessageBuilder.DELETE_SUCCESS_MSG, MessageDialog.INFORMATION, OK_BUTTON);		
	}

	public static void displaySameFilterNameErrorDialog(Shell settingWindowShell) {
		displayMessageDialog(settingWindowShell, Resource.getString("error"),
				Resource.getString("same.filter.name.error"), MessageDialog.ERROR, OK_BUTTON);	
	}
	
	public static void displaySearchNotFoundDialog(Shell settingWindowShell) {
		displayMessageDialog(settingWindowShell, Resource.getString("jtail"),
				Resource.getString("search.not.found"), MessageDialog.INFORMATION, OK_BUTTON);		
	}

	/**
	 * Open input dialogbox to get saved name.
	 * @param settingWidnowShell
	 * @return empty string if user didn't input value, otherwise name user input.
	 */
	public static String openSaveAsDialog(Shell settingWidnowShell, String defaultText) {
		InputDialog inputDialog = new InputDialog(settingWidnowShell,
				"Please input a name to save this setting.", // dialog title
				"Save as (alphabet, digit, _ and - can be used)", // dialog prompt
				defaultText, // default text
				inputNameValidator); // validator to use
		int returnCode = inputDialog.open();
		if (returnCode == InputDialog.OK) return inputDialog.getValue();
		else return Value.EMPTY_STRING;
	}
	
	public static void displayErrorDialog(Throwable t) {
		String printStackTrace = Value.printStackToString(t);
		Exception detailException = new Exception(printStackTrace);
		final String errorMessage = Resource.getString("error.message.header");
		final String reason = t.getLocalizedMessage();
		final Status status = new Status(IStatus.ERROR, "My Plug-in ID", IStatus.OK, reason,
				detailException);
		//final String errorMessage = Value.DEPLOY_ERROR_MESSAGE_HEADER+printStackTrace;
	
		GuiExecutor.getInstance().execute(new Runnable() {
			public void run() {
				Shell shell = DialogUtil.getActiveShell();				
				if (shell.isDisposed()) return;
				ErrorDialog.openError(shell, "Error Dialog", errorMessage, status);
			}
		});
	}

	public static File displayFileOpenDialog(final Shell shell) {
		//final Display display = shell.getDisplay();
		
		final class FileOpener implements Runnable {
			private File selectedFile = null;
			public void run() {
				final FileDialog fd = new FileDialog(shell, SWT.OPEN);
				//fd.setFilterExtensions(new String[]{this.fileExtension});
				final String selectedFilePath = fd.open();
				if (!Value.isEmptyOrNull(selectedFilePath)) {
					selectedFile = new File(selectedFilePath);
				} else {
					selectedFile = null;
				}
			}
			public File getSelectedFile(){
				return this.selectedFile;
			}
		}
		final FileOpener opener = new FileOpener();
		GuiExecutor.getInstance().execute(opener);
		final File file = opener.getSelectedFile();
		return file;
	}
	
	public static Shell getActiveShell(){
		Display display = Display.getDefault();
		Shell shell = display.getActiveShell();
		if (shell == null){
			shell = new Shell(display);
		}	
		return shell;
	}

}
