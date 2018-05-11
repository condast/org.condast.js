package test.org.condast.openlayer.swt;

import java.util.Calendar;
import java.util.logging.Logger;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.ui.date.DateUtils;
import org.condast.commons.ui.session.ISessionListener;
import org.condast.commons.ui.session.RefreshSession;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.layout.GridData;
import org.openlayer.map.control.GeoView;
import org.openlayer.map.control.MapField;
import org.openlayer.map.controller.OpenLayerController;

import test.org.condast.openlayer.thread.DrawLineExecutor;
import test.org.condast.openlayer.thread.ITimerListener;
import test.org.condast.openlayer.thread.TimerEvent;

public class OpenLayerComposite extends Composite {
	private static final long serialVersionUID = 1L;

	//Themes
	public static final String S_TITLE = "TestOpenLayer";

	public static final String S_LONGTITUDE = "Longtitude:";
	public static final String S_LATITUDE = "Latitude:";
	public static final String S_ROUTE = "Route";
	public static final String S_CHART = "Chart";
	public static final String S_VESSEL = "Vessel";

	public static final String S_ERR_LOGIN1 = "Error while logging in";
	public static final String S_ERR_LOGIN2 =  "Incorrect User and/or password. Please try again.";

	private DrawLineExecutor executor;
	
	private Field field;
	private LatLng current;
	
	private Browser browser;
	private OpenLayerController mapController;
	
	private RefreshSession<TimerEvent> session;
	private ITimerListener listener = new ITimerListener() {

		@Override
		public void notifyChanged( final TimerEvent event) {
			if( event == null )
				return;
			session.addData(event);
		};
	};

	private ISessionListener<TimerEvent> slistener = new ISessionListener<TimerEvent>(){

		@Override
		public void notifySessionChanged(SessionEvent<TimerEvent> sevent) {
			try{
				Calendar calendar = Calendar.getInstance();
				if( sevent.getData() == null )
					return;
				LatLng location = sevent.getData().getLocation();
				String title = DateUtils.getFormatted( calendar.getTime());
				location.setDescription(  title );
				logger.info("UPDATING LOCATION: " + location.toLocation());
				MapField mapfield = new MapField( mapController );
				mapfield.setStroke("red", 2);
				mapfield.setLineStyle("red", 2);
				mapfield.drawLine( title, current, location);
				//GeoView geo = new GeoView( mapController);
				//geo.up();
				//mapfield.synchronize();
				current = location;
				//layout( true );
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}
	};

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	private IEvaluationListener<Object[]> geoListener = new IEvaluationListener<Object[]>(){

		@Override
		public void notifyEvaluation(EvaluationEvent<Object[]> event) {
			if(!S_TITLE.equals(event.getId()))
				return;
			logger.info("Evauation event");
			//Object[] coords = (Object[]) event.getData()[2];
			//LatLng latlng = new LatLng(( Double) coords[1], (Double)coords[0]);
		}
	};

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OpenLayerComposite(Composite parent, int style) {
		super(parent, style);

		this.createComposite(parent, style);
		this.mapController = new OpenLayerController( browser, S_TITLE );
		this.mapController.addEvaluationListener(geoListener);
		this.session = new RefreshSession<TimerEvent>();//half second
		this.session.init(getDisplay());
		this.session.addSessionListener(slistener);
		this.session.start();
		setInput();
	}

	public void createComposite( Composite parent, int style ){
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 1;
		setLayout(gridLayout);

		final Composite composite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_composite.widthHint = 330;
		composite.setLayoutData(gd_composite);
		composite.setData( RWT.CUSTOM_VARIANT, S_TITLE );
		composite.setLayout(new GridLayout(1, false));

		browser = new Browser(this, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		browser.addProgressListener(new ProgressListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void completed(ProgressEvent event) {
				try{
					logger.info("Browser activated" );
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}

			@Override
			public void changed(ProgressEvent event) {
			}
		});

		Composite grpVessel = new Composite(this, SWT.BORDER);
		grpVessel.setData( RWT.CUSTOM_VARIANT, S_TITLE);
		grpVessel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		grpVessel.setLayout(new GridLayout(1, false));

		// Add a listener to get the close button on each tab
		if( parent instanceof TabFolder ) {
			TabFolder tabFolder = (TabFolder) parent;
			tabFolder.addSelectionListener( new SelectionAdapter() {
				private static final long serialVersionUID = 1L;

				@Override
				public void widgetSelected(SelectionEvent e) {
					//setInput();
				}
			});
		}
	}

	protected void setInput(){
		this.field = new Field( new LatLng( "Name", 4.19, 51.1 ), 100, 100);
		GeoView geo = new GeoView( this.mapController, field.getCoordinates());
		this.current = field.getCoordinates();
		if( this.executor != null ) {
			this.executor.removeListener(listener);
			this.executor.shutdown();
		}
		this.executor = new DrawLineExecutor(field, 2000, 1000);
		this.executor.addListener(listener);
		geo.setZoom( 18);
		geo.jump();
		geo.synchronize(); 
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void dispose() {
		this.executor.shutdown();
		
		this.session.removeSessionListener(slistener);
		this.session.stop();

		this.mapController.removeEvaluationListener( geoListener);
		this.mapController.dispose();
		super.dispose();
	}
}
