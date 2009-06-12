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

package mx.effects
{

import flash.display.DisplayObjectContainer;
import mx.core.mx_internal;
import mx.effects.effectClasses.AddChildActionInstance;
import mx.effects.effectClasses.PropertyChanges;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="duration", kind="property")]

/**
 *  The AddChildAction class defines an action effect that corresponds
 *  to the <code>AddChild</code> property of a view state definition.
 *  You use an AddChildAction effect within a transition definition
 *  to control when the view state change defined by an AddChild property
 *  occurs during the transition.
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:AddChildAction&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:AddChildAction
 *    <b>Properties</b>
 *    id="ID"
 *    index="-1"
 *    relativeTo=""
 *    position="index"
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.effects.effectClasses.AddChildActionInstance
 *  @see mx.states.AddChild
 *
 *  @includeExample ../states/examples/TransitionExample.mxml
 */
public class AddChildAction extends Effect
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static var AFFECTED_PROPERTIES:Array = [ "parent", "index" ];

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
	public function AddChildAction(target:Object = null)
	{
		super(target);

		instanceClass = AddChildActionInstance;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var localPropertyChanges:Array;
	
	//----------------------------------
	//  index
	//----------------------------------

	[Inspectable(category="General")]
	
	/** 
	 *  The index of the child within the parent.
	 *  A value of -1 means add the child as the last child of the parent.
	 *
	 *  @default -1
	 */
	public var index:int = -1;
		
	//----------------------------------
	//  relativeTo
	//----------------------------------

	[Inspectable(category="General")]
	
	/** 
	 *  The location where the child component is added.
	 *  By default, Flex determines this value from the <code>AddChild</code>
	 *  property definition in the view state definition.
	 */
	public var relativeTo:DisplayObjectContainer;
		
	//----------------------------------
	//  position
	//----------------------------------

	[Inspectable(category="General")]
	
	/** 
	 *  The position of the child in the display list, relative to the
	 *  object specified by the <code>relativeTo</code> property.
	 *  Valid values are <code>"before"</code>, <code>"after"</code>, 
	 *  <code>"firstChild"</code>, <code>"lastChild"</code>, and <code>"index"</code>,
	 *  where <code>"index"</code> specifies to use the <code>index</code> property 
	 *  to determine the position of the child.
	 *
	 *  @default "index"
	 */
	public var position:String = "index";
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function getAffectedProperties():Array /* of String */
	{
		return AFFECTED_PROPERTIES;
	}

	/**
	 *  @private
	 */
	private function getPropertyChanges(target:Object):PropertyChanges
	{
		for (var i:int = 0; i < localPropertyChanges.length; i++)
		{
			if (localPropertyChanges[i].target == target)
				return localPropertyChanges[i];
		}
		
		return null;
	}
	
	/**
	 *  @private
	 */
	private function targetSortHandler(first:Object, second:Object):Number
	{
		var p1:PropertyChanges = getPropertyChanges(first);
		var p2:PropertyChanges = getPropertyChanges(second);
		
		if (p1 && p2)
		{
			if (p1.start.index > p2.start.index)
				return 1;
			else if (p1.start.index < p2.start.index)
				return -1;
		}
		
		return 0;
	}

	/**
	 *  @private
	 */
	override public function createInstances(targets:Array = null):Array /* of EffectInstance */
	{
		if (!targets)
			targets = this.targets;
			
		if (targets && mx_internal::propertyChangesArray)
		{
			localPropertyChanges = mx_internal::propertyChangesArray;
			targets.sort(targetSortHandler);
		}
		
		return super.createInstances(targets);
	}

	/**
	 *  @private
	 */
	override protected function initInstance(instance:IEffectInstance):void
	{
		super.initInstance(instance);
		
		var actionInstance:AddChildActionInstance =
			AddChildActionInstance(instance);

		actionInstance.relativeTo = relativeTo;
		actionInstance.index = index;
		actionInstance.position = position;
	}
	
	/**
	 *  @private
	 */
	override protected function getValueFromTarget(target:Object,
												  property:String):*
	{
		if (property == "index")
			return target.parent ? target.parent.getChildIndex(target) : 0;
		
		return super.getValueFromTarget(target, property);
	}
		
	/**
	 *  @private
	 */	
	override protected function applyValueToTarget(target:Object,
												   property:String, 
												   value:*,
												   props:Object):void
	{
		if (property == "parent" && target.parent && value == undefined)
			target.parent.removeChild(target);
		
		// Ignore index - it's applied along with parent
	}
}

}
