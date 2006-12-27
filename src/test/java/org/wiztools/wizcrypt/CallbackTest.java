/*
 * CallbackTest.java
 *
 * Created on December 27, 2006, 9:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author schandran
 */
public class CallbackTest extends TestCase {
    
    private static final String PLAIN_FILE = "src/test/resources/logo.png";
    private static final String CIPHER_FILE = "src/test/resources/logo.png.wiz";
    private static final String PASSWD = "password";
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(CallbackTest.class);
        
        return suite;
    }
    
    /** Creates a new instance of CallbackTest */
    public CallbackTest(String testName) {
        super(testName);
    }
    
    public void testEncryptCallback(){
        System.out.println("\ntestEncryptCallback()");
        try{
            CipherKey ck = CipherKeyGen.getCipherKeyForEncrypt(PASSWD);
            InputStream is = new FileInputStream(PLAIN_FILE);
            OutputStream os = new FileOutputStream(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png.wiz");
            
            WizCrypt.encrypt(is, os, ck, new TestCallback());
        }
        catch(Exception e){
            fail("An exception occurred: " + e.getMessage());
        }
    }
    
    public void testEncryptPercentageCallback(){
        System.out.println("\ntestEncryptPercentageCallback()");
        try{
            CipherKey ck = CipherKeyGen.getCipherKeyForEncrypt(PASSWD);
            InputStream is = new FileInputStream(PLAIN_FILE);
            OutputStream os = new FileOutputStream(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png.wiz");
            
            WizCrypt.encrypt(is, os, ck, new TestCallback(), new File(PLAIN_FILE).length());
        }
        catch(Exception e){
            fail("An exception occurred: " + e.getMessage());
        }
    }
    
    public void testDecryptCallback(){
        System.out.println("\ntestDecryptCallback()");
        try{
            CipherKey ck = CipherKeyGen.getCipherKeyForDecrypt(PASSWD);
            InputStream is = new FileInputStream(CIPHER_FILE);
            OutputStream os = new FileOutputStream(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png");
            
            WizCrypt.decrypt(is, os, ck, new TestCallback());
        }
        catch(Exception e){
            fail("An exception occurred: " + e.getMessage());
        }
    }
    
    public void testDecryptPercentageCallback(){
        System.out.println("\ntestDecryptCallback()");
        try{
            CipherKey ck = CipherKeyGen.getCipherKeyForDecrypt(PASSWD);
            InputStream is = new FileInputStream(CIPHER_FILE);
            OutputStream os = new FileOutputStream(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png");
            
            WizCrypt.decrypt(is, os, ck, new TestCallback(), new File(CIPHER_FILE).length());
        }
        catch(Exception e){
            fail("An exception occurred: " + e.getMessage());
        }
    }
    
    class TestCallback implements Callback{
        public void begin() {
            System.out.println("~~~~ Callback output ~~~~");
            System.out.print("Begin,");
        }

        public void notifyProgress(long value) {
            System.out.print(value + ",");
        }

        public void end() {
            System.out.println("End.\n");
        }
        
    }
    
}
