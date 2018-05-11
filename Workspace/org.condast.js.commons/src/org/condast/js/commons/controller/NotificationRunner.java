package org.condast.js.commons.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;

public class NotificationRunner<D extends Object> {

	private Collection<IEvaluationListener<D>> listeners;

	private Collection<RunEvent> events;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private ExecutorService service;
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if( lock || events.isEmpty()) {
				return;
			}
			RunEvent re = events.iterator().next();
			boolean process = re.process();
			lock |= process;
			if( !process)
				events.remove(re);	
		}	
	};
	
	private boolean lock;

	public NotificationRunner( Collection<IEvaluationListener<D>> listeners) {
		this.listeners = listeners;
		this.events = new ArrayList<>();
		this.lock = false;
		this.service = Executors.newCachedThreadPool();
		this.service.execute(runnable);
	}
	
	public void addEvent( EvaluationEvent<D> event ) {
		if( this.listeners.isEmpty())
			return;
		this.events.add(new RunEvent( event, this.listeners ));
	}
	
	public void unlock() {
		this.lock = false;
	}

	public void shutdown() {
		this.lock = false;
		this.service.shutdown();
	}
	
	private class RunEvent{

		private EvaluationEvent<D> event;
		private List<IEvaluationListener<D>> listeners;
		
		private RunEvent(EvaluationEvent<D> event, Collection<IEvaluationListener<D>> listeners) {
			super();
			this.event = event;
			this.listeners = new ArrayList<>( listeners );
		}
				
		public boolean process(){
			if( this.listeners.isEmpty())
				return false;
			IEvaluationListener<D> listener =this.listeners.remove(0);
			listener.notifyEvaluation(event);
			logger.info( "EVENT PROCESSED: " + event.getEvaluationEvent());
			return true;
		}
	}
}
