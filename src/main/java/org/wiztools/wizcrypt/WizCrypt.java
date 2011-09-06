package org.wiztools.wizcrypt;

import java.io.UnsupportedEncodingException;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public abstract class WizCrypt{
    
    private static final Version LATEST_VERSION = Version.WC07;
    
    public static WizCrypt getEncryptInstance(){
        return getEncryptInstance(LATEST_VERSION);
    }
    
    public static WizCrypt getDecryptInstance(){
        return getDecryptInstance(LATEST_VERSION);
    }
    
    public static WizCrypt getEncryptInstance(final Version version){
        WizCrypt p = null;
        if(Version.WC07 == version){
            p = Encrypt07.getInstance();
        }
        return p;
    }
    
    public static WizCrypt getDecryptInstance(final Version version){
        WizCrypt p = null;
        if(Version.WC07 == version){
            p = Decrypt07.getInstance();
        }
        return p;
    }
    
    public abstract void process(File file, WizCryptBean wcb, ParamBean cpb)
    throws IOException,
            FileNotFoundException,
            DestinationFileExistsException,
            FileCorruptException,
            PasswordMismatchException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchPaddingException;
    
    public abstract void process(File file, File outFile, WizCryptBean wcb, ParamBean cpb)
    throws IOException,
            FileNotFoundException,
            DestinationFileExistsException,
            FileCorruptException,
            PasswordMismatchException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchPaddingException;
    
}
