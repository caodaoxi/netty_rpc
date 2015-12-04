package com.gbdex.rpc.client.core;

import java.io.IOException;

public interface Client {

	public void close() throws IOException;

	public void connect() throws IOException;
	
	public boolean isActive();

}
