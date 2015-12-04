package com.gbdex.rpc.protocol.transport;

import com.gbdex.rpc.protocol.codec.ProtostuffDecoder;
import com.gbdex.rpc.protocol.codec.ProtostuffEncoder;

import io.netty.channel.ChannelHandlerAdapter;

public class ProtostuffTransport implements RPCTransport {

	public ChannelHandlerAdapter getEncoder() {
		return new ProtostuffEncoder();
	}

	public ChannelHandlerAdapter getDecoder() {
		return new ProtostuffDecoder(65535, 0, 4);
	}

}
