package com.ed.pojo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xBaseJ.DBF;
import org.xBaseJ.fields.Field;
import org.xBaseJ.fields.NumField;
import org.xBaseJ.indexes.Index;
import org.xBaseJ.xBaseJException;

import com.ed.sysdocs.SysConstants;

/**
 * Holder for DBF metadata
 * @author Ed Swaneck
 * @version 1.0
 * @since 04/29/2024
 */
public class DbfMetaData {

	/** Width of the field table */
	public static final int fieldTableWidth = 5;
	
	/** Field name maximum length */
	public static final int fieldNameMaxLen = 10;
	
	/** Header for the field list metadata */
	private static final String fieldListHeader = "Fld   Name       Type   Width   Dec";
	
	/** Complete file specification of the DBF */
	protected String fileSpec = null;

	/** Version of the DBF file structure */
	protected int dbfVersion = 0;

	/** Number of records in the DBF */
	protected long recordCount = 0;
	
	/** Date/time the DBF was last updated */
	protected Date lastUpdated = null;
	
	/** List of DBF field definitions */
	protected List<Field> fields = new ArrayList<>();
	
	/** List of associated indexes */
	protected List<Index> indexes = new ArrayList<>();
	
	private DecimalFormat fcf = new DecimalFormat(SysConstants.fieldCounterFormat);
	private DecimalFormat rcf = new DecimalFormat(SysConstants.recordCountFormat);
	
	/**
	 * @return The complete file specification of the DBF
	 */
	public String getFileSpec() {
		return fileSpec;
	}
	
	/**
	 * @return Formatted line for the DBF file specification
	 */
	public String getFormattedFileSpec() {
		return "Structure for file: " + fileSpec;
	}
	
	/**
	 * @param fileSpec The complete file specification of the DBF
	 */
	public void setFileSpec(String fileSpec) {
		this.fileSpec = fileSpec;
	}
	
    /**
     * @return Version of the DBF structure
     */
	public int getDbfVersion() {
		return dbfVersion;
	}

	/**
	 * @param dbfVersion Version of the DBF structure
	 */
	public void setDbfVersion(int dbfVersion) {
		this.dbfVersion = dbfVersion;
	}

	/**
	 * @return Number of records in the DBF
	 */
	public long getRecordCount() {
		return recordCount;
	}
	
	/**
	 * @return Formatted line for the record count
	 */
	public String getFormattedRecordCount() {
		return "Number of records: " + rcf.format(recordCount);
	}
	
	/**
	 * @param recordCount Number of records in the DBF
	 */
	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}
	
	/**
	 * @return Date/time the DBF was last updated
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	/**
	 * @return Formatted line for the last updated date/time
	 */
	public String getFormattedLastUpdated() {
		SimpleDateFormat sdf = new SimpleDateFormat(SysConstants.timestampDisplay);
		
		return "Last updated: " + sdf.format(lastUpdated);
		
	}
	
	/**
	 * @param lastUpdated Date/time the DBF was last updated
	 */
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	/**
	 * @return List of DBF field definitions
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * Add a field definition to the list of fields
	 * @param field A DBF field definition
	 */
	public void addField(Field field) {
		fields.add(field);
	}
	
	/**
	 * @return List of indexes
	 */
	public List<Index> getIndexes() {
		return indexes;
	}

	/**
	 * Add an index to the list
	 * @param index The index to add to the list
	 */
	public void addIndex(Index index) {
		indexes.add(index);
	}
	
	/**
	 * Generate the meta data for this DBF file
	 * @return The complete formatted metadata for a DBF file 
	 */
	public String generateTableMetaData() {
		StringBuilder md = new StringBuilder();

		md.append(generateMetaDataHeader());
		md.append(SysConstants.newline);
		md.append(generateMetaDataFields());
		md.append(SysConstants.newline);
		md.append(generateMetaDataTotal());
		md.append(SysConstants.newline);
		md.append(SysConstants.newline);
		md.append(generateMetaDataIndex());
		
		return md.toString();
		
	}
	
	/**
	 * Generate the meta data header
	 * @return formatted metadata header
	 */
	private StringBuilder generateMetaDataHeader() {
		StringBuilder hdr = new StringBuilder();

		hdr.append(getFormattedFileSpec());
		hdr.append(SysConstants.newline);
		hdr.append(generateFormattedDbfVersion());
		hdr.append(SysConstants.newline);
		hdr.append(getFormattedRecordCount());
		hdr.append(SysConstants.newline);
		hdr.append(getFormattedLastUpdated());
		
		return hdr;
		
	}

	/**
	 * Generate a formatted string describing the version of the DBF file structure
	 * @return formatted version of the DBF file structure
	 */
	private String generateFormattedDbfVersion() {
		final String dbase = "dBase";
		final String iii = " III";
		final String iv = " IV";
		final String withMemo = " with Memo";
		StringBuilder fdv = new StringBuilder("DBF version: ");
		
		switch (dbfVersion) {
			case DBF.DBASEIII:
				fdv.append(dbase);
				fdv.append(iii);
				break;
			case DBF.DBASEIII_WITH_MEMO:
				fdv.append(dbase);
				fdv.append(iii);
				fdv.append(withMemo);
				break;
			case DBF.DBASEIV:
				fdv.append(dbase);
				fdv.append(iv);
				break;
			case DBF.DBASEIV_WITH_MEMO:
				fdv.append(dbase);
				fdv.append(iv);
				fdv.append(withMemo);
				break;
			case DBF.FOXPRO_WITH_MEMO:
				fdv.append("FoxPro");
				fdv.append(withMemo);
				break;
			default:
				fdv.append("Unknown");
				break;
		
		}
		
		return fdv.toString();
		
	}
	
	/**
	 * Generate the output for the field list
	 * @return the formatted field list table
	 */
	private StringBuilder generateMetaDataFields() {
		StringBuilder ft = new StringBuilder();
		NumField nf = new NumField();
		
		ft.append(fieldListHeader);
		
		for (int pos = 0; pos < fields.size(); pos++) {
			Field f = fields.get(pos);
			char fType;
			
			ft.append(SysConstants.newline);
			ft.append(fcf.format(pos + 1));
			ft.append(spaces(3));
			ft.append(padName(f.getName()));
			ft.append(spaces(3));
			
			try {
				fType = f.getType();
			} 
			catch (xBaseJException e) {
				fType = 'X';
			}
			
			ft.append(fType);
			ft.append(spaces(5));
			ft.append(fcf.format(f.getLength()));
			
			if (fType == nf.getType()) {
				ft.append(spaces(5));
				ft.append(f.getDecimalPositionCount());
			}
			
		}
		
		return ft;
		
	}
	
	/**
	 * Generate the total row
	 * @return the formatted total row
	 */
	private StringBuilder generateMetaDataTotal() {
		StringBuilder t = new StringBuilder();
		int total = 0;

		for (Field f : fields) {
			total += f.getLength();
		}
		
		t.append("** Total **");
		t.append(spaces(12));
		t.append(rcf.format(total));
		
		return t;
		
	}
	
	/**
	 * Generate the list of associated indexes 
	 * @return the formatted list of indexes
	 */
	private StringBuilder generateMetaDataIndex() {
		StringBuilder x = new StringBuilder();
		
		if (!indexes.isEmpty()) {
			x.append(indexes.size() > 1 ? "Indexes" : "Index");
			x.append(SysConstants.newline);
			
			for (int i = 0; i < indexes.size(); i++) {
				Index ndx = indexes.get(i); 
				
				if (i > 0) {
					x.append(SysConstants.newline);
				}
				
				x.append(i + 1);
				x.append(". ");
				
				x.append(fnFromFs(ndx.getName()));
				x.append(": ");
				x.append(ndx.getKeyFields().toUpperCase());
				x.append(", ");
				x.append(ndx.is_unique_key() ? "" : "not ");
				x.append("unique");
				
			}
			
		}
		
		return x;
		
	}
	
	/**
	 * Extract the file name from a filespec 
	 * @param fs The file specification
	 * @return The last element of the file specification
	 */
	private String fnFromFs(String fs) {
		String [] fn = fs.split(String.valueOf(SysConstants.dirSep));
		
		return fn[fn.length - 1];
		
	}
	
	/**
	 * Pad the name field to the maximum length
	 * @param nm Field name to pad
	 * @return the padded field name
	 */
	private String padName(String nm) {
		StringBuilder pnm = new StringBuilder(nm);

		while (pnm.length() < fieldNameMaxLen) {
			pnm.append(' ');
		}
		
		return pnm.toString();
		
	}
	
	/**
	 * Generate a string that is a given number of spaces long
	 * @param spCount Create a string this many spaces long 
	 * @return The requested string of spaces
	 */
	private String spaces(int spCount) {
		StringBuilder sp = new StringBuilder();
		
		for (int i = 0; i < spCount; i++) {
			sp.append(' ');
		}
				
		return sp.toString();
		
	}
	
}
