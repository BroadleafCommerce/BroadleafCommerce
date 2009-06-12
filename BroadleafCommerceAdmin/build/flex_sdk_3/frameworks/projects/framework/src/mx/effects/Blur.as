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

import mx.effects.effectClasses.BlurInstance;

/**
 *  The Blur effect lets you apply a blur visual effect to a component. 
 *  A Blur effect softens the details of an image. 
 *  You can produce blurs that range from a softly unfocused look to a Gaussian
 *  blur, a hazy appearance like viewing an image through semi-opaque glass. 
 *
 *  <p>The Blur effect uses the Flash BlurFilter class
 *  as part of its implementation. 
 *  For more information, see flash.filters.BlurFilter.</p>
 *  
 *  <p>If you apply a Blur effect to a component, you cannot apply a BlurFilter 
 *  or a second Blur effect to the component. </p> 
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Blur&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Blur
 *    id="ID"
 *    blurXFrom="val"
 *    blurXTo="val"
 *    blurYFrom="val"
 *    blurYTo="val"
 *  /&gt;
 *  </pre>
 *  
 *  @see flash.filters.BlurFilter
 *  @see mx.effects.effectClasses.BlurInstance
 *
 *  @includeExample examples/BlurEffectExample.mxml
 */
public class Blur extends TweenEffect
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
	private static var AFFECTED_PROPERTIES:Array = [ "filters" ];

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
	public function Blur(target:Object = null)
	{
		super(target);

		instanceClass = BlurInstance;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  blurXFrom
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]
	
	/** 
	 *  The starting amount of horizontal blur.
	 *  Valid values are from 0.0 to 255.0. 
	 */
	public var blurXFrom:Number;
	
	//----------------------------------
	//  blurXTo
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]
	
	/** 
	 *  The ending amount of horizontal blur.
	 *  Valid values are from 0.0 to 255.0. 
	 */
	public var blurXTo:Number;
	
	//----------------------------------
	//  blurYFrom
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]
	
	/** 
	 *  The starting amount of vertical blur.
	 *  Valid values are from 0.0 to 255.0. 
	 */
	public var blurYFrom:Number;
	
	//----------------------------------
	//  blurYTo
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]
	
	/** 
	 *  The ending amount of vertical blur.
	 *  Valid values are from 0.0 to 255.0. 
	 */
	public var blurYTo:Number;
	
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
	override protected function initInstance(instance:IEffectInstance):void
	{
		super.initInstance(instance);
		
		var blurInstance:BlurInstance = BlurInstance(instance);

		blurInstance.blurXFrom = blurXFrom;
		blurInstance.blurXTo = blurXTo;
		blurInstance.blurYFrom = blurYFrom;
		blurInstance.blurYTo = blurYTo;
	}
}

}
