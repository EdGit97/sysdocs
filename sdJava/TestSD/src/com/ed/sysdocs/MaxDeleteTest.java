package com.ed.sysdocs;

import java.io.IOException;

import org.xBaseJ.xBaseJException;

import com.ed.sysdocs.dao.MediaMaxDbfDao;

public class MaxDeleteTest {
	public static final String rootDir = "C:/Src/sysdocs/"; 

	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		MediaMaxDbfDao props = new MediaMaxDbfDao(rootDir);
		boolean ok = false;
		
		try {
			ok = props.delete("Test");
		} 
		catch (xBaseJException | IOException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			props.pack();
		} 
		catch (SecurityException | xBaseJException | IOException | CloneNotSupportedException e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Delete " + (ok ? "succeeded" : "failed"));
		
	}

}
