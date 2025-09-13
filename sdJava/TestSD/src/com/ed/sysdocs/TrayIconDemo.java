package com.ed.sysdocs;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

// From: https://stackoverflow.com/questions/34490218/how-to-make-a-windows-notification-in-java
public class TrayIconDemo {
	public static final String rootDir = "C:/Src/sysdocs/";
	public static final String sysTitle = "SysDocs Media Usage Updater";

    public static void main(String[] args) throws AWTException {
    	    	
        if (SystemTray.isSupported()) {
            displayTray();
        } 
        else {
            System.err.println("System tray not supported!");
        }
        
        System.exit(0);
        
    }

    public static void displayTray() throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(rootDir + "include/mc.png");
        TrayIcon trayIcon = new TrayIcon(image, sysTitle);

        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
        trayIcon.displayMessage(sysTitle, "Incremented the usage count of media G:", MessageType.INFO);
        
        try {
			Thread.sleep(10000);
		} 
        catch (InterruptedException e) {
			e.printStackTrace();
		}
        
    }
    
}