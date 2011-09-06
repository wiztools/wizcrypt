package org.wiztools.wizcrypt;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 *
 * @author subhash
 */
public enum Version {
    WC07, LEGACY;
    
    private static final Logger LOG = Logger.getLogger(Version.class.getName());
    
    private static final byte[] EMPTY_BYTE = new byte[]{};
    private static final byte[] WC07_BYTE;
    static {
        try{
            WC07_BYTE = "WC07".getBytes(WizCryptAlgorithms.ENCODE_UTF8);
        }
        catch(UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public byte[] getBytes() {
        if(this == WC07) {
            return WC07_BYTE;
        }
        return EMPTY_BYTE;
    }
}
