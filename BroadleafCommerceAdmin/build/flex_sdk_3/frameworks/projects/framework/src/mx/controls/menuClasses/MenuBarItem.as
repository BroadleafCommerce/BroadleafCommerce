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

package mx.controls.menuClasses
{
    
import flash.display.DisplayObject;
import flash.text.TextLineMetrics;
import mx.controls.MenuBar;
import mx.core.FlexVersion;
import mx.core.IFlexDisplayObject; 
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.IProgrammaticSkin;
import mx.core.IStateClient;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.styles.CSSStyleDeclaration;
import mx.styles.ISimpleStyleClient;

use namespace mx_internal;

include "../../styles/metadata/LeadingStyle.as"
include "../../styles/metadata/TextStyles.as"

/**
 *  The MenuBarItem class defines the default item 
 *  renderer for the top-level menu bar of a MenuBar control. 
 *  By default, the item renderer draws the text associated
 *  with each item in the top-level menu bar, and an optional icon. 
 *
 *  <p>A MenuBarItem
 *  instance passes mouse and keyboard interactions to the MenuBar so 
 *  that the MenuBar can correctly show and hide menus. </p>
 *
 *  <p>You can override the default MenuBar item renderer
 *  by creating a custom item renderer that implements the 
 *  IMenuBarItemRenderer interface.</p>
 * 
 *  <p>You can also define an item renderer for the pop-up submenus 
 *  of the MenuBar control. 
 *  Because each pop-up submenu is an instance of the Menu control, 
 *  you use the class MenuItemRenderer to define an item renderer 
 *  for the pop-up submenus.</p>
 *
 *  @see mx.controls.MenuBar
 *  @see mx.controls.Menu
 *  @see mx.controls.menuClasses.IMenuBarItemRenderer
 *  @see mx.controls.menuClasses.MenuItemRenderer
 */

public class MenuBarItem extends UIComponent implements IMenuBarItemRenderer, IFontContextComponent
    {
    
    include "../../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var leftMargin:int = 20;
        
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function MenuBarItem()
    {
        super();
        mouseChildren = false;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  currentSkin
    //----------------------------------

    /**
     *  The skin defining the border and background for this MenuBarItem.
     */
    mx_internal var currentSkin:IFlexDisplayObject;
    
    //----------------------------------
    //  icon
    //----------------------------------

    /**
     *  The IFlexDisplayObject that displays the icon in this MenuBarItem.
     */
    protected var icon:IFlexDisplayObject;

    //----------------------------------
    //  label
    //----------------------------------

    /**
     *  The UITextField that displays the text in this MenuBarItem.
     */
    protected var label:IUITextField;
    
    /**
     *  The default skin's style name
     */
    mx_internal var skinName:String = "itemSkin";
    
    /**
     *  Flags used to save information about the skin and icon styles
     */
    private var defaultSkinUsesStates:Boolean = false;
    private var checkedDefaultSkin:Boolean = false;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden properties: UIComponent
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  The baselinePosition of a MenuBarItem is calculated
     *  for its label.
     */
    override public function get baselinePosition():Number
    {
		if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
			return super.baselinePosition;
			
		if (!validateBaselinePosition())
			return NaN;
		
		return label.y + label.baselinePosition;
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  @private
     */
    private var enabledChanged:Boolean = false;

    /**
     *  @private
     */
    override public function set enabled(value:Boolean):void
    {
        if (super.enabled == value)
            return;
            
        super.enabled = value;
        enabledChanged = true;

        invalidateProperties();
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
     *  All item renderers must implement the IDataRenderer interface.
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
    //  menuBar
    //----------------------------------

    /**
     *  @private
     *  Storage for the menuBar property. 
     */
    private var _menuBar:MenuBar;

    /**
     *  The implementation of the <code>menuBar</code> property
     *  as defined by the IMenuBarItemRenderer interface. 
     *  
     *  @copy mx.controls.menuClasses.IMenuBarItemRenderer#menuBar 
     * 
     *  @see mx.controls.menuClasses.IMenuBarItemRenderer#menuBar
     */
    public function get menuBar():MenuBar
    {
        return _menuBar;
    }

    /**
     *  @private
     */
    public function set menuBar(value:MenuBar):void
    {
        _menuBar = value;
    }   
        
    //----------------------------------
    //  menuBarItemIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the menuBarItemIndex property. 
     */
    private var _menuBarItemIndex:int = -1;

     /**
     *  The implementation of the <code>menuBarItemIndex</code> property
     *  as defined by the IMenuBarItemRenderer interface.  
     *  
     *  @copy mx.controls.menuClasses.IMenuBarItemRenderer#menuBarItemIndex 
     * 
     *  @see mx.controls.menuClasses.IMenuBarItemRenderer#menuBarItemIndex
     */
    public function get menuBarItemIndex():int
    {
        return _menuBarItemIndex;
    }

    /**
     *  @private
     */
    public function set menuBarItemIndex(value:int):void
    {
        _menuBarItemIndex = value;
    }   
    
    //----------------------------------
    //  menuBarItemState
    //----------------------------------

    /**
     *  @private
     *  Storage for the menuBarItemState property. 
     */
    private var _menuBarItemState:String;

    /**
     *  The implementation of the <code>menuBarItemState</code> property
     *  as defined by the IMenuBarItemRenderer interface.  
     * 
     *  @copy mx.controls.menuClasses.IMenuBarItemRenderer#menuBarItemState
     * 
     *  @see mx.controls.menuClasses.IMenuBarItemRenderer#menuBarItemState
     */
    public function get menuBarItemState():String
    {
        return _menuBarItemState;
    }

    /**
     *  @private
     */
    public function set menuBarItemState(value:String):void
    {
        _menuBarItemState = value;
        viewSkin(_menuBarItemState);
    }   
    
    //--------------------------------------------------------------------------
    //
    //  Deprecated Properties 
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
	//  dataProvider
	//----------------------------------

	/**
	 *  @private
	 *  Storage for data provider
	 */
	private var _dataProvider:Object;
	
	/**
	 *  The object that provides the data for the Menu that is popped up
	 *  when this MenuBarItem is selected.
	 * 
	 *  @default "undefined"
	 */
	public function get dataProvider():Object
	{
		return _dataProvider
	}

	[Deprecated(replacement="MenuBarItem.data")]

	/**
	 *  @private
	 */
	public function set dataProvider(value:Object):void
	{
		_dataProvider = value;

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
        
        var styleDeclaration:CSSStyleDeclaration = new CSSStyleDeclaration();
        styleDeclaration.factory = function():void
        {
            this.borderStyle = "none"
        };

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
        
        if (enabledChanged)
        {
            enabledChanged = false;
            if (label)
                label.enabled = enabled;

            if (!enabled)
                menuBarItemState = "itemUpSkin";
        }
        
        //Remove any existing icons. 
        //These will be recreated below if needed.
        if (icon)
        {
            removeChild(DisplayObject(icon));
            icon = null;
        }
        
        if (_data)
        {
            iconClass = menuBar.itemToIcon(data);
            if (iconClass)
            {
                icon = new iconClass();
                addChild(DisplayObject(icon));
            }
            
            label.visible = true;
            var labelText:String;
            if (menuBar.labelFunction != null)
                labelText = menuBar.labelFunction(_data);
            if (labelText == null)
                labelText = menuBar.itemToLabel(_data);
            label.text = labelText;
            label.enabled = enabled;    
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
        
        if (icon && leftMargin < icon.measuredWidth)
        {
            leftMargin = icon.measuredWidth;
        }
        measuredWidth = label.getExplicitOrMeasuredWidth() + leftMargin;
        measuredHeight = label.getExplicitOrMeasuredHeight();
        
        if (icon && icon.measuredHeight > measuredHeight)
            measuredHeight = icon.measuredHeight + 2;
    }   
        
    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        
        if (icon)
        {
            icon.x = (leftMargin - icon.measuredWidth) / 2;
            icon.setActualSize(icon.measuredWidth, icon.measuredHeight);
            label.x = leftMargin;
        }
        else
            label.x = leftMargin / 2;
            
        label.setActualSize(unscaledWidth - leftMargin, 
            label.getExplicitOrMeasuredHeight());
            
        label.y = (unscaledHeight - label.height) / 2;
        
        if (icon)
            icon.y = (unscaledHeight - icon.height) / 2;
            
        menuBarItemState = "itemUpSkin";
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
            label.selectable = false;

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
        
    /**
     *  @private
     */
    private function viewSkin(state:String):void
    {  	
    	var newSkinClass:Class = Class(getStyle(state));
    	var newSkin:IFlexDisplayObject;
    	var stateName:String = "";
    	    	
    	if (!newSkinClass)
    	{
    		// Try the default skin
    		newSkinClass = Class(getStyle(skinName)); 		
    		    	
    		if (state == "itemDownSkin")
    			stateName = "down";
    		else if (state == "itemOverSkin")
    			stateName = "over";
    		else if (state == "itemUpSkin")
    			stateName = "up";
    		
    		// If we are using the default skin, then 
    		if (defaultSkinUsesStates)
    			state = skinName;
    		
     		if (!checkedDefaultSkin && newSkinClass)
    		{
	    		newSkin = IFlexDisplayObject(new newSkinClass());
	    		// Check if the skin class is a state client or a programmatic skin
	    		if (!(newSkin is IProgrammaticSkin) && newSkin is IStateClient)
	    		{
	    			defaultSkinUsesStates = true;
	    			state = skinName;
	    		}
	    		
	    		if (newSkin)
	    		{
		    		checkedDefaultSkin = true;
		    	}
    		}
    	}
    	
      	newSkin = IFlexDisplayObject(getChildByName(state));

        if (!newSkin)
        {
            if (newSkinClass)
            {
                newSkin = new newSkinClass();

                DisplayObject(newSkin).name = state;

                if (newSkin is ISimpleStyleClient)
                    ISimpleStyleClient(newSkin).styleName = this;

                addChildAt(DisplayObject(newSkin), 0);
            }
        }

        newSkin.setActualSize(unscaledWidth, unscaledHeight);

        if (currentSkin)
            currentSkin.visible = false;

        if (newSkin)
            newSkin.visible = true;

        currentSkin = newSkin;
        
        // Update the state of the skin if it accepts states and it implements the IStateClient interface.
		if (defaultSkinUsesStates && currentSkin is IStateClient)
		{
			IStateClient(currentSkin).currentState = stateName;
		}
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
