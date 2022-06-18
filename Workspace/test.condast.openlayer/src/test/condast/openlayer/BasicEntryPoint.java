package test.condast.openlayer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import test.condast.openlayer.views.TestMapBrowser;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	private ScheduledExecutorService timer;

	private TestMapBrowser browser;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private int index;
	
	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new FillLayout());
        browser = new TestMapBrowser(parent, SWT.NONE);
        browser.setInput();
        this.index = 0;
		timer = Executors.newScheduledThreadPool(1);
		timer.scheduleAtFixedRate(()->handleTimer(), 1000, 5000, TimeUnit.MILLISECONDS);	
  }

	private void handleTimer() {
		try {
			logger.info("INDEX: " + index++ );
			browser.updateMarkers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
