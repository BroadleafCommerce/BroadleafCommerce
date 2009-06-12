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

package mx.automation.delegates.containers 
{
import flash.display.DisplayObject;

import mx.automation.Automation;
import mx.automation.delegates.core.ContainerAutomationImpl;
import mx.containers.Form;
import mx.core.mx_internal;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines the methods and properties required to perform instrumentation for the 
 *  Form class. 
 * 
 *  @see mx.containers.Form
 *  
 */
public class FormAutomationImpl extends ContainerAutomationImpl {

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
        Automation.registerDelegateClass(Form, FormAutomationImpl);
    }   
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj Form object to be automated.     
     */
    public function FormAutomationImpl(obj:Form)
    {
        super(obj);
        recordClick = true;
    }

}

}
