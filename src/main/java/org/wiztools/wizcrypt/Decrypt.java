package org.wiztools.wizcrypt;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ResourceBundle;
import org.wiztools.wizcrypt.exception.DestinationFileExistsException;
import org.wiztools.wizcrypt.exception.FileFormatException;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;

/**
 * Class to do the decryption using the WizCrypt naming convention (*.wiz).
 */
public class Decrypt implements IProcess{
    
    private static final Logger LOG = Logger.getLogger(Decrypt.class.getName());
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    private String keyStr;
    
    public void process(final File file, final WizCryptBean wcb,
            final boolean forceOverwrite,
            final boolean keepSource,
            final boolean isOldFormat) 
            throws FileNotFoundException,
                DestinationFileExistsException,
                PasswordMismatchException,
                FileFormatException,
                IOException,
                NoSuchAlgorithmException,
                UnsupportedEncodingException,
                InvalidKeyException,
                NoSuchPaddingException{
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean canDelete = false;
        try{
            String path = file.getAbsolutePath();
            if(!path.endsWith(".wiz")){
                throw new FileNotFoundException(
                        MessageFormat.format(rb.getString("err.file.not.end.wiz"), path));
            }
            String newPath = path.replaceFirst(".wiz$", "");
            File outFile = new File(newPath);
            if(!forceOverwrite && outFile.exists()){
                throw new DestinationFileExistsException(
                        MessageFormat.format(rb.getString("err.destination.file.exists"),
                        outFile.getAbsolutePath()));
            }
            fis = new FileInputStream(file);
            fos = new FileOutputStream(outFile);
            WizCrypt wc = null;
            if(isOldFormat){
                wc = WizCrypt.getOldInstance();
            }
            else{
                wc = WizCrypt.get07Instance();
            }
            wc.decrypt(fis, fos, wcb);

            if(!keepSource){
                canDelete = true;
            }
        } finally{
            // fos & fis will be closed by WizCrypt.decrypt() API
            if(canDelete){
                LOG.fine("Deleting: " + file.getAbsolutePath());
                file.delete();
            }
        }
    }
}
