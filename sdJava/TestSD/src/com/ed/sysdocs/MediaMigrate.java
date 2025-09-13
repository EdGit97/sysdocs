package com.ed.sysdocs;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xBaseJ.DBF;
import org.xBaseJ.fields.CharField;
import org.xBaseJ.fields.LogicalField;
import org.xBaseJ.fields.NumField;

import com.ed.pojo.BackupMedia;
import com.ed.sysdocs.dao.MediaDbfDao;

import org.xBaseJ.xBaseJException;

public class MediaMigrate {
	public static final String rootDir = "C:/Src/sysdocs/"; 
	public static final String oldDataDir = rootDir + "data/save/";
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat(SysConstants.timestampFmt);
	
	private static CharField mediaId;
	private static CharField firstUse;
	private static CharField lastUse;
	private static NumField useCount;
	private static LogicalField active;
	private static CharField type;
	
	public static void main(String [] args) throws xBaseJException, IOException {
		DBF old = new DBF(oldDataDir + "media.dbf");
		List<BackupMedia> ml = new ArrayList<>();
		int recNo = 1;
		
		while (recNo <= old.getRecordCount()) {
			old.gotoRecord(recNo);
					
			mediaId = new CharField(BackupMedia.Fields.MediaId.name(), BackupMedia.Fields.MediaId.getFieldLen());
			firstUse = new CharField(BackupMedia.Fields.FirstUse.name(), BackupMedia.Fields.FirstUse.getFieldLen());
			lastUse = new CharField(BackupMedia.Fields.LastUse.name(), BackupMedia.Fields.LastUse.getFieldLen());
			useCount = new NumField(BackupMedia.Fields.UseCount.name(), BackupMedia.Fields.UseCount.getFieldLen(), 0);
			active = new LogicalField(BackupMedia.Fields.Active.name());
			type = new CharField("TYPE", BackupMedia.Fields.MediaType.getFieldLen());
			
			BackupMedia bm = new BackupMedia();
			
			bm.setMediaId(old.getField(mediaId.Name).get().charAt(0));
			bm.setFirstUse(parseDate(old.getField(firstUse.Name).get()));
			bm.setLastUse(parseDate(old.getField(lastUse.Name).get()));
			bm.setUseCount(Integer.valueOf(old.getField(useCount.Name).get().trim()));
			bm.setActive(logicalToBoolean(old.getField(active.Name).get()));
			bm.setMediaType(MediaTypes.valueOf(old.getField(type.Name).get()));
			
			ml.add(bm);
			recNo++;
			
		}
		
		old.close();
		
		MediaDbfDao media = new MediaDbfDao(rootDir);
		
		for (BackupMedia bm : ml) {
			media.insert(bm);
		}
		
	}

	/**
	 * Convert a date string to a Java Date
	 * @param date the string to convert
	 * @return a Date or null of the string does not represent a valid date
	 */
	private static Date parseDate(String date) {
		Date d = null;
		
		try {
			d = sdf.parse(date);
		} 
		catch (ParseException e) {}
		
		return d;
		
	}
	
	/**
	 * Convert a LOGICAL value to boolean 
	 * @param inValue The value to evaluate
	 * @return true if inValue equals T, otherwise false
	 */
	private static boolean logicalToBoolean(String inValue) {
		
        return (inValue != null && inValue.length() > 0 && inValue.charAt(0) == LogicalField.BYTETRUE);
        	
	}
	
}
