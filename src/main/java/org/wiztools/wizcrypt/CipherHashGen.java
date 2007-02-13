package org.wiztools.wizcrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import static org.wiztools.wizcrypt.WizCryptAlgorithms.PWD_HASH;
import static org.wiztools.wizcrypt.WizCryptAlgorithms.STR_ENCODE;
import static org.wiztools.wizcrypt.WizCryptAlgorithms.CRYPT_ALGO_RC4;
import static org.wiztools.wizcrypt.WizCryptAlgorithms.CRYPT_ALGO_AES;

/**
 * This class has static methods to create <code>CipherKey</code> objects.
 * @see CipherKey
 * @see WizCrypt
 */
public final class CipherHashGen{

    /** Disallow public creation of instances of this class. */
    private CipherHashGen(){
    }
    
    public static byte[] passHash(final byte[] passKey)
            throws NoSuchAlgorithmException,
                UnsupportedEncodingException{
        MessageDigest md = MessageDigest.getInstance(PWD_HASH);
        md.update(passKey);
        byte[] raw = md.digest();
        return raw;
    }
    
    private static Cipher getCipher(final byte[] passKey, final int mode, final String algo)
                throws NoSuchAlgorithmException,
                    UnsupportedEncodingException,
                    InvalidKeyException, 
                    NoSuchPaddingException{
        Cipher cipher = null;
        
        SecretKey key = new SecretKeySpec(passKey, algo);
        cipher = Cipher.getInstance(algo);
        cipher.init(mode, key);
        return cipher;
    }
    
    public static Cipher getCipherForEncrypt(final byte[] passKey, final String algo)
                throws NoSuchAlgorithmException,
                    UnsupportedEncodingException,
                    InvalidKeyException, 
                    NoSuchPaddingException{
        return getCipher(passKey, Cipher.ENCRYPT_MODE, algo);
    }
    
    public static Cipher getCipherForDecrypt(final byte[] passKey, final String algo)
                throws NoSuchAlgorithmException,
                    UnsupportedEncodingException,
                    InvalidKeyException, 
                    NoSuchPaddingException{
        return getCipher(passKey, Cipher.DECRYPT_MODE, algo);
    }
}
