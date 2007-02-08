package org.wiztools.wizcrypt;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
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
    
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    private CipherKey ce;
    
    public void init(final String keyStr)
        throws 
            NoSuchAlgorithmException,
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchPaddingException{
        ce = CipherKeyGen.getCipherKeyForEncrypt(keyStr);
    }
    
    public void process(final File file, final boolean forceOverwrite, final boolean isOldFormat) 
            throws FileNotFoundException, DestinationFileExistsException, IOException{
        FileOutputStream fos = null;
        FileInputStream fis = null;
        boolean canDelete = false;
        try{
            File outFile = new File(file.getAbsolutePath()+".wiz");
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
            wc.encrypt(fis, fos, ce);
            canDelete = true;
        } finally{
            // fos & fis will be closed by WizCrypt.encrypt() API
            if(canDelete){
                file.delete();
            }
        }
    }
    
    public void process(final File file, final boolean forceOverwrite) 
            throws FileNotFoundException, DestinationFileExistsException, IOException{
        process(file, forceOverwrite, false);
    }
}
