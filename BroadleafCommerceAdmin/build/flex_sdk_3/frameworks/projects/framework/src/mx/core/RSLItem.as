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

import flash.display.Loader;
import flash.events.Event;
import flash.events.ErrorEvent;
import flash.events.ProgressEvent;
import flash.events.IOErrorEvent;
import flash.events.SecurityErrorEvent;
import flash.net.URLRequest;
import flash.system.LoaderContext;
import flash.system.ApplicationDomain;
import flash.system.LoaderContext;
import flash.system.Security;
import flash.system.SecurityDomain;
import flash.utils.ByteArray;

import mx.events.RSLEvent;

[ExcludeClass]

/**
 *  @private
 *  RSL Item Class
 * 
 *  Contains properties to describe the RSL and methods to help load the RSL.
 */
public class RSLItem
{
    include "../core/Version.as";
 
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  urlRequest
    //----------------------------------

    /**
     *  @private
     *  Only valid after loading has started
     */
    public var urlRequest:URLRequest;

    //----------------------------------
    //  total
    //----------------------------------

    /**
     *  @private
     */
    public var total:uint = 0;
    
    //----------------------------------
    //  loaded
    //----------------------------------

    /**
     *  @private
     */
    public var loaded:uint = 0;

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    protected var url:String;
    
    /**
     *  @private
     */
    private var errorText:String;
    
    /**
     *  @private
     */
    private var completed:Boolean = false;
    
    /**
     *  @private
     *  External handlers so the load can be 
     *  observed by the class calling load().
     */
    protected var chainedProgressHandler:Function;    
    protected var chainedCompleteHandler:Function;
    protected var chainedIOErrorHandler:Function;
    protected var chainedSecurityErrorHandler:Function;
    protected var chainedRSLErrorHandler:Function;
     

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Create a RSLItem with a given URL.
     * 
     *  @param url location of RSL to load
     */
    public function RSLItem(url:String)
    {
        super();

        this.url = url;
    }
                    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     * 
     *  Load an RSL. 
     * 
     *  @param progressHandler Receives ProgressEvent.PROGRESS events.
     *  May be null.
     *
     *  @param completeHandler Receives Event.COMPLETE events.
     *  May be null.
     *
     *  @param ioErrorHandler Receives IOErrorEvent.IO_ERROR events.
     *  May be null.
     *
     *  @param securityErrorHandler
     *  Receives SecurityErrorEvent.SECURITY_ERROR events.
     *  May be null.
     *
     *  @param rslErrorHandler Receives RSLEvent.RSL_ERROR events.
     *  May be null.
     */
    public function load(progressHandler:Function,
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
        
        var loader:Loader = new Loader();               
        var loaderContext:LoaderContext = new LoaderContext();
        urlRequest = new URLRequest(url);
                    
        // The RSLItem needs to listen to certain events.

        loader.contentLoaderInfo.addEventListener(
            ProgressEvent.PROGRESS, itemProgressHandler);
            
        loader.contentLoaderInfo.addEventListener(
            Event.COMPLETE, itemCompleteHandler);
            
        loader.contentLoaderInfo.addEventListener(
            IOErrorEvent.IO_ERROR, itemErrorHandler);
            
        loader.contentLoaderInfo.addEventListener(
            SecurityErrorEvent.SECURITY_ERROR, itemErrorHandler);

        loaderContext.applicationDomain = ApplicationDomain.currentDomain;
        loader.load(urlRequest, loaderContext); 
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function itemProgressHandler(event:ProgressEvent):void
    {
        // Update the loaded and total properties.
        loaded = event.bytesLoaded;
        total = event.bytesTotal;
        
        // Notify an external listener
        if (chainedProgressHandler != null)
            chainedProgressHandler(event);
    }

    /**
     *  @private
     */
    public function itemCompleteHandler(event:Event):void
    {
        completed = true;
        
        // Notify an external listener
        if (chainedCompleteHandler != null)
            chainedCompleteHandler(event);
    }

    /**
     *  @private
     */
    public function itemErrorHandler(event:ErrorEvent):void
    {
        errorText = decodeURI(event.text);
        completed = true;
        loaded = 0;
        total = 0;
        
        trace(errorText);
        
        // Notify an external listener
        if (event.type == IOErrorEvent.IO_ERROR &&
            chainedIOErrorHandler != null)
        {
            chainedIOErrorHandler(event);
        }
        else if (event.type == SecurityErrorEvent.SECURITY_ERROR && 
                 chainedSecurityErrorHandler != null)
        {
            chainedSecurityErrorHandler(event);
        }
        else if (event.type == RSLEvent.RSL_ERROR && 
                 chainedRSLErrorHandler != null)
        {
            chainedRSLErrorHandler(event);
        }

    }
}

}
