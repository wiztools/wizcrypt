package org.wiztools.crypt;

import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Encrypt implements IProcess{
    
    private CipherEnsc ce;
    
    public void init(String keyStr) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        ce = PassHash.getPassKeyHashForEncrypt(keyStr);
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
