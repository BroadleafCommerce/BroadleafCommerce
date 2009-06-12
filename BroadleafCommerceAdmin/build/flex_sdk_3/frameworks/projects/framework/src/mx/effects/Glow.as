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

import mx.effects.effectClasses.GlowInstance;
import mx.styles.StyleManager;

/**
 *  The Glow effect lets you apply a visual glow effect to a component. 
 *
 *  <p>The Glow effect uses the Flash GlowFilter class
 *  as part of its implementation. 
 *  For more information, see the flash.filters.GlowFilter class.
 *  If you apply a Glow effect to a component, you cannot apply a GlowFilter
 *  or a second Glow effect to the component.</p>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Glow&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Glow
 *    id="ID"
 *    alphaFrom="val"
 *    alphaTo="val"
 *    blurXFrom="val"
 *    blurXTo="val"
 *    blurYFrom="val"
 *    blurYTo="val"
 *    color="<i>themeColor of the application</i>"
 *    inner="false|true"
 *    knockout="false|true"
 *    strength="2"
 *  /&gt;
 *  </pre>
 *  
 *  @see flash.filters.GlowFilter
 *  @see mx.effects.effectClasses.GlowInstance
 *
 *  @includeExample examples/GlowEffectExample.mxml
 */
public class Glow extends TweenEffect
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
	public function Glow(target:Object = null)
	{
		super(target);

		instanceClass = GlowInstance;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  alphaFrom
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]
	
	/** 
	 *  Starting transparency level between 0.0 and 1.0,
	 *  where 0.0 means transparent and 1.0 means fully opaque.
	 */
	public var alphaFrom:Number;
	
	//----------------------------------
	//  alphaTo
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]
	
	/** 
	 *  Ending transparency level between 0.0 and 1.0,
	 *  where 0.0 means transparent and 1.0 means fully opaque.
	 */
	public var alphaTo:Number;
	
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
	
	//----------------------------------
	//  color
	//----------------------------------

	[Inspectable(category="General", format="Color", defaultValue="0xFFFFFFFF")]
	
	/** 
	 *  The color of the glow. 
	 *  The default value is the value of the <code>themeColor</code> style 
	 *  property of the application.
	 */
	public var color:uint = StyleManager.NOT_A_COLOR;
	
	//----------------------------------
	//  inner
	//----------------------------------

	[Inspectable(category="General", defaultValue="false")]
	
	/** 
	 *  Specifies whether the glow is an inner glow. 
	 *  A value of <code>true</code> indicates an inner glow within
	 *  the outer edges of the object. 
	 *  The default value is <code>false</code>, to specify 
	 *  an outer glow around the outer edges of the object. 
	 *
	 *  @default false
	 */
	public var inner:Boolean;
	
	//----------------------------------
	//  knockout
	//----------------------------------

	[Inspectable(defaultValue="false")]
	
	/** 
	 *  Specifies whether the object has a knockout effect. 
	 *  A value of <code>true</code> makes the object's fill color transparent 
	 *  to reveal the background color of the underlying object. 
	 *  The default value is <code>false</code> to specify no knockout effect. 
	 *
	 *  @default false
	 */
	public var knockout:Boolean;
	
	//----------------------------------
	//  strength
	//----------------------------------

	[Inspectable(category="General", defaultValue="2")]
	
	/** 
	 *  The strength of the imprint or spread. 
	 *  The higher the value, the more color is imprinted and the stronger the 
	 *  contrast between the glow and the background. 
	 *  Valid values are from <code>0</code> to <code>255</code>. 
	 *
	 *  @default 2 
	 */
	public var strength:Number;
	
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
		
		var glowInstance:GlowInstance = GlowInstance(instance);

		glowInstance.alphaFrom = alphaFrom;
		glowInstance.alphaTo = alphaTo;
		glowInstance.blurXFrom = blurXFrom;
		glowInstance.blurXTo = blurXTo;
		glowInstance.blurYFrom = blurYFrom;
		glowInstance.blurYTo = blurYTo;
		glowInstance.color = color;
		glowInstance.inner = inner;
		glowInstance.knockout = knockout;
		glowInstance.strength = strength;
	}
}

}
