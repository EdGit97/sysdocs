package com.ed.sysdocs;

import java.io.IOException;
import java.util.Date;

import org.xBaseJ.xBaseJException;

import com.ed.pojo.BackupMedia;
import com.ed.sysdocs.dao.MediaDbfDao;

public class DBFUpdateTest {
	public static final String rootDir = "C:/Src/sysdocs/"; 

	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		MediaDbfDao media = new MediaDbfDao(rootDir);
		BackupMedia bm = new BackupMedia();
		
		bm.setMediaId('T');
		bm.setFirstUse(new Date());
		bm.setLastUse(new Date());
		bm.setUseCount(1);
		bm.setActive(true);
		bm.setMediaType(MediaTypes.externalHD);
		
		try {
			System.out.println(bm.getMediaId() + (media.update(bm) ? " updated." : " not found."));
			 
		} 
		catch (SecurityException | xBaseJException | IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

}
