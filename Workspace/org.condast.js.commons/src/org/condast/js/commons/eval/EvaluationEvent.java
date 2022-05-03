package org.condast.js.commons.eval;

import java.util.EventObject;

import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.controller.Command;

public class EvaluationEvent<T extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	private IEvaluationListener.EvaluationEvents ee;
	
	private Command command;
	
	private String id;
	private T[] data;

	public EvaluationEvent( Object arg0, String id, IEvaluationListener.EvaluationEvents ee ) {
		this( arg0, null, id, ee, null );
	}
	

	public EvaluationEvent( Object arg0, Command command, String id, IEvaluationListener.EvaluationEvents ee ) {
		this( arg0, command, id, ee, null );
	}

	public EvaluationEvent( Object arg0,  String id, IEvaluationListener.EvaluationEvents ee, T[] data ) {
		this( arg0, null, id, ee, data );
	}
	
	public EvaluationEvent( Object arg0, Command command, String id, IEvaluationListener.EvaluationEvents ee, T[] data ) {
		super(arg0);
		this.id = id;
		this.command = command;
		this.ee = ee;
		this.data = data;
	}

	
	public Command getCommand() {
		return command;
	}

	public String getId() {
		return id;
	}

	public T[] getData() {
		return data;
	}

	public IEvaluationListener.EventTypes getEventType() {
		String str = StringStyler.styleToEnum(data[0].toString());
		if( !IEvaluationListener.EventTypes.isValid(str))
			return IEvaluationListener.EventTypes.UNKNOWN;
		return IEvaluationListener.EventTypes.valueOf(str);
	}
	public IEvaluationListener.EvaluationEvents getEvaluationEvent() {
		return ee;
	}
}
