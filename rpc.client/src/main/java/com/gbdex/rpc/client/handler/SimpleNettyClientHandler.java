package com.gbdex.rpc.client.handler;

import java.util.concurrent.ConcurrentHashMap;

import com.gbdex.rpc.protocol.message.Message;
import com.gbdex.rpc.protocol.message.Request;
import com.gbdex.rpc.protocol.message.RespFuture;
import com.gbdex.rpc.protocol.message.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

public class SimpleNettyClientHandler extends
		SimpleChannelInboundHandler<Message> implements NettyClientHandler {

	private ConcurrentHashMap<Long, RespFuture> responseMap = new ConcurrentHashMap<Long, RespFuture>();

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		if (msg instanceof Request) {
			// TODO
		} else if (msg instanceof Response) {
			Response resp = (Response) msg;
			RespFuture future = responseMap.get(resp.getReqId());
			if (future != null) {
				future.fillResponse(resp);
			} else {
				// TODO
			}
		}
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.channel().close();
	}


	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		//TODO notify proxyFacotry to remove the cache channel
		super.close(ctx, promise);
	}


	public void putResponse(Long key, RespFuture futrue) {
		responseMap.put(key, futrue);
	}

	public void removeReponse(Long key) {
		responseMap.remove(key);
	}
}
