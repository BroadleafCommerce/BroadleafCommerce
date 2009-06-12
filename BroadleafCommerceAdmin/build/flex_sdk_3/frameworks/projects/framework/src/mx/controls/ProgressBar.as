////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2002-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.events.Event;
import flash.events.IEventDispatcher;
import flash.events.ProgressEvent;
import flash.events.TimerEvent;
import flash.text.TextLineMetrics;
import flash.utils.Timer;
import mx.core.FlexVersion;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.mx_internal;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.events.FlexEvent;
import mx.styles.ISimpleStyleClient;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the load completes.
 *
 *  @eventType flash.events.Event.COMPLETE
 */
[Event(name="complete", type="flash.events.Event")]

/**
 *  Dispatched when an object's state changes from visible to invisible.
 *
 *  @eventType mx.events.FlexEvent.HIDE
 */
[Event(name="hide", type="mx.events.FlexEvent")]

/**
 *  Dispatched as content loads in event
 *  or polled mode.
 *
 *  @eventType flash.events.ProgressEvent.PROGRESS
 */
[Event(name="progress", type="flash.events.ProgressEvent")]

/**
 *  Dispatched when the component becomes visible.
 *
 *  @eventType mx.events.FlexEvent.SHOW
 */
[Event(name="show", type="mx.events.FlexEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/BarColorStyle.as"
include "../styles/metadata/GapStyles.as"
include "../styles/metadata/LeadingStyle.as"
include "../styles/metadata/PaddingStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  Skin style for a determinate progress bar.
 *
 *  @default mx.skins.halo.ProgressBarSkin
 */
[Style(name="barSkin", type="Class", inherit="no")]

/**
 *  Black section of a three-dimensional border, or the color section
 *  of a two-dimensional border.
 *  @default 0xB7BABC
 */
[Style(name="borderColor", type="uint", format="Color", inherit="no")]

/**
 *  The number of pixels to move the indeterminate progress bar for each indeterminate loop.
 *  Once the progress bar has moved the specified number of pixels, it gets respositioned back to
 *  its starting point. A value of 0 means that the bar is not moved. 
 *
 *  @default 26
 */
[Style(name="indeterminateMoveInterval", type="Number", inherit="no")]

/**
 *  Skin style for an indeterminate progress bar.
 *
 *  @default mx.skins.halo.ProgressIndeterminateSkin
 */
[Style(name="indeterminateSkin", type="Class", inherit="no")]

/**
 *  Skin style for the mask of the determinate and indeterminate bars.
 *
 *  @default mx.skins.halo.ProgressMaskSkin
 */
[Style(name="maskSkin", type="Class", inherit="no")]

/**
 *  Skin style for the progress indicator track.
 *
 *  @default mx.skins.halo.ProgressTrackSkin
 */
[Style(name="trackSkin", type="Class", inherit="no")]

/**
 *  Theme color of the ProgressBar control.
 *  Possible values are haloGreen, haloBlue, and haloOrange.
 *  Adobe recommends setting the <code>themeColor</code> style
 *  in the <code>&lt;mx:Application&gt;</code> tag, instead of in
 *  individual controls. 
 *
 *  @default "haloBlue"
 */
[Style(name="themeColor", type="uint", format="Color", inherit="yes")]

/**
 *  The colors of the track, as an array of two colors.
 *  You can use the same color twice for a solid track color.
 *
 *  @default [ 0xE6EEEE, 0xE6EEEE ]
 */
[Style(name="trackColors", type="Array", arrayType="uint", format="Color", inherit="no")]

/**
 *  The height of the track in pixels. If the value is NaN, then
 *  use the height of the skin specified by the <code>trackSkin</code> property. 
 *  
 *  @default NaN
 */
[Style(name="trackHeight", type="Number", format="Length", inherit="no")]

/**
 *  The width of the label in pixels. If the value is NaN,
 *  the ProgressBar control sets it to the width of the control.
 *
 *  @default NaN
 */
[Style(name="labelWidth", type="Number", format="Length", inherit="yes")]

/**
 *  Effect called when Flex dispatches the <code>complete</code> event, 
 *  which occurs when the load completes.
 */
[Effect(name="completeEffect", event="complete")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultBindingProperty(source="percentComplete")]

[DefaultTriggerEvent("progress")]

[IconFile("ProgressBar.png")]

[ResourceBundle("controls")]
    
/**
 *  The ProgressBar control provides a visual representation of the progress of a task over
 *  time. There are two types of ProgressBar controls: determinate and indeterminate.
 *
 *  <p>A determinate ProgressBar control is a linear representation of the progress of a task over time.
 *  You use a determinate ProgressBar when the scope of the task is known. It displays when the user
 *  has to wait for an extended amount of time.</p>
 *
 *  <p>An indeterminate ProgressBar control represents time-based processes for which the scope is
 *  not yet known. As soon as you can determine the scope, 
 *  you should use a determinate ProgressBar control.</p>
 *
 *  <p>The ProgressBar control has the following default characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>default size</td>
 *           <td>150 pixels wide by 4 pixels high</td>
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
 *  <p>The <code>&lt;mx:ProgressBar&gt;</code> tag inherits all of the tag attributes 
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:ProgressBar
 *    <strong>Properties</strong>
 *    conversion="1"
 *    direction="right|left"
 *    indeterminate="false|true"
 *    label="<i>No default</i>"
 *    labelPlacement="bottom|top|left|right|center"
 *    maximum="0"
 *    minimum="0"
 *    mode="event|polled|manual"
 *    source="<i>No default</i>"
 *  
 *    <strong>Styles</strong>
 *    barColor="undefined"
 *    barSkin="ProgressBarSkin"
 *    borderColor="0xAAB3B3"
 *    color="0x0B333C"
 *    disabledColor="0xAAB3B3"
 *    fontAntiAliasType="advanced"
 *    fontFamily="Verdana"
 *    fontGridFitType="pixel"
 *    fontSharpness="0"
 *    fontSize="10"
 *    fontThickness="0"
 *    fontStyle="normal|italic"
 *    fontWeight="normal|bold"
 *    horizontalGap="8"
 *    indeterminateMoveInterval="26"
 *    indeterminateSkin="ProgressIndeterminateSkin"
 *    labelWidth="Computed"
 *    leading="0"
 *    maskSkin="ProgressMaskSkin"
 *    paddingLeft="0"
 *    paddingRight="0"
 *    textAlign="left|right|center"
 *    textDecoration="none|underline"
 *    textIndent="0"
 *    themeColor="haloGreen|haloBlue|haloOrange"
 *    trackColors="[0xE6EEEE,0xE6EEEE]"
 *    trackHeight="Calculated"
 *    trackSkin="ProgressTrackSkin"
 *    verticalGap="6"
 *  
 *    <strong>Events</strong>
 *    complete="<i>No default</i>"
 *    hide="<i>No default</i>"
 *    progress="<i>No default</i>"
 *    show="<i>No default</i>"
 *  
 *    <strong>Effects</strong>
 *    completeEffect="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *  </p>
 *
 *  @see mx.controls.ProgressBarDirection
 *  @see mx.controls.ProgressBarLabelPlacement
 *  @see mx.controls.ProgressBarMode
 *
 *  @includeExample examples/SimpleProgressBar.mxml
 */
public class ProgressBar extends UIComponent implements IFontContextComponent
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
    public function ProgressBar()
    {
        super();

        pollTimer = new Timer(_interval);
        cacheAsBitmap = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal var _content:UIComponent;

    /**
     *  @private
     */
    mx_internal var _bar:UIComponent;

    /**
     *  @private
     */
    mx_internal var _indeterminateBar:IFlexDisplayObject;

    /**
     *  @private
     */
    mx_internal var _determinateBar:IFlexDisplayObject;

    /**
     *  @private
     */
    mx_internal var _track:IFlexDisplayObject;

    /**
     *  @private
     */
    mx_internal var _barMask:IFlexDisplayObject;

    /**
     *  @private
     */
    mx_internal var _labelField:IUITextField;

    /**
     *  @private
     */
    private var pollTimer:Timer;

    /**
     *  @private
     */
    private var _interval:Number = 30;

    /**
     *  @private
     */
    private var indeterminatePlaying:Boolean = false;

    /**
     *  @private
     */
    private var stopPolledMode:Boolean = false;

    /**
     *  @private
     */
    private var barSkinChanged:Boolean = false;

    /**
     *  @private
     */
    private var trackSkinChanged:Boolean = false;

    /**
     *  @private
     */
    private var indeterminateSkinChanged:Boolean = false;
    
    /**
     *  @private
     */
    private var visibleChanged:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  conversion
    //----------------------------------

    /**
     *  @private
     *  Storage for the conversion property.
     */
    private var _conversion:Number = 1;

    [Bindable("conversionChanged")]
    [Inspectable(defaultValue="1")]

    /**
     *  Number used to convert incoming current bytes loaded value and
     *  the total bytes loaded values.
     *  Flex divides the current and total values by this property and
     *  uses the closest integer that is less than or equal to each
     *  value in the label string. A value of 1 does no conversion.
     *
     *  @default 1
     */
    public function get conversion():Number
    {
        return _conversion;
    }

    /**
     *  @private
     */
    public function set conversion(value:Number):void
    {
        if (!isNaN(value) && Number(value) > 0 && value != _conversion)
        {
            _conversion = Number(value);

            invalidateDisplayList();

            dispatchEvent(new Event("conversionChanged"));
        }
    }

    //----------------------------------
    //  direction
    //----------------------------------

    /**
     *  @private
     *  Storage for the direction property.
     */
    private var _direction:String = ProgressBarDirection.RIGHT;

    [Bindable("directionChanged")]
    [Inspectable(enumeration="left,right", defaultValue="right")]

    /**
     *  Direction in which the fill of the ProgressBar expands toward completion. 
     *  Valid values in MXML are
     *  <code>"right"</code> and <code>"left"</code>.
     *
     *  <p>In ActionScript, you use use the following constants
     *  to set this property:
     *  <code>ProgressBarDirection.RIGHT</code> and
     *  <code>ProgressBarDirection.LEFT</code>.</p>
     *
     *  @see mx.controls.ProgressBarDirection
     *  @default ProgressBarDirection.RIGHT
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
        if (value == ProgressBarDirection.LEFT || value == ProgressBarDirection.RIGHT)
            _direction = value;

        invalidateDisplayList();

        dispatchEvent(new Event("directionChanged"));
    }

    //----------------------------------
    //  fontContext
    //----------------------------------
    
    /**
     *  @private
     */
    public function get fontContext():IFlexModuleFactory
    {
        return moduleFactory;
    }

    /**
     *  @private
     */
    public function set fontContext(moduleFactory:IFlexModuleFactory):void
    {
        this.moduleFactory = moduleFactory;
    }

    //----------------------------------
    //  indeterminate
    //----------------------------------

    /**
     *  @private
     *  Storage for the indeterminate property.
     */
    private var _indeterminate:Boolean = false;

    /**
     *  @private
     */
    private var indeterminateChanged:Boolean = true;

    [Bindable("indeterminateChanged")]
    [Inspectable(category="General", defaultValue="false")]

    /**
     *  Whether the ProgressBar control has a determinate or
     *  indeterminate appearance.
     *  Use an indeterminate appearance when the progress status cannot be determined.
     *  If <code>true</code>, the appearance is indeterminate.
     *
     *  @default false
     */
    public function get indeterminate():Boolean
    {
        return _indeterminate;
    }

    /**
     *  @private
     */
    public function set indeterminate(value:Boolean):void
    {
        _indeterminate = value;
        indeterminateChanged = true;

        invalidateProperties();
        invalidateDisplayList();

        dispatchEvent(new Event("indeterminateChanged"));
    }

    //----------------------------------
    //  label
    //----------------------------------

    /**
     *  @private
     *  Storage for the label property.
     */
    private var _label:String;

    /**
     *  @private
     */
    private var labelOverride:String;
    
    [Bindable("labelChanged")]
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Text that accompanies the progress bar. You can include
     *  the following special characters in the text string:
     * 
     *  <ul>
     *    <li>%1 = current loaded bytes</li>
     *    <li>%2 = total bytes</li>
     *    <li>%3 = percent loaded</li>
     *    <li>%% = "%" character</li>
     *  </ul>
     *
     *  <p>If a field is unknown, it is replaced by "??".
     *  If undefined, the label is not displayed.</p>
     *  
     *  <p>If you are in manual mode, you can set the values of these special characters 
     *  by using the <code>setProgress()</code> method.</p>
     *
     *  @default "LOADING %3%%"
     */
    public function get label():String
    {
        return _label;
    }

    /**
     *  @private
     */
    public function set label(value:String):void
    {
        labelOverride = value;

        _label = value != null ?
                 value :
                 resourceManager.getString(
                     "controls", "label");

        invalidateDisplayList();

        dispatchEvent(new Event("labelChanged"));
    }

    //----------------------------------
    //  labelPlacement
    //----------------------------------

    /**
     *  @private
     *  Storage for the labelPlacement property.
     */
    private var _labelPlacement:String = ProgressBarLabelPlacement.BOTTOM;

    [Bindable("labelPlacementChanged")]
    [Inspectable(category="General", enumeration="left,right,top,bottom,center", defaultValue="bottom")]

    /**
     *  Placement of the label.
     *  Valid values in MXML are <code>"right"</code>, <code>"left"</code>,
     *  <code>"bottom"</code>, <code>"center"</code>, and <code>"top"</code>.
     *
     *  <p>In ActionScript, you can use use the following constants to set this property:
     *  <code>ProgressBarLabelPlacement.RIGHT</code>, <code>ProgressBarLabelPlacement.LEFT</code>,
     *  <code>ProgressBarLabelPlacement.BOTTOM</code>, <code>ProgressBarLabelPlacement.CENTER</code>,
     *  and <code>ProgressBarLabelPlacement.TOP</code>.</p>
     *
     *  @see mx.controls.ProgressBarLabelPlacement
     *  @default ProgressBarLabelPlacement.BOTTOM
     */
    public function get labelPlacement():String
    {
        return _labelPlacement;
    }

    /**
     *  @private
     */
    public function set labelPlacement(value:String):void
    {
        if (value != _labelPlacement)
            _labelPlacement = value;

        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("labelPlacementChanged"));
    }

    //----------------------------------
    //  maximum
    //----------------------------------

    /**
     *  @private
     *  Storage for the maximum property.
     */
    private var _maximum:Number = 0;

    [Bindable("maximumChanged")]
    [Inspectable(category="General", defaultValue="0")]

    /**
     *  Largest progress value for the ProgressBar. You
     *  can only use this property in manual mode.
     *
     *  @default 0
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
        if (!isNaN(value) && _mode == ProgressBarMode.MANUAL && value != _maximum)
        {
            _maximum = value;
            invalidateDisplayList();
            dispatchEvent(new Event("maximumChanged"));
        }
    }

    //----------------------------------
    //  minimum
    //----------------------------------

    /**
     *  @private
     *  Storage for the minimum property.
     */
    private var _minimum:Number = 0;

    [Bindable("minimumChanged")]
    [Inspectable(category="General", defaultValue="0")]

    /**
     *  Smallest progress value for the ProgressBar. This
     *  property is set by the developer only in manual mode.
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
        if (!isNaN(value) && _mode == ProgressBarMode.MANUAL && value != _minimum)
        {
            _minimum = value;

            invalidateDisplayList();

            dispatchEvent(new Event("minimumChanged"));
        }
    }

    //----------------------------------
    //  mode
    //----------------------------------

    /**
     *  @private
     *  Storage for the mode property.
     */
    private var _mode:String = ProgressBarMode.EVENT;

    /**
     *  @private
     */
    private var modeChanged:Boolean = false;

    [Bindable("modeChanged")]
    [Inspectable(category="General", enumeration="event,polled,manual", defaultValue="event")]

    /**
     *  Specifies the method used to update the bar. 
     *  Use one of the following values in MXML:
     *
     *  <ul>
     *    <li><code>event</code> The control specified by the <code>source</code>
     *    property must dispatch progress and completed events.
     *    The ProgressBar control uses these events to update its status.
     *    The ProgressBar control only updates if the value of 
     *    the <code>source</code> property extends the EventDispatcher class.</li>
     *
     *    <li><code>polled</code> The <code>source</code> property must specify
     *    an object that exposes <code>bytesLoaded</code> and
     *    <code>bytesTotal</code> properties. The ProgressBar control
     *    calls these methods to update its status.</li>
     *
     *    <li><code>manual</code> You manually update the ProgressBar status.
     *    In this mode you specify the <code>maximum</code> and <code>minimum</code>
     *    properties and use the <code>setProgress()</code> property method to
     *    specify the status. This mode is often used when the <code>indeterminate</code>
     *    property is <code>true</code>.</li>
     *  </ul>
     *
     *  <p>In ActionScript, you can use use the following constants to set this property:
     *  <code>ProgressBarMode.EVENT</code>, <code>ProgressBarMode.POLLED</code>,
     *  and <code>ProgressBarMode.MANUAL</code>.</p>
     *
     *  @see mx.controls.ProgressBarMode
     *
     *  @default ProgressBarMode.EVENT
     */
    public function get mode():String
    {
        return _mode;
    }

    /**
     *  @private
     */
    public function set mode(value:String):void
    {
        if (value != _mode)
        {
            if (_mode == ProgressBarMode.POLLED)
                stopPolledMode = true;

            _mode = value;

            modeChanged = true;
            indeterminateChanged = true;

            invalidateProperties();
            invalidateDisplayList();
        }
    }

    //----------------------------------
    //  percentComplete
    //----------------------------------

    [Bindable("progress")]

    /**
     *  Percentage of process that is completed.The range is 0 to 100.
     *  Use the <code>setProgress()</code> method to change the percentage.
     */
    public function get percentComplete():Number
    {
        if (_value < _minimum || _maximum < _minimum)
            return 0;

        // Avoid divide by zero fault.
        if ((_maximum - _minimum) == 0)
            return 0;

        var perc:Number = 100 * (_value - _minimum) / (_maximum - _minimum);

        if (isNaN(perc) || perc < 0)
            return 0;
        else if (perc > 100)
            return 100;
        else
            return perc;
    }

    //----------------------------------
    //  source
    //----------------------------------

    /**
     *  @private
     *  Storage for the source property.
     */
    private var _source:Object;

    /**
     *  @private
     */
    private var _stringSource:String;

    /**
     *  @private
     */
    private var sourceChanged:Boolean = false;

    /**
     *  @private
     */
    private var stringSourceChanged:Boolean = false;

    [Bindable("sourceChanged")]
    [Inspectable(category="General")]

    /**
     *  Refers to the control that the ProgressBar is measuring the progress of. Use this property only in
     *  event and polled mode. A typical usage is to set this property to a Loader control.
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
        if (value is String)
        {
            _stringSource = String(value);
            try
            {
                value = document[_stringSource];
            }
            catch(e:Error)
            {
                stringSourceChanged = true; // Try again in commitProperties
            }
        }

        if (value)
        {
            _source = value;
            sourceChanged = true;
            modeChanged = true;
            indeterminateChanged = true;
            invalidateProperties();
            invalidateDisplayList();
        }
        else if (_source != null)
        {
            _source = null;
            sourceChanged = true;
            indeterminateChanged = true;
            invalidateProperties();
            invalidateDisplayList();
            pollTimer.reset();
        }
    }

    //----------------------------------
    //  value
    //----------------------------------

    /**
     *  @private
     *  Storage for the value property.
     */
    private var _value:Number = 0;

    [Bindable("change")]

    /**
     *  Read-only property that contains the amount of progress
     *  that has been made - between the minimum and maximum values.
     */
    public function get value():Number
    {
        return _value;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    override public function set visible(value:Boolean):void
    {
        super.visible = value;
                
        visibleChanged = true;
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
    override protected function createChildren():void
    {
        super.createChildren();

        if (!_content)
        {
            _content = new UIComponent();
            addChild(_content);
        }

        if (!_bar)
        {
            _bar = new UIComponent();
            _content.addChild(_bar);
        }

        if (!_barMask)
        {
            if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
            {
                var barMaskClass:Class = getStyle("maskSkin");
                _barMask = new barMaskClass();
            }
            else
            {
                _barMask = new UIComponent();
            }    

            _barMask.visible = true;
            _bar.addChild(DisplayObject(_barMask));
            UIComponent(_bar).mask = DisplayObject(_barMask);
        }        
        

        if (!_labelField)
        {
            _labelField = IUITextField(createInFontContext(UITextField));
            _labelField.styleName = this;
            addChild(DisplayObject(_labelField));
        }
    }

    /**
     *  @private
     */
    override protected function childrenCreated():void
    {
        super.childrenCreated();

        // Mark all of our skins as changed so they will get created.
        trackSkinChanged = true;
        barSkinChanged = true;
        indeterminateSkinChanged = true;
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        var invalidate:Boolean = false;

        super.styleChanged(styleProp);

        if (styleProp == null || styleProp == "styleName")
        {
            // Wholesale change, need to update all skins
            barSkinChanged = trackSkinChanged = indeterminateSkinChanged = true;
            invalidate = true;
        }
        else if (styleProp == "barSkin")
        {
            barSkinChanged = true;
            invalidate = true;
        }
        else if (styleProp == "trackSkin")
        {
            trackSkinChanged = true;
            invalidate = true;
        }
        else if (styleProp == "indeterminateSkin")
        {
            indeterminateSkinChanged = true;
            invalidate = true;
        }

        if (invalidate)
        {
            invalidateProperties();
            invalidateSize();
            invalidateDisplayList();
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();
        
        // if the font changed and we already created the label, we will need to 
        // destory it so it can be re-created, possibly in a different swf context.
        if (hasFontContextChanged() && _labelField != null)
        {
            var index:int = getChildIndex(DisplayObject(_labelField));
            removeChild(DisplayObject(_labelField));
            _labelField = IUITextField(createInFontContext(UITextField));
            _labelField.styleName = this;
            addChildAt(DisplayObject(_labelField), index);
        }
        
 
        if (trackSkinChanged)
        {
            trackSkinChanged = false;
            createTrack();
        }

        if (barSkinChanged)
        {
            barSkinChanged = false;
            createBar();
        }

        if (indeterminateSkinChanged)
        {
            indeterminateSkinChanged = false;
            createIndeterminateBar();
        }

        if (stringSourceChanged)
        {
            stringSourceChanged = false;
            try
            {
                _source = document[_stringSource];
            }
            catch(e:Error)
            {
                // If we fail here, then the source refers to an object that doesn't exist
                // on the document
            }
        }

        if (sourceChanged)
        {
            sourceChanged = false;
            dispatchEvent(new Event("sourceChanged"));
        }

        if (modeChanged)
        {
            modeChanged = false;
            if (_source)
            {
                if (_mode == ProgressBarMode.EVENT)
                {
                    if (_source is IEventDispatcher)
                    {
                        _source.addEventListener(ProgressEvent.PROGRESS, progressHandler);
                        _source.addEventListener(Event.COMPLETE, completeHandler);
                    }
                    else
                    {
                        // the Loader object is not yet initialized properly,
                        // as it is put on stage after the progressbar
                        // so make it go thru the source re-setting in draw
                        _source = null;
                    }
                }
                else
                {
                    _source.removeEventListener(ProgressEvent.PROGRESS, progressHandler);
                    _source.removeEventListener(Event.COMPLETE, completeHandler);
                }
            }

            if (_mode == ProgressBarMode.POLLED)
            {
                // weak listener in case the pbar is removed before the timer is reset()
                // safe because pollTimer lives in the pbar
                pollTimer.addEventListener(TimerEvent.TIMER, updatePolledHandler, false, 0, true);
                pollTimer.start();
            }
            else if (stopPolledMode)
            {
                stopPolledMode = false;
                pollTimer.removeEventListener(TimerEvent.TIMER, updatePolledHandler);
                pollTimer.reset();
            }

            dispatchEvent(new Event("modeChanged"));
        }
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        var prefWidth:Number;
        var prefHeight:Number;

        var minWidth:Number = NaN;
        var minHeight:Number = NaN;

        var trackHeight:Number = getStyle("trackHeight");

        var preferredTrackWidth:Number = _track.measuredWidth;
        var preferredTrackHeight:Number = isNaN(trackHeight) ?
                                          _track.measuredHeight:
                                          trackHeight;

        var horizontalGap:Number = getStyle("horizontalGap");
        var verticalGap:Number = getStyle("verticalGap");

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        var labelWidth:Number = getStyle("labelWidth");

        var lineMetrics:TextLineMetrics = measureText(predictLabelText());

        var textWidth:Number = isNaN(labelWidth) ?
                               lineMetrics.width + UITextField.TEXT_WIDTH_PADDING :
                               labelWidth;
        var textHeight:Number = lineMetrics.height + UITextField.TEXT_HEIGHT_PADDING;

        switch (labelPlacement)
        {
            case ProgressBarLabelPlacement.LEFT:
            case ProgressBarLabelPlacement.RIGHT:
            {
                prefWidth = textWidth + preferredTrackWidth +
                            paddingLeft + paddingRight + horizontalGap;
                prefHeight = Math.max(textHeight,preferredTrackHeight) +
                             paddingTop + paddingBottom;
                measuredMinWidth = prefWidth;
                break;
            }

            case ProgressBarLabelPlacement.CENTER:
            {
                prefWidth = Math.max(textWidth, preferredTrackWidth) +
                            paddingLeft + paddingRight + horizontalGap;
                prefHeight = Math.max(textHeight,preferredTrackHeight) +
                             paddingTop + paddingBottom;
                measuredMinWidth = textWidth;
                break;
            }
            default: /* ProgressBarLabelPlacement.TOP && ProgressBarLabelPlacement.BOTTOM */
            {
                prefWidth = Math.max(textWidth, preferredTrackWidth) +
                            paddingLeft + paddingRight;
                prefHeight = textHeight + preferredTrackHeight +
                             paddingTop + paddingBottom + verticalGap;
                measuredMinWidth = textWidth;
                break;
            }
        }

        measuredWidth = prefWidth;
        measuredMinHeight = measuredHeight = prefHeight;

        if (!isNaN(minWidth))
            measuredMinWidth = minWidth;
        if (!isNaN(minHeight))
            measuredMinHeight = minHeight;
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var horizontalGap:Number = getStyle("horizontalGap");
        var verticalGap:Number = getStyle("verticalGap");

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        var left:Number = paddingLeft;
        var top:Number = paddingTop;

        var labelWidth:Number = getStyle("labelWidth");
        var trackHeight:Number = getStyle("trackHeight");

        trackHeight = isNaN(trackHeight) ?
                      _track.measuredHeight :
                      trackHeight;

        var lineMetrics:TextLineMetrics = measureText(predictLabelText());

        var textWidth:Number = isNaN(labelWidth) ?
                               lineMetrics.width + UITextField.TEXT_WIDTH_PADDING :
                               labelWidth;

        var textHeight:Number = lineMetrics.height + UITextField.TEXT_HEIGHT_PADDING;

        switch (labelPlacement)
        {
            case ProgressBarLabelPlacement.TOP:
            {
                _labelField.move(left,top);
                _labelField.setActualSize(textWidth, textHeight);
                _content.move(left, top + textHeight + verticalGap);
                layoutContent(unscaledWidth - left - paddingRight, trackHeight);
                break;
            }

            case ProgressBarLabelPlacement.RIGHT:
            {
                var cWidth:Number = unscaledWidth - left -
                                    paddingRight - textWidth - horizontalGap;
                _labelField.move(left + cWidth + horizontalGap,
                                 (unscaledHeight - textHeight) / 2);
                _labelField.setActualSize(textWidth, textHeight);
                _content.move(left,top + (textHeight - trackHeight) / 2);
                layoutContent(cWidth, trackHeight);
                break;
            }

            case ProgressBarLabelPlacement.LEFT:
            {
                _labelField.move(left, top + (unscaledHeight - textHeight) / 2);
                _labelField.setActualSize(textWidth, textHeight);
                _content.move(left + textWidth + horizontalGap,
                              top + (textHeight - trackHeight) / 2);
                layoutContent(unscaledWidth - left - textWidth -
                              verticalGap - paddingRight, trackHeight);
                break;
            }

            case ProgressBarLabelPlacement.CENTER:
            {
                _labelField.move((unscaledWidth - textWidth) / 2,
                                 (unscaledHeight - textHeight) / 2);
                _labelField.setActualSize(textWidth, textHeight);
                _content.move(left,top);
                layoutContent(unscaledWidth - paddingRight,
                              unscaledHeight - paddingBottom);
                break;
            }

            default: /* case ProgressBarLabelPlacement.BOTTOM */
            {
                _labelField.move(left,top + trackHeight + verticalGap);
                _labelField.setActualSize(textWidth, textHeight);
                _content.move(left, top);
                layoutContent(unscaledWidth - left - paddingRight, trackHeight);
                break;
            }
        }

        //layout mask
        if (_barMask)
        {
            _barMask.move(0,0);
            
            if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
            {
                _barMask.setActualSize(_track.width, _track.height);
            }
            else
            {
                var g:Graphics = UIComponent(_barMask).graphics;
                g.clear();
                g.beginFill(0xFFFF00);
                g.drawRect(1, 1, _track.width - 2, _track.height - 2);
                g.endFill();
            }
        }
        // Print position/sizes of children
        //trace("_labelField x",_labelField.x,"y",_labelField.y,"w",_labelField.width,"h",_labelField.height);
        //trace("_content x",_content.x,"y",_content.y,"w",_content.width,"h",_content.height);

        _labelField.text = getFullLabelText();
        // TODO redundant? the IF below does this
        _indeterminateBar.visible = _indeterminate;

        if (indeterminateChanged || visibleChanged)
        {
            indeterminateChanged = false;
            visibleChanged = false;

            _indeterminateBar.visible = _indeterminate;

            if (_indeterminate && _source == null && _mode == ProgressBarMode.EVENT && visible)
                startPlayingIndeterminate();
            else
                stopPlayingIndeterminate();
        }

        if (_indeterminate)
        {
            _determinateBar.setActualSize(_track.width, _track.height);
        }
        else
        {
            var w:Number = Math.max(0, _track.width * percentComplete / 100);
            _determinateBar.setActualSize(w, _track.height);
            _determinateBar.x = direction == ProgressBarDirection.RIGHT ?
                                0 :
                                _track.width - w;
        }
    }

    /**
     *  @private
     */
    override protected function resourcesChanged():void
    {
        super.resourcesChanged();

        label = labelOverride;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Sets the state of the bar to reflect the amount of progress made
     *  when using manual mode.
     *  The <code>value</code> argument is assigned to the <code>value</code>
     *  property and the <code>maximum</code> argument is assigned to the
     *  <code>maximum</code> property.
     *  The <code>minimum</code> property is not altered.
     *
     *  @param value Current value.
     *
     *  @param maximum Total or target value.
     */
    public function setProgress(value:Number, total:Number):void
    {
        if (_mode == ProgressBarMode.MANUAL)
            _setProgress(value, total);
    }

    /**
     *  @private
     *  Changes the value and the maximum properties.
     */
    private function _setProgress(value:Number, maximum:Number):void
    {
        if (enabled && !isNaN(value) && !isNaN(maximum))
        {
            _value = value;
            _maximum = maximum;

            // Dipatch an Event of type "change".
            dispatchEvent(new Event(Event.CHANGE));

            // Dispatch a Progress
            var progressEvent:ProgressEvent = new ProgressEvent(ProgressEvent.PROGRESS);
            progressEvent.bytesLoaded = value;
            progressEvent.bytesTotal = maximum;
            dispatchEvent(progressEvent);

            if (_indeterminate)
                startPlayingIndeterminate();

            if (_value == _maximum && _value > 0)
            {
                if (_indeterminate)
                    stopPlayingIndeterminate();

                if (mode != ProgressBarMode.EVENT) // We get our own complete event when in event mode
                    dispatchEvent(new Event(Event.COMPLETE));
            }

            invalidateDisplayList();
        }
    }

    /**
     *  @private
     */
    private function createTrack():void
    {
        if (_track)
        {
            _content.removeChild(DisplayObject(_track));
            _track = null;
        }

        // Create the track frame
        var trackClass:Class = getStyle('trackSkin');
        if (trackClass)
        {
            _track = new trackClass();
            if (_track is ISimpleStyleClient)
                ISimpleStyleClient(_track).styleName = this;
            _content.addChildAt(DisplayObject(_track), 0);
        }
    }

    /**
     *  @private
     */
    private function createBar():void
    {
        if (_determinateBar)
        {
            _bar.removeChild(DisplayObject(_determinateBar));
            _determinateBar = null;
        }

        // Create the determinate bar
        var barClass:Class = getStyle('barSkin');
        if (barClass)
        {
            _determinateBar = new barClass();
            if (_determinateBar is ISimpleStyleClient)
                ISimpleStyleClient(_determinateBar).styleName = this;
            _bar.addChild(DisplayObject(_determinateBar));
        }
    }

    /**
     *  @private
     */
    private function createIndeterminateBar():void
    {
        if (_indeterminateBar)
        {
            _bar.removeChild(DisplayObject(_indeterminateBar));
            _indeterminateBar = null;
        }

        // Create the indeterminate bar
        var indeterminateClass:Class = getStyle('indeterminateSkin');
        if (indeterminateClass)
        {
            _indeterminateBar = new indeterminateClass();
            if (_indeterminateBar is ISimpleStyleClient)
                ISimpleStyleClient(_indeterminateBar).styleName = this;
            _indeterminateBar.visible = false;
            _bar.addChild(DisplayObject(_indeterminateBar));
        }
    }

    /**
     *  @private
     */
    private function layoutContent(newWidth:Number, newHeight:Number):void
    {
        _track.move(0, 0);
        _track.setActualSize(newWidth, newHeight);

        _bar.move(0, 0);
        _determinateBar.move(0, 0);
        _indeterminateBar.setActualSize(newWidth + getStyle("indeterminateMoveInterval"), newHeight);
    }

    /**
     *  @private
     */
    private function getFullLabelText():String
    {
        var current:Number = Math.max(_value /* - _minimum */,0);
        var total:Number = Math.max(_maximum /* - _minimum */,0);
        var labelText:String = label;

        if (labelText)
        {
            if (_indeterminate)
            {
                labelText = labelText.replace("%1", String(Math.floor(current / _conversion)));
                labelText = labelText.replace("%2", "??");
                labelText = labelText.replace("%3", "");
                labelText = labelText.replace("%%", "");
            }
            else
            {
                labelText = labelText.replace("%1", String(Math.floor(current / _conversion)));
                labelText = labelText.replace("%2", String(Math.floor(total / _conversion)));
                labelText = labelText.replace("%3", String(Math.floor(percentComplete)));
                labelText = labelText.replace("%%", "%");
            }
        }

        return labelText;
    }

    /**
     *  @private
     *  Make a good guess at the largest size of the label based on which placeholders are present
     */
    private function predictLabelText():String
    {
        // The label will be null if there are no resources.
        if (label == null)
            return "";
        
        var labelText:String = label;
    
        var largestValue:Number;
        if (_maximum != 0)
            largestValue = _maximum;
        else
            largestValue = 100000;

        if (labelText)
        {
            if (_indeterminate)
            {
                labelText = labelText.replace("%1", String(Math.floor(largestValue / _conversion)));
                labelText = labelText.replace("%2", "??");
                labelText = labelText.replace("%3", "");
                labelText = labelText.replace("%%", "");
            }
            else
            {
                labelText = labelText.replace("%1", String(Math.floor(largestValue / _conversion)));
                labelText = labelText.replace("%2", String(Math.floor(largestValue / _conversion)));
                labelText = labelText.replace("%3", "100");
                labelText = labelText.replace("%%", "%");
            }
        }

        var actualText:String = getFullLabelText();

        // Return the longer of the two strings
        if (labelText.length > actualText.length)
            return labelText;
        else
            return actualText;
    }

    /**
     *  @private
     */
    private function startPlayingIndeterminate():void
    {
        if (!indeterminatePlaying)
        {
            indeterminatePlaying = true;

            // weak listener in case the pbar is removed before the timer is reset()
            // safe because pollTimer lives in the pbar
            pollTimer.addEventListener(TimerEvent.TIMER, updateIndeterminateHandler, false, 0, true);
            pollTimer.start();
        }
    }

    /**
     *  @private
     */
    private function stopPlayingIndeterminate():void
    {
        if (indeterminatePlaying)
        {
            indeterminatePlaying = false;

            pollTimer.removeEventListener(TimerEvent.TIMER, updateIndeterminateHandler);

            if (_mode != ProgressBarMode.POLLED)
                pollTimer.reset();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  "progress" event handler for event mode
     */
    private function progressHandler(event:ProgressEvent):void
    {
        _setProgress(event.bytesLoaded, event.bytesTotal);
    }

    /**
     *  @private
     *  "complete" event handler for event mode
     */
    private function completeHandler(event:Event):void
    {
        dispatchEvent(event);
        invalidateDisplayList();
    }

    /**
     *  @private
     */
    private function updateIndeterminateHandler(event:Event):void
    {
        if (_indeterminateBar.x < 1)
            _indeterminateBar.x += 1;
        else
            _indeterminateBar.x = - (getStyle("indeterminateMoveInterval") - 2);
    }

    /**
     *  @private
     *  Callback method for polled mode.
     */
    private function updatePolledHandler(event:Event):void
    {
        if (_source)
        {
            var comp:Object = _source;

            var bytesLoaded:Number = comp.bytesLoaded;
            var bytesTotal:Number = comp.bytesTotal;

            if (!isNaN(bytesLoaded) && !isNaN(bytesTotal))
            {
                _setProgress(bytesLoaded, bytesTotal);

                // 0 is the size of an empty movie clip??
                if (percentComplete >= 100 && _value > 0)
                    pollTimer.reset();
            }
        }
    }

}

}
