package kr.co.deotis.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MonitorWriter implements Runnable {

	SocketChannel writech;
	ByteBuffer wbuffer;
	String packet = "003101029588040             2M    3";
	
	public MonitorWriter(SocketChannel writech) {
		this.writech = writech; 
	}
	
	@Override
	public void run() {
		
		while(!Thread.currentThread().isInterrupted()) {
			
			wbuffer = ByteBuffer.allocateDirect(1024);
			wbuffer.clear();
			wbuffer = ByteBuffer.wrap(packet.getBytes());
			try {
				int writeLen = writech.write(wbuffer);
				System.out.println(writeLen);
				Thread.sleep(5000);
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
