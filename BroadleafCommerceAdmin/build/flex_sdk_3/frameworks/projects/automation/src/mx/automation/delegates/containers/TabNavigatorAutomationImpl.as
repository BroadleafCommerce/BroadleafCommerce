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
import flash.events.Event;

import mx.automation.Automation;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationTabularData;
import mx.automation.events.AutomationRecordEvent;
import mx.containers.TabNavigator;
import mx.core.mx_internal;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines the methods and properties required to perform instrumentation for the 
 *  TabNavigator class. 
 * 
 *  @see mx.containers.TabNavigator
 *  
 */
public class TabNavigatorAutomationImpl extends ViewStackAutomationImpl 
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
        Automation.registerDelegateClass(TabNavigator, TabNavigatorAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj TabNavigator object to be automated.     
     */
    public function TabNavigatorAutomationImpl(obj:TabNavigator)
    {
        super(obj);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get tabNavigator():TabNavigator
    {
        return uiComponent as TabNavigator;         
    }
    
    /**
     *  @private
     */
    override public function get automationTabularData():Object
    {
        var delegate:IAutomationObject 
                = tabNavigator.getTabBar() as IAutomationObject;
                
        return delegate.automationTabularData;
    }


    /**
     *  Replays ItemClickEvents by dispatching a MouseEvent to the item that was
     *  clicked.
     *  
     *  @param interaction The event to replay.
     *  
     *  @return <code>true</code> if the replay was successful. Otherwise, returns <code>false</code>.
     */
    override public function replayAutomatableEvent(interaction:Event):Boolean
    {
        var replayer:IAutomationObject = 
                    tabNavigator.getTabBar() as IAutomationObject ;
        return replayer.replayAutomatableEvent(interaction);
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
        tabNavigator.getTabBar().addEventListener(AutomationRecordEvent.RECORD,
                                    tabBar_recordHandler, false, 0, true);
    }

    /**
     *  @private
     */
    private function tabBar_recordHandler(event:AutomationRecordEvent):void
    {
        recordAutomatableEvent(event.replayableEvent);
    }

    }
}