package kr.co.deotis.socket;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class MonitorClient extends Thread {
	
	public static MonitorClient mc = null;
	String ip = null;
	int port = 0;
	Socket sock = null;
	OutputStream os;
	BufferedWriter bw;
	InputStream is;
	BufferedReader br;
	
	MonitorWriter mw;
	InetSocketAddress isa = null;
	Selector sel = null;
	SocketChannel writech;
	SocketChannel readch;
	
	public MonitorClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
		try {
			sel = Selector.open();
			isa = new InetSocketAddress(ip, port);
			writech = SocketChannel.open(isa);
			writech.configureBlocking(false);
			writech.register(sel, SelectionKey.OP_READ);
			
			mw = new MonitorWriter(writech);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		Thread tw = new Thread(mw);
		tw.start();
		readStart();
	}
	
	public void readStart() {
		while(writech.isConnected()) {
			try {
				sel.select();
				Iterator<SelectionKey> iter = sel.selectedKeys().iterator();
				
				while(iter.hasNext()) {
					SelectionKey key = iter.next();
					if(key.isReadable()) {
						read(key);
					}
					iter.remove();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void read(SelectionKey key) {
		
		readch = (SocketChannel)key.channel();
		ByteBuffer rbuffer = ByteBuffer.allocateDirect(2048);
		byte[] byteArr;
		
		rbuffer.position(0);
		try {
			int readByte = readch.read(rbuffer);
			rbuffer.flip();
			byteArr = new byte[rbuffer.limit()];
			rbuffer.get(byteArr);
			
			String revPacket = new String(byteArr);
			System.out.println(readByte+", "+revPacket);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void writeData() throws IOException {
		os = sock.getOutputStream();
		bw = new BufferedWriter(new OutputStreamWriter(os));
		System.out.println("BufferedWriter open.");
		bw.write("003101029588040             2M    3");
		System.out.println("data buffer save.");
		bw.newLine();
		bw.flush();
		System.out.println("data write success.");
		
	}
	
	public void readData() throws IOException {
		is = sock.getInputStream();
		br = new BufferedReader(new InputStreamReader(is));
		System.out.println("BufferedReader open.");
		String receive = br.readLine();
		System.out.println("data read success.");
		System.out.println("받은 데이터 : "+receive);
	}
	
	
	/*public static void main(String[] args) {
		
		try {
			mc.sock = new Socket(mc.ip, mc.port);
			OutputStream os = mc.sock.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	public void makePacket() {
		
	}
}
