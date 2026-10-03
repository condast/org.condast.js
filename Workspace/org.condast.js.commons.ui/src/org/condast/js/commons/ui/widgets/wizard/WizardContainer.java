package org.condast.js.commons.ui.widgets.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.condast.commons.flow.IFlowControl;
import org.condast.commons.i18n.Language;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.wizard.ButtonEvent;
import org.condast.commons.ui.wizard.IButtonWizardContainer;
import org.condast.commons.ui.wizard.IFlowWizardPage;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class WizardContainer extends HeadlessWizardContainer implements IButtonWizardContainer {
	private static final long serialVersionUID = 1L;

	private static final String S_TITLE = "title";
	private static final String S_MESSAGE = "message";

	private Label lblTitle;
	private Label lblMessage;

	private Composite iconComposite;
	private Composite toolBar;
	private Composite buttonBar;
	private Composite helpBar;

	private Map<IButtonWizardContainer.Buttons, Boolean> selection;

	private boolean helpAvaliable;
	private boolean previousnext;

	private Language language;

	private Composite composite;

	private String rwtId;


	public WizardContainer( Language language, IFlowControl flow ) {
		this( language, ContainerTypes.HEAD, flow );
	}


	public WizardContainer( Language language, IFlowControl flow, List<IWizardPage> pages ) {
		this( language, flow, ContainerTypes.HEAD, pages);
	}

	public WizardContainer( Language language, ContainerTypes type, IFlowControl flow ) {
		this( language, flow, type, new ArrayList<IWizardPage>() );
	}

	protected WizardContainer( Language language, IFlowControl flow, ContainerTypes type, List<IWizardPage> pages ) {
		super( flow, type, pages );
		this.language = language;
		this.helpAvaliable = false;
		selection = new TreeMap<>( new ButtonComparator());
	}

	@Override
	public Composite createComposite( Composite parent, int style ) {
		this.composite = new Composite( parent, SWT.NONE );
		composite.setLayout( new GridLayout(1, false ));
		if(rwtId==null)
			composite.setData(RWT.CUSTOM_VARIANT, RWT_WIZARD_CONTAINER );
		else
			composite.setData(RWT.CUSTOM_VARIANT, rwtId + "-container" );

		if( ContainerTypes.HEAD.equals( super.getType() )){
			Composite titleComposite = new Composite(composite, SWT.BORDER);
			if(rwtId==null)
				titleComposite.setData(RWT.CUSTOM_VARIANT, RWT_WIZARD );
			else
				titleComposite.setData(RWT.CUSTOM_VARIANT, rwtId );
			titleComposite.setLayout( new GridLayout(2, false ));
			GridData gd_titleComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
			titleComposite.setLayoutData(gd_titleComposite);

			this.lblTitle = new Label( titleComposite, SWT.NONE );
			if(rwtId==null)
				this.lblTitle.setData( RWT.CUSTOM_VARIANT, RWT_WIZARD_TITLE );
			else
				this.lblTitle.setData( RWT.CUSTOM_VARIANT, rwtId + "-title" );
			this.lblTitle.setText( S_TITLE );
			GridData gd_title = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
			this.lblTitle.setLayoutData( gd_title );

			this.iconComposite = new Composite( titleComposite, SWT.NONE );
			if(rwtId==null)
				this.iconComposite.setData( RWT.CUSTOM_VARIANT, RWT_WIZARD );
			else
				this.iconComposite.setData( RWT.CUSTOM_VARIANT, rwtId );
			GridData gd_icon = new GridData( SWT.FILL, SWT.FILL, false, true, 1, 2 );
			gd_icon.widthHint = 200;
			this.iconComposite.setLayoutData( gd_icon);

			this.lblMessage = new Label( titleComposite, SWT.NONE );
			if(rwtId==null)
				this.lblMessage.setData( RWT.CUSTOM_VARIANT, RWT_WIZARD );
			else
				this.lblMessage.setData( RWT.CUSTOM_VARIANT, rwtId );
			this.lblMessage.setText( S_MESSAGE );
			GridData gd_message = new GridData( SWT.FILL, SWT.FILL, true, false, 1, 1 );
			this.lblMessage.setLayoutData( gd_message );
		}

		Composite body = super.createComposite( composite, SWT.NONE);
		GridData gd_body = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		body.setLayoutData(gd_body);

		toolBar = new Composite( composite, SWT.BORDER );
		toolBar.setLayout( new GridLayout(2, false ));
		toolBar.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ));
		if(rwtId==null)
			toolBar.setData(RWT.CUSTOM_VARIANT, RWT_WIZARD_TOOLBAR );
		else
			toolBar.setData(RWT.CUSTOM_VARIANT, rwtId = "_toolbar" );
		helpBar = new Composite( toolBar, SWT.LEFT_TO_RIGHT );
		GridData gd_help = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		helpBar.setLayoutData(gd_help);
		if(rwtId==null)
			helpBar.setData(RWT.CUSTOM_VARIANT, RWT_WIZARD_TOOLBAR );
		else
			helpBar.setData(RWT.CUSTOM_VARIANT, rwtId = "_toolbar");

		buttonBar = new Composite( toolBar, SWT.RIGHT_TO_LEFT );
		buttonBar.setLayout(new GridLayout(6, false));
		GridData gd_buttons = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		buttonBar.setLayoutData(gd_buttons);
		if(rwtId==null)
			buttonBar.setData(RWT.CUSTOM_VARIANT, RWT_WIZARD_TOOLBAR );
		else
			buttonBar.setData(RWT.CUSTOM_VARIANT, rwtId = "_toolbar");
		return composite;
	}

	/**
	 * Get the current selection of buttons
	 * @return
	 */
	public Buttons[] getSelection(){
		if(( selection == null ) || ( selection.size() == 0 ))
			return null;
		return selection.keySet().toArray( new Buttons[ selection.size() ]);
	}

	public boolean isHelpAvailable() {
		return helpAvaliable;
	}

	public void setHelpAvaliable( boolean helpAvaliable ) {
		this.helpAvaliable = helpAvaliable;
	}

	public boolean isPreviousnext() {
		return previousnext;
	}

	public void setPreviousnext(boolean previousnext) {
		this.previousnext = previousnext;
	}

	@Override
	public Composite getToolBar() {
		return toolBar;
	}

	/* (non-Javadoc)
	 * @see org.condast.commons.ui.wizard.IExtendedWizardContainer#getButtonBar()
	 */
	@Override
	public Composite getButtonBar() {
		return buttonBar;
	}

	@Override
	public Composite getActiveComposite() {
		return composite;
	}

	/**
	 * Clear the buttons, but add the required ones
	 */
	@Override
	public void clearButtons(){
		this.selection.clear();
		setButtons( new Buttons[0]);
	}

	/**
	 * Set the buttons which are required
	 * @param buttons
	 */
	public void addButton( Buttons button, boolean enabled ){
		this.selection.put( button, enabled );
	}

	/**
	 * Set the buttons which are required
	 * @param buttons
	 */
	public void setButtons( IButtonWizardContainer.Buttons[] buttons ){
		Collection<IButtonWizardContainer.Buttons> list = new ArrayList<>( Arrays.asList( buttons ));
		if( this.previousnext ){
			if( !list.contains( IButtonWizardContainer.Buttons.PREVIOUS ))
				list.add( IButtonWizardContainer.Buttons.PREVIOUS);
			if( !list.contains( IButtonWizardContainer.Buttons.NEXT ))
				list.add( IButtonWizardContainer.Buttons.NEXT);
		}
		if( this.helpAvaliable ){
			if( !list.contains( IButtonWizardContainer.Buttons.HELP ))
				list.add( IButtonWizardContainer.Buttons.HELP);
		}
		//first remove button that are not needed
		selection.clear();
		for( IButtonWizardContainer.Buttons button: list ){
			if( IButtonWizardContainer.Buttons.CONTINUE.equals( button ))
				continue;
			selection.put(button, false);
		}
	}

	@Override
	public void updateButtons(){
		if( this.buttonBar == null )
			return;
		Collection<Buttons> cursel = selection.keySet();
		for( Control control: buttonBar.getChildren() ){
			if( !cursel.contains( control.getData() ))
				control.dispose();
		}

		Iterator<Map.Entry<IButtonWizardContainer.Buttons, Boolean>> iterator = selection.entrySet().iterator();
		while( iterator.hasNext() ){
			final Map.Entry<IButtonWizardContainer.Buttons, Boolean> entry = iterator.next();
			Button btn = getButton( entry.getKey() );
			if( btn == null ){
				btn = new Button(buttonBar, SWT.NONE);
				btn.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false));
				btn.setData( entry.getKey());
				btn.setEnabled(( entry.getValue() == null )? false: entry.getValue());
				String txt = ( language == null )? entry.getKey().toString(): language.getString( entry.getKey().name() );
				btn.setText( txt );
				btn.addSelectionListener( new SelectionAdapter(){
					private static final long serialVersionUID = 1L;

					//We first call the super class in order to complete
					//button specific behaviour before notifying listeners
					//this in order to prevent dispose problems
					@Override
					public void widgetSelected(SelectionEvent e) {
						super.widgetSelected(e);
						notifyListeners( new ButtonEvent<Object>( this, entry.getKey(), getCurrentPage() ));
					}
				});
				btn.requestLayout();
				selection.put(entry.getKey(), false);
			}else{
				btn.setEnabled(( entry.getValue() == null )? false: entry.getValue());
			}
		}
		buttonBar.requestLayout();
		super.updateButtons();
		refresh();
	}

	private Button getButton( Buttons button ){
		for( Control btn: buttonBar.getChildren()){
			if( button.equals( btn.getData() ))
				return (Button) btn;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.condast.commons.ui.wizard.IExtendedWizardContainer#buttonVisible(org.condast.commons.ui.wizard.WizardContainer.Buttons, boolean)
	 */
	@Override
	public void buttonVisible( IButtonWizardContainer.Buttons button, boolean choice ){
		this.setButtonEnabled(button, choice);
		Button btn = getButton( button );
		if( btn == null )
			return;
		btn.setVisible( choice );
		btn.requestLayout();
	}

	@Override
	public boolean isButtonEnabled( Buttons button) {
		Button btn = getButton( button );
		return ( btn != null ) && btn.isEnabled();
	}

	/* (non-Javadoc)
	 * @see org.condast.commons.ui.wizard.IExtendedWizardContainer#buttonEnabled(org.condast.commons.ui.wizard.WizardContainer.Buttons, boolean)
	 */
	@Override
	public void setButtonEnabled( Buttons button, boolean choice ){
		if( selection.get( button ) == null )
			return;
		selection.put(button, choice );
		Button btn = getButton(button);
		if( btn != null )
			btn.setEnabled(choice);
	}

	@Override
	public void updateWindowTitle() {
		if( ContainerTypes.HEADLESS.equals( super.getType() ))
			return;
		if( !StringUtils.isEmpty( getCurrentPage().getDescription() ))
			this.lblTitle.setText(getCurrentPage().getDescription());
		if( !StringUtils.isEmpty( getCurrentPage().getMessage()))
			this.lblMessage.setText( getCurrentPage().getMessage() );
	}

	/**
	 * Update the title bar. also updates the help bar.
	 */
	@Override
	public void updateTitleBar() {
		IWizardPage page = getCurrentPage();
		if( !( page instanceof IFlowWizardPage ))
			return;
		IFlowWizardPage fpage=  (IFlowWizardPage) page;
		if( iconComposite != null )
			fpage.createIcon(iconComposite );
		fpage.createToolBar( helpBar );
		super.updateTitleBar();
	}

	@Override
	public void refresh() {
		if( helpBar != null )
			helpBar.layout();
		if( buttonBar != null )
			buttonBar.layout();
		super.refresh();
	}

	@Override
	public void dispose() {
		if( this.composite != null )
			this.composite.dispose();
	}

	/**
	 * return a list of all the available buttons
	 * @return
	 */
	public static IButtonWizardContainer.Buttons[] getAllButtons(){
		return IButtonWizardContainer.Buttons.values();
	}

	/**
	 * return a list of all the available buttons
	 * @return
	 */
	public static IButtonWizardContainer.Buttons[] getDefaultButtons(){
		Collection<IButtonWizardContainer.Buttons> list = new ArrayList<>();
		for( IButtonWizardContainer.Buttons button: IButtonWizardContainer.Buttons.values()){
			switch( button ){
			case HELP:
			case CONTINUE:
				break;
			default:
				list.add( button );
				break;
			}
		}
		return list.toArray( new IButtonWizardContainer.Buttons[ list.size() ]);
	}

	@Override
	public String getRwtId() {
		return rwtId;
	}


	@Override
	public void setRwtId(String rwtId) {
		this.rwtId = rwtId;
	}

	private static class ButtonComparator implements Comparator<IButtonWizardContainer.Buttons>{

		@Override
		public int compare(IButtonWizardContainer.Buttons o1, IButtonWizardContainer.Buttons o2) {
			return o1.ordinal() - o2.ordinal();
		}
	}
}
