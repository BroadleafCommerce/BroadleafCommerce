////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
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
 *  The WipeRightInstance class implements the instance class
 *  for the WipeRight effect.
 *  Flex creates an instance of this class when it plays a WipeRight effect;
 *  you do not create one 
 *  yourself.
 *
 *  @see mx.effects.WipeRight
 */  
public class WipeRightInstance extends MaskEffectInstance
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
	public function WipeRightInstance(target:Object)
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
			
		var targetWidth:Number = target is SWFLoader && target.content ?
								 SWFLoader(target).contentWidth :
								 targetVisualBounds.width / Math.abs(target.scaleX);

		if (target.rotation != 0)
		{
			// The target.width and target.height are expressed in terms of
			// rotated coordinates, but we need to get the object's height 
			// in terms of unrotated coordinates.

			var angle:Number = target.rotation * Math.PI / 180;
			targetWidth = Math.abs(targetVisualBounds.width * Math.cos(angle) -	
								   targetVisualBounds.height * Math.sin(angle));
		}
		
		if (showTarget)
		{
			xFrom = -effectMask.width + targetVisualBounds.x;
			yFrom = targetVisualBounds.y;
			// Line up the right edges of the mask and target
			xTo = effectMask.width <= targetWidth ?
				  targetWidth - effectMask.width + targetVisualBounds.x:
				  targetVisualBounds.x;
			yTo = targetVisualBounds.y;
		}
		else
		{
			// Line up the right edges of the mask and target if mask is wider than target
			xFrom = effectMask.width <= targetWidth ?
					targetVisualBounds.x :
					targetWidth - effectMask.width + targetVisualBounds.x;
			yFrom = targetVisualBounds.y;
			xTo = targetWidth + targetVisualBounds.x;
			yTo = targetVisualBounds.y;
		}
	}
}

}
