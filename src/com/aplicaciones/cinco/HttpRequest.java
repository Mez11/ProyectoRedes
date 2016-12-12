package com.aplicaciones.cinco;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to encapsulate all the information
 * contained with the request of the HTTP protocol.
 * It also contains the stream to read the request headers.
 *
 */
public class HttpRequest {
	//estrae atributos de esa clase
	private static final Logger log= LoggerFactory.getLogger(HttpRequest.class); 
	
	private static final int HD_HOST = 1;
	private static final int HD_USER_AGENT = 2;
	private static final int HD_ACCEPT = 3;
	private static final int HD_ACCEPT_LANGUAGE = 4;
	private static final int HD_ACCEPT_ENCODING = 5;
	private static final int HD_DNT = 6;
	private static final int HD_REFERER = 7;
	private static final int HD_CONNECTION = 8;
	private static final int HD_CACHE_CONTROL = 9;
	private static final int HD_CONTENT_TYPE = 10;
	private static final int HD_CONTENT_LENGTH = 11;

	private Socket socket;
	private Map<String, String> params;
	private InputStream inputStream;
	private BufferedReader reader;
	private String method;
	private String requestURI;
	private String fileName;
	private String httpVersion;
	private String host;
	private String userAgent;
	private String accept;
	private String acceptLanguage;
	private String acceptEncoding;
	private String dnt;
	private String referer;
	private String connection;
	private String cacheControl;
	private String contentType;
	private int contentLength;
	
	/**
	 * Initializes the stream and the reader
	 * based on the socket object.
	 * @param socket Object with the connection between
	 * the client and the server
	 * @throws IOException When something wrong happen when
	 * creating the {@link BufferedReader}
	 */
	public HttpRequest( Socket socket ) throws IOException{
		this.socket = socket;
		inputStream = socket.getInputStream( );
		reader = new BufferedReader( new InputStreamReader( inputStream ) );
	}
	
	/**
	 * The name of the file requested by the client
	 * @return The name 
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * The version of the protocol used
	 * by the client
	 * @return the version of HTTP
	 */
	public String getHttpVersion() {
		return httpVersion;
	}
	
	/**
	 * The method of the client's request
	 *  (For example, GET or POST).
	 * @return The method
	 */
	public String getMethod() {
		return method;
	}
	
	/**
	 * Obtains the parameter value associated with the 
	 * given name
	 * @param paramName The name (key) of the parameter
	 * @return The value associated with the parameter, or null
	 * if no value matched with the parameter name.
	 */
	public String getParameter( String paramName ){
		if( params == null ){
			return null;
		}
		return params.get( "paramName" );
	}
	
	/**
	 * The map with all the pairs of parameter name - parameter value 
	 * found in the request header.
	 * @return An implementation of map with the name-value pairs.
	 */
	public Map<String, String> getParametersMap( ){
		return params;
	}
	
	/**
	 * Use this reader to read the text of the request.
	 */
	public BufferedReader getReader( ){
		return reader;
	}
	
	/**
	 * The request URI catched.
	 * @return The request URI
	 */
	public String getRequestURI() {
		return requestURI;
	}
	
	/**
	 * To know whether the request has params or not.
	 * @return {@code true} when the application has params 
	 * (that happens when the params map object contains at least 
	 * one parameter).
	 */
	public boolean isParamsAvailable( ){
		return params != null && !params.isEmpty( );
	}
	
	/**
	 * Obtains the string associated with the
	 * data input stream
	 * @throws IOException
	 */
	private String getStringHeader( boolean debug, DataInputStream data ) throws IOException{
		byte [] buffer = new byte[2048];
		int read = 0;
		String aux = "";
		while( read != -1 ){
			read = data.read( buffer );
			aux = new String( buffer );
			break;
		}
		if( debug ){
			System.out.println( "[HttpRequest] Start of the request:::::");
			System.out.println( aux );
			System.out.println( "[HttpRequest] End of the request:::::");
		}
		return aux; 
		
	}

	/**
	 * Process the first line of the HTTP request<br/>
	 * Please note that the pattern for this line is the following:<br/>
	 * <code>[METHOD] [Request-URI] [HTTP-Version]</code>
	 * @param line
	 */
	private boolean processFirstLine( String line, boolean verbose ){
		int space = 0;
		if( verbose ){
			System.out.println( "[HttpRequest] First line: " + line);
		}
		
		if( line == null || line.isEmpty() ){
			System.err.println( "[HttpRequest] There are no lines in the HTTP header..." );
			return false;
		}
		
		space = line.indexOf( " " );
		if( space == -1 ){
			//no space means no header to work with
			return false;
		}
		method = line.substring( 0, space );
		method = method.toUpperCase( );
		space = line.indexOf( " ", space + 1 );
		requestURI = line.substring( line.indexOf( "/" ), space );
		httpVersion = line.substring( space + 1 );
		if( verbose ){
			System.out.println( "[HttpRequest] Method: " + method );
			System.out.println( "[HttpRequest] HttpVersion: " + httpVersion );
		}
		processURI( verbose );
		return true;
	}

	/**
	 * Processes the request URI to obtain the basic
	 * info of the request. 
	 * @param verbose If <code>true</code>, every text processed by 
	 * this method will be printed.
	 */
	private void processURI( boolean verbose ){
		int position;
		
		if( verbose )
			System.out.println( "[HttpRequest] Analyzing for params: [" + requestURI + "]" );
		
		position = requestURI.indexOf( "?" );
		if( position == -1 ){
			fileName = requestURI.substring( 1, requestURI.length( ) );
			System.out.println( "[HttpRequest] No params found. fileName: " + fileName );
			return;
		}
		
		fileName = requestURI.substring( 1, position );
		
		if( verbose ){
			System.out.println( "[HttpRequest] fileName: " + fileName );
		}
		
		processParams( requestURI.substring( position + 1 ), verbose );
	}
	
	private void processParams( String paramString, boolean verbose ){
		StringTokenizer tokenizer = null;
		int position;
		String pair = null;
		
		params = new HashMap<String, String>();
		tokenizer = new StringTokenizer( paramString, "&" );
		
		while( tokenizer.hasMoreTokens() ){
			pair = tokenizer.nextToken( );
			position = pair.indexOf( "=" );
			params.put( pair.substring( 0, position ),  pair.substring( position + 1 ) );
		}
		if( verbose ){
			System.out.println( "[HttpRequest] Params found: " );
			for( Map.Entry<String, String> entry : params.entrySet( ) ){
				System.out.println( "\t" + entry.getKey() + " - " + entry.getValue( ) );
			}
			System.out.println( "[HttpRequest] end params");
		}
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	/*public boolean substractInfo( ){
		return substractInfo( false );
	}*/
	
	private void processLineHeader( String line ){
		int opt = 0;
		if( line == null || line.isEmpty() || line.equals( "\n" ) ){
			return;
		}
		if( line.startsWith( "Host" ) ){
			opt = HD_HOST;
		}
		else if( line.startsWith( "User-Agent" ) ){
			opt = HD_USER_AGENT;
		}
		else if( line.startsWith( "Accept" ) ){
			opt = HD_ACCEPT;
			if( line.contains( "Language" ) ){
				opt = HD_ACCEPT_LANGUAGE;
			}
			else if( line.contains( "Encoding" ) ){
				opt = HD_ACCEPT_ENCODING;
			}
		}
		else if( line.startsWith( "DNT" ) ){
			opt = HD_DNT;
		}
		else if( line.startsWith( "Referer" ) ){
			opt = HD_REFERER;
		}
		else if( line.startsWith( "Connection" ) ){
			opt = HD_CONNECTION;
		}
		else if( line.startsWith( "Cache" ) ){
			if( line.contains( "Control" ) ){
				opt = HD_CACHE_CONTROL;
			}
		}
		else if( line.startsWith( "Content" ) ){
			if( line.contains( "Type"  ) ){
				opt = HD_CONTENT_TYPE;
			}
			else if( line.contains( "Length" ) ){
				opt = HD_CONTENT_LENGTH;
			}
		}
		else{
			System.out.println( "Unknown:" + line );
			return;
		}
		setHeadValue( line, opt );
	}
	
	private void setHeadValue( String line, int option ){
		String body = line.substring( line.indexOf( " " ) + 1 );
		switch( option ){
		case HD_HOST:
			host = body;
			break;
		case HD_USER_AGENT:
			userAgent = body;
			break;
		case HD_ACCEPT:
			accept = body;
			break;
		case HD_ACCEPT_LANGUAGE:
			acceptLanguage = body;
			break;
		case HD_ACCEPT_ENCODING:
			acceptEncoding = body;
			break;
		case HD_DNT:
			dnt = body;
			break;
		case HD_REFERER:
			referer = body;
			break;
		case HD_CONNECTION:
			connection = body;
			break;
		case HD_CACHE_CONTROL:
			cacheControl = body;
			break;
		case HD_CONTENT_TYPE:
			contentType = body;
			break;
		case HD_CONTENT_LENGTH:
			contentLength = Integer.parseInt( body.replaceAll("\\s+","") );
			break;
		default:
			System.out.println( "Unkown code: " + option );
		}
	}

	/**
	 * Processes all the information adquired by the request.
	 * @param verbose If <code>true</code>, every text processed by 
	 * this method will be printed.
	 * @return true if the method executed successfully.
	 */
	public boolean substractInfo( boolean verbose ){
		DataInputStream data = null;
		StringTokenizer lines = null;
		String temp = null;
		int currentLine = 0;
		boolean endOfHeader = false;
		
		if( verbose )
			//.info
			//.debug
			//TODO: Change for logger salida estandar consola .err de error 
			//log.i
			System.out.println( "[HttpRequest] Starting to analyze the request..." );
		
		try {
			data = new DataInputStream( socket.getInputStream() );
			
			String aux = getStringHeader( verbose, data );
			lines = new StringTokenizer( aux, "\n" );
			
			while( lines.hasMoreTokens() ){
				temp = lines.nextToken( );
				
				if( temp.length() == 1 ){
					//if the length of the header is 1, it 
					//means that the end of the header of the request
					//has been reached. The next line will
					//be the body of the request.
					endOfHeader = true;
					//No operation matters on this line.
					//Move to the next one.
					continue;
				}
				else if( currentLine == 0 ){					
					if( !processFirstLine( temp, verbose) ){
						return false;
					}
				}
				else if( endOfHeader ){
					//if endOfHeader is true, it means
					//that this line is in the body of
					//the request.
					if( method.equals( MyServlet.METHOD_POST ) ){
						//if it's post, this line contains the parameters
						//of the request.
						processParams( temp, verbose );
					}
					//We don't care about more lines of the request's body.
					//Break the current loop.
					break;
				}
				else{
					processLineHeader( temp );
				}
				currentLine++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getHost() {
		return host;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getAccept() {
		return accept;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public String getAcceptEncoding() {
		return acceptEncoding;
	}

	public String getDnt() {
		return dnt;
	}

	public String getReferer() {
		return referer;
	}

	public String getConnection() {
		return connection;
	}

	public String getCacheControl() {
		return cacheControl;
	}

	public String getContentType() {
		return contentType;
	}

	public int getContentLength() {
		return contentLength;
	}
	
}

