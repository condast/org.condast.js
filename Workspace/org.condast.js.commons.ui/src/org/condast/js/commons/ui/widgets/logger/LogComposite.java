package org.condast.js.commons.ui.widgets.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.condast.commons.Utils;
import org.condast.commons.log.AbstractLogHandler;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.activate.ActivationEvent;
import org.condast.commons.ui.activate.IActivateListener;
import org.condast.commons.ui.comparator.AbstractToggleViewerComparator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class LogComposite extends Composite{
	private static final long serialVersionUID = 1L;

	private enum Columns{
		LOG,
		LEVEL,
		TIME_STAMP;

		public static int getWidht( Columns column ){
			switch( column ){
			case LEVEL:
			case TIME_STAMP:
				return 10;
			default:
				return 90;
			}
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( this.name());
		}
	}

	private TableViewer viewer;
	private TableColumnLayout tableColumnLayout;

	private LogHandler handler;
	private Collection<Map.Entry<Date,String>> input;

	private Button btnActivate;
	private Button btnClear;
	private Button btnCondense;
	
	private List<String> filters;

	private Collection<IActivateListener> listeners;
	private boolean disposed = false;

	private LogViewerComparator comparator;

	private FocusListener flistener = new FocusListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void focusGained( FocusEvent event ) {
			notifyActivateChange(true);
		}

		@Override
		public void focusLost(FocusEvent event) {
			notifyActivateChange(false);
		}
	};

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LogComposite(Composite parent, int style) {
		this( parent, style, Level.FINE );
	}

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LogComposite(Composite parent, int style, Level level ) {
		super(parent, style);
		this.filters = new ArrayList<>();
		this.disposed = false;
		this.handler = new LogHandler(level);
		this.listeners = new ArrayList<>();
		setLayout(new GridLayout(3, false));

		this.btnActivate = new Button(this, SWT.CHECK);
		this.btnActivate.setText("Activate");
		this.btnActivate.setSelection(false);
		this.btnActivate.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Button button = (Button) e.widget;
					notifyActivateChange( button.getSelection() );
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		btnCondense = new Button(this, SWT.CHECK);
		btnCondense.setText("Condense");
		btnCondense.setSelection(true);
		this.btnCondense.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					input.clear();
					selectCollection();
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		this.btnClear = new Button(this, SWT.PUSH);
		this.btnClear.setText("Clear");
		this.btnClear.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					clear();
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		Composite tableComposite = new Composite(this, SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL);
		gd_table.horizontalSpan = 3;
		gd_table.verticalAlignment = SWT.FILL;
		gd_table.grabExcessVerticalSpace = true;
		gd_table.grabExcessHorizontalSpace = true;
		gd_table.horizontalAlignment = SWT.FILL;
		tableComposite.setLayoutData(gd_table);

		viewer = new TableViewer(tableComposite, SWT.VIRTUAL|SWT.BORDER|SWT.MULTI|SWT.FULL_SELECTION);
		viewer.setUseHashlookup( true );
		viewer.setContentProvider( ArrayContentProvider.getInstance());
		viewer.setLabelProvider( new LogLabelProvider());

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible( true );
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		int counter = 0;
		for( Columns column: Columns.values() ){
			createColumn( column, counter++ );
		}
		comparator = new LogViewerComparator();
		viewer.setComparator( comparator);
		viewer.setLabelProvider( new LogLabelProvider());
		selectCollection();
		this.addFocusListener(flistener);
	}

	public void clear() {
			input.clear();
			viewer.setInput(input);
	}
	
	/**
	 * add a filter and return the index for which this applies
	 * @param filter
	 * @return
	 */
	public void clearFilter( ) {
		this.filters.clear();
	}

	/**
	 * add a filter and return the index for which this applies
	 * @param filter
	 * @return
	 */
	public int addFilter( String filter ) {
		this.filters.add(filter);
		return this.filters.size()-1;
	}
	
	private boolean accept( String message ) {
		if( Utils.assertNull(this.filters ))
			return true;
		String[] split = message.split(":");
		if( split.length == 1)
			return true;
		for( int i=0; i<split.length; i++ ) {
			if( this.filters.size() <= i)
				break;
			if( !split[i].equals(this.filters.get(i)))
				return false;
		}
		return true;
	}
	
	private Collection<Map.Entry<Date, String>> selectCollection(){
		if( btnCondense.getSelection())
			input = new TreeSet<>();
		else
			input = new LinkedList<>();
		return input;
	}

	private TableViewerColumn createColumn( Columns columnName, int index ) {
		TableViewerColumn result = new TableViewerColumn( viewer, SWT.V_SCROLL );
		TableColumn tcolumn = result.getColumn();
		tcolumn.setText( columnName.toString() );
		tcolumn.setMoveable( true );
		tcolumn.addSelectionListener( getSelectionAdapter( tcolumn, index ));
		tableColumnLayout.setColumnData(tcolumn, new ColumnWeightData( Columns.getWidht(columnName), 200, true));
		return result;
	}

	public void activate( boolean choice ) {
		this.btnActivate.setSelection(choice);
		notifyActivateChange(choice);
	}

	public boolean isActivated() {
		return this.btnActivate.getSelection();
	}

	public void addActivateListener( IActivateListener listener) {
		this.listeners.add( listener );
	}

	public void removeActivateListener( IActivateListener listener) {
		this.listeners.remove( listener );
	}

	protected void notifyActivateChange( boolean activate ) {
		for( IActivateListener listener: this.listeners )
			listener.notifyActivationChange( new ActivationEvent(this, activate));
	}

	public void appendInput( Level level, String log ) {
		if(( Display.getDefault() == null ) || Display.getDefault().isDisposed())
			return;
		try {
			Entry entry = new Entry( Calendar.getInstance().getTime(), log, level);
			if( btnCondense.getSelection()) {
				input.remove(entry);
				if( accept( log ))
					input.add(entry);
			}else {
				LinkedList<Map.Entry<Date,String>> list = (LinkedList<java.util.Map.Entry<Date, String>>) input;
				list.addFirst( entry);
			}
			Display.getDefault().asyncExec( new Runnable() {

				@Override
				public void run() {

					btnClear.setEnabled( !input.isEmpty());
					viewer.setInput(input);
				}
			});
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column,
			final int index ) {
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

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void dispose() {
		this.handler.close();
		this.removeFocusListener(flistener);
		notifyActivateChange(false);
		this.disposed = true;
		super.dispose();
	}

	private class LogLabelProvider extends LabelProvider implements ITableLabelProvider{
		private static final long serialVersionUID = 1L;

		@Override
		public String getColumnText( Object element, int columnIndex ) {
			String str = super.getText(element);
			Entry entry = (Entry) element;
			Columns column = Columns.values()[ columnIndex ];

			switch( column ) {
			case LEVEL:
				str = entry.getLevel().getName();
				break;
			case TIME_STAMP:
				DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				str = formatter.format(entry.getKey());
				break;
			default:
				str = entry.getValue();
				break;
			}
			return str;
		}


		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	private class Entry implements Map.Entry<Date, String>, Comparable<Entry>{

		private Date key;
		private String value;
		private Level level;

		private Entry(Date key, String value, Level level) {
			super();
			this.key = key;
			this.value = value;
			this.level = level;
		}

		@Override
		public Date getKey() {
			return key;
		}

		@Override
		public String getValue() {
			return value;
		}

		public Level getLevel() {
			return level;
		}

		@Override
		public String setValue(String value) {
			return this.value = value;
		}

		@Override
		public int compareTo(Entry o) {
			if(( o == null ) || ( StringUtils.isEmpty( o.value )))
				return -1;
			if( StringUtils.isEmpty(value))
				return 1;
			
			String[] split1 = this.value.split("[:]");
			String[] split2 = o.value.split("[:]");
			int minLength = (split1.length < split2.length)?split1.length: split2.length;
			int cmpLength = (minLength < 2 )?minLength: 2;
			for( int i=0; i<cmpLength; i++ ) {
				int compare = split1[i].compareTo(split2[i]);
				if( compare != 0 )
					return compare;
			}
			return 0;
		}
	}

	private class LogHandler extends AbstractLogHandler{

		protected LogHandler(Level entryLevel) {
			super(entryLevel);
		}

		@Override
		protected void onPublish( final LogRecord arg0) {
			if( disposed || getDisplay().isDisposed() )
				return;
			getDisplay().asyncExec( new Runnable() {

				@Override
				public void run() {
					appendInput( arg0.getLevel(), arg0.getMessage());
				}
			});
		}

		@Override
		public void close() throws SecurityException {
			// NOTHING
		}
	}

	private class LogViewerComparator extends AbstractToggleViewerComparator<Entry>{
		private static final long serialVersionUID = 1L;

		@Override
		protected Comparator<Entry> getColumnComparator(final int columnIndex) {
			Comparator<Entry> comparator = new Comparator<Entry>(){

				@Override
				public int compare(Entry o1, Entry o2) {
					Columns col = Columns.values()[columnIndex];
					int result = 0;
					switch( col ) {
					case LEVEL:
						result = o1.getLevel().intValue() - o2.getLevel().intValue();
						break;
					case TIME_STAMP:
						result = o1.getKey().compareTo(o2.getKey());
						break;
					default:
						result = o1.getValue().compareTo( o2.getValue() );
						break;
					}
					return result;
				}
			};
			return comparator;
		}
	}
}