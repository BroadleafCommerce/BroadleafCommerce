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

import flash.utils.Dictionary;
import mx.core.mx_internal;
import mx.utils.IXMLNotifiable;

use namespace mx_internal;

/**
 *  Used for watching changes to XML and XMLList objects.
 *  Those objects are not EventDispatchers, so if multiple elements
 *  want to watch for changes they need to go through this mechanism.
 *  Call <code>watchXML()</code>, passing in the same notification
 *  function that you would pass to XML.notification.
 *  Use <code>unwatchXML()</code> to remove that notification.
 *  
 */
public class XMLNotifier
{
	include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
	 *  @private
	 *  XMLNotifier is a singleton.
	 */
    private static var instance:XMLNotifier;

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Get the singleton instance of the XMLNotifier.
     */
    public static function getInstance():XMLNotifier
    {
        if (!instance)
            instance = new XMLNotifier(new XMLNotifierSingleton());

		return instance;
    }

    /**
	 *  @private
     *  Decorates an XML node with a notification function
	 *  that can fan out to multiple targets.
     */
    mx_internal static function initializeXMLForNotification():Function
    {
    	var notificationFunction:Function = function(currentTarget:Object,
													 ty:String,
													 tar:Object,
													 value:Object,
													 detail:Object):void
	    {
	        var xmlWatchers:Dictionary = arguments.callee.watched;
	        if (xmlWatchers != null)
	        {
	            for (var notifiable:Object in xmlWatchers)
	            {
	                IXMLNotifiable(notifiable).xmlNotification(currentTarget, ty, tar, value, detail);
	            }
	        }
	    }

	    return notificationFunction;
	}

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
	 *
	 *  XMLNotifier is a singleton class, so you do not use
	 *  the <code>new</code> operator to create multiple instances of it.
	 *  Instead, call the static method <code>XMLNotifider.getInstance()</code>
	 *  to get the sole instance of this class.
     */
    public function XMLNotifier(x:XMLNotifierSingleton)
    {
		super();
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Given an XML or XMLList, add the notification function
	 *  to watch for changes.
     *
     *  @param xml XML/XMLList object to watch.
     *  @param notification Function that needs to be called.
	 *  @param optional UID for object
     */
    public function watchXML(xml:Object, notifiable:IXMLNotifiable, uid:String = null):void
    {
		var xitem:XML = XML(xml);

		// First make sure the xml node has a notification function.
    	var watcherFunction:Object = xitem.notification();
    	if (!(watcherFunction is Function))
		{
    		watcherFunction = initializeXMLForNotification();
			xitem.setNotification(watcherFunction as Function);
			if (uid && watcherFunction["uid"] == null)
				watcherFunction["uid"] = uid;
		}

    	// Watch lists are maintained on the notification function.
		var xmlWatchers:Dictionary;
        if (watcherFunction["watched"] == undefined)
        	watcherFunction["watched"] = xmlWatchers = new Dictionary(true);
        else
        	xmlWatchers = watcherFunction["watched"];

        xmlWatchers[notifiable] = true;
    }

    /**
     *  Given an XML or XMLList, remove the specified notification function.
	 *
	 *  @param xml XML/XMLList object to un-watch.
     *  @param notification Function notification function.
     */
    public function unwatchXML(xml:Object, notifiable:IXMLNotifiable):void
    {
		var xitem:XML = XML(xml);

		var watcherFunction:Object = xitem.notification();
    	if (!(watcherFunction is Function))
			return;

		var xmlWatchers:Dictionary;
        if (watcherFunction["watched"] != undefined)
        {
            xmlWatchers = watcherFunction["watched"];
        	delete xmlWatchers[notifiable];
			
        }
    }
}

}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: XMLNotifierSingleton
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 */
class XMLNotifierSingleton
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function XMLNotifierSingleton()
	{
		super();
	}
}
