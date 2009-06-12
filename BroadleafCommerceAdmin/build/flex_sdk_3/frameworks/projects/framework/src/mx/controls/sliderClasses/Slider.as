////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls.sliderClasses
{

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.utils.getTimer;
import mx.core.FlexVersion;
import mx.core.IFlexDisplayObject;
import mx.core.mx_internal;
import mx.core.UIComponent;
import mx.effects.Tween;
import mx.events.FlexEvent;
import mx.events.SliderEvent;
import mx.events.SliderEventClickTarget;
import mx.formatters.NumberFormatter;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.styles.ISimpleStyleClient;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the slider changes value due to mouse or keyboard interaction.
 *
 *  <p>If the <code>liveDragging</code> property is <code>true</code>,
 *  the event is dispatched continuously as the user moves the thumb.
 *  If <code>liveDragging</code> is <code>false</code>,
 *  the event is dispatched when the user releases the slider thumb.</p>
 *
 *  @eventType mx.events.SliderEvent.CHANGE
 */
[Event(name="change", type="mx.events.SliderEvent")]

/**
 *  Dispatched when the slider's thumb is pressed and then moved by the mouse.
 *  This event is always preceded by a <code>thumbPress</code> event.
 *  @eventType mx.events.SliderEvent.THUMB_DRAG
 */
[Event(name="thumbDrag", type="mx.events.SliderEvent")]

/**
 *  Dispatched when the slider's thumb is pressed, meaning
 *  the user presses the mouse button over the thumb.
 *
 *  @eventType mx.events.SliderEvent.THUMB_PRESS
 */
[Event(name="thumbPress", type="mx.events.SliderEvent")]

/**
 *  Dispatched when the slider's thumb is released, 
 *  meaning the user releases the mouse button after 
 *  a <code>thumbPress</code> event.
 *
 *  @eventType mx.events.SliderEvent.THUMB_RELEASE
 */
[Event(name="thumbRelease", type="mx.events.SliderEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../../styles/metadata/FillStyles.as";

/**
 *  The color of the black section of the border. 
 *  
 *  @default 0x919999
 */
[Style(name="borderColor", type="uint", format="Color", inherit="no")]

/**
 *  Invert the direction of the thumbs. 
 *  If <code>true</code>, the thumbs will be flipped.
 *  
 *  @default false
 */
[Style(name="invertThumbDirection", type="Boolean", inherit="no")]

/**
 *  The y-position offset (if direction is horizontal)
 *  or x-position offset (if direction is vertical)
 *  of the labels relative to the track.
 *
  *  @default -10
 */
[Style(name="labelOffset", type="Number", format="Length", inherit="no")]

/**
 *  The name of the style to use for the slider label.  
 *
 *  @default undefined
 */
[Style(name="labelStyleName", type="String", inherit="no")]

/**
 *  Duration in milliseconds for the sliding animation
 *  when you click on the track to move a thumb.
 *
 *  @default 300
 */
[Style(name="slideDuration", type="Number", format="Time", inherit="no")]

/**
 *  Tweening function used by the sliding animation
 *  when you click on the track to move a thumb.
 *
 *  @default undefined
 */
[Style(name="slideEasingFunction", type="Function", inherit="no")]

/**
 *  The y-position offset (if direction is horizontal)
 *  or x-position offset (if direction is vertical)
 *  of the thumb relative to the track.
 *
 *  @default 0
 */
[Style(name="thumbOffset", type="Number", format="Length", inherit="no")]

/**
 *  The color of the tick marks.
 *  Can be a hex color value or the string name of a known color.
 *
 *  @default 0x6F7777.
 */
[Style(name="tickColor", type="uint", format="Color", inherit="no")]

/**
 *  The length in pixels of the tick marks.
 *  If <code>direction</code> is <code>Direction.HORIZONTAL</code>,
 *  then adjust the height of the tick marks.
 *  If <code>direction</code> is <code>Direction.VERTICAL</code>,
 *  then adjust the width.
 *
 *  @default 3
 */
[Style(name="tickLength", type="Number", format="Length", inherit="no")]

/**
 *  The y-position offset (if direction is horizontal)
 *  or x-position offset (if direction is vertical)
 *  of the tick marks relative to the track.
 *
 *  @default -6
 */
[Style(name="tickOffset", type="Number", format="Length", inherit="no")]

/**
 *  The thickness in pixels of the tick marks.
 *  If direction is horizontal,
 *  then adjust the width of the tick marks.
 *  If direction is vertical,
 *  then adjust the height.
 *
 *  @default 1
 */
[Style(name="tickThickness", type="Number", format="Length", inherit="no")]

/**
 *  The colors of the track, as an array of two colors.
 *  You can use the same color twice for a solid track color.
 *
 *  <p>You use this property along with the <code>fillAlphas</code> 
 *  property. Typically you set <code>fillAlphas</code> to [ 1.0, 1.0 ] 
 *  when setting <code>trackColors</code>.</p>
 *
 *  @default [ 0xE7E7E7, 0xE7E7E7 ]
 */
[Style(name="trackColors", type="Array", arrayType="uint", format="Color", inherit="no")]

/**
 *  Specifies whether to enable track highlighting between thumbs
 *  (or a single thumb and the beginning of the track).
 *
 *  @default false
 */
[Style(name="showTrackHighlight", type="Boolean", inherit="no")]

/**
 *  The size of the track margins, in pixels.
 *  If <code>undefined</code>, then the track margins will be determined
 *  by the length of the first and last labels.
 *  If given a value, Flex attempts to fit the labels in the available space.
 *
 *  @default undefined
 */
[Style(name="trackMargin", type="Number", format="Length", inherit="no")]

/**
 *  The name of the style declaration to use for the data tip.
 *
 *  @default undefined
 */
[Style(name="dataTipStyleName", type="String", inherit="no")]

/**
 *  The offset, in pixels, of the data tip relative to the thumb.
 *  Used in combination with the <code>dataTipPlacement</code>
 *  style property of the HSlider and VSlider controls.
 *
 *  @default 16
 */
[Style(name="dataTipOffset", type="Number", format="Length", inherit="no")]

/**
 *  Number of decimal places to use for the data tip text.
 *  A value of 0 means to round all values to an integer.
 *
 *  @default 2
 */
[Style(name="dataTipPrecision", type="int", inherit="no")]

/**
 *  The default skin for the slider thumb.
 * 
 *  @default SliderThumbSkin
 */
[Style(name="thumbSkin", type="Class", inherit="no", states="up, over, down, disabled")]

/**
 *  The skin for the slider thumb up state.
 *
 *  @default SliderThumbSkin
 */
[Style(name="thumbUpSkin", type="Class", inherit="no")]

/**
 *  The skin for the slider thumb over state.
 *
 *  @default SliderThumbSkin
 */
[Style(name="thumbOverSkin", type="Class", inherit="no")]

/**
 *  The skin for the slider thumb down state.
 *
 *  @default SliderThumbSkin
 */
[Style(name="thumbDownSkin", type="Class", inherit="no")]

/**
 *  The skin for the slider thumb disabled state.
 *
 *  @default SliderThumbSkin
 */
[Style(name="thumbDisabledSkin", type="Class", inherit="no")]

/**
 *  The skin for the slider track when it is selected.
 */
[Style(name="trackHighlightSkin", type="Class", inherit="no")]

/**
 *  The skin for the slider track.
 */
[Style(name="trackSkin", type="Class", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.SliderAccImpl")]

[ResourceBundle("SharedResources")]

/**
 *  The Slider class is the base class for the Flex slider controls.
 *  The slider controls let users select a value by moving a slider thumb 
 *  between the end points of the slider
 *  track. The current value of the slider is determined by 
 *  the relative location of the thumb between the
 *  end points of the slider, corresponding to the slider's minimum and maximum values.
 *  The Slider class is subclassed by HSlider and VSlider.
 *
 *  @mxml
 *  
 *  <p>The Slider class cannot be used as an MXML tag. Use the <code>&lt;mx:HSlider&gt;</code> 
 *  and <code>&lt;mx:VSlider&gt;</code> tags instead. However, the Slider class does define tag 
 *  attributes used by the <code>&lt;mx:HSlider&gt;</code> and <code>&lt;mx:VSlider&gt;</code> tags. </p>
 *
 *  <p>The Slider class inherits all of the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:<i>tagname</i>
 *    <strong>Properties</strong>
 *    allowThumbOverlap="false|true"
 *    allowTrackClick="true|false"
 *    dataTipFormatFunction="undefined"
 *    direction="horizontal|vertical"
 *    labels="undefined"
 *    liveDragging="false|true"
 *    maximum="10"
 *    minimum="0"
 *    showDataTip="true|false"
 *    sliderDataTipClass="sliderDataTip"
 *    sliderThumbClass="SliderThumb"
 *    snapInterval="0"
 *    thumbCount="1"
 *    tickInterval="0"
 *    tickValues="undefined"
 *    value="<i>The value of the minimum property.</i>"
 * 
 *    <strong>Styles</strong>
 *    borderColor="0x919999"
 *    dataTipOffset="16"
 *    dataTipPrecision="2"
 *    dataTipStyleName="undefined"
 *    fillAlphas="[0.6, 0.4, 0.75, 0.65]"
 *    fillColors="[0xFFFFFF, 0xCCCCCC, 0xFFFFFF, 0xEEEEEE;]"
 *    labelOffset="-10"
 *    labelStyleName="undefined"
 *    showTrackHighlight="false"
 *    slideDuration="300"
 *    slideEasingFunction="undefined"
 *    thumbDisabledSkin="SliderThumbSkin"
 *    thumbDownSkin="SliderThumbSkin"
 *    thumbOffset="0"
 *    thumbOverSkin="SliderThumbSkin"
 *    thumbUpSkin="SliderThumbSkin"
 *    tickColor="0x6F7777"
 *    tickLength="3"
 *    tickOffset="-6"
 *    tickThickness="1"
 *    trackColors="[ 0xEEEEEE, 0xFFFFFF ]"
 *    tracHighlightSkin="SliderHighlightSkin"
 *    trackMargin="undefined"
 *    trackSkin="SliderTrackSkin"
 *  
 *    <strong>Events</strong>
 *    change="<i>No default</i>"
 *    thumbDrag="<i>No default</i>"
 *    thumbPress="<i>No default</i>"
 *    thumbRelease="<i>No default</i>"
 *  /&gt;
 *  </pre>
 */
public class Slider extends UIComponent
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by SliderAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Slider()
    {
        super();

        tabChildren = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var track:IFlexDisplayObject;

    /**
     *  @private
     */
    private var thumbs:UIComponent;

    /**
     *  @private
     */
    private var thumbsChanged:Boolean = true;

    /**
     *  @private
     */
    private var ticks:UIComponent;

    /**
     *  @private
     */
    private var ticksChanged:Boolean = false;

    /**
     *  @private
     */
    private var labelObjects:UIComponent;

    /**
     *  @private
     */
    private var highlightTrack:IFlexDisplayObject;

    /**
     *  @private
     */
    mx_internal var innerSlider:UIComponent;

    /**
     *  @private
     */
    private var trackHitArea:UIComponent;
    
    /**
     *  @private
     */
    mx_internal var dataTip:SliderDataTip;

    /**
     *  @private
     */
    private var trackHighlightChanged:Boolean = true;

    /**
     *  @private
     */
    private var initValues:Boolean = true; // Always initValues at startup

    /**
     *  @private
     */
    private var dataFormatter:NumberFormatter;

    /**
     *  @private
     */
    private var interactionClickTarget:String;

    /**
     *  @private
     */
    private var labelStyleChanged:Boolean = false;

    /**
    *  @private
    *  is the last interaction from the keyboard?
    */
    mx_internal var keyInteraction:Boolean = false;
    
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
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            return super.baselinePosition;

        if (!validateBaselinePosition())
            return NaN;

        return int(0.75 * height);
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  @private
     */
    private var _enabled:Boolean;

    /**
     *  @private
     */
    private var enabledChanged:Boolean = false;

    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]

    /**
     *  @private
     */
    override public function get enabled():Boolean
    {
        return _enabled;
    }

    /**
     *  @private
     */
    override public function set enabled(value:Boolean):void
    {
        _enabled = value;
        enabledChanged = true;

        invalidateProperties();
    }

    /**
     *  @private
     */
    private var _tabIndex:Number;
    
    /**
     *  @private
     */
    private var tabIndexChanged:Boolean;
    
    /**
     *  @private
     */
    override public function set tabIndex(value:int):void
    {
        super.tabIndex = value;
        _tabIndex = value;
        
        tabIndexChanged = true;
        invalidateProperties();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  allowThumbOverlap
    //----------------------------------

    [Inspectable(defaultValue="false")]

    /**
     *  If set to <code>false</code>, then each thumb can only be moved to the edge of
     *  the adjacent thumb.
     *  If <code>true</code>, then each thumb can be moved to any position on the track.
     *
     *  @default false
     */
    public var allowThumbOverlap:Boolean = false;

    //----------------------------------
    //  allowTrackClick
    //----------------------------------

    [Inspectable(defaultValue="true")]

    /**
     *  Specifies whether clicking on the track will move the slider thumb.
     *
     *  @default true
     */
    public var allowTrackClick:Boolean = true;

    //----------------------------------
    //  dataTipFormatFunction
    //----------------------------------

    /**
     *  @private
     */
    private var _dataTipFormatFunction:Function;

    /**
     *  Callback function that formats the data tip text.
     *  The function takes a single Number as an argument
     *  and returns a formatted String.
     *
     *  <p>The function has the following signature:</p>
     *  <pre>
     *  funcName(value:Number):String
     *  </pre>
     *
     *  <p>The following example prefixes the data tip text with a dollar sign and 
     *  formats the text using the <code>dataTipPrecision</code> 
     *  of a Slider Control named 'slide': </p>
     *
     *  <pre>
     *  import mx.formatters.NumberBase;
     *  function myDataTipFormatter(value:Number):String { 
     *      var dataFormatter:NumberBase = new NumberBase(".", ",", ".", ""); 
     *      return   "$ " + dataFormatter.formatPrecision(String(value), slide.getStyle("dataTipPrecision")); 
     *  }
     *  </pre>
     *
     *  @default undefined   
     */
    public function get dataTipFormatFunction():Function
    {
        return _dataTipFormatFunction;
    }

    /**
     *  @private
     */
    public function set dataTipFormatFunction(value:Function):void
    {
        _dataTipFormatFunction = value;
    }

    //----------------------------------
    //  direction
    //----------------------------------

    /**
     *  @private
     */
    private var _direction:String = SliderDirection.HORIZONTAL;

    /**
     *  @private
     */
    private var directionChanged:Boolean = false;

    [Inspectable(defaultValue="horizontal")]

    /**
     *  The orientation of the slider control.
     *  Valid values in MXML are <code>"horizontal"</code> or <code>"vertical"</code>.
     *
     *  <p>In ActionScript, you use the following constants
     *  to set this property:
     *  <code>SliderDirection.VERTICAL</code> and
     *  <code>SliderDirection.HORIZONTAL</code>.</p>
     *
     *  The HSlider and VSlider controls set this property for you;
     *  do not set it when using those controls.
     *
     *  @default SliderDirection.HORIZONTAL
     *  @see mx.controls.sliderClasses.SliderDirection
     */
    public function get direction():String
    {
        return _direction;
    }

    /**
     *  @private
     */
    public function set direction(value:String):void
    {
        _direction = value;
        directionChanged = true;

        invalidateProperties();
        invalidateSize();
        invalidateDisplayList();
    }

    //----------------------------------
    //  labels
    //----------------------------------

    /**
     *  @private
     */
    private var _labels:Array = [];

    /**
     *  @private
     */
    private var labelsChanged:Boolean = false;

    [Inspectable(category="General", arrayType="String", defaultValue="undefined")]

    /**
     *  An array of strings used for the slider labels.
     *  Flex positions the labels at the beginning of the track,
     *  and spaces them evenly between the beginning of the track
     *  and the end of the track.
     *
     *  <p>For example, if the array contains three items,
     *  the first item is placed at the beginning of the track,
     *  the second item in the middle, and the last item
     *  at the end of the track.</p>
     *
     *  <p>If only one label is specified, it is placed at the
     *  beginning of the track.
     *  By default, labels are placed above the tick marks
     *  (if present) or above  the track.
     *  To align the labels with the tick marks, make sure that
     *  the number of tick marks is equal to the number of labels.</p>
     *
     *  @default undefined
     */
    public function get labels():Array
    {
        return _labels;
    }

    /**
     *  @private
     */
    public function set labels(value:Array):void
    {
        _labels = value;
        labelsChanged = true;

        invalidateProperties();
        invalidateSize();
        invalidateDisplayList();
    }

    //----------------------------------
    //  liveDragging
    //----------------------------------

    [Inspectable(category="General", defaultValue="false")]

    /**
     *  Specifies whether live dragging is enabled for the slider.
     *  If <code>false</code>, Flex sets the <code>value</code> and
     *  <code>values</code> properties and dispatches the <code>change</code>
     *  event when the user stops dragging the slider thumb.
     *  If <code>true</code>,  Flex sets the <code>value</code> and
     *  <code>values</code> properties and dispatches the <code>change</code>
     *  event continuously as the user moves the thumb.
     *
     *  @default false
     */
    public var liveDragging:Boolean = false;

    //----------------------------------
    //  maximum
    //----------------------------------

    /**
     *  @private
     *  Storage for the maximum property.
     */
    private var _maximum:Number = 10;

    [Inspectable(category="General", defaultValue="10")]

    /**
     *  The maximum allowed value on the slider.
     *
     *  @default  10
     */
    public function get maximum():Number
    {
        return _maximum;
    }

    /**
     *  @private
     */
    public function set maximum(value:Number):void
    {
        _maximum = value;
        ticksChanged = true;
        if (!initValues)
            valuesChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    //----------------------------------
    //  minimum
    //----------------------------------

    /**
     *  @private
     *  Storage for the minimum property.
     */
    private var _minimum:Number = 0;

    /**
     *  @private
     */
    private var minimumSet:Boolean = false;

    [Inspectable(category="General", defaultValue="0")]

    /**
     *  The minimum allowed value on the slider control.
     *
     *  @default 0
     */
    public function get minimum():Number
    {
        return _minimum;
    }

    /**
     *  @private
     */
    public function set minimum(value:Number):void
    {
        _minimum = value;
        ticksChanged = true;
        
        if (!initValues)
            valuesChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    //----------------------------------
    //  showDataTip
    //----------------------------------

    [Inspectable(category="General", defaultValue="true")]

    /**
     *  If set to <code>true</code>, show a data tip during user interaction
     *  containing the current value of the slider.
     *
     *  @default true
     */
    public var showDataTip:Boolean = true;

    //----------------------------------
    //  sliderThumbClass
    //----------------------------------

    /**
     *  @private
     */
    private var _thumbClass:Class = SliderThumb;

    /**
     *  A reference to the class to use for each thumb.
     *
     *  @default SliderThumb
     */
    public function get sliderThumbClass():Class
    {
        return _thumbClass;
    }

    /**
     *  @private
     */
    public function set sliderThumbClass(value:Class):void
    {
        _thumbClass = value;
        thumbsChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    //----------------------------------
    //  sliderDataTipClass
    //----------------------------------

    /**
     *  @private
     */
    private var _sliderDataTipClass:Class = SliderDataTip;

    /**
     *  A reference to the class to use for the data tip.
     *
     *  @default SliderDataTip
     */
    public function get sliderDataTipClass():Class
    {
        return _sliderDataTipClass;
    }

    /**
     *  @private
     */
    public function set sliderDataTipClass(value:Class):void
    {
        _sliderDataTipClass = value;

        invalidateProperties();
    }

    //----------------------------------
    //  snapInterval
    //----------------------------------

    /**
     *  @private
     */
    private var _snapInterval:Number = 0;

    /**
     *  @private
     */
    private var snapIntervalPrecision:int = -1;

    /**
     *  @private
     */
    private var snapIntervalChanged:Boolean = false;

    [Inspectable(category="General", defaultValue="0")]

    /**
     *  Specifies the increment value of the slider thumb
     *  as the user moves the thumb.
     *  For example, if <code>snapInterval</code> is 2,
     *  the <code>minimum</code> value is 0,
     *  and the <code>maximum</code> value is 10,
     *  the thumb snaps to the values 0, 2, 4, 6, 8, and 10
     *  as the user move the thumb.
     *  A value of 0, means that the slider moves continuously
     *  between the <code>minimum</code> and <code>maximum</code> values.
     *
     *  @default 0
     */
    public function get snapInterval():Number
    {
        return _snapInterval;
    }

    /**
     *  @private
     */
    public function set snapInterval(value:Number):void
    {
        _snapInterval = value;

        var parts:Array = (new String(1 + value)).split(".");
        if (parts.length == 2)
            snapIntervalPrecision = parts[1].length;
        else
            snapIntervalPrecision = -1;
            
        if (!isNaN(value) && value != 0)
        {
            snapIntervalChanged = true;

            invalidateProperties();
            invalidateDisplayList();
        }
    }

    //----------------------------------
    //  thumbCount
    //----------------------------------

    /**
     *  @private
     *  Storage for the thumbCount property.
     */
    private var _thumbCount:int = 1;

    [Inspectable(category="General", defaultValue="1")]

    /**
     *  The number of thumbs allowed on the slider.
     *  Possible values are 1 or 2.
     *  If set to 1, then the <code>value</code> property contains
     *  the current value of the slider.
     *  If set to 2, then the <code>values</code> property contains
     *  an array of values representing the value for each thumb.
     *
     *  @default 1
     */
    public function get thumbCount():int
    {
        return _thumbCount;
    }

    /**
     *  @private
     */
    public function set thumbCount(value:int):void
    {
        var numThumbs:int = (value > 2) ? 2 : value;
        numThumbs = value < 1 ? 1 : value;

        if (numThumbs != _thumbCount)
        {
            _thumbCount =  numThumbs;
            thumbsChanged = true;

            initValues = true;

            invalidateProperties();
            invalidateDisplayList();
        }
    }

    //----------------------------------
    //  thumbStyleFilters
    //----------------------------------

    /**
     *  Set of styles to pass from the Slider to the thumbs.
     *  @see mx.styles.StyleProxy
     */
    protected function get thumbStyleFilters():Object
    {
        return null;
    }
    //----------------------------------
    //  tickInterval
    //----------------------------------

    /**
     *  @private
     */
    private var _tickInterval:Number = 0;

    [Inspectable(category="General", defaultValue="0")]

    /**
     *  The spacing of the tick marks relative to the <code>maximum</code> value
     *  of the control.
     *  Flex displays tick marks whenever you set the <code>tickInterval</code>
     *  property to a nonzero value.
     *
     *  <p>For example, if <code>tickInterval</code> is 1 and
     *  <code>maximum</code> is 10,  then a tick mark is placed at each
     *  1/10th interval along the slider.
     *  A value of 0 shows no tick marks. If the <code>tickValues</code> property 
     *  is set to a non-empty Array, then this property is ignored.</p>
     *
     *  @default 0
     */
    public function get tickInterval():Number
    {
        return _tickInterval;
    }

    /**
     *  @private
     */
    public function set tickInterval(value:Number):void
    {
        _tickInterval = value;
        ticksChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    
    //----------------------------------
    //  tickValues
    //----------------------------------

    /**
     *  @private
     */
    private var _tickValues:Array = [];

    [Inspectable(category="General", defaultValue="undefined", arrayType="Number")]

    /**
     *  The positions of the tick marks on the slider. The positions correspond
     *  to the values on the slider and should be between 
     *  the <code>minimum</code> and <code>maximum</code> values.
     *  For example, if the <code>tickValues</code> property 
     *  is [0, 2.5, 7.5, 10] and <code>maximum</code> is 10, then a tick mark is placed
     *  in the following positions along the slider: the beginning of the slider, 
     *  1/4 of the way in from the left, 
     *  3/4 of the way in from the left, and at the end of the slider. 
     *  
     *  <p>If this property is set to a non-empty Array, then the <code>tickInterval</code> property
     *  is ignored.</p>
     *
     *  @default undefined
     */
    public function get tickValues():Array
    {
        return _tickValues;
    }

    /**
     *  @private
     */
    public function set tickValues(value:Array):void
    {
        _tickValues = value;
        ticksChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }
    
    //----------------------------------
    //  value
    //----------------------------------

    [Bindable("change")]
    [Bindable("valueCommit")]
    [Inspectable(category="General", defaultValue="undefined")]

    /**
     *  Contains the position of the thumb, and is a number between the
     *  <code>minimum</code> and <code>maximum</code> properties.
     *  Use the <code>value</code> property when <code>thumbCount</code> is 1.
     *  When <code>thumbCount</code> is greater than 1, use the
     *  <code>values</code> property instead.
     *  The default value is equal to the minimum property.
     */
    public function get value():Number
    {
        return _values[0];
    }

    /**
     *  @private
     */
    public function set value(val:Number):void
    {
        setValueAt(val, 0, true);
        valuesChanged = true;
        minimumSet = true;

        invalidateProperties();
        invalidateDisplayList();

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));   
    }

    //----------------------------------
    //  values
    //----------------------------------

    /**
     *  @private
     */
    private var _values:Array = [ 0, 0 ];

    /**
     *  @private
     */
    private var valuesChanged:Boolean = false;

    [Bindable("change")]
    [Inspectable(category="General", arrayType="Number")]

    /**
     *  An array of values for each thumb when <code>thumbCount</code>
     *  is greater than 1.
     */
    public function get values():Array
    {
        return _values;
    }

    /**
     *  @private
     */
    public function set values(value:Array):void
    {
        _values = value;
        valuesChanged = true;
        minimumSet = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (Slider.createAccessibilityImplementation != null)
            Slider.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        if (!innerSlider)
        {
            innerSlider = new UIComponent();
            UIComponent(innerSlider).tabChildren = true;
            addChild(innerSlider);
        }

        createBackgroundTrack();
        
        if (!trackHitArea)
        {
            trackHitArea = new UIComponent();
            innerSlider.addChild(trackHitArea); // trackHitArea should always be on top
            
            trackHitArea.addEventListener(MouseEvent.MOUSE_DOWN,
                                           track_mouseDownHandler);
        }

        invalidateProperties(); // Force commitProperties to be called on instantiation
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        var anyStyle:Boolean = styleProp == null || styleProp == "styleName";
        
        super.styleChanged(styleProp);

        if (styleProp == "showTrackHighlight" || anyStyle)
        {
            trackHighlightChanged = true;
            invalidateProperties();
        }

        if (styleProp == "trackHighlightSkin" || anyStyle)
        {
            if (innerSlider && highlightTrack)
            {
                innerSlider.removeChild(DisplayObject(highlightTrack));
                highlightTrack = null;
            }
            trackHighlightChanged = true;
            invalidateProperties();
        }

        if (styleProp == "labelStyleName" || anyStyle)
        {
            labelStyleChanged = true;
            invalidateProperties();
        }

        if (styleProp == "trackMargin" || anyStyle)
        {
            invalidateSize();
        }
        
        if (styleProp == "trackSkin" || anyStyle)
        {
            if (track)
            {
                innerSlider.removeChild(DisplayObject(track));  
                track = null;
                createBackgroundTrack();
            }
        }
        
        invalidateDisplayList();
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        var n:int;
        var i:int;

        if (trackHighlightChanged)
        {
            trackHighlightChanged = false;

            if (getStyle("showTrackHighlight"))
            {
                createHighlightTrack();
            }
            else if (highlightTrack)
            {
                innerSlider.removeChild(DisplayObject(highlightTrack));
                highlightTrack = null;
            }
        }

        if (directionChanged)
        {
            directionChanged = false;
            var isHorizontal:Boolean = _direction == SliderDirection.HORIZONTAL;

            if (isHorizontal)
            {
                DisplayObject(innerSlider).rotation = 0;
            }
            else
            {
                DisplayObject(innerSlider).rotation = -90;
                innerSlider.y = unscaledHeight;
            }

            if (labelObjects)
            {
                for (var labelIndex:int = labelObjects.numChildren - 1; labelIndex >= 0; labelIndex--)
                {
                    var labelObj:SliderLabel = SliderLabel(
                        labelObjects.getChildAt(labelIndex));
                    labelObj.rotation = isHorizontal ? 0 : 90;
                }
            }
        }

        if (labelStyleChanged && !labelsChanged)
        {
            labelStyleChanged = false;

            if (labelObjects)
            {
                var labelStyleName:String = getStyle("labelStyleName");
                n = labelObjects.numChildren;
                for (i = 0; i < n; i++)
                {
                    ISimpleStyleClient(labelObjects.getChildAt(i)).styleName = labelStyleName;
                }
            }
        }
        
        if (ticksChanged)
        {
            ticksChanged = false;

            createTicks();
        }

        if (labelsChanged)
        {
            labelsChanged = false;

            createLabels();
        }

        if (thumbsChanged)
        {
            thumbsChanged = false;

            createThumbs();
        }

        
        if (initValues)
        {
            initValues = false;

            if (!valuesChanged)
            {
                var val:Number = minimum;

                n = _thumbCount;
                for (i = 0; i < n; i++)
                {
                    _values[i] = val;
                    setValueAt(val, i);
                    if (_snapInterval && _snapInterval != 0)
                        val += snapInterval;
                    else
                        val++;
                }
                
                snapIntervalChanged = false;
            }
        }

        if (snapIntervalChanged)
        {
            snapIntervalChanged = false;

            if (!valuesChanged)
            {
                n = thumbs.numChildren;
                for (i = 0; i < n; i++)
                {
                    setValueAt(getValueFromX(SliderThumb(thumbs.getChildAt(i)).xPosition), i);
                }
            }
        }
        
        

        if (valuesChanged)
        {
            valuesChanged = false;

            n = _thumbCount;
            for (i = 0; i < n; i++)
            {
                setValueAt(getValueFromX(getXFromValue(Math.min(Math.max(values[i], minimum), maximum))), i);
            }
        }

        if (enabledChanged)
        {
            enabledChanged = false;

            n = thumbs.numChildren;
            for (i = 0; i < n; i++)
            {
                SliderThumb(thumbs.getChildAt(i)).enabled = _enabled;
            }

            n = labelObjects ? labelObjects.numChildren : 0;
            for (i = 0; i < n; i++)
            {
                SliderLabel(labelObjects.getChildAt(i)).enabled = _enabled;
            }
        }
        
        if (tabIndexChanged)
        {
            tabIndexChanged = false;
            
            n = thumbs.numChildren;
            for (i = 0; i < n; i++)
            {
                SliderThumb(thumbs.getChildAt(i)).tabIndex = _tabIndex;
            }
        }
    }

    /**
     *  Calculates the amount of space that the component takes up.
     *  A horizontal slider control calculates its height by examining
     *  the position of its labels, tick marks, and thumbs
     *  relative to the track.
     *  The height of the control is equivalent to the position
     *  of the bottom of the lowest element subtracted
     *  from the position of the top of the highest element.
     *  The width of a horizontal slider control defaults to 250 pixels.
     *  For a vertical slider control, the width and the length
     *  measurements are reversed.
     */
    override protected function measure():void
    {
        super.measure();

        var isHorizontal:Boolean = (direction == SliderDirection.HORIZONTAL);
        var numLabels:int = labelObjects ? labelObjects.numChildren : 0;
        var trackMargin:Number = getStyle("trackMargin");
        var length:Number = DEFAULT_MEASURED_WIDTH;

        if (!isNaN(trackMargin))
        {
            if (numLabels > 0)
            {
                length += (isHorizontal ?
                           SliderLabel(labelObjects.getChildAt(0)).getExplicitOrMeasuredWidth() / 2 :
                           SliderLabel(labelObjects.getChildAt(0)).getExplicitOrMeasuredHeight() / 2);
            }
            if (numLabels > 1)
            {
                length += (isHorizontal ?
                           SliderLabel(labelObjects.getChildAt(numLabels - 1)).getExplicitOrMeasuredWidth() / 2 :
                           SliderLabel(labelObjects.getChildAt(numLabels - 1)).getExplicitOrMeasuredHeight() / 2);
            }
            //length += track.width;
        }

        var bounds:Object = getComponentBounds();
        var thickness:Number = bounds.lower - bounds.upper;

        measuredMinWidth = measuredWidth = isHorizontal ? length : thickness;
        measuredMinHeight = measuredHeight = isHorizontal ? thickness : length;
    }

    /**
     *  Positions the elements of the control.
     *  The track, thumbs, labels, and tick marks are all positioned
     *  and sized by this method.
     *  The track is sized based on the length of the labels and on the track margin.
     *  If you specify a <code>trackMargin</code>, then the size of the track
     *  is equal to the available width minus the <code>trackMargin</code> times 2.
     *
     *  <p>Tick marks are spaced at even intervals along the track starting from the beginning of the track. An additional tick mark is placed
     *  at the end of the track if one doesn't already exist (if the tick interval isn't a multiple of the maximum value). The tick mark
     *  y-position is based on the <code>tickOffset</code>. An offset of 0 places the bottom of the tick at the top of the track. Negative offsets
     *  move the ticks upwards while positive offsets move them downward through the track.</p>
     *
     *  <p>Labels are positioned at even intervals along the track. The labels are always horizontally centered above their
     *  interval position unless the <code>trackMargin</code> setting is too small. If you specify a <code>trackMargin</code>, then the first and last labels will
     *  position themselves at the left and right borders of the control. Labels will not crop or resize themselves if they overlap,
     *  so be sure to allow enough space for them to fit on the track. The y-position is based on the <code>labelOffset</code> property. An offset of 0
     *  places the bottom of the label at the top of the track. Unlike tick marks, the labels can not be positioned to overlap the track.
     *  If the offset is a positive number, then the top of the label will be positioned below the bottom of the track.</p>
     *
     *  <p>The thumbs are positioned to overlap the track. Their x-position is determined by their value. The y-position is
     *  controlled by the <code>thumbOffset</code> property. An offset of 0 places the center of the thumb at the center of the track. A negative
     *  offset moves the thumbs upwards while a positive offset moves the thumbs downwards.</p>
     *
     *  <p>The placement of the tick marks, labels and thumbs are all independent from each other. They will not attempt to reposition
     *  themselves if they overlap.</p>
     *
     *  <p>For a vertical slider control, the same rules apply. In the above description, substitute width for height, height for width,
     *  left for up or top, right for down or bottom, x-position for y-position, and y-position for x-position.</p>
     *
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.   
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        
//      graphics.beginFill(0xEEEEEE);
//      graphics.drawRect(0, 0, unscaledWidth, unscaledHeight);
//      graphics.endFill();

        var isHorizontal:Boolean = (_direction == SliderDirection.HORIZONTAL);
        var numLabels:int = labelObjects ? labelObjects.numChildren : 0;
        var numThumbs:int = thumbs ? thumbs.numChildren : 0;
        var trackMargin:Number = getStyle("trackMargin");
        var widestThumb:Number = 6;
        var firstThumb:SliderThumb = SliderThumb(thumbs.getChildAt(0));
        if (thumbs && firstThumb)
            widestThumb = firstThumb.getExplicitOrMeasuredWidth();

        var trackLeftOffset:Number = widestThumb / 2; // Enough space for the thumb to rest at the edges
        var trackRightOffset:Number = trackLeftOffset;

        var availSpace:Number;

        var firstLabelSize:Number = 0;
        if (numLabels > 0)
        {
            var firstLabel:SliderLabel =
                SliderLabel(labelObjects.getChildAt(0));

            firstLabelSize = isHorizontal ?
                             firstLabel.getExplicitOrMeasuredWidth() :
                             firstLabel.getExplicitOrMeasuredHeight();
        }

        var lastLabelSize:Number = 0;
        if (numLabels > 1)
        {
            var lastLabel:SliderLabel =
                SliderLabel(labelObjects.getChildAt(numLabels - 1));
            lastLabelSize = isHorizontal ?
                            lastLabel.getExplicitOrMeasuredWidth():
                            lastLabel.getExplicitOrMeasuredHeight();
        }

        if (!isNaN(trackMargin))
            availSpace = trackMargin;
        else
            availSpace = (firstLabelSize + lastLabelSize) / 2;

        if (numLabels > 0)
        {
            if (!isNaN(trackMargin))
            {
                trackLeftOffset = Math.max(trackLeftOffset,
                                           availSpace / (numLabels > 1 ? 2 : 1));
            }
            else
            {
                trackLeftOffset = Math.max(trackLeftOffset, firstLabelSize / 2);
            }
        }
        else
        {

            trackLeftOffset = Math.max(trackLeftOffset, availSpace / 2);
        }

        var bounds:Object = getComponentBounds();

        //track.x = Math.round(trackLeftOffset);

        var trackY:Number = (((isHorizontal ? unscaledHeight : unscaledWidth) -
            (Number(bounds.lower) - Number(bounds.upper))) / 2) - Number(bounds.upper);

        track.move(Math.round(trackLeftOffset), Math.round(trackY));
        track.setActualSize((isHorizontal ? unscaledWidth: unscaledHeight) - (trackLeftOffset * 2), track.height);

        // Layout the thumbs' y positions.
        var tY:Number = track.y +
                        (track.height - firstThumb.getExplicitOrMeasuredHeight()) / 2 +
                        getStyle("thumbOffset");

        var n:int = _thumbCount;
        for (var i:int = 0; i < n; i++)
        {
            var currentThumb:SliderThumb = SliderThumb(thumbs.getChildAt(i));
            currentThumb.move(currentThumb.x, tY);
            currentThumb.visible = true;
            currentThumb.setActualSize(currentThumb.getExplicitOrMeasuredWidth(),
                                 currentThumb.getExplicitOrMeasuredHeight());
        }

        var g:Graphics = trackHitArea.graphics;

        var tLength:Number = 0
        if (_tickInterval > 0 || (_tickValues && _tickValues.length > 0))
            tLength = getStyle("tickLength");
        g.clear();      
        g.beginFill(0,0);
        var fullThumbHeight:Number = firstThumb.getExplicitOrMeasuredHeight();
        var halfThumbHeight:Number = (!fullThumbHeight) ? 0 : (fullThumbHeight / 2);
        g.drawRect(track.x, 
                track.y - halfThumbHeight - tLength, 
                track.width, 
                track.height + fullThumbHeight + tLength);
        g.endFill();

        if (_direction != SliderDirection.HORIZONTAL)
            innerSlider.y = unscaledHeight;

        layoutTicks();

        layoutLabels();

        setPosFromValue(); // use the value to position the thumb's x

        drawTrackHighlight();
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function createBackgroundTrack():void
    {
        if (!track)
        {
            var trackSkinClass:Class = getStyle("trackSkin");

            track = new trackSkinClass();

            if (track is ISimpleStyleClient)
                ISimpleStyleClient(track).styleName = this;

            innerSlider.addChildAt(DisplayObject(track),0); 
        }
    }

    /**
     *  @private
     */
    private function createHighlightTrack():void
    {
        var showTrackHighlight:Boolean = getStyle("showTrackHighlight");
        if (!highlightTrack && showTrackHighlight)
        {
            var trackHighlightClass:Class =
                getStyle("trackHighlightSkin");
            highlightTrack = new trackHighlightClass();

            if (highlightTrack is ISimpleStyleClient)
                ISimpleStyleClient(highlightTrack).styleName = this;

            innerSlider.addChildAt(DisplayObject(highlightTrack),
                innerSlider.getChildIndex(DisplayObject(track)) + 1);
        }
    }

    /**
     *  @private
     */
    private function createThumbs():void
    {
        var n:int;
        var i:int;

        // Delete all thumb children
        if (thumbs)
        {
            n = thumbs.numChildren;
            for (i = n - 1; i >= 0; i--)
            {
                thumbs.removeChildAt(i);
            }
        }
        else
        {
            thumbs = new UIComponent();
            thumbs.tabChildren = true;
            thumbs.tabEnabled = false;
            innerSlider.addChild(thumbs);
        }

        var thumb:SliderThumb;  // We want to force the thumb to be a subclass of SliderThumb

        n = _thumbCount;
        for (i = 0; i < n; i++)
        {
            thumb = SliderThumb(new _thumbClass());

            thumb.owner = this;
            thumb.styleName = new StyleProxy(this, thumbStyleFilters);
            thumb.thumbIndex = i;
            thumb.visible = true;
            thumb.enabled = enabled;

            thumb.upSkinName = "thumbUpSkin";
            thumb.downSkinName = "thumbDownSkin";
            thumb.disabledSkinName = "thumbDisabledSkin";
            thumb.overSkinName = "thumbOverSkin";
            thumb.skinName = "thumbSkin";

            thumbs.addChild(thumb);

            thumb.addEventListener(FocusEvent.FOCUS_IN,
                                   thumb_focusInHandler);
            thumb.addEventListener(FocusEvent.FOCUS_OUT,
                                   thumb_focusOutHandler);
        }
    }

    /**
     *  @private
     */
    private function createLabels():void
    {
        var labelObj:SliderLabel;

        if (labelObjects)
        {
            for (var i:int = labelObjects.numChildren - 1; i >= 0; i--)
            {
                labelObjects.removeChildAt(i);
            }
        }
        else
        {
            labelObjects = new UIComponent();
            innerSlider.addChildAt(labelObjects, innerSlider.getChildIndex(trackHitArea));
        }

        if (_labels)
        {
            var numLabels:int = _labels.length;
    
            for (var j:int = 0; j < numLabels; j++)
            {
                labelObj = new SliderLabel();
    
                labelObj.text = _labels[j] is String ?
                                _labels[j] :
                                _labels[j].toString();
    
                if (_direction != SliderDirection.HORIZONTAL)
                    labelObj.rotation = 90;
    
                var labelStyleName:String = getStyle("labelStyleName");
                if (labelStyleName)
                    labelObj.styleName = labelStyleName;
    
                labelObjects.addChild(labelObj);
            }
        }
    }

    /**
     *  @private
     */
    private function createTicks():void
    {
        if (!ticks)
        {
            ticks = new UIComponent();

            innerSlider.addChild(ticks);
        }
    }

    /**
     *  @private
     */
    private function getComponentBounds():Object
    {
        var isHorizontal:Boolean = (direction == SliderDirection.HORIZONTAL);
        var numLabels:int = labelObjects ? labelObjects.numChildren : 0;
        var labelY:Number;
        var labelSize:Number = 0;
        var thumbY:Number;
        var upperBound:Number = 0;
        var lowerBound:Number = track.height;

        if (numLabels > 0)
        {
            var sliderLabel:SliderLabel =
                SliderLabel(labelObjects.getChildAt(0));

            if (isHorizontal)
            {
                labelSize = sliderLabel.getExplicitOrMeasuredHeight();
            }
            else
            {
                for (var i:int = 0; i < numLabels; i++)
                {
                    sliderLabel = SliderLabel(labelObjects.getChildAt(i));
                    labelSize = Math.max(labelSize,
                        sliderLabel.getExplicitOrMeasuredWidth());
                }
            }

            var labOffset:Number = getStyle("labelOffset");
            labelY = labOffset - (labOffset > 0 ? 0 : labelSize);

            upperBound = Math.min(upperBound, labelY);
            lowerBound = Math.max(lowerBound, labOffset + (labOffset > 0 ? labelSize : 0));
        }

        if (ticks)
        {
            var tLen:Number = getStyle("tickLength");
            var tOff:Number = getStyle("tickOffset");

            upperBound = Math.min(upperBound, tOff - tLen);
            lowerBound = Math.max(lowerBound, tOff);
        }

        if (thumbs.numChildren > 0)
        {
            thumbY = (track.height - SliderThumb(thumbs.getChildAt(0)).getExplicitOrMeasuredHeight()) / 2 +
                     getStyle("thumbOffset");

            upperBound = Math.min(upperBound, thumbY);
            lowerBound = Math.max(lowerBound, thumbY + SliderThumb(thumbs.getChildAt(0)).getExplicitOrMeasuredHeight());
        }

        return { lower: lowerBound, upper: upperBound };
    }

    /**
     *  @private
     */
    private function layoutTicks():void
    {
        if (ticks)
        {
            var g:Graphics = ticks.graphics;
            var tLength:Number = getStyle("tickLength");
            var tOffset:Number = getStyle("tickOffset");
            var tickWidth:Number = getStyle("tickThickness");
            var xOffset:Number = tickWidth / 2;
            var xPos:Number;
            var tColor:Number = getStyle("tickColor");
            
            var usePositions:Boolean = _tickValues && _tickValues.length > 0 ? true : false;
            var positionIndex:int = 0;
            var val:Number = usePositions ? _tickValues[positionIndex++] : minimum;
            
            g.clear();
            
            if (_tickInterval > 0 || usePositions)
            {
                g.lineStyle(tickWidth,tColor,100);

                do
                {
                    xPos = Math.round(getXFromValue(val) - xOffset);
                    g.moveTo(xPos, tLength);
                    g.lineTo(xPos, 0);
                    val = usePositions ? (positionIndex < _tickValues.length ? _tickValues[positionIndex++] : NaN) : _tickInterval + val;
                } while (val < maximum || (usePositions && positionIndex < _tickValues.length))

                // draw the last tick
                if (!usePositions || val == maximum)
                {
                    xPos = track.x + track.width - 1 - xOffset;
                    g.moveTo(xPos, tLength);
                    g.lineTo(xPos, 0);
                }

                ticks.y = Math.round(track.y + tOffset - tLength);
            }
        }
    }

    /**
     *  @private
     */
    private function layoutLabels():void
    {
        var numLabels:Number = labelObjects ? labelObjects.numChildren : 0;
        var availSpace:Number;

        if (numLabels > 0)
        {
            var labelInterval:Number = track.width / (numLabels - 1);
            // The amount of space we have available for the labels to hang past the track
            availSpace = Math.max((_direction == SliderDirection.HORIZONTAL ?
                                  unscaledWidth :
                                  unscaledHeight) - track.width,
                                  SliderThumb(thumbs.getChildAt(0)).getExplicitOrMeasuredWidth());

            var labelPos:Number;
            var left:Number = track.x;
            var curLabel:Object;

            for (var i:int = 0; i < numLabels; i++)
            {
                curLabel = labelObjects.getChildAt(i);
                curLabel.setActualSize(curLabel.getExplicitOrMeasuredWidth(), curLabel.getExplicitOrMeasuredHeight());

                var yPos:Number = track.y  - curLabel.height + getStyle("labelOffset");

                if (_direction == SliderDirection.HORIZONTAL)
                {
                    labelPos = curLabel.getExplicitOrMeasuredWidth() / 2;

                    if (i == 0)
                        labelPos = Math.min(labelPos, availSpace / (numLabels > Number(1) ? Number(2) : Number(1)));
                    else if (i == (numLabels - 1))
                        labelPos = Math.max(labelPos, curLabel.getExplicitOrMeasuredWidth() - availSpace / 2);

                    curLabel.move(left - labelPos,yPos);
                }
                else
                {
                    var labelOff:Number = getStyle("labelOffset");

                    labelPos = curLabel.getExplicitOrMeasuredHeight() / 2;

                    if (i == 0)
                        labelPos = Math.max(labelPos, curLabel.getExplicitOrMeasuredHeight() - availSpace / (numLabels > Number(1) ? Number(2) : Number(1)));
                    else if (i == (numLabels-1))
                        labelPos = Math.min(labelPos,availSpace / 2);

                    curLabel.move(left + labelPos,track.y + labelOff +
                                  (labelOff > 0 ? 0 : -curLabel.getExplicitOrMeasuredWidth()));
                }
                left += labelInterval;
            }
        }
    }

    /**
     *  @private
     */
    mx_internal function drawTrackHighlight():void
    {
        if (highlightTrack)
        {
            var xPos:Number;
            var tWidth:Number;

            var firstThumb:SliderThumb = SliderThumb(thumbs.getChildAt(0));

            if (_thumbCount > 1)
            {
                xPos = firstThumb.xPosition;
                var secondThumb:SliderThumb = SliderThumb(thumbs.getChildAt(1));
                tWidth = secondThumb.xPosition - firstThumb.xPosition;
            }
            else
            {
                xPos = track.x;
                tWidth = firstThumb.xPosition - xPos;
            }


            highlightTrack.move(xPos, track.y + 1);
            highlightTrack.setActualSize(tWidth > 0 ? tWidth : 0, highlightTrack.height);
        }
    }

    /**
     *  @private
     *  Helper function that starts the dataTip and dispatches the press event.
     */
    mx_internal function onThumbPress(thumb:Object):void
    {
        if (showDataTip)
        {
            // Setup number formatter
            dataFormatter = new NumberFormatter();
            dataFormatter.precision = getStyle("dataTipPrecision");

            if (!dataTip)
            {
                dataTip = SliderDataTip(new sliderDataTipClass());
                systemManager.toolTipChildren.addChild(dataTip);

                var dataTipStyleName:String = getStyle("dataTipStyleName");
                if (dataTipStyleName)
                {
                    dataTip.styleName = dataTipStyleName;
                }
            }

            var formattedVal:String;
            if (_dataTipFormatFunction != null)
            {
                formattedVal = this._dataTipFormatFunction(
                    getValueFromX(thumb.xPosition));
            }
            else
            {
                formattedVal = dataFormatter.format(getValueFromX(thumb.xPosition));
            }

            dataTip.text = formattedVal;

            // Tool tip has been freshly created and new text assigned to it.
            // Hence force a validation so that we can set the
            // size required to show the text completely.
            dataTip.validateNow();
            dataTip.setActualSize(dataTip.getExplicitOrMeasuredWidth(),dataTip.getExplicitOrMeasuredHeight());
            positionDataTip(thumb);
        }
        keyInteraction = false;

        var event:SliderEvent = new SliderEvent(SliderEvent.THUMB_PRESS);
        event.value = getValueFromX(thumb.xPosition);;
        event.thumbIndex = thumb.thumbIndex;
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    mx_internal function onThumbRelease(thumb:Object):void
    {
        interactionClickTarget = SliderEventClickTarget.THUMB;

        destroyDataTip();

        setValueFromPos(thumb.thumbIndex);

        dataFormatter = null;

        var event:SliderEvent = new SliderEvent(SliderEvent.THUMB_RELEASE);
        event.value = getValueFromX(thumb.xPosition);;
        event.thumbIndex = thumb.thumbIndex;
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    mx_internal function onThumbMove(thumb:Object):void
    {
        var value:Number = getValueFromX(thumb.xPosition);
        
        if (showDataTip)
        {           
            dataTip.text = _dataTipFormatFunction != null ?
                           _dataTipFormatFunction(value) : 
                           dataFormatter.format(value);
                           
            dataTip.setActualSize(dataTip.getExplicitOrMeasuredWidth(),
                                  dataTip.getExplicitOrMeasuredHeight());
            
            positionDataTip(thumb);
        }

        if (liveDragging)
        {
            interactionClickTarget = SliderEventClickTarget.THUMB;
            setValueAt(value, thumb.thumbIndex);
        }

        var event:SliderEvent = new SliderEvent(SliderEvent.THUMB_DRAG);
        event.value = value;
        event.thumbIndex = thumb.thumbIndex;
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function positionDataTip(thumb:Object):void
    {
        var relX:Number;
        var relY:Number;

        var tX:Number = thumb.x;
        var tY:Number = thumb.y;

        var tPlacement:String =  getStyle("dataTipPlacement");
        var tOffset:Number = getStyle("dataTipOffset");

        // Need to special case tooltip position because the tooltip movieclip
        // resides in the root movie clip, instead of the Slider movieclip
        if (_direction == SliderDirection.HORIZONTAL)
        {
            relX = tX;
            relY = tY;

            if (tPlacement == "left")
            {
                relX -= tOffset + dataTip.width;
                relY += (thumb.height - dataTip.height) / 2;
            }
            else if (tPlacement == "right")
            {
                relX += tOffset + thumb.width;
                relY += (thumb.height - dataTip.height) / 2;
            }
            else if (tPlacement == "top")
            {
                relY -= tOffset + dataTip.height;
                relX -= (dataTip.width - thumb.width) / 2;
            }
            else if (tPlacement == "bottom")
            {
                relY += tOffset + thumb.height;
                relX -= (dataTip.width - thumb.width) / 2;
            }
        }
        else
        {
            relX = tY;
            relY = unscaledHeight - tX - (dataTip.height + thumb.width) / 2;

            if (tPlacement == "left")
            {
                relX -= tOffset + dataTip.width;
            }
            else if (tPlacement == "right")
            {
                relX += tOffset + thumb.height;
            }
            else if (tPlacement == "top")
            {
                relY -= tOffset + (dataTip.height + thumb.width) / 2;
                relX -= (dataTip.width - thumb.height) / 2;
            }
            else if (tPlacement == "bottom")
            {
                relY += tOffset + (dataTip.height + thumb.width) / 2;
                relX -= (dataTip.width - thumb.height) / 2;
            }
        }
        
        var o:Point = new Point(relX, relY);
        var r:Point = localToGlobal(o);

        dataTip.x = r.x < 0 ? 0 : r.x;
        dataTip.y = r.y < 0 ? 0 : r.y;
    }

    /**
     *  @private
     */
    private function destroyDataTip():void
    {
        if (dataTip)
        {
            systemManager.toolTipChildren.removeChild(dataTip);
            dataTip = null;
        }
    }

    /**
     *  @private
     *  Utility for finding the x position which corresponds
     *  to the given value.
     */
    mx_internal function getXFromValue(v:Number):Number
    {
        var val:Number;

        if (v == minimum)
            val = track.x;
        else if (v == maximum)
            val = track.x + track.width;
        else
            val =  track.x + (v - minimum) * (track.width) / (maximum - minimum);

        return val;
    }

    /**
     *  @private
     */
    mx_internal function getXBounds(selectedThumbIndex:int):Object
    {
        var maxX:Number = track.x + track.width;
        var minX:Number = track.x;
        if (allowThumbOverlap)
        {
            return { max: maxX, min: minX };    
        }

        var minBound:Number = NaN;
        var maxBound:Number = NaN;

        var prevThumb:SliderThumb =
            selectedThumbIndex > 0 ?
            SliderThumb(thumbs.getChildAt(selectedThumbIndex - 1)) :
            null;

        var nextThumb:SliderThumb =
            selectedThumbIndex + 1 < thumbs.numChildren ?
            SliderThumb(thumbs.getChildAt(selectedThumbIndex + 1)) :
            null;

        if (prevThumb)
            minBound = prevThumb.xPosition + prevThumb.width / 2;

        if (nextThumb)
            maxBound = nextThumb.xPosition - nextThumb.width / 2;

        if (isNaN(minBound))
            minBound = minX;
        else
            minBound = Math.min(Math.max(minX,minBound),maxX);

        if (isNaN(maxBound))
            maxBound = maxX;
        else
            maxBound = Math.max(Math.min(maxX, maxBound),minX);

        return { max: maxBound, min: minBound };
    }

    /**
     *  @private
     *  Utility for positioning the thumb(s) from the current value.
     */
    private function setPosFromValue():void
    {
        var n:int = _thumbCount;
        for (var i:int = 0; i < n; i++)
        {
            var thumb:SliderThumb = SliderThumb(thumbs.getChildAt(i));
            thumb.xPosition = getXFromValue(values[i]);
        }
    }

    /**
     *  @private
     *  Utility for getting a value corresponding to a given x.
     */
    mx_internal function getValueFromX(xPos:Number):Number
    {
        var v:Number = (xPos - track.x) *
                       (maximum - minimum) /
                       (track.width) + minimum;
        
        // kill rounding error at the edges.
        if (v - minimum <= 0.002)
        {
            v = minimum;
        }
        else if (maximum - v <= 0.002)
        {
            v = maximum;
        }
        else if (!isNaN(_snapInterval) && _snapInterval != 0)
        {
            v = Math.round((v - minimum) / _snapInterval) *
                _snapInterval + minimum;
        }

        return v;
    }

    /**
     *  @private
     *  Utility for committing a value of a given thumb.
     */
    private function setValueFromPos(thumbIndex:int):void
    {
        var thumb:SliderThumb = SliderThumb(thumbs.getChildAt(thumbIndex));
        setValueAt(getValueFromX(thumb.xPosition), thumbIndex);
    }

    /**
     *  @private
     */
    mx_internal function getSnapValue(value:Number, thumb:SliderThumb = null):Number
    {
        if (!isNaN(_snapInterval) && _snapInterval != 0)
        {
                var val:Number = getValueFromX(value);  
                
                if (thumb && (thumbs.numChildren > 1) && !allowThumbOverlap)
                {
                    var check_bounds:Boolean = true;
                    var bounds:Object 

                    bounds = getXBounds(thumb.thumbIndex);
                
                    var prevThumb:SliderThumb =
                        thumb.thumbIndex > 0 ?
                        SliderThumb(thumbs.getChildAt(thumb.thumbIndex- 1)) :
                        null;

                    var nextThumb:SliderThumb =
                        thumb.thumbIndex + 1 < thumbs.numChildren ?
                        SliderThumb(thumbs.getChildAt(thumb.thumbIndex + 1)) :
                        null;

                    if (prevThumb)
                    {
                        bounds.min -= (prevThumb.width / 2);
                        // check if thumb is at minimum, if not we can ignore the bounds logic
                        if (val == minimum)
                            if (getValueFromX((prevThumb.xPosition - prevThumb.width/2)) != minimum)
                                check_bounds = false;
                    }
                    else
                    {
                        if (val == minimum)
                            check_bounds = false;
                    }

                    if (nextThumb)
                    {
                        bounds.max +=  (nextThumb.width / 2);
                        // check if thumb is at maximum, if not we can ignore the bounds logic
                        if (val == maximum)
                            if (getValueFromX((nextThumb.xPosition + nextThumb.width/2)) != maximum)
                                check_bounds = false;
                    }
                    else
                    {
                        if (val == maximum)
                            check_bounds = false;
                    }
                    if (check_bounds)
                        val = Math.min(Math.max(val, getValueFromX(Math.round(bounds.min)) + _snapInterval), 
                                    getValueFromX(Math.round(bounds.max)) - _snapInterval);

                }
                return getXFromValue(val);
        }

       return value;
    }

    /**
     *  @private Accessed by the Thumb to find out the snap interval
     */
    mx_internal function getSnapIntervalWidth():Number
    {
        return _snapInterval * track.width / (maximum - minimum);
    }

    /**
     *  @private
     */
    mx_internal function updateThumbValue(thumbIndex:int):void
    {
        setValueFromPos(thumbIndex);
    }

    /**
     *  Returns the thumb object at the given index. Use this method to
     *  style and customize individual thumbs in a slider control.
     *
     *  @param index The zero-based index number of the thumb.
     *
     *  @return A reference to the SliderThumb object.
     */
    public function getThumbAt(index:int):SliderThumb
    {
        return index >= 0 && index < thumbs.numChildren ?
               SliderThumb(thumbs.getChildAt(index)) :
               null;
    }

    /**
     *  This method sets the value of a slider thumb, and updates the display.
     *
     *  @param index The zero-based index number of the thumb to set
     *  the value of, where a value of 0 corresponds to the first thumb.
     *
     *  @param value The value to set the thumb to
     */
    public function setThumbValueAt(index:int, value:Number):void
    {
        setValueAt(value, index, true);
        valuesChanged = true;

        invalidateProperties();
        invalidateDisplayList();
    }

    /**
     *  @private
     */
    private function setValueAt(value:Number, index:int,
                                isProgrammatic:Boolean = false):void
    {
        var oldValue:Number = _values[index];
        
        // we need to do the round of (to remove the floating point error)
        // if the stepSize had a fractional value
        if (snapIntervalPrecision != -1)
        {
            var scale:Number = Math.pow(10, snapIntervalPrecision);
            value = Math.round(value * scale) / scale;
        }

        _values[index] = value;
        
        if (!isProgrammatic)
        {
            var event:SliderEvent = new SliderEvent(SliderEvent.CHANGE);
            event.value = value;
            event.thumbIndex = index;
            event.clickTarget = interactionClickTarget;
            //set the triggerEvent correctly
            if (keyInteraction)
            {
                event.triggerEvent = new KeyboardEvent(KeyboardEvent.KEY_DOWN);
                //reset to mouse default
                keyInteraction = false;
            }
            else
            event.triggerEvent = new MouseEvent(MouseEvent.CLICK);
            if (!isNaN(oldValue) && Math.abs(oldValue - value) > 0.002)
                dispatchEvent(event);
        }

        invalidateDisplayList();
    }

    /**
     *  @private
     */
    mx_internal function registerMouseMove(listener:Function):void
    {
        innerSlider.addEventListener(MouseEvent.MOUSE_MOVE, listener);
    }

    /**
     *  @private
     */
    mx_internal function unRegisterMouseMove(listener:Function):void
    {
        innerSlider.removeEventListener(MouseEvent.MOUSE_MOVE, listener);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Move the thumb when pressed.
     */
    private function track_mouseDownHandler(event:MouseEvent):void
    {
        if (event.target != trackHitArea && event.target != ticks)
            return;
        if (enabled && allowTrackClick)
        {
            interactionClickTarget = SliderEventClickTarget.TRACK;
            //this is a mouse event
            keyInteraction = false;
            var pt:Point = new Point(event.localX, event.localY);
            var xM:Number = pt.x;
            var minIndex:Number = 0;
            var minDistance:Number = 10000000;

            // find the nearest thumb
            var n:int = _thumbCount;
            for (var i:int = 0; i < n; i++)
            {
                var d:Number = Math.abs(SliderThumb(thumbs.getChildAt(i)).xPosition - xM);
                if (d < minDistance)
                {
                    minIndex = i;
                    minDistance = d;
                }
            }
            var thumb:SliderThumb = SliderThumb(thumbs.getChildAt(minIndex));
            if (!isNaN(_snapInterval) && _snapInterval != 0)
                xM = getXFromValue(getValueFromX(xM));

            var duration:Number = getStyle("slideDuration");
            var t:Tween = new Tween(thumb, thumb.xPosition, xM, duration);

            var easingFunction:Function = getStyle("slideEasingFunction") as Function;
            if (easingFunction != null)
                t.easingFunction = easingFunction;

            drawTrackHighlight();
        }
    }

    /**
     *  @private
     */
    private function thumb_focusInHandler(event:FocusEvent):void
    {
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function thumb_focusOutHandler(event:FocusEvent):void
    {
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    mx_internal function getTrackHitArea():UIComponent
    {
        return trackHitArea;
    }

}

}
