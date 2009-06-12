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

package mx.effects.effectClasses
{

import mx.controls.SWFLoader;

/**
 *  The IrisInstance class implements the instance class for the Iris effect.
 *  Flex creates an instance of this class when it plays an Iris effect;
 *  you do not create one yourself.
 *
 *  @see mx.effects.Iris
 */  
public class IrisInstance extends MaskEffectInstance
{
    include "../../core/Version.as";

 	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  @param target The Object to animate with this effect.
	 */
	public function IrisInstance(target:Object)
	{	
		super(target);
	}

 	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
     */
	override protected function initMaskEffect():void
	{
		super.initMaskEffect();
	
		var targetHeight:Number = target is SWFLoader && target.content ?
								  SWFLoader(target).contentHeight :
								  targetVisualBounds.height / Math.abs(target.scaleY)

		var targetWidth:Number = target is SWFLoader && target.content ? 
								 SWFLoader(target).contentWidth : 
								 targetVisualBounds.width / Math.abs(target.scaleX); 
		
		if (showTarget)
		{
			scaleXFrom = 0;
			scaleYFrom = 0;
			scaleXTo = 1;
			scaleYTo = 1;
			
			xFrom = targetWidth / 2 + targetVisualBounds.x;
			yFrom = targetHeight / 2 + targetVisualBounds.y;
			xTo = targetVisualBounds.x;
			yTo = targetVisualBounds.y;
		}
		else
		{
			scaleXFrom = 1;
			scaleYFrom = 1;
			scaleXTo = 0;
			scaleYTo = 0;
			
			xFrom = targetVisualBounds.x;
			yFrom = targetVisualBounds.y;
			xTo = targetWidth / 2 + targetVisualBounds.x;
			yTo = targetHeight / 2 + targetVisualBounds.y;
		}
	}
}

}
