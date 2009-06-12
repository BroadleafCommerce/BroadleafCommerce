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
import mx.containers.Panel;
import mx.core.Application;
import mx.core.Container;
import mx.core.IUIComponent;
import mx.core.ScrollPolicy;
import mx.core.mx_internal;
import mx.effects.EffectManager;
import mx.events.EffectEvent;
import mx.events.ResizeEvent;
import mx.styles.IStyleClient;

use namespace mx_internal;

/**
 *  The ResizeInstance class implements the instance class
 *  for the Resize effect.
 *  Flex creates an instance of this class when it plays a Resize effect;
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
 *  For the Resize effect, 
 *  the <code>TweenEvent.value</code> property contains a 2-item Array, where: </p>
 *  <ul>
 *    <li>value[0]:Number  A value between the values of the <code>Resize.widthFrom</code> 
 *    and <code>Resize.widthTo</code> property.</li>
 *  
 *    <li>value[1]:Number  A value between the values of the <code>Resize.heightFrom</code> 
 *    and <code>Resize.heightTo</code> property.</li>
 *  </ul>
 *
 *  @see mx.effects.Resize
 *  @see mx.events.TweenEvent
 */  
public class ResizeInstance extends TweenEffectInstance
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
	public function ResizeInstance(target:Object)
	{
		super(target);
		
		needToLayout = true;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var restoreVisibleArray:Array;
	
	/**
	 *  @private
	 */
	private var restoreAutoLayoutArray:Array;
	
	/**
	 *  @private
	 */
	private var numHideEffectsPlaying:Number = 0;
	
	/**
	 *  @private
	 */
	private var origPercentHeight:Number;
	
	/**
	 *  @private
	 */
	private var origPercentWidth:Number;
	
	/**
	 *  @private
	 */
	private var origExplicitHeight:Number;
	
	/**
	 *  @private
	 */
	private var origExplicitWidth:Number;
	
	/**
	 *  @private
	 */
	private var heightSet:Boolean;
	
	/**
	 *  @private
	 */
	private var widthSet:Boolean;
	
	/**
	 *  @private
	 */
	private var explicitWidthSet:Boolean;
	
	/**
	 *  @private
	 */
	private var explicitHeightSet:Boolean;
	
	/**
	 *  @private
	 */
	private var origVerticalScrollPolicy:String = "";
	
	/**
	 *  @private
	 */
	private var origHorizontalScrollPolicy:String = "";
	
	/**
	 *  @private
	 */
	private var parentOrigVerticalScrollPolicy:String = "";
	
	/**
	 *  @private
	 */
	private var parentOrigHorizontalScrollPolicy:String = "";
	
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
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  heightBy
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the heightBy property.
	 */
	private var _heightBy:Number;
	
	/** 
	 *  Number of pixels by which to modify the height of the component.
	 *  Values may be negative.
	 */
	public function get heightBy():Number
	{
		return _heightBy;
	}	
	
	/**
	 *  @private
	 */
	public function set heightBy(value:Number):void
	{
		_heightBy = value;
		heightSet = !isNaN(value);
	}
	
	//----------------------------------
	//  heightFrom
	//----------------------------------

	/** 
	 *  Initial height. If omitted, Flex uses the current size.
	 */
	public var heightFrom:Number;

	//----------------------------------
	//  heightTo
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the heightTo property.
	 */
	private var _heightTo:Number;
	
	/** 
	 *  Final height, in pixels.
	 */
	public function get heightTo():Number
	{
		return _heightTo;
	}	
	
	/**
	 *  @private
	 */
	public function set heightTo(value:Number):void
	{
		_heightTo = value;
		heightSet = !isNaN(value);
	}

	//----------------------------------
	//  hideChildrenTargets
	//----------------------------------

	/**
	 *  An Array of Panels.
	 *  The children of these Panels are hidden while the Resize effect plays.
	 */
	public var hideChildrenTargets:Array /* of Panel */;
	
	//----------------------------------
	//  widthBy
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the widthBy property.
	 */
	private var _widthBy:Number;

	/** 
	 *  Number of pixels by which to modify the width of the component.
	 *  Values may be negative.
	 */	
	public function get widthBy():Number
	{
		return _widthBy;
	}	
	
	/**
	 *  @private
	 */
	public function set widthBy(value:Number):void
	{
		_widthBy = value;
		widthSet = !isNaN(value);
	}

	//----------------------------------
	//  widthFrom
	//----------------------------------

	/** 
	 *  Initial width. If omitted, Flex uses the current size.
	 */
	public var widthFrom:Number;

	//----------------------------------
	//  widthTo
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the widthTo property.
	 */
	private var _widthTo:Number;
	
	/** 
	 *  Final width, in pixels.
	 */
	public function get widthTo():Number
	{
		return _widthTo;
	}	
	
	/**
	 *  @private
	 */
	public function set widthTo(value:Number):void
	{
		_widthTo = value;
		widthSet = !isNaN(value);
	}
	
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

		if (event is ResizeEvent && event.type == ResizeEvent.RESIZE)
		{		
			if (isNaN(widthBy))
			{
				if (isNaN(widthFrom))
				{
					widthFrom = ResizeEvent(event).oldWidth;
				}
				if (isNaN(widthTo))
				{
					_widthTo = target.width;
				}
			}
			
			if (isNaN(heightBy))
			{
				if (isNaN(heightFrom))
				{
					heightFrom = ResizeEvent(event).oldHeight;
				}
				
				if (isNaN(heightTo))
				{
					_heightTo = target.height;
				}
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
		
		calculateExplicitDimensionChanges();
		
		// If the target is a Panel, then find all Panel objects that will
		// be affected by the animation.  Deliver a "resizeStart" event to 
		// each affected Panel, and then wait until the Panel finishes
		// hiding its children.
		var childrenHiding:Boolean = hidePanelChildren();
		
		if (target is IStyleClient)
		{
			left = target.getStyle("left");
			if (left != undefined)
				target.setStyle("left",undefined);
		
			right = target.getStyle("right");
			if (right != undefined)
				target.setStyle("right",undefined);
			
			top = target.getStyle("top");
			if (top != undefined)
				target.setStyle("top",undefined);
			
			bottom = target.getStyle("bottom");
			if (bottom != undefined)
				target.setStyle("bottom",undefined);	
		}
		
		if (!childrenHiding)
			startResizeTween();
	}
	
	/**
	 *  @private
     */
	override public function onTweenUpdate(value:Object):void 
	{
		EffectManager.suspendEventHandling();
		
		// Use Math.round instead of Math.floor, so that the animation appears
		// to begin playing after the value has changed by only half a pixel,
		// instead of waiting for it to change by a whole pixel.
		// Because of the way that the easing function mimics acceleration,
		// it can take a while for the animation to get started.
		target.width = Math.round(value[0]);
		target.height = Math.round(value[1]);
				
		// Set a flag indicating that LayoutManager.validateNow()
		// should be called after we're finished processing
		// all the effects for this frame.
		if (tween)
			tween.needToLayout = true;
		needToLayout = true;
		
		EffectManager.resumeEventHandling();		
	}

	/**
	 *  @private
     */
	override public function onTweenEnd(value:Object):void
	{
		EffectManager.endVectorEffect(IUIComponent(target));

		// Wait a frame before starting to restore the childrens' visibility.
		// That way, we have a chance to run a measurement/layout pass with
		// the final sizes and update the screen.
		Application.application.callLater(restorePanelChildren);
				
		super.onTweenEnd(value);		
		
		EffectManager.suspendEventHandling();
		
		var targetAsContainer:Container;
		var parentAsContainer:Container;

		// Restore the target's percent and explicit sizes
		if (!heightSet)
		{
			target.percentHeight = origPercentHeight;
			target.explicitHeight = origExplicitHeight;
			
			if (origVerticalScrollPolicy != "")
			{
				targetAsContainer = target as Container;
				if (targetAsContainer)
				{
					targetAsContainer.verticalScrollPolicy = origVerticalScrollPolicy;
					origVerticalScrollPolicy = "";
				}	
			}
			
			if (parentOrigVerticalScrollPolicy != "" && target.parent)
			{
				parentAsContainer = target.parent as Container;
				if (parentAsContainer)
				{
					parentAsContainer.verticalScrollPolicy = parentOrigVerticalScrollPolicy;
					parentOrigVerticalScrollPolicy = "";
				}
			}
		}
		
		if (!widthSet)
		{
			target.percentWidth = origPercentWidth;
			target.explicitWidth = origExplicitWidth;
						
			if (origHorizontalScrollPolicy != "")
			{
				targetAsContainer = target as Container;
				if (targetAsContainer)
				{
					targetAsContainer.horizontalScrollPolicy = origHorizontalScrollPolicy;
					origHorizontalScrollPolicy = "";
				}
			}
			
			if (parentOrigHorizontalScrollPolicy != "" && target.parent)
			{
				parentAsContainer = target.parent as Container;
				if (parentAsContainer)
				{
					parentAsContainer.horizontalScrollPolicy = parentOrigHorizontalScrollPolicy;
					parentOrigHorizontalScrollPolicy = "";
				}
			}
		}
		
		if (left != undefined)
			target.setStyle("left",left);
		if (right != undefined)
			target.setStyle("right",right);
		if (top != undefined)
			target.setStyle("top",top);
		if (bottom != undefined)
			target.setStyle("bottom",bottom);	
		
		EffectManager.resumeEventHandling();	
	}

	/**
	 *  @private
     */
	override public function end():void
	{
		// If we were waiting for the initial "hide children" effect to 
		// finish playing, then the tween might not be created yet.
		// In that case, we need to explicitly jump to the end of the Resize,
		// because the TweenEffect.end() function won't do it for us.
		
		if (!tween)
		{
			calculateExplicitDimensionChanges();
			
			onTweenEnd(playReversed ?
					   [ widthFrom, heightFrom ] :
					   [ widthTo, heightTo ]);
		}
		
			
		super.end();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function startResizeTween():void
	{
		EffectManager.startVectorEffect(IUIComponent(target));
				
		// Create a tween to resize the object
		tween = createTween(this, [ widthFrom, heightFrom ],
										 [ widthTo, heightTo ], duration);
			
		// Set back to initial size before the screen refreshes.
		//EffectManager.suspendEventHandling();
		applyTweenStartValues();
		/*if (needToLayout)
			UIComponent.layoutManager.validateNow();
		*/
		//EffectManager.resumeEventHandling();	
	}


	/**
	 *  @private
	 *  Hides children of Panels while the effect is playing.
	 */
	private function hidePanelChildren():Boolean
	{
		if (!hideChildrenTargets)
			return false;
			
		// Initialize a couple arrays that will be needed later
		restoreVisibleArray = [];
		restoreAutoLayoutArray = [];
		
		// Send each panel a "resizeStart" event, which will trigger
		// the resizeStartEffect (if any)
		var n:int = hideChildrenTargets.length;
		for (var i:int = 0; i < n; i++)
		{
			var p:Object = hideChildrenTargets[i];
			
			if (p is Panel)
			{
				var prevNumHideEffectsPlaying:Number = numHideEffectsPlaying;

				p.addEventListener(EffectEvent.EFFECT_START, eventHandler);				
				p.dispatchEvent(new Event("resizeStart"));
				p.removeEventListener(EffectEvent.EFFECT_START, eventHandler);

				// If no effect started playing, then make children invisible
				// immediately instead of waiting for the end of the effect
				if (numHideEffectsPlaying == prevNumHideEffectsPlaying)
					makePanelChildrenInvisible(Panel(p), i);
			}
		}

		return numHideEffectsPlaying > 0;
	}

	/**
	 *  @private
	 */
	private function makePanelChildrenInvisible(panel:Container,
												panelIndex:Number):void
	{
		var childArray:Array = [];
		
		var child:IUIComponent;
		
		// Hide the Panel's children while the Resize is occurring.
		var n:int = panel.numChildren;
		for (var i:int = 0; i < n; i++)
		{
			child = IUIComponent(panel.getChildAt(i));
			if (child.visible)
			{
				childArray.push(child);
				child.setVisible(false, true);
			}
		}
		
		// Hide the Panel's scrollbars while the Resize is occurring.
		child = panel.horizontalScrollBar;
		if (child && child.visible)
		{
			childArray.push(child);
			child.setVisible(false, true);
		}
		child = panel.verticalScrollBar;
		if (child && child.visible)
		{
			childArray.push(child);
			child.setVisible(false, true);
		}
		
		restoreVisibleArray[panelIndex] = childArray;

		// Set autoLayout = false, which prevents the Panel's updateDisplayList()
		// method from executing while the Panel is resizing.  
		if (panel.autoLayout)
		{
			panel.autoLayout = false;
			restoreAutoLayoutArray[panelIndex] = true;
		}
	}
	
	/**
	 * Method is used to explicitely determine widthTo and heightTo, taking into 
	 * account the current state of the component and the inputs to this ResizeEffect
	 * 
	 * @private
	 */
	private function calculateExplicitDimensionChanges():void
	{
		var explicitWidth:* = propertyChanges ? propertyChanges.end["explicitWidth"] : undefined;
		var explicitHeight:* = propertyChanges ? propertyChanges.end["explicitHeight"] : undefined;
		var percentWidth:* = propertyChanges ? propertyChanges.end["percentWidth"] : undefined;
		var percentHeight:* = propertyChanges ? propertyChanges.end["percentHeight"] : undefined;

		var targetAsContainer:Container;
		var parentAsContainer:Container;

		if (!heightSet)
		{
			// Determine the percentHeight/explicitHeight to apply to target when effect ends
			if (percentHeight !== undefined)
				origPercentHeight = percentHeight;
			else
				origPercentHeight = target.percentHeight;
		
			if (isNaN(origPercentHeight))
			{
				if (explicitHeight !== undefined)
					origExplicitHeight = explicitHeight;
				else
					origExplicitHeight = target.explicitHeight;
			}
			
			targetAsContainer = target as Container;
			if (targetAsContainer && targetAsContainer.verticalScrollBar == null)
			{
				origVerticalScrollPolicy = targetAsContainer.verticalScrollPolicy;
				targetAsContainer.verticalScrollPolicy = ScrollPolicy.OFF;
			}
			
			if (target.parent)
			{
				parentAsContainer = target.parent as Container;
				if (parentAsContainer && parentAsContainer.verticalScrollBar == null)
				{
					parentOrigVerticalScrollPolicy = parentAsContainer.verticalScrollPolicy;
					parentAsContainer.verticalScrollPolicy = ScrollPolicy.OFF;
				}		
			}
		}
		
		if (!widthSet)
		{
			// Determine the percentHeight/explicitHeight to apply to target when effect ends
			if (percentWidth !== undefined)
				origPercentWidth = percentWidth;
			else
				origPercentWidth = target.percentWidth;
		
			if (isNaN(origPercentWidth))
			{
				if (explicitWidth !== undefined)
					origExplicitWidth = explicitWidth;
				else
					origExplicitWidth = target.explicitWidth;
			}
			
			targetAsContainer = target as Container;
			if (targetAsContainer && targetAsContainer.horizontalScrollBar == null)
			{
				origHorizontalScrollPolicy = targetAsContainer.horizontalScrollPolicy;
				targetAsContainer.horizontalScrollPolicy = ScrollPolicy.OFF;
			}
			
			if (target.parent)
			{
				parentAsContainer = target.parent as Container;
				if (parentAsContainer && parentAsContainer.horizontalScrollBar == null)
				{
					parentOrigHorizontalScrollPolicy = parentAsContainer.horizontalScrollPolicy;
					parentAsContainer.horizontalScrollPolicy = ScrollPolicy.OFF;
				}		
			}
		}
				
		// The user may have supplied some combination of widthFrom,
		// widthTo, and widthBy. If either widthFrom or widthTo is
		// not explicitly defined, calculate its value based on the
		// other two values.
		if (isNaN(widthFrom))
		{
			widthFrom = (!isNaN(widthTo) && !isNaN(widthBy)) ?
						widthTo - widthBy :
						target.width;
		}
		if (isNaN(widthTo))
		{		
			if (isNaN(widthBy) &&
				propertyChanges &&
				(propertyChanges.end["width"] !== undefined ||
				 explicitWidth !== undefined ))
			{
				if (explicitWidth !== undefined && !isNaN(explicitWidth))
				{
					explicitWidthSet = true;
					_widthTo = explicitWidth;
				}
				else
				{
					_widthTo = propertyChanges.end["width"];
				}
			}
			else
			{
				_widthTo = (!isNaN(widthBy)) ?
						  widthFrom + widthBy :
						  target.width;
			}
		}

		// Ditto for heightFrom, heightTo, and heightBy.
		if (isNaN(heightFrom))
		{
			heightFrom = (!isNaN(heightTo) && !isNaN(heightBy)) ?
						 heightTo - heightBy :
						 target.height;
		}
		if (isNaN(heightTo))
		{		
			if (isNaN(heightBy) &&
				propertyChanges &&
				(propertyChanges.end["height"] !== undefined ||
				 explicitHeight !== undefined))
			{
				if (explicitHeight !== undefined && !isNaN(explicitHeight))
				{
					explicitHeightSet = true;
					_heightTo = explicitHeight;
				}
				else
				{
					_heightTo = propertyChanges.end["height"];
				}
			}
			else
			{
				_heightTo = (!isNaN(heightBy))?
						   heightFrom + heightBy :
						   target.height;
			}
		}
	} 
	
	/**
	 *  @private
	 */
	private function restorePanelChildren():void
	{
		if (hideChildrenTargets)
		{		
			var n:int = hideChildrenTargets.length;
			for (var i:int = 0; i < n; i++)
			{
				var p:IUIComponent = hideChildrenTargets[i];
								
				var childArray:Array = restoreVisibleArray[i];
				if (childArray)
				{
					var m:int = childArray.length;
					for (var j:int = 0; j < m; j++)
					{
						childArray[j].setVisible(true, true);
					}
				}
				
				if (restoreAutoLayoutArray[i])
					Container(p).autoLayout = true;
					
				// Trigger the resizeEndEffect (if any)	
				p.dispatchEvent(new Event("resizeEnd")); 
			}
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  This function is called when one of the Panels finishes
	 *  its "hide children" animation. 
	 */
	override mx_internal function eventHandler(event:Event):void
	{
		var panel:Container = event.target as Container;

		super.eventHandler(event);
		
		if (event.type == EffectEvent.EFFECT_START)
		{
			// Call my eventHandler() method when the effect finishes playing.
			panel.addEventListener(EffectEvent.EFFECT_END, eventHandler);

			// Remember how many effects we're waiting for
			numHideEffectsPlaying++;				
		}

		else if (event.type == EffectEvent.EFFECT_END)
		{		
			// Remove the event listener that triggered this callback.
			panel.removeEventListener(EffectEvent.EFFECT_END, eventHandler);
			
			// Get the array index of the panel
			var n:int = hideChildrenTargets.length;
			for (var i:int = 0; i < n; i++)
			{
				if (hideChildrenTargets[i] == panel)
					break;
			}
			
			makePanelChildrenInvisible(panel, i);

			// If all panels have finished their "hide children" effect,
			// then it's time to start our Resize effect.
			if (--numHideEffectsPlaying == 0)
				startResizeTween();		
		}		
	}
}

}
