package org.condast.js.commons.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.condast.js.commons.session.AbstractSessionHandler;
import org.condast.js.commons.session.ISessionListener;
import org.condast.js.commons.session.SessionEvent;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractSynchroniser<E extends Object> implements ISynchroniser {

	private Map<IJavascriptController, Boolean> controllers;
	
	private SessionHandler handler;
	
	private Map<Class<?>, AbstractListener<?,E>> listeners;
	
	private boolean block;
	
	public AbstractSynchroniser( Display display) {
		controllers = new HashMap<>();
		this.block = false;
		this.listeners = new HashMap<>();
		handler = new SessionHandler( display );
	}
	
	@SuppressWarnings("unchecked")
	protected void addListener( @SuppressWarnings("rawtypes") AbstractListener listener){
		this.listeners.put(listener.getClass(), listener);
	}

	@SuppressWarnings("rawtypes")
	protected void removeListener( AbstractListener listener){
		this.listeners.remove(listener.getClass());
	}
	
	@SuppressWarnings("rawtypes")
	protected AbstractListener getListener( Class<?> clss ) {
		return this.listeners.get(clss);
	}

	/**
	 * Registers the participating controllers. If clear is true, then the
	 * pending commands are cleared if the controller's browser is not visible
	 * @param controller
	 * @param clear
	 */
	@Override
	public void registercontroller( IJavascriptController controller, boolean clear ) {
		this.controllers.put(controller, clear);
	}

	@Override
	public void unregistercontroller( IJavascriptController controller ) {
		this.controllers.remove(controller);
	}

	@Override
	public void dispose() {
		for( AbstractListener<?, E> listener: this.listeners.values() )
			listener.clear();
		handler.dispose();
		controllers.clear();
	}
	
	public synchronized void addData( E data ) {
		this.handler.addData(data);
	}
	
	protected abstract void onHandleSessionEvent(SessionEvent<E> event);
	

	private class SessionHandler extends AbstractSessionHandler<E> {

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected synchronized void onHandleSession(SessionEvent<E> event) {
			try {
				onHandleSessionEvent(event);
				if( ISessionListener.EventTypes.COMPLETED.equals(event.getType())) {
					synchronize();		
				}	
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}

		/**
		 * Synchronises the registered controllers. 
		 * NOTE: only one browser should be visible at at a certain time
		 */
		protected synchronized boolean synchronize() {
			if( block )
				return false;
			//block = true;
			boolean result = false;
			Iterator<Map.Entry<IJavascriptController,Boolean>> iterator = controllers.entrySet().iterator();
			try{
				while( iterator.hasNext() ) {
					Map.Entry<IJavascriptController,Boolean> entry = iterator.next();
					IJavascriptController controller = entry.getKey();
					if( controller.isDisposed())
						continue;

					if( controller.isBrowserVisible() ) {
						if( controller.isInitialised())
							controller.synchronize();
					}else {
						if( entry.getValue())
							controller.clear();
					}
				}
				result = true;
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
			finally {
				block = false;
			}
			return result;
		}

	
	}

	/**
	 * This helper class can be used to quickly add listeners 
	 * 
	 * @author Kees
	 *
	 * @param <D>
	 * @param <L>
	 */
	public abstract static class AbstractListener<L,E extends Object>{

		private Collection<L> listeners;
		
		protected AbstractListener() {
			listeners = new ArrayList<>(); 
		}

		public void clear() {
			this.listeners.clear();
		}
		
		public void addListener( L listener ) {
			this.listeners.add(listener);
		}

		public void removeListener( L listener ) {
			this.listeners.remove(listener);
		}
		
		protected abstract void notifyEvent( L listener, E event );
		
		public void notifyListeners( E event ) {
			for( L listener: listeners )
				notifyEvent( listener, event);
		}
	}
}
