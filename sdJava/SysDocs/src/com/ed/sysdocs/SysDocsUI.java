package com.ed.sysdocs;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xBaseJ.xBaseJException;

import com.ed.pojo.BackupMedia;
import com.ed.pojo.DbfMetaData;
import com.ed.pojo.MediaMaximum;
import com.ed.pojo.Property;
import com.ed.pojo.Property.Properties;
import com.ed.pojo.ScheduledTask;
import com.ed.sysdocs.dao.MediaDbfDao;
import com.ed.sysdocs.dao.MediaMaxDbfDao;
import com.ed.sysdocs.dao.PropertiesDbfDao;
import com.ed.sysdocs.dao.DBFOperations;

/**
 * Operations to support the backups JSP page
 * @author Ed Swaneck
 * @version 1.2
 * @since 12/28/2023
 */
public class SysDocsUI {
	
	/** List of any error messages that were generated */
	protected List<String> errors = new ArrayList<>();

	/** The request object from the browser */
	protected HttpServletRequest request;

	/** The systems properties */
	protected PropertiesOps properties; 
	
	/** Media maximum usage table */
	protected Map<MediaTypes, Integer> mediaMaxs;
	
	/** Directory of the site */
	protected String rootDir;

    /**
     * Retrieve the server root directory of this page
	 * @return the absolute path, on the server, where this page resides
    */
	public String getRootDir() {
		return rootDir;
	}
	
	/**
	 * Retrieve the media maximums
	 * @return the media maximums
	 */
	public Map<MediaTypes, Integer> getMediaMaximums() {
		return mediaMaxs;
	}
	
	/**
	 * Constructor<br>
	 * Initialize property values 
	 * @param request the HTTP request that called this page
	 */
	public SysDocsUI(HttpServletRequest request) {
		super();
		
		this.request = request;
        rootDir = setRootDir();
        
		try {
			properties = new PropertiesOps(rootDir);
			saveProperties();
		    saveMediaMaximums();
		    saveMedia();
		} 
		catch (SecurityException | xBaseJException | IOException | CloneNotSupportedException e) {
			errors.add(e.getMessage());
		}

	}
	
	/**
	 * Display the Media Maximum Settings 
	 * @return The content to display
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public String editMediaMaximums() throws SecurityException, xBaseJException, IOException {
		MediaMaxDbfDao mm = new MediaMaxDbfDao(rootDir);
		List<MediaMaximum> mml = mm.listAll();
		StringBuilder content = new StringBuilder();
		
		if (mml != null) {
			for (MediaMaximum max : mml) {
				if (content.length() > 0) {
					content.append(TableHtml.NewLine.getTag());
					content.append(SysConstants.newline);
				}
				
				content.append(max.makeMediaMaxRow());
				
			}
			
		}
	
		return content.toString();
		
	}

	/**
	 * Generate the display of the defined backup media
	 * @return displayable list of backup media
	 * @throws IOException If the table cannot be read or updated
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	public String showMedia() throws SecurityException, xBaseJException, IOException {
		MediaDbfDao md = new MediaDbfDao(rootDir);
		List<BackupMedia> bm = md.listAll(false);
		StringBuilder content = new StringBuilder();
		boolean odd = true;
		
		if (bm != null) {
			for (BackupMedia media : bm) {
				content.append(media.makeMediaRow(odd, mediaMaxs.get(media.getMediaType())));
				odd = !odd;
			}
		}
	
		return content.toString();
		
	}
	
	/**
	 * Generate the maximum usage notes for the media chart footnote
	 * @return Text for the media chart footer
	 * @throws IOException If the table cannot be read or updated
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	public String showMediaMaximums() throws SecurityException, xBaseJException, IOException {
		StringBuilder maxNotes = new StringBuilder();
		MediaDbfDao md = new MediaDbfDao(rootDir);
		List<BackupMedia> bm = md.listAll(false);
		Map<MediaTypes, Integer> typeUsed = new HashMap<>();
		boolean exceeded = false;
		
		if (bm != null) {
			for (BackupMedia media : bm) {
				if (typeUsed.get(media.getMediaType()) == null) {
					typeUsed.put(media.getMediaType(), Integer.valueOf(0));
					
					if (maxNotes.length() > 0) {
						maxNotes.append(TableHtml.NewLine.getTag());
					}
					
					maxNotes.append("&nbsp;");
					maxNotes.append(media.getMediaType().getDisplayName());
					maxNotes.append(": Replace after the Usage Count reaches ");
					maxNotes.append(mediaMaxs.get(media.getMediaType()));
					maxNotes.append('.');
					
				}
				
				if (media.getUseCount() >= mediaMaxs.get(media.getMediaType())) {
					exceeded = true;
				}
				
			}
			
		}
		
		if (exceeded) {
			maxNotes.append(TableHtml.NewLine.getTag());
			maxNotes.append(TableHtml.SupStart.getTag());
			maxNotes.append(SysConstants.lifeExceededFootNote);
			maxNotes.append(TableHtml.SupEnd.getTag());
			maxNotes.append("Media which has exceeded its life expectancy.");
		}

		return maxNotes.toString();
		
	}
	
	/**
	 * Generate the display of the list of properties for editing
	 * @return displayable list of properties
	 * @throws IOException If the table cannot be read or updated
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	public String showProperties() throws SecurityException, xBaseJException, IOException {
		PropertiesOps pd = new PropertiesOps(rootDir);
		List<Property> pl = pd.list();
		StringBuilder rows = new StringBuilder();
		Property.PropertyGroup lastGroup = null;
		boolean newGroup = true;
		boolean oddRow = false;
		
		for (Property p : pl) {
			if (lastGroup == null || !lastGroup.equals(p.getKey().getGroup())) {
				newGroup = true;
				oddRow = !oddRow;
			}
			
			rows.append(p.makeEditRow(newGroup, oddRow));
			lastGroup = p.getKey().getGroup();
			newGroup = false;
			
		}
	
		return rows.toString();
		
	}
	
	/**
	 * Display the property error messages
	 * @return The property error messages formatted for display
	 */
	public String showPropertyErrors() {
		return properties.formatErrors();
	}
	
	/**
	 * Determine the next backup media to use
	 * @param type Get the next ID for this type of media
	 * @return ID of the media to use for the next backup
	 * @throws IOException If the table cannot be read or updated
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	public char nextMediaId(MediaTypes type) throws SecurityException, xBaseJException, IOException {
		BackupMedia m = getNextMedia(type);
		char nm = '*';
		
		if (m != null) {
		    nm = m.getMediaId();
		}
		
		return nm;
		
	}
	
	/**
	 * Build the Scheduled Tasks table
	 * @return The HTML for the Scheduled Tasks table
	 */
	public String buildScheduledTaskTbl() {
		StringBuilder tbl = new StringBuilder();
		Scheduler sched = new Scheduler(properties, rootDir);
		boolean odd = true; 
		
		sched.processSchedule();
		
		if (sched.getErrors().isEmpty()) {
			for (ScheduledTask t : sched.getTasks()) {
				tbl.append(t.makeTaskRow(odd));
				odd = !odd;
			}
			
		}
		else {
			tbl.append(sched.tableFormatErrors(4));
			
		}
		
		return tbl.toString();
		
	}
	
    /**
     * Determine the most recent log file from a local run
	 * @return A link to the latest log files or an error message if no log is found
     */
	public String getLatestLocalLog() {
		LogOps logOps = new LogOps(rootDir);
		File llog = logOps.getLatestLog(properties.get(Properties.localPrefix).getValue());
		return LogOps.buildLogLink(llog, "log");
		
	}
	
	/**
	 * Generate a notification of any media that have exceeded the maximum 
	 * allowable usage.
	 * @return A warning message to display.  The message will be empty if none of the media are over the maximum usage.
	 * @throws IOException If the table cannot be read or updated
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	public String usageNotification() throws SecurityException, xBaseJException, IOException {
		MediaDbfDao md = new MediaDbfDao(rootDir);
		List<BackupMedia> bm = md.listAll(true);
		Map<MediaTypes, Integer> over = new HashMap<>();
	    StringBuilder warn = new StringBuilder();
	    
	    // Analyze all active media and determine how many of each type are 
	    // over the maximum usage value. 
	    for (BackupMedia m : bm) {
	    	int max = mediaMaxs.get(m.getMediaType());
	    	
	    	if (m.getUseCount() >= max) {
	    		Integer typeOver = over.get(m.getMediaType());
	    		
	    		if (typeOver == null) {
	    			typeOver = Integer.valueOf(1);
	    		}
	    		else {
	    			over.remove(m.getMediaType());
	    			typeOver = Integer.valueOf(typeOver.intValue() + 1);
	    		}
	    		
    			over.put(m.getMediaType(), typeOver);
    			
	    	}
	    	
	    }
	    
	    // Based on the number of media that over the limit, generate 
	    // a message.
	    for (MediaTypes t : over.keySet()) {
	    	if (warn.length() > 0) {
	    		warn.append(SysConstants.newline);
	    	}
	    	
	    	warn.append(over.get(t).intValue());
	    	warn.append(' ');
	    	warn.append(t.getDisplayName());
	    	
	    	if (over.get(t).intValue() > 1) {
		    	warn.append("s have");
	    	}
	    	else {
		    	warn.append(" has");
	    	}
	    	
	    	warn.append(" exceeded the maximum usage and should be replaced.");
	    	
	    }
	    
	    return warn.toString();
	    
	}

	/**
	 * Display any error messages
	 * @return the error message formatted as an HTML list
	 */
	public String showErrors() {
		StringBuilder errLst = new StringBuilder(TableHtml.ListStart.getTag());
		
		for (String error : errors) {
			errLst.append(TableHtml.ListItemStart.getTag());
			errLst.append(error);
			errLst.append(TableHtml.ListItemEnd.getTag());
		}
		
		errLst.append(TableHtml.ListEnd.getTag());
		
		return errLst.toString();
	
	}
	
	/**
	 * Generate the metadata display for Media.dbf 
	 * @return Metadata for the Media.dbf table
	 * @throws SecurityException If the OS will not allow the table to be accessed 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public String showMediaMetaData() throws SecurityException, xBaseJException, IOException {
		MediaDbfDao md = new MediaDbfDao(rootDir);
		DbfMetaData metaData = md.loadMetaData();
		
		return metaData.generateTableMetaData();
		
	}
	
	/**
	 * Generate the metadata display for MediaMax.dbf 
	 * @return Metadata for the MediaMax.dbf table
	 * @throws SecurityException If the OS will not allow the table to be accessed 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public String showMediaMaxMetaData() throws SecurityException, xBaseJException, IOException {
		MediaMaxDbfDao mmd = new MediaMaxDbfDao(rootDir);
		DbfMetaData metaData = mmd.loadMetaData();
		
		return metaData.generateTableMetaData();
		
	}
	
	/**
	 * Generate the metadata display for properties.dbf 
	 * @return Metadata for the propperties.dbf table
	 * @throws SecurityException If the OS will not allow the table to be accessed 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	public String showPropertiesMetaData() throws SecurityException, xBaseJException, IOException {
		PropertiesDbfDao pmd = new PropertiesDbfDao(rootDir);
		DbfMetaData metaData = pmd.loadMetaData();
		
		return metaData.generateTableMetaData();
		
	}

	/**
	 * Generate some vertical space between the structure displays
	 * @return A number of newline characters
	 */
	public String structureSpace() {
		StringBuilder sp = new StringBuilder();
		
		sp.append(SysConstants.newline);
		sp.append("----------");
		sp.append(SysConstants.newline);
		
		return sp.toString();
	}
	
	/**
	 * Save the media maximums to the DBF file and reload
	 * @throws IOException If the table cannot be read or updated
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws CloneNotSupportedException Java error caused by called methods 
	 */
	protected void saveMediaMaximums() throws SecurityException, xBaseJException, IOException, CloneNotSupportedException {
		MediaMaxDbfDao mmd = new MediaMaxDbfDao(rootDir);
		List<MediaMaximum> mml = parseMediaMax();
		
		if (mml != null && validateMediaMaximums(mml)) {
			for (MediaMaximum mm : mml) {
				mmd.update(mm);
			}
		}
		
		mediaMaxs = loadMediaMaximums();
		
	}
	
	/**
	 * Save the media data to the data file
	 * @throws IOException If the table cannot be read or updated
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws CloneNotSupportedException Java error caused by called methods 
	 */
	protected void saveMedia() throws SecurityException, xBaseJException, IOException, CloneNotSupportedException {
		MediaDbfDao md = new MediaDbfDao(rootDir);
		List<BackupMedia> bml = parseMedia();
		boolean pack = false;
		
		if (bml != null && validateMedia(bml)) {
			for (BackupMedia bm : bml) {
				if (bm.isDelete()) {
					md.delete(bm.getMediaId());
					pack = true;
				}
				else if (bm.getUpdated() == DBFOperations.Insert) {
					md.insert(bm);
				}
				else if (bm.getUpdated() == DBFOperations.Modified) {
					md.update(bm);
				}
				
			}
			
			if (pack) {
				md.pack();
			}
	
		}
		
	}

	/**
	 * Save the media data to the data file
	 * @throws SecurityException If the OS will not allow the table to be created 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 * @throws CloneNotSupportedException Java error caused by called methods 
	 */
	protected void saveProperties() throws SecurityException, xBaseJException, IOException, CloneNotSupportedException {
		
		if (parseProperties()) {
			properties.validate();
			
			if  (!properties.hasErrors()) {
				properties.save();
			}
			
		}
		
	}

	/**
	 * Determine the root directory of this site
	 * @return the root directory
	 */
	private String setRootDir() {
	    String root = request.getServletContext().getRealPath(String.valueOf(SysConstants.dirSep)).replace('\\', SysConstants.dirSep) + 
                      request.getServletPath().substring(1);
        int pos = root.lastIndexOf(SysConstants.dirSep);
  
        return root.substring(0, pos + 1);
		
	}
	
	/**
	 * Load the media maximums
	 * @return A map of the system properties
	 * @throws IOException If the table cannot be read or updated
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	private Map<MediaTypes, Integer> loadMediaMaximums() throws SecurityException, xBaseJException, IOException {
		MediaMaxDbfDao pd = new MediaMaxDbfDao(rootDir);
	
		return pd.mapAll();
		
	}
	
	/**
	 * Retrieve the next backup media to use
	 * @param type Get the next ID for this type of media
	 * @return the media to use for the next backup
	 * @throws IOException If the table cannot be read or updated
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws SecurityException If the OS will not allow the table to be created 
	 */
	private BackupMedia getNextMedia(MediaTypes type) throws SecurityException, xBaseJException, IOException {
		MediaDbfDao md = new MediaDbfDao(rootDir);
		List<BackupMedia> bm = md.listByType(type, true);  // Load only active records
		BackupMedia m = null;
		
		if (!bm.isEmpty()) {
		    m = bm.get(0);
		}
		
		return m;
		
	}
	
	/**
	 * Parse the data from the form
	 * @return A list of backup media types and their associated maximum usages
	 */
	private List<MediaMaximum> parseMediaMax() {
		String [] mediaTypes = request.getParameterValues(MediaMaximum.Fields.MediaType.name());
		String [] maxUses = request.getParameterValues(MediaMaximum.Fields.MaxUse.name());
		List<MediaMaximum> mml = new ArrayList<>();
		
		if (mediaTypes == null || maxUses == null || !isPost()) {
			mediaTypes = new String[0];
		}
		
		for (int i = 0; i < mediaTypes.length; i++) {
			MediaMaximum mm = new MediaMaximum();
			
			mm.setMediaType(MediaTypes.valueOf(mediaTypes[i]));
			mm.setMaxUse(processMaxUsage(maxUses[i]));
			mml.add(mm);
			
		}
		
		return mml;
		
	}

	/**
	 * Convert a maximum usage value to an integer
	 * @param mu the value to convert
	 * @return The integer value of the input or -1 if the value cannot be converted to an integer
	 */
	private int processMaxUsage(String mu) {
		int muv;
		
		try {
			muv = Integer.valueOf(mu.trim()).intValue();
		}
		catch (NumberFormatException e) {
			muv = -1;
		}

		return muv;
		
	}
	
	/**
	 * Validate media maximum data
	 * @param mml A list of media maximums
	 * @return true if the list is valid, otherwise false
	 */
	private boolean validateMediaMaximums(List<MediaMaximum> mml) {
		
		for (MediaMaximum mm : mml) {
			errors.addAll(mm.validate());
		}
		
		return errors.isEmpty();
		
	}
	
	/**
	 * Parse the data from the HTML form
	 * @return list of backup media extracted from the request
	 */
	private List<BackupMedia> parseMedia() {
		SimpleDateFormat sdf = new SimpleDateFormat(SysConstants.timestampFmt);
		String [] mediaIds = request.getParameterValues(BackupMedia.Fields.MediaId.name());
		String [] mediaTypes = request.getParameterValues(BackupMedia.Fields.MediaType.name());
		String [] firstUses = request.getParameterValues(BackupMedia.Fields.FirstUse.name());
		String [] lastUses = request.getParameterValues(BackupMedia.Fields.LastUse.name());
		String [] useCounts = request.getParameterValues(BackupMedia.Fields.UseCount.name());
		String [] actives = request.getParameterValues(BackupMedia.Fields.Active.name());
		String [] updateds = request.getParameterValues(BackupMedia.Fields.Updated.name());
		String [] deletes = request.getParameterValues(BackupMedia.Fields.Delete.name());
		List<BackupMedia> bm = new ArrayList<>();
		
		if (mediaIds == null || !isPost()) {
			mediaIds = new String[0];
		}
		
		for (int i = 0; i < mediaIds.length; i++) {
			BackupMedia m = new BackupMedia();
			
			if (mediaIds[i].length() > 0) {
				m.setMediaId(mediaIds[i].toUpperCase().charAt(0));
			}
			
			if (mediaTypes[i].trim().length() > 0) {
				m.setMediaType(MediaTypes.valueOf(mediaTypes[i]));
			}
			
			if (firstUses.length > i && firstUses[i].length() > 0) {
				try {
					m.setFirstUse(sdf.parse(firstUses[i]));
				} 
				catch (ParseException e) {
					errors.add(ErrMsgs.BAD_MEDIA.getMsg(firstUses[i], mediaIds[i]));
				}
			}
			
			if (lastUses.length > i && lastUses[i].length() > 0) {
				try {
					m.setLastUse(sdf.parse(lastUses[i]));
				} 
				catch (ParseException e) {
					errors.add(ErrMsgs.BAD_MEDIA.getMsg(lastUses[i], mediaIds[i]));
				}
			}
			
			if (useCounts.length > i) {
				m.setUseCount(Integer.valueOf(useCounts[i]));
			}
			
			m.setActive(getCheckboxValue(actives, m.getMediaId()));
			m.setUpdated(DBFOperations.getValueOf(updateds[i]));
			m.setDelete(getCheckboxValue(deletes, m.getMediaId()));
			
			bm.add(m);
			
		}
		
		return bm;
		
	}
	
	/**
	 * Parse the properties data from the HTML form
	 * @return true if the data updated at least one property, otherwise false
	 */
	private boolean parseProperties() {
		String [] keys = request.getParameterValues(Property.Fields.Property.name());
		String [] values = request.getParameterValues(Property.Fields.Value.name());
		boolean updates = false;
		
		if (keys == null || !isPost()) {
			keys = new String[0];
		}
		
		for (int i = 0; i < keys.length; i++) {
			Property.Properties key;

			try {
				key = Property.Properties.valueOf(keys[i].split("\\" + Property.groupSep)[1]);
			}
			catch (IllegalArgumentException e) {
				key = null;
			}
			
			if (key != null) {
				Property prop = properties.get(key);
				
				if (prop == null) {
					properties.add(key, values[i]);
					updates = true;
				}
				else if (!prop.getValue().equals(values[i])) {
					prop.setValue(values[i]);
					updates = true;
				}
				
			}
			
		}
		
		return updates;
		
	}
	
	/**
	 * Determine if the request is a POST type request
	 * @return true if the request is a POST, otherwise false
	 */
	private boolean isPost() {
		
		return request.getMethod().equals(SysConstants.HttpRequestTypes.POST.name());
		
	}
	
	/**
	 * Determine the value of a checkbox
	 * @param cbList list of the checkboxes to search for the associated value
	 * @param value the value associated with the desired checkbox
	 * @return true if the checkbox was checked, otherwise false
	 */
	private boolean getCheckboxValue(String [] cbList, char value) {
		boolean cbVal = false; 
		
		if (cbList != null) {
			for (int i = 0; i < cbList.length && !cbVal; i++) {
				cbVal = (cbList[i].charAt(0) == value);
			}
		}

		return cbVal;
		
	}
	
	/**
	 * Validate the media
	 * @param bm list of media to validate
	 * @return true if the list is valid, otherwise false
	 */
	private boolean validateMedia(List<BackupMedia> bm) {
		Map<Character, Character> ids = new HashMap<>();
		
		for (BackupMedia m : bm) {
			errors.addAll(m.validate());
			
			if (ids.containsKey(m.getMediaIdAsCharacter())) {
				errors.add(ErrMsgs.DUPLICATE_MEDIA.getMsg(m.getMediaIdAsString()));
			}
			else {
				ids.put(m.getMediaIdAsCharacter(), null);
			}
			
		}
		
		return errors.isEmpty();
		
	}
	
}
