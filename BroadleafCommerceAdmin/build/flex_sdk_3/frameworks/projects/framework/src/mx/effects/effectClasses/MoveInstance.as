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

import flash.events.Event;
import mx.core.Container;
import mx.core.EdgeMetrics;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.effects.EffectManager;
import mx.events.MoveEvent;
import mx.styles.IStyleClient;

/**
 *  The MoveInstance class implements the instance class
 *  for the Move effect.
 *  Flex creates an instance of this class when it plays a Move effect;
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
 *  The TweenEvent class  defines the property <code>value</code>, which contains 
 *  the tween value calculated by the effect. 
 *  For the Move effect, 
 *  the <code>TweenEvent.value</code> property contains a 2-item Array, where: </p>
 *  <ul>
 *    <li>value[0]:Number  A value between the values of the <code>Move.xFrom</code> 
 *    and <code>Move.xTo</code> property.</li>
 *  
 *    <li>value[1]:Number  A value between the values of the <code>Move.yFrom</code> 
 *    and <code>Move.yTo</code> property.</li>
 *  </ul>
 *
 *  @see mx.effects.Move
 *  @see mx.events.TweenEvent
 */  
public class MoveInstance extends TweenEffectInstance
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
	public function MoveInstance(target:Object)
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
	 *  Stores the left style of the target
	 */
	private var left:*;
	
	/**
	 *  @private 
	 *  Stores the right style of the target
	 */
	private var right:*;
	
	/**
	 *  @private 
	 *  Stores the top style of the target
	 */
	private var top:*;
	
	/**
	 *  @private 
	 *  Stores the bottom style of the target
	 */
	private var bottom:*;
	
	/**
	 *  @private 
	 *  Stores the horizontalCenter style of the target
	 */
	private var horizontalCenter:*;
	
	/**
	 *  @private 
	 *  Stores the verticalCenter style of the target
	 */
	private var verticalCenter:*;
	
	/**
	 *  @private 
	 */
	private var forceClipping:Boolean = false;
	
	/**
	 *  @private 
	 */
	private var checkClipping:Boolean = true;
	
	private var oldWidth:Number;
	private var oldHeight:Number;
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  xBy
	//----------------------------------

	/** 
	 *  Number of pixels to move the components along the x axis.
	 *  Values can be negative. 
	 */
	public var xBy:Number;
	
	//----------------------------------
	//  xFrom
	//----------------------------------

	/** 
	 *  Initial position's x coordinate.
	 */
	public var xFrom:Number;
	
	//----------------------------------
	//  xTo
	//----------------------------------

	/** 
	 *  Destination position's x coordinate.
	 */
	public var xTo:Number;
	
	//----------------------------------
	//  yBy
	//----------------------------------

	/** 
	 *  Number of pixels to move the components along the y axis.
	 *  Values can be negative. 	
	 */
	public var yBy:Number;

	//----------------------------------
	//  yFrom
	//----------------------------------

	/**
	 *  Initial position's y coordinate.
	 */
	public var yFrom:Number;
	
	//----------------------------------
	//  yTo
	//----------------------------------

	/** 
	 *  Destination position's y coordinate.
	 */
	public var yTo:Number;
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function initEffect(event:Event):void
	{
		super.initEffect(event);

		if (event is MoveEvent && event.type == MoveEvent.MOVE)
		{
			
			if (isNaN(xFrom) &&
				isNaN(xTo) &&
				isNaN(xBy) &&
				isNaN(yFrom) &&
				isNaN(yTo) &&
				isNaN(yBy))
			{
				xFrom = MoveEvent(event).oldX;
				xTo = target.x;
				yFrom = MoveEvent(event).oldY;
				yTo = target.y;
			}
		}
	}
	
	/**
	 *  @private
	 */
	override public function play():void
	{
		// Dispatch an effectStart event from the target.
		super.play();
		
		// Try to cache the target as a bitmap.
		EffectManager.mx_internal::startBitmapEffect(IUIComponent(target));

		// The user may have supplied some combination of xFrom, xTo, and xBy.
		// If either xFrom or xTo is not explicitly defined, calculate its
		// value based on the other two values.
		if (isNaN(xFrom))
			xFrom = (!isNaN(xTo) && !isNaN(xBy)) ? xTo - xBy : target.x;
		if (isNaN(xTo))
		{
			if (isNaN(xBy) &&
				propertyChanges &&
				propertyChanges.end["x"] !== undefined)
			{
				xTo = propertyChanges.end["x"];
			}
			else
			{
				xTo = (!isNaN(xBy)) ? xFrom + xBy : target.x;
			}
		}

		// Ditto for yFrom, yTo, and yBy.
		if (isNaN(yFrom))
			yFrom = (!isNaN(yTo) && !isNaN(yBy)) ? yTo - yBy : target.y;
		if (isNaN(yTo))
		{
			if (isNaN(yBy) &&
				propertyChanges &&
				propertyChanges.end["y"] !== undefined)
			{
				yTo = propertyChanges.end["y"];
			}
			else
			{
				yTo = (!isNaN(yBy)) ? yFrom + yBy : target.y;
			}
		}	

		// Create a tween to move the object
		tween = createTween(this, [ xFrom, yFrom ],
										 [ xTo, yTo ], duration);
		
		// Set back to initial position before the screen refreshes
		var p:Container = target.parent as Container;
		
		if (p)
		{
			var vm:EdgeMetrics = p.viewMetrics;
			var l:Number = vm.left;
			var r:Number = p.width - vm.right;
			var t:Number = vm.top;
			var b:Number = p.height - vm.bottom;
				
			if (xFrom < l || xTo < l ||
				xFrom + target.width > r || xTo + target.width > r ||
				yFrom < t || yTo < t ||
				yFrom + target.height > b || yTo + target.height > b)
			{
				forceClipping = true;
				p.mx_internal::forceClipping = true;
			}
		
		}
		
		mx_internal::applyTweenStartValues();
		
		if (target is IStyleClient)
		{
			left = target.getStyle("left");
			if (left != undefined)
				target.setStyle("left", undefined);
		
			right = target.getStyle("right");
			if (right != undefined)
				target.setStyle("right", undefined);
			
			top = target.getStyle("top");
			if (top != undefined)
				target.setStyle("top", undefined);
			
			bottom = target.getStyle("bottom");
			if (bottom != undefined)
				target.setStyle("bottom", undefined);	
			
			horizontalCenter = target.getStyle("horizontalCenter");
			if (horizontalCenter != undefined)
				target.setStyle("horizontalCenter", undefined);	
			
			verticalCenter = target.getStyle("verticalCenter");
			if (verticalCenter != undefined)
				target.setStyle("verticalCenter", undefined);		
			
			if (left != undefined && right != undefined)
			{
				var w:Number = target.width;	
				oldWidth = target.explicitWidth;
				target.width = w;
			}
		
			if (top != undefined && bottom != undefined)
			{
				var h:Number = target.height;
				oldHeight = target.explicitHeight;
				target.height = h;
			}
		}
	}

	/**
	 *  @private
	 */
	override public function onTweenUpdate(value:Object):void 
	{			
		// Tell the EffectManager not to listen to the "move" event.
		// Otherwise, moveEffect="Move" would cause a new Move effect
		// to be create with each onTweenUpdate.
		EffectManager.suspendEventHandling();
		
		if (!forceClipping && checkClipping)
		{
			var p:Container = target.parent as Container;
			
			if (p)
			{
				var vm:EdgeMetrics = p.viewMetrics;
				var l:Number = vm.left;
				var r:Number = p.width - vm.right;
				var t:Number = vm.top;
				var b:Number = p.height - vm.bottom;
			
				if (value[0] < l || value[0] + target.width > r ||
					value[1] < t || value[1] + target.height > b)
				{
					forceClipping = true;
					p.mx_internal::forceClipping = true;
				}
			}
		}
		
		target.move(value[0], value[1]);
		EffectManager.resumeEventHandling();
	}

	/**
	 *  @private
	 */
	override public function onTweenEnd(value:Object):void
	{	
		EffectManager.mx_internal::endBitmapEffect(IUIComponent(target));
		
		if (left != undefined)
			target.setStyle("left", left);
		if (right != undefined)
			target.setStyle("right", right);
		if (top != undefined)
			target.setStyle("top", top);
		if (bottom != undefined)
			target.setStyle("bottom", bottom);			
		if (horizontalCenter != undefined)
			target.setStyle("horizontalCenter", horizontalCenter);
		if (verticalCenter != undefined)
			target.setStyle("verticalCenter", verticalCenter);	
		
		if (left != undefined && right != undefined)
		{
			target.explicitWidth = oldWidth;
		}
		
		if (top != undefined && bottom != undefined)
		{
			target.explicitHeight = oldHeight;
		}
		
		if (forceClipping)
		{
			var p:Container = target.parent as Container;
			
			if (p) 
			{
				forceClipping = false;
				p.mx_internal::forceClipping = false;
			}
		}	
		
		checkClipping = false;
		super.onTweenEnd(value);
	}
}

}
