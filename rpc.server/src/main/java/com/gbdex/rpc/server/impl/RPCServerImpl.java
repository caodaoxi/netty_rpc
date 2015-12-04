package com.gbdex.rpc.server.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.gbdex.rpc.protocol.namespace.Service;
import com.gbdex.rpc.protocol.transport.RPCTransport;
import com.gbdex.rpc.server.RPCServer;
import com.gbdex.rpc.server.handler.RPCServerHandler;

public class RPCServerImpl implements RPCServer, ApplicationContextAware,
		InitializingBean {

	private Map<String, Object> services = new HashMap<String, Object>();
	private String host;
	private int port;
	private AtomicBoolean status = new AtomicBoolean(false);
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ServerBootstrap bootstrap;
	private RPCTransport transport;

	private String zkConnect;
	private CuratorFramework client;

	public RPCServerImpl(String host, int port, RPCTransport transport) {
		this.host = host;
		this.port = port;
		this.transport = transport;
	}

	public RPCServerImpl(String host, int port, RPCTransport transport,
			String zkConnect) {
		this.host = host;
		this.port = port;
		this.transport = transport;
		this.zkConnect = zkConnect;
		this.client = CuratorFrameworkFactory.newClient(this.zkConnect,
				new ExponentialBackoffRetry(1000, 3));
	}

	public void afterPropertiesSet() throws Exception {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		bootstrap
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel channel)
							throws Exception {
						channel.pipeline().addLast(
								RPCServerImpl.this.transport.getDecoder(),
								RPCServerImpl.this.transport.getEncoder(),
								new RPCServerHandler(services));
					}
				}).option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, false)
				.childOption(ChannelOption.SO_SNDBUF, 65535)
				.childOption(ChannelOption.SO_RCVBUF, 65535);

	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		Map<String, Object> serviceBeanMap = applicationContext
				.getBeansWithAnnotation(Service.class);
		if (!serviceBeanMap.isEmpty()) {
			for (Object serviceBean : serviceBeanMap.values()) {
				String interfaceName = serviceBean.getClass()
						.getAnnotation(Service.class).targetInterface();
				services.put(interfaceName, serviceBean);
			}
		}
	}

	public void doStart() throws IOException {
		if (status.compareAndSet(false, true)) {
			try {
				bootstrap.bind(host, port).sync();

				if (client != null) {
					registerServer();
				}
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
		}
	}

	public void doStop() throws IOException {
		if (status.compareAndSet(true, false)) {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			if (client != null
					&& client.getState() == CuratorFrameworkState.STARTED) {
				client.close();
			}
		}
	}

	public void registerMBean(Object... mbeans) {
		for (Object serviceBean : mbeans) {
			String interfaceName = serviceBean.getClass()
					.getAnnotation(Service.class).targetInterface();
			services.put(interfaceName, serviceBean);
		}
	}

	public void removeMBean(Object mbean) {
		String interfaceName = mbean.getClass().getAnnotation(Service.class)
				.targetInterface();
		services.remove(interfaceName);
	}

	public void registerServer() {
		if (client.getState() != CuratorFrameworkState.STARTED) {
			client.start();
		}
		// 父目录是否存在
		try {
			if (client.checkExists().forPath("/RpcServer") == null) {
				client.create().forPath("/RpcServer");
			}
			if (client.checkExists().forPath("/RpcServer/" + host + ":" + port) != null) {
				client.delete().forPath("/RpcServer/" + host + ":" + port);
			}
			client.create().withMode(CreateMode.EPHEMERAL)
					.forPath("/RpcServer/" + host + ":" + port);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
