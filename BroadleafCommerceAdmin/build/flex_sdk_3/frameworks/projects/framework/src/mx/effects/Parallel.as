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

import mx.effects.effectClasses.ParallelInstance;

/**
 *  The Parallel effect plays multiple child effects at the same time.
 *  
 *  <p>You can create a Paralell effect in MXML,
 *  as the following example shows:</p>
 *
 *  <pre>
 *  &lt;mx:Parallel id="WipeRightUp"&gt;
 *    &lt;mx:children&gt;
 *      &lt;mx:WipeRight duration="1000"/&gt;
 *      &lt;mx:WipeUp duration="1000"/&gt;
 *    &lt;/mx:children&gt;
 *  &lt;/mx:Parallel&gt;
 *  
 *  &lt;mx:VBox id="myBox" hideEffect="{WipeRightUp}" &gt;
 *    &lt;mx:TextArea id="aTextArea" text="hello"/&gt;
 *  &lt;/mx:VBox&gt;
 *  </pre>
 *
 *  <p>Notice that the <code>&lt;mx:children&gt;</code> tag is optional.</p>
 *  
 *  <p>Starting a Parallel effect in ActionScript is usually
 *  a five-step process:</p>
 *
 *  <ol>
 *    <li>Create instances of the effect objects to be composited together;
 *    for example: 
 *    <pre>myFadeEffect = new mx.effects.Fade(target);</pre></li>
 *    <li>Set properties, such as <code>duration</code>, on the individual effect objects.</li>
 *    <li>Create an instance of the Parallel effect object;  
 *    for example: 
 *    <pre>myParallelEffect = new mx.effects.Parallel();</pre></li>
 *    <li>Call the <code>addChild()</code> method for each of the effect objects;
 *    for example: 
 *    <pre>myParallelEffect.addChild(myFadeEffect);</pre></li>
 *    <li>Invoke the Parallel effect's <code>play()</code> method; 
 *    for example: 
 *    <pre>myParallelEffect.play();</pre></li>
 *  </ol>
 *
 *  @mxml
 *
 *  <p>The &lt;mx:Parallel&gt; tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Parallel id="<i>identifier</i>"&gt;
 *    &lt;mx:children&gt;
 *      &lt;!-- Specify child effect tags --&gt; 
 *    &lt;/mx:children&gt;
 *  &lt;/mx:Parallel&gt;
 *  </pre>
 *
 *  @see mx.effects.effectClasses.ParallelInstance
 *  
 *  @includeExample examples/ParallelEffectExample.mxml
 */
public class Parallel extends CompositeEffect
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
	 *  @param target This argument is ignored for Parallel effects.
	 *  It is included only for consistency with other types of effects.
	 */
	public function Parallel(target:Object = null)
	{
		super(target);

		instanceClass = ParallelInstance;
	}
}

}
