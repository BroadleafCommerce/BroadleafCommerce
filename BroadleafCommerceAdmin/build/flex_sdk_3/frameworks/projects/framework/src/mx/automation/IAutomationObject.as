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

package mx.automation
{

import flash.events.Event;
import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.events.MouseEvent;

/**
 * The IAutomationObject interface defines the interface 
 * for a delegate object that implements automation
 * for a component.
 */
public interface IAutomationObject 
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  automationDelegate
    //----------------------------------

    /**
     *  The delegate object that is handling the automation-related functionality.
     *  Automation sets this when it creates the delegate object.
     */
    function get automationDelegate():Object;

    /**
     *  @private
     */
    function set automationDelegate(delegate:Object):void;

    //----------------------------------
    //  automationName
    //----------------------------------

    /**
     *  Name that can be used as an identifier for this object.
     */
    function get automationName():String;

    /**
     *  @private
     */
    function set automationName(name:String):void;

    //----------------------------------
    //  automationValue
    //----------------------------------

    /**
     *  This value generally corresponds to the rendered appearance of the 
     *  object and should be usable for correlating the identifier with
     *  the object as it appears visually within the application.
     */
    function get automationValue():Array;
 
    /**
     *  Returns a set of properties that identify the child within 
     *  this container.  These values should not change during the
     *  lifespan of the application.
     *  
     *  @param child Child for which to provide the id.
     * 
     *  @return Sets of properties describing the child which can
     *          later be used to resolve the component.
     */
    function createAutomationIDPart(child:IAutomationObject):Object;

    /**
     *  Resolves a child by using the id provided. The id is a set 
     *  of properties as provided by the <code>createAutomationIDPart()</code> method.
     *
     *  @param criteria Set of properties describing the child.
     *         The criteria can contain regular expression values
     *         resulting in multiple children being matched.
     *  @return Array of children that matched the criteria
     *          or <code>null</code> if no children could not be resolved.
     */
    function resolveAutomationIDPart(criteria:Object):Array;

    /** 
     *  Provides the automation object at the specified index.  This list
     *  should not include any children that are composites.
     *
     *  @param index The index of the child to return
     * 
     *  @return The child at the specified index.
     */
    function getAutomationChildAt(index:int):IAutomationObject;

    /**
     *  The number of automation children this container has.
     *  This sum should not include any composite children, though
     *  it does include those children not significant within the
     *  automation hierarchy.
     */
    function get numAutomationChildren():int;

    /** 
     *  A flag that determines if an automation object
     *  shows in the automation hierarchy.
     *  Children of containers that are not visible in the hierarchy
     *  appear as children of the next highest visible parent.
     *  Typically containers used for layout, such as boxes and Canvas,
     *  do not appear in the hierarchy.
     *
     *  <p>Some controls force their children to appear
     *  in the hierarchy when appropriate.
     *  For example a List will always force item renderers,
     *  including boxes, to appear in the hierarchy.
     *  Implementers must support setting this property
     *  to <code>true</code>.</p>
     */
    function get showInAutomationHierarchy():Boolean;

    /**
     *  @private
     */
    function set showInAutomationHierarchy(value:Boolean):void;
   
    /**
     * An implementation of the <code>IAutomationTabularData</code> interface, which 
     * can be used to retrieve the data.
     * 
     * @return An implementation of the <code>IAutomationTabularData</code> interface.
     */
    function get automationTabularData():Object;

    /**
     *  Replays the specified event.  A component author should probably call 
     *  super.replayAutomatableEvent in case default replay behavior has been defined 
     *  in a superclass.
     *
     *  @param event The event to replay.
     *
     *  @return <code>true</code> if a replay was successful.  
     */
    function replayAutomatableEvent(event:Event):Boolean;
    
}

}
