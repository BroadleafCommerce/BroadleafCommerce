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
import flash.events.Event;
import mx.core.mx_internal;

/**
 *  The RemoveChildActionInstance class implements the instance class
 *  for the RemoveChildAction effect.
 *  Flex creates an instance of this class when it plays a RemoveChildAction
 *  effect; you do not create one yourself.
 *
 *  @see mx.effects.RemoveChildAction
 */  
public class RemoveChildActionInstance extends ActionEffectInstance
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
	public function RemoveChildActionInstance(target:Object)
	{
		super(target);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var _startIndex:Number;

	/**
	 *  @private
	 */
	private var _startParent:DisplayObjectContainer;
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function initEffect(event:Event):void
	{
		super.initEffect(event);
	}
	
	/**
	 *  @private
	 */
	override public function play():void
	{
		var targetDisplayObject:DisplayObject = DisplayObject(target);

		var doRemove:Boolean = true;
		
		// Dispatch an effectStart event from the target.
		super.play();	
		
		if (propertyChanges)
		{
			doRemove = (propertyChanges.start.parent != null &&
						propertyChanges.end.parent == null)
		}
		
		if (!mx_internal::playReversed)
		{
			// Set the style property
			if (doRemove && target && targetDisplayObject.parent != null)
				targetDisplayObject.parent.removeChild(targetDisplayObject);
		}
		else if (_startParent && !isNaN(_startIndex))
		{
			_startParent.addChildAt(targetDisplayObject, _startIndex);
		}
		
		// We're done...
		finishRepeat();
	}
	
	/** 
	 *  @private
	 */
	override protected function saveStartValue():*
	{
		var targetDisplayObject:DisplayObject = DisplayObject(target);

		if (target && targetDisplayObject.parent != null)
		{
			_startIndex =
				targetDisplayObject.parent.getChildIndex(targetDisplayObject);
			_startParent = targetDisplayObject.parent;
		}
	}
}

}
