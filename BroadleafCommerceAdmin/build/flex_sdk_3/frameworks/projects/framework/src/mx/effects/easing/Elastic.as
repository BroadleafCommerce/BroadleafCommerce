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
 *  The Elastc class defines three easing functions to implement 
 *  motion with Flex effect classes, where the motion is defined by 
 *  an exponentially decaying sine wave. 
 *
 *  For more information, see http://www.robertpenner.com/profmx.
 */  
public class Elastic
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
     *  @param a Specifies the amplitude of the sine wave.
     *
     *  @param p Specifies the period of the sine wave.
     *
     *  @return Number corresponding to the position of the component.
     */  
	public static function easeIn(t:Number, b:Number,
								  c:Number, d:Number,
								  a:Number = 0, p:Number = 0):Number
	{
		if (t == 0)
			return b;
		
		if ((t /= d) == 1)
			return b + c;
		
		if (!p)
			p = d * 0.3;
		
		var s:Number;
		if (!a || a < Math.abs(c))
		{
			a = c;
			s = p / 4;
		}
		else
		{
			s = p / (2 * Math.PI) * Math.asin(c / a);
		}

		return -(a * Math.pow(2, 10 * (t -= 1)) *
				 Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
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
     *  @param a Specifies the amplitude of the sine wave.
     *
     *  @param p Specifies the period of the sine wave.
     *
     *  @return Number corresponding to the position of the component.
     */  
	public static function easeOut(t:Number, b:Number,
								   c:Number, d:Number,
								   a:Number = 0, p:Number = 0):Number
	{
		if (t == 0)
			return b;
			
		if ((t /= d) == 1)
			return b + c;
		
		if (!p)
			p = d * 0.3;

		var s:Number;
		if (!a || a < Math.abs(c))
		{
			a = c;
			s = p / 4;
		}
		else
		{
			s = p / (2 * Math.PI) * Math.asin(c / a);
		}

		return a * Math.pow(2, -10 * t) *
			   Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b;
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
     *  @param a Specifies the amplitude of the sine wave.
     *
     *  @param p Specifies the period of the sine wave.
     *
     *  @return Number corresponding to the position of the component.
     */  
	public static function easeInOut(t:Number, b:Number,
									 c:Number, d:Number,
									 a:Number = 0, p:Number = 0):Number
	{
		if (t == 0)
			return b;
			
		if ((t /= d / 2) == 2)
			return b + c;
			
		if (!p)
			p = d * (0.3 * 1.5);

		var s:Number;
		if (!a || a < Math.abs(c))
		{
			a = c;
			s = p / 4;
		}
		else
		{
			s = p / (2 * Math.PI) * Math.asin(c / a);
		}

		if (t < 1)
		{
			return -0.5 * (a * Math.pow(2, 10 * (t -= 1)) *
				   Math.sin((t * d - s) * (2 * Math.PI) /p)) + b;
		}
		
		return a * Math.pow(2, -10 * (t -= 1)) *
			   Math.sin((t * d - s) * (2 * Math.PI) / p ) * 0.5 + c + b;
	}
}

}
