package com.ed.sysdocs;

import java.io.IOException;
import java.util.List;

import org.xBaseJ.xBaseJException;

import com.ed.pojo.MediaMaximum;
import com.ed.sysdocs.dao.MediaMaxDbfDao;

public class MaxReadTest {
	public static final String rootDir = "C:/Src/sysdocs/"; 
	
	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		MediaMaxDbfDao props = new MediaMaxDbfDao(rootDir);
		
		try {
			List<MediaMaximum> pl = props.listAll();
			
			for (MediaMaximum p : pl) {
				System.out.println(p.getMediaType().getDisplayName() + "|" + String.valueOf(p.getMaxUse()));
			}
			
			MediaMaximum p = props.read(MediaTypes.tape.name());
			
			System.out.println(p.getMediaType() + "|" + p.getMaxUse());
			
			p = props.read(MediaTypes.externalHD.name());
			
			System.out.println(p.getMediaType() + "|" + p.getMaxUse());
			
		}
		catch (SecurityException | xBaseJException | IOException e) {
			System.out.println(e.getMessage());
		}

	}

}
