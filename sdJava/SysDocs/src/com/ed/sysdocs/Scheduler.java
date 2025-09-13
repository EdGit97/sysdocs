package com.ed.sysdocs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.ed.pojo.ScheduledTask;
import com.ed.pojo.Property.Properties;

/**
 * Load tasks from the scheduler and organize them for display on the backups.jsp page. 
 * @author Ed Swaneck
 * @version 1.0
 * @since 04-04-2025
 */
public class Scheduler {
	
	/** Command to query the list of tasks in the scheduler */
	public static final String [] schCmd = { "C:\\Windows\\system32\\schtasks.exe", "/query", "/xml" }; 
	
	/** XML tag names for extracting data */
	public enum DataTags {
		/** URI tag */
		URI, 
		
		/** Task start tag */
		StartBoundary, 
		
		/** Application that will be run */
		Command, 
		
		/** Application command line parameters */
		Arguments;
		
	}

	/**
	 * Sort the list of scheduled tasks by scheduled time of day 
	 */
	private class SortByTime implements Comparator<ScheduledTask> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ScheduledTask t1, ScheduledTask t2) {
			
	        return t1.getScheduledHourMinute().compareTo(t2.getScheduledHourMinute());
			
		}
	    
	}

	/** List of tasks that will be displayed on the web page */
	protected List<ScheduledTask> tasks = new ArrayList<>();
	
	/** Directory of the site */
	private String rootDir;
	
	/** List of error messages */
	private List<String> errors;
	
	/** Folder where the scheduled tasks are located */
	private String schedFolder;

	/**
	 * Constructor
	 * @param properties The systems properties
	 * @param rootDir The absolute path, on the server, where the web page page resides 
	 */
	public Scheduler(PropertiesOps properties, String rootDir) {
		
		super();
		
		this.rootDir = rootDir;
		this.errors = new ArrayList<>();
		this.schedFolder = properties.get(Properties.schedFolder).getValue();
		
	}
	
	/**
	 * Load the task list
	 */
	public void processSchedule() {
		String output = null;
		Document doc = null;
		
		try {
			output = loadSchedule();
		} 
		catch (IOException | InterruptedException e) {
			errors.add(e.getMessage());
		}
		
		if (errors.isEmpty()) {
			
			try {
				doc = convertToXML(output);
			}
			catch (ParserConfigurationException | SAXException | IOException e) {
				errors.add(e.getMessage());
			}
			
		}
		
		if (errors.isEmpty()) {
			generateTaskList(doc);
		}
		
	}
	
	/**
	 * Generate a displayable list of the error messages
	 * @param colspan The number of columns that the error messages will span
	 * @return The error list formatted as a series of HTML table rows with one column
	 */
	public String tableFormatErrors(int colspan) {
		StringBuilder rows = new StringBuilder();
		boolean odd = true;

		for (String err : errors) {
			rows.append((odd ? TableHtml.RowStartOdd : TableHtml.RowStart).getTag());
			rows.append(TableHtml.buildCol("links", null, 4, err));
			rows.append(TableHtml.RowEnd.getTag());
			rows.append(SysConstants.newline);
			odd = !odd;
			
		}

		return rows.toString();
		
	}
	
	/**
	 * Process the backup schedule
	 * @return The output from the task scheduler
	 * @throws IOException If an I/O error occurs while the process is running or the results are being read
	 * @throws InterruptedException If the current thread is interrupted by another thread while it is waiting
	 */
	private String loadSchedule() throws IOException, InterruptedException {
		final String xmlHdr = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>";
		ProcessBuilder pb = new ProcessBuilder(schCmd );
        Process p = pb.start();  
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder output = new StringBuilder(xmlHdr);
        String line;
        
        while ((line = reader.readLine()) != null) {
        	if (line.length() > 0 && !xmlHdr.equals(line)) {
                output.append(line);
        	}
        }
        
		return output.toString();
        
	}
	
	/**
	 * Convert the scheduler output to XML
	 * @param xmlString The output to convert
	 * @return An XML document containing the scheduler output
	 * @throws ParserConfigurationException  If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 * @throws SAXException If any parse errors occur
	 * @throws IOException If any IO errors occur.
	 */
	private Document convertToXML(String xmlString) 
			throws ParserConfigurationException, 
			       SAXException, 
			       IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        StringReader sr =  new StringReader(xmlString);
        InputSource inputSource = new InputSource(sr);
        Document doc = builder.parse(inputSource);
        
        sr.close();
        
        return doc;
        
    }
	
	/**
	 * Generate the list of tasks from the XML document
	 * @param doc The XML document
	 */
	private void generateTaskList(Document doc) {
		NodeList taskNodes = doc.getElementsByTagName("Task");
		
		tasks.clear();		
		
		for (int i = 0; i < taskNodes.getLength(); i++) {
			Element taskElt = (Element) taskNodes.item(i);
			String uri = extractContent(taskElt, DataTags.URI.name());
			
			if (uri.startsWith(schedFolder)) {
				tasks.add(makeScheduledTask(taskElt));
			}
			
		}
		
		Collections.sort(tasks, new SortByTime());
		
	}
	
	/**
	 * Extract the content of a tag
	 * @param elt Find tag within this element
	 * @param tagName Extract this tag from the parent
	 * @return The content of the tagName element or empty string if tagName is not a child element
	 */
	private String extractContent(Element elt, String tagName) {
		NodeList nodes = elt.getElementsByTagName(tagName);
		String content = "";
		
		if (nodes.getLength() > 0) {
			content = nodes.item(0).getTextContent();
		}
		
		return content;
		
	}
	
	/**
	 * Generate a task from the XML element
	 * @param elt The XML element containing the task information
	 * @return A populated scheduled task object
	 */
	private ScheduledTask makeScheduledTask(Element elt) {
		SimpleDateFormat sdf = new SimpleDateFormat(SysConstants.taskDateFormat);
		StringBuilder cmd = new StringBuilder(extractContent(elt, DataTags.Command.name()));
		String taskName = extractContent(elt, DataTags.URI.name()).substring(schedFolder.length());
		LogOps logOps = new LogOps(rootDir);
		Date scheduledTime = null;
		
		// Add the command line parameters
		cmd.append(' ');
		cmd.append(extractContent(elt, DataTags.Arguments.name()));
		
		// Get the start time
		try {
			scheduledTime = sdf.parse(extractContent(elt, DataTags.StartBoundary.name()).replace('T', ' '));
		} 
		catch (ParseException e) {
			scheduledTime = null;
		}
		
		return new ScheduledTask(taskName, scheduledTime, logOps.getLatestLog(taskName), cmd.toString());
		
	}

	/**
	 * Getter
	 * @return The list of scheduled tasks
	 */
	public List<ScheduledTask> getTasks() {
		return tasks;
	}

	/**
	 * Getter
	 * @return The list of errors
	 */
	public List<String> getErrors() {
		return errors;
	}
	
	/**
	 * Clear the list of errors
	 */
    public void clearErrors() {
    	errors = new ArrayList<>();
    }
    
}
