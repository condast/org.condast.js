package org.condast.js.authentication;

import org.condast.commons.authentication.def.IAuthenticationControl;
import org.condast.commons.mediator.AbstractMediator;

public class LoginMediator extends AbstractMediator<IAuthenticationControl>{

	private static LoginMediator mediator =  new LoginMediator();
	
	public static LoginMediator getIntance(){
		return mediator;
	}
}
