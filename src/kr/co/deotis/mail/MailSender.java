package kr.co.deotis.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import kr.co.deotis.vo.PersonVO;

public class MailSender {
	
	Properties props;
	PersonVO person = new PersonVO();
	
	public MailSender() {
		
		person.setFromEmail("soyeong.baek@deotis.co.kr");
		person.setPassword("robot8040!!");
		person.setToMail("soyeong.baek@deotis.co.kr");
		person.setFromName("백소영");
		
		props = new Properties();
		props.put("mail.smtp.host", "smtp.worksmobile.com");
		props.put("mail.stmp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtps.ssl.checkserveridentity", "true");
		props.put("mail.smtps.ssl.trust", "*");
		props.setProperty("mail.smtp.quitwait", "false");
	}
	
	public void userSendMail(String msg) {
		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(person.getFromEmail(), person.getPassword());
			}
		};
		
		Session session = Session.getDefaultInstance(props, auth);
		
		MimeMessage message = new MimeMessage(session);
		InternetAddress iadr = new InternetAddress();
		
		try {
			iadr.setAddress(person.getFromEmail());
			iadr.setPersonal(person.getFromName());
			message.setFrom(iadr);
			message.setSender(iadr);
			message.setSubject("[smartARS] 에러알림");		// mail 제목
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(person.getToMail(), "백소영"));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		Multipart mp = new MimeMultipart();
		MimeBodyPart mbp = new MimeBodyPart();
		try {
			String[] error = msg.split("`");
			mbp.setText("SERVER IP : "+error[0]+"\nSERVER PORT : "+error[1]+"\nERROR TIME : "+error[2]+"\nSERVER STATUS MSG : "+error[3]);	// mail 본문
			mp.addBodyPart(mbp);
			//attachFile("화면1.jpg", mp);
			
			message.setContent(mp, "text/html;charset=utf-8");
			
			Transport.send(message);
			System.out.println("send message success");
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public void attachFile(String fileName, Multipart mp) {
		
		if(fileName != null) {
			if(fileSizeCheck(fileName)) {
				MimeBodyPart bodyFile = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(fileName);
				
				try {
					bodyFile.setDataHandler(new DataHandler(fds));
					bodyFile.setFileName(MimeUtility.encodeText(fds.getName(), "UTF-8", "B"));
					
					mp.addBodyPart(bodyFile);
				} catch (MessagingException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				try {
					throw new Exception("file size overflow!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected boolean fileSizeCheck(String fileName) {
		
		if (new File(fileName).length() > (1024 * 1024* 50)) {
			return false;
		}
		return true;
	}
}
