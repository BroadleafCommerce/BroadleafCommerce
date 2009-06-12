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

package mx.effects
{

import mx.core.mx_internal;
import mx.effects.effectClasses.PropertyChanges;
import mx.effects.effectClasses.RemoveChildActionInstance;

use namespace mx_internal;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="duration", kind="property")]

/**
 *  The RemoveChildAction class defines an action effect that corresponds
 *  to the RemoveChild property of a view state definition.
 *  You use a RemoveChildAction effect within a transition definition
 *  to control when the view state change defined by a RemoveChild property
 *  occurs during the transition.
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:RemoveChildAction&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>

 *  <pre>
 *  &lt;mx:RemoveChildAction
 *    <b>Properties</b>
 *    id="ID"
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.effects.effectClasses.RemoveChildActionInstance
 *  @see mx.states.RemoveChild
 *
 *  @includeExample ../states/examples/TransitionExample.mxml
 */
public class RemoveChildAction extends Effect
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
	public function RemoveChildAction(target:Object = null)
	{
		super(target);

		instanceClass = RemoveChildActionInstance;
	}
	
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
	private function propChangesSortHandler(
					first:PropertyChanges, 
					second:PropertyChanges):Number
	{
		if (first.start.index > second.start.index)
			return 1;
		else if (first.start.index < second.start.index)
			return -1;
		
		return 0;
	}
	
	/**
	 *  @private
	 */
	override mx_internal function applyStartValues(propChanges:Array,
									 		  targets:Array):void
	{
		if (propChanges)
			propChanges.sort(propChangesSortHandler);
		
		super.applyStartValues(propChanges, targets);
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
		if (property == "parent" && target.parent == null && value)
			value.addChildAt(target, Math.min(props.index, value.numChildren));
		
		// Ignore index - it's applied along with parent
	}
}

}
