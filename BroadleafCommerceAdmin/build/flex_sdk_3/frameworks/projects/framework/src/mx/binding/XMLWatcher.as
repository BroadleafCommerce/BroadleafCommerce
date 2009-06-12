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

package mx.binding 
{

import mx.core.mx_internal;
import mx.utils.XMLNotifier;
import mx.utils.IXMLNotifiable;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class XMLWatcher extends Watcher implements IXMLNotifiable
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
	 *  Constructor.
	 */
	public function XMLWatcher(propertyName:String, listeners:Array)
    {
		super(listeners);

        _propertyName = propertyName;
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
     *  The parent object of this property.
     */
    private var parentObj:Object;

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  propertyName
	//----------------------------------

	/**
     *  Storage for the propertyName property.
     */
    private var _propertyName:String;

    /**
     *  The name of the property this Watcher is watching.
     */
    public function get propertyName():String
    {
        return _propertyName;
    }

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Watcher
	//
	//--------------------------------------------------------------------------

    /**
     *  If the parent has changed we need to update ourselves
     */
    override public function updateParent(parent:Object):void
    {
        if (parentObj && (parentObj is XML || parentObj is XMLList))
            XMLNotifier.getInstance().unwatchXML(parentObj, this);

        if (parent is Watcher)
            parentObj = parent.value;
        else
            parentObj = parent;

        if (parentObj && (parentObj is XML || parentObj is XMLList))
            XMLNotifier.getInstance().watchXML(parentObj, this);

		// Now get our property.
        wrapUpdate(updateProperty);
    }

	/**
	 *  @private
	 */
    override protected function shallowClone():Watcher
    {
        return new XMLWatcher(_propertyName, listeners);
    }

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Gets the actual property then updates
	 *  the Watcher's children appropriately.
     */
    private function updateProperty():void
    {
        if (parentObj)
        {
            if (_propertyName == "this")
                value = parentObj;
            else
                value = parentObj[_propertyName];
        }
        else
        {
            value = null;
        }

        updateChildren();
    }

	/**
	 *  @private
	 */
    public function xmlNotification(currentTarget:Object, type:String,
							   target:Object, value:Object, detail:Object):void
    {
        updateProperty();

        notifyListeners(true);
    }
}

}
