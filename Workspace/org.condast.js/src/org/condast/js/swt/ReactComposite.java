package org.condast.js.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.google.geo.mapping.ui.controller.GeoCoderController;
import org.google.geo.mapping.ui.model.TilesAndPixelsModel;

public class ReactComposite extends Composite {
	
	private static final long serialVersionUID = 1L;

	private GeoCoderController geoController;
	private Browser browser;
	/*
	private IEvaluationListener<Map<String,String>> elistener = new IEvaluationListener<Map<String,String>>() {

		@Override
		public void notifyEvaluation(final EvaluationEvent<Map<String, String>> event) {
			Display.getCurrent().asyncExec( new Runnable(){

				@Override
				public void run() {
					if( EvaluationEvents.SUCCEEDED.equals( event.getEvaluationEvent() )){
						MarkerModel mmodel = new MarkerModel( geoController );
						//TilesAndPixelsModel tpm = new TilesAndPixelsModel( geoController );
						//tpm.createLocationInfo("sensor", "", lnglat, 15);
						mmodel.synchronize();
					}
				}		
			});			
		}
	};
	*/
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ReactComposite(Composite parent, int style) {
		super(parent, style);
		super.setLayout( new GridLayout( 1, true ));
		this.browser = new Browser( this, SWT.NONE );
		this.browser.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true));
		geoController = new GeoCoderController( browser );
		//geoController.addSessionListener(listener);
		//scheduler.scheduleAtFixedRate( runnable, 5, 5, TimeUnit.SECONDS);
	}

	protected void initComposite(){

		TilesAndPixelsModel tpm = new TilesAndPixelsModel( geoController );
		tpm.setTileSize(40);
		//tpm.setLocation( new LngLat( sensor.getLatitude(), sensor.getLongitude()), 10);
		
		//MarkerModel mmodel = new MarkerModel( geoController );
		
		tpm.synchronize();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
