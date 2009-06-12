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

import mx.automation.Automation;
import mx.automation.IAutomationObject;
import mx.automation.delegates.core.UITextFieldAutomationImpl;
import mx.controls.dataGridClasses.DataGridItemRenderer;
import mx.core.mx_internal;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  DataGridItemRenderer class.
 * 
 *  @see mx.controls.dataGridClasses.DataGridItemRenderer 
 *
 */
public class DataGridItemRendererAutomationImpl extends UITextFieldAutomationImpl 
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
        Automation.registerDelegateClass(DataGridItemRenderer, DataGridItemRendererAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj DataGridItem object to be automated.     
     */
    public function DataGridItemRendererAutomationImpl(obj:DataGridItemRenderer)
    {
        super(obj);
    }

    /**
     *  @private
     */
    protected function get itemRenderer():DataGridItemRenderer
    {
        return uiTextField as DataGridItemRenderer;
    }
    
}

}