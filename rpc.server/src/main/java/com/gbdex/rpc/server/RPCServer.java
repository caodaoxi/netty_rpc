package com.gbdex.rpc.server;

import java.io.IOException;

public interface RPCServer {
	// 启动服务
	public void doStart() throws IOException;

	// 停止服务
	public void doStop() throws IOException;

	// 注册接口服务
	public void registerMBean(Object... mbeans);

	// 删除接口服务
	public void removeMBean(Object mbean);

}
