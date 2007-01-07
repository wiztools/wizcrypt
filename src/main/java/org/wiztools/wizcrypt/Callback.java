/*
 * Callback.java
 *
 * Created on December 27, 2006, 8:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt;

/**
 * Implement this interface to write your own monitoring class.
 * @author subhash
 * @see WizCrypt
 * @see WizCrypt#encrypt(InputStream is, OutputStream os, 
 *      CipherKey ck, Callback cb, long size)
 * @see WizCrypt#decrypt(InputStream is, OutputStream os, 
 *      CipherKey ck, Callback cb, long size)
 */
public interface Callback {
    /**
     * <code>begin()</code> is called just before the encryption/decryption
     * process starts.
     */
    public void begin();
    
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
     *      CipherKey ck, Callback cb, long size)
     * @see WizCrypt#decrypt(InputStream is, OutputStream os, 
     *      CipherKey ck, Callback cb, long size)
     */
    public void notifyProgress(long value);
    
    /**
     * <code>end()</code> is called immediately after the encryption/decryption
     * process ends.
     */
    public void end();
}
