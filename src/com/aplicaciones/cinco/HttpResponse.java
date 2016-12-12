package com.aplicaciones.cinco;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class HttpResponse implements HttpResponseStatusCodes, HttpResponseFileTypes {
	
	public static final String SERVER_NAME = "Huarache ClonCat";
	
	private int status;
	private int contentLength;
	private String version;
	private String contentType;
	private Date date;
	private Socket socket;
	private PrintWriter writer;
	private boolean commited;
	private OutputStream outputStream;
	
	/**
	 * Initialize the response with the default values:<br/>
	 * <ul>
	 * <li>version: 1.0</li>
	 * <li>status: {@link HttpResponseStatusCodes#SC_OK}</li>
	 * <li>contentType: text/html</li>
	 * <li>date: now</li>
	 * </ul>
	 * Also, initializes the outputStreams
	 * @throws IOException 
	 */
	public HttpResponse( Socket socket ) throws IOException {
		version = "1.0";
		status = SC_OK;
		date = new Date( );
		contentType = "text/html";
		
		this.socket = socket;
		this.outputStream = socket.getOutputStream( );
		this.writer = new PrintWriter( outputStream );
	}
	
	/**
	 * Closes the connection between
	 * the server and the client, making the response 
	 * to be interpreted by the browser.
	 */
	public void commit( ){
		writer.flush( );
		try {
			outputStream.flush( );
			socket.close( );
		} catch (IOException e) {
			e.printStackTrace();
		}
		commited = true;
	}
	
	public int getContentLength() {
		return contentLength;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public Date getDate() {
		return date;
	}

	public OutputStream getOutputStream( ){
		return outputStream;
	}

	public int getStatus() {
		return status;
	}


	public String getStatusDescription( ){
		switch( status ){
		case SC_OK:
			return "OK";
		case SC_NOT_IMPLEMENTED:
			return "Not implemented";
		case SC_INTERNAL_SERVER_ERROR:
			return "Internal server error";
		case SC_NOT_FOUND:
			return "File not found";
		}
		return null;
	}

	public String getVersion() {
		return version;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	/**
	 * Initializes the response, sending the headers 
	 * to the browser.
	 * @param debug
	 */
	public void init( boolean debug ){
		StringBuilder builder = new StringBuilder( );
		String content = null;
		
		builder.append( "HTTP/" );
		builder.append( getVersion( ) );
		builder.append( " " );
		builder.append( getStatus( ) );
		builder.append( " " );
		builder.append( getStatusDescription( ) );
		builder.append( "\n" );
		builder.append( "Server: " );
		builder.append( SERVER_NAME );
		builder.append( "\n" );
		builder.append( "Date: " );
		builder.append( getDate() );
		builder.append( "\n" );
		builder.append( "Content-Type: ");
		builder.append( getContentType() );
		builder.append( "\n\n" );
		
		content = builder.toString();
		if( debug ){
			System.out.println( "**********");
			System.out.println( "[HostHandler] Header to be send to the browser:");
			System.out.println( content );
			//System.out.println( "[HostHandler] End of header");
			//System.out.println( "**********");
		}
		
		writer.print( content );
		writer.flush( );
	}

	public boolean isCommited( ){
		return commited;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	
}
