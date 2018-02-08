package kr.co.deotis.vo;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

import kr.co.deotis.socket.MonitorWebsocket;

public class WebSocketQueue {
	//private LinkedList<SocketChannel> m_listQue;
	private Queue<Object> m_listQue = new LinkedList<Object>();
	//private Selector m_readSel;

	public WebSocketQueue() {
		// TODO Auto-generated constructor stub
	}

	/*public void setReadSelector(Selector readSelector) {
		this.m_readSel = readSelector;
	}*/

	public void addSocketList(MonitorWebsocket socket) {
		synchronized (this) {
			this.m_listQue.add(socket);
		}
		//this.m_readSel.wakeup();
	}

	public MonitorWebsocket getSocketList() {
		synchronized (this) {
			if (this.m_listQue.peek() == null)
				return null;
			
			return (MonitorWebsocket) this.m_listQue.poll();
//			if (this.m_listQue.size() == 0)
//				return null;
//
//			return this.m_listQue.removeFirst();
			
		}
	}

	//20121213 syakles
	public int getSocketListSize()
	{
		synchronized (this) {
			return m_listQue.size();
		}
	}
}
