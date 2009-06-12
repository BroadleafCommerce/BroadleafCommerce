////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.Graphics;
import flash.display.InteractiveObject;
import flash.display.Loader;
import flash.display.LoaderInfo;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.display.Stage;
import flash.display.StageAlign;
import flash.display.StageScaleMode;
import flash.events.Event;
import flash.events.IEventDispatcher;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.system.ApplicationDomain;
import flash.system.Capabilities;
import flash.ui.ContextMenu;
import flash.utils.getQualifiedClassName;
import flash.utils.Dictionary;
import flash.utils.Timer;
import flash.text.Font;
import flash.text.TextFormat;

// NOTE: Minimize the non-Flash classes you import here.
// Any dependencies of SystemManager have to load in frame 1,
// before the preloader, or anything else, can be displayed.

import mx.core.FlexSprite;
import mx.core.EmbeddedFontRegistry;
import mx.core.IChildList;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModuleFactory;
import mx.core.IInvalidating;
import mx.core.IRawChildrenContainer;
import mx.core.IUIComponent;
import mx.core.RSLItem;
import mx.core.Singleton;
import mx.core.mx_internal;
import mx.core.TextFieldFactory;
import mx.events.FlexEvent;
import mx.managers.IFocusManagerContainer;
import mx.messaging.config.LoaderConfig;
import mx.preloaders.DownloadProgressBar;
import mx.preloaders.Preloader;
import mx.resources.IResourceManager;
import mx.resources.ResourceBundle;
import mx.resources.ResourceManager;
import mx.styles.IStyleClient;
import mx.styles.ISimpleStyleClient;
import mx.styles.StyleManager;
import mx.styles.StyleManagerImpl;
import mx.utils.ObjectUtil;

// NOTE: Minimize the non-Flash classes you import here.
// Any dependencies of SystemManager have to load in frame 1,
// before the preloader, or anything else, can be displayed.

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the application has finished initializing
 *
 *  @eventType mx.events.FlexEvent.APPLICATION_COMPLETE
 */
[Event(name="applicationComplete", type="mx.events.FlexEvent")]

/**
 *  Dispatched every 100 milliseconds when there has been no keyboard
 *  or mouse activity for 1 second.
 *
 *  @eventType mx.events.FlexEvent.IDLE
 */
[Event(name="idle", type="mx.events.FlexEvent")]

/**
 *  Dispatched when the Stage is resized.
 *
 *  @eventType flash.events.Event.RESIZE
 */
[Event(name="resize", type="flash.events.Event")]

/**
 *  The SystemManager class manages an application window.
 *  Every application that runs on the desktop or in a browser
 *  has an area where the visuals of the application are 
 *  displayed.  
 *  It may be a window in the operating system
 *  or an area within the browser.  That area is an application window
 *  and different from an instance of <code>mx.core.Application</code>, which
 *  is the main, or top-level, window within an application.
 *
 *  <p>Every application has a SystemManager.  
 *  The SystemManager sends an event if
 *  the size of the application window changes (you cannot change it from
 *  within the application, but only through interaction with the operating
 *  system window or browser).  It parents all displayable things within the
 *  application like the main mx.core.Application instance and all popups, 
 *  tooltips, cursors, and so on.  Any object parented by the SystemManager is
 *  considered to be a top-level window, even tooltips and cursors.</p>
 *
 *  <p>The SystemManager also switches focus between top-level windows if there 
 *  are more than one IFocusManagerContainer displayed and users are interacting
 *  with components within the IFocusManagerContainers.  </p>
 *
 *  <p>All keyboard and mouse activity that is not expressly trapped is seen by
 *  the SystemManager, making it a good place to monitor activity should you need
 *  to do so.</p>
 *
 *  <p>If an application is loaded into another application, a SystemManager
 *  will still be created, but will not manage an application window,
 *  depending on security and domain rules.
 *  Instead, it will be the <code>content</code> of the <code>Loader</code> 
 *  that loaded it and simply serve as the parent of the sub-application</p>
 *
 *  <p>The SystemManager maintains multiple lists of children, one each for tooltips, cursors,
 *  popup windows.  This is how it ensures that popup windows "float" above the main
 *  application windows and that tooltips "float" above that and cursors above that.
 *  If you simply examine the <code>numChildren</code> property or 
 *  call the <code>getChildAt()</code> method on the SystemManager, you are accessing
 *  the main application window and any other windows that aren't popped up.  To get the list
 *  of all windows, including popups, tooltips and cursors, use 
 *  the <code>rawChildren</code> property.</p>
 *
 *  <p>The SystemManager is the first display class created within an application.
 *  It is responsible for creating an <code>mx.preloaders.Preloader</code> that displays and
 *  <code>mx.preloaders.DownloadProgressBar</code> while the application finishes loading,
 *  then creates the <code>mx.core.Application</code> instance.</p>
 */
public class SystemManager extends MovieClip
						   implements IChildList, IFlexDisplayObject,
						   IFlexModuleFactory, ISystemManager
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  The number of milliseconds that must pass without any user activity
	 *  before SystemManager starts dispatching 'idle' events.
	 */
	private static const IDLE_THRESHOLD:Number = 1000;

	/**
	 *  @private
	 *  The number of milliseconds between each 'idle' event.
	 */
	private static const IDLE_INTERVAL:Number = 100;

	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  An array of SystemManager instances loaded as child app domains
	 */
	mx_internal static var allSystemManagers:Dictionary = new Dictionary(true);

	/**
	 *  @private
	 *  The last SystemManager instance loaded as child app domains
	 */
	mx_internal static var lastSystemManager:SystemManager;

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  If a class wants to be notified when the Application instance
	 *  has been initialized, then it registers a callback here.
	 *  By using a callback mechanism, we avoid adding unwanted
	 *  linker dependencies on classes like HistoryManager and DragManager.
	 */
	mx_internal static function registerInitCallback(initFunction:Function):void
	{
		if (!allSystemManagers || !lastSystemManager)
		{
			return;
		}

		var sm:SystemManager = lastSystemManager;

		// If this function is called late (after we're done invoking the
		// callback functions for the last time), then just invoke
		// the callback function immediately.
		if (sm.doneExecutingInitCallbacks)
			initFunction(sm);
		else
			sm.initCallbackFunctions.push(initFunction);
	}

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  <p>This is the starting point for all Flex applications.
	 *  This class is set to be the root class of a Flex SWF file.
	 *  The Player instantiates an instance of this class,
	 *  causing this constructor to be called.</p>
	 */
	public function SystemManager()
	{
		super();

		// Loaded SWFs don't get a stage right away
		// and shouldn't override the main SWF's setting anyway.
		if (stage)
		{
			stage.scaleMode = StageScaleMode.NO_SCALE;
			stage.align = StageAlign.TOP_LEFT;
		}

		// If we don't have a stage then we are not top-level,
		// unless there are no other top-level managers, in which
		// case we got loaded by a non-Flex shell or are sandboxed.
		if (SystemManagerGlobals.topLevelSystemManagers.length > 0 && !stage)
			topLevel = false;

		if (!stage)
			isStageRoot = false;

		if (topLevel)
			SystemManagerGlobals.topLevelSystemManagers.push(this);

		lastSystemManager = this;

		var compiledLocales:Array = info()["compiledLocales"];
		ResourceBundle.mx_internal::locale =
			compiledLocales != null && compiledLocales.length > 0 ?
			compiledLocales[0] :
			"en_US";

		executeCallbacks();

		// Make sure to stop the playhead on the current frame.
		stop();

		// Add safeguard in case bug 129782 shows up again.
		if (topLevel && currentFrame != 1)
		{
			throw new Error("The SystemManager constructor was called when the currentFrame was at " + currentFrame +
							" Please add this SWF to bug 129782.");
		}

		// Listen for the last frame (param is 0-indexed) to be executed.
		//addFrameScript(totalFrames - 1, frameEndHandler);

		if (root && root.loaderInfo)
			root.loaderInfo.addEventListener(Event.INIT, initHandler);
	}

    /**
	 *  @private
	 */
    private function deferredNextFrame():void
    {
        if (currentFrame + 1 > totalFrames)
            return;

        if (currentFrame + 1 <= framesLoaded)
		{
            nextFrame();
		}
        else
        {
            // Next frame isn't baked yet, so we'll check back...
    		nextFrameTimer = new Timer(100);
		    nextFrameTimer.addEventListener(TimerEvent.TIMER,
											nextFrameTimerHandler);
		    nextFrameTimer.start();
        }
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  This flag remembers whether we're going to call executeCallbacks again
	 */
	private var doneExecutingInitCallbacks:Boolean = false;

	/**
	 *  @private
	 *  This array stores pointers to all the init callback functions for this
	 *  system manager.
	 *  See registerInitCallback() for more information.
	 */
	private var initCallbackFunctions:Array = [];

	/**
	 *  @private
	 */
	private var initialized:Boolean = false;

	/**
	 *  @private
	 *  Whether we are in the top-level list or not;
	 *  top-level means we are the highest level SystemManager
	 *  for this stage.
	 */
	private var topLevel:Boolean = true;

	/**
	 *  @private
	 *  Whether we are the stage root or not.
	 *  We are only the stage root if we were the root
	 *  of the first SWF that got loaded by the player.
	 *  Otherwise we could be top level but not stage root
	 *  if we are loaded by some other non-Flex shell
	 *  or are sandboxed.
	 */
	private var isStageRoot:Boolean = true;

	/**
	 *  @private
	 *  If we're not top level, then we delegate many things
	 *  to the top level SystemManager.
	 */
	private var _topLevelSystemManager:ISystemManager;

	/**
	 *  Depth of this object in the containment hierarchy.
	 *  This number is used by the measurement and layout code.
	 */
	mx_internal var nestLevel:int = 0;

	/**
	 *  @private
	 */
	private var rslSizes:Array = null;

	/**
	 *  @private
	 *  A reference to the preloader.
	 */
	private var preloader:Preloader;

	/**
	 *  @private
	 *  The mouseCatcher is the 0th child of the SystemManager,
	 *  behind the application, which is child 1.
	 *  It is the same size as the stage and is filled with
	 *  transparent pixels; i.e., they've been drawn, but with alpha 0.
	 *
	 *  Its purpose is to make every part of the stage
	 *  able to detect the mouse.
	 *  For example, a Button puts a mouseUp handler on the SystemManager
	 *  in order to capture mouseUp events that occur outside the Button.
	 *  But if the children of the SystemManager don't have "drawn-on"
	 *  pixels everywhere, the player won't dispatch the mouseUp.
	 *  We can't simply fill the SystemManager itself with
	 *  transparent pixels, because the player's pixel detection
	 *  logic doesn't look at pixels drawn into the root DisplayObject.
	 *
	 *  Here is an example of what would happen without the mouseCatcher:
	 *  Run a fixed-size Application (e.g. width="600" height="600")
	 *  in the standalone player. Make the player window larger
	 *  to reveal part of the stage. Press a Button, drag off it
	 *  into the stage area, and release the mouse button.
	 *  Without the mouseCatcher, the Button wouldn't return to its "up" state.
	 */
	private var mouseCatcher:Sprite;

	/**
	 *  @private
	 *  The top level window.
	 */
	mx_internal var topLevelWindow:IUIComponent;

	/**
	 *  @private
	 *  List of top level windows.
	 */
	private var forms:Array = [];

	/**
	 *  @private
	 *  The current top level window.
	 */
	private var form:IFocusManagerContainer;

	/**
	 *  @private
	 *  Number of frames since the last mouse or key activity.
	 */
	mx_internal var idleCounter:int = 0;

	/**
	 *  @private
	 *  The Timer used to determine when to dispatch idle events.
	 */
	private var idleTimer:Timer;

    /**
	 *  @private
	 *  A timer used when it is necessary to wait before incrementing the frame
	 */
	private var nextFrameTimer:Timer = null;

	//--------------------------------------------------------------------------
	//
	//  Overridden properties: DisplayObject
	//
	//--------------------------------------------------------------------------

    //----------------------------------
    //  height
    //----------------------------------

	/**
	 *  @private
	 */
	private var _height:Number;

	/**
	 *  The height of this object.  For the SystemManager
	 *  this should always be the width of the stage unless the application was loaded
	 *  into another application.  If the application was not loaded
	 *  into another application, setting this value has no effect.
	 */
	override public function get height():Number
	{
		return _height;
	}

	//----------------------------------
	//  stage
	//----------------------------------

	/**
	 *  @private
	 *  get the main stage if we're loaded into another swf in the same sandbox
	 */
	override public function get stage():Stage
	{
		var s:Stage = super.stage;
		if (s)
			return s;

		if (!topLevel && _topLevelSystemManager)
			return _topLevelSystemManager.stage;

		return null;
	}

    //----------------------------------
    //  width
    //----------------------------------

	/**
	 *  @private
	 */
	private var _width:Number;

	/**
	 *  The width of this object.  For the SystemManager
	 *  this should always be the width of the stage unless the application was loaded
	 *  into another application.  If the application was not loaded
	 *  into another application, setting this value will have no effect.
	 */
	override public function get width():Number
	{
		return _width;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden properties: DisplayObjectContainer
	//
	//--------------------------------------------------------------------------

    //----------------------------------
    //  numChildren
    //----------------------------------

	/**
	 *  The number of non-floating windows.  This is the main application window
	 *  plus any other windows added to the SystemManager that are not popups,
	 *  tooltips or cursors.
	 */
	override public function get numChildren():int
	{
		return noTopMostIndex - applicationIndex;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

    //----------------------------------
    //  application
    //----------------------------------

	/**
	 *  The application parented by this SystemManager.
	 *  SystemManagers create an instance of an Application
	 *  even if they are loaded into another Application.
	 *  Thus, this may not match mx.core.Application.application
	 *  if the SWF has been loaded into another application.
	 *  <p>Note that this property is not typed as mx.core.Application
	 *  because of load-time performance considerations
	 *  but can be coerced into an mx.core.Application.</p>
	 */
	public function get application():IUIComponent
	{
		return IUIComponent(_document);
	}

	//----------------------------------
	//  applicationIndex
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the applicationIndex property.
	 */
	private var _applicationIndex:int = 1;

	/**
	 *  @private
	 *  The index of the main mx.core.Application window, which is
	 *  effectively its z-order.
	 */
	mx_internal function get applicationIndex():int
	{
		return _applicationIndex;
	}

	/**
	 *  @private
	 */
	mx_internal function set applicationIndex(value:int):void
	{
		_applicationIndex = value;
	}

	//----------------------------------
	//  cursorChildren
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the cursorChildren property.
	 */
	private var _cursorChildren:SystemChildrenList;

	/**
	 *  @inheritDoc
	 */
	public function get cursorChildren():IChildList
	{
		if (!topLevel)
			return _topLevelSystemManager.cursorChildren;

		if (!_cursorChildren)
		{
			_cursorChildren = new SystemChildrenList(this,
				new QName(mx_internal, "toolTipIndex"),
				new QName(mx_internal, "cursorIndex"));
		}

		return _cursorChildren;
	}

	//----------------------------------
	//  cursorIndex
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the toolTipIndex property.
	 */
	private var _cursorIndex:int = 0;

	/**
	 *  @private
	 *  The index of the highest child that is a cursor.
	 */
	mx_internal function get cursorIndex():int
	{
		return _cursorIndex;
	}

	/**
	 *  @private
	 */
	mx_internal function set cursorIndex(value:int):void
	{
		var delta:int = value - _cursorIndex;
		_cursorIndex = value;
	}

    //----------------------------------
    //  document
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the document property.
	 */
	private var _document:Object;

	/**
	 *  @inheritDoc
	 */
	public function get document():Object
	{
		return _document;
	}

	/**
	 *  @private
	 */
	public function set document(value:Object):void
	{
		_document = value;
	}

	//----------------------------------
	//  embeddedFontList
	//----------------------------------

   	/**
   	 *  @private
   	 *  Storage for the fontList property.
   	 */
   	private var _fontList:Object = null;

	/**
	 *  A table of embedded fonts in this application.  The 
	 *  object is a table indexed by the font name.
	 */
	public function get embeddedFontList():Object
	{
	    if (_fontList == null)
	    {
            _fontList = {};

            var o:Object = info()["fonts"];

			var p:String;

            for (p in o)
         	{
                _fontList[p] = o[p];
            }

            // FIXME: font rules across SWF boundaries have not been finalized!

			// Top level systemManager may not be defined if SWF is loaded
			// as a background image in download progress bar.
      		if (!topLevel && _topLevelSystemManager)                   
   		    {
		        var fl:Object = _topLevelSystemManager.embeddedFontList;
			    for (p in fl)
			    {
			        _fontList[p] = fl[p];
			    }
		    }
		}

		return _fontList;
	}

    //----------------------------------
    //  explicitHeight
    //----------------------------------

	/**
	 *  @private
	 */
	private var _explicitHeight:Number;

	/**
	 *  The explicit width of this object.  For the SystemManager
	 *  this should always be NaN unless the application was loaded
	 *  into another application.  If the application was not loaded
	 *  into another application, setting this value has no effect.
	 */
	public function get explicitHeight():Number
	{
		return _explicitHeight;
	}

	/**
	 *  @private
	 */
    public function set explicitHeight(value:Number):void
    {
        _explicitHeight = value;
	}

    //----------------------------------
    //  explicitWidth
    //----------------------------------

	/**
	 *  @private
	 */
	private var _explicitWidth:Number;

	/**
	 *  The explicit width of this object.  For the SystemManager
	 *  this should always be NaN unless the application was loaded
	 *  into another application.  If the application was not loaded
	 *  into another application, setting this value has no effect.
	 */
	public function get explicitWidth():Number
	{
		return _explicitWidth;
	}

	/**
	 *  @private
	 */
    public function set explicitWidth(value:Number):void
    {
        _explicitWidth = value;
	}

    //----------------------------------
    //  focusPane
    //----------------------------------

    /**
     *  @private
     */
    private var _focusPane:Sprite;

	/**
     *  @copy mx.core.UIComponent#focusPane
	 */
    public function get focusPane():Sprite
	{
		return _focusPane;
	}

	/**
     *  @private
     */
    public function set focusPane(value:Sprite):void
    {
        if (value)
        {
            addChild(value);

            value.x = 0;
			value.y = 0;
            value.scrollRect = null;

            _focusPane = value;
        }
        else
        {
            removeChild(_focusPane);

            _focusPane = null;
        }
    }

	//----------------------------------
	//  info
	//----------------------------------

    /**
	 *  @private
     */
    public function info():Object
    {
        return {};
    }

    //----------------------------------
    //  measuredHeight
    //----------------------------------

	/**
	 *  The measuredHeight is the explicit or measuredHeight of 
	 *  the main mx.core.Application window
	 *  or the starting height of the SWF if the main window 
	 *  has not yet been created or does not exist.
	 */
	public function get measuredHeight():Number
	{
		return topLevelWindow ?
			   topLevelWindow.getExplicitOrMeasuredHeight() :
			   loaderInfo.height;
	}

    //----------------------------------
    //  measuredWidth
    //----------------------------------

	/**
	 *  The measuredWidth is the explicit or measuredWidth of 
	 *  the main mx.core.Application window,
	 *  or the starting width of the SWF if the main window 
	 *  has not yet been created or does not exist.
	 */
	public function get measuredWidth():Number
	{
		return topLevelWindow ?
			   topLevelWindow.getExplicitOrMeasuredWidth() :
			   loaderInfo.width;
	}

	//----------------------------------
	//  noTopMostIndex
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the noTopMostIndex property.
	 */
	private var _noTopMostIndex:int = 0;

	/**
	 *  @private
	 *  The index of the highest child that isn't a topmost/popup window
	 */
	mx_internal function get noTopMostIndex():int
	{
		return _noTopMostIndex;
	}

	/**
	 *  @private
	 */
	mx_internal function set noTopMostIndex(value:int):void
	{
		var delta:int = value - _noTopMostIndex;
		_noTopMostIndex = value;
		topMostIndex += delta;
	}

	//----------------------------------
	//  $numChildren
	//----------------------------------

	/**
	 *  @private
	 *  This property allows access to the Player's native implementation
	 *  of the numChildren property, which can be useful since components
	 *  can override numChildren and thereby hide the native implementation.
	 *  Note that this "base property" is final and cannot be overridden,
	 *  so you can count on it to reflect what is happening at the player level.
	 */
	mx_internal final function get $numChildren():int
	{
		return super.numChildren;
	}

    //----------------------------------
    //  numModalWindows
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the numModalWindows property.
	 */
	private var _numModalWindows:int = 0;

	/**
	 *  The number of modal windows.  Modal windows don't allow
	 *  clicking in another windows which would normally
	 *  activate the FocusManager in that window.  The PopUpManager
	 *  modifies this count as it creates and destroys modal windows.
	 */
	public function get numModalWindows():int
	{
		return _numModalWindows;
	}

	/**
	 *  @private
	 */
	public function set numModalWindows(value:int):void
	{
		_numModalWindows = value;
	}

    //----------------------------------
    //  preloaderBackgroundAlpha
    //----------------------------------

	/**
	 *	The background alpha used by the child of the preloader.
	 */
	public function get preloaderBackgroundAlpha():Number
	{
        return info()["backgroundAlpha"];
	}

    //----------------------------------
    //  preloaderBackgroundColor
    //----------------------------------

	/**
	 *	The background color used by the child of the preloader.
	 */
	public function get preloaderBackgroundColor():uint
	{
		var value:* = info()["backgroundColor"];
		if (value == undefined)
			return StyleManager.NOT_A_COLOR;
		else
			return StyleManager.getColorName(value);
	}

    //----------------------------------
    //  preloaderBackgroundImage
    //----------------------------------

	/**
	 *	The background color used by the child of the preloader.
	 */
	public function get preloaderBackgroundImage():Object
	{
        return info()["backgroundImage"];
	}

	//----------------------------------
    //  preloaderBackgroundSize
    //----------------------------------

	/**
	 *	The background size used by the child of the preloader.
	 */
	public function get preloaderBackgroundSize():String
	{
        return info()["backgroundSize"];
	}

	//----------------------------------
	//  popUpChildren
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the popUpChildren property.
	 */
	private var _popUpChildren:SystemChildrenList;

	/**
	 *  @inheritDoc
	 */
	public function get popUpChildren():IChildList
	{
		if (!topLevel)
			return _topLevelSystemManager.popUpChildren;

		if (!_popUpChildren)
		{
			_popUpChildren = new SystemChildrenList(this,
				new QName(mx_internal, "noTopMostIndex"),
				new QName(mx_internal, "topMostIndex"));
		}

		return _popUpChildren;
	}

	//----------------------------------
	//  rawChildren
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the rawChildren property.
	 */
	private var _rawChildren:SystemRawChildrenList;

	/**
	 *  @inheritDoc
	 */
	public function get rawChildren():IChildList
	{
		//if (!topLevel)
		//	return _topLevelSystemManager.rawChildren;

		if (!_rawChildren)
			_rawChildren = new SystemRawChildrenList(this);

		return _rawChildren;
	}

	//--------------------------------------------------------------------------
	//  screen
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Storage for the screen property.
	 */
	private var _screen:Rectangle;

	/**
	 *  @inheritDoc
	 */
	public function get screen():Rectangle
	{
		if (!_screen)
			Stage_resizeHandler();

		return _screen;
	}

	//----------------------------------
	//  toolTipChildren
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the toolTipChildren property.
	 */
	private var _toolTipChildren:SystemChildrenList;

	/**
	 *  @inheritDoc
	 */
	public function get toolTipChildren():IChildList
	{
		if (!topLevel)
			return _topLevelSystemManager.toolTipChildren;

		if (!_toolTipChildren)
		{
			_toolTipChildren = new SystemChildrenList(this,
				new QName(mx_internal, "topMostIndex"),
				new QName(mx_internal, "toolTipIndex"));
		}

		return _toolTipChildren;
	}

	//----------------------------------
	//  toolTipIndex
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the toolTipIndex property.
	 */
	private var _toolTipIndex:int = 0;

	/**
	 *  @private
	 *  The index of the highest child that is a tooltip
	 */
	mx_internal function get toolTipIndex():int
	{
		return _toolTipIndex;
	}

	/**
	 *  @private
	 */
	mx_internal function set toolTipIndex(value:int):void
	{
		var delta:int = value - _toolTipIndex;
		_toolTipIndex = value;
		cursorIndex += delta;
	}

	//----------------------------------
	//  topLevelSystemManager
	//----------------------------------

	/**
	 *  Returns the SystemManager responsible for the application window.  This will be
	 *  the same SystemManager unless this application has been loaded into another
	 *  application.
	 */
	public function get topLevelSystemManager():ISystemManager
	{
		if (topLevel)
			return this;

		return _topLevelSystemManager;
	}

	//----------------------------------
	//  topMostIndex
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the topMostIndex property.
	 */
	private var _topMostIndex:int = 0;

	/**
	 *  @private
	 *  The index of the highest child that is a topmost/popup window
	 */
	mx_internal function get topMostIndex():int
	{
		return _topMostIndex;
	}

	mx_internal function set topMostIndex(value:int):void
	{
		var delta:int = value - _topMostIndex;
		_topMostIndex = value;
		toolTipIndex += delta;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: EventDispatcher
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Only create idle events if someone is listening.
	 */
	override public function addEventListener(type:String, listener:Function,
											  useCapture:Boolean = false,
											  priority:int = 0,
											  useWeakReference:Boolean = false):void
	{
		// When the first listener registers for 'idle' events,
		// create a Timer that will fire every IDLE_INTERVAL.
		if (type == FlexEvent.IDLE && !idleTimer)
		{
			idleTimer = new Timer(IDLE_INTERVAL);
			idleTimer.addEventListener(TimerEvent.TIMER,
									   idleTimer_timerHandler);
			idleTimer.start();

			// Make sure we get all activity
			// in case someone calls stopPropagation().
			addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler, true);
			addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler, true);
		}

		super.addEventListener(type, listener, useCapture, priority, useWeakReference);
	}

	/**
	 *  @private
	 */
	override public function removeEventListener(type:String, listener:Function,
												 useCapture:Boolean = false):void
	{
		// When the last listener unregisters for 'idle' events,
		// stop and release the Timer.
		if (type == FlexEvent.IDLE)
		{
			super.removeEventListener(type, listener, useCapture);

			if (!hasEventListener(FlexEvent.IDLE) && idleTimer)
			{
				idleTimer.stop();
				idleTimer = null;

				removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
				removeEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
			}
		}
		else
		{
			super.removeEventListener(type, listener, useCapture);
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: DisplayObjectContainer
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function addChild(child:DisplayObject):DisplayObject
	{
		// Adjust the partition indexes
		// before the "added" event is dispatched.
		noTopMostIndex++;

		return rawChildren_addChildAt(child, noTopMostIndex - 1);
	}

	/**
	 *  @private
	 */
	override public function addChildAt(child:DisplayObject,
										index:int):DisplayObject
	{
		// Adjust the partition indexes
		// before the "added" event is dispatched.
		noTopMostIndex++;

		return rawChildren_addChildAt(child, applicationIndex + index);
	}

	/**
	 *  @private
	 */
	override public function removeChild(child:DisplayObject):DisplayObject
	{
		// Adjust the partition indexes
		// before the "removed" event is dispatched.
		noTopMostIndex--;

		return rawChildren_removeChild(child);
	}

	/**
	 *  @private
	 */
	override public function removeChildAt(index:int):DisplayObject
	{
		// Adjust the partition indexes
		// before the "removed" event is dispatched.
		noTopMostIndex--;

		return rawChildren_removeChildAt(applicationIndex + index);
	}

	/**
	 *  @private
	 */
  	override public function getChildAt(index:int):DisplayObject
	{
		return super.getChildAt(applicationIndex + index)
	}

	/**
	 *  @private
	 */
  	override public function getChildByName(name:String):DisplayObject
  	{
		return super.getChildByName(name);
  	}

	/**
	 *  @private
	 */
  	override public function getChildIndex(child:DisplayObject):int
	{
		return super.getChildIndex(child) - applicationIndex;
	}

	/**
	 *  @private
	 */
	override public function setChildIndex(child:DisplayObject, newIndex:int):void
	{
		super.setChildIndex(child, applicationIndex + newIndex)
	}

	/**
	 *  @private
	 */
	override public function getObjectsUnderPoint(point:Point):Array
	{
		var children:Array = [];

		// Get all the children that aren't tooltips and cursors.
		var n:int = topMostIndex;
		for (var i:int = 0; i < n; i++)
		{
			var child:DisplayObject = super.getChildAt(i);
			if (child is DisplayObjectContainer)
			{
				var temp:Array =
					DisplayObjectContainer(child).getObjectsUnderPoint(point);

				if (temp)
					children = children.concat(temp);
			}
		}

		return children;
	}

	/**
	 *  @private
	 */
	override public function contains(child:DisplayObject):Boolean
	{
		if (super.contains(child))
		{
			if (child.parent == this)
			{
				var childIndex:int = super.getChildIndex(child);
				if (childIndex < noTopMostIndex)
					return true;
			}
			else
			{
				for (var i:int = 0; i < noTopMostIndex; i++)
				{
					var myChild:DisplayObject = super.getChildAt(i);
					if (myChild is IRawChildrenContainer)
					{
						if (IRawChildrenContainer(myChild).rawChildren.contains(child))
							return true;
					}
					if (myChild is DisplayObjectContainer)
					{
						if (DisplayObjectContainer(myChild).contains(child))
							return true;
					}
				}
			}
		}
		return false;
	}

	//--------------------------------------------------------------------------
	//
	//  Methods: Initialization
	//
	//--------------------------------------------------------------------------

	/**
	 *   A factory method that requests an instance of a
	 *  definition known to the module.
	 * 
	 *  You can provide an optional set of parameters to let building
	 *  factories change what they create based on the
	 *  input. Passing null indicates that the default definition
	 *  is created, if possible. 
	 *
	 *  This method is overridden in the autogenerated subclass.
	 *
	 * @param params An optional list of arguments. You can pass
	 *  any number of arguments, which are then stored in an Array
	 *  called <code>parameters</code>. 
	 *
	 * @return An instance of the module, or <code>null</code>.
	 */
	public function create(... params):Object
	{
	    var mainClassName:String = info()["mainClassName"];

		if (mainClassName == null)
	    {
            var url:String = loaderInfo.loaderURL;
            var dot:int = url.lastIndexOf(".");
            var slash:int = url.lastIndexOf("/");
            mainClassName = url.substring(slash + 1, dot);
	    }

		var mainClass:Class = Class(getDefinitionByName(mainClassName));
		
		return mainClass ? new mainClass() : null;
	}

	/**
	 *  @private
	 *  Creates an instance of the preloader, adds it as a child, and runs it.
	 *  This is needed by FlexBuilder. Do not modify this function.
	 */
	mx_internal function initialize():void
	{
		if (isStageRoot)
		{
			_width = stage.stageWidth;
			_height = stage.stageHeight;
		}
		else
		{
			_width = loaderInfo.width;
			_height = loaderInfo.height;
		}

		// Create an instance of the preloader and add it to the stage
		preloader = new Preloader();

		// Listen for preloader events
		// Once the preloader dispatches initStart, then create the application instance
		preloader.addEventListener(FlexEvent.INIT_PROGRESS,
								   preloader_initProgressHandler);
		preloader.addEventListener(FlexEvent.PRELOADER_DONE,
								   preloader_preloaderDoneHandler);

		// Add the preloader as a child.  Use backing variable because when loaded
		// we redirect public API to parent systemmanager
		if (!_popUpChildren)
		{
			_popUpChildren = new SystemChildrenList(
				this, new QName(mx_internal, "noTopMostIndex"), new QName(mx_internal, "topMostIndex"));
		}
		_popUpChildren.addChild(preloader);

		var rsls:Array = info()["rsls"];
		var cdRsls:Array = info()["cdRsls"];
		var usePreloader:Boolean = true;
        if (info()["usePreloader"] != undefined)
            usePreloader = info()["usePreloader"];

		var preloaderDisplayClass:Class = info()["preloader"] as Class;
        if (usePreloader && !preloaderDisplayClass)
            preloaderDisplayClass = DownloadProgressBar;

        // Put cross-domain RSL information in the RSL list.
        var rslList:Array = [];
        var n:int;
        var i:int;
		if (cdRsls && cdRsls.length > 0)
		{
			var crossDomainRSLItem:Class = Class(getDefinitionByName("mx.core::CrossDomainRSLItem"));
			n = cdRsls.length;
			for (i = 0; i < n; i++)
			{
				// If crossDomainRSLItem is null, then this is a compiler error. It should not be null.
				var cdNode:Object = new crossDomainRSLItem(cdRsls[i]["rsls"],
													cdRsls[i]["policyFiles"],
													cdRsls[i]["digests"],
													cdRsls[i]["types"],
													cdRsls[i]["isSigned"]);
				rslList.push(cdNode);				
			}
		}

		// Append RSL information in the RSL list.
		if (rsls != null && rsls.length > 0)
		{
			n = rsls.length;
			for (i = 0; i < n; i++)
			{
			    var node:RSLItem = new RSLItem(rsls[i].url);
				rslList.push(node);
			}
		}

		// Register the ResourceManager class with Singleton early
		// so that we can use the ResourceManager in frame 1.
		// Same with EmbfeddedFontRegistry and StyleManager
		// The other managers get registered with Singleton later,
		// in frame 2, by docFrameHandler().
		Singleton.registerClass("mx.resources::IResourceManager",
			Class(getDefinitionByName("mx.resources::ResourceManagerImpl")));
		var resourceManager:IResourceManager = ResourceManager.getInstance();

		var fontRegistry:EmbeddedFontRegistry;	// link in the EmbeddedFontRegistry Class			
		Singleton.registerClass("mx.core::IEmbeddedFontRegistry",
				Class(getDefinitionByName("mx.core::EmbeddedFontRegistry")));
				
		Singleton.registerClass("mx.styles::IStyleManager",
			Class(getDefinitionByName("mx.styles::StyleManagerImpl")));

		Singleton.registerClass("mx.styles::IStyleManager2",
			Class(getDefinitionByName("mx.styles::StyleManagerImpl")));


		// The FlashVars of the SWF's HTML wrapper,
		// or the query parameters of the SWF URL,
		// can specify the ResourceManager's localeChain.
		var localeChainList:String =  
			loaderInfo.parameters["localeChain"];
		if (localeChainList != null && localeChainList != "")
			resourceManager.localeChain = localeChainList.split(",");

		// They can also specify a comma-separated list of URLs
		// for resource modules to be preloaded during frame 1.
		var resourceModuleURLList:String =
			loaderInfo.parameters["resourceModuleURLs"];
		var resourceModuleURLs:Array =
			resourceModuleURLList ? resourceModuleURLList.split(",") : null;

		// Initialize the preloader.
		preloader.initialize(
			usePreloader,
			preloaderDisplayClass,
			preloaderBackgroundColor,
			preloaderBackgroundAlpha,
			preloaderBackgroundImage,
			preloaderBackgroundSize,
			isStageRoot ? stage.stageWidth : loaderInfo.width,
			isStageRoot ? stage.stageHeight : loaderInfo.height,
		    null,
			null,
			rslList,
			resourceModuleURLs);
	}

	/**
	 *  @private
	 *  When this is called, we execute all callbacks queued up to this point.
	 */
	private function executeCallbacks():void
	{
		// temporary workaround for player bug.  The root class should always
		// be parented or we need some other way to determine
		// our application domain
		if (!parent)
			return;

		while (initCallbackFunctions.length > 0)
		{
			var initFunction:Function = initCallbackFunctions.shift();
			initFunction(this);
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Methods: Child management
	//
	//--------------------------------------------------------------------------

	/**
     *  @private
     */
	mx_internal function addingChild(child:DisplayObject):void
	{
		var newNestLevel:int = 1;
		if (!topLevel)
		{
			// non-topLevel SystemManagers are buried by Flash.display.Loader and
			// other non-framework layers so we have to figure out the nestlevel
			// by searching up the parent chain.
			var obj:DisplayObjectContainer = parent.parent;
			while (obj)
			{
				if (obj is ILayoutManagerClient)
				{
					newNestLevel = ILayoutManagerClient(obj).nestLevel + 1;
					break;
				}
				obj = obj.parent;
			}
		}
		nestLevel = newNestLevel;

		if (child is IUIComponent)
			IUIComponent(child).systemManager = this;

		// Local variables for certain classes we need to check against below.
		// This is the backdoor way around linking in the class in question.
		var uiComponentClassName:Class =
			Class(getDefinitionByName("mx.core.UIComponent"));

		// If the document property isn't already set on the child,
		// set it to be the same as this component's document.
		// The document setter will recursively set it on any
		// descendants of the child that exist.
		if (child is IUIComponent &&
			!IUIComponent(child).document)
		{
			IUIComponent(child).document = document;
		}

		// Set the nestLevel of the child to be one greater
		// than the nestLevel of this component.
		// The nestLevel setter will recursively set it on any
		// descendants of the child that exist.
		if (child is ILayoutManagerClient)
        	ILayoutManagerClient(child).nestLevel = nestLevel + 1;

		if (child is InteractiveObject)
			if (doubleClickEnabled)
				InteractiveObject(child).doubleClickEnabled = true;

		if (child is IUIComponent)
			IUIComponent(child).parentChanged(this);

		// Sets up the inheritingStyles and nonInheritingStyles objects
		// and their proto chains so that getStyle() works.
		// If this object already has some children,
		// then reinitialize the children's proto chains.
        if (child is IStyleClient)
			IStyleClient(child).regenerateStyleCache(true);

		if (child is ISimpleStyleClient)
			ISimpleStyleClient(child).styleChanged(null);

        if (child is IStyleClient)
			IStyleClient(child).notifyStyleChangeInChildren(null, true);

		// Need to check to see if the child is an UIComponent
		// without actually linking in the UIComponent class.
		if (uiComponentClassName && child is uiComponentClassName)
			uiComponentClassName(child).initThemeColor();

		// Inform the component that it's style properties
		// have been fully initialized. Most components won't care,
		// but some need to react to even this early change.
		if (uiComponentClassName && child is uiComponentClassName)
			uiComponentClassName(child).stylesInitialized();
	}

	/**
	 *  @private
	 */
	mx_internal function childAdded(child:DisplayObject):void
	{
		child.dispatchEvent(new FlexEvent(FlexEvent.ADD));

		if (child is IUIComponent)
			IUIComponent(child).initialize(); // calls child.createChildren()
	}

	/**
     *  @private
     */
	mx_internal function removingChild(child:DisplayObject):void
	{
		child.dispatchEvent(new FlexEvent(FlexEvent.REMOVE));
	}

	/**
     *  @private
     */
	mx_internal function childRemoved(child:DisplayObject):void
	{
		if (child is IUIComponent)
			IUIComponent(child).parentChanged(null);
	}

	//--------------------------------------------------------------------------
	//
	//  Methods: Support for rawChildren access
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	mx_internal function rawChildren_addChild(child:DisplayObject):DisplayObject
	{
		addingChild(child);

		super.addChild(child);

		childAdded(child); // calls child.createChildren()

		return child;
	}

	/**
	 *  @private
	 */
	mx_internal function rawChildren_addChildAt(child:DisplayObject,
												index:int):DisplayObject
	{
		addingChild(child);

		super.addChildAt(child, index);

		childAdded(child); // calls child.createChildren()

		return child;
	}

	/**
	 *  @private
	 */
	mx_internal function rawChildren_removeChild(child:DisplayObject):DisplayObject
	{
		removingChild(child);

		super.removeChild(child);

		childRemoved(child);

		return child;
	}

	/**
	 *  @private
	 */
	mx_internal function rawChildren_removeChildAt(index:int):DisplayObject
	{
		var child:DisplayObject = super.getChildAt(index);

		removingChild(child);

		super.removeChildAt(index);

		childRemoved(child);

		return child;
	}

	/**
	 *  @private
	 */
  	mx_internal function rawChildren_getChildAt(index:int):DisplayObject
	{
		return super.getChildAt(index);
	}

	/**
	 *  @private
	 */
  	mx_internal function rawChildren_getChildByName(name:String):DisplayObject
  	{
		return super.getChildByName(name);
  	}

	/**
	 *  @private
	 */
  	mx_internal function rawChildren_getChildIndex(child:DisplayObject):int
	{
		return super.getChildIndex(child);
	}

	/**
	 *  @private
	 */
	mx_internal function rawChildren_setChildIndex(child:DisplayObject, newIndex:int):void
	{
		super.setChildIndex(child, newIndex);
	}

	/**
	 *  @private
	 */
	mx_internal function rawChildren_getObjectsUnderPoint(pt:Point):Array
	{
		return super.getObjectsUnderPoint(pt);
	}

	/**
	 *  @private
	 */
	mx_internal function rawChildren_contains(child:DisplayObject):Boolean
	{
		return super.contains(child);
	}

	//--------------------------------------------------------------------------
	//
	//  Methods: Measurement and Layout
	//
	//--------------------------------------------------------------------------

    /**
     *  A convenience method for determining whether to use the
	 *  explicit or measured width.
	 *
     *  @return A Number which is the <code>explicitWidth</code> if defined,
	 *  or the <code>measuredWidth</code> property if not.
     */
    public function getExplicitOrMeasuredWidth():Number
    {
		return !isNaN(explicitWidth) ? explicitWidth : measuredWidth;
    }

    /**
     *  A convenience method for determining whether to use the
	 *  explicit or measured height.
	 *
     *  @return A Number which is the <code>explicitHeight</code> if defined,
	 *  or the <code>measuredHeight</code> property if not.
     */
    public function getExplicitOrMeasuredHeight():Number
    {
		return !isNaN(explicitHeight) ? explicitHeight : measuredHeight;
    }

	/**
	 *  Calling the <code>move()</code> method
	 *  has no effect as it is directly mapped
	 *  to the application window or the loader.
	 *
	 *  @param x The new x coordinate.
	 *
	 *  @param y The new y coordinate.
	 */
	public function move(x:Number, y:Number):void
	{
	}

	/**
	 *  Calling the <code>setActualSize()</code> method
	 *  has no effect if it is directly mapped
	 *  to the application window and if it is the top-level window.
	 *  Otherwise attempts to resize itself, clipping children if needed.
	 *
	 *  @param newWidth The new width.
	 *
	 *  @param newHeight The new height.
	 */
	public function setActualSize(newWidth:Number, newHeight:Number):void
	{
		if (isStageRoot) return;

		_width = newWidth;
		_height = newHeight;

		// mouseCatcher is a mask if not stage root
		if (mouseCatcher)
		{
			mouseCatcher.width = newWidth;
			mouseCatcher.height = newHeight;
		}

		dispatchEvent(new Event(Event.RESIZE));
	}

	//--------------------------------------------------------------------------
	//
	//  Methods: Styles
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Call regenerateStyleCache() on all children of this SystemManager.
	 *  If the recursive parameter is true, continue doing this
	 *  for all descendants of these children.
	 */
	mx_internal function regenerateStyleCache(recursive:Boolean):void
	{
		var foundTopLevelWindow:Boolean = false;

		var n:int = rawChildren.numChildren;
		for (var i:int = 0; i < n; i++)
		{
			var child:IStyleClient =
				rawChildren.getChildAt(i) as IStyleClient;

			if (child)
				child.regenerateStyleCache(recursive);

			if (isTopLevelWindow(DisplayObject(child)))
				foundTopLevelWindow = true;

			// Refetch numChildren because notifyStyleChangedInChildren()
			// can add/delete a child and therefore change numChildren.
			n = rawChildren.numChildren;
		}

		// During startup the top level window isn't added
		// to the child list until late into the startup sequence.
		// Make sure we call regenerateStyleCache()
		// on the top level window even if it isn't a child yet.
		if (!foundTopLevelWindow && topLevelWindow is IStyleClient)
			IStyleClient(topLevelWindow).regenerateStyleCache(recursive);
	}

	/**
	 *  @private
	 *  Call styleChanged() and notifyStyleChangeInChildren()
	 *  on all children of this SystemManager.
	 *  If the recursive parameter is true, continue doing this
	 *  for all descendants of these children.
	 */
	mx_internal function notifyStyleChangeInChildren(styleProp:String,
													 recursive:Boolean):void
	{
		var foundTopLevelWindow:Boolean = false;

		var n:int = rawChildren.numChildren;
		for (var i:int = 0; i < n; i++)
		{
			var child:IStyleClient =
				rawChildren.getChildAt(i) as IStyleClient;

			if (child)
			{
				child.styleChanged(styleProp);
				child.notifyStyleChangeInChildren(styleProp, recursive);
			}

			if (isTopLevelWindow(DisplayObject(child)))
				foundTopLevelWindow = true;

			// Refetch numChildren because notifyStyleChangedInChildren()
			// can add/delete a child and therefore change numChildren.
			n = rawChildren.numChildren;
		}

		// During startup the top level window isn't added
		// to the child list until late into the startup sequence.
		// Make sure we call notifyStyleChangeInChildren()
		// on the top level window even if it isn't a child yet.
		if (!foundTopLevelWindow && topLevelWindow is IStyleClient)
		{
			IStyleClient(topLevelWindow).styleChanged(styleProp);
			IStyleClient(topLevelWindow).notifyStyleChangeInChildren(
				styleProp, recursive);
		}
	}


	//--------------------------------------------------------------------------
	//
	//  Methods: Focus
	//
	//--------------------------------------------------------------------------

	/**
	 *  @inheritDoc
	 */
	public function activate(f:IFocusManagerContainer):void
	{
		// trace("SM: activate " + f + " " + forms.length);

		if (form)
		{
			if (form != f && forms.length > 1)
			{
				// Switch the active form.
				var z:IFocusManagerContainer = form;
				// trace("OLW " + f + " deactivating old form " + z);
				z.focusManager.deactivate();
			}
		}

		form = f;

		// trace("f = " + f);
		if (f.focusManager)
			// trace("has focus manager");

		f.focusManager.activate();

		// trace("END SM: activate " + f);
	}

	/**
	 *  @inheritDoc
	 */
	public function deactivate(f:IFocusManagerContainer):void
	{
		// trace(">>SM: deactivate " + f);

		if (form)
		{
			// If there's more thna one form and this is it, find a new form.
			if (form == f && forms.length > 1)
			{
				form.focusManager.deactivate();

				var newForm:IFocusManagerContainer;

				var n:int = forms.length;
				for (var i:int = 0; i < n; i++)
				{
					var g:IFocusManagerContainer = forms[i];
					if (g == f)
					{
						// use the first form above us in taborder, or the first one below.
						for (i = i + 1; i < n; i++)
						{
							g = forms[i];
							// remember the highest visible window.
							if (Sprite(g).visible == true && IUIComponent(g).enabled)
								newForm = g;
						}
						form = newForm;
						break;
					}
					else
					{
						// remember the highest visible window.
						if (Sprite(g).visible && IUIComponent(g).enabled)
							newForm = g;
					}
				}

				// make sure we have a valid top level window.
				// This can be null if top level window has been hidden for some reason.
				if (form)
					form.focusManager.activate();
			}
		}

		// trace("<<SM: deactivate " + f);
	}

	/**
	 *  @inheritDoc
	 */
	public function addFocusManager(f:IFocusManagerContainer):void
	{
		// trace("OLW: add focus manager" + f);

		forms.push(f);

		// trace("END OLW: add focus manager" + f);
	}

	/**
	 *  @inheritDoc
	 */
	public function removeFocusManager(f:IFocusManagerContainer):void
	{
		// trace("OLW: remove focus manager" + f);

		var n:int = forms.length;
		for (var i:int = 0; i < n; i++)
		{
			if (forms[i] == f)
			{
				if (form == f)
					deactivate(f);
				forms.splice(i, 1);
				// trace("END OLW: successful remove focus manager" + f);
				return;
			}
		}

		// trace("END OLW: remove focus manager" + f);
	}

	//--------------------------------------------------------------------------
	//
	//  Methods: Other
	//
	//--------------------------------------------------------------------------

	/**
	 *  @inheritDoc
	 */
	public function getDefinitionByName(name:String):Object
	{
		var domain:ApplicationDomain =
			!topLevel && parent is Loader ?
			Loader(parent).contentLoaderInfo.applicationDomain :
            info()["currentDomain"] as ApplicationDomain;

		//trace("SysMgr.getDefinitionByName domain",domain,"currentDomain",info()["currentDomain"]);	
			
        var definition:Object;

        if (domain.hasDefinition(name))
		{
			definition = domain.getDefinition(name);
			//trace("SysMgr.getDefinitionByName got definition",definition,"name",name);
		}

		return definition;
	}

	/**
	 *  Returns the root DisplayObject of the SWF that contains the code
	 *  for the given object.
	 *
	 *  @param object Any Object. 
	 * 
	 *  @return The root DisplayObject
	 */
	public static function getSWFRoot(object:Object):DisplayObject
	{
		var className:String = getQualifiedClassName(object);

		for (var p:* in allSystemManagers)
		{
			var sm:ISystemManager = p as ISystemManager;
			var domain:ApplicationDomain = sm.loaderInfo.applicationDomain;
			try
			{
				var cls:Class = Class(domain.getDefinition(className));
				if (object is cls)
					return sm as DisplayObject;
			}
			catch(e:Error)
			{
			}
		}
		return null;
	}
	
	/**
	 *  @inheritDoc
	 */
	public function isTopLevel():Boolean
	{
		return topLevel;
	}

	/**
	 *  Returns <code>true</code> if the given DisplayObject is the 
	 *  top-level window.
	 *
	 *  @param object The DisplayObject to test.
	 *
	 *  @return <code>true</code> if the given DisplayObject is the 
	 *  top-level window.
	 */
	public function isTopLevelWindow(object:DisplayObject):Boolean
	{
		return object is IUIComponent &&
			   IUIComponent(object) == topLevelWindow;
	}

	/**
	 *  @inheritDoc
	 */
    public function isFontFaceEmbedded(textFormat:TextFormat):Boolean
    {
        var fontName:String = textFormat.font;

        var fl:Array = Font.enumerateFonts();
        for (var f:int = 0; f < fl.length; ++f)
        {
            var font:Font = Font(fl[f]);
            if (font.fontName == fontName)
            {
                var style:String = "regular";
                if (textFormat.bold && textFormat.italic)
                    style = "boldItalic";
                else if (textFormat.bold)
                    style = "bold";
                else if (textFormat.italic)
                    style = "italic";

                if (font.fontStyle == style)
                    return true;
            }
        }

		if (!fontName ||
			!embeddedFontList ||
			!embeddedFontList[fontName])
        {
            return false;
        }

        var info:Object = embeddedFontList[fontName];

		return !((textFormat.bold && !info.bold) ||
				 (textFormat.italic && !info.italic) ||
				 (!textFormat.bold && !textFormat.italic &&
				 !info.regular));
    }

	/**
	 *  @private
	 *  Makes the mouseCatcher the same size as the stage,
	 *  filling it with transparent pixels.
	 */
	private function resizeMouseCatcher():void
	{
		if (mouseCatcher)
		{
			var g:Graphics = mouseCatcher.graphics;
			g.clear();
			g.beginFill(0x000000, 0);
			g.drawRect(0, 0, stage.stageWidth, stage.stageHeight);
			g.endFill();
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function initHandler(event:Event):void
	{
		allSystemManagers[this] = this.loaderInfo.url;
	    root.loaderInfo.removeEventListener(Event.INIT, initHandler);

	    var docFrame:int = (totalFrames == 1)? 0 : 1;

        addFrameScript(docFrame, docFrameHandler);
	    for (var f:int = docFrame + 1; f < totalFrames; ++f)
	    {
		    addFrameScript(f, extraFrameHandler);
		}

	    initialize();
	}

	/**
	 *  @private
	 *  Once the swf has been fully downloaded,
	 *  advance the playhead to the next frame.
	 *  This will cause the framescript to run, which runs frameEndHandler().
	 */
	private function preloader_initProgressHandler(event:Event):void
	{
		// Advance the next frame
		preloader.removeEventListener(FlexEvent.INIT_PROGRESS,
									  preloader_initProgressHandler);

        deferredNextFrame();
	}

	/**
	 *  @private
	 *  Remove the preloader and add the application as a child.
	 */
	private function preloader_preloaderDoneHandler(event:Event):void
	{
		var app:IUIComponent = topLevelWindow;

		// Once the preloader dispatches the PRELOADER_DONE event, remove the preloader
		// and add the application as the child
		preloader.removeEventListener(FlexEvent.PRELOADER_DONE,
									  preloader_preloaderDoneHandler);

		_popUpChildren.removeChild(preloader);
        preloader = null;

		// Add the mouseCatcher as child 0.
		mouseCatcher = new FlexSprite();
		mouseCatcher.name = "mouseCatcher";
		// Must use addChildAt because a creationComplete handler can create a
		// dialog and insert it at 0.
		noTopMostIndex++;
		super.addChildAt(mouseCatcher, 0);	
		resizeMouseCatcher();
		if (!topLevel)
		{
			mouseCatcher.visible = false;
			mask = mouseCatcher;
		}

		// Add the application as child 1.
		noTopMostIndex++;
		super.addChildAt(DisplayObject(app), 1);
		
		// Dispatch the applicationComplete event from the Application
		// and then agaom from the SystemManager
		// (so that loading apps know we're done).
		app.dispatchEvent(new FlexEvent(FlexEvent.APPLICATION_COMPLETE));
		dispatchEvent(new FlexEvent(FlexEvent.APPLICATION_COMPLETE));
	}

	/**
	 *  @private
	 *  This is attached as the framescript at the end of frame 2.
	 *  When this function is called, we know that the application
	 *  class has been defined and read in by the Player.
	 */
	mx_internal function docFrameHandler(event:Event = null):void
	{
		// The ResourceManager has already been registered 
		// by initialize() in frame 1.
		
		// Register other singleton classes.
		// Note: getDefinitionByName() will return null
		// if the class can't be found.

		Singleton.registerClass("mx.managers::IBrowserManager",
			Class(getDefinitionByName("mx.managers::BrowserManagerImpl")));

		Singleton.registerClass("mx.managers::ICursorManager",
			Class(getDefinitionByName("mx.managers::CursorManagerImpl")));

		Singleton.registerClass("mx.managers::IHistoryManager",
			Class(getDefinitionByName("mx.managers::HistoryManagerImpl")));

		Singleton.registerClass("mx.managers::ILayoutManager",
			Class(getDefinitionByName("mx.managers::LayoutManager")));

		Singleton.registerClass("mx.managers::IPopUpManager",
			Class(getDefinitionByName("mx.managers::PopUpManagerImpl")));

		Singleton.registerClass("mx.managers::IToolTipManager2",
			Class(getDefinitionByName("mx.managers::ToolTipManagerImpl")));

		if (Capabilities.playerType == "Desktop")
		{
			Singleton.registerClass("mx.managers::IDragManager",
				Class(getDefinitionByName("mx.managers::NativeDragManagerImpl")));
				
			// Make this call to create a new instance of the DragManager singleton. 
			// This will allow the application to receive NativeDragEvents that originate
			// from the desktop.
			// if this class is not registered, it's most likely because the NativeDragManager is not
			// linked in correctly. all back to old DragManager.
			if (Singleton.getClass("mx.managers::IDragManager") == null)
				Singleton.registerClass("mx.managers::IDragManager",
					Class(getDefinitionByName("mx.managers::DragManagerImpl")));
		}
		else
		{ 
			Singleton.registerClass("mx.managers::IDragManager",
				Class(getDefinitionByName("mx.managers::DragManagerImpl")));
		}

		var textFieldFactory:TextFieldFactory; // ref to cause TextFieldFactory to be linked in
		Singleton.registerClass("mx.core::ITextFieldFactory", 
			Class(getDefinitionByName("mx.core::TextFieldFactory")));


		executeCallbacks();
		doneExecutingInitCallbacks = true;

        var mixinList:Array = info()["mixins"];
		if (mixinList && mixinList.length > 0)
		{
		    var n:int = mixinList.length;
			for (var i:int = 0; i < n; ++i)
		    {
		        // trace("initializing mixin " + mixinList[i]);
		        var c:Class = Class(getDefinitionByName(mixinList[i]));
		        c["init"](this);
		    }
        }
		
		installCompiledResourceBundles();

		initializeTopLevelWindow(null);

		deferredNextFrame();
	}

	private function installCompiledResourceBundles():void
	{
		var info:Object = this.info();
		
		var applicationDomain:ApplicationDomain =
			!topLevel && parent is Loader ?
			Loader(parent).contentLoaderInfo.applicationDomain :
            info["currentDomain"];

		var compiledLocales:Array /* of String */ =
			info["compiledLocales"];

		var compiledResourceBundleNames:Array /* of String */ =
			info["compiledResourceBundleNames"];
		
		var resourceManager:IResourceManager =
			ResourceManager.getInstance();
		
		resourceManager.installCompiledResourceBundles(
			applicationDomain, compiledLocales, compiledResourceBundleNames);

		// If the localeChain wasn't specified in the FlashVars of the SWF's
		// HTML wrapper, or in the query parameters of the SWF URL,
		// then initialize it to the list of compiled locales,
        // sorted according to the system's preferred locales as reported by
        // Capabilities.languages or Capabilities.language.
		// For example, if the applications was compiled with, say,
		// -locale=en_US,ja_JP and Capabilities.languages reports [ "ja-JP" ],
        // set the localeChain to [ "ja_JP" "en_US" ].
		if (!resourceManager.localeChain)
			resourceManager.initializeLocaleChain(compiledLocales);
	}

	private function extraFrameHandler(event:Event = null):void
	{
	    var frameList:Object = info()["frames"];

	    if (frameList && frameList[currentLabel])
	    {
	        var c:Class = Class(getDefinitionByName(frameList[currentLabel]));
	        c["frame"](this);
	    }

	    deferredNextFrame();
	}

    /**
	 *  @private
	 */
	private function nextFrameTimerHandler(event:TimerEvent):void
	{
	    if (currentFrame + 1 <= framesLoaded)
	    {
	        nextFrame();
            nextFrameTimer.removeEventListener(TimerEvent.TIMER, nextFrameTimerHandler);
        	// stop the timer
        	nextFrameTimer.reset();
        }
    }
	
	/**
	 *  @private
	 *  Instantiates an instance of the top level window
	 *  and adds it as a child of the SystemManager.
	 */
	private function initializeTopLevelWindow(event:Event):void
	{
		initialized = true;

		if (!parent)
			return;
		
		if (!topLevel)
		{
			var obj:DisplayObjectContainer = parent.parent;

  			// if there is no grandparent at this point, we might have been removed and
  			// are about to be killed so just bail.  Other code that runs after
  			// this point expects us to be grandparented.  Another scenario
  			// is that someone loaded us but not into a parented loader, but that
  			// is not allowed.
  			if (!obj)
  				return;
  
			while (obj)
			{
				if (obj is IUIComponent)
				{
					_topLevelSystemManager = IUIComponent(obj).systemManager;
					break;
				}
				obj = obj.parent;
			}
		}

		// capture mouse down so we can switch top level windows and activate
		// the right focus manager before the components inside start
		// processing the event
		addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler, true); 

		if (topLevel && stage)
		 	stage.addEventListener(Event.RESIZE, Stage_resizeHandler, false, 0, true);

		var app:IUIComponent;
		// Create a new instance of the toplevel class
        document = app = topLevelWindow = IUIComponent(create());

		if (document)
		{
			// Add listener for the creationComplete event
			IEventDispatcher(app).addEventListener(FlexEvent.CREATION_COMPLETE,
												   appCreationCompleteHandler);

			if (topLevel && stage)
			{
				LoaderConfig._url = loaderInfo.url;
				LoaderConfig._parameters = loaderInfo.parameters;
				
				// stageWidth/stageHeight may have changed between initialize() and now,
				// so refresh our _width and _height here. 
				_width = stage.stageWidth;
				_height = stage.stageHeight;
				
				IFlexDisplayObject(app).setActualSize(stage.stageWidth, stage.stageHeight);
			}
			else
			{
				IFlexDisplayObject(app).setActualSize(loaderInfo.width, loaderInfo.height);
			}

			// Wait for the app to finish its initialization sequence
			// before doing an addChild(). 
			// Otherwise, the measurement/layout code will cause the
			// player to do a bunch of unnecessary screen repaints,
			// which slows application startup time.
			
			// Pass the application instance to the preloader.
			// Note: preloader can be null when the user chooses
			// Control > Play in the standalone player.
			if (preloader)
				preloader.registerApplication(app);
						
			// The Application doesn't get added to the SystemManager in the standard way.
			// We want to recursively create the entire application subtree and process
			// it with the LayoutManager before putting the Application on the display list.
			// So here we what would normally happen inside an override of addChild().
			// Leter, when we actually attach the Application instance,
			// we call super.addChild(), which is the bare player method.
			addingChild(DisplayObject(app));
			childAdded(DisplayObject(app)); // calls app.createChildren()
		}
		else
		{
			document = this;
		}
	}
	
	/**
	 *  Override this function if you want to perform any logic
	 *  when the application has finished initializing itself.
	 */
	private function appCreationCompleteHandler(event:FlexEvent):void
	{
		if (!topLevel && parent)
		{
			var obj:DisplayObjectContainer = parent.parent;
			while (obj)
			{
				if (obj is IInvalidating)
				{
					IInvalidating(obj).invalidateSize();
					IInvalidating(obj).invalidateDisplayList();
					return;
				}
				obj = obj.parent;
			}
		}
	}
	
	/**
	 *  @private
	 *  Keep track of the size and position of the stage.
	 */
	private function Stage_resizeHandler(event:Event = null):void
	{	
		var w:Number = stage.stageWidth;
		var h:Number = stage.stageHeight;
		var m:Number = loaderInfo.width;
		var n:Number = loaderInfo.height;

		var x:Number = (m - w) / 2;
		var y:Number = (n - h) / 2;
		
		var align:String = stage.align;

		if (align == StageAlign.TOP)
		{
			y = 0;
		}
		else if (align == StageAlign.BOTTOM)
		{
			y = n - h;
		}
		else if (align == StageAlign.LEFT)
		{
			x = 0;
		}
		else if (align == StageAlign.RIGHT)
		{
			x = m - w;
		}
		else if (align == StageAlign.TOP_LEFT || align == "LT") // player bug 125020
		{
			y = 0;
			x = 0;
		}
		else if (align == StageAlign.TOP_RIGHT)
		{
			y = 0;
			x = m - w;
		}
		else if (align == StageAlign.BOTTOM_LEFT)
		{
			y = n - h;
			x = 0;
		}
		else if (align == StageAlign.BOTTOM_RIGHT)
		{
			y = n - h;
			x = m - w;
		}
		
		if (!_screen)
			_screen = new Rectangle();
		_screen.x = x;
		_screen.y = y;
		_screen.width = w;
		_screen.height = h;

		if (isStageRoot)
		{
			_width = stage.stageWidth;
			_height = stage.stageHeight;
		}

		if (event)
		{
			resizeMouseCatcher();
			dispatchEvent(event);
		}
	}

	/**
	 *  @private
	 *  Track mouse clicks to see if we change top-level forms.
	 */
	private function mouseDownHandler(event:MouseEvent):void
	{
		// Reset the idle counter.
		idleCounter = 0;

		if (numModalWindows == 0) // no modal windows are up
		{
			// Activate a window if we need to.
			if (forms.length > 1)
			{
				var n:int = forms.length;
				var p:DisplayObject = DisplayObject(event.target);
				var isApplication:Boolean = document.rawChildren.contains(p);
				while (p)
				{
					for (var i:int = 0; i < n; i++)
					{
						if (forms[i] == p)
						{
							var j:int = 0;
							var index:int;
							var newIndex:int;
							var childList:IChildList;

							if (p != form && p is IFocusManagerContainer)
								activate(IFocusManagerContainer(p));
							if (popUpChildren.contains(p))
								childList = popUpChildren;
							else
								childList = this;

							index = childList.getChildIndex(p); 
							newIndex = index;
							
							//we need to reset n because activating p's 
							//FocusManager could have caused 
							//forms.length to have changed. 
							n = forms.length;
							for (j = 0; j < n; j++)
							{
								if (childList.contains(forms[j]))
									if (childList.getChildIndex(forms[j]) > index)
										newIndex = Math.max(childList.getChildIndex(forms[j]), newIndex);

							}
							if (newIndex > index && !isApplication)
								childList.setChildIndex(p, newIndex);
							return;
						}
					}
					p = p.parent;
				}
			}
		}
	}

	/**
	 *  @private
	 *  Track mouse moves in order to determine idle
	 */
	private function mouseMoveHandler(event:MouseEvent):void
	{
		// Reset the idle counter.
		idleCounter = 0;
	}

	/**
	 *  @private
	 *  Track mouse moves in order to determine idle.
	 */
	private function mouseUpHandler(event:MouseEvent):void
	{
		// Reset the idle counter.
		idleCounter = 0;
	}

	/**
	 *  @private
	 *  Called every IDLE_INTERVAL after the first listener
	 *  registers for 'idle' events.
	 *  After IDLE_THRESHOLD goes by without any user activity,
	 *  we dispatch an 'idle' event.
	 */
	private function idleTimer_timerHandler(event:TimerEvent):void
	{
		idleCounter++;

		if (idleCounter * IDLE_INTERVAL > IDLE_THRESHOLD)
			dispatchEvent(new FlexEvent(FlexEvent.IDLE));
	}

	// fake out mouseX/mouseY
	mx_internal var _mouseX:*;
	mx_internal var _mouseY:*;


	/**
	 *  @private
	 */
	override public function get mouseX():Number
	{
		if (_mouseX === undefined)
			return super.mouseX;
		return _mouseX;
	}

	/**
	 *  @private
	 */
	override public function get mouseY():Number
	{
		if (_mouseY === undefined)
			return super.mouseY;
		return _mouseY;
	}
}

}
