package org.wiztools.wizcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author subhash
 */
public class EncryptLegacy extends AbstractWizCrypt {

    @Override
    public void process(File file, File outFile, char[] password, ParamBean cpb) throws IOException, WizCryptException {
        CipherInputStream cis = null;
        OutputStream os = null;
        try{
            InputStream is = new FileInputStream(file);
            
            
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
            endCallback();
        }
    }
    
}
