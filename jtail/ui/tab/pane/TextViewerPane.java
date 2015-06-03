package jtail.ui.tab.pane;

import jtail.execute.GuiExecutor;
import jtail.logic.ViewContent;
import jtail.logic.search.SearchHandler;
import jtail.ui.MainWindow;
import jtail.ui.event.ViewPortChangeHandler;
import jtail.ui.tab.TailTab;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public final class TextViewerPane extends Composite {
	// private final StyledText styledText;
	// private StyledText styledText;
	private TextViewer textViewer;
	// private IDocument doc;
	private final SearchHandler searchHandler;
	private final TailTab tailTab;
	
	private IDocument doc;
	private ViewPortChangeHandler viewPortChangeHandler;

	// private final ExecutorService service = Executors.newCachedThreadPool();
	// private Future<?> future;

	@SuppressWarnings("deprecation")
	public TextViewerPane(Composite parent, int style, TailTab tailTab) {
		super(parent, style);
		this.setLayout(new FillLayout());
		this.tailTab = tailTab;
//		this.textViewer = new TextViewer(this, SWT.READ_ONLY | SWT.V_SCROLL
//				| SWT.H_SCROLL);		
		this.textViewer = new TextViewer(this, SWT.WRAP |SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL);
		viewPortChangeHandler = new ViewPortChangeHandler(this.textViewer, this.tailTab);
		textViewer.addViewportListener(viewPortChangeHandler);
		tailTab.setViewPortChangeHandler(this.viewPortChangeHandler);
		StyledText textWidget = textViewer.getTextWidget();
		FontRegistry fr = JFaceResources.getFontRegistry();
		fr.get(JFaceResources.HEADER_FONT);
		fr.get(JFaceResources.DIALOG_FONT);
		Font textFont = fr.get(JFaceResources.TEXT_FONT);
		fr.get(JFaceResources.WINDOW_FONT);
		textWidget.setFont(textFont);
		doc = new Document();
		this.textViewer.setDocument(doc);
		this.searchHandler = new SearchHandler(tailTab);
		this.textViewer.getDocument().addDocumentListener(searchHandler);

		MenuManager popupMenu = new MenuManager();
		popupMenu.add(this.getMainWindow().getCopyAction());
		popupMenu.add(this.getMainWindow().getCloseAction());
		Menu menu = popupMenu.createContextMenu(textViewer.getTextWidget());
		textViewer.getTextWidget().setMenu(menu);

		// this.text.addTextPresentationListener(new TextPresentationListener());
		// styledText = new StyledText(this, SWT.MULTI | SWT.READ_ONLY | SWT.HORIZONTAL |
		// SWT.VERTICAL);
		// list = new List(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

		/*
		 * File file = tailTab.getFile(); assert file != null : "File from TailTab is null."; String
		 * fileContent = ""; try { fileContent = FileUtil.readFile(file); } catch (IOException e){
		 * DialogUtil.displayErrorDialog(e); return; }
		 * 
		 * styledText.setText(fileContent);
		 */
	}

	// public Future<?> getFuture(){
	// return this.future;
	// }

	public void redrawViewArea(final ViewContent viewContent, DisplayTriggerAction triggerAction) {
		GuiExecutor.getInstance().execute(new ViewUpdateTask(tailTab, viewContent, triggerAction));
		//ExecutorService service = Executors.newFixedThreadPool(1);
		//service.execute(new ViewUpdateTask(this, block, viewUpdateInfo));
	}

	// public void updateTailContent(final int offset, final int length) {
	// GuiExecutor.getInstance().execute(new Runnable() {
	// /**
	// * update viewing area.
	// */
	// public void run() {
	// StyledText textWidget = text.getTextWidget();
	// try {
	// text.getDocument().set(FileUtil.readFile(tailTab.getFile()));
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// text.setDocument(doc, 0, (int) tailTab.getFileLength());
	//
	// applyStyle();
	//
	// // int lineIndex = styledText.getLineCount()-1;
	// // int a = styledText.getOffsetAtLine(lineIndex );
	// // styledText.setCaretOffset(a);
	// // MyLogger.log("lineIndex:"+lineIndex+",a:"+a);
	//
	// // have scrollbar scrolled down fully.
	// textWidget.invokeAction(ST.TEXT_END);
	// textWidget.invokeAction(ST.LINE_START);
	// }
	// });
	// }

	// public void updateTailContent(final String fileContent) {
	// GuiExecutor.instance().execute(new Runnable(){
	// public void run(){
	// Display display = list.getDisplay();
	// if (display == null) return;
	// String[] lines = fileContent.split("\\n");
	// list.setItems(lines);
	// //applyStyle();
	//				
	// }
	// });
	// }
	

	/*
	 * getter, setter
	 */
	public MainWindow getMainWindow() {
		return this.tailTab.getMainWindow();
	}

	public TextViewer getTextViewer() {
		return this.textViewer;
	}

	public SearchHandler getSearchHandler() {
		return this.searchHandler;
	}

	public ViewPortChangeHandler getViewPortChangeHandler() {
		return viewPortChangeHandler;
	}
	
	public TailTab getTailTab() {
		return tailTab;
	}

	
//	if (subDocument.matchFound()) {
//	// highlight matched region.
//	textViewer.setSelectedRange(subDocument.getMatchStartIndex(), 
//			subDocument.getMatchLength());
//
//	// copy the match.
//	textViewer.getTextWidget().copy();
//
//} else {
//	// have scrollbar scrolled down fully.
//	textWidget.invokeAction(ST.TEXT_END);
//	textWidget.invokeAction(ST.LINE_START);					
//}		

}
