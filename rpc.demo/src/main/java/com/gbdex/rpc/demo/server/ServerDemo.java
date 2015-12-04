package com.gbdex.rpc.demo.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gbdex.rpc.demo.service.HellowordImpl;
import com.gbdex.rpc.protocol.transport.MessagePactTransport;
import com.gbdex.rpc.protocol.transport.ProtostuffTransport;
import com.gbdex.rpc.server.impl.RPCServerImpl;
import io.netty.util.ResourceLeakDetector;

/**
 * Hello world!
 *
 */
public class ServerDemo {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		ExecutorService ex = Executors.newFixedThreadPool(1);
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
		ex.execute(new Runnable() {
			public void run() {
				RPCServerImpl impl = new RPCServerImpl("127.0.0.1", 10081,new ProtostuffTransport());
				impl.registerMBean(new HellowordImpl());
				try {
					impl.afterPropertiesSet();
					impl.doStart();
					System.out.println("ok");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
