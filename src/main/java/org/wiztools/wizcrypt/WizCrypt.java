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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;
import org.wiztools.wizcrypt.impl.WizCrypt07;
import org.wiztools.wizcrypt.impl.WizCryptOld;

/**
 * This class has the public APIs of WizCrypt application to do encryption and decryption.
 * @see CipherKeyGen
 * @see CipherKey
 * @author subhash
 */
public abstract class WizCrypt {
    
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    private static WizCrypt __instance07;
    private static WizCrypt __instanceOld;
    
    public static WizCrypt get07Instance(){
        if(__instance07 == null){
            __instance07 = new WizCrypt07();
        }
        return __instance07;
    }
    
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
     *     of <code>is</code> need to be written.
     * @param ck The <code>CipherKey</code> object. This has to be 
     *     created by passing the password to 
     *     <code>CipherKeyGen.getCipherKeyForEncrypt(String keyStr)</code>.
     * @param cb <code>Callback</code> object for monitoring the progress.
     * @param size The size of the <code>is</code> stream. When this is passed,
     *      the <code>Callback.notifyProgress(long value)</code> will receive
     *      the percentage completed. If this is not passed, it will get the
     *      number of bytes read and processed.
     * @see CipherKeyGen#getCipherKeyForEncrypt(String keyStr)
     * @see CipherKey
     * @see Callback
     * @throws IOException <code>IOException</code> is thrown when 
     *     faced with IO issues during read/write.
     */
    abstract public void encrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck, final Callback cb, final long size) throws IOException;
    
    /**
     * @see #encrypt(InputStream is, OutputStream os, 
     *      CipherKey ck, Callback cb, long size)
     */
    public void encrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck) throws IOException{
        encrypt(is, os, ck, null, -1);
    }
    
    /**
     * @see #encrypt(InputStream is, OutputStream os, 
     *      CipherKey ck, Callback cb, long size)
     */
    public void encrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck, final Callback cb) throws IOException{
        encrypt(is, os, ck, cb, -1);
    }
    
    /**
     * This is the public API exposed to decrypt.
     * 
     * 
     * @param is The input stream that needs to be decrypted.
     * @param os The output stream where the decrypted content 
     *      of <code>is</code> need to be written.
     * @param ck The <code>CipherKey</code> object. This has to be 
     *      created by passing the password to 
     *      <code>CipherKeyGen.getCipherKeyForDecrypt(String keyStr)</code>.
     * @param cb <code>Callback</code> object for monitoring the progress.
     * @param size The size of the <code>is</code> stream. When this is passed,
     *      the <code>Callback.notifyProgress(long value)</code> will receive
     *      the percentage completed. If this is not passed, it will get the
     *      number of bytes read and processed.
     * @see CipherKeyGen#getCipherKeyForDecrypt(String keyStr)
     * @see CipherKey
     * @see Callback
     * @throws PasswordMismatchException <code>PasswordMismatchException</code> 
     *      is thrown when the supplied password is wrong.
     * @throws IOException <code>IOException</code> is thrown when faced with 
     *      IO issues during read/write.
     */
    abstract public void decrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck, final Callback cb, final long size) throws IOException, PasswordMismatchException;
    
    /**
     * @see #decrypt(InputStream is, OutputStream os, 
     *      CipherKey ck, Callback cb, long size)
     */
    public void decrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck) throws IOException, PasswordMismatchException{
        decrypt(is, os, ck, null, -1);
    }
    
    /**
     * @see #decrypt(InputStream is, OutputStream os, 
     *      CipherKey ck, Callback cb, long size)
     */
    public void decrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck, final Callback cb) throws IOException, 
                PasswordMismatchException{
        decrypt(is, os, ck, cb, -1);
    }
}
