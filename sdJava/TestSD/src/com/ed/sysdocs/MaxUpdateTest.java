package com.ed.sysdocs;

import java.io.IOException;

import org.xBaseJ.xBaseJException;

import com.ed.pojo.MediaMaximum;
import com.ed.sysdocs.dao.MediaMaxDbfDao;

public class MaxUpdateTest {
	public static final String rootDir = "C:/Src/sysdocs/"; 

	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		MediaMaxDbfDao props = new MediaMaxDbfDao(rootDir);
		MediaMaximum p = new MediaMaximum();
		
		p.setMediaType(MediaTypes.flash);
		p.setMaxUse(15);
		
		try {
			System.out.println(p.getMediaType().getDisplayName() + (props.update(p) ? " updated." : " not found."));
		} 
		catch (SecurityException | xBaseJException | IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

}
