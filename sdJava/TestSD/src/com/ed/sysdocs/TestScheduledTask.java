package com.ed.sysdocs;

import java.io.IOException;

import org.xBaseJ.xBaseJException;

import com.ed.pojo.ScheduledTask;

public class TestScheduledTask {
	
	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		String rootDir = "C:/Src/sysdocs/";
		PropertiesOps properties = new PropertiesOps(rootDir);
		Scheduler s = new Scheduler(properties, rootDir);
		
	    s.processSchedule();
	    
	    for (ScheduledTask st : s.getTasks()) {
	    	System.out.println(st.toString());
	    }
		
	}

}
