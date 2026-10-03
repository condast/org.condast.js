package org.condast.js.commons.ui.widgets.wizard;

import org.condast.commons.flow.IFlowControl;
import org.condast.commons.i18n.Language;
import org.condast.commons.ui.wizard.AbstractFlowControlWizard;
import org.condast.commons.ui.wizard.ButtonEvent;
import org.condast.commons.ui.wizard.IButtonSelectionListener;
import org.condast.commons.ui.wizard.IButtonWizardContainer;
import org.condast.commons.ui.wizard.IHeadlessWizardContainer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;

public abstract class AbstractButtonFlowWizard<T extends Object> extends AbstractFlowControlWizard<T> {

	private IWizard wizard;
	private Language language;

	private IButtonSelectionListener<T> listener = new IButtonSelectionListener<T>() {

		@Override
		public void notifyButtonPressed(ButtonEvent<T> event) {
			injectButtonEvent( new ButtonEvent<T>( this, event.getButton() ));
		}
	};

	protected AbstractButtonFlowWizard( Language language, IFlowControl flow, String title ) {
		super( flow, title );
		this.language = language;
		this.wizard = this;
	}

	protected Language getLanguage() {
		return language;
	}

	@Override
	protected IHeadlessWizardContainer onSelectContainer(int index) {
		IHeadlessWizardContainer hwc = (IHeadlessWizardContainer) super.getContainer();
		if(( super.getContainer() == null ) || (!( super.getContainer() instanceof WizardContainer)))
			hwc = new WizardContainer( language, super.getFlowControl());
		return hwc;
	}

	@Override
	protected void initialiseContainer(IHeadlessWizardContainer cont) {
		if(!( cont instanceof WizardContainer ))
			return;
		WizardContainer container = (WizardContainer) cont;
		container.setPreviousnext(true);
		container.setButtons( WizardContainer.getDefaultButtons());
		container.addListener(listener);
	}

	/**
	 * Update the buttons by actively enabling or disabling the
	 * appropriate ones
	 * @param fromPrevious
	 */
	protected void onUpdatePage( int index ){
		IHeadlessWizardContainer hwc = (IHeadlessWizardContainer) getContainer();
		if(( hwc != null ) && !( hwc instanceof IButtonWizardContainer )){
			hwc.updateButtons();
			return;
		}
		IButtonWizardContainer container = (IButtonWizardContainer) hwc;
		boolean choice = ( index > 0 );

		if( this.needsPreviousAndNextButtons() ){
			container.setButtonEnabled( IButtonWizardContainer.Buttons.PREVIOUS, choice);
			choice = ( index < container.size() - 1 );
			container.setButtonEnabled( IButtonWizardContainer.Buttons.NEXT, choice);
		}
		if( container == null )
			return;
		choice = ( index >= container.getFinishIndex() );
		container.setButtonEnabled( IButtonWizardContainer.Buttons.FINISH, choice);
		hwc.updateButtons();
		container.updateButtons();
		return;
	}

	@Override
	protected IWizardPage updatePage(int index) {
		onUpdatePage(index);
		return super.updatePage(index);
	}

	/**
	 * Allow overriding the default behaviour of the buttons
	 * @param event
	 */
	protected void injectButtonEvent( ButtonEvent<T> event ){
		switch( event.getButton() ){
		case CONTINUE:
			selectContainer( getFlowControl().getIndex() );
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
	public void dispose() {
		IButtonWizardContainer container = (IButtonWizardContainer) getContainer();
		container.removeListener(listener);
		super.dispose();
	}
}