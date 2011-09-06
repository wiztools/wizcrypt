/*
 * CliParamBean.java
 *
 * Created on 14 February 2007, 22:04
 *
 */

package org.wiztools.wizcrypt;

/**
 *
 * @author subhash
 */
public class ParamBean {
    
    private boolean forceOverwrite = false;
    private boolean keepSource = false;
    private boolean recurseIntoDir = false;
    private boolean verbose = false;
    
    /** Creates a new instance of CliParamBean */
    public ParamBean() {
    }
    
    public void setForceOverwrite(final boolean forceOverwrite){
        this.forceOverwrite = forceOverwrite;
    }
    
    public boolean isForceOverwrite(){
        return forceOverwrite;
    }
    
    public void setKeepSource(final boolean keepSource){
        this.keepSource = keepSource;
    }
    
    public boolean isKeepSource(){
        return keepSource;
    }
    
    public void setRecurseIntoDir(final boolean recurseIntoDir){
        this.recurseIntoDir = recurseIntoDir;
    }
    
    public boolean isRecurseIntoDir(){
        return recurseIntoDir;
    }
    
    public void setVerbose(final boolean verbose){
        this.verbose = verbose;
    }
    
    public boolean isVerbose(){
        return verbose;
    }
}
