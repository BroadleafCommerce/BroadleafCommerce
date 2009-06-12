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
import mx.automation.Automation;
import mx.automation.IAutomationObjectHelper;
import mx.automation.delegates.TextFieldAutomationHelper;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.core.mx_internal;
import mx.core.IUITextField;
import mx.controls.TextInput;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  TextInput control.
 * 
 *  @see mx.controls.TextInput 
 *
 */
public class TextInputAutomationImpl extends UIComponentAutomationImpl 
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
        Automation.registerDelegateClass(TextInput, TextInputAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj TextInput object to be automated.     
     */
    public function TextInputAutomationImpl(obj:TextInput)
    {
        super(obj);
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
    protected function get  textInput():TextInput
    {
        return uiComponent as TextInput;
    }

    /**
     *  @private
     *  Generic record/replay logic for textfields.
     */
    private var automationHelper:TextFieldAutomationHelper;
        
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
        return [ textInput.text ];
    }


    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function replayAutomatableEvent(interaction:Event):Boolean
    {
        return ((automationHelper &&
                 automationHelper.replayAutomatableEvent(interaction)) ||
                super.replayAutomatableEvent(interaction));
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  Method which gets called after the component has been initialized. 
     *  This can be used to access any sub-components and act on the component.
     */
    override protected function componentInitialized():void
    {
        super.componentInitialized();
        var textField:IUITextField = textInput.getTextField();
        automationHelper = new TextFieldAutomationHelper(uiComponent, uiAutomationObject, textField)
    }

    /**
     *  @private
     *  Prevent duplicate ENTER key recordings. 
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        ;
    }
        
}
}