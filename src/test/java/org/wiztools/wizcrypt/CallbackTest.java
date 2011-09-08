/*
 * CallbackTest.java
 *
 * Created on December 27, 2006, 9:34 AM
 *
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
    
    private static final File PLAIN_FILE = new File("src/test/resources/logo.png");
    private static final File CIPHER_FILE_WC07 = new File("src/test/resources/logo.png.wc07.wiz");
    private static final char[] PASSWD = "password".toCharArray();
    
    private static final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
    
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
            File out = new File(tmpDir, "logo.png.wiz");
            WizCrypt wc = WizCryptDriver.getEncryptInstance(PLAIN_FILE, out, PASSWD, cpb);
            wc.addCallback(new TestCallback());
            wc.process();
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
            File out = new File(tmpDir, "logo.png");
            WizCrypt wc = WizCryptDriver.getDecryptInstance(CIPHER_FILE_WC07, out, PASSWD, cpb);
            wc.addCallback(new TestCallback());
            wc.process();
        }
        catch(Exception e){
            e.printStackTrace();
            Assert.fail("An exception occurred: " + e.getMessage());
        }
    }
    
    class TestCallback implements Callback{
        public TestCallback(){
            
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
