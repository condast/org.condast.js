package org.javax.mail.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.parser.AbstractResourceParser;
import org.condast.commons.preferences.config.Config;
import org.condast.commons.strings.StringStyler;

/**
 * @See: https://www.baeldung.com/java-email
 * @author info
 *
 */
public class MailUtils {

	public static final String S_RESOURCE_CONFIRM = "/resources/confirmation.txt";
	public static final String S_RESOURCE_CONFIRM_CODE = "/resources/confirmcode.txt";
	public static final String S_RESOURCE_FORGOT_PASSWORD_CODE = "/resources/forgot.txt";

	public static final String S_PLEASE_CONFIRM_TITLE = "Please Confirm your Registration:";
	public static final String S_PLEASE_CONFIRM_LOGIN_TITLE = "Please Confirm your Login:";
	public static final String S_PASSWORD_RECOVERY_TITLE = "Password Recovery:";

	public static final String S_DEFAULT_MAIL_RESOURCE = "/resources/mail.properties";

	public enum MailProperties{
		AUTH,
		STARTTLS,
		HOST,
		PORT,
		SSL_TRUST,
		EMAIL_HOST,
		EMAIL_PASSWORD;

		@Override
		public String toString() {
			String str = "mail.smtp.";
			switch( this ) {
			case SSL_TRUST:
				str += "ssl.trust"; 
				break;
			case STARTTLS:
				str += "starttls.enable"; 
				break;
			case EMAIL_HOST:
			case EMAIL_PASSWORD:
				str = name().toLowerCase();
				str = str.replace("_", ".");
				break;
			default:
				String lower = name().toLowerCase();
				str += lower;
				break;
			}
			return str;
		}
		
		public static MailProperties getProperty( String str ) {
			String check = str.trim().toLowerCase();
			for( MailProperties mp: values()) {
				if( mp.toString().equals(check))
					return mp;
			}
			return null;
		}
	}
	
	private enum Parameters{
		NAME,
		CONFIRMATION,
		CONFIRM_CODE,
		FORGOTTEN,
		DOMAIN;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
		
		public static Parameters getParameter( String str ){
			return Parameters.valueOf( StringStyler.styleToEnum(str));
		}
	}

	public MailUtils() {
		super();
	}

	public static void sendConfirmationdMail( InputStream in, Properties properties, LoginData login, String recipient, String domain, long confirmation ) throws Exception {
		String msg = createConfirmationMail( in, login, domain, confirmation);
		sendMail( properties, recipient, S_PLEASE_CONFIRM_TITLE, msg);
	}

	public static void sendConfirmCodeMail( InputStream in, Properties props, ILoginUser user, String domain ) throws Exception {
		String msg = createConfirmCodeMail( in, user, domain);
		sendMail( props, user.getEmail(), S_PLEASE_CONFIRM_LOGIN_TITLE, msg);
	}

	public static void sendForgotPasswordMail( InputStream in, Properties props, ILoginUser user, String domain ) throws Exception {
		String msg = createForgotPasswordMail( in, user, domain);
		sendMail(props, user.getEmail(), S_PASSWORD_RECOVERY_TITLE, msg);
	}

	public static Properties createProperties( InputStream in ) {
		Properties props = new Properties();
		Scanner scanner = new Scanner(in );
		try {
			while( scanner.hasNextLine() ) {
				String line = scanner.nextLine();
				String[] split = line.split("[:]");
				MailProperties mp = MailProperties.getProperty(split[0]);
				if( mp == null )
					continue;
				switch( mp ) {
				default:
					props.put(split[0].trim(), split[1].trim());
					break;
				}
			}
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		finally {
			scanner.close();
		}
		return props;
	}

	public static void sendMail( Properties props, String recipient, String subject, String msg ) throws AddressException, MessagingException {		
		Thread.currentThread().setContextClassLoader( MailUtils.class.getClassLoader() );
		String host = props.getProperty(MailProperties.EMAIL_HOST.toString());
		Session session = Session.getInstance(props, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(host, props.getProperty( MailProperties.EMAIL_PASSWORD.toString()));
		    }
		});
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress( host ));
		message.setRecipients(
		  Message.RecipientType.TO, InternetAddress.parse( recipient ));
		message.setSubject( subject );

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		message.setContent(multipart);
		Transport.send(message);
	}
	
	public static String createConfirmationMail( InputStream inp, LoginData login, String domain, long confirmation ) throws IOException {
		FileParser parser = new FileParser( login, domain, confirmation);
		return parser.parse( inp );
	}

	public static String createConfirmCodeMail( InputStream inp, ILoginUser user, String domain ) throws IOException {
		FileParser parser = new FileParser( new LoginData( user ), domain, user.getSecurity());
		return parser.parse( inp );
	}

	public static String createForgotPasswordMail( InputStream inp, ILoginUser user, String domain ) throws IOException {
		FileParser parser = new FileParser( new LoginData( user ), domain, user.getSecurity());
		return parser.parse( inp);
	}

	private static class FileParser extends AbstractResourceParser{

		private long confirmation;
		private LoginData login;
		private String domain;
				
		public FileParser(LoginData login, String domain, long confirmation) {
			super();
			this.confirmation = confirmation;
			this.login = login;
			this.domain = domain;
		}

		@Override
		protected String onAppendEOL() {
			return "<BR>";
		}

		@Override
		protected String onHandleTitle(String subject, Attributes attr) {
			String result = null;
			switch( attr ){
			case HTML:
				result = "Condast Mail";
				break;
			case PAGE:
				result = "Condast Mail";
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			String result = null;
			Config config = Config.getInstance();
			String path = config.getServerContext() + domain;
			switch( Parameters.getParameter(id)) {
			case NAME:
				result = login.getNickName();
				break;
			case CONFIRMATION:
				result = "<a href=" + path + "/auth/confirm-registration?confirm=" + this.confirmation + ">confirm</a>";
				break;
			case CONFIRM_CODE:
				result = "<a href=" + path + "/auth/confirm?confirm=" + this.confirmation + ">confirm login</a>";
				break;
			case FORGOTTEN:
				result = "<a href=" + path + "/restore-password?confirm=" + this.confirmation + ">restore password</a>";
				break;
			case DOMAIN:
				result = domain;
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onCreateLink(String link, String url, String arguments) {
			return null;
		}

		@Override
		protected String getToken() {
			return String.valueOf(-1);
		}		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
