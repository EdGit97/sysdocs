package com.ed.sysdocs;

import java.io.IOException;
import java.util.List;

import org.xBaseJ.xBaseJException;

import com.ed.pojo.BackupMedia;
import com.ed.sysdocs.dao.MediaDbfDao;

public class DBFReadTest {
	public static final String rootDir = "C:/Src/sysdocs/"; 
	
	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		MediaDbfDao media = new MediaDbfDao(rootDir);

		try {
			List<BackupMedia> ml = media.listAll(false);
			
			for (BackupMedia bm : ml) {
				System.out.println(bm.toString());
			}
			
			BackupMedia bm2 = media.read("T");
			
			if (bm2 == null) {
				System.out.println("Record T not found.");
			}
			else {
				System.out.println(bm2.toString());
			}
			
			ml = media.listByType(MediaTypes.externalHD, false);
			
			for (BackupMedia bm : ml) {
				System.out.println(bm.toString());
			}
			
			ml = media.listByType(MediaTypes.flash, false);
			
			for (BackupMedia bm : ml) {
				System.out.println(bm.toString());
			}
			
		} 
		catch (SecurityException | xBaseJException | IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

}
