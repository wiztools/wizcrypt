/*
 * FileCorruptException.java
 *
 * Created on April 26, 2007, 2:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt.exception;

/**
 *
 * @author schandran
 */
public class FileCorruptException extends Exception {
    
    public static final int HEADER_CRC_ERROR = 1;
    public static final int DATA_CRC_ERROR = 2;
    public static final int FILE_TRUNCATED = 3;
    
    private int type;
    
    /** Creates a new instance of FileCorruptException */
    public FileCorruptException(final int type) {
        this.type = type;
    }
    
}
