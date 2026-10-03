package org.condast.js.commons.ui.widgets.entry;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.widgets.entry.IDataEntryPoint;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractRestEntryPoint<D extends Object> extends AbstractEntryPoint implements IDataEntryPoint<D>, AutoCloseable{
	private static final long serialVersionUID = 1L;

	public static final String S_PAGE = "page";

	public static final String S_ARNAC_RESOURCES = "/resources/index.html";

	public static final String S_INVALID_PREPARATION = "Login first";

	public static final int DEFAULT_SCHEDULE = 1000; //milli seconds

	private D data;
	
	private String customVariant;
	
	private RWTUiSessionHandler handler;

	private String message;

	private ScheduledExecutorService timer;
	private int startTime, rate;

	protected AbstractRestEntryPoint( ) {
		this( null, DEFAULT_SCHEDULE, DEFAULT_SCHEDULE);
	}

	protected AbstractRestEntryPoint( String customVariant ) {
		this( customVariant, DEFAULT_SCHEDULE, DEFAULT_SCHEDULE );
	}
	
	protected AbstractRestEntryPoint( String customVariant, int startTime, int rate ) {
		super();
		this.customVariant = customVariant;
		this.startTime = startTime;
		this.rate = rate;
		this.message = S_INVALID_PREPARATION;
	}

	public D getData() {
		return data;
	}
	
	@Override
	public void setData(D data) {
		this.data = data;
	}

	protected String getCustomVariant() {
		return customVariant;
	}

	protected void setCustomVariant(String customVariant) {
		this.customVariant = customVariant;
	}

	protected Locale onSetLocale() {
		return Locale.getDefault();
	}

	protected abstract boolean prepare( Composite parent );

	protected abstract Composite createComposite( Composite parent  );

	protected void handleTimer() {
		/* default nothing */
	}

	protected boolean postProcess( Composite parent ) {
		return true;
	}

	protected String createMessage( ) {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	protected void createTimer( boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate ) {
		if(!create)
			return;
		timer = Executors.newScheduledThreadPool(nrOfThreads);
		timer.scheduleAtFixedRate(()->handleTimer(), startTime, rate, unit);
	}

	@Override
	protected void createContents(Composite parent) {
		try{
			this.message = S_INVALID_PREPARATION;
			if( !prepare( parent )) {
				Label label = new Label( parent, SWT.NONE);
				label.setText( createMessage());
				return;
			}
			parent.addDisposeListener( e->close());
			handler = new RWTUiSessionHandler(parent.getDisplay());
			//Set the RWT Locale
			if( !StringUtils.isEmpty(customVariant))
				parent.setData( RWT.CUSTOM_VARIANT, customVariant );
			Locale locale = onSetLocale();
			RWT.setLocale(locale);
			RWT.getUISession().setLocale(locale);
			Locale.setDefault( locale );
	        parent.setLayout(new FillLayout( SWT.VERTICAL));
			Composite composite = createComposite(parent );
			if( !StringUtils.isEmpty(customVariant))
				composite.setData( RWT.CUSTOM_VARIANT, customVariant );
			composite.addDisposeListener(e->close());
			this.message = S_INVALID_PREPARATION;
			createTimer(false, 1, TimeUnit.MILLISECONDS, this.startTime, this.rate);
			if(!postProcess(parent)) {
				message = getClass().getName() + ": " + message;
				throw new Exception( createMessage());
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	protected void stopTimer() {
		this.timer.shutdown();
	}

	protected void handleSessionTimeout( boolean reload ) {
		/* default nothing */
	}

	@Override
	public  void close(){
		try {
			if( timer != null )
				timer.shutdown();
			handler.dispose();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private class RWTUiSessionHandler extends org.condast.js.commons.ui.widgets.rwt.AbstractRWTSessionSupport{

		public RWTUiSessionHandler(Display display) {
			super(display, Integer.MAX_VALUE);
		}

		@Override
		protected void onHandleTimeout(boolean reload) {
			handleSessionTimeout( reload);
		}
	}
}