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

package mx.controls
{

import flash.display.Bitmap;
import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.Loader;
import flash.display.LoaderInfo;
import flash.events.Event;
import flash.events.HTTPStatusEvent;
import flash.events.IOErrorEvent;
import flash.events.ProgressEvent;
import flash.events.SecurityErrorEvent;
import flash.geom.Rectangle;
import flash.net.URLRequest;
import flash.system.ApplicationDomain;
import flash.system.Capabilities;
import flash.system.LoaderContext;
import flash.system.SecurityDomain;
import flash.utils.ByteArray;

import mx.core.Application;
import mx.core.FlexLoader;
import mx.core.FlexVersion;
import mx.core.IFlexDisplayObject;
import mx.core.IUIComponent;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.managers.CursorManager;
import mx.managers.ISystemManager;
import mx.managers.SystemManagerGlobals;
import mx.styles.ISimpleStyleClient;
import mx.utils.LoaderUtil;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when content loading is complete.
 *
 *  <p>This event is dispatched regardless of whether the load was triggered
 *  by an autoload or an explicit call to the <code>load()</code> method.</p>
 *
 *  @eventType flash.events.Event.COMPLETE
 */
[Event(name="complete", type="flash.events.Event")]

/**
 *  Dispatched when a network request is made over HTTP 
 *  and Flash Player or AIR can detect the HTTP status code.
 * 
 *  @eventType flash.events.HTTPStatusEvent.HTTP_STATUS
 */
[Event(name="httpStatus", type="flash.events.HTTPStatusEvent")]

/**
 *  Dispatched when the properties and methods of a loaded SWF file 
 *  are accessible. The following two conditions must exist
 *  for this event to be dispatched:
 * 
 *  <ul>
 *    <li>All properties and methods associated with the loaded 
 *    object and those associated with the control are accessible.</li>
 *    <li>The constructors for all child objects have completed.</li>
 *  </ul>
 * 
 *  @eventType flash.events.Event.INIT
 */
[Event(name="init", type="flash.events.Event")]

/**
 *  Dispatched when an input/output error occurs.
 *  @see flash.events.IOErrorEvent
 *
 *  @eventType flash.events.IOErrorEvent.IO_ERROR
 */
[Event(name="ioError", type="flash.events.IOErrorEvent")]

/**
 *  Dispatched when a network operation starts.
 * 
 *  @eventType flash.events.Event.OPEN
 */
[Event(name="open", type="flash.events.Event")]

/**
 *  Dispatched when content is loading.
 *
 *  <p>This event is dispatched regardless of whether the load was triggered
 *  by an autoload or an explicit call to the <code>load()</code> method.</p>
 *
 *  <p><strong>Note:</strong> 
 *  The <code>progress</code> event is not guaranteed to be dispatched.
 *  The <code>complete</code> event may be received, without any
 *  <code>progress</code> events being dispatched.
 *  This can happen when the loaded content is a local file.</p>
 *
 *  @eventType flash.events.ProgressEvent.PROGRESS
 */
[Event(name="progress", type="flash.events.ProgressEvent")]

/**
 *  Dispatched when a security error occurs while content is loading.
 *  For more information, see the SecurityErrorEvent class.
 *
 *  @eventType flash.events.SecurityErrorEvent.SECURITY_ERROR
 */
[Event(name="securityError", type="flash.events.SecurityErrorEvent")]

/**
 *  Dispatched when a loaded object is removed, 
 *  or when a second load is performed by the same SWFLoader control 
 *  and the original content is removed prior to the new load beginning.
 * 
 *  @eventType flash.events.Event.UNLOAD
 */
[Event(name="unload", type="flash.events.Event")]

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  The name of class to use as the SWFLoader border skin if the control cannot
 *  load the content.
 *  @default BrokenImageBorderSkin
 */
[Style(name="brokenImageBorderSkin", type="Class", inherit="no")]

/**
 *  The name of the class to use as the SWFLoader skin if the control cannot load
 *  the content.
 *  The default value is the "__brokenImage" symbol in the Assets.swf file.
 */
[Style(name="brokenImageSkin", type="Class", inherit="no")]

/**
 *  The horizontal alignment of the content when it does not have
 *  a one-to-one aspect ratio.
 *  Possible values are <code>"left"</code>, <code>"center"</code>,
 *  and <code>"right"</code>.
 *  @default "left"
 */
[Style(name="horizontalAlign", type="String", enumeration="left,center,right", inherit="no")]

/**
 *  The vertical alignment of the content when it does not have
 *  a one-to-one aspect ratio.
 *  Possible values are <code>"top"</code>, <code>"middle"</code>,
 *  and <code>"bottom"</code>.
 *  @default "top"
 */
[Style(name="verticalAlign", type="String", enumeration="bottom,middle,top", inherit="no")]

//--------------------------------------
//  Effects
//--------------------------------------

/**
 *  An effect that is started when the complete event is dispatched.
 */
[Effect(name="completeEffect", event="complete")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultBindingProperty(source="percentLoaded", destination="source")]

[DefaultTriggerEvent("progress")]

[IconFile("SWFLoader.png")]

[ResourceBundle("controls")]

/**
 *  The SWFLoader control loads and displays a specified SWF file.
 *  You typically use SWFLoader for loading one Flex application
 *  into a host Flex application.
 *
 *  <p><strong>Note:</strong> You can use the SWFLoader control to load
 *  a GIF, JPEG, or PNG image file at runtime, 
 *  to load a ByteArray representing a SWF, GIF, JPEG, or PNG image at runtime, 
 *  or load an embedded version of any of these file types, 
 *  and SVG files, at compile time
 *  by using <code>&#64;Embed(source='filename')</code>.
 *  However, the Image control is better suited for this capability
 *  and should be used for most image loading.
 *  The Image control is also designed to be used
 *  in custom item renderers and item editors. 
 *  When using either SWFLoader or Image with an SVG file,
 *  you can only load the SVG if it has been embedded in your
 *  application using an Embed statement;
 *  you cannot load an SVG from the network at runtime.</p>
 *
 *  <p>The SWFLoader control lets you scale its content and set its size. 
 *  It can also resize itself to fit the size of its content.
 *  By default, content is scaled to fit the size of the SWFLoader control.
 *  It can also load content on demand programmatically,
 *  and monitor the progress of a load.</p>  
 *
 *  <p>A SWFLoader control cannot receive focus.
 *  However, the contents of a SWFLoader control can accept focus
 *  and have its own focus interactions.</p>
 *
 *  <p>The SWFLoader control has the following default characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Width and height large enough for the loaded content</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>Undefined</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *  
 *  <p>The &lt;mx:SWFLoader&gt; tag inherits all of the tag attributes
 *  of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:SWFLoader
 *    <strong>Properties</strong>
 *    autoLoad="true|false"
 *    loaderContext="null"
 *    maintainAspectRatio="true|false"
 *    scaleContent="true|false"
 *    showBusyCursor="false|true"
 *    source="<i>No default</i>"
 *    trustContent="false|true"
 *  
 *    <strong>Styles</strong>
 *    brokenImageBorderSkin="BrokenImageBorderSkin"
 *    brokenImageSkin="<i>'__brokenImage' symbol in Assets.swf</i>"
 *    horizontalAlign="left|center|right"
 *    verticalAlign="top|middle|bottom"
 *  
 *    <strong>Effects</strong>
 *    completeEffect="<i>No default</i>"
 *    
 *    <strong>Events</strong>
 *    complete="<i>No default</i>"
 *    httpStatus="<i>No default</i>"
 *    init="<i>No default</i>"
 *    ioError="<i>No default</i>"
 *    open="<i>No default</i>"
 *    progress="<i>No default</i>"
 *    securityError="<i>No default</i>"
 *    unload="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *  
 *  @includeExample examples/local.mxml -noswf
 *  @includeExample examples/SimpleLoader.mxml
 *
 *  @see mx.controls.Image
 */
public class SWFLoader extends UIComponent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function SWFLoader()
    {
        super();

        // SWFLoader generally load interactive content.
        tabChildren = true;
        tabEnabled = false;

        addEventListener(FlexEvent.INITIALIZE, initializeHandler);

        showInAutomationHierarchy = false;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal var contentHolder:DisplayObject;

    /**
     *  @private
     */
    private var contentChanged:Boolean = false;

    /**
     *  @private
     */
    private var scaleContentChanged:Boolean = false;

    /**
     *  @private
     */
    private var isContentLoaded:Boolean = false;

    /**
     *  @private
     */
    private var brokenImage:Boolean = false;

    /**
     *  @private
     */
    private var resizableContent:Boolean = false; // true if we've loaded a SWF

    /**
     *  @private
     */
    private var flexContent:Boolean = false; // true if we've loaded a Flex SWF

    /**
     *  @private
     */
    private var contentRequestID:String = null;

    /**
     *  @private
     */
    private var attemptingChildAppDomain:Boolean = false;

    /**
     *  @private
     */
    private var requestedURL:URLRequest;

    /**
     *  @private
     */
    private var brokenImageBorder:IFlexDisplayObject;

    /**
     *  @private
     */
    private var explicitLoaderContext:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  @private
     *  The baselinePosition of a SWFLoader is calculated
     *  the same as for a generic UIComponent.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            return 0;

        return super.baselinePosition;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  autoLoad
    //----------------------------------

    /**
     *  @private
     *  Storage for the autoLoad property.
     */
    private var _autoLoad:Boolean = true;

    [Bindable("autoLoadChanged")]
    [Inspectable(defaultValue="true")]

    /**
     *  A flag that indicates whether content starts loading automatically
     *  or waits for a call to the <code>load()</code> method.
     *  If <code>true</code>, the content loads automatically. 
     *  If <code>false</code>, you must call the <code>load()</code> method.
     *
     *  @default true
     */
    public function get autoLoad():Boolean
    {
        return _autoLoad;
    }

    /**
     *  @private
     */
    public function set autoLoad(value:Boolean):void
    {
        if (_autoLoad != value)
        {
            _autoLoad = value;

            contentChanged = true;

            invalidateProperties();
            invalidateSize();
            invalidateDisplayList();

            dispatchEvent(new Event("autoLoadChanged"));
        }
    }

    //----------------------------------
    //  bytesLoaded (read only)
    //----------------------------------

    /**
     *  @private
     *  Storage for the autoLoad property.
     */
    private var _bytesLoaded:Number = NaN;

    [Bindable("progress")]

    /**
     *  The number of bytes of the SWF or image file already loaded.
     */
    public function get bytesLoaded():Number
    {
        return _bytesLoaded;
    }

    //----------------------------------
    //  bytesTotal (read only)
    //----------------------------------

    /**
     *  @private
     *  Storage for the bytesTotal property.
     */
    private var _bytesTotal:Number = NaN;

    [Bindable("complete")]

    /**
     *  The total size of the SWF or image file.
     */
    public function get bytesTotal():Number
    {
        return _bytesTotal;
    }

    //----------------------------------
    //  content (read only)
    //----------------------------------

    /**
     *  This property contains the object that represents
     *  the content that was loaded in the SWFLoader control. 
     *
     *  @tiptext Returns the content of the SWFLoader
     *  @helpid 3134
     */
    public function get content():DisplayObject
    {
        if (contentHolder is Loader)
            return Loader(contentHolder).content;

        return contentHolder;
    }

    //----------------------------------
    //  contentHeight
    //----------------------------------

    /**
     *  Height of the scaled content loaded by the control, in pixels. 
     *  Note that this is not the height of the control itself, but of the 
     *  loaded content. Use the <code>height</code> property of the control
     *  to obtain its height.
     *
     *  <p>The value of this property is not final when the <code>complete</code> event is triggered. 
     *  You can get the value after the <code>updateComplete</code> event is triggered.</p>
     *
     *  @default NaN
     */
    public function get contentHeight():Number
    {
        return contentHolder ? contentHolder.height : NaN;
    }

    //----------------------------------
    //  contentHolderHeight (private)
    //----------------------------------

    /**
     *  @private
     */
    private function get contentHolderHeight():Number
    {
        // For externally loaded content, use the loaderInfo structure
        var loaderInfo:LoaderInfo;
        if (contentHolder is Loader)
            loaderInfo = Loader(contentHolder).contentLoaderInfo;

        if (loaderInfo)
        {
            if (loaderInfo.contentType == "application/x-shockwave-flash")
            {
                try
                {
                    var content:IFlexDisplayObject =
                        Loader(contentHolder).content as IFlexDisplayObject;
                    if (content)
                        return content.measuredHeight;
                }
                catch(error:Error)
                {
                    return contentHolder.height;
                }
            }
            else
            {
                try
                {
                    var testContent:DisplayObject = Loader(contentHolder).content;
                }
                catch(error:Error)
                {
                    return contentHolder.height;
                }
            }

            return loaderInfo.height;
        }

        // For internally loaded content, use preferredHeight (if present) or height
        if (contentHolder is IUIComponent)
            return IUIComponent(contentHolder).getExplicitOrMeasuredHeight();
        if (contentHolder is IFlexDisplayObject)
            return IFlexDisplayObject(contentHolder).measuredHeight;

        return contentHolder.height;
    }

    //----------------------------------
    //  contentHolderWidth (private)
    //----------------------------------

    /**
     *  @private
     */
    private function get contentHolderWidth():Number
    {
        // For externally loaded content, use the loaderInfo structure
        var loaderInfo:LoaderInfo;
        if (contentHolder is Loader)
            loaderInfo = Loader(contentHolder).contentLoaderInfo;

        if (loaderInfo)
        {
            if (loaderInfo.contentType == "application/x-shockwave-flash")
            {
                try
                {
                    var content:IFlexDisplayObject =
                        Loader(contentHolder).content as IFlexDisplayObject;
                    if (content)
                        return content.measuredWidth;
                }
                catch(error:Error)
                {
                    return contentHolder.width;
                }
            }
            else
            {
                try
                {
                    var testContent:DisplayObject = Loader(contentHolder).content;
                }
                catch(error:Error)
                {
                    return contentHolder.width;
                }
            }

            return loaderInfo.width;
        }

        // For internally loaded content, use explicitWidth (if present) or explicitWidth
        if (contentHolder is IUIComponent)
            return IUIComponent(contentHolder).getExplicitOrMeasuredWidth();
        if (contentHolder is IFlexDisplayObject)
            return IFlexDisplayObject(contentHolder).measuredWidth;

        return contentHolder.width;
    }

    //----------------------------------
    //  contentWidth
    //----------------------------------

    /**
     *  Width of the scaled content loaded by the control, in pixels. 
     *  Note that this is not the width of the control itself, but of the 
     *  loaded content. Use the <code>width</code> property of the control
     *  to obtain its width.
     *
     *  <p>The value of this property is not final when the <code>complete</code> event is triggered. 
     *  You can get the value after the <code>updateComplete</code> event is triggered.</p>
     *
     *  @default NaN
     */
    public function get contentWidth():Number
    {
        return contentHolder ? contentHolder.width : NaN;
    }

    //----------------------------------
    //  loaderContext
    //----------------------------------

    /**
     *  @private
     *  Storage for the loaderContext property.
     */
    private var _loaderContext:LoaderContext;

    [Bindable("loaderContextChanged")]
    [Inspectable(defaultValue="true")]

    /**
     *  A LoaderContext object to use to control loading of the content.
     *  This is an advanced property. 
     *  Most of the time you can use the <code>trustContent</code> property.
     *
     *  <p>The default value is <code>null</code>, which causes the control
     *  to use the <code>trustContent</code> property to create
     *  a LoaderContext object, which you can read back
     *  after the load starts.</p>
     *
     *  <p>To use a custom LoaderContext object, you must understand the 
     *  SecurityDomain and ApplicationDomain classes.
     *  Setting this property will not start a load;
     *  you must set this before the load starts.
     *  This does not mean that you have to set <code>autoLoad</code> property
     *  to <code>false</code>, because the load does not actually start
     *  immediately, but waiting for the <code>creationComplete</code> event 
     *  to set it is too late.</p>
     *
     *  @default null
     *  @see flash.system.LoaderContext
     *  @see flash.system.ApplicationDomain
     *  @see flash.system.SecurityDomain
     */
    public function get loaderContext():LoaderContext
    {
        return _loaderContext;
    }

    /**
     *  @private
     */
    public function set loaderContext(value:LoaderContext):void
    {
        _loaderContext = value;
		explicitLoaderContext = true;

        dispatchEvent(new Event("loaderContextChanged"));
    }

    //----------------------------------
    //  maintainAspectRatio
    //----------------------------------

    /**
     *  @private
     *  Storage for the maintainAspectRatio property.
     */
    private var _maintainAspectRatio:Boolean = true;

    [Bindable("maintainAspectRatioChanged")]
    [Inspectable(defaultValue="true")]

    /**
     *  A flag that indicates whether to maintain the aspect ratio
     *  of the loaded content.
     *  If <code>true</code>, specifies to display the image with the same ratio of
     *  height to width as the original image.
     *
     *  @default true
     */
    public function get maintainAspectRatio():Boolean
    {
        return _maintainAspectRatio;
    }

    /**
     *  @private
     */
    public function set maintainAspectRatio(value:Boolean):void
    {
        _maintainAspectRatio = value;

        dispatchEvent(new Event("maintainAspectRatioChanged"));
    }


    //----------------------------------
    //  percentLoaded (read only)
    //----------------------------------

    [Bindable("progress")]

    /**
     *  The percentage of the image or SWF file already loaded.
     *
     *  @default 0
     */
    public function get percentLoaded():Number
    {
        var p:Number = isNaN(_bytesTotal) || _bytesTotal == 0 ?
                       0 :
                       100 * (_bytesLoaded / _bytesTotal);

        if (isNaN(p))
            p = 0;

        return p;
    }

    //----------------------------------
    //  scaleContent
    //----------------------------------

    /**
     *  @private
     *  Storage for the scaleContent property.
     */
    private var _scaleContent:Boolean = true;

    [Bindable("scaleContentChanged")]
    [Inspectable(category="General", defaultValue="true")]

    /**
     *  A flag that indicates whether to scale the content to fit the
     *  size of the control or resize the control to the content's size.
     *  If <code>true</code>, the content scales to fit the SWFLoader control.
     *  If <code>false</code>, the SWFLoader scales to fit the content. 
     *
     *  @default true
     */
    public function get scaleContent():Boolean
    {
        return _scaleContent;
    }

    /**
     *  @private
     */
    public function set scaleContent(value:Boolean):void
    {
        if (_scaleContent != value)
        {
            _scaleContent = value;

            scaleContentChanged = true;
            invalidateDisplayList();
        }

        dispatchEvent(new Event("scaleContentChanged"));
    }

    //----------------------------------
    //  showBusyCursor
    //----------------------------------

    /**
     *  @private
     *  Storage for the scaleContent property.
     */
    private var _showBusyCursor:Boolean = false;

    [Inspectable(category="General", defaultValue="true")]

    /**
     *  A flag that indicates whether to show a busy cursor while
     *  the content loads.
     *  If <code>true</code>, shows a busy cursor while the content loads.
     *  The default busy cursor is the mx.skins.halo.BusyCursor
     *  as defined by the <code>busyCursor</code> property of the CursorManager class.
     *
     *  @default false
     *
     *  @see mx.managers.CursorManager
     */
    public function get showBusyCursor():Boolean
    {
        return _showBusyCursor;
    }

    /**
     *  @private
     */
    public function set showBusyCursor(value:Boolean):void
    {
        if (_showBusyCursor != value)
        {
            _showBusyCursor = value;

            if (_showBusyCursor)
                CursorManager.registerToUseBusyCursor(this);
            else
                CursorManager.unRegisterToUseBusyCursor(this);
        }
    }

    //----------------------------------
    //  source
    //----------------------------------

    /**
     *  @private
     *  Storage for the source property.
     */
    private var _source:Object;

    [Bindable("sourceChanged")]
    [Inspectable(category="General", defaultValue="", format="File")]

    /**
     *  The URL, object, class or string name of a class to
     *  load as the content.
     *  The <code>source</code> property takes the following form:
     *
     *  <p><pre>
     *  <code>source="<i>URLOrPathOrClass</i>"</code></pre></p>
     *
     *  <p><pre>
     *  <code>source="&#64;Embed(source='<i>PathOrClass</i>')"</code></pre></p>
     *
     *  <p>The value of the <code>source</code> property represents 
     *  a relative or absolute URL; a ByteArray representing a 
     *  SWF, GIF, JPEG, or PNG; an object that implements 
     *  IFlexDisplayObject; a class whose type implements IFlexDisplayObject;
     *  or a String that represents a class. </p> 
     *
     *  <p>When you specify a path to a SWF, GIF, JPEG, PNG, or SVG file,
     *  Flex automatically converts the file to the correct data type 
     *  for use with the SWFLoader control.</p> 
     *
     *  <p>If you omit the Embed statement, Flex loads the referenced file at runtime; 
     *  it is not packaged as part of the generated SWF file. 
     *  At runtime, the <code>source</code> property only supports the loading of
     *  GIF, JPEG, PNG images, and SWF files.</p>
     *
     *  <p>Flex Data Services users can use the SWFLoader control to 
     *  load a Flex application by using the following form:</p>
     *
     *  <p><pre>
     *  <code>source="<i>MXMLPath</i>.mxml.swf"</code></pre></p>
     *
     *  <p>Flex Data Services compiles the MXML file, 
     *  and returns the SWF file to the main application. This technique works well 
     *  with SWF files that add graphics or animations to an application, 
     *  but are not intended to have a large amount of user interaction. 
     *  If you import SWF files that require a large amount of user interaction, 
     *  you should build them as custom components. </p>
     *
     *  @default null
     */
    public function get source():Object
    {
        return _source;
    }

    /**
     *  @private
     */
    public function set source(value:Object):void
    {
        if (_source != value)
        {
            _source = value;

            contentChanged = true;

            invalidateProperties();
            invalidateSize();
            invalidateDisplayList()

            dispatchEvent(new Event("sourceChanged"));
        }
    }

    //----------------------------------
    //  trustContent
    //----------------------------------

    /**
     *  @private
     *  Storage for the trustContent property.
     */
    private var _trustContent:Boolean = false;

    [Bindable("trustContentChanged")]
    [Inspectable(defaultValue="false")]

    /**
     *  If <code>true</code>, the content is loaded
     *  into your security domain.
     *  This means that the load fails if the content is in another domain
     *  and that domain does not have a crossdomain.xml file allowing your
     *  domain to access it. 
     *  This property only has an affect on the next load,
     *  it will not start a new load on already loaded content.
     *
     *  <p>The default value is <code>false</code>, which means load
     *  any content without failing, but you cannot access the content.
     *  Most importantly, the loaded content cannot 
     *  access your objects and code, which is the safest scenario.
     *  Do not set this property to <code>true</code> unless you are absolutely sure of the safety
     *  of the loaded content, especially active content like SWF files.</p>
     *
     *  <p>You can also use the <code>loaderContext</code> property
     *  to exactly determine how content gets loaded,
     *  if setting <code>trustContent</code> does not exactly
     *  meet your needs. 
     *  The <code>loaderContext</code> property causes the SWFLoader
     *  to ignore the value of the <code>trustContent</code> property.
     *  But, you should be familiar with the SecurityDomain
     *  and ApplicationDomain classes to use the <code>loaderContext</code> property.</p>
     *
     *  @default false
     *  @see flash.system.SecurityDomain
     *  @see flash.system.ApplicationDomain
     */
    public function get trustContent():Boolean
    {
        return _trustContent;
    }

    /**
     *  @private
     */
    public function set trustContent(value:Boolean):void
    {
        if (_trustContent != value)
        {
            _trustContent = value;

            invalidateProperties();
            invalidateSize();
            invalidateDisplayList();

            dispatchEvent(new Event("trustContentChanged"));
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
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (contentChanged)
        {
            contentChanged = false;

            if (_autoLoad)
                load(_source);
        }
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        if (isContentLoaded)
        {
            var oldScaleX:Number = contentHolder.scaleX;
            var oldScaleY:Number = contentHolder.scaleY;

            contentHolder.scaleX = 1.0;
            contentHolder.scaleY = 1.0;

            measuredWidth = contentHolderWidth;
            measuredHeight = contentHolderHeight;

            contentHolder.scaleX = oldScaleX;
            contentHolder.scaleY = oldScaleY;
        }
        else
        {
            // If we're in the process of loading new content,
            // keep the old measuredWidth/measuredHeight for now.
            // Otherwise, we size down to 0,0 for a frame and then
            // resize back up once the new content has loaded.
            // Bug 151518.
            if (!_source || _source == "")
            {
                measuredWidth = 0;
                measuredHeight = 0;
            }
        }
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        if (contentChanged)
        {
            contentChanged = false;
            
            if (_autoLoad)
                load(_source);
        }

        if (isContentLoaded)
        {
            // We will either scale the content to the size of the SWFLoader,
            // or we will scale the loader to the size of the content.
            if (_scaleContent && !brokenImage)
                doScaleContent();
            else
                doScaleLoader();

            scaleContentChanged = false;
        }

        if (brokenImage && !brokenImageBorder)
        {
            var skinClass:Class = getStyle("brokenImageBorderSkin");
            if (skinClass)
            {
                brokenImageBorder = IFlexDisplayObject(new skinClass());
                if (brokenImageBorder is ISimpleStyleClient)
                    ISimpleStyleClient(brokenImageBorder).styleName = this;
                addChild(DisplayObject(brokenImageBorder));
            }
        }
        else if (!brokenImage && brokenImageBorder)
        {
            removeChild(DisplayObject(brokenImageBorder));
            brokenImageBorder = null;
        }

        if (brokenImageBorder)
            brokenImageBorder.setActualSize(unscaledWidth, unscaledHeight);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Loads an image or SWF file.
     *  The <code>url</code> argument can reference a GIF, JPEG, PNG,
     *  or SWF file; you cannot use this method to load an SVG file.
     *  Instead,  you must load it using an Embed statement
     *  with the <code>source</code> property.
     *
     *  @param url Absolute or relative URL of the GIF, JPEG, PNG,
     *  or SWF file to load.
     */
    public function load(url:Object = null):void
    {
        if (url)
            _source = url;

        if (contentHolder)
        {
            if (isContentLoaded)
            {
                // can get rid of bitmap data if it's an image on unload
                // this helps with garbage collection (SDK-9533)
                var imageData:Bitmap;
                
                if (contentHolder is Loader)
                {
                    try
                    {
                        if (Loader(contentHolder).content is Bitmap)
                        {
                            imageData = Bitmap(Loader(contentHolder).content);
                            if (imageData.bitmapData)
                                imageData.bitmapData = null; 
                        }
                    }
                    catch(error:Error)
                    {
                        // Ignore any errors trying to access the Bitmap
                        // b/c we may cause a security violation trying to do it
                    }
                
                    Loader(contentHolder).unload();

					if (!explicitLoaderContext)
						_loaderContext = null;
                }
                else
                {
                    if (contentHolder is Bitmap)
                    {
                        imageData = Bitmap(contentHolder);
                        if (imageData.bitmapData)
                            imageData.bitmapData = null;
                    }
                }
            }
            else
            {
                if (contentHolder is Loader)
                {
                    try
                    {
                        Loader(contentHolder).close();
                    }
                    catch(error:Error)
                    {
                        // Ignore any errors thrown by close()
                    }
                }
            }

			// when SWFLoader/Image is used with renderer
			// recycling and the content is a DisplayObject instance
			// the instance can be stolen from us while
			// we're on the free list
			try
			{
				if (contentHolder.parent == this)
					removeChild(contentHolder);
			}
            catch(error:Error)
            {
				try
				{
					// just try to remove it anyway
					removeChild(contentHolder);
				}
				catch(error1:Error)
				{
					// Ignore any errors thrown by removeChild()
				}
            }

            contentHolder = null;
        }

        isContentLoaded = false;
        brokenImage = false;

        if (!_source || _source == "")
            return;

        contentHolder = loadContent(_source);
    }

    /**
     *  @private
     *  If changes are made to this method, make sure to look at
     *  RectangularBorder.updateDisplayList()
     *  to see if changes are needed there as well.
     */
    private function loadContent(classOrString:Object):DisplayObject
    {
        var child:DisplayObject;
        var cls:Class;
        var url:String;
        var byteArray:ByteArray;
        var loader:Loader;
        
        if (classOrString is Class)
        {
            // We've got a class. Use it.
            cls = Class(classOrString);
        }
        else if (classOrString is String)
        {
            // We've got a string. First we'll see if it is a class name,
            // otherwise just use the string.
            try
            {
                cls = Class(systemManager.getDefinitionByName(String(classOrString)));
            }
            catch(e:Error)
            { // ignore
            }
            url = String(classOrString);
        }
        else if (classOrString is ByteArray)
        {
            byteArray = ByteArray(classOrString);
        }
        else
        {
            // We have something that is not a class or string (XMLNode, for 
            // example). Call toString() and try to load it.
            url = classOrString.toString();
        }

        // Create a child UIComponent based on a class reference, such as Button.
        if (cls)
        {
            contentHolder = child = new cls();
            addChild(child);
            contentLoaded();

        }
        else if (classOrString is DisplayObject)
        {
            contentHolder = child = DisplayObject(classOrString);
            addChild(child);
            contentLoaded();
        }
        else if (byteArray)
        {
            loader = new FlexLoader();
            child = loader;
            addChild(child);
            
            loader.contentLoaderInfo.addEventListener(
                Event.COMPLETE, contentLoaderInfo_completeEventHandler);
            loader.contentLoaderInfo.addEventListener(
                Event.INIT, contentLoaderInfo_initEventHandler);
            loader.contentLoaderInfo.addEventListener(
                Event.UNLOAD, contentLoaderInfo_unloadEventHandler);
            
            // if loaderContext null, it will use default, which is AppDomain
            // of child of Loader's context
            loader.loadBytes(byteArray, loaderContext);
        }
        else if (url)
        {
            // Create an instance of the Flash Player Loader class to do all the work
            loader = new FlexLoader();
            child = loader;

            // addChild needs to be called before load()
            addChild(loader);

            // Forward the events from the Flash Loader to anyone
            // who has registered as an event listener on this Loader.
            loader.contentLoaderInfo.addEventListener(
                Event.COMPLETE, contentLoaderInfo_completeEventHandler);
            loader.contentLoaderInfo.addEventListener(
                HTTPStatusEvent.HTTP_STATUS, contentLoaderInfo_httpStatusEventHandler);
            loader.contentLoaderInfo.addEventListener(
                Event.INIT, contentLoaderInfo_initEventHandler);
            loader.contentLoaderInfo.addEventListener(
                IOErrorEvent.IO_ERROR, contentLoaderInfo_ioErrorEventHandler);
            loader.contentLoaderInfo.addEventListener(
                Event.OPEN, contentLoaderInfo_openEventHandler);
            loader.contentLoaderInfo.addEventListener(
                ProgressEvent.PROGRESS, contentLoaderInfo_progressEventHandler);
            loader.contentLoaderInfo.addEventListener(
                SecurityErrorEvent.SECURITY_ERROR, contentLoaderInfo_securityErrorEventHandler);
            loader.contentLoaderInfo.addEventListener(
                Event.UNLOAD, contentLoaderInfo_unloadEventHandler);
            
            // are we in a debug player and this was a debug=true request
            if ( (Capabilities.isDebugger == true) && 
                 (url.indexOf(".jpg") == -1) && 
                 (LoaderUtil.normalizeURL(
                 Application.application.systemManager.loaderInfo).indexOf("debug=true") > -1) )
                url = url + ( (url.indexOf("?") > -1) ? "&debug=true" : "?debug=true" );

            // make relative paths relative to the SWF loading it, not the top-level SWF
            if (!(url.indexOf(":") > -1 || url.indexOf("/") == 0 || url.indexOf("\\") == 0))
            {
                var rootURL:String;
                if (SystemManagerGlobals.bootstrapLoaderInfoURL != null && SystemManagerGlobals.bootstrapLoaderInfoURL != "")
                    rootURL = SystemManagerGlobals.bootstrapLoaderInfoURL;
                else if (root)
                    rootURL = LoaderUtil.normalizeURL(root.loaderInfo);
                else if (systemManager)
                    rootURL = LoaderUtil.normalizeURL(DisplayObject(systemManager).loaderInfo);

                if (rootURL)
                {
                    var lastIndex:int = Math.max(rootURL.lastIndexOf("\\"), rootURL.lastIndexOf("/"));
                    if (lastIndex != -1)
                        url = rootURL.substr(0, lastIndex + 1) + url;
                }
            }

            requestedURL = new URLRequest(url);
                        
            var lc:LoaderContext = loaderContext;
            if (!lc)
            {
                lc = new LoaderContext();
                _loaderContext = lc;
                if (trustContent)
                {
                    lc.securityDomain = SecurityDomain.currentDomain;
                }
                else
                {
                    attemptingChildAppDomain = true;
                    // assume the best, which is that it is in the same domain and
                    // we can make it a child app domain.
                    lc.applicationDomain = new ApplicationDomain(ApplicationDomain.currentDomain);
                }
            }

            loader.load(requestedURL, lc);
        }
        else
        {
            var message:String = resourceManager.getString(
                "controls", "notLoadable", [ source ]);
            throw new Error(message);
        }

        invalidateDisplayList();

        return child;
    }

    /**
     *  @private
     *  Called when the content has successfully loaded.
     */
    private function contentLoaded():void
    {
        isContentLoaded = true;

        // For externally loaded content, use the loaderInfo structure
        var loaderInfo:LoaderInfo;
        if (contentHolder is Loader)
            loaderInfo = Loader(contentHolder).contentLoaderInfo;

        resizableContent = false;
        if (loaderInfo)
        {
            if (loaderInfo.contentType == "application/x-shockwave-flash")
                resizableContent = true;

            if (resizableContent)
            {
                try 
                {
                    flexContent = Loader(contentHolder).content is IFlexDisplayObject;
                }
                catch(e:Error)
                {
                    flexContent = false;
                }
            }
        }

        try
        {
            if (tabChildren &&
                contentHolder is Loader &&
                Loader(contentHolder).content is DisplayObjectContainer)
            {
                Loader(contentHolder).tabChildren = true;
                DisplayObjectContainer(Loader(contentHolder).content).tabChildren = true;
            }
        }
        catch(e:Error)
        {
            // eat security errors from x-domain content.
        }

        invalidateSize();
        invalidateDisplayList();
    }

    /**
     *  @private
     *  If scaleContent = true then two situations arise:
     *  1) the SWFLoader has explicitWidth/Height set so we
     *  simply scale or resize the content to those dimensions; or
     *  2) the SWFLoader doesn't have explicitWidth/Height.
     *  In this case we should have had our measure() method called
     *  which would set the measuredWidth/Height to that of the content,
     *  and when we pass through this code we should just end up at scale = 1.0.
     */
    private function doScaleContent():void
    {
        if (!isContentLoaded)
            return;

        // if not a SWF, then we scale it, otherwise we just set the size of the SWF.
        if (!resizableContent || (maintainAspectRatio && !flexContent))
        {
            // Make sure any previous scaling is undone.
            unScaleContent();

            // Scale the content to the size of the SWFLoader, preserving aspect ratio.
            var interiorWidth:Number = unscaledWidth;
            var interiorHeight:Number = unscaledHeight;
            var contentWidth:Number = contentHolderWidth;
            var contentHeight:Number = contentHolderHeight;

            var x:Number = 0;
            var y:Number = 0;
            
            // bug 84294 a swf may still not have size at this point
            var newXScale:Number = contentWidth == 0 ?
                                   1 :
                                   interiorWidth / contentWidth;
            var newYScale:Number = contentHeight == 0 ?
                                   1 :
                                   interiorHeight / contentHeight;
            
            var scale:Number;

            if (_maintainAspectRatio)
            {
                if (newXScale > newYScale)
                {
                    x = Math.floor((interiorWidth - contentWidth * newYScale) *
                                   getHorizontalAlignValue());
                    scale = newYScale;
                }
                else
                {
                    y = Math.floor((interiorHeight - contentHeight * newXScale) *
                                   getVerticalAlignValue());
                    scale = newXScale;
                }

                // Scale by the same amount in both directions.
                contentHolder.scaleX = scale;
                contentHolder.scaleY = scale;
            }
            else
            {
                contentHolder.scaleX = newXScale;
                contentHolder.scaleY = newYScale;
            }

            contentHolder.x = x;
            contentHolder.y = y;

        }
        else
        {
            contentHolder.x = 0;
            contentHolder.y = 0;

            var w:Number = unscaledWidth;
            var h:Number = unscaledHeight;

            if (contentHolder is Loader)
            {
                var holder:Loader = Loader(contentHolder);
                try
                {
                    // don't resize contentHolder until after it is layed out
                    if (holder.content.width > 0)
                    {
                        if (holder.content is IFlexDisplayObject)
                        {
                            IFlexDisplayObject(holder.content).setActualSize(w, h);
                        }
                        else
                        {
                            // Bug 142705 - we can't just set width and height here. If the SWF content
                            // does not fill the stage, the width/height of the content holder is NOT
                            // the same as the loaderInfo width/height. If we just set width/height
                            // here is can scale the content in unpredictable ways.
                            var lInfo:LoaderInfo = holder.contentLoaderInfo;

                            if (lInfo)
                            {
                                contentHolder.scaleX = w / lInfo.width;
                                contentHolder.scaleY = h / lInfo.height;
                            }
                            else
                            {
                                contentHolder.width = w;
                                contentHolder.height = h;
                            }
                        }
                    }
                    else if (!(holder.content is IFlexDisplayObject))
                    {
                        contentHolder.width = w;
                        contentHolder.height = h;
                    }
                }
                catch(error:Error)
                {
                    contentHolder.width = w;
                    contentHolder.height = h;
                }
            }
            else
            {
                contentHolder.width = w;
                contentHolder.height = h;
            }
        }
    }

    /**
     *  @private
     *  If scaleContent = false then two situations arise:
     *  1) the SWFLoader has been given explicitWidth/Height so we don't change
     *  the size of the SWFLoader and simply place the content at 0,0
     *  and don't scale it and clip it if needed; or
     *  2) the SWFLoader does not have explicitWidth/Height in which case
     *  our measure() method should have been called and we should have
     *  been given the right size.
     *  However if some other constraint applies we simply clip as in
     *  situation #1, which is why there is only one code path in here.
     */
    private function doScaleLoader():void
    {
        if (!isContentLoaded)
            return;

        unScaleContent();

        var w:Number = unscaledWidth;
        var h:Number = unscaledHeight;

        if ((contentHolderWidth > w) ||
            (contentHolderHeight > h))
        {
            contentHolder.scrollRect = new Rectangle(0, 0, w, h);
        }
        else
        {
            contentHolder.scrollRect = null;
        }

        contentHolder.x = (w - contentHolderWidth) * getHorizontalAlignValue();
        contentHolder.y = (h - contentHolderHeight) * getVerticalAlignValue();
    }

    /**
     *  @private
     */
    private function unScaleContent():void
    {
        contentHolder.scaleX = 1.0;
        contentHolder.scaleY = 1.0;
        contentHolder.x = 0;
        contentHolder.y = 0;
    }

    /**
     *  @private
     */
    private function getHorizontalAlignValue():Number
    {
        var horizontalAlign:String = getStyle("horizontalAlign");

        if (horizontalAlign == "left")
            return 0;
        else if (horizontalAlign == "right")
            return 1;

        // default = center
        return 0.5;
    }

    /**
     *  @private
     */
    private function getVerticalAlignValue():Number
    {
        var verticalAlign:String = getStyle("verticalAlign");

        if (verticalAlign == "top")
            return 0;
        else if (verticalAlign == "bottom")
            return 1;

        // default = middle
        return 0.5;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function initializeHandler(event:FlexEvent):void
    {
        if (contentChanged)
        {
            contentChanged = false;
            
            if (_autoLoad)
                load(_source);
        }
    }

    /**
     *  @private
     */
    mx_internal function contentLoaderInfo_completeEventHandler(event:Event):void
    {
        // Sometimes we interrupt a load to start another load after
        // the bytes are in but before the complete event is dispatched.
        // In this case we get an IOError when we call close()
        // and the complete event is dispatched anyway.
        // Meanwhile we've started the new load.
        // We ignore the complete if the contentHolder doesn't match
        // because that means it was for the old content
        if (LoaderInfo(event.target).loader != contentHolder)
            return;

        // Redispatch the event from this SWFLoader.
        dispatchEvent(event);

        contentLoaded();
    }

    /**
     *  @private
     */
    private function contentLoaderInfo_httpStatusEventHandler(
                            event:HTTPStatusEvent):void
    {
        // Redispatch the event from this SWFLoader.
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function contentLoaderInfo_initEventHandler(event:Event):void
    {
        // Redispatch the event from this SWFLoader.
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function contentLoaderInfo_ioErrorEventHandler(
                            event:IOErrorEvent):void
    {
        // Error loading content, show the broken image.
        source = getStyle("brokenImageSkin");

        // Force the load of the broken image skin here, since that will
        // clear the brokenImage flag. After the image is loaded we set
        // the brokenImage flag.
        load();
        contentChanged = false;
        brokenImage = true;

        // Redispatch the event from this SWFLoader,
        // but only if there is a listener.
        // If there are no listeners for ioError event,
        // a runtime error is displayed.
        if (hasEventListener(event.type))
            dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function contentLoaderInfo_openEventHandler(event:Event):void
    {
        // Redispatch the event from this SWFLoader.
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function contentLoaderInfo_progressEventHandler(
                            event:ProgressEvent):void
    {
        _bytesTotal = event.bytesTotal;
        _bytesLoaded = event.bytesLoaded;

        // Redispatch the event from this SWFLoader.
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function contentLoaderInfo_securityErrorEventHandler(
                            event:SecurityErrorEvent):void
    {
        if (attemptingChildAppDomain)
        {
            attemptingChildAppDomain = false;
            var lc:LoaderContext = new LoaderContext();
            _loaderContext = lc;
            callLater(load);
            return;
        }

        // Redispatch the event from this SWFLoader.
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function contentLoaderInfo_unloadEventHandler(event:Event):void
    {
        // Redispatch the event from this SWFLoader.
        dispatchEvent(event);
    }

    /**
     *  @private
     * 
     *  Just push this change, wholesale, onto the loaded content, if the
     *  content is another Flex SWF
     */
    override public function regenerateStyleCache(recursive:Boolean):void
    {
        super.regenerateStyleCache(recursive);
        
        try
        {
            var sm:ISystemManager = content as ISystemManager;
            if (sm != null)
                Object(sm).regenerateStyleCache(recursive);
        }
        catch(error:Error)
        {
            // Ignore any errors trying to access the content
            // b/c we may cause a security violation trying to do it
			// Also ignore if the sm doesn't have a regenerateStyleCache method
        }
    }

    /**
     *  @private
     * 
     *  Just push this change, wholesale, onto the loaded content, if the
     *  content is another Flex SWF
     */
    override public function notifyStyleChangeInChildren(styleProp:String, recursive:Boolean):void
    {
        super.notifyStyleChangeInChildren(styleProp, recursive);
        
        try
        {
            var sm:ISystemManager = content as ISystemManager;
            if (sm != null)
                Object(sm).notifyStyleChangeInChildren(styleProp, recursive);
        }
        catch(error:Error)
        {
            // Ignore any errors trying to access the content
            // b/c we may cause a security violation trying to do it
			// Also ignore if the sm doesn't have a notifyStyleChangeInChildren method
        }
    }
    
}

}
