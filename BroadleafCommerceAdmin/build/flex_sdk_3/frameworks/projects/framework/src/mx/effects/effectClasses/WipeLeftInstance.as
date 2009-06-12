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
 *  The WipeLeftInstance class implements the instance class
 *  for the WipeLeft effect.
 *  Flex creates an instance of this class when it plays a WipeLeft effect;
 *  you do not create one yourself.
 *
 *  @see mx.effects.WipeLeft
 */  
public class WipeLeftInstance extends MaskEffectInstance
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
	public function WipeLeftInstance(target:Object)
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

		var targetHeight:Number = target is SWFLoader && target.content ?
								  SWFLoader(target).contentHeight :
								  targetVisualBounds.height / Math.abs(target.scaleY);
		
		if (target.rotation != 0)
		{
			// The target.width and target.height are expressed in terms of
			// rotated coordinates, but we need to get the object's height 
			// in terms of unrotated coordinates.

			var angle:Number = target.rotation * Math.PI / 180;
			targetWidth = Math.abs(targetWidth * Math.cos(angle) -	
								   targetHeight * Math.sin(angle));
		}
		
		if (showTarget)
		{
			xFrom = targetWidth + targetVisualBounds.x;
			yFrom = targetVisualBounds.y;
			// Line up the right edges of the mask and target
			xTo = effectMask.width <= targetWidth ?
				  targetWidth - effectMask.width + targetVisualBounds.x:
				  targetVisualBounds.x;
			yTo = targetVisualBounds.y;
		}
		else
		{
			xFrom = effectMask.width <= targetWidth ?
					targetWidth - effectMask.width + targetVisualBounds.x:
					targetVisualBounds.x;
			yFrom = targetVisualBounds.y;
			xTo = -effectMask.width + targetVisualBounds.x;
			yTo = targetVisualBounds.y;
		}
	}
}

}
