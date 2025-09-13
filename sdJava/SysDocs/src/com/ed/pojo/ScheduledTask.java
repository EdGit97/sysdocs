package com.ed.pojo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ed.sysdocs.LogOps;
import com.ed.sysdocs.SysConstants;
import com.ed.sysdocs.TableHtml;

/**
 * POJO to hold data for a scheduled task.
 * @author Ed Swaneck
 * @version 1.0
 * @since 04-04-2025
 */
public class ScheduledTask {
	
	/** Format the task start time */
	private static final SimpleDateFormat stime = new SimpleDateFormat("HH:mm");
	
	/** Name of the scheduled task */
	protected String taskName; 
	
	/** Time the scheduled job will run */
	protected Date scheduledTime;
	
    /** Most recent log file */
    protected File logFile;
    
    /** Command that the scheduled task executes */
    protected String cmd;
    
    /**
     * Constructor
     * @param taskName Name of the scheduled job
     * @param scheduledTime Time the scheduled job will run
     * @param logFile Most recent log file
     * @param cmd Command that the scheduled task executes
     */
    public ScheduledTask(String taskName, Date scheduledTime, File logFile, String cmd) {
    	
    	super();
    	
    	this.taskName = taskName;
    	this.scheduledTime = scheduledTime;
    	this.logFile = logFile;
    	this.cmd = cmd;
    	
    }

    /**
     * Format this instance&apos;s data as an HTML table row
     * @param odd true if this an odd numbered row, otherwise false
     * @return An HTML table row
     */
    public String makeTaskRow(boolean odd) {
    	StringBuilder row = new StringBuilder();
    	
		row.append((odd ? TableHtml.RowStartOdd : TableHtml.RowStart).getTag());
		row.append(TableHtml.buildCol("jobName", null, 1, taskName));
		row.append(TableHtml.buildCol("time", "stime", 1, getScheduledHourMinute()));
		
		if (logFile == null) {
			row.append(TableHtml.buildCol("time", null, 1, ""));
		}
		else {
			row.append(TableHtml.buildCol("time", null, 1, LogOps.buildLogLink(logFile)));
		}
		
		row.append(TableHtml.buildCol("links", null, 1, cmd));
		row.append(TableHtml.RowEnd.getTag());
		row.append(SysConstants.newline);
    	
    	return row.toString();
    	
    }

    /**
     * Getter
     * @return Name of the scheduled job
     */
	public String getTaskName() {
		return taskName;
	}

    /**
     * Getter
     * @return Time the scheduled job will run
     */
	public Date getScheduledTime() {
		return scheduledTime;
	}
	
	/**
	 * Getter
	 * @return The scheduled run time formatted as HH:mm
	 */
	public String getScheduledHourMinute() {
		String hm = "00:00";
		
		if (scheduledTime != null) {
			hm = stime.format(scheduledTime);
		}
		
		return hm;
	}

    /**
     * Getter
     * @return Most recent log file
     */
	public File getLogFile() {
		return logFile;
	}

    /**
     * Getter
     * @return Command that the scheduled task executes
     */
	public String getCmd() {
		return cmd;
	}

	/**
	 * Getter
	 * @return Date and time the task was run last
	 */
	public Date getLastRun() {
		
		return new Date(logFile.lastModified());
		
	}
    
}
