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
import flash.ui.Keyboard;
    
import mx.automation.Automation;
import mx.automation.IAutomationObject;
import mx.automation.tabularData.ListTabularData;
import mx.controls.List;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.mx_internal;
import mx.events.ListEvent;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  List control.
 * 
 *  @see mx.controls.List 
 *
 */
public class ListAutomationImpl extends ListBaseAutomationImpl 
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
        Automation.registerDelegateClass(List, ListAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj List object to be automated.     
     */
    public function ListAutomationImpl(obj:List)
    {
        super(obj);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get list():List
    {
        return uiComponent as List;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        switch (event.type)
        {
            case ListEvent.ITEM_EDIT_BEGIN:
            {
                var input:ListEvent = event as ListEvent;
                var ev:ListEvent = new ListEvent(ListEvent.ITEM_EDIT_BEGINNING);
                ev.itemRenderer = input.itemRenderer;
                ev.rowIndex = input.rowIndex;
                ev.columnIndex = 0;
                return list.dispatchEvent(ev);
            }

            default:
            {
                return super.replayAutomatableEvent(event);
            }

        }
    }
        
    /**
     * @private
     */
    override public function getAutomationChildAt(index:int):IAutomationObject
    {
        var listItems:Array = list.rendererArray;
        var numCols:int = listItems[0].length;
        var row:uint = uint(numCols == 0 ? 0 : index / numCols);
        var col:uint = uint(numCols == 0 ? index : index % numCols);

        var item:IListItemRenderer = listItems[row][col];
        if (list.itemEditorInstance && item == list.editedItemRenderer)
            return list.itemEditorInstance as IAutomationObject;
        else
            return item as IAutomationObject;
    }
 
 
    /**
     * @private
     */
    override public function getItemAutomationIndex(delegate:IAutomationObject):String
    {
        var item:IListItemRenderer = delegate as IListItemRenderer;
        if (item == list.itemEditorInstance && list.editedItemPosition)
            item = list.editedItemRenderer;
        
        return super.getItemAutomationIndex(item as IAutomationObject);
    }
   
    /**
     *  A matrix of the automationValues of each item in the grid. The return value
     *  is an array of rows, each of which is an array of item renderers (row-major).
     */
    override public function get automationTabularData():Object
    {
        return new ListTabularData(list);
    }

    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function itemEditBeginHandler(event:ListEvent):void 
    {
        event.columnIndex = 0;
        recordAutomatableEvent(event, true);
    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        // prevent any recording if itemEditorInstance is active
        if (list.itemEditorInstance)
            return;

        if(event.keyCode == Keyboard.ENTER && event.target != list)
            return;

        super.keyDownHandler(event);
    }

    
}
}
