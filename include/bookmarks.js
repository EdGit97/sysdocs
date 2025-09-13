<!--
/*****************************************************************************
* Functions to support the bookmarks.jsp page
*****************************************************************************/
const bmStartTag = 'sysdocsBms';

/*****************************************************************************
*    Function: getBmMenu
* Description: Load the menu subtree from the overall bookmarks data
*   Arguments: bmMenu - The overall bookmarks menu
*     Returns: The menu subtree
*****************************************************************************/
function getBmMenu(bmMenu) {
	var menu = null;

	for (var i = 0; bmMenu != null && i < bmMenu.length && menu == null; i++) {
		if (bmMenu[i].title == 'menu') {
			menu = bmMenu[i].children;
		}
	}

	return menu;

}

/*****************************************************************************
*    Function: determineBmStartPos
* Description: Determine the position of the first bookmark after the last 
*              standard bookmark
*   Arguments: menu - The list of bookmarks
*     Returns: The position in the menu to begin processing
*****************************************************************************/
function determineBmStartPos(menu) {
	var startPos = -1;

	// Find the starting point 
	for (var i = 0; i < menu.length && startPos < 0; i++) {
		if (menu[i].tags == bmStartTag) {
			startPos = i;
		}
	}

	return startPos;

}

/*****************************************************************************
*    Function: showBookmarks
* Description: Display the list of Firefox bookmarks
*   Arguments: None
*     Returns: nothing
*****************************************************************************/
function showBookmarks() {
	function makeLbl(lblText, id) {
		var lbl = document.createElement('label');

		lbl.setAttribute('class', 'bookmark');

		if (id !== undefined) {
			lbl.setAttribute('id', toId(id));
		}

		lbl.appendChild(document.createTextNode(lblText + ':'));


        return lbl;

	}

	function makeList(lvl) {
		var ul = document.createElement('ul');

        ul.setAttribute('class', 'instructions bmlvl' + lvl);

		return ul;

	}

    function displayBookmarks(subTree, startPos, trgt, level) {

        for (var pos = startPos; pos < subTree.length; pos++) {
			var li = document.createElement('li');

			if (typeof subTree[pos].children === "undefined") {
				li.appendChild(makeLbl('Title'));
				li.appendChild(document.createTextNode(subTree[pos].title));
				li.appendChild(document.createElement('br'));
				li.appendChild(makeLbl('URI'));
				li.appendChild(document.createTextNode(subTree[pos].uri));
				trgt.appendChild(li);

			}
			else {
                var ul = makeList(level + 1);

				li.appendChild(makeLbl('Folder', subTree[pos].title));
				li.appendChild(document.createTextNode(subTree[pos].title));
				li.setAttribute('class', 'sublist');
                li.appendChild(ul);
				trgt.appendChild(li);

				displayBookmarks(subTree[pos].children, 0, ul, level + 1);

			}

		}

	}

	var menu = getBmMenu(JSON.parse(ffbm).children);
	var trgt = document.getElementById('bmlvl1');

	if (menu == null) {
		document.write('Error: Cannot find the menu in the bookmarks tree.');
	}
	else {
		var startPos = determineBmStartPos(menu);

        if (startPos >= 0) {
			displayBookmarks(menu, startPos, trgt, 0);
        }

	}

}
//-->
