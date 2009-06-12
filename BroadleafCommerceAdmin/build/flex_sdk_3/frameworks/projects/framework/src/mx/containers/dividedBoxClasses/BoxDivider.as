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

package mx.containers.dividedBoxClasses
{

import flash.display.DisplayObject;
import flash.events.MouseEvent;
import flash.geom.Point;
import mx.containers.DividedBox;
import mx.containers.DividerState;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

include "../../styles/metadata/GapStyles.as"

/**
 *  @copy mx.containers.DividedBox#style:dividerAffordance
 */
[Style(name="dividerAffordance", type="Number", format="Length", inherit="no")]

/**
 *  @copy mx.containers.DividedBox#style:dividerAlpha 
 */
[Style(name="dividerAlpha", type="Number", inherit="no")]

/**
 *  @copy mx.containers.DividedBox#style:dividerColor
 */
[Style(name="dividerColor", type="uint", format="Color", inherit="yes")]

/**
 *  @copy mx.containers.DividedBox#style:dividerThickness
 */
[Style(name="dividerThickness", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

/**
 *  The BoxDivider class represents the divider between children of a DividedBox container.
 *
 *  @see mx.containers.DividedBox
 */
public class BoxDivider extends UIComponent
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function BoxDivider()
    {
        super();

        // Register for player events.
        addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
        addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
        addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var knob:DisplayObject;
    
    /**
     *  @private
     */
    private var isMouseOver:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function set x(value:Number):void
    {
        var oldValue:Number = x;
        super.x = value;
        
        if (!DividedBox(owner).isVertical())
        {
            DividedBox(owner).moveDivider(
                DividedBox(owner).getDividerIndex(this), value - oldValue);
        }
    }

    /**
     *  @private
     */
    override public function set y(value:Number):void
    {
        var oldValue:Number = y;
        super.y = value;
        
        if (DividedBox(owner).isVertical())
        {
            DividedBox(owner).moveDivider(
                DividedBox(owner).getDividerIndex(this), value - oldValue);
        }
    }

    //----------------------------------
    //  state
    //----------------------------------

    /**
     *  @private
     *  Storage for the state property.
     */
    private var _state:String = DividerState.UP;

    /**
     *  @private
     */
    mx_internal function get state():String
    {
        return _state;
    }

    /**
     *  @private
     */
    mx_internal function set state(value:String):void
    {
        _state = value;

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
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        // The mouse-over thickness of the divider is normally determined
        // by the dividerAffordance style, and the visible thickness is
        // normally determined by the dividerThickness style, assuming that
        // the relationship thickness <= affordance <= gap applies. But if
        // one of the other five orderings applies, here is a table of what
        // happens:
        //
        //  divider    divider    horizontalGap/  dividerWidth/  visible width/
        // Thickness  Affordance  verticalGap     dividerHeight  visible height
        //
        //    4           6             8               6              4
        //    4           8             6               6              4
        //    6           4             8               6              6
        //    6           8             4               4              4
        //    8           4             6               6              6
        //    8           6             4               4              4

        if (isNaN(width) || isNaN(height))
            return;

        if (!parent)
            return;

        super.updateDisplayList(unscaledWidth, unscaledHeight);

        graphics.clear();

        graphics.beginFill(0x000000, 0);
        graphics.drawRect(0, 0, width, height);
        graphics.endFill();

        var color:Number;
        var alpha:Number = 1.0;

        var thickness:Number = getStyle("dividerThickness");

        var gap:Number = DividedBox(owner).isVertical() ?
                         DividedBox(owner).getStyle("verticalGap") :
                         DividedBox(owner).getStyle("horizontalGap");

        if (state != DividerState.DOWN)
        {
            // Draw knob, if there is enough room
            if (gap >= 6)
            {
                if (!knob)
                {
                    var knobClass:Class = Class(getStyle("dividerSkin"));
                    if (knobClass)
                        knob = new knobClass();
                    if (knob)
                        addChild(knob);
                }

                if (knob)
                {
                    if (DividedBox(owner).isVertical())
                    {
                        knob.scaleX = 1.0;
                        knob.rotation = 0;
                    }
                    else
                    {
                        // Rotate the knob
                        knob.scaleX = -1.0;
                        knob.rotation = -90;
                    }

                    knob.x = Math.round((width - knob.width) / 2);
                    knob.y = Math.round((height - knob.height) / 2);
                }
            }
            return;
        }

        color = getStyle("dividerColor");
        alpha = getStyle("dividerAlpha");
        graphics.beginFill(color, alpha);

        if (DividedBox(owner).isVertical())
        {
            var visibleHeight:Number = thickness;

            if (visibleHeight > gap)
                visibleHeight = gap;

            var y:Number = (height - visibleHeight) / 2;
            graphics.drawRect(0, y, width, visibleHeight);
        }
        else
        {
            var visibleWidth:Number = thickness;

            if (visibleWidth > gap)
                visibleWidth = gap;

            var x:Number = (width - visibleWidth) / 2;
            graphics.drawRect(x, 0, visibleWidth, height);
        }

        graphics.endFill();
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        super.styleChanged(styleProp);

        if (!styleProp ||
            styleProp == "dividerSkin" ||
            styleProp == "styleName")
        {
            if (knob)
            {
                removeChild(knob);
                knob = null;
            }
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
    private function mouseOverHandler(event:MouseEvent):void
    {
        isMouseOver = true;
        if (!DividedBox(owner).activeDivider)
        {
            state = DividerState.OVER;
            DividedBox(owner).changeCursor(this);
        }
    }

    /**
     *  @private
     */
    private function mouseOutHandler(event:MouseEvent):void
    {
        isMouseOver = false;
        if (!DividedBox(owner).activeDivider)
        {
            state = DividerState.UP;
            if (parent)
                DividedBox(owner).restoreCursor();
        }
    }

    /**
     *  @private
     */
    private function mouseDownHandler(event:MouseEvent):void
    {
        // Don't set down state here. If we're doing a live drag we don't
        // want to show the proxy. If we're not doing a live drag, our
        // parent will create a drag proxy and set the state to DividerState.DOWN.
        // state = DividerState.DOWN;
        DividedBox(owner).changeCursor(this);
        DividedBox(owner).startDividerDrag(this, event);
        systemManager.addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler, true);
    }

    /**
     *  @private
     */
    private function mouseUpHandler(event:MouseEvent):void
    {
        // If a mouseOut was the last mouse event that occurred
        // make sure to restore the system cursor.
        if (!isMouseOver)
            DividedBox(owner).restoreCursor();

        state = DividerState.OVER;
        DividedBox(owner).stopDividerDrag(this, event);
        systemManager.removeEventListener(MouseEvent.MOUSE_UP, mouseUpHandler, true);
    }
}

}
