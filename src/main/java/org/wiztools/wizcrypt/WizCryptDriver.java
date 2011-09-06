package org.wiztools.wizcrypt;

/**
 *
 * @author subhash
 */
public final class WizCryptDriver {

    private WizCryptDriver() {
    }
    
    private static final Version LATEST_VERSION = Version.WC07;
    
    public static WizCrypt getEncryptInstance(){
        return getEncryptInstance(LATEST_VERSION);
    }
    
    public static WizCrypt getDecryptInstance(){
        return getDecryptInstance(LATEST_VERSION);
    }
    
    public static WizCrypt getEncryptInstance(final Version version){
        switch(version){
            case WC07:
                return new Encrypt07();
            case LEGACY:
                return new EncryptLegacy();
            default:
                return null;
        }
    }
    
    public static WizCrypt getDecryptInstance(final Version version){
        switch(version) {
            case WC07:
                return new Decrypt07();
            case LEGACY:
                return new DecryptLegacy();
            default:
                return null;
        }
    }
    
}
