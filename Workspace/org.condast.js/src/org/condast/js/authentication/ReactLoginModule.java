package org.condast.js.authentication;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.condast.commons.Utils;

/*
 * Copyright (c) 2000, 2002, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * -Redistributions of source code must retain the above copyright
 * notice, this  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduct the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Oracle nor the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES  SUFFERED BY LICENSEE AS A RESULT OF  OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

import org.condast.commons.authentication.module.AbstractLoginModule;
import org.condast.js.store.Preferences;

/**
 * <p> This sample LoginModule authenticates users with a password.
 *
 * <p> This LoginModule only recognizes one user:       testUser
 * <p> testUser's password is:  testPassword
 *
 * <p> If testUser successfully authenticates itself,
 * a <code>SamplePrincipal</code> with the testUser's user name
 * is added to the Subject.
 *
 * <p> This LoginModule recognizes the debug option.
 * If set to true in the login Configuration,
 * debug messages will be output to the output stream, System.out.
 *
 */
public class ReactLoginModule extends AbstractLoginModule {

	private static final String S_DEF_USERNAME = "FieldLab RDM";

	private static final String S_FIELDLAB_MODULE = "[FieldLabLoginModule]";
	private static final String S_AUTH_CONFIG = "/data/authentication.auth";

	private Preferences prefs = Preferences.getInstance();

	public ReactLoginModule() {
		super( S_FIELDLAB_MODULE, S_DEF_USERNAME);
	}

	@Override
	protected boolean verifyUsernameAndPassword(String userNameEntry, char[] passwordEntry) throws LoginException{
		if( Utils.assertNull( userNameEntry) || ( passwordEntry.length == 0 ))
			return false;
		Scanner scanner = new Scanner( this.getClass().getResourceAsStream(S_AUTH_CONFIG));
		try{
			while( scanner.hasNext() ){
				String str = scanner.nextLine();
				if( str.startsWith("#") || str.startsWith("/"))
					continue;
				str = str.replaceAll(";","");
				String[] split = str.split("[:]");
				if( split.length != 2 )
					continue;
				String password = String.valueOf(passwordEntry);
				if( split[0].equals(userNameEntry) && split[1].equals(password )){
					prefs.setROrganisation( userNameEntry );
					prefs.setLoggedIn(true);
					return true;
				}
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			throw new LoginException( ex.getMessage() );
		}
		finally{
			scanner.close();
		}
		return false;
	}
}