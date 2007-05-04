package org.wiztools.wizcrypt;

import java.io.UnsupportedEncodingException;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.wiztools.wizcrypt.exception.DestinationFileExistsException;
import org.wiztools.wizcrypt.exception.FileCorruptException;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;
import org.wiztools.wizcrypt.impl.Decrypt07;
import org.wiztools.wizcrypt.impl.DecryptOld;
import org.wiztools.wizcrypt.impl.Encrypt07;
import org.wiztools.wizcrypt.impl.EncryptOld;

public abstract class IProcess{
    
    private static final String LATEST_VERSION = "07";
    
    // Keep all version definition lower case
    public static final String VERSION_07 = "07";
    public static final String VERSION_OLD = "old";
    
    public static IProcess getEncryptInstance(){
        return getEncryptInstance(LATEST_VERSION);
    }
    
    public static IProcess getDecryptInstance(){
        return getDecryptInstance(LATEST_VERSION);
    }
    
    public static IProcess getEncryptInstance(final String version){
        String ver = version.toLowerCase();
        IProcess p = null;
        if(VERSION_07.equals(ver)){
            p = Encrypt07.getInstance();
        }
        else if(VERSION_OLD.equals(ver)){
            p = EncryptOld.getInstance();
        }
        return p;
    }
    
    public static IProcess getDecryptInstance(final String version){
        String ver = version.toLowerCase();
        IProcess p = null;
        if(VERSION_07.equals(ver)){
            p = Decrypt07.getInstance();
        }
        else if(VERSION_OLD.equals(ver)){
            p = DecryptOld.getInstance();
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
