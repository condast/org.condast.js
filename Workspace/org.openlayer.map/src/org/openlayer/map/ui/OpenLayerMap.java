package org.openlayer.map.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.plane.IPolygon;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.commons.ui.widgets.session.AbstractSessionHandler;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.condast.js.commons.images.IDefaultMarkers.Markers;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.openlayer.map.control.IconsView;
import org.openlayer.map.control.NavigationView;
import org.openlayer.map.controller.OpenLayerController;

public class OpenLayerMap extends Browser {
	private static final long serialVersionUID = 1L;

	private OpenLayerController mapController;

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	private SessionHandler handler;

	private boolean located;
	
	private LatLng home;
	
	private boolean showclicked;

	private Collection<IEditListener<LatLng>> listeners;
	private Collection<IEvaluationListener<Object>> evalListeners;
	
	private ProgressListener plistener = new ProgressListener() {
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
			//NOTHING
		}
	};

	public OpenLayerMap(Composite parent, int style) {
		super(parent, style);
		this.located = false;
		home = new LatLng( "Cucuta", 7.89391, -72.50782);
		this.mapController = new OpenLayerController( this, home, 11);
		this.mapController.addEvaluationListener( e->onNotifyEvaluation(e));
		this.addProgressListener(plistener);
		this.handler = new SessionHandler(getDisplay());
		this.listeners = new ArrayList<>();
		this.evalListeners = new ArrayList<>();
		this.showclicked = true;
	}

	protected OpenLayerController getMapController() {
		return mapController;
	}

	protected LatLng getHome() {
		return home;
	}

	protected boolean isShowClicked() {
		return showclicked;
	}

	protected void setShowClicked(boolean showclicked) {
		this.showclicked = showclicked;
	}

	protected boolean isLocated() {
		return located;
	}

	public void addEditListener( IEditListener<LatLng> listener ) {
		this.listeners.add(listener);
	}

	public void removeEditListener( IEditListener<LatLng> listener ) {
		this.listeners.remove(listener);
	}

	protected void notifyEditListeners( EditEvent<LatLng> event ) {
		for( IEditListener<LatLng> listener: listeners)
			listener.notifyInputEdited( event );
	}

	public void addEvalListener( IEvaluationListener<Object> listener ) {
		this.evalListeners.add(listener);
	}

	public void removeEvalListener( IEvaluationListener<Object> listener ) {
		this.evalListeners.remove(listener);
	}

	protected void notifyEvalListeners( EvaluationEvent<Object> event ) {
		for( IEvaluationListener<Object> listener: this.evalListeners)
			listener.notifyEvaluation( event );
	}

	private void onNotifyEvaluation(EvaluationEvent<Object> event) {
		try {
			if( !OpenLayerController.S_CALLBACK_ID.equals(event.getId()) || Utils.assertNull( event.getData())) {
				notifyEvalListeners(event);
				return;
			}
			//If the location of the device was selected, then navigate there
			String str = (String) event.getData()[0];
			if( NavigationView.Commands.isValue(str)) {
				NavigationView.Commands cmd = NavigationView.Commands.valueOf(StringStyler.styleToEnum(str));
				switch( cmd ) {
				case GET_GEO_LOCATION:
					Object[] arr = (Object[]) event.getData()[2];
					home = new LatLng( "home", (double)arr[0], (double)arr[1]);
					located = true;
					updateMap(home);
					notifyEditListeners( new EditEvent<LatLng>( this, EditTypes.CHANGED, home ));
					return;
				default:
					break;
				}
			}
			
			str = (String) event.getData()[1];
			if( !StringUtils.isEmpty(str) && str.startsWith( IPolygon.Types.POINT.name())) {
				Object[] loc = ( Object[])event.getData()[2];
				home = new LatLng((String) event.getData()[1], (double)loc[1], (double)loc[0] );
				this.located = true;
				updateMap(home);
				notifyEditListeners( new EditEvent<LatLng>( this, EditTypes.CHANGED, home ));
				return;
			}
			notifyEvalListeners(event);
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	public void locate() {
		if( located )
			return;
		onNavigation();
	}

	private void onNavigation() {
		try {
			if( located )
				return;
			NavigationView navigation = new NavigationView(mapController);
			navigation.getLocation();
			//Only needed to enforce a refresh
			handler.addData("update");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void updateMap( LatLng home) {
		if( !this.showclicked || ( home == null ))
			return;
		
		IconsView icons = new IconsView( mapController );
		icons.clearIcons();

		Markers marker = Markers.RED;
		char chr = 'H';
		icons.addMarker(home, marker, chr);		
	}

	@Override
	public void dispose() {
		this.mapController.removeEvaluationListener( e->onNotifyEvaluation(e));
		this.mapController.dispose();
		this.removeProgressListener(plistener);
		super.dispose();
	}


	//Only needed to trigger an update
	private class SessionHandler extends AbstractSessionHandler<String>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<String> sevent) {
			// NOTHING
		}
	}
}
