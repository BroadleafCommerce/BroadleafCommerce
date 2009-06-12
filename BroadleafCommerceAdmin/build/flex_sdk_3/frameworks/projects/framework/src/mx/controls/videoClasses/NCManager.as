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

package mx.controls.videoClasses 
{

import flash.events.Event;
import flash.events.NetStatusEvent;
import flash.events.TimerEvent;
import flash.net.NetConnection;
import flash.net.ObjectEncoding;
import flash.net.Responder;
import flash.utils.Timer;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ExcludeClass]

[ResourceBundle("controls")]

/**
 *  @private
 *  Creates <code>NetConnection</code> for <code>VideoPlayer</code>, a
 *  helper class for that user facing class.
 */
public class NCManager implements INCManager
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Default connection timeout in milliseconds.
     *
     *  @see #timeout
     */
    public static const DEFAULT_TIMEOUT:Number = 60000;

    /**
     *  @private
     */
    private static const DEFAULT_NC_TIMEOUT:uint = 1500;       // .5 seconds

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  List of connections tried by connectRTMP, in order tried.
     */
    private static var RTMP_CONN:Array =
	[
        { protocol: "rtmp:/", port: "1935" },
		{ protocol: "rtmp:/", port:"443" },
		{ protocol: "rtmpt:/", port:"80" },
		{ protocol: "rtmps:/", port:"443" }
    ];
            	
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------
  
    /**
     *  @private
     *  Constructor.
     */    
    public function NCManager()    
    {
		super();

        initNCInfo();
        initOtherInfo();

        // intervals
        owner = null;
        _netConnection = null;
        ncConnected = false;

        // actually calls setter
        timeout = DEFAULT_TIMEOUT;
    }
     
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     * <p>fallbackServerName is exposed in two ways:</p>
     * 
     * <p>User can supply second <meta base> in smil and that base
     * attr will be taken as the fallbackServerName (note that only
     * the server name will be taken from this and not the application
     * name or anything else).</p>
     *
     * <p>The second way is the user can directly set this by
     * accessing the ncMgr property in FLVPlayback or VideoPlayer and
     * set fallbackServerName property directly.</p>
     */
    public var fallbackServerName:String;
    
    /**
     *  @private
     *  Reference to the VideoPlayer instance associated with this INCManager.
     */
    private var owner:VideoPlayer;

    /**
     *  @private
     *  The path to the content.
     */
    private var contentPath:String;
    
    /**
     *  @private
     */    
	private var protocol:String;
	
    /**
     *  @private
     *  The host name portion of the URL.
     */
    private var serverName:String;

    /**
     *  @private
     *  The port number portion of the URL.
     */
    private var portNumber:String;
    
    /**
     *  @private
     */    
	private var wrappedURL:String;
	
    /**
     *  @private
     */
    private var appName:String;

    /**
     *  @private
     *  Multiple streams for multiple bandwidths.
     */
    private var streams:Array;

    /**
     *  @private
     *  Whether the protocol is RTMP.
     *   -1: undefined
     *    0: no
     *    1: yes
     */
    private var _isRTMP:int = -1;

    /**
     *  @private
     *  Timer for connection timeout.
     */
    private var connectionTimer:Timer;

    /**
     *  @private
     */
    private var autoSenseBW:Boolean;

    /**
     *  @private
     *  Bandwidth detection stuff.
     */
    public var payload:uint;

    /**
     *  @private
     *  Value of the NetConnection instance's uri property.  This is saved upon
     *  connection and reused for reconnecting.
     */
    private var ncURI:String;

    /**
     *  @private
     */
	private var ncConnected:Boolean;
	
    /**
     *  @private
     *  Info on multiple connections we try.
     */
    public var tryNC:Array;

    /**
     *  @private
     */
    private var tryNCTimer:Timer;


    /**
     *  @private
     *  Counter that tracks the next type to try in RTMP_CONN.
     */
    private var connTypeCounter:uint;

	/**
	 *  @private
	 *  Used for accessing localized Error messages.
	 */
	private var resourceManager:IResourceManager =
									ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    //  Public Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  timeout
    //----------------------------------

    /**
     *  @private
     *  Storage for timeout property.
     */
    private var _timeout:uint;

    /*
     *  @see INCManager#timeout
     */
    public function get timeout():uint 
	{
        return _timeout;
    }

    public function set timeout(t:uint):void 
	{
        _timeout = t;
        connectionTimer = new Timer(_timeout, 1);
        connectionTimer.addEventListener(TimerEvent.TIMER, onFCSConnectTimeOut);
    }

    //----------------------------------
    //  bitrate
    //----------------------------------

    /**
     *  @private
     *  Storage for bitrate property.
     */
    private var _bitrate:Number;

    /**
     *  For RTMP streams, returns value calculated from autodetection,
     *  not value set via bitrate.
     *
     *  @see INCManager#bitrate
     */
    public function get bitrate():Number 
	{
        return _bitrate;
    }

    /**
     *  This value is only used with progressive download (HTTP), with
     *  RTMP streaming uses autodetection.
     *
     *  @see INCManager#bitrate
     */
    public function set bitrate(b:Number):void 
	{
        if (_isRTMP == 0)
            _bitrate = b;
    }

    //----------------------------------
    //  videoPlayer
    //----------------------------------

    /**
     *  @see INCManager#videoPlayer
     */
    public function get videoPlayer():VideoPlayer 
	{
        return owner;
    }

	/**
	 *  @private
	 */
    public function set videoPlayer(value:VideoPlayer):void 
	{
        owner = value;
    }

    //----------------------------------
    //  netConnection
    //----------------------------------

    /**
     *  @private
     */
    private var _netConnection:NetConnection;

    /**
     *  @see INCManagernetConnection
     */
    public function get netConnection():NetConnection 
	{
        return _netConnection;
    }

    //----------------------------------
    //  streamName
    //----------------------------------

    /**
     *  @private
     *  Storage for streamName property.
     */
    private var _streamName:String;

    /**
     *  @see INCManager#streamName
     */
    public function get streamName():String 
	{
        return _streamName;
    }

    //----------------------------------
    //  streamLength
    //----------------------------------

    /**
     *  @private
     *  Storage for streamLength property.
     */
    private var _streamLength:Number;

    /**
     *  @see INCManager#streamLength
     */
    public function get streamLength():Number 
	{
        return _streamLength;
    }

    //----------------------------------
    //  streamWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for streamWidth property.
     */
    private var _streamWidth:Number;

    /**
     *  @see INCManager#streamWidth
     */
    public function get streamWidth():Number 
	{
        return _streamWidth;
    }

    //----------------------------------
    //  streamHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for streamHeight property.
     */
    private var _streamHeight:Number;

    /**
     *  @see INCManager#streamHeight
     */
    public function get streamHeight():Number 
	{
        return _streamHeight;
    }

    //--------------------------------------------------------------------------
    //
    //  Public methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @see INCManager#isRTMP
     */
    public function isRTMP():Boolean
    {
        return _isRTMP != 0;
    }

    /**
     *  @see INCManager#connectToURL()
     */
    public function connectToURL(url:String):Boolean
    {
        initOtherInfo();
        
        contentPath = url;
        if (!contentPath || contentPath == "")
            throw new VideoError(VideoError.INVALID_CONTENT_PATH);

        // parse URL to determine what to do with it
        var parseResults:Object = parseURL(contentPath);

        if (!parseResults.streamName || parseResults.streamName == "")
            throw new VideoError(VideoError.INVALID_CONTENT_PATH, url);

        // connect to either rtmp or http or download and parse smil
        if (parseResults.isRTMP)
        {
			var canReuseRTMP:Boolean = canReuseOldConnection(parseResults);
			_isRTMP = 1;
			protocol = parseResults.protocol;
			_streamName = parseResults.streamName;
			serverName = parseResults.serverName;
			wrappedURL = parseResults.wrappedURL;
			portNumber = parseResults.portNumber;
			appName = parseResults.appName;
			if ( !appName || appName == "" ||
			     !_streamName || _streamName == "" )
				throw new VideoError(VideoError.INVALID_CONTENT_PATH, url);
			autoSenseBW = (_streamName.indexOf(",") >= 0);
			return (canReuseRTMP || connectRTMP());        	
        }
		else
		{
			var canReuseHTTP:Boolean = canReuseOldConnection(parseResults);                
			_isRTMP = 0;
			_streamName = parseResults.streamName;
			return (canReuseHTTP || connectHTTP());
		}
    }

	/**
	 *  @see INCManager#connectAgain()
	 */
	public function connectAgain():Boolean
	{
		var slashIndex:Number = appName.indexOf("/");
		if (slashIndex < 0) 
		{
			// return the appName and streamName back to original form
			// so we can start this process all over again with the
			// fallback server if necessary
			slashIndex = _streamName.indexOf("/");
			if (slashIndex >= 0) 
			{
				appName += "/";
				appName += _streamName.slice(0, slashIndex);
				_streamName = _streamName.slice(slashIndex + 1);
			}
			return false;
		}

		var newStreamName:String = appName.slice(slashIndex + 1);
		newStreamName += "/";
		newStreamName += _streamName;
		_streamName = newStreamName;
		appName = appName.slice(0, slashIndex);
		close();
		payload = 0;
		connTypeCounter = 0;
		cleanConns();
		connectRTMP();
		return true;
	}
	
    /**
     * INCManager#reconnect
     */
    public function reconnect():void
    {
        if (!_isRTMP)
		{
			var message:String = resourceManager.getString(
				"controls", "invalidCall");
            throw new Error(message);
		}

        _netConnection.addEventListener(NetStatusEvent.NET_STATUS, reconnectOnStatus);
        _netConnection.client = new NCManagerReconnectClient(this);
        _netConnection.connect(ncURI, false);
    }

    /**
     *  @see INCManager#close
     */
    public function close():void
    {
        if (_netConnection)
        {
            _netConnection.close();
            ncConnected = false;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Private Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Initialization.
     */
    private function initNCInfo():void
    {
        _isRTMP = -1;
        serverName = null;
        wrappedURL = null;
        portNumber = null;
        appName = null;
    }

    private function initOtherInfo():void 
    {
        contentPath = null;        
        _streamName = null;
        _streamLength = -1;
        _streamWidth = -1;
        _streamHeight = -1;
        streams = null;
        autoSenseBW = false;

        payload = 0;
        connTypeCounter = 0;
        cleanConns();
    }

    /**
     *  matches bitrate with stream
     *
     *  @private
     */
    private function bitrateMatch():void
    {
        var whichStream:Number;
        var checkBitrate:Number = _bitrate;

        if (isNaN(checkBitrate))
            checkBitrate = 0;
        
        for (var j:uint = 0; j < streams.length; j++)
            if (isNaN(streams[j].bitrate) || checkBitrate >= streams[j].bitrate)
            {
                whichStream = j;
                break;
            }

        if (isNaN(whichStream))
            throw new VideoError(VideoError.NO_BITRATE_MATCH);

        if (_streamName)
            _streamName += streams[whichStream].src;
        else
            _streamName = streams[whichStream].src;

        _streamLength = streams[whichStream].dur;
    }

    /**
     *  <p>Parses URL to determine if it is http or rtmp.  If it is rtmp,
     *  breaks it into pieces to extract server URL and port, application
     *  name and stream name.  If .flv is at the end of an rtmp URL, it
     *  will be stripped off.</p>
     *
     *  @private
     */
    private function parseURL(url:String):Object
    {
        var parseResults:Object = {};
		
		// get protocol
		var startIndex:uint = 0;
		var endIndex:int = url.indexOf(":/", startIndex);
		if (endIndex >= 0) 
		{
			endIndex += 2;
			parseResults.protocol = url.slice(startIndex, endIndex);
			parseResults.isRelative = false;
		} 
		else
			parseResults.isRelative = true;
		
		if ( parseResults.protocol != null &&
			     ( parseResults.protocol == "rtmp:/" ||
			       parseResults.protocol == "rtmpt:/" ||
			       parseResults.protocol == "rtmps:/" ||
			       parseResults.protocol == "rtmpe:/" ||
			       parseResults.protocol == "rmpte:/" ) )
		{
			parseResults.isRTMP = true;
			
			startIndex = endIndex;

			if (url.charAt(startIndex) == '/') 
			{
				startIndex++;
				// get server (and maybe port)
				var colonIndex:Number = url.indexOf(":", startIndex);
				var slashIndex:Number = url.indexOf("/", startIndex);
				if (slashIndex < 0) 
				{
					if (colonIndex < 0)
						parseResults.serverName = url.slice(startIndex);
					else 
					{
						endIndex = colonIndex;
						parseResults.portNumber = url.slice(startIndex, endIndex);
						startIndex = endIndex + 1;
						parseResults.serverName = url.slice(startIndex);
					}
					return parseResults;
				}
				if (colonIndex >= 0 && colonIndex < slashIndex) 
				{
					endIndex = colonIndex;
					parseResults.serverName = url.slice(startIndex, endIndex);
					startIndex = endIndex + 1;
					endIndex = slashIndex;
					parseResults.portNumber = url.slice(startIndex, endIndex);
				} 
				else 
				{
					endIndex = slashIndex;
					parseResults.serverName = url.slice(startIndex, endIndex);
				}
				startIndex = endIndex + 1;
			}

			// handle wrapped RTMP servers bit recursively, if it is there
			if (url.charAt(startIndex) == '?') 
			{
				var subURL:String = url.slice(startIndex + 1);
				var subParseResults:Object = parseURL(subURL);
				if (!subParseResults.protocol || !subParseResults.isRTMP)
					throw new VideoError(VideoError.INVALID_CONTENT_PATH, url);
				parseResults.wrappedURL = "?";
				parseResults.wrappedURL += subParseResults.protocol;
				if (subParseResults.serverName != null) 
				{
					parseResults.wrappedURL += "/";
					parseResults.wrappedURL +=  subParseResults.server;
				}
				if (subParseResults.wrappedURL != null) 
				{
					parseResults.wrappedURL += "/?";
					parseResults.wrappedURL +=  subParseResults.wrappedURL;
				}
				parseResults.appName = subParseResults.appName;
				parseResults.streamName = subParseResults.streamName;
				return parseResults;
			}
			
			// get application name
			endIndex = url.indexOf("/", startIndex);
			if (endIndex < 0) 
			{
				parseResults.appName = url.slice(startIndex);
				return parseResults;
			}
			parseResults.appName = url.slice(startIndex, endIndex);
			startIndex = endIndex + 1;

			// check for instance name to be added to application name
			endIndex = url.indexOf("/", startIndex);
			if (endIndex < 0) 
			{
				parseResults.streamName = url.slice(startIndex);
				// strip off .flv and .flv2 if included
				if (parseResults.streamName.slice(-5).toLowerCase() == ".flv2")
					parseResults.streamName = parseResults.streamName.slice(0, -5);
				else if (parseResults.streamName.slice(-4).toLowerCase() == ".flv")
					parseResults.streamName = parseResults.streamName.slice(0, -4);
				return parseResults;
			}
			parseResults.appName += "/";
			parseResults.appName += url.slice(startIndex, endIndex);
			startIndex = endIndex + 1;
				
			// get flv name
			parseResults.streamName = url.slice(startIndex);
			// strip off .flv and .flv2 if included
			if (parseResults.streamName.slice(-5).toLowerCase() == ".flv2")
				parseResults.streamName = parseResults.streamName.slice(0, -5);
			else if (parseResults.streamName.slice(-4).toLowerCase() == ".flv")
				parseResults.streamName = parseResults.streamName.slice(0, -4);
			
		} 
		else 
		{
			// is http, just return the full url received as streamName
			parseResults.isRTMP = false;
			parseResults.streamName = url;
		}

		return parseResults;
    }

    /**
     *  @private
     *  <p>Handles creating <code>NetConnection</code> instance for
     *  progressive download of FLV via http.</p>
     */
    private function connectHTTP():Boolean
    {
        _netConnection = new NetConnection();
        _netConnection.objectEncoding = ObjectEncoding.AMF0;
        _netConnection.connect(null);
        ncConnected = true;
        return true;
    }

    /**
     *  @private
     *  <p>Top level function for creating <code>NetConnection</code>
     *  instance for streaming playback of FLV via rtmp.  Actually
     *  tries to create several different connections using different
     *  protocols and ports in a pipeline, so multiple connection
     *  attempts may be occurring simultaneously, and will use the
     *  first one that connects successfully.</p>
     */
    private function connectRTMP():Boolean
    {
        // setup timeout
        connectionTimer.reset();
        connectionTimer.start();

        tryNC = [];

        tryNCTimer = new Timer(DEFAULT_NC_TIMEOUT);
        tryNCTimer.addEventListener(TimerEvent.TIMER, nextConnect);

        for (var i:uint = 0; i < RTMP_CONN.length; i++)
        {
            tryNC[i] = new NetConnection();
            tryNC[i].objectEncoding = ObjectEncoding.AMF0;
            tryNC[i].client = new NCManagerConnectClient(tryNC[i], this, i);
            tryNC[i].addEventListener(NetStatusEvent.NET_STATUS, connectOnStatus);
        }

        nextConnect(null);
        return false;
    }

    /**
     *  @private
     *  <p>Does work of trying to open rtmp connections.  Called either
     *  by <code>connectRTMP</code> or <code>Timer</code> set up in
     *  that method.</p>
     *
     *  <p>For creating rtmp connections.</p>
     * 
     *  @see #connectRTMP()
     */
    private function nextConnect(event:Event):void
    {
        tryNCTimer.reset();

		var tempProtocol:String;
		var port:String;
		if (connTypeCounter == 0) 
		{
			tempProtocol = protocol;
			if (portNumber)
				port = portNumber;
			else 
				for (var i:Number = 0; i < RTMP_CONN.length; i++)
					if (protocol == RTMP_CONN[i].protocol) 
					{
						port = RTMP_CONN[i].port;
						break;
					}
		} 
		else 
		{
			tempProtocol = RTMP_CONN[connTypeCounter].protocol;
			port = RTMP_CONN[connTypeCounter].port;
		}
		var connectionURL:String = tempProtocol + ((!serverName) ? "" : 
			"/" + serverName + ":" + port + "/") + ((!wrappedURL) ? "" : 
			wrappedURL + "/") + appName;

        tryNC[connTypeCounter].client.pending = true;
        tryNC[connTypeCounter].connect(connectionURL, autoSenseBW);

        if (connTypeCounter < RTMP_CONN.length - 1)
        {
            connTypeCounter++;
            tryNCTimer.start();
        }
    }

    /**
     *  @private
     *  <p>Stops all timers, closes all unneeded connections, and other
     *  cleanup related to the <code>connectRTMP</code> strategy of
     *  pipelining connection attempts to different protocols and
     *  ports.</p>
     *
     *  <p>For creating rtmp connections.</p>
     *
     *  @see #connectRTMP()
     */
    private function cleanConns():void
    {
        if (tryNCTimer != null)
        {
            tryNCTimer.reset();
            tryNCTimer = null;
        }

        if (tryNC)
        {
            for (var i:uint = 0; i < tryNC.length; i++)
            {
                if (tryNC[i])
                {
                    tryNC[i].removeEventListener(NetStatusEvent.NET_STATUS, connectOnStatus);
                    if (tryNC[i].client.pending)
                        tryNC[i].addEventListener(NetStatusEvent.NET_STATUS, disconnectOnStatus);
                    else
                        tryNC[i].close();
                }
                tryNC[i] = null;
            }
            tryNC = null;
        }
    }

    /**
     *  @private
     *  <p>Starts another pipelined connection attempt with
     *  <code>connectRTMP</code> with the fallback server.</p>
     *
     *  <p>For creating rtmp connections.</p>
     *
     *  @see #connectRTMP()
     */
    private function tryFallBack():void
    {
        if (serverName == fallbackServerName || !fallbackServerName)
        {
            //it's not connected
            _netConnection = null;
            ncConnected = false;
            owner.ncConnected();
        }
        else
        {
        	connTypeCounter = 0;
            cleanConns();
            serverName = fallbackServerName;
            connectRTMP();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Callbacks and event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  dispatches reconnect event, called by
     *  <code>NetConnection.onBWDone</code>
     */
    public function onReconnected():void
    {
    	_netConnection.removeEventListener(NetStatusEvent.NET_STATUS, connectOnStatus);
    	ncConnected = true;
        owner.ncReconnected();
    }

    /**
     *  @private
     *  <p>Starts another pipelined connection attempt with
     *  <code>connectRTMP</code> with the fallback server.</p>
     *
     *  <p>For creating rtmp connections.</p>
     *
     *  @see #connectRTMP()
     */
    public function onConnected(p_nc:NetConnection, p_bw:Number):void
    {
        // avoid timeout
        connectionTimer.reset();

        // ditch these now unneeded functions and listeners
        // Don't need to assign the client to null
        // since it will anyways get reassigned on
        // reconnection.
        // p_nc.client = null;
        p_nc.removeEventListener(NetStatusEvent.NET_STATUS, connectOnStatus);
        
        // store pointers to the successful connection and uri
        _netConnection = p_nc;
        ncURI = _netConnection.uri;
        ncConnected = true;

        if (autoSenseBW)
        {
            _bitrate = p_bw * 1024;

            if (streams)
                bitrateMatch();
            else
            {
                var sSplit:Array = _streamName.split(",");

                // remove leading and trailing whitespace from string
                for (var i:uint = 0; i < sSplit.length; i+=2)
                {
                    var sName:String = sSplit[i].replace(/^\s*(\S.*\S)\s*$/, "$1");
                    if (i + 1 < sSplit.length)
                    {
                        // If we have less bw than the next threshold or if
                        // there isn't another threshold (last string)
                        if (p_bw <= Number(sSplit[i+1]))
                        {
                            _streamName = sName;
                            break;
                        }
                    }
                    else
                    {
                        _streamName = sName;
                        break;
                    }
                } // for
		        // strip off .flv and .flv2 if included
		        if (_streamName.slice(-5).toLowerCase() == ".flv2")
		            _streamName = _streamName.slice(0, -5);   
		        else if (_streamName.slice(-4).toLowerCase() == ".flv")
		            _streamName = _streamName.slice(0, -4);                
            }
        }


        // if we need to get the stream length from the server, do it here
		// Donot call getStreamLength when main.asc is not present
		// since getStreamLength is defined in main.asc
        if ((!owner.isLive && _streamLength == -1) && owner.autoBandWidthDetection)
        {
            var res:Responder = new Responder(getStreamLengthResult,
                    getStreamLengthStatus);
            _netConnection.call("getStreamLength", res, _streamName);
        }
        else
            owner.ncConnected();
    }

    /**
     *  @private
     *  netStatus event listener when connecting
     */
    public function connectOnStatus(event:NetStatusEvent):void
    {
        var stuff:Object;
		var target:NetConnection = NetConnection(event.target);

        target.client.pending = false;

        if (event.info.code == "NetConnection.Connect.Success")
        {
            _netConnection = tryNC[target.client.connIndex];
			// Force call to onConnected when main.asc is not present
			// which would call this function through onBWDone
			if (!owner.autoBandWidthDetection)
				onConnected(_netConnection, 0);
            tryNC[target.client.connIndex] = null;
            cleanConns();
        }
        else if (((event.info.code == "NetConnection.Connect.Failed")
                    || (event.info.code == "NetConnection.Connect.Rejected"))
                    && (target.client.connIndex == (RTMP_CONN.length - 1)))
			// Try rearranging the app URL, then the fallbackServer
			if (!connectAgain())
				tryFallBack();
    }

    /**
     *  @private
     *  netStatus event listener when reconnecting
     */
    public function reconnectOnStatus(event:NetStatusEvent):void
    {
        if ((event.info.code == "NetConnection.Connect.Failed")
                || (event.info.code == "NetConnection.Connect.Rejected"))
        {
            // Try the fallbackServer
            _netConnection = null;
            ncConnected = false;
            owner.ncReconnected();
        }
    }

    /**
     *  @private
     *  netStatus event listener for disconnecting extra
     *  NetConnections that were opened in parallel
     */
    public function disconnectOnStatus(event:NetStatusEvent):void
    {
        if (event.info.code == "NetConnection.Connect.Success")
        {
            event.target.removeEventListener(NetStatusEvent.NET_STATUS,
                    disconnectOnStatus);
            NetConnection(event.target).close();
        }
    }

    /**
     *  @private
     *  Responder function to receive streamLength result from
     *  server after making rpc
     */
    private function getStreamLengthResult(length:Number):void
    {
		if (length > 0)
	        _streamLength = length;
        owner.ncConnected();
    }

    /**
     *  @private
     *  Responder function to receive status messages for rpc to
     *  get streamLength
     */
    private function getStreamLengthStatus(item:Object):void 
    {
    }

    /**
     *  @private
     *  <p>Called by timer to timeout all connection attempts.</p>
     *
     *  <p>For creating rtmp connections.</p>
     *
     *  @see #connectRTMP()
     */
    private function onFCSConnectTimeOut(event:Event):void
    {
        cleanConns();
        _netConnection = null;
        ncConnected = false;
        if (!connectAgain())
        	owner.ncConnected();
    }
    
    /**
     *  @private     
     *  <p>Compares connection info with previous NetConnection,
     *  will reuse existing connection if possible.
     */
    private function canReuseOldConnection(parseResults:Object):Boolean 
    {
        // no reuse if no prior connection
        if (_netConnection == null || !ncConnected) 
            return false;

        // http connection
        if (!parseResults.isRTMP) 
        {
            // can reuse if prev connection was http
            if (!_isRTMP) 
                return true;
            // cannot reuse if was rtmp--close
            owner.close();
            _netConnection = null;
            ncConnected = false;
            initNCInfo();
            return false;
        }

        // rtmp connection
        if (_isRTMP) 
        {
            if (parseResults.serverName == serverName && parseResults.appName == appName &&
			     parseResults.protocol == protocol && parseResults.portNumber == portNumber &&
			     parseResults.wrappedURL == wrappedURL)
                return true;
            // cannot reuse this rtmp--close
            owner.close();
            _netConnection = null;
            ncConnected = false;
        }

        initNCInfo();
        return false;
    }

} // class mx.controls.videoClasses.NCManager
}
