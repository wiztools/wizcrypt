/*
 * Callback.java
 *
 * Created on December 27, 2006, 8:52 AM
 *
 */

package org.wiztools.wizcrypt;

/**
 * Extend this abstract class to write your own monitoring class.
 * @author subhash
 * @see WizCrypt
 * @see WizCrypt#addCallback(org.wiztools.wizcrypt.Callback) 
 */
public interface Callback {
    
    /**
     * <code>begin()</code> is called just before the encryption/decryption
     * process starts.
     */
    public abstract void begin();
    
    /**
     * The encryption/decryption process works this way: it loads a bunch of
     * bytes from the InputStream and writes the processed bunch to the
     * OutputStream, and then proceeds to read & process the next bunch.
     * <code>notifyProgress(long value)</code> is called after every
     * bunch of bytes that have been processed. The <code>value</code> is either
     * the percentage of the progress in the encryption/decryption process, or
     * the number of bytes that have been processed. This choice depends on how
     * the Callback object has been initialized (with the size of the object)
     * as parameter or not.
     * 
     */
    public abstract void notifyProgress(long percentage, long bytes);
    
    /**
     * <code>end()</code> is called immediately after the encryption/decryption
     * process ends.
     */
    public abstract void end();
}
