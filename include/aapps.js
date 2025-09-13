<!--
/*****************************************************************************
* Functions to support the aapps.html page
*****************************************************************************/

/*****************************************************************************
*    Function: showAdrApps
* Description: Display the list of Android applications.  List is sorted and 
*              grouped by first letter of the application name.
*   Arguments: None
*     Returns: nothing
*****************************************************************************/
function showAdrApps() {
	var appList = document.getElementById('appList');
	var apps = androidApps.sort();
	var firstLetter = null;
	var li = null;

    for (var i = 0; i < apps.length; i++) {
        if (firstLetter == null || firstLetter != apps[i].substring(0, 1)) {
			if (li != null) {
				appList.appendChild(li);
			}

			li = document.createElement('li');
			firstLetter = apps[i].substring(0, 1);

        }

		if (li.textContent.length > 0) {
			li.appendChild(document.createTextNode(', '));
		}

		li.appendChild(document.createTextNode(apps[i]));

    }

}
//-->
