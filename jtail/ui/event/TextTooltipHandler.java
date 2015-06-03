package jtail.ui.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.widgets.Text;

/**
 * This class displays Text widget's text as tooltip.
 * @author joohwan.oh
 * @since 28-Aug-08
 */
public class TextTooltipHandler extends MouseTrackAdapter {
	private Text text;
	public TextTooltipHandler(Text text){
		this.text = text;
	}
	public void mouseHover(MouseEvent event) {
		text.setToolTipText(text.getText());
	}
}
