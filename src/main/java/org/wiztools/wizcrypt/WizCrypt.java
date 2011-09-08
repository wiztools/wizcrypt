package org.wiztools.wizcrypt;



import java.io.IOException;

public interface WizCrypt{
    
    public void addCallback(Callback callback);
    
    public void process() throws IOException, WizCryptException;
    
}
