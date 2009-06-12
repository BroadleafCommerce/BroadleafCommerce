////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import flash.events.Event;
import flash.events.HTMLUncaughtScriptExceptionEvent;
import flash.events.MouseEvent;
import flash.html.HTMLLoader;
import flash.html.HTMLHistoryItem;
import flash.html.HTMLHost;
import flash.net.URLRequest;
import flash.system.ApplicationDomain;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.ClassFactory;
import mx.core.EdgeMetrics;
import mx.core.IDataRenderer;
import mx.core.IFactory;
import mx.core.FlexHTMLLoader;
import mx.core.mx_internal;
import mx.core.ScrollControlBase;
import mx.core.ScrollPolicy;
import mx.events.FlexEvent;
import mx.events.ScrollEvent;
import mx.styles.StyleManager
import mx.styles.CSSStyleDeclaration;
import mx.managers.IFocusManagerComponent;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched after the last loading operation caused by
 *  setting the <code>location</code> or <code>htmlText</code>
 *  property has completed.
 *
 *  <p>This event is always dispatched asynchronously,
 *  after the JavaScript <code>load</code> event
 *  has been dispatched in the HTML DOM.</p>
 *
 *  <p>An event handler for this event may call any method
 *  or access any property of this control
 *  or its internal <code>htmlLoader</code>.</p>
 *
 *  @eventType flash.events.Event.COMPLETE
 * 
 *  @see location
 *  @see htmlText
 */
[Event(name="complete", type="flash.events.Event")]

/**
 *  Dispatched after the HTML DOM has been initialized
 *  in response to a loading operation caused by
 *  setting the <code>location</code> or <code>htmlText</code> property.
 *
 *  <p>When this event is dispatched,
 *  no JavaScript methods have yet executed.
 *  The <code>domWindow</code>and <code>domWindow.document</code>
 *  objects exist, but other DOM objects may not.
 *  You can use this event to set properties
 *  onto the <code>domWindow</code> and <code>domWindow.document</code>
 *  objects for JavaScript methods to later access.</p>
 *
 *  <p>A handler for this event should not set any properties
 *  or call any methods which start another loading operation
 *  or which affect the URL for the current loading operation;
 *  doing so causes either an ActionScript or a JavaScript exception.</p>
 *
 *  @eventType flash.events.Event.HTML_DOM_INITIALIZE
 * 
 *  @see location
 *  @see htmlText
 */
[Event(name="htmlDOMInitialize", type="flash.events.Event")]

/**
 *  Dispatched when this control's HTML content initially renders,
 *  and each time that it re-renders.
 *
 *  <p>Because an HTML control can dispatch many of these events,
 *  you should avoid significant processing in a <code>render</code>
 *  handler that might negatively impact performance.</p>
 *
 *  @eventType flash.events.Event.HTML_RENDER
 */
[Event(name="htmlRender", type="flash.events.Event")]

/**
 *  Dispatched when the <code>location</code> property changes.
 *
 *  <p>This event is always dispatched asynchronously.
 *  An event handler for this event may call any method
 *  or access any property of this control
 *  or its internal <code>htmlLoader</code>.</p>
 *
 *  @eventType flash.events.Event.LOCATION_CHANGE
 */
[Event(name="locationChange", type="flash.events.Event")]

/**
 *  Dispatched when an uncaught JavaScript exception occurs.
 *
 *  <p>This event is always dispatched asynchronously.
 *  An event handler for this event may call any method
 *  or access any property of this control
 *  or its internal <code>htmlLoader</code>.</p>
 *
 *  @eventType flash.events.HTMLUncaughtScriptExceptionEvent.UNCAUGHT_SCRIPT_EXCEPTION
 */
[Event(name="uncaughtScriptException", type="flash.events.HTMLUncaughtScriptExceptionEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  The number of pixels between the bottom edge of this control
 *  and the bottom edge of its HTML content area.
 *
 *  @default 0
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  The number of pixels between the left edge of this control
 *  and the left edge of its HTML content area.
 *
 *  @default 0
 */
[Style(name="paddingLeft", type="Number", format="Length", inherit="no")]

/**
 *  The number of pixels between the right edge of this control
 *  and the right edge of its HTML content area.
 *
 *  @default 0
 */
[Style(name="paddingRight", type="Number", format="Length", inherit="no")]

/**
 *  The number of pixels between the top edge of this control
 *  and the top edge of its HTML content area.
 *
 *  @default 0
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("HTML.png")]

/**
 *  The HTML control lets you display HTML content in your application.
 *
 *  <p>You use the <code>location</code> property to specify the URL
 *  of an HTML page whose content is displayed in the control, or you
 *  can set the <code>htmlText</code> property to specify a String
 *  containing HTML-formatted text that is rendered in the control.</p>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:HTML&gt;</code> tag inherits all of the tag
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:HTML
 *    <strong>Properties</strong>
 *    data="<i>null</i>"
 *    historyPosition="0"
 *    htmlHost="<i>null</i>"
 *    htmlLoaderFactory="mx.core.ClassFactory"
 *    htmlText=""
 *    listData="<i>null</i>"
 *    location=""
 *    paintsDefaultBackground="false"
 *    runtimeApplicationDomain="<i>null</i>"
 *    userAgent="<i>null</i>"
 * 
 *    <strong>Styles</strong>
 *    paddingBottom="0"
 *    paddingLeft="0"
 *    paddingRight="0"
 *    paddingTop="0"
 * 
 *    <strong>Events</strong>
 *    complete="<i>No default</i>"
 *    htmlDOMInitialize="<i>No default</i>"
 *    htmlRender="<i>No default</i>"
 *    locationChange="<i>No default</i>"
 *    uncaughtScriptException="<i>No default</i>"
 *  /&gt;
 *  </pre>
 * 
 *  @see flash.html.HTMLLoader
 * 
 *  @playerversion AIR 1.1
 */
 public class HTML extends ScrollControlBase
    implements IDataRenderer, IDropInListItemRenderer,
    IListItemRenderer, IFocusManagerComponent
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
    private static const MAX_HTML_WIDTH:Number = 2880;

    /**
     *  @private
     */
    private static const MAX_HTML_HEIGHT:Number = 2880;

    //--------------------------------------------------------------------------
    //
    //  Class properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  pdfCapability
    //----------------------------------

    /**
     *  The type of PDF support on the user's system,
     *  defined as an integer code value.
     *
     *  <p>An HTML object can display PDF content only if this property
     *  evaluates to <code>PDFCapability.STATUS_OK</code>.
     *  The PDFCapability class defines constants for possible values
     *  of the <code>pdfCapability</code> property, as follows:</p>
     *
     *  <table class="innertable">
     *    <tr>
     *     <th>PDFCapability constant</th>
     *     <th>Meaning</th>
     *    </tr>
     *    <tr>
     *     <td><code>STATUS_OK</code></td>
     *     <td>A sufficient version (8.1 or later) of Acrobat Reader
     *         is detected and PDF content can be loaded in an HTML object.
     *       <p><em>Note:</em> On Windows, if a Acrobat Acrobat
     *         or Acrobat Reader version 7.x or above
     *       is currently running on the user's system,
     *         that version is used even if a later version
     *       that supports loading PDF loaded in an HTML object is installed.
     *         In this case, if the the value of the
     *         <code>pdfCampability</code> property is
     *       <code>PDFCapability.STATUS_OK</code>,
     *         when an AIR application attempts to load PDF content
     *      into an HTML object, the older version of Acrobat or Reader
     *         displays an alert, without an error message displayed the AIR runtime.
     *         If this is a possible situation for your end users,
     *         you may consider providing them with instructions to close Acrobat
     *      while running your application.
     *         You may consider displaying these instructions if the PDF
     *      content does not load within an acceptable timeframe.</p></td>
     *    </tr>
     *    <tr>
     *     <td><code>ERROR_INSTALLED_READER_NOT_FOUND</code></td>
     *     <td>No version of Acrobat Reader is detected.
     *         An HTML object cannot display PDF content.</td>
     *    </tr>
     *    <tr>
     *     <td><code>ERROR_INSTALLED_READER_TOO_OLD</code></td>
     *     <td>Acrobat Reader has been detected, but the version is too old.
     *         An HTML object cannot display PDF content.</td>
     *    </tr>
     *    <tr>
     *     <td><code>ERROR_PREFERED_READER_TOO_OLD</code></td>
     *     <td>A sufficient version (8.1 or later) of Acrobat Reader is detected,
     *         but the the version of Acrobat Reader that is setup
     *         to handle PDF content is older than Reader 8.1.
     *         An HTML object cannot display PDF content.</td>
     *    </tr>
     *  </table>
     */
    public static function get pdfCapability():int
    {
        return HTMLLoader.pdfCapability;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function HTML()
    {
        super();

        mx_internal::_horizontalScrollPolicy = ScrollPolicy.AUTO;
        mx_internal::_verticalScrollPolicy = ScrollPolicy.AUTO;

		tabEnabled = false;
		tabChildren = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Flag that will block default data/listData behavior.
     */
    private var textSet:Boolean;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function set verticalScrollPosition(value:Number):void
    {
        // Clip the vertical scroll position to appropriate min/max bounds.
        value = Math.max(value,0);
        if (htmlLoader && (htmlLoader.contentHeight > htmlLoader.height))
            value = Math.min(value,htmlLoader.contentHeight - htmlLoader.height)

        super.verticalScrollPosition = value;

        if (htmlLoader)
            htmlLoader.scrollV = value;
        else
            invalidateProperties();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  contentHeight
    //----------------------------------

    /**
     *  The height, in pixels, of the HTML content.
     */
    public function get contentHeight():Number
    {
        if (!htmlLoader)
            return 0;

        return htmlLoader.contentHeight;
    }

    //----------------------------------
    //  contentWidth
    //----------------------------------

    /**
     *  The width, in pixels, of the HTML content.
     */
    public function get contentWidth():Number
    {
        if (!htmlLoader)
            return 0;

        return htmlLoader.contentWidth;
    }

    //----------------------------------
    //  data
    //----------------------------------

    /**
     *  @private
     *  Storage for the data property.
     */
    private var _data:Object;

    [Bindable("dataChange")]
    [Inspectable(environment="none")]

    /**
     *  Lets you pass a value to the component
     *  when you use it in an item renderer or item editor.
     *  You typically use data binding to bind a field of the <code>data</code>
     *  property to a property of this component.
     *
     *  <p>When you use the control as a drop-in item renderer or drop-in
     *  item editor, Flex automatically writes the current value of the item
     *  to the <code>text</code> property of this control.</p>
     *
     *  <p>You cannot set this property in MXML.</p>
     *
     *  @default null
     *  @see mx.core.IDataRenderer
     */
    public function get data():Object
    {
        return _data;
    }

    /**
     *  @private
     */
    public function set data(value:Object):void
    {
        var newText:*;

        _data = value;

        if (_listData)
        {
            newText = _listData.label;
        }
        else if (_data != null)
        {
            if (_data is String)
                newText = String(_data);
            else
                newText = _data.toString();
        }

        if (newText !== undefined && !textSet)
        {
            htmlText = newText;
            textSet = false;
        }

        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
    }

    //----------------------------------
    //  historyLength
    //----------------------------------

    /**
     *  The overall length of the history list,
     *  including back and forward entries.
     *
     *  This property has the same value
     *  as the <code>window.history.length</code>
     *  JavaScript property of the the HTML content.
     *
     *  @see #historyPosition
     */
    public function get historyLength():int
    {
        if (!htmlLoader)
            return 0;

        return htmlLoader.historyLength;
    }

    //----------------------------------
    //  historyPosition
    //----------------------------------

    /**
     *  The current position in the history list.
     *
     *  <p>The history list corresponds to the <code>window.history</code>
     *  object of the HTML content.
     *  Entries less than the current position are the "back" list;
     *  entries greater are "forward."
     *  Attempting to set the position beyond the end sets it to the end.</p>
	 * 
	 *  @default 0
     */
    public function get historyPosition():int
    {
        if (!htmlLoader)
            return 0;

        return htmlLoader.historyPosition;
    }

    /**
     *  @private
     */
    public function set historyPosition(value:int):void
    {
        if (htmlLoader)
            htmlLoader.historyPosition = value;
    }

    //----------------------------------
    //  htmlLoader
    //----------------------------------

    /**
     *  The internal HTMLLoader object that renders
     *  the HTML content for this control.
     */
    public var htmlLoader:HTMLLoader;

    //----------------------------------
    //  htmlLoaderFactory
    //----------------------------------

    /**
     *  @private
     *  Storage for the htmlLoaderFactory property.
     */
    private var _htmlLoaderFactory:IFactory = new ClassFactory(FlexHTMLLoader);

    [Bindable("htmlLoaderFactoryChanged")]

    /**
     *  The IFactory that creates an HTMLLoader-derived instance
     *  to use as the htmlLoader.
	 *
     *  <p>The default value is an IFactory for HTMLLoader.</p>
     */
    public function get htmlLoaderFactory():IFactory
    {
        return _htmlLoaderFactory;
    }

    /**
     *  @private
     */
    public function set htmlLoaderFactory(value:IFactory):void
    {
        _htmlLoaderFactory = value;

		dispatchEvent(new Event("htmlLoaderFactoryChanged"));
    }

    //----------------------------------
    //  htmlHost
    //----------------------------------

    /**
     *  @private
     *  Storage for the htmlHost property.
     */
    private var _htmlHost:HTMLHost;

    /**
     *  @private
     */
    private var htmlHostChanged:Boolean = false;

    /**
     *  The HTMLHost object is used to handle changes
     *  to certain user interface elements in the HTML content,
     *  such as the <code>window.document.title</code> property.
     *
     *  <p>To override default behaviors for the HTMLLoader,
     *  create a subclass of the HTMLHost class,
     *  override its member functions
     *  to handle various user interface changes in the HTML content,
     *  and set this property to an instance of your subclass.</p>
	 * 
	 *  @default null
     */
    public function get htmlHost():HTMLHost
    {
        return _htmlHost;
    }

    /**
     *  @private
     */
    public function set htmlHost(value:HTMLHost):void
    {
        _htmlHost = value;
        htmlHostChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  htmlText
    //----------------------------------

    /**
     *  @private
     *  Storage for the htmlText property.
     */
    private var _htmlText:String;

    /**
     *  @private
     */
    private var htmlTextChanged:Boolean = false;

    [Bindable("htmlTextChanged")]

    /**
     *  Specifies an HTML-formatted String for display by the control.
     *
     *  <p>Setting this property has the side effect of setting
     *  the <code>location</code> property to <code>null</code>,
     *  and vice versa.</p>
	 * 
	 *  <p>Content added via the <code>htmlText</code> property is put in the 
	 *  application security sandbox. If an AIR application includes an HTML 
	 *  control located in the application sandbox, and remote HTML code is 
	 *  directly added into the control by setting the  <code>htmlText</code> 
	 *  property, any script contained in the HTML text is executed in the 
	 *  application sandbox.</p>
     *
     *  @default ""
     *
     *  @see #location
     */
    public function get htmlText():String
    {
        return _htmlText;
    }

    /**
     *  @private
     */
    public function set htmlText(value:String):void
    {
        _htmlText = value;
        htmlTextChanged = true;

        // Setting both location and htmlText doesn't make sense,
        // so setting one sets the other to null,
        // and the last one set is applied in commitProperties().
        _location = null;
        locationChanged = false;

        invalidateProperties();
        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("htmlTextChanged"));
    }

    //----------------------------------
    //  domWindow
    //----------------------------------

    /**
     *  The JavaScript <code>window</code> object
     *  for the root frame of the HTML DOM inside this control.
     *
     *  <p>This property is <code>null</code> until the
     *  <code>htmlDOMInitialize</code> event has been dispatched.</p>
     *
     *  @default null
     */
    public function get domWindow():Object
    {
        if (!htmlLoader)
            return null;

        return htmlLoader.window;
    }

    //----------------------------------
    //  listData
    //----------------------------------

    /**
     *  @private
     *  Storage for the listData property.
     */
    private var _listData:BaseListData;

    [Bindable("dataChange")]
    [Inspectable(environment="none")]

    /**
     *  When a component is used as a drop-in item renderer or drop-in
     *  item editor, Flex initializes the <code>listData</code> property
     *  of the component with the appropriate data from the List control.
     *  The component can then use the <code>listData</code> property
     *  to initialize the <code>data</code> property of the drop-in
     *  item renderer or drop-in item editor.
     *
     *  <p>You do not set this property in MXML or ActionScript;
     *  Flex sets it when the component is used as a drop-in item renderer
     *  or drop-in item editor.</p>
     *
     *  @default null
     *  @see mx.controls.listClasses.IDropInListItemRenderer
     */
    public function get listData():BaseListData
    {
        return _listData;
    }

    /**
     *  @private
     */
    public function set listData(value:BaseListData):void
    {
        _listData = value;
    }

    //----------------------------------
    //  loaded
    //----------------------------------

    /**
     *  A flag which indicates whether the JavaScript <code>load</code> event
     *  corresponding to the previous loading operation
     *  has been delivered to the HTML DOM in this control.
     *
     *  <p>This property is <code>true</code>
     *  before the <code>complete</code> event is dispatched.</p>
     *
     *  <p>It is possible that this property
     *  never becomes <code>true</code>.
     *  This happens in the same cases
     *  in which the <code>complete</code> event is never dispatched.</p>
     *
     *  @default false
     */
    public function get loaded():Boolean
    {
        if (!htmlLoader || locationChanged || htmlTextChanged)
            return false;

        return htmlLoader.loaded;
    }

    //----------------------------------
    //  location
    //----------------------------------

    /**
     *  @private
     *  Storage for the location property.
     */
    private var _location:String;

    /**
     *  @private
     */
    private var locationChanged:Boolean = false;

    [Bindable("locationChange")]

    /**
     *  The URL of an HTML page to be displayed by this control.
     *
     *  <p>Setting this property has the side effect of setting
     *  the <code>htmlText</code> property to <code>null</code>,
     *  and vice versa.</p>
     *
     *  @default ""
     *
     *  @see #htmlText
     */
    public function get location():String
    {
        return _location;
    }

    /**
     *  @private
     */
    public function set location(value:String):void
    {
        _location = value;
        locationChanged = true;

        // Setting both location and htmlText doesn't make sense,
        // so setting one sets the other to null,
        // and the last one set is applied in commitProperties().
        _htmlText = null;
        htmlTextChanged = false;

        invalidateProperties();
        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("locationChange"));
    }

    //----------------------------------
    //  paintsDefaultBackground
    //----------------------------------

    /**
     *  @private
     *  Storage for the paintsDefaultBackground property.
     */
    private var _paintsDefaultBackground:Boolean;

    /**
     *  @private
     */
    private var paintsDefaultBackgroundChanged:Boolean = false;

    /**
     *  Whether this control's HTML content
     *  has a default opaque white background or not.
     *
     *  <p>If this property is <code>false</code>,
     *  then the background specified for this Flex control, if any,
     *  appears behind the HTML content.</p>
     *
     *  <p>However, if any HTML element has its own opaque background color
     *  (specified by style="background-color:gray", for instance),
     *  then that background appears behind that element.</p>
	 * 
	 *  @default false;
     */
    public function get paintsDefaultBackground():Boolean
    {
        return _paintsDefaultBackground;
    }

    /**
     *  @private
     */
    public function set paintsDefaultBackground(value:Boolean):void
    {
        _paintsDefaultBackground = value;
        paintsDefaultBackgroundChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  runtimeApplicationDomain
    //----------------------------------

    /**
     *  @private
     *  Storage for the runtimeApplicationDomain property.
     */
    private var _runtimeApplicationDomain:ApplicationDomain;

    /**
     *  @private
     */
    private var runtimeApplicationDomainChanged:Boolean = false;

    /**
     *  The ApplicationDomain to use for HTML's <code>window.runtime</code>
     *  scripting.
     *
     *  <p>If this property is <code>null</code>, or if it specifies
     *  an ApplicationDomain from a different security domain
     *  than the HTML content, the HTML page uses a default
     *  <code>ApplicationDomain</code> for the page's domain.</p>
     *
     *  @default null
     */
    public function get runtimeApplicationDomain():ApplicationDomain
    {
        return _runtimeApplicationDomain;
    }

    /**
     *  @private
     */
    public function set runtimeApplicationDomain(value:ApplicationDomain):void
    {
        _runtimeApplicationDomain = value;
        runtimeApplicationDomainChanged = true;

        invalidateProperties();
    }

    //----------------------------------
    //  userAgent
    //----------------------------------

    /**
     *  @private
     *  Storage for the userAgent property.
     */
    private var _userAgent:String;

    /**
     *  @private
     */
    private var userAgentChanged:Boolean = false;

    /**
     *  The user agent string to be used in content requests
     *  from this control.
     *
     *  <p>You can set the default user agent string used by all
     *  HTML controls in an application domain by setting the
     *  static <code>URLRequestDefaults.userAgent</code> property.
     *  If no value is set for the <code>userAgent</code> property
     *  (or if the value is set to <code>null</code>),
     *  the user agent string is set to the value of
     *  <code>URLRequestDefaults.userAgent</code>.</p>
     *
     *  <p>If neither the <code>userAgent</code> property
     *  of this control nor for <code>URLRequestDefaults.userAgent</code>,
     *  has a value set, a default value is used as the user agent string.
     *  This default value varies depending on the runtime
     *  operating system (such as Mac OS or Windows),
     *  the runtime language, and the runtime version,
     *  as in the following two examples:</p>
     *
     *  <pre>
     *  "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) AdobeAIR/1.0"
     *  "Mozilla/5.0 (Windows; U; en) AppleWebKit/420+ (KHTML, like Gecko) AdobeAIR/1.0"
     *  </pre>
     *
     *  @default null
     *
     *  @see flash.net.URLRequest#userAgent
     *  @see flash.net.URLRequestDefaults#userAgent
     */
    public function get userAgent():String
    {
        return _userAgent;
    }

    /**
     *  @private
     */
    public function set userAgent(value:String):void
    {
        _userAgent = value;
        userAgentChanged = true;

        invalidateProperties();
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        if (!htmlLoader)
        {
            htmlLoader = htmlLoaderFactory.newInstance();

            htmlLoader.addEventListener(
                Event.HTML_DOM_INITIALIZE, htmlLoader_domInitialize);

            htmlLoader.addEventListener(
                Event.COMPLETE, htmlLoader_completeHandler);

            htmlLoader.addEventListener(
                Event.HTML_RENDER, htmlLoader_htmlRenderHandler);

             htmlLoader.addEventListener(
                Event.LOCATION_CHANGE, htmlLoader_locationChangeHandler);

            htmlLoader.addEventListener(
                Event.HTML_BOUNDS_CHANGE, htmlLoader_htmlBoundsChangeHandler);

            htmlLoader.addEventListener(
                Event.SCROLL, htmlLoader_scrollHandler);

            htmlLoader.addEventListener(
                HTMLUncaughtScriptExceptionEvent.UNCAUGHT_SCRIPT_EXCEPTION,
                htmlLoader_uncaughtScriptExceptionHandler);

            addChild(htmlLoader);
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        // Change the properties of the HTMLLoader
        // before calling its load() or loadString() method.

        if (htmlHostChanged)
        {
            htmlLoader.htmlHost = _htmlHost;
            htmlHostChanged = false;
        }

        if (paintsDefaultBackgroundChanged)
        {
            htmlLoader.paintsDefaultBackground = _paintsDefaultBackground;
            paintsDefaultBackgroundChanged = false;
        }

        if (runtimeApplicationDomainChanged)
        {
            htmlLoader.runtimeApplicationDomain = _runtimeApplicationDomain;
            runtimeApplicationDomainChanged = false;
        }

        if (userAgentChanged)
        {
            htmlLoader.userAgent = _userAgent;
            userAgentChanged = false;
        }

        if (locationChanged)
        {
            htmlLoader.load(new URLRequest(_location));
            locationChanged = false;
        }

        if (htmlTextChanged)
        {
            htmlLoader.loadString(_htmlText);
            htmlTextChanged = false;
        }
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        var em:EdgeMetrics = viewMetrics;

        em.left += getStyle("paddingLeft");
        em.top += getStyle("paddingTop");
        em.right += getStyle("paddingRight");
        em.bottom += getStyle("paddingBottom");

        measuredWidth = Math.min(htmlLoader.contentWidth + em.left + em.right,
                                 MAX_HTML_WIDTH);
        measuredHeight = Math.min(htmlLoader.contentHeight + em.top + em.bottom,
                                  MAX_HTML_HEIGHT);

        //trace("measure", htmlLoader.contentWidth, htmlLoader.contentHeight, measuredWidth, measuredHeight);
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var em:EdgeMetrics = viewMetrics;

        em.left += getStyle("paddingLeft");
        em.top += getStyle("paddingTop");
        em.right += getStyle("paddingRight");
        em.bottom += getStyle("paddingBottom");

        htmlLoader.x = em.left;
        htmlLoader.y = em.top;

        // The width and height of an HTMLLoader can't be 0.
        var w:Number = Math.max(unscaledWidth - em.left - em.right, 1);
        var h:Number = Math.max(unscaledHeight - em.top - em.bottom, 1);

        htmlLoader.width = w;
        htmlLoader.height = h;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Cancels any load operation in progress.
     *
     *  <p>This method does nothing if it is called before this component's
     *  internal HTMLLoader (the <code>htmlLoader</code> property) has been created.</p>
     */
    public function cancelLoad():void
    {
        if (htmlLoader)
            htmlLoader.cancelLoad();
    }

    /**
     *  Returns the HTMLHistoryItem at the specified position
     *  in this control's history list.
     *
     *  <p>This method returns <code>null</code> if it is called before this
     *  component's internal HTMLLoader (the <code>htmlLoader</code> property) has been created.</p>
     *
     *  @param position The position in the history list.
     *
     *  @return A HTMLHistoryItem object
     *  for the history entry  at the specified position.
     *
     *  @see historyPosition
     */
    public function getHistoryAt(position:int):HTMLHistoryItem
    {
        if (!htmlLoader)
            return null;

        return htmlLoader.getHistoryAt(position);
    }

    /**
     *  Navigates back in this control's history list, if possible.
     *
     *  <p>Calling this method of the HTMLLoader object
     *  has the same effect as calling the <code>back()</code> method
     *  of the <code>window.history</code> property in JavaScript
     *  in the HTML content.</p>
     *
     *  <p>This method does nothing if it is called before this component's
     *  internal HTMLLoader (the <code>htmlLoader</code> property) has been created.</p>
     *
     *  @see #historyPosition
     *  @see #historyForward()
     */
    public function historyBack():void
    {
        if (htmlLoader)
            htmlLoader.historyBack();
    }

    /**
     *  Navigates forward in this control's history list, if possible.
     *
     *  <p>Calling this method of the HTMLLoader object
     *  has the same effect as calling the <code>forward()</code> method
     *  of the <code>window.history</code> property in JavaScript
     *  in the HTML content.</p>
     *
     *  <p>This function throws no errors.</p>
     *
     *  <p>This method does nothing if it is called before this component's
     *  internal HTMLLoader (the <code>htmlLoader</code> property) has been created.</p>
     *
     *  @see #historyPosition
     *  @see #historyBack()
     */
    public function historyForward():void
    {
        if (htmlLoader)
            htmlLoader.historyForward();
    }

    /**
     *  Navigates the specified number of steps in this control's history list.
     *
     *  <p>This method navigates forward if the number of steps
     *  is positive and backward if it is negative.
     *  Navigation by zero steps is equivalent
     *  to calling <code>reload()</code>.</p>
     *
     *  <p>This method is equivalent to calling the <code>go()</code> method
     *  of the <code>window.history</code> property in JavaScript
     *  in the HTML content.</p>
     *
     *  <p>This method does nothing if it is called before this component's
     *  internal HTMLLoader (the <code>htmlLoader</code> property) has been created.</p>
     *
     *  @param steps The number of steps in the history list
     *  to move forward (positive) or backward (negative).
     */
    public function historyGo(steps:int):void
    {
        if (htmlLoader)
            htmlLoader.historyGo(steps);
    }

    /**
     *  Reloads the HTML content from the current <code>location</code>.
     *
     *  <p>This method does nothing if it is called before this component's
     *  internal HTMLLoader (the <code>htmlLoader</code> property) has been created.</p>
     */
    public function reload():void
    {
        if (htmlLoader)
            htmlLoader.reload();
    }

    /**
     *  @private
     */
    private function adjustScrollBars():void
    {
        setScrollBarProperties(htmlLoader.contentWidth, htmlLoader.width,
                               htmlLoader.contentHeight, htmlLoader.height);

        // This is a temporary solution for adjusting the speed of scrolling
        // via scrollbar arrows. There isn't a lot of overhead here, but
        // we still shouldn't have to set these values every time
        // updateDisplayList() is called.
        if (verticalScrollBar)
            verticalScrollBar.lineScrollSize = 20;
        if (horizontalScrollBar)
            horizontalScrollBar.lineScrollSize = 20;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: ScrollControlBase
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function scrollHandler(event:Event):void
    {
        super.scrollHandler(event);

        htmlLoader.scrollH = horizontalScrollPosition;
        htmlLoader.scrollV = verticalScrollPosition;
    }

    /**
     *  @private
     */
    override protected function mouseWheelHandler(event:MouseEvent):void
    {
    	// Ignore mouseWheel events that are bubbling up
    	// from the HTMLLoader; they have already been handled there.
    	if (event.target != this)
    		return;

        // Magnify the scrolling to approximate what browsers do.
        event.delta *= 6;

        super.mouseWheelHandler(event);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function htmlLoader_domInitialize(event:Event):void
    {
         dispatchEvent(event);
    }

    /*
     *  @private
     */
    private function htmlLoader_completeHandler(event:Event):void
    {
         invalidateSize();

         dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function htmlLoader_htmlRenderHandler(event:Event):void
    {
        dispatchEvent(event);

        adjustScrollBars();
    }

    /**
     *  @private
     */
    private function htmlLoader_locationChangeHandler(event:Event):void
    {
        var change:Boolean = _location != htmlLoader.location;

        _location = htmlLoader.location;

        if (change)
            dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function htmlLoader_htmlBoundsChangeHandler(event:Event):void
    {
        invalidateSize();

        adjustScrollBars();
    }

    /**
     *  @private
     */
    private function htmlLoader_scrollHandler(event:Event):void
    {
    	horizontalScrollPosition = htmlLoader.scrollH;
        verticalScrollPosition = htmlLoader.scrollV;
    }

    /**
     *  @private
     */
    private function htmlLoader_uncaughtScriptExceptionHandler(
                            event:HTMLUncaughtScriptExceptionEvent):void
    {
        var clonedEvent:Event = event.clone();

        dispatchEvent(clonedEvent);

        if (clonedEvent.isDefaultPrevented())
            event.preventDefault();
    }
}

}
