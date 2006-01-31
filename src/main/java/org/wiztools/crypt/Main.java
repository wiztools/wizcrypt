package org.wiztools.crypt;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.security.GeneralSecurityException;

import java.io.IOException;
import java.io.File;

import java.util.ResourceBundle;

public class Main{

	private static final ResourceBundle rb = ResourceBundle.getBundle("wizcryptmsg");

	private Options generateOptions(){
		Options options = new Options();
		Option option = OptionBuilder.withLongOpt("password").hasArg().isRequired(true).withDescription(rb.getString("msg.password")).create('p');
		options.addOption(option);
		option = OptionBuilder.withLongOpt("encrypt").isRequired(false).withDescription(rb.getString("msg.encrypt")).create('e');
		options.addOption(option);
		option = OptionBuilder.withLongOpt("decrypt").isRequired(false).withDescription(rb.getString("msg.decrypt")).create('d');
		options.addOption(option);
		option = OptionBuilder.withLongOpt("help").isRequired(false).withDescription(rb.getString("msg.help")).create('h');
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

	public Main(String[] arg){
		Options options = generateOptions();
		try{
			boolean encrypt = false;
			boolean decrypt = false;
			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, arg);
			if(cmd.hasOption('h')){
				printCommandLineHelp(options);
				System.exit(0);
			}
			if(cmd.hasOption('e')){
				encrypt = true;
			}
			if(cmd.hasOption('d')){
				decrypt = true;
			}
			if(encrypt && decrypt){
				throw new ParseException(rb.getString("err.both.selected"));
			}
			if(cmd.hasOption('p')){
				String pwd = cmd.getOptionValue('p');
				if(pwd == null){
					throw new ParseException(rb.getString("err.no.pwd"));
				}
				IProcess iprocess = null;
				if(encrypt){
					iprocess = new Encrypt();
				}
				else if(decrypt){
					iprocess = new Decrypt();
				}
				else{
					throw new ParseException(rb.getString("err.none.selected"));
				}
				iprocess.init(pwd);
				String[] args = cmd.getArgs();
				if(args.length == 0){
					throw new ParseException(rb.getString("err.no.file"));
				}
				for(int i=0;i<args.length;i++){
					File f = new File(args[i]);
					try{
						iprocess.process(f);
					}
					catch(PasswordMismatchException pme){
						System.err.println(pme.getMessage());
					}
					catch(IOException ioe){
						System.err.println(ioe.getMessage());
					}
				}
			}
		}
		catch(ParseException pe){
			System.err.println(pe.getMessage());
			printCommandLineHelp(options);
		}
		catch(GeneralSecurityException gse){
			System.err.println(gse.getMessage());
		}
	}

	public static void main(String[] arg){
		new Main(arg);
	}
}
