/*
 * BackwardCompatibilityTest.java
 *
 * Created on December 27, 2006, 11:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.wiztools.wizcrypt.impl.DecryptOld;
import org.wiztools.wizcrypt.impl.EncryptOld;

/**
 * The test works this way:
 *
 * 1. Encrypt using the current library
 * 2. Generate MD5 hash of the generated file
 * 3. Generate MD5 hash of the old encrypted file
 * 4. Compare hash generated in steps 2 & 3. If equal, test successful!
 *
 * @author schandran
 */
public class BackwardCompatibilityTest {
    
    private static final String PLAIN_FILE = "src/test/resources/logo.png";
    private static final String CIPHER_FILE = "src/test/resources/logo.png.wiz";
    private static final String PASSWD = "password";
    
    @Before
    public void setUp() throws Exception {
        System.out.println("setUp() called...");
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    // method taken from JavaAlmanac!
    private byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    
    private byte[] fileHash(final File file) throws NoSuchAlgorithmException, IOException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(getBytesFromFile(file));
        byte[] raw = md.digest();
        return raw;
    }
    
    @Test
    public void testEncryptBackwardCompatibility(){
        System.out.println("\ntestEncryptBackwardCompatibility()");
        try{
            // 1. Encrypt the file
            WizCryptBean wcb = new WizCryptBean();
            wcb.setPassword(PASSWD.toCharArray());
            File outFile = new File(System.getProperty("java.io.tmpdir") +
                    File.separator + "logo.png.wiz");
            
            IProcess p = EncryptOld.getInstance();
            ParamBean pb = new ParamBean();
            pb.setForceOverwrite(true);
            pb.setKeepSource(true);
            p.process(new File(PLAIN_FILE), outFile, wcb, pb);
            System.out.println("Encrypt step completed...");
            
            // 2. Hash for encrypted file
            byte[] newHash = fileHash(outFile);
            
            // 3. Hash for old encrypted file
            byte[] oldHash = fileHash(new File(CIPHER_FILE));
            
            // 4. Compare!
            if(!Arrays.equals(oldHash, newHash)){
                System.out.println("Encryption fails backward compatibility test!");
                System.out.println("Length: " + new File(CIPHER_FILE).length() + " / " + outFile.length());
                Assert.fail("Encryption fails backward compatibility test!");
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Assert.fail("An exception occured during run of test case: "+e.getMessage());
        }
    }
    
    @Test
    public void testDecryptBackwardCompatibility(){
        System.out.println("\ntestDecryptBackwardCompatibility()");
        try{
            // 1. Decrypt the file
            WizCryptBean wcb = new WizCryptBean();
            wcb.setPassword(PASSWD.toCharArray());
            File outFile = new File(System.getProperty("java.io.tmpdir") +
                    File.separator + "logo.png");
            
            IProcess p = DecryptOld.getInstance();
            ParamBean pb = new ParamBean();
            pb.setForceOverwrite(true);
            pb.setKeepSource(true);
            p.process(new File(CIPHER_FILE), outFile, wcb, pb);
            
            // 2. Hash for decrypted file
            byte[] newHash = fileHash(outFile);
            
            // 3. Hash for old decrypted file
            byte[] oldHash = fileHash(new File(PLAIN_FILE));
            
            // 4. Compare!
            if(!Arrays.equals(oldHash, newHash)){
                System.out.println("Decryption fails backward compatibility test!");
                Assert.fail("Decryption fails backward compatibility test!");
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Assert.fail("An exception occured during run of test case: "+e.getMessage());
        }
    }
}
