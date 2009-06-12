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

package mx.effects.easing
{

/**
 *  The Quadratic class defines three easing functions to implement 
 *  quadratic motion with Flex effect classes. The acceleration of motion 
 *  for a Quadratic easing equation is slower than for a Cubic or Quartic easing equation.
 *
 *  For more information, see http://www.robertpenner.com/profmx.
 */  
public class Quadratic
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

    /**
     *  The <code>easeIn()</code> method starts motion from a zero velocity, 
     *  and then accelerates motion as it executes. 
     *
     *  @param t Specifies time.
	 *
     *  @param b Specifies the initial position of a component.
	 *
     *  @param c Specifies the total change in position of the component.
	 *
     *  @param d Specifies the duration of the effect, in milliseconds.
     *
     *  @return Number corresponding to the position of the component.
     */  
	public static function easeIn(t:Number, b:Number,
								  c:Number, d:Number):Number
	{
		return c * (t /= d) * t + b;
	}

    /**
     *  The <code>easeOut()</code> method starts motion fast, 
     *  and then decelerates motion to a zero velocity as it executes. 
     *
     *  @param t Specifies time.
	 *
     *  @param b Specifies the initial position of a component.
	 *
     *  @param c Specifies the total change in position of the component.
	 *
     *  @param d Specifies the duration of the effect, in milliseconds.
     *
     *  @return Number corresponding to the position of the component.
     */  
	public static function easeOut(t:Number, b:Number,
								   c:Number, d:Number):Number
	{
		return -c * (t /= d) * (t - 2) + b;
	}

    /**
     *  The <code>easeInOut()</code> method combines the motion
     *  of the <code>easeIn()</code> and <code>easeOut()</code> methods
	 *  to start the motion from a zero velocity, 
     *  accelerate motion, then decelerate to a zero velocity. 
     *
     *  @param t Specifies time.
	 *
     *  @param b Specifies the initial position of a component.
	 *
     *  @param c Specifies the total change in position of the component.
	 *
     *  @param d Specifies the duration of the effect, in milliseconds.
     *
     *  @return Number corresponding to the position of the component.
     */  
	public static function easeInOut(t:Number, b:Number,
									 c:Number, d:Number):Number
	{
		if ((t /= d / 2) < 1)
			return c / 2 * t * t + b;

		return -c / 2 * ((--t) * (t - 2) - 1) + b;
	}
}

}
