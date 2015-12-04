package com.gbdex.rpc.server.handler;

import java.util.List;
import java.util.Map;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import com.gbdex.rpc.protocol.message.Request;
import com.gbdex.rpc.protocol.message.Response;
import com.gbdex.rpc.server.constant.ServerStatus;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class RPCServerHandler extends ChannelHandlerAdapter {
	private Map<String, Object> services;

	public RPCServerHandler(Map<String, Object> services) {
		this.services = services;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Request request = (Request) msg;
		Response response = new Response();
		response.setReqId(request.getReqId());
		response.setRespCode(ServerStatus.OK);
		try {
			Object responseEntry = handleRequest(request);
			response.setParamType(responseEntry.getClass().getName());
			response.setResponseEntry(responseEntry);
		} catch (Throwable error) {
			response.setRespCode(ServerStatus.FAIL);
			response.setError(error.getMessage());
		}
		ctx.writeAndFlush(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	private Object handleRequest(Request request) throws Throwable {
		String className = request.getClassName();
		Object serviceBean = services.get(className);

		Class<?> serviceClass = serviceBean.getClass();
		String methodName = request.getMethodName();
		String[] paramTypes = request.getParamTypes().split(",");
		List<Object> params = request.getParamValues();
		Class<?>[] parameterTypes = null;
		Object[] parameters = null;
		if (paramTypes != null && paramTypes.length > 0) {
			parameterTypes = new Class<?>[paramTypes.length];
			parameters = new Object[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++) {
				parameterTypes[i] = params.get(i).getClass();
				parameters[i] = params.get(i);
			}
		}

		FastClass serviceFastClass = FastClass.create(serviceClass);
		FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName,
				parameterTypes);
		return serviceFastMethod.invoke(serviceBean, parameters);
	}

}
