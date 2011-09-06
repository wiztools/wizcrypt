/*
 * WizCryptParam.java
 *
 * Created on February 13, 2007, 12:18 PM
 *
 */

package org.wiztools.wizcrypt;

/**
 * This is a JavaBean class which encapsulates various parameters needed by
 * WizCrypt for encryption/decryption operations. Specifically, this class
 * encapsulates the following information:
 * <ol>
 *  <li><code>Callback</code>: The Callback object to be used with
 *       encryption/decryption operation.</li>
 *  <li>Algo (String): The algorithm to be used for encryption/decryption. This
 *       should be JCE compliant name. This parameter is ignored for decryption.</li>
 *  <li>Password (char[]): The password used for encryption/decryption.</li>
 * </ol>
 *
 * @see Callback
 * @see WizCrypt
 * @author schandran
 */
public class WizCryptBean {
    
    private char[] password;
    
    /** Creates a new instance of WizCryptParam */
    public WizCryptBean() {
    }
    
    public void setPassword(final char[] password){
        this.password = password;
    }
    
    public char[] getPassword(){
        return password;
    }
}
