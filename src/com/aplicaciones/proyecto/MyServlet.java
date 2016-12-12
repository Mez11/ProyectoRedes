package com.aplicaciones.proyecto;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import org.apache.commons.io.FilenameUtils;

public class MyServlet implements Runnable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7638756152308174619L;
	//******CONSTANTS******
    static final String METHOD_HEAD = "HEAD";
    static final String METHOD_GET = "GET";
    static final String METHOD_POST = "POST";

    private static final String DEFAULT_PAGE = "index.html";
    private static final String DEFAULT_LOCATION = "web/";
    //***************INSTANCE VARIABLES****************
	private HttpRequest request;
	private HttpResponse response;
	//private DestroyHandler destroyHandler;
	
	public MyServlet( HttpRequest request, HttpResponse response/*, DestroyHandler destroyHandler*/ ){
		this.request = request;
		this.response = response;
		//this.destroyHandler = destroyHandler;
	}
	
	/**
     * Called by the server (via the <code>service</code> method) to
     * allow a servlet to handle a GET request.
     * <p>The GET method should also be idempotent, meaning
     * that it can be safely repeated. Sometimes making a
     * method safe also makes it idempotent. For example,
     * repeating queries is both safe and idempotent, but
     * buying a product online or modifying data is neither
     * safe nor idempotent.</p>
     * @param request   an {@link HttpRequest} object that
     *                  contains the request the client has made
     *                  of the servlet
     *
     * @param response  an {@link HttpResponse} object that
     *                  contains the response the servlet sends
     *                  to the client
     *
     * @see HttpResponse#setContentType(String)
     */
	protected void doGet( HttpRequest request, HttpResponse response ) throws FileNotFoundException, IOException {
		if( request.getFileName() == null || request.getFileName().isEmpty() ){
			request.setFileName( DEFAULT_PAGE );
		}
		sendTo( DEFAULT_LOCATION + request.getFileName( ) );
		
	}
	
	/**
     * <p>Receives an HTTP HEAD request from the protected
     * <code>service</code> method and handles the
     * request.
     * The client sends a HEAD request when it wants
     * to see only the headers of a response, such as
     * Content-Type or Content-Length. The HTTP HEAD
     * method counts the output bytes in the response
     * to set the Content-Length header accurately.
     *
     * @param request   the request object that is passed to the servlet
     *
     * @param response  the response object that the servlet
     *                  uses to return the headers to the client
	 * @throws IOException 
	 * @throws FileNotFoundException 
     */
	protected void doHead( HttpRequest request, HttpResponse response ) throws FileNotFoundException, IOException{
		if( request.getFileName() == null || request.getFileName().isEmpty() ){
			request.setFileName( DEFAULT_PAGE );
		}
		sendTo( DEFAULT_LOCATION + request.getFileName( ) );
	}
	
	/**
	 * Called when the specified method is not
	 * implemented by the server. It only
	 * prints an error.
	 */
	private void doNotImplemented( ){
		PrintWriter out = response.getWriter( );
		response.init( true );
		printHtmlHeaders( out );
		out.println( "\t\t<center>" );
		out.print( "El m&eacute;todo HTTP no puede ser tratado por este servidor: <b>" );
		out.print( request.getMethod() );
		out.print( "</b>" );
		out.print( "</center> " );
		printHtmlFooters( out );
		response.setStatus( HttpResponse.SC_NOT_IMPLEMENTED );
		response.commit( );
		//destroyHandler.destroy();
	}
	
	/**
     * Called by the server (via the <code>service</code> method)
     * to allow a servlet to handle a POST request.
     *
     * The HTTP POST method allows the client to send
     * data of unlimited length to the Web server a single time
     * and is useful when posting information such as
     * credit card numbers.
     *
     * <p>This method does not need to be either safe or idempotent.
     * Operations requested through POST can have side effects for
     * which the user can be held accountable, for example,
     * updating stored data or buying items online.</p>
     *
     * @param request   an {@link HttpRequest} object that
     *                  contains the request the client has made
     *                  of the servlet
     *
     * @param response  an {@link HttpResponse} object that
     *                  contains the response the servlet sends
     *                  to the client
	 * @throws IOException 
	 * @throws FileNotFoundException 
     *
     * @see HttpResponse#setContentType(String)
     */
	protected void doPost( HttpRequest request, HttpResponse response ) throws FileNotFoundException, IOException{
		/*response.init( true );
		response.getWriter( ).print( "<html><body>Developing...</body></html>" );
		response.commit( );*/
		//destroyHandler.destroy();
		
		if( request.getFileName() == null || request.getFileName().isEmpty() ){
			request.setFileName( DEFAULT_PAGE );
		}
		sendTo( DEFAULT_LOCATION + request.getFileName( ) );
		
	}

	/**
	 * print the general HTML footers (closses body and html tags)
	 * to the output. 
	 * @param out {@link PrintWriter} associated with the servlet
	 */
	private void printHtmlFooters( PrintWriter out ){
		out.println( "\t</body>" );
		out.print( "</html>" );
	}
	
	/**
	 * print the general HTML headers (body, head and html tags)
	 * to the output. 
	 * @param out {@link PrintWriter} associated with the servlet
	 */
	private void printHtmlHeaders( PrintWriter out ){
		out.println( "<!DOCTYPE HTML>" );
		out.println( "<html>" );
		out.println( "\t<head>" );
		out.println( "\t\t<title>Error 500 - ClonCat</title>" );
		out.println( "\t</head>" );
		out.println( "\t<body bgcolor=\"#AACCFF\">" );
	}
	
	private void printHtmlHeadersConsole(  ){
		System.out.println( "<!DOCTYPE HTML>" );
		System.out.println( "<html>" );
		System.out.println( "\t<head>" );
		System.out.println( "\t\t<title>Error 500 - ClonCat</title>" );
		System.out.println( "\t</head>" );
		System.out.println( "\t<body bgcolor=\"#AACCFF\">" );
	}
	
	/**
	 * Prints the stacktrace of an error to the document.
	 * @param out {@link PrintWriter} associated with the servlet
	 * @param ex The error to be printed
	 */
	private void printStackError( PrintWriter out, Exception ex ){
		StackTraceElement[] trace = ex.getStackTrace( );
		
		printHtmlHeaders( out );///
		out.print( "\t\t<h1>[" + HttpResponse.SC_INTERNAL_SERVER_ERROR + "] Internal Server Error.</h1>\n\t\t<h3>" );
		out.print( ex.getClass( ).toString( ).substring( ex.getClass( ).toString( ).indexOf( " " ) ) );
		if( ex.getMessage() != null && !ex.getMessage().isEmpty() ){
			out.print( " - " );
			out.print( ex.getMessage() );
		}
		out.println( "</h3>\n\t\t<p>Complete error trace:</p>" );
		out.println( "\t\t<blockquote>" );
		for( StackTraceElement element : trace ){
			out.print( "\t\t\tat " );
			out.print( element.toString( ) );
			out.println( "<br/>" );
		}
		out.println( "\t\t</blockquote>" );
		printHtmlFooters(out);///
	}
	
	@Override
	public void run() {
		service( );		
	}
	
	/**
	 * Main method to dispatch the request.
	 * Now, it only handles POST, GET and HEAD methods. 
	 * any other method will be responded with a 
	 * {@link HttpResponseStatusCodes#SC_NOT_IMPLEMENTED}
	 */
	private void service( ){
		try{
			request.substractInfo( true );
			String method = request.getMethod( );
		
			if( method.equals( METHOD_GET ) ){
				doGet( request, response );
			}
			else if( method.equals( METHOD_POST ) ){
				doPost( request, response );
			}
			else if( method.equals( METHOD_HEAD ) ){
				doHead( request, response );
			}
			else{
				doNotImplemented( );
			}
		}
		catch( FileNotFoundException file ){
			response.setStatus( HttpResponse.SC_NOT_FOUND );
			response.init( true );
			//System.err.println( "File not found: " + request.getFileName() );
			printHtmlHeaders( response.getWriter( ) );
			printHtmlHeadersConsole( );
			response.getWriter( ).println( "<h1>404: File Not Found</h1>" );
			response.getWriter( ).println( "The requested file (" + request.getFileName( ) +") is unavailable or doesn't exist." );
			printHtmlFooters( response.getWriter( ) );
		} catch (Exception e) {
			response.setStatus( HttpResponse.SC_INTERNAL_SERVER_ERROR );
			response.init( true );
			//printHtmlHeaders( response.getWriter( ) );
			printStackError( response.getWriter( ), e );
			//printHtmlFooters( response.getWriter( ) );
			e.printStackTrace( );
		}
		
		response.commit( );
		//destroyHandler.destroy();
	}
	
	/**
	 * Loads a file in memory and sends it to the web browser.
	 * @param fileName The requested file
	 * @throws FileNotFoundException When the file doesn't exists or is unavailable
	 */
	public void sendTo( String fileName ) throws FileNotFoundException, IOException {
		byte [] buffer = new byte[4096];
		String fileType = null;
		int bytesRead;
		//To load the requested file
		FileInputStream fis = null;
		//To write the file to the web browser
		DataOutputStream dos = null;
		
		
		dos = new DataOutputStream( response.getOutputStream( ) );
		fis = new FileInputStream( fileName );
		fileType = FilenameUtils.getExtension( fileName );
		if( fileType.equalsIgnoreCase( "html" ) || fileType.equalsIgnoreCase( "htm" ) ){
			response.setContentType( HttpResponse.TYPE_HTML );
		}
		else if( fileType.equalsIgnoreCase( "jpeg" ) || fileType.equalsIgnoreCase( "jpg" ) ){
			response.setContentType( HttpResponse.TYPE_JPEG );
		}
		else if( fileType.equalsIgnoreCase( "png" ) ){
			response.setContentType( HttpResponse.TYPE_PNG );
		}
		else if( fileType.equalsIgnoreCase( "gif" ) ){
			response.setContentType( HttpResponse.TYPE_GIF );
		}
		else if( fileType.equalsIgnoreCase( "pdf" ) ){
			response.setContentType( HttpResponse.TYPE_PDF );
		}
		else if( fileType.equalsIgnoreCase( "xml" ) ){
			response.setContentType( HttpResponse.TYPE_XML );
		}
		else if( fileType.equalsIgnoreCase( "txt" ) ){
			response.setContentType( HttpResponse.TYPE_TXT );
		}
		else if( fileType.equalsIgnoreCase( "js" ) ){
			response.setContentType( HttpResponse.TYPE_JS );
		}
		else if( fileType.equalsIgnoreCase( "css" ) ){
			response.setContentType( HttpResponse.TYPE_CSS );
		}
		else{
			response.setContentType( HttpResponse.TYPE_DOWNLOAD );
		}
		
		response.init( true );
		
		
		if( !request.getMethod().equals(METHOD_HEAD) ){
			//Read the first 4096 bytes of the file,
			bytesRead = fis.read( buffer );
			while( bytesRead != -1 ){
				//write them into the output stream
				dos.write( buffer, 0, bytesRead );
				//and read the next 4096 bytes of the file
				bytesRead = fis.read( buffer );
			}
		}
		dos.flush( );
		fis.close( );
	}
}


