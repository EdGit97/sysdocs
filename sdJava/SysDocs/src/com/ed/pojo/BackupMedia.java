package com.ed.pojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ed.sysdocs.ErrMsgs;
import com.ed.sysdocs.SysConstants;
import com.ed.sysdocs.MediaTypes;
import com.ed.sysdocs.TableHtml;
import com.ed.sysdocs.dao.DBFOperations;

/**
 * A backup media record
 * @author Ed Swaneck
 * @version 1.0
 * @since 12/28/2023
 */
public class BackupMedia extends SDPojo{
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat(SysConstants.timestampFmt);
	
	/**
	 * List of fields for a media record
	 */
	public enum Fields {
		
		/** Name of the media ID field */
		MediaId(1),

		/** Name of the first use field */
		FirstUse(14),

		/** Name of the last use field */
		LastUse(14),

		/** Name of the use count field */
		UseCount(4),

		/** Name of the active flag field */
		Active(4),

		/** Name of the media type field */
		MediaType(15),

		/** Name of the delete field */
		Updated(1),

		/** Name of the delete field */
		Delete(1);
		
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
	
	/** Maximum value of the useCount field */
	public static final long maxUseCount = Math.round(Math.pow(10, Fields.UseCount.getFieldLen()) - 1); 

	/** Media ID for this media item */
	protected char mediaId = ' ';

	/** First Use date for this media item */
	protected Date firstUse = null;

	/** Last use date for this media item */
	protected Date lastUse = null;

	/** Number of times this media item has been used */
	protected int useCount = 0;

	/** Active flag for this media item */
	protected boolean active = false;

	/** This media item is this type of media */
	protected MediaTypes mediaType = null;

	/** Updated flag for this media item */
	protected DBFOperations updated = DBFOperations.Unchanged;

	/** Delete flag for this media item */
	protected boolean delete = false;

	/**
	 * Validate the record
	 * @return a list of error messages
	 */
	public List<String> validate() {
		List<String> errLst = new ArrayList<>();
		Character id = Character.toUpperCase(mediaId);
		
		if (id < 'A' || id > 'Z') {
			errLst.add(ErrMsgs.BAD_MEDIA.getMsg());
		}
		
		return errLst;
		
	}
	
	/**
	 * Format the data for storage
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append(String.valueOf(mediaId).trim());
		s.append(SysConstants.recSep);
		
		if (firstUse != null) {
			s.append(sdf.format(firstUse));
		}
		
		s.append(SysConstants.recSep);
		
		if (lastUse != null) {
			s.append(sdf.format(lastUse));
		}
		
		s.append(SysConstants.recSep);
		s.append(useCount);
		s.append(SysConstants.recSep);
		s.append(active);
		s.append(SysConstants.recSep);
		s.append(mediaType.name());
		
		return s.toString();
		
	}
	
	/**
	 * Format the data for display
	 * @param odd true if this is an odd row, otherwise false
	 * @param maxUsage maximum number of times a backup media should be used 
	 * @return the data formatted as the row of an HTML table 
	 */
	public String makeMediaRow(boolean odd, int maxUsage) {
		SimpleDateFormat sdffd = new SimpleDateFormat(SysConstants.timestampDisplay);
		StringBuilder row = new StringBuilder();
		boolean maxExceeded = useCount >= maxUsage; 
		
		if (odd && maxExceeded) {
			row.append(TableHtml.RowStartOddRed.getTag());
		}
		else if (maxExceeded) {
			row.append(TableHtml.RowStartRed.getTag());
		}
		else if (odd) {
			row.append(TableHtml.RowStartOdd.getTag());
		}
		else {
			row.append(TableHtml.RowStart.getTag());
		}
		
		row.append(TableHtml.ColStartC.getTag());
		row.append(mediaId);
		
		if (maxExceeded) {
			row.append(TableHtml.SupStart.getTag());
			row.append(SysConstants.lifeExceededFootNote);
			row.append(TableHtml.SupEnd.getTag());
		}
			
		row.append(generateHidden(Fields.MediaId.name(), String.valueOf(mediaId)));
		row.append(TableHtml.ColEnd.getTag());
		row.append(TableHtml.ColStartC.getTag());
		row.append(generateHidden(Fields.MediaType.name(), mediaType.name()));
		row.append(mediaType.getDisplayName());
		row.append(TableHtml.ColEnd.getTag());
		row.append(TableHtml.ColStartC.getTag());
		row.append(firstUse == null ? "New" : sdffd.format(firstUse));
		row.append(generateHidden(Fields.FirstUse.name(), firstUse == null ? "" : sdf.format(firstUse)));
		row.append(TableHtml.ColEnd.getTag());
		row.append(TableHtml.ColStartC.getTag());
		row.append(lastUse == null ? "New" : sdffd.format(lastUse));
		row.append(generateHidden(Fields.LastUse.name(), lastUse == null ? "" : sdf.format(lastUse)));
		row.append(TableHtml.ColEnd.getTag());
		row.append(TableHtml.ColStartR.getTag());
		row.append(useCount);
		row.append(generateHidden(Fields.UseCount.name(), String.valueOf(useCount)));
		row.append(TableHtml.ColEnd.getTag());
		row.append(TableHtml.ColStartC.getTag());
		row.append(generateCheckBox(Fields.Active.name(), mediaId, active, "mediaChanged(this);"));
		row.append(generateHidden(Fields.Updated.name(), updated.getOperation()));
		row.append(TableHtml.ColEnd.getTag());
		row.append(TableHtml.ColStartC.getTag());
		row.append(generateCheckBox(Fields.Delete.name(), mediaId, false, null));
		row.append(TableHtml.ColEnd.getTag());
		row.append(TableHtml.RowEnd.getTag());
		row.append(SysConstants.newline);
		
		return row.toString();
		
	}
	
	/**
	 * Increment the value of the usage counter
	 */
	public void incrementUseCount() {
		
		if (useCount < maxUseCount) {
			useCount++;
		}
		
	}

	/**
	 * @return Media ID for this media item
	 */
	public char getMediaId() {
		return mediaId;
	}

	/**
	 * @param mediaId Media ID for this media item
	 */
	public void setMediaId(char mediaId) {
		this.mediaId = mediaId;
	}

	/**
	 * @return Media ID for this media item as a string
	 */
	public String getMediaIdAsString() {
		return String.valueOf(mediaId);
	}

	/**
	 * @return Media ID for this media item as a character object
	 */
	public Character getMediaIdAsCharacter() {
		return Character.valueOf(mediaId);
	}

	/**
	 * @return First Use date for this media item
	 */
	public Date getFirstUse() {
		return firstUse;
	}

	/**
	 * @param firstUse First Use date for this media item
	 */
	public void setFirstUse(Date firstUse) {
		this.firstUse = firstUse;
	}

	/**
	 * @return Last use date for this media item
	 */
	public Date getLastUse() {
		return lastUse;
	}

	/**
	 * @param lastUse Last use date for this media item
	 */
	public void setLastUse(Date lastUse) {
		this.lastUse = lastUse;
	}

	/**
	 * @return Number of times this media item has been used
	 */
	public int getUseCount() {
		return useCount;
	}

	/**
	 * @param useCount Number of times this media item has been used
	 */
	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	/**
	 * @return Value of the active flag
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active Value of the active flag
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @param mediaType Media Type code
	 */
	public void setMediaType(MediaTypes mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * @return Media Type code
	 */
	public MediaTypes getMediaType() {
		return mediaType;
	}

	/**
	 * @return Value of the updated flag
	 */
	public DBFOperations getUpdated() {
		return updated;
	}

	/**
	 * @param updated Value for the updated flag
	 */
	public void setUpdated(DBFOperations updated) {
		this.updated = updated;
	}

	/**
	 * @return true if the the delete flag has been set, otherwise false 
	 */
	public boolean isDelete() {
		return delete;
	}

	/**
	 * @param delete true to mark the media for deletion, otherwise false
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}

}
