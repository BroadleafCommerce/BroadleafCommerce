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

package mx.controls.listClasses
{

import flash.display.DisplayObject;
import flash.geom.Point;
import flash.geom.Rectangle;
import mx.core.FlexVersion
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

//--------------------------------------
//  Events
//--------------------------------------

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

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Text color of a component label.
 *  @default 0x0B333C
 */
[Style(name="color", type="uint", format="Color", inherit="yes")]

/**
 *  Text color of the component if it is disabled.
 *  
 *  @default 0xAAB3B3
 */
[Style(name="disabledColor", type="uint", format="Color", inherit="yes")]

/**
 *  The ListItemRenderer class defines the default item renderer
 *  for a List control. 
 *  By default, the item renderer draws the text associated
 *  with each item in the list, and an optional icon.
 *
 *  <p>You can override the default item renderer
 *  by creating a custom item renderer.</p>
 *
 *  @see mx.controls.List
 *  @see mx.core.IDataRenderer
 *  @see mx.controls.listClasses.IDropInListItemRenderer
 */
public class ListItemRenderer extends UIComponent
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
    public function ListItemRenderer()
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
    private var listOwner:ListBase;

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
     *  The baselinePosition of a ListItemRenderer is calculated
     *  for its label.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            if (!label)
                return 0;
                
            return label.baselinePosition;
        }
        
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
     *  The implementation of the <code>data</code> property
     *  as defined by the IDataRenderer interface.
     *  When set, it stores the value and invalidates the component 
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
     *  @inheritDoc 
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
     *  The implementation of the <code>listData</code> property
     *  as defined by the IDropInListItemRenderer interface.
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
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        if (!label)
        {
            label = IUITextField(createInFontContext(UITextField));
            label.styleName = this;
            addChild(DisplayObject(label));
        }
    }

    /**
     *  @private
     *  Apply the data and listData.
     *  Create an instance of the icon if specified,
     *  and set the text into the text field.
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        var childIndex:int = -1;
        if (hasFontContextChanged() && label != null) 
        {
            childIndex = getChildIndex(DisplayObject(label));
            removeChild(DisplayObject(label));
            label = null;
        }

        if (!label)
        {
            label = IUITextField(createInFontContext(UITextField));
            label.styleName = this;
            
            if (childIndex == -1)
                addChild(DisplayObject(label));             
            else
                addChildAt(DisplayObject(label), childIndex);
        }
        
        if (icon)
        {
            removeChild(DisplayObject(icon));
            icon = null;
        }

        if (_data != null)
        {
            listOwner = ListBase(_listData.owner);

            if (_listData.icon)
            {
                var iconClass:Class = _listData.icon;
                icon = new iconClass();

                addChild(DisplayObject(icon));
            }
            
            label.text = _listData.label ? _listData.label : " ";
            label.multiline = listOwner.variableRowHeight;
            label.wordWrap = listOwner.wordWrap;

            if (listOwner.showDataTips)
            {
                if (label.textWidth > label.width ||
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

        var w:Number = 0;

        if (icon)
            w = icon.measuredWidth;

        // Guarantee that label width isn't zero
        // because it messes up ability to measure.
        if (label.width < 4 || label.height < 4)
        {
            label.width = 4;
            label.height = 16;
        }

        if (isNaN(explicitWidth))
        {
            w += label.getExplicitOrMeasuredWidth();
            measuredWidth = w;
            measuredHeight = label.getExplicitOrMeasuredHeight();
        }
        else
        {
            measuredWidth = explicitWidth;
            label.setActualSize(Math.max(explicitWidth - w, 4), label.height);
            measuredHeight = label.getExplicitOrMeasuredHeight();
            if (icon && icon.measuredHeight > measuredHeight)
                measuredHeight = icon.measuredHeight;
        }
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var startX:Number = 0;

        if (icon)
        {
            icon.x = startX;
            startX = icon.x + icon.measuredWidth;
            icon.setActualSize(icon.measuredWidth, icon.measuredHeight);
        }

        label.x = startX;
        label.setActualSize(unscaledWidth - startX, measuredHeight);

        var verticalAlign:String = getStyle("verticalAlign");
        if (verticalAlign == "top")
        {
            label.y = 0;
            if (icon)
                icon.y = 0;
        }
        else if (verticalAlign == "bottom")
        {
            label.y = unscaledHeight - label.height + 2; // 2 for gutter
            if (icon)
                icon.y = unscaledHeight - icon.height;
        }
        else
        {
            label.y = (unscaledHeight - label.height) / 2;
            if (icon)
                icon.y = (unscaledHeight - icon.height) / 2;
        }

        var labelColor:Number;

        if (data && parent)
        {
            if (!enabled)
                labelColor = getStyle("disabledColor");
            else if (listOwner.isItemHighlighted(listData.uid))
                labelColor = getStyle("textRollOverColor");
            else if (listOwner.isItemSelected(listData.uid))
                labelColor = getStyle("textSelectedColor");
            else
                labelColor = getStyle("color");

            label.setColor(labelColor);
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
