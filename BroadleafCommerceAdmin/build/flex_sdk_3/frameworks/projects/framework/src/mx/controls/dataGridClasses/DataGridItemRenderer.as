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

package mx.controls.dataGridClasses
{

import flash.geom.Point;
import flash.geom.Rectangle;
import flash.utils.getQualifiedSuperclassName;
import flash.utils.getDefinitionByName;
import flash.utils.getQualifiedClassName;
import mx.controls.DataGrid;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.IDataRenderer;
import mx.core.IFlexDisplayObject;
import mx.core.IToolTip;
import mx.core.UIComponent;
import mx.core.UIComponentGlobals;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.events.ToolTipEvent;
import mx.managers.ILayoutManagerClient;
import mx.styles.CSSStyleDeclaration;
import mx.styles.IStyleClient;
import mx.styles.StyleManager;
import mx.styles.StyleProtoChain;

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
 *  The DataGridItemRenderer class defines the default item renderer for a DataGrid control. 
 *  By default, the item renderer 
 *  draws the text associated with each item in the grid.
 *
 *  <p>You can override the default item renderer by creating a custom item renderer.</p>
 *
 *  @see mx.controls.DataGrid
 *  @see mx.core.IDataRenderer
 *  @see mx.controls.listClasses.IDropInListItemRenderer
 */
public class DataGridItemRenderer extends UITextField
								  implements IDataRenderer,
								  IDropInListItemRenderer, ILayoutManagerClient,
								  IListItemRenderer, IStyleClient
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
	public function DataGridItemRenderer()
	{
		super();

		tabEnabled = false;
		mouseWheelEnabled = false;

		ignorePadding = false;

		addEventListener(ToolTipEvent.TOOL_TIP_SHOW, toolTipShowHandler);
		
		inheritingStyles = nonInheritingStyles =
			UIComponent.STYLE_UNINITIALIZED;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */
	private var invalidatePropertiesFlag:Boolean = false;
	
    /**
     *  @private
     */
	private var invalidateSizeFlag:Boolean = false;

 	//--------------------------------------------------------------------------
	//
	//  Overridden properties: UIComponent
	//
	//--------------------------------------------------------------------------

    //----------------------------------
    //  nestLevel
    //----------------------------------

    /**
     *  @private
     */
	override public function set nestLevel(value:int):void
	{
		super.nestLevel = value;
	
		UIComponentGlobals.layoutManager.invalidateProperties(this);
		invalidatePropertiesFlag = true;
		
		UIComponentGlobals.layoutManager.invalidateSize(this);
		invalidateSizeFlag = true;
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
     */
    private var _data:Object;

	[Bindable("dataChange")]

    /**
	 *  The implementation of the <code>data</code> property as 
	 *  defined by the IDataRenderer interface.
	 *
	 *  The value is ignored.  Only the listData property is used.
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

		dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
	}

    //----------------------------------
    //  listData
    //----------------------------------

	/**
	 *  @private
	 */
	private var _listData:DataGridListData;

	[Bindable("dataChange")]
	
	/**
	 *  The implementation of the <code>listData</code> property as 
	 *  defined by the IDropInListItemRenderer interface.
	 *  The text of the renderer is set to the <code>label</code>
	 *  property of the listData.
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
		_listData = DataGridListData(value);
		if (nestLevel && !invalidatePropertiesFlag)
		{
			UIComponentGlobals.layoutManager.invalidateProperties(this);
			invalidatePropertiesFlag = true;
			UIComponentGlobals.layoutManager.invalidateSize(this);
			invalidateSizeFlag = true;
		}
	}

    //----------------------------------
    //  styleDeclaration
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the styleDeclaration property.
	 */
	private var _styleDeclaration:CSSStyleDeclaration;

    /**
     *  Storage for the inline inheriting styles on this object.
	 *  This CSSStyleDeclaration is created the first time that setStyle()
	 *  is called on this component to set an inheriting style.
     */
    public function get styleDeclaration():CSSStyleDeclaration
	{
		return _styleDeclaration;
	}

    /**
     *  @private
	 */
    public function set styleDeclaration(value:CSSStyleDeclaration):void
	{
		_styleDeclaration = value;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: UITextField
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function initialize():void
	{
		regenerateStyleCache(false)
	}

	/**
	 *  @private
	 */
	override public function validateNow():void
	{
		if (data && parent)
		{
			var newColor:Number;

			if (DataGridBase(_listData.owner).isItemHighlighted(_listData.uid))
			{
        		newColor = getStyle("textRollOverColor");
			}
			else if (DataGridBase(_listData.owner).isItemSelected(_listData.uid))
			{
        		newColor = getStyle("textSelectedColor");
			}
			else
			{
        		newColor = getStyle("color");
			}

			if (newColor != explicitColor)
			{
				styleChangedFlag = true;
				explicitColor = newColor;
				invalidateDisplayList();
			}
		}

		super.validateNow();
	}

    /**
     *  @copy mx.core.UIComponent#getStyle()
	 */
    override public function getStyle(styleProp:String):*
    {
        return (StyleManager.inheritingStyles[styleProp]) ?        
        	inheritingStyles[styleProp] : nonInheritingStyles[styleProp];
    }

    /**
     *  @copy mx.core.UIComponent#setStyle()
	 */
    override public function setStyle(styleProp:String, newValue:*):void
    {
        if (styleProp == "styleName")
        {
            // Let the setter handle this one, see UIComponent.
            styleName = newValue;

            // Short circuit, because styleName isn't really a style.
            return;
        }

		/*
        if (styleProp == "themeColor")
        {
            // setThemeColor handles the side effects.
            setThemeColor(newValue);

            // Do not short circuit, because themeColor is a style.
            // It just has side effects, too.
        }
		*/

        // If this UIComponent didn't previously have any inline styles,
        // then regenerate the UIComponent's proto chain (and the proto chains
        // of this UIComponent's descendants).
        var isInheritingStyle:Boolean =
			StyleManager.isInheritingStyle(styleProp);
        var isProtoChainInitialized:Boolean =
			inheritingStyles != UIComponent.STYLE_UNINITIALIZED;
        if (isInheritingStyle)
        {
            if (getStyle(styleProp) == newValue && isProtoChainInitialized)
                return;

            if (!styleDeclaration)
            {
                styleDeclaration = new CSSStyleDeclaration();
                styleDeclaration.mx_internal::setStyle(styleProp, newValue);

                // If inheritingStyles is undefined, then this object is being
                // initialized and we haven't yet generated the proto chain.  To
                // avoid redundant work, don't bother to create the proto chain here.
                if (isProtoChainInitialized)
                    regenerateStyleCache(true);
            }
            else
            {
                styleDeclaration.mx_internal::setStyle(styleProp, newValue);
            }
        }
        else
        {
            if (getStyle(styleProp) == newValue && isProtoChainInitialized)
                return;

            if (!styleDeclaration)
            {
                styleDeclaration = new CSSStyleDeclaration();
                styleDeclaration.mx_internal::setStyle(styleProp, newValue);

                // If nonInheritingStyles is undefined, then this object is being
                // initialized and we haven't yet generated the proto chain.  To
                // avoid redundant work, don't bother to create the proto chain here.
                if (isProtoChainInitialized)
                    regenerateStyleCache(false);
            }
            else
            {
                styleDeclaration.mx_internal::setStyle(styleProp, newValue);
            }
        }

        if (isProtoChainInitialized)
        {
            styleChanged(styleProp);
            notifyStyleChangeInChildren(styleProp, isInheritingStyle);
        }
    }

 	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  If Flex calls the <code>LayoutManager.invalidateProperties()</code> 
	 *  method on this ILayoutManagerClient, then
	 *  this function is called when it's time to commit property values.
	 */
	public function validateProperties():void
	{
		invalidatePropertiesFlag = false;
		if (_listData)
		{
			var dg:DataGrid = DataGrid(_listData.owner);

			var column:DataGridColumn =
				dg.columns[_listData.columnIndex];

			text = _listData.label;
			
			if (_data is DataGridColumn)
				wordWrap = dg.columnHeaderWordWrap(column);
			else
				wordWrap = dg.columnWordWrap(column);
			
			if (DataGrid(_listData.owner).variableRowHeight)
				multiline = true;
			
			var dataTips:Boolean = dg.showDataTips;
			if (column.showDataTips == true)
				dataTips = true;
			if (column.showDataTips == false)
				dataTips = false;
			if (dataTips)
			{
				if (!(_data is DataGridColumn) && (textWidth > width 
					|| column.dataTipFunction || column.dataTipField 
					|| dg.dataTipFunction || dg.dataTipField))
				{
					toolTip = column.itemToDataTip(_data);
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
			text = " ";
			toolTip = null;
		}
	}

	/**
	 *  If Flex calls the <code>LayoutManager.invalidateSize()</code>
	 *  method on this ILayoutManagerClient, then
	 *  this function is called when it's time to do measurements.
	 *
	 *  @param recursive If <code>true</code>, call this method
	 *  on the object's children.
	 */
	public function validateSize(recursive:Boolean = false):void
	{
		invalidateSizeFlag = false;
		validateNow();
	}

	/**
	 *  If Flex calls <code>LayoutManager.invalidateDisplayList()</code> 
	 *  method on this ILayoutManagerClient, then
	 *  this function is called when it's time to update the display list.
	 */
	public function validateDisplayList():void
	{
		validateNow();
	}

    /**
     *  @copy mx.core.UIComponent#clearStyle()
	 */
	public function clearStyle(styleProp:String):void
	{
		setStyle(styleProp, undefined);
	}

    /**
	 *  @inheritDoc
     */
    public function notifyStyleChangeInChildren(
						styleProp:String, recursive:Boolean):void
    {    
	}

    /**
	 *  Sets up the <code>inheritingStyles</code> 
	 *  and <code>nonInheritingStyles</code> objects
	 *  and their proto chains so that the <code>getStyle()</code> method can work.
     */
    public function initProtoChain():void
    {
		styleChangedFlag = true;

        var classSelector:CSSStyleDeclaration;

        if (styleName)
        {
            if (styleName is CSSStyleDeclaration)
            {
                // Get the style sheet referenced by the styleName property
                classSelector = CSSStyleDeclaration(styleName);
            }
            else if (styleName is IFlexDisplayObject)
            {
                // If the styleName property is a UIComponent, then there's a
                // special search path for that case.
                StyleProtoChain.initProtoChainForUIComponentStyleName(this);
                return;
            }
            else if (styleName is String)
            {
                // Get the style sheet referenced by the styleName property
                classSelector =
					StyleManager.getStyleDeclaration("." + styleName);
            }
        }

        // To build the proto chain, we start at the end and work forward.
        // Referring to the list at the top of this function, we'll start by
        // getting the tail of the proto chain, which is:
        //  - for non-inheriting styles, the global style sheet
        //  - for inheriting styles, my parent's style object
		var nonInheritChain:Object = StyleManager.stylesRoot;
		/*
        if (nonInheritChain.effects)
            registerEffects(nonInheritChain.effects);
		*/
        var p:IStyleClient = parent as IStyleClient;
        if (p)
        {
            var inheritChain:Object = p.inheritingStyles;
            if (inheritChain == UIComponent.STYLE_UNINITIALIZED)
                inheritChain = nonInheritChain;
        }
        else
        {
        	// Pop ups inheriting chain starts at Application instead of global.
        	// This allows popups to grab styles like themeColor that are
        	// set on Application.
        	// if (isPopUp)
        		// inheritChain = Application.application.inheritingStyles;
        	// else
            	inheritChain = StyleManager.stylesRoot;
        }

        // Working backwards up the list, the next element in the
        // search path is the type selector
		var typeSelectors:Array = getClassStyleDeclarations();
		var n:int = typeSelectors.length;
		for (var i:int = 0; i < n; i++)
		{
			var typeSelector:CSSStyleDeclaration = typeSelectors[i];
            inheritChain = typeSelector.addStyleToProtoChain(inheritChain, this);

            nonInheritChain = typeSelector.addStyleToProtoChain(nonInheritChain, this);
			/*
            if (typeSelector.effects)
                registerEffects(typeSelector.effects);
			*/
        }

        // Next is the class selector
        if (classSelector)
        {
            inheritChain = classSelector.addStyleToProtoChain(inheritChain, this);

            nonInheritChain = classSelector.addStyleToProtoChain(nonInheritChain, this);
			/*
            if (classSelector.effects)
                registerEffects(classSelector.effects);
				*/
        }

        // Finally, we'll add the in-line styles
		// to the head of the proto chain.
        inheritingStyles = styleDeclaration ?
						   styleDeclaration.addStyleToProtoChain(inheritChain, this) :
						   inheritChain;

        nonInheritingStyles = styleDeclaration ?
							  styleDeclaration.addStyleToProtoChain(nonInheritChain, this) :
							  nonInheritChain;
    }

    /**
	 *  @inheritDoc
     */
    public function regenerateStyleCache(recursive:Boolean):void
    {
		initProtoChain();
	}

    /**
     *  @inheritDoc
     */
    public function registerEffects(effects:Array /* of String */):void
    {
	}

    /**
	 *  @inheritDoc
     */
    public function getClassStyleDeclarations():Array
    {
		var className:String = getQualifiedClassName(this)
		className = className.replace("::", ".");

		var decls:Array = [];

		while (className != null &&
			   className != "mx.core.UIComponent" &&
			   className != "mx.core.UITextField")
		{
			var s:CSSStyleDeclaration =
				StyleManager.getStyleDeclaration(className);
			
			if (s)
			{
			 	decls.unshift(s);
			}
			
			try
			{
				className = getQualifiedSuperclassName(getDefinitionByName(className));
				className = className.replace("::", ".");
			}
			catch(e:ReferenceError)
			{
				className = null;
			}
		}	

		return decls;
    }

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  The event handler to position the tooltip.
	 *
	 *  @param event The event object.
	 */
	protected function toolTipShowHandler(event:ToolTipEvent):void
	{
		var toolTip:IToolTip = event.toolTip;

		// Calculate global position of label.
		var pt:Point = new Point(0, 0);
		pt = localToGlobal(pt);
		pt = stage.globalToLocal(pt);			
		
		toolTip.move(pt.x, pt.y + (height - toolTip.height) / 2);
			
		var screen:Rectangle = systemManager.screen;
		var screenRight:Number = screen.x + screen.width;
		if (toolTip.x + toolTip.width > screenRight)
			toolTip.move(screenRight - toolTip.width, toolTip.y);
	}
}

}
