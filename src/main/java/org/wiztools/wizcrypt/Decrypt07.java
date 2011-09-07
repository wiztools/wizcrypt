package org.wiztools.wizcrypt;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.logging.Level;
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


/**
 * Class to do the decryption using the WizCrypt naming convention (*.wiz).
 */
final class Decrypt07 extends AbstractWizCrypt{
    
    private static final Logger LOG = Logger.getLogger(Decrypt07.class.getName());
    
    @Override
    public void process(final File file, File outFile, final char[] password,
            final ParamBean cpb)
            throws IOException, WizCryptException {
        
        final boolean keepSource = cpb.isKeepSource();
        
        // FileInputStream fis = null;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        boolean canDelete = false;
        
        CipherOutputStream cos = null;
        
        // This variable should be set to true after successful processing of
        // Decryption. Remember, if any branch condition creeps into this code
        // subsequently, this has to be updated in that branch.
        boolean isSuccessful = false;
        try{
            outFile = validateAndGetOutFileForDecrypt(file, outFile, cpb);
            
            FileInputStream fis = new FileInputStream(file);
            dis = new DataInputStream(fis);
            fos = new FileOutputStream(outFile);
            
            //***start decryption
            byte[] pwd = new String(password).getBytes(WizCryptAlgorithms.ENCODE_UTF8);
            
            // Begin callback:
            beginCallback();
            
            // Read the magic number
            byte[] versionStr = Version.WC07.getBytes();
            int versionByteLen = versionStr.length;
            LOG.log(Level.FINE, "Length of bytearray holding version: {0}", versionByteLen);
            byte[] magicNumber = new byte[versionByteLen];
            int bytesRead = dis.read(magicNumber, 0, versionByteLen);
            LOG.log(Level.FINE, "Magic number read: {0}", new String(magicNumber));
            if(!Arrays.equals(versionStr, magicNumber)){
                LOG.fine("Magic number does not match. . .");
                throw new FileCorruptException(FileCorruptType.FILE_MAGIC_NUMBER_ERROR);
            }
            if(bytesRead < versionByteLen){
                throw new FileCorruptException(FileCorruptType.FILE_TRUNCATED);
            }
            LOG.log(Level.FINEST, "magicNumber: {0}", new String(magicNumber));
            
            Checksum checksumEngine = new Adler32();
            
            // Read header CRC
            long headerCRC = dis.readLong();
            LOG.log(Level.INFO, "Recorded header CRC: {0}", headerCRC);
            
            // Datastructure to hold header bytes
            ByteArrayOutputStream headerByteArrayOS = new ByteArrayOutputStream();
            DataOutputStream headerOS = new DataOutputStream(headerByteArrayOS);
            
            // read 16 bytes from fis
            int LEN_OF_PWD_HASH = 32;
            byte[] filePassKeyHash = new byte[LEN_OF_PWD_HASH];
            bytesRead = dis.read(filePassKeyHash, 0, LEN_OF_PWD_HASH);
            headerOS.write(filePassKeyHash, 0, bytesRead);
            if(bytesRead < LEN_OF_PWD_HASH){
                throw new FileCorruptException(FileCorruptType.FILE_TRUNCATED);
            }
            
            byte[] passKeyHash = CipherHashGen.getPasswordSha256Hash(pwd);
            if(!Arrays.equals(passKeyHash, filePassKeyHash)){
                throw new PasswordMismatchException();
            }
            
            long dataCRC = dis.readLong();
            headerOS.writeLong(dataCRC);
            LOG.log(Level.INFO, "Recorded data CRC: {0}", dataCRC);
            
            long dataLen = dis.readLong();
            headerOS.writeLong(dataLen);
            LOG.log(Level.INFO, "Recorder data length: {0}", dataLen);
            
            // Check if header CRC matches
            byte[] headerBytes = headerByteArrayOS.toByteArray();
            checksumEngine.update(headerBytes, 0, headerBytes.length);
            long computedHeaderCRC = checksumEngine.getValue();
            if(computedHeaderCRC != headerCRC){
                LOG.log(Level.SEVERE, "computed/header CRC: {0} / {1}", new Object[]{computedHeaderCRC, headerCRC});
                throw new FileCorruptException(FileCorruptType.HEADER_CRC_ERROR);
            }
            LOG.info("***Computed and actual header CRC matches!!***");
            
            cos = new CipherOutputStream(fos, CipherHashGen.getCipherForDecrypt(pwd, WizCryptAlgorithms.CRYPT_ALGO_RC4));
            
            checksumEngine.reset();
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=dis.read(buffer)) != -1){
                readSize += i;
                
                boolean shouldBreak = false;
                if(readSize > dataLen){
                    LOG.log(Level.FINE, "readSize/dataLen/diff: {0}/{1}/{2}", new Object[]{readSize, dataLen, readSize-dataLen});
                    LOG.warning("Bytes above the recorded data is ignored!");
                    i = i - (int)(readSize - dataLen);
                    shouldBreak = true;
                }
                
                checksumEngine.update(buffer, 0, i);
                cos.write(buffer, 0, i);

                
                // Notify callbacks progress:
                notifyCallbackProgress(readSize);
                
                if(shouldBreak){
                    break;
                }
            }
            
            LOG.log(Level.FINEST, "read/write data size: {0}", readSize);
            
            LOG.log(Level.INFO, "Computed CRC: {0}", checksumEngine.getValue());
            
            if(dataCRC != checksumEngine.getValue()){
                throw new FileCorruptException(FileCorruptType.DATA_CRC_ERROR);
            }
            
            //***end decryption
            
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
                if(cos != null){
                    cos.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            try{
                if(dis != null){
                    dis.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            
            // End callbacks
            endCallback();

            // fos & fis will be closed by WizCrypt.decrypt() API
            if(canDelete){
                LOG.log(Level.FINE, "Deleting: {0}", file.getAbsolutePath());
                file.delete();
            }
            if(!isSuccessful){
                if(outFile != null && outFile.exists()){
                    LOG.log(Level.FINE, "Deleting (Not successful): {0}", outFile.getAbsolutePath());
                    outFile.delete();
                }
            }
        }
    }
}
