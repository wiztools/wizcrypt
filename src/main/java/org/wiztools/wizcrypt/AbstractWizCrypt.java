/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiztools.wizcrypt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author subhash
 */
abstract class AbstractWizCrypt implements WizCrypt {
    
    private final List<Callback> callbacks = new ArrayList<Callback>();
    
    protected void beginCallback() {
        for(Callback callback: callbacks) {
            callback.begin();
        }
    }
    
    protected void notifyCallbackProgress(final long readSize) {
        for(Callback callback: callbacks) {
            final long sizeOfStream = callback.getSize();
            if(sizeOfStream == -1){
                callback.notifyProgress(readSize);
            } else{
                callback.notifyProgress(readSize * 100 / sizeOfStream);
            }
        }
    }
    
    protected void endCallback() {
        for(Callback callback: callbacks) {
            callback.end();
        }
    }

    @Override
    public final void addCallback(Callback callback) {
        if(callback == null)
            throw new NullPointerException("Callback cannot be null!");
        callbacks.add(callback);
    }

    @Override
    public final void process(File file, char[] password, ParamBean cpb) throws IOException, WizCryptException {
        process(file, null, password, cpb);
    }

    @Override
    public abstract void process(File file, File outFile, char[] password, ParamBean cpb) throws IOException, WizCryptException;
    
}
