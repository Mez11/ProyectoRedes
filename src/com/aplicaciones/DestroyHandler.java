package com.aplicaciones;

import java.net.Socket;

import org.apache.commons.pool2.ObjectPool;

public class DestroyHandler {
	private ObjectPool<Socket> pool;
	private Socket socket;
	
	public DestroyHandler( ObjectPool<Socket> pool, Socket socket ){
		this.pool = pool;
		this.socket = socket;
	}
	
	public void destroy( ){
		try {
			pool.returnObject( socket );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
