/*
 * WizCryptOld.java
 *
 * Created on 06 February 2007, 09:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wiztools.wizcrypt.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.wiztools.wizcrypt.Callback;
import org.wiztools.wizcrypt.CipherKey;
import org.wiztools.wizcrypt.WizCrypt;
import org.wiztools.wizcrypt.exception.PasswordMismatchException;

/**
 *
 * @author subhash
 */
public class WizCryptOld extends WizCrypt {
    
    /** Creates a new instance of WizCryptOld */
    public WizCryptOld() {
    }

    public void encrypt(final InputStream is, final OutputStream os, final CipherKey ck, final Callback cb, final long size) throws IOException {
    }

    public void decrypt(final InputStream is, final OutputStream os, final CipherKey ck, final Callback cb, final long size) throws IOException, PasswordMismatchException {
    }
    
}
