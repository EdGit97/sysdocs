package com.ed.sysdocs;

import java.io.IOException;

import org.xBaseJ.xBaseJException;

import com.ed.sysdocs.dao.MediaDbfDao;

public class DBFDeleteTest {
	public static final String rootDir = "C:/Src/sysdocs/"; 

	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		MediaDbfDao media = new MediaDbfDao(rootDir);
		boolean ok = false;
		
		try {
			ok = media.delete('T');
		} 
		catch (xBaseJException | IOException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			media.pack();
		} 
		catch (SecurityException | xBaseJException | IOException | CloneNotSupportedException e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Delete " + (ok ? "succeeded" : "failed"));
		
	}

}
