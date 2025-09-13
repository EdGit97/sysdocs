package com.ed.pojo;

import java.util.List;

import com.ed.sysdocs.ComboItem;

/**
 * Root class for POJOs
 * @author Ed Swaneck
 * @version 1.0
 * @since 04/13/2024
 */
public class SDPojo {

	/**
	 * Generate an HTML hidden input tag for a media value
	 * @param name Name of the tag
	 * @param value value associated with the tag
	 * @return the complete HTML hidden tag
	 */
	protected String generateHidden(String name, String value) {
		StringBuilder hidden = new StringBuilder("<input type='hidden' name='");
		
		hidden.append(name);
		hidden.append("' value='");
		hidden.append(value);
		hidden.append("'>");
		
		return hidden.toString();
		
	}
	
	/**
	 * Generate a checkbox and set the associated value
	 * @param name Name of the tag
	 * @param id media id associated with the checkbox
	 * @param checked true if the box should be checked, otherwise false
	 * @param onClick A JavaScript call for the on click attribute.  Set to null if no on click event is needed.   
	 * @return the complete checkbox element
	 */
	protected String generateCheckBox(String name, char id, boolean checked, String onClick) {
		StringBuilder check = new StringBuilder("<input type='checkbox' name='");
		
		check.append(name);
		check.append("' value='");
		check.append(id);
		check.append("' ");
		
		if (onClick != null) {
			check.append("onclick='javascript: ");
			check.append(onClick);
			check.append("' ");
		}

		if (checked) {
			check.append("checked");
		}
		
		check.append(">");

		return check.toString();
		
	}
	
	/**
	 * Generate an HTML text box
	 * @param name The value for the name attribute
	 * @param value The value for the value attribute
	 * @param size The value for the size attribute
	 * @param maxLen The value for the maxlength attribute
	 * @return A complete HTML text box
	 */
	protected String generateTextBox(String name, String value, int size, int maxLen) {
		StringBuilder tb = new StringBuilder("<input type='text' name='");
		
		tb.append(name);
		tb.append("' size='");
		tb.append(size);
		
		if (maxLen > 0) {
			tb.append("' maxlength='");
			tb.append(maxLen);
		}
		
		tb.append("' value='");
		tb.append(value);
		tb.append("'>");
		
		return tb.toString();
		
	}

	/**
	 * Generate an HTML password box
	 * @param name The value for the name attribute
	 * @param value The value for the value attribute
	 * @param size The value for the size attribute
	 * @param maxLen The value for the maxlength attribute
	 * @return A complete HTML password box
	 */
	protected String generatePwdBox(String name, String value, int size, int maxLen) {
		StringBuilder tb = new StringBuilder("<input type='password' name='");
		
		tb.append(name);
		tb.append("' size='");
		tb.append(size);
		
		if (maxLen > 0) {
			tb.append("' maxlength='");
			tb.append(maxLen);
		}
		
		tb.append("' value='");
		tb.append(value);
		tb.append("'>");
		
		return tb.toString();
		
	}
	
	/**
	 * Generate an HTML textarea box
	 * @param name The value for the name attribute
	 * @param value The data that will appear in the textarea
	 * @param width The value for the width attribute
	 * @return A complete HTML textarea box
	 */
	protected String generateTextArea(String name, String value, int width) {
		final String toolTip = "'Place each directory on its own line. Maximum 100 characters per line.'";
		StringBuilder ta = new StringBuilder("<textarea class='property' cols='");
		
		ta.append(width);
		ta.append("' rows='5' wrap='hard' name='");
		
		ta.append(name);
		ta.append("' ");
		ta.append("title=");
		ta.append(toolTip);
		ta.append("placeholder=");
		ta.append(toolTip);
		ta.append(">");
		ta.append(value);
		ta.append("</textarea>");
		
		return ta.toString();
		
	}

	/**
	 * Generate an HTML dropdown
	 * @param name The value for the name attribute
	 * @param value Current value of the field
	 * @param dropDown The list of items for the dropdown
	 * @return A complete HTNL select element
	 */
	protected String generateComboBox(String name, String value, List<ComboItem> dropDown) {
		StringBuilder cb = new StringBuilder("<select name='");

		cb.append(name);
		cb.append("'>");
		
		for (ComboItem o : dropDown) {
			cb.append("<option value='");
			cb.append(o.getValue());
			cb.append("'");
			
			if (o.getValue().equals(value)) {
				cb.append(" selected");
			}
			
			cb.append(">");
			cb.append(o.getLabel());
			cb.append("</option>");
		}
		
		cb.append("</select>");
		
		return cb.toString();
		
	}
	
}
