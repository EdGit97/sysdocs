package com.ed.sysdocs;

import java.awt.SystemTray;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Parse and validate the IncrementMedia command line
 * @author Ed Swaneck
 * @version 1.0
 * @since 05-04-2024
 */
public class CmdLineParms {
	
	/** Quiet mode command line flag */
	public static final String quietModeParm = "-q";
	
	/** Root directory from the command line */
	protected String rootDir;
	
	/** Requested media type from the command line */
	protected MediaTypes mediaType;
	
	/** true if suppress displaying the completion message, otherwise false */
	protected boolean quietMsg = false;
	
	/** true if to suppress displaying the completion notification, otherwise false */
	protected boolean quietNote = false;
	
	/** List of error messages */
	protected List<String> errs = new ArrayList<>();
	
	/**
	 * Constructor
	 * @param args The command line arguments
	 */
	public CmdLineParms(String [] args) {
		super();
		
		if (args.length < 2) {
			errs.add(ErrMsgs.CLI_USAGE.getMsg());
		}
		else {
			rootDir = args[0];
			parseMediaType(args[1]);
			parseQuiet(args);
			validate();
		}
		
	}
	
	/**
	 * Parse the second argument and convert to a media type
	 * @param arg The requested media type
	 */
	protected void parseMediaType(String arg) {
		
		try {
			mediaType = MediaTypes.valueOf(arg);
		}
		catch (IllegalArgumentException | NullPointerException e) {
			mediaType = null;
		}
		
	}
	
	/**
	 * Parse the quiet mode parameters
	 * @param args The command line arguments
	 */
	protected void parseQuiet(String [] args) {
		
		if (args.length > 2 && args[2].startsWith(quietModeParm)) {
			String quiet = args[2].toUpperCase();
			
			quietMsg = (quiet.indexOf('M') >= 0);
			quietNote = (quiet.indexOf('N') >= 0 || !SystemTray.isSupported());
			
		}
		
	}
	
	/**
	 * Validate the values parsed from the command line
	 */
	protected void validate() {
		File dir = new File(rootDir);
		
		if (!dir.exists() || !dir.isDirectory()) {
			errs.add(ErrMsgs.CLI_BAD_DIR.getMsg(rootDir));
		}
		
		if (mediaType == null) {
			errs.add(ErrMsgs.CLI_BAD_TYPE.getMsg());
		}
		
	}
	
	/**
	 * @return Root directory from the command line
	 */
	public String getRootDir() {
		return rootDir;
	}
	
	/**
	 * @return Requested media type from the command line
	 */
	public MediaTypes getMediaType() {
		return mediaType;
	}

	/**
	 * @return true to suppress the result message, otherwise false
	 */
	public boolean isQuietMsg() {
		return quietMsg;
	}

	/**
	 * @return  true to suppress the result notification, otherwise false
	 */
	public boolean isQuietNote() {
		return quietNote;
	}

	/**
	 * @return The list of error messages
	 */
	public List<String> getErrs() {
		return errs;
	}
	
	/**
	 * Add an error message to the list of error messages
	 * @param err The error message to add to the list
	 */
	public void addErr(String err) {
		errs.add(err);
	}
	
	/**
	 * @return true if the parameters are valid, otherwise false
	 */
	public boolean parmsOk() {
		return errs.isEmpty();
	}

}
