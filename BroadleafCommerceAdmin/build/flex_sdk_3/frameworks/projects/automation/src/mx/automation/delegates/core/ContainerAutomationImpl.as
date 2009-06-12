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
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.tabularData.ContainerTabularData;
import mx.containers.ApplicationControlBar;
import mx.core.Application;
import mx.core.Container;
import mx.core.EventPriority;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDetail;
import mx.events.ScrollEventDirection;
import mx.core.mx_internal;
use namespace mx_internal;



[Mixin]
/**
 * 
 *  Defines the methods and properties required to perform instrumentation for the 
 *  Container class. 
 * 
 *  @see mx.core.Container
 *  
 */
public class ContainerAutomationImpl extends UIComponentAutomationImpl
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
        Automation.registerDelegateClass(Container, ContainerAutomationImpl);
    }   
    
    /**
     *  Constructor.
     * @param obj Container object to be automated.     
     */
    public function ContainerAutomationImpl(obj:Container)
    {
        super(obj);
    
        obj.addEventListener(ScrollEvent.SCROLL, scroll_eventHandler, false, 0, true);
        obj.addEventListener(MouseEvent.MOUSE_WHEEL, mouseWheelHandler,
                false, EventPriority.DEFAULT+1, true );
    }

    /**
     *  @private
     */
    private function get container():Container
    {
        return uiComponent as Container;
    }

    /**
     *  @private
     *  Holds the previous scroll event object. This is used to prevent recording
     *  multiple scroll events.
     */
    private var previousEvent:ScrollEvent;
    
    /**
     *  @private
     *  Flag used to control recording of scroll events.
     *  MouseWheel events are recorded as they are handled specially by the containers.
     *  The scrollEvent generated doesnot contain proper information for playback. Hence
     *  we record and playback mouseWheel events.
     */
    private var skipScrollEvent:Boolean = false;
    
    //----------------------------------
    //  automationName
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationName():String
    {
        return container.label || super.automationName;
    }

    //----------------------------------
    //  automationValue
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        if (container.label && container.label.length != 0)
            return [ container.label ];
        
        var result:Array = [];
        
        var n:int = numAutomationChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:IAutomationObject = getAutomationChildAt(i);
            var x:Array = child.automationValue;
            if (x && x.length != 0)
                result.push(x);
        }
        
        return result;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        if (event is ScrollEvent)
        {
            var scrollEvent:ScrollEvent = new ScrollEvent(ScrollEvent.SCROLL);
            
            var se:ScrollEvent = ScrollEvent(event);
            
            var vReplayer:IAutomationObject = (container.verticalScrollBar as IAutomationObject);
            var hReplayer:IAutomationObject = (container.horizontalScrollBar as IAutomationObject);
            if (se.direction == ScrollEventDirection.VERTICAL && vReplayer)
            {
                vReplayer.replayAutomatableEvent(se);
            }
            else if (se.direction == ScrollEventDirection.HORIZONTAL && hReplayer)
            {
                hReplayer.replayAutomatableEvent(se);
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
    
    /**
     *  @private
     */
    override public function createAutomationIDPart(child:IAutomationObject):Object
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help.helpCreateIDPart(uiAutomationObject, child);
    }

    /**
     *  @private
     */
    override public function resolveAutomationIDPart(part:Object):Array
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help.helpResolveIDPart(uiAutomationObject, part);
    }



    /**
     *  @private
     */
    //----------------------------------
    //  numAutomationChildren
    //----------------------------------
      /* this method is written to get the docked application control bars separately
     as they are not part of the numChildren and get childAt.
     but we need these objcts as part of them to get the event from these
     properly recorded
     */
    private function getDockedApplicationControlBarCount():int
    {
        var dockedApplicationControlBars:int = 0;
        
        // number of docked application control bars
        if(container is Application)
        {
            // get its row children and see how many docked application control 
            // bars are present
            var rowChildrenCount:int = container.rawChildren.numChildren;
            for ( var index:int=0 ;index < rowChildrenCount; index++)
            {
                var currentObject:ApplicationControlBar = 
                    container.rawChildren.getChildAt(index) as ApplicationControlBar;
                if( currentObject)
                {
                    if(currentObject.dock == true)
                    {
                        dockedApplicationControlBars++;
                    }
                }
            }
        }
        
        return dockedApplicationControlBars;
    }
    /**
     *  @private
     */
   
    override public function get numAutomationChildren():int
    {
        
        
        return container.numChildren + container.numRepeaters + getDockedApplicationControlBarCount();
    }
    
    /**
     *  @private
     */
    private function getDockedControlBar(index:int):IAutomationObject
    {
        var dockedApplicationControlBarsFound:int = 0;
        
        // number of docked application control bars
        if(container is Application)
        {
            // get its row children and see how many docked application control 
            // bars are present
            var rowChildrenCount:int = container.rawChildren.numChildren;
            for ( var childPos:int=0 ;childPos < rowChildrenCount; childPos++)
            {
                var currentObject:ApplicationControlBar = 
                    container.rawChildren.getChildAt(childPos) as ApplicationControlBar;
                if( currentObject)
                {
                    if(currentObject.dock == true)
                    {
                        if(dockedApplicationControlBarsFound == index)
                        {
                            return currentObject as IAutomationObject;
                        }
                        else
                        {
                            dockedApplicationControlBarsFound++;
                        }
                    }
                }
            }
        }
        return null;
    }
    /**
     *  @private
    */
   
     override public function getAutomationChildAt(index:int):IAutomationObject
    {
        var dockedApplicationBarNumbers:int = getDockedApplicationControlBarCount();
        if(index < dockedApplicationBarNumbers)
        {
            return (getDockedControlBar(index) as IAutomationObject);
        }
        else
        {
            index = index - dockedApplicationBarNumbers;
        }
        if (index < container.numChildren)
        {
            var d:Object = container.getChildAt(index);
            return d as IAutomationObject;
        }   
        
        var r:Object = container.childRepeaters[index - container.numChildren];
        return r as IAutomationObject;
    }
    
    
    //----------------------------------
    //  automationTabularData
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationTabularData():Object
    {
        return new ContainerTabularData(uiAutomationObject);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function scroll_eventHandler(event:ScrollEvent):void
    {
        // we skip recording a scroll event if a mouse wheel
        // event has been recorded
        if (skipScrollEvent)
        {
            skipScrollEvent = false;
            return;
        }   
        if(event.detail == ScrollEventDetail.THUMB_TRACK)
            return;
        // the checks have been added to prevent multiple recording
        // of the same scroll event
        if(!previousEvent || (event.delta && previousEvent.delta != event.delta) ||
             previousEvent.detail != event.detail ||
             previousEvent.direction != event.direction ||
             previousEvent.position != event.position ||
             previousEvent.type != event.type)
        {
            recordAutomatableEvent(event);
            previousEvent = event.clone() as ScrollEvent;
        }
    }
    
    /**
     *  @private
     */
     private function mouseWheelHandler(event:MouseEvent):void
     {
        skipScrollEvent = true;
        if(event.target == uiComponent)
        {   
            recordAutomatableEvent(event, true);
        }
     }
    
}

}