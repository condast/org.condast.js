package org.condast.js.commons.ui.widgets.rwt;

import org.condast.commons.messaging.core.util.NodeData;

public class NodeJumpEvent<P,C extends Object> extends JumpEvent<NodeData<P,C>> {
	private static final long serialVersionUID = 1L;

	public NodeJumpEvent(Object source, long token, String path, JumpController.Operations operation, P data) {
		this(source, token, path, operation, data, null );
	}

	public NodeJumpEvent(Object source, long token, String destination, JumpController.Operations operation, P data, C child) {
		super(source, null, token, destination, operation, new NodeData<>( data, child ));
	}
	
	public NodeJumpEvent(Object source, String identifier, long token, String path, JumpController.Operations operation, P data) {
		this(source, identifier, token, path, operation, data, null );
	}

	public NodeJumpEvent(Object source, String identifier, long token, String destination, JumpController.Operations operation, P data, C child) {
		super(source, identifier, token, destination, operation, new NodeData<>( data, child ));
	}
	
	public P getParent() {
		return super.getData().getData();
	}

	public C getChild() {
		return super.getData().getChild();
	}
}
