#!/usr/bin/env python3
"""Run a local backup to some removable media, update media statistics and 
email the results.

To run: C>py localBackup.py [ -h | --help ] [ tape | externalHD | CDRW | flash ] 
To generate pydoc: py -m pydoc localBackup | py %PYTHONPATH%\\pydocCleanup.py > ..\\scriptDoc\\localBackup.txt

The properties table contains parameters that specify the type of media to 
update, name of the volume to update, the password to access the encrypted 
media and the email address to notify upon completion.
"""
from typing import Final
import datetime
import os
import subprocess
import sendMail
import sys
import sysProps
import util
import wmi

USAGE: Final[str] = "usage: py localBackup.py [ -h | --help ] [ " + " | ".join(sysProps.MEDIA_TYPES) + " ]"
HELP_FLAGS: Final[tuple] = ( "-h", "--help" )
VERACRYPT_CMD: Final[str] = "C:\\Program Files\\VeraCrypt\\VeraCrypt.exe"
FFS_CMD: Final[str] = "C:\\Program Files\\FreeFileSync\\FreeFileSync.exe"
BACKUP_JOB: Final[str] = "C:\\Src\\sysdocs\\backup\\local.ffs_batch"
LOG_DIR: Final[str] = util.resolvePath(__file__, "../backup/logs")
NL: Final[str] = "\n"

class DirEntry:
    """Class to hold a log file specification and associated last modified datetime.
    
    Args:
        fileName (str): Name of the file.
        lastModified (datetime.datetime): Date that fileName was last modified.
    """
    def __init__(self, fileName: str, lastModified: datetime.datetime):
        self.fileName = fileName
        self.lastModified = lastModified
        
class Config(sysProps.SysProps):
    """Class to organize the collected data needed for all operations."""
    
    def __init__(self):
        """Class initialization."""
        super().__init__()
        
    def getMediaType(self) -> str:
        """Determine the type of media to update.  If a valid media type was
        entered on the command line, use it.  Otherwise use the media type
        that was specified in the properties table.
        
        Returns:
            str: The media type from the command line if present, otherwise 
                 the media type from the properties table.
        """
        propMedia = super().getMediaType()
        arg1 = util.getCommandLineArg(1)
        
        if propMedia is None and arg1 is None:
            media = None
        elif arg1 is None:
            media = propMedia
        else:
            media = arg1
        
        if media not in sysProps.MEDIA_TYPES:
            media = None
            
        return media
        
    def validate(self) -> bool:
        """Validate the configuration and generate any error messages.
        
        Return:
            bool: True if all validations pass, otherwise False.
        """
        ok = True

        if util.getCommandLineArg(1) in HELP_FLAGS:
            print(USAGE)
            ok = False
            
        else:
            ok = super().validateSmtp() and super().validateMedia();
            
        return ok
            
def updateMedia(mediaType: str) -> str:
    """Update the media usage in the DB.
    
    Args:
        mediaType (str): The type of media to update (tape | externalHD | CDRW | flash).
        
    Returns:
        str: Results of the update.
    """
    result = subprocess.run([ "java.exe", "-cp", "C:/Src/WEB-INF/lib/*", "com.ed.sysdocs.IncrementMedia", "C:/Src/sysdocs", mediaType ], capture_output=True, text=True)
    lines = result.stdout.split(NL)
    
    return lines[0]
    
def notify(config: Config, msg: str) -> bool:
    """Send the notification email
    
    Args:
        config (Config): Collected parameters for this run
        msg (str): Body of the email.
        
    Returns:
        bool: True if the message was sent successfully, otherwise False.
    """
    mail = sendMail.SendMail(config.getSmtpUrl(), config.getSmtpPort(), config.getSmtpAccount(), config.getSmtpPassword())
    
    return mail.send(config.getRecipientAdr(), "SysDocs " + config.getMediaType() + " backup complete", msg)
    
def mountDrive(volumeName: str, mediaPwd: str) -> str:
    """Mount a VeraCrypt drive and determine the new drive letter.
    
    Args:
        volumeName (str): Name assigned to the removable media.
        mediaPwd (str): Password used to access the removable media.
        
    Returns:
        str: The letter of the newly mounted drive or None if volume label 
             cannot be found.    
    """
    result = subprocess.run([ VERACRYPT_CMD, 
                              "/v", "\\Device\\Harddisk1\\Partition1", 
                              "/p", mediaPwd, 
                              "/q", "/s" ], capture_output=True, text=True)
    
    drives = wmi.WMI()
    driveId = None
    tries = 0
    
    while driveId is None and tries < 5:
        for drive in drives.Win32_LogicalDisk():
            if drive.VolumeName == volumeName:
                driveId = drive.Caption
        
        tries += 1
        
    return driveId
    
def unmountDrive(driveLetter: str) -> None:
    """Unmount the VeraCrypt drive.

    Args:
        driveLetter (str): Unmount this drive letter.
    """
    subprocess.run([ VERACRYPT_CMD, "/u", driveLetter, "/q", "/s" ], capture_output=False, text=True)

def runBackup(config: Config) -> bool:
    """Mount the removable media, backup to the removable media, then unmount the 
    removable media.
    
    Args:
        config (Config): Collected parameters for this run
        
    Returns:
        bool: True if the backup was completed successfully, otherwise False.
    """
    ok = True
    driveLetter = mountDrive(config.getVolumeName(), config.getMediaPwd())
    
    if driveLetter is None:
        print("Error: Unable to mount backup volume.")
        ok = False
    else:
        process = subprocess.Popen([ FFS_CMD, BACKUP_JOB ])
        process.wait()
        
        if process.returncode == 0:
            print("Backup completed successfully.")
        elif process.returncode == 1:
            print("Backup completed with warnings.  See log for details.")
        else:
            print("Backup completed with errors.  See log for details.")
            ok = False
            
        unmountDrive(driveLetter)
        
    return ok
    
def delOldLogs() -> None:
    """Delete old log files."""
    PREFIX_LEN: Final[int] = 4
    logs = os.listdir(LOG_DIR)
    logList = []
    grpPrefix = ""
    grpPos = 0
    
    # Generate a list of log files with their associated last modified datetime.
    # The lastModified value is made negative to sort in decending order.
    for  log in logs:
        fs = os.path.join(LOG_DIR, log)
        
        if os.path.isfile(fs):
            logList.append(DirEntry(log, -1 * os.path.getmtime(fs)))
            
    # Sort the list
    slogs = sorted(logList, key = lambda f: (f.fileName[:PREFIX_LEN], f.lastModified))
    
    # For each group, keep the first two and delete the rest until starting the next group
    for log in slogs:
        if grpPrefix == log.fileName[:PREFIX_LEN]:
            if grpPos > 1:
                os.remove(os.path.join(LOG_DIR, log.fileName))

            grpPos += 1
            
        else:
            grpPrefix = log.fileName[:PREFIX_LEN]
            grpPos = 1
            
def main() -> int:
    """Main program."""
    config = Config();
    rc = 0
    
    if config.validate() and runBackup(config):
        response = updateMedia(config.getMediaType())
        
        if (len(response)) <= 0:
            print("No " + config.getMediaType() + " media to update.")
            rc = 1
        elif notify(config, response):
            print("Message sent.")
        else:
            print("Message failed.")
            rc = 1
            
        delOldLogs()
    
    else:
        rc = 2
        
    return rc
    
if __name__ == '__main__':
    sys.exit(main())
