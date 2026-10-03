/*******************************************************************************
 * Copyright (c) 2014 Chaupal.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *******************************************************************************/
package org.condast.js.commons.ui.widgets.wizard.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.condast.commons.io.IOUtils;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.wizard.IButtonWizardContainer.Buttons;
import org.condast.commons.ui.wizard.IHeadlessWizardContainer.ContainerTypes;
import org.condast.js.commons.ui.widgets.wizard.IWizardBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLWizardBuilder<T extends Object> implements IWizardBuilder<T> {

	public static String S_DEFAULT_FOLDER = "/qualities";
	public static String S_SCHEMA_LOCATION =  S_DEFAULT_FOLDER + "/rdm-schema.xsd";

	protected static final String JAXP_SCHEMA_SOURCE =
		    "http://java.sun.com/xml/jaxp/properties/schemaSource";
	protected static final String JP2P_XSD_SCHEMA = "http://www.condast.com/saight/jp2p-schema.xsd";

	//private static final String S_ERR_NO_SCHEMA_FOUND = "The XML Schema was not found";
	private static final String S_WRN_NOT_NAMESPACE_AWARE = "The parser is not validating or is not namespace aware";

	static final String S_DOCUMENT_ROOT = "DOCUMENT_ROOT";

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	static final String S_ERR_SOURCE = "The url and source may not both be provided. Choose only one";

	private boolean completed, failed;
	private InputStream in;

	private enum Nodes{
		WIZARDS,
		WIZARD,
		TITLEBAR,
		BUTTONBAR,
		PAGES,
		PAGE,
		CONTAINER,
		COMPOSITE,
		BUTTONS,
		BUTTON,
		PUSH;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isNode( String value ){
			if( StringUtils.isEmpty( value ))
				return false;
			for( Nodes node: values() ){
				if( node.name().equals( value ))
					return true;
			}
			return false;
		}
	}

	private enum AttributeNames{
		ID,
		TITLE,
		CSS,//style sheet
		TITLEBAR_STYLE,
		BUTTONBAR_STYLE,
		MESSAGE,
		TYPE,
		DESCRIPTION,
		CLASS,
		STYLE,
		URL,
		SOURCE,
		PREVIOUS_NEXT,
		TOOLBAR,
		VISIBLE,
		ENABLED,
		ON_FINISH,//page to which will be returned when pressing finish (default= 0)
		ON_CANCEL;//page to which will be returned when pressing cancel (default= 0)

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public String toXmlStyle() {
			return StringStyler.xmlStyleString( super.toString() );
		}
	}

	private enum ToolbarOptions{
		TOP,
		BOTTOM;

	}

	private XmlHandler<T> handler;

	private Logger logger = Logger.getLogger( XMLWizardBuilder.class.getName() );

	public XMLWizardBuilder( Class<?> clss, String fileName ) throws IOException {
		this( clss.getResource( fileName ).openStream() );
	}

	/**
	 * Build the factories from the given resource in the class file and add them to the container
	 * @param bundleId
	 * @param clss
	 * @param location
	 * @param builder
	 */
	public XMLWizardBuilder( InputStream in) {
		this.in = in;
		this.completed = false;
		this.failed = false;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.chaupal.xml.IFactoryBuilder#build()
	 */
	/* (non-Javadoc)
	 * @see org.condast.commons.ui.wizard.xml.IWizardBuilder#build(org.condast.commons.ui.wizard.xml.AbstractXmlFlowWizard)
	 */
	@Override
	public void build( IXmlFlowWizard<T> wizard) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		//URL schema_in = XMLFactoryBuilder.class.getResource( S_SCHEMA_LOCATION);
		//if( schema_in == null )
		//	throw new RuntimeException( S_ERR_NO_SCHEMA_FOUND );

		//factory.setNamespaceAware( true );
		//SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// note that if your XML already declares the XSD to which it has to conform, then there's no need to create a validator from a Schema object
		//Source schemaFile = new StreamSource( this.getClass().getResourceAsStream( S_SCHEMA_LOCATION ));

		//First parse the XML file
		try {
			logger.info("Parsing code " );
			//Schema schema = schemaFactory.newSchema(schemaFile);
			//factory.setSchema(schema);//saxParser.

			SAXParser saxParser = factory.newSAXParser();
			if( !saxParser.isNamespaceAware() )
				logger.warning( S_WRN_NOT_NAMESPACE_AWARE );

			//saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			//saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(JP2P_XSD_SCHEMA));
			handler = new XmlHandler<>( wizard );
			saxParser.parse( in, handler);
		} catch( SAXNotRecognizedException e ){
			failed = true;
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			failed = true;
			e.printStackTrace();
		} catch (SAXException e) {
			failed = true;
			e.printStackTrace();
		} catch (IOException e) {
			failed = true;
			e.printStackTrace();
		}
		finally{
			IOUtils.close(in);
		}

		this.completed = true;
	}

	/* (non-Javadoc)
	 * @see org.condast.commons.ui.wizard.xml.IWizardBuilder#complete()
	 */
	@Override
	public boolean complete() {
		this.completed = true;
		return completed;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.chaupal.xml.IFactoryBuilder#isCompleted()
	 */
	/* (non-Javadoc)
	 * @see org.condast.commons.ui.wizard.xml.IWizardBuilder#isCompleted()
	 */
	@Override
	public boolean isCompleted() {
		return completed;
	}

	/* (non-Javadoc)
	 * @see org.condast.commons.ui.wizard.xml.IWizardBuilder#hasFailed()
	 */
	@Override
	public boolean hasFailed() {
		return failed;
	}

	public static String getLocation( String defaultLocation ){
		if( !StringUtils.isEmpty( defaultLocation ))
			return defaultLocation;
		return defaultLocation;
	}

	public static class XmlHandler<T extends Object> extends DefaultHandler{

		public static final int MAX_COUNT = 200;

		private Stack<Nodes> stored;
		private IXmlFlowWizard<T> wizard;
		private Buttons button;

		public XmlHandler( IXmlFlowWizard<T> wizard ) {
			stored = new Stack<>();
			this.wizard = wizard;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			String componentName = StringStyler.styleToEnum( qName );

			//The name is not a group. try the default JP2P components
			if( !Nodes.isNode( componentName ))
				return;

			String id = attributes.getValue( AttributeNames.ID.toXmlStyle());
			String type_str = attributes.getValue( AttributeNames.TYPE.toXmlStyle());
			String title = attributes.getValue( AttributeNames.TITLE.toXmlStyle());
			String description = attributes.getValue( AttributeNames.DESCRIPTION.toXmlStyle());
			String toolbar = attributes.getValue( AttributeNames.TOOLBAR.toXmlStyle());
			ToolbarOptions tbo = StringUtils.isEmpty( toolbar )? ToolbarOptions.BOTTOM: ToolbarOptions.valueOf( toolbar.toUpperCase() );
			String titleStyle = attributes.getValue( AttributeNames.TITLEBAR_STYLE.toXmlStyle());
			String buttonStyle = attributes.getValue( AttributeNames.BUTTONBAR_STYLE.toXmlStyle());
			String comp_clss = attributes.getValue( AttributeNames.CLASS.toXmlStyle());
			String urlstr = attributes.getValue( AttributeNames.URL.toXmlStyle());
			String source = attributes.getValue( AttributeNames.SOURCE.toXmlStyle());
			IndexStore is = wizard.getCurrent();
			ContainerTypes type = ContainerTypes.HEADLESS;
			Nodes node = Nodes.valueOf(componentName);
			Nodes parent = getParent(node);
			switch( node ){
			case WIZARD:
				if( !StringUtils.isEmpty(title ))
					wizard.setWindowTitle(title);
				String stylesheet = attributes.getValue( AttributeNames.CSS.toXmlStyle());
				if( !StringUtils.isEmpty( stylesheet )){
					//ClientFileLoader loader = RWT.getClient().getService( ClientFileLoader.class );
					//loader.requireCss( stylesheet );
				}
				break;
			case TITLEBAR:
				if(!StringUtils.isEmpty( titleStyle ))
					break;
				if( Nodes.CONTAINER.equals( parent ) ){
					is.titleStyle = titleStyle;
				}else{
					wizard.setTitleStyle( titleStyle );
				}
				break;
			case BUTTONBAR:
				if(!StringUtils.isEmpty( titleStyle ))
					break;
				if( Nodes.CONTAINER.equals( parent ) ){
					is.buttonStyle = buttonStyle;
				}else{
					wizard.setButtonbarStyle( buttonStyle );
				}
				break;
			case PAGES:
				wizard.setPreviousNext( Boolean.parseBoolean( attributes.getValue( AttributeNames.PREVIOUS_NEXT.toXmlStyle() )));
				break;
			case PAGE:
				type = StringUtils.isEmpty( type_str )? ContainerTypes.HEADLESS:
					ContainerTypes.valueOf( StringStyler.styleToEnum( type_str ));
				String message = attributes.getValue( AttributeNames.MESSAGE.toXmlStyle());
				if( !StringUtils.isEmpty( message))
					message = title;

				String onCancel = attributes.getValue( AttributeNames.ON_CANCEL.toXmlStyle());
				boolean cancel = StringUtils.isEmpty( onCancel )?false: Boolean.parseBoolean( onCancel );
				String onFinish = attributes.getValue( AttributeNames.ON_FINISH.toXmlStyle());
				boolean finish = StringUtils.isEmpty( onFinish )?false: Boolean.parseBoolean( onFinish );
				wizard.addPage(id, description, message, type, cancel, finish );
				is = wizard.getCurrent();
				if( !StringUtils.isEmpty( urlstr)){
					is.url = urlstr;
				}
				if( !StringUtils.isEmpty( source)){
					if( !StringUtils.isEmpty( urlstr ))
						throw new SAXException( S_ERR_SOURCE + "[ " + urlstr + ", " + source + "]" );
					is.source = source;
				}
				break;
			case CONTAINER:
				type = StringUtils.isEmpty( type_str )? ContainerTypes.HEAD:
					ContainerTypes.valueOf( StringStyler.styleToEnum( type_str ));
				is = wizard.getCurrent();
				is.type = type;
				if( StringUtils.isEmpty( is.titleStyle ))
					is.titleStyle = titleStyle;
				if( StringUtils.isEmpty( is.buttonStyle ))
					is.buttonStyle = buttonStyle;
				is.barOnTop = ToolbarOptions.TOP.equals( tbo );
				break;
			case COMPOSITE:
				is.composite_clss = comp_clss;
				String cstyle = attributes.getValue( AttributeNames.STYLE.toXmlStyle());
				is.composite_style = StringUtils.isEmpty(cstyle)? 0: Integer.parseInt( cstyle);
				is.url = urlstr;
				break;
			case BUTTONS:
				break;
			case BUTTON:
				button = Buttons.valueOf(type_str);
				String visstr = attributes.getValue( AttributeNames.VISIBLE.toXmlStyle());
				boolean visible = StringUtils.isEmpty( visstr )? true: Boolean.parseBoolean( visstr );
				String enstr = attributes.getValue( AttributeNames.ENABLED.toXmlStyle());
				boolean enabled = StringUtils.isEmpty( enstr )? true: Boolean.parseBoolean( enstr );
				is = wizard.getCurrent();
				is.addButtonInfo( button, visible, enabled);
				break;
			default:
				break;
			}
			stored.push(node);
		}

		private Nodes getParent( Nodes child ){
			if( stored.isEmpty() )
				return null;
			int index = stored.indexOf(child );
			return ( index <= 0)? null: stored.get( index-1);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			String componentName = StringStyler.styleToEnum( qName );
			//The name is not a node.
			if( !Nodes.isNode( componentName ))
				return;
			Nodes node = Nodes.valueOf(componentName);
			switch( node ){
			case BUTTON:
				button = null;
				break;
			default:
				break;
			}
			stored.pop();
		}

		@Override
		public void characters(char ch[], int start, int length) throws SAXException {
			String value = new String(ch, start, length);
			if( StringUtils.isEmpty( value ))
				return;
			Nodes current = stored.lastElement();
			switch( current ){
			case PUSH:
				IndexStore is = wizard.getCurrent();
				is.addPushPage( button, value );
				break;
			default:
				break;
			}
		}

		private void print(SAXParseException x)
		{
			Logger.getLogger( this.getClass().getName()).info(x.getMessage());
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			print(e);
			super.error(e);
		}

		@Override
		public void fatalError(SAXParseException arg0) throws SAXException {
			print(arg0);
			super.fatalError(arg0);
		}

		@Override
		public void warning(SAXParseException arg0) throws SAXException {
			print(arg0);
			super.warning(arg0);
		}
	}
}