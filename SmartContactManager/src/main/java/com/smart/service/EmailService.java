package com.smart.service;

import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to) {
		boolean f = false;

		String from = "raj@1234";

		String host = "smtp.gmail.com";

		// get the system properies

		Properties properties = System.getProperties();
		System.out.println("PROPERTIES" + properties);

		// setting information to properties object

		// host set
		

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port","465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step 1 to get Session Object.

		Session session = Session.getInstance(properties, new Authenticator() {

			
			//security/two step veerification/app password set app name and copy password paset here
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("bharatpote5@gmail.com", "xpww pztc bsgs bxed");

			}
		});

		session.setDebug(true);

		// step2 complete the Message [text, multi media]
		MimeMessage m = new MimeMessage(session);

		try {
			m.setFrom(from);

			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			m.setSubject(subject);

			m.setText(message);
			//m.setContent("message","text/html");

			Transport.send(m);
			System.out.println("Send Success....");
			f = false;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return f;

	}
}
