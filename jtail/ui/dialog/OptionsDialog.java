package jtail.ui.dialog;

import java.awt.Font;

import jtail.config.Config;
import jtail.config.XMLConfigBean;
import jtail.resources.Resource;
import jtail.ui.event.optionsDialog.OptionsDialogSaveButtonEventHandler;
import jtail.ui.util.UiUtil;
import jtail.util.Value;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The Class OptionsDialog.
 * 
 * @author Joohwan Oh, 2011-12-20
 */
public class OptionsDialog extends Dialog {
	private Text blockSizeText;
	private Text maximumNumberOfBlocksToBeReadText;
		
	/**
	 * Instantiates a new define regex dialog.
	 * 
	 * @param parentShell the parent shell
	 */
	public OptionsDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);		
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		Shell shell = this.getShell();		
		shell.setText(Resource.getString("options"));
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.numColumns = 10;
		composite.setLayout(gridLayout);
		GridDataFactory gdf = GridDataFactory.fillDefaults();
				
		displayBlockSizeLine(composite, gdf);		
		displayBlockSizeDetailLine(composite, gdf);
		displayEmptyLine(composite, gdf);	
		displayMaximumReadSizeLine(composite, gdf);
		displayMaximumReadSizeDetailLine(composite, gdf);
		
		populateValues();
		
		return composite;
	}

	private void displayBlockSizeLine(Composite composite, GridDataFactory gdf) {
		Label label = new Label(composite, SWT.WRAP);
		label.setText(Resource.getString("block.size")+": ");
		gdf.copy().span(1, 1).applyTo(label);
		
		blockSizeText = new Text(composite, SWT.LEFT | SWT.BORDER);
		gdf.copy().span(1, 1).hint(50, SWT.DEFAULT).applyTo(blockSizeText);
		
		Label kbLabel = new Label(composite, SWT.WRAP);
		kbLabel.setText(Resource.getString("kb"));
		gdf.copy().span(8, 1).applyTo(kbLabel);
	}
	
	private void displayBlockSizeDetailLine(Composite composite, GridDataFactory gdf) {
		Label label = new Label(composite, SWT.WRAP);
		String text = UiUtil.buildMultilineTooltip("block.size.detail", 3);
		label.setText(text);
		gdf.copy().span(10, 1).grab(true, true).applyTo(label);		
	}
	
	private void displayEmptyLine(Composite composite, GridDataFactory gdf) {
		Label label = new Label(composite, SWT.WRAP);
		label.setText("");
		gdf.copy().span(10, 1).grab(true, false).applyTo(label);		
	}
	
	private void displayMaximumReadSizeLine(Composite composite, GridDataFactory gdf) {
		Label label = new Label(composite, SWT.WRAP);
		label.setText(Resource.getString("maximum.number.of.blocks.to.be.read")+": ");
		gdf.copy().span(1, 1).applyTo(label);
		
		maximumNumberOfBlocksToBeReadText = new Text(composite, SWT.LEFT | SWT.BORDER);		
		gdf.copy().span(1, 1).hint(50, SWT.DEFAULT).applyTo(maximumNumberOfBlocksToBeReadText);		
	}
	
	private void displayMaximumReadSizeDetailLine(Composite composite, GridDataFactory gdf) {
		Label label = new Label(composite, SWT.WRAP);
		String text = UiUtil.buildMultilineTooltip("maximum.number.of.blocks.to.be.read.detail", 4);
		label.setText(text);
		gdf.copy().span(10, 1).grab(true, true).applyTo(label);		
	}
	
	
	private void populateValues() {
		this.blockSizeText.setText(String.valueOf(Config.bean.getDisplayBlockSizeInKb()));		
		this.maximumNumberOfBlocksToBeReadText.setText(String.valueOf(Config.bean.getMaximumNumberOfBlocksToBeRead()));	
	}

	/**
	 * Adds buttons to this dialog's button bar.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method adds
	 * standard ok and cancel buttons using the <code>createButton</code>
	 * framework method. These standard buttons will be accessible from
	 * <code>getCancelButton</code>, and <code>getOKButton</code>.
	 * Subclasses may override.
	 * </p>
	 * 
	 * @param parent
	 *            the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		//Button newButton = createButton(parent, IDialogConstants.YES_ID, Resource.getString("new"), false);
		Button okButton = createButton(parent, IDialogConstants.YES_ID, Resource.getString("ok.button.label"), true);
		//Button deleteButton = createButton(parent, IDialogConstants.YES_ID, Resource.getString("delete"), false);
		Button closeButton = createButton(parent, IDialogConstants.CANCEL_ID, Resource.getString("close"), false);
		
		okButton.addSelectionListener(new OptionsDialogSaveButtonEventHandler(this));		
	}
	
	public int getBlockSize() {
		int blockSizeInKb = 0;
		try {
			blockSizeInKb = Value.intValue(blockSizeText.getText().trim());
		} catch (Throwable t){
			blockSizeInKb = XMLConfigBean.DEFAULT_DISPLAY_BLOCK_SIZE_IN_KB;
		}
		return blockSizeInKb;
	}
	
	public void setBlockSize(int size) {
		blockSizeText.setText(String.valueOf(size));
	}
	
	public int getMaximumNumberOfBlocksToBeRead() {
		int maxBlockNumber = 0;
		try {
			maxBlockNumber = Value.intValue(this.maximumNumberOfBlocksToBeReadText.getText().trim());
		} catch (Throwable t){
			maxBlockNumber = XMLConfigBean.DEFAULT_MAXIMUM_NUMBER_OF_BLOCKS_TO_BE_READ;
		}
		return maxBlockNumber;
	}
	
	public void setMaximumNumberOfBlocksToBeRead(int size) {
		maximumNumberOfBlocksToBeReadText.setText(String.valueOf(size));
	}
	
	
}
