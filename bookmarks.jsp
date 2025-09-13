<%@ page language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel='stylesheet' href='include/sysdocs.css' type='text/css'>
    <link rel='stylesheet' href='include/bookmarks.css' type='text/css'>
    <link rel="icon" href="images/win11.png" type="image/x-icon" />
	<script type='text/javascript' src='include/sysdocs.js'></script>
	<script type='text/javascript' src='include/bookmarks.js'></script>
	<script type='text/javascript'>
	    const ffbm = JSON.stringify(<%@ include file="include/ffbm.json" %>);
	</script>	
</head>
<body>
    <div class='main'>
	    <script type='text/javascript'>makePageHead(winSys, 'Firefox Bookmarks', homeLinks.both, true);</script>
		<div id='contentDiv' class='content'>
			<h2 id='install' name='toc'><img src='images/ff.png' alt='Firefox Icon' class='logo'>Installation &amp; Links</h2>
			<h3 id='install.installproc' name='toc'>Install Firefox</h3>
			<h4>Download &amp; Install</h4>
            <ol class='instructions' type='1'>
                <li>C&gt;curl -o C:\Src\setups\ffInstall.exe "https://download.mozilla.org/?product=firefox-latest&os=win64"</li>
                <li>C&gt;cd \Src\setups</li>
                <li>C&gt;ffInstall</li>
            </ol>
			<h4>After installation is complete</h4>
			</p>
            <ol class='instructions' type='1'>
                <li>Tools -&gt; Settings</li>
                <li>Home</li>
                <li>
                    Firefox Home Content:
                    <ol class='instructions' type='a'>
                        <li>Uncheck Web Search</li>
                        <li>Check Shortcuts, 3 rows</li>
                        <li>Check Weather</li>
                        <li>Uncheck Recommended stories</li>
                        <li>Uncheck Recent activity</li>
                    </ol>
                </li>
                <li>
                    Mark the first bookmark to be included in the list below:
                    <ol class='instructions' type='a'>
                        <li>Bookmarks -&gt; Manage Bookmarks</li>
                        <li>Select "Bookmarks Menu" in the sidebar.</li>
                        <li>Click on the first bookmark to be included in the list below.</li>
                        <li>
                            At the bottom of the screen: Tags=
	                        <script type='text/javascript'>
                                document.write(bmStartTag);
                            </script>
                        </li>
                        <li>Tab</li>
                        <li>Close the Library screen.</li>
                        <li>Run the Backup Bookmarks process below.</li>
                    </ol>
                </li>
            </ol>
			<h3 id='install.links' name='toc'>Links</h3>
			<ol class='instructions'>
				<li><a href='https://www.mozilla.org/en-US/firefox/' target='_blank'>Firefox Home Page</a></li>
				<li><a href='https://kb.mozillazine.org/Firefox_links' target='_blank'>Firefox Knowledge Base</a></li>
				<li><a href='https://support.mozilla.org/en-US/kb/keyboard-shortcuts-perform-firefox-tasks-quickly' target='_blank'>Firefox Shortcuts</a></li>
			</ol>
            <h2 id='load' name='toc'><img src='images/backup2.png' alt='Backup Icon' class='logo'>Bookmark Load</h2>
			<p class='instructions'>
				To update this list, in Firefox:
			</p>
			<ol class='instructions'>
			    <li>
				    Backup Bookmarks:
					<ol class='instructions' type='a'>
						<li>Ctrl+Shift+O to open the Manage Bookmarks window.</li>
						<li>Import and Backup -&gt; Backup...</li>
						<li>Directory: C:\Src\sysdocs\include</li>
						<li>File name: ffbm.json</li>
						<li>Save</li>
						<li>Close the Manage Bookmarks window.</li>
						<li>Refresh this page with Ctrl-F5.</li>
					</ol>
				</li>
				<li>
				    Restore Bookmarks:
					<ol class='instructions' type='a'>
						<li>Ctrl+Shift+O to open the Manage Bookmarks window.</li>
						<li>Import and Backup -&gt; Restore -&gt; Choose File...</li>
						<li>Select: C:\Src\sysdocs\include\ffbm.json</li>
						<li>Open</li>
						<li>OK</li>
					</ol>
				</li>
			</ol>
            <h2 id='list' name='toc'><img src='images/bm.png' alt='Bookmark Icon' class='logo'>Bookmark List</h2>
			<ul id='bmlvl1' class='instructions bmlvl0'>
			</ul>
			<script type='text/javascript'>
				showBookmarks();
				setContentHeight();
			</script>
		</div>
	</div>
</body>
</html>
