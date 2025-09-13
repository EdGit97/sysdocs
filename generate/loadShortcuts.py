#!/usr/bin/env python3
"""Generate a Javascript file that contains the definitions of the shortcuts 
used in the Windows start menu toolbars and taskbar.

To run: C>py loadShortcuts.py
To generate pydoc: 
    py -m pydoc loadShortcuts | py %PYTHONPATH%\\pydocCleanup.py > ..\\scriptDoc\\loadShortcuts.txt

The properties.dbf file contains the list of the directories that are searched 
for .lnk files.  The directory list is compiled from the toolbars and pinned 
property groups.

References:
https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-showwindow - For WINSTATE dictionary
"""
import os
import json
import LnkParse3
import warnings
from typing import Final
from typing import TextIO
from typing import Callable
import datetime
import sys
import sysProps
import util

JS_FILE_SPEC: Final[str] = util.resolvePath(__file__, "..\\include\\shortcuts.js")
JS_PARM_SEP: Final[str] = "', '"
NL: Final[str] = "\n"
KEY_MODS: Final[dict] = {
    "CTRL": 0b0010,  # The Ctrl Key
    "SHIFT": 0b0001, # The Shift Key
    "ALT": 0b0100    # The Alt Key
}
WINSTATE: Final[dict] = { 
    "SW_HIDE": 0,
    "SW_SHOWNORMAL": 1,
    "SW_NORMAL": 1,
    "SW_SHOWMINIMIZED":  2,
    "SW_SHOWMAXIMIZED": 3,
    "SW_MAXIMIZE": 3,
    "SW_SHOWNOACTIVATE": 4,
    "SW_SHOW": 5,
    "SW_MINIMIZE": 6,
    "SW_SHOWMINNOACTIVE": 7,
    "SW_SHOWNA": 8,
    "SW_RESTORE": 9,
    "SW_SHOWDEFAULT": 10,
    "SW_FORCEMINIMIZE": 11 
}

class tbProc:
    """Data for toolbar directory processing
    
    Args:
        dirs (list): List of directories associated with a toolbar group.
        titleProc (Callable[[str], str]): Lambda function to generate the toolbar title.
    """
    def __init__(self, dirs: list, titleProc: Callable[[str], str]):
        self.dirs = dirs
        self.titleProc = titleProc
    
def processHotKeyModifiers(hkMods: int) -> str:
    """Process the hot key modifier and determine which modifier keys are depressed.
    
    Args:
        hkMods (int): The bits defining which modifier key (Ctrl, Alt, Shift) are required for the hotkey.
    
    Returns:
        str: String with some combination of Ctrl+Shift+Alt+.
    """
    mods = ""
    
    for keyMod in KEY_MODS:
        if KEY_MODS[keyMod] & hkMods > 0:
            mods += keyMod.capitalize() + "+"
            
    return mods

def writeJsonKey(jsFile: TextIO, jsonSection: dict, key: str, writeSep: bool, defaultVal: str="") -> None:
    """Escape the backslashes in a Windows path for inclusion in a Javascript file.
    
    Args:
        jsFile (TextIO): JavaScript output file.
        jsonSection (dict): The part of the JSON to scan for a key.
        key (str): The JSON key to find in the jsonSection.
        writeSep (bool): True to write the Javascript separator at the end.
        defaultValue (str): The value to use if the key is not found.
    """
    try:
        value = jsonSection[key].replace("\\", "\\\\")
    except:
        value = defaultVal
        
    jsFile.write(value)
    
    if writeSep:
        jsFile.write(JS_PARM_SEP)

def writeWinStyle(jsFile: TextIO, jsonSection: dict, key: str, writeSep: bool) -> None:
    """Process the window state.
    
    Args:
        jsFile (TextIO): JavaScript output file.
        jsonSection (dict): The part of the JSON to scan for a key.
        key (str): The JSON key to find in the jsonSection.
        defaultValue (str): The value to use if the key is not found.
    """
    try:
        dicKey = jsonSection[key]
    except:
        dicKey = "SW_SHOWNORMAL"
    
    try:
        value = WINSTATE[dicKey]
    except KeyError:
        value = WINSTATE["SW_SHOWNORMAL"]

    jsFile.write(str(value))
    
    if writeSep:
        jsFile.write(JS_PARM_SEP)

def processHotKey(jsFile: TextIO, jsonSection: dict, key: str, writeSep: bool) -> None:
    """Process the shortcut key if present.  The key consists of two bytes.
    The first eight bits are the ASCII code of the alphanumeric key associated
    with the hot key.
    
    Args:
        jsFile (TextIO): JavaScript output file.
        jsonSection (dict): The part of the JSON to scan for a key.
        key (str): The JSON key to find in the jsonSection.
        writeSep (bool): True to write the Javascript separator at the end.
    """
    hotKey = ""
    
    try:
        value = jsonSection[key]
    except:
        value = ""
        
    if len(value) == 4:
        hotKey += processHotKeyModifiers(int(value[-1:]))
        hotKey += bytearray.fromhex(value[0:2]).decode()
    
    jsFile.write(str(hotKey))
    
    if writeSep:
        jsFile.write(JS_PARM_SEP)

def writeShortcut(jsFile: TextIO, shortcutFs: str, tbName: str, startWithNewLine: bool) -> None:
    """Write the shortcut data to the output file.
    
    Args:
        jsFile (TextIO): JavaScript output file.
        shortcutFs (str): File specification of the shortcut file.
        tbName (str): Name of the toolbar.
        startWithNewLine (bool): True to start with a new line, False to write on the current line.
    """
    lnk = open(shortcutFs, "rb")
    shortcut = LnkParse3.lnk_file(lnk)
    pp = shortcutFs.split("\\")
    json = shortcut.get_json()
    data = json["data"]
    linkInfo = json["link_info"]
    header = json["header"]
    
    if startWithNewLine:
        jsFile.write("," + NL)

    jsFile.write(" " * 4)
    jsFile.write("new ShortCut('")
    jsFile.write(tbName)
    jsFile.write(JS_PARM_SEP)
    jsFile.write(shortcutFs.replace("\\", "\\\\"))
    jsFile.write(JS_PARM_SEP)
    writeJsonKey(jsFile, data, "command_line_arguments", True)
    writeJsonKey(jsFile, data, "working_directory", True)
    writeJsonKey(jsFile, linkInfo, "local_base_path", True)
    writeJsonKey(jsFile, data, "icon_location", True, ",0")
    processHotKey(jsFile, header, "hotkey", True)
    writeWinStyle(jsFile, header, "windowstyle", True)
    writeJsonKey(jsFile, data, "description", False)
    jsFile.write("')")

def getToolBarName(tbPath: str) -> str:
    """ Determine the name of the toolbar from the path.
    
    Args:
        tbPath (str): Path from the INI file.
    
    Returns:
        str: The last component of tbPath.
    """
    pp = tbPath.split("\\")
    
    if len(pp) > 1:
        toolBarName = pp[len(pp) - 1]
    else:
        toolBarName = tbPath
        
    return toolBarName.title() + " Toolbar"
    
def listFiles(jsFile: TextIO, dirName: str, tbName: str, startWithNewLine: bool) -> bool:
    """List the files in a directory and all of it's sub-directories.
    
    Args:
        jsFile (TextIO): JavaScript output file.
        dirName (str): List the files starting in this directory.
        tbName (str): Name of the toolbar.
        startWithNewLine (bool): True to start with a new line, False to write on the current line.
        
    Returns:
        bool: The updated startWithNewLine value.
    """    
    for root, dirs, files in os.walk(dirName):
        for fn in files:
            if fn.lower().endswith(".lnk"):
                writeShortcut(jsFile, root + "\\" + fn, tbName, startWithNewLine)
                startWithNewLine = True
                
    return startWithNewLine
          
def process(jsFile: TextIO) -> None:
    """Generate the JavaScript shortcut entries based on the directories listed
    in the properties.dbf file.
    
    Args:
        jsFile (TextIO): JavaScript output file.
    """
    startWithNewLine = False
    sp = sysProps.SysProps()

    sections = { 
        sysProps.TOOLBARS_SECTION: tbProc(sp.toolbarsData.items(), lambda dirName : getToolBarName(dirName)),
        sysProps.PINNED_SECTION: tbProc(sp.pinnedData.items(), lambda dirName: sysProps.PINNED_SECTION.title() + " Taskbar")
    }
    
    if len(sp.toolbarsData) <= 0 and len(sp.pinnedData) <= 0:
        print("Error: No shortcut folders are listed.  Shortcut list will be empty.")
    else:
        for index0, (section, tbp) in enumerate(sections.items()):
            for index1, (prop, dirName) in enumerate(tbp.dirs):
                print("Processing shortcuts in " + section + "." + prop + " => " + dirName)
                startWithNewLine = listFiles(jsFile, dirName, tbp.titleProc(dirName), startWithNewLine)

def generateHeader(jsFile: TextIO) -> None:
    """ Write the top part of the shortcuts.js file.
    
    Args:
        jsFile (TextIO): JavaScript output file.
    """
    jsFile.write("<!--" + NL)
    jsFile.write("// From: " + __file__ + " on " + str(datetime.datetime.now()) + NL)
    jsFile.write("var sclu = '" + str(datetime.datetime.now()) + "';" + NL)
    jsFile.write("var shortcuts = [" + NL)

def generateFinish(jsFile: TextIO) -> None:
    """ Write the end of the shortcuts.js file. 
    
    Args:
        jsFile (TextIO): JavaScript output file.
    """
    jsFile.write(" ];" + NL)
    jsFile.write("//-->" + NL)
    
def main() -> int:
    """Main program.
    
    Returns:
        int: System return code.
    """
    warnings.filterwarnings("ignore")
    
    jsFile = open(JS_FILE_SPEC, "w")

    generateHeader(jsFile)
    process(jsFile)
    generateFinish(jsFile)

    jsFile.close()
    print(JS_FILE_SPEC + " generation complete.")
    return 0

if __name__ == '__main__':
    sys.exit(main())
