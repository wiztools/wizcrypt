/*
 * DestinationFileExistsException.java
 *
 * Created on 24 December 2006, 21:56
 *
 */

package org.wiztools.wizcrypt;

/**
 *
 * @author subhash
 */
public class DestinationFileExistsException extends WizCryptException {
    
    public DestinationFileExistsException(){
        
    }
    
    /** Creates a new instance of DestinationFileExistsException */
    public DestinationFileExistsException(final String msg) {
        super(msg);
    }
    
}
