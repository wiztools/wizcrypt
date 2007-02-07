/*
 * WizCrypt07.java
 *
 * Created on 06 February 2007, 09:13
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
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import org.wiztools.wizcrypt.Callback;
import org.wiztools.wizcrypt.CipherKey;
import org.wiztools.wizcrypt.FileFormatVersion;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;
import org.wiztools.wizcrypt.WizCrypt;
import org.wiztools.wizcrypt.WizCryptAlgorithms;

/**
 *
 * @author subhash
 */
public class WizCrypt07 extends WizCrypt {
    
    private static final Logger LOG = Logger.getLogger(WizCrypt07.class.getName());
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    /** Creates a new instance of WizCrypt07 */
    public WizCrypt07() {
    }
    
    public void encrypt(final InputStream is, final OutputStream os, 
            final CipherKey ck, final Callback cb, final long size) throws IOException{
        
        CipherInputStream cis = null;
        OutputStream gos = new GZIPOutputStream(os);
        try{
            if(cb != null){
                cb.begin();
            }
            
            cis = new CipherInputStream(is, ck.cipher);
            
            // Write the file-format magic number
            byte[] versionStr = FileFormatVersion.WC07.getBytes(WizCryptAlgorithms.STR_ENCODE);
            os.write(versionStr);
            
            // Write the hash in next 16 bytes
            gos.write(ck.passKeyHash);
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=cis.read(buffer)) != -1){
                gos.write(buffer, 0, i);
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
                if(gos != null){
                    gos.close();
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
        InputStream gis = new GZIPInputStream(is);
        try{
            if(cb != null){
                cb.begin();
            }
            
            // Read the magic number
            byte[] versionStr = FileFormatVersion.WC07.getBytes(WizCryptAlgorithms.STR_ENCODE);
            int versionByteLen = versionStr.length;
            byte[] magicNumber = new byte[versionByteLen];
            int bytesRead = is.read(magicNumber, 0, versionByteLen);
            if(bytesRead < versionByteLen){
                // TODO throw exception
            }
            LOG.finest("magicNumber: "+new String(magicNumber));
            
            // read 16 bytes from fis
            byte[] filePassKeyHash = new byte[16];
            bytesRead = gis.read(filePassKeyHash, 0, 16);
            if(bytesRead < 16){
                // TODO throw exception
            }
            
            if(!Arrays.equals(ck.passKeyHash, filePassKeyHash)){
                throw new PasswordMismatchException(rb.getString("err.pwd.not.match"));
            }
            
            cos = new CipherOutputStream(os, ck.cipher);
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=gis.read(buffer)) != -1){
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
                if(gis != null){
                    gis.close();
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
