/*
 * WizCryptParam.java
 *
 * Created on February 13, 2007, 12:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt;

/**
 *
 * @author schandran
 */
public class WizCryptBean {
    
    private Callback callback = null;
    private String algo = WizCryptAlgorithms.CRYPT_ALGO_RC4;
    private char[] password;
    
    /** Creates a new instance of WizCryptParam */
    public WizCryptBean() {
    }
    
    public void setCallback(final Callback callback){
        this.callback = callback;
    }
    
    public Callback getCallback(){
        return callback;
    }
    
    public void setAlgo(final String algo){
        this.algo = algo;
    }
    
    public String getAlgo(){
        return algo;
    }
    
    public void setPassword(final char[] password){
        this.password = password;
    }
    
    public char[] getPassword(){
        return password;
    }
}
