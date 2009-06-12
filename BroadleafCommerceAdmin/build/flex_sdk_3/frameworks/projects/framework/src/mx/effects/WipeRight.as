////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.effects
{

import mx.effects.effectClasses.WipeRightInstance;

/**
 *  The WipeRight class defines a bar wipe right effect.
 *  The before or after state of the component must be invisible. 
 * 
 *  <p>You often use this effect with the <code>showEffect</code> 
 *  and <code>hideEffect</code> triggers. The <code>showEffect</code> 
 *  trigger occurs when a component becomes visible by changing its 
 *  <code>visible</code> property from <code>false</code> to <code>true</code>. 
 *  The <code>hideEffect</code> trigger occurs when the component becomes 
 *  invisible by changing its <code>visible</code> property from 
 *  <code>true</code> to <code>false</code>.</p>
 *
 *  <p>This effect inherits the <code>MaskEffect.show</code> property. 
 *  If you set the value to <code>true</code>, the component appears. 
 *  If you set the value to <code>false</code>, the component disappears.
 *  The default value is <code>true</code>.</p>
 *
 *  <p>If you specify this effect for a <code>showEffect</code> or 
 *  <code>hideEffect</code> trigger, Flex sets the <code>show</code> property 
 *  for you, either to <code>true</code> if the component becomes invisible, 
 *  or <code>false</code> if the component becomes visible. </p>
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:WipeRight&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:WipeRight
 *    id="ID"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.effects.effectClasses.WipeRightInstance
 *  
 *  @includeExample examples/WipeRightExample.mxml
 */
public class WipeRight extends MaskEffect
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
	public function WipeRight(target:Object = null)
	{
		super(target);

		instanceClass = WipeRightInstance;
	}
}

}
