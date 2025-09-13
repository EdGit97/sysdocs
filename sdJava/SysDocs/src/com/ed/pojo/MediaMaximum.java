package com.ed.pojo;

import java.util.ArrayList;
import java.util.List;

import com.ed.sysdocs.ErrMsgs;
import com.ed.sysdocs.MediaTypes;
import com.ed.sysdocs.TableHtml;

/**
 * A media maximum usage record
 * @author Ed Swaneck
 * @version 1.0
 * @since 04/12/2024
 */
public class MediaMaximum extends SDPojo {

	/**
	 * List of fields for a media maximum record
	 */
	public enum Fields {
		
		/** Name of the media type field */
		MediaType(15),

		/** Name of the maximum use field */
		MaxUse(3);
		
		private int fieldLen;
		
		/**
		 * Constructor
		 * @param fieldLen Length of the field in the DBF file
		 */
		private Fields(int fieldLen) {
			this.fieldLen = fieldLen;
		}
		
		/**
		 * @return The size of the associated field
		 */
		public int getFieldLen() {
			return fieldLen;
		}
		
	}
	
	/** Maximum value of the maxUse field */
	public static final long maxMaxUse = Math.round(Math.pow(10, Fields.MaxUse.getFieldLen()) - 1); 

	/** Media ID for this media type */
	protected MediaTypes mediaType = null;

	/** Maximum number of times this media type should be used */
	protected int maxUse = 0;

	/**
	 * Validate the record
	 * @return a list of error messages
	 */
	public List<String> validate() {
		List<String> errLst = new ArrayList<>();

		if (maxUse < 1 || maxUse > maxMaxUse) {
			errLst.add(ErrMsgs.MEDIA_MAXUSAGE.getMsg(mediaType.getDisplayName(), String.valueOf(maxMaxUse)));
		}
		
		return errLst;
		
	}

	/**
	 * Format the data for display
	 * @return the data formatted as the row of an HTML table 
	 */
	public String makeMediaMaxRow() {
		StringBuilder row = new StringBuilder(TableHtml.LabelStart.getTag());
		
		row.append(mediaType.getDisplayName());
		row.append(": ");
		row.append(TableHtml.LabelEnd.getTag());
			
		row.append(generateHidden(Fields.MediaType.name(), mediaType.name()));
		row.append(generateTextBox(Fields.MaxUse.name(), String.valueOf(maxUse), 2, 3));
				
		return row.toString();
		
	}
	
	/**
	 * @return A media type value
	 */
	public MediaTypes getMediaType() {
		return mediaType;
	}

	/**
	 * @param mediaType A media type value 
	 */
	public void setMediaType(MediaTypes mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * @return The maximum number of times this media type should be used
	 */
	public int getMaxUse() {
		return maxUse;
	}

	/**
	 * @param maxUse The maximum number of times this media type should be used
	 */
	public void setMaxUse(int maxUse) {
		this.maxUse = maxUse;
	}
	
}
