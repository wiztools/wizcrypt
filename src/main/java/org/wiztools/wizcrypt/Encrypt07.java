package org.wiztools.wizcrypt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Class to do the encryption using the WizCrypt naming convention (*.wiz).
 */
final class Encrypt07 extends AbstractWizCrypt{
    
    private static final Logger LOG = Logger.getLogger(Encrypt07.class.getName());
    
    @Override
    public void process(final File file, File outFileTmp, final char[] password, final ParamBean cpb)
                    throws IOException, WizCryptException {
        
        final boolean keepSource = cpb.isKeepSource();
        
        FileInputStream fis = null;
        boolean canDelete = false;
        RandomAccessFile outFile = null;
        
        CipherInputStream cis = null;
        
        // This variable should be set to true after successful processing of
        // Encryption. Remember, if any branch condition creeps into this code
        // subsequently, this has to be updated in that branch.
        boolean isSuccessful = false;
        try{
            outFileTmp = validateAndGetOutFileForEncrypt(file, outFileTmp, cpb);
            outFile = new RandomAccessFile(outFileTmp, "rw");
            
            fis = new FileInputStream(file);
            
            //***start encryption code
            long crcHeaderSkipLen = 0;
            
            byte[] pwd = new String(password).getBytes(WizCryptAlgorithms.ENCODE_UTF8);

            // Begin callback:
            beginCallback();
            
            // Byte array having header info: used for computing header CRC
            ByteArrayOutputStream headerByteArrayOS = new ByteArrayOutputStream();
            DataOutputStream headerOS = new DataOutputStream(headerByteArrayOS);
            
            // Write the file-format magic number
            byte[] versionStr = Version.WC07.getBytes();
            LOG.log(Level.FINE, "Length of bytearray containing version: {0}", versionStr.length);
            outFile.write(versionStr, 0, versionStr.length);
            crcHeaderSkipLen += versionStr.length;
            
            // Leave space for header CRC
            outFile.writeLong(0);
            crcHeaderSkipLen += 8; // Long takes 8 bytes
            
            cis = new CipherInputStream(fis, CipherHashGen.getCipherForEncrypt(pwd, WizCryptAlgorithms.CRYPT_ALGO_RC4));
            
            // Write password hash
            byte[] pwdHash = CipherHashGen.getPasswordSha256Hash(pwd);
            LOG.log(Level.FINE, "Length of Sha hash: {0}", pwdHash.length);
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
                
                // Notify callback progress:
                notifyCallbackProgress(readSize);
            }
            
            // Write computed checksum to header
            outFile.seek(crcHeaderSkipLen);
            outFile.writeLong(checksumEngine.getValue());
            headerOS.writeLong(checksumEngine.getValue());
            LOG.log(Level.FINEST, "Length of crc data written: {0}", (outFile.getFilePointer()-crcHeaderSkipLen));
            LOG.log(Level.FINEST, "CRC: {0}", checksumEngine.getValue());
            
            // Write data length to header
            outFile.writeLong(readSize);
            headerOS.writeLong(readSize);
            LOG.log(Level.FINEST, "read/write data size: {0}", readSize);
            
            // Write header checksum
            byte[] headerBytes = headerByteArrayOS.toByteArray();
            checksumEngine.reset();
            checksumEngine.update(headerBytes, 0, headerBytes.length);
            outFile.seek(Version.WC07.getBytes().length);
            outFile.writeLong(checksumEngine.getValue());
            /***end encryption code*/
            
            if(!keepSource){
                canDelete = true;
            }
            
            isSuccessful = true;
        }
        catch(InvalidKeyException ex) {
            throw new WizCryptException(ex);
        }
        catch(NoSuchAlgorithmException ex) {
            throw new WizCryptException(ex);
        }
        catch(NoSuchPaddingException ex) {
            throw new WizCryptException(ex);
        }
        finally {
            try{
                if(cis != null){
                    cis.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            
            // End callback:
            endCallback();
            
            if(outFile != null){
                try{
                    outFile.close();
                } catch(IOException ioe){
                    System.err.println(ioe.getMessage());
                }
            }
            
            if(canDelete){
                LOG.log(Level.FINE, "Deleting: {0}", file.getAbsolutePath());
                file.delete();
            }
            // delete corrupt outFile
            if(!isSuccessful){
                if(outFileTmp != null && outFileTmp.exists()){
                    LOG.log(Level.FINE, "Deleting: {0}", outFileTmp.getAbsolutePath());
                    outFileTmp.delete();
                }
            }
        }
    }
}
