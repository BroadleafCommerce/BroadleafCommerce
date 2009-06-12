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

package mx.automation.delegates.core
{
import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.MouseEvent;

import mx.automation.Automation;
import mx.automation.IAutomationObjectHelper;
import mx.automation.events.AutomationRecordEvent;
import mx.controls.scrollClasses.ScrollBar;
import mx.core.EventPriority;
import mx.core.mx_internal;
import mx.core.ScrollControlBase;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDirection;
import mx.events.ScrollEventDetail;
import mx.automation.IAutomationObject;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines the methods and properties required to perform instrumentation for the 
 *  ScrollControlBase class. 
 * 
 *  @see mx.core.ScrollControlBase
 *  
 */
public class ScrollControlBaseAutomationImpl extends UIComponentAutomationImpl 
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
        Automation.registerDelegateClass(ScrollControlBase, ScrollControlBaseAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor.
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj ScrollControlBase object to be automated.         
     */
    public function ScrollControlBaseAutomationImpl(obj:ScrollControlBase)
    {
        super(obj);
        
        obj.addEventListener(Event.ADDED, childAddedHandler, false, 0, true);
        obj.addEventListener(MouseEvent.MOUSE_WHEEL, mouseScrollHandler, false, EventPriority.DEFAULT+1, true);
        
        addScrollRecordHandlers();
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    protected function get sBase():ScrollControlBase
    {
        return uiComponent as ScrollControlBase;
    }
    
    //--------------------------------------------------------------------------
    //  Overridden Methods
    //--------------------------------------------------------------------------

    /**
     * Replays ScrollEvents. ScrollEvents are replayed
     * by calling ScrollBar.scrollIt on the appropriate (horizontal or vertical)
     * scrollBar.
     *  
     *  @param event The event to replay.
     *  
     *  @return <code>true</code> if the replay was successful. Otherwise, returns <code>false</code>.     
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        if (event is ScrollEvent)
        {
            var se:ScrollEvent = ScrollEvent(event);

            var position:Number = se.position;
            var direction:String = se.direction;
            var delegate:IAutomationObject;

            switch (direction)
            {
                case ScrollEventDirection.VERTICAL:
                {
                    if (!(sBase.scroll_verticalScrollBar && sBase.scroll_verticalScrollBar.enabled))
                        return false;
                    delegate = sBase.scroll_verticalScrollBar as IAutomationObject;
                    delegate.replayAutomatableEvent(event);
                    break;
                }

                case ScrollEventDirection.HORIZONTAL:
                {
                    if (!(sBase.scroll_horizontalScrollBar && sBase.scroll_horizontalScrollBar.enabled))
                        return false;
//                  sBase.horizontalScrollPosition = position;
                    delegate = sBase.scroll_horizontalScrollBar as IAutomationObject;
                    delegate.replayAutomatableEvent(event);
                    break;
                }

                default:
                {
                    return false;
                }
            }
            return true;
        }
        else if (event is MouseEvent && event.type == MouseEvent.MOUSE_WHEEL)
        {
            var help:IAutomationObjectHelper = Automation.automationObjectHelper;
            help.replayMouseEvent(uiComponent, event as MouseEvent);
            return true;
        }

        return super.replayAutomatableEvent(event);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    private function childAddedHandler(ev:Event):void
    {
        if(ev.target is ScrollBar)
        {
            ev.target.addEventListener(AutomationRecordEvent.RECORD,
                                             scrollBar_recordHandler, false, 0, true);
        }
    }
    
    private function addScrollRecordHandlers():void
    {
        var scrollBar:ScrollBar = sBase.scroll_verticalScrollBar ;
        if(scrollBar)
            scrollBar.addEventListener(AutomationRecordEvent.RECORD,
                                         scrollBar_recordHandler, false, 0, true);
        scrollBar = sBase.scroll_horizontalScrollBar ;
        if(scrollBar)
            scrollBar.addEventListener(AutomationRecordEvent.RECORD,
                                         scrollBar_recordHandler, false, 0, true);
    }
    
    /**
     * @private
     */
    private function mouseScrollHandler(event:MouseEvent):void
    {
        recordAutomatableEvent(event);
    }

    /**
     *  @private
     */
    private function scrollBar_recordHandler(event:AutomationRecordEvent):void
    {
        if (event.automationObject == sBase.scroll_verticalScrollBar ||
            event.automationObject == sBase.scroll_horizontalScrollBar)
        {
            if (event.replayableEvent is ScrollEvent &&
                    ScrollEvent(event.replayableEvent).detail != ScrollEventDetail.THUMB_TRACK)
                recordAutomatableEvent(event.replayableEvent);
        }
    }
        
    }
}