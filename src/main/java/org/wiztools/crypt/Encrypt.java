package org.wiztools.crypt;

import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Class to do the encryption using the WizCrypt naming convention (*.wiz).
 */
public class Encrypt implements IProcess{
    
    private CipherKey ce;
    
    public void init(String keyStr) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        ce = CipherKeyGen.getCipherKeyForEncrypt(keyStr);
    }
    
    public void process(File file) throws IOException, FileNotFoundException{
        FileOutputStream fos = null;
        FileInputStream fis = null;
        boolean canDelete = false;
        try{
            File outFile = new File(file.getCanonicalPath()+".wiz");
            fis = new FileInputStream(file);
            fos = new FileOutputStream(outFile);
            WizCrypt.encrypt(fis, fos, ce);
            canDelete = true;
        } finally{
            // fos & fis will be closed by WizCrypt.encrypt() API
            if(canDelete){
                file.delete();
            }
        }
    }
}
