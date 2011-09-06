package org.wiztools.wizcrypt;



import java.io.File;
import java.io.IOException;

public interface WizCrypt{
    
    public void process(File file, WizCryptBean wcb, ParamBean cpb)
                    throws IOException, WizCryptException;
    
    public void process(File file, File outFile, WizCryptBean wcb, ParamBean cpb)
                    throws IOException, WizCryptException;
    
}
