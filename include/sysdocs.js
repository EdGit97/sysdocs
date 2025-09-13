<!--
/*****************************************************************************
* Functions to support the sysdocs site
*****************************************************************************/
const homeLinks = {
		none: 0,
		index: 1,
		system: 2,
		both: 3
	  }
const borderSetting = '--wBorderColor';
const winBorder = '#1E90FF';
const adrBorder = '#3ddc84';
const nookBorder = '#b2bc36';
const linkSysBorder = '#b83347';
const laserJetBorder = '#1F3485';

/*****************************************************************************
*    Class: SysDef
* Description: Definition of a system
*****************************************************************************/
class SysDef {
    
    /*************************************************************************
    *    Function: constructor
    * Description: Setup an instance of SysDef object
    *   Arguments: sysTitle - Main title that will appear on pages that document
    *                         a particular system
    *              sysHomeName - Label to use for the link to the system page
    *              sysHomeLink - URL for the system page
    *              bdrColor - Color of the widgit border for this page
    *     Returns: Nothing
    *************************************************************************/
    constructor(sysTitle, sysHomeName, sysHomeLink, bdrColor) {

        if (sysTitle != null && sysTitle.length > 0) {
            this.sysTitle = sysTitle + ' ';
        }
        else {
            this.sysTitle = '';
        }

        this.sysTitle += 'System Documentation';
        this.sysHomeName = sysHomeName;
        this.sysHomeLink = sysHomeLink;
        this.sysBorder = bdrColor === undefined ? null : bdrColor;
        
    }

}

var rootSys = new SysDef(null, 'Home', 'index.html');
var winSys = new SysDef('Windows', 'Windows Home', 'sysdocs.jsp', winBorder);
var testSys = new SysDef('Windows Test', 'Windows Test Home', 'winTest.html', winBorder);
var androidSys = new SysDef('Android', 'Android Home', 'androidDocs.html', adrBorder);
var nookSys = new SysDef('Nook', 'Nook Home', 'nookDocs.html', nookBorder);
var linkSys = new SysDef('LinkSys Router', 'LinkSys Home', 'router.html', linkSysBorder);
var p1102w = new SysDef('HP LaserJet P1102w', 'HP LaserJet Home', 'p1100.html', laserJetBorder);

/*****************************************************************************
*    Function: setContentHeight
* Description: Set the height of the content section
*   Arguments: None
*     Returns: Nothing
*****************************************************************************/
function setContentHeight() {
	var wh = "innerHeight" in window ? window.innerHeight : document.documentElement.offsetHeight;
	var div = document.getElementById('contentDiv');
	var ch = wh - div.offsetTop - 20;

	div.style.maxHeight = ch + 'px';

}

/*****************************************************************************
*    Function: handleKeyUp
* Description: If a key is pressed on a widjit, examine the key and take 
*              appropriate action
*   Arguments: srcElt - The widjit that had focus when the key was pressed
*     Returns: nothing
*****************************************************************************/
function handleKeyUp(srcElt) {

    if (event.key == 'Enter') {
		srcElt.click();
	}

}

/*****************************************************************************
*    Function: makePageHead
* Description: Create the page header
*   Arguments: sysdef - System definition to use for this page
*              subtitle - Title that will appear after the main title in the 
*                         page header
*              homeLink - Links to include in the header.  See homeLinks
*              separator - true to include a horizonal ruke
*     Returns: nothing
*****************************************************************************/
function makePageHead(sysdef, subtitle, homeLink, separator) {
	function makeLink(sysdef) {
		var a = document.createElement('a');

		a.setAttribute('href', sysdef.sysHomeLink);
		a.setAttribute('class', 'titleLink');
		a.setAttribute('tabindex', '1');
		a.appendChild(document.createTextNode('[' + sysdef.sysHomeName + ']'));

		return a;

	}

	var titleElt = document.getElementsByTagName('title')[0];
	var pnt = document.getElementsByClassName('main')[0];
	var title = sysdef.sysTitle;
	var p = document.createElement('p');

	p.setAttribute('class', 'title');

	if (subtitle != null && subtitle.length > 0) {
		titleElt.appendChild(document.createTextNode(subtitle + ' - ' + title));
		p.appendChild(document.createTextNode(title + ' - ' + subtitle));
	}
	else {
		titleElt.appendChild(document.createTextNode(title));
		p.appendChild(document.createTextNode(title));
	}

	if (homeLink != homeLinks.none) {
		p.appendChild(document.createElement('br'));
	}

	if (homeLink == homeLinks.both) {
		p.appendChild(makeLink(rootSys));
		p.appendChild(document.createTextNode(' '));
		p.appendChild(makeLink(sysdef));
	}
	else if (homeLink == homeLinks.system) {
		p.appendChild(makeLink(sysdef));
	}
	else if (homeLink == homeLinks.index) {
		p.appendChild(makeLink(rootSys));
	}

	if (separator) {
		p.appendChild(document.createElement('hr'));
	}

	pnt.appendChild(p);
    
    if (sysdef.sysBorder != null) {
        document.querySelector(':root').style.setProperty(borderSetting, sysdef.sysBorder);
    }

}

/*****************************************************************************
*    Function: toId
* Description: Remove spaces from a string so that it can act as an ID value
*   Arguments: stg - The orginal string
*     Returns: stg with all spaces removed
*****************************************************************************/
function toId(stg) {

    return stg.replaceAll(' ', '');

}
//-->
