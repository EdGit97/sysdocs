package com.ed.sysdocs;

/**
 * Tags for displaying the media data
 * @author Ed Swaneck
 * @version 1.0
 * @since 12/28/2023
 */
public enum TableHtml {
	
	/** HTML tr open tag */
	RowStart("<tr>"), 
	
	/** HTML tr open tag, dark background */
	RowStartOdd("<tr class='odd'>"), 
	
	/** HTML tr open tag with color set to red */
	RowStartRed("<tr class='red'>"), 
	
	/** HTML tr open tag with color set to red and dark background */
	RowStartOddRed("<tr class='oddRed'>"), 
	
	/** HTML centered td open tag */
	ColStartC("<td class='mediaC'>"), 
	
	/** HTML left aligned td open tag */
	ColStartL("<td class='mediaL'>"), 
	
	/** HTML right aligned td open tag */
	ColStartR("<td class='mediaR'>"), 
	
	/** HTML left aligned td open tag */
	PlainColStart("<td>"), 
	
	/** HTML right aligned td open tag */
	PropColStartL("<td class='propL'>"), 
	
	/** HTML right aligned td open tag */
	PropColStartR("<td class='propR'>"), 
	
	/** HTML td close tag */
	ColEnd("</td>"), 
	
	/** HTML tr close tag */
	RowEnd("</tr>"),
	
	/** HTML ul open tag */
	ListStart("<ul>"),
	
	/** HTML ul close tag */
	ListEnd("</ul>"),
	
	/** HTML li open tag */
	ListItemStart("<li>"),
	
	/** HTML li close tag */
	ListItemEnd("</li>"),
	
	/** HTML br tag */
	NewLine("<br>"),
	
	/** HTML superscript open tag */
	SupStart("<sup>"),
	
	/** HTML superscript close tag */
	SupEnd("</sup>"),
	
	/** HTML label open tag */
	LabelStart("<label class='media'>"),
	
	/** HTML label close tag */
	LabelEnd("</label>"),
	
	/** HTML hard space */
	HardSpace("&nbsp;");
	
	private String tag;
	
	/**
	 * Constructor
	 * @param tag associated HTML tag
	 */
	private TableHtml(String tag) {
		this.tag = tag;
	}
	
	/**
	 * @return The value associated with the tag
	 */
	public String getTag() {
		return tag;
	}
	
	/**
	 * Generate the td tag for the scheduled task table
	 * @param classNm Class name to include in the tag.  For no class name, pass null or an empty string
	 * @param name Value of an HTML name attribute. For no name, pass null or an empty string
	 * @param colspan The number of columns that this column will span
	 * @param content Text content of the column
	 * @return A complete HTML table column start tag
	 */
	public static String buildCol(String classNm, String name, int colspan, String content) {
		StringBuilder td = new StringBuilder(PlainColStart.getTag().substring(0, 3));
		
		if (classNm != null && classNm.length() > 0) {
			td.append(" class='");
			td.append(classNm);
			td.append("'");
		}
		
		if (name != null && name.length() > 0) {
			td.append(" name='");
			td.append(name);
			td.append("'");
		}
		
		if (colspan > 1) {
			td.append(" colspan='");
			td.append(colspan);
			td.append("'");
		}
		
		td.append(">");
		td.append(content);
		td.append(ColEnd.getTag());
		
		return td.toString();
				
	}
	
}
