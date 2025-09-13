package com.ed.sysdocs.dao;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.CharField;
import org.xBaseJ.fields.NumField;

import com.ed.pojo.DbfMetaData;
import com.ed.pojo.Property;
import com.ed.sysdocs.SysConstants;

/**
 * Data Access Object (DAO) to manage the properties data.<br>
 * The structure of the properties.dbf file is as follows:
 * <pre> Fld   Name       Type   Width   Dec
 * 001   GROUP        C     015
 * 002   PROPERTY     C     020
 * 002   POSITION     N     001     0
 * 003   VALUE        C     100
 * ** Total **            00136
 *
 * Index
 * 1. properties.ndx: GROUP+PROPERTY+POSITION, unique</pre>
 * @author Ed Swaneck
 * @version 1.1
 * @since 04/12/2024
 */
public class PropertiesDbfDao extends DBFUtilities {

	/** Filename of the media maximum data file */
	public static final String dataFileName = "properties.dbf";
	
	/** Filename of the primary index */
	public static final String index1Name = "properties.ndx";
	
	// Internal properties
	private CharField group;
	private CharField property;
	private NumField position;
	private CharField value;
	private String dbfFileSpec;
	private String ndx1FileSpec;

	/**
	 * Constructor
	 * @param rootDir Root of the directory path where the data file is located
	 * @throws IOException If the table cannot be created
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	public PropertiesDbfDao(String rootDir) throws SecurityException, xBaseJException, IOException {
		super(rootDir);
		
		try {
			group = new CharField(Property.Fields.Group.name(), Property.Fields.Group.getFieldLen());
			property = new CharField(Property.Fields.Property.name(), Property.Fields.Property.getFieldLen());
			position = new NumField(Property.Fields.Position.name(), Property.Fields.Position.getFieldLen(), 0);
			value = new CharField(Property.Fields.Value.name(), Property.Fields.Value.getFieldLen());
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

	/* (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#create()
	 */
	@Override
	public void create() throws SecurityException, xBaseJException, IOException {
		DBF prp = new DBF(dbfFileSpec, false);
		
		prp.addField(group);
		prp.addField(property);
		prp.addField(position);
		prp.addField(value);
		prp.close();
		index();
		
		for (Property.Properties p : Property.Properties.values()) {
			Property prop = new Property(p);
			
			insert(prop);
			
		}

	}

	/* (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#index()
	 */
	@Override
	public void index() throws SecurityException, xBaseJException, IOException {
		String ndx1Key = buildKey(Property.Fields.Group.name(), 
				                  Property.Fields.Property.name(), 
				                  Property.Fields.Position.name());
		
		createIndex(dbfFileSpec, ndx1FileSpec, ndx1Key, true);

	}

	/* (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#pack()
	 */
	@Override
	public void pack() throws xBaseJException, IOException, SecurityException, CloneNotSupportedException {
		packDbf(dbfFileSpec, ndx1FileSpec);
	}

	/* (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#loadMetaData()
	 */
	@Override
	public DbfMetaData loadMetaData() throws xBaseJException, IOException, SecurityException {
		return getMetaData(dbfFileSpec, ndx1FileSpec);
	}

	/**
	 * Create a new record
	 * @param prop The data to save
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public void insert(Property prop) throws SecurityException, xBaseJException, IOException {
		DBF prp = new DBF(dbfFileSpec);
		
		prp.useIndex(ndx1FileSpec);

		prp.getField(group.Name).put(prop.getKey().getGroup().name());
		prp.getField(property.Name).put(prop.getKey().name());
		prp.getField(position.Name).put(String.valueOf(prop.getPosition()));
		prp.getField(value.Name).put(encodePropValue(prop));
		
		prp.write();
		prp.close();
		
	}

	/**
	 * Update an existing record
	 * @param prop The data to save
	 * @return true if the record was updated, otherwise false
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public boolean update(Property prop) throws SecurityException, xBaseJException, IOException {
		DBF prp = new DBF(dbfFileSpec);
		boolean done = false;
		
		prp.useIndex(ndx1FileSpec);
		
		if (prp.findExact(generateRecordKey(prop.getKey(), prop.getPosition()))) {
			prp.getField(position.Name).put(String.valueOf(prop.getPosition()));
			prp.getField(value.Name).put(encodePropValue(prop));
			prp.update();
			prp.undelete();
			done = true;
		}

		prp.close();
		
		return done;
		
	}
	
	/**
	 * Delete a property record
	 * @param prop The record to delete
	 * @return true if the delete was successful, otherwise false
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public boolean delete(Property prop) throws xBaseJException, IOException {
		DBF prp = new DBF(dbfFileSpec);
		boolean done = false;
		
		prp.useIndex(ndx1FileSpec);
		
		if (prp.findExact(generateRecordKey(prop.getKey(), prop.getPosition()))) {
			prp.delete();
			done = prp.deleted();
		}

		prp.close();
		
		return done;
		
	}
	
	/**
	 * Load a property from the properties table
	 * @param key Load this property
	 * @return A populated Property record or null if the key is not found
	 * @throws ArrayIndexOutOfBoundsException If the field position is invalid 
	 * @throws xBaseJException  If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public Property read(Property.Properties key) throws ArrayIndexOutOfBoundsException, xBaseJException, IOException {
		
		return read(key, 0);
		
	}
	
	/**
	 * Load a property from the properties table
	 * @param key Load this property
	 * @param position The position of this value in a multi-value property 
	 * @return A populated Property record or null if the key is not found
	 * @throws ArrayIndexOutOfBoundsException If the field position is invalid 
	 * @throws xBaseJException  If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public Property read(Property.Properties key, int position) 
		   throws ArrayIndexOutOfBoundsException, 
		          xBaseJException, 
		          IOException {
		DBF prp = new DBF(dbfFileSpec);
		Property p = null;
		
		prp.useIndex(ndx1FileSpec);
		
		if (prp.findExact(generateRecordKey(key, position))) {
			p = parseData(prp);
		}
		
		prp.close();
		
		return p;
		
	}
	
	/**
	 * Generate a map of all properties
	 * @return A populated map of all properties
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	public Map<Property.Properties, Property> loadMap() throws SecurityException, xBaseJException, IOException {
		DBF props = new DBF(dbfFileSpec);
		Map<Property.Properties, Property> pm = new HashMap<>();
		
		props.useIndex(ndx1FileSpec);
		props.startTop();
		
		for (int i = 1; i <= props.getRecordCount(); i++) {
			props.findNext();
			
			if (!props.deleted()) {
				Property p = parseData(props);
				
				if (pm.get(p.getKey()) == null) {
					pm.put(p.getKey(), p);
					
				}
				else {
					Property p0 = pm.get(p.getKey());
					StringBuilder compoundVal = new StringBuilder(p0.getValue()); 
					
					compoundVal.append(SysConstants.newline);
					compoundVal.append(p.getValue());
					p0.setValue(compoundVal.toString());
					
				}
				
			}
			
		}
		
		props.close();
		
		return pm;
		
	}
	
	/**
	 * Generate the search key for a record
	 * @param key Generate the index key for this record
	 * @param position Add the position value to the key
	 * @return The generated key
	 */
	private String generateRecordKey(Property.Properties key, int position) {
		StringBuilder sk = new StringBuilder(key.getGroup().name());
		
		sk.append(key.name());
		sk.append(position);
		
		return sk.toString();
		
	}

	/**
	 * Load data from the properties table into a property record  
	 * @param props the table
	 * @return a property record
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws ArrayIndexOutOfBoundsException If the field position is invalid  
	 */
	private Property parseData(DBF props) throws ArrayIndexOutOfBoundsException, xBaseJException { 
		String val = props.getField(value.Name).get();
		Property.Properties prp;
		Property prop;
		
		try {
			prp = Property.Properties.valueOf(props.getField(property.Name).get());
			
			if (prp.isEncoded()) {
				val = new String(Base64.getDecoder().decode(val.getBytes()));
			}
			
			prop = new Property(prp, val);
		}
		catch (IllegalArgumentException e) {
			prop = null;
		}

		return prop;
		
	}
	
	/**
	 * Encode a property value if it is supposed to be encoded
	 * @param prop Encode the value of this property is required
	 * @return
	 */
	private String encodePropValue(Property prop) {
		String ev = prop.getValue();
		
		if (prop.getKey().isEncoded()) {
			ev = Base64.getEncoder().encodeToString(ev.getBytes());
		}
		
		return ev;

	}
	
}
