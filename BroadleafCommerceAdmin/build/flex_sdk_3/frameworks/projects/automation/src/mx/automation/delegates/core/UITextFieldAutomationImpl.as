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
import flash.display.DisplayObjectContainer;
import flash.events.Event;

import mx.automation.Automation;
import mx.automation.IAutomationObject;
import mx.core.UITextField;

[Mixin]
/**
 * 
 *  Defines the methods and properties required to perform instrumentation for the 
 *  UITextField class. 
 * 
 *  @see mx.core.UITextField
 *  
 */
public class UITextFieldAutomationImpl implements IAutomationObject
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
        Automation.registerDelegateClass(UITextField, UITextFieldAutomationImpl);
    }   
    
    /**
     * Constructor.
     * @param obj UITextField object to be automated.     
     */ 
    public function UITextFieldAutomationImpl(obj:UITextField)
    {
        super();
        uiTextField = obj;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     * @private
     */ 
    protected var uiTextField:UITextField;
    
    //----------------------------------
    //  automationName
    //----------------------------------

    /**
     * @private
     */
    public function get automationName():String
    {
        return uiTextField.text;
    }

    /**
     * @private
     */
    public function set automationName(value:String):void
    {
        
         if( uiTextField is IAutomationObject)
         {
              var tempObj:IAutomationObject = IAutomationObject(uiTextField);
              if(tempObj != null)
              {
                tempObj.automationName = value;
              }
         }
    }

    //----------------------------------
    //  automationValue
    //----------------------------------
 
    /**
     *  @inheritDoc
     */
    public function get automationValue():Array
    {
        return [ uiTextField.text ];
    }
    
    /**
     *  @private
     */
    public function createAutomationIDPart(child:IAutomationObject):Object
    {
        return null;
    }

    /**
     *  @private
     */
    public function resolveAutomationIDPart(criteria:Object):Array
    {
        return [];
    }

    /**
     *  @private
     */
    public function get numAutomationChildren():int
    {
        return 0;
    }
    
     /**
     *  @private
     */
    public function getAutomationChildAt(index:int):IAutomationObject
    {
        return null;
    }
    
    /**
     *  @private
     */
    public function get automationTabularData():Object
    {
        return null;    
    }

    /**
     *  @private
     */
    public function get showInAutomationHierarchy():Boolean
    {
        return true;
    }

    /**
     *  @private
     */
    public function set showInAutomationHierarchy(value:Boolean):void
    {
    }

    /**
     *  @private
     */
    public function get owner():DisplayObjectContainer
    {
        return null;
    }
    
    /**
     *  @private
     */
    public function replayAutomatableEvent(event:Event):Boolean
    {
        return false;
    }
    
    /**
     *  @private
     */
    public function set automationDelegate(val:Object):void
    {
        trace("Invalid setter function call. Should have been called on the component");
    }
    
    /**
     *  @private
     */
    public function get automationDelegate():Object
    {
        trace("Invalid setter function call. Should have been called on the component");
        return this;
    }
    
}

}