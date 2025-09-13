package com.ed.sysdocs;

/**
 * Error messages
 * @author Ed Swaneck
 * @version 1.0
 * @since 12/28/2023
 */
public enum ErrMsgs {

	/** Bad media ID error message */
	BAD_MEDIA("Media ID must be a letter."),

	/** Bad date error message */
	BAD_DATE("Invalid date, {0}, on media {1}."),

	/** The media ID already exists error message */
	DUPLICATE_MEDIA("Media ID {0} occurs multiple times."),

	/** Bad value entered for the maximum usage value error message */
	MEDIA_MAXUSAGE("{0} maximum usage must be a value between 1 and {1} inclusive."),
	
	/** Usage string for the command line increment */
	CLI_USAGE("Usage: java -cp C:/Src/WEB-INF/lib/* com.ed.sysdocs.IncrementMedia <rootDir> " + MediaTypes.list() + " [ -qm | -qn | -qmn ]"),
	
	/** Bad directory on the command line */
	CLI_BAD_DIR("Directory {0} does not exist or is not a directory."),
	
	/** Bad media type on the command line */
	CLI_BAD_TYPE("Unknown media type."),
	
	/** No existing media of the requested type */
	CLI_NO_TYPE("No existing media of type {0}."),
	
	/** Property must be a numeric value */
	PROP_NUMERIC("{0} must be a numeric value."),
	
	/** Length of the value is greater than the maximum */
	VALUE_MAXLEN("{0} length is greater than {1}."),
	
	/** Number of values in a multi-value property is greater than the allowable amount */
	VALUES_MAX("The number of values cannot be greater than" + SysConstants.maxMultiValues + ".");
	
	private String msg;
	
	/**
	 * Constructor
	 * @param msg the actual error message associated with this error
	 */
    private ErrMsgs(String msg) {
	    this.msg = msg;
	}
    
    /**
     * Display an error message
     * @return the associated error message
     */
	public String getMsg() {
		return msg;
	}
	
	/**
	 * Display an error message
	 * @param parms values to be substituted into the message
	 * @return error message with markers replaced by parms
	 */
	public String getMsg(String ... parms) {
		String message = msg;
		
		for (int i = 0; i < parms.length; i++) {
			String pattern = "\\{" + String.valueOf(i) + "\\}";
			message = message.replaceFirst(pattern, parms[i]);
		}
		
		return message;
		
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
    @Override
	public String toString() {
    	
    	return this.name() + ": " + msg;
    	
	}

}
