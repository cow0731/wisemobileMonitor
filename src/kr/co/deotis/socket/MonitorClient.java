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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MonitorClient extends Thread {
	
	Logger logger = LoggerFactory.getLogger("Main");
	ConcurrentMap<String, String> stateCode = new ConcurrentHashMap<>();
	ConcurrentMap<String, String> stateHangle = new ConcurrentHashMap<>();
	
	public static MonitorClient mc = null;
	private MonitorWebsocket mwsocket = null;
	String ip = null;
	int port = 0;
	int sec = 0;
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
	
	public MonitorClient(String ip, int port, int sec, MonitorWebsocket websocket, boolean mSvc) {
		this.ip = ip;
		this.port = port;
		this.sec = sec;
		this.mwsocket = websocket;
		try {
			sel = Selector.open();
			isa = new InetSocketAddress(ip, port);
			writech = SocketChannel.open(isa);
			writech.configureBlocking(false);
			writech.register(sel, SelectionKey.OP_READ);
			
			mw = new MonitorWriter(writech, sec, mwsocket);
			stateHangle.put("000", "정상");
			stateHangle.put("001", "DB 접속 안됨");
			stateHangle.put("002", "https프로세스 장애 상황");
			stateHangle.put("003", "smartARS프로세스 장애 상황");
			stateHangle.put("004", "UpdateServer 장애 상황");
			
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
				
				while(iter.hasNext() && mwsocket.isMService()) {
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
			@SuppressWarnings("unused")
			int readByte = readch.read(rbuffer);
			rbuffer.flip();
			byteArr = new byte[rbuffer.limit()];
			rbuffer.get(byteArr);
			
			String revPacket = new String(byteArr);
			logger.debug("[{}]", revPacket);
			analysisPacket(revPacket);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void analysisPacket(String packet) {
		int headInt = Integer.parseInt(packet.substring(0, 4));
		int bodyLen = packet.substring(4).length();
		
		if(headInt == bodyLen) {
			logger.debug("packet recieve success");
			@SuppressWarnings("unused")
			int statelen = Integer.parseInt(packet.substring(34, 35));
			String data = packet.substring(35);
			String[] detailData = data.split("`");
			
			for (int i = 0; i < detailData.length; i++) {
				String[] keyValue = detailData[i].split(";");
				String key = keyValue[0];
				String value = keyValue[1];
				stateCode.put(key, value);
			}
			
			String time = stateCode.get("TIME");
			String scode = stateCode.get("SCODE");
			String msg = null;
			if(detailData.length == 3) {
				msg = stateCode.get("MSG");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd-hh:mm:ss");
			String dateTime = sdf.format(new Date(Long.parseLong(time)));
			logger.debug("Time : {}", dateTime);
			logger.debug("Scode : {}", scode);
			logger.debug("Msg : {}", msg);
			
			mwsocket.setTIME(dateTime);
			mwsocket.setSCODE(scode);
			if(Integer.parseInt(scode) >= 1) {
				mwsocket.setMSG(msg);
			}
			String sendPacket = "";
			sendPacket += dateTime+"`"+stateHangle.get(scode);
			if(detailData.length == 3) {
				sendPacket += "`"+msg;
			}
			mwsocket.sendToWeb(sendPacket);
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

}
