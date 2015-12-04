package com.gbdex.rpc.protocol.transport;

import io.netty.channel.ChannelHandlerAdapter;

public interface RPCTransport {

	public ChannelHandlerAdapter getEncoder();

	public ChannelHandlerAdapter getDecoder();
}
