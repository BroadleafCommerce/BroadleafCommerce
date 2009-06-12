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
rem aasdoc.bat script for Windows.
rem This simply executes asdoc.exe in the same directory,
rem inserting the option +configname=air, which makes
rem asdoc.exe use air-config.xml instead of flex-config.xml.
rem On Unix, aasdoc is used instead.
rem

"%~dp0asdoc.exe" +configname=air %*

