/*
 * WizCrypt.java
 *
 * Created on 23 December 2006, 09:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.crypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

/**
 * This class has the public APIs of WizCrypt application to do encryption and decryption.
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
     * @param is The input stream that needs to be encrypted.
     * @param os The output stream where the encrypted content of <code>is</code> need to be written.
     * @param ce The <code>CipherKey</code> object. This has to be created by passing the password to <code>PassHash.getPassKeyHashForEncrypt(String keyStr)</code>.
     * @throws IOException <code>IOException</code> is thrown when faced with IO issues during read/write.
     * @see PassHash#getPassKeyHashForEncrypt(String keyStr)
     * @see CipherKey
     */
    public static void encrypt(final InputStream is, final OutputStream os, 
            final CipherKey ce) throws IOException{
        
        CipherInputStream cis = null;
        try{
            cis = new CipherInputStream(is, ce.cipher);
            // Write the hash in first 16 bytes
            os.write(ce.passKeyHash);
            
            int i = -1;
            while((i=cis.read()) != -1){
                os.write((char)i);
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
        }
    }
    
    /**
     * This is the public API exposed to decrypt.
     * 
     * @param is The input stream that needs to be decrypted.
     * @param os The output stream where the decrypted content of <code>is</code> need to be written.
     * @param ce The <code>CipherKey</code> object. This has to be created by passing the password to <code>PassHash.getPassKeyHashForDecrypt(String keyStr)</code>.
     * @throws PasswordMismatchException <code>PasswordMismatchException</code> is thrown when the supplied password is wrong.
     * @throws IOException <code>IOException</code> is thrown when faced with IO issues during read/write.
     * @see PassHash#getPassKeyHashForDecrypt(String keyStr)
     * @see CipherKey
     * 
     */
    public static void decrypt(final InputStream is, final OutputStream os, 
            final CipherKey ce) throws IOException, PasswordMismatchException{
        
        CipherOutputStream cos = null;

        try{
            // read 16 bytes from fis
            byte[] filePassKeyHash = new byte[16];
            is.read(filePassKeyHash, 0, 16);
            
            if(!Arrays.equals(ce.passKeyHash, filePassKeyHash)){
                throw new PasswordMismatchException(rb.getString("err.pwd.not.match"));
            }
            
            cos = new CipherOutputStream(os, ce.cipher);
            
            int i = -1;
            while((i=is.read()) != -1){
                cos.write((char)i);
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
        }
    }
    
}
