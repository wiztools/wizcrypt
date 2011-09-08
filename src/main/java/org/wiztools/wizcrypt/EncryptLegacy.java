package org.wiztools.wizcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author subhash
 */
final class EncryptLegacy extends AbstractWizCrypt {
    
    private static final Logger LOG = Logger.getLogger(EncryptLegacy.class.getName());
    
    EncryptLegacy(File file, File outFile, char[] password, ParamBean cpb) {
        super(file, outFile, password, cpb);
    }

    @Override
    public void process() throws IOException, WizCryptException {
        boolean isSuccessful = false;
        CipherInputStream cis = null;
        OutputStream os = null;
        try{
            InputStream is = new FileInputStream(file);
            File out = validateAndGetOutFileForEncrypt(file, super.outFile, cpb);
            
            os = new FileOutputStream(out);
            
            
            beginCallback();
            
            final byte[] pwd = new String(password).getBytes(
                    WizCryptAlgorithms.ENCODE_UTF8);
            final byte[] passKeyHash = CipherHashGen.getPasswordMD5Hash(pwd);
            
            cis = new CipherInputStream(is, CipherHashGen.getCipherForEncrypt(
                    pwd, WizCryptAlgorithms.CRYPT_ALGO_RC4));
            // Write the hash in first 16 bytes
            os.write(passKeyHash);
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=cis.read(buffer)) != -1){
                os.write(buffer, 0, i);
                readSize += i;
                notifyCallbackProgress(readSize);
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
        finally{
            try{
                if(os != null){
                    os.close();
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
            
            if(isSuccessful && !cpb.isKeepSource()) {
                LOG.log(Level.FINE, "Deleting: {0}", file.getAbsolutePath());
                file.delete();
            }
            
            // delete corrupt outfile:
            if(!isSuccessful) {
                if(outFile!=null && outFile.exists()) {
                    LOG.log(Level.FINE, "Deleting: {0}", outFile.getAbsolutePath());
                    outFile.delete();
                }
            }
            
            endCallback();
        }
    }
    
}
