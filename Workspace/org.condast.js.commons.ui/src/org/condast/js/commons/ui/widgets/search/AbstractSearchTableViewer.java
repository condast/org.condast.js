package org.condast.js.commons.ui.widgets.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import org.condast.commons.Utils;
import org.condast.js.commons.ui.widgets.search.ISearchSelectionListener.SearchEvents;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractSearchTableViewer<T extends Object> extends Composite implements ISearchResultsComposite<T>{
	private static final long serialVersionUID = 1L;

	public static final String SQL_PERSONS_QUERY = "select ap from ApplicationPerson ap";
	protected static final String RWT_SEARCH = "search";
	public static final int DEFAULT_PAGE_SIZE = 64;

	public enum Fields{
		SEARCH,
		GO,
		NUMBER,//text for itemCount
		TOTAL,//text for count
		NONE;
	}

	private Table table;
	private TableViewer viewer;
	private TableColumnLayout tableColumnLayout;
	private Text text_keywords;
	private SearchViewerComparator comparator;
	private int itemCount = 0;
	private Label sizeLabel;
	private Text sizeText;//Shows the amount afer a certain search.
	private Combo selectionCombo;
	private int count; // The counted numbers of items. This is the total amount.
	private int pageSize; //The amount of items held in a table
	private Group grp_searchbar;
	private String ttt; /// ttt is the ToolTipTekst
	private int matchComboSelectionIndex = 0;//this variable is a dummy for SearchProfiletableViewer!

	/**
	 * Store the current query
	 */
	//private String currentQuery;

	private Collection<ISearchSelectionListener<T>> listeners;

	protected AbstractSearchTableViewer( Composite parent, int style ) {
		this( parent, style, DEFAULT_PAGE_SIZE );
	}

	protected AbstractSearchTableViewer( Composite parent, int style, int pageSize ) {
		super( parent,style );
		this.pageSize = pageSize;
		createComposite( parent, style );
		listeners = new ArrayList<>();
	}

	protected void onAddSearchItemBefore( Group searchBar ) {
		/* DEFAULT NOTHING */
	}

	protected void createComposite( Composite parent, int style ){
		setLayout( new GridLayout( 1, false ) );

		grp_searchbar = new Group( this, SWT.NONE );
		//searchBar.setBackgroundImage( ProfileImages.getImage( ProfileImages.Images.BACKGROUND_HOR_BLUE ) );
		grp_searchbar.setText( getFieldText( Fields.SEARCH ) );
		grp_searchbar.setLayout( new GridLayout( 5, false ) );
		GridData gd_gr = new GridData( SWT.FILL, SWT.FILL, true, false );
		gd_gr.heightHint = 57;
		grp_searchbar.setLayoutData( gd_gr );
		grp_searchbar.setData( RWT.CUSTOM_VARIANT, RWT_SEARCH );

		//next line added for the MatchSearchTableViewer:
		this.onAddSearchItemBefore( grp_searchbar);

		selectionCombo = new Combo( grp_searchbar, SWT.SINGLE );
		GridData gd_combo = new GridData( SWT.FILL, SWT.LEFT, false, false, 1, 1 );
		gd_combo.widthHint = 200;
		selectionCombo.setLayoutData( gd_combo );
		selectionCombo.setItems( getComboItems() );
		selectionCombo.setVisibleItemCount( getComboItems().length );
		selectionCombo.select( 0 );
		selectionCombo.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected( SelectionEvent e ) {
				try {
					table.setToolTipText( null  );
					onSearchCriteria( matchComboSelectionIndex, selectionCombo.getSelectionIndex(), text_keywords.getText() );
					viewer.refresh();
					int i = viewer.getTable().getItemCount();
					sizeText.setText( String.valueOf( i ) );
					super.widgetSelected( e );
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}
		});

		text_keywords = new Text( grp_searchbar, SWT.BORDER );
		text_keywords.addModifyListener( new ModifyListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void modifyText( ModifyEvent event ) {
				try {
					table.setToolTipText( null );
					onSearchCriteria( matchComboSelectionIndex, selectionCombo.getSelectionIndex(), text_keywords.getText());
					viewer.refresh();
					int i = viewer.getTable().getItemCount();
					sizeText.setText( String.valueOf( i ) );
					//setCountedItems( i );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		GridData gd_text_1 = new GridData( SWT.FILL, SWT.FILL, true, false, 1, 1 );
		gd_text_1.widthHint = 267;
		text_keywords.setLayoutData( gd_text_1 );

		final Button btnGo = new Button( grp_searchbar, SWT.END );
		btnGo.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected( SelectionEvent e ) {
				try {
					table.setToolTipText( null );
					Collection<T> results = onGoButtonPressed( e );
					if( Utils.assertNull( results ) )
						return;
					//First update the results prior to viewing
					notifySearchListeners( new SearchEvent<T>( viewer, SearchEvents.PREPARE, (String)e.data ) );
					viewer.setInput( results );
					int i = viewer.getTable().getItemCount();
					sizeText.setText( String.valueOf( i ) );
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnGo.setText( getFieldText( Fields.GO ) );

		sizeLabel = new Label( grp_searchbar, SWT.END | SWT.COLOR_TRANSPARENT );
		//amountLabel.setBackground( Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND) );
		sizeLabel.setForeground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );
		sizeLabel.setText( getFieldText( Fields.NUMBER ) );

		sizeText = new Text( grp_searchbar, SWT.NULL | SWT.READ_ONLY );
		//sizeText.setBackground( sizeLabel.getBackground() );//show the backgroundcolor of sizeLabel for sizeText
		sizeText.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_TRANSPARENT ) );//show the backgroundpicture
		//sizeText.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_BLACK ) );//set a black backgroundcolour.
		sizeText.setForeground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );

		Composite tableComposite = new Composite( this, SWT.NONE|SWT.WRAP );
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout( tableColumnLayout );
	    GridData gd_table = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 );
	    gd_table.widthHint = 300;
		tableComposite.setLayoutData( gd_table );

		viewer = new TableViewer( tableComposite, SWT.VIRTUAL|SWT.BORDER|SWT.MULTI|SWT.FULL_SELECTION );
		comparator = new SearchViewerComparator();
		viewer.setComparator( comparator );
		table = viewer.getTable();
		table.setHeaderVisible( true );
	    table.setLinesVisible( true );
	    table.setItemCount ( this.count );
	    table.setToolTipText( this.ttt );

	    String[] names = getColumnNames();
	    viewer.setUseHashlookup( true );
	    viewer.setContentProvider( ArrayContentProvider.getInstance() );

	    count = this.calculateCount();//placed at this line, count is known and can be used in method createColumn
	    int counter = 0;
	    for( String name: names ){
	    	createColumn( name, counter++ );
	    }
	    //viewer.addSelectionChangedListener( listener )
	    viewer.addDoubleClickListener( new IDoubleClickListener() {

	    	@SuppressWarnings("unchecked")
	    	@Override
	    	public synchronized void doubleClick( DoubleClickEvent event ) {
	    		try {
	    			table.setToolTipText( null );
	    			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
	    			T data = (T)selection.getFirstElement();
	    			for( ISearchSelectionListener<T> listener: listeners )
	    				listener.notifySearchResults( new SearchEvent<>( this, SearchEvents.RESULTS, data  ) );
	    		}
	    		catch( Exception ex ){
	    			ex.printStackTrace();
	    		}
	    	}
	    });
	    table.addSelectionListener( new SelectionAdapter() {
	    	private static final long serialVersionUID = 1L;

	    	@Override
	    	public void widgetSelected( SelectionEvent e ) {
	    		try {
	    			Object obj = e.item.getData();//The object is of type Location (=host) in case of a host, or Profile in case of a guest
	    			//viewer.refresh();
	    			changeToolTipText( obj );
	    		}
	    		catch( Exception ex ) {
	    			ex.printStackTrace();
	    		}
	    	}
	    });
/*	    table.addMouseListener( new MouseListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
				table.setToolTipText( "" );
			}});
*/
	}

	/**
	 * Get the text that corresponds with the given field
	 * @param field
	 * @return
	 */
	protected abstract String getFieldText( Fields field );

	public int getPageSize() {
		return pageSize;
	}

	protected TableColumnLayout getTableColumnLayout() {
		return tableColumnLayout;
	}

	protected TableViewer getViewer() {
		return viewer;
	}

	/**
	 * Get the selection index;
	 * @return
	 */
	protected int getComboSelectionIndex(){
		return this.selectionCombo.getSelectionIndex();
	}

	/**
	 * Respond to clicking a combo item or keyword text box
	 * @param selectionIndex
	 */
	protected abstract Collection<T> onUpdateQuery( int startIndexs, int pageCount );

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> getInput(){
		return (Collection<T>)this.viewer.getInput();
	}

	protected void setInput( Collection<T> input ){
		this.viewer.setInput( input );
	}

	/**
	 * Response to pressing the GO button
	 * @param e
	 */
	protected abstract Collection<T> onGoButtonPressed( SelectionEvent e );

	/**
	 * A list of column names
	 * @return
	 */
	protected abstract String[] getColumnNames();

	/**
	 * The default behaviour for the combo box is to
	 * add the column names
	 * @return
	 */
	protected String[] getComboItems(){
		return getColumnNames();
	}

	//public String getCurrentQuery() {
	//	return currentQuery;
	//}

	protected String getKeyWords(){
		return this.text_keywords.getText();
	}

	/**
	 * Respond to clicking a combo item or keyword text box
	 * @param selectionIndex
	 * @param matchComboSelectionIndex only used in MatchSearchTableViewer and dummy for SearchProfileTableViewer
	 */
	protected abstract void onSearchCriteria( int matchComboSelectionIndex, int selectionIndex, String keywords );

	/**
	 * Set the initial sort column (default 0)
	 * @return
	 */
	protected void setInitialSortColumn( int index ){
		comparator.setColumn( index );
	}

	protected Group getSearchBar() {
		return grp_searchbar;
	}

	/**
	 * Calculate the count value of the table. This is the maximum amount of
	 * items that the table can hold;
	 * @See: https://eclipse.org/articles/Article-SWT-Virtual/Virtual-in-SWT.html
	 * @return
	 */
	protected abstract int calculateCount();

	/**
	 * Set up the newly created column
	 * @param column
	 */
	protected abstract void setupColumn( TableColumn column, int index );

	private TableViewerColumn createColumn( String columnName, int index ) {
		TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
		TableColumn tcolumn = result.getColumn();
		if( index == 0) {
			tcolumn.setText( getExtraColumnText() + this.count + "\n" + columnName );
		}
		else {
			tcolumn.setText( columnName );
		}
		tcolumn.setMoveable( true );
		this.setupColumn( tcolumn, index );
		tcolumn.addSelectionListener( getSelectionAdapter( tcolumn, index ) );
		return result;
	}

	private SelectionAdapter getSelectionAdapter( final TableColumn column, final int index ) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected( SelectionEvent e ) {
				comparator.setColumn( index );
				int dir = comparator.getDirection();
				viewer.getTable().setSortDirection( dir );
				viewer.getTable().setSortColumn( column );
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	/* (non-Javadoc)
	 * @see org.condast.na.swt.widgets.ISearchResultsComposite#addSelectionListener(org.condast.commons.ui.search.ISearchSelectionListener)
	 */
	@Override
	public void addSelectionListener( ISearchSelectionListener<T> listener ) {
		listeners.add( listener );
	}

	/* (non-Javadoc)
	 * @see org.condast.na.swt.widgets.ISearchResultsComposite#removeSelectionListener(org.condast.commons.ui.search.ISearchSelectionListener)
	 */
	@Override
	public void removeSelectionListener( ISearchSelectionListener<T> listener ) {
		listeners.remove( listener );
	}

	protected void notifySearchListeners( SearchEvent<T> event ){
		for( ISearchSelectionListener<T> listener: this.listeners )
			listener.notifySearchResults( event );
	}

	/**
	 * convenience method
	 * @param type
	 */
	protected void notifySearchListeners( SearchEvents type ){
		this.notifySearchListeners( new SearchEvent<T>( viewer, type, text_keywords.getText() ) );
	}

	/**
	 * Create a comaparotr for the given column. return null if a
	 *  default is good enough.
	 *
	 * @param index
	 * @return
	 */
	protected Comparator<T> getColumnComparator( int index ){
		return null;
	}

	/*
	 * refresh the viewers
	 */
	protected void refresh(){
		if( getDisplay() == null || getDisplay().isDisposed() )
			return;
		getDisplay().asyncExec( new Runnable(){

			@Override
			public void run() {
				getViewer().refresh();
			}

		});
	}

	private class SearchViewerComparator extends ViewerComparator{
		private static final long serialVersionUID = 1L;
		private int selectedColumn;
		private int direction = SWT.DOWN;

		int getDirection(){
			return direction;
		}

		void setColumn( int index ) {
			if( index == this.selectedColumn ) {
				// Same column as last sort; toggle the direction
				direction = ( direction == SWT.UP )? SWT.DOWN: SWT.UP;
			} else {
				// New column; do an ascending sort
				direction = SWT.DOWN;
				this.selectedColumn = index;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compare( Viewer vwer, Object e1, Object e2 ) {
			Comparator<T> comp = getColumnComparator( selectedColumn );
			int rc = ( comp == null )? super.compare( vwer, e1, e2 ):
				comp.compare( (T)e1 , (T)e2 );
			return ( direction == SWT.DOWN )? rc: -rc ;
		}
	}

	public void changeToolTipText( Object obj ) {
		this.ttt = this.getAllMatchesAsString( obj );
		table.setToolTipText( this.ttt );
	}

	/**
	 * provide the matchdates as tooltiptext
	 * @param obj, the object that is selected
	 * @return String tooltiptext
	 */
	protected abstract String getAllMatchesAsString( Object obj );

	/**
	 * provide extra column text
	 * @return String extraText
	 */
	protected abstract String getExtraColumnText();

	protected void setCountedItems( int i ) {
		this.itemCount = i;
	}

	protected int getItemCounted() {
		return this.itemCount;
	}

	/**
	 * Give sizeText to MatchSearchTableViewer, showing a Textfield that shows the number of rows
	 * @return Text sizeText
	 */
	protected Text getSizeText() {
		return this.sizeText;
	}

	//protected Combo getSelectionCombo() {
	//	return this.selectionCombo;
	//}

	protected Text getTextKeyWords() {
		return this.text_keywords;
	}

	protected int getMatchComboSelectionIndex() {
		return matchComboSelectionIndex;
	}

	protected void setMatchComboSelectionIndex( int matchComboSelectionIndex ) {
		this.matchComboSelectionIndex = matchComboSelectionIndex;
	}

}