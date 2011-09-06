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
 * @see WizCrypt#encrypt(InputStream is, OutputStream os, 
 *      WizCryptBean wcb)
 * @see WizCrypt#decrypt(InputStream is, OutputStream os, 
 *      WizCryptBean wcb)
 */
public abstract class Callback {
    
    private final long size;
    
    /**
     * Use this constructor when encryption/decryption progress has to be displayed
     * in terms of bytes processed.
     */
    public Callback(){
        size = -1;
    }
    
    /**
     * Use this constructor when encryption/decryption progress has to be displayed
     * in terms of percentage completed.
     * @param size The size of the stream/file.
     */
    public Callback(long size){
        this.size = size;
    }
    
    public final long getSize(){
        return size;
    }
    
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
     * the number of bytes that have been processed. This choice depends on the
     * parameter given to the <code>WizCrypt</code>'s 
     * <code>encrypt()</code>/<code>decrypt()</code> method.
     * 
     * @see WizCrypt#encrypt(InputStream is, OutputStream os, 
     *      WizCryptBean wcb)
     * @see WizCrypt#decrypt(InputStream is, OutputStream os, 
     *      WizCryptBean wcb)
     */
    public abstract void notifyProgress(long value);
    
    /**
     * <code>end()</code> is called immediately after the encryption/decryption
     * process ends.
     */
    public abstract void end();
}
