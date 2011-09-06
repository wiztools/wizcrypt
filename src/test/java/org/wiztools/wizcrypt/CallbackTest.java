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
import java.io.IOException;
import java.util.logging.LogManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author schandran
 */
public class CallbackTest {
    
    private static final String PLAIN_FILE = "src/test/resources/logo.png";
    private static final String CIPHER_FILE = "src/test/resources/logo.png.wiz";
    private static final String CIPHER_FILE_WC07 = "src/test/resources/logo.png.wc07.wiz";
    private static final String PASSWD = "password";
    
    private static final String tmpDir = System.getProperty("java.io.tmpdir")
                            + File.separator;
    
    private final ParamBean cpb = new ParamBean();
    
    @Before
    public void setUp() throws Exception {
        System.out.println("setUp()");
        try{
            LogManager.getLogManager().readConfiguration(
                Main.class.getClassLoader()
                .getResourceAsStream("org/wiztools/wizcrypt/logging.properties"));
        }
        catch(IOException ioe){
            assert true: "Logger configuration load failed!";
        }
        cpb.setForceOverwrite(true);
        cpb.setKeepSource(true);
        System.out.println("setUp() End");
    }
    
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEncryptCallback(){
        System.out.println("\ntestEncryptCallback()");
        try{
            WizCryptBean wcb = new WizCryptBean();
            wcb.setPassword(PASSWD.toCharArray());
            wcb.setCallback(new TestCallback());
            File in = new File(PLAIN_FILE);
            File out = new File(tmpDir + "logo.png.wiz");
            WizCrypt ip = Encrypt07.getInstance();
            ip.process(in, out, wcb, cpb);
        }
        catch(Exception e){
            e.printStackTrace();
            Assert.fail("An exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testEncryptPercentageCallback(){
        System.out.println("\ntestEncryptPercentageCallback()");
        try{
            WizCryptBean wcb = new WizCryptBean();
            wcb.setPassword(PASSWD.toCharArray());
            wcb.setCallback(new TestCallback(new File(PLAIN_FILE).length()));
            File in = new File(PLAIN_FILE);
            File out = new File(tmpDir + "logo.png.wiz");
            WizCrypt ip = Encrypt07.getInstance();
            ip.process(in, out, wcb, cpb);
        }
        catch(Exception e){
            e.printStackTrace();
            Assert.fail("An exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testDecryptCallback(){
        System.out.println("\ntestDecryptCallback()");
        try{
            WizCryptBean wcb = new WizCryptBean();
            wcb.setPassword(PASSWD.toCharArray());
            wcb.setCallback(new TestCallback());
            File in = new File(CIPHER_FILE_WC07);
            File out = new File(tmpDir + "logo.png");
            WizCrypt ip = Decrypt07.getInstance();
            ip.process(in, out, wcb, cpb);
            // WizCrypt.get07Instance().decrypt(is, os, wcb);
        }
        catch(Exception e){
            e.printStackTrace();
            Assert.fail("An exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testDecryptPercentageCallback(){
        System.out.println("\ntestDecryptCallback()");
        try{
            WizCryptBean wcb = new WizCryptBean();
            wcb.setPassword(PASSWD.toCharArray());
            wcb.setCallback(new TestCallback(new File(CIPHER_FILE_WC07).length()));
            File in = new File(CIPHER_FILE_WC07);
            File out = new File(tmpDir + "logo.png");
            WizCrypt ip = Decrypt07.getInstance();
            ip.process(in, out, wcb, cpb);
            // WizCrypt.get07Instance().decrypt(is, os, wcb);
        }
        catch(Exception e){
            e.printStackTrace();
            Assert.fail("An exception occurred: " + e.getMessage());
        }
    }
    
    class TestCallback extends Callback{
        public TestCallback(){
            
        }
        
        public TestCallback(final long size){
            super(size);
        }
        
        @Override
        public void begin() {
            System.out.println("~~~~ Callback output ~~~~");
            System.out.print("Begin,");
        }

        @Override
        public void notifyProgress(long value) {
            System.out.print(value + ",");
        }

        @Override
        public void end() {
            System.out.println("End.\n");
        }
        
    }
}
