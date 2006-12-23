package org.wiztools.crypt;

import javax.crypto.Cipher;


public class CipherEnsc{
    
    // Disallow external classes to create default instance
    private CipherEnsc(){
        cipher = null;
        passKeyHash = null;
    }
    
    public CipherEnsc(Cipher cipher, byte[] passKeyHash){
        this.cipher = cipher;
        this.passKeyHash = passKeyHash;
    }
    
    public final Cipher cipher;
    public final byte[] passKeyHash;
}