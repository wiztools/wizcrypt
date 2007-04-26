/*
 * EncryptOld.java
 *
 * Created on April 26, 2007, 2:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.crypto.CipherInputStream;
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
public final class EncryptOld extends IProcess {
    
    private static final Logger LOG = Logger.getLogger(EncryptOld.class.getName());
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    private static EncryptOld _instance;
    
    private EncryptOld(){
        
    }
    
    public static IProcess getInstance(){
        if(_instance == null){
            _instance = new EncryptOld();
        }
        return _instance;
    }

    public void process(final File inFile, final WizCryptBean wcb, final ParamBean cpb) throws IOException, FileNotFoundException, DestinationFileExistsException, FileFormatException, PasswordMismatchException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException {
        process(inFile, null, wcb, cpb);
    }

    public void process(final File file, File outFileTmp, final WizCryptBean wcb, final ParamBean cpb) throws IOException, FileNotFoundException, DestinationFileExistsException, FileFormatException, PasswordMismatchException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException {
        final boolean forceOverwrite = cpb.getForceOverwrite();
        final boolean keepSource = cpb.getKeepSource();
        
        FileOutputStream fos = null;
        FileInputStream fis = null;
        boolean canDelete = false;
        RandomAccessFile outFile = null;
        
        CipherInputStream cis = null;
        Callback cb = wcb.getCallback();
        
        // This variable should be set to true after successful processing of
        // Encryption. Remember, if any branch condition creeps into this code
        // subsequently, this has to be updated in that branch.
        boolean isSuccessful = false;
        try{
            if(outFileTmp == null){
                outFileTmp = new File(file.getAbsolutePath()+".wiz");
            }
            if(!forceOverwrite && outFileTmp.exists()){
                throw new DestinationFileExistsException(
                        MessageFormat.format(rb.getString("err.destination.file.exists"),
                        outFileTmp.getAbsolutePath()));
            }
            fis = new FileInputStream(file);
            
            outFile = new RandomAccessFile(outFileTmp, "rw");
            if(!keepSource){
                canDelete = true;
            }
            
            isSuccessful = true;
        }
        finally{
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
            if(outFile != null){
                try{
                    outFile.close();
                }
                catch(IOException ioe){
                    System.err.println(ioe.getMessage());
                }
            }
            // fos & fis will be closed by WizCrypt.encrypt() API
            if(canDelete){
                LOG.fine("Deleting file: " + file.getAbsolutePath());
                file.delete();
            }
            // delete corrupt outFile
            if(!isSuccessful){
                if(outFileTmp != null && outFileTmp.exists()){
                    LOG.fine("Deleting: " + outFileTmp.getAbsolutePath());
                    outFileTmp.delete();
                }
            }
        }
    }
    
}
