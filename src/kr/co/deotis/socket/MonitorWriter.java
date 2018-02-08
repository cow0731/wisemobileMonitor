package kr.co.deotis.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.deotis.mail.MailSender;

public class MonitorWriter implements Runnable {

	Logger logger = LoggerFactory.getLogger("Main");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss");
	MailSender mail = new MailSender();
	
	int cnt = 0;
	int sec = 0;
	SocketChannel writech;
	MonitorWebsocket wmsocket;
	ByteBuffer wbuffer;
	String packet = "00310000                    2M    3";
	
	public MonitorWriter(SocketChannel writech, int sec, MonitorWebsocket wmsocket) {
		this.writech = writech; 
		this.sec = sec;
		this.wmsocket = wmsocket;
	}
	
	@Override
	public void run() {
		
		while(!Thread.currentThread().isInterrupted() && wmsocket.isMService()) {
			System.out.println("write 부분");
			wbuffer = ByteBuffer.allocateDirect(1024);
			wbuffer.clear();
			wbuffer = ByteBuffer.wrap(packet.getBytes());
			try {
				int writeOk = writech.write(wbuffer);
				if(writeOk > 0) {
					logger.debug("[{}]", packet);
				}
				Thread.sleep(sec);
				
			} catch (IOException e) {
				cnt++;
				String dateTime = sdf.format(new Date());
				e.printStackTrace();
				logger.debug("socket writeChannel disconnect", e);
				wmsocket.sendToWeb(dateTime+"`writeChannel Error.");
				if(cnt == 1) {
					mail.userSendMail(writech.socket().getRemoteSocketAddress()+"`"+writech.socket().getPort()+"`"+dateTime+"`"+"socket write error");
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
