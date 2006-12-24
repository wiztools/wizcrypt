package org.wiztools.wizcrypt;

import javax.crypto.Cipher;

/**
 * This is a Bean like class which just holds data.
 * @see CipherKeyGen
 * @see WizCrypt
 */
public final class CipherKey{
    
    // Disallow external classes to create default instance
    private CipherKey(){
        cipher = null;
        passKeyHash = null;
    }
    
    /**
     * The only constructor to create CipherKey object.
     *
     * @param cipher The cipher object created using the password.
     * @param passKeyHash The MD5 hash of the password.
     */
    public CipherKey(Cipher cipher, byte[] passKeyHash){
        this.cipher = cipher;
        this.passKeyHash = passKeyHash;
    }
    
    /** The cipher object created using the password. */
    public final Cipher cipher;
    
    /** The MD5 hash of the password. */
    public final byte[] passKeyHash;
}