package hu.barbar.util;

import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import hu.barbar.tasker.util.Config;
import hu.barbar.tasker.util.Env;
import hu.barbar.util.logger.Log;

public class Mailer {
	
	private static HashMap<String, String> mailConfig = null;
	private static boolean configurationAvailable = false;
	
	public static boolean readConfig(){
		String mailConfigFile = Env.getDataFolderPath() + Config.FILENAME_MAIL_CONFIG;
		mailConfig = FileHandler.readConfig(mailConfigFile);
		
		if(mailConfig == null){
			Log.w("Mailer can not initialized!\n" 
				 +"Missing mail config: (" + mailConfigFile + ")");
			configurationAvailable = false;
			return configurationAvailable;
		}
		
		if(mailConfig.get(Config.KEY_MAIL_SENDER_ACCOUNT) == null){
			Log.w("Mailer can not initialized!\n" 
					 +"Email account missing.\n");
			configurationAvailable = false;
			return configurationAvailable;
		}
		
		if(mailConfig.get(Config.KEY_MAIL_SENDER_P) == null){
			Log.w("Mailer can not initialized!\n" 
					 +"Email pw missing.\n");
			configurationAvailable = false;
			return configurationAvailable;
		}
			
		configurationAvailable = true;
		
		return configurationAvailable;
	}

	public static boolean sendEmail(String to, String subject, String text){

		if(configurationAvailable){
			
			/*
			 * Remove "."-s from first part of gmail based email addresses
			 */
			String recepiant = to;
			String[] recipiantMailParts = to.split("@");
			try{
				if(recipiantMailParts[1].equalsIgnoreCase("gmail.com")){
					String recepiantUser = recipiantMailParts[0].replace(".","");
					recepiant = recepiantUser + "@gmail.com";
				}
			}catch(Exception doesNotMatterException){}
			
			
			Properties props = new Properties();
		    props.put("mail.smtp.host", "smtp.gmail.com");
		    props.put("mail.smtp.socketFactory.port", "465");
		    props.put("mail.smtp.socketFactory.class",
		            "javax.net.ssl.SSLSocketFactory");
		    props.put("mail.smtp.auth", "true");
		    props.put("mail.smtp.port", "465"); 
		    Session session = Session.getDefaultInstance(props,
		        new javax.mail.Authenticator() {
		                            @Override
		            protected PasswordAuthentication getPasswordAuthentication() {
		                return new PasswordAuthentication(
		                			mailConfig.get(Config.KEY_MAIL_SENDER_ACCOUNT),
		                			mailConfig.get(Config.KEY_MAIL_SENDER_P));
		            }
		        });
	
		    try {
	
		        Message message = new MimeMessage(session);
		        message.setFrom(new InternetAddress(mailConfig.get(Config.KEY_MAIL_SENDER_ACCOUNT))); //frommail@gmail.com
		        message.setRecipients(Message.RecipientType.TO,
		                InternetAddress.parse(recepiant)); //tomail@gmail.com
		        //message.setSubject("Testing Subject");
		        message.setSubject(subject);
		        //message.setText("Test Mail");
		        message.setText(text);
	
		        Transport.send(message);
	
		        //System.out.println("Done");
		        return true;
	
		    } catch (MessagingException e) {
		        //throw new RuntimeException(e);
		    	return false;
		    }
		}
		return false;
	}
	
}
