<!--
/*****************************************************************************
* Functions to support the table of contents pages
*****************************************************************************/
const tocGroupStart = ': [';
const tocGroupEnd = ']';

/*****************************************************************************
*    Function: widjitTargets
* Description: Process a page for TOC information and generate links directly
*              to the child pages of the processed page.
*   Arguments: cid - ID of the element that will hold the TOC entries
*              url - URL of the page to process
*     Returns: nothing
*****************************************************************************/
function widjitTargets(cid, url) {

    function makeTargets(html) {
		var parser = new DOMParser();
		var dom = parser.parseFromString(html, 'text/html');
        var tocElts = dom.getElementsByName('toc');

        for (var i = 0; i < tocElts.length; i++) {
            var onclickVal = tocElts[i].parentElement.getAttribute('onclick');
            var destUrl = onclickVal.replace(/"/g, '').replace(';', '').split('=')[1];
            var a = document.createElement('a');
            
            if (i > 0) {
                tocPnt.appendChild(document.createTextNode(' | '));
            }
            
            a.setAttribute('href', destUrl);
            a.setAttribute('class', 'widjitLink');
            a.appendChild(document.createTextNode(tocElts[i].textContent));
            tocPnt.appendChild(a);
            
        }
					
	}
    
    function widjitResponse() {

        if (request.readyState == 4) {
            if (request.status == 200) {
				makeTargets(request.responseText);
			}

        }

	}

	var request = new XMLHttpRequest();
	var tocPnt = document.getElementById(cid);

	request.onreadystatechange = widjitResponse;
	request.open('GET', url, true);
	request.send();
    
}

/*****************************************************************************
*    Function: loadTocFromPage
* Description: Process a page for Table of Contents (TOC) information.  To be
*              included in the TOC, an element must have the following 
*              attributes:
*                  1. name='toc'
*                  2. id=<some value>.  If the element is a child of another 
*                     element, the id must start with the id of the parent
*                     element, followed by a dot and the unique id of the
*                     child element.  So, child element id=parentId.childId.
*                  3. An HTML anchor is then created using the URL of the 
*                     HTML/JSP page, the element id as the hash and the text 
*                     contents of the TOC element as the link text.
*   Arguments: cid - ID of the element that will hold the TOC entries
*              url - URL of the page to process
*              runAfter - Run a method after the load completes
*     Returns: nothing
*****************************************************************************/
function loadTocFromPage(cid, url, runAfter) {

	function TocElt(id, txt) {
		this.id = id;
		this.txt = txt;
		this.subs = [];
	}

	function makeAnchor(href, lbl) {
		var a = document.createElement('a');

		a.setAttribute('href', href);
        a.setAttribute('class', 'widjitLink');
		a.appendChild(document.createTextNode(lbl));

		return a;
		
	}

    // Process all of the TOC nodes into a parent/child list
	function organizeToc(tocNodes) {
		var tocTree = [];

        for (var i = 0; i < tocNodes.length; i++) {
            var id = tocNodes[i].getAttribute('id');
			var txt = tocNodes[i].textContent;

			if (id.indexOf('.') < 0) {
				tocTree.push(new TocElt(id, txt));

			}
			else {
				var pntId = id.split('.')[0];
                var pnt = null;

				for (var j = 0; j < tocTree.length && pnt == null; j++) {
					if (tocTree[j].id == pntId) {
						pnt = tocTree[j];
						pnt.subs.push(new TocElt(id, txt));
					}

				}

			}

        }

		return tocTree;

	}

	// Generate the links to display in the TOC
	function generateLinks(tocTree) {

		for (var i = 0; i < tocTree.length; i++) {
			if (i > 0) {
				tocPnt.appendChild(document.createTextNode(' | '));
			}

			tocPnt.appendChild(makeAnchor(url + '#' + tocTree[i].id, tocTree[i].txt));

			if (tocTree[i].subs.length > 0) {
				tocPnt.appendChild(document.createTextNode(tocGroupStart));
                generateLinks(tocTree[i].subs);
				tocPnt.appendChild(document.createTextNode(tocGroupEnd));
			}

		}

	}

    // Generate a list TOC elements from the HTML
    function makeToc(html) {
		var parser = new DOMParser();
		var dom = parser.parseFromString(html, 'text/html');
		var tocTree = organizeToc(dom.getElementsByName('toc'));

		generateLinks(tocTree);

		if (runAfter !== undefined) {
			tocPnt.appendChild(document.createTextNode(tocGroupStart));
			runAfter();
			tocPnt.appendChild(document.createTextNode(tocGroupEnd));
		}
					
	}

    // Process the AJAX response
    function processResponse() {

        if (request.readyState == 4) {
            if (request.status == 200) {
				makeToc(request.responseText);
			}

        }

	}

	var request = new XMLHttpRequest();
	var tocPnt = document.getElementById(cid);

	request.onreadystatechange = processResponse;
	request.open('GET', url, true);
	request.send();

}

/*****************************************************************************
*    Function: makeShortCutToc
* Description: Generate a table of contents for the Start Menu shortcuts page
*   Arguments: cid - ID of the element that will hold the TOC entries
*              dest - URL of the target page
*     Returns: nothing
*****************************************************************************/
function makeShortCutToc(cid, dest) {
	var id = document.getElementById(cid);
	var content = document.getElementById('contentDiv');
	var curName = null;

    for (var i = 0; i < shortcuts.length; i++) {
		if (curName != shortcuts[i].tbName) {
			var a = document.createElement('a');

			// Add separator if this is not the first link
			if (curName != null) {
				id.appendChild(document.createTextNode(' | '));
			}

			curName = shortcuts[i].tbName;
			a.setAttribute('href', dest + '#' + toId(curName));
            a.setAttribute('class', 'widjitLink');
			a.appendChild(document.createTextNode(curName));
			id.appendChild(a);

		}

    }

}

/*****************************************************************************
*    Function: makeBookmarkToc
* Description: Generate a table of contents for the bookmarks page
*   Arguments: cid - ID of the element that will hold the TOC entries
*              dest - URL of the target page
*     Returns: nothing
*****************************************************************************/
function makeBookmarkToc(cid, dest) {
	var id = document.getElementById(cid);
	var menu = getBmMenu(JSON.parse(ffbm).children);
	var first = true;

	if (menu != null) {
		var startPos = determineBmStartPos(menu);

        if (startPos >= 0) {
			for (var pos = startPos; pos < menu.length; pos++) {
				if (typeof menu[pos].children !== "undefined") {
					var a = document.createElement('a');

					// Add separator if this is not the first link
					if (!first) {
						id.appendChild(document.createTextNode(' | '));
					}

					a.setAttribute('href', dest + '#' + toId(menu[pos].title));
                    a.setAttribute('class', 'widjitLink');
					a.appendChild(document.createTextNode(menu[pos].title));
					id.appendChild(a);
					first = false;

				}

			}

		}

	}

}
//-->
