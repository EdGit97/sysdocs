package com.ed.sysdocs;

import java.util.ArrayList;
import java.util.List;

/**
 * List of available backup media types
 * @author Ed Swaneck
 * @version 1.0
 * @since 04/13/2024
 */
public enum MediaTypes {
	
	/** If the media is a tape */
	tape("Tape"),
	
	/** If the media is an external hard drive */
	externalHD("External HD"),
	
	/** If the media is a read/write CD */
	CDRW("Read/Write CD"),
	
	/** If the media is a flash drive */
	flash("Flash Drive");

	/** Separator for a media type listing */
	public static final char typeSep = ':'; 
	
	private String displayName;
	
	/**
	 * Constructor
	 * @param displayName Name that will be used for displaying the media type
	 */
	private MediaTypes(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * @return The displayable name associated with this media type
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Create a list of the media types
	 * @return The media types listed as enum:displayname|...
	 */
	public static String listMediaTypes() {
		StringBuilder ml = new StringBuilder();
		
		for (MediaTypes mt : MediaTypes.values()) {
			if (ml.length() > 0) {
				ml.append(SysConstants.recSep);
			}
			
			ml.append(mt.name());
			ml.append(typeSep);
			ml.append(mt.getDisplayName());
			
		}
		
		return ml.toString();
		
	}
	
	/**
	 * Create a list of the media types
	 * @return The media types listed as enum|...
	 */
	public static String list() {
		StringBuilder ml = new StringBuilder();
		
		for (MediaTypes mt : MediaTypes.values()) {
			if (ml.length() > 0) {
				ml.append(SysConstants.recSep);
			}
			
			ml.append(mt.name());
			
		}
		
		return ml.toString();
		
	}
	
	/**
	 * Generate a list of the media types for a combo box dropdown 
	 * @return The media type organized as a combo box dropdown
	 */
	public static List<ComboItem> generateCombo() {
		List<ComboItem> dd = new ArrayList<>();
		
		for (MediaTypes mt : values()) {
			dd.add(new ComboItem(mt.getDisplayName(), mt.name()));
		}
		
		return dd;
		
	}
	
}
