package org.wiztools.wizcrypt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.wiztools.wizcrypt.exception.DestinationFileExistsException;

/**
 * Class to do the encryption using the WizCrypt naming convention (*.wiz).
 */
public class Encrypt07 implements IProcess{
    
    private static final Logger LOG = Logger.getLogger(Encrypt07.class.getName());
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    public void process(final File inFile, final WizCryptBean wcb, final ParamBean cpb) 
            throws FileNotFoundException, DestinationFileExistsException, IOException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchPaddingException{
        process(inFile, null, wcb, cpb);
    }
    
    public void process(final File file, File outFileTmp, final WizCryptBean wcb, final ParamBean cpb) 
            throws FileNotFoundException, DestinationFileExistsException, IOException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchPaddingException{
        
        final boolean forceOverwrite = cpb.getForceOverwrite();
        final boolean keepSource = cpb.getKeepSource();
        final boolean isOldFormat = cpb.getIsOldFormat();
        
        FileOutputStream fos = null;
        FileInputStream fis = null;
        boolean canDelete = false;
        RandomAccessFile outFile = null;
        
        CipherInputStream cis = null;
        Callback cb = wcb.getCallback();
        
        // This variable should be set to true after successful processing of
        // Encryption. Remember, if any branch condition creeps into this code
        // subsequently, this has to be updated in that branch.
        boolean isSuccessful = false;
        try{
            if(outFileTmp == null){
                outFileTmp = new File(file.getAbsolutePath()+".wiz");
            }
            if(!forceOverwrite && outFileTmp.exists()){
                throw new DestinationFileExistsException(
                        MessageFormat.format(rb.getString("err.destination.file.exists"),
                        outFileTmp.getAbsolutePath()));
            }
            fis = new FileInputStream(file);
            
            outFile = new RandomAccessFile(outFileTmp, "rw");
            
            //***start encryption code
            
            final long sizeOfStream = cb==null?-1:cb.getSize();
            
            long crcHeaderSkipLen = 0;
            
            byte[] pwd = new String(wcb.getPassword()).getBytes(WizCryptAlgorithms.STR_ENCODE);
            
            if(cb != null){
                cb.begin();
            }
            
            // Byte array having header info: used for computing header CRC
            ByteArrayOutputStream headerByteArrayOS = new ByteArrayOutputStream();
            DataOutputStream headerOS = new DataOutputStream(headerByteArrayOS);
            
            // Write the file-format magic number
            byte[] versionStr = FileFormatVersion.WC07.getBytes(WizCryptAlgorithms.STR_ENCODE);
            LOG.fine("Length of bytearray containing version: "+versionStr.length);
            outFile.write(versionStr, 0, versionStr.length);
            crcHeaderSkipLen += versionStr.length;
            
            // Leave space for header CRC
            outFile.writeLong(0);
            crcHeaderSkipLen += 8; // Long takes 8 bytes

            cis = new CipherInputStream(fis, CipherHashGen.getCipherForEncrypt(pwd, WizCryptAlgorithms.CRYPT_ALGO_RC4));
            
            // Write password hash
            byte[] pwdHash = CipherHashGen.getPasswordSha256Hash(pwd);
            LOG.fine("Length of Sha hash: " + pwdHash.length);
            outFile.write(pwdHash);
            headerOS.write(pwdHash);
            crcHeaderSkipLen += pwdHash.length;
            
            // Skip bytes for CRC info
            outFile.writeLong(0);
            
            // Skip bytes for length of data
            outFile.writeLong(0);
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            Checksum checksumEngine = new Adler32();
            while((i=cis.read(buffer)) != -1){
                checksumEngine.update(buffer, 0, i);
                outFile.write(buffer, 0, i);
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
            
            // Write computed checksum to header
            outFile.seek(crcHeaderSkipLen);
            outFile.writeLong(checksumEngine.getValue());
            headerOS.writeLong(checksumEngine.getValue());
            LOG.finest("Length of crc data written: " + (outFile.getFilePointer()-crcHeaderSkipLen));
            LOG.finest("CRC: " + checksumEngine.getValue());
            
            // Write data length to header
            outFile.writeLong(readSize);
            headerOS.writeLong(readSize);
            LOG.finest("read/write data size: " + readSize);
            
            // Write header checksum
            byte[] headerBytes = headerByteArrayOS.toByteArray();
            checksumEngine.reset();
            checksumEngine.update(headerBytes, 0, headerBytes.length);
            outFile.seek(FileFormatVersion.WC07.getBytes(WizCryptAlgorithms.STR_ENCODE).length);
            outFile.writeLong(checksumEngine.getValue());
            /***end encryption code*/
            
            if(!keepSource){
                canDelete = true;
            }
            
            isSuccessful = true;
        }
        finally{
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
            if(outFile != null){
                try{
                    outFile.close();
                }
                catch(IOException ioe){
                    System.err.println(ioe.getMessage());
                }
            }
            // fos & fis will be closed by WizCrypt.encrypt() API
            if(canDelete){
                LOG.fine("Deleting file: " + file.getAbsolutePath());
                file.delete();
            }
            // delete corrupt outFile
            if(!isSuccessful){
                if(outFileTmp != null && outFileTmp.exists()){
                    LOG.fine("Deleting: " + outFileTmp.getAbsolutePath());
                    outFileTmp.delete();
                }
            }
        }
    }
}
