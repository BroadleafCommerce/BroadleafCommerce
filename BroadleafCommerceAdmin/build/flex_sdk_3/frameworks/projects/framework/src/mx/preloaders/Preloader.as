////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.preloaders
{

import flash.display.DisplayObject;
import flash.display.Loader;
import flash.display.LoaderInfo;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.events.ErrorEvent;
import flash.events.Event;
import flash.events.IEventDispatcher;
import flash.events.IOErrorEvent;
import flash.events.ProgressEvent;
import flash.events.SecurityErrorEvent;
import flash.events.TimerEvent;
import flash.net.URLLoader;
import flash.net.URLLoaderDataFormat;
import flash.net.URLRequest;
import flash.utils.Timer;

import mx.core.mx_internal;
import mx.core.ResourceModuleRSLItem;
import mx.core.RSLItem;
import mx.core.RSLListLoader;
import mx.events.FlexEvent;
import mx.events.RSLEvent;

/**
 *  The Preloader class is used by the SystemManager to monitor
 *  the download and initialization status of a Flex application.
 *  It is also responsible for downloading the runtime shared libraries (RSLs).
 *
 *  <p>The Preloader class instantiates a download progress bar, 
 *  which must implement the IPreloaderDisplay interface, and passes download
 *  and initialization events to the download progress bar.</p>
 *
 *  @see mx.preloaders.DownloadProgressBar
 *  @see mx.preloaders.Preloader
 */
public class Preloader extends Sprite
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *	Constructor.
	 */
	public function Preloader()
	{
		super()
	}	
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var displayClass:IPreloaderDisplay = null;
	
	/**
	 *  @private
	 */
	private var timer:Timer;
	
	/**
	 *  @private
	 */
	private var showDisplay:Boolean;
	
	/**
	 *  @private
	 */
	private var rslListLoader:RSLListLoader;
	
	/**
	 *  @private
	 */
	private var rslDone:Boolean = false;
	
	/**
	 *  @private
	 */
	private var app:IEventDispatcher = null;
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**  
	 *  Called by the SystemManager to initialize a Preloader object.
	 * 
	 *  @param showDisplay Determines if the display class should be displayed.
	 *
	 *  @param displayClassName The IPreloaderDisplay class to use
	 *  for displaying the preloader status.
	 *
	 *  @param backgroundColor Background color of the application.
	 *
	 *  @param backgroundAlpha Background alpha of the application.
	 *
	 *  @param backgroundImage Background image of the application.
	 *
	 *  @param backgroundSize Background size of the application.
	 *
	 *  @param displayWidth Width of the application.
	 *
	 *  @param displayHeight Height of the application.
	 *
	 *  @param libs Array of string URLs for the runtime shared libraries.
	 *
	 *  @param sizes Array of uint values containing the byte size for each URL
	 *  in the libs argument
	 * 
	 *  @param rslList Array of object of type RSLItem and CdRSLItem.
	 *  This array describes all the RSLs to load.
	 *  The libs and sizes parameters are ignored and must be set to null.
	 *
	 *  @param resourceModuleURLs Array of Strings specifying URLs
	 *  from which to preload resource modules.
	 */ 
	public function initialize(showDisplay:Boolean, 
							   displayClassName:Class,
							   backgroundColor:uint,
							   backgroundAlpha:Number,
							   backgroundImage:Object,
							   backgroundSize:String,
							   displayWidth:Number,
							   displayHeight:Number,
							   libs:Array = null,
							   sizes:Array = null,
							   rslList:Array = null,
							   resourceModuleURLs:Array = null):void
	{
        if ((libs != null || sizes != null) && rslList != null)
        {
            // both args can't be used at the same time
            throw new Error("RSLs may only be specified by using libs and sizes or rslList, not both.");  // $NON-NLS-1$
        }

		root.loaderInfo.addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
	
		var n:int;
		var i:int;
		
		// Store the RSL information.
		// Keep this code for API backwards compatibility
		if (libs && libs.length > 0)
		{
			if (rslList == null)
			{
				rslList = [];
			}
	
			n = libs.length;
			for (i = 0; i < n; i++)
			{
			    var node:RSLItem = new RSLItem(libs[i]);
				rslList.push(node);
			}
		}

		// Preloading resource modules is similar enough to loading RSLs
		// that we can simply create ResourceModuleRSLItems for them
		// and append these to the rslList.
		if (resourceModuleURLs && resourceModuleURLs.length > 0)
		{
			n = resourceModuleURLs.length;
			for (i = 0; i < n; i++)
			{
				var resourceModuleNode:ResourceModuleRSLItem =
					new ResourceModuleRSLItem(resourceModuleURLs[i]);
				rslList.push(resourceModuleNode);
			}
		}

        rslListLoader = new RSLListLoader(rslList);
		
		this.showDisplay = showDisplay;

		// Create the timer (really should be adding event listeners to root.LoaderInfo)	
		timer = new Timer(10);
		timer.addEventListener(TimerEvent.TIMER, timerHandler);
		timer.start();
		
		// Create a new instance of the display class and attach it to the stage
		if (showDisplay)
		{
			displayClass = new displayClassName(); 
			// Listen for when the displayClass no longer needs to be on the stage
			displayClass.addEventListener(Event.COMPLETE,
										  displayClassCompleteHandler);
			
			// Add the display class as a child of the Preloader
			addChild(DisplayObject(displayClass));
						
			displayClass.backgroundColor = backgroundColor;
			displayClass.backgroundAlpha = backgroundAlpha;
			displayClass.backgroundImage = backgroundImage;
			displayClass.backgroundSize = backgroundSize;
			displayClass.stageWidth = displayWidth;
			displayClass.stageHeight = displayHeight;
			displayClass.initialize();  
			displayClass.preloader = this;
		}	
		
		// move below showDisplay so error messages can be displayed
		if (rslListLoader.getItemCount() > 0)
		{
			// Start loading the RSLs.
			rslListLoader.load(mx_internal::rslProgressHandler,
							   mx_internal::rslCompleteHandler,
							   mx_internal::rslErrorHandler,
							   mx_internal::rslErrorHandler,
							   mx_internal::rslErrorHandler);
		}
		else
		{
		    rslDone = true;
		}
	}
	
	/**
	 *  Called by the SystemManager after it has finished instantiating
	 *  an instance of the application class. Flex calls this method; you 
	 *  do not call it yourself.
	 *
	 *  @param app The application object.
	 */
	public function registerApplication(app:IEventDispatcher):void
	{
		// Listen for events from the application.
		app.addEventListener("validatePropertiesComplete", appProgressHandler);
		app.addEventListener("validateSizeComplete", appProgressHandler);
		app.addEventListener("validateDisplayListComplete", appProgressHandler);
		app.addEventListener(FlexEvent.CREATION_COMPLETE, appCreationCompleteHandler);
        
        // Cache for later cleanup.
        this.app = app;
	}
	
	
	/**
	 *  @private
	 *  Return the number of bytes loaded and total for the SWF and any RSLs.
	 */
	private function getByteValues():Object
	{
		var li:LoaderInfo = root.loaderInfo;
		var loaded:int = li.bytesLoaded;
		var total:int = li.bytesTotal;
		
		// Look up the rsl bytes and include those
		var n:int = rslListLoader ? rslListLoader.getItemCount() : 0;
		for (var i:int = 0; i < n; i++)
		{
			loaded += rslListLoader.getItem(i).loaded;
			total += rslListLoader.getItem(i).total;
		}
		
		return { loaded: loaded, total: total };
	}
	
	/**
	 *  @private
	 */
	private function dispatchAppEndEvent(event:Object = null):void
	{
		// Dispatch the application initialization end event
		dispatchEvent(new FlexEvent(FlexEvent.INIT_COMPLETE));
		
		if (!showDisplay)
			displayClassCompleteHandler(null);
	}
	
	/**
	 *  @private
	 *  We don't listen for the events directly
	 *  because we don't know which RSL is sending the event.
	 *  So we have the RSLNode listen to the events
	 *  and then pass them along to the Preloader.
	 */ 
	mx_internal function rslProgressHandler(event:ProgressEvent):void
	{
	    var index:int = rslListLoader.getIndex();
	    var item:RSLItem = rslListLoader.getItem(index);
	    
	    
		var rslEvent:RSLEvent = new RSLEvent(RSLEvent.RSL_PROGRESS);
		rslEvent.bytesLoaded = event.bytesLoaded;
		rslEvent.bytesTotal = event.bytesTotal;
		rslEvent.rslIndex = index;
		rslEvent.rslTotal = rslListLoader.getItemCount();
		rslEvent.url = item.urlRequest;
		dispatchEvent(rslEvent);
	}

	
	/**
	 *  @private
	 *  Load the next RSL in the list and dispatch an event.
	 */
	mx_internal function rslCompleteHandler(event:Event):void
	{
	    var index:int = rslListLoader.getIndex();
	    var item:RSLItem = rslListLoader.getItem(index);
		var rslEvent:RSLEvent = new RSLEvent(RSLEvent.RSL_COMPLETE);
		rslEvent.bytesLoaded = item.total;
		rslEvent.bytesTotal = item.total;
		rslEvent.rslIndex = index;
		rslEvent.rslTotal = rslListLoader.getItemCount();
		rslEvent.url = item.urlRequest;
		dispatchEvent(rslEvent);
		
		rslDone = (index + 1 == rslEvent.rslTotal);
	}
		
	
	/**
	 *  @private
	 */
	mx_internal function rslErrorHandler(event:ErrorEvent):void
	{
		// send an error event
	    var index:int = rslListLoader.getIndex();
	    var item:RSLItem = rslListLoader.getItem(index);
		var rslEvent:RSLEvent = new RSLEvent(RSLEvent.RSL_ERROR);
		rslEvent.bytesLoaded = 0;
		rslEvent.bytesTotal = 0;
		rslEvent.rslIndex = index;
		rslEvent.rslTotal = rslListLoader.getItemCount();
		rslEvent.url = item.urlRequest;
		rslEvent.errorText = decodeURI(event.text);
		dispatchEvent(rslEvent);

	}

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Listen or poll for progress events and dispatch events
	 *  describing the current state of the download
	 */
	private function timerHandler(event:TimerEvent):void
	{
		// loaded swfs may not have root right away
		if (!root)
			return;

		var bytes:Object = getByteValues();
		var loaded:int = bytes.loaded;
		var total:int = bytes.total;
		
		// Dispatch a progress event (later we might conditionalize this
		// so that it isn't sent on a cache load).
		dispatchEvent(new ProgressEvent(ProgressEvent.PROGRESS,
										false, false, loaded, total));

		// Check if we are finished
		if (rslDone &&
			((loaded >= total && total > 0) || (total == 0 && loaded > 0) || (root is MovieClip && (MovieClip(root).totalFrames > 2) && (MovieClip(root).framesLoaded >= 2)) ))
		{
			timer.removeEventListener(TimerEvent.TIMER, timerHandler);
			
			// Stop the timer.
			timer.reset();
			
			// Dispatch a complete event.
			dispatchEvent(new Event(Event.COMPLETE));
			
			// Dispatch an initProgress event.
			dispatchEvent(new FlexEvent(FlexEvent.INIT_PROGRESS));
		}
	}

	/**
	 *  @private
	 */
	private function ioErrorHandler(event:IOErrorEvent):void
	{
		// Ignore the event
	}

	/**
	 *  @private
	 *	Called when the displayClass has finished animating
	 *  and no longer needs to be displayed.
	 */
	private function displayClassCompleteHandler(event:Event):void
	{
	    // Cleanup
		if (displayClass)
			displayClass.removeEventListener(Event.COMPLETE, displayClassCompleteHandler);
		
		if (root) 
		    root.loaderInfo.removeEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
		    
		if (app) 
		{
    		app.removeEventListener("validatePropertiesComplete", appProgressHandler);
    		app.removeEventListener("validateSizeComplete", appProgressHandler);
    		app.removeEventListener("validateDisplayListComplete", appProgressHandler);
    		app.removeEventListener(FlexEvent.CREATION_COMPLETE, appCreationCompleteHandler);
    		app = null;
    	}
		    
		// Send an event to the SystemManager that we are completely finished
		dispatchEvent(new FlexEvent(FlexEvent.PRELOADER_DONE));
	}
		
	/**
	 *  @private
	 */
	private function appCreationCompleteHandler(event:FlexEvent):void
	{		
		dispatchAppEndEvent();
	}
	
	/**
	 *  @private
	 */
	private function appProgressHandler(event:Event):void
	{		
		dispatchEvent(new FlexEvent(FlexEvent.INIT_PROGRESS));
	}
}

}
	
