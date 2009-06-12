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
import mx.controls.LinkBar;
import mx.core.EventPriority;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  LinkBar control.
 * 
 *  @see mx.controls.LinkBar 
 *
 */
public class LinkBarAutomationImpl extends NavBarAutomationImpl 
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
        Automation.registerDelegateClass(LinkBar, LinkBarAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj LinkBar object to be automated.     
     */
    public function LinkBarAutomationImpl(obj:LinkBar)
    {
        super(obj);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get linkBar():LinkBar
    {
        return uiComponent as LinkBar;
    }
    
    /**
     *  @private
     */
    private var preventRecording:Boolean = false;
    

    /**
     *  @private
     */
    override public function recordAutomatableEvent(event:Event,
                                           cacheable:Boolean = false):void
    {
        if (!preventRecording)
            super.recordAutomatableEvent(event, cacheable);
        
        preventRecording = false;
    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        preventRecording = true;
    }
     
}

}