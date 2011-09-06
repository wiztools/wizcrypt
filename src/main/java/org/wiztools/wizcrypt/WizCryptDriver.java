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
        WizCrypt p = null;
        if(Version.WC07 == version){
            p = new Encrypt07();
        }
        return p;
    }
    
    public static WizCrypt getDecryptInstance(final Version version){
        WizCrypt p = null;
        if(Version.WC07 == version){
            p = new Decrypt07();
        }
        return p;
    }
    
}
