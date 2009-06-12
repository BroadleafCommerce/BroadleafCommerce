////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import flash.system.Capabilities;
import mx.controls.Image;
import mx.core.UIComponent;
import mx.states.SetProperty;
import mx.states.State;

/**
 *  The skin for the restore button in the TitleBar
 *  of a WindowedApplication or Window.
 * 
 *  @playerversion AIR 1.1
 */
public class WindowRestoreButtonSkin extends UIComponent
{
    include "../../core/Version.as";    
    
	//--------------------------------------------------------------------------
	//
	//  Class assets
	//
	//--------------------------------------------------------------------------

    [Embed(source="../../../../assets/win_restore_up.png")]
    private static var winRestoreUpSkin:Class;
    
   	[Embed(source="../../../../assets/win_restore_down.png")]
    private static var winRestoreDownSkin:Class;
    
    [Embed(source="../../../../assets/win_restore_over.png")]
    private static var winRestoreOverSkin:Class;
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function WindowRestoreButtonSkin()
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
	private var skinImage:Image;
	
	//--------------------------------------------------------------------------
	//
	//  Overridden properties: UIComponent
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		if (skinImage.measuredHeight)
			return skinImage.measuredHeight;
		else
			return 13;
	}

	//----------------------------------
	//  measuredWidth
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredWidth():Number
	{
		if (skinImage.measuredWidth)
			return skinImage.measuredWidth;
		else
			return 12;
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
		skinImage = new Image();
		addChild(skinImage);
		
		initializeStates();
		
		skinImage.setActualSize(12, 13);
		skinImage.move(0, 0);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function initializeStates():void
	{
		var upState:State = new State();
		upState.name = "up";
		var upProp:SetProperty = new SetProperty();
		upProp.name = "source";
		upProp.target = skinImage;
		upProp.value = winRestoreUpSkin;
		upState.overrides.push(upProp);
		states.push(upState);
		
		var downState:State = new State();
		downState.name = "down";
		var downProp:SetProperty = new SetProperty();
		downProp.name = "source";
		downProp.target = skinImage;
		downProp.value = winRestoreDownSkin;
		downState.overrides.push(downProp);
		states.push(downState);
		
		var overState:State = new State();
		overState.name = "over";
		var overProp:SetProperty = new SetProperty();
		overProp.name = "source";
		overProp.target = skinImage;
		overProp.value = winRestoreOverSkin;
		overState.overrides.push(overProp);
		states.push(overState);
	}
}

}
