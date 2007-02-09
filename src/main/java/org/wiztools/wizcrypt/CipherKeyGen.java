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
public final class CipherKeyGen{

    /** Disallow public creation of instances of this class. */
    private CipherKeyGen(){
    }
    
    public static byte[] passHash(final byte[] passKey)
            throws NoSuchAlgorithmException,
                UnsupportedEncodingException{
        MessageDigest md = MessageDigest.getInstance(PWD_HASH);
        md.update(passKey);
        byte[] raw = md.digest();
        return raw;
    }
    
    private static CipherKey getCipherKey(final String keyStr, final int mode, final String algo) 
                throws NoSuchAlgorithmException,
                    UnsupportedEncodingException,
                    InvalidKeyException, 
                    NoSuchPaddingException{
        byte[] passKeyHash = null;
        Cipher cipher = null;
        
        byte[] passKey = keyStr.getBytes(STR_ENCODE);

        SecretKey key = new SecretKeySpec(passKey, algo);
        cipher = Cipher.getInstance(algo);
        cipher.init(mode, key);
        passKeyHash = CipherKeyGen.passHash(passKey);
        
        CipherKey ck = new CipherKey(cipher, passKeyHash, algo);
        
        return ck;
    }
    
    /**
     * This is the public API used for getting the <code>CipherKey</code> object
     * used in <code>WizCrypt</code> public APIs.
     *
     * @param keyStr The password used for creating the cipher.
     * @throws NoSuchAlgorithmException When the Algorithm used by WizCrypt (RC4) is not found in JVM. This is highly unlikely, because SUN JVM has this.
     * @throws UnsupportedEncodingException Password provided is encoded using UTF-8. If UTF-8 encoder is not available in JVM, this exception is thrown. This exception is not likely to happen.
     * @throws InvalidKeyException The key should be of specific size. If this size limits are not met, <code>InvalidKeyException</code> exception is thrown.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment. This also is also highly unlikely to happen.
     * @return Returns the initialized <code>CipherKey</code> object for encryption.
     * @see WizCrypt#encrypt(InputStream is, OutputStream os, CipherKey ce)
     * @see CipherKey
     */
    public static CipherKey getCipherKeyForEncrypt(final String keyStr) 
            throws NoSuchAlgorithmException,
                UnsupportedEncodingException,
                InvalidKeyException,
                NoSuchPaddingException{
        return getCipherKey(keyStr, Cipher.ENCRYPT_MODE, CRYPT_ALGO_RC4);
    }
    
    /**
     * This is the public API used for getting the <code>CipherKey</code> object
     * used in <code>WizCrypt</code> public APIs.
     *
     * @param keyStr The password used for creating the cipher.
     * @throws NoSuchAlgorithmException When the Algorithm used by WizCrypt (RC4) is not found in JVM. This is highly unlikely, because SUN JVM has this.
     * @throws UnsupportedEncodingException Password provided is encoded using UTF-8. If UTF-8 encoder is not available in JVM, this exception is thrown. This exception is not likely to happen.
     * @throws InvalidKeyException The key should be of specific size. If this size limits are not met, <code>InvalidKeyException</code> exception is thrown.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment. This also is also highly unlikely to happen.
     * @return Returns the initialized <code>CipherKey</code> object for decryption.
     * @see WizCrypt#decrypt(InputStream is, OutputStream os, CipherKey ce)
     * @see CipherKey
     */
    public static CipherKey getCipherKeyForDecrypt(final String keyStr) 
            throws NoSuchAlgorithmException,
                UnsupportedEncodingException,
                InvalidKeyException,
                NoSuchPaddingException{
        return getCipherKey(keyStr, Cipher.DECRYPT_MODE, CRYPT_ALGO_RC4);
    }
    
    public static CipherKey getCipherKeyForDecrypt(final String keyStr, final String algoName) 
            throws NoSuchAlgorithmException,
                UnsupportedEncodingException,
                InvalidKeyException,
                NoSuchPaddingException{
        return getCipherKey(keyStr, Cipher.DECRYPT_MODE, algoName);
    }
}
