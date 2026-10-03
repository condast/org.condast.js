package org.condast.js.commons.ui.widgets.combo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.AbstractEntityComposite;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.controller.IEntityController;
import org.condast.commons.ui.image.AbstractImages;
import org.condast.commons.ui.image.DashboardImages;
import org.condast.commons.ui.image.IImageProvider;
import org.condast.commons.ui.swt.ISelectionWidget;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractComboComposite<T,U extends Object> extends AbstractEntityComposite<T> implements ISelectionWidget<T>{
	private static final long serialVersionUID = 1L;

	protected static final String RWT_FRONTEND = "frontend";

	private Label textLabel;
	private Combo combo;
	private Text text_description;
	private Button infoButton;
	private GridData gd_textLabel;
	private GridData gd_combo;

	private List<U> selection;

	protected AbstractComboComposite( Composite parent, int style ) {
		this( parent, style, false );
	}

	protected AbstractComboComposite( Composite parent, int style, IEntityController<T> controller ) {
		super( parent, style, controller );
		this.selection = new ArrayList<>();
	}

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	protected AbstractComboComposite( Composite parent, int style, boolean skipText ) {
		super( parent, style );
		this.selection = new ArrayList<>();
	}

	@Override
	protected void createComposite( Composite parent, int style ) {
		//int height = 10;
		boolean info = ( SWT.HELP & style ) > 0;
		int grids = info ? 5 : 4;
		setLayout( new GridLayout( grids, false ) );

		textLabel = new Label( this, SWT.RIGHT );
		gd_textLabel = new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 );
		textLabel.setLayoutData( gd_textLabel );
		//gd_textLabel.heightHint = height;

		combo = new Combo( this, ( SWT.READ_ONLY & style ) );
		combo.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected( SelectionEvent e ) {
				getController().notifyWidgetSelected( e );
				super.widgetSelected( e );
				combo.pack();
				refresh();
			}
		});
		gd_combo = new GridData( SWT.FILL, SWT.CENTER, false, false, 1, 1 );
		gd_combo.widthHint = 200;
		//gd_combo.heightHint = height;
		combo.setLayoutData( gd_combo );

		boolean addText = ( SWT.FULL_SELECTION & style ) > 0;
		if( addText ){
			text_description = new Text( this, SWT.BORDER );
			text_description.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
			text_description.addModifyListener( new ModifyListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void modifyText( ModifyEvent event ) {
					notifyInputEdited( new EditEvent<T>( this, EditTypes.CHANGED, getInput()));		
				}
			});
		}

		if( !info )
			return;
		infoButton = new Button( this, SWT.SHADOW_OUT );
		infoButton.setData( RWT.CUSTOM_VARIANT, RWT_FRONTEND );
		infoButton.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
		IImageProvider.ImageSize imageSize = IImageProvider.ImageSize.MEDIUM;
		infoButton.setImage( DashboardImages.getImage( DashboardImages.Images.HELP, IImageProvider.ImageSize.MEDIUM ) );
		//infoButton.setSize( 50, height ) ;
		//infoButton.computeSize( SWT.DEFAULT, SWT.DEFAULT, false );
		infoButton.computeSize( imageSize.getSize(), imageSize.getSize(), false );
		infoButton.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected( SelectionEvent e ) {
				MessageBox messageBox = new MessageBox( getDisplay().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK );
				onInfoButtonPressed( messageBox );
				messageBox.open();
			}
		});
	}

	protected void changeButtonImage( DashboardImages.Images imageEnum, AbstractImages.ImageSize imageSizeWord ) {
		infoButton.setData( null );
		infoButton.setImage( null );
		Image buttonImage = DashboardImages.getImage( imageEnum, imageSizeWord );
		infoButton.setImage( buttonImage );
		infoButton.computeSize( imageSizeWord.getSize(), imageSizeWord.getSize(), true );
	}

	/**
	 * Implements default behaviour when pressing the info button
	 * @param messagebox
	 */
	protected void onInfoButtonPressed( MessageBox messagebox ) {
		/* DEFAULT NOTHING */
	}

	@Override
	public void addSelectionListener( SelectionListener listener ){
		this.combo.addSelectionListener( listener );
		if( text_description != null )
			this.text_description.addSelectionListener( listener );
	}

	@Override
	public void removeSelectionListener( SelectionListener listener ){
		this.combo.removeSelectionListener( listener );
		if( text_description != null )
			this.text_description.removeSelectionListener( listener );
	}

	public void setAlignment( int widthHint ){
		this.gd_textLabel.widthHint = widthHint;
	}

	protected void setCenter( int widthHint ){
		this.gd_combo.widthHint = widthHint;
	}

	@Override
	protected abstract T onGetInput( T input );

	@Override
	protected abstract void onSetInput( T input, boolean overwrite );


	public String getLabelText(){
		return textLabel.getText();
	}

	public void setLabelText( String text ){
		this.textLabel.setText( text );
	}

	public String[] getItems(){
		return combo.getItems();
	}

	public Combo getCombo(){
		return combo;
	}

	public void select( U select ){
		this.combo.select( selection.indexOf( select ) );
	}

	protected String getSelected(){
		return this.combo.getText();
	}

	public U getSelection(){
		return selection.get( this.combo.getSelectionIndex() );
	}

	/**
	 * Store the items and the corresponding names (e.g. after internationalisation)
	 * @param items
	 * @param names
	 */
	protected void setItems( Map<U, String> items ){
		setItems( items, true );
	}

	/**
	 * Store the items and the corresponding names (e.g. after internationalisation)
	 * @param items
	 * @param names
	 */
	protected void setItems( Map<U, String> items, boolean setDefault ){
		this.combo.setItems( items.values().toArray( new String[items.size()] ) );
		this.selection.addAll( items.keySet() );
		if( !setDefault )
			return;
		this.combo.select( 0 );
		this.combo.pack();//fit to the text
	}

	protected void setItems( Map<U, String> items, int index ){
		this.combo.setItems( items.values().toArray( new String[items.size()] ) );
		this.selection.addAll( items.keySet() );
		this.combo.select( index );
		this.combo.pack();//fit to the text
	}

	public U getItem(){
		int select = this.combo.getSelectionIndex();
		if( select < 0 )
			select = 0;
		return this.selection.get( select );
	}

	protected void setItem( U item ){
		int index = this.selection.indexOf( item );
		this.combo.select( index );
		this.combo.pack();
	}

	protected void setText( String str ) {
		if( ( text_description == null ) || StringUtils.isEmpty( str ) )
			return;
		text_description.setText( str );
	}

	protected String getText() {
		if( text_description == null )
			return null;
		return text_description.getText();
	}

	public Text getDescriptionControl() {
		return text_description;
	}

	@Override
	public boolean checkRequiredFields() {
		return ( getItem() != null );
	}

	protected void refresh(){
		Display.getDefault().asyncExec( new Runnable(){

			@Override
			public void run() {
				combo.getParent().requestLayout();
			}
		});
	}
}