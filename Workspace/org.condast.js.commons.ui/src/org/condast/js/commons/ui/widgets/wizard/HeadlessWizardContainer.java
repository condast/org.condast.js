package org.condast.js.commons.ui.widgets.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.condast.commons.Utils;
import org.condast.commons.flow.IFlowControl;
import org.condast.commons.ui.wizard.ButtonEvent;
import org.condast.commons.ui.wizard.IButtonSelectionListener;
import org.condast.commons.ui.wizard.IButtonWizardContainer;
import org.condast.commons.ui.wizard.IHeadlessWizardContainer;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class HeadlessWizardContainer implements IHeadlessWizardContainer{
	private static final long serialVersionUID = 1L;

	private ContainerTypes type;
	private List<IWizardPage> pages;
	private IFlowControl flow;
	private Composite body;
	private int finishIndex;
	private boolean disposed;

	private String rwtId;


	private Collection<IButtonSelectionListener<?>> listeners;

	public HeadlessWizardContainer( IFlowControl flow ) {
		this( flow, new ArrayList<IWizardPage>());
	}

	public HeadlessWizardContainer( IFlowControl flow, List<IWizardPage> pages ) {
		this( flow, ContainerTypes.HEADLESS, pages );
	}

	protected HeadlessWizardContainer( IFlowControl flow, ContainerTypes type, List<IWizardPage> pages ) {
		this.type = type;
		this.finishIndex = 0;
		this.flow = flow;
		this.disposed = false;
		this.pages = pages;
		listeners = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see org.condast.commons.ui.wizard.IHeadlessWizardContainer#createContainer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite createComposite( Composite parent, int style) {
		body = new Composite(parent, style);
		if(rwtId==null)
			body.setData(RWT.CUSTOM_VARIANT, IButtonWizardContainer.RWT_WIZARD_CONTAINER );
		else
			body.setData(RWT.CUSTOM_VARIANT,rwtId + "container");
		body.setLayout( new FillLayout());
		return body;
	}

	protected ContainerTypes getType() {
		return type;
	}

	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public void addPage( IWizardPage page ){
		if( this.finishIndex == this.pages.size() - 1)
			this.finishIndex++;
		this.pages.add( page );
	}

	@Override
	public void removePage( IWizardPage page ){
		if( this.finishIndex == this.pages.size() - 1)
			this.finishIndex--;
		this.pages.remove( page );
	}


	@Override
	public IWizardPage getPage( int index ){
		return pages.get(index);
	}

	@Override
	public IWizardPage[] getPages(){
		return this.pages.toArray( new IWizardPage[ this.pages.size() ]);
	}

	/**
	 * Set the given pages in this container
	 * @param newPages
	 */
	@Override
	public void setPages( IWizardPage[] newPages ){
		this.pages.clear();
		this.pages.addAll( Arrays.asList( newPages ));
	}

	@Override
	public int size(){
		return pages.size();
	}

	@Override
	public int getFinishIndex() {
		return finishIndex;
	}

	@Override
	public void setFinishIndex(int finishIndex) {
		this.finishIndex = finishIndex;
	}

	@Override
	public boolean canFinish( int index ){
		return ( index >= this.finishIndex );
	}

	/**
	 * Returns true if the index points to the last page
	 * @return
	 */
	@Override
	public boolean isLastPage( int index ){
		return( index >= ( this.pages.size() - 1 ));
	}

	public void addListener( IButtonSelectionListener<?> listener ){
		this.listeners.add( listener );
	}

	public void removeListener( IButtonSelectionListener<?> listener ){
		this.listeners.remove( listener );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected synchronized final void notifyListeners( ButtonEvent<?> event ){
		if( this.disposed )
			return;
		for( IButtonSelectionListener listener: this.listeners ){
			listener.notifyButtonPressed(event);
		}
	}

	@Override
	public void run(boolean arg0, boolean arg1, IRunnableWithProgress arg2)
			throws InvocationTargetException, InterruptedException {
		if( body.isDisposed() )
			return;
		body.getDisplay().asyncExec( new Runnable(){

			@Override
			public void run() {
				for( Control child: body.getChildren() ){
					child.dispose();
				}
				try{
					IWizardPage page = pages.get( flow.getIndex());
					page.createControl( body );
					updateButtons();
					updateMessage();
					updateWindowTitle();
					updateTitleBar();
					body.layout( true );//DO NOT REMOVE! It may result in unexpected UI behaviour!
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public IWizardPage getCurrentPage() {
		if( Utils.assertNull( pages ))
			return null;
		return pages.get( flow.getIndex() );
	}

	@Override
	public Shell getShell() {
		return Display.getCurrent().getActiveShell();
	}

	@Override
	public void showPage(IWizardPage arg0) {
		if(( body == null ) || disposed )
			return;
		try {
			run( false, false, null );
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateMessage() {
		/* NOTHING */
	}

	@Override
	public void updateTitleBar() {
		// NOTHING
	}

	@Override
	public void updateWindowTitle() {
		/* NOTHING */
	}

	@Override
	public void updateButtons() {/* NOTHING */}

	/**
	 * Dispose the container
	 */
	@Override
	public void dispose(){
		this.disposed = true;
		if( body != null )
			body.dispose();
	}

	@Override
	public Composite getActiveComposite() {
		return body;
	}

	@Override
	public void refresh() {
		Display.getCurrent().asyncExec( new Runnable(){

			@Override
			public void run() {
				getActiveComposite().layout();
			}

		});
	}

	public String getRwtId() {
		return rwtId;
	}

	public void setRwtId(String rwtId) {
		this.rwtId = rwtId;
	}


}
