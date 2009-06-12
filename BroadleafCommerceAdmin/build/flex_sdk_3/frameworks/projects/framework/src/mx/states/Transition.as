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

package mx.states
{

import mx.effects.IEffect;

[DefaultProperty("effect")]

/**
 *  The Transition class defines a set of effects that play in response
 *  to a change of view state. While a view state definition
 *  defines how to change states, a transition defines the order in which
 *  visual changes occur during the state change.
 *
 *  <p>To define a transition, you set the transition property of an Application
 *  to an Array of Transition objects. </p>
 *
 *  <p>You use the <code>toState</code> and <code>fromState</code> properties of
 *  the Transition class to specify the state changes that trigger the transition.
 *  By default, both the <code>fromState</code> and <code>toState</code> properties
 *  are set to "&#42;", meaning apply the transition to any changes to the view state.</p>
 *
 *  <p>You can use the <code>fromState</code> property to explicitly specify a
 *  view state that your are changing from, and the <code>toState</code> property
 *  to explicitly specify a view state that you are changing to.
 *  If a state change matches two transitions, the <code>toState</code> property
 *  takes precedence over the <code>fromState</code> property. If more than one
 *  transition match, Flex uses the first definition in the transition array. </p>
 *
 *  <p>You use the <code>effect</code> property to specify the Effect object to play
 *  when you apply the transition. Typically, this is a composite effect object,
 *  such as the Parallel or Sequence effect, that contains multiple effects,
 *  as the following example shows:</p><pre>
 *
 *  &lt;mx:Transition id="myTransition" fromState="&#42;" toState="&#42;"&gt;
 *    &lt;mx:Parallel&gt;
 *        ...
 *    &lt;/mx:Parallel&gt;
 *  &lt;/mx:Transition&gt;
 *  </pre>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Transition&gt;</code> tag
 *  defines the following attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Transition
 *    <b>Properties</b>
 *    id="ID"
 *    effect=""
 *    fromState="&#42;"
 *    toState="&#42;"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.effects.AddChildAction
 *  @see mx.effects.RemoveChildAction
 *  @see mx.effects.SetPropertyAction
 *  @see mx.effects.SetStyleAction
 *
 *  @includeExample examples/TransitionExample.mxml
 */
public class Transition
{
	include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
	public function Transition()
	{
		super();
	}

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  effect
    //----------------------------------

	/**
	 *  The IEffect object to play when you apply the transition. Typically,
	 *  this is a composite effect object, such as the Parallel or Sequence effect,
	 *  that contains multiple effects.
	 *
	 *  <p>The <code>effect</code> property is the default property of the
	 *  Transition class. You can omit the <code>&lt;mx:effect&gt;</code> tag 
	 *  if you use MXML tag syntax.</p>
	 */
	public var effect:IEffect;

    //----------------------------------
	//  fromState
    //----------------------------------

	[Inspectable(category="General")]

    /**
     *  A String specifying the view state that your are changing from when
     *  you apply the transition. The default value is "&#42;", meaning any view state.
     *
     *  <p>You can set this property to an empty string, "",
     *  which corresponds to the base view state.</p>
     *
     *  @default "&#42;"
     */
	public var fromState:String = "*";

    //----------------------------------
	//  toState
    //----------------------------------

	[Inspectable(category="General")]

	/**
	 *  A String specifying the view state that you are changing to when
	 *  you apply the transition. The default value is "&#42;", meaning any view state.
     *
     *  <p>You can set this property to an empty string, "",
     *  which corresponds to the base view state.</p>
     *
     *  @default "&#42;"
	 */
	public var toState:String = "*";
}

}
