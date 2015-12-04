package com.gbdex.rpc.protocol.message;

import java.util.List;

import com.gbdex.rpc.protocol.utils.KeyUtils;

public class Request implements Message{
	private long reqId;

	public Request() {
		this(KeyUtils.uuLongKey());
	}

	public Request(long id) {
		this.reqId = id;
	}

	public long getReqId() {
		return reqId;
	}

	private String className;
	private String methodName;
	private String paramTypes;
	private List<Object> paramValues;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(String paramTypes) {
		this.paramTypes = paramTypes;
	}

	public List<Object> getParamValues() {
		return paramValues;
	}

	public void setParamValues(List<Object> paramValues) {
		this.paramValues = paramValues;
	}
}
