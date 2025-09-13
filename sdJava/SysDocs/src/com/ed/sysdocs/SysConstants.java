package com.ed.sysdocs;

/**
 * Constants for the media management application
 * @author Ed Swaneck
 * @version 1.0
 * @since 12/28/2023
 */
public class SysConstants {
	
	/** Date format pattern for timestamp data */
	public static final String timestampFmt = "yyyyMMddHHmmss";
	
	/** Date format pattern for a displayed timestamp */
	public static final String timestampDisplay = "yyyy-MM-dd HH:mm:ss";
	
	/** Format used to display the last modified date of a log file */
	public static final String logTimestampDisplay = "EEE, MM-dd-yyyy @ HH:mm";
	
	/** Internal FFS XML format of the task start times */
	public static final String taskDateFormat = "yyyy-MM-dd HH:mm:ss";
	
	/** Subdirectory where the data is stored */
	public final static String dataDir = "data/";
	
	/** Subdirectory where the backup logs are located */
	public final static String logDir = "backup/logs/";
	
	/** File extension for log files */
	public final static String logExt = ".html";

	/** Newline character */
	public final static char newline = '\n';
	
	/** Record separator character */
	public final static char recSep = '|';
	
	/** Directory separator */
	public final static char dirSep = '/';
	
	/** Header for the lifetime exceeded footnote */
	public final static char lifeExceededFootNote = '2';
	
	/** Media ID for an unknown media device */
	public static final char badMediaId = '*';
	
	/** Valid HTTP Servlet request method types */
	public enum HttpRequestTypes {
		
		/** Get request type */
		GET, 
		
		/** Post request type */
		POST;
	}
	
	/** Format for displaying the field position value for the metadata table */
	public final static String fieldCounterFormat = "000";

	/** Format for displaying the record count value for the metadata table */
	public final static String recordCountFormat = "00000";
	
	/** The maximum number of values in a multi-value field */
	public static final int maxMultiValues = 10;

}
