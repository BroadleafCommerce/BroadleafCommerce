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
    import mx.messaging.messages.MessagePerformanceInfo;
    
    /** 
     * The MessagePerformanceUtils utility class is used to retrieve various metrics about
     * the sizing and timing of a message sent from a client to the server and its 
     * response message, as well as pushed messages from the server to the client.  
     * Metrics are gathered when corresponding properties on the channel used are enabled:
     * &lt;record-message-times&gt; denotes capturing of timing information,
     * &lt;record-message-sizes&gt; denotes capturing of sizing information.
     * 
     * <p>You can then use methods of this utility class to retrieve various performance information
     * about the message that you have just received.</p>
     * 
     * <p>When these metrics are enabled an instance of this class should be created from 
     * a response, acknowledgement, or message handler via something like: </p>
     * @example
     * <pre>
     *      var mpiutil:MessagePerformanceUtils = new MessagePerformanceUtils(event.message);
     * </pre>    
     * 
     */ 
    public class MessagePerformanceUtils
    {   
        /**
         * @private 
         * 
         * Information about the original message sent out by the client
         */
        public var mpii:MessagePerformanceInfo;
        
        /**
         * @private 
         * 
         * Information about the response message sent back to the client
         */     
        public var mpio:MessagePerformanceInfo;
        
        /**
         * @private 
         * 
         * If this is a pushed message, information about the original message
         * that caused the push
         */             
        public var mpip:MessagePerformanceInfo;        
        
        /**
         * @private 
         * 
         * Header for MPI of original message sent by client
         */         
        public static const MPI_HEADER_IN:String = "DSMPII";
        
        /**
         * @private 
         * 
         * Header for MPI of response message sent to the client
         */         
        public static const MPI_HEADER_OUT:String = "DSMPIO";
        
        /**
         * @private 
         * 
         * Header for MPI of a message that caused a pushed message             
         */         
        public static const MPI_HEADER_PUSH:String = "DSMPIP";        

        
        //--------------------------------------------------------------------------
        //
        // Constructor
        // 
        //--------------------------------------------------------------------------    
        
        /**
         * Constructor
         * 
         * Creates an MPUtils instance with information from the MPI headers
         * of the passed in message
         * 
         * @param message The message whose MPI headers will be used in retrieving
         * MPI information  
         */                 
        public function MessagePerformanceUtils(message:Object):void
        {
            super();

            this.mpii=message.headers[MPI_HEADER_IN] as MessagePerformanceInfo;                                   
            this.mpio=message.headers[MPI_HEADER_OUT] as MessagePerformanceInfo;
                        
            // it is possible that if not all participants have mpi enabled we might be missing parts here
            if (mpio == null || (mpii == null && message.headers[MPI_HEADER_PUSH] == null))
            {
                throw new Error("Message is missing MPI headers.  Verify that all participants have it enabled.");
            }                
            
            if (pushedMessageFlag)
                this.mpip = message.headers[MPI_HEADER_PUSH] as MessagePerformanceInfo;            
        }
        
        //--------------------------------------------------------------------------
        //
        //  Public Methods
        //
        //--------------------------------------------------------------------------        
        
        /**
         * Time between this client sending a message and receiving a response
         * for it from the server
         * 
         * @return Total time in milliseconds
         */         
        public function get totalTime():Number
        {
            if (mpii == null)
                return 0;
            else
                return mpio.receiveTime - mpii.sendTime;
        }
        
        /**
         * Time between server receiving the client message and either the time
         * the server responded to the received message or had the pushed message ready
         * to be sent to the receiving client.  
         * 
         * @return Server processing time in milliseconds
         */         
        public function get serverProcessingTime():Number
        {
            if (pushedMessageFlag)
            {
            	return mpip.serverPrePushTime - mpip.receiveTime;
            }
            else
            {
            	return mpio.sendTime - mpii.receiveTime;
            }                
        }       
        
        /**
         * Time between server receiving the client message and the server beginning to push
         * messages out to other clients as a result of the original message.  
         * 
         * @return Server pre-push processing time in milliseconds
         */         
        public function get serverPrePushTime():Number
        {
            if (mpii == null)
                return 0;
            if (mpii.serverPrePushTime == 0)
                return serverProcessingTime;
            
            return mpii.serverPrePushTime - mpii.receiveTime;
        }    
        
        /**
         * Time spent in the adapter associated with the destination for this message before
         * either the response to the message was ready or the message had been prepared
         * to be pushed to the receiving client.  
         * 
         * @return Server adapter processing time in milliseconds
         */           
		public function get serverAdapterTime():Number
		{
			if (pushedMessageFlag)
			{
				if (mpip == null)
					return 0;
				if (mpip.serverPreAdapterTime == 0 || mpip.serverPostAdapterTime == 0)
					return 0;
			
				return mpip.serverPostAdapterTime - mpip.serverPreAdapterTime;				
			}
			else
			{
				if (mpii == null)
					return 0;
				if (mpii.serverPreAdapterTime == 0 || mpii.serverPostAdapterTime == 0)
					return 0;
			
				return mpii.serverPostAdapterTime - mpii.serverPreAdapterTime;
			}
		}	

        /**
         * Time spent in a module invoked from the adapter associated with the destination for this message 
         * but external to it, before either the response to the message was ready or the message had been 
         * prepared to be pushed to the receiving client.  
         * 
         * @return Server adapter-external processing time in milliseconds
         */ 		
		public function get serverAdapterExternalTime():Number
		{
			if (pushedMessageFlag)
			{
				if (mpip == null)
					return 0;
				if (mpip.serverPreAdapterExternalTime == 0 || mpip.serverPostAdapterExternalTime == 0)
					return 0;
			
				return mpip.serverPostAdapterExternalTime - mpip.serverPreAdapterExternalTime;				
			}
			else			
			{
				if (mpii == null)
					return 0;
				if (mpii.serverPreAdapterExternalTime == 0 || mpii.serverPostAdapterExternalTime == 0)
					return 0;
			
				return mpii.serverPostAdapterExternalTime - mpii.serverPreAdapterExternalTime;
			}
		}	

        /**
         * @return Time that the message waited on the server after it was ready to be pushed to the client
         * but had not yet been polled for.
         */		
		public function get serverPollDelay():Number
		{
			if (mpip == null)
				return 0;
			if (mpip.serverPrePushTime == 0 || mpio.sendTime == 0)
				return 0;
			
			return mpio.sendTime - mpip.serverPrePushTime;	
		}
		
        /**
         * Server processing time spent outside of the adapter associated with the destination of this message
         * 
         * @return Non-adapter server processing time in milliseconds
         */ 		
		public function get serverNonAdapterTime():Number
		{		
			return serverProcessingTime - serverAdapterTime;
		}		
        
        /**
         * The network round trip time for a client message and the server response to it,
         * calculated by the difference between total time and server processing time
         * 
         * @return Network round trip time in milliseconds
         */             
        public function get networkRTT():Number
        {
        	if (!pushedMessageFlag)
            	return totalTime - serverProcessingTime;
            else
            	return 0;
        }           
        
        /**
         * Timestamp in milliseconds since epoch of when the server sent a response message back
         * to the client
         * 
         * @return Timestamp in milliseconds since epoch
         */             
        public function get serverSendTime():Number
        {
            return  mpio.sendTime;
        }       
        
        /**
         * Timestamp in milliseconds since epoch of when the client received response message from
         * the server
         * 
         * @return Timestamp in milliseconds since epoch
         */         
        public function get clientReceiveTime():Number
        {
            return mpio.receiveTime;    
        }                       
        
        /**
         * The size of the original client message as measured during deserialization by the server
         * endpoint
         * 
         * @return Message size in Bytes
         */         
        public function get messageSize():int
        {
            if (mpii == null)
                return 0;
            else
                return mpii.messageSize;
        }           
        
        /**
         * The size of the response message sent to the client by the server as measured during serialization
         * at the server endpoint
         * 
         * @return Message size in Bytes
         */         
        public function get responseMessageSize():int
        {
            return mpio.messageSize;
        }       
        
        /**
         * Returns true if message was pushed to the client and is not a response to a message that
         * originated on the client
         * 
         * @return true if this message was pushed to the client and is not a response to a message that
         * originated on the client
         */         
        public function get pushedMessageFlag():Boolean
        {
            return mpio.pushedFlag;
        }                           
        
        /**
         * Only populated in the case of a pushed message, this is the time between the push causing client
         * sending its message and the push receving client receiving it.  Note that the two clients'
         * clocks must be in sync for this to be meaningful.
         * 
         * @return Total push time in milliseconds
         */             
        public function get totalPushTime():Number
        {           
            return clientReceiveTime - originatingMessageSentTime  - pushedOverheadTime;
        }           
        
        /**
         * Only populated in the case of a pushed message, this is the network time between
         * the server pushing the message and the client receiving it.  Note that the server
         * and client clocks must be in sync for this to be meaningful.
         * 
         * @return One way server push time in milliseconds
         */ 
        public function get pushOneWayTime():Number
        {
            return  clientReceiveTime - serverSendTime;
        }                   
        
        /**
         * Only populated in the case of a pushed message, timestamp in milliseconds since epoch of 
         * when the client that caused a push message sent its message.
         * 
         * @return Timestamp in milliseconds since epoch
         */             
        public function get originatingMessageSentTime():Number
        {
            return mpip.sendTime;   
        }                   
        
        /**
         * Only populated in the case of a pushed message, size in Bytes of the message that originally
         * caused this pushed message
         * 
         * @return Pushed causer message size in Bytes
         */         
        public function get originatingMessageSize():Number
        {
            return mpip.messageSize;    
        }                               
        
        /**
         * Method returns a summary of all information available in MPI.  A suggested use of this
         * is something like,
         * @example
         * <listing version="3.0">
         *      var mpiutil:MessagePerformanceUtils = new MessagePerformanceUtils(message);                     
         *      Alert.show(mpiutil.prettyPrint(), "MPI Output", Alert.NONMODAL);
         * </listing>            
         * 
         * @return String containing a summary of all information available in MPI
         */         
        public function prettyPrint():String
        {       
            var alertString:String = new String("");
            if (messageSize != 0)
                alertString +="Original message size(B): " + messageSize + "\n";
            if (responseMessageSize != 0)              
                alertString +="Response message size(B): " + responseMessageSize + "\n";
            if (totalTime != 0)
                alertString +="Total time (s): " + (totalTime / 1000) + "\n";
            if (networkRTT != 0)
                alertString +="Network Roundtrip time (s): " + (networkRTT / 1000) + "\n";
            if (serverProcessingTime != 0)
                alertString +="Server processing time (s): " + (serverProcessingTime / 1000) + "\n";
      		if (serverAdapterTime != 0)
      			alertString +="Server adapter time (s): " + (serverAdapterTime / 1000) + "\n";      
      		if (serverNonAdapterTime != 0)
      			alertString +="Server non-adapter time (s): " + (serverNonAdapterTime / 1000) + "\n"      			  
      		if (serverAdapterExternalTime != 0)
      			alertString +="Server adapter external time (s): " + (serverAdapterExternalTime / 1000) + "\n";     
            
            if (pushedMessageFlag)
            {
                alertString += "PUSHED MESSAGE INFORMATION:\n";
                if (totalPushTime != 0)
                    alertString += "Total push time (s): " + (totalPushTime / 1000) + "\n";
                if (pushOneWayTime != 0)
                    alertString += "Push one way time (s): " + (pushOneWayTime / 1000) + "\n";
                if (originatingMessageSize != 0)
                    alertString += "Originating Message size (B): " + originatingMessageSize + "\n";
      			if (serverPollDelay != 0)
      				alertString +="Server poll delay (s): " + (serverPollDelay / 1000) + "\n";                        
            }
            
            return alertString;
        }           
        
        //--------------------------------------------------------------------------
        //
        //  Private Methods
        //
        //--------------------------------------------------------------------------     
                
        /**
         * @private
         * 
         * Overhead time in milliseconds for processing of the push causer message
         */                 
        private function get pushedOverheadTime():Number
        {
            return mpip.overheadTime;   
        }                           
        
    }
}