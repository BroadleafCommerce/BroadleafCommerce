////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
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
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.display.StageAlign;
import flash.display.StageScaleMode;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.system.ApplicationDomain;
import flash.text.Font;
import flash.text.TextFormat;
import flash.ui.ContextMenu;

import mx.core.FlexSprite;
import mx.core.IChildList;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModule;
import mx.core.IUIComponent;
import mx.core.Singleton;
import mx.core.Window;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.styles.ISimpleStyleClient;
import mx.styles.IStyleClient;


use namespace mx_internal;

/**
 *  The WindowedSystemManager class manages any non-Application windows in a 
 *  Flex-based AIR application. This includes all windows that are instances of 
 *  the Window component or a Window subclass, but not a WindowedApplication 
 *  window. For those windows, the WindowedSystemManager serves the same role 
 *  that a SystemManager serves for a WindowedApplication instance or an 
 *  Application instance in a browser-based Flex application.
 * 
 *  <p>As this comparison suggests, the WindowedSystemManager class serves 
 *  many roles. For instance, it is the root display object of a Window, and 
 *  manages tooltips, cursors, popups, and other content for the Window.</p>
 * 
 *  @see mx.managers.SystemManager
 * 
 *  @playerversion AIR 1.1
 */
public class WindowedSystemManager extends MovieClip implements ISystemManager
{
	
	public function WindowedSystemManager(rootObj:IUIComponent)
	{
		super();
		_topLevelSystemManager = this;
		topLevelWindow = rootObj;
		SystemManagerGlobals.topLevelSystemManagers.push(this);
		//docFrameHandler(null);
		addEventListener(Event.ADDED, docFrameHandler);
	}
	
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
	
	private var topLevel:Boolean = true;
	
	private var initialized:Boolean = false;
	
	/**
	 *  @private
	 *  Number of frames since the last mouse or key activity.
	 */
	mx_internal var idleCounter:int = 0;
	
	/**
	 *  @private
	 *  The top level window.
	 */
	mx_internal var topLevelWindow:IUIComponent;
	
	/**
	 *  @private
	 *  pointer to Window, for cleanup
	 */
	private var myWindow:Window;
	
	/**
	 *  @private
	 */
	private var originalSystemManager:SystemManager;
	
	/**
	 *  @private
	 */
	private var _topLevelSystemManager:ISystemManager;
	
		/**
	 *  Depth of this object in the containment hierarchy.
	 *  This number is used by the measurement and layout code.
	 */
	mx_internal var nestLevel:int = 0;

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
	
	//-----------------------------------
	//  ISystemManager implementations
	//-----------------------------------
		
	//----------------------------------
	//  cursorChildren
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the cursorChildren property.
	 */
	private var _cursorChildren:WindowedSystemChildrenList;

	/**
	 *  @inheritDoc
	 */
	public function get cursorChildren():IChildList
	{
		if (!topLevel)
			return _topLevelSystemManager.cursorChildren;

		if (!_cursorChildren)
		{
			_cursorChildren = new WindowedSystemChildrenList(this,
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
	//  popUpChildren
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the popUpChildren property.
	 */
	private var _popUpChildren:WindowedSystemChildrenList;

	/**
	 *  @inheritDoc
	 */
	public function get popUpChildren():IChildList
	{
		if (!topLevel)
			return _topLevelSystemManager.popUpChildren;

		if (!_popUpChildren)
		{
			_popUpChildren = new WindowedSystemChildrenList(this,
				new QName(mx_internal, "noTopMostIndex"),
				new QName(mx_internal, "topMostIndex"));
		}

		return _popUpChildren;
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
	//  rawChildren
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the rawChildren property.
	 */
	private var _rawChildren:WindowedSystemRawChildrenList;

	/**
	 *  @inheritDoc
	 */
	public function get rawChildren():IChildList
	{
		if (!topLevel)
			return _topLevelSystemManager.rawChildren;

		if (!_rawChildren)
			_rawChildren = new WindowedSystemRawChildrenList(this);

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
			_screen = new Rectangle();
		_screen.x = 0;
		_screen.y = 0;
		_screen.width = stage.stageWidth; //Capabilities.screenResolutionX;
		_screen.height = stage.stageHeight; //Capabilities.screenResolutionY;


		return _screen;
	}
	
	//----------------------------------
	//  toolTipChildren
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the toolTipChildren property.
	 */
	private var _toolTipChildren:WindowedSystemChildrenList;

	/**
	 *  @inheritDoc
	 */
	public function get toolTipChildren():IChildList
	{
		if (!topLevel)
			return _topLevelSystemManager.toolTipChildren;

		if (!_toolTipChildren)
		{
			_toolTipChildren = new WindowedSystemChildrenList(this,
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
	
	//----------------------------------
    //  window
    //----------------------------------
    /**
	 *  @private
	 */	
	private var _window:Window = null;
	
	mx_internal function get window():Window
	{
		return _window;
	}
	
	mx_internal function set window(value:Window):void
	{
		_window = value;
	}
	
	
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
    //  Methods: Access to overridden methods of base classes
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This method allows access to the Player's native implementation
     *  of addChild(), which can be useful since components
     *  can override addChild() and thereby hide the native implementation.
     *  Note that this "base method" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function $addChild(child:DisplayObject):DisplayObject
    {
        return super.addChild(child);
    }

    /**
     *  @private
     *  This method allows access to the Player's native implementation
     *  of addChildAt(), which can be useful since components
     *  can override addChildAt() and thereby hide the native implementation.
     *  Note that this "base method" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function $addChildAt(child:DisplayObject,
                                           index:int):DisplayObject
    {
        return super.addChildAt(child, index);
    }

    /**
     *  @private
     *  This method allows access to the Player's native implementation
     *  of removeChild(), which can be useful since components
     *  can override removeChild() and thereby hide the native implementation.
     *  Note that this "base method" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function $removeChild(child:DisplayObject):DisplayObject
    {
        return super.removeChild(child);
    }

    /**
     *  @private
     *  This method allows access to the Player's native implementation
     *  of removeChildAt(), which can be useful since components
     *  can override removeChildAt() and thereby hide the native implementation.
     *  Note that this "base method" is final and cannot be overridden,
     *  so you can count on it to reflect what is happening at the player level.
     */
    mx_internal final function $removeChildAt(index:int):DisplayObject
    {
        return super.removeChildAt(index);
    }
	
	//--------------------------------------------------------------------------
	//
	//  Methods: Initialization
	//
	//--------------------------------------------------------------------------

	/**
	 *  This method is overridden in the autogenerated subclass.
	 */
	public function create(... params):Object
	{
		var mainClassName:String = String(params[0]);
	    
		var mainClass:Class = Class(getDefinitionByName(mainClassName));
		if (!mainClass)
			throw new Error("Class '" + mainClassName + "' not found.");

		var instance:Object = new mainClass();
		if (instance is IFlexModule)
			(IFlexModule(instance)).moduleFactory = this;
		return instance;
	}
	
	/**
	 *  @private
	 *  This is attached as the framescript at the end of frame 2.
	 *  When this function is called, we know that the application
	 *  class has been defined and read in by the Player.
	 */
	protected function docFrameHandler(event:Event = null):void
	{
		removeEventListener(Event.ADDED, docFrameHandler);
		
		// Register singleton classes.
		// Note: getDefinitionByName() will return null
		// if the class can't be found.
		/*
		Singleton.registerClass("mx.managers::ICursorManager",
			Class(getDefinitionByName("mx.managers::CursorManagerImpl")));

		Singleton.registerClass("mx.managers::IDragManager",
			Class(getDefinitionByName("mx.managers::DragManagerImpl")));

		Singleton.registerClass("mx.managers::IHistoryManager",
			Class(getDefinitionByName("mx.managers::HistoryManagerImpl")));

		Singleton.registerClass("mx.managers::ILayoutManager",
			Class(getDefinitionByName("mx.managers::LayoutManager")));

		Singleton.registerClass("mx.managers::IPopUpManager",
			Class(getDefinitionByName("mx.managers::PopUpManagerImpl")));

		Singleton.registerClass("mx.styles::IStyleManager",
			Class(getDefinitionByName("mx.styles::StyleManagerImpl")));

		Singleton.registerClass("mx.styles::IStyleManager2",
			Class(getDefinitionByName("mx.styles::StyleManagerImpl")));

		Singleton.registerClass("mx.managers::IToolTipManager2",
			Class(getDefinitionByName("mx.managers::ToolTipManagerImpl")));*/

//		executeCallbacks();
//		doneExecutingInitCallbacks = true;

		// Loaded SWFs don't get a stage right away
		// and shouldn't override the main SWF's setting anyway.
		if (stage)
		{
			stage.scaleMode = StageScaleMode.NO_SCALE;
			stage.align = StageAlign.TOP_LEFT;
		}

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
		
	//	installCompiledResourceBundles();

		initializeTopLevelWindow(null);
		
		if (Singleton.getClass("mx.managers::IDragManager").getInstance() is NativeDragManagerImpl)
			NativeDragManagerImpl(Singleton.getClass("mx.managers::IDragManager").getInstance()).registerSystemManager(this);
	}
	
	/**
	 *  @private
	 *  Instantiates an instance of the top level window
	 *  and adds it as a child of the SystemManager.
	 */
	protected function initializeTopLevelWindow(event:Event):void
	{
		initialized = true;

		if (!parent)
			return;
		
		initContextMenu();
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

	//	if (topLevel && stage)
		 	stage.addEventListener(Event.RESIZE, Stage_resizeHandler, false, 0, true);

		var app:IUIComponent;
		// Create a new instance of the toplevel class
        document = app = topLevelWindow;// = IUIComponent(create());

		if (document)
		{
			// Add listener for the creationComplete event
/*			IEventDispatcher(app).addEventListener(FlexEvent.CREATION_COMPLETE,
												   appCreationCompleteHandler);
*/
			if (topLevel && stage)
			{
			//	LoaderConfig._url = loaderInfo.url;
			//	LoaderConfig._parameters = loaderInfo.parameters;
				
				// stageWidth/stageHeight may have changed between initialize() and now,
				// so refresh our _width and _height here. 
				_width = stage.stageWidth;
				_height = stage.stageHeight;
				//trace("width", _width);
				
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
			
			// Pass in the application instance to the preloader using registerApplication
		//	preloader.registerApplication(app);
						
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
	
		// because we have no preload done handler, we need to 
		// do that work elsewhere
		addChildAndMouseCatcher();
	}
	    	
	/**
	 *  @private
	 *  Same as SystemManager's preload done handler.  It adds 
	 *  the window to the application (and a mouse catcher)
	 * 
	 *  Called from initializeTopLevelWindow()
	 */
	private function addChildAndMouseCatcher():void
	{
		var app:IUIComponent = topLevelWindow;
		// Add the mouseCatcher as child 0.
		mouseCatcher = new FlexSprite();
		mouseCatcher.name = "mouseCatcher";
		
		// Must use addChildAt because a creationComplete handler can create a
		// dialog and insert it at 0.
		noTopMostIndex++;
		super.addChildAt(mouseCatcher, 0);	
		resizeMouseCatcher();
		
		// topLevel seems to always be true, but keeping it here just in case
		if (!topLevel)
		{
			mouseCatcher.visible = false;
			mask = mouseCatcher;
		}
		
		noTopMostIndex++;
	    super.addChild(DisplayObject(app));
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

	
    /**
     *  @private
     *  Disable all the built-in items except "Print...".
     */
    private function initContextMenu():void
    {
        var defaultMenu:ContextMenu = new ContextMenu();
        defaultMenu.hideBuiltInItems();
        defaultMenu.builtInItems.print = true;
        contextMenu = defaultMenu;
    }
		/**
	 *  Returns <code>true</code> if the given DisplayObject is the 
	 *  top-level window.
	 *
	 *  @param object 
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
	public function getDefinitionByName(name:String):Object
	{
		var domain:ApplicationDomain = ApplicationDomain.currentDomain;
	/*		!topLevel && parent is Loader ?
			Loader(parent).contentLoaderInfo.applicationDomain :
            info()["currentDomain"] as ApplicationDomain;
*/
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
	 *  @inheritDoc
	 */
	public function isTopLevel():Boolean
	{
		return topLevel;
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
	 *  Keep track of the size and position of the stage.
	 */
	private function Stage_resizeHandler(event:Event = null):void
	{	
		var w:Number = stage.stageWidth;
		var h:Number = stage.stageHeight;
	
		var	y:Number = 0;
		var	x:Number = 0;
		
		if (!_screen)
			_screen = new Rectangle();
		_screen.x = x;
		_screen.y = y;
		_screen.width = w;
		_screen.height = h;

		
		_width = stage.stageWidth;
		_height = stage.stageHeight;
		
//trace(_width, event.type);
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
										newIndex = childList.getChildIndex(forms[j]);

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
		return super.getChildAt(applicationIndex + index);
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
		super.setChildIndex(child, applicationIndex + newIndex);
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

	/**
	 *  @private
	 *  Cleans up references to Window. Also removes self from topLevelSystemManagers list. 
	 */
	mx_internal function cleanup(e:Event):void
	{
		if (NativeDragManagerImpl(Singleton.getClass("mx.managers::IDragManager").getInstance())
					 is NativeDragManagerImpl)
			NativeDragManagerImpl(Singleton.getClass("mx.managers::IDragManager").getInstance()).unregisterSystemManager(this);
		SystemManagerGlobals.topLevelSystemManagers.splice(SystemManagerGlobals.topLevelSystemManagers.indexOf(this), 1);
		myWindow.removeEventListener("close", cleanup);
		myWindow = null;
	}

	/**
	 *  @private
	 *  only registers Window for later cleanup.
	 */
	mx_internal function addWindow(win:Window):void
	{
		myWindow = win;
		myWindow.nativeWindow.addEventListener("close", cleanup);
	}
}
}