package org.wiztools.wizcrypt;

import java.io.DataInputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ResourceBundle;
import org.wiztools.wizcrypt.exception.DestinationFileExistsException;
import org.wiztools.wizcrypt.exception.FileFormatException;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;

/**
 * Class to do the decryption using the WizCrypt naming convention (*.wiz).
 */
public class Decrypt implements IProcess{
    
    private static final Logger LOG = Logger.getLogger(Decrypt.class.getName());
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    public void process(final File file, final WizCryptBean wcb,
            final CliParamBean cpb) 
            throws FileNotFoundException,
                DestinationFileExistsException,
                PasswordMismatchException,
                FileFormatException,
                IOException,
                NoSuchAlgorithmException,
                InvalidKeyException,
                NoSuchPaddingException{
        process(file, null, wcb, cpb);
    }
    
    public void process(final File file, File outFile, final WizCryptBean wcb,
            final CliParamBean cpb) 
            throws FileNotFoundException,
                DestinationFileExistsException,
                PasswordMismatchException,
                FileFormatException,
                IOException,
                NoSuchAlgorithmException,
                InvalidKeyException,
                NoSuchPaddingException{
        
        final boolean forceOverwrite = cpb.getForceOverwrite();
        final boolean keepSource = cpb.getKeepSource();
        final boolean isOldFormat = cpb.getIsOldFormat();
        
        FileInputStream fis = null;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        boolean canDelete = false;
        
        CipherOutputStream cos = null;
        Callback cb = wcb.getCallback();
        final long sizeOfStream = cb==null?-1:cb.getSize();
        
        // This variable should be set to true after successful processing of
        // Decryption. Remember, if any branch condition creeps into this code
        // subsequently, this has to be updated in that branch.
        boolean isSuccessful = false;
        try{
            String path = file.getAbsolutePath();
            if(!path.endsWith(".wiz")){
                throw new FileNotFoundException(
                        MessageFormat.format(rb.getString("err.file.not.end.wiz"), path));
            }
            String newPath = path.replaceFirst(".wiz$", "");
            if(outFile == null){
                outFile = new File(newPath);
            }
            if(!forceOverwrite && outFile.exists()){
                throw new DestinationFileExistsException(
                        MessageFormat.format(rb.getString("err.destination.file.exists"),
                        outFile.getAbsolutePath()));
            }
            
            if(isOldFormat){
                WizCrypt wc = WizCrypt.getOldInstance();
                // TODO process, then return
            }
            
            fis = new FileInputStream(file);
            dis = new DataInputStream(fis);
            fos = new FileOutputStream(outFile);
            
            //***start decryption
            byte[] pwd = new String(wcb.getPassword()).getBytes(WizCryptAlgorithms.STR_ENCODE);
            
            if(cb != null){
                cb.begin();
            }
            
            // Read the magic number
            byte[] versionStr = FileFormatVersion.WC07.getBytes(WizCryptAlgorithms.STR_ENCODE);
            int versionByteLen = versionStr.length;
            LOG.fine("Length of bytearray holding version: "+versionByteLen);
            byte[] magicNumber = new byte[versionByteLen];
            int bytesRead = dis.read(magicNumber, 0, versionByteLen);
            LOG.fine("Magic number read: " + new String(magicNumber));
            if(!Arrays.equals(versionStr, magicNumber)){
                LOG.fine("Magic number does not match. . .");
                throw new FileFormatException();
            }
            if(bytesRead < versionByteLen){
                // TODO throw exception
            }
            LOG.finest("magicNumber: "+new String(magicNumber));
            
            // read 16 bytes from fis
            int LEN_OF_PWD_HASH = 32;
            byte[] filePassKeyHash = new byte[LEN_OF_PWD_HASH];
            bytesRead = dis.read(filePassKeyHash, 0, LEN_OF_PWD_HASH);
            if(bytesRead < LEN_OF_PWD_HASH){
                // TODO throw exception
            }
            
            byte[] passKeyHash = CipherHashGen.getPasswordSha256Hash(pwd);
            if(!Arrays.equals(passKeyHash, filePassKeyHash)){
                throw new PasswordMismatchException();
            }
            
            long crc = dis.readLong();
            LOG.info("Recorded CRC: " + crc);
            
            long dataLen = dis.readLong();
            LOG.info("Recorder data length: " + dataLen);
            
            cos = new CipherOutputStream(fos, CipherHashGen.getCipherForDecrypt(pwd, WizCryptAlgorithms.CRYPT_ALGO_RC4));
            
            Checksum checksumEngine = new Adler32();
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=dis.read(buffer)) != -1){
                readSize += i;
                
                boolean shouldBreak = false;
                if(readSize > dataLen){
                    LOG.fine("readSize/dataLen/diff: " + readSize + "/" 
                            + dataLen + "/" + (readSize-dataLen));
                    LOG.warning("Bytes above the recorded data is ignored!");
                    i = i - (int)(readSize - dataLen);
                    shouldBreak = true;
                }
                
                checksumEngine.update(buffer, 0, i);
                cos.write(buffer, 0, i);
                
                if(cb != null){
                    if(sizeOfStream == -1){
                        cb.notifyProgress(readSize);
                    }
                    else{
                        cb.notifyProgress(readSize * 100 / sizeOfStream);
                    }
                }
                
                if(shouldBreak){
                    break;
                }
            }
            
            LOG.finest("read/write data size: " + readSize);
            
            LOG.info("Computed CRC: " + checksumEngine.getValue());
            
            if(crc != checksumEngine.getValue()){
                throw new FileFormatException(); // TODO i18n msg
            }
            
            //***end decryption

            if(!keepSource){
                canDelete = true;
            }
            
            isSuccessful = true;
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
                if(fis != null){
                    fis.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            if(cb != null){
                cb.end();
            }
            // fos & fis will be closed by WizCrypt.decrypt() API
            if(canDelete){
                LOG.fine("Deleting: " + file.getAbsolutePath());
                file.delete();
            }
            if(!isSuccessful){
                if(outFile != null && outFile.exists()){
                    LOG.fine("Deleting (Not successful): " + outFile.getAbsolutePath());
                    outFile.delete();
                }
            }
        }
    }
}
