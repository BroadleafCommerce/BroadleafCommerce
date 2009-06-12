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

package mx.automation.delegates 
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.events.Event;
import flash.events.IEventDispatcher;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import mx.automation.Automation;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.automation.events.AutomationDragEvent;
import mx.automation.events.AutomationDragEventWithPositionInfo;
import mx.events.DragEvent;
import mx.managers.dragClasses.DragProxy;
import mx.managers.DragManager;
import mx.core.mx_internal;
import mx.core.IUIComponent;
import mx.core.UIComponent;
import flash.display.Graphics;
import flash.display.DisplayObject;
import flash.utils.*;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines the methods and properties required to perform instrumentation for the 
 *  DragManager class. 
 * 
 *  @see mx.managers.DragManager
 *  
 */
public class DragManagerAutomationImpl extends UIComponentAutomationImpl
{
    include "../../core/Version.as";

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
        Automation.registerDelegateClass(DragProxy, DragManagerAutomationImpl);
    }
    
    /**
     *  Constructor.
     *  
     *  @param proxy DragManager object to be automated.     
     */
    public function DragManagerAutomationImpl(proxy:UIComponent)
    {
        super(proxy);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var dragStarted:Boolean = false;
    
    /**
     *  @private
     */
    private var dragOwner:IAutomationObject;
    
    /**
     *  @private
     */
    public static var callBackBeforeDrop:Function;
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public static function toMouseEvent(type:String, dragEvent:AutomationDragEvent):MouseEvent
    {
        var result:MouseEvent = new MouseEvent(type);
        result.localX = dragEvent.localX;
        result.localY = dragEvent.localY;
        result.shiftKey = dragEvent.shiftKey;
        result.ctrlKey = dragEvent.ctrlKey;
        result.altKey = dragEvent.altKey;
        return result;
    }

    /**
     *  @private
     */
    public function getChildAutomationObject(target:IUIComponent,
                                                    mouseEvent:MouseEvent):IAutomationObject
    {
        var delegate:IAutomationObject = (target as IAutomationObject);
        //find the child of the target that is under the point
        if (!delegate || delegate.numAutomationChildren == 0)
            return null;

        //find the child of the target that is under the point
        //use local because stage will be empty if no target in the event
        var eventTarget:DisplayObject = (mouseEvent.target != null 
                                         ? DisplayObject(mouseEvent.target) 
                                         : DisplayObject(target));
        var ptGlobal:Point = new Point(mouseEvent.localX, mouseEvent.localY);
        ptGlobal = eventTarget.localToGlobal(ptGlobal);
        
        var numAChildren:int = delegate.numAutomationChildren;
        var highestChild:IAutomationObject = null;
        var highestChildIndex:int = -1;
        var childAO:IAutomationObject;
        var childDO:DisplayObject;
        var x:int;
        var y:int;
        var p:Point;
        
        for (var i:int = 0; i < numAChildren; ++i)
        {
            childAO = delegate.getAutomationChildAt(i);
            childDO = DisplayObject(childAO);

            x = childDO.x;
            y = childDO.y;
            p = new Point(x, y);
            p = childDO.parent.localToGlobal(p);
            p.x += childDO.width;
            p.y += childDO.height;
            
            if (childDO.visible 
                    && childDO.hitTestPoint(ptGlobal.x, ptGlobal.y))
            {
                var childIndex:int = childDO.parent.getChildIndex(childDO);
                if (highestChild == null || childIndex > highestChildIndex)
                {
                    highestChild = childAO;
                    highestChildIndex = childIndex;
                }
            }
        }
        
        if(!highestChild)
        {        
            for (i = 0; i < numAChildren; ++i)
            {
                childAO = delegate.getAutomationChildAt(i);
                childDO = DisplayObject(childAO);
    
                x = childDO.x;
                y = childDO.y;
                // adjust for scrollRect
                var ui:UIComponent = childDO as UIComponent;
                if(ui && ui.$parent.scrollRect)
                {
                    x -= ui.$parent.scrollRect.left;
                    y -= ui.$parent.scrollRect.top;
                }
                p = new Point(x, y);
                p = childDO.parent.localToGlobal(p);
                p.x += childDO.width;
                p.y += childDO.height;
                
                if (childDO.visible 
                        && ptGlobal.x < p.x
                        && ptGlobal.y < p.y) 
                {
                    highestChild = childAO;
                    break;
                }
            }
        }
       
        return highestChild;
    }

    /**
     *  @private
     */
    public function recordAutomatableDragStart(dragInitiator:IUIComponent, 
                                                       mouseEvent:MouseEvent):void
    {
        var delegate:IAutomationObject = (dragInitiator as IAutomationObject);
        if (!delegate)
            return;
        
        var am:IAutomationManager = Automation.automationManager;
        if (am && am.recording)
        {
            dragOwner = delegate;
            
            if(!delegate.automationDelegate.hasOwnProperty("isDragEventPositionBased") ||
                (delegate.automationDelegate).isDragEventPositionBased() == false)
            {
                var e:AutomationDragEvent = new AutomationDragEvent(AutomationDragEvent.DRAG_START);
                e.draggedItem = 
                    getChildAutomationObject(dragInitiator, mouseEvent);
                
                if (!e.draggedItem)
                {
                    e.draggedItem = dragInitiator as IAutomationObject;
                    dragOwner = (Automation.automationManager as IAutomationManager).getParent(delegate);
                }
                                
                am.recordAutomatableEvent(dragOwner, e, false);
            }
            else
            {
                var e1:AutomationDragEventWithPositionInfo = new AutomationDragEventWithPositionInfo(AutomationDragEvent.DRAG_START);
                e1.draggedItem = 
                    getChildAutomationObject(dragInitiator, mouseEvent);
                
                if (!e1.draggedItem)
                {
                    e1.draggedItem = dragInitiator as IAutomationObject;
                    dragOwner = (Automation.automationManager as IAutomationManager).getParent(delegate);
                }
            
                var localPoint:Point;
                localPoint= (delegate.automationDelegate).getLocalPoint(new Point(mouseEvent.localX, mouseEvent.localY),e1.draggedItem);
                e1.localX = localPoint.x;
                e1.localY = localPoint.y;
                am.recordAutomatableEvent(dragOwner, e1, false);
            }
            
            dragStarted = true;
        }
    }
    
    /**
     *  @private
     */
    public function recordAutomatableDragDrop(target:IUIComponent, 
                                                      dragEvent:DragEvent):void
    {
        if (!dragStarted)
            return;
        var delegate:IAutomationObject = (target as IAutomationObject);
        if (!delegate)
            return;

        var am:IAutomationManager = Automation.automationManager;
        if (am && am.recording)
        {
            if(!delegate.automationDelegate.hasOwnProperty("isDragEventPositionBased") ||
            (delegate.automationDelegate).isDragEventPositionBased() == false)
            {
                var e:AutomationDragEvent = new AutomationDragEvent(dragEvent.type);
                e.draggedItem = getChildAutomationObject(target, dragEvent);

                am.recordAutomatableEvent(delegate, e, false);
                dragStarted = false;
            }
            else
            {
                var e1:AutomationDragEvent = new  AutomationDragEventWithPositionInfo(dragEvent.type);
                e1.draggedItem = getChildAutomationObject(target, dragEvent);
                e1.localX = dragEvent.localX;
                e1.localY = dragEvent.localY;
                am.recordAutomatableEvent(delegate, e1, false);
                dragStarted = false;
            }
        }
    }
    
    /**
     *  @private
     */
    public function recordAutomatableDragCancel(target:IUIComponent, 
                                                        dragEvent:DragEvent):void
    {
        if (!dragStarted)
            return;
        var delegate:IAutomationObject = (target as IAutomationObject);
        if (!delegate)
            return;

        var am:IAutomationManager = Automation.automationManager;
        if (am && am.recording)
        {
            var e:AutomationDragEvent = new AutomationDragEvent(dragEvent.type);
            e.action = dragEvent.action;
            am.recordAutomatableEvent(dragOwner, e, false);
            dragStarted = false;
        }
    }

    //--------------------------------------------------------------------------
    //
    // Replay support
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    public static function replayAutomatableEvent(target:IAutomationObject,
                                                  interaction:Event):Boolean
    {
        if (! (interaction is AutomationDragEvent))
            return false;
   
        var mouseEvent:MouseEvent = null;   
        var dragEvent:AutomationDragEvent = AutomationDragEvent(interaction);
    
    
        var delegate:IAutomationObject = (dragEvent.draggedItem as IAutomationObject);
        if (!delegate)
            delegate = (target as IAutomationObject);
      
         
         var realTarget:IEventDispatcher = 
            IEventDispatcher(delegate);

        var help:IAutomationObjectHelper = Automation.automationObjectHelper;

        if (dragEvent.type == DragEvent.DRAG_START)
        {
            mouseEvent = toMouseEvent(MouseEvent.MOUSE_DOWN, dragEvent);
            mouseEvent.buttonDown = true;
            help.replayMouseEvent(realTarget, mouseEvent);

            //note the 10 pixel offset hack is some arbitrary amount to
            //make the component think the cursor has moved
            dragEvent.localX = -10;
            dragEvent.localY = -10;
                
            mouseEvent = toMouseEvent(MouseEvent.MOUSE_MOVE, dragEvent);
            mouseEvent.buttonDown = true;
            help.replayMouseEvent(realTarget, mouseEvent);

            mouseEvent = toMouseEvent(MouseEvent.MOUSE_OUT, dragEvent);
            mouseEvent.buttonDown = true;
            help.replayMouseEvent(realTarget, mouseEvent);
        }
        else if (dragEvent.type == DragEvent.DRAG_DROP)
        {
            if(!target.automationDelegate.hasOwnProperty("isDragEventPositionBased") ||
            (target.automationDelegate).isDragEventPositionBased() == false)
            {
                // if the drag event is position based, it has the coordinate 
                // information. else calculate centre of the container and reproduce
                // the mouse move event
                var maxX:int = 0;
                var maxY:int = 0;
                if (realTarget is DisplayObjectContainer &&
                    delegate.numAutomationChildren != 0)
                {
                    // we need to find a point where we'll hit the real target and not another
                    // child that is obstructing it.  this algorithm makes a lot of assumptions,
                    // namely that we're dropping at the end of a contiguous region and that that's
                    // the point the user chose.  we could be more rigorous and try to find the 
                    // largest visible region of the container and select a coordinate there, but
                    // this is gonna be imperfect either way (imagine dropping into a tic tac toe
                    // board) so might as well keep it simple and assume we're dropping at the
                    // end of a horizontal or vertical list.
                    var aObjContainer:IAutomationObject = delegate;
                    var dObjContainer:DisplayObject =
                        realTarget as DisplayObjectContainer;
                    for (var i:uint = 0; i < aObjContainer.numAutomationChildren; i++)
                    {
                        var child:IAutomationObject = 
                            aObjContainer.getAutomationChildAt(i);
                        if (child is DisplayObject)
                        {
                            var dObj:DisplayObject = child as DisplayObject;
                            maxX = Math.max(maxX, dObj.x + dObj.width);
                            maxY = Math.max(maxY, dObj.y + dObj.height);
                        }
                    }
                    if (maxX >= dObjContainer.width && maxY >= dObjContainer.height)
                        throw new Error();
                }
                var container:DisplayObject = DisplayObject(realTarget);
                if ( (container.width - maxX) > 5)
                    dragEvent.localX = maxX + (container.width - maxX)/2;
                else
                    dragEvent.localX = container.width - maxX/2;
                if( (container.height - maxY) > 5)
                    dragEvent.localY = maxY + (container.height - maxY)/2;
                else
                    dragEvent.localY = container.height - maxY/2;
                // maybe add a test to make sure that localX and localY actually
                // do point at the realTarget and aren't being obstructed by another automation
                // object?
             }
             else
             {
                // get the cooridnate based on the dragged item
                var point:Point = (target.automationDelegate).getLocalPoint(new Point(dragEvent.localX, dragEvent.localY),dragEvent.draggedItem);
                dragEvent.localX= point.x;
                dragEvent.localY=point.y;
             }

            mouseEvent = toMouseEvent(MouseEvent.MOUSE_MOVE, dragEvent);
            mouseEvent.buttonDown = true;
            help.replayMouseEvent(realTarget, mouseEvent);
            
            if(callBackBeforeDrop != null)
            {
                callBackBeforeDrop();
                callBackBeforeDrop = null;
            }

            mouseEvent = toMouseEvent(MouseEvent.MOUSE_UP, dragEvent);
            DragManager.dragProxy.action = dragEvent.action;
            help.replayMouseEvent(realTarget, mouseEvent);
            help.addSynchronization(function():Boolean
            {
                return !DragManager.isDragging;
            });
        }
        else if (dragEvent.type == DragEvent.DRAG_COMPLETE)
        {
            mouseEvent = toMouseEvent(MouseEvent.MOUSE_UP, dragEvent);
            var proxy:DragProxy = DragManager.dragProxy;
            if (!proxy)
                return false;
            var pt:Point = 
                proxy.globalToLocal(new Point(proxy.startX, proxy.startY));
            mouseEvent.localX = pt.x;
            mouseEvent.localY = pt.y;

            help.replayMouseEvent(DragManager.dragProxy, mouseEvent);
            help.addSynchronization(function():Boolean
            {
                return !DragManager.isDragging;
            });
        }
        return true;
    }
        
}

}
