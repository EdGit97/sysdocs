package com.ed.sysdocs.dao;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.LogicalField;

import com.ed.pojo.DbfMetaData;
import com.ed.sysdocs.SysConstants;

/**
 * General utilities for processing DBF files
 * @author Ed Swaneck
 * @version 1.0
 * @since 04/07/2024
 */
public abstract class DBFUtilities {
	
	/** Operator for joining terms together in an index key */
	public static final char keyOp = '+';
	
	/** Date format for storing date/times */
	protected static final SimpleDateFormat sdf = new SimpleDateFormat(SysConstants.timestampFmt);

	/** Root of the directory path where the data file is located */
	protected String rootDir;
	
	/**
	 * Getter
	 * @return Root of the directory path where the data file is located
	 */
	public String getRootDir() {
		return rootDir;
	}
	
	/**
	 * Constructor
	 * @param rootDir Root of the directory path where the data file is located
	 */
	public DBFUtilities(String rootDir) {
		
		super();
		this.rootDir = rootDir;
		
	}
	
	/**
	 * Create the DBF file
	 * @throws IOException If the table cannot be created
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	public abstract void create() throws SecurityException, xBaseJException, IOException;
	
	/**
	 * Create or re-index the index(es) of a DBF table 
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public abstract void index() throws SecurityException, xBaseJException, IOException;

	/**
	 * Pack the table and update the index(es)
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws CloneNotSupportedException Java error caused by called methods 
	 */
	public abstract void pack() throws xBaseJException, IOException, SecurityException, CloneNotSupportedException;
	
	/**
	 * Assemble the metadata of a DBF
	 * @return The DBF metadata
	 * @throws xBaseJException If the table doesn't exist
	 * @throws IOException If the table cannot be read or updated
	 * @throws SecurityException If the OS will not allow the table to be accessed 
	 */
	public abstract DbfMetaData loadMetaData() throws xBaseJException, IOException, SecurityException;
	
	/**
	 * Build the filespec for a DBF or NDX
	 * @param rootDir Root of the directory path where the data file is located
	 * @param fn The name of the file
	 * @return The complete filespec for a data file or index
	 */
	protected String buildFileSpec(String rootDir, String fn) {
		StringBuilder fs = new StringBuilder(rootDir);
		
		if (!rootDir.endsWith(String.valueOf(SysConstants.dirSep)) && 
			!fn.startsWith(String.valueOf(SysConstants.dirSep))) {
			fs.append(SysConstants.dirSep);
		}
		
		fs.append(SysConstants.dataDir);
		fs.append(fn);
		
		return fs.toString();
		
	}
	
	/**
	 * Generate an index.  Any current index will be deleted.
	 * @param dbfFileSpec File specification of the DBF file to index
	 * @param ndxFileSpec File specification of the index file to create
	 * @param key Index expression 
	 * @param unique true if this index will generate unique keys, otherwise false
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	protected void createIndex(String dbfFileSpec, 
			                   String ndxFileSpec, 
			                   String key, 
			                   boolean unique) 
			  throws SecurityException, 
			         xBaseJException, 
			         IOException {
		DBF dbf = new DBF(dbfFileSpec);
		File fileId = new File(ndxFileSpec);
		
		if (fileId.exists()) {
			fileId.delete();
		}
		
		dbf.createIndex(ndxFileSpec, key, true, unique);
		dbf.close();
		
	}
	
	/**
	 * Generate an index key from a list of field names
	 * @param terms The list of field names that will form the key
	 * @return The complete index key expression
	 */
	protected String buildKey(String... terms) {
		StringBuilder key = new StringBuilder();
		
		for (String term : terms) {
			if (key.length() > 0) {
				key.append(keyOp);
			}
			
			key.append(term);
			
		}
		
		return key.toString();
		
	}
	
	/**
	 * Pack the table and update the index(es)
	 * @param dbfFileSpec File specification of the DBF file to index
	 * @param ndxFileSpecs File specification(s) of the index file(s) to create
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws CloneNotSupportedException Java error caused by called methods 
	 */
	protected void packDbf(String dbfFileSpec, String... ndxFileSpecs) throws xBaseJException, IOException, SecurityException, CloneNotSupportedException {
		DBF mm = new DBF(dbfFileSpec);
		
		for (String ndxFs : ndxFileSpecs) {
			mm.useIndex(ndxFs);
		}
		
		mm.pack();
		mm.close();
		index();
		
	}

	/**
	 * Convert a date string to a Java Date
	 * @param date the string to convert
	 * @return a Date or null of the string does not represent a valid date
	 */
	protected Date parseDate(String date) {
		Date d = null;
		
		try {
			d = sdf.parse(date);
		} 
		catch (ParseException e) {}
		
		return d;
		
	}
	
	/**
	 * Convert a boolean to a LOGICAL
	 * @param inValue The boolean value to convert
	 * @return T or F
	 */
	protected String booleanToLogical(boolean inValue) {
		return String.valueOf((char) (inValue ? LogicalField.BYTETRUE : LogicalField.BYTEFALSE));  
	}
	
	/**
	 * Convert a LOGICAL value to boolean 
	 * @param inValue The value to evaluate
	 * @return true if inValue equals T, otherwise false
	 */
	protected boolean logicalToBoolean(String inValue) {
		
        return (inValue != null && inValue.length() > 0 && inValue.charAt(0) == LogicalField.BYTETRUE);
        	
	}
	
	/**
	 * Gather the metadata for a DBF file
	 * @param dbfFileSpec File specification of the DBF file
	 * @param ndxFileSpecs Zero or more index file specifications
	 * @return The metadata for the DBF file
	 * @throws SecurityException If the OS will not allow the table to be accessed 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	protected DbfMetaData getMetaData(String dbfFileSpec, String... ndxFileSpecs) throws SecurityException, xBaseJException, IOException {
		DBF dbf = new DBF(dbfFileSpec);
		File dbfFile = new File(dbfFileSpec);
		Date dbfUpdated = new Date(dbfFile.lastModified());
		DbfMetaData metaData = new DbfMetaData();
		
		metaData.setFileSpec(dbf.getName());
		metaData.setDbfVersion(dbf.getVersion());
		metaData.setRecordCount(dbf.getRecordCount());
		metaData.setLastUpdated(dbfUpdated);
		
		for (int i = 1; i <= dbf.getFieldCount(); i++) {
			metaData.addField(dbf.getField(i));
		}
		
		for (String ndx : ndxFileSpecs) {
			metaData.addIndex(dbf.useIndex(ndx));
		}
		
		dbf.close();
		
		return metaData;
		
	}

}
