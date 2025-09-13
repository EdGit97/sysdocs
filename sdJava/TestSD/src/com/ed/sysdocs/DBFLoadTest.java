package com.ed.sysdocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xBaseJ.xBaseJException;

import com.ed.sysdocs.dao.MediaDbfDao;
import com.ed.pojo.BackupMedia;

public class DBFLoadTest {
	public static final String rootDir = "C:/Src/sysdocs/"; 
	
	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		MediaDbfDao media = new MediaDbfDao(rootDir);
		List<BackupMedia> bml = new ArrayList<>();
		BackupMedia bm = new BackupMedia();
		
		bm.setMediaId('T');
		bm.setFirstUse(new Date());
		bm.setLastUse(null);
		bm.setUseCount(0);
		bm.setActive(true);
		bm.setMediaType(MediaTypes.externalHD);
		bml.add(bm);
		
		bm = new BackupMedia();
		bm.setMediaId('U');
		bm.setFirstUse(new Date());
		bm.setLastUse(new Date());
		bm.setUseCount(1);
		bm.setActive(true);
		bm.setMediaType(MediaTypes.externalHD);
		bml.add(bm);
		
		bm = new BackupMedia();
		bm.setMediaId('F');
		bm.setFirstUse(new Date());
		bm.setLastUse(new Date());
		bm.setUseCount(1);
		bm.setActive(true);
		bm.setMediaType(MediaTypes.flash);
		bml.add(bm);
		
		bm = new BackupMedia();
		bm.setMediaId('G');
		bm.setFirstUse(null);
		bm.setLastUse(null);
		bm.setUseCount(0);
		bm.setActive(true);
		bm.setMediaType(MediaTypes.flash);
		bml.add(bm);
		
		try {
			for (BackupMedia m : bml) {
				media.insert(m);
			}

			System.out.println(bml.size() + " records loaded.");
			
		} 
		catch (SecurityException | xBaseJException | IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

}
