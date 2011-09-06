package org.wiztools.wizcrypt;



import java.io.File;
import java.io.IOException;

public interface WizCrypt{
    
    public void addCallback(Callback callback);
    
    public void process(File file, char[] password, ParamBean cpb)
                    throws IOException, WizCryptException;
    
    public void process(File file, File outFile, char[] password, ParamBean cpb)
                    throws IOException, WizCryptException;
    
}
