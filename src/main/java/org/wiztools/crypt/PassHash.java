package org.wiztools.crypt;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PassHash{

    private static final String ALGO = ResourceBundle.getBundle("wizcrypt").getString("algorithm");
    
    private static byte[] passHash(String password) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] raw = md.digest();
        return raw;
    }
    
    private static CipherEnsc getPassKeyHash(String keyStr, int mode) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        byte[] passKeyHash = null;
        Cipher cipher = null;

        SecretKey key = new SecretKeySpec(keyStr.getBytes(), ALGO);
        cipher = Cipher.getInstance(ALGO);
        cipher.init(mode, key);
        passKeyHash = PassHash.passHash(keyStr);
        
        CipherEnsc ce = new CipherEnsc(cipher, passKeyHash);
        
        return ce;
    }
    
    public static CipherEnsc getPassKeyHashForEncrypt(String keyStr) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        return getPassKeyHash(keyStr, Cipher.ENCRYPT_MODE);
    }
    
    public static CipherEnsc getPassKeyHashForDecrypt(String keyStr) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        return getPassKeyHash(keyStr, Cipher.DECRYPT_MODE);
    }
}
