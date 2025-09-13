#!/usr/bin/env python3
"""Generate a Javascript file that contains a list of the apps installed on an
Android device.

To run: C>py adrApps.py
To generate pydoc: 
    py -m pydoc adrApps | py %PYTHONPATH%\\pydocCleanup.py > ..\\scriptDoc\\adrApps.txt
"""
import warnings
from typing import Final
import datetime
import subprocess
import util
import sys

JS_FILE_SPEC: Final[str] = util.resolvePath(__file__, "..\\include\\apps.js")
ANDROID_BRIDGE: Final[str] = "C:\\Src\\CellPhone\\platform-tools\\adb.exe"
JS_VAR_DECL: Final[str] = "var androidApps = [ "
NL: Final[str] = "\n"

def loadPkgs() -> list:
    """Load a list of packages that are installed on the Android device.
    
    Returns:
        list: A list of package data.
    """
    PREFIX: Final[str] = "package:"
    LINE_END: Final[str] = "/base.apk"

    pkgs = []
    
    result = subprocess.run([ ANDROID_BRIDGE, "shell", "pm", "list", "packages", "-3", "-e", "-f" ], capture_output=True, text=True)
    lines = result.stdout.split(NL)

    for line in lines:
        if len(line.strip()) > 0:
            endPos = line.index(LINE_END)
            pkgs.append(line[len(PREFIX):endPos + len(LINE_END)])

    return pkgs

def procPkg(pkg: str) -> str:
    """Process the data from an individual package.
    
    Args:
        pkg (str): Data for a single package.
        
    Returns:
        str: The application name referenced by the package.
    """
    NM_LBL: Final[str] = "application: label='"

    result = subprocess.run([ ANDROID_BRIDGE, "shell", "/data/local/tmp/aapt", "dump", "badging", pkg ], capture_output=True, text=True)
    lines = result.stdout.split(NL)
    appNm = ""
    
    for line in lines:
        if line[:len(NM_LBL)] == NM_LBL:
            delimPos = line[len(NM_LBL):].index("'")
            appNm = line[len(NM_LBL):delimPos + len(NM_LBL)]
            break

    return appNm

def procPkgs(pkgs: list) -> list:
    """Process a list of packages.
    
    Args:
        pkgs (list): A list of package data.
        
    Returns:
        list: A list of application names.
    """
    appNms = []

    for pkg in pkgs:
        appNm = procPkg(pkg)

        if len(appNm) > 0:
            appNms.append(appNm)

    return appNms

def writeAppList(appNms: list) -> None:
    """Write the application list to the Javscript file.

    Args:    
        appNms (list): List of application names.
    """
    jsFile = open(JS_FILE_SPEC, "wt")

    jsFile.write("<!--" + NL)
    jsFile.write("// From: " + __file__ + " on " + str(datetime.datetime.now()) + NL)
    jsFile.write("var applu = '" + str(datetime.datetime.now()) + "';" + NL)
    jsFile.write(JS_VAR_DECL)

    for i in range(len(appNms)):
        if i > 0:
            jsFile.write(",")
            jsFile.write(NL)
            jsFile.write(" " * len(JS_VAR_DECL))

        jsFile.write("'" + appNms[i] + "'")

    jsFile.write(" ];" + NL)
    jsFile.write("//-->" + NL)
    jsFile.close()

def intro() -> bool:
    """ Determine if an Android device is connected and output the intro line. 
    
    Returns:
        bool: True if an Android device is connected, otherwise false
    """
    result = subprocess.run([ ANDROID_BRIDGE, "devices", "-l" ], capture_output=True, text=True)
    lines = result.stdout.split(NL)
    
    if len(lines[1].strip()) > 0:
        print("Generating application data for " + lines[1].replace("  ", ": ", 1).replace("  ", ""))
        ok = True
    else:
        print("Error: No Android device is attached.")
        ok = False

    return ok

def main() -> int:
    """Main program.
    
    Returns:
        int: System return code.
    """
    rc = 0

    if intro():
        pkgs = loadPkgs()
        appNms = procPkgs(pkgs)
        writeAppList(appNms)
        print(JS_FILE_SPEC + " generation complete")
    else:
        rc = 1

    return rc

if __name__ == '__main__':
    sys.exit(main())
