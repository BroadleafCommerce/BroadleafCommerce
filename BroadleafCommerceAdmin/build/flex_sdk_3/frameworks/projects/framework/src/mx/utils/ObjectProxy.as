////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.utils
{

import flash.events.Event;
import flash.events.EventDispatcher;
import flash.utils.getQualifiedClassName;
import flash.utils.IDataInput;
import flash.utils.IDataOutput;
import flash.utils.IExternalizable;
import flash.utils.Proxy;
import flash.utils.flash_proxy;
import mx.core.IPropertyChangeNotifier;
import mx.events.PropertyChangeEvent;
import mx.events.PropertyChangeEventKind;

use namespace flash_proxy;
use namespace object_proxy;

[Bindable("propertyChange")]
[RemoteClass(alias="flex.messaging.io.ObjectProxy")]

/**
 *  This class provides the ability to track changes to an item
 *  managed by this proxy.
 *  Any number of objects can "listen" for changes on this
 *  object, by using the <code>addEventListener()</code> method.
 *
 *  @example
 *  <pre>
 *  import mx.events.PropertyChangeEvent;
 *  import mx.utils.ObjectUtil;
 *  import mx.utils.ObjectProxy;
 *  import mx.utils.StringUtil;
 *
 *  var a:Object = { name: "Tyler", age: 5, ssnum: "555-55-5555" };
 *  var p:ObjectProxy = new ObjectProxy(a);
 *  p.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE, updateHandler);
 *  p.name = "Jacey";
 *  p.age = 2;
 *  delete p.ssnum;
 *
 *  // handler function
 *  function updateHandler(event:ChangeEvent):void
 *  {
 *      trace(StringUtil.substitute("updateHandler('{0}', {1}, {2}, {3}, '{4}')",
 *                                     event.kind,
 *                                     event.property,
 *                                     event.oldValue,
 *                                     event.newValue,
 *                                     event.target.object_proxy::UUID));
 *  }
 * 
 *  // The trace output appears as:
 *  // updateHandler('opUpdate', name, Jacey, '698AF8CB-B3D9-21A3-1AFFDGHT89075CD2')
 *  // updateHandler('opUpdate', age, 2, '698AF8CB-B3D9-21A3-1AFFDGHT89075CD2')
 *  // updateHandler('opDelete', ssnum, null, '698AF8CB-B3D9-21A3-1AFFDGHT89075CD2')
 *  </pre>
 */
public dynamic class ObjectProxy extends Proxy
                                 implements IExternalizable,
                                 IPropertyChangeNotifier
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Initializes this proxy with the specified object, id and proxy depth.
     * 
     *  @param item Object to proxy.
     *  If no item is specified, an anonymous object will be constructed
     *  and assigned.
     *
     *  @param uid String containing the unique id
     *  for this object instance.
     *  Required for IPropertyChangeNotifier compliance as every object must 
     *  provide a unique way of identifying it.
     *  If no value is specified, a random id will be assigned.
     *
     *  @param proxyDepth An integer indicating how many levels in a complex
     *  object graph should have a proxy created during property access.
     *  The default is -1, meaning "proxy to infinite depth".
     *  
     *  @example
     *
     *  <pre>
     *  import mx.events.PropertyChangeEvent;
     *  import mx.utils.ObjectUtil;
     *  import mx.utils.ObjectProxy;
     *  import mx.utils.StringUtil;
     *
     *  var a:Object = { name: "Tyler", age: 5, ssnum: "555-55-5555" };
     *  var p:ObjectProxy = new ObjectProxy(a);
     *  p.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE, updateHandler);
     *  p.name = "Jacey";
     *  p.age = 2;
     *  delete p.ssnum;
     *
     *  // handler function
     *  function updateHandler(event:PropertyChangeEvent):void
     *  {
     *      trace(StringUtil.substitute("updateHandler('{0}', {1}, {2}, {3}, '{4}')",
     *                                     event.kind,
     *                                     event.property,
     *                                     event.oldValue,
     *                                     event.newValue,
     *                                     event.target.uid));
     *  }
     *
     *  // trace output
     *  updateHandler('opUpdate', name, Jacey, '698AF8CB-B3D9-21A3-1AFFDGHT89075CD2')
     *  updateHandler('opUpdate', age, 2, '698AF8CB-B3D9-21A3-1AFFDGHT89075CD2')
     *  updateHandler('opDelete', ssnum, null, '698AF8CB-B3D9-21A3-1AFFDGHT89075CD2')
     *  </pre>
     */
    public function ObjectProxy(item:Object = null, uid:String = null,
                                proxyDepth:int = -1)
    {
        super();

        if (!item)
            item = {};
        _item = item;

        _proxyLevel = proxyDepth;
       
        notifiers = {};

        dispatcher = new EventDispatcher(this);

        // If we got an id, use it.  Otherwise the UID is lazily
        // created in the getter for UID.
        if (uid)
            _id = uid;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  A reference to the EventDispatcher for this proxy.
     */
    protected var dispatcher:EventDispatcher;

    /**
     *  A hashmap of property change notifiers that this proxy is 
     *  listening for changes from; the key of the map is the property name.
     */
    protected var notifiers:Object;
    
    /**
     *  Indicates what kind of proxy to create
     *  when proxying complex properties.
     *  Subclasses should assign this value appropriately.
     */
    protected var proxyClass:Class = ObjectProxy;
    
    /**
     *  Contains a list of all of the property names for the proxied object.
     *  Descendants need to fill this list by overriding the
     *  <code>setupPropertyList()</code> method.
     */
    protected var propertyList:Array;
    
    /**
     *  Indicates how deep proxying should be performed.
     *  If -1 (default), always proxy; 
     *  if this value is zero, no proxying will be performed.
     */
    private var _proxyLevel:int;
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  object
    //----------------------------------

    /**
     *  Storage for the object property.
     */
    private var _item:Object;

    /**
     *  The object being proxied.
     */
    object_proxy function get object():Object
    {
        return _item;
    }

    //----------------------------------
    //  type
    //----------------------------------

    /**
     *  @private
     *  Storage for the qualified type name.
     */
    private var _type:QName;

    /**
     *  The qualified type name associated with this object.
     */
    object_proxy function get type():QName
    {
        return _type;
    }

    /**
     *  @private
     */
    object_proxy function set type(value:QName):void
    {
        _type = value;
    }

    //----------------------------------
    //  uid
    //----------------------------------

    /**
     *  @private
     *  Storage for the uid property.
     */
    private var _id:String;

    /**
     *  The unique identifier for this object.
     */
    public function get uid():String
    {
    	if (_id === null)
            _id = UIDUtil.createUID();
            
        return _id;
    }

    /**
     *  @private
     */
    public function set uid(value:String):void
    {
        _id = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns the specified property value of the proxied object.
     *
     *  @param name Typically a string containing the name of the property,
     *  or possibly a QName where the property name is found by 
     *  inspecting the <code>localName</code> property.
     *
     *  @return The value of the property.
     *  In some instances this value may be an instance of 
     *  <code>ObjectProxy</code>.
     */
    override flash_proxy function getProperty(name:*):*
    {
        // if we have a data proxy for this then
        var result:*;

        if (notifiers[name.toString()])
            return notifiers[name];

        result = _item[name];

        if (result)
        {
            if (_proxyLevel == 0 || ObjectUtil.isSimple(result))
            {
                return result;
            }
            else
            {
                result = object_proxy::getComplexProperty(name, result);
            } // if we are proxying
        }

        return result;
    }

    /**
     *  Returns the value of the proxied object's method with the specified name.
     *
     *  @param name The name of the method being invoked.
     *
     *  @param rest An array specifying the arguments to the
     *  called method.
     *
     *  @return The return value of the called method.
     */
    override flash_proxy function callProperty(name:*, ... rest):*
    {
        return _item[name].apply(_item, rest)
    }

    /**
     *  Deletes the specified property on the proxied object and
     *  sends notification of the delete to the handler.
     * 
     *  @param name Typically a string containing the name of the property,
     *  or possibly a QName where the property name is found by 
     *  inspecting the <code>localName</code> property.
     *
     *  @return A Boolean indicating if the property was deleted.
     */
    override flash_proxy function deleteProperty(name:*):Boolean
    {
        var notifier:IPropertyChangeNotifier = IPropertyChangeNotifier(notifiers[name]);
        if (notifier)
        {
            notifier.removeEventListener(PropertyChangeEvent.PROPERTY_CHANGE,
                                         propertyChangeHandler);
            delete notifiers[name];
        }

        var oldVal:* = _item[name];
        var deleted:Boolean = delete _item[name]; 

        if (dispatcher.hasEventListener(PropertyChangeEvent.PROPERTY_CHANGE))
        {
            var event:PropertyChangeEvent = new PropertyChangeEvent(PropertyChangeEvent.PROPERTY_CHANGE);
            event.kind = PropertyChangeEventKind.DELETE;
            event.property = name;
            event.oldValue = oldVal;
            event.source = this;
            dispatcher.dispatchEvent(event);
        }

        return deleted;
    }

    /**
     *  @private
     */
    override flash_proxy function hasProperty(name:*):Boolean
    {
        return(name in _item);
    }
    
    /**
     *  @private
     */
    override flash_proxy function nextName(index:int):String
    {
        return propertyList[index -1];
    }
    
    /**
     *  @private
     */
    override flash_proxy function nextNameIndex(index:int):int
    {
        if (index == 0)
        {
            setupPropertyList();
        }
        
        if (index < propertyList.length)
        {
            return index + 1;
        }
        else
        {
            return 0;
        }
    }
    
    /**
     *  @private
     */
    override flash_proxy function nextValue(index:int):*
    {
        return _item[propertyList[index -1]];
    }

    /**
     *  Updates the specified property on the proxied object
     *  and sends notification of the update to the handler.
     *
     *  @param name Object containing the name of the property that
     *  should be updated on the proxied object.
     *
     *  @param value Value that should be set on the proxied object.
     */
    override flash_proxy function setProperty(name:*, value:*):void
    {
        var oldVal:* = _item[name];
        if (oldVal !== value)
        {
            // Update item.
            _item[name] = value;

            // Stop listening for events on old item if we currently are.
            var notifier:IPropertyChangeNotifier =
                IPropertyChangeNotifier(notifiers[name]);
            if (notifier)
            {
                notifier.removeEventListener(
                    PropertyChangeEvent.PROPERTY_CHANGE,
                    propertyChangeHandler);
                delete notifiers[name];
            }

            // Notify anyone interested.
            if (dispatcher.hasEventListener(PropertyChangeEvent.PROPERTY_CHANGE))
            {
                if (name is QName)
                    name = QName(name).localName;
                var event:PropertyChangeEvent =
                    PropertyChangeEvent.createUpdateEvent(
                        this, name.toString(), oldVal, value);
                dispatcher.dispatchEvent(event);
            } 
        }
    }

    //--------------------------------------------------------------------------
    //
    //  object_proxy methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Provides a place for subclasses to override how a complex property that
     *  needs to be either proxied or daisy chained for event bubbling is managed.
     * 
     *  @param name Typically a string containing the name of the property,
     *  or possibly a QName where the property name is found by 
     *  inspecting the <code>localName</code> property.
     *
     *  @param value The property value.
     *
     *  @return The property value or an instance of <code>ObjectProxy</code>.
     */  
    object_proxy function getComplexProperty(name:*, value:*):*
    {
        if (value is IPropertyChangeNotifier)
        {
            value.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE,
                                   propertyChangeHandler);
            notifiers[name] = value;
            return value;
        }
        
        if (getQualifiedClassName(value) == "Object")
        {
            value = new proxyClass(_item[name], null,
                _proxyLevel > 0 ? _proxyLevel - 1 : _proxyLevel);
            value.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE,
                                   propertyChangeHandler);
            notifiers[name] = value;
            return value;
        }

        return value;
    }
    
    //--------------------------------------------------------------------------
    //
    //  IExternalizable Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Since Flex only uses ObjectProxy to wrap anonymous objects,
     *  the server flex.messaging.io.ObjectProxy instance serializes itself
     *  as a Map that will be returned as a plain ActionScript object. 
     *  You can then set the object_proxy object property to this value.
     *
     *  @param input The source object from which the ObjectProxy is
     *  deserialized. 
     */
    public function readExternal(input:IDataInput):void
    {
        var value:Object = input.readObject();
        _item = value;
    }

    /**
     *  Since Flex only serializes the inner ActionScript object that it wraps,
     *  the server flex.messaging.io.ObjectProxy populates itself
     *  with this anonymous object's contents and appears to the user
     *  as a Map.
     *
     *  @param output The source object from which the ObjectProxy is
     *  deserialized.
     */
    public function writeExternal(output:IDataOutput):void
    {
        output.writeObject(_item);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Registers an event listener object  
     *  so that the listener receives notification of an event. 
     *  For more information, including descriptions of the parameters see 
     *  <code>addEventListener()</code> in the 
     *  flash.events.EventDispatcher class.
     *
     *  @see flash.events.EventDispatcher#addEventListener()
     */
    public function addEventListener(type:String, listener:Function,
                                     useCapture:Boolean = false,
                                     priority:int = 0,
                                     useWeakReference:Boolean = false):void
    {
        dispatcher.addEventListener(type, listener, useCapture,
                                    priority, useWeakReference);
    }

    /**
     *  Removes an event listener. 
     *  If there is no matching listener registered with the EventDispatcher object, 
     *  a call to this method has no effect.
     *  For more information, see 
     *  the flash.events.EventDispatcher class.
     *  
     *  @param type The type of event.
     * 
     *  @param listener The listener object to remove.
     *
     *  @param useCapture Specifies whether the listener was registered for the capture 
     *  phase or the target and bubbling phases. If the listener was registered for both 
     *  the capture phase and the target and bubbling phases, two calls to 
     *  <code>removeEventListener()</code> are required to remove both, one call with 
     *  <code>useCapture</code> 
     *  set to <code>true</code>, and another call with <code>useCapture</code>
     *  set to <code>false</code>.
     *
     *  @see flash.events.EventDispatcher#removeEventListener()
     */
    public function removeEventListener(type:String, listener:Function,
                                        useCapture:Boolean = false):void
    {
        dispatcher.removeEventListener(type, listener, useCapture);
    }

    /**
     *  Dispatches an event into the event flow. 
     *  For more information, see
     *  the flash.events.EventDispatcher class.
     *  
     *  @param event The Event object that is dispatched into the event flow. If the 
     *  event is being redispatched, a clone of the event is created automatically. 
     *  After an event is dispatched, its target property cannot be changed, so you 
     *  must create a new copy of the event for redispatching to work.
     *
     *  @return Returns <code>true</code> if the event was successfully dispatched. 
     *  A value 
     *  of <code>false</code> indicates failure or that <code>preventDefault()</code>
     *  was called on the event.
     *
     *  @see flash.events.EventDispatcher#dispatchEvent()
     */
    public function dispatchEvent(event:Event):Boolean
    {
        return dispatcher.dispatchEvent(event);
    }
    
    /**
     *  Checks whether there are any event listeners registered 
     *  for a specific type of event. 
     *  This allows you to determine where an object has altered handling 
     *  of an event type in the event flow hierarchy. 
     *  For more information, see
     *  the flash.events.EventDispatcher class.
     *
     *  @param type The type of event
     *
     *  @return Returns <code>true</code> if a listener of the specified type is 
     *  registered; <code>false</code> otherwise.
     *
     *  @see flash.events.EventDispatcher#hasEventListener()
     */
    public function hasEventListener(type:String):Boolean
    {
        return dispatcher.hasEventListener(type);
    }
    
    /**
     *  Checks whether an event listener is registered with this object 
     *  or any of its ancestors for the specified event type. 
     *  This method returns <code>true</code> if an event listener is triggered 
     *  during any phase of the event flow when an event of the specified 
     *  type is dispatched to this object or any of its descendants.
     *  For more information, see the flash.events.EventDispatcher class.
     *
     *  @param type The type of event.
     *
     *  @return Returns <code>true</code> if a listener of the specified type will 
     *  be triggered; <code>false</code> otherwise.
     *
     *  @see flash.events.EventDispatcher#willTrigger()
     */
    public function willTrigger(type:String):Boolean
    {
        return dispatcher.willTrigger(type);
    }

    /**
     *  Called when a complex property is updated.
     *
     *  @param event An event object that has changed.
     */
    public function propertyChangeHandler(event:PropertyChangeEvent):void
    {
        dispatcher.dispatchEvent(event);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  This method creates an array of all of the property names for the 
     *  proxied object.
     *  Descendants must override this method if they wish to add more 
     *  properties to this list.
     *  Be sure to call <code>super.setupPropertyList</code> before making any
     *  changes to the <code>propertyList</code> property.
     */
    protected function setupPropertyList():void
    {
        if (getQualifiedClassName(_item) == "Object")
        {
            propertyList = [];
            for (var prop:String in _item)
                propertyList.push(prop);
        }
        else
        {
            propertyList = ObjectUtil.getClassInfo(_item, null, {includeReadOnly:true, uris:["*"]}).properties;
        }
    }
}

}
