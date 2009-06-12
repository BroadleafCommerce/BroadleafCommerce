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

import flash.ui.Keyboard;
import mx.controls.scrollClasses.ScrollBar;
import mx.controls.scrollClasses.ScrollBarDirection;
import mx.core.mx_internal;
import mx.events.ScrollEvent;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the ScrollBar control scrolls through
 *  user initiated action or programmatically. 
 *
 *  @eventType mx.events.ScrollEvent.SCROLL
 */
[Event(name="scroll", type="mx.events.ScrollEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Number of milliseconds to wait after the first <code>buttonDown</code>
 *  event before repeating <code>buttonDown</code> events at the
 *  <code>repeatInterval</code>.
 *  The default value is 500.
 */
[Style(name="repeatDelay", type="Number", format="Time", inherit="no")]

/**
 *  Number of milliseconds between <code>buttonDown</code> events
 *  if the user presses and holds the mouse on a button.
 *  The default value is 35.
 */
[Style(name="repeatInterval", type="Number", format="Time", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="direction", kind="property")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultBindingProperty(source="scrollPosition", destination="scrollPosition")]

[DefaultTriggerEvent("scroll")]

[IconFile("VScrollBar.png")]

/**
 *  The VScrollBar (vertical ScrollBar) control  lets you control
 *  the portion of data that is displayed when there is too much data
 *  to fit in a display area.
 * 
 *  This control extends the base ScrollBar control. 
 *  
 *  <p>Although you can use the VScrollBar control as a stand-alone control,
 *  you usually combine it as part of another group of components to
 *  provide scrolling functionality.</p>
 *  
 *  <p>A ScrollBar control consist of four parts: two arrow buttons,
 *  a track, and a thumb. 
 *  The position of the thumb and the display of the arrow buttons
 *  depend on the current state of the ScrollBar control.
 *  The ScrollBar control uses four parameters to calculate its 
 *  display state:</p>
 *
 *  <ul>
 *    <li>Minimum range value</li>
 *    <li>Maximum range value</li>
 *    <li>Current position - must be within the
 *    minimum and maximum range values</li>
 *    <li>Viewport size - represents the number of items
 *    in the range that you can display at one time. The
 *    number of items must be less than or equal to the 
 *    range, where the range is the set of values between
 *    the minimum range value and the maximum range value.</li>
 *  </ul>
 *  
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:VScrollBar&gt;</code> tag inherits all the
 *  tag attributes of its superclass, and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:VScrollBar
 *    <strong>Styles</strong>
 *    repeatDelay="500"
 *    repeatInterval="35"
 * 
 *    <strong>Events</strong>
 *    scroll="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *  
 *  @includeExample examples/VScrollBarExample.mxml
 *
 *  @see mx.controls.scrollClasses.ScrollBar
 */
public class VScrollBar extends ScrollBar
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
    public function VScrollBar()
    {
        super();

        // ScrollBar variables.
        super.direction = ScrollBarDirection.VERTICAL;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  direction
    //----------------------------------
    
    [Inspectable(environment="none")]   

    /**
     *  @private
     *  Don't allow user to change the direction.
     */
    override public function set direction(value:String):void
    {
    }

    //----------------------------------
    //  minWidth
    //----------------------------------

    /**
     *  @private
     */
    override public function get minWidth():Number
    {
        return _minWidth;
    }

    //----------------------------------
    //  minHeight
    //----------------------------------

    /**
     *  @private
     */
    override public function get minHeight():Number
    {
        return _minHeight;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        measuredWidth = _minWidth;
        measuredHeight = _minHeight;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: ScrollBar
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Map keys to scroll events.
     */
    override mx_internal function isScrollBarKey(key:uint):Boolean
    {
        if (key == Keyboard.UP)
        {
            lineScroll(-1);
            return true;
        }

        else if (key == Keyboard.DOWN)
        {
            lineScroll(1);
            return true;
        }

        else if (key == Keyboard.PAGE_UP)
        {
            pageScroll(-1);
            return true;
        }

        else if (key == Keyboard.PAGE_DOWN)
        {
            pageScroll(1);
            return true;
        }

        return super.isScrollBarKey(key);
    }
}

}
