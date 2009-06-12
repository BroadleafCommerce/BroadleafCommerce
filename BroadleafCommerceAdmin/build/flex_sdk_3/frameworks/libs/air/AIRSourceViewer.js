/* AIRSourceViewer.js - Revision: 1.1 */

// Copyright 2007-2008 Adobe Systems Incorporated.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//   * Redistributions of source code must retain the above copyright notice,
//     this list of conditions and the following disclaimer.
//   * Redistributions in binary form must reproduce the above copyright notice,
//     this list of conditions and the following disclaimer in the documentation
//     and/or other materials provided with the distribution.
//   * Neither the name of Adobe Systems Incorporated nor the names of its
//     contributors may be used to endorse or promote products derived from this
//     software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

// @the Source Viewer uses the <air> namespace:
if (typeof air == 'undefined') {air = {}};
air.SourceViewer = function() {
	throw ( new Error (
			"\n\n" +
			"You cannot instantiate the 'air.SourceViewer' class. " +
			"Instead, use 'air.SourceViewer.getDefault()' to retrieve the " +
			"class' unique instance." +
			"\n\n"
	));
}
air.SourceViewer.getDefault = function() {
	
	// The Source Viewer only works in the AIR application sandbox:
	if (typeof window.runtime == "undefined") {
		throw ( new Error (
			"\n\n" +
			"The Source Browser module can only work inside the <application " +
			"sandbox>. Please include the SourceViewer.js file in the " +
			"application's main *.html file." +
			"\n\n"
		));
	}
	
	// @return the existing instance if there is one; creating it otherwise:
	var context = arguments.callee;
	if (context.instance) { return context.instance };



	/**
	 * CLASS
	 * 		air.SourceViewer
	 * DESCRIPTION
	 * 		A self-contained module to embed in HTML-based AIR applications. 
	 * 		It will display a customizable selection of source files in 
	 * 		an expandable tree structure. The user will be able 
	 * 		to select a file to view its source code.
	 * SAMPLE USAGE
	 * 		var viewer1 = air.SourceViewer.getDefault();
	 * 		viewer1.viewSource(); 
	 * 
	 * 		var viewer2 = air.SourceViewer.getDefault();
	 * 		var oConfig = { exclude: ['/icons', '/images'] };
	 * 		viewer2.setup(oConfig);
	 * 		viewer2.viewSource(); 
	 * 
	 * @class
	 * @public
	 * @singleton
	 */
	function _SourceViewer() {



		/**
		 * CONSTANTS
		 * Private members that aren't ment to be altered in any way.
		 * @private
		 * @constant
		 */
		// @AIR runtime:
		var HTMLLoader               	= window.runtime.flash.html.HTMLLoader;
		var NativeWindowInitOptions  	= window.runtime.flash.display.
									   		NativeWindowInitOptions;
		var Rectangle                	= window.runtime.flash.geom.Rectangle;
		var NativeWindowType         	= window.runtime.flash.display.
		                               		NativeWindowType;
		var File                     	= window.runtime.flash.filesystem.File;
		var FileStream               	= window.runtime.flash.filesystem.
		                               		FileStream;
		var Screen						= window.runtime.flash.display.Screen;

		// @application
		var TEXT_EXTENSIONS         	= ['txt', 'xml', 'mxml', 'htm', 'html',
		                                	'js', 'as', 'css', 'properties', 
		                                	'config', 'ini', 'bat', 'readme'];
		var IMAGE_EXTENSIONS        	= ['jpg', 'jpeg', 'png', 'gif'];
		var TEXT_TYPE 					= "text";
		var IMAGE_TYPE 					= "image";

		// @events
		var WINDOW_CREATED_EVENT       = 'windowCreatedEvent';
		var FILES_LIST_READY_EVENT     = 'filesListReadyEvent';
		var FILE_LISTED_EVENT          = 'fileListedEvent';
		var FOLDER_CHECKED_EVENT       = 'folderCheckedEvent';
		var FOLDER_FIRST_CLICKED_EVENT = 'folderFirstClickedEvent';
		var FOLDER_STATE_CHANGED_EVENT = 'folderStateChanged';
		var ITEM_MOUSE_OVER_EVENT      = 'itemMouseOverEvent';
		var ITEM_MOUSE_OUT_EVENT       = 'itemMouseOutEvent';
		var ITEM_MOUSE_CLICK_EVENT     = 'itemMouseClickEvent';
		var FILE_ITEM_CLICKED          = 'fileItemClicked';
		var FILE_CONTENT_READY_EVENT   = 'fileContentReady';

		// @strings
		var CANNOT_READ_TEXT_MESSAGE   = 'Cannot retrieve text content from ' +
			                             'this filetype.';
		var IO_ERROR_MESSAGE           = 'An IO Error occured while trying to ' +
				                         'read this text.';


		var ADOBE_TOKEN				   = 'ADOBE';
		var COPYRIGHT_TOKEN			   = '&copy;';
		var AIR_TOKEN				   = 'AIR';
		var TRADEMARK_TOKEN			   = '&trade;';
		var APP_LEGAL_NAME             = 'HTML View Source Framework';
		var APP_VERSION_MAJOR_MINOR	   = '1.1';
		var TREE_DESCRIPTION_MESSAGE   = 'Select a source file in the tree to '+
			                             'see its content in the right pane:';
		var COPYRIGHT_MESSAGE          = '';

		/**
		 * Flag to raise when the Source Viewer's window is open.
		 * @field
		 * @private
		 */
		var isMainWindowOpen;

		/**
		 * The DOMProvider instance shared by all application components. It
		 * is instantiated by WindowsManager.makeMainWindow().
		 * @field
		 * @private
		 */
		var domProvider;
		
		/**
		 * The CSSProvider instance shared by all application components. It
		 * is instantiated by WindowsManager.makeMainWindow().
		 * @field
		 * @private 
		 */		
		var cssProvider;
		
		/**
		 * The UIBuilder instance shared by all application components. It
		 * is instantiated by WindowsManager.makeMainWindow().
		 * @field
		 * @private
		 */
		var uiBuilder;
		
		/**
		 * The EventManager instance shared by all application components.
		 * @field
		 * @private 
		 */
		var eventManager = new EventManager();
		
		/**
		 * The WindowsManager instance shared by all application components.
		 * @field
		 * @private
		 */
		var windowManager = new WindowsManager();
		
		/**
		 * The LayoutProvider instance shared by all application components.
		 * @field
		 * @private
		 */
		var layoutProvider = new LayoutProvider();

		/**
		 * The FileSystemWalker instance shared by all application components.
		 * @field
		 * @private
		 */
		var fileSystemWalker = new FileSystemWalker();
		
		/**
		 * The RequestedFilesRegistry instance shared by all application 
		 * components.
		 * @field
		 * @private
		 */		
		var requestedFilesRegistry = new RequestedFilesRegistry();
		
		/**
		 * A hash that gets populated with references to DOM Elements 
		 * representing application's UI main sections. Namely, the hash's 
		 * structure is:
		 * 		- header { HTML DOM element }
		 * 				A reference to the DOM Element representing the header 
		 * 				(top most section) of the application's UI
		 * 		- sidebar { HTML DOM element }
		 * 				A reference to the DOM Element representing the sidebar
		 * 				(left hand section) of the application's UI
		 * 		- tree { HTML DOM element }
		 * 				The <ul> HTML DOM Element that represents the root
		 * 				of the tree UI component (the one that displays 
		 * 				clickable entries for applications files and folders)
		 * 		- contentArea { HTML DOM element }
		 * 				A reference to the DOM Element representing the content
		 * 				area (right hand section) of the application's UI.
		 * 		- ruler { HTML DOM element }
		 * 				A reference to the DOM Element representing the 
		 * 				scrollable ruller that displays line numbers
		 * 		- sourceArea { HTML DOM element }
		 * 				The <div> HTML DOM Element that actually contains the 
		 * 				text of the file being displayed.
		 * 		- footer { HTML DOM element }
		 * 				A reference to the DOM Element representing the footer 
		 * 				(bottom most section) of the application's UI.
		 * @field
		 * @private
		 */
		var ui;

		/**
		 * The main method to be called by the client programmer. Opens the 
		 * Source Viewer UI and lists the first level of source files.
		 * @method
		 * @public
		 */	
		this.viewSource = function() {
			if(isMainWindowOpen){ return };
			var windowCreatedHandler = function(event) {
				initUI(event.body.window.document);
				eventManager.removeListener(
					WINDOW_CREATED_EVENT,
					windowCreatedHandler
				);
			};
			eventManager.addListener(
				WINDOW_CREATED_EVENT,
				windowCreatedHandler
			);
			windowManager.makeMainWindow (function(oWindow) {});
		}
		
		/**
		 * Holds the configuration object provided bythe client programmer.
		 * @field
		 * @private
		 */
		var oConfig = {};
		
		/**
		 * Holds the user preference regarding Source Viewer's modal state.
		 * A value of "true" will make our window modal, whereas a value of
		 * "false" will leave it a regular window. Default is "true".
		 * @field
		 * @private
		 */
		var isToBeModal = false;
		
		/**
		 * Holds the user preference regarding a default file that is to be 
		 * displayed by the Source Viewer when it starts. Will contain an 
		 * application's root relative file path.
		 * @field
		 * @private 
		 */
		var defaultFilePath;
		
		/**
		 * There is a list of file extensions that are 'recognized', and hence, 
		 * displayed. Should the user also want some other file extensions, he 
		 * must declare them here.
		 * @field
		 * @private  
		 */
		var typesToAdd = [];
		
		/**
		 * There is a list of file extensions that are 'recognized', and hence, 
		 * displayed. Should the user want some of these file extensions 
		 * removed, he must declare them here.
		 * Note:
		 * The "typesToRemove" exclusion list superseedes the "typesToAdd" 
		 * addition list.
		 * @field
		 * @private  
		 */
		var typesToRemove = [];
		
		/**
		 * Holds the user preference regarding the color scheme to be used for 
		 * the Source Viewer's UI. There are currently two color schemes 
		 * available:
		 * 		- professionalBlue
		 * 		- nightScape
		 * @field
		 * @private
		 */
		var userColorScheme;
		
		/**
		 * Holds the user preference regarding the initial [x, y] position of
		 * the Source Viewer's window on the screen. This is expected to be an 
		 * array of the form [x, y].
		 * @field
		 * @private
		 */
		var initPosition;
	
		/**
		 * Also part of the public API. Transmits the settings to the internal
		 * core.
		 * @method
		 * @public
		 * @param cfg { Object }
		 * 		Object literal containing settings for the Source Viewer.
		 * 		Currently only supports:
		 * 		- exclude { Array }
		 * 			An app root relative array of paths. Files or folders
		 * 			starting with one of these paths will not show in the tree.
		 * 		- modal { Boolean }
		 * 			Whether the Source viewer's window should be modal to the 
		 * 			host application's window. 
		 */
		this.setup = function (cfg) {
			oConfig = cfg;

			// @collect user's modal preference:
			if ((typeof oConfig['modal'] != "undefined") &&
				oConfig['modal'] !== null) {
				isToBeModal = oConfig['modal']? true: false; 
			}

			// @collect user's default file preference:
			if ((typeof oConfig['defaultFile'] != "undefined") &&
				oConfig['defaultFile'] !== null) {
				defaultFilePath = oConfig['defaultFile'];
			}

			// @collect user's file extensions list preference:
			if((typeof oConfig['typesToAdd'] != "undefined") 
				&& oConfig['typesToAdd'] !== null) {
				typesToAdd = oConfig['typesToAdd'];
			}
			if((typeof oConfig['typesToRemove'] 
				!= "undefined") && oConfig['typesToRemove'] !== null) {
				typesToRemove = oConfig['typesToRemove'];
			}
			var existingList = {};
			for(var i=0; i<TEXT_EXTENSIONS.length; i++) {
				var ext = TEXT_EXTENSIONS[i];
				existingList['text.'+ext] = 'text';
			}
			for(var j=0; j<IMAGE_EXTENSIONS.length; j++) {
				var ext = IMAGE_EXTENSIONS[j];
				existingList['image.'+ext] = 'image';
			}
			for(var k=0; k<typesToAdd.length; k++) {
				var newType = typesToAdd[k];
				var match1 = newType.match(/(?:^text\.(\w+)$)/);
				if(match1) {
					existingList['text.' + match1[1]] = 'text';
				}
				var match2 = newType.match(/(?:^image\.(\w+)$)/);
				if(match2) {
					existingList['image.' + match2[1]] = 'text';
				}
			}
			for(var l=0; l<typesToRemove.length; l++) {
				var type = typesToRemove[l];
				var match3 = type.match(/(?:^text\.(\w+)$)/);
				if(match3) { delete existingList['text.' + match3[1]] };
				var match4 = type.match(/(?:^image\.(\w+)$)/);
				if(match4) { delete existingList['image.' + match4[1]] };
			}
			TEXT_EXTENSIONS = [];
			IMAGE_EXTENSIONS = [];
			for (var key in existingList) {
				var value = existingList[key];
				var extension = key.substr(key.indexOf('.')+1);
				if (value == 'text') {
					TEXT_EXTENSIONS.push(extension);
				} else if(value == 'image') {
					IMAGE_EXTENSIONS.push(extension);
				}
			}

			// @collect user's color scheme preference:
			if((typeof oConfig['colorScheme'] != "undefined")
				&& oConfig['colorScheme'] !== null) {
				userColorScheme = oConfig['colorScheme'];
			}
			
			// @collect user's initial window position preference:
			if(
				typeof oConfig['initialPosition'] != "undefined"
				&& oConfig['initialPosition'] !== null
				&& oConfig['initialPosition'].length == 2
				&& !isNaN(parseInt(oConfig['initialPosition'][0]))
				&& !isNaN(parseInt(oConfig['initialPosition'][1]))
				) {
					initPosition = oConfig['initialPosition'];
				}

		}

		/**
		 * Checks whether the given 'file' is to be hidden according to config
		 * regulations.
		 * @method
		 * @private
		 * @param fileUrl { String }
		 * 		The file url to check.
		 */
		function isFileToBeHidden (file) {
			var relUrl = Utils.getRelativePath(file);
			var toHide = oConfig.exclude;
			if (toHide) {
				for(var i=0; i<toHide.length; i++) {
					var testVal = toHide[i];
					testVal = testVal.replace(/\\/g, '/');
					testVal = testVal.replace(/\/$/g, '');
					if (testVal[0] != '/') { testVal = '/' + testVal };
					if(testVal == relUrl) { return true };
					if(relUrl.indexOf(testVal) == 0) {
						if (relUrl[testVal.length] == '/') {
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * Initializes the application UI.
		 * @method
		 * @private
		 * @param oDocument { HTML Document Object }
		 * 		The document object to be used by classes responsible with UI
		 * 		creation.
		 */
		function initUI (oDocument) {
			cssProvider = new CSSProvider(oDocument);
			cssProvider.changeColorScheme(userColorScheme);
			domProvider = new DOMProvider(oDocument);
			uiBuilder = new UIBuilder(domProvider);
			ui = uiBuilder.createMainLayout();
			populateTree(ui.tree);
			
			// @attempt to load default file's content, if there is one:
			if (typeof defaultFilePath != 'undefined') {
				var segments = String(defaultFilePath).split('/');
				var urlSegments = [];
				for(var i=0; i<segments.length; i++) {
					var sgm = segments[i];
					if (!/\w/.test(sgm)) { continue };
					urlSegments.push(sgm);
				};
				var relURL = urlSegments.join('/');
				var appDir = File.applicationDirectory;
				var file = appDir.resolvePath(relURL);
				
				// @the path must finally point to a file:
				if(file.exists) {
					if(!file.isDirectory) {
						if (!isFileToBeHidden(file)) {
							fileSystemWalker.queryFileContent(file);
						}
					}
				}
			}
		}
		
		/**
		 * Fills the tree with the first level of files.
		 * @method
		 * @private
		 * @param treeRoot { HTML Element }
		 * 		An HTML Element representing the root of the tree.
		 */
		function populateTree (treeRoot) {
			var fileListReadyHandler = function(event){
				uiBuilder.displayFilesList(treeRoot, event.body.filesList);
				eventManager.removeListener(
					FILES_LIST_READY_EVENT,
					fileListReadyHandler
				);
			}
			eventManager.addListener(
				FILES_LIST_READY_EVENT,
				fileListReadyHandler
			);
			fileSystemWalker.makeInitialQuery();
		}

		/**
		 * Clears all data that would naturally persist over sessions.
		 * @method
		 * @private 
		 */
		function purge() {
			uiBuilder.unsetClickableItems();
			eventManager.removeListenersFor (FOLDER_STATE_CHANGED_EVENT);
			requestedFilesRegistry.reset();
			context.instance = null;
			delete context.instance;
		}


		
		/**
		 * CLASS
		 * 		WindowsManager
		 * DESCRIPTION
		 * 		Handles window creation and manipulation for this application.
		 * SAMPLE USAGE
		 * 		N/A (internal use only)
		 * @class
		 * @private
		 */
		 function WindowsManager () {
			
			/**
			 * Returns the default display options for a newly created window.
			 * @method
			 * @private
			 * @return { NativeWindowInitOptions }
			 * 		An object specifying display options for a new window. 
			 */
			function getDefWindowOptions () {
				var options = new NativeWindowInitOptions();
				options.type = NativeWindowType.UTILITY;
				return options;
			}
			
			/**
			 * Returns the screen the host application is currently in (we call
			 * "host application" the application that has launched the Source
			 * Viewer).
			 * If the host application spans more than a single screen, the one
			 * displaying a larger area of the host application UI will be 
			 * considered.
			 * If the host application almost equally spans two screns, or if it
			 * spans more than two screens, the primary screen will be returned.
			 * Needless to say, the single screen is returned for single screen
			 * hardware configurations.
			 * @method
			 * @private
			 * @return { Screen } The screen object.
			 */
			function getCurrentScreen () {
				// Get all screens on which our host application is displayed:
				var hostBounds = window.nativeWindow.bounds;
				var screens = Screen.getScreensForRectangle(hostBounds);
				// Get the most important screen showing a portion of the app:
				var largestArea = 0;
				var mostImportantScreen = null;
				var screenOffset = -1;
				for (var i=0; i<screens.length; i++) {
					var screen = screens[i];
					var intersection = screen.bounds.intersection(hostBounds);
					var area = intersection.width * intersection.height;
					if (area > largestArea) {
						largestArea = area;
						mostImportantScreen = screen;
						screenOffset = i;
					}
				}
				return mostImportantScreen;
			}
			
			/**
			 * Will translate the absolute, agnostic coordinates of the given 
			 * rectangle into a relative, screen-dependant set of coordinates. 
			 * 
			 * This is required, as screen coordinates may drammatically vary
			 * based on that screen geographic position -- to the left or to the 
			 * right of the primary screen, for instance.
			 * @method
			 * @private
			 * @return { Rectangle } The translated coordinates. 
			 */
			function positionRectangleOnScreen (screen, rectangle) {
				// Adjust the 'x' coordinate:
				if ((rectangle.x + screen.visibleBounds.x) 
					< screen.visibleBounds.x) {
						rectangle.x = screen.visibleBounds.x; 
				} else {
					var leftPoint = screen.visibleBounds.x + rectangle.x;
					var rightPoint = leftPoint + rectangle.width;
					if (rightPoint <= screen.visibleBounds.width + 
						screen.visibleBounds.x) {
							rectangle.x = leftPoint;
					} else {
						var adjustedLeftPoint = screen.visibleBounds.width - 
							rectangle.width + screen.visibleBounds.x;
						rectangle.x = Math.max(adjustedLeftPoint, 
							screen.visibleBounds.x);
					}
				}
				// Adjust the 'y' coordinate:
				if (rectangle.y < screen.visibleBounds.y) {
					rectangle.y = screen.visibleBounds.y; 
				} else if(rectangle.y + rectangle.height > 
					screen.visibleBounds.height) {
					rectangle.y = screen.visibleBounds.height - 
						rectangle.height;
				}
				return rectangle;
			}

			/**
			 * Centers the given rectangle on the given screen.
			 * @method
			 * @private
			 * @see positionRectangleOnScreen
			 * @return { Rectangle} The centered rectangle
			 */
			function centerRectangleOnScreen (screen, rectangle) {
				rectangle.x = Math.floor(screen.visibleBounds.width - 
					rectangle.width) / 2;
				rectangle.y = Math.floor(screen.visibleBounds.height - 
					rectangle.height) / 2;
				return positionRectangleOnScreen(screen, rectangle);
			}

			/**
			 * Returns the default display boundaries for a newly created 
			 * window.
			 * @method
			 * @private
			 * @return { Rectangle }
			 * 		A rectangle defining the boundaries of this new window.
			 */			
			function getDefaultBoundaries () {
				return new Rectangle (0, 0, 800, 600);
			}
			
			/**
			 * Creates the main window of the application.
			 * @method
			 * @public
			 */
			this.makeMainWindow = function () {
				var screen = getCurrentScreen();
				var bounds = getDefaultBoundaries();
				if (initPosition) {
					bounds.x = initPosition[0];
					bounds.y = initPosition[1];
					bounds = positionRectangleOnScreen (screen, 
						bounds);
				} else {
					bounds = centerRectangleOnScreen(screen, bounds);
				}
				var htmlLoader = HTMLLoader.createRootWindow (
					true,
					getDefWindowOptions(), 
					false,
					bounds
				);
				var domInitHandler = function() {
					isMainWindowOpen = true;
					if(isToBeModal) {
						makeWindowModal (htmlLoader.window, self);
					} else {
						
						// @close our window before parent window gets closed:
						var parWinClosingHandler = function() {
							self.nativeWindow.removeEventListener('closing', 
								parWinClosingHandler);
							self.nativeWindow.removeEventListener('activate', 
								parWindowActivateHandler);
							if(htmlLoader && htmlLoader.window &&
								htmlLoader.window.nativeWindow) {
								WindowsManager.closeWindow(
									htmlLoader.window.nativeWindow);
							}
						}
						self.nativeWindow.addEventListener ('closing', 
							parWinClosingHandler);
							
						// @activate our window when parent gets activated:
						var parWindowActivateHandler = function() {
							if(htmlLoader &&
							htmlLoader.window &&
							htmlLoader.window.nativeWindow) {
								htmlLoader.window.nativeWindow.
									orderInFrontOf(self.nativeWindow);
							}
						}
						self.nativeWindow.addEventListener ('activate', 
							parWindowActivateHandler);
					}
					htmlLoader.window.nativeWindow.addEventListener (
						'close',
						 function(evt) {
						 	isMainWindowOpen = false;
						 }
					);
					var unconditionedClosingHandler = function (event) {
						if (!event.isdefaultPrevented) {
							purge();
						}
						htmlLoader.window.nativeWindow.removeEventListener (
							'closing',
							unconditionedClosingHandler
						);
					}
					htmlLoader.window.nativeWindow.addEventListener (
						'closing',
						unconditionedClosingHandler
					);
					var event = eventManager.createEvent(
						WINDOW_CREATED_EVENT,
						{'window': htmlLoader.window}
					);
					eventManager.fireEvent(event);
					
					// @this is a once-in-a-lifetime run:
					htmlLoader.removeEventListener('htmlDOMInitialize',
						domInitHandler);
				}
				htmlLoader.addEventListener('htmlDOMInitialize',domInitHandler);
				htmlLoader.loadString('');
			}
			
			/**
			 * Makes a window modal to a certain parent window.
			 * @method
			 * @private
			 * @param oWindow { Object Window }
			 * 		The window to be made modal.
			 * @param oParentWindow { Object Window }
			 * 		The parent of the modal window. Any attempt to access the 
			 * 		parent while the modal window is open will fail.
			 */
			function makeWindowModal (oWindow, oParentWindow) {
				
				// @prevent parent window closing:
				var closingHandler = function (event) {
					if (isMainWindowOpen) { 
						event.preventDefault();
						return;
					};
					oParentWindow.nativeWindow.removeEventListener(
						'closing', closingHandler
					);
				}
				oParentWindow.nativeWindow.addEventListener (
					'closing', closingHandler
				);
				
				// @prevent parent window minimizing or maximizing:
				var stateChangingHandler = function (event) {
					if (isMainWindowOpen) { 
						event.preventDefault();
						return;
					};
					oParentWindow.nativeWindow.removeEventListener(
						'displayStateChanging', stateChangingHandler
					);
				}
				oParentWindow.nativeWindow.addEventListener (
					'displayStateChanging', stateChangingHandler
				);
				
				// @prevent parent window moving:
				var movingHandler = function(event) {
					if (isMainWindowOpen) {
						event.preventDefault();
						return;
					};
					oParentWindow.nativeWindow.removeEventListener(
						'moving', movingHandler
					);
				}
				oParentWindow.nativeWindow.addEventListener (
					'moving', movingHandler
				);
				
				// @prevent parent window resizing:
				var resizingHandler = function(event) {
					if(isMainWindowOpen) {
						event.preventDefault();
						return;
					};
					oParentWindow.nativeWindow.removeEventListener(
						'resizing', resizingHandler
					);
				}
				oParentWindow.nativeWindow.addEventListener (
					'resizing', resizingHandler
					
				);
				
				// @make sure parent window will stay behind the modal window:
				var ensureProperOrder = function() {
					if (oWindow && oWindow.nativeWindow &&
					    oParentWindow && oParentWindow.nativeWindow) {
							oWindow.nativeWindow.activate();
							oParentWindow.nativeWindow
								.orderInBackOf(oWindow.nativeWindow);
					}
				}
				
				// @works by default on Windows, hacked on Mac:				
				var osString = runtime.flash.system.Capabilities.os; 
				if(osString.indexOf('Windows') != -1) {
					oWindow.nativeWindow.addEventListener ('deactivate',
						ensureProperOrder, false);
				} else {
					var EPO_INTERVAL = window.setInterval(ensureProperOrder, 0);
				}
				
				// @prevent user from interacting with the parent's content:
				var parentContentBlocker;
				var parentDomProvider = new DOMProvider(oParentWindow.document);
				var parentLayoutProvider = new LayoutProvider();
				parentContentBlocker = parentDomProvider.makeDiv();
				parentLayoutProvider.setupBox(parentContentBlocker);
				parentLayoutProvider.setupStretched(parentContentBlocker);
				parentContentBlocker.style.backgroundColor = 'black';
				parentContentBlocker.style.opacity = '0.1';
				var bodyElement = parentContentBlocker.parentNode;
				var localCSSProvider = new CSSProvider(oParentWindow.document);

				// @restore parent windows' state:
				var modalWindowClosingHandler = function(evt) {
					if (!evt.isDefaultPrevented()) {
						window.clearInterval(EPO_INTERVAL);
						if(parentContentBlocker) {
							var parEl = parentContentBlocker.parentNode;
							parEl.removeChild(parentContentBlocker);
						}
						oWindow.nativeWindow.removeEventListener('closing',
							modalWindowClosingHandler);
					}
				}
				oWindow.nativeWindow.addEventListener ('closing',
					modalWindowClosingHandler);
			}
		}

		/**
		 * Programmatically closes the given native window, while still allowing
		 * it to opt out, via preventing the default behavior of the 'closing'
		 * event.
		 * @method
		 * @public
		 * @static
		 * @param oWindow { NativeWindow }
		 * 		The instance of the NativeWindow class that is to be closed.
		 * @return
		 * 		True, if the given window has been closed; false otherwise.
		 */		
		WindowsManager.closeWindow = function (oWindow) {
			var closeEvent = new runtime.flash.events.Event (
		    	runtime.flash.events.Event.CLOSING, true, true );
		    oWindow.dispatchEvent(closeEvent);
		    if (!closeEvent.isDefaultPrevented()){
		        oWindow.close();
		        return true;
		    } else {
		      return false;
			}
		}



		/**
		 * CLASS
		 * 		UIBuilder
		 * DESCRIPTION
		 * 		Private class that handles the application's layout creation.
		 * SAMPLE USAGE
		 * 		N/A (internal use only)
		 * @class
		 * @private
		 * @param oDomProvider { DOMProvider }
		 * 		An instance of the DOMProvider class. Required, in order to be 
		 * 		able to build the layout blocks.
		 */
		function UIBuilder (oDomProvider) {

			// @let private methods see own class' instance:			
			var that = this;
			
			/**
			 * Updates the title shown by the Source Viewer window.
			 * @method
			 * @private 
			 * @param fileUrl { String }
			 * 		The title to show.
			 */
			this.updateTitle = function(file) {
				var relPath = Utils.getRelativePath(file);
				var oDocument = dProvider.getClientDocument ();
				oDocument.title = APP_LEGAL_NAME + " - " + relPath;
			}

			/**
			 * Custom initialization for the class UIBuilder.
			 * @method
			 * @private
			 */
			function init() {
				eventManager.addListener(FOLDER_STATE_CHANGED_EVENT, 
					function (event) {
						uiBuilder.toggleItemContent(event.body.folder);
					}
				)
				var fileContentReadyHandler = function (event) {
					var content = event.body.content;
					var type = event.body.type? event.body.type : 'text';
					if (type == 'text') {
						uiBuilder.showText(content);
					} else if (type == 'image'){
						uiBuilder.showImage(content);
					}
					that.updateTitle(event.body.file);
				}
				eventManager.addListener (
					FILE_CONTENT_READY_EVENT, fileContentReadyHandler);
			}
			
			/**
			 * Displays the content of the selected file item inside the source
			 * area element.
			 * @method
			 * @public
			 * @param content { String }
			 * 		The content to be displayed.
			 * @param oDocument { HTML Document Object }
			 * 		The document object to display the content in.
			 * @param uid { String }
			 * 		The id for an HTML Element that will host provided content.
			 */
			this.showText = function(content) {
				var oDocument = dProvider.getClientDocument();
				var el = oDocument.getElementById('sourceCodeArea');
				var txtNode = (el.getElementsByTagName('span')[0]|| 
					dProvider.makeText(' ', el, 'sourceCodeText')).firstChild;
				txtNode.nodeValue = content;
				if(el.style.visibility != 'visible') {
					el.style.visibility = 'visible';
				}
				var noContentText = oDocument.getElementById('srcAreaBgText');
				if (noContentText.style.visibility != 'hidden') {
					noContentText.style.visibility = 'hidden';
				}
				var ruler = oDocument.getElementById('lineNoRuler');
				uiBuilder.initRuler (ruler, content);
				if(ruler.style.visibility != 'visible') {
					ruler.style.visibility = 'visible';
				}
				var img;
				while (img = el.getElementsByTagName('img')[0]){
					el.removeChild(img);
				}
				el.scrollTop = 0;
				el.scrollLeft = 0;
				ruler.scrollTop = 0;
			}
			
			/**
			 * Displays the selected file item, provided it is an image.
			 * @method
			 * @public
			 * @param { String }
			 * 		The application root relative url of the image to display.
			 */
			this.showImage = function(url) {
				var oDocument = dProvider.getClientDocument();
				var el = oDocument.getElementById('sourceCodeArea');
				el.innerHTML = "";
				var ruler = oDocument.getElementById('lineNoRuler');
				ruler.innerHTML = "";
				var img = dProvider.makeElement ('img', el, 'imageContent');
				img.src = url;
				el.scrollTop = 0;
				el.scrollLeft = 0;
			}
			
			/**
			 * The DOM provider instance used for building UI elements.
			 * @field
			 * @private
			 */
			var dProvider = oDomProvider;

			/**
			 * Creates an item in the files list on the left.
			 * @method
			 * @public
			 * @param parentEl { HTML Element }
			 * 		The HTML element to build the new item in. Both 'ul' or 'li'
			 * 		elements can be specified here.
			 * @param text { String }
			 * 		The text to display inside the newly created item (i.e, file
			 * 		name).
			 * @param className { String }
			 * 		The css class to apply to the newly created element.
			 * @return { HTML Element }
			 * 		The 'li' element created
			 */
			this.makeItem = function(parentEl, text, className) {
				var item = makeTreeItem(parentEl, text, className);
				return item;
			}
			
			/**
			 * Transparently creates a generic tree item, regardless of the 
			 * actual HTML Element given as parent.
			 * @method
			 * @private
			 * @param parentEl { HTML Element }
			 * 		The HTML element to build the new item in. Both 'ul' or 'li'
			 * 		elements can be specified here.
			 * @param text { String }
			 * 		The text to display inside the newly created item (i.e, file
			 * 		name).
			 * @param className { String }
			 * 		The css class to apply to the newly created element.
			 */
			function makeTreeItem (parentEl, text, className) {
				var isUL = 
					parentEl.nodeName &&
					parentEl.nodeName.toLowerCase() == 'ul';
				var isLI = 
					parentEl.nodeName &&
					parentEl.nodeName.toLowerCase() == 'li';
				var clsName = className? className : 'item';
				var item;
				if(isUL) {item = dProvider.makeElement('li', parentEl, clsName)} 
				else if(isLI) {
					var wrapper = 
						parentEl.getElementsByTagName('ul')[0] || 
						dProvider.makeElement('ul', parentEl);
					item = dProvider.makeElement('li', wrapper, clsName);
				}
				var textClsName = className? className+'Text' : 'itemText';
				var txt = dProvider.makeText(text, item, textClsName);
				txt.addEventListener('mouseover', function (event){
					var evt = eventManager.createEvent (
						ITEM_MOUSE_OVER_EVENT,
						{ htmlElement: event.target }
					);
					eventManager.fireEvent(evt);
				}, false);
				txt.addEventListener('mouseout', function (event){
					var evt = eventManager.createEvent (
						ITEM_MOUSE_OUT_EVENT,
						{ htmlElement: event.target }
					);
					eventManager.fireEvent(evt);
				}, false);
				txt.addEventListener('click', function( event ) {
					var evt = eventManager.createEvent (
						ITEM_MOUSE_CLICK_EVENT,
						{ htmlElement: event.target }
					);
					eventManager.fireEvent(evt);
				}, false);
				return item;
			}
			
			/**
			 * Possibly returns a tree item that matches the given path. 
			 * @method
			 * @public
			 * @param treeRoot { DOM Element }
			 * 		The root, <ul> DOM Element of the tree to search.
			 * @param path { String }
			 *		The path to look for. It is expected to be an application 
			 * 		root relative path. Illegal or absolute paths will fail
			 * 		silently.
			 * @return { DOM Element }
			 * 		The <li> DOM Element that matches the given path, or null in
			 * 		case of failure.
			 */
			this.getTreeItemByPath = function (treeRoot, path) {
				var getItemsLabel = function(item) {
					var sp = item.getElementsByTagName('span')[0];
					var txt = sp.innerText;
					txt = txt.replace(/^\[([^\]]+)\]$/, '$1');
					return txt;
				}
				var getChildByLabel = function(parentEl, childLabel) {
					var children = parentEl.childNodes;
					for (var i=0; i<children.length; i++) {
						var child = children.item(i);
						if(child.nodeType != 1) { continue };
						if(child.nodeName.toLowerCase() != 'li') { continue };
						var labelValue = getItemsLabel(child);
						if (labelValue == childLabel) {
							return child;
						}
					}
				}
				var segments = path.split('/');
				segments.reverse();
				var current = treeRoot;
				do {
					if(segments.length == 0) { break };
					var segment = segments.pop();
					if(segment.length == 0) { continue };
					var item = getChildByLabel(current, segment);
					if(item) { current = item } 
					else { break };
				} while(true);
				return (current && (segments.length == 0))? current: null;
			}
			
			/**
			 * Displays all given files as sibling items in the tree.
			 * @method
			 * @public
			 * @param parentEl { HTML Element }
			 * 		The HTML element to build the list in. This can be either a
			 * 		'li' node or the root, 'ul' node.
			 * 		Note:
			 * 		Any required wrapping is done transparently.
			 * @param files { Array }
			 * 		An array containing File objects that are to be
			 * 		listed.
			 */
			this.displayFilesList = function(parentEl, files) {
				this.removeProgressIndicator(parentEl);
				for(var i=0; i<files.length; i++) {
					var file = files[i];
					var class_name = 'leaf';
					var name = file.name;
					if(file.isDirectory) {
						name = '[' +name+ ']';
						class_name = 'branch';
					}
					var item = this.makeItem(parentEl, name, class_name);
					var event = eventManager.createEvent(FILE_LISTED_EVENT,
						{'file': file, 'item': item});
					eventManager.fireEvent(event);
				}
			}
			
			/**
			 * Adds visual clues that the given item has children i.e., a 
			 * different CSS style, a progress indicator.
			 * @method
			 * @private
			 * @param item { HTML Element }
			 * 		The element that is to be marked as non empty. 
			 */
			this.markItemAsNonEmpty = function(item) {
				item.className = 'nonEmptyBranch';
				var textEl = item.getElementsByTagName('span')[0];
				textEl.className = 'nonEmptyBranchText';
				this.addProgressIndicator(item);
				this.toggleItemContent(item);
				this.setupAsClickableFolderItem(textEl);
			}
			
			/**
			 * Maintains a list with all clickable items.
			 * @field
			 * @private
			 */
			var clickableItems = [];
			
			/**
			 * Explicitelly unsets all items previously set as clickable.
			 * @method
			 * @public
			 */
			this.unsetClickableItems = function() {
				for(var i=0; i<clickableItems.length; i++) {
					var item = clickableItems[i];
					item.onclick = "";
				}
				clickableItems = [];
			}
			
			/**
			 * Sets the given item as a clickable folder, i.e., clicking on it
			 * will fetch its children, and will collapse/expand it afterwards.
			 * @method
			 * @public
			 * @param item { HTML Element }
			 * 		The element that is to be setup as a clickable folder.
			 */
			this.setupAsClickableFolderItem = function(item) {
				var cb = function(DOMEvent) {
					var el = DOMEvent.target;
					var notYetExpanded = hasProgressIndicator(el.parentNode);
					if(notYetExpanded) {
						var event = eventManager.createEvent(
							FOLDER_FIRST_CLICKED_EVENT, 
							{'folder': el.parentNode}
						);
						eventManager.fireEvent(event);
					} else {
						var event = eventManager.createEvent(
							FOLDER_STATE_CHANGED_EVENT, {'folder': 
							el.parentNode});
						eventManager.fireEvent(event);
					}
				}
				item.onclick = cb;
				clickableItems.push(item);
			}

			/**
			 * Sets the given file as clickable, i.e., clicking on it will 
			 * display its content in the right pane.
			 * @method
			 * @private
			 * @param item { HTML Element }
			 * 		The element that is to be setup as a clickable file.
			 * @param file { File }
			 * 		The file object associated to the item to setup.
			 */
			this.setupAsClickableFileItem = function(item, file) {
				var fileItemClickHandler = function(){
					var event = eventManager.createEvent(FILE_ITEM_CLICKED, {
						'item': item, 'file': file });
					eventManager.fireEvent(event);
				}
				item.onclick = fileItemClickHandler;
				clickableItems.push(item);
			}

			/**
			 * Sets up visual feedback in response to user interacting with the
			 * tree items (i.e., mouse-hovering an item, etc).
			 * @method
			 * @private
			 * @see CSSProvider.dynamicCSS
			 */
			function hookItemsVisualFeedback() {
				var current = arguments.callee;

				// @visual effect for mouse over on tree items.
				var hoverCb = function(event) {
					var el = event.body.htmlElement;
					if (current.lastClicked &&
						current.lastClicked === el) {
						return;
					}
					cssProvider.setStyle(el, 'background-color', 'darkNeutral');
				};
				eventManager.addListener(ITEM_MOUSE_OVER_EVENT, hoverCb);
				// @visual effect for mouse out on tree items.
				var outCb = function(event) {
					var el = event.body.htmlElement;
					if (current.lastClicked &&
						current.lastClicked === el) {
						return;
					}
					el.style.backgroundColor = 'transparent';
				};
				eventManager.addListener (ITEM_MOUSE_OUT_EVENT, outCb);
				// @visual effect for mouse click on tree items.
				var clickCb = function(event) {
					var el = event.body.htmlElement;
					var old = current.lastClicked; 
					if (old) {
						old.style.backgroundColor = 'transparent';
					}
					cssProvider.setStyle(el, 'background-color',
						'lighterColorAccent');
					current.lastClicked = el;
				};
				eventManager.addListener(ITEM_MOUSE_CLICK_EVENT, clickCb);
			}
			
			/**
			 * Alternatively collapses or expands the children of a tree item.
			 * @method
			 * @public
			 * @param { HTML Element }
			 * 		The item to be collapsed or expanded (based on its current 
			 * 		state).
			 */
			this.toggleItemContent = function(item) {
				var wrapper = item.getElementsByTagName('ul')[0];
				if (wrapper) {
					var visible = wrapper.style.display == 'none'? false: true;
					wrapper.style.display = visible? 'none' : 'block';
				}
			}
			
			/**
			 * Adds a special, temporary child to the given item, indicating 
			 * that its actual children are being loaded in background.
			 * @method
			 * @public
			 * @param item { HTML Element }
			 * 		The element to signalize background loading for.
			 */
			this.addProgressIndicator = function(item) {
				if (!hasProgressIndicator(item)) {
					makeTreeItem (item, 'loading...', 'progress');
				}
			}
			
			/**
			 * Removes the 'progress indicator' from the given item, should it 
			 * have one. 
			 * @method
			 * @public
			 * @param item { HTML Element }
			 * 		The element to remove the progress indicator from.
			 */
			this.removeProgressIndicator = function(item) {
				var children = item.getElementsByTagName('span');
				for(var i=0; i<children.length; i++) {
					var child = children[i];
					var value = child.innerHTML;  
					if (value == 'loading...') {
						var itemToRemove = child.parentNode;
						var _parent = itemToRemove.parentNode;
						_parent.removeChild(itemToRemove);
						return;
					}
				}
			}
			
			/**
			 * Checks whether a given item currently presents a 'progress 
			 * indicator' instead of its actual content.
			 * @method
			 * @private
			 * @param item { HTML Element }
			 * 		The element to be checked.
			 * @return { Boolean }
			 * 		True if the element has a 'progress indicator' in place, 
			 * 		false otherwise.
			 */
			function hasProgressIndicator (item) {
				var firstWrapper = item.getElementsByTagName('ul')[0];
				if(firstWrapper) {
					var firstTextEl = firstWrapper.
						getElementsByTagName('span')[0];
					if (firstTextEl) {
						var value = firstTextEl.innerHTML;
						if (value == 'loading...') {return true};
					}
				}
				return false;
			}
						
			/**
			 * Creates the header of the application UI.
			 * @method
			 * @private
			 * @param parentEl { HTML Element }
			 * 		The HTML Element to build in.
			 * @return { HTML Element }
			 * 		The HTML Element holding the UI header.
			 */
			function createHeader (parentEl) {
				var header = dProvider.makeDiv( parentEl, 'rcHeader' );
				layoutProvider.setupBox(header, {h: 3})
				layoutProvider.setupStretched(header, { bottom: -1 })
				dProvider.makeText(ADOBE_TOKEN, header, 'hdrToken');
				dProvider.makeText(COPYRIGHT_TOKEN, header, 'hdrToken');
				dProvider.makeText(AIR_TOKEN, header, 'hdrToken');
				dProvider.makeText(TRADEMARK_TOKEN, header, 'hdrToken');
				dProvider.makeText(APP_LEGAL_NAME, header, 'hdrToken');
				dProvider.makeText(
					APP_VERSION_MAJOR_MINOR,
					header,
					'vToken'
				);
				return header;
			}
			
			/**
			 * Creates the left side of the application UI -- the side bar that
			 * contains the tree list showing files and folders.
			 * @method
			 * @private
			 * @param parentEl { HTML Element }
			 * 		The HTML Element to build in.
			 * @return { HTML Element }
			 * 		The HTML Element holding the UI sidebar.
			 */
			function createSideBar (parentEl) {
				var left = dProvider.makeDiv( parentEl, 'rcTree' );
				layoutProvider.setupBox(left, {w: 13.85});
				layoutProvider.setupStretched(left, {top: 3.2, bottom: 2, right: -1});
				dProvider.makeText(TREE_DESCRIPTION_MESSAGE, left,
					'listDescr');
				var lstBackground = dProvider.makeDiv(left, 'listBackground');
				layoutProvider.setupBox(lstBackground);
				layoutProvider.setupStretched(lstBackground, {top:3, right:0.5, bottom:1, 
					left:0.5});
				return left;
			}
			
			/**
			 * Creates the tree list that displays source files.
			 * @method
			 * @private
			 * @param parentEl { HTML Element }
			 * 		The HTML element to build the tree in.
			 * @return { HTML Element }
			 * 		The HTML Element holding the UI tree's root.
			 */
			function createTree (parentEl) {
				var root = dProvider.makeElement('ul', parentEl, 'tree');
				layoutProvider.setupBox(root);
				layoutProvider.setupStretched(root, {top:2.5, right:1, 
					bottom:0.5, left: 1});
				root.style.overflow = 'auto';
				return root;
			}
			
			/**
			 * Creates the right side of the application UI -- the area that 
			 * displays selected file's content.
			 * @method
			 * @private
			 * @param parentEl { HTML Element }
			 * 		The HTML Element to build in.
			 * @return { HTML Element}
			 * 		The HTML Element holding the UI content area.
			 */
			function createContentArea (parentEl) {
				var right = dProvider.makeDiv( parentEl, 'rcContent' );
				layoutProvider.setupBox(right);
				layoutProvider.setupStretched(right, {top: 3.2, left: 14, bottom: 2});
				var txt = dProvider.makeText('no content to display', right, 
					'noContent');
				layoutProvider.setupBox(txt, {w:10, h:1});
				layoutProvider.setupCentered(txt);
				txt.setAttribute('id', 'srcAreaBgText');
				return right; 
			}
			
			/**
			 * Creates the ruler showing line numbering for displayed file's 
			 * content.
			 * @method
			 * @private
 			 * @param parentEl { HTML Element }
			 * 		The HTML Element to build in.
			 * @return { HTML Element }
			 * 		The HTML Element holding the UI ruler
			 */
			function createRuler(parentEl) {
				var lnNumb = dProvider.makeDiv(parentEl, 'ruler');
				layoutProvider.setupBox (lnNumb, {w:4});
				layoutProvider.setupStretched (lnNumb, {left:0.5, top:0.5, 
					right:-1, bottom:0.8 });
				lnNumb.setAttribute('id', 'lineNoRuler');
				return lnNumb;
			}
			
			/**
			 * Creates the UI element that will display the selected source 
			 * file's source code.
			 * @method
			 * @private
 			 * @param parentEl { HTML Element }
			 * 		The HTML Element to build in.
			 * @return { HTML Element }
			 * 		The HTML Element holding the UI source area.
			 */
			function createSourceArea(parentEl) {
				var srcArea = dProvider.makeDiv(parentEl, 'srcCodeArea');
				layoutProvider.setupBox (srcArea);
				layoutProvider.setupStretched (srcArea, {left:4.5, top:0.5, 
					right:0.35, bottom:0.8, });
				srcArea.setAttribute('id', 'sourceCodeArea');
				return srcArea;
			}
			
			/**
			 * Creates the footer of the application UI.
			 * @method
			 * @private
			 * @param parentEl { HTML Element }
			 * 		The HTML Element to build in.
			 * @return { HTML Element }
			 * 		The HTML Element holding the UI footer area.
			 */
			function createFooter (parentEl) {
				var footer = dProvider.makeDiv( parentEl, 'rcFooter' );
				layoutProvider.setupBox(footer, {h: 2});
				layoutProvider.setupStretched(footer, {top: -1});
				dProvider.makeText(COPYRIGHT_MESSAGE, footer, 
					'copyrightText');
				return footer;
			}
			
			/**
			 * Creates the application user interface.
			 * @method
			 * @public
			 * @param parentEl { HTML Element }
			 * 		The HTML Element to build in.
			 * @return { Object }
			 * 		A hash with all UI elements created.
			 */
			this.createMainLayout = function(parentEl) {
				var header = createHeader(parentEl);
				var sidebar = createSideBar(parentEl);
				var tree = createTree(sidebar);
				this.addProgressIndicator (tree);
				var contentArea = createContentArea(parentEl);
				var ruler = createRuler(contentArea);
				var sourceArea = createSourceArea(contentArea);
				linkScrollable(ruler, sourceArea);
				var footer = createFooter(parentEl);
				cssProvider.applyCSS();
				hookItemsVisualFeedback();
				return {
					'header'		:	header,
					'sidebar'		:	sidebar,
					'tree'			:	tree,
					'contentArea'	:	contentArea,
					'ruler'			:	ruler,
					'sourceArea'	:	sourceArea,
					'footer'		:	footer
				};
			}
			
			/**
			 * Initiates the linked ruler that shows line numbers for the file 
			 * content being displayed.
			 * @method
			 * @public
			 * @param parentEl { HTML Element }
			 * 		The HTML Element to build in.
			 * @assocText { String }
			 * 		A formatted string to show line numbers for. The ruler will 
			 * 		add a new number for each occurence of the new line 
			 * 		character in this string.
			 */
			this.initRuler = function(parentEl, assocText) {
				var count = 0;
				var index = 0
				var str = '';
				domProvider.destroyContent(parentEl);
				do {
					var searchIndex = assocText.indexOf("\n", index);
					count++;
					str += count + "\n";
					index = searchIndex + 1;
					if (searchIndex == -1) { 
						str+="EOF";
						break;
					};
				} while (true);
				dProvider.makeText(str, parentEl, 'rulerText');
			}
			
			/**
			 * Links two elements, so that they scroll together.
			 * @method
			 * @private
			 * @param scrollable { HTML Element }
			 * 		The target element to be linked.
			 * @param related { HTML Element }
			 * 		The source element to link with.
			 * @param proxy { Function }
			 * 		An optional function that specifies how the scroll value of 
			 * 		the 'related' element applies to the 'scrollable' element. 
			 * 		Default is to just pass the 'scrollTop' value from 'related' 
			 * 		to 'scrollable'.
			 */
			function linkScrollable (scrollable, related, proxy) {
				// @private function; scrolls a given element.
				var scrollElement = function(element, offset) {
					element.scrollTop = offset;	
				}
				related.addEventListener('scroll',  function() {
					scrollElement (scrollable, 
						Utils.isFunction(proxy)? proxy.call(this, related.scrollTop):
						related.scrollTop
					); 
				}, false );
			}

			// @perform custom initialization of this class.
			init();
		}



		/**
		* CLASS
		* 	FileSystemWalker
		* DESCRIPTION
		* 	Private class that encapsulates functionality related to file
		* 	system traversal (i.e., recursively iterating through a folder's
		* 	children).
		* SAMPLE USAGE
		* 	N/A (internal use only)
		* @class
		* @private
		*/
		function FileSystemWalker() {
			
			// @let private methods see own class' instance:			
			var that = this;

			/**
			 * Keeps a record of all listed files.
			 * @field
			 * @private
			 */
			var listedFiles = {};
			
			/**
			 * Custom initialization for the FileSystemWalker class.
			 * @method
			 * @private
			 */
			function init() {
				eventManager.addListener(FOLDER_FIRST_CLICKED_EVENT,
					function(event) {
						var folderItem = event.body.folder;
						var file = getRegisteredFileByItem(folderItem);
						// @nested listener
						eventManager.addListener(FILES_LIST_READY_EVENT, 
							function(evt) {
								uiBuilder.displayFilesList(folderItem, 
									evt.body.filesList);
								// @this is a one-time callback:
								eventManager.removeListener(
									FILES_LIST_READY_EVENT, arguments.callee);
							}
						)
						queryChildren(file);
					}
				);
				eventManager.addListener(FOLDER_CHECKED_EVENT, function(event){
					var file = event.body.folder;
					var item = getRegisteredFile(file.url).item;
					var hasChildren = event.body.result;
					if(hasChildren) {
						uiBuilder.markItemAsNonEmpty(item);
					}
				});
				eventManager.addListener(FILE_LISTED_EVENT, function(event){
					var file = event.body.file;
					var item = event.body.item;
					registerListedFile (file, item);
					if (file.isDirectory) {
						queryIfHasChildren(file);
					} else {
						uiBuilder.setupAsClickableFileItem(item, file);
					}
				});
				eventManager.addListener(FILE_ITEM_CLICKED, function(event){
					var file = event.body.file;
					var oDocument = event.body.item.ownerDocument;
					that.queryFileContent(file, oDocument);
				})
			}

			/**
			 * Checks whether the given 'file' object has already been listed.
			 * @method
			 * @private
			 * @param file { File }
			 * 		A file object to look up.
			 * @return { Boolean }
			 * 		True is the file was already listed, false otherwise. 
			 */
			function isFileAlreadyListed (file) {
				return listedFiles[file.url]? true: false;
			}

			/**
			 * Retrieves a file previously registered as 'already listed'.
		 	 * @method
			 * @private
			 * @param url { String }
			 * 		The url of the file to retrieve.
			 * @return { Object }
			 * 		A hash with two keys, 'file': the registered file, and 
			 * 		'item': its associated item. Returns undefined if the file
			 * 		cannot be found. 
			 */
			function getRegisteredFile (url) {
				return listedFiles[url];
			}
			
			/**
			 * Performs a reverse lookup through the registry, possibly finding
			 * the file associated with the given item.
			 * @method
			 * @private
			 * @param item { HTML Element }
			 * 		The item associated with the file object to search for.
			 * @return { File }
			 * 		The associated file object, or null if it cannot be found.
			 */
			function getRegisteredFileByItem (item) {
				for (var url in listedFiles) {
					var entry = listedFiles[url];
					if(entry.item == item) {
						return entry.file;
					}
				}
				return null;
			}

			/**
			 * Registers a file as 'already listed'.
			 * @method
			 * @private
			 * @param file { File }
			 * 		A file object to register.
			 * @param item { HTML Element }
			 * 		The HTML element that has been created in order to list the 
			 * 		file.
			 */  
			function registerListedFile (file, item) {
				var item = item? item: null;
				listedFiles[file.url] = {'file':file, 'item': item};
			};

			/**
			 * Retrieves direct children of the application directory folder.
			 * All subsequent retrieval is made on demand.
			 * @method
			 * @public
			 */
			this.makeInitialQuery = function() {
				var appDir = File.applicationDirectory;
				queryChildren (appDir);
			}

			/**
			 * Lists the children of the given parent file, provided it is a 
			 * directory.
			 * @method
			 * @private
			 * @param parentFile { File }
			 * 		The directory file to list.
			 */
			function queryChildren (parentFile) {
				if (!parentFile.isDirectory) {return};
				parentFile.addEventListener( 'directoryListing', function(e){
					var files = [];
					for (var i=0; i<e.files.length; i++) {
						var file = e.files[i];
						if(isFileToBeHidden(file)) {continue};
						files.push(file);
					};
					files.sort (function(a, b) {return b.isDirectory?-1:
						!a.isDirectory? 1: 0;
					});
					var evt = eventManager.createEvent (
						FILES_LIST_READY_EVENT, {'filesList': files});
					eventManager.fireEvent(evt);
				}, false);
				parentFile.getDirectoryListingAsync();
			}
			
			/**
			 * Checks whether the given file object has children, i.e., it is a
			 * directory containing other files.
			 * @method
			 * @private
			 * @param file { File }
			 * 		The file object to check.
			 */
			function queryIfHasChildren (file) {
				file.addEventListener('directoryListing', function( event ) {
					var result = event.files.length? true: false;
					var evt = eventManager.createEvent (
						FOLDER_CHECKED_EVENT,
						{folder: file, 'result': result}
					);
					eventManager.fireEvent(evt);
				});
				file.getDirectoryListingAsync();
			}
			
			/**
			 * Fires a FILE_CONTENT_READY_EVENT, thus causing the given content
			 * to be displayed in the right pane.
			 * @method
			 * @private
			 * @param content { String }
			 * 		The content to be shown.
			 * @param title { String }
			 * 		Optional. The title to be shown by the window title bar.
			 * @param type { String }
			 * 		Optional, defaults to TEXT_TYPE. Controlls whether the 
			 * 		content provided will be displayed as such, or set as the
			 * 		"src" attribute of an <image> HTML Element.
			 */
			function showContent (file, content) {
				var evtBody = {};
				if(content) {
					evtBody.content = content;
					evtBody.type = TEXT_TYPE;
				} else {
					evtBody.content = file.url;
					evtBody.type = IMAGE_TYPE;
				}
				evtBody.file = file;
				var event = eventManager.createEvent (
					FILE_CONTENT_READY_EVENT, evtBody
				);
				eventManager.fireEvent(event);	
				lastFileShown = file.url; 			
			}
			
			/**
			 * The last file whose content has been displayed.
			 * @field
			 * @private
			 */
			var lastFileShown;
			
			/**
			 * Possibly obtains the text content of the given file object.
			 * @method
			 * @public
			 * @param file { File }
			 * 		The file object to retrieve text content from.
			 * @param oDocument { HTML Document Object}
			 * 		The associated document object to display retrieved content
			 * 		in.
			 */
			this.queryFileContent = function(file) {
				if(file && file.exists && !file.isDirectory) {
					if(!isLegalFile(file)) {
						showContent(file, CANNOT_READ_TEXT_MESSAGE);
						return;
					}
				}
				if (!requestedFilesRegistry.lookUp(file.url)) {
					requestedFilesRegistry.add(file);
					showFileOpenMessage(file);
					var fStr = new FileStream();
					var fileContent = new runtime.flash.utils.ByteArray();
					var loaded = 0;
					var total = 0;
					var count = 0;
					var fsProgressHandler = function(event) {
						loaded = event.bytesLoaded;
						total = event.bytesTotal;
						showFileProgressMessage(file, loaded, total);
						if(isTextFile(file)) {
							var bytesAvailable = fStr.bytesAvailable;
							fStr.readBytes(fileContent, count, bytesAvailable);
							count += bytesAvailable;							
						};
					}
					var fsCompleteHandler = function() {
						fsProgressHandler.call(that, {
							'bytesLoaded': total,
							'bytesTotal': total
						});
						fStr.close();
						var cnt = null;
							if(fileContent.length>0){
								fileContent.position = 0;
								cnt = fileContent.readUTFBytes(fileContent.length);
							}
						var reqFile = requestedFilesRegistry.lookUp(file.url);
						reqFile.content = cnt;
						window.setTimeout(showContent, 10, file, cnt);
					}
					var fsErrorHandler = function(event) {
						var msg = IO_ERROR_MESSAGE+ '\n' +event.errorID +'\n'+
							event.text;
							fStr.close();
						showContent(file, msg);
					}
					fStr.addEventListener('progress', fsProgressHandler, false);
					fStr.addEventListener('complete', fsCompleteHandler, false);
					fStr.addEventListener('ioError', fsErrorHandler, false);
					fStr.openAsync(file, 'read');
				} else {
					if(file.url != lastFileShown) {
						var reqFile = requestedFilesRegistry.lookUp(file.url);
						var loadedContent = reqFile.content;
						showContent(file, loadedContent);
					}
				}
			}

			/**
			 * Shows a message, that a file is about to be opened.
			 * @method
			 * @private
			 * @param file { File Object }
			 * 		The file that is about to be opened.
			 */
			function showFileOpenMessage(file) {
				var msg = "" +
					"now opening file: "+
					file.name;
				var event = eventManager.createEvent(
					FILE_CONTENT_READY_EVENT,
					{ 'content': msg, 'file': file }
				);
				eventManager.fireEvent(event);
			}
			
			/**
			 * Shows a message with progress information when opening large 
			 * files.
			 * @method
			 * @private
			 * @param file { File Object }
			 * 		The file being opened.
			 * @param current { Number }
			 * 		The amount of bytes loaded that far
			 * @param total { Number }
			 * 		The amount of bytes to load.
			 */
			function showFileProgressMessage(file, current, total) {
				current = Math.round(current/1024);
				total = Math.round(total/1024);
				var fileName = file.name;
				var msg = "opening " +fileName+ ": " +current+ " of " 
					+total+ " Kb";
				var event = eventManager.createEvent(
					FILE_CONTENT_READY_EVENT,
					{ 'content': msg, 'file': file }
				);
				eventManager.fireEvent(event);
			}
			
			/**
			 * Checks whether the given file is of a legal type. We do not want
			 * to retrieve text for binary files, for instance.
			 * @method
			 * @private
			 * @param file { File }
			 * 		The file object to check.
			 * @return { Boolean }
			 * 		Returns true if the file extension is in the 
			 * 		VALID_EXTENSIONS list, false otherwise.
			 */
			function isLegalFile(file) {
				return (isTextFile(file) || isImageFile(file));
			}
			
			/**
			 * Checks whether the given file is of a <text> type.
			 * @method
			 * @private
 			 * @param file { File }
			 * 		The file object to check.
			 * @return { Boolean }
			 * 		Returns true if the file extension is of the <text> type,
			 * 		false otherwise.
			 */
			function isTextFile(file) {
				return checkFileExtension(file, TEXT_EXTENSIONS)
			}
			
			/**
			 * Check whether the given file is of a <image> type.
			 * @method
			 * @private
			 * @param file { File }
			 * 		The file object to check.
			 * @return { Boolean }
			 * 		Returns true if the file extension is of the <image> type,
			 * 		false otherwise. 
			 */
			function isImageFile(file) {
				return checkFileExtension(file, IMAGE_EXTENSIONS)
			}
			
			/**
			 * Checks whether given file's extension matches one of the 
			 * extensions in the list.
			 * @method
			 * @private
			 * @param file { File }
			 * 		The file object to check.
			 * @param extensionList { Array }
			 * 		The extension list to check against.
			 */
			function checkFileExtension (file, extensionList) {
				var fileName = file.name;
				var match = fileName.match(/\.([^\.]*)$/);
				var extension = (match? match[1] : fileName).toLowerCase();
				for(var i=0; i<extensionList.length; i++) {
					if (extensionList[i] == extension) {
						return true;
					}
				}
				return false;
			}
			
			// @perform custom initialization of this class.
			init();
		}


			
		/**
		 * CLASS
		 * 		RequestedFilesRegistry
		 * DESCRIPTION
		 * 		Provides bare bones functionality for caching files being
		 * 		opened.
		 * USAGE
		 * 		N/A (Internal use only).
		 */
		function RequestedFilesRegistry() {

			/**
			 * The cache itself.
			 * @field
			 * @private 
			 */
			var registry = {};

			/**
			 * Adds a file to the registry.
			 * @method
			 * @public
			 * @param file { File Object }
			 * 		The file to add.
			 */
			this.add = function(file) {
				var reqFile = new RequestedFile(file.url);
				registry[file.url] = reqFile;
			}

			/**
			 * Clears the registry.
			 * @method
			 * @public
			 */
			this.reset = function() {
				registry = {};
			}

			/**
			 * Checks whether the given file exists in the registry.
			 * @method
			 * @public
			 * @param url { String }
			 * 		The file url to look for.
			 * @return { RequestedFile Object }
			 * 		The coresponding object, or null if it cannot be found.
			 */
			this.lookUp = function(url) {
				return registry[url]? registry[url]: null;
			}



			/**
			 * CLASS
			 * 		RequestedFile
			 * DESCRIPTION
			 * 		Represents one single item in the registry.
			 * USAGE
			 * 		N/A (Internal use only).
			 */
			function RequestedFile(url) {
				this.url = url;
				this.content = null;
			}
		}



		/**
		* CLASS
		* 	Utils
		* DESCRIPTION
		* 	Private class; holds only static members that aid in dealing 
		*    with file paths & URLs.
		* SAMPLE USAGE
		* 	N/A (internal use only)
		* @class
		* @private
		*/
		var Utils = {
			/**
			 * Removes spaces from beginning and end of a string.
			 * @method
			 * @public
			 * @static
			 * @param str { String }
			 * 		The string to trim.
			 * @return { String }
			 * 		The given string, with all the leading and trailing spaces 
			 * 		removed.
			 */
			trim: function (str) {
				var str = String(str);
				var isNotEmpty = (str && str.length && /[^\s]/.test(str));
				if(isNotEmpty) {
					return ret = str.replace(/\s*$/, '').replace(/^\s*/, '');
				}
				return '';
			},
			
			/**
			 * Tests whether the given argument is a function.
			 * @method
			 * @public
			 * @static
			 * @param arg { * }
			 * 		The argument that is to be checked for being a function.
			 * @return { Boolean }
			 * 		True, if the given argument is a function, false otherwise.
			 */
			 isFunction : function (arg) {
			 	return arg instanceof Function;
			 },
			 
			/**
			 * Returns a relative path for the file given as argument.
			 * @method
			 * @public
			 * @static
			 * @param file { File }
			 * 		The file to return a relative path of.
			 * @return { String }
			 * 		The translated file url. Will allways have a leading slash,
			 * 		never a trailing one: "/my/relative/url"
			 */ 
			getRelativePath: function (file) {
				var appDir = File.applicationDirectory;
				var f = file;
				var pathSegments = [];
				while (f) {
					pathSegments.splice(0, 0, f.name);
					f = f.parent;
					if(f.nativePath === appDir.nativePath) { break };
				}
				return ['/', pathSegments.join('/')].join('');
			},
			
			/**
			 * Translates a native path into a file URL.
			 * @method
			 * @public
			 * @static
			 * @param nativePath { String }
			 * 		The native path to translate.
			 * @return { String }
			 * 		A file URL, translated from the given native path.
			 */ 
			translateToURL: function (nativePath) {
				nativePath = nativePath.replace(/\\/g, '/');
				nativePath = nativePath.replace(/^\//, '');
				nativePath = encodeURI(nativePath);
				var url = 'file:///' + nativePath;
				return url;
			}
		}


		/**
		* CLASS
		* 	EventManager
		* DESCRIPTION
		* 	Private class that provides abstract event management functionality.
		* SAMPLE USAGE
		* 	N/A (internal use only)
		* @class
		* @private
		*/		
		function EventManager() {
			
			/**
			 * Holds all the registered event listeners.
			 * @field
			 * @private
			 */
			var listeners = {};
			
			/**
			 * Registers an event listener.
			 * @method
			 * @public
			 * @param type { String }
			 * 		The type of events this listener is interested in.
			 * @param callback { Function }
			 * 		The callback to activate when a listener of this type will 
			 * 		be notified.
			 */
			this.addListener = function(type, callback) {
				var list = listeners[type] || (listeners[type] = []);
				list.push (callback);
			}

			/**
			 * Unregisters an event listener.
			 * @method
			 * @public
			 * @param type { String }
			 * 		The type of the listener(s) to remove.
			 * @param callback { Function }
			 * 		The callback registered with the listener(s) to remove.
			 */
			this.removeListener = function(type, callback) {
				var list = listeners[type];
				for(var i=0; i<list.length; i++) {
					var cb = list[i];
					if(cb === callback) {
						list[i] = null;
						break;
					}
				}
				list.sort(function(a,b){return a === null? 1:0});
				while (list[Math.min(0, list.length-1)] === null) {
					list.length -= 1;	
				}
			}
			
			/**
			 * Unregisters all event listeners of a specific type.
			 * @method
			 * @public
			 * @param type { String }
			 * 		The type of the listeners to be removed.
			 */
			this.removeListenersFor = function(type) {
				listeners[type] = null;
				delete listeners[type];
			}
			
			/**
			 * Unregisters all event listeners.
			 * @method
			 * @public
			 */
			this.removeListeners = function() {
				listeners = {};
			}
			
			/**
			 * Notifies all event listeners of a specific type.
			 * @method
			 * @public
			 * @param event { EventManager.Event }
			 * 		The event object being passed to the callback.
			 */
			this.fireEvent = function (event) {
				var type = event.type;
				if(!listeners[type]) {return};
				for (var i=0; i<listeners[type].length; i++) {
					var callback = listeners[type][i];
					callback(event);
				}
			}
			
			/**
			 * Returns an instance of the Event class to the caller.
			 * @method
			 * @public
			 * @param type { String }
			 * 		The type of this event.
			 * @param body { Object }
			 * 		An object literal that holds the information this event
			 * 		transports. Both notifier and callback must have agreed upon
			 * 		this object literal structure.
			 * @param id { String }
			 * 		An optional unique id for this event, should it need be 
			 * 		recognized at some later time.
			 * @return { EventManager.Event }
			 * 		An event object having the specified type, body and id.
			 */
			this.createEvent = function(type, body, id) {
				return new Event(type, body, id);
			}

			
			
			
			/**
			 * CLASS
			 * 		Event
			 * DESCRIPTION
			 * 		Private class that provides a vehicle for transporting 
			 * 		information from the notifier to the callback.
			 * SAMPLE USAGE
			 * 		N/A (internal use only)
			 * @class
			 * @private
			 * @param type { String }
			 * 		The type of this event.
			 * @param body { Object }
			 * 		An object literal that holds the information this event
			 * 		transports. Both notifier and must have agreed upon
			 * 		this object literal structure.
			 * @param id { String }
			 * 		An optional unique id for this event, should it need be 
			 * 		recognized at some later time.
			 */
			function Event(type, body, id) {
				this.type = type;
				this.body = body? body: {};
				this.id = id? id : 'anonymous';
				this.toString = function() {
					var ret = '['+this.id+']: '+this.type+' event; ';
					for(var prop in this.body) {
						ret += '\n'+prop+': '+(
							this.body[prop] instanceof Function? 'function':
							this.body[prop]? this.body[prop].toString():
							this.body[prop] === null? 'null value':
							'undefined value');
					}
					return ret;
				}
			}
		}



		/**
		* CLASS
		* 	LayoutProvider
		* DESCRIPTION
		* 	Private class that provides layout building blocks for the
		* 	application UI.
		* SAMPLE USAGE
		* 	N/A (internal use only)
		* @class
		* @private
		*/
		function LayoutProvider () {

			/**
			* Turns a certain HTML element into a CSS box.
			* @method
			* @public
			* @param target { HTML Element }
			* 		The HTML element that is to be set up as a CSS box. This 
			* 		implies both out-of-page-flow(1) positioning and fixed 
			* 		dimensions(2).
			* @param oPoint { Object }
			* 		An object literal that specifies the box's boundaries. Use:
			* 		- x: The horizontal position of top left corner.
			* 		- y: The vertical position of top left corner.
			* 		- w: The width of the box.
			* 		- h: The height of the box.
			* 		All are optional. Not defining one of the above members will
			* 		unset the corresponding CSS property.
			* 		Note:
			* 		(1) Out-of-page-flow positioning translates to 'fixed' if 
			*          the target element is a direct child of the body 
			*          element; it translates to 'absolute' otherwise.
			* 		(2) All values are computed as ems.
			*/
			this.setupBox = function(target, oPoint) {
				var isTopLevel = target.parentNode.nodeName
					.toLowerCase() == 'body';
				target.style.position = isTopLevel? "fixed": "absolute";
				target.style.left = oPoint && oPoint.x? (oPoint.x + "em") : '';
				target.style.top = oPoint && oPoint.y? (oPoint.y + "em") : '';
				target.style.width = oPoint && oPoint.w? (oPoint.w + "em") : '';
				target.style.height = oPoint && oPoint.h? (oPoint.h + "em"): '';
			}

			/**
			 * Centers a certain CSS box inside its parent.
			 * @method
			 * @private
			 * @param target { HTML Element }
			 * 		The HTML element (already set up as a box) that is to be 
			 * 		centered.
			 * @param oPoint { Object }
			 * 		An object literal that describes an optional offset from the 
			 * 		computed 'center' position. Use:
			 * 		- x: a positive value will move the box right.
			 * 		- y: a positive value will move to box down.
			 * Note:
			 * All values are computed as ems.
			 */
			this.setupCentered = function(target, oPoint) {
				var w = parseFloat(target.style.width);
				var h = parseFloat(target.style.height);
				var xOff = oPoint && oPoint.x? parseFloat(oPoint.x) : 0;
				var yOff = oPoint && oPoint.y? parseFloat(oPoint.y) : 0;
				target.style.left = '50%';
				target.style.top = '50%';
				target.style.marginLeft = (-1*(w/2-xOff)+'em');
				target.style.marginTop = (-1*(h/2-yOff)+'em');
			}
			
			/**
			 * Makes a certain CSS box stretch.
			 * @method
			 * @private
			 * @param target { HTML Element }
			 * 		The HTML element (already set up as a box) that has to 
			 * 		stretch.
			 * @param oPoint { Object }
			 * 		An object literal that defines one to four anchor points. 
			 * 		The box will stretch, the way that its boundaries stay 
			 * 		aligned to each defined anchor point, respectivelly.
			 * 		Example:
			 * 		oPoint = { bottom: 1.5, top: 0 }
			 * 		The box's bottom boundary will be anchored at 1.5 em away 
			 * 		from the parent-box's bottom boundary; also the top boundary
			 * 		of the box will be anchored at the parent-box's top 
			 * 		boundary. As the parent box resizes, the box resizes with
			 * 		it, while keeping the given anchors.
			 */
			this.setupStretched = function(target, oPoint) {
				var topA = oPoint && oPoint.top?
					parseFloat(oPoint.top) : 0;
				var rightA = oPoint && oPoint.right?
					parseFloat(oPoint.right) : 0;
				var bottomA = oPoint && oPoint.bottom?
					parseFloat(oPoint.bottom) : 0;
				var leftA = oPoint && oPoint.left?
					parseFloat(oPoint.left) : 0;
				if(topA >= 0) {
					target.style.top = (topA+ 'em');
				}
				if(rightA >= 0) {
					target.style.right = (rightA+ 'em');
				}
				if(bottomA >= 0) {
					target.style.bottom = (bottomA+ 'em');
				}
				if(leftA >= 0) {
					target.style.left = (leftA+ 'em');
				}
			}
		}



		/**
		* CLASS
		* 		DOMProvider
		* DESCRIPTION
		* 		Private class that provides DOM element creation tools and 
		* 		related functionality for the application.
		* SAMPLE USAGE
		* 		N/A (internal use only)
		* @class
		* @private
		* @param oDocument { Object }
		*		The document object to provide DOM services for.
		*/
		function DOMProvider (oDocument) {
			
			// @let private methods see own class' instance:
			var that = this;
			
			/**
			* The client document object we are providing DOM services for.
 			* @field
			* @private
			*/
			var clientDoc = oDocument;

			/**
			* Generic functionality for creating DOM nodes.
			* @method
			* @public
			* @param elName { String }
			* 		The name of the node to create.
			* @param elParent { Object }
			* 		The parent of the node to create (optional, defaults to 
			* 		'clientDoc'). Can be one of the following:
			* 		- a Document node;
			* 		- the global 'window' object;
			* 		- an Element node.
			* 		For the first two cases, the new node will be appended to 
			* 		the Body element (which, in turn, will be created if it 
			* 		doesn't exist.
			* @param cssClass { String }
			* 		The name of a CSS class to add to this node (optional, 
			* 		defaults to empty string - i.e., no class attribute).
			* @param attributes { Object }
			* 		A hash defining a number of arbitrary attributes.
			* 		Note:
			* 		DOM event listeners will not fire if defined this way. Use
			* 		'addEventListener()' instead.
			* @return { HTML Object }
			* 		The newly created HTML Object.
			*/
			this.makeElement = function(elName, elParent, cssClass, attributes){
				// @private function; gracefully returns the 'html' HTML node. 
				var getHtmlNode = function(oDoc) {
					if(arguments.callee.node) { return arguments.callee.node };
					var node = oDoc.getElementsByTagName('html')[0];
					if(!node) {
						node = oDoc.appendChild(oDoc.createElement('html'));
					}
					arguments.callee.node = node;
					return node;
				}
				// @private function; gracefully returns the 'head' HTML node.
				var getHeadNode = function(oDoc) {
					if(arguments.callee.node) { return arguments.callee.node };
					var node = oDoc.getElementsByTagName('head')[0];
					if(!node) {
						var htmlNode = getHtmlNode(oDoc);
						node = htmlNode.insertBefore(oDoc.createElement('head'),
							htmlNode.firstNode);
					}
					arguments.callee.node = node;
					return node;
				}
				// @private function; gracefully returns the 'body' HTML node.
				var getBodyNode = function(oDoc) {
					if(arguments.callee.node) { return arguments.callee.node };
					var node = oDoc.getElementsByTagName('body')[0];
					if(!node) {
						var htmlNode = getHtmlNode(oDoc);
						var headNode = getHeadNode(oDoc);
						node = htmlNode.insertBefore(oDoc.createElement('body'),
							headNode.nextSibling);
					}
					arguments.callee.node = node;
					return node;
				}
				var parentType = 
					(elParent)?
						(elParent.nativeWindow)? 
							'WINDOW_OBJECT' :
						(elParent.nodeType && elParent.nodeType == 9)? 
							'DOCUMENT' :
						(elParent.nodeType && elParent.nodeType == 1)? 
							'ELEMENT' :
						null :
					null;
				var _parent;
				switch (parentType) {
					case 'WINDOW_OBJECT':
						var oDoc = elParent.document;
						_parent = getBodyNode(oDoc);
						break;
					case 'DOCUMENT':
						var oDoc = elParent;
						_parent = getBodyNode(oDoc);
						break;
					case 'ELEMENT':
						_parent = elParent;
						break;
					default:
						var oDoc = clientDoc;
						_parent = getBodyNode(oDoc);
				}
				var el = _parent.ownerDocument.createElement (elName);
				if (cssClass) { 
					el.className = cssClass;
				};
				if (attributes) {
					for (atrName in attributes) {
						el.setAttribute (atrName, attributes[atrName]);
					}
				}
				el = _parent.appendChild(el);
				return el;
			}
			
			/**
			 * Convenience method to create an empty div element.
			 * @method
			 * @private
			 * @see makeElement()
			 * @param className { String }
			 * 		The name of the css class to apply to the newly created div.
			 * 		Optional, defaults to empty string (i.e., no class 
			 * 		attribute).
			 * @param _parent { Object }
			 * 		The parent to create the new div in. Optional, defaults 
			 * 		in effect to the body element.
			 * @return { HTML Object }
			 * 		The newly created div element.
			 */
			this.makeDiv = function (_parent, className) {
				return this.makeElement('div', _parent, className);
			}
			
			/**
			 * Creates a styled text node.
			 * @method
			 * @private
			 * @see makeElement()
			 * @param value { String }
			 * 		The content of the text node to create. 
			 * 		Note:
			 * 		HTML markup will not be expanded.
			 * @param _parent { Object }
			 * 		The parent to create the new text node in. Optional, 
			 * 		defaults to the body element.
			 * @param className { String }
			 * 		The css class name to apply to the newly created text node.
			 * 		Note:
			 * 		The class name is rather applied to a 'span' wrapper that 
			 * 		holds the text node. The span wrapper is added regardless of
			 * 		the fact that the 'className' attribute is present or not.
			 * @return { HTML element }
			 * 		A span element wrapping the newly created text node.
			 */
			this.makeText = function(value, _parent, className) {
				var wrapper = this.makeElement('span', _parent, className);
				var text = wrapper.ownerDocument.createTextNode(value);
				wrapper.appendChild(text);
				if(wrapper.parentNode.id != "lineNoRuler") {
					if(wrapper.parentNode.parentNode.id != "sourceCodeArea") {
						wrapper.innerHTML = wrapper.innerHTML.replace (
							/\&amp\;([^\;]+?)\;/, '\&$1\;');
					}
				}
				return wrapper;
			}
			
			/**
			 * Removes the text blocks created via makeText();
			 * @method
			 * @private
			 * @param _parent { HTML Element }
			 * 		The element to remove the text blocks from.
			 */
			this.destroyContent = function(_parent) {
				_parent.innerHTML = ' ';
			}
			
			/**
			 * Returns the default client document used by this DOM provider.
			 * @method
			 * @public
			 * @return { HTML DOM Element }
			 * 		The document object this DOM provider defaults to.
			 */
			this.getClientDocument = function() {
				return oDocument;
			}
			
		}



		/**
		* CLASS
		* 		CSSProvider
		* DESCRIPTION
		* 		Private class that provides CSS styling services for the 
		* 		application.
		* SAMPLE USAGE
		* 		N/A (internal use only)
		* @class
		* @private
		* @param oDocument { Object }
		* 		The document object to provide CSS for.
		*/
		function CSSProvider ( oDocument ) {
			/**
			 * Change the current color scheme with the specified one, if a 
			 * scheme with the given name can be found.
			 * @method
			 * @public
			 * @param scheme { String }
			 * 		The name of the new color scheme to apply. It will fail 
			 * 		silently if such a color scheme does not exist.
			 */
			this.changeColorScheme = function (scheme) {
				colorScheme = scheme;
			}
			
			/**
			 * The client document object we are providing CSS services for.
 			 * @field
			 * @private
			 */
			var clientDoc = oDocument;
			
			/**
			 * Default color scheme to use in the application CSS.
			 * @field
			 * @private
			 */
			var defaultColorScheme = 'professionalBlue'; 
			
			/**
			 * Holds the name of the color scheme to be applied. Defaults to
			 * 'professionalBlue'.
			 * @field
			 * @private 
			 */
			var colorScheme = defaultColorScheme;
			
			/**
			 * Applies the colors in the current color scheme to the application
			 * CSS, then returns the modified CSS
			 * @method
			 * @private
			 * @return {String}
			 * 		A copy of CSSProvider.cssContent with all the colors place
			 * 		holders resolved to the current color scheme.
			 */
			function resolveColorNames (cssText) {
				var colors = CSSProvider.colorSchemes[colorScheme];
				if (!colors) {
					colors = CSSProvider.colorSchemes[defaultColorScheme];
				}
				var newCss = cssText;
				for (colorName in colors) {
					var p;
					p = new RegExp(colorName, "g");
					newCss = newCss.replace(p, colors[colorName]);
				}
				return newCss;
			}
			
			/**
			 * Applies the inline CSS by inserting it inside the page head 
			 * element.
			 * @method
			 * @private
			 * @cssText { String }
			 * 		The CSS to apply.
			 */
			this.applyCSS = function() {
				var cssText = resolveColorNames (CSSProvider.cssContent);
				var headEl = clientDoc.getElementsByTagName('head')[0];
				var styleEl = (clientDoc.getElementsByTagName('style')[0] ||
					domProvider.makeElement('style', headEl));
				var textNode = clientDoc.createTextNode(cssText);
				styleEl.appendChild(textNode);
			}

			/**
			 * Sets the provided style on an HTML element.
			 * @field
			 * @public
			 * @static
			 * @param target {HTML Element}
			 * 		An HTML Element to set CSS style on.
			 * @param property (String)
			 * 		The name of the CSS property to be set
			 * @param value {String}
			 * 		The new value to set
			 */
			this.setStyle = function (target, property, value) {
				value = resolveColorNames(value);
				target.style[property] = String(value);
			}
		}
		
		 
		/**
		 * Holds color schemes for the application
		 * @field
		 * @public
		 * @static
		 */
		CSSProvider.colorSchemes = {
			'professionalBlue' : {
				absLight			:	'#ffffff',
				absDark				:	'#000000',
				lightNeutral		:	"#d3dcf2",
				darkNeutral			:	"#9197a6",
				colorMain			:	"#7690cf",
				lighterColorAccent	:	"#48577d",
				darkerColorAccent	:	"#4e5159"
			},
			'nightScape' : {
				absLight			:	'#ffffff',
				absDark				:	'#000000',
				lightNeutral		:	'#4F4F4F',
				darkNeutral			:	'#696969',
				colorMain			:	'#757575',
				lighterColorAccent	:	'#cab792',
				darkerColorAccent	:	'#cf9a35',
				'-night-scape-hdr'	: 	'#ffffff',
				'-night-scape-ruler': 	'#fbf5e8',
			}
		}
		
		/**
		 * Holds all the CSS information for the application's layout
		 * @field
		 * @public
		 * @static
		 */
		CSSProvider.cssContent = '\
			.branch, .nonEmptyBranch {\
				list-style-type: circle;\
			}\
			body {\
				font-size: 16px;\
			}\
			.rcHeader {\
				background-color: lightNeutral;\
				border-bottom: 0.2em solid darkNeutral;\
				padding-left: 1em;\
			}\
			.hdrToken, .vToken {\
				font-family: arial, verdana, sans_;\
				padding: 0.2em;\
				font-weight: bold;\
				cursor: default;\
				line-height: 4em;\
			}\
			.hdrToken, .vToken {\
				font-size: 80%;\
				color: darkerColorAccent;\
			}\
			.vToken {\
				font-style: italic;\
			}\
			.rcTree {\
				background-color: colorMain;\
				border-right: 0.2em solid lightNeutral;\
			}\
			.listDescr {\
				font-family: arial, verdana, sans_;\
				font-size: 0.8em;\
				color: absLight;\
				display: block;\
				padding: 0.5em;\
			}\
			.listBackground {\
				border: 0.1em solid absLight;\
				background-color: lighterColorAccent;\
				opacity: 0.1;\
			}\
			.tree, ul {\
				padding: 0;\
				color: absLight;\
				list-style-type: none;\
			}\
			.item, .branch, .nonEmptyBranch, .leaf {\
				margin-left: 1.5em;\
				padding: 0;\
			}\
			.nonEmptyBranch {\
				list-style-type: disc;\
			}\
			.leaf {\
				list-style-type: square;\
			}\
			.itemText, .branchText, .nonEmptyBranchText, .leafText {\
				font-family: arial, verdana, sans_;\
				font-size: 0.8em;\
				padding-left: 0.5em;\
				padding-right: 0.5em;\
				cursor: default;\
				white-space: nowrap;\
			}\
			.branchText, .nonEmptyBranchText {\
				font-weight: bold;\
				font-style: italic;\
				cursor: text;\
			}\
			.nonEmptyBranchText {\
				font-style: normal;\
				cursor: pointer;\
			}\
			.progress {\
				font-family: arial, verdana, sans_;\
				font-size: 0.7em;\
				font-style: italic;\
			}\
			.rcContent {\
				background-color: absDark;\
			}\
			.noContent {\
				font-family: arial, verdana, sans_;\
				color: lighterColorAccent;\
				font-size: 1em;\
				font-weight: bold;\
				font-style: italic;\
			}\
			.srcCodeArea, .ruler {\
				border: 0.1em solid colorMain;\
				background-color: absLight;\
				opacity: 0.95;\
				overflow: auto;\
				padding-left: 0.2;\
				visibility: hidden;\
			}\
			.ruler {\
				padding-left: 0;\
				padding-top: 2px;\
				border-width: 0.1em;\
				background-color: lighterColorAccent;\
				z-index: 2;\
				overflow: hidden;\
			}\
			.rulerText, .sourceCodeText {\
				display: block;\
				font-family: courier new, courier, mono_;\
				white-space: pre;\
				font-size: 16px;\
			}\
			.rulerText {\
				color: #ffffff;\
				text-align: right;\
			}\
			.sourceCodeText {\
				margin-top: 3px;\
			}\
			.rcFooter {\
				border-top: 0.2em solid lightNeutral;\
				background-color: darkNeutral;\
			}\
			.copyrightText {\
				color: lightNeutral;\
				font-family: arial, verdana, sans_;\
				font-size: 80%;\
				font-weight: bold;\
				text-align: right;\
				display: block;\
				margin: 0.5em;\
			}\
			.imageContent {\
				padding: 2em;\
				border: 1px solid darkNeutral;\
				background-color: lightNeutral;\
				margin: 1em;\
			}\
			';
	}
	context.instance = new _SourceViewer();
	return context.instance;
}