////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.rpc.xml
{

import flash.events.Event;
import flash.events.EventDispatcher;

import mx.core.mx_internal;
import mx.rpc.AsyncToken;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;
import mx.rpc.events.XMLLoadEvent;
import mx.rpc.http.HTTPService;
import mx.utils.URLUtil;

use namespace mx_internal;

[Event(name="fault", type="mx.rpc.events.FaultEvent")]
[Event(name="xmlLoad", type="mx.rpc.events.XMLLoadEvent")]

[ExcludeClass]

/**
 * Base class to help manage loading of an XML document at runtime.
 * @private
 */
public class XMLLoader extends EventDispatcher
{
    public function XMLLoader(httpService:HTTPService = null)
    {
        super();

        initializeService(httpService);
    }

    /**
     * Asynchronously loads an XML document for the given URL.
     */
    public function load(url:String):void
    {
        url = getQualifiedLocation(url);
        internalLoad(url);
    }

    protected function initializeService(httpService:HTTPService = null):void
    {
        loader = new HTTPService();

        if (httpService != null)
        {
            loader.asyncRequest = httpService.asyncRequest;
            if (httpService.destination != null)
                loader.destination = httpService.destination;
            loader.useProxy = httpService.useProxy;
            loader.rootURL = httpService.rootURL;
        }

        loader.addEventListener(ResultEvent.RESULT, resultHandler);
        loader.addEventListener(FaultEvent.FAULT, faultHandler);
        loader.resultFormat = HTTPService.RESULT_FORMAT_E4X;
    }

    protected function internalLoad(location:String):AsyncToken
    {
        loadsOutstanding++;

        loader.url = location;
        var token:AsyncToken = loader.send();

        if (token != null)
            token.location = location;

        return token;
    }

    protected function getQualifiedLocation(location:String, 
        parentLocation:String = null):String
    {
        if (parentLocation != null)
            location = URLUtil.getFullURL(parentLocation, location);
        else
            location = URLUtil.getFullURL(loader.rootURL, location);

        return location;
    }

    /**
     * If a fault occured trying to load the XML document, a FaultEvent
     * is simply redispatched.
     */
    protected function faultHandler(event:FaultEvent):void
    {
        loadsOutstanding--;
        dispatchEvent(event);
    }

    /**
     * Dispatches an XMLLoadEvent with the XML formatted result
     * and location (if known).
     */
    protected function resultHandler(event:ResultEvent):void
    {
        loadsOutstanding--;

        var xml:XML = XML(event.result);
        var token:AsyncToken = event.token;
        var location:String = token == null ? null : token.location;
        var xmlLoadEvent:XMLLoadEvent = XMLLoadEvent.createEvent(xml, location);

        dispatchEvent(xmlLoadEvent);
    }

    protected var loader:HTTPService;
    public var loadsOutstanding:int;
}
    
}