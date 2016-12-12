package com.aplicaciones.proyecto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
	public static final int PORT = 8000;
	
	/*public WebServer( ObjectPool<Socket> pool ){
		Socket host;
		System.out.println( "Starting server... ");
		try {
			System.out.println( "Server ready. Wating for clients...");
			while( true ){
				host = pool.borrowObject( );
				System.out.println( "************************************" );
				//new Thread( new MyServlet( new HttpRequest( host ), new HttpResponse( host ), new DestroyHandler( pool, host) ) ).start( );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public WebServer( ServerSocket serverSocket ) {
		Socket host;
		System.out.println( "Server ready. Wating for clients...");
		while( true ){
			try {
				host = serverSocket.accept( );
				System.out.println( "************************************" );
				new Thread( new MyServlet( new HttpRequest( host ), new HttpResponse( host ) ) ).start( );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main( String args [] ){
		System.out.println( "Starting server... ");
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket( PORT );
			new WebServer( serverSocket );
		} catch (IOException e) {
			System.out.println("Unable to start server...");
		}
		//new WebServer( new GenericObjectPool<Socket>( new SocketFactory( serverSocket ) ) );
	}
}//end class WebServer

