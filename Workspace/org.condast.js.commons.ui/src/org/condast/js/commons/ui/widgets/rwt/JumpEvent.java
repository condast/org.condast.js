package org.condast.js.commons.ui.widgets.rwt;

import java.util.EventObject;

public class JumpEvent<D extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	private long token;
	
	//An identifier for the source
	private String identifier;
	
	private String path;
	
	private JumpController.Operations operation;
	
	private D data;

	public JumpEvent(Object source, long token, String destinationPath, JumpController.Operations operation, D data) {
		this( source, null, token, destinationPath, operation, data );
	}
	
	public JumpEvent(Object source, String identifier, long token, String destinationPath, JumpController.Operations operation, D data) {
		super(source);
		this.identifier = identifier;
		this.token = token;
		this.path = destinationPath;
		this.operation = operation;
		this.data = data;
	}

	public long getToken() {
		return token;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getDestinationPath() {
		return path;
	}

	public JumpController.Operations getOperation() {
		return operation;
	}

	public D getData() {
		return data;
	}
}
