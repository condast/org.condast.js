package org.condast.js.commons.ui.widgets.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;
import org.condast.commons.data.plane.Field;
import org.condast.commons.data.plane.FieldData;
import org.condast.commons.data.plane.FieldData.Shapes;
import org.condast.commons.data.plane.IField;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.image.DashboardImages;
import org.condast.commons.ui.widgets.field.FieldChangeEvent;
import org.condast.commons.ui.widgets.field.FieldDialog;
import org.condast.commons.ui.widgets.field.IFieldChangeListener;
import org.condast.commons.ui.widgets.field.IFieldChangeListener.LocationEvents;
import org.condast.commons.ui.widgets.location.ILocationChangeListener;
import org.condast.commons.ui.widgets.location.LocationEvent;
import org.condast.js.commons.ui.widgets.location.LocationComposite;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class FieldComposite extends Composite {
	private static final long serialVersionUID = 1L;

	public static final String S_ARNAC = "arnac";
	public static final String S_NEW_LOCATION = "New Location";

	private LocationComposite locationComposite;

	private Collection<IFieldChangeListener> listeners;

	private LinkedList<FieldData> fields;

	private Composite drawComposite;
	private Spinner lengthSpinner;
	private Spinner widthSpinner;
	private Spinner angleSpinner;
	private CCombo polygonCombo;
	private GridData gd_draw;
	private FieldComposite fieldComposite;
	private Button drawButton;
	private Button btnAddButton;
	private Button btnNewButton;
	private Button btnLocateButton;
	private boolean disposed;

	private LatLng location;

	private ILocationChangeListener locationListener = e->onNotifyLocationChanged(e);
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FieldComposite(Composite parent, int style) {
		super(parent, style);
		this.disposed = false;
		this.fields = new LinkedList<>();
		this.listeners = new ArrayList<>();
		this.createComposite(parent, style);
		this.fieldComposite = this;
		this.location = null;
	}

	public void createComposite( Composite parent, int style ){
		GridLayout gridLayout = new GridLayout(3, false);
		setLayout(gridLayout);

		locationComposite = new LocationComposite( this, style);
		locationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		locationComposite.addLocationListener( this.locationListener );

		Composite cmpFieldControl = new Composite(this, SWT.BORDER);
		cmpFieldControl.setLayout(new GridLayout(1, false));
		cmpFieldControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite composite = new Composite( cmpFieldControl, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
		composite.setData( RWT.CUSTOM_VARIANT, S_ARNAC );
		GridLayout gl_composite = new GridLayout(9, false);
		composite.setLayout(gl_composite);

		Button zoomButton = new Button(composite, SWT.NONE);
		zoomButton.setData( RWT.CUSTOM_VARIANT, S_ARNAC);
		zoomButton.setImage( new FieldImages().getImage( FieldImages.Images.ZOOM_IN ));
		zoomButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		zoomButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FieldData field = fields.get( locationComposite.getSelectedIndex());
					notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.ZOOM_IN ));
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});

		zoomButton = new Button(composite, SWT.NONE);
		zoomButton.setData( RWT.CUSTOM_VARIANT, S_ARNAC);
		zoomButton.setImage( new FieldImages().getImage( FieldImages.Images.ZOOM_OUT ));
		zoomButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		zoomButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FieldData field = fields.get( locationComposite.getSelectedIndex());
					notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.ZOOM_OUT ));
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});
		createButton(composite, FieldImages.Images.UP, IFieldChangeListener.LocationEvents.UP);
		createButton(composite, FieldImages.Images.LEFT, IFieldChangeListener.LocationEvents.LEFT);
		createButton(composite, FieldImages.Images.RIGHT, IFieldChangeListener.LocationEvents.RIGHT);
		createButton(composite, FieldImages.Images.DOWN, IFieldChangeListener.LocationEvents.DOWN);

		btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setData( RWT.CUSTOM_VARIANT, S_ARNAC);
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setImage( DashboardImages.getImage( DashboardImages.Images.FIELD, 32 ));
		btnNewButton.setEnabled(false);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FieldData fd = FieldData.getDefaultField(location );
					fd.setName(S_NEW_LOCATION);
					addInput(fd);
					notifyLocationChanged( new FieldChangeEvent( fieldComposite, fd, LocationEvents.NEW, true ));
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});

		btnLocateButton = new Button(composite, SWT.NONE);
		btnLocateButton.setData( RWT.CUSTOM_VARIANT, S_ARNAC);
		btnLocateButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FieldData fieldData = getFieldData();
					notifyLocationChanged( new FieldChangeEvent( fieldComposite, fieldData, LocationEvents.LOCATE, true ));
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});
		btnLocateButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnLocateButton.setImage( DashboardImages.getImage( DashboardImages.Images.LOCATE, 32 ) );

		drawButton = new Button( composite, SWT.CHECK );
		drawButton.setEnabled(false);
		drawButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Button button = (Button) e.widget;
				try {
					boolean edit = button.getSelection();
					gd_draw.exclude = !edit;
					drawComposite.setVisible( edit );
					drawComposite.requestLayout();
					FieldData field = fields.get( locationComposite.getSelectedIndex());
					if( edit )
						notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.EDIT ));
					else {
						notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.FINISH ));
						notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.VIEW ));
					}

				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});
		drawButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		drawComposite = new Composite(cmpFieldControl, SWT.NONE);
		drawComposite.setLayout(new GridLayout(10, false));
		gd_draw = new GridData(SWT.RIGHT, SWT.FILL, true, true );
		gd_draw.exclude = true;
		drawComposite.setLayoutData(gd_draw);

		Label lblLength = new Label(drawComposite, SWT.NONE);
		lblLength.setBounds(0, 0, 55, 15);
		lblLength.setText("Length:");

		lengthSpinner = new Spinner(drawComposite, SWT.BORDER);
		lengthSpinner.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FieldData field = fields.get( locationComposite.getSelectedIndex());
					field.setLength(lengthSpinner.getSelection());
					fields.add(locationComposite.getSelectedIndex(), field);
					notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.UPDATE_FIELD));
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});
		lengthSpinner.setMaximum(10000);
		lengthSpinner.setSelection(100);
		lengthSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lengthSpinner.setBounds(0, 0, 76, 21);

		Label lblWidth = new Label(drawComposite, SWT.NONE);
		lblWidth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblWidth.setText("Width:");

		widthSpinner = new Spinner(drawComposite, SWT.BORDER);
		widthSpinner.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FieldData field = fields.get( locationComposite.getSelectedIndex());
					field.setWidth(widthSpinner.getSelection());
					fields.add(locationComposite.getSelectedIndex(), field);
					notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.UPDATE_FIELD));
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});
		widthSpinner.setMaximum(10000);
		widthSpinner.setSelection(100);
		widthSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		Label lblRotation = new Label(drawComposite, SWT.NONE);
		lblRotation.setText("Rotation:");

		angleSpinner = new Spinner(drawComposite, SWT.BORDER);
		angleSpinner.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					int angle = angleSpinner.getSelection();
					angleSpinner.setSelection((int) LatLngUtilsDegrees.mod(angle));
					FieldData field = fields.get( locationComposite.getSelectedIndex());
					field.setAngle(angleSpinner.getSelection());
					fields.add(locationComposite.getSelectedIndex(), field);
					notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.UPDATE_FIELD));
					super.widgetSelected(e);
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});
		angleSpinner.setMinimum(-360);
		angleSpinner.setMaximum(360);
		angleSpinner.setSelection(0);
		angleSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		polygonCombo = new CCombo( drawComposite, SWT.NONE);
		polygonCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		polygonCombo.setItems(Shapes.getItems());
		polygonCombo.select(Shapes.CIRCLE.ordinal());
		polygonCombo.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					CCombo combo = (CCombo) e.widget;
					FieldData field = fields.get( locationComposite.getSelectedIndex());
					Shapes shape = Shapes.values()[ combo.getSelectionIndex()];
					field.setShape(shape);
					notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.UPDATE_FIELD));
					super.widgetSelected(e);
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});

		Button btnClearButton = new Button(drawComposite, SWT.NONE);
		btnClearButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FieldData field = fields.get( locationComposite.getSelectedIndex());
					notifyLocationChanged( new FieldChangeEvent(this, field, IFieldChangeListener.LocationEvents.CLEAR ));
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});
		btnClearButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnClearButton.setImage( DashboardImages.getImage( DashboardImages.Images.DELETE, 32 ));

		btnAddButton = new Button(drawComposite, SWT.NONE);
		btnAddButton.setData( RWT.CUSTOM_VARIANT, S_ARNAC);
		btnAddButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FieldDialog dialog = new FieldDialog( getDisplay().getActiveShell(), "Save Field");
					if( dialog.open() == Window.OK ) {
						if( StringUtils.isEmpty( dialog.getName() ))
							return;
						FieldData field = createField( dialog.getName());
						fields.add( field);
						field.setDescription(dialog.getName());
						locationComposite.addSelection(field.getCoordinates(), true);
						gd_draw.exclude = true;
						drawComposite.setVisible( false );
						drawComposite.requestLayout();
						notifyLocationChanged( new FieldChangeEvent( fieldComposite, field, LocationEvents.ADD, true ));
					}
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});
		btnAddButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnAddButton.setImage( DashboardImages.getImage( DashboardImages.Images.ADD, 32 ));
	}

	private void onNotifyLocationChanged(LocationEvent event) {
		try {
			if( Utils.assertNull(fields))
				return;
			FieldData fieldData = Utils.assertNull(fields)? null: fields.get( event.getSelectionIndex());
			updateFields(fieldData);
			for( IFieldChangeListener listener: listeners )
				listener.notifyLocationChanged( new FieldChangeEvent( this, fieldData, LocationEvents.SET_FIELD ));
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	protected void includecontrol( Composite parent, int style ) {
		/* DEFAULT NOTHING */
	}

	protected Button createButton( Composite parent, FieldImages.Images image, final IFieldChangeListener.LocationEvents eventType ) {
		Button button = new Button(parent, SWT.NONE);
		button.setData( RWT.CUSTOM_VARIANT, S_ARNAC);
		button.setImage( new FieldImages().getImage( image ));
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		button.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					if( Utils.assertNull(fields))
						return;
					FieldData fieldData = fields.get( locationComposite.getSelectedIndex());
					notifyLocationChanged( new FieldChangeEvent(this, fieldData, eventType ));
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
		return button;

	}

	public void addFieldListener( IFieldChangeListener listener ) {
		this.listeners.add( listener );
	}

	public void removeFieldListener( IFieldChangeListener listener ) {
		this.listeners.remove( listener );
	}

	protected void notifyLocationChanged( FieldChangeEvent event ) {
		switch( event.getEventType()) {
		case ADD:
		case FINISH:
			drawButton.setSelection(false);
			break;
		default:
			break;
		}
		for(IFieldChangeListener listener: this.listeners )
			listener.notifyLocationChanged(event);
	}

	public void enableNewButton( LatLng location, boolean choice ) {
		this.location = location;
		this.btnNewButton.setEnabled(choice);
	}

	public void enableDraw( final boolean enabled ) {
		if( disposed || getDisplay().isDisposed())
			return;
		getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				drawButton.setEnabled(enabled);
				if(!enabled ) {
					gd_draw.exclude = true;
					drawButton.setSelection(false);
					drawComposite.setVisible( false );
				}
			}
		});
	}

	public int getIndex( FieldData fieldData ) {
		return fields.indexOf(fieldData);
	}

	public FieldData getFieldData() {
		return Utils.assertNull(fields)? null: fields.get( locationComposite.getSelectedIndex());
	}

	public LatLng getSelected() {
		return this.locationComposite.getSelected();
	}

	public int getSelectedIndex() {
		return this.locationComposite.getSelectedIndex();
	}

	public FieldData setSelection( int selection ) {
		locationComposite.setSelection( selection );
		return this.fields.get(selection);
	}

	public FieldData setSelection( FieldData fieldData ) {
		for( int i=0; i< fields.size(); i++ ) {
			FieldData field = fields.get(i);
			if( field.getId() != fieldData.getId() )
				continue;
			locationComposite.setSelection( i );
			notifyLocationChanged(new FieldChangeEvent(this, fieldData, IFieldChangeListener.LocationEvents.SET_FIELD, false));
			return field;
		}
		return null;
	}

	public FieldData[] getInput() {
		return this.fields.toArray( new FieldData[ this.fields.size()]);
	}

	public FieldData setInput( FieldData[] data ) {
		if( this.isDisposed())
			return null;
		this.drawButton.setEnabled(false);
		this.fields.clear();
		if( Utils.assertNull( data )) {
			return null;
		}
		this.fields.addAll( Arrays.asList(data));
		drawButton.setEnabled(true);
		int selection = 0;
		for( int i=0; i< data.length; i++ ){
			FieldData fd = data[i];
			if( !fd.isSelected() )
				continue;
			selection = i;
		}

		FieldData field = data[selection];
		if( field != null ) {
			LatLng[] locations = new LatLng[data.length];
			for( int i=0; i< data.length; i++ )
				locations[i] = data[i].getCoordinates();
			locationComposite.setInput( locations, selection );
			updateFields(field );
		}
		return field;
	}

	public void setInput( LatLng location ) {
		this.location = location;
		FieldData[] fieldData = fieldComposite.getInput();
		if( Utils.assertNull(fieldData)) {
			fieldData = new FieldData[1];
			fieldData[0] = new FieldData( new Field( location, 100, 100 ));
		}
		setInput(fieldData);
	}

	private void addInput( FieldData field ) {
		Collection<FieldData> fieldData = Arrays.asList( fieldComposite.getInput());
		List<FieldData> results = fieldData.stream().filter( f-> f.getId() >= 0).collect( Collectors.toList());
		results.add(field);
		fields.clear();
		fields.addAll(results);
		locationComposite.addSelection(field.getCoordinates(), false );
	}

	protected void updateFields( FieldData fieldData ) {
		lengthSpinner.setSelection((int) fieldData.getLength());
		widthSpinner.setSelection((int) fieldData.getWidth());
		angleSpinner.setSelection((int) fieldData.getAngle());
		polygonCombo.select( Shapes.getIndex( fieldData.getShape()));
	}

	protected FieldData createField( String name ) {
		int select = locationComposite.getSelectedIndex();
		FieldData fd = this.fields.get( select );
		fd.getCoordinates().setId(name);
		IField field = new Field( fd.getCoordinates(), lengthSpinner.getSelection(),
		widthSpinner.getSelection(), angleSpinner.getSelection());
		FieldData newField = new FieldData( field, fd.getZoom(), false );
		fields.set(select, newField);
		return newField;
	}

	public static FieldData[] getDefaultField( LatLng location ) {
		FieldData fieldData = FieldData.getDefaultField(location);
		FieldData[] fd = new FieldData[1];
		fd[0] = fieldData;
		return fd;

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void dispose() {
		this.disposed = true;
		locationComposite.removeLocationListener( this.locationListener);
		super.dispose();
	}
}