package com.gbdex.rpc.protocol.namespace;

public class EndpointInfo {

	private int timeout=-1;
	private StringBuilder endpoint;

	public EndpointInfo() {

	}

	public EndpointInfo(StringBuilder endpoint) {
		this(-1, endpoint);
	}

	public EndpointInfo(int timeout, StringBuilder endpoint) {
		this.timeout = timeout;
		this.endpoint = endpoint;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public StringBuilder getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(StringBuilder endpoint) {
		this.endpoint = endpoint;
	}

}
