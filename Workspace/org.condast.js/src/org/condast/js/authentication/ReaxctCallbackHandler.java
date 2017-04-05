package org.condast.js.authentication;

import java.io.IOException;

import javax.security.auth.callback.Callback;

import org.condast.commons.authentication.callback.AbstractCallbackHandler;
import org.condast.commons.authentication.def.IAuthenticationControl;

/**
 * Handles the callbacks to show a RCP/RAP UI for the LoginModule.
 */
public class ReaxctCallbackHandler extends AbstractCallbackHandler {

	LoginMediator mediator = LoginMediator.getIntance();
	
	/**
	 * Invoke an array of Callbacks.
	 */
	@Override
	public void onHandleCallbacks(Callback[] callbacks)
			throws IOException {
		this.createCallbackHandlers( (IAuthenticationControl) mediator.getLast() );
	}
}