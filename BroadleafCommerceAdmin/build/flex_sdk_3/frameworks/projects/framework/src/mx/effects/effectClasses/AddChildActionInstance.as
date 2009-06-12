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

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import mx.core.mx_internal;

/**
 *  The AddChildActionInstance class implements the instance class
 *  for the AddChildAction effect.
 *  Flex creates an instance of this class when it plays
 *  an AddChildAction effect; you do not create one yourself.
 *
 *  @see mx.effects.AddChildAction
 */  
public class AddChildActionInstance extends ActionEffectInstance
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
	public function AddChildActionInstance(target:Object)
	{
		super(target);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  index
	//----------------------------------
	
	/** 
	 *  The index of the child within the parent.
	 */
	public var index:int = -1;
	
	//----------------------------------
	//  relativeTo
	//----------------------------------
	
	/** 
	 *  The location where the child component is added.
	 */
	public var relativeTo:DisplayObjectContainer;
	
	//----------------------------------
	//  position
	//----------------------------------
	
	/** 
	 *  The position of the child component, relative to relativeTo, where it is added.
	 */
	public var position:String;
	
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
		var targetDisplayObject:DisplayObject = DisplayObject(target);

		// Dispatch an effectStart event from the target.
		super.play();	
		
		if (!relativeTo && propertyChanges)
		{
			if (propertyChanges.start.parent == null &&
				propertyChanges.end.parent != null)
			{
				relativeTo = propertyChanges.end.parent;
				position = "index";
				index = propertyChanges.end.index;
			}
		}
		
		if (!mx_internal::playReversed)
		{
			// Set the style property
			if (target && targetDisplayObject.parent == null && relativeTo)
			{
				switch (position)
				{
					case "index":
					{
						if (index == -1)
							relativeTo.addChild(targetDisplayObject);
						else
							relativeTo.addChildAt(targetDisplayObject, 
												Math.min(index, relativeTo.numChildren));
						break;
					}
					
					case "before":
					{
						relativeTo.parent.addChildAt(targetDisplayObject,
							relativeTo.parent.getChildIndex(relativeTo));
						break;
					}

					case "after":
					{
						relativeTo.parent.addChildAt(targetDisplayObject,
							relativeTo.parent.getChildIndex(relativeTo) + 1);
						break;
					}
					
					case "firstChild":
					{
						relativeTo.addChildAt(targetDisplayObject, 0);
					}
					
					case "lastChild":
					{
						relativeTo.addChild(targetDisplayObject);
					}
				}
			}
		}
		else
		{
			if (target && relativeTo && targetDisplayObject.parent == relativeTo)
			{
				relativeTo.removeChild(targetDisplayObject);
			}
		}
		
		// We're done...
		finishRepeat();
	}
}	

}
