package com.gbdex.rpc.client.handler;

import com.gbdex.rpc.protocol.message.RespFuture;

public interface NettyClientHandler {
	public void putResponse(Long key,RespFuture futrue);
	
	public void removeReponse(Long key);
}
