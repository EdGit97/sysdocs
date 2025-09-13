<!--
/*****************************************************************************
* Functions to support the sysdocs media page
*****************************************************************************/
const autoUpdateFlag = 'incMediaType=';

/**
 * Add a row to the list of media
 * @param mediaTypes A list of media types for the new media dropdown
 * @param mtRecSep Record separator for the mediaTypes list
 * @param mtFieldSep Field separator for the media types in the mediaType list
 * @return false
 */
function addRow(mediaTypes, mtRecSep, mtFieldSep) {
    function makeCol(clazz) {
	    var td = document.createElement('td');

        if (clazz != null) {
			td.setAttribute('class', clazz);
        }

		return td;

	}

    function makeHidden(nm, val) {
        var inp = document.createElement('input');

		inp.setAttribute('type', 'hidden');
		inp.setAttribute('name', nm);
		inp.setAttribute('value', val);

		return inp;

	}

    function makeCheckbox(nm, checked) {
        var inp = document.createElement('input');

		inp.setAttribute('type', 'checkbox');
		inp.setAttribute('name', nm);
		inp.checked = checked;

		return inp;

	}
    
    function makeMediaDD() {
        var sel = document.createElement('select');
        var mtl = mediaTypes.split(mtRecSep);
        var opt = document.createElement('option');
        
        sel.setAttribute('name', 'MediaType');
        opt.value = ' ';
        sel.appendChild(opt);
        
        
        for (var i = 0; i < mtl.length; i++) {
            var vals = mtl[i].split(mtFieldSep);
            
            opt = document.createElement('option');
            opt.value = vals[0];
            opt.text = vals[1];
            sel.appendChild(opt);
        }
        
        return sel;
        
    }

	var tbl = document.getElementById('mediaList');
	var tr = document.createElement('tr');
	var td = makeCol('mediaC');
    var inp = document.createElement('input');

    // Media ID column
    inp.setAttribute('type', 'text');
	inp.setAttribute('name', 'MediaId');
	inp.setAttribute('maxlength', 1);
	inp.setAttribute('size', 1);
	inp.setAttribute('style', 'text-transform: uppercase;');
	inp.setAttribute('onchange', 'javascript: changeId(this);');
	td.appendChild(inp);
	tr.appendChild(td);
    
    // Media Type column
	td = makeCol('mediaC');
    td.appendChild(makeMediaDD());
	tr.appendChild(td);

    // First Used column
	td = makeCol('mediaC');
	td.appendChild(makeHidden('FirstUse', ''));
	td.appendChild(document.createTextNode('New'));
	tr.appendChild(td);

    // Last Used column
	td = makeCol('mediaC');
	td.appendChild(makeHidden('LastUse', ''));
	td.appendChild(document.createTextNode('New'));
	tr.appendChild(td);

    // Usage Count column
	td = makeCol('mediaR');
	td.appendChild(makeHidden('UseCount', '0'));
	td.appendChild(document.createTextNode('0'));
	tr.appendChild(td);

    // Active column
	td = makeCol('mediaC');
	td.appendChild(makeCheckbox('Active', true));
	td.appendChild(makeHidden('Updated', 'I'));
	tr.appendChild(td);

    // Delete column
	td = makeCol('mediaC');
	td.appendChild(makeCheckbox('Delete', false));
	tr.appendChild(td);

	tbl.appendChild(tr);
    inp.focus();

	return false;

}

/**
 * Confirm that the user is ready to save
 * @return true if the user confirms saving, otherwise false
 */
function readyToSave() {
    var deletes = document.getElementsByName('Delete');
    var deleting = false;
	var ok = true;

	for (var i = 0; i < deletes.length && !deleting; i++) {
		deleting = deletes[i].checked;
	}

	if (deleting) {
        ok = confirm('Records are marked for deletion.  Deleted records cannot be recovered.  Continue?');
	}
    
	return ok;

}

/**
 * Record that the media data has been changed
 * @param inpCb - A checkbox
 * @return false
 */
function mediaChanged(inpCb) {
    
    if (inpCb.nextElementSibling.value == 'U') {
        inpCb.nextElementSibling.value = 'M';
    }
    
    return false;
    
}

/**
 * When a media ID is updated, modify the value of the associated checkboxs
 * with the value contained in the textbox
 * @param idTb The media ID textbox element
 * @return false
 */
function changeId(idTb) {
    var pnt = idTb.parentElement;

	// Navigate to the associated active checkbox
	var cbTd = pnt.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling;
	var cb = cbTd.getElementsByTagName('input')[0];

	cb.setAttribute('value', idTb.value.toUpperCase());

	// Navigate to the associated deleted checkbox
	cbTd = cbTd.nextElementSibling;
	cb = cbTd.getElementsByTagName('input')[0];
	cb.setAttribute('value', idTb.value.toUpperCase());

	return false;

}

/**
 * Display the media maximums popup
 * @param show true to display the popup, false to hide
 * @returns false
 */
function maxValues(show) {
    var inputDiv = document.getElementById('maxPopup');

    if (show) {
        inputDiv.style.display = 'block';
        document.getElementsByName('MaxUse')[0].focus();
    }
    else {
        inputDiv.style.display = 'none';
    }
    
    return false;

}

/*****************************************************************************
*    Function: savedNotification
* Description: Display a notification that the media maximums were saved
*   Arguments: None
*     Returns: nothing
*****************************************************************************/
function savedNotification() {
    
    if (location.search.indexOf('saved=true') >= 0) {
        alert('Media maximums saved.');
    }
    
}

/*****************************************************************************
*    Function: usageWarning
* Description: Display a warning if a backup media has exceeded its maximum usage
*   Arguments: warningMessage - The message to display
*     Returns: nothing
*****************************************************************************/
function usageWarning(warningMessage) {

	if (location.search.indexOf(autoUpdateFlag) < 0 && warningMessage.length > 0) {
		alert(warningMessage);
	}

}

/*****************************************************************************
*    Function: autoRefresh
* Description: Set the time to auto-refresh the page
*   Arguments: None
*     Returns: Nothing
*****************************************************************************/
function autoRefresh() {
    function startTimeHour(startTime) {
        return parseInt(startTime.split(":")[0]);
    }
    
    function startTimeMin(startTime) {
        return parseInt(startTime.split(":")[1]);
    }
    
    function sortTimes() {
        var timeCells = document.getElementsByName("stime");
        var times = [];
        
        for (var i = 0; i < timeCells.length; i++) {
            times.push(timeCells[i].textContent);
        }
        
        return times.sort();
        
    }
    
    var times = sortTimes();
    var now = new Date();
    var pst = new Date();
    var rtime = null;
    
    pst.setSeconds(0);
    pst.setMilliseconds(0);
    
    for (var i = 0; i < times.length && rtime == null; i++) {
        pst.setHours(startTimeHour(times[i]));
        pst.setMinutes(startTimeMin(times[i]));
                           
        if (pst > now) {
            rtime = pst;
        }
                           
    }
    
    if (rtime != null) {
        var hd = document.getElementsByTagName('head')[0];
        var meta = document.createElement('meta');
        var interval = Math.trunc((rtime.getTime() - now.getTime()) / 1000) + 120;
        
        meta.setAttribute('http-equiv', 'refresh');
        meta.setAttribute('content', interval.toString());
        hd.appendChild(meta);
        
    }        
    
}

/*****************************************************************************
*    Function: showLog
* Description: Display a log file in a popup
*   Arguments: logUrl - URL of the log file to display
*     Returns: false
*****************************************************************************/
function showLog(logUrl) {
    var logDiv = document.getElementById('logPopup');
    var title = document.getElementById('logFn');
    var content = document.getElementById('logContent');
    
    if (title.firstChild != null) {
        title.removeChild(title.firstChild);
    }
    
    title.appendChild(document.createTextNode(logUrl.replaceAll('%20', ' ').replaceAll('%5B', '[').replaceAll('%5D', ']')));
    content.setAttribute('data', logUrl);

    logDiv.style.display = 'block';
    content.focus();
    
    return false;

}

/*****************************************************************************
*    Function: hideLog
* Description: Hide the log popup display
*   Arguments: None
*     Returns: false
*****************************************************************************/
function hideLog() {
    document.getElementById('logPopup').style.display = 'none';
    return false;
}
//-->
