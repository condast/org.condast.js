package org.condast.js.commons.ui.widgets.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.condast.commons.Utils;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.image.DashboardImages;
import org.condast.commons.ui.image.IImageProvider;
import org.condast.commons.ui.image.LabelProviderImages;
import org.condast.commons.ui.image.LabelProviderImages.Images;
import org.condast.commons.ui.wtk.IStoreWithDelete;
import org.condast.js.commons.ui.widgets.edit.CheckBoxEditingSupport;
import org.condast.commons.ui.widgets.celleditors.AbstractCheckBoxCellEditor;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class AbstractTableViewerWithDelete<T extends Object> extends Composite {
	private static final long serialVersionUID = 1L;

	public enum Buttons{
		ADD,
		DELETE,
		DONTSHOW,
		REMOVE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	private TableViewer viewer;
	private DeleteViewerComparator comparator;

	private int deleteColumnIndex;

	private Composite buttonBar;
	private Map<Buttons, Button> buttons;

	private boolean barOnTop;

	private TableColumnLayout tableLayout;
	private Collection<SelectionListener> slisteners;

	private Collection< IEditListener<T>> listeners;

	private int selectedColumn;

	private boolean disposed;

	protected AbstractTableViewerWithDelete( Composite parent, int style ) {
		this( parent, style, false );
	}

	protected AbstractTableViewerWithDelete( Composite parent, int style, boolean includeAddButton ) {
		this( parent,style, createDefaultMap(includeAddButton));		
	}
	
	protected AbstractTableViewerWithDelete( Composite parent, int style, EnumMap<Buttons,Integer> buttons ) {
		super( parent, style );
		this.deleteColumnIndex = 0;
		slisteners = new ArrayList<>();
		listeners=  new ArrayList<>();
		createContentComposite( parent, style );
		this.buttons = new HashMap<>();
		this.setupButtonbar( buttonBar, buttons );

		this.selectedColumn = 0;
		this.disposed = false;
		this.init();
		requestLayout();
	}

	protected void createContentComposite( Composite parent, int style ){
		setLayout( new GridLayout( 1, false ) );

		if( this.barOnTop ){
			buttonBar = new Composite( this, SWT.NONE );
		}

		//Required for table viewer
		Composite comp = new Composite( this, SWT.NONE );
		comp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
		tableLayout = new TableColumnLayout();
		comp.setLayout( tableLayout );

		viewer = new TableViewer( comp, SWT.BORDER|SWT.MULTI|SWT.FULL_SELECTION );
		Table table = viewer.getTable();
		table.setHeaderVisible( true );
		table.setLinesVisible( true );
		table.addMouseListener( new MouseAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void mouseDown( MouseEvent event ) {
		        try {
					Point p = new Point( event.x, event.y );
					ViewerCell cell = viewer.getCell( p );
					if( cell == null )
						return;
					selectedColumn = cell.getColumnIndex();
				} catch( Exception e ) {
					e.printStackTrace();
				}
		    }
		});
		viewer.setUseHashlookup( true );
		comparator = new DeleteViewerComparator();
		viewer.setComparator( comparator );
		viewer.setContentProvider( ArrayContentProvider.getInstance() );
		viewer.setLabelProvider( new DeleteLabelProvider() );
	    viewer.addSelectionChangedListener( new ISelectionChangedListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					Iterator<?> iterator = selection.iterator();
					while( iterator.hasNext() ){
						StoreWithDelete swd = (AbstractTableViewerWithDelete<T>.StoreWithDelete)iterator.next() ;
						onRowClicked( swd.data );
					}
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}
	    });
	    viewer.addDoubleClickListener( new IDoubleClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void doubleClick( DoubleClickEvent event ) {
				try {
					IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
					Iterator<?> iterator = selection.iterator();
					while( iterator.hasNext() ){
						StoreWithDelete swd = (AbstractTableViewerWithDelete<T>.StoreWithDelete)iterator.next() ;
						onRowDoubleClick( swd.data );
					}
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}
		});

		if( !this.barOnTop ){
			buttonBar = new Composite( this, SWT.NONE );
		}

		GridData gd_buttons = new GridData( SWT.FILL, SWT.FILL, true, false );
		buttonBar.setLayoutData( gd_buttons );
	}


	public void addEditListener( IEditListener<T> listener ){
		this.listeners.add( (IEditListener<T>) listener );
	}

	public void removeEditListener( IEditListener<T> listener ){
		this.listeners.remove( listener );
	}

	protected void notifyEditEvent( EditEvent<T> event ) {
		this.listeners.forEach( l-> l.notifyInputEdited(event));
	}

	protected void onRowClicked( T selection ) {
		notifyEditEvent( new EditEvent<T>( this, EditEvent.EditTypes.VIEW, selection ));
	}

	protected void onRowDoubleClick( T selection ) {
		notifyEditEvent( new EditEvent<T>( this, EditEvent.EditTypes.SELECTED, selection ));
	}

	protected void init(){ /* Default NOTHING */ }

	protected boolean isBarOnTop() {
		return barOnTop;
	}

	protected void setBarOnTop( boolean barOnTop ) {
		this.barOnTop = barOnTop;
	}

	public Composite getButtonBar() {
		return buttonBar;
	}

	protected Button getButton( Buttons type ) {
		return this.buttons.get(type);
	}
	
	protected int getSelectedColumn() {
		return selectedColumn;
	}
	
	/**
	 *
	 * @param buttonBar the buttonBar composite
	 * @param bMap an EnumMap<Buttons, Integer> containing buttonTypes of type Buttons, with the size as Integer
	 */
	protected void setupButtonbar( Composite buttonBar, EnumMap<Buttons, Integer> bMap ){
		//A column for an empty label on the left plus columns for the amount of button types of type Buttons:
		int columns = 1 + bMap.size();
		buttonBar.setLayout( new GridLayout( columns, false ) );
		Label label = new Label( buttonBar, SWT.LEFT );//an empty label on the left
		label.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );

		for( Entry<Buttons, Integer> entry : bMap.entrySet() ) {
			Buttons button = entry.getKey();
			Integer	  buttonSize	= bMap.get( entry.getKey() );//the size of the button
			IImageProvider.ImageSize imageSizeEnum	= IImageProvider.ImageSize.getImageSize( buttonSize );
			Button control = null;
			switch( button ) {
			case DONTSHOW:
			case DELETE:
				control = createButton(buttonBar, button, imageSizeEnum );
				control.addSelectionListener( new SelectionAdapter() {
					private static final long serialVersionUID = 1L;

					@Override
					public void widgetSelected( SelectionEvent e ) {
						try {
							Button button = (Button) e.widget;
							boolean result = deleteButtonPressed( e );
							button.setEnabled( !result );
						}
						catch( Exception ex ){
							ex.printStackTrace();
						}
					}
				});
				break;
			default:
				control = createButton( buttonBar, button, imageSizeEnum );
				control.setEnabled( true );
				control.addSelectionListener( new SelectionAdapter() {
					private static final long serialVersionUID = 1L;

					@Override
					public void widgetSelected( SelectionEvent e ) {
						try {
							Button button = (Button) e.widget;
							Buttons type = (Buttons) button.getData();
							onButtonSelected( type, e );
							super.widgetSelected( e );
						} catch( Exception e1 ) {
							e1.printStackTrace();
						}
					}
				});
				break;
			}
			buttons.put(button, control );
			this.onButtonCreated( button, control );
		}
	}

	//Allow modification of the button
	protected abstract void onButtonCreated( Buttons type, Button button );

	/**
	 * respond to an add button click.
	 * returns true if the add was completed successfully.
	 * @return
	 */
	protected boolean onButtonSelected( Buttons button, SelectionEvent e ){
		return true;
	}

	/**
	 * respond to a delete button click.
	 * returns true if the deletion was completed successfully.
	 * @return
	 */
	protected abstract boolean onDeleteButton( Collection<T> deleted );

	/**
	 * respond to a dontshow button click.
	 * returns true if the dontshow was completed successfully.
	 * @return
	 */
	protected boolean onDontShowButton( Collection<T> dontshow ) {
		return true;
	}

	/**
	 * @param columnName the column heading
	 * @param index
	 * @param weight
	 * @param alignmentContent for instance expressed in SWT.NONE, SWT.LEFT, SWT.CENTER or SWT.RIGHT
	 * @return TableViewerColumn the column you want
	 */
	protected TableViewerColumn createColumn( String columnName, int index, int weight, int alignmentContent ) {
		TableViewerColumn result = new TableViewerColumn( viewer, alignmentContent );
		TableColumn tcolumn = result.getColumn();
		if( !StringUtils.isEmpty( columnName ) )
			tcolumn.setText( columnName );
		tcolumn.setMoveable( true );
		tcolumn.addSelectionListener( getSelectionAdapter( tcolumn, index ) );
		tableLayout.setColumnData( tcolumn, new ColumnWeightData( weight ) );
		return result;
	}

	protected TableViewerColumn createColumn( String columnName, int index, int weight ) {
		return createColumn( columnName, index, weight, SWT.NONE );
	}

	private SelectionAdapter getSelectionAdapter( final TableColumn column,
			final int index ) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected( SelectionEvent e ) {
				try {
					comparator.setColumn( index );
					int dir = comparator.getDirection();
					viewer.getTable().setSortDirection( dir );
					viewer.getTable().setSortColumn( column );
					viewer.refresh();
				} catch( Exception e1 ) {
					e1.printStackTrace();
				}
			}
		};
		return selectionAdapter;
	}

	public void addSelectionListener( SelectionListener listener ) {
		slisteners.add( listener );
	}

	public void removeSelectionListener( SelectionListener listener ) {
		slisteners.add( listener );
	}

	protected void notifyWidgetSelected( SelectionEvent event ){
		for( SelectionListener listener: this.slisteners )
			listener.widgetSelected( event );
	}

	@SuppressWarnings("unchecked")
	protected boolean deleteButtonPressed( SelectionEvent event ){
		Collection<StoreWithDelete> input = (Collection<StoreWithDelete>)this.viewer.getInput();
		Map<StoreWithDelete, T> deleted = new HashMap<>();
		for( StoreWithDelete swd: input ){
			if( swd.isDelete() )
				deleted.put( swd, swd.getStore() );
		}
		boolean result = onDeleteButton( deleted.values() );
		if( result ){
			if(!Utils.assertNull(deleted)) {
				input.removeAll( deleted.keySet() );
				notifyEditEvent( new EditEvent<T>( this, EditTypes.DELETE, deleted.values()));
			}
			this.viewer.setInput( input );
		}
		Button button = (Button) event.widget;
		button.setEnabled( !result );
		event.data = EditTypes.CHANGED;
		this.notifyWidgetSelected( event );
		this.refresh();
		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean updateDeleteButton() {
		Button button = this.buttons.get(Buttons.DELETE);
		button.setEnabled(false);
		Collection<StoreWithDelete> input = (Collection<StoreWithDelete>)this.viewer.getInput();
		if( Utils.assertNull(input))
			return false;
		for( StoreWithDelete s: input ){ 
			if( s.isDelete()) {
				button.setEnabled(true);
				return true;
			}
		};
		return false;
	}
	
	protected TableViewer getViewer() {
		return viewer;
	}

	protected int getDeleteColumnindex() {
		return deleteColumnIndex;
	}

	protected TableViewerColumn createDeleteColumn( int deleteColumnindex, String name, int weight ) {
		TableViewerColumn tcol = this.createColumn( name, deleteColumnindex, weight );
		tcol.setEditingSupport( new CheckBoxEditingSupport<>( viewer, new DeleteCheckBoxEditor() ) );
		this.deleteColumnIndex = deleteColumnindex;
		return tcol;
	}

	/**
	 * @param deleteColumnindex
	 * @param name
	 * @param weight
	 * @param alignmentContent for instance expressed in SWT.NONE, SWT.LEFT, SWT.CENTER or SWT.RIGHT
	 * @return TableViewerColumn the column you want
	 */
	protected TableViewerColumn createDeleteColumn( int deleteColumnindex, String name, int weight, int alignmentContent ) {
		TableViewerColumn tcol = this.createColumn( name, deleteColumnindex, weight, alignmentContent );
		tcol.setEditingSupport( new CheckBoxEditingSupport<>( viewer, new DeleteCheckBoxEditor() ) );
		this.deleteColumnIndex = deleteColumnindex;
		return tcol;
	}

	/**
	 * Returns true if the table is empty
	 * @return
	 */
	public boolean isEmpty(){
		return ( getStoreInput() == null ) ? true: getStoreInput().isEmpty();
	}

	@SuppressWarnings("unchecked")
	protected Collection<IStoreWithDelete<T>> getStoreInput(){
		return (Collection<IStoreWithDelete<T>>)viewer.getInput();
	}

	@SuppressWarnings("unchecked")
	protected T[] getInput(){
		Collection<StoreWithDelete> input = (Collection<AbstractTableViewerWithDelete<T>.StoreWithDelete>)viewer.getInput();
		if( Utils.assertNull( input ) )
			return null;
		Collection<T> results = new ArrayList<>();
		for( StoreWithDelete str: input )
			results.add( str.getStore() );
		return (T[])results.toArray();
	}

	protected void setInput( Collection<T> elements ){
		Collection<StoreWithDelete> input = new ArrayList<>();
		int index = 0;
		for( T element: elements ){
			input.add( new StoreWithDelete( element, index++, elements.size() ) );
		}
		viewer.setInput( input );
	}

	protected abstract void onRefresh();

	protected void refresh(){
		if( isDisposed() || disposed )
			return;
		getDisplay().asyncExec( new Runnable(){

			@Override
			public void run() {
				if( isDisposed() || disposed )
					return;
				try {
					onRefresh();
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
				layout( false );
			}
		});
	}

	/**
	 * sometimes the index of the comparator should be set
	 * externally.
	 * @param index
	 */
	public void setComparatorIndex( int index, int direction ){
		comparator.setColumnIndex( index, direction );
	}

	/**
	 * Create a comparator for the given column. return null if a
	 *  default is good enough.
	 *
	 * @param index
	 * @return
	 */
	protected Comparator<T> getColumnComparator( int index ){
		return null;
	}

	@Override
	public void dispose() {
		disposed = true;
		super.dispose();
	}

	private static EnumMap<Buttons, Integer> createDefaultMap( boolean includeAddButton ){
		EnumMap<Buttons, Integer> buttons = new EnumMap<>( Buttons.class);
		buttons.put(Buttons.DELETE, 32);
		if( includeAddButton )
			buttons.put(Buttons.ADD, 32);
		return buttons;
	}

	/**
	 * Create a button of the wished size and type
	 * If the corresponding button image (f.i. dustbin.png) is not present or has not the same name as the buttonType,
	 * show its text (f.i. "Delete").
	 * Sometimes The position of the button must be corrected, because the image displayed on it is not displayed right.
	 * This is done with the attribute correctedPosition with possible values SWT.LEFT, SWT.CENTER, SWT.RIGHT.
	 * The attribute widening is used for further correction.
	 * A button with a backgroundImage in stead of a (front) image does not have to be corrected,
	 * but alas a backgroundImage cannot be greyed with btn.setEnabled( false )
	 * @param buttonType the corresponding type described in Buttons, like Buttons.ADD, DELETE, REMOVE
	 * @param buttonSize the corresponding size described in ImageSize, like AbstractImages.ImageSize.TINY, SMALL, and so on
	 * @return btn the desired button
	 */
	protected static Button createButton( Composite buttonBar, Buttons buttonType, IImageProvider.ImageSize buttonSize ){
		int correctedPosition;
		int widening = 0;
		switch( buttonSize ) {
			case TINY://16 The tiny image won't otherwise fit correctly onto the button.
					  //Only necessary for btn.setImage, not for btn.setBackgroundImage.
				correctedPosition = SWT.RIGHT;
				widening =  - buttonSize.getSize() / 16;
			break;
			case SMALL://24
				correctedPosition = SWT.CENTER;
				widening = buttonSize.getSize() / 4;
			break;
			case LARGE://48
			case BIG://64
			case HUGE://128
				correctedPosition = SWT.CENTER;
			break;
			default:// is NORMAL 32, same as MEDIUM 32
				correctedPosition = SWT.CENTER;
				widening = buttonSize.getSize() / 8;
			break;
		}
		Button btn = new Button( buttonBar, correctedPosition | SWT.PUSH );
		btn.setData(buttonType);
		if( DashboardImages.Images.isValid(buttonType.name() )){
			DashboardImages.Images dashboardImagesEnum = DashboardImages.Images.valueOf( buttonType.name() );
			Image image = DashboardImages.getImage( dashboardImagesEnum, buttonSize );
			int bSize = buttonSize.getSize();
			btn.setImage( image );
			GridData gridData = new GridData( bSize + widening, bSize );
			btn.setLayoutData( gridData );
			//btn.setSize( bSize, bSize );// <-- btn.setSize does not solve the wrong position for TINY
			//btn.computeSize( bSize, bSize, true );
		}
		else {
			btn.setText( buttonType.toString() );
			btn.setLayoutData( new GridData( SWT.RIGHT, SWT.TOP, true, true ) );
		}
		btn.getLayoutData().toString();
		btn.setEnabled( false );
		return btn;
	}

	private class DeleteViewerComparator extends ViewerComparator{
		private static final long serialVersionUID = 1L;
		private int selectedColumn;
		private int direction = SWT.DOWN;

		int getDirection(){
			return direction;
		}

		/**
		 * Set the column index, without toggling
		 * @param index
		 */
		private void setColumnIndex( int index, int direction ){
			this.selectedColumn = index;
			this.direction = direction;
		}

		void setColumn( int index ) {
			if( index == this.selectedColumn ) {
				// Same column as last sort; toggle the direction
				direction = ( direction == SWT.UP ) ? SWT.DOWN: SWT.UP;
			} else {
				// New column; do an ascending sort
				direction = SWT.DOWN;
				this.selectedColumn = index;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compare( Viewer vwer, Object e1, Object e2 ) {
			if( selectedColumn == deleteColumnIndex ){
				Collection<StoreWithDelete> items = (Collection<StoreWithDelete>)viewer.getInput();
				for( StoreWithDelete item: items )
					item.setDelete( direction == SWT.DOWN );
				return 0;
			}
			Comparator<T> comp = getColumnComparator( selectedColumn );
			StoreWithDelete swd1 = (StoreWithDelete)e1;
			StoreWithDelete swd2 = (StoreWithDelete)e2;
			int rc = ( comp == null ) ? super.compare( vwer, swd1.getText( selectedColumn ), swd2.getText( selectedColumn ) ) :
				comp.compare( swd1.getStore(), swd2.getStore() );
			return (direction == SWT.DOWN) ? rc: -rc ;
		}
	}

	protected class DeleteLabelProvider extends LabelProvider implements ITableLabelProvider{
		private static final long serialVersionUID = 1L;

		@Override
		public String getColumnText( Object element, int columnIndex ) {
			return ( columnIndex < deleteColumnIndex ) ? null : "";
		}

		@SuppressWarnings("unchecked")
		@Override
		public Image getColumnImage( Object arg0, int columnIndex ) {
			IStoreWithDelete<T> swd = (IStoreWithDelete<T>)arg0;
			Image image = null;
			if( deleteColumnIndex == columnIndex ){
				image = setCheckedButton( true, swd.isDelete() );
				Button button = buttons.get(Buttons.DELETE);
				if( button != null )
					button.setEnabled( deleteItems() );
				button = buttons.get(Buttons.DONTSHOW);
				if( button != null )
					button.setEnabled( deleteItems() );
			}
			return image;
		}

		/**
		 * Set the correct checkbox image for the given value. If enabled is false,
		 * the checkbox is grayed
		 * @param button
		 * @param enabled
		 * @param value
		 * @return
		 */
		protected Image setCheckedButton( boolean enabled, boolean value ){
			LabelProviderImages images = new LabelProviderImages();
			Image image = value ? images.getImage( Images.CHECKED, enabled ) : images.getImage( Images.UNCHECKED, enabled );
			return image;
		}

		@SuppressWarnings("unchecked")
		private boolean deleteItems(){
			Collection<StoreWithDelete> items = (Collection<AbstractTableViewerWithDelete<T>.StoreWithDelete>)viewer.getInput();
			for( IStoreWithDelete<T> item: items ){
				if( item.isDelete() )
					return true;
			}
			return false;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
	}

	protected class StoreWithDelete implements IStoreWithDelete<T>{

		private List<String> texts;
		private T data;
		private boolean delete;
		private int index, count;

		public StoreWithDelete( T data, int index, int count ) {
			super();
			this.data = data;
			this.delete = false;
			this.index = index;
			this.count = count;
			this.texts = new ArrayList<>();
		}

		/* (non-Javadoc)
		 * @see org.condast.commons.ui.widgets.IStoreWithDelete#getText(int)
		 */
		@Override
		public String getText( int index ) {
			if( this.texts.isEmpty() )
				return null;
			return texts.get( index );
		}

		/* (non-Javadoc)
		 * @see org.condast.commons.ui.widgets.IStoreWithDelete#addText(java.lang.String)
		 */
		@Override
		public void addText( String text ) {
			this.texts.add( text );
		}


		/* (non-Javadoc)
		 * @see org.condast.commons.ui.widgets.IStoreWithDelete#getIndex()
		 */
		@Override
		public int getIndex() {
			return index;
		}

		/* (non-Javadoc)
		 * @see org.condast.commons.ui.widgets.IStoreWithDelete#getCount()
		 */
		@Override
		public int getCount() {
			return count;
		}

		/* (non-Javadoc)
		 * @see org.condast.commons.ui.widgets.IStoreWithDelete#isDelete()
		 */
		@Override
		public boolean isDelete() {
			return delete;
		}

		public void setDelete( boolean choice ) {
			this.delete = choice;
		}

		/* (non-Javadoc)
		 * @see org.condast.commons.ui.widgets.IStoreWithDelete#getData()
		 */
		@Override
		public T getStore() {
			return data;
		}
	}

	private class DeleteCheckBoxEditor extends AbstractCheckBoxCellEditor<StoreWithDelete>{
		private static final long serialVersionUID = 1L;

		@Override
		protected void onToggle() {
			boolean value = super.getData().isDelete();
			super.getData().setDelete( !value );
			updateDeleteButton();
		}

		@Override
		protected Object doGetValue() {
			return super.getData().isDelete();
		}

		@Override
		protected void doSetValue( Object value ) {
			super.getData().setDelete( (boolean)value );
		}
	}
}