package com.gbdex.rpc.client.core.netty;

import java.io.IOException;

import com.gbdex.rpc.client.core.Client;
import com.gbdex.rpc.client.handler.NettyClientHandler;

public abstract class NettyClient<P, T> implements Client {
	protected NettyClientHandler handler;

	public abstract T invoke(P p) throws IOException;
	
	public abstract T invoke(P p,int timeout) throws IOException;
}
