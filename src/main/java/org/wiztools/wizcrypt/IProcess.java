package org.wiztools.wizcrypt;

import java.io.UnsupportedEncodingException;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.wiztools.wizcrypt.exception.DestinationFileExistsException;
import org.wiztools.wizcrypt.exception.FileFormatException;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;

public interface IProcess{
    
    public void process(File file, WizCryptBean wcb, boolean forceOverwrite,
            final boolean keepSource,
            boolean isOldFormat)
    throws IOException,
            FileNotFoundException,
            DestinationFileExistsException,
            FileFormatException,
            PasswordMismatchException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchPaddingException;
    
}
