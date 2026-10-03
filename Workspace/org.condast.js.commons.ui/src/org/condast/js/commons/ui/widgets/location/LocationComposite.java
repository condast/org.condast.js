package org.condast.js.commons.ui.widgets.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.swt.DigitsSpinner;
import org.condast.commons.ui.widgets.location.ILocationChangeListener;
import org.condast.commons.ui.widgets.location.LocationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LocationComposite extends Composite {
	private static final long serialVersionUID = 1L;

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
	public LocationComposite(Composite parent, int style) {
		super(parent, style);
		this.locations = new ArrayList<>();
		this.listeners = new ArrayList<>();
		this.createComposite(parent, style);
	}

	public void createComposite( Composite parent, int style ){
		int columns = (( style & SWT.BAR ) >0)?6 : 4;
		setLayout(new GridLayout(columns, false));

		Label locationLabel = new Label( this, SWT.NONE );
		locationLabel.setText("Location: ");
		locationLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		locationCombo = new CCombo(this, SWT.BORDER);
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_combo.horizontalSpan = ( columns == 4)?3:1;
		locationCombo.setLayoutData(gd_combo);
		locationCombo.setEnabled(false);
		locationCombo.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{					
					CCombo combo = (CCombo) e.widget;
					selection = locationCombo.getSelectionIndex();
					LatLng latlng = locations.get(selection);
					latlng.setId(combo.getText());
					notifyLocationChanged( new LocationEvent( this, latlng, selection ));
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
		locationCombo.addModifyListener( new ModifyListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void modifyText(ModifyEvent event) {
				try{
					CCombo combo = (CCombo) event.widget;
					LatLng latlng = locations.get(selection);
					latlng.setId(combo.getText());
					notifyLocationChanged( new LocationEvent( this, latlng, selection ));
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

	public String getComboName() {
		return this.locationCombo.getText();
	}
	
	public int getSelectedIndex() {
		return selection;
	}

	/**
	 * Get the selected location
	 * @return
	 */
	public LatLng getSelected() {
		if( Utils.assertNull( this.locations ))
			return null;
		return this.locations.get(selection);
	}

	public void setInput( LatLng[] locations, final int selection  ) {
		if( super.isDisposed())
			return;
		this.locations = new ArrayList<>( );
		if( !Utils.assertNull(locations))
			this.locations.addAll( Arrays.asList( locations));
		if( Utils.assertNull(this.locations))
				return;
		locationCombo.setEnabled(true);
		final Collection<String> names = new ArrayList<>();
		for( LatLng location: this.locations ) {
			String name = location.getId();
			if(!StringUtils.isEmpty(name))
				names.add( name );
		}
		this.selection =0;
		this.locationCombo.select(0);
		if( Utils.assertNull(names))
			return;
		try {
			locationCombo.setItems( names.toArray( new String[ names.size()]) );
			setSelection(selection);
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	public void addSelection( LatLng latlng, boolean notify ){
		this.locations.add(latlng);
		this.locationCombo.add( latlng.getId() );
		this.locationCombo.select( this.locationCombo.indexOf( latlng.getId()));
		latSpinner.setSelection( latlng.getLongitude());
		lonSpinner.setSelection( latlng.getLatitude());
		this.selection = this.locations.size()-1;
		if(notify )
			notifyLocationChanged( new LocationEvent( this, latlng ));
	}

	public void setSelection( int selection ){
		if( Utils.assertNull(this.locations)) {
			this.selection = 0;
			this.locationCombo.select(0);
			notifyLocationChanged( new LocationEvent( this, null, selection ));
			return;
		}
		if( this.locationCombo.getSelectionIndex() == selection )
			return;
		this.selection = ( this.locationCombo.getItemCount() > selection )? selection: this.locationCombo.getItemCount()-1;
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
