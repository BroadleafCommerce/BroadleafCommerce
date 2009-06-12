Adobe AIR Seamless Install Badge

This is a sample SWF file and supporting HTML file for the seamless detection and installation of the Adobe AIR runtime along with the installation of an AIR application. Using this 'badge' lets you as a developer have greater control over the installation experience of your Adobe AIR application. Rather than requiring the user to download the AIR runtime installer from the Adobe website before returning to your site to download and run the AIR for your application, with this badge any user with Flash Player 9 Update 3 (version 9,0,115) can install the AIR runtime as part of your application's installation process.

This badge allows for an image to be loaded into the button, and for the color of the badge and the button to be changed via variables which are set in the FlashVars parameters in the Object and Embed tags. Please note that the the FlashVars need to be repeated three times in the HTML file: once for passing to the externalized JavaScript detection, and once each for the Object and Embed tags within the <noscript> tag.

Required variables (to be passed in FlashVars parameter of Object and Embed tags in HTML):

o appname -- the name of application displayed in message under install button if the AIR runtime is not present
o appurl -- The URL of .air file on the server
o airversion -- The version of the AIR runtime required

Optional variables:

o buttoncolor -- a six-digit hex value for the color of the button background. Setting this value to "transparent" is also possible.
o messagecolor -- a six-digit hex value for the color of the text message displayed under install button.
o imageurl -- The URL of the .jpg file to display in the badge interface. The URL should either be a relative path or use the HTTP, HTTPS, or FTP scheme.

Note that all of these values must be escaped per the requirements of the FlashVars parameter.

Also note that you can set the badge background color with the standard Object/Embed "bgcolor" parameter.
