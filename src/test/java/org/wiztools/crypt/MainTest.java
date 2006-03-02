/*
 * MainTest.java
 * JUnit based test
 *
 * Created on March 2, 2006, 5:32 PM
 */

package org.wiztools.crypt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import junit.framework.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import java.security.InvalidKeyException;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.File;
import java.util.ResourceBundle;

/**
 *
 * @author subhash
 */
public class MainTest extends TestCase {
    
    public MainTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(MainTest.class);
        
        return suite;
    }
    
    /**
     * Test of main method, of class org.wiztools.crypt.Main.
     */
    public void testMain() throws IOException {
        System.out.println("main");
        
        final String path = System.getProperty("java.io.tmpdir")+
                File.separator + "wiztest.txt";
        
        final String password = "testpwd";
        
        // Please provide content with NO new lines (\n):
        final String content = "This is a dummy text!";
        
        File fin = new File(path);
        fin.deleteOnExit();
        
        // Write dummy content into the file
        
        PrintWriter pw = new PrintWriter(new FileWriter(fin));
        pw.println(content);
        pw.close();
        
        String[] arg = new String[]{"-e", "-p", password, path};
        
        // Encryption
        
        Encrypt e = new Encrypt();
        try {
            e.init(password);
            e.process(fin);
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        }
        
        // Creation of fout
        
        File fout = new File(path+".wiz");
        
        // Decryption
        
        Decrypt d = new Decrypt();
        try {
            d.init(password);
            try{
                d.process(fout);
            } catch (PasswordMismatchException ex) {
                fail(ex.getMessage());
            }
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        }
        
        // Read content to verify
        BufferedReader br = new BufferedReader(new FileReader(fin));
        String str = br.readLine();
        br.close();
        
        assertEquals(content, str);

    }
    
}