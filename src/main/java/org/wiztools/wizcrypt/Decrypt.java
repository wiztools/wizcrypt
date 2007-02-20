package org.wiztools.wizcrypt;

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
    
    public void process(final File file, final WizCryptBean wcb,
            final CliParamBean cpb) 
            throws FileNotFoundException,
                DestinationFileExistsException,
                PasswordMismatchException,
                FileFormatException,
                IOException,
                NoSuchAlgorithmException,
                InvalidKeyException,
                NoSuchPaddingException{
        
        final boolean forceOverwrite = cpb.getForceOverwrite();
        final boolean keepSource = cpb.getKeepSource();
        final boolean isOldFormat = cpb.getIsOldFormat();
        
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean canDelete = false;
        File outFile = null;
        
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
            outFile = new File(newPath);
            if(!forceOverwrite && outFile.exists()){
                throw new DestinationFileExistsException(
                        MessageFormat.format(rb.getString("err.destination.file.exists"),
                        outFile.getAbsolutePath()));
            }
            
            WizCrypt wc = null;
            if(isOldFormat){
                wc = WizCrypt.getOldInstance();
            }
            else{
                wc = WizCrypt.get07Instance();
            }
            
            fis = new FileInputStream(file);
            fos = new FileOutputStream(outFile);
            
            wc.decrypt(fis, fos, wcb);

            if(!keepSource){
                canDelete = true;
            }
            
            isSuccessful = true;
        }
        finally{
            // fos & fis will be closed by WizCrypt.decrypt() API
            if(canDelete){
                LOG.fine("Deleting: " + file.getAbsolutePath());
                file.delete();
            }
            if(!isSuccessful){
                if(outFile != null && outFile.exists()){
                    LOG.fine("Deleting: " + outFile.getAbsolutePath());
                    outFile.delete();
                }
            }
        }
    }
}
