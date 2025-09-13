package com.ed.sysdocs;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

public class LogLoadTest {

	/**
	 * Filter out log files from the log directory
	 */
	private class LogFilter implements FileFilter {

		/* (non-Javadoc)
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File pathname) {

			return pathname.isFile() && 
				   pathname.getName().startsWith("") && 
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
			
	        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			
		}
	    
	}
	
	protected String rootDir = "C:/Src/sysdocs/";
	
	public static void main(String [] args) {
		LogLoadTest llt = new LogLoadTest();
		
		System.out.println(llt.getLatestLogs());
		
	}
	
	public String getLatestLogs() {
		File logDir = new File(rootDir + SysConstants.logDir);
		LogFilter logFilter = new LogFilter(); 
		SortLogFiles logSort = new SortLogFiles();
		File [] logs = logDir.listFiles(logFilter);
		StringBuilder link;
		
		if (logs == null || logs.length <= 0) {
			link = new StringBuilder("<li>No logs found.</li>");
		
		}
		else {
			link = new StringBuilder();
			Arrays.sort(logs, logSort);
			
			for (int i = 0; i < 9 && i < logs.length; i++) {
				link.append("<li><a href='");
				link.append(SysConstants.logDir);
				link.append(logs[i].getName());
				link.append("' target='_blank'>");
				link.append(logs[i].getName());
				link.append("</a></li>");
				link.append(SysConstants.newline);
			}
			
		}

		return link.toString();

	}
	

}

