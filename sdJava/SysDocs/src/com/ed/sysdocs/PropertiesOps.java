package com.ed.sysdocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.xBaseJ.xBaseJException;

import com.ed.pojo.Property;
import com.ed.sysdocs.dao.PropertiesDbfDao;

/**
 * System properties operations
 * @author Ed Swaneck
 * @version 1.0
 * @since 07/24/2024
 */
public class PropertiesOps {
	
	/**
	 * Sort the list of properties by group and name 
	 */
	private class SortProps implements Comparator<Property> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Property p1, Property p2) {
			
			return p1.getKeyName().compareTo(p2.getKeyName());
			
		}
	    
	}

	/** Directory of the site */
	protected String rootDir;
	
	/** Map of all defined properties */
	protected Map<Property.Properties, Property> props;
	
	/** List of error messages */
	protected List<String> errors = new ArrayList<>();

	/**
	 * Constructor
	 * @param rootDir Root of the directory path where the data file is located
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	public PropertiesOps(String rootDir) throws SecurityException, xBaseJException, IOException {
		
		super();
		
		this.rootDir = rootDir;
		load();
		
	}
	
	/**
	 * Load all properties
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	public void load() throws SecurityException, xBaseJException, IOException {
		PropertiesDbfDao dbf = new PropertiesDbfDao(rootDir);
		
		props = dbf.loadMap();
		
		// Add any defined properties that are not in the table
		for (Property.Properties p : Property.Properties.values()) {
			if (props.get(p) == null) {
				add(p, p.getDefaultValue());
			}
		}
		
	}
	
	/**
	 * Validate all of the properties
	 */
	public void validate() {
		
		for (Property.Properties pp : props.keySet()) {
			Property p = props.get(pp);
			
			errors.addAll(p.validate());
		}
		
		
	}

	/**
	 * Save all properties to the properties table
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 * @throws CloneNotSupportedException Java error caused by called methods 
	 */
	public void save() throws SecurityException, xBaseJException, IOException, CloneNotSupportedException {
		PropertiesDbfDao dbf = new PropertiesDbfDao(rootDir);
		boolean deleteHappened = false;
		
		for (Property.Properties pp : props.keySet()) {
			Property p = props.get(pp);
			
			if (p.getKey().getGroup().isMultiValue()) {
				String [] values = p.getMultiValue();
				int i;
				
				for (i = 0; i < values.length; i++) {
					p.setPosition(i);
					p.setValue(values[i]);
					deleteHappened |= processProperty(dbf, p);
				}
				
				// Delete all positions that do not have a value
				for (; i < SysConstants.maxMultiValues; i++) {
					p.setPosition(i);
					
					if (dbf.delete(p)) {
						deleteHappened = true;
					}
					
				}
				
			}
			else {
				deleteHappened |= processProperty(dbf, p);
			}
			
		}
		
		if (deleteHappened) {
			dbf.pack();
		}
		
	}
	
	/**
	 * Write a property to the DB
	 * @param dbf DAO to manage the DB operations
	 * @param p The data to process
	 * @return true if a delete happened, otherwise false
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be created
	 */
	private boolean processProperty(PropertiesDbfDao dbf, Property p) 
			throws SecurityException, 
			       xBaseJException, 
			       IOException {
		Property p2 = dbf.read(p.getKey(), p.getPosition());
		boolean deleteHappened = false;
		
		if (p2 == null) {
			dbf.insert(p);
		}
		else if (p.getValue() == null) {
			dbf.delete(p);
			deleteHappened = true;
		}
		else if (!p.getValue().equals(p2.getValue())) {
			dbf.update(p);
		}
		
		return deleteHappened;

	}
	
	/**
	 * Format the errors for display
	 * @return The errors formatted for display
	 */
	public String formatErrors() {
		StringBuilder errs = new StringBuilder();
		
		for (String error : errors) {
			errs.append(TableHtml.ListItemStart.getTag());
			errs.append(error);
			errs.append(TableHtml.ListItemEnd.getTag());
		}
		
		return errs.toString();
		
	}
	
	/**
	 * Get a property from the properties list
	 * @param key Key of the property to retrieve
	 * @return The property that matches the key or the property with the default value
	 */
	public Property get(Property.Properties key) {
		Property p = props.get(key);
		
		if (p == null) {
			p = new Property(key, key.getDefaultValue());
		}
		
		return p;
		
	}
	
	/**
	 * Set the value of a property
	 * @param key Set the value of this property
	 * @param value New value of the property
	 */
	public void set(Property.Properties key, String value) {
		Property p = props.get(key);
		
		if (p != null) {
			p.setValue(value);
		}
		
	}
	
	/**
	 * Add a new property to the property list
	 * @param key Create a property with this key
	 * @param value Assign this value to the new property
	 */
	public void add(Property.Properties key, String value) {
		Property p = props.get(key);
		
		if (p == null) {
			p = new Property(key, value);
			props.put(key, p);
		}
		else {
			p.setValue(value);
		}
		
	}
	
	/**
	 * Mark a property for deletion
	 * @param key Mark his property for deletion
	 */
	public void delete(Property.Properties key) {
		Property p = props.get(key);
		
		if (p != null) {
			p.setValue(null);
		}
		
	}
	
	/**
	 * List the properties
	 * @return A list of the non-deleted properties
	 */
	public List<Property> list() {
		List<Property> pl = new ArrayList<>();
		
		for (Property.Properties pp : props.keySet()) {
			Property p = props.get(pp);
			
			if (p.getValue() != null) {
				pl.add(props.get(pp));
			}
			
		}

		Collections.sort(pl, new SortProps());

		return pl;
		
	}
	
	/**
	 * Determine if there are any errors
	 * @return true if the error list is not empty, otherwise false
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

}
