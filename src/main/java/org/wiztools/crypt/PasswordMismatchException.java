package org.wiztools.crypt;

public class PasswordMismatchException extends Exception{

	private String msg;

	public PasswordMismatchException(String msg){
		super(msg);
		this.msg = msg;
	}

	public String getMessage(){
		return this.msg;
	}

}
