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
 *  The skin for the minimize button in the TitleBar
 *  of a WindowedApplication or Window.
 * 
 *  @playerversion AIR 1.1
 */
public class WindowMinimizeButtonSkin extends UIComponent
{
    include "../../core/Version.as";    
    
	//--------------------------------------------------------------------------
	//
	//  Class assets
	//
	//--------------------------------------------------------------------------
	
	[Embed(source="../../../../assets/mac_min_up.png")]
    private static var macMinUpSkin:Class;

    [Embed(source="../../../../assets/win_min_up.png")]
    private static var winMinUpSkin:Class;
	   
	[Embed(source="../../../../assets/mac_min_over.png")]
	private static var macMinOverSkin:Class;
	
	[Embed(source="../../../../assets/win_min_over.png")]
	private static var winMinOverSkin:Class;

  	[Embed(source="../../../../assets/mac_min_down.png")]
    private static var macMinDownSkin:Class;

    [Embed(source="../../../../assets/win_min_down.png")]
    private static var winMinDownSkin:Class;
    
    [Embed(source="../../../../assets/mac_min_dis.png")]
    private static var macMinDisabledSkin:Class;

    [Embed(source="../../../../assets/win_min_dis.png")]
    private static var winMinDisabledSkin:Class;
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function WindowMinimizeButtonSkin()
	{
		super();

		isMac = Capabilities.os.substring(0,3) == "Mac";
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var isMac:Boolean;
	
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
		upProp.value = isMac ? macMinUpSkin : winMinUpSkin;
		upState.overrides.push(upProp);
		states.push(upState);
		
		var downState:State = new State();
		downState.name = "down";
		var downProp:SetProperty = new SetProperty();
		downProp.name = "source";
		downProp.target = skinImage;
		downProp.value = isMac ? macMinDownSkin : winMinDownSkin;
		downState.overrides.push(downProp);
		states.push(downState);
		
		var overState:State = new State();
		overState.name = "over";
		var overProp:SetProperty = new SetProperty();
		overProp.name = "source";
		overProp.target = skinImage;
		overProp.value = isMac ? macMinOverSkin : winMinOverSkin;
		overState.overrides.push(overProp);
		states.push(overState);
		
		var disabledState:State = new State();
		disabledState.name = "disabled";
		var disabledProp:SetProperty = new SetProperty();
		disabledProp.name = "source";
		disabledProp.target = skinImage;
		disabledProp.value = isMac ? macMinDisabledSkin : winMinDisabledSkin;
		disabledState.overrides.push(disabledProp);
		states.push(disabledState);
	}
}

}
