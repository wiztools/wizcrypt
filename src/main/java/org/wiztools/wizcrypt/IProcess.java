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
    
    public void process(File file, WizCryptBean wcb, CliParamBean cpb)
    throws IOException,
            FileNotFoundException,
            DestinationFileExistsException,
            FileFormatException,
            PasswordMismatchException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchPaddingException;
    
    public void process(File file, File outFile, WizCryptBean wcb, CliParamBean cpb)
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
