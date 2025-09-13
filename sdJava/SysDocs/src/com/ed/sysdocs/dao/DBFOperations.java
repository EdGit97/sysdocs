package com.ed.sysdocs.dao;

/**
 * Operations that can be performed on a DBF file
 * @author Ed Swaneck
 * @version 1.0
 * @since 04/12/2024
 */
public enum DBFOperations {
	
	/** This media record was not modified */
	Unchanged,
	
	/** This is a new media record  */
	Insert,
	
	/** This media record was modified */
	Modified;

	/**
	 * @return The associated operation value
	 */
	public String getOperation() {
		return name().substring(0, 1);
	}
	
	/**
	 * Convert a string into a DBFOperation
	 * @param value The value to test
	 * @return A DBFOperations value.  If value is an invalid value, return Unchanged.
	 */
	public static DBFOperations getValueOf(String value) {
		DBFOperations result = null;
		
		for (int i = 0; i < values().length && result == null; i++) {
			if (values()[i].getOperation().equalsIgnoreCase(value)) {
				result = values()[i];
			}
		}
		
		if (result == null) {
			result = Unchanged;
		}
		
		return result;
		
	}

}
