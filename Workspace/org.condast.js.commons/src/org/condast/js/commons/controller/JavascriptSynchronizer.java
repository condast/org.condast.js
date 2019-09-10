package org.condast.js.commons.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.condast.js.commons.session.AbstractSessionHandler;
import org.condast.js.commons.session.ISessionListener;
import org.condast.js.commons.session.SessionEvent;
import org.eclipse.swt.widgets.Display;

public class JavascriptSynchronizer<D extends Object>  {

	public static final int DEFAULT_DELAY = 200;
	
	private ScheduledExecutorService service;
	private Handler handler;
	
	private Collection<IJavascriptController> controllers;

	public JavascriptSynchronizer( IJavascriptController controller) {
		this( controller.getBrowser().getDisplay(), DEFAULT_DELAY );
		this.controllers.add(controller);
	}

	public JavascriptSynchronizer( IJavascriptController controller ,long delay) {
		this( controller.getBrowser().getDisplay(), delay );
	}

	public JavascriptSynchronizer( Display display,long delay) {
		controllers = new ArrayList<>();
		service = Executors.newSingleThreadScheduledExecutor();
		handler = new Handler( display );
		service.scheduleWithFixedDelay( handler, 0, delay, TimeUnit.MILLISECONDS);
	}

	public void addController( IJavascriptController controller ) {
		this.controllers.add(controller);
	}

	public void removeController( IJavascriptController controller ) {
		this.controllers.remove(controller);
	}

	public <T extends ISessionListener<D>> void addSessionListener( ISessionListener<D> listener ) {
		this.handler.addSessionListener(listener);
	}

	public void removeSessionListener( ISessionListener<D> listener ) {
		this.handler.removeSessionListener(listener);
	}

	public void dispose() {
		service.shutdown();
		handler.dispose();
	}

	private class Handler extends AbstractSessionHandler<D> implements Runnable{

		protected Handler(Display display) {
			super(display);
		}

		@Override
		public void run() {
			for( IJavascriptController controller: controllers) {
				if( !controller.isEmpty()) {
					addData(null);
					return;
				}
			}		
		}

		@Override
		protected void onHandleSession(SessionEvent<D> sevent) {
			if( sevent == null )
				return;
			for( IJavascriptController controller: controllers)
				controller.synchronize();
		}	
	}
}
