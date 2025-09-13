package com.ed.sysdocs.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;
import org.xBaseJ.fields.NumField;

import com.ed.pojo.DbfMetaData;
import com.ed.pojo.MediaMaximum;
import com.ed.sysdocs.MediaTypes;

/**
 * Data Access Object (DAO) to manage the media maximum use data.<br>
 * The structure of the MediaMax.dbf file is as follows:
 * <pre> Fld   Name       Type   Width   Dec
 * 001   MEDIATYPE    C     015     
 * 002   MAXUSE       N     003     0
 * ** Total **            00018
 * 
 * Index
 * 1. mediamax.ndx: MEDIATYPE, unique</pre>
 * @author Ed Swaneck
 * @version 1.0
 * @since 04/12/2024
 */
public class MediaMaxDbfDao extends DBFUtilities {
	
	/** Filename of the media maximum data file */
	public static final String dataFileName = "mediamax.dbf";
	
	/** Filename of the primary index */
	public static final String index1Name = "mediamax.ndx";
	
	// Internal properties
	private CharField mediaType;
	private NumField maxUse;
	private String dbfFileSpec;
	private String ndx1FileSpec;

	/**
	 * Constructor
	 * @param rootDir Root of the directory path where the data file is located
	 * @throws IOException If the table cannot be created
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	public MediaMaxDbfDao(String rootDir) throws SecurityException, xBaseJException, IOException {
		super(rootDir);
		
		try {
			mediaType = new CharField(MediaMaximum.Fields.MediaType.name(), MediaMaximum.Fields.MediaType.getFieldLen());
			maxUse = new NumField(MediaMaximum.Fields.MaxUse.name(), MediaMaximum.Fields.MaxUse.getFieldLen(), 0);
		}
		catch (xBaseJException | IOException e) {
			// This will never happen
		}
		
		dbfFileSpec = buildFileSpec(rootDir, dataFileName);
		ndx1FileSpec = buildFileSpec(rootDir, index1Name);
		
		File dbf = new File(dbfFileSpec);
		File ndx = new File(ndx1FileSpec);
		
		if (!dbf.exists()) {
			create();
		}
		
		if (!ndx.exists()) {
			index();
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#create()
	 */
	@Override
	public void create() throws SecurityException, xBaseJException, IOException {
		DBF mmd = new DBF(dbfFileSpec, false);
		
		mmd.addField(mediaType);
		mmd.addField(maxUse);
		mmd.close();
		index();
		
		for (MediaTypes mt : MediaTypes.values()) {
			MediaMaximum mm = new MediaMaximum();
			
			mm.setMediaType(mt);
			mm.setMaxUse(0);
			
			insert(mm);
			
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#index()
	 */
	@Override
	public void index() throws SecurityException, xBaseJException, IOException {
		
		createIndex(dbfFileSpec, ndx1FileSpec, MediaMaximum.Fields.MediaType.name(), true);

	}
	
	/**
	 * Create a new record
	 * @param mm The data to save
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public void insert(MediaMaximum mm) throws SecurityException, xBaseJException, IOException {
		DBF mmd = new DBF(dbfFileSpec);
		
		mmd.useIndex(ndx1FileSpec);

		mmd.getField(mediaType.Name).put(mm.getMediaType().name());
		mmd.getField(maxUse.Name).put(String.valueOf(mm.getMaxUse()));
		
		mmd.write();
		mmd.close();
		
	}
	
	/**
	 * Update an existing record
	 * @param prop The data to save
	 * @return true if the record was updated, otherwise false
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public boolean update(MediaMaximum prop) throws SecurityException, xBaseJException, IOException {
		DBF mm = new DBF(dbfFileSpec);
		boolean done = false;
		
		mm.useIndex(ndx1FileSpec);
		
		if (mm.findExact(prop.getMediaType().name())) {
			mm.getField(maxUse.Name).put(String.valueOf(prop.getMaxUse()));
			mm.update();
			done = true;
		}

		mm.close();
		
		return done;
		
	}
	
	/**
	 * Delete a media maximum record
	 * @param mediaName Name of the media type to delete
	 * @return true if the delete was successful, otherwise false
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public boolean delete(String mediaName) throws xBaseJException, IOException {
		DBF mm = new DBF(dbfFileSpec);
		boolean done = false;
		
		mm.useIndex(ndx1FileSpec);
		
		if (mm.findExact(mediaName)) {
			mm.delete();
			done = mm.deleted();
		}

		mm.close();
		
		return done;
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#pack()
	 */
	@Override
	public void pack() throws xBaseJException, IOException, SecurityException, CloneNotSupportedException {
		
		packDbf(dbfFileSpec, ndx1FileSpec);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#loadMetaData()
	 */
	@Override
	public DbfMetaData loadMetaData() throws xBaseJException, IOException, SecurityException {
		
		return getMetaData(dbfFileSpec, ndx1FileSpec);
		
	}
	
	/**
	 * Generate a list of all backup media types and their maximums
	 * @return A populated list of all backup media
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	public List<MediaMaximum> listAll() throws SecurityException, xBaseJException, IOException {
		DBF mm = new DBF(dbfFileSpec);
		List<MediaMaximum> pl = new ArrayList<>();
		List<MediaMaximum> add = new ArrayList<>();
		
		mm.useIndex(ndx1FileSpec);
		mm.startTop();
		
		for (MediaTypes mt : MediaTypes.values()) {
			if (mm.findExact(mt.name())) {
				pl.add(parseData(mm));
			}
			else {
				MediaMaximum sdp = new MediaMaximum();
				
				sdp.setMediaType(mt);
				sdp.setMaxUse(0);
				add.add(sdp);
				
			}
			
		}

		// Add any new media types to the list and the DBF
		for (MediaMaximum sdp : add) {
			pl.add(sdp);
			insert(sdp);
		}
		
		mm.close();
		
		return pl;
		
	}
	
	
	/**
	 * Generate a map of all backup media types and their maximums
	 * @return A populated map of all backup media
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	public Map<MediaTypes, Integer> mapAll() throws SecurityException, xBaseJException, IOException {
		DBF mm = new DBF(dbfFileSpec);
		Map<MediaTypes, Integer> pm = new HashMap<>();
		List<MediaMaximum> add = new ArrayList<>();
		
		mm.useIndex(ndx1FileSpec);
		mm.startTop();
		
		for (MediaTypes mt : MediaTypes.values()) {
			MediaMaximum p;
			
			if (mm.findExact(mt.name())) {
				p = parseData(mm);
				pm.put(p.getMediaType(), Integer.valueOf(p.getMaxUse()));
			}
			else {
				p = new MediaMaximum();
				p.setMediaType(mt);
				p.setMaxUse(0);
				add.add(p);
			}
			
		}

		// Add any new media types to the map and the DBF
		for (MediaMaximum sdp : add) {
			pm.put(sdp.getMediaType(), sdp.getMaxUse());
			insert(sdp);
		}
		
		mm.close();
		
		return pm;
		
	}
	
	/**
	 * Load a media maximum record
	 * @param pn Name of the record to load
	 * @return The data or null if the media name does not exist
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	public MediaMaximum read(String pn) throws SecurityException, xBaseJException, IOException {
		DBF mm = new DBF(dbfFileSpec);
		MediaMaximum prop = null;
		
		mm.useIndex(ndx1FileSpec);
		
		if (mm.findExact(pn)) {
			prop = parseData(mm);
		}
		
		mm.close();
		
		return prop;
		
	}
	
	/**
	 * Load data from the media maximums table into an object  
	 * @param mmd the table
	 * @return a media maximum object
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws ArrayIndexOutOfBoundsException If the field position is invalid  
	 */
	private MediaMaximum parseData(DBF mmd) throws ArrayIndexOutOfBoundsException, xBaseJException { 
		MediaMaximum mm = new MediaMaximum();
		
		mm.setMediaType(MediaTypes.valueOf(mmd.getField(mediaType.Name).get()));
		mm.setMaxUse(Integer.valueOf(mmd.getField(maxUse.Name).get().trim()));

		return mm;
		
	}
	
}
