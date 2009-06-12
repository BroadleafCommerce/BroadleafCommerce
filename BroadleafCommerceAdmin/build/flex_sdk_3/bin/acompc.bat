@echo off

rem
rem ADOBE SYSTEMS INCORPORATED
rem Copyright 2007 Adobe Systems Incorporated
rem All Rights Reserved.
rem 
rem NOTICE: Adobe permits you to use, modify, and distribute this file
rem in accordance with the terms of the license agreement accompanying it.
rem 

rem
rem acompc.bat script for Windows.
rem This simply executes compc.exe in the same directory,
rem inserting the option +configname=air, which makes
rem compc.exe use air-config.xml instead of flex-config.xml.
rem On Unix, acompc is used instead.
rem

"%~dp0compc.exe" +configname=air %*

