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

package mx.effects.effectClasses
{

import mx.core.mx_internal;
import mx.effects.EffectManager;

//  Let (phi) be angle between r=(Ox,Oy - Cx,Cy) and -X Axis.
//   (theta) be clockwise further angle of rotation.
//  
//  Xtheta = Cx - rCos(theta + phi);
//  Ytheta = Cy - rSin(theta + phi);
//  
//  Xtheta = Cx - rCos(theta)Cos(phi) + rSin(theta)Sin(phi);
//  Ytheta = Cy - rSin(theta)Cos(phi) - rCos(theta)Sin(phi);
//  
//  Now Cos(phi) = w/2r; Sin(phi) = h/2r;
//  
//  Xtheta = Cx - rCos(theta)Cos(phi) + rSin(theta)Sin(phi);
//  Ytheta = Cy - rSin(theta)Cos(phi) - rCos(theta)Sin(phi);
//  
//  Xtheta = Cx - rCos(theta)w/2r + rSin(theta)h/2r;
//  Ytheta = Cy - rSin(theta)w/2r - rCos(theta)h/2r;
//  
//  Xtheta = Cx - wCos(theta)/2 + hSin(theta)/2;
//  Ytheta = Cy - wSin(theta)/2 - hCos(theta)/2;
//

/**
 *  The RotateInstance class implements the instance class
 *  for the Rotate effect.
 *  Flex creates an instance of this class when it plays a Rotate effect;
 *  you do not create one yourself.
 *  
 *  <p>Every effect class that is a subclass of the TweenEffect class 
 *  supports the following events:</p>
 *  
 *  <ul>
 *    <li><code>tweenEnd</code>: Dispatched when the tween effect ends. </li>
 *  
 *    <li><code>tweenUpdate</code>: Dispatched every time a TweenEffect 
 *      class calculates a new value.</li> 
 *  </ul>
 *  
 *  <p>The event object passed to the event listener for these events is of type TweenEvent. 
 *  The TweenEvent class defines the property <code>value</code>, which contains 
 *  the tween value calculated by the effect. 
 *  For the Rotate effect, 
 *  the <code>TweenEvent.value</code> property contains a Number between the values of 
 *  the <code>Rotate.angleFrom</code> and 
 *  <code>Rotate.angleTo</code> properties.</p>
 *
 *  @see mx.effects.Rotate
 *  @see mx.events.TweenEvent
 */  
public class RotateInstance extends TweenEffectInstance
{
    include "../../core/Version.as";

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
	public function RotateInstance(target:Object)
	{
		super(target);
	}
  
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  The x coordinate of the absolute point of rotation.
	 */
	private var centerX:Number;
	
	/**
	 *  @private
	 *  The y coordinate of absolute point of rotation.
	 */
	private var centerY:Number;

	/**
	 *  @private
	 */
	private var newX:Number;
	
	/**
	 *  @private
	 */
	private var newY:Number;
	
	/**
	 *  @private
	 */
	private var originalOffsetX:Number;
	
	/**
	 *  @private
	 */
	private var originalOffsetY:Number;
	
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
	 *  then the target rotates in a counterclockwise direction.
	 *  Otherwise, it rotates in clockwise direction.
	 *  If you want the target to rotate multiple times,
	 *  set this value to a large positive or small negative number.</p>
	 *  
	 *  @default 360
	 */
	public var angleTo:Number = 360;
 
	//----------------------------------
	//  originY
	//----------------------------------

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
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function play():void
	{
		super.play();
		
		var radVal:Number = Math.PI * target.rotation / 180;		
		
		// Default to the center
		if (isNaN(originX))
			originX = target.width / 2;
		
		if (isNaN(originY))
			originY = target.height / 2;

		// Find the about point
		centerX = target.x +
				  originX * Math.cos(radVal) -
				  originY * Math.sin(radVal);
		centerY = target.y +
				  originX * Math.sin(radVal) +
				  originY * Math.cos(radVal);
			
		if (isNaN(angleFrom))
			angleFrom = target.rotation;
		
		if (isNaN(angleTo))
		{
			angleTo = (target.rotation == 0) ?
					  ((angleFrom > 180) ? 360 : 0) :
					  target.rotation;
		}
		
		tween = createTween(this, angleFrom, angleTo, duration);

		target.rotation = angleFrom;
		
		radVal = Math.PI * angleFrom/180;

		EffectManager.suspendEventHandling();
		
		originalOffsetX = originX * Math.cos(radVal) - originY * Math.sin(radVal);
		originalOffsetY = originX * Math.sin(radVal) + originY * Math.cos(radVal);
		
		newX = Number((centerX - originalOffsetX).toFixed(1)); // use a precision of 1
		newY = Number((centerY - originalOffsetY).toFixed(1)); // use a precision of 1
		
		target.move(newX, newY);
							
		EffectManager.resumeEventHandling();			
	}
  
	/**
	 *  @private
	 */
	override public function onTweenUpdate(value:Object):void
	{
		// If somebody else has changed our position
		if (Math.abs(newX - target.x) > 0.1)
		{
			centerX = target.x + originalOffsetX;
		}
		
		if (Math.abs(newY - target.y) > 0.1)
		{
			centerY = target.y + originalOffsetY;
		}
		
		var rotateValue:Number = Number(value);		
		var radVal:Number = Math.PI * rotateValue / 180;
		
		EffectManager.suspendEventHandling();
		
		target.rotation = rotateValue;
		
		newX = centerX - originX * Math.cos(radVal) + originY * Math.sin(radVal);
		newY = centerY - originX * Math.sin(radVal) - originY * Math.cos(radVal);
		
		newX = Number(newX.toFixed(1)); // use a precision of 1
		newY = Number(newY.toFixed(1)); // use a precision of 1
		
		target.move(newX, newY);  
				   
		EffectManager.resumeEventHandling();		   
	}
}

}
