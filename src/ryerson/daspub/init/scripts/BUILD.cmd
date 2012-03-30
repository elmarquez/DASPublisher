REM Sample build file for use on a Windows OS machine
@echo off

echo.
echo Building the CACB presentation ... it will take a few minutes
echo .............................................................
echo.

echo Mounting the web server share under the current user account
use n: \\www\wwwroot /persistent:no 1>>OUTPUT.log 2>&1
echo.

echo Making mobile presentation
java -jar config\DASPub.jar -config config\config.txt -publish mobile -clean -output W:\cacb\presentation\mobile 1>OUTPUT.log 2>ERRORS.log
echo.

echo Making artifact gallery for QR code lookups
java -jar config\DASPub.jar -config config\config.txt -publish artifact -clean -output W:\cacb\presentation\artifacts 1>OUTPUT.log 2>ERRORS.log
echo.

echo Making artifact QR code tag sheets
java -jar config\DASPub.jar -config config\config.txt -publish tagsheet -clean -output W:\cacb\presentation\artifacts 1>OUTPUT.log 2>ERRORS.log
echo.

echo Making desktop slideshow
java -jar config\DASPub.jar -config config\config.txt -publish slideshow -clean -output W:\cacb\presentation\slideshow 1>OUTPUT.log 2>ERRORS.log
echo.

echo Dismounting the web server share
net use n: /delete
echo.

echo Build complete
echo.

REM pause before closing the window
pause