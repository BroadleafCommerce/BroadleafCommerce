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
 *  The Back class defines three easing functions to implement 
 *  motion with Flex effect classes. 
 *
 *  For more information, see http://www.robertpenner.com/profmx.
 */  
public class Back
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------
	
    /**
     *  The <code>easeIn()</code> method starts 
     *  the motion by backtracking, 
     *  then reversing direction and moving toward the target.
	 *
     *  @param t Specifies time.
	 *
     *  @param b Specifies the initial position of a component.
	 *
     *  @param c Specifies the total change in position of the component.
	 *
     *  @param d Specifies the duration of the effect, in milliseconds.
	 *
	 *  @param s Specifies the amount of overshoot, where the higher the value, 
	 *  the greater the overshoot.
     *
     *  @return Number corresponding to the position of the component.
     */  
	public static function easeIn(t:Number, b:Number, c:Number,
								  d:Number, s:Number = 0):Number
	{
		if (!s)
			s = 1.70158;
		
		return c * (t /= d) * t * ((s + 1) * t - s) + b;
	}

    /**
     *  The <code>easeOut()</code> method starts the motion by
     *  moving towards the target, overshooting it slightly, 
     *  and then reversing direction back toward the target.
	 *
     *  @param t Specifies time.
	 *
     *  @param b Specifies the initial position of a component.
	 *
     *  @param c Specifies the total change in position of the component.
	 *
     *  @param d Specifies the duration of the effect, in milliseconds.
	 *
	 *  @param s Specifies the amount of overshoot, where the higher the value, 
	 *  the greater the overshoot.
     *
     *  @return Number corresponding to the position of the component.
     */  
	public static function easeOut(t:Number, b:Number, c:Number,
								   d:Number, s:Number = 0):Number
	{
		if (!s)
			s = 1.70158;
		
		return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
	}

    /**
     *  The <code>easeInOut()</code> method combines the motion
	 *  of the <code>easeIn()</code> and <code>easeOut()</code> methods
	 *  to start the motion by backtracking, then reversing direction and 
	 *  moving toward target, overshooting target slightly, reversing direction again, and 
	 *  then moving back toward the target.
	 *
     *  @param t Specifies time.
	 *
     *  @param b Specifies the initial position of a component.
	 *
     *  @param c Specifies the total change in position of the component.
	 *
     *  @param d Specifies the duration of the effect, in milliseconds.
	 *
	 *  @param s Specifies the amount of overshoot, where the higher the value, 
	 *  the greater the overshoot.
     *
     *  @return Number corresponding to the position of the component.
     */  
	public static function easeInOut(t:Number, b:Number, c:Number,
									 d:Number, s:Number = 0):Number
	{
		if (!s)
			s = 1.70158; 
		
		if ((t /= d / 2) < 1)
			return c / 2 * (t * t * (((s *= (1.525)) + 1) * t - s)) + b;
		
		return c / 2 * ((t -= 2) * t * (((s *= (1.525)) + 1) * t + s) + 2) + b;
	}
}

}
