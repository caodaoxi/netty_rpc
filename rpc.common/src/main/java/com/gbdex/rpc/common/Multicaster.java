package com.gbdex.rpc.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicaster {
	public int port = 5540;

	protected String IPAddress;

	private MulticastSocket msConn;
	private InetAddress netAddr;

	public Multicaster(String IPAddress) {
		this.IPAddress = IPAddress;
	}

	public Multicaster(String IPAddress, int port) {
		this.IPAddress = IPAddress;
		this.port = port;
	}

	/**
	 * 接收广播数据
	 * 
	 * @return
	 */
	public String recieveData() throws IOException {
		byte[] buf = new byte[1000];
		DatagramPacket pack = new DatagramPacket(buf, buf.length);
		msConn.receive(pack);
		return new String(pack.getData());
	}

	/**
	 * 创建连接
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException {
		msConn = new MulticastSocket(port);
		netAddr = InetAddress.getByName(IPAddress);
		msConn.joinGroup(netAddr);
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		if (msConn != null && !msConn.isClosed()) {
			msConn.close();
		}
	}

	/**
	 * 发送广播数据
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void sendData(String data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data.getBytes(),
				data.length(), this.netAddr, this.port);

		this.msConn.send(packet);
	}

	public static void main(String[] args) {
		Multicaster multicast = new Multicaster("239.0.0.0");
		try {
			multicast.connect();
			multicast.sendData("Hi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
