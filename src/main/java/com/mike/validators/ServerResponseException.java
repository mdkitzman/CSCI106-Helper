package com.mike.validators;

/**
 * 
 * This exception is used when the server responds with a status other than 200(OK).
 * 
 * @author Mike Kitzman
 *
 */
public class ServerResponseException extends Exception {

	private static final long serialVersionUID = -8447117244950594591L;
	
	public int m_errorCode = 200;
	
	public ServerResponseException(){
		super("Unknown Server Response Exception message.");
		setStatusCode(500);
	}
	
	public ServerResponseException(String message, int statusCode){
		super(message);
		setStatusCode(statusCode);
	}
	
	public ServerResponseException(String message, Exception e, int statusCode){
		super(message, e);
		setStatusCode(statusCode);
	}
	
	public int getStatusCode(){
		return m_errorCode;
	}
	
	private void setStatusCode(int statusCode){
		m_errorCode = statusCode;
	}

}
