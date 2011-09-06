/*
 * FileCorruptException.java
 *
 * Created on April 26, 2007, 2:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt;

/**
 *
 * @author schandran
 */
public class FileCorruptException extends WizCryptException {
    
    public static final int HEADER_CRC_ERROR = 1;
    public static final int DATA_CRC_ERROR = 2;
    public static final int FILE_TRUNCATED = 3;
    public static final int FILE_MAGIC_NUMBER_ERROR = 4;
    
    private FileCorruptType type;
    
    /** Creates a new instance of FileCorruptException */
    public FileCorruptException(final FileCorruptType type) {
        this.type = type;
    }
    
    public FileCorruptType getErrorType(){
        return type;
    }
    
}
