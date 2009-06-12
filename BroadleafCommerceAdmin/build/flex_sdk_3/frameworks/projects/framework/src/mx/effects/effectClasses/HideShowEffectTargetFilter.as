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

import mx.effects.EffectTargetFilter;

/**
 *  HideShowEffectTargetFilter is a subclass of EffectTargetFilter
 *  that handles the logic for filtering targets that have been shown or hidden
 *  by modifying their <code>visible</code> property.
 *  If you set the Effect.filter property to <code>hide</code>
 *  or <code>show</code>, one of these is used.
 */
public class HideShowEffectTargetFilter extends EffectTargetFilter
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
	public function HideShowEffectTargetFilter()
	{
		super();

		filterProperties = [ "visible" ];
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  show
	//----------------------------------

	/**
	 *  Determines if this is a show or hide filter.
	 * 
	 *  @default true
	 */
	public var show:Boolean = true;
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override protected function defaultFilterFunction(
									propChanges:Array,
									instanceTarget:Object):Boolean
	{
		var n:int = propChanges.length;
		for (var i:int = 0; i < n; i++)
		{
			var props:PropertyChanges = propChanges[i];
			
			if (props.target == instanceTarget)
				return props.end["visible"] == show;
		}
		
		return false;
	}
}

}
