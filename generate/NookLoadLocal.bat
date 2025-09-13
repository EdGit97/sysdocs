@echo off
    rem NookLoadLocal.bat
    rem Copy local books to the Nook
    rem
    set NookRoot=D:\NOOK\My Documents
    set EbookRoot=C:\Src\ebooks
    set JRoot=C:\webdevenv\showDocs\judging
    set TravRoot=C:\Users\swane\Documents\Traveller
    set TravEBooks=C:\Src\designs\Traveller\ebooks
    set epubDest=Books
    set pdfDest=Documents

    rem Check for the Nook plugged in
    dir D:\ 2> null | find /i "D is NOOK"
    if errorlevel 1 goto NoNook

    rem Create destination directories if they do not exist
    if not exist "%NookRoot%\Books" md "%NookRoot%\%epubDest%"
    if not exist "%NookRoot%\Documents" md "%NookRoot%\%pdfDest%"

    copy %EbookRoot%\epubCal\calendar.epub  "%NookRoot%\%epubDest%"
    copy %EbookRoot%\Conversions\conversions.epub "%NookRoot%\%epubDest%"
    copy %JRoot%\judging.epub "%NookRoot%\%epubDest%"
    copy %TravEBooks%\*.epub "%NookRoot%\%epubDest%"
    copy %TravRoot%\*.pdf "%NookRoot%\%pdfDest%"
    copy C:\Src\Pictures\nookSaver.png "%NookRoot%\ScreenSaver"
    echo Copy of local books complete.

    goto Done
:NoNook
    echo The Nook is not attached to this laptop.
:Done
    set NookRoot=
    set EbookRoot=
    set JRoot=
    set TravRoot=
    set epubDest=
    set TravEBooks=
    set pdfDest=
