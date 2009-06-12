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

import mx.effects.effectClasses.ResizeInstance;

/**
 *  The Resize effect changes the width, height, or both dimensions
 *  of a component over a specified time interval. 
 *  
 *  <p>If you specify only two of the three values of the
 *  <code>widthFrom</code>, <code>widthTo</code>, and
 *  <code>widthBy</code> properties, Flex calculates the third.
 *  If you specify all three, Flex ignores the <code>widthBy</code> value.
 *  If you specify only the <code>widthBy</code> or the
 *  <code>widthTo</code> value, the <code>widthFrom</code> property
 *  is set to be the object's current width.
 *  The same is true for <code>heightFrom</code>, <code>heightTo</code>,
 *  and <code>heightBy</code> property values.</p>
 *  
 *  <p>If you specify a Resize effect for a resize trigger,
 *  and if you do not set the six From, To, and By properties,
 *  Flex sets them to create a smooth transition
 *  between the object's old size and its new size.</p>
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Resize&gt;</code> tag
 *  inherits all of the tag attributes of its superclass, 
 *  and adds the following tab attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Resize
 *    id="ID"
 *    widthFrom="val"
 *    heightFrom="val"
 *    widthTo="val"
 *    heightTo="val"
 *    widthBy="val"
 *    heightBy="val"
 *    hideChildrenTargets=""
 *  /&gt;
 *  </pre>
 *  
 *  @includeExample examples/ResizeEffectExample.mxml
 *
 *  @see mx.effects.effectClasses.ResizeInstance
 *  @see mx.effects.Tween
 */
public class Resize extends TweenEffect
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
	private static var AFFECTED_PROPERTIES:Array =
	[
		"width", "height",
		"explicitWidth", "explicitHeight",
		"percentWidth", "percentHeight"
	];

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
	public function Resize(target:Object = null)
	{
		super(target);

		instanceClass = ResizeInstance;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
 
 	//----------------------------------
	//  heightBy
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]

	/** 
	 *  Number of pixels by which to modify the height of the component.
	 *  Values may be negative.
	 */
	public var heightBy:Number;
	
 	//----------------------------------
	//  heightFrom
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]

	/** 
	 *  Initial height, in pixels.
	 *  If omitted, Flex uses the current height.
	 */
	public var heightFrom:Number;

 	//----------------------------------
	//  heightTo
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]

	/** 
	 *  Final height, in pixels.
	 */
	public var heightTo:Number;
	
 	//----------------------------------
	//  hideChildrenTargets
	//----------------------------------

	/**
	 *  An Array of Panel containers.
	 *  The children of these Panel containers are hidden while the Resize
	 *  effect plays.
	 *
	 *  <p>You use data binding syntax to set this property in MXML, 
	 *  as the following example shows, where panelOne and panelTwo 
	 *  are the names of two Panel containers in your application:</p>
	 *
	 *  <pre>&lt;mx:Resize id="e" heightFrom="100" heightTo="400"
	 *	hideChildrenTargets="{[panelOne, panelTwo]}" /&gt;</pre>		
	 */
	public var hideChildrenTargets:Array /* of Panel */;
		
 	//----------------------------------
	//  widthBy
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]

	/** 
	 *  Number of pixels by which to modify the width of the component.
	 *  Values may be negative.
	 */
	public var widthBy:Number;

 	//----------------------------------
	//  widthFrom
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]

	/** 
	 *  Initial width, in pixels.
	 *  If omitted, Flex uses the current width.
	 */
	public var widthFrom:Number;
	
 	//----------------------------------
	//  widthTo
	//----------------------------------

	[Inspectable(category="General", defaultValue="NaN")]

	/** 
	 *  Final width, in pixels.
	 */
	public var widthTo:Number;
	
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
		
		var resizeInstance:ResizeInstance = ResizeInstance(instance);

		if (!isNaN(widthFrom))
			resizeInstance.widthFrom = widthFrom;
		if (!isNaN(widthTo))
			resizeInstance.widthTo = widthTo;
		if (!isNaN(widthBy))
			resizeInstance.widthBy = widthBy;
		if (!isNaN(heightFrom))
			resizeInstance.heightFrom = heightFrom;
		if (!isNaN(heightTo))
			resizeInstance.heightTo = heightTo;
		if (!isNaN(heightBy))
			resizeInstance.heightBy = heightBy;
		resizeInstance.hideChildrenTargets = hideChildrenTargets;
	}
}

}
