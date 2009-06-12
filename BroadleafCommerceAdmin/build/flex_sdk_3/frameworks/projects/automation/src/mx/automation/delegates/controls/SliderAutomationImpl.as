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

package mx.automation.delegates.controls 
{
import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.ui.Keyboard;
import flash.utils.getTimer;

import mx.automation.Automation;
import mx.automation.IAutomationObjectHelper;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.sliderClasses.Slider;
import mx.controls.sliderClasses.SliderThumb;
import mx.core.mx_internal;
import mx.events.SliderEvent;
import mx.events.SliderEventClickTarget;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  Slider class, which is the parent of the HSlider and VSlider classes.
 * 
 *  @see mx.controls.sliderClasses.Slider
 *  @see mx.controls.HSlider 
 *  @see mx.controls.VSlider   
 *
 */
public class SliderAutomationImpl extends UIComponentAutomationImpl 
{
    include "../../../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------


    /**
     *  Registers the delegate class for a component class with automation manager.
     *  
     *  @param root The SystemManger of the application.
     */
    public static function init(root:DisplayObject):void
    {
        Automation.registerDelegateClass(Slider, SliderAutomationImpl);
    }   
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj Slider object to be automated.     
     */
    public function SliderAutomationImpl(obj:Slider)
    {
        super(obj);
        
        obj.addEventListener(SliderEvent.CHANGE, sliderChangeHandler, false, 0, true);
        obj.addEventListener(KeyboardEvent.KEY_DOWN, thumbKeyDownHandler, true, 0, true);
    }
    
    /**
     * @private
     */
    private var recentKeyCode:int ;
    
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get slider():Slider
    {
        return uiComponent as Slider;
    }
    
    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        //if slider has only one thumb, return only single value, which reflects screen better
        return [ "[" + ((slider.thumbCount == 1)? slider.value : slider.values.join(",")) + "]" ];
    }

    /**
     *  @private
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        if (event is SliderEvent)
        {
            var sliderEvent:SliderEvent = SliderEvent(event);
            var thumb:SliderThumb = slider.getThumbAt(sliderEvent.thumbIndex);

            if (sliderEvent.triggerEvent is KeyboardEvent)
            {
                if (slider.getFocus() != thumb)
                    help.replayClick(thumb);
                var ke:KeyboardEvent = new KeyboardEvent(KeyboardEvent.KEY_DOWN);
                ke.keyCode = sliderEvent.keyCode;

                help.replayKeyboardEvent(thumb, ke);

                return true;
            }
            else if (sliderEvent.triggerEvent is MouseEvent)
            {
                //We don't want to truly mimic the track or thumb click here, because that
                //will force the easingFunction to kick in or rely on the current position of the thumb
                //to calculate the new position. So we just set the value explicitly.
                //Of course, this makes me wonder if we need to be forcing events in the
                //Keyboard case above.
                if (sliderEvent.clickTarget == SliderEventClickTarget.THUMB)
                {
                    var me:MouseEvent = new MouseEvent(MouseEvent.MOUSE_DOWN);
                    help.replayMouseEvent(thumb, me);
                    thumb.xPosition = slider.getXFromValue(sliderEvent.value);
                    me = new MouseEvent(MouseEvent.MOUSE_UP);
                    help.replayMouseEvent(thumb, me);
                }
                else
                {
                    var completeTime:Number = getTimer() + slider.getStyle("slideDuration");
                    
                    help.addSynchronization(function():Boolean
                    {
                        return getTimer() >= completeTime;
                    });
                    
                    var x:Number = slider.getXFromValue(sliderEvent.value);
                    var ev:MouseEvent = new MouseEvent(MouseEvent.MOUSE_DOWN);
                    ev.localX = x;
                    ev.localY = 0;
                    help.replayMouseEvent(slider.getTrackHitArea(), ev);
                }
                return true;
            }
            else
                throw new Error();
        }
        return super.replayAutomatableEvent(event);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function sliderChangeHandler (event:SliderEvent):void 
    {
        // during initilization we get this event but clickTarget
        // will not be set. We need this information to do proper
        // recording.
        if (event.clickTarget)
        {
            if (event.triggerEvent is KeyboardEvent)
            {
                event.keyCode = recentKeyCode;
            }
            else
            {
                // when the event is not from the keyboard, the keycode is
                // comding as -1 in 3.0.0 sdk where as in 2.0.0 sdk it was coming as
                // 0. HOwever since the keycode is not important for mouse,and since -1 will cause the problem in the decoding
                //  make the keycode as 0
                // this is done to solve the bug FLEXENT-684
                if(event.keyCode == -1)
                {
                    event.keyCode=0;
                }
            }
                
           recordAutomatableEvent(event);
        }
    }
    
    /**
     *  @private
     *  Slider needs to record the key code if keyboard is used to change 
     *  the value. We save the most recent keycode here and use it in change record
     *  handler.
     */
    protected function thumbKeyDownHandler(event:KeyboardEvent):void
    {
        if (event.target is slider.sliderThumbClass && event.keyCode != Keyboard.ENTER)
            recentKeyCode = event.keyCode;
    }

        
}
}
