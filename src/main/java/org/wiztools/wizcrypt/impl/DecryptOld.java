/*
 * DecryptOld.java
 *
 * Created on April 26, 2007, 2:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt.impl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import org.wiztools.wizcrypt.Callback;
import org.wiztools.wizcrypt.IProcess;
import org.wiztools.wizcrypt.ParamBean;
import org.wiztools.wizcrypt.WizCryptBean;
import org.wiztools.wizcrypt.exception.DestinationFileExistsException;
import org.wiztools.wizcrypt.exception.FileFormatException;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;

/**
 *
 * @author schandran
 */
public class DecryptOld implements IProcess {
    
    private static final Logger LOG = Logger.getLogger(DecryptOld.class.getName());
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    /** Creates a new instance of DecryptOld */
    public DecryptOld() {
    }

    public void process(File file, WizCryptBean wcb, ParamBean cpb) throws IOException, FileNotFoundException, DestinationFileExistsException, FileFormatException, PasswordMismatchException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException {
        process(file, null, wcb, cpb);
    }

    public void process(File file, File outFile, WizCryptBean wcb, ParamBean cpb) throws IOException, FileNotFoundException, DestinationFileExistsException, FileFormatException, PasswordMismatchException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException {
        final boolean forceOverwrite = cpb.getForceOverwrite();
        final boolean keepSource = cpb.getKeepSource();
        
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean canDelete = false;
        
        CipherOutputStream cos = null;
        Callback cb = wcb.getCallback();
        final long sizeOfStream = cb==null?-1:cb.getSize();
        
        // This variable should be set to true after successful processing of
        // Decryption. Remember, if any branch condition creeps into this code
        // subsequently, this has to be updated in that branch.
        boolean isSuccessful = false;
        try{
            String path = file.getAbsolutePath();
            if(!path.endsWith(".wiz")){
                throw new FileNotFoundException(
                        MessageFormat.format(rb.getString("err.file.not.end.wiz"), path));
            }
            String newPath = path.replaceFirst(".wiz$", "");
            if(outFile == null){
                outFile = new File(newPath);
            }
            if(!forceOverwrite && outFile.exists()){
                throw new DestinationFileExistsException(
                        MessageFormat.format(rb.getString("err.destination.file.exists"),
                        outFile.getAbsolutePath()));
            }
            
            fis = new FileInputStream(file);
            fos = new FileOutputStream(outFile);
            
            WizCryptOld wco = WizCryptOld.getInstance();
            wco.decrypt(fis, fos, wcb);
            
            if(!keepSource){
                canDelete = true;
            }
            
            isSuccessful = true;
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
                if(fis != null){
                    fis.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            if(cb != null){
                cb.end();
            }
            // fos & fis will be closed by WizCrypt.decrypt() API
            if(canDelete){
                LOG.fine("Deleting: " + file.getAbsolutePath());
                file.delete();
            }
            if(!isSuccessful){
                if(outFile != null && outFile.exists()){
                    LOG.fine("Deleting (Not successful): " + outFile.getAbsolutePath());
                    outFile.delete();
                }
            }
        }
    }
    
}
