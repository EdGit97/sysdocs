package com.ed.sysdocs;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Log management utility
 * @author Ed Swaneck
 * @version 1.0
 * @since 04-03-2025
 */
public class LogOps {
	
	/** Format the task last run time */
	private static final SimpleDateFormat lrun = new SimpleDateFormat(SysConstants.logTimestampDisplay);
	
	/**
	 * Include only log files from the log directory
	 */
	private class LogFilter implements FileFilter {
		
		private String logFileNamePrefix;
		
		/**
		 * Constructor
		 * @param logFileNamePrefix String that the log file name begins with
		 */
		public LogFilter(String logFileNamePrefix) {
			super();
			this.logFileNamePrefix = logFileNamePrefix;
		}

		/* (non-Javadoc)
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File pathname) {

			return pathname.isFile() && 
				   pathname.getName().regionMatches(true, 0, logFileNamePrefix, 0, logFileNamePrefix.length()) && 
				   pathname.getName().endsWith(SysConstants.logExt);
			
		}
		
	}

	/**
	 * Sort the list of log files
	 */
	private class SortLogFiles implements Comparator<File> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(File f1, File f2) {
			
			// Newest to oldest
	        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			
		}
	    
	}

	/** Directory of the site */
	protected String rootDir;

	/**
	 * Constructor
	 * @param rootDir The absolute path, on the server, where the web page page resides 
	 */
	public LogOps(String rootDir) {
		
		super();
		this.rootDir = rootDir;
		
	}
	
	/**
	 * Find the most recent log file
	 * @param logPrefix String that starts the name of the log file
	 * @return The log file or null if no log file exists 
	 */
	public File getLatestLog(String logPrefix) {
		File logDir = new File(rootDir + SysConstants.logDir);
		LogFilter logFilter = new LogFilter(logPrefix); 
		SortLogFiles logSort = new SortLogFiles();
		File [] logs = logDir.listFiles(logFilter);
		File log = null;
	
		if (logs.length > 0) {
			Arrays.sort(logs, logSort);
			log = logs[0];
		}
		
		return log;
		
	}
	
	/**
	 * Build a link to a log file
	 * @param logFile The log file
	 * @param linkContent The value that will be displayed in the link.  If this value is null, the last modified date of the logFile will. be used.
	 * @return An HTML link to the log file
	 */
	public static String buildLogLink(File logFile, String linkContent) {
		StringBuilder logLink = new StringBuilder("<a href='#' onclick=\"javascript: showLog('");
		
		logLink.append(SysConstants.logDir);
		logLink.append(fixBrackets(logFile.getName()));
		logLink.append("')\">");
		
		if (linkContent == null || linkContent.length() <= 0) {
			logLink.append(lrun.format(new Date(logFile.lastModified())));
		}
		else {
			logLink.append(linkContent);
		}
		
		logLink.append("</a>");

		return logLink.toString();
		
	}
	
	/**
	 * Build a link to a log file
	 * @param logFile The log file
	 * @return An HTML link to the log file
	 */
	public static String buildLogLink(File logFile) {
		return buildLogLink(logFile, null);
	}

	/**
	 * Fix brackets in a log file URL
	 * @param url The URL to check
	 * @return The URL with and [ replaced by %5B and any ] replaced by %5D
	 */
	private static String fixBrackets(String url) {
		
		return url.replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
		
	}
	
}
