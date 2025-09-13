package com.ed.sysdocs;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;

import org.xBaseJ.xBaseJException;

import com.ed.sysdocs.dao.MediaDbfDao;

/**
 * Increment the last used date and use count for a media type<br>
 * Usage: java -cp C:/Src/WEB-INF/lib/* com.ed.sysdocs.IncrementMedia &lt;Site RootDir&gt; &lt;Media Type&gt; [ -qm | -qn | -qmn ]<br>
 * &lt;Site RootDir&gt; ::= Root directory of the website that contains the Media.dbf file<br>
 * &lt;Media Type&gt; ::= tape | externalHD | CDRW | flash<br>
 * -qm | -qn | -qmn ::= Quiet mode.  Suppress printing of the confirmation message, notification message or both.
 * @author Ed Swaneck
 * @version 1.0
 * @since 05-03-2024
 */
public class IncrementMedia {
	
	private static final String notificationIcon = "images/mc.png";
	
	/**
	 * Run the IncrementMedia utility
	 * @param args Command line arguments:
	 * <ol>
	 *     <li>Root directory of the site.</li>
	 *     <li>One of: tape | externalHD | CDRW | flash.</li>
	 *     <li>Zero or one of: -qm | -qn | -qmn - To suppress the final confirmation message and/or notification. </li>
	 * </ol>
	 */
	public static void main(String [] args) {
		CmdLineParms parms = new CmdLineParms(args);
		
		if (parms.parmsOk()) {
			try {
				char mediaId = runIncrement(parms);
				
				if (mediaId == SysConstants.badMediaId) {
					parms.addErr(ErrMsgs.CLI_NO_TYPE.getMsg(parms.getMediaType().getDisplayName()));
				}
				else {
					displayOutput(parms, mediaId, parms.getMediaType());
				}
				
			} 
			catch (SecurityException | xBaseJException | IOException | 
				   AWTException | InterruptedException e) {
				parms.addErr(e.getMessage());
			}
			
		}

		for (String e : parms.getErrs()) {
			System.err.println(e);
		}
		
		System.exit(0);
			
	}
	
	/**
	 * Update the last used date and counter of the least recently used media of the requested media type
	 * @param parms Validated command line parameters
	 * @return The ID of the media that was updated or * if the update was unsuccessful 
	 * @throws SecurityException If the OS will not allow the table to be updated 
	 * @throws xBaseJException If the table already exists and an attempt is made to overwrite it
	 * @throws IOException If the table cannot be read or updated
	 */
	private static char runIncrement(CmdLineParms parms) throws SecurityException, xBaseJException, IOException {
		MediaDbfDao md = new MediaDbfDao(parms.getRootDir());
		
		return md.increment(parms.getMediaType());
		
	}
	
	/**
	 * Display any completion messages
	 * @param parms Parameters from the command line
	 * @param mediaId ID of the media that was updated
	 * @param mediaType Type of media that is being used for the backed up
	 * @throws AWTException If the desktop system tray is missing
	 * @throws InterruptedException If any thread has interrupted the current thread 
	 */
	private static void displayOutput(CmdLineParms parms, char mediaId, MediaTypes mediaType) throws AWTException, InterruptedException {
		
		if (!parms.isQuietMsg()) {
			System.out.println(completionMsg(mediaId));
		}
		
		if (!parms.isQuietNote()) {
			displayNotification(parms, mediaId, mediaType);
		}
		
	}

	/**
	 * Display a system tray notification message
	 * @param parms Parameters from the command line
	 * @param mediaId ID of the media that was updated
	 * @param mediaType Type of media that is being used for the backed up
	 * @throws AWTException If the desktop system tray is missing
	 * @throws InterruptedException If any thread has interrupted the current thread 
	 */
    private static void displayNotification(CmdLineParms parms, char mediaId, MediaTypes mediaType) throws AWTException, InterruptedException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(parms.getRootDir() + notificationIcon);
        TrayIcon trayIcon = new TrayIcon(image);
        String title = "SysDocs " + mediaType.getDisplayName() + " backup complete";

        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
        trayIcon.displayMessage(title, completionMsg(mediaId), MessageType.INFO);
		Thread.sleep(5000);
    	
    }
    
    /**
     * Generate the completion message
     * @param mediaId ID of the media that was updated
     * @return The completion message to display
     */
    private static String completionMsg(char mediaId) {
    	return "Media " + mediaId + ": Last Used date and Usage Count updated.";
    }
    
}
