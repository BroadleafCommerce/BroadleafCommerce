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

package mx.controls.treeClasses
{

import flash.display.DisplayObject;
import flash.display.InteractiveObject;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.utils.getDefinitionByName;
import mx.controls.Tree;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.FlexVersion;
import mx.core.IDataRenderer;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModuleFactory
import mx.core.IFontContextComponent;
import mx.core.IToolTip;
import mx.core.IUITextField;
import mx.core.SpriteAsset;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.events.ToolTipEvent;
import mx.events.TreeEvent;

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
 *  The default value is <code>0x0B333C</code>.
 */
[Style(name="color", type="uint", format="Color", inherit="yes")]

/**
 *  Color of the component if it is disabled.
 *  The default value is <code>0xAAB3B3</code>.
 *
 */
[Style(name="disabledColor", type="uint", format="Color", inherit="yes")]

/**
 *  The TreeItemRenderer class defines the default item renderer for a Tree control. 
 *  By default, the item renderer draws the text associated with each item in the tree, 
 *  an optional icon, and an optional disclosure icon.
 *
 *  <p>You can override the default item renderer by creating a custom item renderer.</p>
 *
 *  @see mx.controls.Tree
 *  @see mx.core.IDataRenderer
 *  @see mx.controls.listClasses.IDropInListItemRenderer
 */
public class TreeItemRenderer extends UIComponent
	   implements IDataRenderer, IDropInListItemRenderer, IListItemRenderer,
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
	public function TreeItemRenderer()
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
	 */
	private var listOwner:Tree;

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
     *  The baselinePosition of a TreeItemRenderer is calculated
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
	 *  defined by the IDataRenderer interface.
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
	//  disclosureIcon
    //----------------------------------

	/**
	 *  The internal IFlexDisplayObject that displays the disclosure icon
	 *  in this renderer.
	 */
	protected var disclosureIcon:IFlexDisplayObject;
	
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
	private var _listData:TreeListData;

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
		_listData = TreeListData(value);
		
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

		addEventListener(ToolTipEvent.TOOL_TIP_SHOW, toolTipShowHandler);
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

		if (icon)
		{
			removeChild(DisplayObject(icon));
			icon = null;
		}

		if (disclosureIcon)
		{
			disclosureIcon.removeEventListener(MouseEvent.MOUSE_DOWN, 
			      							   disclosureMouseDownHandler);
			removeChild(DisplayObject(disclosureIcon));
			disclosureIcon = null;
		}

		if (_data != null)
		{
			listOwner = Tree(_listData.owner);

			if (_listData.disclosureIcon)
			{
				var disclosureIconClass:Class = _listData.disclosureIcon;
				var disclosureInstance:* = new disclosureIconClass();
				
				// If not already an interactive object, then we'll wrap 
				// in one so we can dispatch mouse events.
				if (!(disclosureInstance is InteractiveObject))
				{
					var wrapper:SpriteAsset = new SpriteAsset();
					wrapper.addChild(disclosureInstance as DisplayObject);
					disclosureIcon = wrapper as IFlexDisplayObject;
				}
				else
				{
					disclosureIcon = disclosureInstance;
				}

				addChild(disclosureIcon as DisplayObject);
				disclosureIcon.addEventListener(MouseEvent.MOUSE_DOWN,
												disclosureMouseDownHandler);
			}
			
			if (_listData.icon)
			{
				var iconClass:Class = _listData.icon;
				icon = new iconClass();

				addChild(DisplayObject(icon));
			}
			
			label.text = _listData.label;
			label.multiline = listOwner.variableRowHeight;
			label.wordWrap = listOwner.wordWrap;
		}
		else
		{
			label.text = " ";
			toolTip = null;
		}

		invalidateDisplayList();
	}

	/**
	 *  @private
	 */
	override protected function measure():void
	{
		super.measure();

		var w:Number = _data ? _listData.indent : 0;

		if (disclosureIcon)
			w += disclosureIcon.width;

		if (icon)
			w += icon.measuredWidth;

		// guarantee that label width isn't zero because it messes up ability to measure
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
			label.width = Math.max(explicitWidth - w, 4);
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

		var startx:Number = _data ? _listData.indent : 0;
		
		if (disclosureIcon)
		{
			disclosureIcon.x = startx;

			startx = disclosureIcon.x + disclosureIcon.width;
			
			disclosureIcon.setActualSize(disclosureIcon.width,
										 disclosureIcon.height);
			
			disclosureIcon.visible = _data ?
									 _listData.hasChildren :
									 false;
		}
		
		if (icon)
		{
			icon.x = startx;
			startx = icon.x + icon.measuredWidth;
			icon.setActualSize(icon.measuredWidth, icon.measuredHeight);
		}
		
		label.x = startx;
		label.setActualSize(unscaledWidth - startx, measuredHeight);

		var verticalAlign:String = getStyle("verticalAlign");
		if (verticalAlign == "top")
		{
			label.y = 0;
			if (icon)
				icon.y = 0;
			if (disclosureIcon)
				disclosureIcon.y = 0;
		}
		else if (verticalAlign == "bottom")
		{
			label.y = unscaledHeight - label.height + 2; // 2 for gutter
			if (icon)
				icon.y = unscaledHeight - icon.height;
			if (disclosureIcon)
				disclosureIcon.y = unscaledHeight - disclosureIcon.height;
		}
		else
		{
			label.y = (unscaledHeight - label.height) / 2;
			if (icon)
				icon.y = (unscaledHeight - icon.height) / 2;
			if (disclosureIcon)
				disclosureIcon.y = (unscaledHeight - disclosureIcon.height) / 2;
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
		
		if (_data != null)
		{			
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
        if (label != null)
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
	 *  @private
	 */
	private function toolTipShowHandler(event:ToolTipEvent):void
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
	private function disclosureMouseDownHandler(event:Event):void
	{
		event.stopPropagation();
		
		if (listOwner.isOpening || !listOwner.enabled)
			return;

		var open:Boolean = _listData.open;
		_listData.open = !open;
		
		listOwner.dispatchTreeEvent(TreeEvent.ITEM_OPENING,
		                        _listData.item, //item
                                this,  	//renderer
                                event, 	//trigger
                                !open, 	//opening
    							true,  	//animate
    							true)   //dispatch
	}
	
	/**
	 *  @private
	 */
	mx_internal function getLabel():IUITextField
	{
		return label;
	}
	
	/**
	 *  @private
	 */
	mx_internal function getDisclosureIcon():IFlexDisplayObject
	{
		return disclosureIcon;
	}

}

}
