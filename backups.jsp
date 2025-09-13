<%@ page language="java" %>
<%@ page import="com.ed.sysdocs.SysConstants" %>
<%@ page import="com.ed.sysdocs.SysDocsUI" %>
<%@ page import="com.ed.sysdocs.MediaTypes" %>
<%
    SysDocsUI sd = new SysDocsUI(request);
%>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel='stylesheet' href='include/sysdocs.css' type='text/css'>
    <link rel='stylesheet' href='include/backups.css' type='text/css'>
    <link rel="icon" href="images/win11.png" type="image/x-icon" />
	<script type='text/javascript' src='include/sysdocs.js'></script>
	<script type='text/javascript' src='include/media.js'></script>
</head>
<body>
    <div class='main'>
	    <script type='text/javascript'>makePageHead(winSys, 'Backup Operations', homeLinks.both, true);</script>
		<div id='contentDiv' class='content'>
			<script type='text/javascript'>
				setContentHeight();
			</script>
			<h2 id='Google' name='toc'><img src='images/gdrive.png' alt='Google Drive Logo' class='logo'>Google Drive/GoogleOne</h2>
			<h3 id='Google.plan' name='toc'>Plan Maintenance</h3>
			<ol class='instructions'>
				<li>Go to <a href='https://play.google.com/' target='_blank'>Google Play</a></li>
				<li>Click account "Ed" on the upper right.</li>
				<li>Payments &amp; Subscriptions</li>
				<li>Subscriptions tab</li>
				<li>GoogleOne -&gt; Manage</li>
				<li>Currently have the Premium 2TB, $9.99/month.</li>
				<li>Payments and cancellation can be managed here.</li>
			</ol>
			<h3 id='Google.config' name='toc'>Configuration</h3>
			<ol class='instructions'>
				<li>Log in as ed9707@gmail.com</li>
				<li>
					In <a href='https://drive.google.com/drive/my-drive' target='_blank'>My Drive</a>, create:
					<ul class='instructions'>
						<li>A new folder: Ed-HP2</li>
						<li>
							readme.txt in the Ed-HP2 folder with the following content:
							<div class='readme'>Restore instructions located in /Src/sysdocs/recover.txt</div>
						</li>
					</ul>
				</li>
			</ol>
			<h2 id='VeraCrypt' name='toc'><img src='images/VeraCrypt.png' alt='VeraCrypt Logo' class='logo'>VeraCrypt</h2>
			<h3 id='VeraCrypt.install' name='toc'>Installation</h3>
			<ol class='instructions'>
                <li>Download from <a href='https://www.veracrypt.fr/en/Downloads.html' target='_blank'>https://www.veracrypt.fr/en/Downloads.html</a></li>
                <li>Install using \Src\setups\VeraCrypt_Setup_x64_1.26.20.msi</li>
            </ol>
			<h3 id='VeraCrypt.preparation' name='toc'>Media Preparation</h3>
			<p class='instructions'>
                Format a new VeraCrypt Flash Drive Partition. Any media should 
                have a capacity of at least 60GB. Setup each drive as follows:
			</p>
			<ol class='instructions'>
                <li>
                    Wipe the drive (Recommend performing this operation on the test laptop):
                    <ol class='instructions' type='a'>
                        <li>Plug the flash drive into the appropriate USB port.</li>
                        <li>Open an Administrator Command Prompt.</li>
                        <li>C&gt;diskpart</li>
                        <li>DISKPART&gt; list disk</li>
                        <li>
                            DISKPART&gt; select disk &lt;x&gt;<br>
                            Where &lt;x&gt; is the "Disk ###" from the table 
                            generated the previous step.
                        </li>
                        <li>
                            DISKPART&gt; clean all<br>
                            This step will take a number of hours.
                        </li>
                        <li>DISKPART&gt; create partition primary</li>
                        <li>DISKPART&gt; exit</li>
                        <li>C&gt;exit</li>
                    </ol>
                </li>
                <li>
                    Format the drive:
                    <ol class='instructions' type='a'>
                        <li>Open VeraCrypt</li>
                        <li>Create Volume</li>
                        <li>Encrypt a non-system partition/drive</li>
                        <li>Next</li>
                        <li>Standard VeraCrypt volume</li>
                        <li>Next</li>
                        <li>
                            Volume Location:
                            <ol class='instructions' type='i'>
                                <li>Select Device...</li>
                                <li>
                                    Under "Removable Disk 1:" select "\Device\Harddisk1\Partition1 X:"<br>
                                    Where X: is the drive letter that was assigned 
                                    to the USB device when it was plugged in.
                                </li>
                                <li>OK</li>
                            </ol>
                        </li> 
                        <li>Next</li>
                        <li>Volume Creation Mode: Create encrypted volume and format it</li>
                        <li>Next</li>
                        <li>Encryption Algorithm: AES</li>
                        <li>Hash Algorithm: SHA-512</li>
                        <li>Next</li>
                        <li>Volume Size: Size of the partition</li>
                        <li>Next</li>
                        <li>Volume Password: Fill in a password</li>
                        <li>Next</li>
                        <li>
                            Large Files: Do you intend to store files larger than 
                            4GB in this VeraCrypt volume? Yes
                        </li>
                        <li>Next</li>
                        <li>
                            Volume Format:
                            <ol class='instructions' type='i'>
                                <li>Filesystem: exFAT</li>
                                <li>
                                    Move the mouse around until the "Randomness Collected
                                    From Mouse Movements" bar turns green.
                                </li>
                            </ol>
                        </li>
                        <li>Format</li>
                    </ol>
                </li>
                <li>
                    Once the VeraCrypt format has completed:
                    <ol class='instructions' type='a'>
                        <li>
                            C&gt;"C:\Program Files\VeraCrypt\VeraCrypt.exe" /v \Device\Harddisk1\Partition1 /p &lt;password entered during format&gt; /m label=BKUP /q /s<br>
                            It may take a few seconds for the VeraCrypt volume to 
                            connect.  Open Windows Explorer and note when the BKUP
                            volume is assigned a drive letter.
                        </li>
                        <li>
                            Using the drive letter from the previous step &lt;dl&gt;,
                            create readme.txt in the root directory of the newly created 
                            volume:<br>
                            <div class='readme'>C&gt;echo Restore instructions located in \Src\sysdocs\recover.txt &gt; &lt;dl&gt;:\readme.txt</div>
                        </li>
                        <li>C&gt;"C:\Program Files\VeraCrypt\VeraCrypt.exe" /u /q /s</li>
                        <li>C&gt;exit</li>
                    </ol>
				</li>
				<li>Record any backup media in the table below.</li>
			</ol>
            <div class='mediaMax' id='maxPopup'>
                <h2 class='mediaMax'>Backup Media Settings</h2>
                <form id='maximums' method='post' action='backups.jsp#media'>
                    <input type='hidden' name='mediaMaximums' value='Y'>
                    <p class='instructions'>
                        Enter the maximum number of times each of the following
                        media types should be used before being replaced.
                    </p>
                    <p class='mediaList'>
                        <%=sd.editMediaMaximums() %>
                    </p>
                    <div class='media2'>
                        <input type='submit' class='mmButton' value='Save'><br>
                        <button class='mmButton' onclick='javascript: return maxValues(false);'>Close</button>
                    </div>
                    <p class='instructions' style='clear: left;'>
                        To generate a new media type, add a new value to the 
                        com.ed.sysdocs.MediaTypes enum and rebuild.                    
                    </p>
                    <script type='text/javascript'>
                        savedNotification();
                    </script>
                </form>
            </div>
			<form id='media' method='post' action='backups.jsp#media'>
                <input type='hidden' name='mediaList' value='Y'>
				<div class='errors'>
				    <%=sd.showErrors() %>
				</div>
				<table id='mediaList' class='media'>
					<tr>
					    <th>Media ID</th>
					    <th>Media Type<sup>1</sup></th>
					    <th>First Used</th>
					    <th>Last Used</th>
					    <th>Usage Count</th>
					    <th>Active</th>
					    <th>Delete</th>
					</tr>
					<%=sd.showMedia() %>
					<tfoot>
					    <tr><td><hr class='foot'></td><td colspan='6'>&nbsp;</td></tr>
					    <tr>
                            <td colspan='7'>
                               <sup>1</sup>Listed Media Types:<br>
                                <%=sd.showMediaMaximums() %>
                            </td>
                        </tr>
					</tfoot>
				</table>
				<div class='media'>
					<button onclick='javascript: return addRow("<%=MediaTypes.listMediaTypes() %>", "<%=SysConstants.recSep %>", "<%=MediaTypes.typeSep %>");'>Add</button>
				    <button onclick='javascript: return maxValues(true);'>Settings</button>
					<input type='submit' value='Save' onclick='javascript: return readyToSave();'>
				</div>
                <p class='instructions'>
                    Once any media reaches the end of its useful life, wipe the 
                    media by repeating step 1 of the <a href='#VeraCrypt.preparation'>Media Preparation</a> 
                    instructions before any reallocation of the device.
                </p>
				<script type='text/javascript'>
					usageWarning('<%=sd.usageNotification() %>');
				</script>
			</form>
			<h2 id='FreeFileSync' name='toc'><img src='images/ffs.png' alt='FreeFileSync Logo' class='logo'>FreeFileSync</h2>
			<h3 id='FreeFileSync.install' name='toc'>Installation</h3>
			<ol class='instructions'>
                <li>Download from <a href='https://freefilesync.org/download.php' target='_blank'>https://freefilesync.org/download.php</a></li>
                <li>Install using \Src\setups\FreeFileSync_14.2_Windows_Setup.exe</li>
            </ol>
			<h3 id='FreeFileSync.ops' name='toc'>Operations</h3>
			<h4>Scheduled Operations</h4>
			<p class='instructions'>
                Scheduled backup jobs are initiated by the Windows Task Scheduler.
                To setup the jobs in the Task Scheduler:
            </p>
			<ol class='instructions'>
                <li>C&gt;cd\Src\sysdocs\generate</li>
                <li>C&gt;py <a href='scriptDoc/genSched.txt' target='_blank'>genSched.py</a></li>
                <li>
                    Open the Window Task Scheduler: Start -> Windows Tools -> Task Scheduler
                    <ol class='instructions' type='a'>
                        <li>Task Scheduler Library -&gt; ffsb</li>
                        <li>
                            For each task in the ffsb folder:
                            <ol class='instructions' type='i'>
                                <li>Context Menu -> Properties</li>
                                <li>Conditions Tab</li>
                                <li>Uncheck "Start the task only if the computer is on AC power"</li>
                                <li>OK</li>
                            </ol>
                        </li>
                        <li>Close the Task Scheduler</li>
                    </ol>
                </li>
            </ol>
			<p class='instructions'>
				Once the tasks are loaded in to the Windows Task Scheduler, FreeFileSync
                will automatically upload any changes to Google Drive once a day for each
                task in the "Task Scheduler Library\ffsb" folder.
			</p>
			<table class='instructions tasks'>
				<caption class='jobs'>Summary of Scheduled Tasks</caption>
				<tr><th>Task Name</th><th>Run Time</th><th>Last Run</th><th>Backup Command</th></tr>
                <%=sd.buildScheduledTaskTbl() %>
			</table>
			<h4 id='mofd'>Manual Operations to Flash Drive</h4>
			<ol class='instructions'>
				<li>
					Plug flash drive <%=sd.nextMediaId(MediaTypes.flash) %>, as noted in 
					<a href='#media'>media table</a> above, into an appropriate USB
                    port on the Ed-HP2 laptop.
				</li>
				<li>C&gt;cd\Src\sysdocs\generate</li>
				<li>C&gt;py <a href='scriptDoc/localBackup.txt' target='_blank'>localBackup.py</a></li>
				<li>
					After the job completes:
					<ol class='instructions' type='a'>
						<li>Remove the flash drive from the USB port.</li>
                        <li>Press F5 to update the flash drive letter in step 1.</li>
                        <li>Review the <%=sd.getLatestLocalLog() %>.</li>
					</ol>
				</li>
			</ol>
            <p class='instructions'>
                To view the contents of a VeraCrypt encrypted USB drive, use 
                the mounting procedure described in the <a href='#Restore'>Restore Process</a> section 
                below.
            </p>
            <div class='logDisplay' id='logPopup'>
                <button class='logClose' onclick='javascript: hideLog()'>X</button>
                <h3 id='logFn' class='logFn'></h3>
                <object class='logContent' id='logContent'></object>
            </div>
			<h2 id='Restore' name='toc'><img src='images/download.png' alt='Download Icon' class='logo'>Restore Process</h2>
			<p class='instructions'>
				The documention in this section is available in /Src/sysdocs/recover.txt 
                on all backup media that is referenced on this page.
			</p>
			<object data='recover.txt' type='text/plain' width='95%' height='480'></object>
			<h2 id='Tech' name='toc'><img src='images/tech.png' alt='Technical Icon' class='logo'>Technical</h2>
			<p class='instructions'>
                Backup media, associated usage counts and access to the backup 
                logs are maintained on this page by the SysDocs project. Data 
                is stored in DBF files that are accessed via the xBaseJ library.  
                After a backup to external media is performed, the system will 
                automatically update the usage statistics so that the 
                <a href='#mofd'>Manual Operations to Flash Drive</a> always 
                displays the next external media device to use.  See the 
                <a href='#Links'>Links</a> section below for more information 
                about the SysDocs project and xBaseJ library.
			</p>
			<p class='instructions' style='clear: left;'>
                xBaseJ was installed by copying the JAR files from the lib 
                directory of C:\Src\setups\xBaseJ-20090922.bin.zip to the 
                C:\Src\WEB-INF\lib directory.
			</p>
			<h4>DBF Tables used by this page</h4>
            <pre class='metadata' style='float: left;'><%=sd.showMediaMetaData() %><%=sd.structureSpace() %><%=sd.showPropertiesMetaData() %></pre>
            <pre class='metadata' style='float: right;'><%=sd.showMediaMaxMetaData() %></pre>
			<p class='instructions' style='clear: left;'>
                Note: To rebuild any indexes that have  become corrupted, delete 
                the corrupted index file(s) and refresh this page.
			</p>
			<p class='instructions' style='clear: left;'>
                To view the raw data, use the xBaseJ dbfViewer.<br>
			</p>
            <p class='code'>
                C:\Src\sysdocs\data&gt;java -cp "\Src\WEB-INF\lib\*" org.xBaseJ.swing.dbfViewer media.dbf media1.ndx<br>
                C:\Src\sysdocs\data&gt;java -cp "\Src\WEB-INF\lib\*" org.xBaseJ.swing.dbfViewer mediamax.dbf mediamax.ndx<br>
                C:\Src\sysdocs\data&gt;java -cp "\Src\WEB-INF\lib\*" org.xBaseJ.swing.dbfViewer properties.dbf properties.ndx
            </p>
			<h4 style='margin-bottom: 0;'>Properties Maintenance</h4>
			<form id='props' method='post' action='backups.jsp#Links'>
                <ul class='errors'><%=sd.showPropertyErrors() %></ul>
                <table id='propList' class='properties'>
                    <tbody>
                        <%=sd.showProperties() %>
                    </tbody>
                </table>
				<div class='properties'>
					<input type='submit' value='Save'>
				</div>
            </form>
			<h2 id='Links' name='toc'><img src='images/links.png' alt='Links Icon' class='logo'>Links</h2>
			<ul class='instructions'>
                <li><a href='https://www.veracrypt.fr/en/Home.html' target='_blank'>VeraCrypt Home Page</a></li>
                <li><a href='https://freefilesync.org/' target='_blank'>FreeFileSync Home Page</a></li>
                <li><a href='javadoc/index.html' target='_blank'>SysDocs Javadoc</a>
				<li><a href='https://xbasej.sourceforge.net/' target='_blank'>xBaseJ Summary</a></li>
				<li><a href='https://sourceforge.net/projects/xbasej/files/XBaseJ-Binaries/' target='_blank'>xBaseJ Downloadable Binaries</a></li>
				<li><a href='https://xbasej.sourceforge.net/api/overview-summary.html' target='_blank'>xBaseJ Javadoc</a></li>
			</ul>
		</div>
	</div>
	<script type='text/javascript'>
        autoRefresh();
    </script>
</body>
</html>
