package com.gbdex.rpc.protocol.codec;

import io.netty.channel.ChannelHandler;
import org.msgpack.MessagePack;
import org.msgpack.packer.MessagePackBufferPacker;

import com.gbdex.rpc.protocol.message.Message;
import com.gbdex.rpc.protocol.message.Request;
import com.gbdex.rpc.protocol.message.Response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class MessagePackEncoder extends MessageToByteEncoder<Message> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out)
			throws Exception {
		MessagePackBufferPacker buffer = new MessagePackBufferPacker(
				new MessagePack());
		if (msg instanceof Request) {
			Request req = (Request) msg;
			buffer.write(0);//请求标识
			buffer.write(req.getReqId());
			buffer.write(req.getClassName());
			buffer.write(req.getMethodName());
			buffer.write(req.getParamTypes());
			if (req.getParamValues() != null) {
				for (Object o : req.getParamValues()) {
					buffer.write(o);
				}
			}
		} else if (msg instanceof Response) {
			Response resp = (Response) msg;
			buffer.write(1);//响应标识
			buffer.write(resp.getReqId());
			buffer.write(resp.getRespCode());
			buffer.write(resp.getParamType());
			buffer.write(resp.getError());
			buffer.write(resp.getResponseEntry());
		} else {
			buffer.close();
			throw new Exception("not support message type :["+msg.getClass().getSimpleName()+"]");
		}
		out.writeInt(buffer.getBufferSize());
		out.writeBytes(buffer.toByteArray());
		buffer.close();
	}
}
