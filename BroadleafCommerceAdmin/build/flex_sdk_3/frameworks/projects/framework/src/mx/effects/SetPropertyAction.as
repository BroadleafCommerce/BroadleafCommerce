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

import mx.effects.effectClasses.SetPropertyActionInstance;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="duration", kind="property")]

/**
 *  The SetPropertyAction class defines an action effect that corresponds
 *  to the <code>SetProperty property</code> of a view state definition.
 *  You use a SetPropertyAction effect within a transition definition
 *  to control when the view state change defined by a
 *  <code>SetProperty</code> property occurs during the transition.
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:SetPropertyAction&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 * 
 *  <pre>
 *  &lt;mx:SetPropertyAction
 *    <b>Properties</b>
 *    id="ID"
 *    name=""
 *    value=""
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.effects.effectClasses.SetPropertyActionInstance
 *  @see mx.states.SetProperty
 *
 *  @includeExample ../states/examples/TransitionExample.mxml
 */
public class SetPropertyAction extends Effect
{
    include "../core/Version.as";

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
	public function SetPropertyAction(target:Object = null)
	{
		super(target);

		instanceClass = SetPropertyActionInstance;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  name
	//----------------------------------

	[Inspectable(category="General")]
	
	/** 
	 *  The name of the property being changed.
	 *  By default, Flex determines this value from the
	 *  <code>SetProperty</code> property definition
	 *  in the view state definition.
	 */
	public var name:String;
	
	//----------------------------------
	//  value
	//----------------------------------

	[Inspectable(category="General")]
	
	/** 
	 *  The new value for the property.
	 *  By default, Flex determines this value from the
	 *  <code>SetProperty</code> property definition
	 *  in the view state definition.
	 */
	public var value:*;
		
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
		return [ name ];
	}

	/**
	 *  @private
	 */
	override protected function initInstance(instance:IEffectInstance):void
	{
		super.initInstance(instance);
		
		var actionInstance:SetPropertyActionInstance =
			SetPropertyActionInstance(instance);

		actionInstance.name = name;
		actionInstance.value = value;
	}
}

}
