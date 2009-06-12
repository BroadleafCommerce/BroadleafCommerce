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

import mx.core.mx_internal;

/**
 *  The PauseInstance class implements the instance class for the Pause effect.
 *  Flex creates an instance of this class when it plays a Pause effect;
 *  you do not create one 
 *  yourself.
 *
 *  @see mx.effects.Pause
 */  
public class PauseInstance extends TweenEffectInstance
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
	 *  @param target This argument is ignored by the Pause effect.
	 *  It is included for consistency with other effects.
	 */
	public function PauseInstance(target:Object)
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
	override public function play():void
	{
		// Dispatch an effectStart event from the target.
		super.play();
		
		tween = createTween(this, 0, 0, duration);
	}
}

}
