////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.effects
{

import mx.effects.effectClasses.PauseInstance;

/**
 *  The Pause effect is useful when sequencing effects.
 *  It does nothing for a specified period of time.
 *  If you add a Pause effect as a child of a Sequence effect,
 *  you can create a pause between the two other effects.
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Pause&gt;</code> tag
 *  inherits all the tag attributes of its superclass, 
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Pause 
 *    id="ID" 
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.effects.effectClasses.PauseInstance
 *
 *  @includeExample examples/PauseEffectExample.mxml
 */
public class Pause extends TweenEffect
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
	 *  @param target This argument is ignored by the Pause effect.
	 *  It is included for consistency with other effects.
	 */
	public function Pause(target:Object = null)
	{
		super(target);

		instanceClass = PauseInstance;
	}
	
	/**
	 *  @private
	 */
	override public function createInstances(targets:Array = null):Array
	{
		var newInstance:IEffectInstance = createInstance();
		
		return newInstance ? [ newInstance ] : [];
	}
}

}
