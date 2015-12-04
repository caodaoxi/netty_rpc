package com.gbdex.rpc.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.gbdex.rpc.protocol.message.Message;
import com.gbdex.rpc.protocol.message.Request;
import com.gbdex.rpc.protocol.utils.ProtostuffUtils;

public class ProtostuffEncoder extends MessageToByteEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out)
			throws Exception {
		if (msg instanceof Request) {
			byte[] bb = ProtostuffUtils.serialize(msg);
			out.writeInt(bb.length+2);
			out.writeShort(0);
			out.writeBytes(bb);
		} else {
			byte[] bb = ProtostuffUtils.serialize(msg);
			out.writeInt(bb.length+2);
			out.writeShort(1);
			out.writeBytes(bb);
		}
	}

}
