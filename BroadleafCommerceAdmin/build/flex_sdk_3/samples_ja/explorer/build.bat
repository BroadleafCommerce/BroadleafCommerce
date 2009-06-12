@echo off

SET OPTS=-use-network=false -locale=ja_JP

REM this is a little wasteful because it compiles the components too, but there aren't that many
for /R . %%f in (*.mxml) do  ..\..\bin\mxmlc.exe %OPTS% "%%f"
