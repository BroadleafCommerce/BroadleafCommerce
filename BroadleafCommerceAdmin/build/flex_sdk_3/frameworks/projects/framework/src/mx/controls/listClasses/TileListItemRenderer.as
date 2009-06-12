////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls.listClasses
{

import flash.display.DisplayObject;
import flash.display.Sprite;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.text.TextFieldType;
import mx.core.FlexVersion;
import mx.core.IDataRenderer;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.IToolTip;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.events.ToolTipEvent;

use namespace mx_internal;

/**
 *  Dispatched when the <code>data</code> property changes.
 *
 *  <p>When you use a component as an item renderer,
 *  the <code>data</code> property contains the data to display.
 *  You can listen for this event and update the component
 *  when the <code>data</code> property changes.</p>
 * 
 *  @eventType mx.events.FlexEvent.DATA_CHANGE
 */
[Event(name="dataChange", type="mx.events.FlexEvent")]

/**
 *  Text color of a component label.
 *  @default 0x0B333C
 */
[Style(name="color", type="uint", format="Color", inherit="yes")]

/**
 *  Text color of the component if it is disabled.
 *  @default 0xAAB3B3
 */
[Style(name="disabledColor", type="uint", format="Color", inherit="yes")]

/**
 *  Number of pixels between children in the vertical direction. 
 *  @default 6
 */
[Style(name="verticalGap", type="Number", format="Length", inherit="no")]

/**
 *  The TileListItemRenderer class defines the default item renderer for the 
 *  HorizontalList and TileList controls. 
 *  By default, the item renderer 
 *  draws the text associated with each item in the list, and an optional icon.
 *
 *  <p>You can override the default item renderer by creating a custom item renderer.</p>
 *
 *  @see mx.controls.HorizontalList
 *  @see mx.controls.TileList
 *  @see mx.core.IDataRenderer
 *  @see mx.controls.listClasses.IDropInListItemRenderer
 */
public class TileListItemRenderer extends UIComponent
                                  implements IDataRenderer,
                                  IDropInListItemRenderer, IListItemRenderer,
                                  IFontContextComponent
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
    public function TileListItemRenderer()
    {
        super();

        addEventListener(ToolTipEvent.TOOL_TIP_SHOW, toolTipShowHandler);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var listOwner:TileBase;

    /**
     *  @private
     */
    private var iconClass:Class;

    /**
     *  @private
     */
    private var iconOnly:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties: UIComponent
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  @private
     *  The baselinePosition of a TileListItemRenderer is calculated
     *  for its label.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            super.baselinePosition;
            
        if (!validateBaselinePosition())
            return NaN;

        return label.y + label.baselinePosition;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  data
    //----------------------------------

    /**
     *  @private
     *  Storage for the data property.
     */
    private var _data:Object;

    [Bindable("dataChange")]

    /**
     *  The implementation of the <code>data</code> property as 
     *  defined by the IDataRenderer interface.  It simply stores
     *  the value and invalidates the component
     *  to trigger a relayout of the component.
     *
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
        _data = value;

        invalidateProperties();

        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
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
    //  icon
    //----------------------------------

    /**
     *  The internal IFlexDisplayObject that displays the icon in this renderer.
     */
    protected var icon:IFlexDisplayObject;

    //----------------------------------
    //  label
    //----------------------------------

    /**
     *  The internal UITextField that displays the text in this renderer.
     */
    protected var label:IUITextField;

    //----------------------------------
    //  listData
    //----------------------------------

    /**
     *  @private
     *  Storage for the listData property.
     */
    private var _listData:ListData;

    [Bindable("dataChange")]
    
    /**
     *  The implementation of the <code>listData</code> property as 
     *  defined by the IDropInListItemRenderer interface.
     *
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
        _listData = ListData(value);

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
    
        createLabel(-1);
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        // if the font changed and we already created the label, we will need to 
        // destory it so it can be re-created, possibly in a different swf context.
        if (hasFontContextChanged() && label != null)
        {
            var index:int = getChildIndex(DisplayObject(label));
            removeLabel();
            createLabel(index);
        }

        // remove icon if we're recycled and now have a null data
        if (icon && !_data)
        {
            removeChild(DisplayObject(icon));
            icon = null;
            iconClass = null;
        }

        if (_data)
        {
            listOwner = TileBase(_listData.owner);

            if (_listData.icon)
            {
                var newIconClass:Class = _listData.icon;;

                if (iconClass != newIconClass)
                {
                    iconClass = newIconClass;

                    if (icon)
                        removeChild(DisplayObject(icon));

                    icon = new iconClass();
                    addChild(DisplayObject(icon));
                }
            }

            // trace(_data.value);
            label.text = _listData.label;

            label.multiline = listOwner.variableRowHeight;

            label.wordWrap = listOwner.wordWrap;
        }
        else
        {
            label.text = " ";
            toolTip = null;
        }

    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        var h:Number = 0;

        if (icon)
        {
            h += icon.measuredHeight;
        }

        if (label.text == "" || label.text == " " || label.text == null)
        {
            // hide the label
            label.explicitHeight = 0;
            iconOnly = true;
        }
        else
        {
            // clear so we use measured value
            label.explicitHeight = NaN;
            h += getStyle("verticalGap")
            iconOnly = false;
        }

        measuredHeight = label.getExplicitOrMeasuredHeight() + h;
        
        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");

        measuredWidth = label.getExplicitOrMeasuredWidth() + paddingLeft + paddingRight;
        if (icon && icon.measuredWidth + paddingLeft + paddingRight > measuredWidth)
            measuredWidth = icon.measuredWidth + paddingLeft + paddingRight;
    }


    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var verticalGap:Number = iconOnly ? 0 : getStyle("verticalGap");

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");

        if (icon)
        {
            icon.width = Math.min(unscaledWidth - (paddingLeft + paddingRight), icon.measuredWidth);
            icon.height = Math.min(Math.max(unscaledHeight - verticalGap - label.getExplicitOrMeasuredHeight(), 0),
                                   icon.measuredHeight);
            icon.x = paddingLeft + (unscaledWidth - paddingLeft - paddingRight - icon.width) / 2;
        }

        label.width = unscaledWidth - (paddingLeft + paddingRight);
        label.height = Math.min(label.getExplicitOrMeasuredHeight(),
                                icon ?
                                Math.max(unscaledHeight - verticalGap - icon.height, 0) :
                                unscaledHeight);
        label.x = paddingLeft;
        
        if (listOwner && listOwner.showDataTips)
        {
            // By default label is used for dataTip. We show dataTip when 
            // text doesnot fit. If user has specified a dataTipField/dataTipFunction
            // we always display the tip.
            if ( label.textWidth > label.width ||
                 (listOwner.dataTipField && listOwner.dataTipField != "label") ||
                listOwner.dataTipFunction != null)
            {
                toolTip = listOwner.itemToDataTip(_data);
            }
            else
            {
                toolTip = null;
            }
        }
        else
        {
            toolTip = null;
        }

        var startY:Number;
        var totalHeight:Number = label.height;
        if (icon)
            totalHeight += icon.height + verticalGap;

        var verticalAlign:String = getStyle("verticalAlign");
        if (verticalAlign == "top")
        {
            startY = 0;
            if (icon)
            {
                icon.y = startY;
                startY += verticalGap + icon.height;
            }
            label.y = startY;
        }
        else if (verticalAlign == "bottom")
        {
            startY = unscaledHeight - label.height;
            label.y = startY;
            if (icon)
            {
                startY -= verticalGap + icon.height;
                icon.y = startY;
            }
        }
        else
        {
            startY = (unscaledHeight - totalHeight) / 2;
            if (icon)
            {
                icon.y = startY;
                startY += verticalGap + icon.height;
            }
            label.y = startY;
        }

        var labelColor:Number;

        if (data && parent)
        {
            if (!enabled)
                labelColor = getStyle("disabledColor");

            else if (listOwner.isItemSelected(listData.uid))
                labelColor = getStyle("textSelectedColor");

            else if (listOwner.isItemHighlighted(listData.uid))
                labelColor = getStyle("textRollOverColor");

            else
                labelColor = getStyle("color");

            label.setColor(labelColor);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Creates the label and adds it as a child of this component.
     * 
     *  @param childIndex The index of where to add the child.
     *  If -1, the text field is appended to the end of the list.
     */
    mx_internal function createLabel(childIndex:int):void
    {
        if (!label)
        {
            label = IUITextField(createInFontContext(UITextField));
            label.styleName = this;
            
            if (childIndex == -1)
                addChild(DisplayObject(label));
            else 
                addChildAt(DisplayObject(label), childIndex);
        }
    }

    /**
     *  @private
     *  Removes the label from this component.
     */
    mx_internal function removeLabel():void
    {
        if (label)
        {
            removeChild(DisplayObject(label));
            label = null;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  Positions the ToolTip object.
     *
     *  @param The Event object.
     */
    protected function toolTipShowHandler(event:ToolTipEvent):void
    {
        var toolTip:IToolTip = event.toolTip;

        // Calculate global position of label.
        var pt:Point = new Point(0, 0);
        pt = label.localToGlobal(pt);
        pt = stage.globalToLocal(pt);

        toolTip.move(pt.x, pt.y + (height - toolTip.height) / 2);

        var screen:Rectangle = systemManager.screen;
        var screenRight:Number = screen.x + screen.width;
        if (toolTip.x + toolTip.width > screenRight)
            toolTip.move(screenRight - toolTip.width, toolTip.y);
    }
    
    /**
     *  @private
     */
    mx_internal function getLabel():IUITextField
    {
        return label;
    }
}

}
