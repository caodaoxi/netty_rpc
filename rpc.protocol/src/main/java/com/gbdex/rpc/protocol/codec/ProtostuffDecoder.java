package com.gbdex.rpc.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

import com.gbdex.rpc.protocol.message.Request;
import com.gbdex.rpc.protocol.message.Response;
import com.gbdex.rpc.protocol.utils.ProtostuffUtils;

public class ProtostuffDecoder extends LengthFieldBasedFrameDecoder {

	public ProtostuffDecoder(ByteOrder byteOrder, int maxFrameLength,
			int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, boolean failFast) {
		super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip, failFast);
	}

	public ProtostuffDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, boolean failFast) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip, failFast);
	}

	public ProtostuffDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip);
	}

	public ProtostuffDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		Object frame = super.decode(ctx, in);
		if (frame != null && frame instanceof ByteBuf) {
			ByteBuf buf = (ByteBuf) frame;
			int size = buf.readInt();
			int flag = buf.readShort();
			byte[] data = new byte[size-2];
			buf.readBytes(data);
			buf.release();
			if (flag == 0) {
				return ProtostuffUtils.deserialize(data, Request.class);
			} else {
				return ProtostuffUtils.deserialize(data, Response.class);
			}
		}
		return null;
	}

}
