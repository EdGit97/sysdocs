package com.ed.pojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ed.sysdocs.ComboItem;
import com.ed.sysdocs.ErrMsgs;
import com.ed.sysdocs.MediaTypes;
import com.ed.sysdocs.SysConstants;
import com.ed.sysdocs.TableHtml;

/**
 * A property record
 * @author Ed Swaneck
 * @version 1.0
 * @since 07/24/2024
 */
public class Property extends SDPojo {
	
	/** Group/property separator */
	public static final String groupSep = ".";
	
	/**
	 * List of fields for a media record
	 */
	public enum Fields {
		
		/** Name of the property group field */
		Group(15),

		/** Name of the property field */
		Property(20),
		
		/** Position of the record in a multi-value property */
		Position(1),
		
		/** Description of the property */
		Description(50),
		
		/** Type of data associated with this property (Character or Numeric) */
		Type(1),

		/** Name of the value field */
		Value(100);
		
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
	
	/**
	 * Property data types 
	 */
	public enum DataType {
		
		/** Data consists of a string of characters */
		Character,
		
		/** Data consists only of digits */
		Numeric;
		
	}
	
	/**
	 * Value group names 
	 */
	public enum PropertyGroup {
		
		/** Group name of the log section */
		log("Log", false),
		
		/** Group name of the tool bars section */
		toolbars("Toolbars", true),
		
		/** Group name of the pinned section */
		pinned("Pinned Apps", true),
		
		/** Group name of the media update section */
		mediaUpdate("Media Update", false),
		
		/** Group name of the SMTP section */
		smtp("SMTP", false);
		
		// Internal property values
		private String displayName;
		private boolean multiValue;
		
		/**
		 * Constructor
		 * @param displayName Value used for UI
		 * @param multiValue true if there can be multiple values for this property 
		 */
		private PropertyGroup(String displayName, boolean multiValue) {
			this.displayName = displayName;
			this.multiValue = multiValue;
		}
		
		/**
		 * Getter
		 * @return The display name associated with the the property group 
		 */
		public String getDisplayName() {
			return displayName;
		}

		/**
		 * Getter
		 * @return true if there can be multiple values for this property 
		 */
		public boolean isMultiValue() {
			return multiValue;
		}
		
	}
	
	/**
	 * Valid group/key pairs 
	 */
	public enum Properties {
		
		/** Prefix for local full backup log */
		localPrefix(PropertyGroup.log, DataType.Character, "Local log file name prefix", "", 20, 20, false, null),
		
		/** Task Scheduler folder that contains the daily backup tasks */
		schedFolder(PropertyGroup.log, DataType.Character, "Task Scheduler folder", "\\", 20, 20, false, null),
		
		/** Tool bar directory */
		tbdir(PropertyGroup.toolbars, DataType.Character, "Directories", "", 70, 100, false, null),
		
		/** Directory of pinned start menu icons */
		pin1(PropertyGroup.pinned, DataType.Character, "Directories", "", 70, 100, false, null),
		
		/** Default media type */
		mediaType(PropertyGroup.mediaUpdate, DataType.Character, "Default media type", MediaTypes.flash.getDisplayName(), 10, 10, false, MediaTypes.generateCombo()),
		
		/** Notification email */
		notifyEmail(PropertyGroup.mediaUpdate, DataType.Character, "Notification email address", "", 20, 50, false, null),
		
		/** Default backup media password */
		mediaPwd(PropertyGroup.mediaUpdate, DataType.Character, "Default media password", "", 20, 50, true, null),
		
		/** Volume label of the local backup media */
		volumeLbl(PropertyGroup.mediaUpdate, DataType.Character, "Local media volume label", "", 10, 12, false, null),
		
		/** URL of the email server */
		serverAddr(PropertyGroup.smtp, DataType.Character, "SMTP server URL", "", 20, 50, false, null),
		
		/** Email server port */
		serverPort(PropertyGroup.smtp, DataType.Numeric, "SMTP server port", "", 5, 5, false, null),
		
		/** Email server account */
		serverAcct(PropertyGroup.smtp, DataType.Character, "SMTP account", "", 20, 50, false, null),
		
		/** Email server password */
		serverPwd(PropertyGroup.smtp, DataType.Character, "SMTP account password", "", 20, 20, true, null);
		
		// Internal property values
		private PropertyGroup group;
		private String defaultValue;
		private DataType dataType;
		private String description;
		private int textBoxSize;
		private int propMaxLen;
		private boolean encoded;
		private List<ComboItem> dropDown;
		
		/**
		 * Constructor
		 * @param group The group that is associated with this property
		 * @param dataType Type of data stored in this property, Character or Numeric.
		 * @param description Label to use when displaying the property
		 * @param defaultValue The default value of this property
		 * @param textBoxSize Character width of the text box that will display this property
		 * @param propMaxLen Maximum data length of this property
		 * @param encoded true if this property is encoded in the DB, otherwise false
		 * @param dropDown A list of items for a combo box dropdown or null if no such list
		 */
		private Properties(PropertyGroup group, 
				           DataType dataType, 
				           String description, 
				           String defaultValue,
				           int textBoxSize,
				           int propMaxLen,
				           boolean encoded,
				           List<ComboItem> dropDown) {
			this.group = group;
			this.dataType = dataType;
			this.description = description;
			this.defaultValue = defaultValue;
			this.textBoxSize = textBoxSize;
			this.propMaxLen = propMaxLen;
			this.encoded = encoded;
			this.dropDown = dropDown;
			
		}
		
		/**
		 * Getter
		 * @return The group name of this property
		 */
		public PropertyGroup getGroup() {
			return group;
		}
		
		/**
		 * Getter
		 * @return The default value of this property
		 */
		public String getDefaultValue() {
			return defaultValue;
		}
		
		/**
		 * Getter
		 * @return The description of the property
		 */
		public String getDescription() {
			return description;
		}
		
		/**
		 * Getter
		 * @return The type of data this property holds
		 */
		public DataType getDataType() {
			return dataType;
		}

		/**
		 * Getter
		 * @return Character width of the text box that will display this property
		 */
		public int getTextBoxSize() {
			return textBoxSize;
		}

		/**
		 * Getter
		 * @return Maximum data length of this property
		 */
		public int getPropMaxLen() {
			return propMaxLen;
		}
		
		/**
		 * Getter
		 * @return true if the value is encoded in the DB, otherwise false
		 */
		public boolean isEncoded() {
			return encoded;
		}

		/**
		 * Getter
		 * @return A dropdown list for a combo box
		 */
		public List<ComboItem> getDropDown() {
			return dropDown;
		}
		
	}
	
	/** Key name for this property */
	protected Properties key = null;

	/** Position of the value for a multi-value property.  Zero for all other properties */
	protected int position = 0;

	/** Value associated with this property.  If the value is null, the property will be deleted on save. */
	protected String value;
	
	/** Properties that will hold a directory specification */
	private final Set<Properties> dirProps = Set.of(Properties.tbdir, Properties.pin1);

	/**
	 * Constructor
	 * @param key The key associated with this property
	 */
	public Property(Properties key) {
		this.key = key;
		this.value = key.getDefaultValue();
	}
	
	/**
	 * Constructor
	 * @param key The key associated with this property
	 * @param value The value associated with this key
	 */
	public Property(Properties key, String value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * Validate the value that is associated with this property
	 * @return A list of error messages
	 */
	public List<String> validate() {
		List<String> errs = new ArrayList<>();
		
		if (key.getDataType().equals(DataType.Numeric)) {
			
			try {
				Integer.valueOf(value);
			}
			catch (NumberFormatException e) {
				errs.add(ErrMsgs.PROP_NUMERIC.getMsg(key.getDescription()));
			}
			
		}
		else if (key.getGroup().isMultiValue()) {
			String [] lines = getMultiValue();
			
			for (String line : lines) {
				if (line.length() > key.getPropMaxLen()) {
					errs.add(ErrMsgs.VALUE_MAXLEN.getMsg(line, String.valueOf(key.getPropMaxLen())));
				}
			}
			
			if (dirProps.contains(key)) {
				errs.addAll(checkDirs(lines));
			}
			
			if (lines.length > SysConstants.maxMultiValues) {
				errs.add(ErrMsgs.VALUES_MAX.getMsg());
			}
			
		}
		
		return errs;
		
	}
	
	/**
	 * Make a table row for the property edit
	 * @param newGroup true if this row is a different group than the last group, otherwise false
	 * @param oddRow true if this row is an odd number, otherwise false
	 * @return A formatted table row
	 */
	public String makeEditRow(boolean newGroup, boolean oddRow) {
		StringBuilder row = new StringBuilder();
		String keyNm = getKeyName();
		
		if (oddRow) {
			row.append(TableHtml.RowStartOdd.getTag());
		}
		else {
			row.append(TableHtml.RowStart.getTag());
		}
		
		row.append(TableHtml.PropColStartL.getTag());
		row.append(TableHtml.HardSpace.getTag());
		
		if (newGroup) {
			row.append(key.getGroup().getDisplayName());
		}
		
		row.append(TableHtml.ColEnd.getTag());
		
		row.append(TableHtml.PropColStartR.getTag());
		row.append(generateHidden(Fields.Property.name(), keyNm));
		row.append(key.getDescription());
		row.append(" = ");
		row.append(TableHtml.ColEnd.getTag());
		row.append(TableHtml.PlainColStart.getTag());
		
		if (key.getDropDown() != null) {
			row.append(generateComboBox(Fields.Value.name(), value, key.getDropDown()));
		}
		else if (key.getGroup().isMultiValue()) {
			row.append(generateTextArea(Fields.Value.name(), value, key.getTextBoxSize()));
		}
		else if (key.isEncoded()) {
			row.append(generatePwdBox(Fields.Value.name(), value, key.getTextBoxSize(), key.getPropMaxLen()));
		}
		else {
			row.append(generateTextBox(Fields.Value.name(), value, key.getTextBoxSize(), key.getPropMaxLen()));
		}
		
		row.append(TableHtml.HardSpace.getTag());
		row.append(TableHtml.ColEnd.getTag());
		row.append(TableHtml.RowEnd.getTag());
				
		return row.toString();
		
	}
	
	/**
	 * Examine the list of directories and check that each one if valid and exists
	 * @param dirs The list of potential directory values to evaluate
	 * @return A list of error messages
	 */
	private List<String> checkDirs(String [] dirs) {
		List<String> errs = new ArrayList<>();
		
		for (String d : dirs) {
			File f = new File(d);
			
			if (!f.exists() || !f.isDirectory()) {
				errs.add(ErrMsgs.CLI_BAD_DIR.getMsg(d));
			}
			
		}
		
		return errs;
		
	}
	
	/**
	 * Getter
	 * @return The key value associated with this property
	 */
	public Properties getKey() {
		return key;
	}
	
	/**
	 * Getter
	 * @return The full key name associated with this property, Group.key.
	 */
	public String getKeyName() {
		return key.getGroup().name() + groupSep + key.name();
	}

	/**
	 * Getter
	 * @return Position of the value for a multi-value property.  Zero for all other properties
	 */
    public int getPosition() {
		return position;
	}

    /**
     * Setter
     * @param position Position of the value for a multi-value property.  Zero for all other properties
     */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
     * Getter	
     * @return The value associated with this property
     */
	public String getValue() {
		return value;
	}
	
	/**
	 * Getter
	 * @return The value associated with this property as an integer.  If the value cannot be converted return zero.
	 */
	public int getIntValue() {
		int iVal;
		
		try {
			iVal = Integer.valueOf(value).intValue();
		}
		catch (NumberFormatException e) {
			iVal = 0;
		}
		
		return iVal;
		
	}
	
	/**
	 * Return the value of a multi-value property as an array
	 * @return An array of values split by new line character
	 */
	public String [] getMultiValue() {
		String [] mv;
		
		if (value.trim().length() <= 0) {
			mv = new String[0];
		}
		else if (key.getGroup().isMultiValue()) {
			final String nl = String.valueOf(SysConstants.newline);
			
			mv = value.replaceAll("\\r\\n", nl).replaceAll("\\r", nl).split(nl);
			
		}
		else {
			mv = new String[1];
			mv[0] = value;
		}
		
		return mv;
		
	}

	/**
	 * Setter
	 * @param value The new value to associate with this property
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Getter
	 * @return true if the property is marked for deletion
	 */
	public boolean isDeleted() {
		return value == null;
	}

	/**
	 * Setter
	 * @param markForDeletion true to mark the property for deletion, false to undelete
	 */
	public void setDeleted(boolean markForDeletion) {
		
		if (markForDeletion) {
			value = null;
		}
		else if (value == null) {
			value = "";
		}
		
	}
	
}
