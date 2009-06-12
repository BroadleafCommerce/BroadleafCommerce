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
 *  The Exponential class defines three easing functions to implement 
 *  motion with Flex effect classes, where the motion is defined by 
 *  an exponentially decaying sine wave.  
 *
 *  For more information, see http://www.robertpenner.com/profmx.
 */  
public class Exponential
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

    /**
     *  The <code>easeIn()</code> method starts motion slowly, 
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
		return t == 0 ? b : c * Math.pow(2, 10 * (t / d - 1)) + b;
	}

    /**
     *  The <code>easeOut()</code> method starts motion fast, 
     *  and then decelerates motion as it executes. 
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
		return t == d ? b + c : c * (-Math.pow(2, -10 * t / d) + 1) + b;
	}

    /**
     *  The <code>easeInOut()</code> method combines the motion 
     *  of the <code>easeIn()</code> and <code>easeOut()</code> methods
	 *  to start the motion slowly, accelerate motion, then decelerate. 
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
		if (t == 0)
			return b;

		if (t == d)
			return b + c;

		if ((t /= d / 2) < 1)
			return c / 2 * Math.pow(2, 10 * (t - 1)) + b;

		return c / 2 * (-Math.pow(2, -10 * --t) + 2) + b;
	}
}

}
