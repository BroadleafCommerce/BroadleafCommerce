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

package mx.containers
{

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.Point;
import mx.containers.dividedBoxClasses.BoxDivider;
import mx.core.EdgeMetrics;
import mx.core.IFlexDisplayObject;
import mx.core.IInvalidating;
import mx.core.IUIComponent;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.ChildExistenceChangedEvent;
import mx.events.DividerEvent;
import mx.managers.CursorManager;
import mx.managers.CursorManagerPriority;
import mx.styles.StyleManager;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched multiple times as the user drags any divider.
 *
 *  The <code>dividerDrag</code> event is dispatched
 *  after the <code>dividerPress</code> event
 *  and before the <code>dividerRelease</code> event.
 *
 *  @eventType mx.events.DividerEvent.DIVIDER_DRAG
 */
[Event(name="dividerDrag", type="mx.events.DividerEvent")]

/**
 *  Dispatched when the user presses any divider in this container.
 *
 *  The <code>dividerPress</code> event is dispatched
 *  before any <code>dividerDrag</code> events are dispatched.
 *
 *  @eventType mx.events.DividerEvent.DIVIDER_PRESS
 */
[Event(name="dividerPress", type="mx.events.DividerEvent")]

/**
 *  Dispatched when the user releases a divider.
 *
 *  The <code>dividerRelease</code> event is dispatched
 *  after the <code>dividerDrag</code> events,
 *  but before the container's children are resized.
 *  The divider's x and y properties are not updated until
 *  after this event is triggered. As a result, a call to 
 *  <code>hDividerBox.getDividerAt(0).x</code> will return
 *  the value of the original x position of the first divider. If you want the
 *  position of the divider after the move, you can access it when the
 *  DividerBox's updateComplete event has been triggered.
 *
 *  @eventType mx.events.DividerEvent.DIVIDER_RELEASE
 */
[Event(name="dividerRelease", type="mx.events.DividerEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Thickness in pixels of the area where the user can click to drag a 
 *  divider.
 *  This area is centered in the gaps between the container's children,
 *  which are determined by the <code>horizontalGap</code> or
 *  <code>verticalGap</code> style property.
 *  The affordance cannot be set to a value larger than the gap size.
 *  A resize cursor appears when the mouse is over this area.
 *
 *  @default 6
 */
[Style(name="dividerAffordance", type="Number", format="Length", inherit="no")]

/**
 *  The alpha value that determines the transparency of the dividers.
 *  A value of <code>0.0</code> means completely transparent
 *  and a value of <code>1.0</code> means completely opaque.
 *  
 *  @default 0.75
 */
[Style(name="dividerAlpha", type="Number", inherit="no")]

/**
 *  Color of the dividers when the user presses or drags the dividers
 *  if the <code>liveDragging</code> property is set to <code>false</code>.
 *  If the <code>liveDragging</code> property is set to <code>true</code>,
 *  only the divider's knob is shown.
 *
 *  @default 0x6F7777
 */
[Style(name="dividerColor", type="uint", format="Color", inherit="yes")]

/**
 *  The divider skin.
 *
 *  The default value is the "mx.skin.BoxDividerSkin" symbol in the Assets.swf file.
 */
[Style(name="dividerSkin", type="Class", inherit="no")]

/**
 *  Thickness in pixels of the dividers when the user presses or drags the 
 *  dividers, if the <code>liveDragging</code> property is set to <code>false</code>.
 *  (If the <code>liveDragging</code> property is set to <code>true</code>,
 *  only the divider's knob is shown.)
 *  The visible thickness cannot be set to a value larger than the affordance.
 *  
 *  @default 3
 */
[Style(name="dividerThickness", type="Number", format="Length", inherit="no")]

/**
 *  The cursor skin for a horizontal DividedBox.
 *
 *  The default value is the "mx.skins.cursor.HBoxDivider" symbol in the Assets.swf file.
 */
[Style(name="horizontalDividerCursor", type="Class", inherit="no")]

/**
 *  The cursor skin for a vertical DividedBox.
 *
 *  The default value is the "mx.skins.cursor.VBoxDivider" symbol in the Assets.swf file.
 */
[Style(name="verticalDividerCursor", type="Class", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="focusIn", kind="event")]
[Exclude(name="focusOut", kind="event")]

[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]

[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("DividedBox.png")]

/**
 *  A DividedBox container measures and lays out its children
 *  horizontally or vertically in exactly the same way as a
 *  Box container, but it inserts
 *  draggable dividers in the gaps between the children.
 *  Users can drag any divider to resize the children on each side.
 *  
 *  <p>The DividedBox class is the base class for the more commonly used
 *  HDividedBox and VDividedBox classes.</p>
 *
 *  <p>The <code>direction</code> property of a DividedBox container, inherited 
 *  from Box container, determines whether it has horizontal
 *  or vertical layout.</p>
 *  
 *  <p>A DividedBox, HDividedBox, or VDividedBox container has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td><strong>Vertical DividedBox</strong> Height is large enough to hold all of its children at the default or explicit 
 *               heights of the children, plus any vertical gap between the children, plus the top and bottom padding of the 
 *               container. The width is the default or explicit width of the widest child, plus the left and right padding of 
 *               the container. 
 *               <br><strong>Horizontal DividedBox</strong> Width is large enough to hold all of its children at the 
 *               default or explicit widths of the children, plus any horizontal gap between the children, plus the left and 
 *               right padding of the container. Height is the default or explicit height of the tallest child 
 *               plus the top and bottom padding of the container.</br></td>
 *        </tr>
 *        <tr>
 *           <td>Default padding</td>
 *           <td>0 pixels for the top, bottom, left, and right values.</td>
 *        </tr>
 *        <tr>
 *           <td>Default gap</td>
 *           <td>10 pixels for the horizontal and vertical gaps.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:DividedBox&gt;</code> tag inherits all of the tag 
 *  attributes of its superclass, and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:DividedBox
 *    <strong>Properties</strong>
 *    liveDragging="false|true"
 *    resizeToContent="false|true"
 *  
 *    <strong>Styles</strong>
 *    dividerAffordance="6"
 *    dividerAlpha="0.75"
 *    dividerColor="0x6F7777"
 *    dividerSkin="<i>'mx.skins.BoxDividerSkin' symbol in Assets.swf</i>"
 *    dividerThickness="3"
 *    horizontalDividerCursor="<i>'mx.skins.cursor.HBoxDivider' symbol in Assets.swf</i>"
 *    verticalDividerCursor="<i>'mx.skins.cursor.VBoxDivider' symbol in Assets.swf</i>"
 * 
 *    <strong>Events</strong>
 *    dividerPress="<i>No default</i>"
 *    dividerDrag="<i>No default</i>"
 *    dividerRelease="<i>No default</i>"
 *    &gt;
 *      ...
 *      <i>child tags</i>
 *      ...
 *  &lt;/mx:DividedBox&gt;
 *  </pre>
 *  
 *  @see mx.containers.HDividedBox
 *  @see mx.containers.VDividedBox
 *
 *  @includeExample examples/DividedBoxExample.mxml
 */
public class DividedBox extends Box
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
	private static const PROXY_DIVIDER_INDEX:int = 999;

	//--------------------------------------------------------------------------
	//
	//  Class properties
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static var classInitialized:Boolean = false;

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  This method gets called once at first instance construction
	 *  rather than during class initialization.
	 *  In order for calls to StyleManager methods to work,
	 *  the factory class for the application or module
	 *  must have already registered StyleManagerImpl with Singleton.
	 *  This may not be the case at class initialization time.
	 */
	private static function initializeClass():void
	{
		StyleManager.registerSizeInvalidatingStyle("dividerAffordance");
		StyleManager.registerSizeInvalidatingStyle("dividerThickness");
	}

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function DividedBox()
	{
		super();

		if (!classInitialized)
		{
			initializeClass();
			classInitialized = true;
		}

		addEventListener(ChildExistenceChangedEvent.CHILD_ADD, childAddHandler);
		addEventListener(ChildExistenceChangedEvent.CHILD_REMOVE, 
						 childRemoveHandler);

		// make divided box visible in automation by default because
		// users interact with the dividers
		showInAutomationHierarchy = true;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Container for holding divider objects.
	 */
	private var dividerLayer:UIComponent = null;

	/**
	 *  @private
	 */
	mx_internal var activeDivider:BoxDivider;
	
	/**
	 *  @private
	 */
	private var activeDividerIndex:int = -1;
	
	/**
	 *  @private
	 */
	private var activeDividerStartPosition:Number;
	
	/**
	 *  @private
	 */
	private var dragStartPosition:Number;
	
	/**
	 *  @private
	 */
	private var dragDelta:Number;
	
	/**
	 *  @private
	 */
	private var oldChildSizes:Array /* of ChildSizeInfo */;
	
	/**
	 *  @private
	 */
	private var minDelta:Number;
	
	/**
	 *  @private
	 */
	private var maxDelta:Number;

	/**
	 *  @private
	 *  Only allow a single divider to move at a time.
	 */
	private var dontCoalesceDividers:Boolean;

	/**
	 *  @private
	 */
	private var cursorID:int = CursorManager.NO_CURSOR;

	/**
	 *  @private
	 *  We'll measure ourselves once and then store the results here
	 *  for the lifetime of the DividedBox.
	 */
	private var dbMinWidth:Number;
	private var dbMinHeight:Number;
	private var dbPreferredWidth:Number;
	private var dbPreferredHeight:Number;

	/**
	 *  @private
	 */
	private var layoutStyleChanged:Boolean = false;

    /**
     *  @private
     */
    private	var _resizeToContent:Boolean = false;

	/**
	 *  @private
	 *  Number of children with their includeInLayout set to true.  The rest of
	 *  the children don't count.
	 */
	private var numLayoutChildren:int = 0;

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  direction
	//----------------------------------

	/**
	 *  @private
	 */
	override public function set direction(value:String):void
	{
		if (super.direction != value)
		{
			super.direction = value;

			// Need to invalidate all our dividers
			if (dividerLayer)
			{
				for (var i:int = 0; i < dividerLayer.numChildren; i++)
					getDividerAt(i).invalidateDisplayList();
			}
		}
	}

	//----------------------------------
	//  dividerClass
	//----------------------------------

	/**
	 *  The class for the divider between the children.
	 *
	 *  @default mx.containers.dividedBoxClasses.BoxDivider
	 */
	protected var dividerClass:Class = BoxDivider;

	//----------------------------------
	//  liveDragging
	//----------------------------------

	[Inspectable(category="General")]

	/**
	 *  If <code>true</code>, the children adjacent to a divider
	 *  are continuously resized while the user drags it.
	 *  If <code>false</code>, they are not resized
	 *  until the user releases the divider.
	 *  @default false
	 */
	public var liveDragging:Boolean = false;

    //----------------------------------
    //	numDividers
    //----------------------------------

	/**
 	 *  The number of dividers. 
	 *  The count is always <code>numChildren</code> - 1.
	 *
	 *  @return The number of dividers.
	 */
	public function get numDividers():int
	{
		if (dividerLayer)
			return dividerLayer.numChildren;
		else 
			return 0;
	}

    //----------------------------------
    //	resizeToContent
    //----------------------------------

    /**
	 *  If <code>true</code>, the DividedBox automatically resizes to the size
	 *  of its children.
     *  @default false
     */
    public function	get	resizeToContent():Boolean
    {
        return _resizeToContent;
    }

    /**
     *  @private
     */
    public function	set	resizeToContent(value:Boolean):void
    {
        if (value != _resizeToContent)
        {
            _resizeToContent = value;
            if (value)
                invalidateSize();
        }
    }
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Override of the measure method of Box.
	 *
	 *  <p>This function is almost identical to the Box version except
	 *  that more extensive testing of the min, max
	 *  boundary conditions is performed. This is because the DividedBox allows
	 *  default values to be less than the min value of a control.</p>
	 */
	override protected function measure():void
	{
		super.measure();

		// We only measure ourselves once, since as we change the 
		// size of children, they in turn change the preferred sizes
		//
		// We need to copy the cached values into the measured fields
		// again to handle the case where scaleX or scaleY is not 1.0.
		// When the ViewStack is zoomed, code in UIComponent.measureSizes
		// scales the measuredWidth/Height values every time that
		// measureSizes is called. (Bug 100749)
		if (!isNaN(dbPreferredWidth) && !_resizeToContent && !layoutStyleChanged) 
		{
			measuredMinWidth = dbMinWidth;
			measuredMinHeight = dbMinHeight;
			measuredWidth = dbPreferredWidth;
			measuredHeight = dbPreferredHeight;
			return;
		}
	
		layoutStyleChanged = false;
	
		var isVertical:Boolean = this.isVertical();
		var minWidth:Number = 0;
		var minHeight:Number = 0;
		var preferredWidth:Number = 0;
		var preferredHeight:Number = 0;

		var n:int = numChildren;
		for (var i:int = 0; i < n; i++)
		{
			var child:IUIComponent = IUIComponent(getChildAt(i));

			if (!child.includeInLayout)
				continue;

			var prefW:Number = child.getExplicitOrMeasuredWidth();
			var prefH:Number = child.getExplicitOrMeasuredHeight();
			
			var minW:Number = child.minWidth;
			var minH:Number = child.minHeight;
			
			var wFlex:Boolean = !isNaN(child.percentWidth);
			var hFlex:Boolean = !isNaN(child.percentHeight);
		
			// Make sure we take the lowest of the low, since pref < min.
			var smallestMinW:Number = Math.min(prefW, minW);
			var smallestMinH:Number = Math.min(prefH, minH);

			if (isVertical)
			{
				minWidth = Math.max(wFlex ? minW : prefW, minWidth);
				preferredWidth = Math.max(prefW, preferredWidth);
				minHeight += hFlex ? smallestMinH : prefH;
				preferredHeight += prefH;
			}
			else
			{
				minWidth += wFlex ? smallestMinW : prefW;
				preferredWidth += prefW;
				minHeight = Math.max(hFlex ? minH : prefH, minHeight);
				preferredHeight = Math.max(prefH, preferredHeight);
			}
		}

		var wPadding:Number = layoutObject.widthPadding(numChildren);
		var hPadding:Number = layoutObject.heightPadding(numChildren);

		measuredMinWidth = dbMinWidth = minWidth + wPadding;
		measuredMinHeight = dbMinHeight = minHeight + hPadding;
		measuredWidth = dbPreferredWidth = preferredWidth + wPadding;
		measuredHeight = dbPreferredHeight = preferredHeight + hPadding;
	}

	/**
	 *  @private
	 */
	override protected function updateDisplayList(unscaledWidth:Number,
												  unscaledHeight:Number):void
	{
 		var n:int;
 		var i:int;

		// This method gets called while dragging a divider,
		// but we don't want to do anything then.
		if (!liveDragging && activeDivider)
 		{
 			n = numChildren;
 			for (i = 0; i < n; i++)
 			{
 				var child:IUIComponent = IUIComponent(getChildAt(i));

				if (!child.includeInLayout)
					continue;
 	
 				// Clear out measured min/max
 				// so super.layout() doesn't use them.
 				child.measuredMinWidth = 0; 
 				child.measuredMinHeight = 0;
 			}
   			return;
 		}

		// Before we allow layout, let's clear out any measured min 
		// values of our children so that they don't restrict us.
		// We only honour explicitly set mins/maxs.
		// We also try to remove any excess space, but tweaking 
		// the % values on the flexible components.
		preLayoutAdjustment();

		// Let Box lay out the children.
		super.updateDisplayList(unscaledWidth, unscaledHeight);
		
		// Lay out the dividers.
		if (dividerLayer)
		{
			var vm:EdgeMetrics = viewMetrics;

			dividerLayer.x = vm.left;
			dividerLayer.y = vm.top;

			n = dividerLayer.numChildren;
			for (i = 0; i < n; i++)
			{
				layoutDivider(i, unscaledWidth, unscaledHeight);
			}
		}
	}

	/**
	 *  @private
	 */
	override public function styleChanged(styleProp:String):void
	{
		super.styleChanged(styleProp);
		
		// Need to invalidate all our dividers
		if (dividerLayer)
		{
			var n:int = dividerLayer.numChildren;
			for (var i:int = 0; i < n; i++)
			{
				getDividerAt(i).styleChanged(styleProp);
			}
		}
		
		if (StyleManager.isSizeInvalidatingStyle(styleProp))
		{
			layoutStyleChanged = true;
		}
	}
	
	/**
	 *  @private
	 */
	override protected function scrollChildren():void
	{
		super.scrollChildren();

		// Scroll divider layer.
		if (contentPane && dividerLayer)
			dividerLayer.scrollRect = contentPane.scrollRect;
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Returns a reference to the specified BoxDivider object
	 *  in this DividedBox container. 
	 *
	 *  @param i Zero-based index of a divider, counting from 
	 *  the left for a horizontal DividedBox, 
	 *  or from the top for a vertical DividedBox.
	 *
	 *  @return A BoxDivider object.
	 */
	public function getDividerAt(i:int):BoxDivider
	{
		return BoxDivider(dividerLayer.getChildAt(i));
	}

	/**
	 *  Move a specific divider a given number of pixels.
	 *
	 *  @param i Zero-based index of a divider, counting from 
	 *  the left for a horizontal DividedBox, 
	 *  or from the top for a vertical DividedBox.
	 *
	 *  @param amt The number of pixels to move the divider.
     *  A negative number can be specified in order to move
     *  a divider up or left. The divider movement is
     *  constrained in the same manner as if a user
 	 *  had moved it.
	 */
	public function moveDivider(i:int, amt:Number):void
	{
		// Check whether this is a valid divider index.
		if (i < 0 || i >= numDividers)
			return;

		// Make sure the user is not currently dragging.
		if (activeDividerIndex >= 0)
			return;

		// We have to first hit the children if we haven't
		// yet done so since the first movement .

		// Mimic a divider moving.
		activeDividerIndex = i;

		// Store away child sizes,
		// then determine the limits on our movement.
		cacheChildSizes();
		computeMinAndMaxDelta();
		dragDelta = limitDelta(amt);

		// Now update the children sizes accordingly.
		adjustChildSizes();

		invalidateSize();
		invalidateDisplayList();

		// Reset the divider tracking stuff.
		resetDividerTracking();
	}

	/**
	 *  @private
	 */
	private function createDivider(i:int):BoxDivider
	{
		// Create separate layer for holding divider objects.
		if (!dividerLayer)
		{
			dividerLayer = UIComponent(rawChildren.addChild(new UIComponent()));
		}

		var divider:BoxDivider = BoxDivider(new dividerClass());
		dividerLayer.addChild(divider);
		
		// if we are creating the active divider bring the divider layer 
		// to the top most so that users can see the divider line over 
		// the other children
		if (i == PROXY_DIVIDER_INDEX)
		{
			rawChildren.setChildIndex(dividerLayer, 
										  rawChildren.numChildren-1);
		}
		
		// Tell BoxDivider to use DividedBox's styles,
		// unless we are sliding the divider; in that case,
		// use the styles of the divider we are sliding.
		var basedOn:IFlexDisplayObject = (i == PROXY_DIVIDER_INDEX) ?
										 getDividerAt(activeDividerIndex) :
										 this;
		
		divider.styleName = basedOn;
		
		divider.owner = this;
		
		return divider;
	}

	/**
	 *  @private
	 */
	private function layoutDivider(i:int, 
								   unscaledWidth:Number, 
								   unscaledHeight:Number):void
	{
		// The mouse-over thickness of the divider is normally determined
		// by the dividerAffordance style, and the visible thickness is 
		// normally determined by the dividerThickness style, assuming that
		// the relationship thickness <= affordance <= gap applies. But if
		// one of the other five orderings applies, here is a table of what
		// happens:
		//
		//  divider    divider    horizontalGap/  dividerWidth/  visible width/
		// Thickness  Affordance  verticalGap     dividerHeight  visible height
		//                                           
		//    4           6             8               6              4
		//    4           8             6               6              4
		//    6           4             8               6              6
		//    6           8             4               4              4
		//    8           4             6               6              6        
		//    8           6             4               4              4

		var divider:BoxDivider = BoxDivider(getDividerAt(i));

		var prevChild:IUIComponent = IUIComponent(getChildAt(i));
		var nextChild:IUIComponent = IUIComponent(getChildAt(i + 1));
		
		var vm:EdgeMetrics = viewMetrics;

		var verticalGap:Number = getStyle("verticalGap");
		var horizontalGap:Number = getStyle("horizontalGap");

		var thickness:Number = divider.getStyle("dividerThickness");
		var affordance:Number = divider.getStyle("dividerAffordance");

		if (isVertical())
		{
			var dividerHeight:Number = affordance;
				// dividerHeight is the mouse-over height,
				// not necessarily the visible height.
			
			// The specified affordance should be greater than the thickness.
			// But if it isn't, use the thickness instead to determine the
			// divider height.
			if (dividerHeight < thickness)
				dividerHeight = thickness;

			// Don't let the divider overlap the children.
			if (dividerHeight > verticalGap)
				dividerHeight = verticalGap;
			
			divider.setActualSize(unscaledWidth - vm.left - vm.right, dividerHeight);

			divider.move(vm.left,
						 Math.round((prevChild.y + prevChild.height +
						  			nextChild.y - dividerHeight) / 2));
		}
		else
		{
			var dividerWidth:Number = affordance;
				// dividerWidth is the mouse-over width,
				// not necessarily the visible width.

			// The specified affordance should be greater than the thickness.
			// But if it isn't, use the thickness instead to determine the
			// divider width.
			if (dividerWidth < thickness)
				dividerWidth = thickness;

			// Don't let the divider overlap the children.
			if (dividerWidth > horizontalGap)
				dividerWidth = horizontalGap;

			divider.setActualSize(dividerWidth, unscaledHeight - vm.top - vm.bottom);

			divider.move(Math.round((prevChild.x + prevChild.width +
						  			nextChild.x - dividerWidth) / 2),
						 vm.top);
		}

		divider.invalidateDisplayList();
	}

	/**
	 *  @private
	 */
	mx_internal function changeCursor(divider:BoxDivider):void
	{
		if (cursorID == CursorManager.NO_CURSOR)
		{
			// If a cursor skin has been set for the specified BoxDivider,
			// use it. Otherwise, use the cursor skin for the DividedBox.
			var cursorClass:Class = isVertical() ?
									getStyle("verticalDividerCursor") as Class :
									getStyle("horizontalDividerCursor") as Class;

			cursorID = cursorManager.setCursor(cursorClass,
											   CursorManagerPriority.HIGH, 0, 0);
		}
	}

	/**
	 *  @private
	 */
	mx_internal function restoreCursor():void
	{
		if (cursorID != CursorManager.NO_CURSOR)
		{
			cursorManager.removeCursor(cursorID);
			cursorID = CursorManager.NO_CURSOR;
		}
	}

	/**
	 *  @private
	 */
	mx_internal function getDividerIndex(divider:BoxDivider):int
	{
		var n:int = numChildren;
		for (var i:int = 0; i < n - 1; i++)
		{
			if (getDividerAt(i) == divider)
				return i;
		}
		return -1;
	}

	/**
	 *  @private
	 */
	private function getMousePosition(event:MouseEvent):Number
	{
        var point:Point = new Point(event.stageX, event.stageY);
        point = globalToLocal(point);
		return isVertical() ? point.y : point.x;
	}

	/**
	 *  @private
	 */
	mx_internal function startDividerDrag(divider:BoxDivider,
                                          trigger:MouseEvent):void
	{
		// Make sure the user is not currently dragging.
		if (activeDividerIndex >= 0)
			return;
		
		activeDividerIndex = getDividerIndex(divider);

		var event:DividerEvent = 
            new DividerEvent(DividerEvent.DIVIDER_PRESS);
		event.dividerIndex = activeDividerIndex;
		dispatchEvent(event);

		if (liveDragging)
			activeDivider = divider;
		else
		{
			activeDivider = createDivider(PROXY_DIVIDER_INDEX);
			activeDivider.visible = false;
			activeDivider.state = DividerState.DOWN;
			activeDivider.setActualSize(divider.width, divider.height);
			activeDivider.move(divider.x, divider.y);
			activeDivider.visible = true;
			divider.state = DividerState.UP;
		}

		if (isVertical())
			activeDividerStartPosition = activeDivider.y;
		else
			activeDividerStartPosition = activeDivider.x;

        dragStartPosition = getMousePosition(trigger);
		dragDelta = 0;

		cacheChildSizes();
		adjustChildSizes();

		computeMinAndMaxDelta();

		systemManager.addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler, true);
	}

	/**
	 *  @private
	 *  Store away some important information about
	 *  each child for us to use while we move the divider.
	 */
	private function cacheSizes():void
	{
		oldChildSizes = []; // empty array

		var vertical:Boolean = isVertical();
		
		var smallest:Number = Number.MAX_VALUE; // use a big number
		
		var n:int = numChildren;
		for (var i:int = 0; i < n; i++)
		{
			var child:IUIComponent = IUIComponent(getChildAt(i));
		
			var sz:Number = vertical ? child.height : child.width;

			var mx:Number = vertical ? child.maxHeight : child.maxWidth;
			
			var umn:Number = vertical ?
							 child.explicitMinHeight :
							 child.explicitMinWidth; // avoid measured values
			
			// A NaN min means 0.
			var mn:Number = (isNaN(umn)) ? 0 : umn;
			
			// Compute these for later use.
			var dMin:Number = Math.max(0, sz - mn);
			var dMax:Number = Math.max(0, mx - sz);

			if (sz > 0 && sz < smallest)
				smallest = sz;

			oldChildSizes.push(new ChildSizeInfo(sz, mn, mx, dMin, dMax));
		}

		// Remember the smallest child size we saw.
		oldChildSizes.push(new ChildSizeInfo((smallest == Number.MAX_VALUE) ? 1 : smallest));
	}

	/**
	 *  @private
	 */
	private function cacheChildSizes():void
	{
		oldChildSizes = []; // clear or store
		
		cacheSizes();
	}

	/**
	 *  @private
	 */
	private function mouseMoveHandler(event:MouseEvent):void
	{
		dragDelta = limitDelta(getMousePosition(event) - dragStartPosition);

		var dividerEvent:DividerEvent = 
            new DividerEvent(DividerEvent.DIVIDER_DRAG);
		dividerEvent.dividerIndex = activeDividerIndex;
		dividerEvent.delta = dragDelta;
		dispatchEvent(dividerEvent);

		if (liveDragging)
		{
			adjustChildSizes();

			invalidateDisplayList();
			
			updateDisplayList(unscaledWidth, unscaledHeight);
		}
		else
		{
			if (isVertical())
				activeDivider.move(0, activeDividerStartPosition + dragDelta);
			else
				activeDivider.move(activeDividerStartPosition + dragDelta, 0);
		}
	}

	/**
	 *  @private
	 */
	mx_internal function stopDividerDrag(divider:BoxDivider,
                                         trigger:MouseEvent):void
	{
		dragDelta = limitDelta(getMousePosition(trigger) - dragStartPosition);

		var event:DividerEvent = 
            new DividerEvent(DividerEvent.DIVIDER_RELEASE);
		event.dividerIndex = activeDividerIndex;
		event.delta = dragDelta;
        dispatchEvent(event);

        if (!liveDragging)
		{
			if (dragDelta == 0)
				getDividerAt(activeDividerIndex).state = DividerState.OVER;

			if (activeDivider)
				dividerLayer.removeChild(activeDivider);
			activeDivider = null;
			
			adjustChildSizes();

			invalidateSize();
			invalidateDisplayList();
		}

		resetDividerTracking();
		systemManager.removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler, true);
	}

	/**
	 *  @private
	 */
	private function resetDividerTracking():void
	{
 		activeDivider = null;
		activeDividerIndex = -1;
		activeDividerStartPosition = NaN;
		dragStartPosition = NaN;
		dragDelta = NaN;
		oldChildSizes = null;
		minDelta = NaN;
		maxDelta = NaN;
	}

	/**
	 *  @private
	 *  Determine the maximum amount of movement that 
	 *  a divider, i, can move both up and down.
	 * 
	 *  We base this calculation on the amount of 
	 *  travel that each divider supports, assuming
	 *  that as one divider reaches its limits, the next
 	 *  divider will move.  In this way dividers will
	 *  cascade.
  	 */
	private function computeAllowableMovement(at:int):void
	{
		// We break the computation into two loops, 
		// so that we can calc min and max travel
		// independently as one would move the divider
		// either up or down.
		var deltaMinAbove:Number = 0;
		var deltaMaxAbove:Number = 0;
		var deltaMinBelow:Number = 0;
		var deltaMaxBelow:Number = 0;
		
		var n:int = numChildren;
		var i:int;
		var child:ChildSizeInfo;

		if (at < 0)
			return;
			
		for (i = at; i >= 0; i = (i > 0) ? i - 1 : -1)
		{	
			child = ChildSizeInfo(oldChildSizes[i]);
			
			deltaMinAbove += (dontCoalesceDividers && deltaMinAbove) ?
							 0 : child.deltaMin;
			
			deltaMaxAbove += (dontCoalesceDividers && deltaMaxAbove) ?
							 0 : child.deltaMax;
		}

		for (i = at + 1; i < n; i++)
		{
			child = ChildSizeInfo(oldChildSizes[i]);
			
			deltaMinBelow += (dontCoalesceDividers && deltaMinBelow) ?
							 0 : child.deltaMin;
			
			deltaMaxBelow += (dontCoalesceDividers && deltaMaxBelow) ?
							 0 : child.deltaMax;
		}

		// Now the maximum movement we can have if
		// the divider is moved up is equal to the 
		// smaller of how much we can shrink all
		// components above our divider or the 
		// maximum of how much the components below
		// our divider can grow. Similarly for the 
		// divider moving in the opposite direction.
		var deltaUp:Number = Math.min(deltaMinAbove, deltaMaxBelow);
		var deltaDn:Number = Math.min(deltaMinBelow, deltaMaxAbove);

		// deltaUp needs to be in -ve in order for our 
		// update logic to work
		minDelta = -deltaUp;
		maxDelta = deltaDn;
	}

	/**
	 *  @private
  	 */
	private function computeMinAndMaxDelta():void
	{
		computeAllowableMovement(activeDividerIndex);
	}

	/**
	 *  @private
  	 */
	private function limitDelta(delta:Number):Number
	{
		if (delta < minDelta)
			delta = minDelta;
		else if (delta > maxDelta)
			delta = maxDelta;

		// Make sure it is not fractional,
		// otherwise we lose pixels. (Bug 87339)
		delta = Math.round(delta);
		return delta;
	}

	/**
	 *  @private
     *  We distribute the delta space in the same
	 *  fashion that we calculated it. That is we
	 *  start at the divider an give out space
	 *  until we hit a limit on the component or
	 *  we run out of space to distribute.
	 *  We need to do this in both directions since
	 *  in one direction we are shrinking and 
     *  in the other we are growing.
	 */
	private function distributeDelta():void
	{
		// if there is no movement of divider we need not
		// do any child resizing.
		if (!dragDelta)
			return;

		var vertical:Boolean = isVertical();
		var n:int = numChildren;
		var k:int = activeDividerIndex;
		var smallest:Number = oldChildSizes[n].size -
			Math.abs(dragDelta); // smallest possible child size
		
		if (smallest <= 0 || isNaN(smallest))
			smallest = 1;

		var i:int;
		var size:ChildSizeInfo;
		var move:Number;
		var newSize:Number;
		var child:IUIComponent;
		var childSize:Number;
		
		// Distribute space starting from the center and 
		// moving upwards.
		var amt:Number = dragDelta;
		for (i = k; i >= 0; i = (i > 0) ? i - 1 : -1)
		{
			// If dragDelta -ve  => shrink upper components
			// otherwise grow them.
			size = ChildSizeInfo(oldChildSizes[i]);
			move = (amt < 0) ?
				   -Math.min(-amt, size.deltaMin) :
				   Math.min(amt, size.deltaMax);

			// Adjust the component and reduce the remaining delta
			newSize = size.size + move;
			amt -= move;

			// Adjust the child size.
			child = IUIComponent(getChildAt(i));
			childSize = (newSize / smallest) * 100;
			
			if (vertical)
				child.percentHeight = childSize;
			else
				child.percentWidth = childSize;

			// Force a re-measure.
			if (child is IInvalidating)
				IInvalidating(child).invalidateSize();
		}

		// assert(amt == 0)

		// Now do the same distribution but moving downwards.
		amt = dragDelta;
		for (i = k + 1; i < n; i++)
		{
			// If dragDelta -ve  => grow lower components
			// otherwise shrink them.
			size = ChildSizeInfo(oldChildSizes[i]);
			move = (amt < 0) ?
				   Math.min(-amt, size.deltaMax) :
				   -Math.min(amt, size.deltaMin);

			// Adjust the component and reduce the remaining delta.
			newSize = size.size + move;
			amt += move;

			child = IUIComponent(getChildAt(i));
			childSize = (newSize / smallest) * 100;
			
			if (vertical)
				child.percentHeight = childSize;
			else
				child.percentWidth = childSize;

			// Force a re-measure.
			if (child is IInvalidating)
				IInvalidating(child).invalidateSize();
		}
	}

	/**
	 *  @private
 	 *  For 1.5 we normalize all children to the smallest one
	 *  So that we can remove n-1 children and still guarantee
	 *  one child can consume 100%.
	 *  Also we support the concept of fixed sized children, 
	 *  which allows us to have one or more children be rigid
     *  in the DividedBox in this case, the dividers above 
     *  and below the fixed component move in unison.
	 */
	private function adjustChildSizes():void
	{
		distributeDelta();
	}

	/**
	 *  @private
	 *  Algorithm employed pre-layout to ensure that 
	 *  we don't leave any dangling space and to ensure
	 *  that only explicit min/max values are honored.
	 * 
	 *  We first compute the sum of %'s across all 
	 *  children to ensure that we have at least 100%.
 	 *  If so, we are done.  If not, then we attempt 
	 *  to attach the remaining amount to the last 
	 *  component, if not, then we distribute the 
	 *  percentages evenly across all % components.
	 * 
	 */
	private function preLayoutAdjustment():void
	{
		// Calculate the total %
		var vertical:Boolean = isVertical();
		
		var totalPerc:Number = 0;
		var percCount:Number = 0;
		
		var n:int = numChildren;
		var i:int;
		var child:IUIComponent;
		var perc:Number;

		for (i = 0; i < n; i++)
		{
			child = IUIComponent(getChildAt(i));

			if (!child.includeInLayout)
				continue;

			// Clear out measured min/max
			// so super.layout() doesn't use them.
			child.measuredMinWidth = 0; 
			child.measuredMinHeight = 0;

			perc = vertical ? child.percentHeight : child.percentWidth;
			
			if (!isNaN(perc))
			{
				totalPerc += perc;
				percCount++;
			}
		}

		// No flexible children, so we make the last one 100%.
		if (totalPerc == 0 && percCount == 0)
		{
			// Everyone is fixed and we can give 100% to the last one 
			// without concern.
			if (n > 0)
			{
				child = IUIComponent(getChildAt(n - 1));

				if (child.includeInLayout)
				{
					if (vertical)
						child.percentHeight = 100;
					else
						child.percentWidth = 100;
				}
			}
		}
		else if (totalPerc < 100)
		{
			// We have some %s but they don't total to 100, so lets
			// distribute the delta across all of them and in the
			// meantime normalize all %s to unscaledHeight/Width.
			// The normalization takes care of the case where any one
			// of the components hits a min/max limit on their size,
			// which could result in the others filling less than 100%.
			var delta:Number = Math.ceil((100 - totalPerc) / percCount);
			for (i = 0; i < n; i++)
			{
				child = IUIComponent(getChildAt(i));

				if (!child.includeInLayout)
					continue;
				
				if (vertical)
				{
					perc = child.percentHeight;
					if (!isNaN(perc))
						child.percentHeight = (perc + delta) * unscaledHeight;
				}
				else
				{
					perc = child.percentWidth;
					if (!isNaN(perc))
						child.percentWidth = (perc + delta) * unscaledWidth;
				}
			}
		}

		// OK after all this magic we still can't guarantee that the space is
		// entirely filled. For example, all percent components hit their max
		// values. In this case, the layout will include empty space at the end,
		// and once the divider is touched, the non-percent based components
		// will be converted into percent based ones and fill the remaining
		// space. It seems to me that this scenario is highly unlikely.
		// Thus I've choosen the route of stretching the percent based
		// components and not touching the explicitly sized or default
		// sized ones.
		//
		// Another option would be to stretch the default sized components
		// either in addition to the percent based ones or instead of.
		// This seemed a  little odd to me as the user never indicated
		// that these components are to be stretched initially, so in the end
		// I choose to tweak the components that the user has indicated
		// as being stretchable. 
	}

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function childAddHandler(event:ChildExistenceChangedEvent):void
	{
		var child:DisplayObject = event.relatedObject;

		if (IUIComponent(child).includeInLayout)
			numLayoutChildren++;
		
		if (numLayoutChildren > 1)
			createDivider(numLayoutChildren - 2);

		child.addEventListener("includeInLayoutChanged",
                               child_includeInLayoutChangedHandler);

		// If this child is the new last child, and we have already
		// set the old last child to 100%, move that 100% setting to
		// the new last child. This way dynamically added children
		// (across multiple layout passes) behave the same as if they
		// were all added in a single layout pass. Bug 165249.
		if (initialized && getChildIndex(child) == numChildren - 1 && numChildren > 1)
		{
			var foundPercentage:Boolean = false;
			var comp:IUIComponent;
			var isVertical:Boolean = this.isVertical();
			
			// First, check for any percentage values
			for (var i:int = 0; i < numChildren - 2; i++)
			{
				comp = IUIComponent(getChildAt(i));
				if (!isNaN(isVertical ? comp.percentHeight : comp.percentWidth))
					foundPercentage = true;
			}
			
			// Next, see if the old last child has 100%, and reset if it 
			// does.
			comp = IUIComponent(getChildAt(numChildren - 2));
			if (!foundPercentage)
			{
				if (isVertical)
				{
					if (comp.percentHeight == 100)
						comp.percentHeight = NaN;
				}
				else
				{
					if (comp.percentWidth == 100)
						comp.percentWidth = NaN;
				}
			}
			
			// The new child will be set to 100% in preLayoutAdjustment().
		}
		
		// Clear the cached values so that we do another 
		// measurement pass.
		dbMinWidth = NaN;
		dbMinHeight = NaN;
		dbPreferredWidth = NaN;
		dbPreferredHeight = NaN;
	}

	/**
	 *  @private
	 */
	private function childRemoveHandler(event:ChildExistenceChangedEvent):void
	{
		var child:DisplayObject = event.relatedObject;
		
		if (IUIComponent(child).includeInLayout)
			numLayoutChildren--;
	
		if (numLayoutChildren > 0)
			dividerLayer.removeChild(getDividerAt(numLayoutChildren - 1));

		child.removeEventListener("includeInLayoutChanged",
                                  child_includeInLayoutChangedHandler);

		// Clear the cached values so that we do another 
		// measurement pass.
		dbMinWidth = NaN;
		dbMinHeight = NaN;
		dbPreferredWidth = NaN;
		dbPreferredHeight = NaN;
		invalidateSize();
	}
	
	/**
	 *  @private
	 *  When a child's includeInLayout changes, we either remove or add a
	 *  divider.
	 */
	private function child_includeInLayoutChangedHandler(event:Event):void
	{
		var child:UIComponent = UIComponent(event.target);

		if (child.includeInLayout && ++numLayoutChildren > 1)
			createDivider(numLayoutChildren - 2);

		else if (!child.includeInLayout && --numLayoutChildren > 0)
			dividerLayer.removeChild(getDividerAt(numLayoutChildren - 1));
	}
	
}

}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: ChildSizeInfo
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 */
class ChildSizeInfo
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	public function ChildSizeInfo(size:Number,
								  min:Number = 0, max:Number = 0,
								  deltaMin:Number = 0, deltaMax:Number = 0)
	{
		super();

		this.size = size;
		this.min = min;
		this.max = max;
		this.deltaMin = deltaMin;
		this.deltaMax = deltaMax;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  deltaMin
	//----------------------------------

	/**
	 *  @private
	 */
	public var deltaMin:Number;

	//----------------------------------
	//  deltaMax
	//----------------------------------

	/**
	 *  @private
	 */
	public var deltaMax:Number;

	//----------------------------------
	//  min
	//----------------------------------

	/**
	 *  @private
	 */
	public var min:Number;

	//----------------------------------
	//  max
	//----------------------------------

	/**
	 *  @private
	 */
	public var max:Number;

	//----------------------------------
	//  size
	//----------------------------------

	/**
	 *  @private
	 */
	public var size:Number;
}

