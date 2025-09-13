package com.ed.sysdocs.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xBaseJ.DBF;
import org.xBaseJ.fields.CharField;
import org.xBaseJ.fields.LogicalField;
import org.xBaseJ.fields.NumField;
import org.xBaseJ.xBaseJException;

import com.ed.pojo.BackupMedia;
import com.ed.pojo.DbfMetaData;
import com.ed.sysdocs.MediaTypes;
import com.ed.sysdocs.SysConstants;

/**
 * Data Access Object (DAO) to manage the backup media data.<br>
 * The structure of the Media.dbf file is as follows:
 * <pre> Fld   Name       Type   Width   Dec
 * 001   MEDIAID      C     001
 * 002   FIRSTUSE     C     014
 * 003   LASTUSE      C     014
 * 004   USECOUNT     N     004     0
 * 005   ACTIVE       L     001
 * 006   MEDIATYPE    C     015
 * ** Total **            00049
 * 
 * Indexes
 * 1. media1.ndx: MEDIAID, unique
 * 2. media2.ndx: MEDIATYPE+LASTUSE, not unique</pre> 
 * @author Ed Swaneck
 * @version 1.0
 * @since 04/04/2024
 */
public class MediaDbfDao extends DBFUtilities {
	
	/** Filename of the media data file */
	public static final String dataFileName = "media.dbf";
	
	/** Filename of the primary index */
	public static final String index1Name = "media1.ndx";
	
	/** Filename of the secondary index */
	public static final String index2Name = "media2.ndx";
	
	// Internal properties
	private CharField mediaId;
	private CharField firstUse;
	private CharField lastUse;
	private NumField useCount;
	private LogicalField active;
	private CharField mediaType;
	private String dbfFileSpec;
	private String ndx1FileSpec;
	private String ndx2FileSpec;
	
	/**
	 * Constructor
	 * @param rootDir Root of the directory path where the data file is located
	 * @throws IOException If the table cannot be created
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	public MediaDbfDao(String rootDir) throws SecurityException, xBaseJException, IOException {
		super(rootDir);
		
		try {
			mediaId = new CharField(BackupMedia.Fields.MediaId.name(), BackupMedia.Fields.MediaId.getFieldLen());
			firstUse = new CharField(BackupMedia.Fields.FirstUse.name(), BackupMedia.Fields.FirstUse.getFieldLen());
			lastUse = new CharField(BackupMedia.Fields.LastUse.name(), BackupMedia.Fields.LastUse.getFieldLen());
			useCount = new NumField(BackupMedia.Fields.UseCount.name(), BackupMedia.Fields.UseCount.getFieldLen(), 0);
			active = new LogicalField(BackupMedia.Fields.Active.name());
			mediaType = new CharField(BackupMedia.Fields.MediaType.name(), BackupMedia.Fields.MediaType.getFieldLen());
		}
		catch (xBaseJException | IOException e) {
			// This will never happen
		}
		
		dbfFileSpec = buildFileSpec(rootDir, dataFileName);
		ndx1FileSpec = buildFileSpec(rootDir, index1Name);
		ndx2FileSpec = buildFileSpec(rootDir, index2Name);
		
		File dbf = new File(dbfFileSpec);
		File ndx1 = new File(ndx1FileSpec);
		File ndx2 = new File(ndx2FileSpec);
		
		if (!dbf.exists()) {
			create();
		}
		
		if (!ndx1.exists() || !ndx2.exists()) {
			index();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#create()
	 */
	@Override
	public void create() throws SecurityException, xBaseJException, IOException {
		DBF media = new DBF(dbfFileSpec, false);
		
		media.addField(mediaId);
		media.addField(firstUse);
		media.addField(lastUse);
		media.addField(useCount);
		media.addField(active);
		media.addField(mediaType);
		media.close();
		index();
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#index()
	 */
	@Override
	public void index() throws SecurityException, xBaseJException, IOException {
		String ndx2Key = buildKey(BackupMedia.Fields.MediaType.name(), BackupMedia.Fields.LastUse.name());
		
		createIndex(dbfFileSpec, ndx1FileSpec, BackupMedia.Fields.MediaId.name(), true);
		createIndex(dbfFileSpec, ndx2FileSpec, ndx2Key, false);

	}
	
	/**
	 * Create a new record
	 * @param bm The data to save
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public void insert(BackupMedia bm) throws SecurityException, xBaseJException, IOException {
		DBF media = new DBF(dbfFileSpec);
		
		media.useIndex(ndx1FileSpec);
		media.useIndex(ndx2FileSpec);

		media.getField(mediaId.Name).put(bm.getMediaIdAsString());
		
		if (bm.getFirstUse() == null) {
			media.getField(firstUse.Name).put("");
		}
		else {
			media.getField(firstUse.Name).put(sdf.format(bm.getFirstUse()));
		}
		
		if (bm.getLastUse() == null) {
			media.getField(lastUse.Name).put("");
		}
		else {
			media.getField(lastUse.Name).put(sdf.format(bm.getLastUse()));
		}
		
		media.getField(useCount.Name).put(String.valueOf(bm.getUseCount()));
		media.getField(active.Name).put(booleanToLogical(bm.isActive()));
		media.getField(mediaType.Name).put(bm.getMediaType().name());

		media.write();
		media.close();
		
	}
	
	/**
	 * Update an existing record
	 * @param bm The data to save
	 * @return true if the record was updated, otherwise false
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public boolean update(BackupMedia bm) throws SecurityException, xBaseJException, IOException {
		DBF media = new DBF(dbfFileSpec);
		boolean done = false;
		
		media.useIndex(ndx2FileSpec);
		media.useIndex(ndx1FileSpec);
		
		if (media.findExact(bm.getMediaIdAsString())) {
			
			if (bm.getFirstUse() == null) {
				media.getField(firstUse.Name).put("");
			}
			else {
				media.getField(firstUse.Name).put(sdf.format(bm.getFirstUse()));
			}
			
			if (bm.getLastUse() == null) {
				media.getField(lastUse.Name).put("");
			}
			else {
				media.getField(lastUse.Name).put(sdf.format(bm.getLastUse()));
			}
			
			media.getField(useCount.Name).put(String.valueOf(bm.getUseCount()));
			media.getField(active.Name).put(booleanToLogical(bm.isActive()));

			media.update();
			done = true;
			
		}

		media.close();
		
		return done;
		
	}
	
	/**
	 * Update the last used date and use count of the least recently used media of a given type
	 * @param mt Update the least recently used media of this type
	 * @return Media ID of the media item that was updated
	 * @throws SecurityException If the OS will not allow the table to be updated 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public char increment(MediaTypes mt) throws SecurityException, xBaseJException, IOException {
		List<BackupMedia> bml = listByType(mt, true);
		char mediaId = SysConstants.badMediaId; 
		BackupMedia bm = null;
		
		if (bml != null && !bml.isEmpty()) {
			bm = bml.get(0);
		}
		
		if (bm != null) {
			if (bm.getFirstUse() == null) {
				bm.setFirstUse(new Date());
			}
			
			bm.setLastUse(new Date());
			bm.incrementUseCount();
			update(bm);
			
			mediaId = bm.getMediaId();
			
		}
		
		return mediaId;
		
	}
	
	/**
	 * Delete a media record
	 * @param mediaId ID of the media to delete
	 * @return true if the delete was successful, otherwise false
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public boolean delete(char mediaId) throws xBaseJException, IOException {
		DBF media = new DBF(dbfFileSpec);
		boolean done = false;
		
		media.useIndex(ndx2FileSpec);
		media.useIndex(ndx1FileSpec);
		
		if (media.findExact(String.valueOf(mediaId))) {
			media.delete();
			done = media.deleted();
		}

		media.close();
		
		return done;
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#pack()
	 */
	@Override
	public void pack() throws xBaseJException, IOException, SecurityException, CloneNotSupportedException {
		
		packDbf(dbfFileSpec, ndx2FileSpec, ndx1FileSpec);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#loadMetaData()
	 */
	@Override
	public DbfMetaData loadMetaData() throws xBaseJException, IOException, SecurityException {
		
		return getMetaData(dbfFileSpec, ndx1FileSpec, ndx2FileSpec);
		
	}
	
	/**
	 * Generate a list of all backup media
	 * @param activeOnly true to retrieve only active media types
	 * @return A populated list of all backup media
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	public List<BackupMedia> listAll(boolean activeOnly) throws SecurityException, xBaseJException, IOException {
		DBF media = new DBF(dbfFileSpec);
		List<BackupMedia> ml = new ArrayList<>();
		
		media.useIndex(ndx1FileSpec);
		media.startTop();
		
		for (int i = 1; i <= media.getRecordCount(); i++) {
			media.findNext();
			
			if (!activeOnly || logicalToBoolean(media.getField(BackupMedia.Fields.Active.name()).get())) {
				ml.add(parseData(media));
			}
			
		}
		
		media.close();
		
		return ml;
		
	}
	
	/**
	 * List the media of a specific type
	 * @param requestedType The type of media to list
	 * @param activeOnly true to retrieve only active media types
	 * @return A list of media that matches the requested type
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	public List<BackupMedia> listByType(MediaTypes requestedType, boolean activeOnly) throws SecurityException, xBaseJException, IOException {
		DBF media = new DBF(dbfFileSpec);
		List<BackupMedia> ml = new ArrayList<>();
		boolean done = false;
		
		media.useIndex(ndx2FileSpec);
		
		try {
			media.find(requestedType.name());
		}
		catch (xBaseJException e) {
			done = true;
		}
		
		if (!done && media.getField(mediaType.Name).get().equals(requestedType.name())) {

			if (!activeOnly || logicalToBoolean(media.getField(BackupMedia.Fields.Active.name()).get())) {
				ml.add(parseData(media));
			}
			
			while (!done) {
				try {
					media.findNext();
					
					if (media.getField(mediaType.Name).get().equals(requestedType.name())) { 
						if (!activeOnly || logicalToBoolean(media.getField(BackupMedia.Fields.Active.name()).get())) {
							ml.add(parseData(media));
						}
					}
					else {
						done = true;
					}
					
				}
				catch (xBaseJException e) {
					done = true;
				}
				
			}
			
		}
		
		media.close();
		
		return ml;
		
	}
	
	/**
	 * Load a media record
	 * @param mediaId ID of the record to load
	 * @return The media data or null if the media ID does not exist
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	public BackupMedia read(String mediaId) throws SecurityException, xBaseJException, IOException {
		DBF media = new DBF(dbfFileSpec);
		BackupMedia bm = null;
		
		media.useIndex(ndx1FileSpec);
		
		if (media.findExact(mediaId)) {
			bm = parseData(media);
		}
		
		media.close();
		
		return bm;
		
	}
	
	/**
	 * Load data from the backup media table into a backup media record  
	 * @param media the table
	 * @return a backup media record
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws ArrayIndexOutOfBoundsException If the field position is invalid  
	 */
	private BackupMedia parseData(DBF media) throws ArrayIndexOutOfBoundsException, xBaseJException { 
		BackupMedia bm = new BackupMedia();
		
		bm.setMediaId(media.getField(mediaId.Name).get().charAt(0));
		bm.setFirstUse(parseDate(media.getField(firstUse.Name).get()));
		bm.setLastUse(parseDate(media.getField(lastUse.Name).get()));
		bm.setUseCount(Integer.valueOf(media.getField(useCount.Name).get().trim()));
		bm.setActive(logicalToBoolean(media.getField(active.Name).get()));
		bm.setMediaType(MediaTypes.valueOf(media.getField(mediaType.Name).get()));

		return bm;
		
	}
	
}
