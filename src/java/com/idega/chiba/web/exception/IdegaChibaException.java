package com.idega.chiba.web.exception;

public class IdegaChibaException extends RuntimeException {

	private static final long serialVersionUID = 79841216093931974L;

	private String messageToClient;
	
	private boolean reloadPage = Boolean.TRUE;
	
	public IdegaChibaException() {
		super();
	}
	
	public IdegaChibaException(String message) {
		super(message);
	}
	
	public IdegaChibaException(String message, String messageToClient, boolean reloadPage) {
		this(message);
		
		this.messageToClient = messageToClient;
		this.reloadPage = reloadPage;
	}

	public String getMessageToClient() {
		return messageToClient;
	}

	public void setMessageToClient(String messageToClient) {
		this.messageToClient = messageToClient;
	}

	public boolean isReloadPage() {
		return reloadPage;
	}

	public void setReloadPage(boolean reloadPage) {
		this.reloadPage = reloadPage;
	}

}