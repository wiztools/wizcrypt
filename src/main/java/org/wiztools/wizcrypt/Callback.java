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
 *
 * @author schandran
 */
public interface Callback {
    public void begin();
    public void notifyProgress(long value);
    public void end();
}
