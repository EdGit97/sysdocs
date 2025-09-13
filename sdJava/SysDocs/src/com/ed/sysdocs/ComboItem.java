package com.ed.sysdocs;

/**
 * A label/value pair for a combo dropdown list
 * @author Ed Swaneck
 * @version 1.0
 * @since 05/12/2025
 */
public class ComboItem {
	
	/** String that will display in the dropdown */
	protected String label = "";
	
	/** The actual value */
	protected String value = "";
	
	/**
	 * Constructor
	 * @param label String that will display in the dropdown
	 * @param value The actual value
	 */
	public ComboItem(String label, String value) {
		
		super();
		this.label = label;
		this.value = value;
		
	}

	/**
	 * Getter
	 * @return String that will display in the dropdown
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Getter
	 * @return The actual value
	 */
	public String getValue() {
		return value;
	}

}
