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

package mx.messaging.config
{

import flash.utils.getDefinitionByName;

import mx.collections.ArrayCollection;
import mx.core.mx_internal;
import mx.messaging.Channel;
import mx.messaging.ChannelSet;
import mx.messaging.MessageAgent;
import mx.messaging.errors.InvalidChannelError;
import mx.messaging.errors.InvalidDestinationError;
import mx.messaging.errors.MessagingError;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.StringUtil;
import mx.utils.ObjectUtil;

use namespace mx_internal;

[ResourceBundle("messaging")]

/**
 *  This class provides access to the server messaging configuration information.
 *  This class encapsulates information from the services-config.xml file on the client
 *  and is used by the messaging system to provide configured ChannelSets and Channels 
 *  to the messaging framework.
 *  
 *  <p>The XML source is provided during the compilation process.
 *  However, there is currently no internal restriction preventing the
 *  acquisition of this XML data by other means, such as network, local file
 *  system, or shared object at runtime.</p>
 */
public class ServerConfig
{
    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Channel config parsing constant.
     */
    public static const CLASS_ATTR:String = "type";

    /**
     *  @private
     *  Channel config parsing constant.
     */
    public static const URI_ATTR:String = "uri";

    //--------------------------------------------------------------------------
    //
    // Class variables
    // 
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Storage for the resourceManager getter.
	 *  This gets initialized on first access,
	 *  not at static initialization time, in order to ensure
	 *  that the Singleton registry has already been initialized.
	 */
	private static var _resourceManager:IResourceManager;
	
	/**
	 *  @private
     *  A reference to the object which manages
     *  all of the application's localized resources.
     *  This is a singleton instance which implements
     *  the IResourceManager interface.
	 */
	private static function get resourceManager():IResourceManager
	{
		if (!_resourceManager)
			_resourceManager = ResourceManager.getInstance();

		return _resourceManager;
	}

    //--------------------------------------------------------------------------
    //
    // Static Variables
    // 
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  The server configuration data.
	 */
	public static var serverConfigData:XML;
	
    /**
     *  @private
     *  Caches shared ChannelSets, keyed by strings having the format:
     *  <list of comma delimited channel ids>:[true|false] - where the final 
     *  flag indicates whether the ChannelSet should be used for clustered
     *  destinations or not.
     */
    private static var _channelSets:Object = {};
    
    /**
     *  @private
     *  Caches shared clustered Channel instances keyed by Channel id.
     */
    private static var _clusteredChannels:Object = {};
    
    /**
     *  @private 
     *  Caches shared unclustered Channel instances keyed by Channel id.
     */
    private static var _unclusteredChannels:Object = {};    

    /** 
     * @private
     * Keeps track of Channel endpoint uris whose configuration has been fetched 
     * from the server. 
     */
    private static var _configFetchedChannels:Object;
    
    //--------------------------------------------------------------------------
    //
    // Static Properties
    // 
    //--------------------------------------------------------------------------

	//----------------------------------
	//  xml
	//----------------------------------

    /**
     *  The XML configuration; this value must contain the relevant portions of
     *  the &lt;services&gt; tag from the services-config.xml file.
     */
    public static function get xml():XML
    {
        if (serverConfigData == null)
            serverConfigData = <services/>;
        return serverConfigData;
    }

    /**
     *  @private
     */
    public static function set xml(value:XML):void
    {
        serverConfigData = value;
        // Reset cached Channels and ChannelSets.
        _channelSets = {};
        _clusteredChannels = {};
        _unclusteredChannels = {};
    }
                    
    //--------------------------------------------------------------------------
    //
    // Static Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  This method ensures that the destinations specified contain identical
     *  channel definitions.
     *  If the channel definitions between the two destinations specified are
     *  not identical this method will throw an ArgumentError.
     * 
     *  @param   destinationA:String first destination to compare against
     *  @param   destinationB:String second destination to compare channels with
     *  @throw   ArgumentError if the channel definitions of the specified 
     *           destinations aren't identical.
     */
    public static function checkChannelConsistency(destinationA:String, 
                                                   destinationB:String):void
    {
        var channelIdsA:Array = getChannelIdList(destinationA);
        var channelIdsB:Array = getChannelIdList(destinationB);
        if (ObjectUtil.compare(channelIdsA, channelIdsB) != 0)
            throw new ArgumentError("Specified destinations are not channel consistent");
    }
    
    /**
     *  Returns a shared instance of the configured Channel.
     *
     *  @param id The id of the desired Channel.
     * 
     *  @param clustered True if the Channel will be used in a clustered
     *                   fashion; otherwise false.
     * 
     *  @return The Channel instance.
     * 
     *  @throws mx.messaging.errors.InvalidChannelError If no Channel has the specified id.
     */
    public static function getChannel(id:String, 
                                      clustered:Boolean = false):Channel
    {
        var channel:Channel;

        if (!clustered)
        {
            if (id in _unclusteredChannels)
            {
                return _unclusteredChannels[id];   
            }
            else
            {
                 channel = createChannel(id);
                _unclusteredChannels[id] = channel;
                return channel;
            }
        }
        else
        {
            if (id in _clusteredChannels)
            {
                return _clusteredChannels[id];
            }
            else
            {
                channel = createChannel(id);
                _clusteredChannels[id] = channel;
                return channel;
            }
        }
    }
    
    /**
     *  Returns a shared ChannelSet for use with the specified destination
     *  belonging to the service that handles the specified message type.
     * 
     *  @param destinationId The target destination id.    
     * 
     *  @return The ChannelSet.
     * 
     *  @throws mx.messaging.errors.InvalidDestinationError If the specified destination
     *                                  does not have channels and the application
     *                                  did not define default channels.
     */
    public static function getChannelSet(destinationId:String):ChannelSet
    {             	
    	var destinationConfig:XML = getDestinationConfig(destinationId);   	
        return internalGetChannelSet(destinationConfig, destinationId);
    }
    
    /**
     *  Returns the property information for the specified destination
     *
     *  @param destinationId The id of the desired destination.
     * 
     *  @return XMLList containing the &lt;property&gt; tag information.
     * 
     *  @throws mx.messaging.errors.InvalidDestinationError If the specified destination is not found.
     */
    public static function getProperties(destinationId:String):XMLList
    {
    	var destination:XMLList = xml..destination.(@id == destinationId);
    		
    	if (destination.length() > 0)
		{
    		return destination.properties;
		}
    	else
		{
			var message:String = resourceManager.getString(
				"messaging", "unknownDestination", [ destinationId ]);
    		throw new InvalidDestinationError(message);
		}
    	
    	return destination;
    }
        
    //--------------------------------------------------------------------------
    //
    // Static Internal Methods
    // 
    //--------------------------------------------------------------------------
     
    /**
     *  This method returns true iff the channelset specified has channels with
     *  ids or uris that match those found in the destination specified.
     */
    mx_internal static function channelSetMatchesDestinationConfig(channelSet:ChannelSet,
                                                                   destination:String):Boolean
    {
        if (channelSet != null)
        {
            if (ObjectUtil.compare(channelSet.channelIds, getChannelIdList(destination)) == 0)
                return true;
                
            // if any of the specified channelset channelIds do not match then 
            // we have to move to comparing the uris, as the ids could be null
            // in the specified channelset.
            var csUris:Array = [];
            var csChannels:Array = channelSet.channels;
            for (var i:uint = 0; i<csChannels.length; i++)
                csUris.push(csChannels[i].uri);
                
            var ids:Array = getChannelIdList(destination);
            var dsUris:Array = [];
            var dsChannels:XMLList;
            var channelConfig:XML;
            for (var j:uint = 0; j<ids.length; j++)
            {
                dsChannels = xml.channels.channel.(@id == ids[j]);
                channelConfig = dsChannels[0];                               
                dsUris.push(channelConfig.endpoint[0].attribute(URI_ATTR).toString());
            }
            
            return ObjectUtil.compare(csUris, dsUris) == 0;
            
        }
        return false;
    }
    
    /**
     * @private
     * returns if the specified endpoint has been fetched already
     */
    mx_internal static function fetchedConfig(endpoint:String):Boolean
    {
        return _configFetchedChannels != null && _configFetchedChannels[endpoint] != null;
    }
    
    /**
     *  @private
     *  This method returns a list of the channel ids for the given destination
     *  configuration. If no channels exist for the destination, it returns a 
     *  list of default channel ids for the applcation
     */
    mx_internal static function getChannelIdList(destination:String):Array
    {                    
     	var destinationConfig:XML = getDestinationConfig(destination);	
		return destinationConfig? getChannelIds(destinationConfig) 
                                        : getDefaultChannelIds();
    }

    /**
     *  @private 
     *  Used by the Channels to determine whether the Channel should request 
     *  dynamic configuration from the server for its MessageAgents. 
     */	 
	mx_internal static function needsConfig(channel:Channel):Boolean
	{
	    // Configuration for the endpoint has not been fetched by some other channel.
	    if (_configFetchedChannels == null || _configFetchedChannels[channel.endpoint] == null)
        {	            
    	    var channelSets:Array = channel.channelSets;
    	    var m:int = channelSets.length;
    		for (var i:int = 0; i < m; i++)
            {
                var messageAgents:Array = ChannelSet(channelSets[i]).messageAgents;
                var n:int = messageAgents.length;
                for (var j:int = 0; j < n; j++)
                {
                    if (MessageAgent(messageAgents[j]).needsConfig)
                        return true;                        
                }            
            }
        }
        return false;        	        	    
	}	    
    	     		         
    /**
     *  @private
     *  This method updates the xml with serverConfig object returned from the
     *  server during initial client connect
     */
     mx_internal static function updateServerConfigData(serverConfig:ConfigMap, endpoint:String = null):void
     {      	        
		if (serverConfig != null) 
		{	
		    if (endpoint != null)
		    {
    		    // Add the endpoint uri to the list of uris whose configuration 
    		    // has been fetched.
    		    if (_configFetchedChannels == null)
    		        _configFetchedChannels = {};
    		        
    		    _configFetchedChannels[endpoint] = true;
		    }
		    
			var newServices:XML = <services></services>; 																						
			convertToXML(serverConfig, newServices);
	        
	        // Update default-channels of the application     
            xml["default-channels"] = newServices["default-channels"];	             
            
            // Update the services
			for each (var newService:XML in newServices..service)
			{    
				var oldServices:XMLList = xml.service.(@id == newService.@id);
				if (oldServices.length() != 0)
				{					
					// Assuming only one service exists with the id
					var oldService:XML = oldServices[0];
														
					// Update destinations
					for each (var newDestination:XML in newService..destination)
					{
						var oldDestinations:XMLList = oldService.destination.(@id == newDestination.@id);
						if (oldDestinations.length() != 0)
						{
							// Assuming only one destination exists with the id
							delete oldDestinations[0];							
						}
						oldService.appendChild(newDestination);
					}
				}
				else
				{
					xml.appendChild(newService);			
				}
			}
			
			// Update the channels
			var newChannels:XMLList = newServices.channels;
            if (newChannels.length() > 0)
            {                
       		    var oldChannels:XML = xml.channels[0];          
	    		if (oldChannels == null || oldChannels.length() == 0)	    		
    			{
			        xml.appendChild(newChannels);
	    		}
	    		// Commenting this section out as there is no real use case
	    		// for updating channel definitions. 
	    		/*
		    	else
			    {
			        for each (var newChannel:XML in newChannels.channel)
			        {
			            var oldChannel:XMLList = oldChannels.channel.(@id == newChannel.@id);
			            if (oldChannel.length() > 0)
	                    {
	                        // Assuming only one channel exists with the same id.
	                        delete oldChannel[0];	                    	                    
	                    }
	                    oldChannels.appendChild(newChannel);        
			        }
			    }
			    */
            }			   
		}
     }
   
    //--------------------------------------------------------------------------
    //
    // Static Private Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  Helper method that builds a new Channel instance based on the 
     *  configuration for the specified id.
     *  
     *  @param id The id for the configured Channel to build.
     * 
     *  @return The Channel instance.
     * 
     *  @throws mx.messaging.errors.InvalidChannelError If no configuration data for the specified
     *                             id exists.
     */
    private static function createChannel(channelId:String):Channel
    {
		var message:String;
		     
        var channels:XMLList = xml.channels.channel.(@id == channelId);
        if (channels.length() == 0)
        {
			message = resourceManager.getString(
				"messaging", "unknownChannelWithId", [ channelId ]);
            throw new InvalidChannelError(message);
        }
                
        var channelConfig:XML = channels[0];                               
        var className:String = channelConfig.attribute(CLASS_ATTR).toString();
        var uri:String = channelConfig.endpoint[0].attribute(URI_ATTR).toString();
        var channel:Channel = null;
        try
        {
            var channelClass:Class = getDefinitionByName(className) as Class;
            channel = new channelClass(channelId, uri);
            channel.applySettings(channelConfig);
        }
        catch(e:ReferenceError)
        {
			message = resourceManager.getString(
				"messaging", "unknownChannelClass", [ className ]);
            throw new InvalidChannelError(message);
        }
        return channel;
    }
    
   	/**
   	 * Converts the ConfigMap of properties into XML
   	 */
	private static function convertToXML(config:ConfigMap, configXML:XML):void
	{				
		for (var propertyKey:Object in config)
		{			
			var propertyValue:Object = config[propertyKey];
						
			if (propertyValue is String)
			{
				if (propertyKey == "")
				{
					// Add as a value	
					var name:Object = configXML.localName();
					var parent:XML = configXML.parent();
					parent[name] = propertyValue;
				}
				else
				{
					// Add as an attribute
					configXML.@[propertyKey] = propertyValue;
				}				
			}			
			else if (propertyValue is ArrayCollection || propertyValue is Array)
			{				
				var propertyValueList:Array; 
				if (propertyValue is ArrayCollection)
					propertyValueList = ArrayCollection(propertyValue).toArray();
				else
					propertyValueList = propertyValue as Array;
					
			    for (var i:int = 0; i < propertyValueList.length; i++)    	            
	    		{	    			
   					var propertyXML1:XML = <{propertyKey}></{propertyKey}>				
					configXML.appendChild(propertyXML1);
					convertToXML(propertyValueList[i] as ConfigMap, propertyXML1);
	    		}				
			}
			else // assuming that it is ConfigMap
			{
   				var propertyXML2:XML = <{propertyKey}></{propertyKey}>				
				configXML.appendChild(propertyXML2);
				convertToXML(propertyValue as ConfigMap, propertyXML2);				
			}
		}
	}
	
    private static function getChannelIds(destinationConfig:XML):Array
    {
        var result:Array = [];
        var channels:XMLList = destinationConfig.channels.channel;
	    var n:int = channels.length();
	    for (var i:int = 0; i < n; i++)    	            
	    {
	        result.push(channels[i].@ref.toString());
	    }
        return result;
    }
	
    /**
     * @private
     * This method returns a list of default channel ids for the application 
     */ 
	private static function getDefaultChannelIds():Array
	{
		var result:Array = [];
		var channels:XMLList = xml["default-channels"].channel;
        var n:int = channels.length();		
        for (var i:int = 0; i < n; i++)
        {
            result.push(channels[i].@ref.toString());
        }  			            
        return result;   	    				             	
	}
	 
    /**
     *  Returns the destination XML data specific to the destination and message
     *  type specified. Returns null if the destination is not found.
     */
    private static function getDestinationConfig(destinationId:String):XML
    {
        var destinations:XMLList = xml..destination.(@id == destinationId);
        var destinationCount:int = destinations.length();
        if (destinationCount == 0)	
        {
			return null;         
        }
        else 
        {
            // Destination ids are unique among services
            return destinations[0];            
        }
    }
     
    /**
     *  Helper method to look up and return a cached ChannelSet (and build and
     *  cache an instance if necessary).
     * 
     *  @param destinationConfig The configuration for the target destination.
     *  @param destinatonId The id of the target destination.
     * 
     *  @return The ChannelSet.
     */
    private static function internalGetChannelSet(
                                        destinationConfig:XML, 
                                        destinationId:String):ChannelSet
    {
        var channelIds:Array;
		var clustered:Boolean;
         
        if (destinationConfig == null)
        {
        	channelIds = getDefaultChannelIds();    
            if (channelIds.length == 0)
            {
				var message:String = resourceManager.getString(
					"messaging", "noChannelForDestination", [ destinationId ]);
                throw new InvalidDestinationError(message);                   
            }        	        
        	clustered = false; 
        }
        else
        {
			channelIds = getChannelIds(destinationConfig);
			clustered = (destinationConfig.properties.network.cluster.length() > 0) ? true : false;			
        } 
                            
        var channelSetId:String = channelIds.join(",") + ":" + clustered;
        
        if (channelSetId in _channelSets)
        {
            return _channelSets[channelSetId];    
        }
        else
        {
            var channelSet:ChannelSet = new ChannelSet(channelIds, clustered);
            if (clustered)
            {
                channelSet.initialDestinationId = destinationId;
            }
            _channelSets[channelSetId] = channelSet;
            return channelSet;
        }
    }      
            
}

}
