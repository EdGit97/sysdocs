#!/usr/bin/env python3
"""Generate the FreeFileSync jobs and load them into the Windows Scheduler.

To run: C>py genSched.py
To generate pydoc: py -m pydoc genSched > ..\\scriptDoc\\genSched.txt
"""
from typing import Final
import subprocess
import util

FFS_PATH: Final[str] = "C:\\Program Files\\FreeFileSync\\FreeFileSync.exe"
BACKUP_ROOT: Final[str] = "C:\\Src\\sysdocs\\backup\\"
PARM_FIELDS: Final[str] = [ "taskNm", "jobNm", "jobTime" ]
SCHED_CMD: Final[str] = "schtasks /create /sc DAILY /tn ffsb\\%" + PARM_FIELDS[0] + "% /tr \"'" + FFS_PATH + "' '" + BACKUP_ROOT + "%" + PARM_FIELDS[1] + "%.ffs_batch'\" /mo 1 /st %" + PARM_FIELDS[2] + "%"
JOB_PARMS: Final[tuple] = (
    { PARM_FIELDS[0]: "accounting", PARM_FIELDS[1]: "Accounting", PARM_FIELDS[2]: "19:00" },
    { PARM_FIELDS[0]: "backup", PARM_FIELDS[1]: "Backup", PARM_FIELDS[2]: "19:30" },
    { PARM_FIELDS[0]: "documents", PARM_FIELDS[1]: "Documents", PARM_FIELDS[2]: "12:00" },
    { PARM_FIELDS[0]: "legacy", PARM_FIELDS[1]: "Legacy", PARM_FIELDS[2]: "11:00" },
    { PARM_FIELDS[0]: "music", PARM_FIELDS[1]: "Music", PARM_FIELDS[2]: "13:00" },
    { PARM_FIELDS[0]: "pictures", PARM_FIELDS[1]: "Pictures", PARM_FIELDS[2]: "14:00" },
    { PARM_FIELDS[0]: "src", PARM_FIELDS[1]: "Src", PARM_FIELDS[2]: "10:00" },
    { PARM_FIELDS[0]: "webdevenv", PARM_FIELDS[1]: "webdevenv", PARM_FIELDS[2]: "09:00" },
    { PARM_FIELDS[0]: "toolbars", PARM_FIELDS[1]: "Toolbars", PARM_FIELDS[2]: "15:00" }
)

def generateSched() -> list:
    """Generate the scheduler commands.
    
    Returns:
        list: List of commands to run that will add the jobs to the schedule.
    """
    schedList = []

    for job in JOB_PARMS:
        schedTask = SCHED_CMD
        
        for fldNm in PARM_FIELDS:
            schedTask = schedTask.replace("%" + fldNm + "%", job[fldNm])
        
        schedList.append(schedTask)
            
    return schedList
    
def loadSched(tasks: list) -> None:
    """Load jobs into the scheduler.
    
    Args:
        The list of commands to run to load the schedule with the backup jobs.
    """
    for cmd in tasks:
        try:
            result = subprocess.run(cmd, capture_output=True, text=True, check=False, shell=True)
            print(result.stdout)
            
        except subprocess.CalledProcessError as e:
            print("Error loading job:", e)
            print("Error Output:", e.stderr)        

def main() -> None:    
    """Main method"""
    tasks = generateSched()
    loadSched(tasks)

# Main program
if __name__ == "__main__":
    main()