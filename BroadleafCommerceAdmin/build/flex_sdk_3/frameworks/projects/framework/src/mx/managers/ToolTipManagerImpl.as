////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers
{

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.utils.Timer;
import mx.controls.ToolTip;
import mx.core.ApplicationGlobals;
import mx.core.IInvalidating;
import mx.core.IToolTip;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.effects.IAbstractEffect;
import mx.effects.EffectManager;
import mx.events.EffectEvent;
import mx.events.ToolTipEvent;
import mx.managers.IToolTipManagerClient;
import mx.styles.IStyleClient;
import mx.validators.IValidatorListener;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 *  The ToolTipManager lets you set basic ToolTip and error tip functionality,
 *  such as display delay and the disabling of ToolTips.
 *
 *  @see mx.controls.ToolTip
 *  @see mx.validators.Validator
 */
public class ToolTipManagerImpl extends EventDispatcher
             implements IToolTipManager2
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static var instance:IToolTipManager2;

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    public static function getInstance():IToolTipManager2
    {
        if (!instance)
            instance = new ToolTipManagerImpl();

        return instance;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function ToolTipManagerImpl()
    {
        super();
        
        if (instance)
            throw new Error("Instance already exists.");
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  A flag that keeps track of whether this class's initialize()
     *  method has been executed.
     */
    mx_internal var initialized:Boolean = false;

    /**
     *  @private
     *  This timer is used to delay the appearance of a normal ToolTip
     *  after the mouse moves over a target; an error tip has no such delay.
     *
     *  <p>This timer, which is lazily created, is started when the mouse
     *  moves over an object with a ToolTip, with a duration specified
     *  by showDelay.
     *  If the mouse moves out of this object before the timer fires,
     *  the ToolTip is never created.
     *  If the mouse stays over the object until the timer fires,
     *  the ToolTip is created and its showEffect is started.
     */
    mx_internal var showTimer:Timer;

    /**
     *  @private
     *  This timer is used to make the tooltip "time out" and hide itself
     *  if the mouse stays over a target.
     *
     *  <p>This timer, which is lazily created, is started
     *  when the showEffect ends.
     *  When it fires, the hideEffect is started.</p>
     */
    mx_internal var hideTimer:Timer;

    /**
     *  @private
     *  This timer is used to implement mousing quickly over multiple targets
     *  with ToolTip...
     *
     *  <p>This timer, which is lazily created, is started
     *  when ...</p>
     */
    mx_internal var scrubTimer:Timer;

    /**
     *  @private
     */
    mx_internal var currentText:String;

    /**
     *  @private
     */
    mx_internal var isError:Boolean;

    /**
     *  The UIComponent with the ToolTip assigned to it
     *  that was most recently under the mouse.
     *  During much of the tool tip life cycle this property
     *  has the same value as the <code>currentTarget</code> property.
     */
    mx_internal var previousTarget:DisplayObject;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  currentTarget
    //----------------------------------

    /**
     *  @private
     */
    private var _currentTarget:DisplayObject;

    /**
     *  The UIComponent that is currently displaying a ToolTip,
     *  or <code>null</code> if none is.
     */
    public function get currentTarget():DisplayObject
    {
        return _currentTarget;
    }
    
    /**
     *  @private
     */
    public function set currentTarget(value:DisplayObject):void
    {
        _currentTarget = value;
    }

    //----------------------------------
    //  currentToolTip
    //----------------------------------

    /**
     *  @private
     */
    private var _currentToolTip:IToolTip;

    /**
     *  The ToolTip object that is currently visible,
     *  or <code>null</code> if none is shown.
     */
    public function get currentToolTip():IToolTip
    {
        return _currentToolTip;
    }
    
    /**
     *  @private
     */
    public function set currentToolTip(value:IToolTip):void
    {
        _currentToolTip = value;
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  @private
     */
    private var _enabled:Boolean = true;

    /**
     *  If <code>true</code>, the ToolTipManager will automatically show
     *  ToolTips when the user moves the mouse pointer over components.
     *  If <code>false</code>, no ToolTips will be shown.
     *
     *  @default true
     */
    public function get enabled():Boolean 
    {
        return _enabled;
    }
    
    /**
     *  @private
     */
    public function set enabled(value:Boolean):void
    {
        _enabled = value;
    }

    //----------------------------------
    //  hideDelay
    //----------------------------------

    /**
     *  @private
     */
    private var _hideDelay:Number = 10000; // milliseconds

    /**
     *  The amount of time, in milliseconds, that Flex waits
     *  to hide the ToolTip after it appears.
     *  Once Flex hides a ToolTip, the user must move the mouse
     *  off the component and then back onto it to see the ToolTip again.
     *  If you set <code>hideDelay</code> to <code>Infinity</code>,
     *  Flex does not hide the ToolTip until the user triggers an event,
     *  such as moving the mouse off of the component.
     *
     *  @default 10000
     */
    public function get hideDelay():Number 
    {
        return _hideDelay;
    }
    
    /**
     *  @private
     */
    public function set hideDelay(value:Number):void
    {
        _hideDelay = value;
    }

    //----------------------------------
    //  hideEffect
    //----------------------------------

    /**
     *  @private
     */
    private var _hideEffect:IAbstractEffect;

    /**
     *  The effect that plays when a ToolTip is hidden,
     *  or <code>null</code> if the ToolTip should disappear with no effect.
     *
     *  @default null
     */
    public function get hideEffect():IAbstractEffect
    {
        return _hideEffect;
    }
    
    /**
     *  @private
     */
    public function set hideEffect(value:IAbstractEffect):void
    {
        _hideEffect = value as IAbstractEffect;
    }

    //----------------------------------
    //  scrubDelay
    //----------------------------------

    /**
     *  @private
     */
    private var _scrubDelay:Number = 100; // milliseconds

    /**
     *  The amount of time, in milliseconds, that a user can take
     *  when moving the mouse between controls before Flex again waits
     *  for the duration of <code>showDelay</code> to display a ToolTip.
     *
     *  <p>This setting is useful if the user moves quickly from one control
     *  to another; after displaying the first ToolTip, Flex will display
     *  the others immediately rather than waiting.
     *  The shorter the setting for <code>scrubDelay</code>, the more
     *  likely that the user must wait for an amount of time specified
     *  by <code>showDelay</code> in order to see the next ToolTip.
     *  A good use of this property is if you have several buttons on a
     *  toolbar, and the user will quickly scan across them to see brief
     *  descriptions of their functionality.</p>
     *
     *  @default 100
     */
    public function get scrubDelay():Number 
    {
        return _scrubDelay;
    }
    
    /**
     *  @private
     */
    public function set scrubDelay(value:Number):void
    {
        _scrubDelay = value;
    }

    //----------------------------------
    //  showDelay
    //----------------------------------

    /**
     *  @private
     */
    private var _showDelay:Number = 500; // milliseconds

    /**
     *  The amount of time, in milliseconds, that Flex waits
     *  before displaying the ToolTip box once a user
     *  moves the mouse over a component that has a ToolTip.
     *  To make the ToolTip appear instantly, set <code>showDelay</code> to 0.
     *
     *  @default 500
     */
    public function get showDelay():Number 
    {
        return _showDelay;
    }
    
    /**
     *  @private
     */
    public function set showDelay(value:Number):void
    {
        _showDelay = value;
    }

    //----------------------------------
    //  showEffect
    //----------------------------------

    /**
     *  @private
     */
    private var _showEffect:IAbstractEffect;

    /**
     *  The effect that plays when a ToolTip is shown,
     *  or <code>null</code> if the ToolTip should appear with no effect.
     *
     *  @default null
     */
    public function get showEffect():IAbstractEffect
    {
        return _showEffect;
    }
    
    /**
     *  @private
     */
    public function set showEffect(value:IAbstractEffect):void
    {
        _showEffect = value as IAbstractEffect;
    }

    //----------------------------------
    //  toolTipClass
    //----------------------------------

    /**
     *  @private
     */
    private var _toolTipClass:Class = ToolTip;

    /**
     *  The class to use for creating ToolTips.
     *  
     *  @default mx.controls.ToolTip
     */
    public function get toolTipClass():Class 
    {
        return _toolTipClass;
    }
    
    /**
     *  @private
     */
    public function set toolTipClass(value:Class):void
    {
        _toolTipClass = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Initializes the class.
     *
     *  <p>This method sets up three Timer objects that ToolTipManager
     *  starts and stops while tracking the mouse.
     *  The repeatCount is set to 1 so that they fire only once.
     *  Their duration is set later, just before they are started.
     *  The timers are never destroyed once they are created here.</p>
     *
     *  <p>This method is called by targetChanged(); Flex waits to initialize
     *  the class until mouse-tracking happens in order to optimize
     *  startup time.</p>
     */
    mx_internal function initialize():void
    {
        if (!showTimer)
        {
            showTimer = new Timer(0, 1);
            showTimer.addEventListener(TimerEvent.TIMER,
                                       showTimer_timerHandler);
        }

        if (!hideTimer)
        {
            hideTimer = new Timer(0, 1);
            hideTimer.addEventListener(TimerEvent.TIMER,
                                       hideTimer_timerHandler);
        }

        if (!scrubTimer)
            scrubTimer = new Timer(0, 1);

        initialized = true;
    }

    /**
     *  Registers a target UIComponent or UITextField, and the text
     *  for its ToolTip, with the ToolTipManager.
     *  This causes the ToolTipManager to display a ToolTip
     *  when the mouse hovers over the target.
     *
     *  <p>This method is called by the setter
     *  for the toolTip property in UIComponent and UITextField.</p>
     *
     *  @param target The UIComponent or UITextField that owns the ToolTip.
     *
     *  @param oldToolTip The old text that was displayed
     *  in the ToolTip.
     * 
     *  @param newToolTip The new text to display in the ToolTip.
     *  If null, no ToolTip will be displayed when the mouse hovers
     *  over the target.
     */
    public function registerToolTip(target:DisplayObject,
                                    oldToolTip:String,
									newToolTip:String):void
    {
        if (!oldToolTip && newToolTip)
        {
            target.addEventListener(MouseEvent.MOUSE_OVER,
                                    toolTipMouseOverHandler);
            target.addEventListener(MouseEvent.MOUSE_OUT,
                                    toolTipMouseOutHandler);
                                    
            // If the mouse is already over the object
            // that's getting a toolTip, show the tip.
            if (mouseIsOver(target))
            	showImmediately(target);
        }
        else if (oldToolTip && !newToolTip)
        {
            target.removeEventListener(MouseEvent.MOUSE_OVER,
                                       toolTipMouseOverHandler);
            target.removeEventListener(MouseEvent.MOUSE_OUT,
                                       toolTipMouseOutHandler);
            
            // If the mouse is over the object whose toolTip
            // is being removed, hide the tip.
            if (mouseIsOver(target))
            	hideImmediately(target);
        }
    }

    /**
     *  Registers a target UIComponent, and the text
     *  for its error tip, with the ToolTipManager.
     *  This causes the ToolTipManager to display an error tip
     *  when the mouse hovers over the target.
     *
     *  <p>This method is called by the setter
     *  for the errorString property in UIComponent.</p>
     *
     *  @param target The UIComponent or UITextField that owns the ToolTip.
     * 
     *  @param oldErrorString The old text that was displayed
     *  in the error tip.
     *
     *  @param newErrorString The new text to display in the error tip.
     *  If null, no error tip will be displayed when the mouse hovers
     *  over the target.
     */
    public function registerErrorString(target:DisplayObject,
                                        oldErrorString:String,
										newErrorString:String):void
    {
        if (!oldErrorString && newErrorString)
        {
            target.addEventListener(MouseEvent.MOUSE_OVER,
                                    errorTipMouseOverHandler);
            target.addEventListener(MouseEvent.MOUSE_OUT,
                                    errorTipMouseOutHandler);
                                    
            // If the mouse is already over the object
            // that's getting an errorTip, show the tip.
            if (mouseIsOver(target))
            	showImmediately(target);
        }
        else if (oldErrorString && !newErrorString)
        {
            target.removeEventListener(MouseEvent.MOUSE_OVER,
                                       errorTipMouseOverHandler);
            target.removeEventListener(MouseEvent.MOUSE_OUT,
                                       errorTipMouseOutHandler);
            
            // If the mouse is over the object whose toolTip
            // is being removed, hide the tip.
            if (mouseIsOver(target))
            	hideImmediately(target);
        }
    }
    
    /**
     *  @private
     *  Returns true if the mouse is over the specified target.
     */
    private function mouseIsOver(target:DisplayObject):Boolean
    {
    	if (!target || !target.stage)
    		return false;
    		
    	//SDK:13465 - If we pass through the above if block, then
    	//we have a target component and its been added to the 
    	//display list. If the mouse coordinates are (0,0), there 
    	//is a chance the component has not been positioned yet 
    	//and we'll end up mistakenly showing tooltips since the 
    	//target hitTest will return true. 
    	if ((target.stage.mouseX == 0)	 && (target.stage.mouseY == 0))
    		return false;
    		
    	return target.hitTestPoint(target.stage.mouseX,
    							   target.stage.mouseY, true);
    }
    
    /**
     *  @private
     *  Shows the tip immediately when the toolTip or errorTip property 
     *  becomes non-null and the mouse is over the target.
     */
    private function showImmediately(target:DisplayObject):void
    {
    	var oldShowDelay:Number = ToolTipManager.showDelay;
    	ToolTipManager.showDelay = 0;
    	checkIfTargetChanged(target);
    	ToolTipManager.showDelay = oldShowDelay;
    }

    /**
     *  @private
     *  Hides the tip immediately when the toolTip or errorTip property 
     *  becomes null and the mouse is over the target.
     */
    private function hideImmediately(target:DisplayObject):void
    {
    	checkIfTargetChanged(null);
    }

    /**
     *  Replaces the ToolTip, if necessary.
     *
     *  <p>Determines whether the UIComponent or UITextField object
     *  with the ToolTip assigned to it that is currently under the mouse
     *  pointer is the most recent such object.
     *  If not, it removes the old ToolTip and displays the new one.</p>
     *
     *  @param displayObject The UIComponent or UITextField that is currently under the mouse.
     */
    mx_internal function checkIfTargetChanged(displayObject:DisplayObject):void
    {
        if (!enabled)
            return;

        findTarget(displayObject);
		
        if (currentTarget != previousTarget)
        {
            targetChanged();
            previousTarget = currentTarget;
        }
    }

    /**
     *  Searches from the <code>displayObject</code> object up the chain
     *  of parent objects until it finds a UIComponent or UITextField object
     *  with a <code>toolTip</code> or <code>errorString</code> property.
     *  Treats an empty string as a valid <code>toolTip</code> property.
     *  Sets the <code>currentTarget</code> property.
     */
    mx_internal function findTarget(displayObject:DisplayObject):void
    {
        // Walk up the DisplayObject parent chain looking for a UIComponent
        // with a toolTip or errorString property. Note that we stop
        // even if we find a tooltip which is an empty string. Although
        // we don't display empty tooltips, we have to track when we
        // are over a movieclip with an empty tooltip so that we can
        // hide any previous tooltip. This allows a child to set
        // toolTip="" to "cancel" its parent's toolTip.
        while (displayObject)
        {
            if (displayObject is IValidatorListener)
            {
                currentText = IValidatorListener(displayObject).errorString;
                if (currentText != null && currentText != "")
                {
                    currentTarget = displayObject;
                    isError = true;
                    return;
                }
            }

            if (displayObject is IToolTipManagerClient)
            {
                currentText = IToolTipManagerClient(displayObject).toolTip;
                if (currentText != null)
                {
                    currentTarget = displayObject;
                    isError = false;
                    return;
                }
            }

            displayObject = displayObject.parent;
        }

        currentText = null;
        currentTarget = null;
    }

    /**
     *  Removes any ToolTip that is currently displayed and displays
     *  the ToolTip for the UIComponent that is currently under the mouse
     *  pointer, as determined by the <code>currentTarget</code> property.
     */
    mx_internal function targetChanged():void
    {
        // Do lazy creation of the Timer objects this class uses.
        if (!initialized)
            initialize()

        var event:ToolTipEvent;
        
        if (previousTarget && currentToolTip)
        {
            event = new ToolTipEvent(ToolTipEvent.TOOL_TIP_HIDE);
            event.toolTip = currentToolTip;
            previousTarget.dispatchEvent(event);
        }   
            
        reset();

        if (currentTarget)
        {
            // Don't display empty tooltips.
            if (currentText == "")
                return;

            // Dispatch a "startToolTip" event
            // from the object displaying the tooltip.
            event = new ToolTipEvent(ToolTipEvent.TOOL_TIP_START);
            currentTarget.dispatchEvent(event);

            if (showDelay == 0 || scrubTimer.running)
            {
                // Create the tooltip and start its showEffect.
                createTip();
                initializeTip();
                positionTip();
                showTip();
            }
            else
            {
                showTimer.delay = showDelay;
                showTimer.start();
                // After the delay, showTimer_timerHandler()
                // will create the tooltip and start its showEffect.
            }
        }
    }

    /**
     *  Creates an invisible new ToolTip.
     *
     *  <p>If the ToolTipManager's <code>enabled</code> property is
     *  <code>true</code> this method is automatically called
     *  when the user moves the mouse over an object that has
     *  the <code>toolTip</code> property set,
     *  The ToolTipManager makes subsequent calls to
     *  <code>initializeTip()</code>, <code>positionTip()</code>,
     *  and <code>showTip()</code> to complete the display
     *  of the ToolTip.</p>
     *
     *  <p>The type of ToolTip that is created is determined by the
     *  <code>toolTipClass</code> property.
     *  By default, this is the ToolTip class.
     *  This class can be styled to appear as either a normal ToolTip
     *  (which has a yellow background by default) or as an error tip
     *  for validation errors (which is red by default).</p>
     *
     *  <p>After creating the ToolTip with the <code>new</code>
     *  operator, this method stores a reference to it in the
     *  <code>currentToolTip</code> property.
     *  It then uses addChild() to add this ToolTip to the
     *  SystemManager's toolTips layer.</p>
     */
    mx_internal function createTip():void
    {
        // Dispatch a "createToolTip" event
        // from the object displaying the tooltip.
        var event:ToolTipEvent =
            new ToolTipEvent(ToolTipEvent.TOOL_TIP_CREATE);
        currentTarget.dispatchEvent(event);

        if (event.toolTip)
            currentToolTip = event.toolTip;
        else
            currentToolTip = new toolTipClass();

        currentToolTip.visible = false;

        var sm:ISystemManager = getSystemManager(currentTarget);
        sm.toolTipChildren.addChild(DisplayObject(currentToolTip));
    }

    /**
     *  Initializes a newly created ToolTip with the appropriate text,
     *  based on the object under the mouse.
     *
     *  <p>If the ToolTipManager's <code>enabled</code> property is
     *  <code>true</code> this method is automatically called
     *  when the user moves the mouse over an object that has
     *  the <code>toolTip</code> property set.
     *  The ToolTipManager calls <code>createTip()</code> before
     *  this method, and <code>positionTip()</code> and
     *  <code>showTip()</code> after.</p>
     *
     *  <p>If a normal ToolTip is being displayed, this method
     *  sets its text as specified by the <code>toolTip</code>
     *  property of the object under the mouse.
     *  If an error tip is being displayed, the text is as
     *  specified by the <code>errorString</code> property
     *  of the object under the mouse.</p>
     *
     *  <p>This method also makes the ToolTip the appropriate
     *  size for the text that it needs to display.</p>
     */
    mx_internal function initializeTip():void
    {
        // Set the text of the tooltip.
        if (currentToolTip is ToolTip)
            ToolTip(currentToolTip).text = currentText;

        if (isError && currentToolTip is IStyleClient)
            IStyleClient(currentToolTip).setStyle("styleName", "errorTip");
        
        sizeTip(currentToolTip);

        if (currentToolTip is IStyleClient)
        {
            // Set up its "show" and "hide" effects.
            if (showEffect)
                IStyleClient(currentToolTip).setStyle("showEffect", showEffect);
            if (hideEffect)
                IStyleClient(currentToolTip).setStyle("hideEffect", hideEffect);
        }

        if (showEffect || hideEffect)
        {
            currentToolTip.addEventListener(EffectEvent.EFFECT_END,
                                            effectEndHandler);
        }
    }

    /**
     *  @private
     *  Objects added to the SystemManager's ToolTip layer don't get
     *  automatically measured or sized, so ToolTipManager has to
     *  measure it and set its size.
     */
    public function sizeTip(toolTip:IToolTip):void
    {
        // Force measure() to be called on the tooltip.
        // Otherwise, its measured size will be 0.
        if (toolTip is IInvalidating)
            IInvalidating(toolTip).validateNow();
        
        toolTip.setActualSize(
            toolTip.getExplicitOrMeasuredWidth(),
            toolTip.getExplicitOrMeasuredHeight());
    }

    /**
     *  Positions a newly created and initialized ToolTip on the stage.
     *
     *  <p>If the ToolTipManager's <code>enabled</code> property is
     *  <code>true</code> this method is automatically called
     *  when the user moves the mouse over an object that has
     *  the <code>toolTip</code> property set.
     *  The ToolTipManager calls <code>createTip()</code> and
     *  <code>initializeTip()</code> before this method,
     *  and <code>showTip()</code> after.</p>
     *
     *  <p>If a normal ToolTip is being displayed, this method positions
     *  its upper-left corner near the lower-right of the arrow cursor.
     *  This method ensures that the ToolTip is completely in view.
     */
    mx_internal function positionTip():void
    {
        var x:Number;
        var y:Number;

        var screenWidth:Number = currentToolTip.screen.width;
        var screenHeight:Number = currentToolTip.screen.height;

        if (isError)
        {
            var targetGlobalBounds:Rectangle = getGlobalBounds(currentTarget);

            x = targetGlobalBounds.right + 4;
            y = targetGlobalBounds.top - 1;

            // If there's no room to the right of the control, put it above
            // or below, with the left edge of the error tip aligned with
            // the left edge of the target.
            if (x + currentToolTip.width > screenWidth)
            {
                var newWidth:Number = NaN;
                var oldWidth:Number = NaN;

                x = targetGlobalBounds.left - 2;

                // If the error tip would be too wide for the stage,
                // reduce the maximum width to fit onstage. Note that
                // we have to reassign the text in order to get the tip
                // to relayout after changing the border style and maxWidth.
                if (x + currentToolTip.width + 4 > screenWidth)
                {
                    newWidth = screenWidth - x - 4;
                    oldWidth = Object(toolTipClass).maxWidth;
                    Object(toolTipClass).maxWidth = newWidth;
                    if (currentToolTip is IStyleClient)
                        IStyleClient(currentToolTip).setStyle("borderStyle", "errorTipAbove");
                    currentToolTip["text"] = currentToolTip["text"];
                    Object(toolTipClass).maxWidth = oldWidth;
                }

                // Even if the error tip will fit onstage, we still need to
                // change the border style and get the error tip to relayout.
                else
                {
                    if (currentToolTip is IStyleClient)
                        IStyleClient(currentToolTip).setStyle("borderStyle", "errorTipAbove");
                    currentToolTip["text"] = currentToolTip["text"];
                }

                if (currentToolTip.height + 2 < targetGlobalBounds.top)
                {
                    // There's room to put it above the control.
                    y = targetGlobalBounds.top - (currentToolTip.height + 2);
                }
                else
                {
                    // No room above, put it below the control.
                    y = targetGlobalBounds.bottom + 2;

                    if (!isNaN(newWidth))
                        Object(toolTipClass).maxWidth = newWidth;
                    if (currentToolTip is IStyleClient)
                        IStyleClient(currentToolTip).setStyle("borderStyle", "errorTipBelow");
                    currentToolTip["text"] = currentToolTip["text"];
                    if (!isNaN(oldWidth))
                        Object(toolTipClass).maxWidth = oldWidth;
                }
            }

            // Since the border style of the error tip may have changed,
            // we have to force a remeasurement and change its size.
            // This is because objects in the toolTips layer
            // don't undergo normal measurement and layout.
            sizeTip(currentToolTip)

            // We might be loaded and offset.
            var pos:Point = new Point(x, y);
            var ctt:IToolTip = currentToolTip;
            pos = DisplayObject(ctt).root.globalToLocal(pos);
            x = pos.x;
            y = pos.y;
        }
        else
        {
            // Position the upper-left of the tooltip
            // at the lower-right of the arrow cursor.
            x = ApplicationGlobals.application.mouseX + 11;
            y = ApplicationGlobals.application.mouseY + 22;

            // If the tooltip is too wide to fit onstage, move it left.
            var toolTipWidth:Number = currentToolTip.width;
            if (x + toolTipWidth > screenWidth)
                x = screenWidth - toolTipWidth;

            // If the tooltip is too tall to fit onstage, move it up.
            var toolTipHeight:Number = currentToolTip.height;
            if (y + toolTipHeight > screenHeight)
                y = screenHeight - toolTipHeight;
        }

        currentToolTip.move(x, y);
    }

    /**
     *  Shows a newly created, initialized, and positioned ToolTip.
     *
     *  <p>If the ToolTipManager's <code>enabled</code> property is
     *  <code>true</code> this method is automatically called
     *  when the user moves the mouse over an object that has
     *  the <code>toolTip</code> property set.
     *  The ToolTipManager calls <code>createTip()</code>,
     *  <code>initializeTip()</code>, and <code>positionTip()</code>
     *  before this method.</p>
     *
     *  <p>This method first dispatches a <code>"showToolTip"</code>
     *  event from the object under the mouse.
     *  This gives you a chance to do special processing on a
     *  particular object's ToolTip just before it becomes visible.
     *  It then makes the ToolTip visible, which triggers
     *  the ToolTipManager's <code>showEffect</code> if one is specified.
     */
    mx_internal function showTip():void
    {
        // Dispatch a "showToolTip" event
        // from the object displaying the tooltip.
        var event:ToolTipEvent =
            new ToolTipEvent(ToolTipEvent.TOOL_TIP_SHOW);
        event.toolTip = currentToolTip;
        currentTarget.dispatchEvent(event);

        if (isError)
        {
            // Listen for a change event so we know when to hide the tip
            currentTarget.addEventListener("change", changeHandler);
        }
        else
        {
            var sm:ISystemManager = getSystemManager(currentTarget);
            sm.addEventListener(MouseEvent.MOUSE_DOWN,
                                systemManager_mouseDownHandler);
        }

        // Make the tooltip visible.
        // If showEffect exists, this effect will play.
        // When the effect ends, effectEndHandler()
        // will start the hideTimer.
        currentToolTip.visible = true;

        if (!showEffect)
            showEffectEnded();
    }

    /**
     *  Hides the current ToolTip.
     */
    mx_internal function hideTip():void
    {
        // Dispatch a "hideToolTip" event
        // from the object that was displaying the tooltip.
        if (previousTarget)
        {
            var event:ToolTipEvent =
                new ToolTipEvent(ToolTipEvent.TOOL_TIP_HIDE);
            event.toolTip = currentToolTip;
            previousTarget.dispatchEvent(event);
        }

        // Make the tooltip invisible.
        // If hideEffect exists, this effect will play.
        // When the effect ends, effectEndHandler()
        // will reset the ToolTipManager to a no-tip state.
        if (currentToolTip)
            currentToolTip.visible = false;

        // When to do this?
        if (isError)
        {
            if (currentTarget)
                currentTarget.removeEventListener("change", changeHandler);
        }
        else
        {
            if (previousTarget)
            {
                var sm:ISystemManager = getSystemManager(previousTarget);
                sm.removeEventListener(MouseEvent.MOUSE_DOWN,
                                       systemManager_mouseDownHandler);
            }
        }

        if (!hideEffect)
            hideEffectEnded();
    }

    /**
     *  Removes any currently visible ToolTip.
     *  If the ToolTip is starting to show or hide, this method
     *  removes the ToolTip immediately without completing the effect.
     */
    mx_internal function reset():void
    {
        // Reset the three timers, in case any are running.
        showTimer.reset();
        hideTimer.reset();

        // If there is a current tooltip...
        if (currentToolTip)
        {
            // Remove the event handlers for the effectEnd of the showEffect
            // and hideEffect, so that calling endEffectsForTarget() doesn't
            // trigger effectEndHandler().
            if (showEffect || hideEffect)
            {
                currentToolTip.removeEventListener(EffectEvent.EFFECT_END,
                                                   effectEndHandler);
            }

            // End any show or hide effects that might be playing on it.
            EffectManager.endEffectsForTarget(currentToolTip);

            // Remove it.
            var sm:ISystemManager = currentToolTip.systemManager;
            sm.toolTipChildren.removeChild(DisplayObject(currentToolTip));
            currentToolTip = null;

            scrubTimer.delay = scrubDelay;
            scrubTimer.reset();
            if (scrubDelay > 0)
			{
                scrubTimer.delay = scrubDelay;
                scrubTimer.start();
			}
        }
    }

    /**
     *  Creates an instance of the ToolTip class with the specified text
     *  and displays it at the specified location in stage coordinates.
     *
     *  <p>ToolTips appear in their own layer, on top of everything
     *  except cursors.</p>
     *
     *  <p>The standard way of using ToolTips is to let the ToolTipManager
     *  automatically show and hide them as the user moves the mouse over
     *  the objects that have the <code>toolTip</code> property set.
     *  You can turn off this automatic ToolTip management by setting
     *  the ToolTipManager's <code>enabled</code> property to
     *  <code>false</code>.</p>
     *
     *  <p>By contrast, this method&#x2014;along with <code>hideToolTip()</code>&#x2014;gives 
     *  you programmatic control over ToolTips.
     *  You can show them when and where you choose,
     *  and you can even show more than one at once if you need to.
     *  (The ToolTipManager never does this, because it is generally
     *  confusing to the user).</p>
     *
     *  <p>This method first creates a new instance of ToolTip and calls the 
     *  <code>addChild()</code> method to put it into the SystemManager's
     *  toolTips layer.
     *  If you are showing an error tip, it sets the appropriate styles.
     *  Then it sets the text for the ToolTip, sizes the ToolTip based on
     *  its text, and positions it where you specified.</p>
     *
     *  <p>You must save the reference to the ToolTip that this method
     *  returns so that you can pass it to the <code>hideToolTip()</code> method.</p>
     *
     *  @param text The text to display in the ToolTip instance.
     *
     *  @param x The horizontal coordinate of the ToolTip in stage coordinates.
     *  In case of multiple stages, the relevant stage is determined
     *  from the <code>context</code> argument.
     *
     *  @param y The vertical coordinate of the ToolTip in stage coordinates.
     *  In case of multiple stages, the relevant stage is determined
     *  from the <code>context</code> argument.
     *
     *  @param errorTipBorderStyle The border style of an error tip. This method 
     *  argument can be null, "errorTipRight", "errorTipAbove", or "errorTipBelow". 
     *  If it is null, then the <code>createToolTip()</code> method creates a normal ToolTip. If it is 
     *  "errorTipRight", "errorTipAbove", or "errorTipBelow", then the <code>createToolTip()</code> 
     *  method creates an error tip, and this parameter determines where the arrow 
     *  of the error tip points to (the error's target). For example, if you pass "errorTipRight", Flex 
     *  positions the error tip (via the x and y arguments) to the 
     *  right of the error target; the arrow is on the left edge of the error tip.
     *
     *  @param context This property is not currently used.
     *
     *  @return The newly created ToolTip.
     *
     */
    public function createToolTip(text:String, x:Number, y:Number,
                                         errorTipBorderStyle:String = null,
                                         context:IUIComponent = null):IToolTip
    {
        var toolTip:ToolTip = new ToolTip();

        var sm:ISystemManager = context ?
                                context.systemManager :
                                ApplicationGlobals.application.systemManager;
        sm.toolTipChildren.addChild(toolTip);

        if (errorTipBorderStyle)
        {
            toolTip.setStyle("styleName", "errorTip");
            toolTip.setStyle("borderStyle", errorTipBorderStyle);
        }

        toolTip.text = text;

        sizeTip(toolTip);

        toolTip.move(x, y);
        // Ensure that tip is on screen?
        // Should x and y for error tip be tip of pointy border?

        // show effect?

        return toolTip as IToolTip;
    }

    /**
     *  Destroys a specified ToolTip that was created by the <code>createToolTip()</code> method.
     *
     *  <p>This method calls the <code>removeChild()</code> method to remove the specified
     *  ToolTip from the SystemManager's ToolTips layer.
     *  It will then be garbage-collected unless you keep a
     *  reference to it.</p>
     *
     *  <p>You should not call this method on the ToolTipManager's
     *  <code>currentToolTip</code>.</p>
     *
     *  @param toolTip The ToolTip instance to destroy.
     */
    public function destroyToolTip(toolTip:IToolTip):void
    {
        var sm:ISystemManager = toolTip.systemManager;
        sm.toolTipChildren.removeChild(DisplayObject(toolTip));

        // hide effect?
    }

    /**
     *  @private
     */
    mx_internal function showEffectEnded():void
    {
        if (hideDelay == 0)
        {
            hideTip();
        }
        else if (hideDelay < Infinity)
        {
            hideTimer.delay = hideDelay;
            hideTimer.start();
        }
        if (currentTarget)
        {
            // Dispatch a "toolTipShown" event
            // from the object displaying the tooltip.
            var event:ToolTipEvent =
                new ToolTipEvent(ToolTipEvent.TOOL_TIP_SHOWN);
            event.toolTip = currentToolTip;
            currentTarget.dispatchEvent(event);
        }
    }

    /**
     *  @private
     */
    mx_internal function hideEffectEnded():void
    {
        reset();
        // Dispatch a "toolTipEnd" event
        // from the object that was displaying the tooltip.
        if (previousTarget)
        {
            var event:ToolTipEvent =
                new ToolTipEvent(ToolTipEvent.TOOL_TIP_END);
            event.toolTip = currentToolTip;
            previousTarget.dispatchEvent(event);
        }
    }

    /**
     *  @private
     */
     private function getSystemManager(
                                    target:DisplayObject):ISystemManager
     {
        return target is IUIComponent ?
               IUIComponent(target).systemManager :
               null;
     }

    /**
     *  @private
     */
    private function getGlobalBounds(obj:DisplayObject):Rectangle
    {
        var upperLeft:Point = new Point(0, 0);
        upperLeft = obj.localToGlobal(upperLeft);
        return new Rectangle(upperLeft.x, upperLeft.y, obj.width, obj.height);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This handler is called when the mouse moves over an object
     *  with a toolTip.
     */
    mx_internal function toolTipMouseOverHandler(event:MouseEvent):void
    {
        checkIfTargetChanged(DisplayObject(event.target));
    }

    /**
     *  @private
     *  This handler is called when the mouse moves out of an object
     *  with a toolTip.
     */
    mx_internal function toolTipMouseOutHandler(event:MouseEvent):void
    {
        checkIfTargetChanged(event.relatedObject);
    }

    /**
     *  @private
     *  This handler is called when the mouse moves over an object
     *  with an errorString.
     */
    mx_internal function errorTipMouseOverHandler(event:MouseEvent):void
    {
        checkIfTargetChanged(DisplayObject(event.target));
    }

    /**
     *  @private
     *  This handler is called when the mouse moves out of an object
     *  with an errorString.
     */
    mx_internal function errorTipMouseOutHandler(event:MouseEvent):void
    {
        checkIfTargetChanged(event.relatedObject);
    }

    /**
     *  @private
     *  This handler is called when the showTimer fires.
     *  It creates the tooltip and starts its showEffect.
     */
    mx_internal function showTimer_timerHandler(event:TimerEvent):void
    {
        // Make sure we still have a currentTarget when the timer fires.
        if (currentTarget)
        {
            createTip();
            initializeTip();
            positionTip();
            showTip();
        }
    }

    /**
     *  @private
     *  This handler is called when the hideTimer fires.
     *  It starts the hideEffect.
     */
    mx_internal function hideTimer_timerHandler(event:TimerEvent):void
    {
        hideTip();
    }

    /**
     *  @private
     *  This handler is called when the showEffect or hideEffect ends.
     *  When the showEffect ends, it starts the hideTimer,
     *  which will automatically start hiding the tooltip when it fires,
     *  even if the mouse is still over the target.
     *  When the hideEffect ends, the tooltip is removed.
     */
    mx_internal function effectEndHandler(event:EffectEvent):void
    {
        if (event.effectInstance.effect == showEffect)
            showEffectEnded();
        else if (event.effectInstance.effect == hideEffect)
            hideEffectEnded();
    }

    /**
     *  @private
     *  This handler is called when the user clicks the mouse
     *  while a normal tooltip is displayed.
     *  It immediately hides the tooltip.
     */
    mx_internal function systemManager_mouseDownHandler(event:MouseEvent):void
    {
        reset();
    }

    /**
     *  @private
     */
    mx_internal function changeHandler(event:Event):void
    {
        reset();
    }
}

}
