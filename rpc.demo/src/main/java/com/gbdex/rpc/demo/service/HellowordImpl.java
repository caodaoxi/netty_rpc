package com.gbdex.rpc.demo.service;

import com.gbdex.rpc.protocol.namespace.Service;

@Service(targetInterface = "com.gbdex.rpc.demo.service.Helloword")
public class HellowordImpl implements Helloword {

	public String say(String str) {
		return "hello : " + str;
	}

}
