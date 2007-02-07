package org.wiztools.wizcrypt.exception;

public class PasswordMismatchException extends Exception{
    
    public PasswordMismatchException(){
        
    }
    
    public PasswordMismatchException(String msg){
        super(msg);
    }
    
}
