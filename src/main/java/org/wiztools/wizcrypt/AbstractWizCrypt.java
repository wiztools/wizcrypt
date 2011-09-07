/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiztools.wizcrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author subhash
 */
abstract class AbstractWizCrypt implements WizCrypt {
    
    protected static final ResourceBundle rb = ResourceBundle.getBundle(
                                        "org.wiztools.wizcrypt.wizcryptmsg");
    
    private final List<Callback> callbacks = new ArrayList<Callback>();
    
    protected File validateAndGetOutFileForEncrypt(File file, File outFile, ParamBean pb)
                    throws DestinationFileExistsException{
        if(outFile == null){
            outFile = new File(file.getAbsolutePath()+".wiz");
        }
        if(!pb.isForceOverwrite() && outFile.exists()){
            throw new DestinationFileExistsException(
                    MessageFormat.format(rb.getString("err.destination.file.exists"),
                    outFile.getAbsolutePath()));
        }
        return outFile;
    }
    
    protected File validateAndGetOutFileForDecrypt(File file, File outFile, ParamBean pb)
                    throws FileNotFoundException, DestinationFileExistsException{
        String path = file.getAbsolutePath();
        if(!path.endsWith(".wiz")){
            throw new FileNotFoundException(
                    MessageFormat.format(rb.getString("err.file.not.end.wiz"), path));
        }
        if(outFile == null){
            final String newPath = path.replaceFirst(".wiz$", "");
            outFile = new File(newPath);
        }
        if(!pb.isForceOverwrite() && outFile.exists()){
            throw new DestinationFileExistsException(
                    MessageFormat.format(rb.getString("err.destination.file.exists"),
                    outFile.getAbsolutePath()));
        }
        return outFile;
    }
    
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
