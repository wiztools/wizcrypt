package org.wiztools.wizcrypt;

import java.io.Console;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.LogManager;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.security.InvalidKeyException;
import java.security.GeneralSecurityException;

import java.io.IOException;
import java.io.File;

import java.util.ResourceBundle;

/**
 * The class which does the commandline parsing the calls the public APIs to encrypt/decrypt.
 */
public class Main{
    
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.wizcrypt.wizcryptmsg");
    
    private boolean isEncryptMode = false;
    private boolean isDecryptMode = false;
    private boolean useLegacyFormat = false;
    
    // Error booleans
    // Note: Even when just one of the input file
    // triggers any of these Exceptions, the flag
    // is set.
    private boolean IO_EXCEPTION = false;
    private boolean INVALID_PWD = false;
    private boolean PWD_MISMATCH = false;
    private boolean SECURITY_EXCEPTION = false;
    private boolean PARSE_EXCEPTION = false;
    private boolean CONSOLE_NOT_AVBL_EXCEPTION = false;
    private boolean DEST_FILE_EXISTS = false;
    private boolean PWD_ENCODING_EXCEPTION = false;
    
    // Error codes
    private static final int C_IO_EXCEPTION = 1;
    private static final int C_INVALID_PWD = 2;
    private static final int C_PWD_MISMATCH = 3;
    private static final int C_CONSOLE_NOT_AVBL_EXCEPTION = 7;
    private static final int C_DEST_FILE_EXISTS = 8;
    // Both of the above
    private static final int C_MULTIPLE_EXCEPTION = 4;
    private static final int C_SECURITY_EXCEPTION = 5;
    private static final int C_PARSE_EXCEPTION = 6;
    
    private Options generateOptions(){
        Options options = new Options();
        Option option = OptionBuilder.withLongOpt("password")
                .hasOptionalArg()
                .isRequired(false)
                .withDescription(rb.getString("msg.password"))
                .create('p');
        options.addOption(option);
        option = OptionBuilder.withLongOpt("encrypt")
                .isRequired(false)
                .withDescription(rb.getString("msg.encrypt"))
                .create('e');
        options.addOption(option);
        option = OptionBuilder.withLongOpt("decrypt")
                .isRequired(false)
                .withDescription(rb.getString("msg.decrypt"))
                .create('d');
        options.addOption(option);
        option = OptionBuilder.withLongOpt("help")
                .isRequired(false)
                .withDescription(rb.getString("msg.help"))
                .create('h');
        options.addOption(option);
        option = OptionBuilder.withLongOpt("version")
                .isRequired(false)
                .withDescription(rb.getString("msg.version"))
                .create();
        options.addOption(option);
        option = OptionBuilder.withLongOpt("verbose")
                .isRequired(false)
                .withDescription(rb.getString("msg.verbose"))
                .create('v');
        options.addOption(option);
        option = OptionBuilder.withLongOpt("force-overwrite")
                .isRequired(false)
                .withDescription(rb.getString("msg.force.overwrite"))
                .create('f');
        options.addOption(option);
        option = OptionBuilder.withLongOpt("keep")
                .isRequired(false)
                .withDescription(rb.getString("msg.keep"))
                .create('k');
        options.addOption(option);
        options.addOption(option);
        option = OptionBuilder.withLongOpt("recursive")
                .isRequired(false)
                .withDescription(rb.getString("msg.recursive"))
                .create('r');
        options.addOption(option);
        option = OptionBuilder.withLongOpt("legacy-format")
                .isRequired(false)
                .withDescription(rb.getString("msg.legacy.format"))
                .create('l');
        options.addOption(option);
        
        return options;
    }
    
    private void printCommandLineHelp(Options options){
        HelpFormatter hf = new HelpFormatter();
        String cmdLine = rb.getString("display.cmdline");
        String descriptor = rb.getString("display.detail");
        String moreHelp = rb.getString("display.footer");
        hf.printHelp(cmdLine, descriptor, options, moreHelp);
    }
    
    private void printVersionInfo(){
        InputStream is = this.getClass().getClassLoader()
                .getResourceAsStream("org/wiztools/wizcrypt/VERSION");
        try{
            int i = -1;
            byte[] buffer = new byte[0xFFFF];
            while((i=is.read(buffer))!=-1){
                System.out.print(new String(buffer, 0, i));
            }
            is.close();
            System.out.println();
        } catch(IOException ioe) {
            assert true: "VERSION file not found in Jar!";
        }
    }
    
    private char[] getConsolePassword(String msg) throws PasswordMismatchException,
            ConsoleNotAvailableException{
        Console cons = System.console();
        if(cons == null){
            throw new ConsoleNotAvailableException();
        }
        char[] passwd;
        passwd = cons.readPassword("%s ", msg);
        if(passwd == null){
            throw new PasswordMismatchException(rb.getString("err.no.pwd"));
        }
        
        return passwd;
    }
    
    private char[] getConsolePassword() throws PasswordMismatchException,
            ConsoleNotAvailableException{
        return getConsolePassword(rb.getString("msg.interactive.password"));
    }
    
    private char[] getConsolePasswordVerify() throws PasswordMismatchException,
            ConsoleNotAvailableException{
        char[] passwd = getConsolePassword(
                rb.getString("msg.interactive.password"));
        char[] passwd_retype = getConsolePassword(
                rb.getString("msg.interactive.password.again"));
        
        if(!Arrays.equals(passwd, passwd_retype)){
            throw new PasswordMismatchException(
                    rb.getString("err.interactive.pwd.not.match"));
        }
        return passwd;
    }
    
    public int getExitStatus(){
        int exitVal = 0;
        if(PARSE_EXCEPTION){
            exitVal = C_PARSE_EXCEPTION;
        } else if(CONSOLE_NOT_AVBL_EXCEPTION){
            exitVal = C_CONSOLE_NOT_AVBL_EXCEPTION;
        } else if(INVALID_PWD){
            exitVal = C_INVALID_PWD;
        } else if(SECURITY_EXCEPTION || PWD_ENCODING_EXCEPTION){
            exitVal = C_SECURITY_EXCEPTION;
        } else{
            int count = 0; // count the number of exceptions
            if(DEST_FILE_EXISTS){
                exitVal = C_DEST_FILE_EXISTS;
                count++;
            }
            if(IO_EXCEPTION){
                exitVal = C_IO_EXCEPTION;
                count++;
            }
            if(PWD_MISMATCH){
                exitVal = C_PWD_MISMATCH;
                count++;
            }
            if(count > 1){
                exitVal = C_MULTIPLE_EXCEPTION;
            }
        }
        return exitVal;
    }
    
    private void process(final File f,
            final char[] pwd,
            final ParamBean cpb)
            throws NoSuchAlgorithmException,
            InvalidKeyException,
            NoSuchPaddingException{
        boolean verbose = cpb.isVerbose();
        boolean recursive = cpb.isRecurseIntoDir();
        
        
        if(f.isDirectory()){
            if(recursive){
                for(File ff: f.listFiles()){
                    process(ff, pwd, cpb);
                }
            } else{
                System.err.println(MessageFormat.format(
                        rb.getString("err.is.dir"),
                        f.getAbsolutePath()));
            }
        }
        else { // is a file to be encrypted / decrypted
            try{
                final WizCrypt wc;
                if(isEncryptMode){
                    wc = useLegacyFormat?
                            WizCryptDriver.getEncryptInstance(Version.LEGACY, f, null, pwd, cpb)
                            :
                            WizCryptDriver.getEncryptInstance(f, null, pwd, cpb);

                }
                else { // is Decrypt mode
                    wc = useLegacyFormat? 
                            WizCryptDriver.getDecryptInstance(Version.LEGACY, f, null, pwd, cpb)
                            :
                            WizCryptDriver.getDecryptInstance(f, null, pwd, cpb);
                }
                wc.process();
                if(verbose){
                    System.out.println(
                            MessageFormat.format(
                            rb.getString("msg.verbose.success"),
                            f.getAbsolutePath()));
                }
            } 
            catch(WizCryptException ex) {
                if(ex instanceof DestinationFileExistsException) {
                    DEST_FILE_EXISTS = true;
                    System.err.println(ex.getMessage());
                }
                else if(ex instanceof PasswordMismatchException) {
                    PWD_MISMATCH = true;
                    String msg = rb.getString("err.pwd.not.match");
                    msg = MessageFormat.format(msg, f.getAbsolutePath());
                    System.err.println(msg);
                }
                else if(ex instanceof FileCorruptException) {
                    FileCorruptType code = ((FileCorruptException) ex).getErrorType();
                    String msg = null;
                    switch(code) {
                        case FILE_TRUNCATED:
                            msg = rb.getString("err.file.corrupt.truncated");
                            break;
                        case HEADER_CRC_ERROR:
                            msg = rb.getString("err.file.corrupt.header.crc");
                            break;
                        case DATA_CRC_ERROR:
                            msg = rb.getString("err.file.corrupt.data.crc");
                            break;
                        case FILE_MAGIC_NUMBER_ERROR:
                            msg = rb.getString("err.file.corrupt.magicnumber");
                            break;
                    }

                    System.err.println(msg);
                }
            }
            catch(IOException ex){
                IO_EXCEPTION = true;
                System.err.println(ex.getMessage());
            }
        }
    }
    
    public void process(String[] arg){
        try{
            LogManager.getLogManager().readConfiguration(
                    Main.class.getClassLoader()
                    .getResourceAsStream("org/wiztools/wizcrypt/logging.properties"));
        } catch(IOException ioe){
            assert true: "Logger configuration load failed!";
        }
        
        Options options = generateOptions();
        try{
            
            boolean forceOverwrite = false;
            boolean verbose = false;
            boolean keepSource = false;
            boolean recurseIntoDir = false;
            
            CommandLineParser parser = new GnuParser();
            CommandLine cmd = parser.parse(options, arg);
            if(cmd.hasOption('h')){
                printCommandLineHelp(options);
                return;
            }
            if(cmd.hasOption("version")){
                printVersionInfo();
                return;
            }
            
            // All other options require files as argument
            String[] args = cmd.getArgs();
            
            if(args == null || args.length == 0){
                throw new ParseException(rb.getString("err.no.file"));
            }
            
            if(cmd.hasOption('v')){
                verbose = true;
            }
            if(cmd.hasOption('f')){
                forceOverwrite = true;
            }
            if(cmd.hasOption('e')){
                isEncryptMode = true;
            }
            if(cmd.hasOption('d')){
                isDecryptMode = true;
            }
            if(cmd.hasOption('k')){
                keepSource = true;
            }
            if(cmd.hasOption('r')){
                recurseIntoDir = true;
            }
            if(cmd.hasOption('l')){
                useLegacyFormat = true;
            }
            if(isEncryptMode && isDecryptMode){
                throw new ParseException(rb.getString("err.both.selected"));
            }
            if(!isEncryptMode && !isDecryptMode){
                throw new ParseException(rb.getString("err.none.selected"));
            }
            char[] pwd = null;
            if(cmd.hasOption('p')){
                String pwdStr = cmd.getOptionValue('p');
                if(pwdStr == null){
                    pwd = isEncryptMode? getConsolePasswordVerify(): getConsolePassword();
                } else{
                    pwd = pwdStr.toCharArray();
                }
                
            } else{
                pwd = isEncryptMode? getConsolePasswordVerify(): getConsolePassword();
            }
                
            
            // Create CliParamBean object
            ParamBean cpb = new ParamBean();
            cpb.setForceOverwrite(forceOverwrite);
            cpb.setKeepSource(keepSource);
            cpb.setRecurseIntoDir(recurseIntoDir);
            cpb.setVerbose(verbose);
            
            for(int i=0;i<args.length;i++){
                File f = new File(args[i]);
                
                process(f, pwd, cpb);
            }
        }
        catch(ParseException pe){
            PARSE_EXCEPTION = true;
            System.err.println(pe.getMessage());
            printCommandLineHelp(options);
        }
        catch(ConsoleNotAvailableException cna){
            CONSOLE_NOT_AVBL_EXCEPTION = true;
            System.err.println(rb.getString("err.console.not.avbl"));
        }
        catch(PasswordMismatchException pme){
            INVALID_PWD = true;
            System.err.println(pme.getMessage());
        }
        catch(InvalidKeyException ike){
            INVALID_PWD = true;
            System.err.println(rb.getString("err.invalid.pwd"));
        }
        catch(GeneralSecurityException gse){
            SECURITY_EXCEPTION = true;
            System.err.println(gse.getMessage());
        }
    }
    
    public static void main(String[] arg){
        Main m = new Main();
        m.process(arg);
        System.exit(m.getExitStatus());
    }
    
}
