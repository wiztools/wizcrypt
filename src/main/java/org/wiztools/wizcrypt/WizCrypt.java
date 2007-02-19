/*
 * WizCrypt.java
 *
 * Created on 23 December 2006, 09:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import org.wiztools.wizcrypt.exception.FileFormatException;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;
import org.wiztools.wizcrypt.impl.WizCrypt07;
import org.wiztools.wizcrypt.impl.WizCryptOld;

/**
 * This class has the public APIs of WizCrypt application to do encryption and decryption.
 * @see WizCryptBean
 * @author subhash
 */
public abstract class WizCrypt {
    
    private static WizCrypt __instance07;
    private static WizCrypt __instanceOld;
    
    /**
     * Use this method to get the instance of WizCrypt to process the WizCrypt07
     * format files.
     */
    public static WizCrypt get07Instance(){
        if(__instance07 == null){
            __instance07 = new WizCrypt07();
        }
        return __instance07;
    }
    
    /**
     * Use this method to get the instance of WizCrypt to process the old WizCrypt
     * format files (WizCrypt 1.x/2.x).
     */
    public static WizCrypt getOldInstance(){
        if(__instanceOld == null){
            __instanceOld = new WizCryptOld();
        }
        return __instanceOld;
    }
    
    /**
     * This is the public API exposed to encrypt.
     * 
     * 
     * @param is The input stream that needs to be encrypted.
     * @param os The output stream where the encrypted content 
     *      of <code>is</code> need to be written.
     * @param wcb The <code>WizCryptBean</code> object. This object encapsulates
     *      various WizCrypt needed parameters like password, <code>Callback</code>
     *      object and algorithm to be used for encryption.
     * @see WizCryptBean
     * @throws IOException <code>IOException</code> is thrown when 
     *     faced with IO issues during read/write.
     * @throws NoSuchAlgorithmException <code>NoSuchAlgorithmException</code> is
     *      thrown when the JVM's JCE does not have the requested algorithm installed.
     * @throws InvalidKeyException <code>InvalidKeyException</code> is thrown
     *      when the key is not of the specified size/range as required by the
     *      specified algorithm.
     * @throws NoSuchPaddingException <code>NoSuchPaddingException</code> is
     *      thrown when a particular padding mechanism is requested but is not 
     *      available in the environment.
     */
    abstract public void encrypt(final InputStream is, final OutputStream os, 
            final WizCryptBean wcb)
            throws IOException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            NoSuchPaddingException;
    
    /**
     * Does encryption using the default algorithm for WizCrypt (RC4), and no
     * Callback configured.
     * @see #encrypt(InputStream is, OutputStream os, 
     *      WizCryptBean wcb)
     */
    public void encrypt(final InputStream is, final OutputStream os, final char[] password) 
            throws IOException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            NoSuchPaddingException{
        WizCryptBean wcb = new WizCryptBean();
        wcb.setPassword(password);
        encrypt(is, os, wcb);
    }
    
    /**
     * This is the public API exposed to decrypt.
     * 
     * 
     * @param is The input stream that needs to be decrypted.
     * @param os The output stream where the decrypted content 
     *      of <code>is</code> need to be written.
     * @param wcb The <code>WizCryptBean</code> object. This object encapsulates
     *      various WizCrypt needed parameters like password, <code>Callback</code>
     *      object.
     * @see WizCryptBean
     * @throws PasswordMismatchException <code>PasswordMismatchException</code> 
     *      is thrown when the supplied password is wrong.
     * @throws IOException <code>IOException</code> is thrown when faced with 
     *      IO issues during read/write.
     * @throws FileFormatException <code>FileFormatException</code> is thrown
     *      when the file to decrypt does not match the specification.
     * @throws NoSuchAlgorithmException <code>NoSuchAlgorithmException</code> is
     *      thrown when the JVM's JCE does not have the requested algorithm installed.
     * @throws InvalidKeyException <code>InvalidKeyException</code> is thrown
     *      when the key is not of the specified size/range as required by the
     *      specified algorithm.
     * @throws NoSuchPaddingException <code>NoSuchPaddingException</code> is
     *      thrown when a particular padding mechanism is requested but is not 
     *      available in the environment.
     */
    abstract public void decrypt(final InputStream is, final OutputStream os, 
            final WizCryptBean wcb) 
            throws IOException,
            PasswordMismatchException,
            FileFormatException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            NoSuchPaddingException;
    
    /**
     * Does decryption using the default configurations.
     * @see #decrypt(InputStream is, OutputStream os, 
     *      WizCryptBean wcb)
     */
    public void decrypt(final InputStream is, final OutputStream os, final char[] password) 
            throws IOException,
            PasswordMismatchException,
            FileFormatException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            NoSuchPaddingException{
        WizCryptBean wcb = new WizCryptBean();
        wcb.setPassword(password);
        decrypt(is, os, wcb);
    }
}
