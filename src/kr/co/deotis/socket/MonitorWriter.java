package kr.co.deotis.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorWriter implements Runnable {

	Logger logger = LoggerFactory.getLogger("Main");
	
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
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
