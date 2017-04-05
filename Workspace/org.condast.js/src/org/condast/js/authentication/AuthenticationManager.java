package org.condast.js.authentication;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.condast.commons.authentication.composite.AuthenticationGroup;
import org.condast.commons.authentication.core.AbstractAuthenticationCompositeManager;
import org.condast.commons.authentication.core.AuthenticationEvent;
import org.condast.commons.authentication.core.IAuthenticationListener;
import org.condast.commons.authentication.def.IAuthenticationControl;
import org.condast.js.image.ReactImages;
import org.condast.js.image.ReactImages.Images;
import org.condast.js.store.Preferences;
import org.condast.js.swt.FrontEndComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class AuthenticationManager extends AbstractAuthenticationCompositeManager{

	public static final String S_RWT_SCREENSAVER = "screensaver";

	static final String S_CALLBACK_ID = "FIELDLABRDM";

	private LoginMediator mediator = LoginMediator.getIntance();
	
	private IAuthenticationListener listener = new IAuthenticationListener(){

		@Override
		public void notifyLoginChanged(AuthenticationEvent event) {
			switch( event.getEvent() ){
			case REGISTER:
				break;
			default:
				break;
				
			}
		}
	};

	private Preferences prefs = Preferences.getInstance();
	
	private AuthenticationManager manager;

	public AuthenticationManager() {
		super( S_CALLBACK_ID );
		manager = this;
	}

	@Override
	protected boolean onCheckLoggedin() {
		boolean loggedin = false;
		try{
			//Prevent renewing if a refresh button  is pushed
			loggedin = prefs.isLoggedin();
			if( loggedin )
				return loggedin;
			//Config config = new Config();
			//loggedin = true;// !config.isProduction();
			
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return true;
	}

	
	@Override
	protected IAuthenticationControl createAuthenticationControl(Composite parent) {
		Composite screensaver = new Composite( parent, SWT.NONE );
		screensaver.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		screensaver.setLayout( new GridLayout( 1, false ));
		//screensaver.setData( RWT.CUSTOM_VARIANT, S_RWT_SCREENSAVER );
		
		screensaver.setBackgroundImage( ReactImages.getScreenSaver(parent));

		AuthenticationGroup composite =  (AuthenticationGroup) new AuthenticationGroup(screensaver, SWT.NONE);
		composite.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, true ));
		composite.setText("Login for FieldLab RDM");
		composite.setImage(ReactImages.getImage( Images.AQUABOT ));
		composite.addListener(listener);
		return composite;
	}

	@Override
	protected Composite createEntryComposite(Composite parent) {
		FrontEndComposite comp = new FrontEndComposite( parent, SWT.NONE );
		comp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true));
		return comp;
	}

	@Override
	protected void onHandleLoginStart(Subject arg0) {
		mediator.add( super.getLoginControl());
	}

	@Override
	protected void onHandleLoginFinished(Subject arg0, LoginException arg1) {
		mediator.remove(super.getLoginControl());
	}

	@Override
	protected void onHandleLoginFailed(Subject arg0, LoginException arg1) {
		Display.getDefault().asyncExec( new Runnable(){

			@Override
			public void run() {
				ReactLoginModule module = new ReactLoginModule();
				try {
					module.login( new ReaxctCallbackHandler());
					
					if( !prefs.isLoggedin() )
						return;
					AuthenticationManager.clearParent( getParent());
					manager.createEntryComposite( getParent());
				} catch (LoginException e) {
					e.printStackTrace();
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}		
		});	
	}
}
