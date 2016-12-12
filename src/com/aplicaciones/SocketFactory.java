package com.aplicaciones;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class SocketFactory extends BasePooledObjectFactory<Socket> {
	
	private ServerSocket serverSocket;
	
	public SocketFactory( ServerSocket serverSocket ){
		this.serverSocket = serverSocket;
	}

	@Override
	public Socket create() throws Exception {
		return serverSocket.accept( );
	}

	@Override
	public PooledObject<Socket> wrap(Socket obj) {
		return new DefaultPooledObject<Socket>( obj );
	}

}
