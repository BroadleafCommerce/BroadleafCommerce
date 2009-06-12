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
 
import flash.display.DisplayObject;
import flash.errors.IllegalOperationError;
import flash.events.StatusEvent;    
import mx.core.mx_internal;    
import mx.collections.ArrayCollection;
import mx.collections.ListCollectionView;
import mx.events.PropertyChangeEvent;
import mx.messaging.messages.IMessage;   
import mx.rpc.AsyncDispatcher;
import mx.rpc.AsyncToken;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;
import mx.rpc.Fault;

use namespace mx_internal;    

/**
 *  Dispatched when the status of the message queue changes.
 * 
 *  @see mx.messaging.events.MessageQueueStatusCode
 *
 *  @eventType flash.events.StatusEvent.STATUS
 */
[Event(name="status", type="flash.events.StatusEvent")]    

/**
 * The result event is dispatched when an asynchronous operation of
 * the message queue completes successfully.
 * 
 * @eventType mx.rpc.events.ResultEvent.RESULT 
 */
[Event(name="result", type="mx.rpc.events.ResultEvent")]

/**
 * The fault event is dispatched when an asynchronous operation of
 * the message queue fails.
 * 
 * @eventType mx.rpc.events.FaultEvent.FAULT 
 */
[Event(name="fault", type="mx.rpc.events.FaultEvent")]

// Commenting out to avoid warnings while building rpc.swc dependencies.
//[Deprecated(since="3.0.0")]

[ExcludeClass]

/**
 * @private
 *  The base class for message queue implementations.
 * 
 * NOTE: This class was deprecated in Flex 3.0 and should no longer be used.
 */   
public class AbstractMessageStore extends ArrayCollection
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    [Deprecated(since="3.0.0")]
    /**
     *  Constructs an AbstractMessageStore.
     */
    public function AbstractMessageStore() 
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

	//----------------------------------
	//  autoSend
	//----------------------------------

    private var _autoSend:Boolean;
    
    [Bindable(event="propertyChange")]
    
    /**
     *  Indicates whether queued messages are automatically sent upon
     *  connect or reconnect of the associated Producer.
     */
    public function get autoSend():Boolean
    {
        return _autoSend;
    }
    
    public function set autoSend(value:Boolean):void
    {
        if (_autoSend != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "autoSend", _autoSend, value);
            _autoSend = value;
            
            attemptToSendOrConnect();
            
            dispatchEvent(event);
        }
    }
    
	//----------------------------------
	//  cacheID
	//----------------------------------
	
	private var _cacheId:String;
	
	[Bindable(event="propertyChange")]
	
    /**
     *  Provides access to the cache identifier for this service.
     *  A cache identifier must be set prior to performing any operations that
     *  require interaction with data stored locally on disk.
     *  If a cache identifier is not set all cache methods and properties are 
     *  considered inconsistent and a <code>IllegalOperationError</code> will be 
     *  thrown during any operation that requires data from the local disk.
     *  This property is provides a unique "session" identifier for data stored 
     *  locally. 
     *  A developer must set this property to a unique value for the 
     *  application.
     *  A value of <code>null</code> or empty string is considered unset. 
     */  
    public function get cacheID():String
    {
        return _cacheId;
    }
    
    public function set cacheID(value:String):void
    {
        if (_cacheId != value)
        {
            _initialized = value == null;
            _cacheId = value;
            
            internalSetCacheId(value);
        }
    }
    
	//----------------------------------
	//  isInitialized
	//----------------------------------

    // only not initialized by default if there is a cacheID set otherwise
    // this store is in-memory only and is ready to use at construction time
    // for details see setter for cacheID
    protected var _initialized:Boolean = true;
    
    [Bindable(event="propertyChange")]
    /**
     *  Indicates if the store has been initialized.
     *  If not initialized call the <code>initialize()</code> method before
     *  using this store.
     */
     public function get isInitialized():Boolean
     {
         return _initialized;
     }
     
	//----------------------------------
	//  loaded
	//----------------------------------

    private var _loaded:Boolean;
    
    [Bindable(event="propertyChange")]
    
    /**
     *  True if the persistent state for the store has been loaded.
     */
    public function get loaded():Boolean
    {
        return _loaded;
    }

	//----------------------------------
	//  producer
	//----------------------------------
    
    private var _producer:AbstractProducer;
    
    [Bindable(event="propertyChange")]
    
    /**
     *  The Producer the queue is assigned to.
     */
    public function get producer():AbstractProducer
    {
        return _producer;
    }
    
    //--------------------------------------------------------------------------
    //
    // Public Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  Adds the passed message to the queue.
     *  If the queue is configured to autoSend and the associated Producer is connected
     *  the message is sent directly rather than being queued.
     * 
     *  @param item The message to add to the queue.
     */
    override public function addItem(item:Object):void
    {
        var msg:IMessage = IMessage(item);
        if (msg != null)
        {
            if (autoSend && producer != null && producer.connected)
                producer.send(msg);
            else
                super.addItem(msg);
        }
    }    

    /**
     *  Clears out the persistent store and the in-memory state for the MessageStore.
     *  Add a responder to the returned token to handle success or failure.
     *  Alternately add any desired data to the token and listen for general
     *  ResultEvents and FaultEvents dispatched by the MessageStore.
     * 
     *  @throws IllegalOperationError if no cacheID has been set
     */
    public function clearCache():AsyncToken
    {
        checkCacheId();
	    var token:AsyncToken = new AsyncToken(null);
	    var ms:AbstractMessageStore = this;
        var success:Function = function():void
        {
            ms.internalClearCache(token);
        }
        
        var failed:Function = function():void
        {
	        dispatchFaultEvent(getInitFailedFault(), token);
        }
        
        if (!isInitialized)
            internalInitialize(success, failed);
        else
            new AsyncDispatcher(success, [], 1);
        
        return token; 	            
    }
    
    /**
     *  This method will fill the specified <code>ListCollectionView</code>
     *  with all cache identifiers previously used in the application.
     * 
     *  @param view ListcollectionView reference to a collection that should be
     *  filled with all cache identifiers previously used in the application.
	 *  @return AsyncToken reference to the token that will identify this 
	 *  operation in a result or fault event dispatched from this service.
     */
    public function getCacheIDs(view:ListCollectionView):AsyncToken
    {
        var result:AsyncToken = new AsyncToken(null);
        internalGetCacheIDs(view, result);
        return result;
    }
    
    
    /**
     *  Intializes the message store.
     * 
     *  @return AsyncToken which can be used to respond to the success or 
     *  failure of the operation.
     */
    public function initialize():AsyncToken
    {
        var result:AsyncToken = new AsyncToken(null);
        var success:Function = function():void
        {
            var event:ResultEvent = ResultEvent.createEvent(null, result);
            dispatchResultEvent(event, result);
        }
        
        var failed:Function = function():void
        {
	        dispatchFaultEvent(getInitFailedFault(), result);
        }
        
        if (!isInitialized)
            internalInitialize(success, failed);
        else
            new AsyncDispatcher(success, [], 1);
            
	    return result;
    }
    
    /**
     *  Loads the current persistent messages for the queue after clearing out
     *  its current in-memory state.
     *  This method can be used to return the store to its last known good
     *  persistent state if the addition of a message overruns the allowed 
     *  persistent storage space and fails.
     *  A <code>cacheID</code> must be set before this method can be called.
     * 
     *  @throws IllegalOperationError if the <code>cacheID</code> property is 
     *  invalid.
     *  @see mx.messaging.MessageStore#cacheID
     */
    public function loadCache():AsyncToken
    {
        checkCacheId();
	    var token:AsyncToken = new AsyncToken(null);
	    var ms:AbstractMessageStore = this;
        var success:Function = function():void
        {
            ms.internalLoadCache(token);
        }
        
        var failed:Function = function():void
        {
	        dispatchFaultEvent(getInitFailedFault(), token);
        }
        
        if (!isInitialized)
            internalInitialize(success, failed);
        else
            new AsyncDispatcher(success, [], 1);
        
        return token; 	            
    }
    
    /**
     *  Releases resources used by this MessageStore.
     *  Any pending unsaved changes are not pesisted when this method is invoked.
     *  If this method is invoked before the MessageStore has initialized or 
     *  after it has been released it is a no-op; however it will still return 
     *  a token and dispatch a ResultEvent.
     */
    public function release():AsyncToken
    {     
        // Commenting out as AbstractProducer no longer has a message store.
        //if (producer != null)
        //    producer.messageStore = null;
            
        setLoaded(false);  
        
        var token:AsyncToken = new AsyncToken(null);
        internalRelease(token); 
        return token;
    }
    
    /**
     *  Saves any manual modifations that have been made to messages within the 
     *  Store.
     *  The MessageStore only automatically saves when messages are added or 
     *  removed as the result of a Producer.send() call.  
     *  Any other manual modifications are not saved unless saveCache() is 
     *  called.
     *  Add a responder to the returned token to handle success or failure.
     *  Alternately add any desired data to the token and listen for general
     *  ResultEvents and FaultEvents dispatched by the MessageStore.
     * 
     *  @throws IllegalOperationError If the cacheID has not been set.
     */
    public function saveCache():AsyncToken
    {
        checkCacheId();
	    var token:AsyncToken = new AsyncToken(null);
	    var ms:AbstractMessageStore = this;
        var success:Function = function():void
        {
            ms.internalSaveCache(token);
        }
        
        var failed:Function = function():void
        {
	        dispatchFaultEvent(getInitFailedFault(), token);
        }
        
        if (!isInitialized)
            internalInitialize(success, failed);
        else
            new AsyncDispatcher(success, [], 1);
        
        return token; 	            
    }
    
    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------
   
    /**
     *  Invoked by the associated Producer to add a message to the store. 
     *  Subclasses may override to persist the message or perform other custom 
     *  processing. Messages that are added to the store must be tagged with a 
     *  DSMessageStore header by the store implementation. 
     *
     *  @param msg The message to queue. 
     */
    mx_internal function addMessage(msg:IMessage):void {}
    
    /**
     *  Invoked by the associated Producer to remove a message from the store.
     *  This method is called when the store has autoSend = false and the 
     *  application passes a queued message to the Producer to send. 
     *  The producer calls this method to ensure that the message is fully 
     *  removed from the store before being sent over the network.
     *
     *  @param msg The message to remove.
     */
    mx_internal function removeMessage(msg:IMessage):void {}
    
    /**
     *  Sets the producer for this message store.
     *  This method is called by the AbstractProducer when it's messageStore
     *  property is set.
     */
    mx_internal function setProducer(value:AbstractProducer):void
    {
        if (_producer != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "producer", _producer, value);            
            
            if (_producer != null)
                _producer.removeEventListener(PropertyChangeEvent.PROPERTY_CHANGE, producerPropertyChangeHandler);
                
            _producer = value;
            
            if (_producer != null)
            {
                _producer.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE, producerPropertyChangeHandler);
                attemptToSendOrConnect();
            }

            dispatchEvent(event);          
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Protected Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Utility method that checks the queue state and associated Producer state
     *  and auto sends queued messages if possible or connects the Producer if
     *  possible.
     */
    protected function attemptToSendOrConnect():void
    {
        if (length && autoSend && producer != null)
        {
            if (!producer.connected && producer.autoConnect)
                producer.connect(); // This will trigger a send once the connect completes.
            else if (producer.connected && producer.clientId != null)
                sendAll();
        }
    }
    
    /**
     *  Dispatch a fault event associated with a pending call/token.
     * 
     *  @param token The token associated with the fault.
     *  @param fault The fault.
     */
    protected function dispatchFaultEvent(fault:Fault, token:AsyncToken):void
    {        
        var faultEvent:FaultEvent = FaultEvent.createEvent(fault, token);
        faultEvent.callTokenResponders();
        if (hasEventListener(FaultEvent.FAULT) && token != null)    
            dispatchEvent(faultEvent);
        else
            throw fault;
    }
    
    /**
     *  Dispatch a result event associated with a pending call/token.
     * 
     *  @param token The token associated with the result.
     *  @param generalDispatch Allows code to trigger responders without dispatching a general result event.
     */
    protected function dispatchResultEvent(event:ResultEvent, token:AsyncToken):void
    {
        event.callTokenResponders();	
        if (hasEventListener(ResultEvent.RESULT) && token != null)
            dispatchEvent(event);
    }
    
    /**
     * The decendant class must clear the local disk of any cached data.
     */
    protected function internalClearCache(token:AsyncToken):void {}
    
    /**
     *  The descendant class must retrieve the cache ids from the local store and
     *  add them to the specified view.
     */
    protected function internalGetCacheIDs(view:ListCollectionView, 
                                           token:AsyncToken):void {}

    /**
     *  The descendant class must initialize the local store.
     */
    protected function internalInitialize(success:Function, failed:Function):void
    {
        success();
    }
    
    /**
     *  The descendant class must load the store with any data found for the
     *  specified cacheID.
     */
    protected function internalLoadCache(token:AsyncToken):void {}
    
    /**
     *  The descendant class must release any disk/local store resources
     */
    protected function internalRelease(token:AsyncToken):void {}
    
    /**
     *  The descendant class must perform any local storage configuration here.
     */
    protected function internalSetCacheId(value:String):void {}
    
    /**
     *  The descendant class must save the current state of the store to disk.
     */
    protected function internalSaveCache(token:AsyncToken):void {}

    /**
     *  @private
     *  This method sends a message after stripping the queue header.
     *  It may be invoked by sendAll() or remove() in concrete implementations
     *  to pass a message back to the associated Producer to be sent.
     * 
     *  @param msg The message to send.
     */
    protected function send(msg:IMessage):void
    {
        if (msg.headers[MESSAGE_STORE_HEADER] != null)
            delete msg.headers[MESSAGE_STORE_HEADER];
        producer.send(msg);
    }

    /**
     *  @private 
     *  This method passes all queued messages to the associated Producer
     *  to be sent after removing them completely from the queue.
     */
    protected function sendAll():void {}

    /**
     *  This method is called by implementations once all data is loaded from
     *  disk.
     */
	protected function setLoaded(value:Boolean):void
	{
	    if (_loaded != value)
	    {
	        var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "loaded", _loaded, value);
	        _loaded = value;
	        dispatchEvent(event);
	    }
	}

    //--------------------------------------------------------------------------
    //
    // Private Methods
    // 
    //--------------------------------------------------------------------------

    private function checkCacheId():void
    {
	    if (cacheID == null || cacheID.length == 0)
	        throw new IllegalOperationError("A cache identifier must be set before performing this operation.");
    }
    
    private function getInitFailedFault(details:String = null):Fault
    {
        var fault:Fault = new Fault("Client.Initialization.Failed",
                                    "Could not initialize MessageStore.",
                                    details);
        return fault;
    }
   
    /**
     *  @private
     *  This handler responds to property changes on the associated Producer.
     */
    private function producerPropertyChangeHandler(event:PropertyChangeEvent):void
    {
        switch (event.property)
        {
            case "autoConnect":
            case "connected":
                if (event.newValue)
                    attemptToSendOrConnect();
            break;
            case "clientId":
                if (event.newValue != null && event.oldValue == null)
                    attemptToSendOrConnect();
            break;
        }
    }

    //--------------------------------------------------------------------------
    //
    // Public Static Constants
    // 
    //--------------------------------------------------------------------------

    /**
     *  A header injected into messages that are added to the queue to allow the
     *  associated Producer to ensure that queued messages that are passed to it
     *  originate from the proper queue and have been removed from the queue before
     *  being sent. This header is automatically removed from the message before it 
     *  is sent.
     */
    public static const MESSAGE_STORE_HEADER:String = "DSMessageStore";
}

}
