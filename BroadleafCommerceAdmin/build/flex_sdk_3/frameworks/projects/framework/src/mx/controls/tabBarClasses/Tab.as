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

package mx.controls.tabBarClasses
{

import flash.display.DisplayObject;
import flash.text.TextLineMetrics;
import mx.controls.Button;
import mx.core.IFlexDisplayObject;
import mx.core.IProgrammaticSkin;
import mx.core.IStateClient;
import mx.core.mx_internal;
import mx.styles.ISimpleStyleClient;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class Tab extends Button
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
	public function Tab()
	{
		super();

		// Tabs are not tab-enabled.
		// The TabNavigator handles all focus management.
		focusEnabled = false;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var focusSkin:IFlexDisplayObject;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: UIComponent
	//
	//--------------------------------------------------------------------------
    
    /**
	 *  @private
	 */
    override public function measureText(text:String):TextLineMetrics
    {
        // ToggleButtonBar can swap out our textField style if we are the
        // selected tab, so we override measureText to ensure Button uses the
        // textField's textFormat rather than its own to size the
        // label (when appropriate).
        return (textField.styleName == this) ? super.measureText(text) :
            textField.getUITextFormat().measureText(text);
    }
    
	/**
	 *  @private
	 */
	override protected function updateDisplayList(unscaledWidth:Number,
												  unscaledHeight:Number):void
	{
		super.updateDisplayList(unscaledWidth, unscaledHeight);

		if (currentIcon)
		{
			currentIcon.scaleX = 1.0;
			currentIcon.scaleY = 1.0;
		}

		viewIcon();
	}

	/**
	 *  @private
	 */
	override public function drawFocus(isFocused:Boolean):void
	{
		// To draw the focused state, we just swap in a rollover state.
		if (isFocused && !selected && !isEffectStarted)
		{
			if (!focusSkin)
			{
				var checkDefaultUsesStates:Boolean = false;
				
				var focusClass:Class = getStyle(overSkinName);
				
				if (!focusClass)
				{
					focusClass = getStyle(skinName);
					checkDefaultUsesStates = true;
				}
				
				if (focusClass)
				{
				
					focusSkin = new focusClass();
					
					DisplayObject(focusSkin).name = overSkinName;
					if (focusSkin is ISimpleStyleClient)
						ISimpleStyleClient(focusSkin).styleName = this;
	
					addChild(DisplayObject(focusSkin));
					
					if (checkDefaultUsesStates && !(focusSkin is IProgrammaticSkin) && focusSkin is IStateClient)
					{
						IStateClient(focusSkin).currentState = "over";
					}
				}
			}

			invalidateDisplayList();
			validateNow();
		}
		else
		{
			if (focusSkin)
			{
				removeChild(DisplayObject(focusSkin));
				focusSkin = null;
			}
		}
	}
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Button
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override mx_internal function layoutContents(unscaledWidth:Number,
												 unscaledHeight:Number, 
												 offset:Boolean):void
	{
		super.layoutContents(unscaledWidth, unscaledHeight, offset);

		// If we're pressed, offset the label down by a pixel
		if (selected)
		{
			textField.y++;

			if (currentIcon)
				currentIcon.y++;
		}

		// This is copied from Button with the addition of layering in
		// the focusSkin if we have one.
		if (currentSkin)
			setChildIndex(DisplayObject(currentSkin), numChildren - 1);
		
		if (focusSkin && !selected)
		{
			focusSkin.setActualSize(unscaledWidth, unscaledHeight);
			setChildIndex(DisplayObject(focusSkin), numChildren - 1);
		}

		if (currentIcon)
			setChildIndex(DisplayObject(currentIcon), numChildren - 1);
		
		if (textField)
			setChildIndex(DisplayObject(textField), numChildren - 1);
	}

	/**
	 *  @private
	 */
	override mx_internal function viewIcon():void
	{
		super.viewIcon();

		if (currentIcon)
		{
			if (height != 0 && currentIcon.height > height - 4)
			{
				var scale:Number = (height - 4) / currentIcon.height;

				currentIcon.scaleX = scale;
				currentIcon.scaleY = scale;
				invalidateSize();
				if (height > 0)
					layoutContents(width, height, false);
			}
		}
	}
}

}
