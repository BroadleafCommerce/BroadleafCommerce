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
 *  AddRemoveEffectTargetFilter is a subclass of EffectTargetFilter that handles
 *  the logic for filtering targets that have been added or removed as
 *  children to a container.
 *  If you set the Effect.filter property to "add" or "remove",
 *  then one of these is used. 
 */
public class AddRemoveEffectTargetFilter extends EffectTargetFilter
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
	public function AddRemoveEffectTargetFilter()
	{
		super();

		filterProperties = [ "parent" ];
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  add
	//----------------------------------

	/**
	 *  Determines if this is an add or remove filter.
	 *  
	 *  @default true
	 */
	public var add:Boolean = true;
	
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
			{
				if (add)
				{
					return props.start["parent"] == null &&
						   props.end["parent"] != null;
				}
				else
				{
					return props.start["parent"] != null &&
						   props.end["parent"] == null;
				}
			}
		}
		
		return false;
	}
}

}
