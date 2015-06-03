package jtail.ui.tab.pane;

import jtail.execute.GuiExecutor;
import jtail.log.MyLogger;
import jtail.ui.tab.TailTab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;

/**
 * This class represents the window pane which displays tailed file.
 * @author joohwan oh
 * @since 2009-02-19
 */
public final class TailPaneNotUsed extends Composite {
	//private final StyledText styledText;
	private StyledText styledText;
	private List list;
	private final TailTab tailTab;
//	private final ExecutorService service = Executors.newCachedThreadPool();
//	private Future<?> future;
	
	public TailPaneNotUsed(Composite parent, int style, TailTab tailTab) {
		super(parent, style);
		this.setLayout(new FillLayout());
		this.tailTab = tailTab;
		//styledText = new StyledText(this, SWT.MULTI | SWT.READ_ONLY | SWT.HORIZONTAL | SWT.VERTICAL);
		styledText = new StyledText(this, SWT.READ_ONLY | SWT.HORIZONTAL | SWT.VERTICAL);
		//list = new List(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		
	    
		/*File file = tailTab.getFile();
		assert file !=  null : "File from TailTab is null.";
		String fileContent = "";
		try {
			fileContent = FileUtil.readFile(file);			
		} catch (IOException e){
			DialogUtil.displayErrorDialog(e);
			return;
		}
		
		styledText.setText(fileContent);*/
		
		
	}
	
//	public Future<?> getFuture(){
//		return this.future;
//	}

	public void updateTailContent(final String fileContent) {
		GuiExecutor.getInstance().execute(new Runnable(){
			public void run(){
				Display display = styledText.getDisplay();
				if (display == null) return;
				
				// get last 100 lines
//				String[] lines = fileContent.split("\\n");
//				String[]  last100Lines = new String[100];
//				if (lines.length > 100){
//					System.arraycopy(lines, lines.length-100, last100Lines, 0, 100);
//				} else {
//					last100Lines = lines;
//				}
//				StringBuffer sb = new StringBuffer();
//				for (String s : last100Lines){
//					sb.append(s+"\n");
//				}
//				styledText.setText(sb.toString());
				
				styledText.setText(fileContent);				
				applyStyle();
				
//				int lineIndex = styledText.getLineCount()-1;
//				int a = styledText.getOffsetAtLine(lineIndex );
//				styledText.setCaretOffset(a);
//				MyLogger.log("lineIndex:"+lineIndex+",a:"+a);
				styledText.invokeAction(ST.TEXT_END);
//				styledText.invokeAction(ST.LINE_END);
			}
		});		
	}
	
//	public void updateTailContent(final String fileContent) {
//		GuiExecutor.instance().execute(new Runnable(){
//			public void run(){
//				Display display = list.getDisplay();
//				if (display == null) return;
//				String[] lines = fileContent.split("\\n");
//				list.setItems(lines);				
//				//applyStyle();
//				
//			}
//		});		
//	}


	private void applyStyle() {
		Display display = styledText.getDisplay();
		//styledText.set
		//styledText.setWordWrap(true);
		
		StyleRange style0 = new StyleRange();
	    style0.metrics = new GlyphMetrics(0, 0, 40);
	    style0.foreground = display.getSystemColor(SWT.COLOR_BLUE);
	    Bullet bullet0 = new Bullet(style0);
		
		StyleRange style = new StyleRange();
	    style.metrics = new GlyphMetrics(0, 0, 50);	    
	    style.foreground = display.getSystemColor(SWT.COLOR_BLUE);
	    Bullet bullet = new Bullet(ST.BULLET_NUMBER | ST.BULLET_TEXT, style);
	    bullet.text = ".";
	    
	    int lineCount = styledText.getLineCount();
	    //styledText.setLineBullet(0, lineCount, bullet0);
	}

	
	
}
