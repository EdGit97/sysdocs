package com.ed.sysdocs;

import java.io.IOException;

import org.xBaseJ.xBaseJException;

import com.ed.sysdocs.dao.MediaDbfDao;

public class DBFReindex {
	public static final String rootDir = "C:/Src/sysdocs/"; 
	
	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		MediaDbfDao media = new MediaDbfDao(rootDir);
		
		try {
			media.index();
			System.out.println("Media reindexed successfully.");
		} 
		catch (SecurityException | xBaseJException | IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

}
