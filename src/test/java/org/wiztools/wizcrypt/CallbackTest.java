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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.LogManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wiztools.wizcrypt.impl.Decrypt07;
import org.wiztools.wizcrypt.impl.Encrypt07;

/**
 *
 * @author schandran
 */
public class CallbackTest {
    
    private static final String PLAIN_FILE = "src/test/resources/logo.png";
    private static final String CIPHER_FILE = "src/test/resources/logo.png.wiz";
    private static final String PASSWD = "password";
    private static final ParamBean cpb = new ParamBean();
    
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
            File out = new File(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png.wiz");
            IProcess ip = Encrypt07.getInstance();
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
            File out = new File(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png.wiz");
            IProcess ip = Encrypt07.getInstance();
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
            File in = new File(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png.wiz");
            File out = new File(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png");
            IProcess ip = Decrypt07.getInstance();
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
            wcb.setCallback(new TestCallback(new File(CIPHER_FILE).length()));
            File in = new File(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png.wiz");
            File out = new File(System.getProperty("java.io.tmpdir")
                            + File.separator + "logo.png");
            IProcess ip = Decrypt07.getInstance();
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
