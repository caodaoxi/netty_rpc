package com.gbdex.rpc.client.core.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.gbdex.rpc.client.handler.SimpleNettyClientHandler;
import com.gbdex.rpc.protocol.message.Request;
import com.gbdex.rpc.protocol.message.RespFuture;
import com.gbdex.rpc.protocol.message.Response;
import com.gbdex.rpc.protocol.transport.RPCTransport;

public class SimpleNettyClient extends NettyClient<Request, Response> {

	private Bootstrap bootstrap;
	private List<Channel> sessions = new LinkedList<Channel>();
	private List<String> serverAddress;
	private AtomicInteger index = new AtomicInteger(0);
	private AtomicBoolean state = new AtomicBoolean(false);
	private RPCTransport transport;
	public SimpleNettyClient(String address, RPCTransport transport) {
		EventLoopGroup work = new NioEventLoopGroup(10);
		this.transport = transport;
		handler = new SimpleNettyClientHandler();
		bootstrap = new Bootstrap();
		bootstrap.group(work);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.SO_RCVBUF, 65535);
		bootstrap.option(ChannelOption.SO_SNDBUF, 65535);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline()
						.addLast(SimpleNettyClient.this.transport.getDecoder(),
								 SimpleNettyClient.this.transport.getEncoder(),
								(SimpleNettyClientHandler) handler);
			}
		});
		this.serverAddress = Arrays.asList(address.split(","));
	}

	public void close() throws IOException {
		if (state.compareAndSet(true, false)) {
			for (Channel c : sessions) {
				c.close();
			}
			sessions.clear();
		}
	}

	public void connect() throws IOException {
		if (state.compareAndSet(false, true)) {
			for (String address : serverAddress) {
				String[] host = address.split(":");
				try {
					ChannelFuture future = bootstrap.connect(host[0],
							Integer.valueOf(host[1])).sync();
					sessions.add(future.channel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (sessions.size() == 0) {
				state.set(false);
				throw new IOException("can not connect to server ...!");
			}
		}
	}

	@Override
	public Response invoke(Request p) throws IOException {
		try {
			RespFuture future = new RespFuture(p.getReqId(),
					new CountDownLatch(1));
			handler.putResponse(p.getReqId(), future);
			select().writeAndFlush(p);
			return future.getResponse(-1);
		} finally {
			handler.removeReponse(p.getReqId());
		}

	}

	@Override
	public Response invoke(Request p, int timeout) throws IOException {
		try {
			RespFuture future = new RespFuture(p.getReqId(),
					new CountDownLatch(1));
			handler.putResponse(p.getReqId(), future);
			select().writeAndFlush(p);
			return future.getResponse(timeout);
		} finally {
			handler.removeReponse(p.getReqId());
		}
	}

	private Channel select() {
		index.compareAndSet(sessions.size(), 0);
		return sessions.get(index.getAndIncrement());
	}

	public boolean isActive() {
		return state.get();
	}

}
