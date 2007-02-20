/*
 * Algorithms.java
 *
 * Created on January 21, 2007, 12:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt;

/**
 *
 * @author schandran
 */
public interface WizCryptAlgorithms {
    
    public static final String CRYPT_ALGO_RC2 = "RC2";
    public static final String CRYPT_ALGO_RC4 = "RC4";
    public static final String CRYPT_ALGO_RC5 = "RC5";
    public static final String CRYPT_ALGO_BLOWFISH = "Blowfish";
    public static final String CRYPT_ALGO_DES = "DES";
    public static final String CRYPT_ALGO_3DES = "DESede";
    public static final String CRYPT_ALGO_AES = "AES";
    
    public static final String STR_ENCODE = "UTF-8";
    public static final String PWD_HASH_OLD = "MD5";
    public static final String PWD_HASH_07 = "SHA-256";
    
}
