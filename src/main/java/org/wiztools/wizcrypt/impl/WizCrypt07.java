/*
 * WizCrypt07.java
 *
 * Created on 06 February 2007, 09:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import org.wiztools.wizcrypt.Callback;
import org.wiztools.wizcrypt.CipherHashGen;
import org.wiztools.wizcrypt.FileFormatVersion;
import org.wiztools.wizcrypt.WizCryptBean;
import org.wiztools.wizcrypt.exception.FileFormatException;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;
import org.wiztools.wizcrypt.WizCrypt;
import org.wiztools.wizcrypt.WizCryptAlgorithms;

/**
 *
 * @author subhash
 */
public class WizCrypt07 extends WizCrypt {
    
    private static final Logger LOG = Logger.getLogger(WizCrypt07.class.getName());
    
    /** Creates a new instance of WizCrypt07 */
    public WizCrypt07() {
    }
    
    public void encrypt(final InputStream is, final OutputStream os, 
            final WizCryptBean wcb) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        
        OutputStream gos = null;
        CipherInputStream cis = null;
        Callback cb = wcb.getCallback();
        final long sizeOfStream = cb==null?-1:cb.getSize();
        try{
            byte[] pwd = new String(wcb.getPassword()).getBytes(WizCryptAlgorithms.STR_ENCODE);
            
            if(cb != null){
                cb.begin();
            }
            
            // Write the file-format magic number
            byte[] versionStr = FileFormatVersion.WC07.getBytes(WizCryptAlgorithms.STR_ENCODE);
            LOG.fine("Length of bytearray containing version: "+versionStr.length);
            os.write(versionStr, 0, versionStr.length);
            os.flush();
            
            cis = new CipherInputStream(is, CipherHashGen.getCipherForEncrypt(pwd, wcb.getAlgo()));
            
            // Write the hash in next 16 bytes
            gos = new GZIPOutputStream(os);
            LOG.fine("Length of Sha hash: "+CipherHashGen.getPasswordSha256Hash(pwd).length);
            gos.write(CipherHashGen.getPasswordSha256Hash(pwd));
            
            // Length of Algorithm
            final String ALGO = wcb.getAlgo();
            String lenStr = null;
            int len = ALGO.length();
            if(len < 10){ // add 0 padding
                lenStr = "0" + String.valueOf(len);
            }
            else{
                lenStr = String.valueOf(len);
            }
            gos.write(lenStr.getBytes(WizCryptAlgorithms.STR_ENCODE));
            gos.write(ALGO.getBytes(WizCryptAlgorithms.STR_ENCODE));
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=cis.read(buffer)) != -1){
                gos.write(buffer, 0, i);
                readSize += i;
                if(cb != null){
                    if(sizeOfStream == -1){
                        cb.notifyProgress(readSize);
                    }
                    else{
                        cb.notifyProgress(readSize * 100 / sizeOfStream);
                    }
                }
            }
        }
        finally{
            try{
                if(gos != null){
                    gos.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            try{
                if(cis != null){
                    cis.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            if(cb != null){
                cb.end();
            }
        }
    }
    
    public void decrypt(final InputStream is, final OutputStream os, 
            final WizCryptBean wcb) 
            throws IOException, PasswordMismatchException, FileFormatException,
            NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{

        CipherOutputStream cos = null;
        InputStream gis = null;
        Callback cb = wcb.getCallback();
        final long sizeOfStream = cb==null?-1:cb.getSize();
        try{
            byte[] pwd = new String(wcb.getPassword()).getBytes(WizCryptAlgorithms.STR_ENCODE);
            
            if(cb != null){
                cb.begin();
            }
            
            // Read the magic number
            byte[] versionStr = FileFormatVersion.WC07.getBytes(WizCryptAlgorithms.STR_ENCODE);
            int versionByteLen = versionStr.length;
            LOG.fine("Length of bytearray holding version: "+versionByteLen);
            byte[] magicNumber = new byte[versionByteLen];
            int bytesRead = is.read(magicNumber, 0, versionByteLen);
            LOG.fine("Magic number read: " + new String(magicNumber));
            if(!Arrays.equals(versionStr, magicNumber)){
                LOG.fine("Magic number does not match. . .");
                throw new FileFormatException();
            }
            if(bytesRead < versionByteLen){
                // TODO throw exception
            }
            LOG.finest("magicNumber: "+new String(magicNumber));
            
            // read 16 bytes from gis
            LOG.fine("Creating GZip stream!");
            gis = new GZIPInputStream(is);
            int LEN_OF_PWD_HASH = 32;
            byte[] filePassKeyHash = new byte[LEN_OF_PWD_HASH];
            bytesRead = gis.read(filePassKeyHash, 0, LEN_OF_PWD_HASH);
            if(bytesRead < LEN_OF_PWD_HASH){
                // TODO throw exception
            }
            
            byte[] passKeyHash = CipherHashGen.getPasswordSha256Hash(pwd);
            if(!Arrays.equals(passKeyHash, filePassKeyHash)){
                throw new PasswordMismatchException();
            }
            
            // next 2 bytes have the length of the algorithm
            byte[] algoNameLength = new byte[2];
            gis.read(algoNameLength, 0, 2);
            
            int len = -1;
            try{
                len = Integer.parseInt(new String(algoNameLength));
                LOG.fine("algorithm name length: "+len);
                if(len<1){
                    throw new FileFormatException();
                }
            }
            catch(NumberFormatException nfe){
                throw new FileFormatException();
            }
            
            // next few bytes have the algorithm name
            byte[] algoName = new byte[len];
            gis.read(algoName, 0, len);
            
            String algoNameStr = new String(algoName);
            LOG.fine("algo name: " + algoNameStr);
            
            cos = new CipherOutputStream(os, CipherHashGen.getCipherForDecrypt(pwd, algoNameStr));
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=gis.read(buffer)) != -1){
                cos.write(buffer, 0, i);
                readSize += i;
                if(cb != null){
                    if(sizeOfStream == -1){
                        cb.notifyProgress(readSize);
                    }
                    else{
                        cb.notifyProgress(readSize * 100 / sizeOfStream);
                    }
                }
            }
        }
        finally{
            try{
                if(cos != null){
                    cos.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            try{
                if(gis != null){
                    gis.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            if(cb != null){
                cb.end();
            }
        }
    }
}
