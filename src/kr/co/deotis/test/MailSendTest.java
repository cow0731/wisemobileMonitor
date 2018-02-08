package kr.co.deotis.test;

import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import kr.co.deotis.mail.MailSender;

@RunWith(MockitoJUnitRunner.class)
public class MailSendTest {
	
	@Test
	public void mailtest() {
		MailSender ms = mock(MailSender.class);
		ms.userSendMail("error");
		
		verify(ms, times(1)).userSendMail("error");
		
		MailSender sender = new MailSender();
		sender.userSendMail("192.168.0.53`40011`2018:02:02-17:18:47`[127.0.0.1] 모바일 서버 에러");
	}
	
	@Test
	public void todaytest() {
		String time = "1517559446369";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss");
		String errorTime = sdf.format(new Date(Long.parseLong(time)));
		System.out.println(errorTime);
	}
}
