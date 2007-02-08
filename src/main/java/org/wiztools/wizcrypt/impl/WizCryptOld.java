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
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import org.wiztools.wizcrypt.Callback;
import org.wiztools.wizcrypt.CipherKey;
import org.wiztools.wizcrypt.WizCrypt;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;

/**
 *
 * @author subhash
 */
public class WizCryptOld extends WizCrypt {
    
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    /** Creates a new instance of WizCryptOld */
    public WizCryptOld() {
    }
    
    public void encrypt(final InputStream is, final OutputStream os,
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
                    } else{
                        cb.notifyProgress(readSize * 100 / size);
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
                    } else{
                        cb.notifyProgress(readSize * 100 / size);
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
