/*
 * DestinationFileExistsException.java
 *
 * Created on 24 December 2006, 21:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt;

/**
 *
 * @author subhash
 */
public class DestinationFileExistsException extends Exception {
    
    /** Creates a new instance of DestinationFileExistsException */
    public DestinationFileExistsException(final String msg) {
        super(msg);
    }
    
}
