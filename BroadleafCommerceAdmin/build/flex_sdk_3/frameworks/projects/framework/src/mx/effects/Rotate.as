////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.effects
{

import mx.effects.effectClasses.RotateInstance;

/**
 *  The Rotate effect rotates a component around a specified point. 
 *  You can specify the coordinates of the center of the rotation, 
 *  and the starting and ending angles of rotation. 
 *  You can specify positive or negative values for the angles. 
 *
 *  <p><b>Note:</b> To use the Rotate effect with text,
 *  you must use an embedded font, not a device font.</p> 
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Rotate&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Rotate
 *    id="ID"
 *    angleFrom="0"
 *    angleTo="360"
 *    originX="0"
 *    originY="0"
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.effects.effectClasses.RotateInstance
 *
 *  @includeExample examples/RotateEffectExample.mxml
 */
public class Rotate extends TweenEffect
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
	private static var AFFECTED_PROPERTIES:Array = [ "rotation" ];

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
	public function Rotate(target:Object = null)
	{
		super(target);
		
		instanceClass = RotateInstance;
		hideFocusRing = true;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  angleFrom
	//----------------------------------

	[Inspectable(category="General", defaultValue="0")]

	/** 
	 *  The starting angle of rotation of the target object,
	 *  expressed in degrees.
	 *  Valid values range from 0 to 360.
	 *
	 *  @default 0
	 */
	public var angleFrom:Number = 0;

	//----------------------------------
	//  angleTo
	//----------------------------------

	[Inspectable(category="General", defaultValue="360")]

	/** 
	 *  The ending angle of rotation of the target object,
	 *  expressed in degrees.
	 *  Values can be either positive or negative.
	 *
	 *  <p>If the value of <code>angleTo</code> is less
	 *  than the value of <code>angleFrom</code>,
	 *  the target rotates in a counterclockwise direction.
	 *  Otherwise, it rotates in clockwise direction.
	 *  If you want the target to rotate multiple times,
	 *  set this value to a large positive or small negative number.</p>
	 *  
	 *  @default 360
	 */
	public var angleTo:Number = 360;
	
	//----------------------------------
	//  originX
	//----------------------------------

	[Inspectable(category="General", defaultValue="0")]

	/**
	 *  The x-position of the center point of rotation.
	 *  The target rotates around this point.
	 *  The valid values are between 0 and the width of the target.
	 *
	 *  @default 0
	 */
	public var originX:Number;

	//----------------------------------
	//  originY
	//----------------------------------

	[Inspectable(category="General", defaultValue="0")]

	/**
	 *  The y-position of the center point of rotation.
	 *  The target rotates around this point.
	 *  The valid values are between 0 and the height of the target.
	 *
	 *  @default 0
	 */
	public var originY:Number;

	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Determines whether the effect should hide the focus ring when starting the
	 *  effect. The target itself is responsible for the actual hiding of the focus ring. 
	 *  @default true
	 */
	override public function set hideFocusRing(value:Boolean):void
	{
		super.hideFocusRing = value;
	}
	
	override public function get hideFocusRing():Boolean
	{
		return super.hideFocusRing;
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
	override protected function initInstance(instance:IEffectInstance):void
	{
		super.initInstance(instance);
		
		var rotateInstance:RotateInstance = RotateInstance(instance);

		rotateInstance.angleFrom = angleFrom;
		rotateInstance.angleTo = angleTo;
		rotateInstance.originX = originX;
		rotateInstance.originY = originY;
	}
}
	
}
