package test.openlayers.map.swt;

import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.Polygon;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.location.LocationEvent;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.openlayer.map.control.GeoView;
import org.openlayer.map.control.MapField;
import org.openlayer.map.control.ShapesView;
import org.openlayer.map.controller.OpenLayerController;
import org.satr.arnac.core.def.IRegisterVessel;
import org.satr.arnac.core.def.IUserData;
import org.satr.arnac.core.model.MapLocation;
import org.satr.arnac.core.registration.IRegistrationClient;
import org.satr.arnac.core.registration.IRegistrationListener;
import org.satr.arnac.ui.swt.location.FieldChangeEvent;
import org.satr.arnac.ui.swt.location.FieldComposite;
import org.satr.arnac.ui.swt.location.IFieldChangeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class OpenLayersComposite extends Composite {
	private static final long serialVersionUID = 1L;

	public enum DrawTypes{
		POINT,
		POLYGON
	}
	
	private FieldComposite fieldComposite;
	
	private Browser browser;
	private OpenLayerController controller;
	
	private IFieldChangeListener listener = new IFieldChangeListener() {

		@Override
		public void notifyLocationChanged(final FieldChangeEvent event) {
			getDisplay().asyncExec( new Runnable() {

				@Override
				public void run() {
					GeoView geo = new GeoView( controller );
					ShapesView shapes = new ShapesView( controller );
					switch( event.getEventType() ) {
					case CLEAR:
						shapes.clear();
						shapes.synchronize();
						break;
					case SET_FIELD:
						geo.setLatlng( event.getLocation());
						int zoom = geo.getZoom();
						geo.setZoom(zoom);
						geo.jump();
						if( event.getField() != null ) {
							Field field = event.getField();
							MapField mapfield = new MapField( controller );
							mapfield.clearShapes();
							mapfield.setStroke("red", 2);
							mapfield.setField(field, 1);
							shapes = new ShapesView( controller );
							shapes.setShape("test", ShapesView.Types.SQUARE );
							shapes.synchronize();
						}
						break;
					case ZOOM_IN:
						geo.zoomin();
						break;
					case ZOOM_OUT:
						geo.zoomout();
						break;
					default:
						break;
					}
					geo.synchronize();
					//fieldComposite.setInput(registration);
				}
			});			
		}

		@Override
		public void notifyLocationChanged(LocationEvent event) {
			// TODO Auto-generated method stub

		}
	};
	
	private IEvaluationListener<Object[]> elistener = new IEvaluationListener<Object[]>() {

		@Override
		public void notifyEvaluation(EvaluationEvent<Object[]> event) {
			if( Utils.assertNull( event.getData()))
				return;
			String wkt = (String )event.getData()[1];
			if( StringUtils.isEmpty( wkt ))
				return;
			try {
				String tp = (String) event.getData()[0];
				StringBuffer buffer = new StringBuffer();
				buffer.append(tp);
				if( wkt.startsWith( Polygon.Types.POLYGON.name())) {
					buffer.append(wkt + "\n");
					Polygon polygon = Polygon.fromWKT("test", wkt);
					LatLng latlng = polygon.getLast();
					LatLng point = LatLngUtils.extrapolate(latlng, 180, 3000);
					latlng.setLatitude(point.getLatitude());
					latlng.setLongitude( point.getLongitude());
					MapField mapfield = new MapField( controller);
					ShapesView shapes = new ShapesView( controller);
					shapes.clear();
					mapfield.setLineStyle("red", 2);
					shapes.addShape(polygon);
					shapes.synchronize();
				}
				logger.info( buffer.toString());
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
			logger.info("Event " + event.getId() + " " + event.getData());
		}	
	};

	private static Logger logger = Logger.getLogger( OpenLayerController.class.getName());
			
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OpenLayersComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		this.fieldComposite = new FieldComposite(this, SWT.BORDER);
		fieldComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.browser = new Browser( this, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.controller = new OpenLayerController(browser, "TEST");
		this.controller.addEvaluationListener(elistener);
		this.fieldComposite.setInput( new RegistrationClient());
		this.fieldComposite.addLocationListener(listener);
	}

	public void setInput() {
		
	}
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void dispose() {
		this.fieldComposite.removeLocationListener(listener);
		this.controller.removeEvaluationListener(elistener);
		super.dispose();
	}

	private class RegistrationClient implements IRegistrationClient{

		@Override
		public LatLng[] getSelection(ILoginUser user) {
			return MapLocation.Location.getLocations();
		}

		@Override
		public void addRegistrationListener(IRegistrationListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeRegistrationListener(IRegistrationListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isRegistered(String name, String passphrase) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRegistered(IRegisterVessel vessel) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public LatLng getLocation(IRegisterVessel vessel) {
			return MapLocation.Location.getLocations()[0];
		}

		@Override
		public LatLng[] getRegisteredLocations() {
			return MapLocation.Location.getLocations();
		}

		@Override
		public String[] getRegisteredVessels() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IUserData getUserData(org.condast.commons.authentication.user.ILoginUser user) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setUserData(org.condast.commons.authentication.user.ILoginUser user, IUserData data) {
			// TODO Auto-generated method stub

		}
	}
}
