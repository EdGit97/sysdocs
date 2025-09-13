<!--
/*****************************************************************************
* Functions to support the sab.html page
*****************************************************************************/

/*****************************************************************************
*    Class: ShortCut
* Description: Object to hold parameters for a Windows shortcut
*****************************************************************************/
class ShortCut {
                      
    /*************************************************************************
    *    Function: constructor
    * Description: Setup an instance of SysDef object
    *   Arguments: tbName - Name of the toolbar
    *              fullName - File specification of the .lnk file
    *              clArgs - Command line arguments
    *              workingDirectory - Directory where the application will 
    *                                 be run
    *              targetPath - File specification of the command to run
    *              iconLocation - File specification of the shortcut icon
    *              hotkey - Key combination that will lauch the shortcut
    *              windowStyle - The style of window that will be created 
    *                            to run the application.  1-Normal, 
    *                            3-Maximized, 7-Minimized
    *              description - Display name of the shortcut
    *     Returns: Nothing
    *************************************************************************/
    constructor(tbName, fullName, clArgs, workingDirectory, targetPath,
                iconLocation, hotkey, windowStyle, description) {

        this.tbName = tbName;
        this.fullName = fullName;
        this.clArgs = clArgs;
        this.workingDirectory = workingDirectory;
        this.targetPath = targetPath;
        this.iconLocation = iconLocation;
        this.hotkey = hotkey;
        this.windowStyle = windowStyle;
        this.description = description;
        
    }
                
}

/*****************************************************************************
*    Function: showShortcuts
* Description: Display the list of toolbar shortcuts
*   Arguments: None
*     Returns: nothing
*****************************************************************************/
function showShortcuts() {
	function makeEntry(pnt, lblText, shortcutValue) {
		var lbl = document.createElement('label');

		pnt.appendChild(document.createElement('br'));

        lbl.setAttribute('class', 'shortcuts');
		lbl.appendChild(document.createTextNode(lblText + ':'));
		pnt.appendChild(lbl);

		pnt.appendChild(document.createTextNode(shortcutValue));

	}

	function getDisplayName(fileSpec) {
		var lastBsPos = fileSpec.lastIndexOf('\\');
		var displayName = fileSpec;

		if (lastBsPos >= 0) {
			displayName = displayName.substring(lastBsPos + 1)
		}

        return displayName.substring(0, displayName.length - 4);

	}

	function interpretWindowStyle(winStyle) {
		var winStat;

		switch (parseInt(winStyle)) {
		    case 3:
				winState = '3 - Maximized';
			    break;
			case 7:
				winState = '7 - Minimized';
			    break;
			default:
				winState = '1 - Normal window';
		}

		return winState;

	}

	function processIcon(iconVal) {

        return iconVal == ',0' ? 'Default' : iconVal;

	}

	var content = document.getElementById('contentDiv');
	var curName = null;
	var curP = null;

    for (var i = 0; i < shortcuts.length; i++) {
		if (curName != shortcuts[i].tbName) {
			curName = shortcuts[i].tbName;

			if (curP != null) {
				content.appendChild(curP);
			}

			curP = document.createElement('p');
			curP.setAttribute('class', 'shortcuts');

			var span = document.createElement('span');

            span.setAttribute('id', toId(curName));
			span.setAttribute('class', 'shortcuts');
			span.appendChild(document.createTextNode(curName));
			curP.appendChild(span);

		}

		makeEntry(curP, 'Display Name', getDisplayName(shortcuts[i].fullName));
		makeEntry(curP, 'Shortcut Filespec', shortcuts[i].fullName);
		makeEntry(curP, 'Target', shortcuts[i].targetPath + ' ' + shortcuts[i].clArgs);
		makeEntry(curP, 'Start in', shortcuts[i].workingDirectory);
		makeEntry(curP, 'Shortcut key', shortcuts[i].hotkey);
		makeEntry(curP, 'Run', interpretWindowStyle(shortcuts[i].windowStyle));
		makeEntry(curP, 'Comment', shortcuts[i].description);
		makeEntry(curP, 'Icon', processIcon(shortcuts[i].iconLocation));
		curP.appendChild(document.createElement('br'));

    }

    if (curP != null) {
		content.appendChild(curP);
    }

}
//-->
