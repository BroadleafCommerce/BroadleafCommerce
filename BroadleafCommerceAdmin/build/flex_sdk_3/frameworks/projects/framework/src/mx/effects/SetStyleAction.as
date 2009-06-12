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

import mx.effects.effectClasses.SetStyleActionInstance;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="duration", kind="property")]

/**
 *  The SetStyleAction class defines an action effect that corresponds
 *  to the SetStyle property of a view state definition.
 *  You use an SetStyleAction effect within a transition definition
 *  to control when the view state change defined by a 
 *  <code>SetStyle</code> property occurs during the transition.
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:SetStyleAction&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:SetStyleAction
 *    <b>Properties</b>
 *    id="ID"
 *    style=""
 *    value=""
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.effects.effectClasses.SetStyleActionInstance
 *  @see mx.states.SetStyle
 *
 *  @includeExample ../states/examples/TransitionExample.mxml
 */
 public class SetStyleAction extends Effect
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
	public function SetStyleAction(target:Object = null)
	{
		super(target);

		instanceClass = SetStyleActionInstance;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  relevantStyles
	//----------------------------------

	/**
	 *  Contains the style properties modified by this effect. 
	 *  This getter method overrides the superclass method.
	 *
	 *  <p>If you create a subclass of this class to create a custom effect, 
	 *  you must override this method 
	 *  and return an Array that contains a list of the style properties 
	 *  modified by your subclass.</p>
	 *
	 *  @return An Array of Strings specifying the names of the 
	 *  style properties modified by this effect.
	 *
	 *  @see mx.effects.Effect#getAffectedProperties()
	 */
	override public function get relevantStyles():Array /* of String */
	{
		return [ name ];
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
	 *  The name of the style property being changed.
	 *  By default, Flex determines this value from the <code>SetStyle</code>
	 *  property definition in the view state definition.
	 */
	public var name:String;
	
	//----------------------------------
	//  value
	//----------------------------------

	[Inspectable(category="General")]
	
	/** 
	 *  The new value for the style property.
	 *  By default, Flex determines this value from the <code>SetStyle</code>
	 *  property definition in the view state definition.
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
	override protected function initInstance(instance:IEffectInstance):void
	{
		super.initInstance(instance);
		
		var actionInstance:SetStyleActionInstance =
			SetStyleActionInstance(instance);
		actionInstance.name = name;
		actionInstance.value = value;
	}
}

}
