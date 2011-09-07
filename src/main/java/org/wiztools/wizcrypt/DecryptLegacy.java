package org.wiztools.wizcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author subhash
 */
public class DecryptLegacy extends AbstractWizCrypt {

    @Override
    public void process(File file, File outFile, char[] password, ParamBean cpb) throws IOException, WizCryptException {
        final boolean forceOverwrite = cpb.isForceOverwrite();
        final boolean keepSource = cpb.isKeepSource();
        
        
        CipherOutputStream cos = null;
        InputStream is = null;

        try{
            outFile = validateAndGetOutFileForDecrypt(file, outFile, cpb);
            
            is = new FileInputStream(file);
            OutputStream os = new FileOutputStream(outFile);
            
            beginCallback();
            
            // read 16 bytes from fis
            byte[] filePassKeyHash = new byte[16];
            is.read(filePassKeyHash, 0, 16);
            
            // Password hash compute:
            final byte[] pwd = new String(password).getBytes(WizCryptAlgorithms.ENCODE_UTF8);
            final byte[] passKeyHash = CipherHashGen.getPasswordMD5Hash(pwd);
            
            if(!Arrays.equals(passKeyHash, filePassKeyHash)){
                throw new PasswordMismatchException(rb.getString("err.pwd.not.match"));
            }
            
            cos = new CipherOutputStream(os,
                    CipherHashGen.getCipherForDecrypt(
                                pwd, WizCryptAlgorithms.CRYPT_ALGO_RC4));
            
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            long readSize = 0;
            while((i=is.read(buffer)) != -1){
                cos.write(buffer, 0, i);
                readSize += i;
                notifyCallbackProgress(readSize);
            }
        }
        catch(NoSuchAlgorithmException ex) {
            throw new WizCryptException(ex);
        }
        catch(InvalidKeyException ex) {
            throw new WizCryptException(ex);
        }
        catch(NoSuchPaddingException ex) {
            throw new WizCryptException(ex);
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
                if(is != null){
                    is.close();
                }
            } catch(IOException ioe){
                System.err.println(ioe.getMessage());
            }
            endCallback();
        }
    }
    
}