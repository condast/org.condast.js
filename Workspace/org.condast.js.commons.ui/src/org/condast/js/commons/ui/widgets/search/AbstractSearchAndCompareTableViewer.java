package org.condast.js.commons.ui.widgets.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.condast.commons.ui.widgets.table.AbstractViewerComparator;
import org.condast.js.commons.ui.widgets.search.ISearchSelectionListener.SearchEvents;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractSearchAndCompareTableViewer<T extends Object> extends Composite implements ISearchResultsComposite<T>{
	private static final long serialVersionUID = 1L;

	public static final String SQL_PERSONS_QUERY = "select ap from ApplicationPerson ap";
	protected static final String RWT_SEARCH = "search";

	public enum Fields{
		SEARCH,
		GO;
	}

	private List<TableViewer> viewers;
	private int nrofTables;
	private TableColumnLayout tableColumnLayout;

	private Combo selectionCombo;
	private Text text_keywords;
	private SearchViewerComparator comparator;

	/**
	 * Store the current query
	 */
	private String currentQuery;

	private Collection<ISearchSelectionListener<T>> listeners;

	protected AbstractSearchAndCompareTableViewer(Composite parent,int style ) {
		this( parent, style, 1);
	}

	protected AbstractSearchAndCompareTableViewer(Composite parent,int style, int nrofTables ) {
		super(parent,style);
		this.nrofTables = nrofTables;
		viewers = new ArrayList<>();
		createComposite( parent, style );
		listeners = new ArrayList<>();
	}

	/**
	 * Get the selection index;
	 * @return
	 */
	protected int getComboSelection(){
		return this.selectionCombo.getSelectionIndex();
	}

	/**
	 * The default behaviour for the combo box is to
	 * add the column names
	 * @return
	 */
	protected String[] getComboItems(){
		return getColumnNames();
	}

	public String getCurrentQuery() {
		return currentQuery;
	}

	protected String getKeyWords(){
		return this.text_keywords.getText();
	}

	/**
	 * Get the index of the given treeviewer in the list
	 * @param viewer
	 * @return
	 */
	protected List<TableViewer> getViewers(){
		return viewers;
	}
	/**
	 * Get the text that corresponds with the given field
	 * @param field
	 * @return
	 */
	protected abstract String getFieldText( Fields field );

	protected TableColumnLayout getTableColumnLayout() {
		return tableColumnLayout;
	}

	protected TableViewer getViewer( int index ) {
		return viewers.get( index );
	}

	/**
	 * Response to pressing the GO button for the viewer with the given index
	 * @param e
	 */
	protected abstract void onGoButtonPressed( SelectionEvent e );

	/**
	 * A list of column names
	 * @return
	 */
	protected abstract String[] getColumnNames();

	/**
	 * Respond to clicking a combo item
	 * @param selectionIndex
	 */
	protected abstract void onSearchCriteria( int selectionIndex, String keywords );

	protected void createComposite( Composite parent,int style ){
		setLayout(new GridLayout(this.nrofTables, false));

		Group gr = new Group(this,SWT.NONE);
		gr.setText( getFieldText( Fields.SEARCH ));
		gr.setLayout(new GridLayout(3,false));
		GridData gd_gr = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd_gr.horizontalSpan = this.nrofTables;
		gr.setLayoutData(gd_gr);
		gr.setData(RWT.CUSTOM_VARIANT,RWT_SEARCH);

		selectionCombo = new Combo(gr, SWT.NONE);
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 112;
		selectionCombo.setLayoutData(gd_combo);
		selectionCombo.setItems( getComboItems() );
		selectionCombo.select(0);
		selectionCombo.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				onSearchCriteria(selectionCombo.getSelectionIndex(), text_keywords.getText());
				refresh();
			}
		});

		text_keywords = new Text(gr, SWT.BORDER);
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		text_keywords.setLayoutData(gd_text_1);
		text_keywords.addModifyListener( new ModifyListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void modifyText(ModifyEvent event) {
				onSearchCriteria( selectionCombo.getSelectionIndex(), text_keywords.getText());
				refresh();
			}
		});

		final Button btnGo = new Button(gr, SWT.NONE);
		btnGo.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				onGoButtonPressed( e);
			}
		});
		btnGo.setText( getFieldText( Fields.GO ));

		tableColumnLayout = new TableColumnLayout();
		Composite composite;
		for( int i=0; i<nrofTables; i++ ){
			composite = new Composite(this, SWT.VERTICAL);
		    GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		    gd_table.widthHint = 100;
			composite.setLayoutData(gd_table);
			composite.setLayout( new GridLayout( 1, true ));

			Label label = new Label( composite, SWT.NONE );
			label.setData(RWT.CUSTOM_VARIANT,RWT_SEARCH);
			label.setText( this.setColumnText(i));
			label.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1 ));
			Composite tableComposite = new Composite( composite, SWT.NONE);
			tableComposite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1 ));
			tableComposite.setLayout(tableColumnLayout);
			viewers.add( createTableViewer( tableComposite, style, i ));
		}
	}

	protected abstract String setColumnText( int columnIndex );

	protected TableViewer createTableViewer( Composite parent, int style, int index ){
		int mask = SWT.BORDER | SWT.SINGLE | SWT.MULTI | SWT.FULL_SELECTION;
		final TableViewer viewer = new TableViewer( parent, mask & style );
		comparator = new SearchViewerComparator( index );
		viewer.setComparator( comparator );
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
	    table.setLinesVisible( true );


	    String[] names = getColumnNames();
	    int counter = 0;
	    for( String name: names ){
	    	createColumn( viewer, name, counter++ );
	    }
	    viewer.setUseHashlookup( true );
	    viewer.setContentProvider( ArrayContentProvider.getInstance());


	    //viewer.addFilter( viewerFilter );

	    viewer.addDoubleClickListener(new IDoubleClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public synchronized void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				T ap = (T) selection.getFirstElement();
				for( ISearchSelectionListener<T> listener: listeners )
					listener.notifySearchResults( new SearchEvent<>(event.getSource(), currentQuery, ap  ));
			}
		});
	    viewer.addSelectionChangedListener( new ISelectionChangedListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				T ap = (T) selection.getFirstElement();
				onTableRowSelected( (TableViewer) event.getSource(), ap);
			}
		});
	    return viewer;
	}

	/**
	 * Respond to selecting a table row
	 * @param viewer
	 * @param data
	 */
	protected abstract void onTableRowSelected( TableViewer viewer, T data );

	/**
	 * Set up the newly created column
	 * @param column
	 */
	protected abstract void setupColumn( TableColumn column, int index );

	private TableViewerColumn createColumn( TableViewer viewer, String columnName, int index ) {
		TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
		TableColumn tcolumn = result.getColumn();
		tcolumn.setText( columnName );
		tcolumn.setMoveable( true );
		this.setupColumn( tcolumn, index );
		tcolumn.addSelectionListener( getSelectionAdapter( viewer, tcolumn, index ));
		return result;
	}

	private SelectionAdapter getSelectionAdapter( final TableViewer viewer, final TableColumn column, final int index ) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn( index );
				int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	/* (non-Javadoc)
	 * @see org.condast.na.swt.widgets.ISearchResultsComposite#addSelectionListener(org.condast.commons.ui.search.ISearchSelectionListener)
	 */
	@Override
	public void addSelectionListener(ISearchSelectionListener<T> listener) {
		listeners.add( listener );
	}

	/* (non-Javadoc)
	 * @see org.condast.na.swt.widgets.ISearchResultsComposite#removeSelectionListener(org.condast.commons.ui.search.ISearchSelectionListener)
	 */
	@Override
	public void removeSelectionListener(ISearchSelectionListener<T> listener) {
		listeners.remove( listener );
	}

	protected void notifySearchListeners( SearchEvent<T> event ){
		for( ISearchSelectionListener<T> listener: this.listeners )
			listener.notifySearchResults(event);
	}

	/**
	 * convenience method to notify a default 'GO' button event
	 * @param type
	 */
	protected void notifySearchListeners( TableViewer viewer, SearchEvents type ){
		this.notifySearchListeners( new SearchEvent<T>( viewer, type, text_keywords.getText() ));
	}

	protected int getNrOfTables() {
		return nrofTables;
	}

	protected void refresh(){
		for( TableViewer viewer: getViewers() ){
			viewer.refresh();
		}
	}

	/**
	 * Create a comparator for the given column. return null if a
	 *  default is good enough.
	 *
	 * @param index
	 * @return
	 */
	protected abstract int compareTables( int tableIndex, int columnIndex, T o1 );

	protected class SearchViewerComparator extends AbstractViewerComparator<T>{
		private static final long serialVersionUID = 1L;

		private int tableIndex;

		public SearchViewerComparator( int tableIndex ) {
			super();
			this.tableIndex = tableIndex;
		}

		public int getTableIndex() {
			return tableIndex;
		}

		@Override
		protected int compareColumn(int columnIndex, T o1, T o2) {
			return compareTables( this.tableIndex, columnIndex, o2 );
		}
	}
}