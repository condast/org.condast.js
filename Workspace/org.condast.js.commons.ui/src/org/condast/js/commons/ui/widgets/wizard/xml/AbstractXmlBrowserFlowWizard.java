package org.condast.js.commons.ui.widgets.wizard.xml;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.condast.commons.flow.AbstractFlowControl;
import org.condast.commons.flow.IFlowControl;
import org.condast.commons.i18n.Language;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.wizard.AbstractFlowControlWizard;
import org.condast.commons.ui.wizard.AbstractFlowWizardPage;
import org.condast.commons.ui.wizard.ButtonEvent;
import org.condast.commons.ui.wizard.IButtonSelectionListener;
import org.condast.commons.ui.wizard.IButtonWizardContainer;
import org.condast.commons.ui.wizard.IButtonWizardContainer.Buttons;
import org.condast.commons.ui.wizard.IHeadlessWizardContainer;
import org.condast.commons.ui.wizard.IHeadlessWizardContainer.ContainerTypes;
import org.condast.js.commons.ui.widgets.wizard.IWizardBuilder;
import org.condast.js.commons.ui.widgets.wizard.WizardContainer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractXmlBrowserFlowWizard<T extends Object> extends AbstractFlowControlWizard<T> {

	private IWizard wizard;
	private boolean previousNext;
	private FlowControl flow;

	private Map<String, IndexStore> store;
	private IndexStore current;
	private Class<?> clss;

	private String titleStyle;
	private String buttonbarStyle;

	private Browser browser;

	private IButtonSelectionListener<T> listener = new IButtonSelectionListener<T>() {

		@Override
		public void notifyButtonPressed(ButtonEvent<T> event) {
			injectButtonEvent( new ButtonEvent<T>( this, event.getButton() ));
		}
	};

	protected AbstractXmlBrowserFlowWizard( InputStream in, Class<?> clss, Language language ) {
		this.wizard = this;
		this.clss = clss;
		this.previousNext = true;
		store = new HashMap<>();
		createWizard(in);
	}

	protected abstract IWizardBuilder<T> createBuilder( InputStream in );

	/**
	 * Create the wizard from the provided XML file
	 * @param in
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected IFlowControl createWizard( InputStream in ){
		IWizardBuilder<T> builder = createBuilder(in);
		try{
			builder.build( (IXmlFlowWizard<T>) this);
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return null;
		}
		flow = new FlowControl();
		super.setFlow(flow);
		return flow;
	}


	@Override
	public void createPageControls(Composite parent) {
		browser = new Browser( parent, SWT.NONE );
		super.createPageControls(browser);
		super.getFlowControl().init();
	}

	@Override
	protected IHeadlessWizardContainer onSelectContainer(int index) {
		WizardContainer wc = (WizardContainer) super.getContainer();
		IndexStore is = getIndexStore(index);
		String ts = ( is.titleStyle == null )? this.titleStyle: is.titleStyle;
		setBarStyle( wc.getToolBar(), index, ts );
		String bs = ( is.buttonStyle == null )? this.buttonbarStyle: is.buttonStyle;
		setBarStyle( wc.getButtonBar(), index, bs );
		for( Buttons button: is.getButtons()){
			wc.addButton(button, is.isButtonEnabled(button));
		}
		return wc;
	}

	private void setBarStyle( Composite bar, int index, String style ){
		if( StringUtils.isEmpty( style ))
			return;
		bar.setData(RWT.CUSTOM_VARIANT, style );
	}

	@Override
	public void setWindowTitle(String title) {
		super.setWindowTitle(title);
	}

	/**
	 * Get the store for the given index;
	 * @param index
	 * @return
	 */
	protected IndexStore getIndexStore( int index ){
		for( IndexStore is: store.values() ){
			if( is.getIndex() == index )
				return is;
		}
		return null;
	}


	@Override
	protected void initialiseContainer(IHeadlessWizardContainer cont) {
		WizardContainer container = (WizardContainer) cont;
		container.setPreviousnext( this.previousNext );
		IndexStore is = getIndexStore( 0 );
		container.setButtons( is.getButtons() );
		for( Buttons button: is.getButtons() ){
			container.buttonVisible( button, is.isButtonVisible(button));
			container.setButtonEnabled(button, is.isButtonEnabled(button));
		}
		container.addListener(listener);
	}

	void setPreviousNext(boolean previousNext) {
		this.previousNext = previousNext;
	}

	@Override
	public void addPages() {
		Collection<IndexStore> sorted = new TreeSet<>( new IndexStoreComparator() );
		sorted.addAll( store.values() );
		for( IndexStore is: sorted ){
			super.addPage( is.pageName, is.description, is.message );
		}
	}

	/**
	 * Add a page to the the wizard
	 * @param pageName
	 * @param description
	 * @param message
	 * @param type
	 */
	IndexStore addPage(String pageName, String description, String message, ContainerTypes type, boolean onCancel, boolean onFinish ) {
		current = new IndexStore( pageName, description, message, type, this.store.size() );
		if( onCancel ){
			for( IndexStore is: store.values() )
				is.onCancel = false;
			current.onCancel = true;
		}
		if( onFinish ){
			for( IndexStore is: store.values() )
				is.onFinish = false;
			current.onFinish = true;
		}
		store.put( pageName, current );
		return current;
	}

	IndexStore getCurrent(){
		return current;
	}

	void setTitleStyle(String titleStyle) {
		this.titleStyle = titleStyle;
	}

	void setButtonbarStyle(String buttonbarStyle) {
		this.buttonbarStyle = buttonbarStyle;
	}

	/**
	 * Update the buttons by actively enabling or disabling the
	 * appropriate ones
	 * @param fromPrevious
	 */
	@Override
	protected IWizardPage updatePage(int index) {
		WizardContainer container = (WizardContainer) getContainer();

		IndexStore is = getIndexStore(index);
		String ts = ( is.titleStyle == null )? this.titleStyle: is.titleStyle;
		setBarStyle( container.getToolBar(), index, ts );
		String bs = ( is.buttonStyle == null )? this.buttonbarStyle: is.buttonStyle;
		container.setButtons( is.getButtons() );
		setBarStyle( container.getButtonBar(), index, bs );

		for( Buttons button: is.getButtons() ){
			container.setButtonEnabled(button, is.isButtonEnabled(button));
		}

		boolean choice = ( index > 0 );

		if( this.needsPreviousAndNextButtons() ){
			container.setButtonEnabled( IButtonWizardContainer.Buttons.PREVIOUS, choice);
			choice = ( index < container.size() - 1 );
			container.setButtonEnabled( IButtonWizardContainer.Buttons.NEXT, choice);
		}
		choice = ( index >= container.getFinishIndex() );
		container.setButtonEnabled( IButtonWizardContainer.Buttons.FINISH, choice);
		container.updateButtons();
		for( Buttons button: is.getButtons() ){
			container.buttonVisible( button, is.isButtonVisible(button));
		}

		return super.updatePage(index);
	}

	/**
	 * Allow overriding the default behaviour of the buttons
	 * @param event
	 */
	protected void injectButtonEvent( ButtonEvent<T> event ){
		switch( event.getButton() ){
		case CONTINUE:
			selectContainer( getFlowControl().getIndex());
			updatePage( getFlowControl().getIndex() );
			notifyListeners(event);
			break;
		case NEXT:
			getNextPage( super.getCurrentPage() );
			break;
		case PREVIOUS:
			getPreviousPage( super.getCurrentPage() );
			break;
		case CANCEL:
			performCancel();
			break;
		case FINISH:
			performFinish();
			break;
		case SAVE:
			performSave( super.getCurrentPage() );
			break;
		default:
			break;
		}
	}

	@Override
	public boolean isHelpAvailable() {
		if(!( getContainer() instanceof WizardContainer ))
			return false;
		WizardContainer container = (WizardContainer) getContainer();
		return container.isHelpAvailable();
	}

	public void performSave( IWizardPage arg0 ){
		synchronizeData( arg0 );
		if( getContainer() instanceof WizardContainer ){
			WizardContainer container = (WizardContainer) getContainer();
			container.setButtonEnabled( IButtonWizardContainer.Buttons.SAVE, false );
		}
		notifyListeners( new ButtonEvent<T>( wizard, IButtonWizardContainer.Buttons.SAVE ));
	}

	@Override
	public boolean performCancel() {
		for( IndexStore is: store.values() ){
			if( is.onCancel ){
				flow.setIndex( is.getIndex());
				selectContainer( is.index );
				updatePage( is.index );
				break;
			}
		}
		//this.clear();
		this.notifyListeners( new ButtonEvent<T>( this, IButtonWizardContainer.Buttons.CANCEL ));
		return true;
	}

	@Override
	public boolean performFinish() {
		for( IndexStore is: store.values() ){
			if( is.onFinish ){
				flow.setIndex( is.getIndex());
				selectContainer( is.index );
				updatePage( is.index );
				break;
			}
		}
		this.notifyListeners( new ButtonEvent<T>( this, IButtonWizardContainer.Buttons.FINISH ));
		return true;
	}


	@Override
	public void dispose() {
		IButtonWizardContainer container = (IButtonWizardContainer) getContainer();
		container.removeListener(listener);
		super.dispose();
	}

	/**
	 * Default flow wizard page
	 * @author Kees
	 *
	 */
	protected abstract class FlowWizardPage extends AbstractFlowWizardPage{
		private static final long serialVersionUID = 1L;

		public FlowWizardPage( String pageName, String description, String message ) {
			super(wizard, flow, pageName, description, message);
		}

		@Override
		public IWizardContainer selectContainer(IWizardContainer currentContainer) {
			return currentContainer;
		}

		protected abstract void onControlCreated( int index, Composite composite );

		@SuppressWarnings("unchecked")
		@Override
		public Control onCreatePage(Composite parent) {
			Composite comp = null;
		    try {
				ClassLoader classLoader = clss.getClassLoader();
		        IndexStore is = store.get( super.getName() );
				if( StringUtils.isEmpty( is.composite_clss ))
		        	return null;
				Class<Composite> compClass = (Class<Composite>) classLoader.loadClass( is.composite_clss );
				Constructor<?> argsConstructor = compClass.getConstructor( Composite.class, int.class );
		        comp = (Composite) argsConstructor.newInstance( parent, is.composite_style );
		        if( !StringUtils.isEmpty( is.url ) && ( comp instanceof Browser )){
		        	Browser browser = (Browser) comp;
		        	browser.setUrl( is.url );
		        }
		        onControlCreated(is.getIndex(),  comp );
		    } catch ( Exception e) {
		        e.printStackTrace();
		    }
		    return comp;
		}

		@Override
		protected void onUpdateControl(Control control) {
			if( !( super.getContainer() instanceof WizardContainer ))
				return;
			IButtonWizardContainer container = (IButtonWizardContainer) getContainer();
			container.setButtonEnabled( IButtonWizardContainer.Buttons.CANCEL, true );
			container.updateButtons();
		}

		@Override
		protected void cleanup(Control control) {
			control.dispose();
		}

	}

	protected class FlowControl extends AbstractFlowControl<T>{


		@Override
		public void init() {
			super.init();
		}

		@Override
		protected int onNextIndex(int index) {
			String next = getIndexStore( index ).getButtonPushed( Buttons.NEXT );
			IndexStore is = store.get( next );
			int select = ( index+1) % store.size();
			return StringUtils.isEmpty( next )? select: is.getIndex();
		}

		@Override
		protected final void setIndex(int index) {
			super.setIndex(index);
		}
	}

	private class IndexStoreComparator implements Comparator<IndexStore>{

		@Override
		public int compare(IndexStore o1, IndexStore o2) {
			return o1.index - o2.index;
		}

	}
}