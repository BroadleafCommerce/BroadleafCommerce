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

import mx.core.mx_internal;

/**
 *  The SetPropertyActionInstance class implements the instance class
 *  for the SetPropertyAction effect.
 *  Flex creates an instance of this class when it plays a SetPropertyAction
 *  effect; you do not create one yourself.
 *
 *  @see mx.effects.SetPropertyAction
 */  
public class SetPropertyActionInstance extends ActionEffectInstance
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
	public function SetPropertyActionInstance(target:Object)
	{
		super(target);
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  name
	//----------------------------------

	/** 
	 *  The name of the property being changed. 
	 */
	public var name:String;
	
	//----------------------------------
	//  value
	//----------------------------------

	/** 
	 *  Storage for the value property.
	 */
	private var _value:*;
	
	/** 
	 *  The new value for the property.
	 */
	public function get value():*
	{
		var val:*;
	
		if (mx_internal::playReversed)
		{
			 val = getStartValue();
			 if (val != undefined)
			 	return val;
		}
		
		return _value;
	}
	
	/** 
	 *  @private
	 */
	public function set value(val:*):void
	{
		_value = val;
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
		
		if (value === undefined && propertyChanges)
		{
			if (name in propertyChanges.end &&
				propertyChanges.start[name] != propertyChanges.end[name])
				value = propertyChanges.end[name];
		}
		
		// Set the property
		if (target && name && value !== undefined)
		{
			if (target[name] is Number)
			{
				var propName:String = name;
				var val:Object = value;
				
				// Special case for width and height. If they are percentage values, 
				// set the percentWidth/percentHeight instead.
				if (name == "width" || name == "height")
				{
					if (val is String && val.indexOf("%") >= 0)
					{
						propName = name == "width" ? "percentWidth" : "percentHeight";
						val = val.slice(0, val.indexOf("%"));
					}
				}
				
				target[propName] = Number(val);
			}
			else if (target[name] is Boolean)
			{
				if (value is String)
					target[name] = (value.toLowerCase() == "true");
				else
					target[name] = value;
			}
			else
			{
				target[name] = value;
			}
		}
		
		// We're done...
		finishRepeat();
	}
	
	/** 
	 *  @private
	 */
	override protected function saveStartValue():*
	{
		if (name != null)
		{
			try
			{
				return target[name];
			}
			catch(e:Error)
			{
				// Do nothing. Let us return undefined.
			}
		}
		
		return undefined;
			
	}
}

}
