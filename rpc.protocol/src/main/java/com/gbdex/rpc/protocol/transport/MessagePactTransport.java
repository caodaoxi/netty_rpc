package com.gbdex.rpc.protocol.transport;

import com.gbdex.rpc.protocol.codec.MessagePackDecoder;
import com.gbdex.rpc.protocol.codec.MessagePackEncoder;

import io.netty.channel.ChannelHandlerAdapter;

public class MessagePactTransport implements RPCTransport {

	public MessagePactTransport() {
	}

	public ChannelHandlerAdapter getEncoder() {
		return new MessagePackEncoder();
	}

	public ChannelHandlerAdapter getDecoder() {
		return new MessagePackDecoder(65535, 0, 4);
	}

}
