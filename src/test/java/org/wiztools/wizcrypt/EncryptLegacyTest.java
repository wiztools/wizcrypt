package org.wiztools.wizcrypt;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wiztools.commons.FileUtil;
import static org.junit.Assert.*;

/**
 *
 * @author subhash
 */
public class EncryptLegacyTest {
    
    public EncryptLegacyTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    private static final File CIPHER_FILE_LEGACY = new File("src/test/resources/logo.png.wiz");
    
    private static final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
    private static final char[] PASSWD = "password".toCharArray();

    /**
     * Test of process method, of class EncryptLegacy.
     */
    @Test
    public void testProcess() throws Exception {
        System.out.println("process");
        File file = new File("src/test/resources/logo.png");
        File outFile = new File(tmpDir, "logo.png.wiz");
        ParamBean cpb = new ParamBean();
        cpb.setForceOverwrite(true);
        cpb.setKeepSource(true);
        WizCrypt instance = WizCryptDriver.getEncryptInstance(Version.LEGACY);
        instance.process(file, outFile, PASSWD, cpb);
        
        // Validate:
        byte[] validateWith = FileUtil.getContentAsBytes(CIPHER_FILE_LEGACY);
        byte[] result = FileUtil.getContentAsBytes(outFile);
        assertArrayEquals(validateWith, result);
    }
}
