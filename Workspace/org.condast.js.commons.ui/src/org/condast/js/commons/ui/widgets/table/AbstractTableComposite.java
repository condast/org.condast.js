package org.condast.js.commons.ui.widgets.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.condast.commons.ui.widgets.table.AbstractViewerComparator;
import org.condast.commons.ui.widgets.table.ITableEventListener;
import org.condast.commons.ui.widgets.table.ITableEventListener.TableEvents;
import org.condast.commons.ui.widgets.table.TableEvent;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class AbstractTableComposite<D extends Object> extends Composite
{
	private static final long serialVersionUID = 976428552549736382L;

	protected static final String S_INDEX = "INDEX";
	private Table table;

	private TableViewer tableViewer;
	private TableColumnLayout tclayout;
	private List<ColumnLabelProvider> columnLabelProviders;
	private SearchViewerComparator comparator;

	private D input;

	private String title;

	private Collection<ITableEventListener<D>> listeners;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	protected AbstractTableComposite( Composite parent, int style) {
		this( parent, true, style );
	}

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	protected AbstractTableComposite( Composite parent, boolean init, int style)
	{
		super(parent, style);
		this.listeners = new ArrayList<>();
		this.columnLabelProviders = new ArrayList<>();
		this.prepare();
		this.createComposite(parent, style );
		if( init )
			this.initComposite();
	}

	public void addTableEventListener( ITableEventListener<D> listener ){
		this.listeners.add( listener );
	}

	public void removeTableEventListener( ITableEventListener<D> listener ){
		this.listeners.remove( listener );
	}

	protected void notifyTableEvent( TableEvent<D> event ){
		for( ITableEventListener<D> listener: this.listeners ){
			listener.notifyTableEvent(event);
		}
	}

	/**
	 * Prepare the composite
	 */
	protected abstract void prepare();

	/**
	 * Create the composite
	 * @param parent
	 */
	protected void createComposite( Composite parent, int style ){
		tableViewer = new TableViewer( this, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		table.setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( 22 ));
		tableViewer.addSelectionChangedListener( e-> onRowSelected( e ));
		tableViewer.addDoubleClickListener( e-> onRowDoubleClicked( e ));
		tableViewer.setComparator( comparator );
		tclayout = new TableColumnLayout();
		this.setLayout( tclayout );
		comparator = new SearchViewerComparator();
	}

	protected void setContentProvider( IContentProvider provider ) {
		this.tableViewer.setContentProvider(provider);
	}

	/**
	 * Initialise the component
	 */
	protected void initComposite(){
		this.createColumns( this, tableViewer);
		this.initTableColumnLayout(tclayout);
	}

	protected void addColumnLabelProvider( ColumnLabelProvider labelProvider ){
		this.columnLabelProviders.add( labelProvider );
	}

	protected void removeColumnLabelProvider( ColumnLabelProvider labelProvider ){
		this.columnLabelProviders.remove( labelProvider );
	}

	protected void setViewerComparator( ViewerComparator comparator ) {
		tableViewer.setComparator(comparator);
	}

	/**
	 * Get the column label provider for the column at the given index
	 * @param index
	 * @return
	 */
	public ColumnLabelProvider getColumnProvider( int index ) {
		return this.columnLabelProviders.get(index );
	}

	@Override
	public void dispose() {
		this.columnLabelProviders.clear();
		super.dispose();
	}

	public void refresh(){
		if( this.tableViewer.getTable().isDisposed() )
			return;
		this.tableViewer.refresh();
	}

	public void refresh( Object item, boolean choice ){
		if( this.tableViewer.getTable().isDisposed() )
			return;
		this.tableViewer.refresh( item, choice );
	}

	/**
	 * @return the title
	 */
	public final String getTitle(){
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public final void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * initialise the table column layout
	 * @param tclayout
	 */
	protected abstract void initTableColumnLayout( TableColumnLayout tclayout );

	/**
	 * @return the tableViewer
	 */
	protected final TableViewer getTableViewer()
	{
		return tableViewer;
	}

	@SuppressWarnings("unchecked")
	protected Collection<D> getSelected(){
		IStructuredSelection selection = (IStructuredSelection) this.tableViewer.getSelection();
		Iterator<?> iterator = selection.iterator();
		Collection<D> results=  new ArrayList<>();
		while( iterator.hasNext() ) {
			results.add( (D) iterator.next());
		}
		return results;
	}

	public void setSelection( int index ) {
		this.table.setSelection(index);
	}

	public int getSelectionCount() {
		return this.table.getSelectionCount();
	}

	protected SelectionAdapter getSelectionAdapter( final TableColumn column,
			final int index ) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected( SelectionEvent e ) {
				try {
					comparator.setColumn( index );
					int dir = comparator.getDirection();
					tableViewer.getTable().setSortDirection( dir );
					tableViewer.getTable().setSortColumn( column );
					tableViewer.refresh();
				} catch( Exception e1 ) {
					e1.printStackTrace();
				}
			}
		};
		return selectionAdapter;
	}

	protected TableColumnLayout getTableColumnLayout(){
		return this.tclayout;
	}

	/**
	 * The leaf that is currently set, or null if nothing has been added yet
	 * @return
	 */
	protected D getInput() {
		return input;
	}

	/**
	 * Prepare the input prior to setting it
	 * @param leaf
	 */
	protected abstract void onSetInput( D[] leaf );

	/**
	 * Set the input
	 * @param leaf
	 */
	public void setInput( D[] input ){
		onSetInput(input);
		this.tableViewer.setInput(input);
		this.table.setSelection(0);
	}

	/**
	 * Register a column to the table
	 * @param columnName
	 * @param className
	 * @param bound
	 * @return
	 */
	protected TableViewerColumn registerColum( String className, int style, int weight, int index ){
		final TableViewerColumn viewerColumn = new TableViewerColumn( tableViewer, style);
		final TableColumn column = viewerColumn.getColumn();
		column.setWidth(weight);
		column.setResizable(true);
		column.setMoveable(true);
		column.setData( RWT.CUSTOM_VARIANT, className );
		column.setData( S_INDEX, index );
		column.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;
			@Override
			public void widgetSelected(SelectionEvent e){
				onHeaderClicked( e );
			}
		});
		ColumnLabelProvider provider = null;
		if( index < this.columnLabelProviders.size() )
			provider = this.columnLabelProviders.get(index);
		if( provider != null )
			viewerColumn.setLabelProvider( provider);
		return viewerColumn;
	}

	/**
	 * Response to clicking a header
	 * @param e
	 */
	protected abstract void onHeaderClicked( SelectionEvent e );

	/**
	 * Prepare the composite
	 */
	@SuppressWarnings("unchecked")
	protected void onRowSelected( SelectionChangedEvent e ) {
		try {
			IStructuredSelection selection = (IStructuredSelection)e.getSelection();
			D entry = (D) selection.getFirstElement();
			notifyTableEvent( new TableEvent<>( this, TableEvents.SELECT, entry));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Prepare the composite
	 */
	@SuppressWarnings("unchecked")
	protected void onRowDoubleClicked( DoubleClickEvent e ) {
		try {
			IStructuredSelection selection = (IStructuredSelection)e.getSelection();
			D entry = (D) selection.getFirstElement();
			notifyTableEvent( new TableEvent<>( this, TableEvents.DOUBLE_CLICK, entry));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected abstract int compareTables( int columnIndex, D o1, D o2);

	// This will create the columns for the table
	protected abstract void createColumns(final Composite parent, final TableViewer viewer);

	protected class SearchViewerComparator extends AbstractViewerComparator<D>{
		private static final long serialVersionUID = 1L;

		public SearchViewerComparator() {
			super();
		}

		@Override
		protected int compareColumn(int columnIndex, D o1, D o2) {
			return compareTables( columnIndex, o1, o2 );
		}
	}
}
