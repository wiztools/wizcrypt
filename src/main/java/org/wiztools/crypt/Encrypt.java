package org.wiztools.crypt;

import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ResourceBundle;

public class Encrypt implements IProcess{
    private final String ALGO = ResourceBundle.getBundle("wizcrypt").getString("algorithm");
    
    private Cipher cipher;
    private byte[] passKeyHash;
    
    public void init(String keyStr) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        SecretKey key = new SecretKeySpec(keyStr.getBytes(), ALGO);
        cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        passKeyHash = PassHash.passHash(keyStr);
    }
    
    public void process(File file) throws IOException, FileNotFoundException{
        FileOutputStream fos = null;
        CipherInputStream cis = null;
        boolean canDelete = false;
        try{
            File outFile = new File(file.getCanonicalPath()+".wiz");
            cis = new CipherInputStream(new FileInputStream(file), cipher);
            fos = new FileOutputStream(outFile);
            // Write the hash in first 16 bytes
            fos.write(passKeyHash);
            
            int i = -1;
            while((i=cis.read()) != -1){
                fos.write((char)i);
            }
            canDelete = true;
        } finally{
            try{
                if(fos != null){
                    fos.close();
                }
            } catch(IOException ioe){
                // Log Exception
            }
            try{
                if(cis != null){
                    cis.close();
                }
            } catch(IOException ioe){
                // Log Exception
            }
            if(canDelete){
                file.delete();
            }
        }
    }
}
