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

package mx.messaging.messages
{	
	[RemoteClass(alias="flex.messaging.messages.MessagePerformanceInfo")]
	
	/** 
	 * @private
	 * 
	 * The MessagePerformanceInfo class is used to capture various metrics about
	 * the sizing and timing of a message sent from a client to the server and its 
	 * response message, as well as pushed messages from the server to the client.	
	 * A response message should have two instances of this class among its headers,
	 * headers[MPII] - info for the client to server message,
	 * headers[MPIO] - info for the response message from server to client.
	 * A pushed message will have an extra headers and its headers will represent,
	 * headers[MPII] - info for the client to server message poll message (non RTMP)
	 * headers[MPIO] - info for the pushed message from server to client,
	 * headers[MPIP] - info for the message from the client that caused the push message
	 */
	public class MessagePerformanceInfo
	{
		
    	//--------------------------------------------------------------------------
    	//
    	// Constructor
    	// 
    	//--------------------------------------------------------------------------	
        public function MessagePerformanceInfo()
        {
            super();
        }    		

        //--------------------------------------------------------------------------
    	//
    	// Properties
    	// 
    	//--------------------------------------------------------------------------		
		
		/**
	 	 * Size of message in Bytes (message types depends on what header this MPI is in)
	 	 */		
		public var messageSize:int;
		
		/**
	 	 * Millisecond timestamp of when this message was sent
	 	 * (origin depends on on what header this MPI is in)
	 	 */			
		public var sendTime:Number = 0;
		
		/**
	 	 * Millisecond timestamp of when this message was received
	 	 * (destination depends on on what header this MPI is in)
	 	 */			
		public var receiveTime:Number;
		
		/**
	 	 * Amount of time in milliseconds that this message was being processed on the server
	 	 * in order to calculate and populate MPI metrics
	 	 */			
		public var overheadTime:Number;
		
		/**
	 	 * "OUT" when this message originated on the server
	 	 */			 	 	
		private var _infoType:String;
		
		/**
	 	 * True if this is info for a message that was pushed from server to client
	 	 */				
		public var pushedFlag:Boolean;
		
		/**
	 	 * Millisecond timestamp of when the server became ready to push this message out 
	 	 * to clients
	 	 */			
		public var serverPrePushTime:Number;
		
		/**
	 	 * Millisecond timestamp of when the server called into the adapter associated with the
	 	 * destination of this message
	 	 */				
		public var serverPreAdapterTime:Number;

		/**
	 	 * Millisecond timestamp of when server processing returned from the adapater associated 
	 	 * with the destination of this message
	 	 */				
		public var serverPostAdapterTime:Number;	
		
		/**
	 	 * Millisecond timestamp of when the adapter associated with the destination of this message
	 	 * made a call to an external component (for example a JMS server)
	 	 */		
		public var serverPreAdapterExternalTime:Number;
		
		/**
	 	 * Millisecond timestamp of when processing came back to the adapter associated with the destination 
	 	 * of this message from a call to an external component (for example a JMS server)
	 	 */				
		public var serverPostAdapterExternalTime:Number;
		
		/**
	 	 * Flag is true when record-message-times is enabled for the communication channel
	 	 */			
        public var recordMessageTimes:Boolean;
        
		/**
	 	 * Flag is true when record-message-sizes is enabled for the communication channel
	 	 */	        
        public var recordMessageSizes:Boolean;		
		
    	//--------------------------------------------------------------------------
    	//
    	// Public Methods
    	// 
    	//--------------------------------------------------------------------------
		
	  /**
	   *  Sets the info type of this message (IN or OUT).  Used to mark the MPI with the 
	   *  client receive time when this MPI is of type OUT (IN, OUT are from the perspective of the
	   *  server)
	   * 
	   * @param type - "IN" or "OUT" info type
	   */		
		public function set infoType(type:String):void
		{
			_infoType = type;
			if (_infoType=="OUT")
			{			
				var curDate:Date = new Date();
				this.receiveTime = curDate.getTime();
			}
		}
		
	  /**
	   *  Get the info type of this message (IN or OUT).
	   * 
	   * @return "IN" or "OUT" (from the perspective of the server)
	   */			
		public function get infoType():String
		{
			return this._infoType;
		}
		
	}
}