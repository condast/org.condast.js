package org.condast.js.commons.ui.widgets.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.ui.swt.DigitsSpinner;
import org.condast.commons.ui.widgets.location.ILocationChangeListener;
import org.condast.commons.ui.widgets.location.LocationEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class LocationGroup extends Group {
	private static final long serialVersionUID = 1L;

	public static final String S_ARNAC = "arnac";
	public static final String S_CHART = "Chart";

	public static final String S_LONGTITUDE = "Longtitude:";
	public static final String S_LATITUDE = "Latitude:";

	private CCombo locationCombo;
	private DigitsSpinner latSpinner;
	private DigitsSpinner lonSpinner;

	private List<LatLng> locations;
	private int selection;
	private Collection<ILocationChangeListener> listeners;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LocationGroup(Composite parent, int style) {
		super(parent, style);
		this.locations = new ArrayList<>();
		this.listeners = new ArrayList<>();
		this.createComposite(parent, style);
	}

	public void createComposite( Composite parent, int style ){
		setData( RWT.CUSTOM_VARIANT, S_ARNAC );
		setLayout(new GridLayout(5, false));

		locationCombo = new CCombo(this, SWT.BORDER);
		locationCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		locationCombo.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					selection = locationCombo.getSelectionIndex();
					setSelection(selection);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
		Label lblLongtitude = new Label( this, SWT.NONE);
		lblLongtitude.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblLongtitude.setText( S_LONGTITUDE);
		latSpinner = new DigitsSpinner( this, SWT.BORDER);
		latSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		latSpinner.setDigits(5);
		latSpinner.setMaximum( 10000);
		latSpinner.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				LatLng location = new LatLng( lonSpinner.getSelection(), latSpinner.getSelection() );
				locations.remove(selection);
				locations.add(selection, location);
				setSelection( selection );
			}
		});

		Label lblLatitude = new Label( this, SWT.NONE);
		lblLatitude.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblLatitude.setText( S_LATITUDE);

		lonSpinner = new DigitsSpinner( this, SWT.BORDER);
		lonSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lonSpinner.setDigits(5);
		lonSpinner.setMaximum(10000);

		lonSpinner.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				LatLng location = new LatLng( lonSpinner.getSelection(), latSpinner.getSelection() );
				locations.remove(selection);
				locations.add(selection, location);
				setSelection( selection );
			}
		});
	}

	public void addLocationListener( ILocationChangeListener listener ) {
		this.listeners.add( listener );
	}

	public void removeLocationListener( ILocationChangeListener listener ) {
		this.listeners.remove( listener );
	}

	protected void notifyLocationChanged( LocationEvent event ) {
		for(ILocationChangeListener listener: this.listeners )
			listener.notifyLocationChanged(event);
	}

	public void setInput( LatLng[] locations ) {
		this.locations = new ArrayList<>( Arrays.asList( locations));
		Collection<String> names = new ArrayList<>();
		for( LatLng location: locations )
			names.add( location.getId());
		locationCombo.setItems( names.toArray( new String[ names.size()]) );
		setSelection( this.selection );
	}

	public void setSelection( int selection ){
		this.selection = selection;
		this.locationCombo.select( this.selection );
		LatLng latlng = this.locations.get(this.selection);
		latSpinner.setSelection( latlng.getLongitude());
		lonSpinner.setSelection( latlng.getLatitude());
		notifyLocationChanged( new LocationEvent( this, latlng, selection ));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
