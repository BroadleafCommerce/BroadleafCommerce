////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.effects
{

import flash.errors.IOError;
import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.events.ProgressEvent;
import flash.media.Sound;
import flash.media.SoundLoaderContext;
import flash.net.URLRequest;
import mx.effects.effectClasses.SoundEffectInstance;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

/**
 *  Dispatched when the sound file finishes loading.
 *
 *  @eventType flash.events.Event.COMPLETE
 */
[Event(name="complete", type="flash.events.Event")]

/**
 *  Dispatched when ID3 data is available for an MP3 sound file.
 *
 *  @eventType flash.events.Event.ID3
 */
[Event(name="id3", type="flash.events.Event")]

/**
 *  Dispatched when an error occurs during the loading of the sound file.
 *
 *  @eventType flash.events.IOErrorEvent.IO_ERROR 
 */
[Event(name="ioError", type="flash.events.IOErrorEvent")]

/**
 *  Dispatched periodically as the sound file loads.
 *
 *  <p>Within the event object, you can access the number of bytes
 *  currently loaded and the total number of bytes to load.
 *  The event is not guaranteed to be dispatched, which means that
 *  the <code>complete</code> event might be dispatched 
 *  without any <code>progress</code> events being dispatched.</p>
 *
 *  @eventType flash.events.ProgressEvent.PROGRESS 
 */
[Event(name="progress", type="flash.events.ProgressEvent")]

[ResourceBundle("effects")]

/**
 *  The SoundEffect class plays an MP3 audio file. 
 *  For example, you could play a sound when a user clicks a Button control. 
 *  This effect lets you repeat the sound, select the source file,
 *  and control the volume and pan. 
 *
 *  <p>You specify the MP3 file using the <code>source</code> property. 
 *  If you have already embedded the MP3 file, using the <code>Embed</code>
 *  keyword, you can pass the Class object of the MP3 file to the
 *  <code>source</code> property. 
 *  Otherwise, specify the full URL to the MP3 file.</p>
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:SoundEffect&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:SoundEffect
 *    <b>Properties</b>
 *    id="ID"
 *    autoLoad="true|false"
 *    bufferTime="1000"
 *    loops="0"
 *    panEasingFunction=""
 *    panFrom="0"
 *    source=""
 *    startTime="0"
 *    useDuration="true|false"
 *    volumeEasingFunction="true|false"
 *    volumeTo="1"
 *     
 *    <b>Events</b>
 *    complete="<i>No default</i>"
 *    id3="<i>No default</i>"
 *    ioError="<i>No default</i>"
 *    progress="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.effects.effectClasses.SoundEffectInstance
 *  @see flash.media.Sound
 *
 *  @includeExample examples/SoundEffectExample.mxml
 */
public class SoundEffect extends Effect
{
    include "../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param target The Object to animate with this effect.
     */
    public function SoundEffect(target:Object = null)
    {
        super(target);
        
        instanceClass = SoundEffectInstance;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /** 
     *  @private
     */
    private var needsLoading:Boolean = false;
    
	/**
	 *  @private
	 *  Used for accessing localized Error messages.
	 */
	private var resourceManager:IResourceManager =
									ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  autoLoad
    //----------------------------------

    /**
     *  @private
     *  Storage for the autoLoad property.
     */
    private var _autoLoad:Boolean = true;
    
    [Inspectable(category="General", defaultValue="true")]

    /** 
     *  If <code>true</code>, load the MP3 file 
     *  when the <code>source</code> has been specified.
     *
     *  @default true
     */
    public function get autoLoad():Boolean
    {
        return _autoLoad;
    }

    /**
     *  @private
     */
    public function set autoLoad(value:Boolean):void
    {
        _autoLoad = value;
    }
    
    //----------------------------------
    //  bufferTime
    //----------------------------------

    [Inspectable(category="General", defaultValue="1000")]

    /** 
     *  The SoundEffect class uses an internal Sound object to control
     *  the MP3 file.
     *  This property specifies the minimum number of milliseconds 
     *  worth of sound data to hold in the Sound object's buffer.
     *  The Sound object waits until it has at least
     *  this much data before beginning playback,
     *  and before resuming playback after a network stall.
     *
     *  @default 1000
     */
    public var bufferTime:Number = 1000;
    
    //----------------------------------
    //  isLoading
    //----------------------------------

    /**
     *  This property is <code>true</code> if the MP3 has been loaded. 
     */
    public function get isLoading():Boolean
    {
        if (_sound)
        {
            return _sound.bytesTotal > 0;
        }

        return false;
    }
    
    //----------------------------------
    //  loops
    //----------------------------------

    [Inspectable(category="General", defaultValue="0")]

    /**
     *  The number of times to play the sound in a loop, where a value of
     *  0 means play the effect once, a value of 1 means play the effect twice,
     *  and so on. If you repeat the MP3 file, it still uses the setting of the
     *  <code>useDuration</code> property to determine the playback time.
     *
     *  <p>The <code>duration</code> property takes precedence
     *  over this property. 
     *  If the effect duration is not long enough to play the sound
     *  at least once, the sound does not loop.</p>
     *
     *  @default 0
     */
    public var loops:int = 0;
    
    //----------------------------------
    //  panEasingFunction
    //----------------------------------

    /**
     *  The easing function for the pan effect.
     *  Use this function to interpolate between the values
     *  of <code>panFrom</code> and <code>panTo</code>.
     */
    public var panEasingFunction:Function;
    
    //----------------------------------
    //  panFrom
    //----------------------------------

    [Inspectable(category="General", defaultValue="0.0")]

    /**
     *  Initial pan of the Sound object.
     *  The value can range from -1.0 to 1.0, where -1.0 uses only the left 
     *  channel, 1.0 uses only the right channel, and 0.0 balances the sound 
     *  evenly between the two channels.
     *
     *  @default 0.0     
     */
    public var panFrom:Number;
    
    //----------------------------------
    //  panTo
    //----------------------------------

    [Inspectable(category="General", defaultValue="0.0")]

    /**
     *  Final pan of the Sound object.
     *  The value can range from -1.0 to 1.0, where -1.0 uses only the left channel,
     *  1.0 uses only the right channel, and 0.0 balances the sound evenly
     *  between the two channels.
     *
     *  @default 0.0
     */
    public var panTo:Number;
    
    //----------------------------------
    //  sound
    //----------------------------------
            
    /**
     *  @private
     */
    private var _sound:Sound;
    
    /**
     *  The Sound object that the MP3 file has been loaded into. 
     */
    public function get sound():Sound
    {
        return _sound;
    }

    //----------------------------------
    //  source
    //----------------------------------

    /** 
     *  @private
     *  Storage for the source property.
     */
    private var _source:Object;

    [Inspectable(category="General", defaultValue="null")]

    /**
     *  The URL or class of the MP3 file to play.
     *  If you have already embedded the MP3 file, using the <code>Embed</code> keyword, 
     *  you can pass the Class object of the MP3 file to the <code>source</code> property. 
     *  Otherwise, specify the full URL to the MP3 file.
     */
    public function get source():Object
    {
        return _source;
    }

    /** 
     *  @private
     */
    public function set source(value:Object):void
    {   
        if (_sound)
        {
            try 
            {
                _sound.close();
            }
            catch(e:IOError)
            {
                // Ignore these
            }
            
            removeSoundListeners();
        }
        
        _source = value;
        
        if (_source is Class)
        {
            var cls:Class = Class(_source);
            _sound = new cls();
            attachSoundListeners();
        }
        else if (_source is String)
        {
            needsLoading = true;
            _sound = new Sound(); 
            attachSoundListeners();
        }
        else
        {
			var message:String = resourceManager.getString(
				"effects", "incorrectSource");
            throw new Error(message);
        }
        
        if (autoLoad)
            load();
    }
    
    //----------------------------------
    //  startTime
    //----------------------------------

    [Inspectable(category="General", defaultValue="0")]

    /** 
     *  The initial position in the MP3 file, in milliseconds, 
     *  at which playback should start.
     *
     *  @default 0
     */
    public var startTime:Number = 0;
    
    //----------------------------------
    //  useDuration
    //----------------------------------

    [Inspectable(category="General", defaultValue="true")]

    /**
     *  If <code>true</code>, stop the effect
     *  after the time specified by the <code>duration</code> 
     *  property has elapsed.
     *  If <code>false</code>, stop the effect
     *  after the MP3 finishes playing or looping.
     *
     *  @default true    
     */
    public var useDuration:Boolean = true;
    
    //----------------------------------
    //  volumeEasingFunction
    //----------------------------------

    /**
     *  The easing function for the volume effect.
     *  This function is used to interpolate between the values
     *  of <code>volumeFrom</code> and <code>volumeTo</code>.
     */
    public var volumeEasingFunction:Function;
    
    //----------------------------------
    //  volumeFrom
    //----------------------------------

    [Inspectable(category="General", defaultValue="1.0")]

    /**
     *  Initial volume of the Sound object.
     *  Value can range from 0.0 to 1.0.
     *
     *  @default 1   
     */
    public var volumeFrom:Number;
    
    //----------------------------------
    //  volumeTo
    //----------------------------------

    [Inspectable(category="General", defaultValue="1.0")]

    /**
     *  Final volume of the Sound object.
     *  Value can range from 0.0 to 1.0.
     *
     *  @default 1   
     */
    public var volumeTo:Number;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function initInstance(instance:IEffectInstance):void
    {
        super.initInstance(instance);
        
        var soundEffectInstance:SoundEffectInstance =
            SoundEffectInstance(instance);
        
        soundEffectInstance.source = source;
        soundEffectInstance.sound = sound;
        soundEffectInstance.panFrom = panFrom;
        soundEffectInstance.panTo = panTo;
        soundEffectInstance.volumeFrom = volumeFrom;
        soundEffectInstance.volumeTo = volumeTo;
        soundEffectInstance.panEasingFunction = panEasingFunction;
        soundEffectInstance.volumeEasingFunction = volumeEasingFunction;
        soundEffectInstance.loops = loops;
        soundEffectInstance.startTime = startTime;
        soundEffectInstance.useDuration = useDuration;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Loads the MP3 if the <code>source</code> property points to a URL.
     */
    public function load():void
    {
        if (_sound && _source && _source is String && needsLoading)
            _sound.load(new URLRequest(String(_source)), new SoundLoaderContext(bufferTime));
    }
    
    /**
     *  @private
     */
    private function attachSoundListeners():void
    {
        if (_sound)
        {
            _sound.addEventListener(Event.COMPLETE, soundEventHandler);
            _sound.addEventListener(ProgressEvent.PROGRESS, soundEventHandler);
            _sound.addEventListener(IOErrorEvent.IO_ERROR, soundEventHandler);
            _sound.addEventListener(Event.ID3, soundEventHandler);
        }
    }
    
    /**
     *  @private
     */
    private function removeSoundListeners():void
    {
        if (_sound)
        {
            _sound.removeEventListener(Event.COMPLETE, soundEventHandler);
            _sound.removeEventListener(ProgressEvent.PROGRESS, soundEventHandler);
            _sound.removeEventListener(IOErrorEvent.IO_ERROR, soundEventHandler);
            _sound.removeEventListener(Event.ID3, soundEventHandler);
        }
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  Just act as a proxy for all events from the sound.
     */
    private function soundEventHandler(event:Event):void
    {
        dispatchEvent(event);
    }
}

}
