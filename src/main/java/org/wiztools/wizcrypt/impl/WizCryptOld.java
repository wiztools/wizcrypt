/*
 * WizCryptOld.java
 *
 * Created on 06 February 2007, 09:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import org.wiztools.wizcrypt.Callback;
import org.wiztools.wizcrypt.CipherHashGen;
import org.wiztools.wizcrypt.WizCrypt;
import org.wiztools.wizcrypt.WizCryptAlgorithms;
import org.wiztools.wizcrypt.WizCryptBean;
import org.wiztools.wizcrypt.exception.FileFormatException;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;

/**
 *
 * @author subhash
 */
final class WizCryptOld {
    
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    private static WizCryptOld wco;
    
    public static WizCryptOld getInstance(){
        if(wco == null){
            wco = new WizCryptOld();
        }
        return wco;
    }
    
    /** Creates a new instance of WizCryptOld */
    private WizCryptOld() {
    }
    
    public void encrypt(final InputStream is, final OutputStream os,
            final WizCryptBean wcb) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        
        CipherInputStream cis = null;
        Callback cb = wcb.getCallback();
        final long sizeOfStream = cb==null?-1:cb.getSize();
        try{
            byte[] pwd = new String(wcb.getPassword()).getBytes(WizCryptAlgorithms.STR_ENCODE);
            
            if(cb != null){
                cb.begin();
            }
            
            cis = new CipherInputStream(is, CipherHashGen.getCipherForEncrypt(pwd, WizCryptAlgorithms.CRYPT_ALGO_RC4));
            // Write the hash in first 16 bytes
            byte[] passKeyHash = CipherHashGen.getPasswordMD5Hash(pwd);
            os.write(passKeyHash);
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=cis.read(buffer)) != -1){
                os.write(buffer, 0, i);
                readSize += i;
                if(cb != null){
                    if(sizeOfStream == -1){
                        cb.notifyProgress(readSize);
                    } else{
                        cb.notifyProgress(readSize * 100 / sizeOfStream);
                    }
                }
            }
        } finally{
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
    
    public void decrypt(final InputStream is, final OutputStream os,
            final WizCryptBean wcb)
            throws IOException, PasswordMismatchException, FileFormatException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        
        CipherOutputStream cos = null;
        Callback cb = wcb.getCallback();
        final long sizeOfStream = cb==null?-1:cb.getSize();
        try{
            byte[] pwd = new String(wcb.getPassword()).getBytes(WizCryptAlgorithms.STR_ENCODE);
            byte[] passKeyHash = CipherHashGen.getPasswordMD5Hash(pwd);
            
            Cipher cipher = CipherHashGen.getCipherForDecrypt(pwd, WizCryptAlgorithms.CRYPT_ALGO_RC4);

            if(cb != null){
                cb.begin();
            }
            
            // read 16 bytes from fis
            byte[] filePassKeyHash = new byte[16];
            is.read(filePassKeyHash, 0, 16);
            
            if(!Arrays.equals(passKeyHash, filePassKeyHash)){
                throw new PasswordMismatchException(rb.getString("err.pwd.not.match"));
            }
            
            cos = new CipherOutputStream(os, cipher);
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=is.read(buffer)) != -1){
                cos.write(buffer, 0, i);
                readSize += i;
                if(cb != null){
                    if(sizeOfStream == -1){
                        cb.notifyProgress(readSize);
                    } else{
                        cb.notifyProgress(readSize * 100 / sizeOfStream);
                    }
                }
            }
        } finally{
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
    
}
