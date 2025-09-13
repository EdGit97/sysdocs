<%@ page language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel='stylesheet' href='include/sysdocs.css' type='text/css'>
    <link rel='stylesheet' href='include/widjit.css' type='text/css'>
    <link rel="icon" href="images/win11.png" type="image/x-icon" />
	<script type='text/javascript' src='include/sysdocs.js'></script>
	<script type='text/javascript' src='include/bookmarks.js'></script>
	<script type='text/javascript' src='include/widjits.js'></script>
	<script type='text/javascript' src='include/sab.js'></script>
	<script type='text/javascript' src='include/shortcuts.js'></script>
	<script type='text/javascript'>
	    const ffbm = JSON.stringify(<%@ include file="include/ffbm.json" %>);
	</script>	
</head>
<body>
    <div class='main'>
	    <script type='text/javascript'>makePageHead(winSys, null, homeLinks.index, false);</script>
		<div id='contentDiv' class='content'>
			<script type='text/javascript'>
				setContentHeight();
			</script>
			<div class='widjit' tabindex='10' autofocus onclick='location.href="bookmarks.jsp";' onkeyup='javascript: handleKeyUp(this);'>
				<p id='ffb' name='toc' class='widjitTitle'><img src='images/ff.png' alt='Firefox Logo' class='logo'>Firefox Installation &amp; Bookmarks</p>
				<p class='widjitContent'>
					List of current bookmarks and associated parameters in the Firefox
					browser.
				</p>
				<table class='widjitContent'>
                    <tr>
                        <td class='widjitContents'>Contents:</td>
                        <td id='bookmarks'>
                            <script type='text/javascript'>
                                loadTocFromPage('bookmarks', 'bookmarks.jsp', function() { makeBookmarkToc('bookmarks', 'bookmarks.jsp') });
                            </script>
                        </td>
                    </tr>
                </table>
			</div>
			<div class='widjit' tabindex='20' onclick='location.href="sab.html";' onkeyup='javascript: handleKeyUp(this);'>
				<p id='sab' name='toc' class='widjitTitle'><img src='images/clover.png' alt='StartAllBack Logo' class='logo'>StartAllBack Installation &amp; Shortcuts</p>
				<p class='widjitContent'>
					Instructions for configuring StartAllBack and toolbar/taskbar shortcuts.
				</p>
				<p id='sabToc' class='widjitContent'>
				    Contents:
					<script type='text/javascript'>
						loadTocFromPage('sabToc', 'sab.html', function() { makeShortCutToc('sabToc', 'sab.html') });
					</script>
				</p>
			</div>
			<!-- div class='widjit' tabindex='20' onclick='location.href="emclient.html";' onkeyup='javascript: handleKeyUp(this);'>
				<p id='emc' name='toc' class='widjitTitle'><img src='images/emc.png' alt='emClient Logo' class='logo'>eM Client Installation</p>
				<p class='widjitContent'>
					Instructions for installing and configuring eM Client.
				</p>
			</div -->
			<div class='widjit' tabindex='30' onclick='location.href="tbird.html";' onkeyup='javascript: handleKeyUp(this);'>
				<p id='tb' name='toc' class='widjitTitle'><img src='images/tblogo.png' alt='Thunderbird Logo' class='logo'>Thunderbird Installation</p>
				<p class='widjitContent'>
					Instructions for installing and configuring Thunderbird.
				</p>
				<p id='tbird' class='widjitContent'>
				    Contents:
					<script type='text/javascript'>
						loadTocFromPage('tbird', 'tbird.html');
					</script>
				</p>
			</div>
			<div class='widjit' tabindex='40' onclick='location.href="backups.jsp";' onkeyup='javascript: handleKeyUp(this);'>
				<p id='backupOps' name='toc' class='widjitTitle'><img src='images/backup.png' alt='Backup Logo' class='logo'>Backup Operations</p>
				<p class='widjitContent'>
					Instructions for configuring and performing backups and restores.
				</p>
				<table class='widjitContent'>
                    <tr>
                        <td class='widjitContents'>Contents:</td>
                        <td id='backup'>
                            <script type='text/javascript'>
                                loadTocFromPage('backup', 'backups.jsp');
                            </script>
                        </td>
                    </tr>
                </table>
			</div>
			<div class='widjit' tabindex='50' onclick='location.href="notes.html";' onkeyup='javascript: handleKeyUp(this);'>
				<p id='noteOps' name='toc' class='widjitTitle'><img src='images/notes.png' alt='Notes Logo' class='logo'>Notes</p>
				<p class='widjitContent'>
					Miscellaneous information about this Windows system and site setup.
				</p>
				<p id='notes' class='widjitContent'>
				    Contents:
					<script type='text/javascript'>
						loadTocFromPage('notes', 'notes.html');
					</script>
				</p>
			</div>
		</div>
	</div>
</body>
</html>
