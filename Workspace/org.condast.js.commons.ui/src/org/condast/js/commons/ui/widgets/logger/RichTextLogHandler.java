package org.condast.js.commons.ui.widgets.logger;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.condast.commons.log.BufferedLogHandler;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;

public class RichTextLogHandler extends BufferedLogHandler{

	private RichTextEditor eventLogger;

	private static RichTextLogHandler handler = null;

	private RichTextLogHandler( String name ) {
		this( name, Level.SEVERE, 1 );
	}

	private RichTextLogHandler( String name, Level baseLevel, int offset ) {
		super( name, baseLevel.intValue() + offset );
	}

	public static RichTextLogHandler getInstance( String name, Level baseLevel, int offset ){
		if( handler == null )
			handler = new RichTextLogHandler(name, baseLevel, offset);
		return handler;
	}

	public static RichTextLogHandler getInstance(){
		return handler;
	}

	public void init(){}

	public void setEventLogger(RichTextEditor eventLogger) {
		this.eventLogger = eventLogger;
		flush();
	}

	@Override
	public void publish( final LogRecord logRecord) {
		super.publish(logRecord);
		this.flush();
	}

	@Override
	public void flush() {
		if(( this.eventLogger == null ) || ( this.eventLogger.isDisposed()))
			return;
		eventLogger.getDisplay().asyncExec( new Runnable(){

			@Override
			public void run() {
				eventLogger.setText( flush( true ));
			}
		});

		super.flush();
	}


}
