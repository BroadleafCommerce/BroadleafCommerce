////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

import flash.events.IEventDispatcher;

/**
 *  The <code>IPropertyChangeNotifier</code> interface defines a marker 
 *  interface.
 *  Classes that support this interface declare support for event propagation
 *  in a specialized manner.
 *  Classes that implement this interface must dispatch events for each property
 *  of this class and any nested classes publicly exposed as properties.
 *  For those properties that are anonymous (complex and not strongly typed),
 *  implementing classes provide custom support or directly use the
 *  ObjectProxy class.
 *  Implementors of this interface should use the 
 *  <code>PropertyChangeEvent.createUpdateEvent()</code> method to construct an
 *  appropriate update event for dispatch.
 *  @example
 *  <code><pre>
 *   
 * function set myProperty(value:Object):void
 * {
 *    var oldValue:IPropertyChangeNotifier = _myProperty;
 *    var newValue:IPropertyChangeNotifier = value;
 *    
 *    // Need to ensure to dispatch changes on the new property.
 *    // Listeners use the source property to determine which object 
 *    // actually originated the event.
 *    // In their event handler code, they can tell if an event has been 
 *    // propagated from deep within the object graph by comparing 
 *    // event.target and event.source. If they are equal, then the property
 *    // change is at the surface of the object. If they are not equal, the
 *    // property change is somewhere deeper in the object graph.
 *    newValue.addEventListener(
 *                PropertyChangeEvent.PROPERTY_CHANGE, 
 *                dispatchEvent);
 * 
 *    // need to stop listening for events from the old property
 *    oldValue.removeEventListener(
 *                PropertyChangeEvent.PROPERTY_CHANGE,
 *                dispatchEvent);
 * 
 *    _myProperty = newValue;
 * 
 *    // now notify anyone that is listening
 *    if (dispatcher.hasEventListener(PropertyChangeEvent.PROPERTY_CHANGE))
 *    {
 *         var event:PropertyChangeEvent = 
 *                         PropertyChangeEvent.createUpdateEvent(
 *                                                       this,
 *                                                       "myProperty",
 *                                                       newValue,
 *                                                       oldValue);
 *        dispatchEvent(event);
 *     }
 *  }
 * 
 *      
 *  </pre></code>
 */
public interface IPropertyChangeNotifier extends IEventDispatcher, IUID
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

	// Inherits uid property from IUID
}

}
