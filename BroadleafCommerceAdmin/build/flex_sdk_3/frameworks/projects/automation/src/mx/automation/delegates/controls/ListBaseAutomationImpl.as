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
import flash.display.DisplayObjectContainer;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.ui.Keyboard;
import flash.utils.getTimer;
import flash.utils.describeType;

import mx.automation.Automation;
import mx.automation.AutomationIDPart;
import mx.automation.IAutomationClass;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.IAutomationTabularData;
import mx.automation.events.ListItemSelectEvent;
import mx.automation.delegates.core.ScrollControlBaseAutomationImpl;
import mx.automation.delegates.DragManagerAutomationImpl;
import mx.automation.events.AutomationDragEvent;
import mx.automation.events.AutomationRecordEvent;
import mx.automation.tabularData.ListBaseTabularData;
import mx.controls.listClasses.ListBase;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.EventPriority;
import mx.core.mx_internal;
import mx.events.DragEvent;
import mx.events.ListEvent;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDetail;
import mx.events.ScrollEventDirection;
import mx.managers.DragManager;
import mx.utils.StringUtil;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  ListBase class.
 * 
 *  @see mx.controls.listClasses.ListBase 
 *
 */
public class ListBaseAutomationImpl extends ScrollControlBaseAutomationImpl 
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
        Automation.registerDelegateClass(ListBase, ListBaseAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj ListBase object to be automated.     
     */
    public function ListBaseAutomationImpl(obj:ListBase)
    {
        super(obj);
        
        //for item renderers already present
        //updateItemRenderers(); 

        obj.addEventListener(Event.ADDED, childAddedHandler, false, 0, true);
        obj.addEventListener(MouseEvent.CLICK, mouseClickHandler, false, 0, true);
        obj.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler, false, 0, true);

        obj.addEventListener(ListEvent.ITEM_DOUBLE_CLICK, recordAutomatableEvent, false, 0, true);
        
        obj.addEventListener(DragEvent.DRAG_START, dragStartHandler);
        obj.addEventListener(DragEvent.DRAG_DROP, dragDropHandler, false, EventPriority.DEFAULT+1, true);
        obj.addEventListener(DragEvent.DRAG_COMPLETE, dragCompleteHandler);
        obj.addEventListener(AutomationRecordEvent.RECORD, recordHandler);
        obj.addEventListener(ScrollEvent.SCROLL, scrollHandler);
    }
    

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get listBase():ListBase
    {
        return uiComponent as ListBase;
    }
    
    /**
     *  @private
     */
    protected var preventDragDropRecording:Boolean = true;
    
    /**
     *  @private
     */
    protected var shiftKeyDown:Boolean = false;

    /**
     *  @private
     */
    protected var ctrlKeyDown:Boolean = false;
    
    /**
     *  @private
     */
    protected var itemUnderMouse:IListItemRenderer;

    /**
     *  @private
     */
    protected var dragScrollEvent:ScrollEvent = null;
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    protected function recordListItemSelectEvent(item:IListItemRenderer,
                                                         trigger:Event, 
                                                         cacheable:Boolean=true):void
    {
        var selected:Boolean = listBase.isItemSelected(item.data);
        var selectionType:String = ListItemSelectEvent.SELECT;
        var keyEvent:KeyboardEvent = trigger as KeyboardEvent;
        var mouseEvent:MouseEvent = trigger as MouseEvent;
        
        var indexSelection:Boolean = false;
        
        if (!Automation.automationManager || !Automation.automationManager.automationEnvironment)
            return ;

        var automationClass:IAutomationClass = Automation.automationManager.automationEnvironment.getAutomationClassByInstance(listBase);
        if (automationClass)
        {
            var propertyNameMap:Object = automationClass.propertyNameMap;
            if (propertyNameMap["enableIndexBasedSelection"])
            {
                selectionType = ListItemSelectEvent.SELECT_INDEX;
                indexSelection = true;
            }
        }
        
        var event:ListItemSelectEvent = new ListItemSelectEvent(selectionType);
        if (indexSelection)
            fillItemRendererIndex(item, event);
        else
            event.itemRenderer = item;
        
        event.triggerEvent = trigger;
        if(keyEvent)
        {
            event.ctrlKey = keyEvent.ctrlKey;
            event.shiftKey = keyEvent.shiftKey;
            event.altKey = keyEvent.altKey;
        }
        else if(mouseEvent)
        {
            event.ctrlKey = mouseEvent.ctrlKey;
            event.shiftKey = mouseEvent.shiftKey;
            event.altKey = mouseEvent.altKey;
        }

        recordAutomatableEvent(event, cacheable);
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    mx_internal var itemAutomationNameFunction:Function = getItemAutomationValue;

    /**
     * @private
     */
    public function getItemAutomationValue(item:IAutomationObject):String
    {
        // check for atleast one non-null item  
        var values:Array = item.automationValue;
        if (values)
        {
            var itemCount:int = values.length;
            for (var i:int = 0; i < itemCount; ++i)
            {
                if (values[i])
                {
                    // found one non null item, so return
                    return values.join(" | ");
                }
            }
        }
                    
        return null;
    }

    /**
     * @private
     */
    public function getItemAutomationName(item:IAutomationObject):String
    {
        return item.automationName;
    }

    /**
     * @private
     */
    public function getItemAutomationIndex(item:IAutomationObject):String
    {
        return String("index:" + listBase.itemRendererToIndex(IListItemRenderer(item)));
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
     * @private
     */
    override public function get automationValue():Array
    {
        var result:Array = [];

        var selectedItemsAboveView:Boolean = false;
        var selectedItemsBelowView:Boolean = false;

        var selItems:Array = listBase.selectedIndices;
        var n:int = selItems.length;
        var listLength:int = listBase.rendererArray.length * listBase.rendererArray[0].length;
        for (var i:int = 0; i < n; i++)
        {
            var viewRelativeIndex:int = selItems[i] - listBase.verticalScrollPosition;

            if (viewRelativeIndex < 0)
                selectedItemsAboveView = true;
            else if (viewRelativeIndex >= listLength)
                selectedItemsBelowView = true;
            else
            {
                var item:IListItemRenderer = listBase.indexToItemRenderer(selItems[i]);
                result.push(IAutomationObject(item).automationValue);
            }
        }

        if (selectedItemsAboveView)
            result.unshift("...");

        if (selectedItemsBelowView)
            result.push("...");

        return result;
    }

    /**
     *  @private
     */
    override public function createAutomationIDPart(child:IAutomationObject):Object
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return (help
                ? help.helpCreateIDPart(uiAutomationObject, child, itemAutomationNameFunction,
                                            getItemAutomationIndex)
                : null);
    }

    /**
     *  @private
     */
    override public function resolveAutomationIDPart(part:Object):Array
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help ? help.helpResolveIDPart(uiAutomationObject, part) : null;
    }

    /**
     *  @private
     */
    override public function get numAutomationChildren():int
    {
        var listItems:Array = listBase.rendererArray;
        if (listItems.length == 0)
            return 0;

        var result:int = listItems.length * listItems[0].length;
        var row:uint = listItems.length - 1;
        var col:uint = listItems[0].length - 1;
        while (!listItems[row][col] && result > 0)
        {
            result--;
            if (col != 0)
                col--;
            else if (row != 0)
            {
                row--;
                col = listItems[0].length - 1;
            }
        }
        return result;
    }

    /**
     *  @private
     */
    override public function getAutomationChildAt(index:int):IAutomationObject
    {
        var listItems:Array = listBase.rendererArray;
        var numCols:int = listItems[0].length;
        var row:uint = uint(numCols == 0 ? 0 : index / numCols);
        var col:uint = uint(numCols == 0 ? index : index % numCols);
        var item:IListItemRenderer = listItems[row][col];
        return item as IAutomationObject;
    }

    /**
     *  A matrix of the automationValues of each item in the grid. The return value
     *  is an array of rows, each of which is an array of item renderers (row-major).
     */
    override public function get automationTabularData():Object
    {
        return new ListBaseTabularData(listBase);
    }
    
    /**
     * @private
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        switch (event.type)
        {
            case MouseEvent.CLICK:
                return help.replayClick(uiComponent, MouseEvent(event));
            
            case ListEvent.ITEM_DOUBLE_CLICK:
            {
                var clickEvent:ListEvent = ListEvent(event);
                return replayMouseDoubleClickOnItem(clickEvent.itemRenderer);
            }    
                
            case KeyboardEvent.KEY_DOWN:
            {
                listBase.setFocus();
                return help.replayKeyboardEvent(uiComponent, KeyboardEvent(event));
            }

            case ListItemSelectEvent.SELECT_INDEX:
            case ListItemSelectEvent.DESELECT:
            case ListItemSelectEvent.MULTI_SELECT:
            case ListItemSelectEvent.SELECT:
            {
                var completeTime:Number = getTimer() + listBase.getStyle("selectionDuration");

                help.addSynchronization(function():Boolean
                {
                    return getTimer() >= completeTime;
                });

                var lise:ListItemSelectEvent = ListItemSelectEvent(event);
                
                if (event.type == ListItemSelectEvent.SELECT_INDEX)
                {
                    listBase.scrollToIndex(lise.itemIndex);
                    lise.itemRenderer = getItemRendererForEvent(lise);
                }
                else
                {
                    if(!lise.itemRenderer)
                    findItemRenderer(lise);
                }


                // keyboard and mouse are currently treated the same
                if (lise.triggerEvent is MouseEvent)
                {
                    return replayMouseClickOnItem(lise.itemRenderer,
                                                  lise.ctrlKey,
                                                  lise.shiftKey,
                                                  lise.altKey);
                }
                else if (lise.triggerEvent is KeyboardEvent)
                {
                    return help.replayKeyDownKeyUp(lise.itemRenderer,
                                                   Keyboard.SPACE,
                                                   lise.ctrlKey,
                                                   lise.shiftKey,
                                                   lise.altKey);
                }
                else
                {
                    throw new Error();
                }
            }
            
            case AutomationDragEvent.DRAG_START:
            case AutomationDragEvent.DRAG_DROP:
            case AutomationDragEvent.DRAG_COMPLETE:
            {
                 return DragManagerAutomationImpl.replayAutomatableEvent(uiAutomationObject,
                                                              event);
            }
            
            case ScrollEvent.SCROLL:
                if(DragManager.isDragging)
                {
                    var scrollEv:ScrollEvent = event as ScrollEvent;
                    if(scrollEv.direction == ScrollEventDirection.VERTICAL)
                    {
                        listBase.verticalScrollPosition = scrollEv.position;
                        listBase.validateNow();
                    }
                    DragManagerAutomationImpl.callBackBeforeDrop = function():void
                        {
                            //stop dragScroll as it modifies the scroll position
                            listBase.resetDragScrolling();
                            var scrollEv:ScrollEvent = event as ScrollEvent;
                            if(scrollEv.direction == ScrollEventDirection.VERTICAL)
                            {
                                listBase.verticalScrollPosition = scrollEv.position;
                                listBase.validateNow();
                            }
                            
                        };
                    return true;
                }
            // fall thru if not dragging while scroll occurs
            default:
            {
                return super.replayAutomatableEvent(event);
            }
        }
    }

    /**
     * @private
     * Plays back MouseEvent.CLICK on the item renderer.
     */
    protected function replayMouseClickOnItem(item:IListItemRenderer,
                                                ctrlKey:Boolean = false,
                                                shiftKey:Boolean = false,
                                                altKey:Boolean = false):Boolean
    {
        var me:MouseEvent = new MouseEvent(MouseEvent.CLICK);
        me.ctrlKey = ctrlKey;
        me.altKey = altKey;
        me.shiftKey = shiftKey;
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help.replayClick(item, me);
    }

    /**
     * @private
     * Plays back MouseEvent.DOUBLE_CLICK on the item renderer.
     */
    protected function replayMouseDoubleClickOnItem(item:IListItemRenderer):Boolean
    {
        var me:MouseEvent = new MouseEvent(MouseEvent.DOUBLE_CLICK);
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help.replayMouseEvent(item, me);
    }
    
    /**
    * @private
    */
    protected function updateItemRenderers():void
    {
        var listItems:Array = listBase.rendererArray;
        if (listItems.length == 0)
            return ;

        var rows:uint = listItems.length;
        var cols:uint = listItems[0].length;
        
        var ownerList:DisplayObjectContainer = listBase as DisplayObjectContainer;
        for(var i:int = 0; i < rows; ++i)
        {
            for(var j:int = 0; j < cols; ++j)
            {
                var item:IListItemRenderer = listItems[i][j];
                if(item)
                {   
                    item.owner = ownerList;
                    if(item is IAutomationObject)
                        IAutomationObject(item).showInAutomationHierarchy = true;
                }
            }   
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function childAddedHandler(event:Event):void
    {
        var child:Object = event.target;
        
        if (child is IListItemRenderer && child.parent == listBase.getListContentHolder())
        {
            IListItemRenderer(child).owner = uiComponent as DisplayObjectContainer;
            child.showInAutomationHierarchy = true;
        }
    }
    
    
    /**
     *  @private
     */
    protected function mouseClickHandler(event:MouseEvent):void
    {
        var item:IListItemRenderer = listBase.getItemRendererForMouseEvent(event);
        if (!item)
        {
            //DataGrid overrides displayObjectToItemRenderer to return
            //null if the item is the active item editor, so that's
            //not a reliable way of determining if the user clicked on a blank
            //row or now, so use mouseEventToItemRendererOrEditor instead
            if (listBase.mouseEventToItemRendererOrEditor(event) == null)
                recordAutomatableEvent(event, true)
            return;
        }
        else 
        {
            // take the key modifiers from the mouseDown event because
            // they were used by List for making the selection
            event.ctrlKey = ctrlKeyDown;
            event.shiftKey = shiftKeyDown;
            recordListItemSelectEvent(item, event);
        }
    }
    
    /**
     *  @private
     */
    protected function mouseDownHandler(event:MouseEvent):void
    {
        ctrlKeyDown = event.ctrlKey;
        shiftKeyDown = event.shiftKey;
        itemUnderMouse = listBase.getItemRendererForMouseEvent(event);
    }

    
    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        if (event.keyCode == Keyboard.SPACE)
        {
            var listItems:Array = listBase.rendererArray;
            var caretIndex:int = listBase.getCaretIndex();
            if (caretIndex != -1)
            {
                var rendererIndex:int = caretIndex - listBase.verticalScrollPosition; 
//                                      + listBase.lockedRowCount;
 // fix to adapt to the new listbase (which does not have lockedRowCount
                var item:IListItemRenderer = listItems[rendererIndex][0] as IListItemRenderer;
                recordListItemSelectEvent(item, event);
            }   
        }
        else if (event.keyCode != Keyboard.SPACE &&
            event.keyCode != Keyboard.CONTROL &&
            event.keyCode != Keyboard.SHIFT &&
            event.keyCode != Keyboard.TAB)
        {
            recordAutomatableEvent(event);
        }   
    }
    
    /**
     *  @private
     */
    protected function dragStartHandler(event:DragEvent):void
    {
        var drag:AutomationDragEvent = new AutomationDragEvent(event.type);
        drag.draggedItem = itemUnderMouse as IAutomationObject;
        drag.ctrlKey = ctrlKeyDown;
        drag.shiftKey = shiftKeyDown;
        
       var re:AutomationRecordEvent = new AutomationRecordEvent(AutomationRecordEvent.RECORD, false);
       re.automationObject = uiAutomationObject;
       re.cacheable = false;
       re.replayableEvent = drag;
        
        var am:IAutomationManager = Automation.automationManager;
        preventDragDropRecording = false;
        am.recordAutomatableEvent(uiAutomationObject, re);
        preventDragDropRecording = true;
    }
    
    /**
     *  @private
     */
    protected function dragDropHandler(event:DragEvent):void
    {
        if(dragScrollEvent)
        {
            recordAutomatableEvent(dragScrollEvent);
            dragScrollEvent=null;
        }

        var drag:AutomationDragEvent = new AutomationDragEvent(event.type);
        drag.action = event.action;
        
        var index:int = listBase.calculateDropIndex(event);
        
        if (listBase.dataProvider && index != listBase.dataProvider.length)
        {
 // fix to adapt to the new listbase (which does not have lockedRowCount
//          if (index >= listBase.lockedRowCount)
                if (index >= 0)
                index -= listBase.verticalScrollPosition;

            var rc:Number = listBase.rendererArray.length;
            if (index >= rc)
                index = rc - 1;
        
            if (index < 0)
                index = 0;
    
            drag.draggedItem = getAutomationChildAt(index);
        }

        var am:IAutomationManager = Automation.automationManager;
        preventDragDropRecording = false;
        am.recordAutomatableEvent(uiAutomationObject, drag);
        preventDragDropRecording = true;
    }

    /**
     *  @private
     */
    protected function dragCompleteHandler(event:DragEvent):void
    {
        if(event.action == DragManager.NONE)
        {
            var drag:AutomationDragEvent = new AutomationDragEvent(event.type);
            drag.action = event.action;

            var am:IAutomationManager = Automation.automationManager;
            preventDragDropRecording = false;
            am.recordAutomatableEvent(uiAutomationObject, drag);
            preventDragDropRecording = true;
        }
    }
    
    /**
     *  @private
     */
    private function recordHandler(ev:AutomationRecordEvent):void
    {
        // list based controls handle drag-drop on their own
        if(preventDragDropRecording && ev.replayableEvent is AutomationDragEvent)
            ev.preventDefault();    
    }
    
    /**
     *  @private
     */
    private function scrollHandler(ev:ScrollEvent):void
    {
        // normall scrolling is recorded by the base class
        // auto scrolling during drag is recorded here
        if(DragManager.isDragging)
            dragScrollEvent = ev;
    }
    
    /**
     *  @private
     */
    protected function trimArray(val:Array):void
    {
        for (var index:int = 0; index < val.length; ++index)
            val[index] = StringUtil.trim(val[index]);
    }
    
    /**
     *  @private
     */
     protected function findItemRenderer(selectEvent:ListItemSelectEvent):Boolean
     {
        if (selectEvent.itemAutomationValue && selectEvent.itemAutomationValue.length)
        {
            var itemLabel:String = selectEvent.itemAutomationValue;
            var tabularData:IAutomationTabularData = automationTabularData as IAutomationTabularData;
            var values:Array = tabularData.getValues(0, tabularData.numRows);
            var length:int = values.length;

            var part:AutomationIDPart = new AutomationIDPart();
            part.automationName = itemLabel;

            var labels:Array = itemLabel.split("|");
            
            trimArray(labels);
            
            var index:int = 0;
            for each(var a:Array in values)
            {
                values[index] = [];
                trimArray(a);
                var colIndex:int = 0 ;
                for each(var b:String in a)
                {
                    var splitArray:Array = b.split("|");
                    for each ( var c:String in splitArray)
                        values[index].push(c);
                }
                trimArray(values[index]);
                ++index;
            }

            for (index = 0; index < labels.length; ++index)
            {
                var lString:String = labels[index];
                if (lString.charAt(0) == "*" && lString.charAt(lString.length-1) == "*")
                    labels[index] = lString.substr(1, lString.length-2);
            }
                        
            for (index = 0; index < length; ++index)
            {
                if(compare(labels, values[index]))
                {
                    listBase.scrollToIndex(index);
                    var ao:IAutomationObject = Automation.automationManager.resolveIDPartToSingleObject(uiAutomationObject, part);
                    
                    if (ao)
                    {
                        selectEvent.itemRenderer = ao as IListItemRenderer;
                        return true;
                    }
                }
            }
        }

        return false;
     }
     
    /**
     *  @private
     */
     protected function compare(labels:Array, values:Array):Boolean
     {
         if (labels.length != values.length)
            return false;
         var length:int = labels.length;
         for ( var index:int = 0; index < length; ++index)
             if (labels[index] != values[index])
                return false;
         
         return true;
     }
     
    /**
     *  @private
     */
     protected function getItemRendererForEvent(lise:ListItemSelectEvent):IListItemRenderer
     {
         return listBase.indexToItemRenderer(lise.itemIndex);
     }
     
    /**
     *  @private
     */
     protected function fillItemRendererIndex(item:IListItemRenderer, event:ListItemSelectEvent):void
     {
        event.itemIndex = listBase.itemRendererToIndex(item);
     }  
     
    /**
     *  @private
     */
     public function getItemsCount():int
     {
        if (listBase.dataProvider)
            return listBase.dataProvider.length;
        
        return 0;
     }
     
}
}
