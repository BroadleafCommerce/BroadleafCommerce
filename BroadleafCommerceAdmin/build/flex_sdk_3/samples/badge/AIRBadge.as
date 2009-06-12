/* 
Adobe AIR Application Install Badge -- AS3

Required variables (to be passed in FlashVars parameter of Object & Embed tags in HTML):
o appname (name of application displayed in message under install button)
o appurl (url of .air file on server)
o airversion (version of the AIR Runtime)

Optional variables:
o buttoncolor (six digit hex value of button background; setting value to "transparent" is also possible)
o messagecolor (six digit hex value of text message displayed under install button)
o imageurl (url of .jpg file to load)

Note that all of these values must be escaped per the requirements of the FlashVars parameter.

Also note that you can set the badge background color with the standard Object/Embed "bgcolor" parameter

*/


package {
	import flash.display.*;
	import flash.events.*;
	import flash.geom.ColorTransform;
	import flash.net.URLRequest;
	import flash.system.*;
	import flash.text.TextField;

	// AIRBadge is our main document class
	public class AIRBadge extends MovieClip {

		public function AIRBadge() {
			// Read FlashVars
			try {
				var parameters:Object = LoaderInfo(this.root.loaderInfo).parameters;

				_messageColor = validateColor(parameters["messagecolor"]);

				_buttonColor = parameters["buttoncolor"];
				if (_buttonColor != "transparent") {
					_buttonColor = validateColor(_buttonColor);
				}

				_imageURL = validateURL(parameters["imageurl"]);

				_airVersion = String(parameters["airversion"]);

				_appURL = validateURL(encodeURI(parameters["appurl"]));

				// Make sure the appname does not contain any tags, by checking for "less than" characters
				_appName = parameters["appname"];
				if ( _appName == null || _appName.length == 0 || _appName.indexOf("<") >= 0) {
					_appName = null;

				}
			} catch (error:Error) {
				_messageColor = "FF0000";
				_buttonColor = "000000";
				_appURL = "";
				_appName = null;
				_airVersion = "";
			}
			// Set-up event handler for button
			this.addEventListener(MouseEvent.MOUSE_UP, onButtonClicked);

			// Reset status message text
			root.statusMessage.text = "";

			// Load background image
			if (_imageURL && _imageURL.length > 0) {
				try {
					var loader:Loader = new Loader();
					loader.load(new URLRequest(_imageURL));
					root.image_mc.addChild(loader);
				} catch (error:Error) {
				}
			}

			// Colorize button background movieclip (buttonBg_mc)
			if ( _buttonColor != "transparent" ) {
				root.buttonBg_mc._visible = true;
				var tint:uint = new Number("0x" + _buttonColor).valueOf();

				var transform:ColorTransform = new ColorTransform();
				transform.redMultiplier = ((tint & 0xFF0000) >> 16) / 256.0;
				transform.greenMultiplier = ((tint & 0x00FF00) >> 8) / 256.0;
				transform.blueMultiplier = ((tint & 0x0000FF)) / 256.0;

				root.buttonBg_mc.transform.colorTransform = transform;

			} else {
				root.buttonBg_mc._visible = false;
			}

			_loader = new Loader();
			var loaderContext:LoaderContext = new LoaderContext();
			loaderContext.applicationDomain = ApplicationDomain.currentDomain;

			_loader.contentLoaderInfo.addEventListener(Event.INIT, onInit);
			try {
				_loader.load(new URLRequest(BROWSERAPI_URL_BASE + "/air.swf"), loaderContext);
			} catch (e:Error) {
				root.statusMessage.text = e.message;
			}
		}

		private function onInit(e:Event):void {
			_air = e.target.content;
			switch (_air.getStatus()) {
				case "installed" :
					root.statusMessage.text = "";
					break;
				case "available" :
					if (_appName && _appName.length > 0) {
						root.statusMessage.htmlText = "<p align='center'><font color='#" + _messageColor + "'>In order to run " + _appName + ", this installer will also set up Adobe® AIR™.</font></p>";
					} else {
						root.statusMessage.htmlText = "<p align='center'><font color='#" + _messageColor + "'>In order to run this application, this installer will also set up Adobe® AIR™.</font></p>";
					}
					break;
				case "unavailable" :
					root.statusMessage.htmlText = "<p align='center'><font color='#" + _messageColor + "'>Adobe® AIR™ is not available for your system.</font></p>";
					root.buttonBg_mc.enabled = false;
					break;
			}
		}

		private function onButtonClicked(e:Event):void {
			try {
				switch (_air.getStatus()) {
					case "installed" :
						root.statusMessage.htmlText = "<p align='center'><font color='#" + _messageColor + "'>Download and open the AIR file to begin the installation.</font></p>";
						_air.installApplication( _appURL, _airVersion );
						break;
					case "available" :
						root.statusMessage.htmlText = "<p align='center'><font color='#" + _messageColor + "'>Starting install...</font></p>";
						_air.installApplication( _appURL, _airVersion );
						break;
					case "unavailable" :
						// do nothing
						break;
				}
			} catch (e:Error) {
				root.statusMessage.text = e.message;
			}
			/* clearInterval( _global.installIntId ); */
		}

		// Validate URL: only allow HTTP, HTTPS scheme or relative path
		// Return null if not a valid URL
		private static function validateURL(url:String):String {
			if (url && url.length > 0) {
				var schemeMarker:int = url.indexOf(":");
				if (schemeMarker < 0) {
					schemeMarker = url.indexOf("%3a");
				}
				if (schemeMarker < 0) {
					schemeMarker = url.indexOf("%3A");
				}
				if (schemeMarker > 0) {
					var scheme:String = url.substr(0, schemeMarker).toLowerCase();
					if (scheme != "http" && scheme != "https" && scheme != "ftp") {
						url = null;
					}
				}
			}
			return url;
		}

		// Validate color: only allow 6 hex digits
		// Always return a valid color, black by default
		private static function validateColor(color:String):String {
			if ( color == null || color.length != 6 ) {
				color = "000000";
			} else {
				var validHex:String = "0123456789ABCDEFabcdef";
				var numValid:int = 0;
				for (var i:int=0; i < color.length; ++i) {
					if (validHex.indexOf(color.charAt(i)) >= 0) {
						++numValid;
					}
				}
				if (numValid != 6) {
					color = "000000";
				}
			}
			return color;
		}

		private const BROWSERAPI_URL_BASE: String = "http://airdownload.adobe.com/air/browserapi";

		private var _messageColor: String;
		private var _buttonColor: String;
		private var _imageURL: String;
		private var _appURL: String;
		private var _appName: String;
		private var _airVersion: String;

		private var _loader:Loader;
		private var _air:Object;
	}
}
