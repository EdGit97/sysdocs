package com.ed.sysdocs;

import java.io.IOException;

import org.xBaseJ.xBaseJException;

import com.ed.sysdocs.dao.MediaMaxDbfDao;

public class MaxCreateTest {
	public static final String rootDir = "C:/Src/sysdocs/"; 
	
	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		new MediaMaxDbfDao(rootDir);
		
		System.out.println("sysdocsprops.dbf, sysdocsprops.ndx created successfully.");
		
	}

}
