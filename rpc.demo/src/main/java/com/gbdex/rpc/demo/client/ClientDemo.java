package com.gbdex.rpc.demo.client;

import java.io.IOException;

import com.gbdex.rpc.client.proxy.SimpleRPCProxyFactory;
import com.gbdex.rpc.demo.service.Helloword;
import com.gbdex.rpc.protocol.transport.MessagePactTransport;
import com.gbdex.rpc.protocol.transport.ProtostuffTransport;
import io.netty.util.ResourceLeakDetector;

public class ClientDemo {
	public static void main(String[] args) {
		SimpleRPCProxyFactory factory = new SimpleRPCProxyFactory(new ProtostuffTransport());
		try {
			ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
			factory.addEnpoint("127.0.0.1:10081");
			Helloword h = factory.create(Helloword.class);
			long st = System.currentTimeMillis();
			for (int i = 0; i < 1000000; i++) {
				String msg = h.say("word");
//				System.out.println(msg);
			}
			System.out.println(1000000/ ((System.currentTimeMillis() - st) / 1000) + "/s");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				factory.dispose();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
