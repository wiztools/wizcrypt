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
 *
 * @author subhash
 */
public final class WizCrypt {
    
    private static final ResourceBundle rb = ResourceBundle.getBundle("wizcryptmsg");
    
    /** Creates a new instance of WizCrypt */
    private WizCrypt() {
    }
    
    public static void encrypt(InputStream is, OutputStream os, CipherEnsc ce) throws IOException{
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
    
    public static void decrypt(InputStream is, OutputStream os, CipherEnsc ce) throws IOException, PasswordMismatchException{
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
