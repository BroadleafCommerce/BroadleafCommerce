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

package mx.core
{

import flash.events.ErrorEvent;
import flash.events.Event;
import flash.events.IEventDispatcher;
import flash.events.IOErrorEvent;
import flash.events.ProgressEvent;
import mx.events.ResourceEvent;
import mx.events.RSLEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ExcludeClass]

/**
 *	@private
 *
 *  Preloads a resource module during frame 1, from an URL that was
 *  specified in the application's parameters["resourceModuleURLs"].
 *
 *  The Preloader uses RSLListLoader to sequentially load
 *  cross-domain RSLs, regular RSLs, and resource modules.
 *  Each of these is represented by an RSLItem in a queue
 *  processed by RSLListLoader.
 *  The class names ResourceModuleRSLItem, RSLItem, RSLListLoader,
 *  and RSLEvent are slightly misleading in that a resource module
 *  isn't really an RSL.
 */
public class ResourceModuleRSLItem extends RSLItem
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
     */	
	public function ResourceModuleRSLItem(url:String)
	{
		super(url);
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 * 
	 *  Preloads a resource module
     * 
     *  @param progressHandler Receives ProgressEvent.PROGRESS events, may be null
	 *
     *  @param completeHandler Receives Event.COMPLETE events, may be null
	 *
     *  @param ioErrorHandler Receives IOErrorEvent.IO_ERROR events, may be null
	 *
     *  @param securityErrorHandler Receives SecurityErrorEvent.SECURITY_ERROR events, may be null
	 *
     *  @param rslErrorHandler Receives RSLEvent.RSL_ERROR events, may be null
	 */
	override public function load(progressHandler:Function,
                                  completeHandler:Function,
	                              ioErrorHandler:Function,
	                              securityErrorHandler:Function,
	                              rslErrorHandler:Function):void 
	{
	    chainedProgressHandler = progressHandler;
	    chainedCompleteHandler = completeHandler;
	    chainedIOErrorHandler = ioErrorHandler;
	    chainedSecurityErrorHandler = securityErrorHandler;
	    chainedRSLErrorHandler = rslErrorHandler;
	    
		var resourceManager:IResourceManager = ResourceManager.getInstance();
		
		var eventDispatcher:IEventDispatcher =
			resourceManager.loadResourceModule(url);
		
		eventDispatcher.addEventListener(
			ResourceEvent.PROGRESS, itemProgressHandler);
			
		eventDispatcher.addEventListener(
			ResourceEvent.COMPLETE, itemCompleteHandler);
			
		eventDispatcher.addEventListener(
			ResourceEvent.ERROR, resourceErrorHandler);
	}
		
	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function resourceErrorHandler(event:ResourceEvent):void
	{
		var errorEvent:IOErrorEvent = new IOErrorEvent(IOErrorEvent.IO_ERROR);
		errorEvent.text = event.errorText;

		super.itemErrorHandler(errorEvent);
	}
}

}
