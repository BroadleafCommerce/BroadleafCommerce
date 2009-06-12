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

package mx.controls.scrollClasses
{

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.geom.Point;
import flash.ui.Keyboard;
import flash.utils.Timer;
import mx.controls.Button;
import mx.core.FlexVersion;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDetail;
import mx.styles.ISimpleStyleClient;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

include "../../styles/metadata/SkinStyles.as"

/**
 *  Name of the class to use as the default skin for the down arrow button of 
 *  the scroll bar.
 * 
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="downArrowSkin", type="Class", inherit="no", states="up, over, down, disabled")]

/**
 *  Name of the class to use as the skin for the down arrow button of the 
 *  scroll bar when it is disabled. 
 * 
 *  If you change the skin, either graphically or programmatically, 
 *  you should ensure that the new skin is the same height 
 *  (for horizontal ScrollBars) or width (for vertical ScrollBars) as the track.
 * 
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="downArrowDisabledSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the down arrow button of the 
 *  scroll bar when you click the arrow button
 * . 
 *  If you change the skin, either graphically or programmatically, 
 *  you should ensure that the new skin is the same height 
 *  (for horizontal ScrollBars) or width (for vertical ScrollBars) as the track.
 *  
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="downArrowDownSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the down arrow button of the 
 *  scroll bar when the mouse pointer is over the arrow button. 
 * 
 *  If you change the skin, either graphically or programmatically, 
 *  you should ensure that the new skin is the same height 
 *  (for horizontal ScrollBars) or width (for vertical ScrollBars) as the track.
 *  
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="downArrowOverSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the down arrow button of 
 *  the scroll bar. 
 *  
 *  If you change the skin, either graphically or programmatically, 
 *  you should ensure that the new skin is the same height 
 *  (for horizontal ScrollBars) or width (for vertical ScrollBars) as the track.
 *  
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="downArrowUpSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the default skin for the down arrow button of 
 *  the scroll bar. 
 *  
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="thumbSkin", type="Class", inherit="no", states="up, over, down")]

/**
 *  Name of the class to use as the skin for the thumb of the scroll bar 
 *  when you click the thumb. 
 * 
 *  @default mx.skins.halo.ScrollThumbSkin
 */
[Style(name="thumbDownSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the icon for the thumb of the scroll bar. 
 *  
 *  @default "undefined"
 */
[Style(name="thumbIcon", type="Class", inherit="no")]

/**
 *  The number of pixels to offset the scroll thumb from the center of the scroll bar. 
 * 
 *  @default 0 
 */
[Style(name="thumbOffset", type="Number", inherit="no")]

/**
 *  Name of the class to use as the skin for the thumb of the scroll bar 
 *  when the mouse pointer is over the thumb. 
 * 
 *  @default mx.skins.halo.ScrollThumbSkin
 */
[Style(name="thumbOverSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the thumb of the scroll bar.
 *  
 *  @default mx.skins.halo.ScrollThumbSkin
 */
[Style(name="thumbUpSkin", type="Class", inherit="no")]

/**
 *  The colors of the track, as an array of two colors.
 *  You can use the same color twice for a solid track color.
 * 
 *  @default [0x94999b, 0xe7e7e7]
 */
[Style(name="trackColors", type="Array", arrayType="uint", format="Color", inherit="no")]


/**
 *  Name of the class to use as the default skin for the track of the scroll bar. 
 * 
 *  @default mx.skins.halo.ScrollTrackSkin
 */
[Style(name="trackSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the track of the scroll bar 
 *  when the scroll bar is disabled.
 * 
 *  @default undefined
 */
[Style(name="trackDisabledSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the track of the scroll bar 
 *  when you click on the track.
 * 
 *  @default undefined
 */
[Style(name="trackDownSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the track of the scroll bar 
 *  when the mouse pointer is over the scroll bar.
 * 
 *  @default undefined
 */
[Style(name="trackOverSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the track of the scroll bar.
 * 
 *  @default undefined
 */
[Style(name="trackUpSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the default skin for the up arrow button of the scroll bar. 
 * 
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="upArrowSkin", type="Class", inherit="no", states="up, over, down, disabled")]

/**
 *  Name of the class to use as the skin for the up arrow button of the scroll bar 
 *  when it is disabled. 
 * 
 *  If you change the skin, either graphically or programmatically, 
 *  you should ensure that the new skin is the same height 
 *  (for horizontal ScrollBars) or width (for vertical ScrollBars) as the track.
 *  
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="upArrowDisabledSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the up arrow button of the scroll bar 
 *  when you click the arrow button. 
 * 
 *  If you change the skin, either graphically or programmatically, 
 *  you should ensure that the new skin is the same height 
 *  (for horizontal ScrollBars) or width (for vertical ScrollBars) as the track.
 *  
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="upArrowDownSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the up arrow button of the scroll bar 
 *  when the mouse pointer is over the arrow button.
 *  
 *  If you change the skin, either graphically or programmatically, 
 *  you should ensure that the new skin is the same height 
 *  (for horizontal ScrollBars) or width (for vertical ScrollBars) as the track.
 *  
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="upArrowOverSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the up arrow button of the 
 *  scroll bar. 
 *  
 *  If you change the skin, either graphically or programmatically, 
 *  you should ensure that the new skin is the same height 
 *  (for horizontal ScrollBars) or width (for vertical ScrollBars) as the track.
 *    
 *  @default mx.skins.halo.ScrollArrowSkin
 */
[Style(name="upArrowUpSkin", type="Class", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="doubleClickEnabled", kind="property")]

[Exclude(name="errorColor", kind="style")]
[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]

/**
 *  The ScrollBar class is the base class for the HScrollBar and VScrollBar
 *  controls.
 *  A ScrollBar consists of two arrow buttons, a track between them,
 *  and a variable-size scroll thumb. The scroll thumb can move by 
 *  clicking on either of the two arrow buttons, dragging the scroll thumb
 *  along the track, or clicking on the track. 
 *
 *  <p>The width of a scroll bar is equal to the largest width of its subcomponents 
 *  (up arrow, down arrow, thumb, and track). 
 *  Every subcomponent is centered in the scroll bar.</p>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:ScrollBar&gt;</code> tag inherits all of the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:ScrollBar
 *    <strong>Properties</strong>
 *    direction="vertical|horizontal"
 *    lineScrollSize="1"
 *    maxScrollPosition="0"
 *    minScrollPosition="0"
 *    pageScrollSize="<i>Reset to the pageSize parameter of setScrollProperties</i>"
 *    pageSize="0"
 *    scrollPosition="0"
 * 
 *    <strong>Styles</strong>
 *    borderColor="0xB7BABC" 
 *    cornerRadius="0" 
 *    disabledIconColor="0x999999"
 *    downArrowDisabledSkin="mx.skins.halo.ScrollArrowSkin"
 *    downArrowDownSkin="mx.skins.halo.ScrollArrowSkin"
 *    downArrowOverSkin="mx.skins.halo.ScrollArrowSkin"
 *    downArrowUpSkin="mx.skins.halo.ScrollArrowSkin"
 *    fillAlphas="[0.6, 0.4]" 
 *    fillColors="[0xFFFFFF, 0xCCCCCC]" 
 *    highlightAlphas="[0.3, 0.0]" 
 *    iconColor="0x111111"
 *    thumbDownSkin="mx.skins.halo.ScrollThumbSkin"
 *    thumbIcon="undefined"
 *    thumbOffset="0"
 *    thumbOverSkin="mx.skins.halo.ScrollThumbSkin"
 *    thumbUpSkin="mx.skins.halo.ScrollThumbSkin"
 *    trackColors="[0x94999b, 0xe7e7e7]"
 *    trackSkin="mx.skins.halo.ScrollTrackSkin"
 *    upArrowDisabledSkin="mx.skins.halo.ScrollArrowSkin"
 *    upArrowDownSkin="mx.skins.halo.ScrollArrowSkin"
 *    upArrowOverSkin="mx.skins.halo.ScrollArrowSkin"
 *    upArrowUpSkin="mx.skins.halo.ScrollArrowSkin"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.controls.HScrollBar
 *  @see mx.controls.VScrollBar
 *  @see mx.controls.Button
 *  @see mx.controls.scrollClasses.ScrollThumb
 *
 */
public class ScrollBar extends UIComponent
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The width of a vertical scrollbar, or the height of a horizontal
     *  scrollbar, in pixels.
     */
    public static const THICKNESS:Number = 16;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor. 
     */
    public function ScrollBar()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  The up arrow button.
     */
    mx_internal var upArrow:Button;

    /**
     *  @private
     *  The down arrow button.
     */
    mx_internal var downArrow:Button;

    /**
     *  @private
     *  The scroll track
     */
    mx_internal var scrollTrack:Button;

    /**
     *  @private
     *  The scroll thumb
     */
    mx_internal var scrollThumb:ScrollThumb;

    /**
     *  @private
     *  Used to keep track of minimums because of the orientation change.
     */
    mx_internal var _minWidth:Number = 16;
    
    /**
     *  @private
     */
    mx_internal var _minHeight:Number = 32;

    /**
     *  @private
     *  true if servicing a scroll event.
     */
    mx_internal var isScrolling:Boolean;

    /**
     *  @private
     *  Timer used to autoscroll when holding the mouse down on the track.
     */
    private var trackScrollTimer:Timer;

    /**
     *  @private
     *  The direction we're going when in track scroll repeat.
     */
    private var trackScrollRepeatDirection:int;

    /**
     *  @private
     *  The direction we're going when in track scroll repeat.
     */
    private var trackScrolling:Boolean = false;

    /**
     *  @private
     *  Where the mouse is on the track.
     */
    private var trackPosition:Number;

    /**
     *  @private
     *  Old position, used to compute deltas when button presses are auto
     *  repeated
     */
    mx_internal var oldPosition:Number;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  doubleClickEnabled
    //----------------------------------

    /**
     *  @private
     *  Scrollbars cannot be doubleClickEnabled.
     *  It messes up fast clicking on its buttons.
     */
    override public function set doubleClickEnabled(value:Boolean):void
    {
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  @private
     *  Turn off buttons, or turn on buttons and resync thumb.
     */
    override public function set enabled(value:Boolean):void
    {
        super.enabled = value;

        invalidateDisplayList();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  direction
    //----------------------------------

    /**
     *  @private
     *  Storage for the direction property.
     */
    private var _direction:String = ScrollBarDirection.VERTICAL;

    [Bindable("directionChanged")]
    [Inspectable(category="General", enumeration="vertical,horizontal", defaultValue="vertical")]

    /**
     *  Specifies whether the ScrollBar is for horizontal or vertical scrolling.
     *  Valid values in MXML are <code>"vertical"</code> and <code>"horizontal"</code>.
     *
     *  <p>In ActionScript, you use the following constants
     *  to set this property:
     *  <code>ScrollBarDirection.VERTICAL</code> and
     *  <code>ScrollBarDirection.HORIZONTAL</code>.</p>
     *
     *  @default ScrollBarDirection.VERTICAL
     *
     *  @see mx.controls.scrollClasses.ScrollBarDirection
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
        
        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("directionChanged"));
    }
    
    //----------------------------------
    //  downArrowStyleFilters
    //----------------------------------

    /**
     *  Set of styles to pass from the ScrollBar to the down arrow.
     *  @see mx.styles.StyleProxy
     */
    protected function get downArrowStyleFilters():Object
    {
        return null;
    }    

    //----------------------------------
    //  lineMinusDetail
    //----------------------------------

    /**
     *  @private
     *  String used to set the detail property of a ScrollEvent.
     */
    mx_internal function get lineMinusDetail():String
    {
        return direction == ScrollBarDirection.VERTICAL ?
               ScrollEventDetail.LINE_UP :
               ScrollEventDetail.LINE_LEFT;
    }

    //----------------------------------
    //  linePlusDetail
    //----------------------------------

    /**
     *  @private
     *  String used to set the detail property of a ScrollEvent.
     */
    mx_internal function get linePlusDetail():String
    {
        return direction == ScrollBarDirection.VERTICAL ?
               ScrollEventDetail.LINE_DOWN :
               ScrollEventDetail.LINE_RIGHT;
    }

    //----------------------------------
    //  lineScrollSize
    //----------------------------------

    /**
     *  @private
     *  Storage for the lineScrollSize property.
     */
    private var _lineScrollSize:Number = 1;

    [Inspectable(category="Other", defaultValue="1")]

    /**
     *  Amount to scroll when an arrow button is pressed, in pixels.
     *
     *  @default 1
     */
    public function get lineScrollSize():Number
    {
        return _lineScrollSize;
    }

    /**
     *  @private
     */
    public function set lineScrollSize(value:Number):void
    {
        _lineScrollSize = value;
    }

    //----------------------------------
    //  maxDetail
    //----------------------------------

    /**
     *  @private
     *  String used to set the detail property of a ScrollEvent.
     */
    private function get maxDetail():String
    {
        return direction == ScrollBarDirection.VERTICAL ?
               ScrollEventDetail.AT_BOTTOM :
               ScrollEventDetail.AT_RIGHT;
    }

    //----------------------------------
    //  maxScrollPosition
    //----------------------------------

    /**
     *  @private
     *  Storage for the maxScrollPosition property.
     */
    private var _maxScrollPosition:Number = 0;

    [Inspectable(category="Other", defaultValue="0")]

    /**
     *  Number which represents the maximum scroll position.
     *
     *  @default 0
     */
    public function get maxScrollPosition():Number
    {
        return _maxScrollPosition;
    }

    /**
     *  @private
     */
    public function set maxScrollPosition(value:Number):void
    {
        _maxScrollPosition = value;
    }

    //----------------------------------
    //  minDetail
    //----------------------------------

    /**
     *  @private
     *  String used to set the detail property of a ScrollEvent.
     */
    private function get minDetail():String
    {
        return direction == ScrollBarDirection.VERTICAL ?
               ScrollEventDetail.AT_TOP :
               ScrollEventDetail.AT_LEFT;
    }

    //----------------------------------
    //  minScrollPosition
    //----------------------------------

    /**
     *  @private
     *  Storage for the minScrollPosition property.
     */
    private var _minScrollPosition:Number = 0;

    [Inspectable(category="Other", defaultValue="0")]

    /**
     *  Number that represents the minimum scroll position.
     *
     *  @default 0
     */
    public function get minScrollPosition():Number
    {
        return _minScrollPosition;
    }

    /**
     *  @private
     */
    public function set minScrollPosition(value:Number):void
    {
        _minScrollPosition = value;
    }

    //----------------------------------
    //  pageMinusDetail
    //----------------------------------

    /**
     *  @private
     *  String used to set the detail property of a ScrollEvent.
     */
    mx_internal function get pageMinusDetail():String
    {
        return direction == ScrollBarDirection.VERTICAL ?
               ScrollEventDetail.PAGE_UP :
               ScrollEventDetail.PAGE_LEFT;
    }

    //----------------------------------
    //  pagePlusDetail
    //----------------------------------

    /**
     *  @private
     *  String used to set the detail property of a ScrollEvent.
     *  Can be <code>ScrollEventDetail.PAGE_RIGHT</code> or
     *  <code>ScrollEventDetail.PAGE_DOWN</code>.
     */
    mx_internal function get pagePlusDetail():String
    {
        return direction == ScrollBarDirection.VERTICAL ?
               ScrollEventDetail.PAGE_DOWN :
               ScrollEventDetail.PAGE_RIGHT;
    }

    //----------------------------------
    //  pageSize
    //----------------------------------

    /**
     *  @private
     *  Storage for the pageSize property.
     */
    private var _pageSize:Number = 0;

    [Inspectable(category="Other", defaultValue="0")]

    /**
     *  The number of lines equivalent to one page.
     *
     *  @default 0
     */
    public function get pageSize():Number
    {
        return _pageSize;
    }

    /**
     *  @private
     */
    public function set pageSize(value:Number):void
    {
        _pageSize = value;
    }

    //----------------------------------
    //  pageScrollSize
    //----------------------------------

    /**
     *  @private
     *  Storage for the pageScrollSize property.l
     */
    private var _pageScrollSize:Number = 0;

    [Inspectable(category="Other", defaultValue="0")]

    /**
     *  Amount to move the scroll thumb when the scroll bar 
     *  track is pressed, in pixels.
     *
     *  @default 0
     */
    public function get pageScrollSize():Number
    {
        return _pageScrollSize;
    }

    /**
     *  @private
     */
    public function set pageScrollSize(value:Number):void
    {
        _pageScrollSize = value;
    }

    //----------------------------------
    //  scrollPosition
    //----------------------------------

    /**
     *  @private
     *  Storage for the scrollPosition property.
     */
    private var _scrollPosition:Number = 0;

    [Inspectable(category="Other", defaultValue="0")]

    /**
     *  Number that represents the current scroll position.
     * 
     *  The value is between <code>minScrollPosition</code> and
     *  <code>maxScrollPosition</code> inclusively.
     *  
     *  @default 0
     */
    public function get scrollPosition():Number
    {
        return _scrollPosition;
    }

    /**
     *  @private
     */
    public function set scrollPosition(value:Number):void
    {
        _scrollPosition = value;

        if (scrollThumb)
        {
            // Turn on bitmap caching whenever we start scrolling.  Turn it
            // off whenever we resize the scrollbar (because caching hurts
            // performance during a resize animation)
            if (!cacheAsBitmap)
                cacheHeuristic = scrollThumb.cacheHeuristic = true;

            if (!isScrolling)
            {
                // Update thumb.
                value = Math.min(value, maxScrollPosition);
                value = Math.max(value, minScrollPosition);

                var denom:Number = maxScrollPosition - minScrollPosition;
                var y:Number = (denom == 0 || isNaN(denom)) ? 0 :
                    ((value - minScrollPosition) * (trackHeight - scrollThumb.height) /
                    (denom)) + trackY;

                var x:Number = (virtualWidth - scrollThumb.width) / 2  + getStyle("thumbOffset");
                scrollThumb.move(Math.round(x), Math.round(y));
            }
        }
    }

    //----------------------------------
    //  thumbStyleFilters
    //----------------------------------

    /**
     *  Set of styles to pass from the ScrollBar to the thumb.
     *  @see mx.styles.StyleProxy
     */
    protected function get thumbStyleFilters():Object 
    {
        return null;
    }

    //----------------------------------
    //  trackHeight
    //----------------------------------

    /**
     *  @private
     */
    private function get trackHeight():Number
    {
        return virtualHeight -
               (upArrow.getExplicitOrMeasuredHeight() +
                downArrow.getExplicitOrMeasuredHeight());
    }

    //----------------------------------
    //  trackY
    //----------------------------------

    /**
     *  @private
     */
    private function get trackY():Number
    {
        return upArrow.getExplicitOrMeasuredHeight();
    }

    //----------------------------------
    //  upArrowStyleFilters
    //----------------------------------

    /**
     *  Set of styles to pass from the ScrollBar to the up arrow.
     *  @see mx.styles.StyleProxy
     */
    protected function get upArrowStyleFilters():Object
    {
        return null;
    }

    //----------------------------------
    //  virtualHeight
    //----------------------------------

    /**
     *  @private
     *  For internal use only.
     *  Used by horizontal bar to deal with rotation.
     */
    mx_internal function get virtualHeight():Number
    {
        return unscaledHeight;
    }
    
    //----------------------------------
    //  virtualWidth
    //----------------------------------

    /**
     *  @private
     *  For internal use only.
     *  Used by horizontal bar to deal with rotation.
     */
    mx_internal function get virtualWidth():Number
    {
        return unscaledWidth;
    }
    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Create child objects.
     */
    override protected function createChildren():void
    {
        super.createChildren();

        // Create the scroll track.
        if (!scrollTrack)
        {
            scrollTrack = new Button();
            scrollTrack.focusEnabled = false;
            scrollTrack.skinName = "trackSkin";
            scrollTrack.upSkinName = "trackUpSkin";
            scrollTrack.overSkinName = "trackOverSkin";
            scrollTrack.downSkinName = "trackDownSkin";
            scrollTrack.disabledSkinName = "trackDisabledSkin";
            
            if (scrollTrack is ISimpleStyleClient)
                ISimpleStyleClient(scrollTrack).styleName = this;

            addChild(scrollTrack);
            
            scrollTrack.validateProperties();
        }

        // Create the up-arrow button, layered above the track.
        if (!upArrow)
        {
            upArrow = new Button();

            // It will get enabled later in setScrollProperties().
            upArrow.enabled = false;

            // Holding it down will cause continuous scrolling.
            upArrow.autoRepeat = true;

            upArrow.focusEnabled = false;

            // This button is a 4-state Button
            // that by default uses the ScrollArrowSkin.
            upArrow.upSkinName = "upArrowUpSkin";
            upArrow.overSkinName = "upArrowOverSkin";
            upArrow.downSkinName = "upArrowDownSkin";
            upArrow.disabledSkinName = "upArrowDisabledSkin";
            upArrow.skinName = "upArrowSkin";
            upArrow.upIconName = "";
            upArrow.overIconName = "";
            upArrow.downIconName = "";
            upArrow.disabledIconName = "";
            
            addChild(upArrow);

            upArrow.styleName = new StyleProxy(this, upArrowStyleFilters);

            upArrow.validateProperties();
            upArrow.addEventListener(FlexEvent.BUTTON_DOWN, upArrow_buttonDownHandler);
        }

        // Create the down-arrow button, layered above the track.
        if (!downArrow)
        {
            downArrow = new Button();
            
            // It will get enabled later in setScrollProperties().
            downArrow.enabled = false;

            // Holding it down will cause continuous scrolling.
            downArrow.autoRepeat = true;

            downArrow.focusEnabled = false;

            // This button is a 4-state Button
            // that by default uses the ScrollArrowSkin.
            downArrow.upSkinName = "downArrowUpSkin";
            downArrow.overSkinName = "downArrowOverSkin";
            downArrow.downSkinName = "downArrowDownSkin";
            downArrow.disabledSkinName = "downArrowDisabledSkin";
            downArrow.skinName = "downArrowSkin";
            downArrow.upIconName = "";
            downArrow.overIconName = "";
            downArrow.downIconName = "";
            downArrow.disabledIconName = "";

            addChild(downArrow);

            downArrow.styleName = new StyleProxy(this, downArrowStyleFilters);

            downArrow.validateProperties();
            downArrow.addEventListener(FlexEvent.BUTTON_DOWN, downArrow_buttonDownHandler);
        }
    }

    /**
     *  @private
     *  Determine our min width/height based on the up and down
     *  arrow sizes.
     */
    override protected function measure():void
    {
        super.measure();
        
        //make sure arrows have required values
        upArrow.validateSize();
        downArrow.validateSize();
        scrollTrack.validateSize();
        
        if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
        {
            _minWidth = scrollThumb ? scrollThumb.getExplicitOrMeasuredWidth() : 0;
            _minWidth = Math.max(scrollTrack.getExplicitOrMeasuredWidth(), upArrow.getExplicitOrMeasuredWidth(),
                                 downArrow.getExplicitOrMeasuredWidth(), _minWidth); 
        }
        else
        {
            _minWidth = upArrow.getExplicitOrMeasuredWidth();
        }
        _minHeight = upArrow.getExplicitOrMeasuredHeight() +
                     downArrow.getExplicitOrMeasuredHeight();
    }
    
    /**
     *  @private
     *  Size changed so re-position everything.
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        if ($height == 1)
            return;

        if (!upArrow)
            return;

        super.updateDisplayList(unscaledWidth, unscaledHeight);

        // Turn on bitmap caching whenever we start scrolling.  Turn it
        // off whenever we resize the scrollbar (because caching hurts
        // performance during a resize animation)
        if (cacheAsBitmap)
            cacheHeuristic = scrollThumb.cacheHeuristic = false;
       
        upArrow.setActualSize(upArrow.getExplicitOrMeasuredWidth(),
                              upArrow.getExplicitOrMeasuredHeight());
        
        if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
            upArrow.move((virtualWidth - upArrow.width) / 2, 0);
        else
            upArrow.move(0,0);

        scrollTrack.setActualSize(scrollTrack.getExplicitOrMeasuredWidth(), virtualHeight);

        if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
            scrollTrack.x = (virtualWidth - scrollTrack.width) / 2;
        scrollTrack.y = 0;        
     
        downArrow.setActualSize(downArrow.getExplicitOrMeasuredWidth(),
                                downArrow.getExplicitOrMeasuredHeight());
        if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
        {
            downArrow.move((virtualWidth - downArrow.width) / 2,
                            virtualHeight - downArrow.getExplicitOrMeasuredHeight());
        }
        else
        {
            downArrow.move(0, virtualHeight - downArrow.getExplicitOrMeasuredHeight());
        }


        setScrollProperties(pageSize, minScrollPosition,
                            maxScrollPosition, _pageScrollSize);

        // Reset thumb position.
        scrollPosition = _scrollPosition;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Sets the range and viewport size of the ScrollBar control. 
     * 
     *  The ScrollBar control updates the state of the arrow buttons and 
     *  size of the scroll thumb accordingly.
     *
     *  @param pageSize Number which represents the size of one page. 
     *
     *  @param minScrollPosition Number which represents the bottom of the 
     *  scrolling range.
     *
     *  @param maxScrollPosition Number which represetns the top of the 
     *  scrolling range.
     *
     *  @param pageScrollSize Number which represents the increment to move when 
     *  the scroll track is pressed.
     *
     */
    public function setScrollProperties(pageSize:Number,
                                        minScrollPosition:Number,
                                        maxScrollPosition:Number,
                                        pageScrollSize:Number = 0):void
    {
        var thumbHeight:Number;

        this.pageSize = pageSize;

        _pageScrollSize = (pageScrollSize > 0) ? pageScrollSize : pageSize;

        this.minScrollPosition = Math.max(minScrollPosition, 0);
        this.maxScrollPosition = Math.max(maxScrollPosition, 0);

        _scrollPosition = Math.max(this.minScrollPosition, _scrollPosition);
        _scrollPosition = Math.min(this.maxScrollPosition, _scrollPosition);

        // If the ScrollBar is enabled and has a nonzero range ...
        if (this.maxScrollPosition - this.minScrollPosition > 0 && enabled)
        {
            upArrow.enabled = true;
            downArrow.enabled = true;
            scrollTrack.enabled = true;

            addEventListener(MouseEvent.MOUSE_DOWN,
                             scrollTrack_mouseDownHandler);
            addEventListener(MouseEvent.MOUSE_OVER,
                             scrollTrack_mouseOverHandler);
            addEventListener(MouseEvent.MOUSE_OUT,
                             scrollTrack_mouseOutHandler);

            if (!scrollThumb)
            {
                scrollThumb = new ScrollThumb();

                scrollThumb.focusEnabled = false;

                // Add the thumb above the up arrow but below the down arrow
                addChildAt(scrollThumb, getChildIndex(downArrow));

                scrollThumb.styleName = new StyleProxy(this, thumbStyleFilters);

                // This button is a 4-state Button
                // that by default uses the ScrollThumbSkin.
                scrollThumb.upSkinName = "thumbUpSkin";
                scrollThumb.overSkinName = "thumbOverSkin";
                scrollThumb.downSkinName = "thumbDownSkin";
                scrollThumb.iconName = "thumbIcon";
                scrollThumb.skinName = "thumbSkin";
            }

            thumbHeight = trackHeight < 0 ? 0 : Math.round(
                pageSize /
                (this.maxScrollPosition - this.minScrollPosition + pageSize) *
                trackHeight);

            if (thumbHeight < scrollThumb.minHeight)
            {
                if (trackHeight < scrollThumb.minHeight)
                {
                    scrollThumb.visible = false;
                }
                else
                {
                    thumbHeight = scrollThumb.minHeight;
                    scrollThumb.visible = true;
                    scrollThumb.setActualSize(scrollThumb.measuredWidth, scrollThumb.minHeight);
                }
            }
            else
            {
                scrollThumb.visible = true;
                scrollThumb.setActualSize(scrollThumb.measuredWidth, thumbHeight);
            }

            scrollThumb.setRange(upArrow.getExplicitOrMeasuredHeight() + 0,
                                 virtualHeight -
                                 downArrow.getExplicitOrMeasuredHeight() -
                                 scrollThumb.height,
                                 this.minScrollPosition,
                                 this.maxScrollPosition);

            scrollPosition = Math.max(Math.min(scrollPosition, this.maxScrollPosition), this.minScrollPosition);
        }
        else
        {
            upArrow.enabled = false;
            downArrow.enabled = false;
            scrollTrack.enabled = false;

            if (scrollThumb)
                scrollThumb.visible = false;
        }
    }

    /**
     *  @private
     */
    mx_internal function lineScroll(direction:int):void
    {
        var delta:Number = _lineScrollSize;

        var newPos:Number = _scrollPosition + direction * delta;
        if (newPos > maxScrollPosition)
            newPos = maxScrollPosition;
        else if (newPos < minScrollPosition)
            newPos = minScrollPosition;

        if (newPos != scrollPosition)
        {
            var oldPosition:Number = scrollPosition;
            scrollPosition = newPos;
            var detail:String = direction < 0 ? lineMinusDetail : linePlusDetail;
            dispatchScrollEvent(oldPosition, detail);
        }
    }

    /**
     *  @private
     */
    mx_internal function pageScroll(direction:int):void
    {
        var delta:Number = _pageScrollSize != 0 ? _pageScrollSize : pageSize;

        var newPos:Number = _scrollPosition + direction * delta;
        if (newPos > maxScrollPosition)
            newPos = maxScrollPosition;
        else if (newPos < minScrollPosition)
            newPos = minScrollPosition;

        if (newPos != scrollPosition)
        {
            var oldPosition:Number = scrollPosition;
            scrollPosition = newPos;
            var detail:String = direction < 0 ? pageMinusDetail : pagePlusDetail;
            dispatchScrollEvent(oldPosition, detail);
        }
    }

    /**
     *  @private
     *  Dispatch a scroll event.
     */
    mx_internal function dispatchScrollEvent(oldPosition:Number,
                                             detail:String):void
    {
        var event:ScrollEvent = new ScrollEvent(ScrollEvent.SCROLL);
        event.detail = detail;
        event.position = scrollPosition;
        event.delta = scrollPosition - oldPosition;
        event.direction = direction;
        dispatchEvent(event);
    }

    /**
     *  @private
     *  Returns true if it is a scrollbar key.
     *  It will execute the equivalent code for that key as well.
     */
    mx_internal function isScrollBarKey(key:uint):Boolean
    {
        var oldPosition:Number;

        if (key == Keyboard.HOME)
        {
            if (scrollPosition != 0)
            {
                oldPosition = scrollPosition;
                scrollPosition = 0;
                dispatchScrollEvent(oldPosition, minDetail);
            }
            return true;
        }

        else if (key == Keyboard.END)
        {
            if (scrollPosition < maxScrollPosition)
            {
                oldPosition = scrollPosition;
                scrollPosition = maxScrollPosition;
                dispatchScrollEvent(oldPosition, maxDetail);
            }
            return true;
        }

        return false;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Callback when the up-arrow button is pressed or autorepeated.
     */
    private function upArrow_buttonDownHandler(event:FlexEvent):void
    {
        if (isNaN(oldPosition))
            oldPosition = scrollPosition;
        
        lineScroll(-1);
    }

    /**
     *  @private
     *  Callback when the down-arrow button is pressed or autorepeated.
     */
    private function downArrow_buttonDownHandler(event:FlexEvent):void
    {
        if (isNaN(oldPosition))
            oldPosition = scrollPosition;
        
        lineScroll(1);
    }

    /**
     *  @private
     *  Show the over skin of the scrollTrack if there is one.
     */
    private function scrollTrack_mouseOverHandler(event:MouseEvent):void
    {
        if (!(event.target == this || event.target == scrollTrack))
            return;

        if (trackScrolling)
            trackScrollTimer.start();
    }

    /**
     *  @private
     *  Hide the over skin of the scrollTrack.
     */
    private function scrollTrack_mouseOutHandler(event:MouseEvent):void
    {
        if (trackScrolling)
            trackScrollTimer.stop();
    }

    /**
     *  @private
     *  Set up the repeating events when pressing on the track.
     */
    private function scrollTrack_mouseDownHandler(event:MouseEvent):void
    {
        if (!(event.target == this || event.target == scrollTrack))
            return;

        trackScrolling = true;
        
        systemManager.addEventListener(
            MouseEvent.MOUSE_UP, scrollTrack_mouseUpHandler, true);
        systemManager.addEventListener(
            MouseEvent.MOUSE_MOVE, scrollTrack_mouseMoveHandler, true);
        // in case we go offscreen
        systemManager.stage.addEventListener(MouseEvent.MOUSE_MOVE, 
                            stage_scrollTrack_mouseMoveHandler);
        // in case we go offscreen
        systemManager.stage.addEventListener(Event.MOUSE_LEAVE, 
                            scrollTrack_mouseLeaveHandler);
        
        var pt:Point = new Point(event.localX, event.localY);
        pt = event.target.localToGlobal(pt);
        pt = globalToLocal(pt);
        
        trackPosition = pt.y;

        if (isNaN(oldPosition))
            oldPosition = scrollPosition;

        trackScrollRepeatDirection =
            scrollThumb.y + scrollThumb.height < pt.y ? 1 :
            scrollThumb.y > pt.y ? -1 : 0;
        pageScroll(trackScrollRepeatDirection);

        if (!trackScrollTimer)
        {
            trackScrollTimer = new Timer(getStyle("repeatDelay"), 1);
            trackScrollTimer.addEventListener(TimerEvent.TIMER, trackScrollTimerHandler);
        }
        trackScrollTimer.start();
    }

    /**
     *  @private
     *  This gets called at certain intervals
     *  to repeat the scroll event when pressing the track.
     */
    private function trackScrollTimerHandler(event:Event):void
    {
        if (trackScrollRepeatDirection == 1)
            if (scrollThumb.y + scrollThumb.height > trackPosition)
                return;

        if (trackScrollRepeatDirection == -1)
            if (scrollThumb.y < trackPosition)
                return;

        pageScroll(trackScrollRepeatDirection);
        if (trackScrollTimer && trackScrollTimer.repeatCount == 1)
        {
            trackScrollTimer.delay = getStyle("repeatInterval");
            trackScrollTimer.repeatCount = 0;
        }
    }

    /**
     *  @private
     *  Stop repeating events because the track is no longer pressed
     *  special case to restore focus when we've released the mouse
     */
    private function scrollTrack_mouseUpHandler(event:MouseEvent):void
    {
        scrollTrack_mouseLeaveHandler(event);
    }
    private function scrollTrack_mouseLeaveHandler(event:Event):void
    {
        trackScrolling = false;

        systemManager.removeEventListener(
            MouseEvent.MOUSE_UP, scrollTrack_mouseUpHandler, true);
        systemManager.removeEventListener(
            MouseEvent.MOUSE_MOVE, scrollTrack_mouseMoveHandler, true);
        // in case we go offscreen
        systemManager.stage.removeEventListener(MouseEvent.MOUSE_MOVE, 
                            stage_scrollTrack_mouseMoveHandler);
        // in case we go offscreen
        systemManager.stage.removeEventListener(Event.MOUSE_LEAVE, 
                            scrollTrack_mouseLeaveHandler);

        if (trackScrollTimer)
            trackScrollTimer.reset();

        if (event.target != scrollTrack)
            return;
            
        var detail:String = oldPosition > scrollPosition ?
                            pageMinusDetail :
                            pagePlusDetail;
        dispatchScrollEvent(oldPosition, detail);
        
        oldPosition = NaN;
    }

    private function stage_scrollTrack_mouseMoveHandler(event:MouseEvent):void
    {
        if (event.target != stage)
            return;

        scrollTrack_mouseMoveHandler(event);
    }

    /**
     *  @private
     *  Stop repeating events because the track is no longer pressed
     *  special case to restore focus when we've released the mouse
     */
    private function scrollTrack_mouseMoveHandler(event:MouseEvent):void
    {
        if (trackScrolling)
        {
            var pt:Point = new Point(event.stageX, event.stageY);
            pt = globalToLocal(pt);
            trackPosition = pt.y;
        }
    }

}

}
