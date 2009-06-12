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
import flash.events.ProgressEvent;
import flash.events.TimerEvent;
import flash.media.SoundTransform;
import flash.media.Video;
import flash.net.NetConnection;
import flash.net.NetStream;
import flash.utils.Timer;
import mx.core.mx_internal;
import mx.events.MetadataEvent;
import mx.events.VideoEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the <code>NetConnection</code> is closed,
 *  whether by being timed out or by calling the <code>close()</code> method.
 *  This event is only dispatched with RTMP streams, never HTTP.
 *
 *  @eventType mx.events.VideoEvent.CLOSE
 *  @helpid 3482
 *  @tiptext close event
 */
[Event(name="close", type="mx.events.VideoEvent")]

/**
 *  Dispatched when playing completes by reaching the end of the FLV.
 *  This event not dispatched if the method <code>stop()</code> or
 *  <code>pause()</code> are called.
 *
 *  <p>When using progressive download and not setting totalTime
 *  explicitly and downloading an FLV with no metadata duration,
 *  the totalTime will be set to an approximate total value, now
 *  that we have played the whole file we can make a guess.
 *  That value is set by the time this event is dispatched.</p>
 *
 *  @eventType mx.events.VideoEvent.COMPLETE
 *  @helpid 3482
 *  @tiptext complete event
 */
[Event(name="complete", type="mx.events.VideoEvent")]

/**
 *  Dispatched when a cue point is reached.
 *
 *  @eventType mx.events.MetadataEvent.CUE_POINT
 *  @helpid 3483
 *  @tiptext cuePoint event
 */
[Event(name="cuePoint", type="mx.events.MetadataEvent")]

/**
 *  Dispatched the first time the FLV metadata is reached.
 *
 *  @eventType mx.events.MetadataEvent.METADATA_RECEIVED
 *  @tiptext metadata event
 */
[Event(name="metadataReceived", type="mx.events.MetadataEvent")]

/**
 *  Dispatched every 0.25 seconds while the video is playing.
 *  This event is not dispatched when it is paused or stopped,
 *  unless a seek occurs.
 *
 *  @eventType mx.events.VideoEvent.PLAYHEAD_UPDATE
 *  @helpid 3480
 *  @tiptext change event
 */
[Event(name="playheadUpdate", type="mx.events.VideoEvent")]

/**
 *  Dispatched every 0.25 seconds while the video is downloading.
 *
 *  <p>Indicates progress made in number of bytes downloaded.
 *  You can use this event to check the number of bytes loaded
 *  or the number of bytes in the buffer.
 *  This event starts when <code>load</code> is called and ends
 *  when all bytes are loaded or if there is a network error.</p>
 *
 *  @eventType flash.events.ProgressEvent.PROGRESS
 *  @helpid 3485
 *  @tiptext progress event
 */
[Event(name="progress", type="flash.events.ProgressEvent")]

/**
 *  Dispatched when the video is loaded and ready to display.
 *
 *  <p>This event is dispatched the first time the VideoPlayer
 *  enters a responsive state after a new FLV is loaded
 *  with the <code>play()</code> or <code>load()</code> method.
 *  It is dispatched once for each FLV loaded.</p>
 *
 *  @eventType mx.events.VideoEvent.READY
 */
[Event(name="ready", type="mx.events.VideoEvent")]

/**
 *  Dispatched when the video autorewinds.
 *
 *  @eventType mx.events.VideoEvent.REWIND
 */
[Event(name="rewind", type="mx.events.VideoEvent")]

/**
 *  Dispatched when the playback state changes.
 *
 *  <p>This event can be used to track when playback enters and leaves
 *  unresponsive states (for example in the middle of connecting,
 *  resizing or rewinding) during which times the method
 *  <code>play()</code>, <code>pause()</code>, <code>stop()</code>
 *  and <code>seek()</code> will queue the requests to be executed
 *  when the player enters a responsive state.</p>
 *
 *  @eventType mx.events.VideoEvent.STATE_CHANGE
 */
[Event(name="stateChange", type="mx.events.VideoEvent")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[ExcludeClass]

[ResourceBundle("controls")]

/**
 *  @private
 *  VideoPlayer is an easy to use wrapper for Video, NetConnection,
 *  NetStream, etc. that makes playing FLV easy.  It supports streaming
 *  from Flash Communication Server (FCS) and http download of FLVs.
 *
 *  <p>VideoPlayer extends Video.</p>
 *
 *  @tiptext    VideoPlayer: FLV player
 *  @helpid ???
 */
public class VideoPlayer extends Video
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  public state constants
    //----------------------------------

    /**
     *  <p>State constant.  This is the state when the VideoPlayer is
     *  constructed and when the stream is closed by a call to
     *  <code>close()</code> or timed out on idle.</p>
     *
     *  <p>This is a responsive state.</p>
     *
     *  @see #state
     *  @see #stateResponsive
     *  @see #connected
     *  @see #idleTimeout
     *  @see #close()
     */
    public static const DISCONNECTED:String = "disconnected";

    /**
     *  <p>State constant.  FLV is loaded and play is stopped.  This state
     *  is entered when <code>stop()</code> is called and when the
     *  playhead reaches the end of the stream.</p>
     *
     *  <p>This is a responsive state.</p>
     *
     *  @see #state
     *  @see #stateResponsive
     *  @see #stop()
     */
    public static const STOPPED:String = "stopped";

    /**
     *  <p>State constant.  FLV is loaded and is playing.
     *  This state is entered when <code>play()</code>
     *  is called.</p>
     *
     *  <p>This is a responsive state.</p>
     *
     *  @see #state
     *  @see #stateResponsive
     *  @see #play()
     */
    public static const PLAYING:String = "playing";

    /**
     *  <p>State constant.  FLV is loaded, but play is paused.
     *  This state is entered when <code>pause()</code> is
     *  called or when <code>load()</code> is called.</p>
     *
     *  <p>This is a responsive state.</p>
     *
     *  @see #state
     *  @see #stateResponsive
     *  @see #pause()
     *  @see #load()
     */
    public static const PAUSED:String = "paused";

    /**
     *  <p>State constant.  State entered immediately after
     *  <code>play()</code> or <code>load()</code> is called.</p>
     *
     *  <p>This is a responsive state.</p>
     *
     *  @see #state
     *  @see #stateResponsive
     */
    public static const BUFFERING:String = "buffering";

    /**
     *  <p>State constant.  State entered immediately after
     *  <code>play()</code> or <code>load()</code> is called.</p>
     *
     *  <p>This is a unresponsive state.</p>
     *
     *  @see #state
     *  @see #stateResponsive
     *  @see #load()
     *  @see #play()
     */
    public static const LOADING:String = "loading";

    /**
     *  <p>State constant.  Stream attempted to load was unable to load
     *  for some reason.  Could be no connection to server, stream not
     *  found, etc.</p>
     *
     *  <p>This is a unresponsive state.</p>
     *
     *  @see #state  
     *  @see #stateResponsive
     */
    public static const CONNECTION_ERROR:String = "connectionError";

    /**
     *  <p>State constant.  State entered during a autorewind triggered
     *  by a stop.  After rewind is complete, the state will be
     *  <code>STOPPED</code>.</p>
     *
     *  <p>This is a unresponsive state.</p>
     *
     *  @see #state  
     *  @see #autoRewind
     *  @see #stateResponsive
     */
    public static const REWINDING:String = "rewinding";

    /**
     *  <p>State constant.  State entered after <code>seek()</code>
     *  is called.</p>
     *
     *  <p>This is a unresponsive state.</p>
     *
     *  @see #state  
     *  @see #stateResponsive
     *  @see #seek()
     */
    public static const SEEKING:String = "seeking";

    /**
     *  <p>State constant.  State entered during autoresize.</p>
     *
     *  <p>This is a unresponsive state.</p>
     *
     *  @see #state
     *  @see #stateResponsive
     */
    public static const RESIZING:String = "resizing";

    /**
     *  <p>State constant.  State during execution of queued command.
     *  There will never get a "stateChange" event notification with
     *  this state; it is internal only.</p>
     *
     *  <p>This is a unresponsive state.</p>
     *
     *  @see #state
     *  @see #stateResponsive
     */
    public static const EXEC_QUEUED_CMD:String = "execQueuedCmd";

    //----------------------------------
    //  buffer states
    //----------------------------------

    /**
     *  @private
     */
    private static const BUFFER_EMPTY:String = "bufferEmpty";

    /**
     *  @private
     */
    private static const BUFFER_FULL:String = "bufferFull";

    /**
     *  @private
     *  use this full plus state to work around bug where sometimes
     *  empty full messages coming in quick succession come in wrong
     *  order
     */

    private static const BUFFER_FLUSH:String = "bufferFlush";

    //----------------------------------
    //  default times for intervals
    //----------------------------------

    public static const DEFAULT_UPDATE_TIME_INTERVAL:Number = 250;   // .25 seconds
    public static const DEFAULT_UPDATE_PROGRESS_INTERVAL:Number = 250;   // .25 seconds
    public static const DEFAULT_IDLE_TIMEOUT_INTERVAL:Number = 300000; // five minutes
    public static const AUTO_RESIZE_INTERVAL:Number = 100;        // .1 seconds
    public static const AUTO_RESIZE_PLAYHEAD_TIMEOUT:Number = .5;       // .5 seconds
    public static const AUTO_RESIZE_METADATA_DELAY_MAX:Number = 5;        // .5 seconds
    public static const FINISH_AUTO_RESIZE_INTERVAL:Number = 250;  // .25 seconds
    public static const RTMP_DO_STOP_AT_END_INTERVAL:Number = 500; // .5 seconds
    public static const RTMP_DO_SEEK_INTERVAL:Number = 100; // .1 seconds
    public static const HTTP_DO_SEEK_INTERVAL:Number = 250; // .25 seconds    
    public static const HTTP_DO_SEEK_MAX_COUNT:Number = 4; // 4 times * .25 seconds = 1 second
    public static const CLOSE_NS_INTERVAL:Number = .25; // .25 secconds
    public static const HTTP_DELAYED_BUFFERING_INTERVAL:Number = 100; // .1 seconds
    
    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------
    
    /**
     *  <p>Set this property to the name of your custom class to
     *  make all VideoPlayer objects created use that class as the
     *  default INCManager implementation.  The default value is
     *  "mx.controls.videoClasses.NCManager".</p>
     */
    public static var DEFAULT_INCMANAGER:Class = NCManager;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  <p>Constructor.</p>
     *  @private
     *  Constructor.
     *  @helpid 0
     *  @see INCManager
     *  @see NCManager
     */
    public function VideoPlayer(width:uint, height:uint, ncMgrClassName:Class = null) 
    {
        super(width, height);
        
        // init state variables
        _state = DISCONNECTED;
        cachedState = _state;
        bufferState = BUFFER_EMPTY;
        sawPlayStop = false;
        cachedPlayheadTime = 0;
        _metadata = null;
        startingPlay = false;
        invalidSeekTime = false;
        invalidSeekRecovery = false;
        currentPos = 0;
        atEnd = false;
        cmdQueue = [];
        readyDispatched = false;
        lastUpdateTime = -1;
        sawSeekNotify = false;
        
        // put off creation of INCManager until last minute to
        // give time to customize DEFAULT_INCMANAGER
        ncMgrClassName = (ncMgrClassName == null) ? DEFAULT_INCMANAGER : ncMgrClassName;

        // setup timers
        updateTimeTimer = new Timer(DEFAULT_UPDATE_TIME_INTERVAL);
        updateTimeTimer.addEventListener(TimerEvent.TIMER, doUpdateTime);
        updateProgressTimer = new Timer(DEFAULT_UPDATE_PROGRESS_INTERVAL);
        updateProgressTimer.addEventListener(TimerEvent.TIMER, doUpdateProgress);
        idleTimeoutTimer = new Timer(DEFAULT_IDLE_TIMEOUT_INTERVAL, 1);
        idleTimeoutTimer.addEventListener(TimerEvent.TIMER, doIdleTimeout);
        autoResizeTimer = new Timer(AUTO_RESIZE_INTERVAL);
        autoResizeTimer.addEventListener(TimerEvent.TIMER, doAutoResize);
        rtmpDoStopAtEndTimer = new Timer(RTMP_DO_STOP_AT_END_INTERVAL);
        rtmpDoStopAtEndTimer.addEventListener(TimerEvent.TIMER, rtmpDoStopAtEnd);
        rtmpDoSeekTimer = new Timer(RTMP_DO_SEEK_INTERVAL);
        rtmpDoSeekTimer.addEventListener(TimerEvent.TIMER, doSeek);
        httpDoSeekTimer = new Timer(HTTP_DO_SEEK_INTERVAL);
        httpDoSeekTimer.addEventListener(TimerEvent.TIMER, doSeek);        
        finishAutoResizeTimer = new Timer(FINISH_AUTO_RESIZE_INTERVAL, 1); 
        finishAutoResizeTimer.addEventListener(TimerEvent.TIMER, finishAutoResize);
        delayedBufferingTimer = new Timer(HTTP_DELAYED_BUFFERING_INTERVAL);
        delayedBufferingTimer.addEventListener(TimerEvent.TIMER, doDelayedBuffering);
        
        // setup intervals
        httpDoSeekCount = 0;

        // init get/set properties
        _isLive = false;
        autoPlay = true;
        _autoRewind = true;
        _bufferTime = 0.1;
        _volume = 100;
        _soundTransform = new SoundTransform(_volume);
        _visible = true;
        _url = "";
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  See VideoDisplay's autoBandWidthDetection
     */
    public var autoBandWidthDetection:Boolean = false;  
    
    /**
     *  @private
     */
    private var cachedState:String;
    
    /**
     *  @private
     */    
    private var bufferState:String;
    
    /**
     *  @private
     */        
    private var sawPlayStop:Boolean;
        
    /**
     *  @private
     */
    private var cachedPlayheadTime:Number;
    
    /**
     *  @private
     */
    private var startingPlay:Boolean;
        
    /**
     *  @private
     */
    private var invalidSeekRecovery:Boolean;
        
    /**
     *  @private
     */
    private var invalidSeekTime:Boolean;
        
    /**
     *  @private
     */
    private var readyDispatched:Boolean;
        
    /**
     *  @private
     */        
    private var lastUpdateTime:Number;

    /**
     *  @private
     */           
    private var sawSeekNotify:Boolean;
         
    /**
     *  @private
     */
    public var ncMgrClassName:Class;

    //----------------------------------
    //  info about NetStream
    //----------------------------------
        
    /**
     *  @private
     */
    private var ns:VideoPlayerNetStream;
        
    /**
     *  @private
     */
    private var currentPos:Number;
        
    /**
     *  @private
     */
    private var atEnd:Boolean;
        
    /**
     *  @private
     */
    private var streamLength:Number;

    /**
     *  @private
     *  <p>If true, then video plays immediately, if false waits for
     *  <code>play</code> to be called.  Set to true if stream is
     *  loaded with call to <code>play()</code>, false if loaded
     *  by call to <code>load()</code>.</p>
     *
     *  <p>Even if <code>autoPlay</code> is set to false, we will start
     *  loading the video after <code>initialize()</code> is called.
     *  In the case of FCS, this means creating the stream and loading
     *  the first frame to display (and loading more if
     *  <code>autoSize</code> or <code>aspectRatio</code> is true).  In
     *  the case of HTTP download, we will start downloading the stream
     *  and show the first frame.</p>
     */
    private var autoPlay:Boolean;

    /**
     *  @private
     *  The bytes loaded at the prior sample.
     *  Used to determine whether to dispatch a progress event.
     */
    private var _priorBytesLoaded:int = -1;
    
    /**
     *  @private
     *  Internally used for sizing
     */
    private var internalVideoWidth:Number = -1;
    private var internalVideoHeight:Number = -1;
    private var prevVideoWidth:Number = -1;
    private var prevVideoHeight:Number = -1;

    /**
     *  @private
     *  Timers
     */
    private var updateTimeTimer:Timer;
    private var updateProgressTimer:Timer;
    private var idleTimeoutTimer:Timer;
    private var autoResizeTimer:Timer;
    private var rtmpDoStopAtEndTimer:Timer;
    private var rtmpDoSeekTimer:Timer;
    private var httpDoSeekTimer:Timer;    
    private var finishAutoResizeTimer:Timer;
    private var delayedBufferingTimer:Timer;

    /**
     *  @private
     *  Count for httpDoSeekTimer
     */
    private var httpDoSeekCount:Number;

    /**
     *  @private
     *  queues up Objects describing queued commands to be run later
     *  QueuedCommand defined at the end of this file
     */
    private var cmdQueue:Array;

    /**
     *  @private
     *  Used for accessing localized Error messages.
     */
    private var resourceManager:IResourceManager =
                                    ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  scaleX
    //----------------------------------
    
    /**
     *  100 is standard scale
     *
     *  @see #setScale()
     *  @tiptext Specifies the horizontal scale factor
     *  @helpid 3974
     */
    override public function set scaleX(xs:Number):void
    {
        setScale(xs, this.scaleY);
    }
    
    //----------------------------------
    //  scaleY
    //----------------------------------
    
    /**
     *  100 is standard scale
     *
     *  @see #setScale()
     *  @tiptext Specifies the vertical scale factor
     *  @helpid 3975
     */
    override public function set scaleY(ys:Number):void
    {
        setScale(this.scaleX, ys);
    }
    
    //----------------------------------
    //  width
    //----------------------------------
    
    /**
     *  <p>Width of video instance.  Not same as Video.width, that is videoWidth.</p>
     *
     *  @see #setSize()
     *  @see #videoWidth
     *  @helpid 0
     */
    override public function set width(value:Number):void
    {
        setSize(value, height);
    }
    
    //----------------------------------
    //  height
    //----------------------------------
    
    /**
     *  <p>Height of video.  Not same as Video.height, that is videoHeight.</p>
     *
     *  @see #setSize()
     *  @see #videoHeight
     *  @helpid 0
     */
    override public function set height(value:Number):void
    {
        setSize(width, value);
    }

    
    /**
     *  <p>Source width of loaded FLV file.  Read only.  Returns
     *  undefined if no information available yet.</p>
     *
     *  @see #width
     */
    override public function get videoWidth():int
    {
        // _videoWidth and _videoHeight come from the NCManager, which would normally mean they
        // came from the SMIL and they get top priority if they are non-negative
        if (internalVideoWidth > 0) return internalVideoWidth;
        // Next priority is the metadata height and width.  If the metadata height and width are the same,
        // then it might be buggy metadata from an older version of the sorenson encoder, so we ignore it
        // and use the super.videoWidth and super.videoHeight instead ONLY if ready has been dispatched.
        // this is because we never consider the super.videoWidth and super.videoHeight to be ready
        // until ready is dispatched--it could still be 0 or still match the last video loaded
        if (metadata != null && !isNaN(metadata.width) && !isNaN(metadata.height))
        {
            if (metadata.width == metadata.height && readyDispatched)
                return super.videoWidth;
            else
                return int(metadata.width);
        }
        // last priority is the super.videoWidth and the super.videoHeight, which is
        // only used if ready has been dispatched, otherwise return -1
        if (readyDispatched) return super.videoWidth;
        return -1;
    }

    /**
     *  <p>Source height of loaded FLV file.  Read only.  Returns
     *  undefined if no information available yet.</p>
     *
     *  @see #height
     */
    override public function get videoHeight():int
    {
        // _videoWidth and _videoHeight come from the NCManager, which would normally mean they
        // came from the SMIL and they get top priority if they are non-negative
        if (internalVideoHeight > 0) return internalVideoHeight;
        // Next priority is the metadata height and width.  If the metadata height and width are the same,
        // then it might be buggy metadata from an older version of the sorenson encoder, so we ignore it
        // and use the super.videoWidth and super.videoHeight instead ONLY if ready has been dispatched.
        // this is because we never consider the super.videoWidth and super.videoHeight to be ready
        // until ready is dispatched--it could still be 0 or still match the last video loaded
        if (metadata != null && !isNaN(metadata.width) && !isNaN(metadata.height))
        {
            if (metadata.width == metadata.height && readyDispatched)
                return super.videoHeight;
            else
                return int(metadata.height);
        }
        // last priority is the super.videoWidth and the super.videoHeight, which is
        // only used if ready has been dispatched, otherwise return -1
        if (readyDispatched) return super.videoHeight;
        return -1;
    }
    
    //----------------------------------
    //  visible
    //----------------------------------
    
    /**
     *  @private
     */
    private var _visible:Boolean;
    
    /**
     * <p>Use this instead of <code>_visible</code> because we
     * sometimes do internal visibility management when doing an
     * autoresize.</p>
     */
    override public function get visible():Boolean 
    {
        _visible = super.visible;
        return _visible;
    }
    
    /**
     *  @private
     */
    override public function set visible(v:Boolean):void 
    {
        _visible = v;
        super.visible = _visible;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  autoRewind
    //----------------------------------
    
    /**
     *  @private
     */
    private var _autoRewind:Boolean;
    
    /**
     *  <p>Determines whether the FLV is rewound to the first frame
     *  when play stops, either by calling <code>stop()</code> or by
     *  reaching the end of the stream.  Meaningless for live streams.</p>
     *
     *  @helpid 0
     */
    public function get autoRewind():Boolean
    {
        return _autoRewind;
    }
        
    /**
     *  @private
     */
    public function set autoRewind(flag:Boolean):void
    {
        _autoRewind = flag;
    }

    //----------------------------------
    //  playheadTime
    //----------------------------------
    
    /**
     *  <p>The current playhead time in seconds.  Setting does a seek
     *  and has all the restrictions of a seek.</p>
     *
     *  <p>The event "playheadUpdate" is dispatched when the playhead
     *  time changes, including every .25 seconds while the FLV is
     *  playing.</p>
     *
     *  @return The playhead position, measured in seconds since the start.  Will return a fractional value.
     *
     *  @tiptext Current position of the playhead in seconds
     *  @helpid 3463
     *  @see #seek()
     */
    public function get playheadTime():Number
    {
        var nowTime:Number = (ns == null) ? currentPos : ns.time; // or _ncMgr.isHttp ? ns.time : ns.time + currentPos;
        if (_metadata && _metadata.audiodelay) 
        {
            nowTime -= _metadata.audiodelay;
            if (nowTime < 0) nowTime = 0;
        }
        return nowTime;       
    }
        
    /**
     *  @private
     */
    public function set playheadTime(position:Number):void
    {
        seek(position);
    }

    //----------------------------------
    //  url
    //----------------------------------
    
    /**
     *  @private
     */
    private var _url:String;
    
    /**
     *  <p>url of currently loaded (or loading) stream. Will be url
     *  last sent to <code>play()</code> or <code>load()</code>, <code>null</code>
     *  if no stream is loaded.</p>
     *
     *  @tiptext Holds the relative path and filename of the media to be streamed
     *  @helpid 3457
     */
    public function get url():String
    {
        return _url;
    }

    //----------------------------------
    //  volume
    //----------------------------------
    
    /**
     *  @private
     */
    private var _volume:Number;
    
    /**
     *  <p>Volume control in range from 0 to 1.</p>
     *
     *  @return The most recent volume setting
     *
     *  @tiptext The volume setting in value range from 0 to 1.
     *  @helpid 3468
     *  @see #soundTransform
     */
    public function get volume():Number
    {
        return _volume;
    }
        
    /**
     *  @private
     */
    public function set volume(aVol:Number):void
    {
        if ((aVol>= 0) && (aVol <= 1)) 
            _volume = aVol;
        else if (aVol < 0)
            _volume = 0;
        else
            _volume = 1;

        _soundTransform.volume = _volume;
        if (ns != null) 
            ns.soundTransform = _soundTransform;
    }

    //----------------------------------
    //  soundTransform
    //----------------------------------
    
    /**
     *  @private
     */
    private var _soundTransform:SoundTransform;
    
    /**
     *  <p>Provides direct access to the
     *  <code>flash.media.SoundTransform</code> object to expose
     *  more sound control.  Must set property for changes to take
     *  effect, get property just to get a copy of the current
     *  settings to tweak.
     *
     *  @see #volume
     */
    public function get soundTransform():SoundTransform 
    {
        return _soundTransform;
    }
        
    /**
     *  @private
     */
    public function set soundTransform(s:SoundTransform):void 
    {
        _soundTransform = s;
        _volume = _soundTransform.volume;
        ns.soundTransform = _soundTransform;
    }

    //----------------------------------
    //  isRTMP
    //----------------------------------
    
    /**
     * True if stream is RTMP download (streaming from Flash
     * Communication Server), read only.
     */
    public function get isRTMP():Boolean 
    {
        if (_ncMgr == null) 
            return true;
        return _ncMgr.isRTMP();
    }

    //----------------------------------
    //  isLive
    //----------------------------------
    
    /**
     *  @private
     */
    private var _isLive:Boolean;
    
    /**
     * <p>True if stream is live, read only.  isLive only makes sense when
     * streaming from FVSS or FCS, value is ignored when doing http
     * download.</p>
     */
    public function get isLive():Boolean 
    {
        return _isLive;
    }

    //----------------------------------
    //  state
    //----------------------------------
    
    /**
     *  @private
     */
    private var _state:String;
    
    /**
     * Get state.  Read only.  Set with <code>load</code>,
     * <code>play()</code>, <code>stop()</code>,
     * <code>pause()</code> and <code>seek()</code>.
     */
    public function get state():String 
    {
        return _state;
    }

    //----------------------------------
    //  stateResponsive
    //----------------------------------
    
    /**
     *  Read only. Gets whether state is responsive.  If state is
     *  unresponsive, calls to APIs <code>play()</code>,
     *  <code>load()</code>, <code>stop()</code>,
     *  <code>pause()</code> and <code>seek()</code> will queue the
     *  requests for later, when the state changes to a responsive
     *  one.
     *
     *  @see #connected
     *  @see #MAX_RESPONSIVE_STATE
     *  @see #DISCONNECTED
     *  @see #STOPPED
     *  @see #PLAYING
     *  @see #PAUSED
     *  @see #LOADING
     *  @see #RESIZING
     *  @see #CONNECTION_ERROR
     *  @see #REWINDING
     */
    public function get stateResponsive():Boolean 
    {
        switch (_state) 
        {
        case DISCONNECTED:
        case STOPPED:
        case PLAYING:
        case PAUSED:
        case BUFFERING:
            return true;
        default:
            return false;
        }
    }

    //----------------------------------
    //  bytesLoaded
    //----------------------------------
    
    /**
     *  <p>property bytesLoaded, read only.  Returns -1 when there
     *  is no stream, when the stream is FCS or if the information
     *  is not yet available.  Return value only useful for HTTP
     *  download.</p>
     *
     *  @tiptext Number of bytes already loaded
     *  @helpid 3455
     */
    public function get bytesLoaded():int
    {
        if (ns == null || _ncMgr.isRTMP()) 
            return -1;
        return ns.bytesLoaded;
    }

    //----------------------------------
    //  bytesTotal
    //----------------------------------
    
    /**
     *  <p>property bytesTotal, read only.  Returns -1 when there
     *  is no stream, when the stream is FCS or if the information
     *  is not yet available.  Return value only useful for HTTP
     *  download.</p>
     *
     *  @tiptext Number of bytes to be loaded
     *  @helpid 3456
     */
    public function get bytesTotal():int
    {
        if (ns == null || _ncMgr.isRTMP()) 
            return -1;
        return ns.bytesTotal;
    }

    //----------------------------------
    //  totalTime
    //----------------------------------
    
    /**
     *  <p>property totalTime.  read only.  -1 means that property
     *  was not pass into <code>play()</code> or
     * <code>load()</code> and was unable to detect automatically,
     *  or have not yet.
     *
     *  @return The total running time of the FLV in seconds
     *  @tiptext The total length of the FLV in seconds
     *  @helpid 3467
     */
    public function get totalTime():Number
    {
        return streamLength;
    }

    //----------------------------------
    //  bufferTime
    //----------------------------------
    
    /**
     *  @private
     */
    private var _bufferTime:Number;
    
    /**
     * <p>Sets number of seconds to buffer in memory before playing
     * back stream.  For slow connections streaming over rtmp, it is
     * important to increase this from the default.  Default is
     * 0.1</p>
     */
    public function get bufferTime():Number
    {
        return _bufferTime;
    }
        
    /**
     *  @private
     */
    public function set bufferTime(aTime:Number):void
    {
        _bufferTime = aTime;
        if (ns != null)
            ns.bufferTime = _bufferTime;
    }

    //----------------------------------
    //  idleTimeout
    //----------------------------------
    
    /**
     * <p>Property idleTimeout, which is amount of time in
     * milliseconds before connection is idle (playing is paused
     * or stopped) before connection to the FCS server is
     * terminated.  Has no effect to HTTP download of FLV.</p>
     *
     * <p>If set when stream already idle, restarts idle timeout with
     * new value.</p>
     */
    public function get idleTimeout():uint 
    {
        return idleTimeoutTimer.delay;
    }
        
    /**
     *  @private
     */
    public function set idleTimeout(aTime:uint):void 
    {
        idleTimeoutTimer.delay = aTime;
    }

    //----------------------------------
    //  playheadUpdateInterval
    //----------------------------------
    
    /**
     * <p>Property playheadUpdateInterval, which is amount of time
     * in milliseconds between each "playheadUpdate" event.</p>
     *
     * <p>If set when stream is playing, will restart timer.</p>
     */
    public function get playheadUpdateInterval():uint 
    {
        return updateTimeTimer.delay;
    }
        
    /**
     *  @private
     */
    public function set playheadUpdateInterval(aTime:uint):void 
    {
        updateTimeTimer.delay = aTime;
    }

    //----------------------------------
    //  progressInterval
    //----------------------------------
    
    /**
     * <p>Property progressInterval, which is amount of time
     * in milliseconds between each "progress" event.</p>
     *
     * <p>If set when stream is playing, will restart timer.</p>
     */
    public function get progressInterval():uint 
    {
        return updateProgressTimer.delay;
    }
        
    /**
     *  @private
     */
    public function set progressInterval(aTime:uint):void 
    {
        updateProgressTimer.delay = aTime;
    }

    //----------------------------------
    //  ncMgr
    //----------------------------------
    
    /**
     *  @private
     */
    private var _ncMgr:INCManager;
    
    /**
     * <p>Access to instance of the class implementing
     * <code>INCManager</code>.  Read only.</p>
     *
     * <p>One use case for this is that a custom
     * <code>INCManager</code> implementation may require custom
     * initialization.</p>
     */
    public function get ncMgr():INCManager 
    {
        if (_ncMgr == null)
            createINCManager();
        return _ncMgr;
    }

    //----------------------------------
    //  metadata
    //----------------------------------
    
    /**
     *  @private
     */
    private var _metadata:Object;
    
    /**
     *  <p>Read only.  Object received by call to onMetaData callback.
     *  null if onMetaData callback has not been called since the last
     *  load or play call.  Always null with FLVs with no onMetaData
     *  packet.</p>
     *
     *  @see #load()
     *  @see #play()
     */
    public function get metadata():Object 
    { 
        return _metadata; 
    }    

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------


    /**
     *  <p>set width and height simultaneously.  Since setting either
     *  one can trigger an autoresize, this can be better than invoking
     *  set width and set height individually.</p>
     *
     *  <p>If autoSize is true then this has no effect, since the player
     *  sets its own dimensions.  If maintainAspectRatio is true and
     *  autoSize is false, then changing width or height will trigger
     *  an autoresize.</p>
     *
     *  @param width
     *  @param height
     *  @see width
     *  @see height
     */
    public function setSize(w:Number, h:Number):void
    {
        if (w == width && h == height)
            return;
        super.width = w;
        super.height = h;
    }

    /**
     *  <p>set scaleX and scaleY simultaneously.  Since setting either
     *  one can trigger an autoresize, this can be better than invoking
     *  set width and set height individually.</p>
     *
     *  <p>If autoSize is true then this has no effect, since the player
     *  sets its own dimensions.  If maintainAspectRatio is true and
     *  autoSize is false, then changing scaleX or scaleY will trigger an
     *  autoresize.</p>
     *
     *  @param scaleX
     *  @param scaleY
     *  @see scaleX
     *  @see scaleY
     */
    public function setScale(xs:Number, ys:Number):void 
    {
        if (xs == super.scaleX && ys == super.scaleY)
            return; 
        super.scaleX = xs; 
        super.scaleY = ys;
    }

    /**
     *  <p>Causes the video to play.  Can be called while the video is
     *  paused, stopped, or while the video is already playing.  Call this
     *  method with no arguments to play an already loaded video or pass
     *  in a url to load a new stream.</p>
     *
     *  <p>If player is in an unresponsive state, queues the request.</p>
     *
     *  <p>Throws an exception if called with no args and no stream
     *  is connected.  Use "stateChange" event and
     *  <code>connected</code> property to determine when it is
     *  safe to call this method.</p>
     *
     *  @param url Pass in a url string if you want to load and play a
     *  new FLV.  If you have already loaded an FLV and want to continue
     *  playing it, pass in <code>null</code>.
     *  @param isLive Pass in true if streaming a live feed from FCS.
     *  Defaults to false.
     *  @param totalTime Pass in length of FLV.  Pass in -1 
     *  to automatically detect length from metadata, server
     *  or xml.  If <code>INCManager.streamLength</code> is not -1 when
     *  <code>ncConnected</code> is called, then
     *  that value will trump this one in any case.  Default is -1.
     * 
     *  @see #connected
     *  @see #stateResponsive
     *  @see #load()
     */
    public function play(url:String = null, isLive:Boolean = false, totalTime:Number = -1):void 
    {
        // if new url passed, ask the INCManager to reconnect for us
        if (url != null) 
        {
            if (_state == EXEC_QUEUED_CMD)
                _state = cachedState;
            else if (!stateResponsive)
            {
                queueCmd(VideoPlayerQueuedCommand.PLAY, url, isLive, totalTime);
                return;
            } 
            else
                execQueuedCmds();
            autoPlay = true;
            _load(url, isLive, totalTime);
            // playing will start automatically once stream is setup, so return.
            return;
        }

        if (!isXnOK()) 
        {
            if (_state == CONNECTION_ERROR ||
                 _ncMgr == null || 
                 _ncMgr.netConnection == null)
                throw new VideoError(VideoError.NO_CONNECTION);
            else 
            {
                flushQueuedCmds();
                queueCmd(VideoPlayerQueuedCommand.PLAY);
                setState(LOADING);
                cachedState = LOADING;
                _ncMgr.reconnect();
                // playing will start automatically once stream is setup, so return.
                return;
            }
        } 
        else if (_state == EXEC_QUEUED_CMD)
            _state = cachedState;
        else if (!stateResponsive) 
        {
            queueCmd(VideoPlayerQueuedCommand.PLAY, null, isLive);
            return;
        } 
        else
            execQueuedCmds();
        

        // recreate stream if necessary (this will never happen with
        // http download, just rtmp)
        if (ns == null) 
        {
            createStream();
            attachNetStream(ns);            
            //this.attachAudio(ns); revisit - Is this needed?
        }

        switch (_state) 
        {
            case BUFFERING:
                if (_ncMgr.isRTMP()) 
                {
                    _play(0);
                    if (atEnd) 
                    {
                        atEnd = false;
                        currentPos = 0;
                        setState(REWINDING);
                    } 
                    else if (currentPos > 0) 
                    {
                        _seek(currentPos);
                        currentPos = 0;
                    }
                }
                // no break
            case PLAYING:
                // already playing
                return;
            case STOPPED:
                if (_ncMgr.isRTMP())
                {
                    if (isLive) 
                    {
                        _play(-1);
                        setState(BUFFERING);
                    } 
                    else 
                    {
                        _play(0);
                        if (atEnd) 
                        {
                            atEnd = false;
                            currentPos = 0;
                            _state = BUFFERING;
                            setState(REWINDING);
                        } 
                        else if (currentPos > 0) 
                        {
                            _seek(currentPos);
                            currentPos = 0;
                            setState(BUFFERING);
                        } 
                        else
                            setState(BUFFERING);
                    }
                }
                else
                {
                    _pause(false);
                    if (atEnd) 
                    {
                        atEnd = false;
                        _seek(0);
                        _state = BUFFERING;
                        setState(REWINDING);
                    } 
                    else 
                    {
                        if (bufferState == BUFFER_EMPTY)
                            setState(BUFFERING);
                        else
                            setState(PLAYING);
                    }
                } 
                break;
            case PAUSED:
                _pause(false);
                if (!_ncMgr.isRTMP())
                    if (bufferState == BUFFER_EMPTY)
                        setState(BUFFERING);
                    else
                        setState(PLAYING);
                else
                    setState(BUFFERING);
                break;
        } // switch
    }

    /**
     *  <p>Similar to play, but causes the FLV to be loaded without
     *  playing.  Autoresizing will occur if appropriate and the first
     *  frame of FLV will be shown (except for maybe not in the live case).
     *  After initial load and autoresize, state will be <code>PAUSED</code>.</p>
     *
     *  <p>Takes same arguments as <code>play()</code>, but unlike that
     *  method it is never acceptable to call <code>load()</code> with
     *  no url.  If you do, an <code>Error</code> will be thrown.</p>
     *
     *  <p>If player is in an unresponsive state, queues the request.</p>
     *
     *  @param url Pass in a url string for the FLV you want to load.
     *  @param isLive Pass in true if streaming a live feed from FCS.
     *  Defaults to false.
     *  @param totalTime Pass in length of FLV.  Pass in -1 to
     *  automatically detect length from metadata, server or xml.
     *  If <code>INCManager.streamLength</code> is not -1 when
     *  <code>ncConnected</code> is called, then that value will
     *  trump this one in any case.  Default is -1.
     *  @see #connected
     *  @see #play()
     */
    public function load(url:String, isLive:Boolean = false, totalTime:Number = -1):void 
    {
        if (url == null)
        {
            var message:String = resourceManager.getString(
                "controls", "nullURL");
            throw new ArgumentError(message);
        }

        if (_state == EXEC_QUEUED_CMD)
            _state = cachedState;
        else if (!stateResponsive) 
        {
            queueCmd(VideoPlayerQueuedCommand.LOAD, url, isLive, totalTime);
            return;
        } 
        else
            execQueuedCmds();
        autoPlay = false;
        _load(url, isLive, totalTime);
    }

    /**
     *  <p>Pauses video playback.  If video is paused or stopped, has
     *  no effect.  To start playback again, call <code>play()</code>.
     *  Takes no parameters</p>
     *
     *  <p>If player is in an unresponsive state, queues the request.</p>
     *
     *  <p>Throws an exception if called when no stream is
     *   connected.  Use "stateChange" event and
     *  <code>connected</code> property to determine when it is
     *  safe to call this method.</p>
     *
     *  <p>If state is already stopped, pause is does nothing and state
     *  remains stopped.</p>
     *
     *  @see #connected
     *  @see #stateResponsive
     *  @see #play()
     */
    public function pause():void 
    {
        if (!isXnOK()) 
            if (_state == CONNECTION_ERROR ||
                 _ncMgr == null ||
                 _ncMgr.netConnection == null)
                throw new VideoError(VideoError.NO_CONNECTION);
            else
                return;
        else if (_state == EXEC_QUEUED_CMD)
            _state = cachedState;
        else if (!stateResponsive) 
        {
            queueCmd(VideoPlayerQueuedCommand.PAUSE);
            return;
        } 
        else
            execQueuedCmds();
        if (_state == PAUSED || _state == STOPPED || ns == null) 
            return;
        _pause(true);
        setState(PAUSED);
    }

    /**
     *  <p>Stops video playback.  If <code>autoRewind</code> is set to
     *  <code>true</code>, rewinds to first frame.  If video is already
     *  stopped, has no effect.  To start playback again, call
     *  <code>play()</code>.  Takes no parameters</p>
     *
     *  <p>If player is in an unresponsive state, queues the request.</p>
     *
     *  <p>Throws an exception if called when no stream is
     *  connected.  Use "stateChange" event and
     *  <code>connected</code> property to determine when it is
     *  safe to call this method.</p>
     *
     *  @see #connected
     *  @see #stateResponsive
     *  @see #autoRewind
     *  @see #play()
     */
    public function stop():void
    {
        if (!isXnOK()) 
            if (_state == CONNECTION_ERROR ||
                 _ncMgr == null ||
                 _ncMgr.netConnection == null) 
                throw new VideoError(VideoError.NO_CONNECTION);
            else
                return;
        else if (_state == EXEC_QUEUED_CMD)
            _state = cachedState;
        else if (!stateResponsive) 
        {
            queueCmd(VideoPlayerQueuedCommand.STOP);
            return;
        } 
        else
            execQueuedCmds();
        if (_state == STOPPED || ns == null) 
            return;

        if (_ncMgr.isRTMP())
        {
            if (_autoRewind && !_isLive) 
            {
                currentPos = 0;
                _play(0, 0);
                _state = STOPPED;
                setState(REWINDING);
            } 
            else 
            {
                closeNS();
                setState(STOPPED);
            }
        }
        else
        {
            _pause(true);
            if (_autoRewind) 
            {
                _seek(0);
                _state = STOPPED;
                setState(REWINDING);
            }
            else
                setState(STOPPED);
        } 
    }

    /**
     *  <p>Seeks to given second in video.  If video is playing,
     *  continues playing from that point.  If video is paused, seek to
     *  that point and remain paused.  If video is stopped, seek to
     *  that point and enters paused state.  Has no effect with live
     *  streams.</p>
     *
     *  <p>If time is less than 0 or NaN, throws exeption.  If time
     *  is past the end of the stream, or past the amount of file
     *  downloaded so far, then will attempt seek and when fails
     *  will recover.</p>
     *
     *  <p>If player is in an unresponsive state, queues the request.</p>
     *
     *  <p>Throws an exception if called when no stream is
     *  connected.  Use "stateChange" event and
     *  <code>connected</code> property to determine when it is
     *  safe to call this method.</p>
     *
     *  @param time seconds
     *  @throws VideoError if time is < 0
     *  @see #connected
     *  @see #stateResponsive
     */
    public function seek(time:Number):void
    {
        // we do not allow more seeks until we are out of an invalid seek time state        
        if (invalidSeekTime)
            return;
        if (isNaN(time) || time < 0) 
            throw new VideoError(VideoError.INVALID_SEEK);
        if (!isXnOK()) 
            if (_state == CONNECTION_ERROR ||
                 _ncMgr == null ||
                 _ncMgr.netConnection == null) 
                throw new VideoError(VideoError.NO_CONNECTION);
            else 
            {
                flushQueuedCmds();
                queueCmd(VideoPlayerQueuedCommand.SEEK, null, false, time);
                setState(LOADING);
                cachedState = LOADING;
                _ncMgr.reconnect();
                // playing will start automatically once stream is setup, so return.
                return;
            }
        else if (_state == EXEC_QUEUED_CMD)
            _state = cachedState;
        else if (!stateResponsive) 
        {
            queueCmd(VideoPlayerQueuedCommand.SEEK, null, false, time);
            return;
        } 
        else 
            execQueuedCmds();
        // recreate stream if necessary (this will never happen with
        // http download, just rtmp)
        if (ns == null) 
        {
            createStream();
            attachNetStream(ns);
            //this.attachAudio(ns); revisit - Is this needed?
        }
        if (atEnd && time <= playheadTime)
            atEnd = false;        
        switch (_state) 
        {
            case PLAYING:
                _state = BUFFERING;
                // no break;
            case BUFFERING:
            case PAUSED:
                _seek(time);
                setState(SEEKING);
                break;
            case STOPPED:
                if (_ncMgr.isRTMP()) 
                {
                    _play(0);
                    _pause(true);
                }
                _seek(time);
                _state = PAUSED;
                setState(SEEKING);
                break;
        }        
    }

    /**
     *  <p>Forces close of video stream and FCS connection.  Triggers
     *  "close" event.  Typically calling this directly is not necessary
     *  because the idle timeout functionality will take care of this.</p>
     *
     *  @see idleTimeout
     */
    public function close():void 
    {
        closeNS();
        // never makes sense to close an http NetConnection, it doesn't really maintain
        // any kind of network connection!
        if (_ncMgr != null && _ncMgr.isRTMP())
            _ncMgr.close();
        setState(DISCONNECTED);
        
        var videoEvent:VideoEvent = new VideoEvent(VideoEvent.CLOSE);
        videoEvent.state = _state;
        videoEvent.playheadTime = playheadTime;
        dispatchEvent(videoEvent);
    }
    
    //----------------------------------
    //  public callbacks, not really APIs
    //----------------------------------

    /**
     *  @private
     *  <p>Called by <code>Timer updateTimeTimer</code> to send
     *  "playheadUpdate" events.  Events only sent when playhead is
     *  moving, sent every .25 seconds (see
     *  <code>_updateTimeInterval</code>).
     */
    public function doUpdateTime(event:Event):void 
    {
        var theTime:Number = playheadTime;

        // stop timer if we are stopped or paused
        switch (_state) 
        {
            case STOPPED:
            case PAUSED:
            case DISCONNECTED:
            case CONNECTION_ERROR:
                if (event != null) 
                    updateTimeTimer.reset();
                break;
        }
        if (lastUpdateTime != theTime) 
        {
            var videoEvent:VideoEvent =
                new VideoEvent(VideoEvent.PLAYHEAD_UPDATE);
            videoEvent.state = _state;
            videoEvent.playheadTime = theTime;
            dispatchEvent(videoEvent);
            
            lastUpdateTime = theTime;
        }
    }

    /**
     *  @private
     *  <p>Called by <code>Timer _updateProgressTimer</code> to send
     *  "progress" events.  Event dispatch starts when
     *  <code>_load</code> is called, ends when all bytes downloaded or
     *  a network error of some kind occurs, dispatched every .25
     *  seconds.
     */
    public function doUpdateProgress(event:Event):void 
    {
        if (ns == null) 
            return;

        if (ns.bytesTotal >= 0 && ns.bytesLoaded != _priorBytesLoaded)
            dispatchEvent(new ProgressEvent(ProgressEvent.PROGRESS, false, false,
                                 ns.bytesLoaded, ns.bytesTotal));
                              
        if (_state == DISCONNECTED || _state == CONNECTION_ERROR ||
             ns.bytesLoaded == ns.bytesTotal)
           updateProgressTimer.reset();
           
        _priorBytesLoaded = ns.bytesLoaded;
    }

    /**
     *  @private
     *  <p><code>NetStream.onStatus</code> callback for rtmp.  Handles
     *  automatic resizing, autorewind and buffering messaging.</p>
     */
    public function rtmpOnStatus(event:NetStatusEvent):void
    {
        if (_state == CONNECTION_ERROR)
            // always do nothing
            return;

        switch (event.info.code) 
        {
        case "NetStream.Play.Stop":
            if (startingPlay) 
                return;
            switch (_state) 
            {
                case RESIZING:
                    break;
                case LOADING:
                case STOPPED:
                case PAUSED:
                    // yes we are stopped, we already know this
                    break;
                default:
                    sawPlayStop = true;
                    break;
            } // switch (_state)
            break;
        case "NetStream.Buffer.Empty":
            switch (bufferState) 
            {
            case BUFFER_FULL:
                if (sawPlayStop)
                    rtmpDoStopAtEnd(null);
                else if (_state == PLAYING)
                    setState(BUFFERING);
                break;
            }
            bufferState = BUFFER_EMPTY;
            sawPlayStop = false;
            break;
        case "NetStream.Buffer.Flush":
            if (sawSeekNotify && _state == SEEKING) 
            {
                bufferState = BUFFER_EMPTY;
                sawPlayStop = false;
                setStateFromCachedState();
                doUpdateTime(null);
            }
            if (sawPlayStop &&
                (bufferState == BUFFER_EMPTY || 
                (_bufferTime <= 0.1 && ns.bufferLength <= 0.1)))
            {
                // if we did a seek toward the end of the file so that
                // there is less file left to show than we have
                // buffer, than we will get a NetStream.Play.Stop when
                // the buffer loads rest of the file, but never get
                // a NetStream.Buffer.Full, since it won't fill, so
                // we check if we are done on a timer
                cachedPlayheadTime = playheadTime;
                rtmpDoStopAtEndTimer.start(); 
            } 
            switch (bufferState) 
            {
            case BUFFER_EMPTY:
                if ((_state == LOADING && cachedState == PLAYING) 
                        || _state == BUFFERING)
                    setState(PLAYING);
                else if (cachedState == BUFFERING)
                    cachedState = PLAYING;
                bufferState = BUFFER_FLUSH;
                break;
            default:
                if (_state == BUFFERING)
                    setStateFromCachedState();
                break;
            } // switch (bufferState)
            break;
        case "NetStream.Buffer.Full":
            if (sawSeekNotify && _state == SEEKING) 
            {
                bufferState = BUFFER_EMPTY;
                sawPlayStop = false;
                setStateFromCachedState();
                doUpdateTime(null);
            }        
            switch (bufferState) 
            {
                case BUFFER_EMPTY:
                    bufferState = BUFFER_FULL;
                    if ((_state == LOADING && cachedState == PLAYING) || _state == BUFFERING)
                        setState(PLAYING);
                    else if (cachedState == BUFFERING)
                        cachedState = PLAYING;  
                    if (rtmpDoStopAtEndTimer.running) 
                    {
                        sawPlayStop = true;
                        rtmpDoStopAtEndTimer.reset();
                    }
                    break;
                case BUFFER_FLUSH:
                    bufferState = BUFFER_FULL;
                    if (rtmpDoStopAtEndTimer.running) 
                    {
                        sawPlayStop = true;
                        rtmpDoStopAtEndTimer.reset();
                    }
                    break;
            } // switch (bufferState)
            if (_state == BUFFERING)
                setStateFromCachedState();        
            break;
        case "NetStream.Pause.Notify":
            // do nothing
            break;
        case "NetStream.Unpause.Notify":
            if (_state == PAUSED) 
            {
                _state = PLAYING;
                setState(BUFFERING);
            } 
            else
                cachedState = PLAYING;
            break;          
        case "NetStream.Play.Start":
            rtmpDoStopAtEndTimer.reset(); 
            bufferState = BUFFER_EMPTY;
            sawPlayStop = false;
            if (startingPlay) 
            {
                startingPlay = false;
                cachedPlayheadTime = playheadTime;
            }
            else if (_state == PLAYING)
                setState(BUFFERING);
            break;
        case "NetStream.Play.Reset":
            rtmpDoStopAtEndTimer.reset();
            if (_state == REWINDING) 
            {
                rtmpDoSeekTimer.reset();
                if (playheadTime == 0 || playheadTime < cachedPlayheadTime)
                    setStateFromCachedState();
                else 
                {
                    cachedPlayheadTime = playheadTime;
                    rtmpDoSeekTimer.start();
                }
            }
            break;            
        case "NetStream.Seek.Notify":
            if (playheadTime != cachedPlayheadTime) 
            {
                setStateFromCachedState();
                doUpdateTime(null);
            } 
            else 
            {
                sawSeekNotify = true;
                if (!rtmpDoSeekTimer.running)
                    rtmpDoSeekTimer.start();
            }
            break;                
        case "Netstream.Play.UnpublishNotify": 
            break;  
        case "Netstream.Play.PublishNotify":
            break;  
        case "NetStream.Play.StreamNotFound":
            if (!_ncMgr.connectAgain())
                setState(CONNECTION_ERROR);
            break;
        case "NetStream.Play.Failed":
        case "NetStream.Failed":
            setState(CONNECTION_ERROR);
            break;
        } // switch (event.info.code)
    }

    /**
     *  @private
     *  <p><code>NetStream.onStatus</code> callback for http.  Handles
     *  autorewind.</p>
     */
    public function httpOnStatus(event:NetStatusEvent):void
    {
        switch (event.info.code)
        {
        case "NetStream.Play.Stop":
            delayedBufferingTimer.reset();
            if (invalidSeekTime) 
            {
                recoverInvalidSeek();
            } 
            else    
                switch (_state) 
                {
                    case PLAYING:
                    case BUFFERING:
                    case SEEKING:
                        httpDoStopAtEnd();
                        break;
                }
            break;
        case "NetStream.Seek.InvalidTime":
            if (invalidSeekRecovery) 
            {
                invalidSeekTime = false;
                invalidSeekRecovery = false;
                setState(cachedState);
                seek(0);
            } 
            else 
            {
                recoverInvalidSeek();
            }
            break;
        case "NetStream.Buffer.Empty":
            bufferState = BUFFER_EMPTY;
            if (_state == PLAYING)
                delayedBufferingTimer.start(); 
            break;
        case "NetStream.Buffer.Full":
        case "NetStream.Buffer.Flush":
            delayedBufferingTimer.reset(); 
            bufferState = BUFFER_FULL;
            if ((_state == LOADING && cachedState == PLAYING) || _state == BUFFERING)
                setState(PLAYING);
            else if (cachedState == BUFFERING)
                cachedState = PLAYING;  
            break;
        case "NetStream.Seek.Notify":
            invalidSeekRecovery = false;
            switch (_state) 
            {
                case SEEKING:
                case REWINDING:
                    if (!httpDoSeekTimer.running)
                    {
                        httpDoSeekCount = 0;
                        httpDoSeekTimer.start();
                    }
                break;
            } // switch (_state)                
            break;    
        case "NetStream.Play.StreamNotFound":
            setState(CONNECTION_ERROR);
            break;
        } // switch (event.info.code)
    }

    /**
     *  @private 
     *  <p>Called by INCManager after when connection complete or
     *  failed after call to <code>INCManager.connectToURL</code>.
     *  If connection failed, set <code>INCManager.netConnection = null</code>
     *  before calling.</p>
     *
     *  @see #ncReconnected()
     *  @see INCManager#connectToURL
     *  @see NCManager#connectToURL
     */
    public function ncConnected():void    
    {
        if (_ncMgr == null ||
             _ncMgr.netConnection == null)
            setState(CONNECTION_ERROR);
        else 
        {
            createStream();
            setUpStream();
        }
    }

    /**
     *  @private     
     *  <p>Called by INCManager after when reconnection complete or
     *  failed after call to <code>INCManager.reconnect</code>.  If
     *  connection failed, set <code>INCManager.netConnection = null</code>
     *  before calling.</p>
     *
     *  @see #ncConnected()
     *  @see INCManager#reconnect
     *  @see NCManager#reconnect
     */
    public function ncReconnected():void
    {
        if (_ncMgr == null ||
             _ncMgr.netConnection == null)
            setState(CONNECTION_ERROR);
        else 
        {
            ns = null;
            _state = STOPPED;
            execQueuedCmds();
        }
    }

    /**
     *  handles NetStream.onMetaData callback
     *
     *  @private
     */
    public function onMetaData(info:Object):void 
    {
        if (_metadata != null)
            return;
        
        _metadata = info;
        
        if (isNaN(streamLength) || streamLength <= 0)
            streamLength = info.duration;

        if (isNaN(internalVideoWidth) || internalVideoWidth <= 0)
            internalVideoWidth = info.width;

        if (isNaN(internalVideoHeight) || internalVideoHeight <= 0)
            internalVideoHeight = info.height;

        var metadataEvent:MetadataEvent =
            new MetadataEvent(MetadataEvent.METADATA_RECEIVED);
        metadataEvent.info = info;
        dispatchEvent(metadataEvent);
    }

    /**
     *  handles NetStream.onCuePoint callback
     *
     *  @private
     */
    public function onCuePoint(info:Object):void 
    {
        var metadataEvent:MetadataEvent =
            new MetadataEvent(MetadataEvent.CUE_POINT);
        metadataEvent.info = info;
        dispatchEvent(metadataEvent);
    }
    
    //----------------------------------
    //  Private Methods
    //----------------------------------
        
    /*
     *  @private
     *  does loading work for play and load
     */
    private function _load(url:String, isLive:Boolean, totalTime:Number):void 
    {       
        if (prevVideoWidth == -1) 
            (videoWidth >= 0) ? prevVideoWidth = videoWidth : prevVideoWidth = 0;
        if (prevVideoHeight == -1) 
            (videoHeight >= 0) ? prevVideoHeight = videoHeight : prevVideoHeight = 0;

        // reset state
        cachedPlayheadTime = 0;
        bufferState = BUFFER_EMPTY;
        sawPlayStop = false;
        _metadata = null;
        startingPlay = false;
        invalidSeekRecovery = false;
        invalidSeekTime = false;
        _isLive = isLive;
        _url = url;
        currentPos = 0;
        streamLength = totalTime;
        atEnd = false;
        internalVideoWidth = -1;
        internalVideoHeight = -1;
        readyDispatched = false;
        lastUpdateTime = -1;
        sawSeekNotify = false;
        
        // must stop ALL intervals here
        updateTimeTimer.reset();
        updateProgressTimer.reset();
        idleTimeoutTimer.reset();
        autoResizeTimer.reset();
        rtmpDoStopAtEndTimer.reset();
        rtmpDoSeekTimer.reset();
        httpDoSeekTimer.reset();
        finishAutoResizeTimer.reset();
        delayedBufferingTimer.reset();

        // close netstream
        closeNS(false);

        // if returns false, wait for a "connected" message and
        // then do these things
        if (_ncMgr == null)
            createINCManager();
        
        var instantConnect:Boolean = _ncMgr.connectToURL(_url);
        setState(LOADING);
        cachedState = LOADING;
        if (instantConnect) 
        {        
            createStream();
            setUpStream();
        }
        if (!_ncMgr.isRTMP())
            updateProgressTimer.start(); 
    }

    /**
     *  @private
     *  sets state, dispatches event, execs queued commands.  Always try to call
     *  this AFTER you do your work, because the state might change again after
     *  you call this if you set it to a responsive state becasue of the call
     *  to exec queued commands.  If you set this to a responsive state and
     *  then do more state based logic, check _state to make sure it did not
     *  change out from under you.
     */
    private function setState(s:String):void 
    {
        if (s == _state) 
            return;
        cachedState = _state;
        cachedPlayheadTime = playheadTime;
        _state = s;
        var newState:String = _state;

        var videoEvent:VideoEvent = new VideoEvent(VideoEvent.STATE_CHANGE);
        videoEvent.state = newState;
        videoEvent.playheadTime = playheadTime;
        dispatchEvent(videoEvent);

        if (!readyDispatched) 
        {
            switch (newState) 
            {
                case STOPPED:
                case PLAYING:
                case PAUSED:
                case BUFFERING:    
                {    
                    readyDispatched = true;
 
                    videoEvent = new VideoEvent(VideoEvent.READY);
                    videoEvent.state = newState;
                    videoEvent.playheadTime = playheadTime;
                    dispatchEvent(videoEvent);
                }
            }
        }

        switch (cachedState) 
        {
            case REWINDING:
            {
                videoEvent = new VideoEvent(VideoEvent.REWIND);
                videoEvent.state = newState;
                videoEvent.playheadTime = playheadTime;
                dispatchEvent(videoEvent);
                
                if (_ncMgr.isRTMP() && newState == STOPPED)
                    closeNS();
                break;
            }
        }

        switch (newState) 
        {
            case STOPPED:
            case PAUSED:
            {
                if (_ncMgr.isRTMP() && !idleTimeoutTimer.running)
                    idleTimeoutTimer.start(); 
                break;
            }

            case SEEKING:
            case REWINDING:
            {
                bufferState = BUFFER_EMPTY;
                sawPlayStop = false;
                // no break
            }

            case PLAYING:
            case BUFFERING:
            {
                if (!updateTimeTimer.running) 
                    updateTimeTimer.start(); 
                // no break
            }

            case LOADING:
            case RESIZING:
            {
                idleTimeoutTimer.reset(); 
                break;
            }
        }

        execQueuedCmds();
    }

    /**
     *  @private
     *  Sets state to _cachedState if the _cachedState is PLAYING,
     *  PAUSED or BUFFERING, otherwise sets state to STOPPED.
     */
    private function setStateFromCachedState():void
    {
        switch (cachedState) 
        {
            case PLAYING:
            case PAUSED:
            {
                setState(cachedState);
                break;
            }

            case BUFFERING:
            {
                if (bufferState == BUFFER_EMPTY)
                    setState(BUFFERING);
                else
                    setState(cachedState);
                break;
            }

            default:
            {
                setState(STOPPED);
                break;
            }
        }
    }
    
    /**
     * @private
     * Helper used when an invalid seek occurs. We reset
     * our player state and seek back to a valid playhead 
     * location.
     */
    private function recoverInvalidSeek():void
    {
        setStateFromCachedState();
        invalidSeekTime = false;
        invalidSeekRecovery = true;
        seek(playheadTime);    
    }
    
    /**
     *  @private
     *  creates our implementatino of the <code>INCManager</code>.
     *  We put this off until we need to do it to give time for the
     *  user to customize the <code>DEFAULT_INCMANAGER</code>
     *  static variable.
     */
    private function createINCManager():void 
    {
        var ncMgrClass:Class = (ncMgrClassName == null) ? DEFAULT_INCMANAGER : ncMgrClassName;
        _ncMgr = new ncMgrClass();
        _ncMgr.videoPlayer = this;
    }

    /**
     *  @private
     *  <p>ONLY CALL THIS WITH RTMP STREAMING</p>
     *
     *  <p>Has the logic for what to do when we decide we have come to
     *  a stop by coming to the end of an rtmp stream.  There are a few
     *  different ways we decide this has happened, and we sometimes
     *  even set an interval that calls this function repeatedly to
     *  check if the time is still changing, which is why it has its
     *  own special function.</p>
     */
    private function rtmpDoStopAtEnd(event:TimerEvent):void 
    {
        // check if we really want to stop if this was triggered on an
        // timer.  If we are running this on an timer (see
        // rtmpOnStatus) we do a stop when the playhead hasn't moved
        // since last time we checked, we check every .25 seconds.
        if (event != null) 
        {
            switch (_state) 
            {
                case DISCONNECTED:
                case CONNECTION_ERROR:
                    rtmpDoStopAtEndTimer.reset(); 
                    return;
            }
            if (cachedPlayheadTime == playheadTime) 
                rtmpDoStopAtEndTimer.reset();
            else 
            {
                cachedPlayheadTime = playheadTime;
                return;
            }
        }
        bufferState = BUFFER_EMPTY;
        sawPlayStop = false;
        atEnd = true;
        
        // all this triggers callbacks, so need to keep checking if
        // _state == STOPPED--if no longer, then we bail
        setState(STOPPED);
        if (_state != STOPPED) 
            return;
        
        doUpdateTime(null);
        if (_state != STOPPED) 
            return;
                
        var videoEvent:VideoEvent = new VideoEvent(VideoEvent.COMPLETE);
        videoEvent.state = _state;
        videoEvent.playheadTime = playheadTime;
        dispatchEvent(videoEvent);
        
        if (_state != STOPPED) 
            return;
        
        if (_autoRewind && !_isLive && playheadTime != 0) 
        {
            atEnd = false;
            currentPos = 0;
            _play(0, 0);
            setState(REWINDING);
        } 
        else
            closeNS();
    }

    /**
     *  @private
     *  <p>ONLY CALL THIS WITH RTMP STREAMING</p>
     *
     *  <p>Wait until time goes back to zero to leave rewinding state.</p>
     */
    private function rtmpDoSeek():void 
    {
        if (_state != REWINDING && _state != SEEKING) 
        {
            rtmpDoSeekTimer.reset();
            sawSeekNotify = false;
        } 
        else if (playheadTime != cachedPlayheadTime) 
        {
            rtmpDoSeekTimer.reset();
            sawSeekNotify = false;
            setStateFromCachedState();
            doUpdateTime(null);
        }
    }

    /**
     *  @private
     *  <p>ONLY CALL THIS WITH HTTP PROGRESSIVE DOWNLOAD</p>
     *
     *  <p>Call this when playing stops by hitting the end.</p>
     */
    private function httpDoStopAtEnd():void 
    {
        atEnd = true;
        if (isNaN(streamLength) || streamLength <= 0)
            streamLength = ns.time;
       
        _pause(true);
        setState(STOPPED);
        if (_state != STOPPED)
            return;
        
        doUpdateTime(null);
        if (_state != STOPPED)
            return;

        var videoEvent:VideoEvent = new VideoEvent(VideoEvent.COMPLETE);
        videoEvent.state = _state;
        videoEvent.playheadTime = playheadTime;
        dispatchEvent(videoEvent);
        
        if (_state != STOPPED)
            return;

        if (_autoRewind) 
        {
            atEnd = false;            
            _pause(true);
            _seek(0);
            setState(REWINDING);
        }
    }

    /**
     *  @private
     *  <p>If we get an onStatus callback indicating a seek is over,
     *  but the playheadTime has not updated yet, then we wait on a
     *  timer before moving forward.</p>
     */
    private function doSeek(event:Event):void 
    {
        var seekState:Boolean = (_state == REWINDING || _state == SEEKING);
        // if seeking or rewinding, then need to wait for playhead time to
        // change or for timeout
        if (seekState && httpDoSeekCount < HTTP_DO_SEEK_MAX_COUNT &&
             (cachedPlayheadTime == playheadTime || invalidSeekTime)) 
        {
            httpDoSeekCount++;
            return;
        }

        // reset
        httpDoSeekCount = 0;
        httpDoSeekTimer.reset(); 
        
        // only do the rest if were seeking or rewinding to start with
        if (!seekState) 
            return;

        if (invalidSeekTime) 
        {
            recoverInvalidSeek();
        } 
        else
        {
            setStateFromCachedState();
            doUpdateTime(null);
        }
    }

    /**
     *  @private
     *  <p>Wrapper for <code>NetStream.close()</code>.  Never call
     *  <code>NetStream.close()</code> directly, always call this
     *  method because it does some other housekeeping.</p>
     */
    private function closeNS(updateCurrentPos:Boolean = true):void 
    {
        if (ns != null)
        {
            if (updateCurrentPos) 
            {
                updateTimeTimer.reset(); 
                doUpdateTime(null);
                currentPos = ns.time;
            }
            ns.removeEventListener(NetStatusEvent.NET_STATUS, httpOnStatus);
            ns.removeEventListener(NetStatusEvent.NET_STATUS, rtmpOnStatus);
            ns.close();
            ns = null;
        }
    }

    /**
     *  @private
     *  <p>We do a brief timer before entering BUFFERING state to avoid
     *  quick switches from BUFFERING to PLAYING and back.</p>
     */
    private function doDelayedBuffering(event:Event):void 
    {
        switch (_state) 
        {
            case LOADING:
            case RESIZING:
                // if loading or resizing, still at beginning so keep whirring, might go into buffering state
                break;
            case PLAYING:
                // still in that playing state, let's go to buffering
                delayedBufferingTimer.reset(); 
                setState(BUFFERING);
                break;
            default:
                // any other state, bail and kill timer
                delayedBufferingTimer.reset(); 
                break;
        }
    }

    /**
     *  @private
     *  Wrapper for <code>NetStream.pause()</code> and <code>NetStream.resume()</code>.
     *  Never call these NetStream methods directly; always call this
     *  method because it does some other housekeeping.
     */
    private function _pause(doPause:Boolean):void 
    {
        rtmpDoStopAtEndTimer.reset();       
        if (doPause)
            ns.pause();
        else
            ns.resume();
    }

    /**
     *  @private
     *  Wrapper for <code>NetStream.play()</code>.  Never call
     *  <code>NetStream.play()</code> directly, always call this
     *  method because it does some other housekeeping.
     */
    private function _play(... rest):void 
    {
        rtmpDoStopAtEndTimer.reset();
        startingPlay = true;        
        switch (rest.length) 
        {
            case 0:
                ns.play(_ncMgr.streamName);
                break;
            case 1:
                ns.play(_ncMgr.streamName, rest[0]);
                break;
            case 2:
                ns.play(_ncMgr.streamName, rest[0], rest[1]);
                break;
            default:
            {
                var message:String = resourceManager.getString(
                    "controls", "badArgs");
                throw new ArgumentError(message);
            }
        }
    }

    /**
     *  @private
     *  Wrapper for <code>NetStream.seek()</code>.  Never call
     *  <code>NetStream.seek()</code> directly, always call
     *  this method because it does some other housekeeping.
     */
    private function _seek(time:Number):void 
    {
        rtmpDoStopAtEndTimer.reset();        
        // round the number to three decimal places...
        var seekTime:Number = Math.round(time * 1000) / 1000;
        
        if (_metadata && _metadata.audiodelay && time + _metadata.audiodelay < streamLength) // Revisit
            time += _metadata.audiodelay;
        ns.seek(time);
        invalidSeekTime = false;
        bufferState = BUFFER_EMPTY;
        sawPlayStop = false;
        sawSeekNotify = false;
    }

    /**
     *  @private
     *  Gets whether connected to a stream.  If not, then calls to APIs
     *  <code>play() with no args</code>, <code>stop()</code>,
     *  <code>pause()</code> and <code>seek()</code> will throw
     *  exceptions.
     *
     *  @see #stateResponsive
     */
    private function isXnOK():Boolean 
    {
        if (_state == LOADING) return true;
        if (_state == CONNECTION_ERROR) return false;
        if (_state != DISCONNECTED) 
        {
            if (_ncMgr == null || 
                 _ncMgr.netConnection == null ||
                 !_ncMgr.netConnection.connected)
            {
                setState(DISCONNECTED);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     *  @private
     *  <p>Does the actual work of resetting the width and height.</p>
     *
     *  <p>Called on a timer which is stopped when width and height
     *  of the <code>Video</code> object are not zero.  Finishing the
     *  resize is done in another method which is either called on a
     *  timer set up here for live streams or on a
     *  NetStream.Play.Stop event in <code>rtmpOnStatus</code> after
     *  stream is rewound if it is not a live stream.  Still need to
     *  get a http solution.</p>
     */
    private function doAutoResize(event:Event):void 
    {
        if (event != null) 
        {
            switch (_state) 
            {
                case RESIZING:
                case LOADING:
                    break;
                case DISCONNECTED:
                case CONNECTION_ERROR:
                    // autoresize will happen later automatically
                    autoResizeTimer.reset();
                    return;
                default:
                    if (!stateResponsive)
                        // keep trying until we get into a responsive state
                        return;
            }
            if (videoWidth != prevVideoWidth || videoHeight != prevVideoHeight ||
                 bufferState == BUFFER_FULL || bufferState == BUFFER_FLUSH || 
                 ((ns) ? ns.time > AUTO_RESIZE_PLAYHEAD_TIMEOUT : false)) 
            { // revisit - Was ns.time > AUTO_RESIZE_PLAYHEAD_TIMEOUT
                // if have not received metadata yet, slight delay to avoid race condition in player
                // but there may not be any metadata, so cannot wait forever

                internalVideoWidth = videoWidth;
                internalVideoHeight = videoHeight;
                autoResizeTimer.reset();
            } 
            else
                // keep trying until our size is set
                return;
        }
        // do not need to do autoresize, but DO need to signal readyness
        setState(cachedState);
    }

    /**
     * <p>Makes video visible, turns on sound and starts
     * playing if live or autoplay.</p>
     */
    private function finishAutoResize(event:Event):void 
    {
        finishAutoResizeTimer.reset();
        if (stateResponsive) 
            return;
        super.visible = _visible;
        _soundTransform.volume = _volume;
        ns.soundTransform = _soundTransform;
 
        if (autoPlay) 
            if (_ncMgr.isRTMP())
            {
                if (!_isLive) 
                {
                    currentPos = 0;
                    _play(0);
                }
                if (_state == RESIZING) 
                {
                    setState(LOADING);
                    cachedState = PLAYING;
                }
            }
            else
            {
                _pause(false);
                cachedState = PLAYING;
            } 
        else 
            setState(STOPPED);
    }

    /**
     *  @private
     *  <p>Creates <code>NetStream</code> and does some basic
     *  initialization.</p>
     */
    private function createStream():void 
    {
        ns = new VideoPlayerNetStream(_ncMgr.netConnection, this);
        ns.bufferTime = _bufferTime;
        ns.soundTransform = _soundTransform;
        ns.addEventListener(NetStatusEvent.NET_STATUS,
                            (_ncMgr.isRTMP()) ? rtmpOnStatus : httpOnStatus);
        attachNetStream(ns);
    }

    /**
     *  @private
     *  <p>Does initialization after first connecting to the server
     *  and creating the stream.  Will get the stream duration from
     *  the <code>INCManager</code> if it has it for us.</p>
     *
     *  <p>Starts resize if necessary, otherwise starts playing if
     *  necessary, otherwise loads first frame of video.  In http case,
     *  starts progressive download in any case.</p>
     */
    private function setUpStream():void 
    {
        // INCManager MIGHT have gotten the stream length, width and height for
        // us.  If its length is null, undefined or < 0, then it did not.
        if (!isNaN(_ncMgr.streamLength) && _ncMgr.streamLength >= 0) 
            streamLength = _ncMgr.streamLength;

        if (!isNaN(_ncMgr.streamWidth) && _ncMgr.streamWidth >= 0)
            internalVideoWidth = _ncMgr.streamWidth;
        else
            internalVideoWidth = -1;
            
        if (!isNaN(_ncMgr.streamHeight) && _ncMgr.streamHeight >= 0) 
            internalVideoHeight = _ncMgr.streamHeight;
        else
            internalVideoHeight = -1        

        // just start if static, start resize otherwise
        if (autoPlay) 
            if (!_ncMgr.isRTMP()) 
            {
                cachedState = BUFFERING;
                _play();
            } 
            else if (_isLive) 
            {
                cachedState = BUFFERING;
                _play(-1);
            } 
            else 
            {
                cachedState = BUFFERING;
                _play(0);
            } 
        else 
        {
            cachedState = STOPPED;
            if (_ncMgr.isRTMP()) 
                _play(-2, 0);
            else
            {
                _play();
                _pause(true);
                _seek(0);
            } 
        }
        
        autoResizeTimer.reset();
        autoResizeTimer.start(); 
    }

    /**
     *  @private
     *  <p>ONLY CALL THIS WITH RTMP STREAMING</p>
     *
     *  <p>Only used for rtmp connections.  When we pause or stop,
     *  setup a timer to call this after a delay (see property
     *  <code>idleTimeout</code>).  We do this to spare the server from
     *  having a bunch of extra xns hanging around, although this needs
     *  to be balanced with the load that creating connections puts on
     *  the server, and keep in mind that FCS can be configured to
     *  terminate idle connections on its own, which is a better way to
     *  manage the issue.</p>
     */
    private function doIdleTimeout(event:Event):void
    {
        idleTimeoutTimer.reset(); 
        close();
    }

    /**
     *  @private
     *  Dumps all queued commands without executing them
     */
    private function flushQueuedCmds():void 
    {
        while (cmdQueue.length > 0) 
            cmdQueue.pop();
    }

    /**
     *  @private
     *  Executes as many queued commands as possible, obviously
     *  stopping when state becomes unresponsive.
     */
    private function execQueuedCmds():void 
    {
        while (cmdQueue.length > 0 && (stateResponsive || _state == CONNECTION_ERROR) &&
                ((cmdQueue[0].url != null) ||
                  (_state != DISCONNECTED && _state != CONNECTION_ERROR)))
        {
            var nextCmd:VideoPlayerQueuedCommand = cmdQueue.shift();
            cachedState = _state;
            _state = EXEC_QUEUED_CMD;
            switch (nextCmd.type) 
            {
                case VideoPlayerQueuedCommand.PLAY:
                    play(nextCmd.url, nextCmd.isLive, nextCmd.time);
                    break;
                case VideoPlayerQueuedCommand.LOAD:
                    load(nextCmd.url, nextCmd.isLive, nextCmd.time);
                    break;
                case VideoPlayerQueuedCommand.PAUSE:
                    pause();
                    break;
                case VideoPlayerQueuedCommand.STOP:
                    stop();
                    break;
                case VideoPlayerQueuedCommand.SEEK:
                    seek(nextCmd.time);
                    break;
            } // switch
        }
    }

    private function queueCmd(type:uint, url:String = null, isLive:Boolean = false, time:Number = 0):void 
    {
        cmdQueue.push(new VideoPlayerQueuedCommand(type, url, isLive, time));
    }

} // class mx.controls.videoClasses.VideoPlayer

} // package mx.controls.videoClasses

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: VideoPlayerNetStream
//
////////////////////////////////////////////////////////////////////////////////

import flash.net.NetConnection;
import flash.net.NetStream;
import mx.controls.videoClasses.VideoPlayer;

/**
 *  @private
 *  This subclass of NetStream handles onMetaData() and onCuePoint()
 *  calls from the server and forwards them to the VideoPlayer.
 */
dynamic class VideoPlayerNetStream extends NetStream
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function VideoPlayerNetStream(connection:NetConnection,
                                         videoPlayer:VideoPlayer)
    {
        super(connection);

        this.videoPlayer = videoPlayer;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var videoPlayer:VideoPlayer;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Called by the server.
     */
    public function onMetaData(info:Object, ... rest):void
    {
        videoPlayer.onMetaData(info);
    }

    /**
     *  @private
     *  Called by the server.
     */
    public function onCuePoint(info:Object, ... rest):void
    {
        videoPlayer.onCuePoint(info);
    }

    /**
     *  @private
     */
    public function onPlayStatus(... rest):void
    {
    }   
}
