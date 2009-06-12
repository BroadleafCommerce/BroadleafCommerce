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
import flash.events.TextEvent;
import flash.ui.Keyboard;
import flash.utils.getTimer;
import mx.automation.Automation;
import mx.automation.AutomationIDPart;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.events.AutomationRecordEvent;
import mx.automation.events.ListItemSelectEvent;
import mx.automation.events.TextSelectionEvent;
import mx.controls.ComboBase;
import mx.controls.ComboBox;
import mx.controls.List;
import mx.core.EventPriority;
import mx.core.mx_internal;
import mx.core.UIComponent;
import mx.events.DropdownEvent;
import mx.events.ScrollEvent;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  ComboBox control.
 * 
 *  @see mx.controls.ComboBox 
 *
 */
public class ComboBoxAutomationImpl extends ComboBaseAutomationImpl
{
    include "../../../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private  
     * Registers the delegate class for a component class with automation manager.
     *  
     *  @param root The SystemManger of the application.
     */
    public static function init(root:DisplayObject):void
    {
        Automation.registerDelegateClass(ComboBox, ComboBoxAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *  
     *  @param obj The ComboBox to be automated.
     */
    public function ComboBoxAutomationImpl(obj:ComboBox)
    {
        super(obj);
        obj.addEventListener(DropdownEvent.CLOSE, openCloseHandler, false, 0, true);
        obj.addEventListener(DropdownEvent.OPEN, openCloseHandler, false, 0, true);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get comboBox():ComboBox
    {
        return uiComponent as ComboBox;
    }

    /**
     *  @private
     *  Replays the event specified by the parameter if possible.
     *
     *  @param interaction The event to replay.
     * 
     *  @return Whether or not a replay was successful.
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var completeTime:Number;
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        var textReplayer:IAutomationObject = 
                    comboBox.getTextInput() as IAutomationObject;
        var aoDropDown:IAutomationObject = comboBox.dropdown as IAutomationObject;
        
        if (event is ListItemSelectEvent)
        {
            var result:Boolean = aoDropDown.replayAutomatableEvent(event);

            // selection closes the comboBox. 
            // We need to wait for the dropDown to close.
            if (result)
            {
                completeTime = getTimer() + comboBox.getStyle("closeDuration");

                help.addSynchronization(function():Boolean
                {
                    return getTimer() >= completeTime;
                });
            }
            
            return result;
        }
        else if (event is KeyboardEvent)
        {
            var keyEvent:KeyboardEvent = event as KeyboardEvent;
            if (comboBox.getTextInput() && comboBox.editable)
            {
                if (keyEvent.keyCode == Keyboard.UP ||
                    keyEvent.keyCode == Keyboard.DOWN ||
                    keyEvent.keyCode == Keyboard.PAGE_UP ||
                    keyEvent.keyCode == Keyboard.PAGE_DOWN)
                {
                    return help.replayKeyboardEvent(uiComponent, KeyboardEvent(event));
                }
                else
                {
                    return textReplayer.replayAutomatableEvent(event);
                }
            }
            else
            {
                // if comboBox is closing due to either selection or escape we need to wait
                // and sync up
                if(keyEvent.keyCode == Keyboard.ENTER || keyEvent.keyCode == Keyboard.ESCAPE)
                {
                    completeTime = getTimer() + comboBox.getStyle("closeDuration");
        
                   help.addSynchronization(function():Boolean
                    {
                        return getTimer() >= completeTime;
                    });
                }
                return help.replayKeyboardEvent(uiComponent, KeyboardEvent(event));
            }
        }
        else if (event is DropdownEvent)
        {
            var cbdEvent:DropdownEvent = DropdownEvent(event);
            if (cbdEvent.triggerEvent is KeyboardEvent)
            {
                var kbEvent:KeyboardEvent =
                    new KeyboardEvent(KeyboardEvent.KEY_DOWN);
                kbEvent.keyCode =
                    (cbdEvent.type == DropdownEvent.OPEN
                     ? Keyboard.DOWN
                     : Keyboard.UP);
                kbEvent.ctrlKey = true;
                help.replayKeyboardEvent(uiComponent, kbEvent);
            }
            else //triggerEvent is MouseEvent
            {
                if ((cbdEvent.type == DropdownEvent.OPEN &&
                     !comboBox.isShowingDropdown) ||
                    (cbdEvent.type == DropdownEvent.CLOSE &&
                     comboBox.isShowingDropdown))
                {
                    help.replayClick(comboBox.ComboDownArrowButton);
                }
            }
    
            completeTime = getTimer() +
                comboBox.getStyle(cbdEvent.type == DropdownEvent.OPEN ?
                         "openDuration" :
                         "closeDuration");

            help.addSynchronization(function():Boolean
            {
                return getTimer() >= completeTime;
            });

            return true;
        }
        else if (comboBox.getTextInput() && (event is TextEvent || event is TextSelectionEvent) )
        {
            return textReplayer.replayAutomatableEvent(event);
        }
        else if (event is ScrollEvent)
        {
            return aoDropDown.replayAutomatableEvent(event);
        }

        return super.replayAutomatableEvent(event);
    }

    /**
     *  @private
     *  Provide a set of properties that identify the child within 
     *  this container.  These values should not change during the
     *  lifespan of the application.
     *  
     *  @param child the child for which to provide the id.
     *  @return a set of properties describing the child which can
     *          later be used to resolve the component.
     */
    override public function createAutomationIDPart(child:IAutomationObject):Object
    {
        var delegate:IAutomationObject = comboBox.dropdown as IAutomationObject;
        return  delegate.createAutomationIDPart(child);
    }

    /**
     *  @private
     *
     *  Resolve a child using the id provided.  The id is a set 
     *  of properties as provided by createAutomationID.
     *
     *  @param criteria a set of properties describing the child.
     *         The criteria can contain regular expression values
     *         resulting in multiple children being matched.
     *  @return the an array of children that matched the criteria
     *          or <tt>null</tt> if no children could not be resolved.
     */
    override public function resolveAutomationIDPart(criteria:Object):Array
    {
        var delegate:IAutomationObject = comboBox.dropdown as IAutomationObject;
        return  delegate.resolveAutomationIDPart(criteria);
    }

    /** 
     *  @private
     *  Provides the automation object at the specified index.  This list
     *  should not include any children that are composites.
     *
     *  @param index the index of the child to return
     *  @return the child at the specified index.
     */
    override public function getAutomationChildAt(index:int):IAutomationObject
    {
        var delegate:IAutomationObject = comboBox.dropdown as IAutomationObject;
        return  delegate.getAutomationChildAt(index);
    }

    /**
     *  @private
     *  Provides the number of automation children this container has.
     *  This sum should not include any composite children, though
     *  it does include those children not signficant within the
     *  automation hierarchy.
     *
     *  @return the number of automation children this container has.
     */
    override public function get numAutomationChildren():int
    {
        var delegate:IAutomationObject = comboBox.dropdown as IAutomationObject;
        return delegate.numAutomationChildren;
    }

    /**
     *  @private
     *  A matrix containing the automation values of all parts of the components.
     *  Should be row-major (return value is an array of rows, each of which is
     *  an array of "items").
     *
     *  @return A matrix containing the automation values of all parts of the components.
     */
    override public function get automationTabularData():Object
    {
        var delegate:IAutomationObject = comboBox.dropdown as IAutomationObject;
        return  delegate.automationTabularData;
    }

    /**
     * @private
    */
    override protected function setupEditHandler():void
    {
        super.setupEditHandler();

        var text:DisplayObject = comboBase.getTextInput();
        if (!text)
            return;

        // If comboBox is editable we setup a listener for the textInput control.
        if(comboBox.editable)
        {
            text.addEventListener(KeyboardEvent.KEY_DOWN, 
                            textKeyDownHandler, false, EventPriority.DEFAULT+1);
        }
        else
        {
            text.removeEventListener(KeyboardEvent.KEY_DOWN, 
                            textKeyDownHandler, false);
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
    private function openCloseHandler(event:DropdownEvent):void
    {
        var textInput:DisplayObject = comboBox.getTextInput();
        if (event.triggerEvent)
            recordAutomatableEvent(event);
        if (event.type == DropdownEvent.OPEN)
        {
            comboBox.dropdown.addEventListener(AutomationRecordEvent.RECORD, 
                                dropdown_recordHandler, false, 0, true);
        }
        else
        {
            if (comboBox.hasDropdown())
            {
                comboBox.dropdown.removeEventListener(AutomationRecordEvent.RECORD, dropdown_recordHandler);
            }
        }   
    }

    /**
     *  @private
     */
    private function dropdown_recordHandler(event:AutomationRecordEvent):void
    {
        var re:Event = event.replayableEvent;
        if ((re is ListItemSelectEvent || re is ScrollEvent) 
            && event.target is List )
           recordAutomatableEvent(event.replayableEvent, event.cacheable);
    }

    /**
     * @private
     * Keyboard events like up/down/page_up/page_down/enter are
     * recorded here. They are not recorded by textInput control but
     * we require them to be recorded.
     */    
    private function textKeyDownHandler(event:KeyboardEvent):void
    {
        // we do not record key events with modifiers
        // open/close events with ctrl key are recorded seperately.
        if (event.ctrlKey)
            return;
        // record keys which are of used for navigation in the dropdown list
        if (event.keyCode == Keyboard.UP ||
            event.keyCode == Keyboard.DOWN ||
            event.keyCode == Keyboard.PAGE_UP ||
            event.keyCode == Keyboard.PAGE_DOWN ||
            event.keyCode == Keyboard.ESCAPE ||
            event.keyCode == Keyboard.ENTER)
        {
            recordAutomatableEvent(event);
        }   
    }

    /**
     * @private
    */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        // if editable we have a different keydown handler
        if (comboBox.editable)
            return;

        // we do not record key events with modifiers
        if (event.ctrlKey)
            return;

        if(event.target == comboBox && 
            event.keyCode != Keyboard.ENTER &&
            event.keyCode != Keyboard.SPACE)
            recordAutomatableEvent(event);
        
        super.keyDownHandler(event);
    }
    
    /**
     * @private
     */
    public function getItemsCount():int
    {
        if (comboBox.dataProvider)
            return comboBox.dataProvider.length;
        
        return 0;
    }

}
}
