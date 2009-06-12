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

package mx.messaging
{
 
import flash.events.EventDispatcher; 
    
import mx.core.mx_internal;
import mx.events.PropertyChangeEvent;

use namespace mx_internal;
    
/**
 *  Dispatched when a property of the FlexClient singleton changes.
 *  Listeners must be added via FlexClient.getInstance().addEventListener(...).
 * 
 *  @eventType mx.events.PropertyChangeEvent.PROPERTY_CHANGE
 */
[Event(name="propertyChange", type="mx.events.PropertyChangeEvent")]    
    
/**
 *  Singleton class that stores the global Id for this Player instance that is 
 *  server assigned when the client makes its initial connection to the server.
 */
public class FlexClient extends EventDispatcher
{
    //--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     *  This value is passed to the server in an initial client connect to
     *  indicate that the client needs a server-assigned FlexClient Id.
     */
    mx_internal static const NULL_FLEXCLIENT_ID:String = "nil";

    //--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  The sole instance of this singleton class.
     */
	private static var _instance:FlexClient;
    
	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Returns the sole instance of this singleton class,
	 *  creating it if it does not already exist.
         *
         *  @return Returns the sole instance of this singleton class,
	 *  creating it if it does not already exist.
	 */
	public static function getInstance():FlexClient
	{
		if (_instance == null)
			_instance = new FlexClient();

		return _instance;
	}

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Constructor.
	 */
	public function FlexClient()
	{
		super();
	}    
    
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  id
	//----------------------------------

	/**
	 *  @private
     *  Storage for the global FlexClient Id for the Player instance. 
     *  This value is server assigned and is set as part of the Channel connect process.
     */	
    private var _id:String;
    
    [Bindable(event="propertyChange")]
    /**
     *  The global FlexClient Id for this Player instance.
     *  This value is server assigned and is set as part of the Channel connect process.
     *  Once set, it will not change for the duration of the Player instance's lifespan.
     *  If no Channel has connected to a server this value is null.
     */
    public function get id():String
    {
        return _id;
    } 
        
	/**
	 *  @private
	 */
    public function set id(value:String):void
    {
        if (_id != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "id", _id, value);
            _id = value;
            dispatchEvent(event);
        }        
    }
    
	//----------------------------------
	//  waitForFlexClientId
	//----------------------------------
            
    /**
     *  @private
     */
    private var _waitForFlexClientId:Boolean = false; // Initialize to false so the first Channel that checks this can attempt to connect.
    
    [Bindable(event="propertyChange")]
    /**
     *  @private 
     *  Guard condition that Channel instances use to coordinate their connect attempts during application startup
     *  when a FlexClient Id has not yet been returned by the server.
     *  The initial Channel connect process must be serialized.
     *  Once a FlexClient Id is set further Channel connects and disconnects do not require synchronization.
     */
    mx_internal function get waitForFlexClientId():Boolean
    {
        return _waitForFlexClientId;
    }
    
    /**
     *  @private
     */
    mx_internal function set waitForFlexClientId(value:Boolean):void
    {
        if (_waitForFlexClientId != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "waitForFlexClientId", _waitForFlexClientId, value);
            _waitForFlexClientId = value;
            dispatchEvent(event);
        }
    }
}

}
