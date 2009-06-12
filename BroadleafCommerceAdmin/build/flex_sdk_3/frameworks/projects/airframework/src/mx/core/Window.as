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

package mx.core
{

import flash.desktop.NativeApplication;
import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.NativeWindow;
import flash.display.NativeWindowDisplayState;
import flash.display.NativeWindowInitOptions;
import flash.display.NativeWindowResize;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.NativeWindowBoundsEvent;
import flash.events.NativeWindowDisplayStateEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.system.Capabilities;

import mx.controls.Button;
import mx.controls.FlexNativeMenu;
import mx.core.windowClasses.StatusBar;
import mx.core.windowClasses.TitleBar;
import mx.events.AIREvent;
import mx.events.FlexEvent;
import mx.events.FlexNativeWindowBoundsEvent;
import mx.managers.CursorManagerImpl;
import mx.managers.FocusManager;
import mx.managers.ICursorManager;
import mx.managers.ISystemManager;
import mx.managers.WindowedSystemManager;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when this application gets activated.
 *
 *  @eventType mx.events.AIREvent.APPLICATION_ACTIVATE
 */
[Event(name="applicationActivate", type="mx.events.AIREvent")]

/**
 *  Dispatched when this application gets deactivated.
 *
 *  @eventType mx.events.AIREvent.APPLICATION_DEACTIVATE
 */
[Event(name="applicationDeactivate", type="mx.events.AIREvent")]

/**
 *  Dispatched after the window has been activated.
 *
 *  @eventType mx.events.AIREvent.WINDOW_ACTIVATE
 */
[Event(name="windowActivate", type="mx.events.AIREvent")]

/**
 *  Dispatched after the window has been deactivated.
 *
 *  @eventType mx.events.AIREvent.WINDOW_DEACTIVATE
 */
[Event(name="windowDeactivate", type="mx.events.AIREvent")]

/**
 *  Dispatched after the window has been closed.
 *
 *  @eventType flash.events.Event.CLOSE
 *
 *  @see flash.display.NativeWindow
 */
[Event(name="close", type="flash.events.Event")]

/**
 *  Dispatched before the window closes.
 *  This event is cancelable.
 *
 *  @eventType flash.events.Event.CLOSING
 *
 *  @see flash.display.NativeWindow
 */
[Event(name="closing", type="flash.events.Event")]

/**
 *  Dispatched after the display state changes
 *  to minimize, maximize or restore.
 *
 *  @eventType flash.events.NativeWindowDisplayStateEvent.DISPLAY_STATE_CHANGE
 */
[Event(name="displayStateChange", type="flash.events.NativeWindowDisplayStateEvent")]

/**
 *  Dispatched before the display state changes
 *  to minimize, maximize or restore.
 *
 *  @eventType flash.events.NativeWindowDisplayStateEvent.DISPLAY_STATE_CHANGING
 */
[Event(name="displayStateChanging", type="flash.events.NativeWindowDisplayStateEvent")]

/**
 *  Dispatched before the window moves,
 *  and while the window is being dragged.
 *
 *  @eventType flash.events.NativeWindowBoundsEvent.MOVING
 */
[Event(name="moving", type="flash.events.NativeWindowBoundsEvent")]

/**
 *  Dispatched when the computer connects to or disconnects from the network.
 *
 *	@eventType flash.events.Event.NETWORK_CHANGE
 */
[Event(name="networkChange", type="flash.events.Event")]

/**
 *  Dispatched before the underlying NativeWindow is resized, or
 *  while the Window object boundaries are being dragged.
 *
 *  @eventType flash.events.NativeWindowBoundsEvent.RESIZING
 */
[Event(name="resizing", type="flash.events.NativeWindowBoundsEvent")]

/**
 *  Dispatched when the Window completes its initial layout
 *  and opens the underlying NativeWindow.
 *
 *  @eventType mx.events.AIREvent.WINDOW_COMPLETE
 */
[Event(name="windowComplete", type="mx.events.AIREvent")]

/**
 *  Dispatched after the window moves.
 *
 *  @eventType mx.events.FlexNativeWindowBoundsEvent.WINDOW_MOVE
 */
[Event(name="windowMove", type="mx.events.FlexNativeWindowBoundsEvent")]

/**
 *  Dispatched after the underlying NativeWindow is resized.
 *
 *  @eventType mx.events.FlexNativeWindowBoundsEvent.WINDOW_RESIZE
 */
[Event(name="windowResize", type="mx.events.FlexNativeWindowBoundsEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Position of buttons in title bar. Possible values: <code>"left"</code>,
 *  <code>"right"</code>, <code>"auto"</code>.
 *
 *  <p>A value of <code>"left"</code> means the buttons are aligned
 *  at the left of the title bar.
 *  A value of <code>"right"</code> means the buttons are aligned
 *  at the right of the title bar.
 *  A value of <code>"auto"</code> means the buttons are aligned
 *  at the left of the title bar on Mac OS X and on the
 *  right on Windows.</p>
 *
 *  @default "auto"
 */
[Style(name="buttonAlignment", type="String", enumeration="left,right,auto", inherit="yes")]

/**
 *  Defines the distance between the titleBar buttons.
 *
 *  @default 2
 */
[Style(name="buttonPadding", type="Number", inherit="yes")]

/**
 *  Skin for close button when using Flex chrome.
 *
 *  @default mx.skins.halo.WindowCloseButtonSkin
 */
[Style(name="closeButtonSkin", type="Class", inherit="no",states="up, over, down, disabled")]

/**
 *  The extra space around the gripper. The total area of the gripper
 *  plus the padding around the edges is the hit area for the gripper resizing.
 *
 *  @default 3
 */
[Style(name="gripperPadding", type="Number", format="Length", inherit="no")]

/**
 *  Style declaration for the skin of the gripper.
 *
 *  @default "gripperStyle"
 */
[Style(name="gripperStyleName", type="String", inherit="no")]

/**
 *  The explicit height of the header. If this style is not set, the header
 *  height is calculated from the largest of the text height, the button
 *  heights, and the icon height.
 *
 *  @default undefined
 */
[Style(name="headerHeight", type="Number", format="Length", inherit="no")]

/**
 *  Skin for maximize button when using Flex chrome.
 *
 *  @default mx.skins.halo.WindowMaximizeButtonSkin
 */
[Style(name="maximizeButtonSkin", type="Class", inherit="no",states="up, over, down, disabled")]

/**
 *  Skin for minimize button when using Flex chrome.
 *
 *  @default mx.skins.halo.WindowMinimizeButtonSkin
 */
[Style(name="minimizeButtonSkin", type="Class", inherit="no",states="up, over, down, disabled")]

/**
 *  Skin for restore button when using Flex chrome.
 *  This style is ignored for Mac OS X.
 *
 *  @default mx.skins.halo.WindowRestoreButtonSkin
 */
[Style(name="restoreButtonSkin", type="Class", inherit="no",states="up, over, down, disabled")]

/**
 *  Determines whether the window draws its own Flex Chrome or depends on the developer
 *  to draw chrome. Changing this style once the window is open has no effect.
 *
 *  @default true
 */
[Style(name="showFlexChrome", type="Boolean", inherit="no")]

/**
 *  The colors used to draw the status bar.
 *
 *  @default 0xC0C0C0
 */
[Style(name="statusBarBackgroundColor", type="uint", format="Color", inherit="yes")]

/**
 *  The status bar background skin.
 *
 *  @default mx.skins.halo.StatusBarBackgroundSkin
 */
[Style(name="statusBarBackgroundSkin", type="Class", inherit="yes")]

/**
 *  Style declaration for the status text.
 *
 *  @default undefined
 */
[Style(name="statusTextStyleName", type="String", inherit="yes")]

/**
 *  Position of the title in title bar.
 *  The possible values are <code>"left"</code>,
 *  <code>"center"</code>, <code>"auto"</code>
 *
 *  <p>A value of <code>"left"</code> means the title is aligned
 *  at the left of the title bar.
 *  A value of <code>"center"</code> means the title is aligned
 *  at the center of the title bar.
 *  A value of <code>"auto"</code> means the title is aligned
 *  at the left on Windows and at the center on Mac OS X.</p>
 *
 *  @default "auto"
 */
[Style(name="titleAlignment", type="String", enumeration="left,center,auto", inherit="yes")]

/**
 *  The title background skin.
 *
 *  @default mx.skins.halo.ApplicationTitleBarBackgroundSkin
 */
[Style(name="titleBarBackgroundSkin", type="Class", inherit="yes")]

/**
 *  The distance between the furthest out title bar button and the
 *  edge of the title bar.
 *
 *  @default 5
 */
[Style(name="titleBarButtonPadding", type="Number", inherit="true")]

/**
 *  An array of two colors used to draw the header.
 *  The first color is the top color.
 *  The second color is the bottom color.
 *  The default values are <code>undefined</code>, which
 *  makes the header background the same as the
 *  panel background.
 *
 *  @default [ 0x000000, 0x000000 ]
 */
[Style(name="titleBarColors", type="Array", arrayType="uint", format="Color", inherit="yes")]

/**
 *  The style name for the title text.
 *
 *  @default undefined
 */
[Style(name="titleTextStyleName", type="String", inherit="yes")]

//--------------------------------------
//  Effects
//--------------------------------------

/**
 *  Played when the window is closed.
 */
[Effect(name="closeEffect", event="windowClose")]

/**
 *  Played when the component is minimized.
 */
[Effect(name="minimizeEffect", event="windowMinimize")]

/**
 *  Played when the component is unminimized.
 */
[Effect(name="unminimizeEffect", event="windowUnminimize")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="moveEffect", kind="effect")]

//--------------------------------------
//  Other metadata
//--------------------------------------

/**
 *  The frameworks must be initialized by SystemManager.
 *  This factoryClass will be automatically subclassed by any
 *  MXML applications that don't explicitly specify a different
 *  factoryClass.
 */
[Frame(factoryClass="mx.managers.WindowedSystemManager")]

[IconFile("Window.png")]

[ResourceBundle("core")]

/**
 *  The Window is a top-level container for additional windows
 *  in an AIR desktop application.
 *
 *  <p>The Window container is a special kind of container in the sense
 *  that it cannot be used within other layout containers. An mx:Window
 *  component must be the top-level component in its MXML document.</p>
 *
 *  <p>The easiest way to use a Window component to define a native window is to
 *  create an MXML document with an <code>&lt;mx:Window&gt;</code> tag
 *  as the top-level tag in the document. You use the Window component
 *  just as you do any other container, including specifying the layout
 *  type, defining child controls, and so forth. Like any other custom
 *  MXML component, when your application is compiled your MXML document
 *  is compiled into a custom class that is a subclass of the Window
 *  component.</p>
 *
 *  <p>In your application code, to make an instance of
 *  your Window subclass appear on the screen you first create an instance
 *  of the class in code (by defining a variable and calling the <code>new
 *  MyWindowClass()</code> constructor. Next you set any properties you wish
 *  to specify for the new window. Finally you call your Window component's
 *  <code>open()</code> method to open the window on the screen.</p>
 *
 *  <p>Note that several of the Window class's properties can only be set
 *  <strong>before</strong> calling the <code>open()</code> method to open
 *  the window. Once the underlying NativeWindow is created, these initialization
 *  properties can be read but cannot be changed. This restriction applies to
 *  the following properties:</p>
 *
 *  <ul>
 *    <li><code>maximizable</code></li>
 *    <li><code>minimizable</code></li>
 *    <li><code>resizable</code></li>
 *    <li><code>systemChrome</code></li>
 *    <li><code>transparent</code></li>
 *    <li><code>type</code></li>
 *  </ul>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Window&gt;</code> tag inherits all of the tag
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:Window
 *    <strong>Properties</strong>
 *    alwaysInFront="false"
 *    height="100"
 *    maxHeight="10000"
 *    maximizable="true"
 *    maxWidth="10000"
 *    menu="<i>null</i>"
 *    minHeight="0"
 *    minimizable="true"
 *    minWidth="0"
 *    resizable="true"
 *    showGripper="true"
 *    showStatusBar="true"
 *    showTitleBar="true"
 *    status=""
 *    statusBarFactory="mx.core.ClassFactory"
 *    systemChrome="standard"
 *    title=""
 *    titleBarFactory="mx.core.ClassFactory"
 *    titleIcon="<i>null</i>"
 *    transparent="false"
 *    type="normal"
 *    visible="true"
 *    width="100"
 * 
 *    <strong>Styles</strong>
 *    buttonAlignment="auto"
 *    buttonPadding="2"
 *    closeButtonSkin="mx.skins.halo.windowCloseButtonSkin"
 *    gripperPadding="3"
 *    gripperStyleName="gripperStyle"
 *    headerHeight="<i>undefined</i>"
 *    maximizeButtonSkin="mx.skins.halo.WindowMaximizeButtonSkin"
 *    minimizeButtonSkin="mx.skins.halo.WindowMinimizeButtonSkin"
 *    restoreButtonSkin="mx.skins.halo.WindowRestoreButtonSkin"
 *    showFlexChrome="true"
 *    statusBarBackgroundColor="0xC0C0C0"
 *    statusBarBackgroundSkin="mx.skins.halo.StatusBarBackgroundSkin"
 *    statusTextStyleName="<i>undefined</i>"
 *    titleAlignment="auto"
 *    titleBarBackgroundSkin="mx.skins.halo.ApplicationTitleBarBackgroundSkin"
 *    titleBarButtonPadding="5"
 *    titleBarColors="[ 0x000000, 0x000000 ]"
 *    titleTextStyleName="<i>undefined</i>"
 * 
 *    <strong>Effects</strong>
 *    closeEffect="<i>No default</i>"
 *    minimizeEffect="<i>No default</i>"
 *    unminimizeEffect="<i>No default</i>"
 * 
 *    <strong>Events</strong>
 *    applicationActivate="<i>No default</i>"
 *    applicationDeactivate="<i>No default</i>"
 *    closing="<i>No default</i>"
 *    displayStateChange="<i>No default</i>"
 *    displayStateChanging="<i>No default</i>"
 *    moving="<i>No default</i>"
 *    networkChange="<i>No default</i>"
 *    resizing="<i>No default</i>"
 *    windowComplete="<i>No default</i>"
 *    windowMove="<i>No default</i>"
 *    windowResize="<i>No default</i>"
 *  /&gt;
 *  </pre>
 * 
 *  @see mx.core.WindowedApplication
 * 
 *  @playerversion AIR 1.1
 */
public class Window extends LayoutContainer implements IWindow
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static const HEADER_PADDING:Number = 4;

    /**
     *  @private
     */
    private static const MOUSE_SLACK:Number = 5;
    
    /**
     *  The default height for a window (SDK-14399) 
     *  @private
     */
    private static const DEFAULT_WINDOW_HEIGHT:Number = 100;
    
    /**
     *  The default width for a window (SDK-14399) 
     *  @private
     */
    private static const DEFAULT_WINDOW_WIDTH:Number = 100;

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns the Window to which a component is parented.
     *
     *  @param component the component whose Window you wish to find.
     */
    public static function getWindow(component:UIComponent):Window
    {
    	if (component.systemManager is WindowedSystemManager)
    		return WindowedSystemManager(component.systemManager).window;
    	
		return null;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Window()
    {
        super();

        addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
        addEventListener(FlexEvent.PREINITIALIZE, preinitializeHandler);
 		
		invalidateProperties();
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var _nativeWindow:NativeWindow;

	/**
	 *  @private
	 */
	private var _nativeWindowVisible:Boolean = true;
	
 	/**
     *  @private
     */
 	private var maximized:Boolean = false;

 	/**
     *  @private
     */
 	private var _cursorManager:ICursorManager;
 	
 	
 	/**
     *  @private
     */
 	private var toMax:Boolean = false;

    /**
     *  @private
     */
    private var appViewMetrics:EdgeMetrics;

    /**
     *  @private
     *  Ensures that the Window has finished drawing
     *  before it becomes visible.
     */
    private var frameCounter:int = 0;

    /**
     *  @private
     */
    private var gripper:Button;

    /**
     *  @private
     */
    private var gripperHit:Sprite;

    /**
     *  @private
     */
    private var flagForOpen:Boolean = false;

    /**
     *   @private
     */
    private var openActive:Boolean = true;

    /**
     *  @private
     *  A reference to this Application's title bar skin.
     *  This is a child of the titleBar.
     */
    mx_internal var titleBarBackground:IFlexDisplayObject;

    /**
     *  @private
     *  A reference to this Application's status bar skin.
     *  This is a child of the statusBar.
     */
    mx_internal var statusBarBackground:IFlexDisplayObject;

    /**
     *  @private
     */
    private var oldX:Number;

    /**
     *  @private
     */
    private var oldY:Number;

    /**
     *  @private
     */
    private var prevX:Number;

    /**
     *  @private
     */
    private var prevY:Number;

    /**
     *  @private
     */
    private var resizeHandlerAdded:Boolean = false;

    /**
     *  @private
     *  This flag indicates whether the width of the Application instance
     *  can change or has been explicitly set by the developer.
     *  When the stage is resized we use this flag to know whether the
     *  width of the Application should be modified.
     */
    private var resizeWidth:Boolean = true;

    /**
     *  @private
     *  This flag indicates whether the height of the Application instance
     *  can change or has been explicitly set by the developer.
     *  When the stage is resized we use this flag to know whether the
     *  height of the Application should be modified.
     */
    private var resizeHeight:Boolean = true;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties: UIComponent
    //
    //--------------------------------------------------------------------------

   	//----------------------------------
    //  height
    //----------------------------------

    [Bindable("heightChanged")]
    [Inspectable(category="General")]
    [PercentProxy("percentHeight")]

    /**
     *  @private
     */
	override public function get height():Number
    {
    	return _bounds.height;
    }

    /**
     *  @private
	 *  Also sets the stage's height.
     */
    override public function set height(value:Number):void
    {
		if (value < minHeight)
     		value = minHeight;
     	else if (value > maxHeight)
     		value = maxHeight;
     	
		_bounds.height = value;
     	boundsChanged = true;

        invalidateProperties();
        invalidateSize();
        invalidateViewMetricsAndPadding();

	    dispatchEvent(new Event("heightChanged"));
        // also dispatched in the resizeHandler
    }

	//----------------------------------
    //  maxHeight
    //----------------------------------

	/**
     *  @private
	 *  Storage for the maxHeight property.
     */
	private var _maxHeight:Number = 10000;
	
	/**
     *  @private
	 *  Keeps track of whether maxHeight property changed so we can
	 *  handle it in commitProperties.
     */
    private var maxHeightChanged:Boolean = false;

    [Bindable("maxHeightChanged")]
    [Bindable("windowComplete")]

	/**
     *  @private
     */
    override public function get maxHeight():Number
    {
    	if (nativeWindow && !maxHeightChanged)
    		return nativeWindow.maxSize.y - chromeHeight();
        else
        	return _maxHeight;
    }

	/**
     *  Specifies the maximum height of the application's window.
     */
    override public function set maxHeight(value:Number):void
    {
        _maxHeight = value;
        maxHeightChanged = true;
        invalidateProperties();
    }

    //----------------------------------
    //  maxWidth
    //----------------------------------

    /**
     *  @private
	 *  Storage for the maxWidth property.
     */
    private var _maxWidth:Number = 10000;
    
    /**
     *  @private
	 *  Keeps track of whether maxWidth property changed so we can
	 *  handle it in commitProperties.
     */
    private var maxWidthChanged:Boolean = false;

    [Bindable("maxWidthChanged")]
    [Bindable("windowComplete")]

	/**
     *  @private
     */
    override public function get maxWidth():Number
    {
    	if (nativeWindow && !maxWidthChanged)
    		return nativeWindow.maxSize.x - chromeWidth();
        else
        	return _maxWidth;
    }

    /**
     *  Specifies the maximum width of the application's window.
     */
    override public function set maxWidth(value:Number):void
    {
        _maxWidth = value;
        maxWidthChanged = true;
        invalidateProperties();
    }

     //---------------------------------
     //  minHeight
     //---------------------------------

    /**
     *  @private
     */
    private var _minHeight:Number = 0;
	
	/**
     *  @private
	 *  Keeps track of whether minHeight property changed so we can
	 *  handle it in commitProperties.
     */
    private var minHeightChanged:Boolean = false;

    [Bindable("minHeightChanged")]
    [Bindable("windowComplete")]

    /**
     *  Specifies the minimum height of the application's window.
     */
    override public function get minHeight():Number
    {
    	if (nativeWindow && !minHeightChanged)
    		return nativeWindow.minSize.y - chromeHeight();
        else
        	return _minHeight;
    }

    /**
     *  @private
     */
    override public function set minHeight(value:Number):void
    {
        _minHeight = value;
        minHeightChanged = true;
        invalidateProperties();
    }

     //---------------------------------
     //  minWidth
     //---------------------------------

    /**
     *  @private
	 *  Storage for the minWidth property.
     */
    private var _minWidth:Number = 0;
    
   /**
     *  @private
	 *  Keeps track of whether minWidth property changed so we can
	 *  handle it in commitProperties.
     */
    private var minWidthChanged:Boolean = false;

    [Bindable("minWidthChanged")]
    [Bindable("windowComplete")]

    /**
     *  Specifies the minimum width of the application's window.
     */
    override public function get minWidth():Number
    {
        if (nativeWindow && !minWidthChanged)
    		return nativeWindow.minSize.x - chromeWidth();
        else
        	return _minWidth;
    }

    /**
     *  @private
     */
    override public function set minWidth(value:Number):void
    {
        _minWidth = value;
        minWidthChanged = true;
        invalidateProperties();
    }
	
	//----------------------------------
	//  visible
	//----------------------------------
	
	[Bindable("hide")]
    [Bindable("show")]
    [Bindable("windowComplete")]
	
	/**
	 *  Controls the window's visibility. Unlike the
	 *  <code>UIComponent.visible</code> property of most Flex
	 *  visual components, this property affects the visibility
	 *  of the underlying NativeWindow (including operating system
	 *  chrome) as well as the visibility of the Window's child
	 *  controls.
	 *
	 *  <p>When this property changes, Flex dispatches a <code>show</code>
	 *  or <code>hide</code> event.</p>
	 *
	 *  @default true
	 */	
	override public function get visible():Boolean
	{
		if (!nativeWindow.closed)
			return _nativeWindow.visible;
		else
			return false;
	}
	/**
	 *  @private
	 */
	override public function set visible(value:Boolean):void
	{
		if (!_nativeWindow)
		{
			_nativeWindowVisible = value;
			invalidateProperties();
		}
		else if (!_nativeWindow.closed)
		{
			var e:FlexEvent;
			if (value)
			{
				_nativeWindow.visible = value;
				e = new FlexEvent(FlexEvent.SHOW);
				dispatchEvent(e);
			}
			else
			{
				e = new FlexEvent(FlexEvent.HIDE);
				if (getStyle("hideEffect"))
    			{
             		addEventListener("effectEnd", hideEffectEndHandler);
				}
				else
				{
					_nativeWindow.visible = value;
					dispatchEvent(e);
				}
			}
		}
	}
	
   	//----------------------------------
    //  width
    //----------------------------------
	
    [Bindable("widthChanged")]
    [Inspectable(category="General")]
    [PercentProxy("percentWidth")]
	
    /**
     *  @private
     */
    override public function get width():Number
    {
    	return _bounds.width;
    }

    /**
     *  @private
	 *  Also sets the stage's width.
     */
    override public function set width(value:Number):void
    {
     	if (value < minWidth)
     		value = minWidth;
     	else if (value > maxWidth)
     		value = maxWidth;
     	
     	_bounds.width = value;
     	boundsChanged = true;

        invalidateProperties();
        invalidateSize();
        invalidateViewMetricsAndPadding();

	    dispatchEvent(new Event("widthChanged"));
	    // also dispatched in the resize handler
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden properties: Container
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  viewMetrics
    //----------------------------------

    /**
     *  @private
     */
    override public function get viewMetrics():EdgeMetrics
    {

  		var bm:EdgeMetrics = super.viewMetrics;
        var vm:EdgeMetrics = new EdgeMetrics(bm.left, bm.top,
        									 bm.right, bm.bottom);
		
   		// Since the header covers the solid portion of the border,
        // we need to use the larger of borderThickness or headerHeight

		if (showTitleBar)
        {
	        var hHeight:Number = getHeaderHeight();
	        if (!isNaN(hHeight))
	            vm.top += hHeight;
        }

		if (showStatusBar)
        {
            var sHeight:Number = getStatusBarHeight();
            if (!isNaN(sHeight))
                vm.bottom += sHeight;
        }

		if (controlBar && controlBar.includeInLayout)
        {
            vm.top += controlBar.getExplicitOrMeasuredHeight();
        }

        return vm;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    // alwaysInFront
    //----------------------------------

    /**
     *  @private
	 *  Storage for the alwaysInFront property.
     */
    private var _alwaysInFront:Boolean = false;

    /**
	 *  Determines whether the underlying NativeWindow is always in front
	 *  of other windows (including those of other applications). Setting
	 *  this property sets the <code>alwaysInFront</code> property of the
	 *  underlying NativeWindow. See the <code>NativeWindow.alwaysInFront</code>
	 *  property description for details of how this affects window stacking
	 *  order.
	 *
	 *  @see flash.display.NativeWindow#alwaysInFront
     */
    public function get alwaysInFront():Boolean
    {
    	if (_nativeWindow && !_nativeWindow.closed)
    		return nativeWindow.alwaysInFront;
    	else
    		return _alwaysInFront;
    }
	
	/**
     *  @private
	 */
	public function set alwaysInFront(value:Boolean):void
	{
		_alwaysInFront = value;
		if (_nativeWindow && !_nativeWindow.closed)
			nativeWindow.alwaysInFront = value;
	}

    //----------------------------------
    //  bounds
    //----------------------------------

    /**
     *  @private
     *  Storage for the bounds property.
     */
    private var _bounds:Rectangle = new Rectangle(0, 0, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);

    /**
     *  @private
     */
    private var boundsChanged:Boolean = false;

    /**
     *  @private
     *  A Rectangle specifying the window's bounds
	 *  relative to the screen.
     */
    protected function get bounds():Rectangle
    {
        return _bounds;
    }

    /**
     *  @private
     */
    protected function set bounds(value:Rectangle):void
    {
        _bounds = value;
        boundsChanged = true;

        invalidateProperties();
        invalidateSize();
        invalidateViewMetricsAndPadding();
    }

   	//----------------------------------
    //  closed
    //----------------------------------

    /**
     *  A flag indicating whether the window has been closed.
     */
    public function get closed():Boolean
    {
    	return nativeWindow.closed;
    }

   	//----------------------------------
    //  controlBar
    //----------------------------------

    /**
     *  The ApplicationControlBar for this Window.
     *
     *  @see mx.containers.ApplicationControlBar
     *  @default null
     */
    public var controlBar:IUIComponent;

	//----------------------------------
	//  maximizable
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the maximizable property.
	 */
	private var _maximizable:Boolean = true;
	
	/**
	 *  Specifies whether the window can be maximized.
	 *  This property's value is read-only after the window
	 *  has been opened.
	 */
	public function get maximizable():Boolean
	{
		return _maximizable;
	}
	
	/**
	 *  @private
	 */
	public function set maximizable(value:Boolean):void
	{
		if (!_nativeWindow)
		{
			_maximizable = value;

			invalidateProperties();
		}
	}
	
	//----------------------------------
	//  menu
	//----------------------------------
	
	/**
     *  @private
	 *  Storage for the menu property.
     */
    private var _menu:FlexNativeMenu;

   	//----------------------------------
    //  cursorManager
    //----------------------------------
    /**
     *  Returns the cursor manager for this Window.
     */
    override public function get cursorManager():ICursorManager
    {
    	return _cursorManager;
    }


    /**
     *  @private
     */
    private var menuChanged:Boolean = false;

    /**
     *  @private
     */
    public function get menu():FlexNativeMenu
    {
    	return _menu;
    }

    /**
     *  The window menu for this window.
	 *  Some operating systems do not support window menus,
	 *  in which case this property is ignored.
     */
    public function set menu(value:FlexNativeMenu):void
    {
    	_menu = value;
    	menuChanged = true;
    }
	
	//----------------------------------
	//  minimizable
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the minimizable property.
	 */
	private var _minimizable:Boolean = true;
	
	/**
	 *  Specifies whether the window can be minimized.
	 *  This property is read-only after the window has
	 *  been opened.
	 */
	public function get minimizable():Boolean
	{
		return _minimizable;
	}

	/**
	 *  @private
	 */
	public function set minimizable(value:Boolean):void
	{
		if (!_nativeWindow)
		{
			_minimizable = value;

			invalidateProperties();
		}
	}
	
	//----------------------------------
	//  nativeWindow
	//----------------------------------

    /**
     *  The underlying NativeWindow that this Window component uses.
     */
    public function get nativeWindow():NativeWindow
    {
    	if (systemManager && systemManager.stage)
    		return systemManager.stage.nativeWindow;
    	
		return null;
    }

	//----------------------------------
	//  resizable
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the resizable property.
	 */
	private var _resizable:Boolean = true;
	
	/**
	 *  Specifies whether the window can be resized.
	 *  This property is read-only after the window
	 *  has been opened.
	 */
	public function get resizable():Boolean
	{
		return _resizable;
	}

	/**
	 *  @private
	 */
	public function set resizable(value:Boolean):void
	{

		if (!_nativeWindow)
		{
			_resizable = value;

			invalidateProperties();
		}
	}
	
	//----------------------------------
    //  showGripper
    //----------------------------------

    /**
     *  @private
     *  Storage for the showGripper property.
     */
    private var _showGripper:Boolean = true;

    /**
     *  @private
     */
    private var showGripperChanged:Boolean = true;

    /**
     *  If <code>true</code>, the gripper is visible.
	 *
     *  <p>On Mac OS X a window with <code>systemChrome</code>
	 *  set to <code>"standard"</code>
     *  always has an operating system gripper, so this property is ignored
     *  in that case.</p>
     *
     *  @default true
     */
    public function get showGripper():Boolean
    {
        return _showGripper;
    }

    /**
     *  @private
     */
    public function set showGripper(value:Boolean):void
    {
        if (_showGripper == value)
            return;

        _showGripper = value;
        showGripperChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    //----------------------------------
    //  showStatusBar
    //----------------------------------

    /**
     *  Storage for the showStatusBar property.
     */
    private var _showStatusBar:Boolean = true;

    /**
     *  @private
     */
    private var showStatusBarChanged:Boolean = true;

    /**
     *  If <code>true</code>, the status bar is visible.
     *
     *  @default true
     */
    public function get showStatusBar():Boolean
    {
        return _showStatusBar;
    }

    /**
     *  @private
     */
    public function set showStatusBar(value:Boolean):void
    {
        if (_showStatusBar == value)
            return;

        _showStatusBar = value;
        showStatusBarChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    //----------------------------------
    //  showTitleBar
    //----------------------------------

    /**
     *  Storage for the showTitleBar property.
     */
    private var _showTitleBar:Boolean = true;

    /**
     *  @private
     */
    private var showTitleBarChanged:Boolean = true;

    /**
     *  If <code>true</code>, the window's title bar is visible.
     *
     *  @default true
     */
    public function get showTitleBar():Boolean
    {
        return _showTitleBar;
    }

    /**
     *  @private
     */
    public function set showTitleBar(value:Boolean):void
    {
        if (_showTitleBar == value)
            return;

        _showTitleBar = value;
        showTitleBarChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    //----------------------------------
    //  status
    //----------------------------------

    /**
     *  @private
	 *  Storage for the status property.
     */
    private var _status:String = "";

    /**
     *  @private
     */
    private var statusChanged:Boolean = false;

    [Bindable("statusChanged")]

    /**
     *  The string that appears in the status bar, if it is visible.
     *
     *  @default ""
     */
    public function get status():String
    {
        return _status;
    }

    /**
     *  @private
     */
    public function set status(value:String):void
    {

       	_status = value;
       	statusChanged = true;

        invalidateProperties();
        invalidateSize();
        invalidateViewMetricsAndPadding();

		dispatchEvent(new Event("statusChanged"));
    }

    //----------------------------------
    //  statusBar
    //----------------------------------

    /**
	 *  @private
     *  Storage for the statusBar property.
     */
    private var _statusBar:UIComponent;

    /**
     *  The UIComponent that displays the status bar.
     */
    public function get statusBar():UIComponent
    {
    	return _statusBar;
    }

    //----------------------------------
    //  statusBarFactory
    //----------------------------------
	
	/**
	 *  @private
	 *  Storage for the statusBarFactory property
	 */
	private var _statusBarFactory:IFactory = new ClassFactory(StatusBar);
	
    /**
     *  @private
     */
	private var statusBarFactoryChanged:Boolean = false;
	
	[Bindable("statusBarFactoryChanged")]
	
	/**
     *  The IFactory that creates an instance to use
     *  as the status bar.
     *  The default value is an IFactory for StatusBar.
     *
     *  <p>If you write a custom status bar class, it should expose
     *  a public property named <code>status</code>.</p>
     */
    public function get statusBarFactory():IFactory
    {
        return _statusBarFactory;
    }

    /**
     *  @private
     */
    public function set statusBarFactory(value:IFactory):void
    {
        _statusBarFactory = value;
		statusBarFactoryChanged = true;

		invalidateProperties();

        dispatchEvent(new Event("statusBarFactoryChanged"));
    }

    //----------------------------------
    //  statusBarStyleFilters
    //----------------------------------

    private static var _statusBarStyleFilters:Object =
    {
        "statusBarBackgroundColor" : "statusBarBackgroundColor",
        "statusBarBackgroundSkin" : "statusBarBackgroundSkin",
        "statusTextStyleName" : "statusTextStyleName"
    };

    /**
     *  Set of styles to pass from the window to the status bar.
	 *
     *  @see mx.styles.StyleProxy
     */
    protected function get statusBarStyleFilters():Object
    {
        return _statusBarStyleFilters;
    }

	//----------------------------------
	//  systemChrome
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the systemChrome property.
	 */
	private var _systemChrome:String = "standard";
	
	/**
	 *  Specifies the type of system chrome (if any) the window has.
	 *  The set of possible values is defined by the constants
	 *  in the NativeWindowSystemChrome class.
	 *
	 *  <p>This property is read-only once the window has been opened.</p>
	 *
	 *  <p>The default value is <code>NativeWindowSystemChrome.STANDARD</code>.</p>
	 *
	 *  @see flash.display.NativeWindowSystemChrome
	 *  @see flash.display.NativeWindowInitOptions#systemChrome
	 */
	public function get systemChrome():String
	{
		return _systemChrome;
	}

	/**
	 *  @private
	 */
	public function set systemChrome(value:String):void
	{
		if (!_nativeWindow)
		{
			_systemChrome = value;

			invalidateProperties();
		}
	}
	
    //----------------------------------
    //  title
    //----------------------------------

    /**
     *  @private
     *  Storage for the title property.
     */
    private var _title:String = "";

    /**
     *  @private
     */
    private var titleChanged:Boolean = false;

    [Bindable("titleChanged")]

    /**
     *  The title text that appears in the window title bar and
     *  the taskbar.
     *
     *  @default ""
     */
    public function get title():String
    {
        return _title;
    }

    /**
     *  @private
     */
    public function set title(value:String):void
    {
       	titleChanged = true;
       	_title = value;

        invalidateProperties();
        invalidateSize();
        invalidateViewMetricsAndPadding();
        invalidateDisplayList();

        dispatchEvent(new Event("titleChanged"));
    }

    //----------------------------------
    //  titleBar
    //----------------------------------

    /**
	 *  @private
     *  Storage for the titleBar property.
     */
    private var _titleBar:UIComponent;

    /**
     *  The UIComponent that displays the title bar.
     */
    public function get titleBar():UIComponent
    {
    	return _titleBar;
    }

    //----------------------------------
    //  titleBarFactory
    //----------------------------------
	
	/**
	 *  @private
	 *  Storage for the titleBarFactory property
	 */
	private var _titleBarFactory:IFactory = new ClassFactory(TitleBar);
	
	/**
     *  @private
     */
	private var titleBarFactoryChanged:Boolean = false;
	
	[Bindable("titleBarFactoryChanged")]
	
	/**
     *  The IFactory that creates an instance to use
     *  as the title bar.
     *  The default value is an IFactory for TitleBar.
     *
     *  <p>If you write a custom title bar class, it should expose
     *  public properties named <code>titleIcon</code>
     *  and <code>title</code>.</p>
     */
    public function get titleBarFactory():IFactory
    {
        return _titleBarFactory;
    }

    /**
     *  @private
     */
    public function set titleBarFactory(value:IFactory):void
    {
        _titleBarFactory = value;
		titleBarFactoryChanged = true;

		invalidateProperties();

        dispatchEvent(new Event("titleBarFactoryChanged"));
    }

    //----------------------------------
    //  titleBarStyleFilters
    //----------------------------------

    private static var _titleBarStyleFilters:Object =
    {
		"buttonAlignment" : "buttonAlignment",
		"buttonPadding" : "buttonPadding",
		"closeButtonSkin" : "closeButtonSkin",
		"cornerRadius" : "cornerRadius",
		"headerHeight" : "headerHeight",
		"maximizeButtonSkin" : "maximizeButtonSkin",
		"minimizeButtonSkin" : "minimizeButtonSkin",
		"restoreButtonSkin" : "restoreButtonSkin",
		"titleAlignment" : "titleAlignment",
		"titleBarBackgroundSkin" : "titleBarBackgroundSkin",
		"titleBarButtonPadding" : "titleBarButtonPadding",
		"titleBarColors" : "titleBarColors",
		"titleTextStyleName" : "titleTextStyleName"
	};

    /**
     *  Set of styles to pass from the Window to the titleBar.
	 *
     *  @see mx.styles.StyleProxy
     */
    protected function get titleBarStyleFilters():Object
    {
        return _titleBarStyleFilters;
    }

	//----------------------------------
	//  titleIcon
	//----------------------------------
	
    /**
     *  @private
     *  Storage for the titleIcon property.
     */
    private var _titleIcon:Class;

    /**
     *  @private
     */
    private var titleIconChanged:Boolean = false;

    [Bindable("titleIconChanged")]

    /**
     *  The Class (usually an image) used to draw the title bar icon.
     *
     *  @default null
     */
    public function get titleIcon():Class
    {
        return _titleIcon;
    }

    /**
     *  @private
     */
    public function set titleIcon(value:Class):void
    {
       	_titleIcon = value;
       	titleIconChanged = true;

        invalidateProperties();
        invalidateSize();
        invalidateViewMetricsAndPadding();
        invalidateDisplayList();

        dispatchEvent(new Event("titleIconChanged"));
    }

	//----------------------------------
	//  transparent
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the transparent property.
	 */
	private var _transparent:Boolean = false;
	
	/**
	 *  Specifies whether the window is transparent. Setting this
	 *  property to <code>true</code> for a window that uses
	 *  system chrome is not supported.
	 *
	 *  <p>This property is read-only after the window has been opened.</p>
	 */
	public function get transparent():Boolean
	{
		return _transparent;
	}

	/**
	 *  @private
	 */
	public function set transparent(value:Boolean):void
	{
		if (!_nativeWindow)
		{
			_transparent = value;

			invalidateProperties();
		}
	}
	
	//----------------------------------
	//  type
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the type property.
	 */
	private var _type:String = "normal";
	
	/**
	 *  Specifies the type of NativeWindow that this component
	 *  represents. The set of possible values is defined by the constants
	 *  in the NativeWindowType class.
	 *
	 *  <p>This property is read-only once the window has been opened.</p>
	 *
	 *  <p>The default value is <code>NativeWindowType.NORMAL</code>.</p>
	 *
	 *  @see flash.display.NativeWindowType
	 *  @see flash.display.NativeWindowInitOptions#type
	 */
	public function get type():String
	{
		return _type;
	}
	
	/**
	 *  @private
	 */
	public function set type(value:String):void
	{
		if (!_nativeWindow)
		{
			_type = value;

			invalidateProperties();
		}
	}
	
    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function initialize():void
    {
        var sm:ISystemManager = systemManager;
        if (documentDescriptor)
        {
            creationPolicy = documentDescriptor.properties.creationPolicy;
            if (creationPolicy == null || creationPolicy.length == 0)
                creationPolicy = ContainerCreationPolicy.AUTO;

            var properties:Object = documentDescriptor.properties;

            if (properties.width != null)
            {
                width = properties.width;
                delete properties.width;
            }
            if (properties.height != null)
            {
                height = properties.height;
                delete properties.height;
            }

            // Flex auto-generated code has already set up events.
            documentDescriptor.events = null;
        }
         super.initialize();
    }

    /**
     *  @private
     */
    override protected function createChildren():void
    {
    	// this is to help initialize the stage
        width = _bounds.width;
        height = _bounds.height;

		super.createChildren();

		if (getStyle("showFlexChrome") == false ||
			getStyle("showFlexChrome") == "false")
        {
        	setStyle("borderStyle", "none");
        	setStyle("backgroundAlpha", 0);
        	return;
        }

      	if (systemManager.stage.nativeWindow.type != "utility")
        {
	        if (!_statusBar)
	        {
          		_statusBar = statusBarFactory.newInstance();
            	_statusBar.styleName = new StyleProxy(this, statusBarStyleFilters);
            	rawChildren.addChild(DisplayObject(_statusBar));
      	        showStatusBarChanged = true;
	        }
	    }

        if (!gripper)
        {
        	gripper = new Button();
        	var gripSkin:String = getStyle("gripperStyleName");
        	if (gripSkin)
        	{
        		var tmp:CSSStyleDeclaration =
                    StyleManager.getStyleDeclaration("." + gripSkin);
                gripper.styleName = gripSkin;
            }
            rawChildren.addChild(gripper);
            gripperHit = new Sprite();
  			rawChildren.addChild(gripperHit);
            gripperHit.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler);
        }

    	if (systemManager.stage.nativeWindow.systemChrome != "none")
        {
			setStyle("borderStyle", "none");
            return;
        }
		
        if (!_titleBar)
        {
            _titleBar = titleBarFactory.newInstance();
        	_titleBar.styleName = new StyleProxy(this, titleBarStyleFilters);
        	rawChildren.addChild(DisplayObject(titleBar));
        	showTitleBarChanged = true;
		}
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        // Create and open window.
        if (flagForOpen && !_nativeWindow)
        {
        	var init:NativeWindowInitOptions = new NativeWindowInitOptions();
        	init.maximizable = _maximizable;
        	init.minimizable = _minimizable;
        	init.resizable = _resizable;
        	init.type = _type;
        	init.systemChrome = _systemChrome;
        	init.transparent = _transparent;
        	
        	_nativeWindow = new NativeWindow(init);
			var sm:WindowedSystemManager = new WindowedSystemManager(this);
			_nativeWindow.stage.addChild(sm);
	    	systemManager = sm;
	    	
	    	sm.window = this;
	    	
	    	_nativeWindow.alwaysInFront = _alwaysInFront;
	    	initManagers(sm);
	     	addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler);
	
	        var nativeApplication:NativeApplication = NativeApplication.nativeApplication;
	 		nativeApplication.addEventListener(Event.ACTIVATE, nativeApplication_activateHandler, false, 0, true);
	 		nativeApplication.addEventListener(Event.DEACTIVATE, nativeApplication_deactivateHandler, false, 0, true);
	 		nativeApplication.addEventListener(Event.NETWORK_CHANGE,
 				nativeApplication_networkChangeHandler, false, 0, true);
 			_nativeWindow.addEventListener("activate", nativeWindow_activateHandler, false, 0, true);
            _nativeWindow.addEventListener("deactivate", nativeWindow_deactivateHandler, false, 0, true);
            
 			addEventListener(Event.ENTER_FRAME, enterFrameHandler);
 			//debug
 			var x:Object = StyleManager.stylesRoot;
 			//'register' with WindowedSystemManager so it can cleanup when done.
 			sm.addWindow(this);
        }
        
        // minimum width and height
		if (minWidthChanged || minHeightChanged)
		{
			var newMinWidth:Number = minWidthChanged ? _minWidth + chromeWidth() : nativeWindow.minSize.x;
			var newMinHeight:Number = minHeightChanged ? _minHeight + chromeHeight() : nativeWindow.minSize.y;
			
			nativeWindow.minSize = new Point(newMinWidth, newMinHeight);
			
			if (minWidthChanged)
			{
				minWidthChanged = false;
				if (width < minWidth)
            		width = minWidth;
        		dispatchEvent(new Event("minWidthChanged"));
   			}
        	if (minHeightChanged)
        	{
        		minHeightChanged = false;
        		if (height < minHeight)
            		height = minHeight;
        		dispatchEvent(new Event("minHeightChanged"));
        	}
		}
		
		// maximum width and height
		if (maxWidthChanged || maxHeightChanged)
		{
			var newMaxWidth:Number = maxWidthChanged ? _maxWidth + chromeWidth() : nativeWindow.maxSize.x;
			var newMaxHeight:Number = maxHeightChanged ? _maxHeight + chromeHeight() : nativeWindow.maxSize.y;
			
			nativeWindow.maxSize = new Point(newMaxWidth, newMaxHeight);
			
			if (maxWidthChanged)
			{
				maxWidthChanged = false;
				if (width > maxWidth)
            		width = maxWidth;
        		dispatchEvent(new Event("maxWidthChanged"));
   			}
			if (maxHeightChanged)
			{
				maxHeightChanged = false;
				if (height > maxHeight)
            		height = maxHeight;
        		dispatchEvent(new Event("maxHeightChanged"));
   			}
		}

        if (boundsChanged)
        {
            // We use temporary variables because when we set stageWidth or 
            // stageHeight _bounds will be overwritten when we receive 
            // a RESIZE event.
            var newWidth:Number  = _bounds.width;
            var newHeight:Number = _bounds.height;
            systemManager.stage.stageWidth = newWidth;
            systemManager.stage.stageHeight = newHeight;
            boundsChanged = false;
        }

        if (menuChanged && !nativeWindow.closed)
        {
			menuChanged = false;
			
			if (menu == null)
			{
		    	if (NativeWindow.supportsMenu)
		    		nativeWindow.menu = null;
			}
			else if (menu.nativeMenu)
			{
		    	if (NativeWindow.supportsMenu)
		    		nativeWindow.menu = menu.nativeMenu;
		    }
        }

        if (titleBarFactoryChanged)
        {
        	if (_titleBar)
            {
           		// Remove old titleBar.
                rawChildren.removeChild(DisplayObject(titleBar));
                _titleBar = null;
            }
            _titleBar = titleBarFactory.newInstance();
            _titleBar.styleName = new StyleProxy(this, titleBarStyleFilters);
            rawChildren.addChild(DisplayObject(titleBar));
            titleBarFactoryChanged = false;
            invalidateDisplayList();
        }

        if (showTitleBarChanged)
        {
        	if (_titleBar)
        		_titleBar.visible = _showTitleBar;
        	showTitleBarChanged = false;
        }

        if (titleIconChanged)
        {
        	if (_titleBar && "titleIcon" in _titleBar)
        		_titleBar["titleIcon"] = _titleIcon;
        	titleIconChanged = false;
        }

        if (titleChanged)
        {
            if (!nativeWindow.closed)
            	systemManager.stage.nativeWindow.title = _title;
        	if (_titleBar && "title" in _titleBar)
        		_titleBar["title"] = _title;
            titleChanged = false;
        }

        if (statusBarFactoryChanged)
        {
        	if (_statusBar)
            {
                // Remove old status bar.
                rawChildren.removeChild(DisplayObject(_statusBar));
                _statusBar = null
            }
            _statusBar = statusBarFactory.newInstance();
            _statusBar.styleName = new StyleProxy(this, statusBarStyleFilters);
            // Add it underneath the gripper.
            if (gripper)
            	rawChildren.addChildAt(DisplayObject(_statusBar), rawChildren.getChildIndex(gripper));
            else
            	rawChildren.addChild(DisplayObject(_statusBar));
            statusBarFactoryChanged = false;
            showStatusBarChanged = true;
            invalidateDisplayList();
        }

        if (showStatusBarChanged)
        {
            if (_statusBar)
	            _statusBar.visible = _showStatusBar;
            showStatusBarChanged = false;
        }

        if (statusChanged)
        {
        	if (_statusBar && "status" in _statusBar)
        		_statusBar["status"] = _status;
        	statusChanged = false;
        }

        if (showGripperChanged)
        {
        	if (gripper)
        	{
        		gripper.visible = _showGripper;
        		gripperHit.visible = _showGripper;
        	}
        	showGripperChanged = false;
        }

        if (toMax)
        {
        	toMax = false;
        	if (!nativeWindow.closed)
        		nativeWindow.maximize();
        }

        // If nativeWindow has not been created yet, it's time.
     }

    /**
     *  @private
     */
    override protected function measure():void
    {
        if (maximized)
        {
        	maximized = false;
        	if (!nativeWindow.closed)
        		systemManager.stage.nativeWindow.maximize();
        }

        super.measure();
    }

	/**
     *  @private
     */
	override public function validateDisplayList():void
	{
		super.validateDisplayList();
		if (Capabilities.os.substring(0, 3) == "Mac" && systemChrome == "standard")
        {
        	//need to move the scroll bars to not overlap the systemChrome gripper
        	//if both scrollbars are already visible, this has been done for us
        	if ((horizontalScrollBar || verticalScrollBar) && !(horizontalScrollBar && verticalScrollBar) && !showStatusBar)
        	{
            	if (!whiteBox)
	            {
	                whiteBox = new FlexShape();
	                whiteBox.name = "whiteBox";
	
	                var g:Graphics = whiteBox.graphics;
	                g.beginFill(0xFFFFFF);
	                g.drawRect(0, 0, verticalScrollBar ? verticalScrollBar.minWidth : 15, horizontalScrollBar ? horizontalScrollBar.minHeight : 15);
	                g.endFill()
	
	                rawChildren.addChild(whiteBox);
	            }
        		whiteBox.visible = true;
        		
        	
	        	if (horizontalScrollBar)
				{
	                horizontalScrollBar.setActualSize(
						horizontalScrollBar.width - whiteBox.width,
						horizontalScrollBar.height);
				}
	            if (verticalScrollBar)
				{
	                verticalScrollBar.setActualSize(
						verticalScrollBar.width,
						verticalScrollBar.height - whiteBox.height);
				}
	            whiteBox.x = systemManager.stage.stageWidth - whiteBox.width;
	            whiteBox.y = systemManager.stage.stageHeight - whiteBox.height;
         	}
         	else if (!(horizontalScrollBar && verticalScrollBar))
         	{
         		if (whiteBox)
	            {
	                rawChildren.removeChild(whiteBox);
	                whiteBox = null;
	            }
          	}
        }
		else if (gripper && showGripper && !showStatusBar)
		{
			//see if there are both scrollbars
       		if (whiteBox)
       		{
       			whiteBox.visible = false;
       			//if gripper + padding > whiteBox size, we need to move scrollbars
       			//this is, um, generally non-optimal looking
       			if (gripperHit.height > whiteBox.height)
       				verticalScrollBar.setActualSize(verticalScrollBar.width,
       					verticalScrollBar.height - (gripperHit.height - whiteBox.height));
       			if (gripperHit.width > whiteBox.width)
       				horizontalScrollBar.setActualSize(
       						horizontalScrollBar.width - (gripperHit.width  - whiteBox.height),
       						horizontalScrollBar.height);
       		}
       		else if (horizontalScrollBar)
       		{
       			horizontalScrollBar.setActualSize(
					horizontalScrollBar.width - gripperHit.width,
					horizontalScrollBar.height);
       		}
       		else if (verticalScrollBar)
       		{
       			verticalScrollBar.setActualSize(
					verticalScrollBar.width,
					verticalScrollBar.height - gripperHit.height);
       		}
  		}
  		else if (whiteBox)//if there's no gripper, we need to show the white box, if appropriate
  			whiteBox.visible = true;
	}

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        if (!nativeWindow.closed)
        {
	        super.updateDisplayList(unscaledWidth, unscaledHeight);
	        // Application.updateDisplayList can change the width and height.
	        // We use their new values.
	
			resizeWidth = isNaN(explicitWidth);
	        resizeHeight = isNaN(explicitHeight);
	        if (resizeWidth || resizeHeight)
	        {
	            resizeHandler(new Event(Event.RESIZE));
	
	            if (!resizeHandlerAdded)
	            {
	                // weak reference
	                systemManager.addEventListener(Event.RESIZE, resizeHandler, false, 0, true);
	                resizeHandlerAdded = true;
	            }
	        }
	        else
	        {
	            if (resizeHandlerAdded)
	            {
	                systemManager.removeEventListener(Event.RESIZE, resizeHandler);
	                resizeHandlerAdded = false;
	            }
	        }
	
	        // Wait to layout the border after all the children
	        // have been positioned.
	        createBorder();
	
	        var bm:EdgeMetrics = borderMetrics;
	
	        var leftOffset:Number = 10;
	        var rightOffset:Number = 10;
	
	        if (_statusBar)
	        {
	        	_statusBar.move(bm.left, unscaledHeight - bm.bottom - getStatusBarHeight());
	        	_statusBar.setActualSize(unscaledWidth - bm.left - bm.right, getStatusBarHeight());
	
	        }
	
	        if (systemManager.stage.nativeWindow.systemChrome != "none")
	            return;
	
	        var buttonAlign:String =
				String(getStyle("buttonAlignment"));
	        if (titleBar)
	        {
		        titleBar.move(bm.left, bm.top);
		        titleBar.setActualSize(unscaledWidth - bm.left - bm.right,
		                               getHeaderHeight());
	        }
	        if (titleBar && controlBar)
	        	controlBar.move(0, titleBar.height);
	        if (gripper && showGripper)
	       	{
	       		var gripperPadding:Number = getStyle("gripperPadding");
	       		gripper.setActualSize(gripper.measuredWidth,
	       							  gripper.measuredHeight);
	       		gripperHit.graphics.beginFill(0xffffff, .0001);
	       		gripperHit.graphics.drawRect(0, 0, gripper.width + (2 * gripperPadding), gripper.height + (2 * gripperPadding));
	       		gripper.move(unscaledWidth - gripper.measuredWidth - gripperPadding,
	       					unscaledHeight - gripper.measuredHeight - gripperPadding);
	       		gripperHit.x = gripper.x - gripperPadding;
	       		gripperHit.y = gripper.y - gripperPadding;
	       	}
	    }
    }

	/**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        super.styleChanged(styleProp);
        if (!nativeWindow.closed)
        {
	        if (!(getStyle("showFlexChrome") == "false" || getStyle("showFlexChrome") == false))
			{
		     	if (styleProp == null || styleProp == "headerHeight"
		     			|| styleProp == "gripperPadding")
		 		{
		 			invalidateViewMetricsAndPadding();
		 			invalidateDisplayList();
		 			invalidateSize();
		 		}
		 	}
        }
    }

    /**
     *  @private
     */
    override public function move(x:Number, y:Number):void
	{
		if (nativeWindow && !nativeWindow.closed)
		{
			var tmp:Rectangle = nativeWindow.bounds;
			tmp.x = x;
			tmp.y = y;
			nativeWindow.bounds = tmp;
		}
	}

	/**
     *  @private
     *  Window also handles themeColor defined
     *  on the global selector. (Stolen from Application)
     */
    override mx_internal function initThemeColor():Boolean
    {
        var result:Boolean = super.initThemeColor();

        if (!result)
        {
            var tc:Object;  // Can be number or string
            var rc:Number;
            var sc:Number;
            var globalSelector:CSSStyleDeclaration =
                StyleManager.getStyleDeclaration("global");

            if (globalSelector)
            {
                tc = globalSelector.getStyle("themeColor");
                rc = globalSelector.getStyle("rollOverColor");
                sc = globalSelector.getStyle("selectionColor");
            }

            if (tc && isNaN(rc) && isNaN(sc))
            {
                setThemeColor(tc);
            }
            result = true;
        }

        return result;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Closes the window. This action is cancelable.
     */
    public function close():void
    {
    	if (_nativeWindow && !nativeWindow.closed)
    	{
	    	var e:Event = new Event("closing", false, true);
	    	stage.nativeWindow.dispatchEvent(e);
	    	if (!(e.isDefaultPrevented()))
	    	{
	    		stage.nativeWindow.close();
	    		_nativeWindow = null;
	    		systemManager.removeChild(this);
	    	}
    	}
    }

    /**
     *  @private
     *  Returns the height of the header.
     */
    private function getHeaderHeight():Number
    {
    	if (!nativeWindow.closed)
    	{
	        if (getStyle("headerHeight") != null)
	        	return getStyle("headerHeight");
	 		if (!systemManager.stage)
	 			return 0;
	        if (systemManager.stage.nativeWindow.systemChrome != "none")
	            return 0;
	        if (titleBar)
		        return(titleBar.getExplicitOrMeasuredHeight());
	    }
	    return 0;
	
    }

    /**
     *  @private
     *  Returns the height of the statusBar.
     */
    public function getStatusBarHeight():Number
    {
        if (_statusBar)
	        return _statusBar.getExplicitOrMeasuredHeight();
	    return 0;
    }

    /**
     *  @private
     */
    private function initManagers(sm:ISystemManager):void
    {
        if (sm.isTopLevel())
        {
            focusManager = new FocusManager(this);
            sm.activate(this);
			_cursorManager = new CursorManagerImpl(sm);
        }
    }

 	/**
     *  Maximizes the window, or does nothing if it's already maximized.
     */
    public function maximize():void
    {
        if (!nativeWindow || !nativeWindow.maximizable || nativeWindow.closed)
    		return;
        if (stage.nativeWindow.displayState!= NativeWindowDisplayState.MAXIMIZED)
        {
         	var f:NativeWindowDisplayStateEvent = new NativeWindowDisplayStateEvent(
                        NativeWindowDisplayStateEvent.DISPLAY_STATE_CHANGING,
                        false, true, stage.nativeWindow.displayState,
                        NativeWindowDisplayState.MAXIMIZED);
            stage.nativeWindow.dispatchEvent(f);
        	if (!f.isDefaultPrevented())
        	{
        		toMax = true;
        		invalidateProperties();
        		invalidateSize();
        	}
        }
    }

    /**
     *  Minimizes the window.
     */
    public function minimize():void
    {
    	if (!nativeWindow.closed)
    	{
	        var e:NativeWindowDisplayStateEvent = new NativeWindowDisplayStateEvent(
	        		NativeWindowDisplayStateEvent.DISPLAY_STATE_CHANGING,
	        		false, true, nativeWindow.displayState,
	        		NativeWindowDisplayState.MINIMIZED)
	        stage.nativeWindow.dispatchEvent(e);
	        if (!e.isDefaultPrevented())
	        	stage.nativeWindow.minimize();
     	}
    }

    /**
     *  Restores the window (unmaximizes it if it's maximized, or
     *  unminimizes it if it's minimized).
     */
	public function restore():void
    {
    	if (!nativeWindow.closed)
    	{
	    	var e:NativeWindowDisplayStateEvent;
	    	if (stage.nativeWindow.displayState == NativeWindowDisplayState.MAXIMIZED)
	        {
	          	e = new NativeWindowDisplayStateEvent(
	                        NativeWindowDisplayStateEvent.DISPLAY_STATE_CHANGING,
	                        false, true, NativeWindowDisplayState.MAXIMIZED,
	                        NativeWindowDisplayState.NORMAL);
	            stage.nativeWindow.dispatchEvent(e);
	            if (!e.isDefaultPrevented())
	            	nativeWindow.restore();
	        }
	        else if (stage.nativeWindow.displayState == NativeWindowDisplayState.MINIMIZED)
	        {
	        	e = new NativeWindowDisplayStateEvent(
	                NativeWindowDisplayStateEvent.DISPLAY_STATE_CHANGING,
	                false, true, NativeWindowDisplayState.MINIMIZED,
	                NativeWindowDisplayState.NORMAL);
	            stage.nativeWindow.dispatchEvent(e);
	            if (!e.isDefaultPrevented())
	            	nativeWindow.restore();
	        }
	    }
    }

    /**
     *  Activates the underlying NativeWindow (even if this Window's application
	 *  is not currently active).
     */
    public function activate():void
    {
    	if (!nativeWindow.closed)
    		_nativeWindow.activate();	
    }

    /**
     *  Creates the underlying NativeWindow and opens it.
     *
     *  @param  openWindowActive specifies whether the Window opens
     *  activated (that is, whether it has focus). The default value
	 *  is <code>true</code>.
     */
    public function open(openWindowActive:Boolean = true):void
    {
    	flagForOpen = true;
    	openActive = openWindowActive;
    	commitProperties();
    }
	
    /**
     *  Orders the window just behind another. To order the window behind
     *  a NativeWindow that does not implement IWindow, use this window's
     *  nativeWindow's <code>orderInBackOf()</code> method.
     *
     *  @param window The IWindow (Window or WindowedAplication)
     *  to order this window behind.
     *
     *  @return <code>true</code> if the window was succesfully sent behind;
     *          <code>false</code> if the window is invisible or minimized.
     */
    public function orderInBackOf(window:IWindow):Boolean
    {
     	if (nativeWindow && !nativeWindow.closed)
     		return nativeWindow.orderInBackOf(window.nativeWindow);
     	else
     		return false;
    }

    /**
     *  Orders the window just in front of another. To order the window
     *  in front of a NativeWindow that does not implement IWindow, use this
     *  window's nativeWindow's  <code>orderInFrontOf()</code> method.
     *
     *  @param window The IWindow (Window or WindowedAplication)
     *  to order this window in front of.
     *
     *  @return <code>true</code> if the window was succesfully sent in front;
     *          <code>false</code> if the window is invisible or minimized.
     */
    public function orderInFrontOf(window:IWindow):Boolean
    {
     	if (nativeWindow && !nativeWindow.closed)
     		return nativeWindow.orderInFrontOf(window.nativeWindow);
     	else
     		return false;
    }

	/**
     *  Orders the window behind all others in the same application.
     *
     *  @return <code>true</code> if the window was succesfully sent to the back;
     *  <code>false</code> if the window is invisible or minimized.
     */
	public function orderToBack():Boolean
	{
     	if (nativeWindow && !nativeWindow.closed)
     		return nativeWindow.orderToBack();
     	else
     		return false;
	}

	/**
     *  Orders the window in front of all others in the same application.
     *
     *  @return <code>true</code> if the window was succesfully sent to the front;
     *  <code>false</code> if the window is invisible or minimized.
     */
	public function orderToFront():Boolean
	{
		if (nativeWindow && !nativeWindow.closed)
     		return nativeWindow.orderToFront();
     	else
     		return false;
	}
	
	/**
     *  @private
     *  Returns the width of the chrome for the window
     */
    private function chromeWidth():Number
    {
        return nativeWindow.width - systemManager.stage.stageWidth;
    }
    
    /**
     *  @private
     *  Returns the height of the chrome for the window
     */
    private function chromeHeight():Number
    {
        return nativeWindow.height - systemManager.stage.stageHeight;
    }
	
    /**
     *  @private
     *  Starts a system move.
     */
    private function startMove(event:MouseEvent):void
    {
        addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
        addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);

        prevX = event.stageX;
        prevY = event.stageY;
    }

    /**
     *  @private
     *  Starts a system resize.
     */
    private function startResize(start:String):void
    {
        if (resizable && !nativeWindow.closed)
            stage.nativeWindow.startResize(start);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function enterFrameHandler(e:Event):void
    {
    	if (frameCounter == 2)
    	{
	    	removeEventListener(Event.ENTER_FRAME, enterFrameHandler);
	    	_nativeWindow.visible = _nativeWindowVisible;
	    	dispatchEvent(new AIREvent(AIREvent.WINDOW_COMPLETE));
	    	if (_nativeWindow.visible)
	    	{
	    		if (openActive)
	    			_nativeWindow.activate();
	    	}
    	}
    	frameCounter++;
    }

	/**
	 *  @private
	 */
	private function hideEffectEndHandler(event:Event):void
	{
		_nativeWindow.visible = false;
		
		dispatchEvent(new FlexEvent(FlexEvent.HIDE));
	}

    /**
     *  @private
     */
    private function windowMinimizeHandler(event:Event):void
    {
    	if (!nativeWindow.closed)
        	stage.nativeWindow.minimize();
        removeEventListener("effectEnd", windowMinimizeHandler);
    }

    /**
     *  @private
     */
    private function windowUnminimizeHandler(event:Event):void
    {
        removeEventListener("effectEnd", windowUnminimizeHandler);
    }

    /**
     *  @private
     */
    private function window_moveHandler(event:NativeWindowBoundsEvent):void
    {
		var newEvent:FlexNativeWindowBoundsEvent =
			new FlexNativeWindowBoundsEvent(
				FlexNativeWindowBoundsEvent.WINDOW_MOVE,
				event.bubbles, event.cancelable,
				event.beforeBounds, event.afterBounds);
		dispatchEvent(newEvent);
    }

    /**
     *  @private
     */
    private function window_displayStateChangeHandler(
                    		event:NativeWindowDisplayStateEvent):void
    {
        // Redispatch event .
        dispatchEvent(event);

        height = stage.stageHeight;
        width = stage.stageWidth;
    }

    /**
     *  @private
     */
    private function window_displayStateChangingHandler(
                    		event:NativeWindowDisplayStateEvent):void
    {
        // Redispatch event for cancellation purposes.
        dispatchEvent(event);

        if (event.isDefaultPrevented())
        	return;
        if (event.afterDisplayState == NativeWindowDisplayState.MINIMIZED)
        {
            if (getStyle("minimizeEffect"))
            {
                event.preventDefault();
                addEventListener("effectEnd", windowMinimizeHandler);
                dispatchEvent(new Event("windowMinimize"));
            }
        }

        // After here, afterState is normal
        else if (event.beforeDisplayState == NativeWindowDisplayState.MINIMIZED)
        {
            addEventListener("effectEnd", windowUnminimizeHandler);
            dispatchEvent(new Event("windowUnminimize"));
        }
    }

    /**
     *  @private
     */
    private function windowMaximizeHandler(event:Event):void
    {
        removeEventListener("effectEnd", windowMaximizeHandler);
        if (!nativeWindow.closed)
        	stage.nativeWindow.maximize();
    }

	/**
     *  @private
     */
    private function windowUnmaximizeHandler(event:Event):void
    {
        removeEventListener("effectEnd", windowUnmaximizeHandler);
        if (!nativeWindow.closed)
        	stage.nativeWindow.restore();
    }

    /**
     *  Manages mouse down events on the window border.
     */
    protected function mouseDownHandler(event:MouseEvent):void
    {
        if (systemManager.stage.nativeWindow.systemChrome != "none")
            return;
        if (event.target == gripperHit)
        {
       		startResize(NativeWindowResize.BOTTOM_RIGHT);
        	event.stopPropagation();
        }
        else
        {
	        var dragWidth:int = Number(getStyle("borderThickness")) + 6;
	        var cornerSize:int = 12;
	        // we short the top a little
	
	        if (event.stageY < Number(getStyle("borderThickness")))
	        {
	            if (event.stageX < cornerSize)
	                startResize(NativeWindowResize.TOP_LEFT);
	            else if (event.stageX > width - cornerSize)
	                startResize(NativeWindowResize.TOP_RIGHT);
	            else
	                startResize(NativeWindowResize.TOP);
	        }
	
	        else if (event.stageY > (height - dragWidth))
	        {
	            if (event.stageX < cornerSize)
	                 startResize(NativeWindowResize.BOTTOM_LEFT);
	            else if (event.stageX > width - cornerSize)
	                startResize(NativeWindowResize.BOTTOM_RIGHT);
	            else
	                startResize(NativeWindowResize.BOTTOM);
	        }
	
	        else if (event.stageX < dragWidth )
	        {
	            if (event.stageY < cornerSize)
	                startResize(NativeWindowResize.TOP_LEFT);
	            else if (event.stageY > height - cornerSize)
	                startResize(NativeWindowResize.BOTTOM_LEFT);
	            else
	                startResize(NativeWindowResize.LEFT);
	            event.stopPropagation();
	        }
	
	        else if (event.stageX > width - dragWidth)
	        {
	            if (event.stageY < cornerSize)
	                startResize(NativeWindowResize.TOP_RIGHT);
	            else if (event.stageY > height - cornerSize)
	                startResize(NativeWindowResize.BOTTOM_RIGHT);
	            else
	                startResize(NativeWindowResize.RIGHT);
	        }
	    }
    }

    /**
     *  @private
     */
    private function closeButton_clickHandler(event:Event):void
    {
        if (!nativeWindow.closed)
       		stage.nativeWindow.close();
    }

    /**
     *  @private
     *  Triggered by a resize event of the stage.
     *  Sets the new width and height.
     *  After the SystemManager performs its function,
     *  it is only necessary to notify the children of the change.
     */
    private function resizeHandler(event:Event):void
    {
        // When user has not specified any width/height,
        // application assumes the size of the stage.
        // If developer has specified width/height,
        // the application will not resize.
        // If developer has specified percent width/height,
        // application will resize to the required value
        // based on the current stage width/height.
        // If developer has specified min/max values,
        // then application will not resize beyond those values.

        var w:Number;
        var h:Number

        if (resizeWidth)
        {
            if (isNaN(percentWidth))
            {
                w = DisplayObject(systemManager).width;
            }
            else
            {
                super.percentWidth = Math.max(percentWidth, 0);
                super.percentWidth = Math.min(percentWidth, 100);
                w = percentWidth*screen.width/100;
            }

            if (!isNaN(explicitMaxWidth))
                w = Math.min(w, explicitMaxWidth);

            if (!isNaN(explicitMinWidth))
                w = Math.max(w, explicitMinWidth);
        }
        else
        {
            w = width;
        }

        if (resizeHeight)
        {
            if (isNaN(percentHeight))
            {
                h = DisplayObject(systemManager).height;
            }
            else
            {
                super.percentHeight = Math.max(percentHeight, 0);
                super.percentHeight = Math.min(percentHeight, 100);
                h = percentHeight*screen.height/100;
            }

            if (!isNaN(explicitMaxHeight))
                h = Math.min(h, explicitMaxHeight);

            if (!isNaN(explicitMinHeight))
                h = Math.max(h, explicitMinHeight);
        }
        else
        {
            h = height;
        }

        if (w != width || h != height)
        {
            invalidateProperties();
            invalidateSize();
        }

        setActualSize(w, h);

        invalidateDisplayList();
    }

    /**
     *  @private
     */
    private function creationCompleteHandler(event:Event = null):void
    {
        systemManager.stage.nativeWindow.addEventListener(
            "closing", window_closingHandler);

        systemManager.stage.nativeWindow.addEventListener(
            "close", window_closeHandler, false, 0, true);
                        
        systemManager.stage.nativeWindow.addEventListener(
            NativeWindowBoundsEvent.MOVING, window_boundsHandler);

        systemManager.stage.nativeWindow.addEventListener(
            NativeWindowBoundsEvent.MOVE, window_moveHandler);

        systemManager.stage.nativeWindow.addEventListener(
            NativeWindowBoundsEvent.RESIZING, window_boundsHandler);

        systemManager.stage.nativeWindow.addEventListener(
           NativeWindowBoundsEvent.RESIZE, window_resizeHandler);

    }

    /**
     *  @private
     */
    private function preinitializeHandler(event:FlexEvent):void
    {
    	systemManager.stage.nativeWindow.addEventListener(
            NativeWindowDisplayStateEvent.DISPLAY_STATE_CHANGING,
            window_displayStateChangingHandler);
        systemManager.stage.nativeWindow.addEventListener(
            NativeWindowDisplayStateEvent.DISPLAY_STATE_CHANGE,
            window_displayStateChangeHandler);
    }

    /**
     *  @private
     */
    private function mouseMoveHandler(event:MouseEvent):void
    {
        stage.nativeWindow.x += event.stageX - prevX;
        stage.nativeWindow.y += event.stageY - prevY;
    }

    /**
     *  @private
     */
    private function mouseUpHandler(event:MouseEvent):void
    {
        removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
        removeEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
    }

    /**
     *  @private
     */
    private function window_boundsHandler(event:NativeWindowBoundsEvent):void
    {

        var newBounds:Rectangle = event.afterBounds;
        var r:Rectangle;
        if (event.type == NativeWindowBoundsEvent.MOVING)
        {
        	dispatchEvent(event);
        	if (event.isDefaultPrevented())
        		return;
        }
        else //event is resizing
        {
        	dispatchEvent(event);
        	if (event.isDefaultPrevented())
        		return;
        	var cancel:Boolean = false;
        	if (newBounds.width < nativeWindow.minSize.x)
        	{	
        		cancel = true;
        		if (newBounds.x != event.beforeBounds.x && !isNaN(oldX))
        			newBounds.x = oldX;
        		newBounds.width = nativeWindow.minSize.x;
        	}
        	else if (newBounds.width > nativeWindow.maxSize.x)
        	{
        		cancel = true;
        		if (newBounds.x != event.beforeBounds.x && !isNaN(oldX))
        			newBounds.x = oldX;
        		newBounds.width = nativeWindow.maxSize.x;
        	}
        	if (newBounds.height < nativeWindow.minSize.y)
        	{
        		cancel = true;
        		if (event.afterBounds.y != event.beforeBounds.y && !isNaN(oldY))
 	       			newBounds.y = oldY;
        		newBounds.height = nativeWindow.minSize.y;
        	}
        	else if (newBounds.height > nativeWindow.maxSize.y)
        	{
        		cancel = true;
        		if (event.afterBounds.y != event.beforeBounds.y && !isNaN(oldY))
 	       			newBounds.y = oldY;
        		newBounds.height = nativeWindow.maxSize.y;
        	}
        	if (cancel)
        	{
        		event.preventDefault();
        		stage.nativeWindow.bounds = newBounds;
        	}
        }
        oldX = newBounds.x;
        oldY = newBounds.y;
    }

    /**
     *  @private
     */
    private function window_closeEffectEndHandler(event:Event):void
    {
        removeEventListener("effectEnd", window_closeEffectEndHandler);
        if (!nativeWindow.closed)
        	stage.nativeWindow.close();
    }

    /**
     *  @private
     */
    private function window_closingHandler(event:Event):void
    {
        var e:Event = new Event("closing", true, true);
        dispatchEvent(e);
        if (e.isDefaultPrevented())
        {
            event.preventDefault();
        }
        else if (getStyle("closeEffect") &&
                 stage.nativeWindow.transparent == true)
        {
            addEventListener("effectEnd", window_closeEffectEndHandler);
            dispatchEvent(new Event("windowClose"));
            event.preventDefault();
        }
    }

    /**
     *  @private
     */
    private function window_closeHandler(event:Event):void
    {
        dispatchEvent(new Event("close"));
    }
    
    /**
     *  @private
     */
    private function window_resizeHandler(event:NativeWindowBoundsEvent):void
    {
        invalidateViewMetricsAndPadding();
        invalidateDisplayList();

        var dispatchWidthChangeEvent:Boolean = (bounds.width != stage.stageWidth);
        var dispatchHeightChangeEvent:Boolean = (bounds.height != stage.stageHeight);

        bounds.x = stage.x;
        bounds.y = stage.y;
        bounds.width = stage.stageWidth;
        bounds.height = stage.stageHeight;

        validateNow();
        var e:FlexNativeWindowBoundsEvent =
        	new FlexNativeWindowBoundsEvent(FlexNativeWindowBoundsEvent.WINDOW_RESIZE, event.bubbles, event.cancelable,
        			event.beforeBounds, event.afterBounds);
        dispatchEvent(e);

        if (dispatchWidthChangeEvent)
    		dispatchEvent(new Event("widthChanged"));
    	if (dispatchHeightChangeEvent)
    		dispatchEvent(new Event("heightChanged"));
    }

 	/**
 	 *  @private
 	 */
 	private function nativeApplication_activateHandler(event:Event):void
 	{
 		dispatchEvent(new AIREvent(AIREvent.APPLICATION_ACTIVATE));
 	}

 	/**
 	 *  @private
 	 */
 	private function nativeApplication_deactivateHandler(event:Event):void
 	{
 		dispatchEvent(new AIREvent(AIREvent.APPLICATION_DEACTIVATE));
 	}

 	/**
 	 *  @private
 	 */
 	private function nativeWindow_activateHandler(event:Event):void
 	{
 		dispatchEvent(new AIREvent(AIREvent.WINDOW_ACTIVATE));
 	}	
 	
 	/**
 	 *  @private
 	 */
 	private function nativeWindow_deactivateHandler(event:Event):void
 	{
 		dispatchEvent(new AIREvent(AIREvent.WINDOW_DEACTIVATE));
 	}
 	
 	/**
 	 *  @private
 	 */
 	private function nativeApplication_networkChangeHandler(event:Event):void
 	{
 		dispatchEvent(event);
 	} 	
}

}
