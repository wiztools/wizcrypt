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
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

/**
 * This class has the public APIs of WizCrypt application to do encryption and decryption.
 * @see CipherKeyGen
 * @see CipherKey
 * @author subhash
 */
public final class WizCrypt {
    
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    /** Disallow public creation of instances of this object. */
    private WizCrypt() {
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
    public static void encrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck, final Callback cb, final long size) throws IOException{
        
        CipherInputStream cis = null;
        try{
            if(cb != null){
                cb.begin();
            }
            
            cis = new CipherInputStream(is, ck.cipher);
            // Write the hash in first 16 bytes
            os.write(ck.passKeyHash);
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=cis.read(buffer)) != -1){
                os.write(buffer, 0, i);
                readSize += i;
                if(cb != null){
                    if(size == -1){
                        cb.notifyProgress(readSize);
                    }
                    else{
                        cb.notifyProgress(readSize * 100 / size);
                    }
                }
            }
        }
        finally{
            try{
                if(os != null){
                    os.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            try{
                if(cis != null){
                    cis.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            if(cb != null){
                cb.end();
            }
        }
    }
    
    /**
     * @see #encrypt(InputStream is, OutputStream os, 
     *      CipherKey ck, Callback cb, long size)
     */
    public static void encrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck) throws IOException{
        encrypt(is, os, ck, null, -1);
    }
    
    /**
     * @see #encrypt(InputStream is, OutputStream os, 
     *      CipherKey ck, Callback cb, long size)
     */
    public static void encrypt(final InputStream is, final OutputStream os, 
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
    public static void decrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck, final Callback cb, final long size) throws IOException, PasswordMismatchException{
        
        CipherOutputStream cos = null;

        try{
            if(cb != null){
                cb.begin();
            }
            
            // read 16 bytes from fis
            byte[] filePassKeyHash = new byte[16];
            is.read(filePassKeyHash, 0, 16);
            
            if(!Arrays.equals(ck.passKeyHash, filePassKeyHash)){
                throw new PasswordMismatchException(rb.getString("err.pwd.not.match"));
            }
            
            cos = new CipherOutputStream(os, ck.cipher);
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=is.read(buffer)) != -1){
                cos.write(buffer, 0, i);
                readSize += i;
                if(cb != null){
                    if(size == -1){
                        cb.notifyProgress(readSize);
                    }
                    else{
                        cb.notifyProgress(readSize * 100 / size);
                    }
                }
            }
        }
        finally{
            try{
                if(cos != null){
                    cos.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            try{
                if(is != null){
                    is.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            if(cb != null){
                cb.end();
            }
        }
    }
    
    /**
     * @see #decrypt(InputStream is, OutputStream os, 
     *      CipherKey ck, Callback cb, long size)
     */
    public static void decrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck) throws IOException, PasswordMismatchException{
        decrypt(is, os, ck, null, -1);
    }
    
    /**
     * @see #decrypt(InputStream is, OutputStream os, 
     *      CipherKey ck, Callback cb, long size)
     */
    public static void decrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck, final Callback cb) throws IOException, 
                PasswordMismatchException{
        decrypt(is, os, ck, cb, -1);
    }
}
