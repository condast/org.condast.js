package org.condast.js.swt;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.condast.commons.latlng.LatLng;
import org.condast.js.bootstrap.controller.BootstrapController;
import org.condast.js.bootstrap.controller.BootstrapController.Pages;
import org.condast.js.commons.controller.IJavascriptController;
import org.condast.js.react.controller.ReactController;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.google.geo.mapping.ui.controller.GeoCoderController;
import org.google.geo.mapping.ui.model.MarkerModel;
import org.openlayer.map.control.TransformModel;
//import org.satr.pronto.ProntoLanguage;
import org.openlayer.map.controller.OpenLayerController;

/**
 * @author Kees
 *
 */
public class FrontEndComposite extends Composite {
	private static final long serialVersionUID = 1L;

	private static final String RWT_FRONTEND = "frontend";
	private static final String RWT_FRONTEND_TITLE = "frontend-title";
	
	private static final String TAB_TEXT_GEO_BROWSER = "Geo Browser";
	private static final String TAB_TEXT_REACT_BROWSER = "React Browser";
	private static final String TAB_TEXT_OPEN_LAYER_BROWSER = "OpenLayer Browser";
	private static final String TAB_TEXT_BOOTSTRAP_BROWSER = "Bootstrap Browser";
	private static final String TAB_TEXT_BARE_BOOTSTRAP_BROWSER = "Bootstrap Bare Template";
	
	//Text fields
	private enum Fields{
		TITLE;

		//public String toString(){
		//	return ProntoLanguage.getInstance().getString( this );
		//}
	}
	
	public enum Tabs{
		REACT(0),
		GEO_BROWSER(1),
		OPEN_LAYER(2),
		BOOTSTRAP(3),
		BOOTSTRAP_BARE(4);
		
		private int index;
		
		private Tabs( int index ){
			this.index= index;
		}

		public int getIndex() {
			return index;
		}
		
		public static Tabs getTab( int index ){
			return Tabs.values()[ index ];
		}
		
		
	}

	private Label lblTitle;
	
	private Composite body;
	
	private Combo choices;
	
	private CTabFolder tabFolder;
	
	private Composite comp_info;
	private Text text_id;
	private Composite selected;
	private Tabs tab;
	
	private Map<Tabs,IJavascriptController> controllers;
	
	public FrontEndComposite( Composite parent, int style) {
		super(parent, style);
		controllers = new HashMap<Tabs,IJavascriptController>();
		this.createComposite( parent, style );
		this.initComposite();
	}

	private void createComposite(Composite parent, int style) {
		setLayout(new GridLayout(2, false));
		
		Composite titleComposite = new Composite(this, SWT.NONE);
		titleComposite.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND );	
		titleComposite.setLayout( new GridLayout(3, false ));
		GridData gd_title = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		//gd_title.widthHint = 411;
		gd_title.heightHint = 100;
		titleComposite.setLayoutData( gd_title );

		this.lblTitle = new Label( titleComposite, SWT.NONE );
		lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		this.lblTitle.setData( RWT.CUSTOM_VARIANT, RWT_FRONTEND_TITLE );	
		this.lblTitle.setText( Fields.TITLE.toString() );
		
		this.choices = new Combo( titleComposite, SWT.NONE );
		this.choices.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		this.choices.setData( RWT.CUSTOM_VARIANT, RWT_FRONTEND_TITLE );	
		this.choices.setEnabled(false);
		this.choices.setVisible(false);
		this.choices.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				switch( tab){
				case REACT:
					break;
				case BOOTSTRAP:
				case BOOTSTRAP_BARE:
					BootstrapController btController = (BootstrapController) controllers.get( Tabs.BOOTSTRAP_BARE );
					btController.setBrowser( Pages.values()[choices.getSelectionIndex()]);
					tabFolder.layout(true);
					break;
				default:
					break;
				}
				super.widgetSelected(e);
			}	
		});
		
		comp_info = new Composite(titleComposite, SWT.NONE);
		comp_info.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND );	
		GridData gd_comp_info = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_comp_info.widthHint = 350;
		comp_info.setLayoutData(gd_comp_info);
		comp_info.setLayout(new GridLayout(1, false));
		
		text_id = new Text(comp_info, SWT.MULTI | SWT.NO_SCROLL );
		text_id.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND );	
		text_id.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
		body = new Composite( this, SWT.NONE );
		body.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_body = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_body.horizontalIndent = 0;
		gd_body.verticalIndent = 0;
		body.setLayoutData( gd_body);

		tabFolder = new CTabFolder(body, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		// Add an event listener to write the selected tab to stdout
		tabFolder.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
				setSelected();
			}
		});	
		
		Browser browser = new Browser( tabFolder, SWT.NONE );
		controllers.put( Tabs.REACT, new ReactController(browser));
		CTabItem item = new CTabItem(tabFolder, SWT.NONE);
		item.setText( TAB_TEXT_REACT_BROWSER);
		selected = browser;
		item.setControl( selected );
				
		browser = new Browser( tabFolder, SWT.NONE );
		controllers.put( Tabs.GEO_BROWSER, new GeoCoderController(browser));
		item = new CTabItem(tabFolder, SWT.NONE);
		item.setText( TAB_TEXT_GEO_BROWSER);
		item.setControl( browser );	

		browser = new Browser( tabFolder, SWT.NONE );
		controllers.put( Tabs.OPEN_LAYER, new OpenLayerController(browser));
		item = new CTabItem(tabFolder, SWT.NONE);
		item.setText( TAB_TEXT_OPEN_LAYER_BROWSER );
		item.setControl( browser );	

		browser = new Browser( tabFolder, SWT.NONE );
		controllers.put( Tabs.BOOTSTRAP, new BootstrapController(browser));
		item = new CTabItem(tabFolder, SWT.NONE);
		item.setText( TAB_TEXT_BOOTSTRAP_BROWSER );
		item.setControl( browser );	

		browser = new Browser( tabFolder, SWT.NONE );
		controllers.put( Tabs.BOOTSTRAP_BARE, new BootstrapController(browser));
		item = new CTabItem(tabFolder, SWT.NONE);
		item.setText( TAB_TEXT_BARE_BOOTSTRAP_BROWSER );
		item.setControl( browser );	

		tabFolder.setSelection(0);
	}	

	protected void initComposite(){
		//selected.initComposite();
		//setSelected();
		refresh();
	}
	
	protected void setSelected(){
		selected = (Composite) tabFolder.getSelection().getControl();
		tab = Tabs.getTab( tabFolder.getSelectionIndex());
		InputStream in = null;
		switch( tab){
		case REACT:
			in = this.getClass().getResourceAsStream( "/script/view.js" );
			//reactController.addWidgets( in );			

			in = this.getClass().getResourceAsStream( "/script/contacts.js" );
			ReactController reactController = (ReactController) controllers.get( Tabs.REACT );
			reactController.render( in );	
			break;

		case GEO_BROWSER:
			GeoCoderController geoController = (GeoCoderController) controllers.get( Tabs.GEO_BROWSER );
			MarkerModel mm = new MarkerModel( geoController );
			mm.addMarker( new LatLng(51.910d, 4.4120d) , "images/restaurant-32.png");
			mm.addMarker( new LatLng(51.910d, 4.4120d));
			mm.fitBounds(13);
			mm.synchronize();
			//this.geoController.initComposite();
			break;

		case OPEN_LAYER:
			OpenLayerController olController = (OpenLayerController) controllers.get( Tabs.OPEN_LAYER );
			TransformModel tm = new TransformModel( olController );
			tm.doPan( new LatLng( 4.3d, 52.4d));
			//tm.doRotate(0.5f);
			//mm.addMarker( new LngLat(51.910d, 4.4120d));
			//mm.fitBounds(13);
			tm.synchronize();
			//this.olController.initComposite();
			break;
		case BOOTSTRAP:
			this.choices.setEnabled(true);
			this.choices.setVisible(true);
			this.choices.setItems( Pages.getItems());
			this.choices.select(Pages.BARE.ordinal());
			BootstrapController btController = (BootstrapController) controllers.get( Tabs.BOOTSTRAP );
			btController.setBrowser( Pages.INDEX );
			break;
		case BOOTSTRAP_BARE:
			this.choices.setEnabled(true);
			this.choices.setVisible(true);
			btController = (BootstrapController) controllers.get( Tabs.BOOTSTRAP_BARE );
			btController.setBrowser( Pages.BARE );
			this.choices.setItems( Pages.getItems());
			this.choices.select(Pages.BARE.ordinal());
			break;
		default:
			this.choices.setEnabled(false);
			this.choices.setVisible(false);
			break;
		}
		//selected.initComposite();
	}	
	
	protected void refresh(){
		layout(true);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}