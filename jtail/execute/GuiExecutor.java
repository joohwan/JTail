package jtail.execute;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;

public final class GuiExecutor extends AbstractExecutorService {
	// Singletons have a private constructor and a public factory
    private static final GuiExecutor instance = new GuiExecutor();

    private GuiExecutor() {}

    public static GuiExecutor getInstance() { return instance; }

    public void execute(Runnable r) {
    	final Display display = Display.getCurrent();
        if (display == null) {
        	Display.getDefault().syncExec(r);
        } else {
        	r.run();
        }
    }

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return false;
	}

	public boolean isShutdown() {
		return false;
	}

	public boolean isTerminated() {
		return false;
	}
	
	public void shutdown() {}

	public List<Runnable> shutdownNow() {
		return null;
	}
}
