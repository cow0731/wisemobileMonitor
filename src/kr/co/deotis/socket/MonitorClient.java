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


public class MonitorClient extends Thread {
	
	public static MonitorClient mc = null;
	String ip = null;
	int port = 0;
	Socket sock = null;
	OutputStream os;
	BufferedWriter bw;
	InputStream is;
	BufferedReader br;
	
	InetSocketAddress isa = null;
	
	public MonitorClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			sock = new Socket(ip, port);
			writeData();
			readData();
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
