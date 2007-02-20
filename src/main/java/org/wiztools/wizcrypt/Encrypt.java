package org.wiztools.wizcrypt;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.wiztools.wizcrypt.exception.DestinationFileExistsException;

/**
 * Class to do the encryption using the WizCrypt naming convention (*.wiz).
 */
public class Encrypt implements IProcess{
    
    private static final Logger LOG = Logger.getLogger(Encrypt.class.getName());
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    public void process(final File file, final WizCryptBean wcb, final CliParamBean cpb) 
            throws FileNotFoundException, DestinationFileExistsException, IOException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchPaddingException{
        
        final boolean forceOverwrite = cpb.getForceOverwrite();
        final boolean keepSource = cpb.getKeepSource();
        final boolean isOldFormat = cpb.getIsOldFormat();
        
        FileOutputStream fos = null;
        FileInputStream fis = null;
        boolean canDelete = false;
        File outFile = null;
        try{
            outFile = new File(file.getAbsolutePath()+".wiz");
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
            wc.encrypt(fis, fos, wcb);
            if(!keepSource){
                canDelete = true;
            }
        }
        catch(FileNotFoundException ex){
            if(outFile != null){
                LOG.fine("Deleting: " + outFile.getAbsolutePath());
                outFile.delete();
            }
            throw ex;
        }
        catch(DestinationFileExistsException ex){
            if(outFile != null){
                LOG.fine("Deleting: " + outFile.getAbsolutePath());
                outFile.delete();
            }
            throw ex;
        }
        catch(IOException ex){
            if(outFile != null){
                LOG.fine("Deleting: " + outFile.getAbsolutePath());
                outFile.delete();
            }
            throw ex;
        }
        catch(NoSuchAlgorithmException ex){
            if(outFile != null){
                LOG.fine("Deleting: " + outFile.getAbsolutePath());
                outFile.delete();
            }
            throw ex;
        }
        catch(InvalidKeyException ex){
            if(outFile != null){
                LOG.fine("Deleting: " + outFile.getAbsolutePath());
                outFile.delete();
            }
            throw ex;
        }
        catch(NoSuchPaddingException ex){
            if(outFile != null){
                LOG.fine("Deleting: " + outFile.getAbsolutePath());
                outFile.delete();
            }
            throw ex;
        }
        finally{
            // fos & fis will be closed by WizCrypt.encrypt() API
            if(canDelete){
                LOG.fine("Deleting file: " + file.getAbsolutePath());
                file.delete();
            }
        }
    }
}
