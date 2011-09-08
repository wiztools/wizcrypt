package org.wiztools.wizcrypt;

import java.io.File;

/**
 *
 * @author subhash
 */
public final class WizCryptDriver {

    private WizCryptDriver() {
    }
    
    private static final Version LATEST_VERSION = Version.WC07;
    
    public static WizCrypt getEncryptInstance(File file, File outFile, char[] password, ParamBean cpb){
        return getEncryptInstance(LATEST_VERSION, file, outFile, password, cpb);
    }
    
    public static WizCrypt getDecryptInstance(File file, File outFile, char[] password, ParamBean cpb){
        return getDecryptInstance(LATEST_VERSION, file, outFile, password, cpb);
    }
    
    public static WizCrypt getEncryptInstance(final Version version,
            File file, File outFile, char[] password, ParamBean cpb){
        switch(version){
            case LEGACY:
                return new EncryptLegacy(file, outFile, password, cpb);
            case WC07:
            default:
                return new Encrypt07(file, outFile, password, cpb);
        }
    }
    
    public static WizCrypt getDecryptInstance(final Version version,
            File file, File outFile, char[] password, ParamBean cpb){
        switch(version) {
            case LEGACY:
                return new DecryptLegacy(file, outFile, password, cpb);
            case WC07:
            default:
                return new Decrypt07(file, outFile, password, cpb);
        }
    }
    
}
