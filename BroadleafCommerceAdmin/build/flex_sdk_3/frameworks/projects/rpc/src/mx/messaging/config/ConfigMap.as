////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.messaging.config
{

import flash.utils.getQualifiedClassName;
import flash.utils.Proxy;
import flash.utils.flash_proxy;
import mx.utils.object_proxy;
import mx.utils.ObjectUtil;

use namespace flash_proxy;
use namespace object_proxy;

[RemoteClass(alias="flex.messaging.config.ConfigMap")]

/**
 *  The ConfigMap class provides a mechanism to store the properties returned 
 *  by the server with the ordering of the properties maintained. 
 */ 
public dynamic class ConfigMap extends Proxy
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     * Constructor.
     *
     * @param item An Object containing name/value pairs.
     */
    public function ConfigMap(item:Object = null)
    {
        super();

        if (!item)
            item = {};
        _item = item;
       
		propertyList = [];                                
    }    
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     *  Contains a list of all of the property names for the proxied object.
     */
    object_proxy var propertyList:Array;
            
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
     */
    override flash_proxy function getProperty(name:*):*
    {
        // if we have a data proxy for this then
        var result:Object = null;
            
        result = _item[name];
        
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
        var oldVal:Object = _item[name];
        var deleted:Boolean = delete _item[name]; 
		
        var deleteIndex:int = -1;
        for (var i:int = 0; i < propertyList.length; i++)
        {
        	if (propertyList[i] == name)
        	{
        		deleteIndex = i;
        		break;
        	}
        }
		if (deleteIndex > -1)
		{
			propertyList.splice(deleteIndex, 1);
		}
				
        return deleted;
    }

    /**
     *  This is an internal function that must be implemented by 
     *  a subclass of flash.utils.Proxy.
     *  
     *  @param name The property name that should be tested 
     *  for existence.
     *
     *  @return If the property exists, <code>true</code>; 
     *  otherwise <code>false</code>.
     *
     *  @see flash.utils.Proxy#hasProperty()
     */
    override flash_proxy function hasProperty(name:*):Boolean
    {
        return(name in _item);
    }

    /**
     *  This is an internal function that must be implemented by 
     *  a subclass of flash.utils.Proxy.
     *
     *  @param index The zero-based index value of the object's
     *  property.
     *
     *  @return The property's name.
     *
     *  @see flash.utils.Proxy#nextName()
     */
    override flash_proxy function nextName(index:int):String
    {
        return propertyList[index -1];
    }

    /**
     *  This is an internal function that must be implemented by 
     *  a subclass of flash.utils.Proxy.
     *
     *  @see flash.utils.Proxy#nextNameIndex()
     */
    override flash_proxy function nextNameIndex(index:int):int
    {        
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
     *  This is an internal function that must be implemented by 
     *  a subclass of flash.utils.Proxy.
     *
     *  @param index The zero-based index value of the object's
     *  property.
     *
     *  @return The property's value.
     *
     *  @see flash.utils.Proxy#nextValue()
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
            
			for (var i:int = 0; i < propertyList.length; i++)
        	{
        		if (propertyList[i] == name)
        		{
        			return;
	        	}
	        }
	        propertyList.push(name);
        }
    }              	
}

}
