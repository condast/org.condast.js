/*******************************************************************************
 * Copyright (c) 2014 Chaupal.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *******************************************************************************/
package org.condast.js.commons.ui.widgets.xml;

import java.lang.reflect.Constructor;
import java.util.EnumSet;

import org.condast.commons.preferences.xml.AbstractXMLBuilder;
import org.condast.commons.preferences.xml.AbstractXmlHandler;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.swt.IStyle;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.xml.sax.Attributes;

public class XMLFactoryBuilder extends AbstractXMLBuilder<Widget, AbstractXMLBuilder.Selection> {

	public XMLFactoryBuilder( Composite parent, Class<?> clss ) {
		this( parent, clss, S_DEFAULT_FOLDER + "/" + S_DEFAULT_DESIGN_FILE);
		//URL url = clss.getResource( S_DEFAULT_FOLDER + "/" + S_DEFAULT_DESIGN_FILE);
	}

	public XMLFactoryBuilder( Composite parent, Class<?> clss, String path ) {
		super( new XMLHandler( clss, parent ), clss.getResourceAsStream( path ));
	}

	public XMLFactoryBuilder( Composite parent ) {
		this( parent, XMLFactoryBuilder.class );
	}

	public static String getLocation( String defaultLocation ){
		if( !StringUtils.isEmpty( defaultLocation ))
			return defaultLocation;
		return defaultLocation;
	}

	public Composite getRoot(){
		XMLHandler handler = (XMLHandler) super.getHandler();
		return handler.getRoot();
	}

	@Override
	public Widget[] getUnits() {
		return getHandler().getUnits();
	}

	private static class XMLHandler extends AbstractXmlHandler<Widget,XMLFactoryBuilder.Selection>{

		private Composite composite;
		private Composite root;
		private LayoutDataBuilder databuilder;
		//private Language language;
		//private IImageProvider imageProvider;
		private int select;

		public XMLHandler( Class<?> clss, Composite parent ) {
			super( clss, EnumSet.allOf( XMLFactoryBuilder.Selection.class));
			this.root = parent;
		}

		public Composite getRoot(){
			return root;
		}

		@Override
		public Composite[] getUnits() {
			Composite[] comps = new Composite[1];
			comps[0] = composite;
			return comps;
		}

		@Override
		protected Widget parseNode( Selection node, Attributes attributes) {
			Widget  retval = null;
			String style_str = getAttribute( attributes, AttributeNames.STYLE );
			String name = getAttribute( attributes, AttributeNames.NAME );
			//String url = getAttribute( attributes, AttributeNames.URL );
			//String height_str = getAttribute( attributes, AttributeNames.HEIGHT );
			//int height = StringUtils.isEmpty( height_str )? 50: Integer.parseInt( height_str );
			//String size_str = getAttribute( attributes, AttributeNames.SIZE );
			//int size = StringUtils.isEmpty( size_str )? 50: Integer.parseInt( size_str );
			int style = StringUtils.isEmpty(style_str)? SWT.NONE: IStyle.SWT_ENUM.convert( style_str );
			//boolean horizontal = SWT.HORIZONTAL == style;

			String select_str = getAttribute( attributes, AttributeNames.SELECT );
			select = StringUtils.isEmpty(select_str)?0: Integer.parseInt( select_str);

			//String width_str = getAttribute( attributes, AttributeNames.WIDTH );
			//int width = StringUtils.isEmpty( width_str )? 50: Integer.parseInt( width_str );

			Widget parent = ( super.getCurrentData() == null )? this.root: ( Widget)super.getCurrentData();
			Widget widget = null;
			Composite comp = null;
			Control control = null;
			String class_str = null;
			switch( node ){
			case PREFERENCES:
				break;
			case STORE:
				String id = getAttribute( attributes, AttributeNames.ID );
				super.addPreferenceStore(id, name);
				break;
			case LANGUAGE:
				class_str = getAttribute( attributes, AttributeNames.CLASS );
				//this.language = (Language) createObject( super.getClass(), class_str);
				break;
			case IMAGE_PROVIDER:
				class_str = getAttribute( attributes, AttributeNames.CLASS );
				//this.imageProvider = (IImageProvider) createObject( super.getClass(), class_str);
				break;
			case FRONTEND:
				widget = new Composite((Composite) parent, IStyle.SWT_ENUM.convert( style_str ) | SWT.BORDER );
				composite = (Composite) widget;
				composite.setLayout( new FillLayout( SWT.HORIZONTAL ));
				retval = composite;
				break;
			case LAYOUT:
				LayoutBuilder layoutBuilder = new LayoutBuilder();
				comp = (Composite) parent;
				layoutBuilder.setLayout(comp, attributes);
				break;
			case TABFOLDER:
				widget = new TabFolder( (Composite) parent, style);
				comp = (Composite) widget;
				comp.setLayout( new GridLayout());
				control = (Control) widget;
				retval = widget;
				break;
			case COMPOSITE:
				class_str = getAttribute( attributes, AttributeNames.CLASS );
				if( parent instanceof Composite ){
					widget = createComposite( this.getHandlerClass(), class_str, (Composite) parent, style );
				}else if( parent instanceof TabItem ){
					TabItem item = (TabItem) parent;
					TabFolder folder = item.getParent();
					widget = createComposite( this.getHandlerClass(), class_str, folder, style );
					item.setControl((Control) widget);
				}
				break;
			case TOOLBAR:
				class_str = getAttribute( attributes, AttributeNames.CLASS );
				ToolBar toolbar = StringUtils.isEmpty(class_str)? new ToolBar( (Composite) parent, SWT.NONE ):
					(ToolBar) createComposite( this.getHandlerClass(), class_str, (Composite) parent, style );
				widget = toolbar;
				break;
			case CONTROL:
				class_str = getAttribute( attributes, AttributeNames.CLASS );
				String text = getAttribute( attributes, AttributeNames.TEXT );
				if( parent instanceof ToolItem ){
					ToolItem item = (ToolItem) parent;
					widget = createControl( this.getHandlerClass(), class_str, item.getParent(), style );
					item.setControl((Control) widget);
				}else if( parent instanceof CoolItem ){
					CoolItem item = (CoolItem) parent;
					widget = createControl( this.getHandlerClass(), class_str, item.getParent(), style );
					item.setControl((Control) widget);
				}
				setText(widget, text);
				break;
			case LAYOUT_DATA:
				this.databuilder = new LayoutDataBuilder();
				if( parent instanceof Composite ) {
					comp = (Composite) parent;
					comp.setLayoutData(this.databuilder.getData());
				}
				break;
			case HORIZONTAL:
				this.databuilder.setGridLayoutData(node, attributes);
				break;
			case VERTICAL:
				this.databuilder.setGridLayoutData(node, attributes);
				break;
			case IMAGE:
				//NavigationComposite navcomp = (NavigationComposite) super.getCurrentData();
				//Image image = AbstractImages.getImageFromResource( root.getDisplay(), this.getHandlerClass(), url );
				//navcomp.setImage( image );
				break;
			case ITEM:
				if( super.getCurrentData() instanceof TabFolder ) {
					TabFolder tabcomp = (TabFolder) super.getCurrentData();
					TabItem item = new TabItem( tabcomp, style );
					item.setText(name);
					widget = item;
				}else if( super.getCurrentData() instanceof ToolBar ) {
					toolbar = (ToolBar) super.getCurrentData();
					ToolItem item = new ToolItem( toolbar, style );
					item.setText(name);
					widget = item;
				}else if( super.getCurrentData() instanceof CoolBar ) {
					CoolBar coolbar = (CoolBar) super.getCurrentData();
					CoolItem item = new CoolItem( coolbar, style );
					item.setText(name);
					widget = item;
				}
				break;
			case BODY:
				comp = new Composite((Composite) parent, IStyle.SWT_ENUM.convert( style_str ));
				comp.setLayout(new FillLayout());
				widget = comp;
				GridData gd_body = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
				gd_body.horizontalIndent = 0;
				gd_body.verticalIndent = 0;
				control = (Control) widget;
				control.setLayoutData( gd_body);
				break;
			case STATUS_BAR:
				/*
				StatusBar bar = new StatusBar((Composite) parent, IStyle.SWT_ENUM.convert( style_str ));
				widget = bar;
				if( !StringUtils.isEmpty( name ))
					bar.setLabelText( name );
				widget = bar;
				bar.setLayout(new FillLayout(SWT.HORIZONTAL));
				*/
				//GridData gd_text_status = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
				//gd_text_status.heightHint = height;
				//bar.setLayoutData(gd_text_status);
				break;
			default:
				break;
			}
			if( widget != null ){
				String rwtCustom = getAttribute( attributes, AttributeNames.RWT_CUSTOM );
				if( !StringUtils.isEmpty( rwtCustom ))
					widget.setData( RWT.CUSTOM_VARIANT, rwtCustom );
				retval = widget;
			}
			return retval;
		}

		@Override
		protected void completeNode(Enum<Selection> node) {
			Selection selection = Selection.valueOf( node.name());
			switch( selection ){
			case TABFOLDER:
				TabFolder tabfolder = (TabFolder) super.getCurrentData();
				tabfolder.setSelection(select);
				break;
			default:
				break;
			}
		}

		@Override
		protected void addValue(Enum<Selection> node, String value) {
		}

		@Override
		public Composite getUnit(String id) {
			return null;
		}
	}

	protected static void setText( Widget widget, String text ) {
		if( StringUtils.isEmpty(text))
			return;
		if( widget instanceof Label ) {
			Label label = (Label) widget;
			label.setText(text);
		}else if( widget instanceof Text ) {
			Text txt = (Text) widget;
			txt.setText(text);
		}else if( widget instanceof Button ) {
			Button button = (Button) widget;
			button.setText(text);
		}
	}
	@SuppressWarnings("unchecked")
	protected static Composite createComposite( Class<?> clss, String className, Composite parent, int style ){
		if( StringUtils.isEmpty( className ))
			return null;
		Composite composite = null;
		Class<Composite> cls  = null;
		try{
			cls = (Class<Composite>) clss.getClassLoader().loadClass( className );
			Constructor<Composite> cons = cls.getConstructor( Composite.class, Integer.TYPE);
			composite = cons.newInstance( parent, style );
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return composite;
	}

	@SuppressWarnings("unchecked")
	protected static Control createControl( Class<?> clss, String className, Composite parent, int style ){
		if( StringUtils.isEmpty( className ))
			return null;
		Control control = null;
		Class<? extends Control> cls  = null;
		try{
			cls = (Class<? extends Control>) clss.getClassLoader().loadClass( className );
			Constructor<? extends Control> cons = cls.getConstructor( Composite.class, Integer.TYPE);
			control = cons.newInstance( parent, style );
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return control;
	}

	private static class LayoutBuilder{

		private enum Layout{
			FILL_LAYOUT,
			GRID_LAYOUT
		}

		private enum LayoutAttributes{
			NUM_COLUMS,
			SPACE_EVENLY;
		}

		public void setLayout( Composite parent, Attributes attributes ){
			String name = getAttribute( attributes, AttributeNames.NAME );
			Layout layout = StringUtils.isEmpty( name )? Layout.FILL_LAYOUT:
				Layout.valueOf( StringStyler.styleToEnum( name ));
			String numcol_str = getAttribute(attributes, LayoutAttributes.NUM_COLUMS );
			int numcol = StringUtils.isEmpty( numcol_str)?1: Integer.parseInt( numcol_str);
			String space_str = getAttribute(attributes, LayoutAttributes.SPACE_EVENLY );
			boolean space = StringUtils.isEmpty( space_str)?false: Boolean.parseBoolean( space_str);
			switch( layout ){
			case GRID_LAYOUT:
				parent.setLayout( new GridLayout( numcol, space ));
				break;
			default:
				String type_str = getAttribute(attributes, LayoutAttributes.SPACE_EVENLY );
				int type = StringUtils.isEmpty( type_str)?0: Integer.parseInt( type_str);
				parent.setLayout( new FillLayout( type ));
				break;
			}

		}
	}

	private static class LayoutDataBuilder{

		private enum LayoutAttributes{
			ALIGN,
			GRAB_EXCESS,
			SPAN;
		}

		private GridData data;

		protected LayoutDataBuilder() {
			this( new GridData() );
		}

		protected LayoutDataBuilder( GridData data) {
			this.data = data;
		}

		public GridData getData() {
			return data;
		}

		private void setGridLayoutData( Selection layout, Attributes attributes ){
			String align_str = getAttribute( attributes, LayoutAttributes.ALIGN );
			Integer align = StringUtils.isEmpty( align_str )? SWT.FILL:
				IStyle.SWT_ENUM.convert( StringStyler.styleToEnum( align_str ));
			String grab_excess_str = getAttribute( attributes, LayoutAttributes.GRAB_EXCESS );
			boolean grab_excess = StringUtils.isEmpty( grab_excess_str)? false:
				Boolean.parseBoolean( grab_excess_str);
			String span_str = getAttribute( attributes, LayoutAttributes.SPAN );
			int span = StringUtils.isEmpty( span_str )? 0: Integer.parseInt( span_str );
			switch( layout ){
			case HORIZONTAL:
				data.grabExcessHorizontalSpace = grab_excess;
				data.horizontalAlignment = align;
				data.horizontalSpan = span;
				break;
			default:
				data.grabExcessVerticalSpace = grab_excess;
				data.verticalAlignment = align;
				data.verticalSpan = span;
				break;
			}
		}

	}

}