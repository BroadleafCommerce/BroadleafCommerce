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

package mx.core
{

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.Shape;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.Point;

import mx.controls.HScrollBar;
import mx.controls.ToolTip;
import mx.controls.VScrollBar;
import mx.controls.scrollClasses.ScrollBar;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDetail;
import mx.events.ScrollEventDirection;
import mx.managers.ToolTipManager;
import mx.styles.ISimpleStyleClient;

use namespace mx_internal;

/**
 *  Dispatched when the content is scrolled.
 *
 *  @eventType mx.events.ScrollEvent.SCROLL
 *  @helpid 3269
 */
[Event(name="scroll", type="mx.events.ScrollEvent")]

include "../styles/metadata/BorderStyles.as"
include "../styles/metadata/LeadingStyle.as"
include "../styles/metadata/TextStyles.as"

/**
 *  Style name for horizontal scrollbar.  This allows more control over
 *  the appearance of the scrollbar.
 *
 *  @default undefined
 */
[Style(name="horizontalScrollBarStyleName", type="String", inherit="no")]

/**
 *  Style name for vertical scrollbar.  This allows more control over
 *  the appearance of the scrollbar.
 *
 *  @default undefined
 */
[Style(name="verticalScrollBarStyleName", type="String", inherit="no")]

/**
 *  The ScrollControlBase class is the base class for controls
 *  with scroll bars.
 *  The user interacts with the scroll bar or the developer accesses
 *  methods and properties that alter the viewable area.
 *  The ScrollControlBase takes a single child object and positions and
 *  masks or sizes that object to display the viewable content.
 *  All items to be scrolled must be children of that content object
 *
 *  @mxml
 *
 *  <p>The <code>&lt;ScrollControlBase&gt;</code> tag inherits all the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:<i>tagname</i>
 *  <b>Properties</b>
 *  border="<i>object of border skin style</i>"
 *  horizontalScrollPolicy="off|on|auto"
 *  horizontalScrollPosition="0"
 *  liveScrolling="true|false"
 *  maxHorizontalScrollPosition="NaN"
 *  maxVerticalScrollPosition="NaN"
 *  scrollTipFunction="undefined"
 *  showScrollTips="false|true"
 *  verticalScrollPolicy="auto|off|on"
 *  verticalScrollPosition="0" 
 *  <b>Styles</b>
 *  backgroundAlpha="1.0"
 *  backgroundColor="undefined"
 *  backgroundImage="undefined"
 *  backgroundSize="auto"
 *  borderColor="0xAAB3B3"
 *  borderSides="left top right bottom"
 *  borderSkin="ClassReference('mx.skins.halo.HaloBorder')"
 *  borderStyle="inset"
 *  borderThickness="1"
 *  color="0x0B333C"
 *  cornerRadius="0"
 *  disabledColor="0xAAB3B3"
 *  dropShadowColor="0x000000"
 *  dropShadowEnabled="false"
 *  fontFamily="Verdana"
 *  fontSize="10"
 *  fontStyle="normal|italic"
 *  fontWeight="normal|bold"
 *  horizontalScrollBarStyleName=""
 *  leading="2"
 *  shadowDirection="center"
 *  shadowDistance="2"
 *  textAlign="<i>value; see detail.</i>"
 *  textDecoration="none|underline"
 *  textIndent="0"
 *  verticalScrollBarStyleName=""
 * 
 *  <b>Events</b>
 *  scroll="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @helpid 3270
 *  @tiptext base class for views/containers that support scrolling
 */
public class ScrollControlBase extends UIComponent
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
    public function ScrollControlBase()
    {
        super();

        _viewMetrics = EdgeMetrics.EMPTY;

        // Listen for "mouseWheel" events on myself or any of my children
        addEventListener(MouseEvent.MOUSE_WHEEL, mouseWheelHandler);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  The border object.
     */
    protected var border:IFlexDisplayObject;

    /**
     *  @private
     *  Offsets including borders and scrollbars.
     */
    private var _viewMetrics:EdgeMetrics;

    /**
     *  The mask. This property may be undefined if no scroll bars
     *  are currently displayed or in some subclasses which
     *  have a different masking mechanism.
     *  A mask defined the viewable area of a displayable object.
     *  Thus this mask is used to hide the portions of the content
     *  that are not currently viewable.
     *  In general you do not access this property directly.
     *  Manipulation of the <code>horizontalScrollPolicy</code>, 
     *  <code>verticalScrollPolicy</code>, <code>horizontalScrollPosition</code>, 
     *  and <code>verticalScrollPosition</code> properties
     *  should provide sufficient control over the mask.
     */
    protected var maskShape:Shape;

    /**
     *  The horizontal scroll bar.
     *  This property is null if no horizontal scroll bar
     *  is currently displayed.
     *  In general you do not access this property directly.
     *  Manipulation of the <code>horizontalScrollPolicy</code> 
     *  and <code>horizontalScrollPosition</code>
     *  properties should provide sufficient control over the scroll bar.
     */
    protected var horizontalScrollBar:ScrollBar;

    /**
     *  The vertical scroll bar.
     *  This property is null if no vertical scroll bar
     *  is currently displayed.
     *  In general you do not access this property directly.
     *  Manipulation of the <code>verticalScrollPolicy</code> 
     *  and <code>verticalScrollPosition</code>
     *  properties should provide sufficient control over the scroll bar.
     */
    protected var verticalScrollBar:ScrollBar;

    /**
     *  @private
     */
    private var numberOfCols:Number = 0;

    /**
     *  @private
     */
    private var numberOfRows:Number = 0;

    /**
     *  @private
     */
    mx_internal var _maxVerticalScrollPosition:Number;
    mx_internal var _maxHorizontalScrollPosition:Number;

    /**
     *  @private
     */
    private var viewableRows:Number;
    private var viewableColumns:Number;

    /**
     *  @private
     */
    private var propsInited:Boolean;

    /**
     *  A flag that the scrolling area changed due to the appearance or disappearance of
     *  scrollbars.  Used by most layout methods to re-adjust the scrolling properties again.
     */
    protected var scrollAreaChanged:Boolean;

    /**
     *  @private
     */
    private var invLayout:Boolean;

    /**
     *  @private
     *  Instance of the scrollTip. (There can be only one.)
     */
    private var scrollTip:ToolTip;

    /**
     *  @private
     *  Base position for the scrollTip.
     */
    private var scrollThumbMidPoint:Number;

    /**
     *  @private
     *  Keep track of the whether the ToolTipManager was enabled
     *  before dealing with scroll tips.
     */
    private var oldTTMEnabled:Boolean;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  @private
     *  Scrollbars must be enabled/disabled when we are.
     */
    override public function set enabled(value:Boolean):void
    {
        super.enabled = value;

        if (horizontalScrollBar)
            horizontalScrollBar.enabled = value;

        if (verticalScrollBar)
            verticalScrollBar.enabled = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  borderMetrics
    //----------------------------------

    /**
     *  Returns an EdgeMetrics object that has four properties:
     *  <code>left</code>, <code>top</code>, <code>right</code>,
     *  and <code>bottom</code>.
     *  The value of each property is equal to the thickness of one side
     *  of the border, expressed in pixels.
     *
     *  @return EdgeMetrics object with the left, right, top,
     *  and bottom properties.
     */
    public function get borderMetrics():EdgeMetrics
    {
        return (border && border is IRectangularBorder) ?
                IRectangularBorder(border).borderMetrics : EdgeMetrics.EMPTY;
    }

    //----------------------------------
    //  horizontalScrollPosition
    //----------------------------------

    /**
     *  @private
     *  Storage for the horizontalScrollPosition property.
     */
    mx_internal var _horizontalScrollPosition:Number = 0;

    [Bindable("scroll")]
    [Bindable("viewChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  The offset into the content from the left edge.  This can
     *  be a pixel offset in some subclasses or some other metric
     *  like the number of columns in a DataGrid or number of items
     *  in a HorizontalList or TileList.
     *
     *  @default 0
     */
    public function get horizontalScrollPosition():Number
    {
        return _horizontalScrollPosition;
    }

    /**
     *  @private
     *  This only moves the scrollBars -- assumes the call emanated
     *  from the scrollable content.
     */
    public function set horizontalScrollPosition(value:Number):void
    {
        _horizontalScrollPosition = value;

        if (horizontalScrollBar)
            horizontalScrollBar.scrollPosition = value;

        dispatchEvent(new Event("viewChanged"));
    }

    //----------------------------------
    //  horizontalScrollPolicy
    //----------------------------------

    /**
     *  @private
     *  Storage for the horizontalScrollPolicy property.
     */
    mx_internal var _horizontalScrollPolicy:String = ScrollPolicy.OFF;

    [Bindable("horizontalScrollPolicyChanged")]
    [Inspectable(enumeration="off,on,auto", defaultValue="off")]

    /**
     *  A property that indicates whether the horizontal scroll 
     *  bar is always on, always off,
     *  or automatically changes based on the parameters passed to the
     *  <code>setScrollBarProperties()</code> method.
     *  Allowed values are <code>ScrollPolicy.ON</code>,
     *  <code>ScrollPolicy.OFF</code>, and <code>ScrollPolicy.AUTO</code>.
     *  MXML values can be <code>"on"</code>, <code>"off"</code>,
     *  and <code>"auto"</code>.
     *
     *  <p>Setting this property to <code>ScrollPolicy.OFF</code> for ListBase
     *  subclasses does not affect the <code>horizontalScrollPosition</code>
     *  property; you can still scroll the contents programmatically.</p>
     *
     *  <p>Note that the policy can affect the measured size of the component
     *  If the policy is <code>ScrollPolicy.AUTO</code> the
     *  scrollbar is not factored in the measured size.  This is done to
     *  keep the layout from recalculating when the scrollbar appears.  If you
     *  know that you will have enough data for scrollbars you should set
     *  the policy to <code>ScrollPolicy.ON</code>.  If you
     *  don't know, you may need to set an explicit width or height on
     *  the component to allow for scrollbars to appear later.</p>
     *
     *  @default ScrollPolicy.OFF
     */
    public function get horizontalScrollPolicy():String
    {
        return _horizontalScrollPolicy;
    }

    /**
     *  @private
     */
    public function set horizontalScrollPolicy(value:String):void
    {
        var newPolicy:String = value.toLowerCase();

        if (_horizontalScrollPolicy != newPolicy)
        {
            _horizontalScrollPolicy = newPolicy;
            invalidateDisplayList();

            dispatchEvent(new Event("horizontalScrollPolicyChanged"));
        }
    }

    //----------------------------------
    //  liveScrolling
    //----------------------------------

    [Inspectable(defaultValue="true")]

    /**
     *  A flag that indicates whether scrolling is live as the 
     *  scrollbar thumb is moved
     *  or the view is not updated until the thumb is released.
     *  The default value is <code>true</code>.
     */
    public var liveScrolling:Boolean = true;

    //----------------------------------
    //  maxHorizontalScrollPosition
    //----------------------------------

    [Bindable("maxHorizontalScrollPositionChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  The maximum value for the <code>horizontalScrollPosition</code> property.
     *  Note that this is not the width of the content because the
     *  <code>maxHorizontalScrollPosition</code> property contains the width 
     *  of the content minus the width of the displayable area.
     *
     *  <p>In most components, the value of the 
     *  <code>maxHorizontalScrollPosition</code> property is computed from the
     *  data and size of component, and must not be set by
     *  the application code.</p>
     */
    public function get maxHorizontalScrollPosition():Number
    {
        if (!isNaN(_maxHorizontalScrollPosition))
            return _maxHorizontalScrollPosition;

        var m:Number = horizontalScrollBar ?
                       horizontalScrollBar.maxScrollPosition :
                       0;
        return m;
    }

    /**
     *  @private
     */
    public function set maxHorizontalScrollPosition(value:Number):void
    {
        _maxHorizontalScrollPosition = value;

        dispatchEvent(new Event("maxHorizontalScrollPositionChanged"));
    }

    //----------------------------------
    //  maxVerticalScrollPosition
    //----------------------------------

    [Bindable("maxVerticalScrollPositionChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  The maximum value for the <code>verticalScrollPosition</code> property.
     *  Note that this is not the height of the content because the
     *  <code>maxVerticalScrollPosition</code> property contains the height 
     *  of the content minus the height of the displayable area.
     *
     *  <p>The value of the 
     *  <code>maxVerticalScrollPosition</code> property is computed from the
     *  data and size of component, and must not be set by
     *  the application code.</p>
     */
    public function get maxVerticalScrollPosition():Number
    {
        if (!isNaN(_maxVerticalScrollPosition))
            return _maxVerticalScrollPosition;

        var m:Number = verticalScrollBar ?
                       verticalScrollBar.maxScrollPosition :
                       0;
        return m;
    }

    /**
     *  @private
     */
    public function set maxVerticalScrollPosition(value:Number):void
    {
        _maxVerticalScrollPosition = value;

        dispatchEvent(new Event("maxVerticalScrollPositionChanged"));
    }

    //----------------------------------
    //  scrollTipFunction
    //----------------------------------

    /**
     *  @private
     *  Storage for the scrollTipFunction property.
     */
    private var _scrollTipFunction:Function;

    [Bindable("scrollTipFunctionChanged")]
    [Inspectable(defaultValue="")]

    /**
     *  A function that computes the string to be displayed as the ScrollTip.
     *  This function is called if the <code>showScrollTips</code> property
     *  is set to <code>true</code> and the scroll thumb is being dragged.
     *  The function should return a String that used as a ScrollTip.
     *  The function is passed two parameters.
     *  The first is the <code>direction</code> of the scroll bar.
     *  The second is its <code>scrollPosition</code>, as the following example shows:
     *  
     *  <pre>
     *  function scrollTipFunction(direction:String, position:Number):String 
     *  {
     *    if (direction == "vertical") return myToolTips[position];
     *    else return "";
     *  }</pre>
     */
    public function get scrollTipFunction():Function
    {
        return _scrollTipFunction;
    }

    /**
     *  @private
     */
    public function set scrollTipFunction(value:Function):void
    {
        _scrollTipFunction = value;

        dispatchEvent(new Event("scrollTipFunctionChanged"));
    }

    //----------------------------------
    //  showScrollTips
    //----------------------------------

    [Inspectable(defaultValue="false")]

    /**
     *  A flag that indicates whether a tooltip should appear
     *  near the scroll thumb when it is being dragged.
     *  The default value is <code>false</code> to disable the tooltip.
     */
    public var showScrollTips:Boolean = false;

    //----------------------------------
    //  viewMetrics
    //----------------------------------

    /**
     *  An EdgeMetrics object taking into account the scroll bars,
     *  if visible.
     *
     *  @return EdgeMetrics object with the thickness, in pixels,
     *  of the left, top, right, and bottom edges.
     */
    public function get viewMetrics():EdgeMetrics
    {
        _viewMetrics = borderMetrics.clone();

        if (!horizontalScrollBar &&
            (horizontalScrollPolicy == ScrollPolicy.ON))
        {
            createHScrollBar(true);
            
            horizontalScrollBar.addEventListener(ScrollEvent.SCROLL, scrollHandler);
            horizontalScrollBar.addEventListener(ScrollEvent.SCROLL, scrollTipHandler);
            horizontalScrollBar.scrollPosition = _horizontalScrollPosition;

            invalidateDisplayList();
        }

        if (!verticalScrollBar && verticalScrollPolicy == ScrollPolicy.ON)
        {
            createVScrollBar(true);
            
            verticalScrollBar.addEventListener(ScrollEvent.SCROLL, scrollHandler);
            verticalScrollBar.addEventListener(ScrollEvent.SCROLL, scrollTipHandler);
            verticalScrollBar.scrollPosition = _verticalScrollPosition;
            
            invalidateDisplayList();
        }

        if (verticalScrollBar && verticalScrollBar.visible)
            _viewMetrics.right += verticalScrollBar.minWidth;

        if (horizontalScrollBar && horizontalScrollBar.visible)
            _viewMetrics.bottom += horizontalScrollBar.minHeight;

        return _viewMetrics;
    }

    //----------------------------------
    //  verticalScrollPosition
    //----------------------------------

    /**
     *  @private
     *  Storage for the verticalScrollPosition property.
     */
    mx_internal var _verticalScrollPosition:Number = 0;

    [Bindable("scroll")]
    [Bindable("viewChanged")]
    [Inspectable(defaultValue="0")]

    /**
     *  The offset into the content from the top edge.  This can
     *  be a pixel offset in some subclasses or some other metric
     *  like number of lines in a List or number of tiles in
     *  a TileList.
     * 
     *  @default 0
     */
    public function get verticalScrollPosition():Number
    {
        return _verticalScrollPosition;
    }

    /**
     *  @private
     */
    public function set verticalScrollPosition(value:Number):void
    {
        _verticalScrollPosition = value;

        if (verticalScrollBar)
            verticalScrollBar.scrollPosition = value;

        dispatchEvent(new Event("viewChanged"));
    }

    //----------------------------------
    //  verticalScrollPolicy
    //----------------------------------

    /**
     *  @private
     *  Storage for the verticalScrollPolicy property.
     */
    mx_internal var _verticalScrollPolicy:String = ScrollPolicy.AUTO;

    [Bindable("verticalScrollPolicyChanged")]
    [Inspectable(enumeration="off,on,auto", defaultValue="auto")]

    /**
     *  A property that indicates whether the vertical scroll bar is always on, always off,
     *  or automatically changes based on the parameters passed to the
     *  <code>setScrollBarProperties()</code> method.
     *  Allowed values are <code>ScrollPolicy.ON</code>,
     *  <code>ScrollPolicy.OFF</code>, and <code>ScrollPolicy.AUTO</code>.
     *  MXML values can be <code>"on"</code>, <code>"off"</code>,
     *  and <code>"auto"</code>.
     * 
     *  <p>Setting this property to <code>ScrollPolicy.OFF</code> for ListBase
     *  subclasses does not affect the <code>verticalScrollPosition</code>
     *  property; you can still scroll the contents programmatically.</p>
     *
     *  <p>Note that the policy can affect the measured size of the component
     *  If the policy is <code>ScrollPolicy.AUTO</code> the
     *  scrollbar is not factored in the measured size.  This is done to
     *  keep the layout from recalculating when the scrollbar appears.  If you
     *  know that you will have enough data for scrollbars you should set
     *  the policy to <code>ScrollPolicy.ON</code>.  If you
     *  don't know, you may need to set an explicit width or height on
     *  the component to allow for scrollbars to appear later.</p>
     *
     *  @default ScrollPolicy.AUTO
     */
    public function get verticalScrollPolicy():String
    {
        return _verticalScrollPolicy;
    }

    /**
     *  @private
     */
    public function set verticalScrollPolicy(value:String):void
    {
        var newPolicy:String = value.toLowerCase();

        if (_verticalScrollPolicy != newPolicy)
        {
            _verticalScrollPolicy = newPolicy;
            invalidateDisplayList();

            dispatchEvent(new Event("verticalScrollPolicyChanged"));
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Creates objects that are children of this ScrollControlBase,
     *  which in this case are the border and mask.
     *  Flex calls this method when the ScrollControlBase is first created.
     *  If a subclass overrides this method, the subclass should call
     *  the <code>super.createChildren()</code> method so that the logic
     *  in the <code>ScrollControlBase.createChildren()</code> method is executed.
     */
    override protected function createChildren():void
    {
        super.createChildren();

        createBorder();

        if (!maskShape)
        {
            maskShape = new FlexShape();
            maskShape.name = "mask";

            var g:Graphics = maskShape.graphics;
            g.beginFill(0xFFFFFF);
            g.drawRect(0, 0, 10, 10);
            g.endFill();

            addChild(maskShape);
        }

        maskShape.visible = false;
    }

    /**
     *  @private
     *  Sets the position and size of the scroll bars and content
     *  and adjusts the mask.
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        layoutChrome(unscaledWidth, unscaledHeight);

        var w:Number = unscaledWidth;
        var h:Number = unscaledHeight;

        invLayout = false;

        var vm:EdgeMetrics = _viewMetrics = viewMetrics;

        if (horizontalScrollBar && horizontalScrollBar.visible)
        {
            horizontalScrollBar.setActualSize(w - vm.left - vm.right,
                                              horizontalScrollBar.minHeight);
            horizontalScrollBar.move(vm.left,
                                     h - vm.bottom);

            horizontalScrollBar.enabled = enabled;
        }

        if (verticalScrollBar && verticalScrollBar.visible)
        {
            verticalScrollBar.setActualSize(verticalScrollBar.minWidth,
                                            h - vm.top - vm.bottom);
            verticalScrollBar.move(w - vm.right, vm.top);

            verticalScrollBar.enabled = enabled;
        }

        var mask:DisplayObject = maskShape;

        var wd:Number = w - vm.left - vm.right;
        var ht:Number = h - vm.top - vm.bottom;

        mask.width = wd < 0 ? 0 : wd;
        mask.height = ht < 0 ? 0 : ht;

        mask.x = vm.left;
        mask.y = vm.top;
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        var allStyles:Boolean = (styleProp == null || styleProp == "styleName");

        super.styleChanged(styleProp);

        if (allStyles || styleProp == "horizontalScrollBarStyleName")
        {
            if (horizontalScrollBar)
            {
                var horizontalScrollBarStyleName:String =
                    getStyle("horizontalScrollBarStyleName");
                horizontalScrollBar.styleName = horizontalScrollBarStyleName;
            }
        }

        if (allStyles || styleProp == "verticalScrollBarStyleName")
        {
            if (verticalScrollBar)
            {
                var verticalScrollBarStyleName:String =
                    getStyle("verticalScrollBarStyleName");
                verticalScrollBar.styleName = verticalScrollBarStyleName;
            }
        }
        
        // Replace the borderSkin
 		if (allStyles || styleProp == "borderSkin")
 		{
 			if (border)
 			{
 				removeChild(DisplayObject(border));
 				border = null;
 				createBorder();
 			}
 		}
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Responds to size changes by setting the positions and sizes
     *  of this control's borders.
     *
     *  <p>The <code>ScrollControlBase.layoutChrome()</code> method sets the
     *  position and size of the ScrollControlBase's border.
     *  In every subclass of ScrollControlBase, the subclass's <code>layoutChrome()</code>
     *  method should call the <code>super.layoutChrome()</code> method,
     *  so that the border is positioned properly.</p>
     *
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.
     */
    protected function layoutChrome(unscaledWidth:Number,
                                    unscaledHeight:Number):void
    {
        // Border covers the whole thing.
        if (border)
        {
            border.move(0, 0);
            border.setActualSize(unscaledWidth, unscaledHeight);
        }
    }

    /**
     *  Creates the border for this component.
     *  Normally the border is determined by the
     *  <code>borderStyle</code> and <code>borderSkin</code> styles.  
     *  It must set the border property to the instance
     *  of the border.
     */
    protected function createBorder():void
    {
        if (!border && isBorderNeeded())
        {
            var borderClass:Class = getStyle("borderSkin");

			if (borderClass != null)
			{
	            border = new borderClass();
	
	            if (border is IUIComponent)
	                IUIComponent(border).enabled = enabled;
	            if (border is ISimpleStyleClient)
	                ISimpleStyleClient(border).styleName = this;
	
	            // Add the border behind all the children.
	            addChildAt(DisplayObject(border), 0);
	
	            invalidateDisplayList();
   			}
        }
    }

    /**
     *  Returns <code>true</code> if a border is needed for this component based
     *  on the borderStyle and whether or not there is a background
     *  for the component.
     */
    private function isBorderNeeded():Boolean
    {
        //trace("isBorderNeeded",this,"ms",getStyle("mouseShield"),"borderStyle",getStyle("borderStyle"));

        var v:Object = getStyle("borderStyle");
        if (v)
        {
            // If borderStyle is "none", then only create a border if the mouseShield style is true
            // (meaning that there is a mouse event listener on this view). We don't create a border
            // if our parent's mouseShieldChildren style is true.
            if ((v != "none") || (v == "none" && getStyle("mouseShield")))
            {
                return true;
            }
        }

        v = getStyle("backgroundColor");
        if (v !== null && v !== "")
            return true;

        v = getStyle("backgroundImage");
        return v != null && v != "";
    }

    /**
     *  Causes the ScrollControlBase to show or hide scrollbars based
     *  on the parameters passed in. If a TextArea can only show 100 pixels
     *  across and 5 lines of text, but the actual text to display is 200 pixels wide
     *  and 30 lines of text, then the <code>setScrollBarProperties()</code> method 
     *  is called as
     *  <code>setScrollBarProperties(200, 100, 30, 5)</code>.
     *
     *  @param totalColumns The number of horizontal units that need to be displayed.
     *
     *  @param visibleColumns The number of horizontal units that can be displayed at one time.
     *
     *  @param totalRows The number of vertical units that need to be displayed.
     *
     *  @param visibleRows The number of vertical units that can be displayed at one time
     */
    protected function setScrollBarProperties(totalColumns:int, visibleColumns:int,
                                        totalRows:int, visibleRows:int):void
    {
        var horizontalScrollPolicy:String = this.horizontalScrollPolicy;
        var verticalScrollPolicy:String = this.verticalScrollPolicy;
        
        var shouldBeVisible:Boolean;

        scrollAreaChanged = false;

		// scrollbars when the scrollPolicy is ON are calculated in viewMetrics
        if (horizontalScrollPolicy == ScrollPolicy.ON ||
        	(visibleColumns < totalColumns && totalColumns > 0 &&
            (horizontalScrollPolicy == ScrollPolicy.AUTO)))
        {
            // We need a horizontal scrollBar.
            
            // Nope, need to add it.
            if (!horizontalScrollBar)
            {
                createHScrollBar(false);
                            
		        horizontalScrollBar.addEventListener(ScrollEvent.SCROLL, scrollHandler);
		        horizontalScrollBar.addEventListener(ScrollEvent.SCROLL, scrollTipHandler);
		        
		        horizontalScrollBar.scrollPosition = _horizontalScrollPosition;
            }
            
            shouldBeVisible = roomForScrollBar(horizontalScrollBar, unscaledWidth, unscaledHeight);
            	
        	if (shouldBeVisible != horizontalScrollBar.visible)
        	{
        		horizontalScrollBar.visible = shouldBeVisible;
        		scrollAreaChanged = true;
        	}

            if (horizontalScrollBar && horizontalScrollBar.visible && (numberOfCols != totalColumns ||
                viewableColumns != visibleColumns || scrollAreaChanged))
            {
                horizontalScrollBar.setScrollProperties(
                    visibleColumns, 0, totalColumns - visibleColumns);

                if (horizontalScrollBar.scrollPosition != _horizontalScrollPosition)
                    horizontalScrollBar.scrollPosition = _horizontalScrollPosition;

                viewableColumns = visibleColumns;
                numberOfCols = totalColumns;
            }
        }
        else if ((horizontalScrollPolicy == ScrollPolicy.AUTO ||
                  horizontalScrollPolicy == ScrollPolicy.OFF) &&
                 horizontalScrollBar && horizontalScrollBar.visible)
        {
            // We need to remove this scrollBar.
            horizontalScrollPosition = 0;
            horizontalScrollBar.setScrollProperties(
                 visibleColumns, 0, 0);
            horizontalScrollBar.visible = false;
            viewableColumns = NaN;
            scrollAreaChanged = true;
        }

        if (verticalScrollPolicy == ScrollPolicy.ON || 
        	(visibleRows < totalRows && totalRows > 0 &&
            (verticalScrollPolicy == ScrollPolicy.AUTO)))
        {
            // We need a vertical scrollBar. Does it exist?
            if (!verticalScrollBar)
            {
                // No it doesn't, and we're allowed to add it.
                createVScrollBar(false);
                
                verticalScrollBar.addEventListener(ScrollEvent.SCROLL, scrollHandler);
            	verticalScrollBar.addEventListener(ScrollEvent.SCROLL, scrollTipHandler);
            	
            	verticalScrollBar.scrollPosition = _verticalScrollPosition;
            }
            
            shouldBeVisible = roomForScrollBar(verticalScrollBar, unscaledWidth, unscaledHeight);
            	
        	if (shouldBeVisible != verticalScrollBar.visible)
        	{
        		verticalScrollBar.visible = shouldBeVisible;
        		scrollAreaChanged = true;
        	}

            if (verticalScrollBar && verticalScrollBar.visible && (numberOfRows != totalRows ||
                (viewableRows != visibleRows) || scrollAreaChanged))
            {
                verticalScrollBar.setScrollProperties(
                    visibleRows, 0, totalRows - visibleRows);

                if (verticalScrollBar.scrollPosition != _verticalScrollPosition)
                    verticalScrollBar.scrollPosition = _verticalScrollPosition;

                viewableRows = visibleRows;
                numberOfRows = totalRows;
            }
        }
        else if ((verticalScrollPolicy == ScrollPolicy.AUTO ||
                  verticalScrollPolicy == ScrollPolicy.OFF) &&
                 verticalScrollBar && verticalScrollBar.visible)
        {
            verticalScrollPosition = 0;
            verticalScrollBar.setScrollProperties(
                    visibleRows, 0, 0);
            verticalScrollBar.visible = false;
            viewableRows = NaN;
            scrollAreaChanged = true;
        }

        // Now, if any scrollBar came into or left existence,
        // it's possible that the content is occluded or revealed enough
        // that the other scrollBar needs to appear or go away.
        if (scrollAreaChanged)
        {
            // This is for content that conforms to the (discrete)
            // scrollable interface. It just falls through on analog content.
            // (I hope.)
            updateDisplayList(unscaledWidth, unscaledHeight);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Private methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Create the HScrollBar
     */
    private function createHScrollBar(visible:Boolean):ScrollBar
    {      
        horizontalScrollBar =  new HScrollBar();
        horizontalScrollBar.visible = visible;
        horizontalScrollBar.enabled = enabled;

        var horizontalScrollBarStyleName:String =
            getStyle("horizontalScrollBarStyleName");
        horizontalScrollBar.styleName = horizontalScrollBarStyleName;

        // Place the new scrollbar in front of other children
        // in the ScrollControlBase
        addChild(horizontalScrollBar);

        horizontalScrollBar.validateNow();

        return horizontalScrollBar;
    }

    /**
     *  @private
     *  Create the VScrollBar
     */
    private function createVScrollBar(visible:Boolean):ScrollBar
    {
        verticalScrollBar = new VScrollBar();
        verticalScrollBar.visible = visible;
        verticalScrollBar.enabled = enabled;

        var verticalScrollBarStyleName:String =
            getStyle("verticalScrollBarStyleName");
        verticalScrollBar.styleName = verticalScrollBarStyleName;
        
        // Place the new scrollbar in front of other children
        // in the ScrollControlBase
        addChild(verticalScrollBar);
        
        //need verticalScrollBar.validateNow()?
        
        return verticalScrollBar;
    }

    /**
     *  Determines if there is enough space in this component to display 
     *  a given scrollbar.
     *
     *  @param bar The scrollbar
     *
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.
     *
     *  @return Returns true if there is enough space for a scrollbar.
     */
    
    protected function roomForScrollBar(bar:ScrollBar, 
    		unscaledWidth:Number, unscaledHeight:Number):Boolean
    {
    	var bm:EdgeMetrics = borderMetrics;
    	return (unscaledWidth >= bar.minWidth + bm.left + bm.right) &&
        	   (unscaledHeight >= bar.minHeight + bm.top + bm.bottom)
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  Default event handler for the <code>scroll</code> event.
     *
     *  @param event The event object.
     */
    protected function scrollHandler(event:Event):void
    {
        // TextField.scroll bubbles so you might see it here
        if (event is ScrollEvent)
        {
            var scrollBar:ScrollBar = ScrollBar(event.target);

            var pos:Number = scrollBar.scrollPosition;

            var prop:QName;
            if (scrollBar == verticalScrollBar)
                prop = new QName(mx_internal, "_verticalScrollPosition");
            else if (scrollBar == horizontalScrollBar)
                prop = new QName(mx_internal, "_horizontalScrollPosition");

            dispatchEvent(event);

            if (prop)
                this[prop] = pos;
        }
    }

    /**
     *  @private
     */
    private function scrollTipHandler(event:Event):void
    {
        // TextField.scroll bubbles so you might see it here
        if (event is ScrollEvent)
        {
            if (!showScrollTips)
                return;

            if (ScrollEvent(event).detail == ScrollEventDetail.THUMB_POSITION)
            {
                if (scrollTip)
                {
                    systemManager.toolTipChildren.removeChild(scrollTip);
                    scrollTip = null;
                    ToolTipManager.enabled = oldTTMEnabled;
                }
            }
            else if (ScrollEvent(event).detail == ScrollEventDetail.THUMB_TRACK)
            {
                var scrollBar:ScrollBar = ScrollBar(event.target);
                var isVertical:Boolean = scrollBar == verticalScrollBar;
                var dir:String = isVertical ? "vertical" : "horizontal";
                var pos:Number = scrollBar.scrollPosition;

                if (!scrollTip)
                {
                    scrollTip = new ToolTip();
                    systemManager.toolTipChildren.addChild(scrollTip);
                    scrollThumbMidPoint = scrollBar.scrollThumb.height / 2;
                    oldTTMEnabled = ToolTipManager.enabled;
                    ToolTipManager.enabled = false;
                }

                var tip:String = pos.toString();

                if (_scrollTipFunction != null)
                    tip = _scrollTipFunction(dir, pos);

                if (tip == "")
                {
                    scrollTip.visible = false;
                }
                else
                {
                    scrollTip.text = tip;

                    ToolTipManager.sizeTip(scrollTip);

                    var pt:Point = new Point();
                    if (isVertical)
                    {
                        pt.x = -3 - scrollTip.width;
                        pt.y = scrollBar.scrollThumb.y + scrollThumbMidPoint -
                               scrollTip.height / 2;
                    }
                    else
                    {
                        // The scrollbar is rotated so we kind of reverse things
                        // around with width and height.
                        pt.x = -3 - scrollTip.height;
                        pt.y = scrollBar.scrollThumb.y + scrollThumbMidPoint -
                               scrollTip.width / 2;
                    }
                    pt = scrollBar.localToGlobal(pt);
                    scrollTip.move(pt.x, pt.y);

                    scrollTip.visible = true;
                }
            }
        }
    }

    /**
     *  Event handler for the mouse wheel scroll event.
     *
     *  @param event The event object.
     */
    protected function mouseWheelHandler(event:MouseEvent):void
    {
        // If this Container has a vertical scrollbar,
        // then handle the event and prevent further bubbling.
        if (verticalScrollBar && verticalScrollBar.visible)
        {
            event.stopPropagation();

            var scrollDirection:int = event.delta <= 0 ? 1 : -1;

            // Make sure we scroll by at least one line
            var scrollAmount:Number = Math.max(Math.abs(event.delta),
                                               verticalScrollBar.lineScrollSize);

            // Multiply by 3 to make scrolling a little faster
            var oldPosition:Number = verticalScrollPosition;
            verticalScrollPosition += 3 * scrollAmount * scrollDirection;

            var scrollEvent:ScrollEvent = new ScrollEvent(ScrollEvent.SCROLL);
            scrollEvent.direction = ScrollEventDirection.VERTICAL;
            scrollEvent.position = verticalScrollPosition;
            scrollEvent.delta = verticalScrollPosition - oldPosition;
            dispatchEvent(scrollEvent);
        }
    }
	
    /**
     *  @private
     */
	mx_internal function get scroll_verticalScrollBar():ScrollBar
	{
		return verticalScrollBar;
	}

    /**
     *  @private
     */
	mx_internal function get scroll_horizontalScrollBar():ScrollBar
	{
		return horizontalScrollBar;
	}
}
}
