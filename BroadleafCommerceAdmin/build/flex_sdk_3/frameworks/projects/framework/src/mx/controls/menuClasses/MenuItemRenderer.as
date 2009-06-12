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

package mx.controls.menuClasses
{

import flash.display.DisplayObject;
import flash.utils.getDefinitionByName;
import mx.controls.Menu;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListData;
import mx.core.FlexVersion;
import mx.core.IDataRenderer;
import mx.core.IFlexDisplayObject;
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.FlexVersion;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.FlexEvent;

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
 *  Text color of the menu item label.
 *  
 *  @default 0x0B333C
 */
[Style(name="color", type="uint", format="Color", inherit="yes")]

/**
 *  Color of the menu item if it is disabled.
 *  
 *  @default 0xAAB3B3
 */
[Style(name="disabledColor", type="uint", format="Color", inherit="yes")]

/**
 *  The MenuItemRenderer class defines the default item renderer
 *  for menu items in any menu control.
 * 
 *  By default, the item renderer draws the text associated
 *  with each menu item, the separator characters, and icons.
 *
 *  <p>You can override the default item renderer
 *  by creating a custom item renderer.</p>
 *
 *  @see mx.controls.Menu
 *  @see mx.controls.MenuBar
 *  @see mx.core.IDataRenderer
 *  @see mx.controls.listClasses.IDropInListItemRenderer
 */
public class MenuItemRenderer extends UIComponent
							  implements IDataRenderer, IListItemRenderer,
							  IMenuItemRenderer, IDropInListItemRenderer,
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
	public function MenuItemRenderer()
	{
		super();
	}

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
     *  The baselinePosition of a MenuItemRenderer is calculated
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
	//  branchIcon
    //----------------------------------

	/**
	 *  The internal IFlexDisplayObject that displays the branch icon
	 *  in this renderer.
	 *  
	 *  @default null 
	 */
	protected var branchIcon:IFlexDisplayObject;

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
		invalidateSize();

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
    
    // using getter/setter so we can have backwards-compatibility.  If someone
    // subclassed and referred to icon, and they wanted to refer to the
    // checkbox/radio button or a separator, it is now moved into typeIcon
    // and separatorIcon

	/**
	 *  @private
	 *  Storage for the icon property.
	 */
	private var _icon:IFlexDisplayObject;

	/**
	 *  The internal IFlexDisplayObject that displays the icon in this renderer.
	 *  
	 *  @default null 
	 */
	protected function get icon():IFlexDisplayObject
	{
		if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
		{
			if (_data)
			{
				var dataDescriptor:IMenuDataDescriptor =
					Menu(_listData.owner).dataDescriptor;
				
				var type:String = dataDescriptor.getType(_data);
	
				// Separator
				if (type.toLowerCase() == "separator")
					return separatorIcon;
			}
			
			if (typeIcon)
				return typeIcon;
			
			return _icon;
		}
		
		return _icon;
	}

	/**
	 *  @private
	 */
	protected function set icon(value:IFlexDisplayObject):void
	{
		_icon = value;
	}

    //----------------------------------
	//  label
    //----------------------------------

	/**
	 *  The internal UITextField that displays the text in this renderer.
	 * 
	 *  @default null 
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

    //----------------------------------
	//  menu
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the menu property.
	 */
	private var _menu:Menu;

	/**
	 *  Contains a reference to the associated Menu control.
	 * 
	 *  @default null 
	 */
	public function get menu():Menu
	{
		return _menu;
	}

	/**
	 *  @private
	 */
	public function set menu(value:Menu):void
	{
		_menu = value;
	}
	
	//----------------------------------
	//  separatorIcon
    //----------------------------------

	/**
	 *  The internal IFlexDisplayObject that displays the separator icon in this renderer
	 *  
	 *  @default null 
	 */
	protected var separatorIcon:IFlexDisplayObject;
	
	//----------------------------------
	//  typeIcon
    //----------------------------------

	/**
	 *  The internal IFlexDisplayObject that displays the type icon in this renderer for
	 *  check and radio buttons.
	 *  
	 *  @default null 
	 */
	protected var typeIcon:IFlexDisplayObject;

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

		var iconClass:Class;
		var typeIconClass:Class;
		var separatorIconClass:Class;
		var branchIconClass:Class;

		// Remove any existing icon/type/separator/branch icons.
		// These will be recreated below if needed.
		if (_icon)
		{
			removeChild(DisplayObject(_icon));
			_icon = null;
		}
		if (typeIcon)
		{
			removeChild(DisplayObject(typeIcon));
			typeIcon = null;
		}
		if (separatorIcon)
		{
			removeChild(DisplayObject(separatorIcon));
			separatorIcon = null;
		}
		if (branchIcon)
		{
			removeChild(DisplayObject(branchIcon));
			branchIcon = null;
		}

		if (_data)
		{
			var dataDescriptor:IMenuDataDescriptor =
				Menu(_listData.owner).dataDescriptor;
			
			var isEnabled:Boolean = dataDescriptor.isEnabled(_data);
			var type:String = dataDescriptor.getType(_data);

			// Separator
			if (type.toLowerCase() == "separator")
			{
				label.text = "";
				label.visible = false;
				separatorIconClass = getStyle("separatorSkin");
				separatorIcon = new separatorIconClass();
				addChild(DisplayObject(separatorIcon));
				return;
			}
			else
			{
				label.visible = true;
			}

			// Icon
			if (_listData.icon)
			{
				var listDataIcon:Object = _listData.icon;
				if (listDataIcon is Class)
				{
					iconClass = Class(listDataIcon);
				}
				else if (listDataIcon is String)
				{
					iconClass =
						Class(getDefinitionByName(String(listDataIcon)));
				}

				_icon = new iconClass();

				addChild(DisplayObject(_icon));
			}	

			// Label
			label.text = _listData.label;

			label.enabled = isEnabled;

			// Check/radio icon
			if (dataDescriptor.isToggled(_data))
			{
				var typeVal:String = dataDescriptor.getType(_data);
				if (typeVal)
				{
					typeVal = typeVal.toLowerCase();
					if (typeVal == "radio")
					{
						typeIconClass = getStyle(isEnabled ?
											 "radioIcon" :
											 "radioDisabledIcon");
					}
					else if (typeVal == "check")
					{
						typeIconClass = getStyle(isEnabled ?
											 "checkIcon" :
											 "checkDisabledIcon");
					}

					if (typeIconClass)
					{
						typeIcon = new typeIconClass();
						addChild(DisplayObject(typeIcon));
					}
				}
			}

			// Branch icon
			if (dataDescriptor.isBranch(_data))
			{
				branchIconClass = getStyle(isEnabled ?
										   "branchIcon" :
										   "branchDisabledIcon");

				if (branchIconClass)
				{
					branchIcon = new branchIconClass();
					addChild(DisplayObject(branchIcon));
				}
			}
		}
		else
		{
			label.text = " ";
		}
		
		// Invalidate layout here to ensure icons are positioned correctly.
		invalidateDisplayList();
	}

	/**
	 *  @private
	 */
	override protected function measure():void
	{
		super.measure();
		
		if (separatorIcon)
		{
			measuredWidth = separatorIcon.measuredWidth;
			measuredHeight = separatorIcon.measuredHeight;
			return;
		}
		
		if (_listData)
		{
			// need to determine the left/right margin needed
			// depends on whether there's an icon, a typeIcon, and/or a branchIcon
			var iconWidth:Number = MenuListData(_listData).maxMeasuredIconWidth;
			var typeIconWidth:Number = MenuListData(_listData).maxMeasuredTypeIconWidth;
			var branchIconWidth:Number = MenuListData(_listData).maxMeasuredBranchIconWidth;
			var useTwoColumns:Boolean = MenuListData(_listData).useTwoColumns;
			
			var leftMargin:Number = Math.max(getStyle("leftIconGap"), 
										useTwoColumns ? iconWidth + typeIconWidth : 
														Math.max(iconWidth, typeIconWidth));
			var rightMargin:Number = Math.max(getStyle("rightIconGap"), branchIconWidth);
	
			if (isNaN(explicitWidth))
			{
				measuredWidth = label.measuredWidth + leftMargin + rightMargin + 7;
			}
			else
			{
				label.width = explicitWidth - leftMargin - rightMargin;
			}
		
			measuredHeight = label.measuredHeight;
			
			// need to determine the height needed
			if (_icon && _icon.measuredHeight > measuredHeight)
				measuredHeight = _icon.measuredHeight;
				
			if (typeIcon && typeIcon.measuredHeight > measuredHeight)
				measuredHeight = typeIcon.measuredHeight;
				
			if (branchIcon && branchIcon.measuredHeight > measuredHeight)
				measuredHeight = branchIcon.measuredHeight;
		}
	}

	/**
	 *  @private
	 */
	override protected function updateDisplayList(unscaledWidth:Number,
												  unscaledHeight:Number):void
	{
		super.updateDisplayList(unscaledWidth, unscaledHeight);

		if (_listData)
		{
			if (Menu(_listData.owner).dataDescriptor.
				getType(_data).toLowerCase() == "separator")
			{
				if (separatorIcon)
				{
					separatorIcon.x = 2;
					separatorIcon.y = (unscaledHeight - separatorIcon.measuredHeight) / 2;
					separatorIcon.setActualSize(unscaledWidth - 4, separatorIcon.measuredHeight);
				}
				return;
			}
			
			var iconWidth:Number = MenuListData(_listData).maxMeasuredIconWidth;
			var typeIconWidth:Number = MenuListData(_listData).maxMeasuredTypeIconWidth;
			var branchIconWidth:Number = MenuListData(_listData).maxMeasuredBranchIconWidth;
			var useTwoColumns:Boolean = MenuListData(_listData).useTwoColumns;
			
			var leftMargin:Number = Math.max(getStyle("leftIconGap"), 
									useTwoColumns ? iconWidth + typeIconWidth : 
													Math.max(iconWidth, typeIconWidth));
			var rightMargin:Number = Math.max(getStyle("rightIconGap"), branchIconWidth);
			
			// check to see if laying out in two columns or not
			if (useTwoColumns)
			{				
				// if in two columns, center the two columns
				// center the respective icons (if present) in their own column
				var left:Number = (leftMargin - (iconWidth + typeIconWidth))/2
				
				if (_icon)
				{
					_icon.x = left + (iconWidth - _icon.measuredWidth)/2;
					_icon.setActualSize(_icon.measuredWidth, _icon.measuredHeight);
				}
				
				if (typeIcon)
				{
					typeIcon.x = left + iconWidth + (typeIconWidth - typeIcon.measuredWidth)/2;
					typeIcon.setActualSize(typeIcon.measuredWidth, typeIcon.measuredHeight);
				}
			}
			else
			{
				// if in one column mode, just center the one item in the left icon gap
				if (_icon)
				{
					_icon.x = (leftMargin - _icon.measuredWidth)/2;
					_icon.setActualSize(_icon.measuredWidth, _icon.measuredHeight);
				}
				
				if (typeIcon)
				{
					typeIcon.x = (leftMargin - typeIcon.measuredWidth)/2;
					typeIcon.setActualSize(typeIcon.measuredWidth, typeIcon.measuredHeight);
				}
			}
			
			if (branchIcon)
			{
				branchIcon.x = unscaledWidth - rightMargin + 
							   (rightMargin - branchIcon.measuredWidth)/2;
				branchIcon.setActualSize(branchIcon.measuredWidth,
										 branchIcon.measuredHeight);
			}

			label.x = leftMargin;		
			label.setActualSize(unscaledWidth - leftMargin - rightMargin,
								label.getExplicitOrMeasuredHeight());

			if (_listData && !Menu(_listData.owner).showDataTips)
			{
				label.text = _listData.label;
				if (label.truncateToFit())
					toolTip = _listData.label;
				else
					toolTip = null;
			}

			var verticalAlign:String = getStyle("verticalAlign");
			if (verticalAlign == "top")
			{
				label.y = 0;
				if (_icon)
					_icon.y = 0;
				if (typeIcon)
					typeIcon.y = 0;
				if (branchIcon)
					branchIcon.y = 0;
			}
			else if (verticalAlign == "bottom")
			{
				label.y = unscaledHeight - label.height + 2; // 2 for gutter
				if (_icon)
					_icon.y = unscaledHeight - _icon.height;
				if (typeIcon)
					typeIcon.y = unscaledHeight - typeIcon.height;
				if (branchIcon)
					branchIcon.y = unscaledHeight - branchIcon.height;
			}
			else
			{
				label.y = (unscaledHeight - label.height) / 2;
				if (_icon)
					_icon.y = (unscaledHeight - _icon.height) / 2;
				if (typeIcon)
					typeIcon.y = (unscaledHeight - typeIcon.height) / 2;
				if (branchIcon)
					branchIcon.y = (unscaledHeight - branchIcon.height) / 2;
			}

			var labelColor:Number;

			if (data && parent)
			{
				if (!enabled)
				{
					labelColor = getStyle("disabledColor");
				}
				else if (Menu(listData.owner).isItemHighlighted(listData.uid))
				{
					labelColor = getStyle("textRollOverColor");
				}
				else if (Menu(listData.owner).isItemSelected(listData.uid))
				{
					labelColor = getStyle("textSelectedColor");
				}
				else
				{
					labelColor = getStyle("color");
				}

				label.setColor(labelColor);
			}
		}
	}

	/**
	 *  @private
	 */
	override public function styleChanged(styleProp:String):void
	{
		super.styleChanged(styleProp);

		if (!styleProp ||
			styleProp == "styleName" ||
			(styleProp.toLowerCase().indexOf("icon") != -1))
		{
			// If any icons change, invalidate everything.
			// We could be smarter about this if it causes
			// performance problems.
			invalidateSize();
			invalidateDisplayList();
		}
	}

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Creates the title text field and adds it as a child of this component.
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
     *  Removes the title text field from this component.
     */
    mx_internal function removeLabel():void
    {
        if (label)
        {
            removeChild(DisplayObject(label));
            label = null;
        }
    }

    /**
     *  @private
     */
    mx_internal function getLabel():IUITextField
    {
        return label;
    }

    /**
     *  The width of the icon
     */
    public function get measuredIconWidth():Number
    {
    	var horizontalGap:Number = getStyle("horizontalGap");
    	return _icon ? _icon.measuredWidth + horizontalGap : 0;
    }
    
    /**
     *  The width of the type icon (radio/check)
     */
    public function get measuredTypeIconWidth():Number
    {
    	var horizontalGap:Number = getStyle("horizontalGap");
 
    	if (typeIcon)
    		return typeIcon.measuredWidth + horizontalGap;
    	
    	// even if there's no type icon, get what it's width would be...
    	if (_data)
    	{	
    		var typeIconClass:Class;
    		var dataDescriptor:IMenuDataDescriptor =
				Menu(_listData.owner).dataDescriptor;
			var isEnabled:Boolean = dataDescriptor.isEnabled(_data);
    		var typeVal:String = dataDescriptor.getType(_data);
			if (typeVal)
			{
				typeVal = typeVal.toLowerCase();
				if (typeVal == "radio")
				{
					typeIconClass = getStyle(isEnabled ?
										 "radioIcon" :
										 "radioDisabledIcon");
				}
				else if (typeVal == "check")
				{
					typeIconClass = getStyle(isEnabled ?
										 "checkIcon" :
										 "checkDisabledIcon");
				}

				if (typeIconClass)
				{
					typeIcon = new typeIconClass();
					var typeIconWidth:Number = typeIcon.measuredWidth;
					typeIcon = null;
					return typeIconWidth + horizontalGap;
				}
			}
    	}
    	
    	return 0;
    }
    
    /**
     *  The width of the branch icon
     */
    public function get measuredBranchIconWidth():Number
    {
    	var horizontalGap:Number = getStyle("horizontalGap");
        return branchIcon ? branchIcon.measuredWidth + horizontalGap : 0;
    }
}

}
