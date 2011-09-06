package org.wiztools.wizcrypt;

public class PasswordMismatchException extends WizCryptException{
    
    public PasswordMismatchException(){
        
    }
    
    public PasswordMismatchException(String msg){
        super(msg);
    }
    
}
