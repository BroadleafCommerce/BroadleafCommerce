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
import mx.automation.Automation;
import mx.automation.IAutomationObjectHelper;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.DateChooser;
import mx.core.mx_internal;
import mx.events.CalendarLayoutChangeEvent;
import mx.events.DateChooserEvent;
import mx.events.DateChooserEventDetail;
import mx.events.FlexEvent;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  DateChooser control.
 * 
 *  @see mx.controls.DateChooser 
 *
 */
public class DateChooserAutomationImpl extends UIComponentAutomationImpl {

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
        Automation.registerDelegateClass(DateChooser, DateChooserAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj DateChooser object to be automated.     
     */
    public function DateChooserAutomationImpl(obj:DateChooser)
    {
        super(obj);
        
        obj.addEventListener(CalendarLayoutChangeEvent.CHANGE,
                                  date_changeHandler, false, 0, true);
        obj.addEventListener(DateChooserEvent.SCROLL,
                                  date_scrollHandler, false, 0, true);
    }


    /**
     *  @private
     *  storage for the owner component
     */
    protected function get dateChooser():DateChooser
    {
        return uiComponent as DateChooser;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  automationValue
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        return  [ dateChooser.selectedDate ? dateChooser.selectedDate.toString() : "" ];
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Replays DateChooserChangeEvent.CHANGE and DateChooserEvent.SCROLL
     *  events. Replays change by simply setting the date to the one recorded.
     *  Replays scroll by clicking on the month forward or month back button
     *  depending on the direction of the scroll.
     *
     *  @param interaction The event to replay.
     * 
     *  @return Whether or not a replay was successful.
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        if (event is CalendarLayoutChangeEvent)
        {
            var newDate:Date = CalendarLayoutChangeEvent(event).newDate;
            dateChooser.selectedDate = newDate;
            dateChooser.validateProperties();
            dateChooser.dispatchEvent(event);
            return true;
        }
        else if (event is DateChooserEvent)
        {
            var scrollType:String = DateChooserEvent(event).detail;

            switch(scrollType)
            {
                case DateChooserEventDetail.PREVIOUS_MONTH:
                    help.replayClick(dateChooser.backMonthButton);
                    break;
                case DateChooserEventDetail.PREVIOUS_YEAR:
                    help.replayClick(dateChooser.downYearButton);
                    break;
                case DateChooserEventDetail.NEXT_MONTH:
                    help.replayClick(dateChooser.fwdMonthButton);   
                    break;
                case DateChooserEventDetail.NEXT_YEAR:
                    help.replayClick(dateChooser.upYearButton);
                    break;
            } 
            
            return true;
        }
        else if (event is KeyboardEvent)
            return help.replayKeyboardEvent(dateChooser.dateGrid, KeyboardEvent(event));
        else
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
    private function date_scrollHandler(event:DateChooserEvent):void
    {
        if (event.triggerEvent &&
            (event.triggerEvent is MouseEvent || event.triggerEvent.type == FlexEvent.BUTTON_DOWN))
        { 
            if(event.triggerEvent.target == dateChooser.fwdMonthButton)
                event.detail = DateChooserEventDetail.NEXT_MONTH;
            else if(event.triggerEvent.target == dateChooser.backMonthButton)
                event.detail = DateChooserEventDetail.PREVIOUS_MONTH;
            
            recordAutomatableEvent(event, false);
        }
        else if (event.triggerEvent is KeyboardEvent)
            recordAutomatableEvent(event.triggerEvent, false);
    }

    /**
     *  @private
     */
    private function date_changeHandler(event:CalendarLayoutChangeEvent):void
    {
        if (event.triggerEvent is MouseEvent)
            recordAutomatableEvent(event);
        else if (event.triggerEvent is KeyboardEvent)
            recordAutomatableEvent(event.triggerEvent, false);
    }

}
}