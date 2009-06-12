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
import flash.events.TimerEvent;
import flash.net.URLRequest;
import flash.net.URLLoader;
import flash.net.URLLoaderDataFormat;
import flash.system.LoaderContext;
import flash.system.ApplicationDomain;
import flash.system.LoaderContext;
import flash.system.Security;
import flash.system.SecurityDomain;
import flash.utils.ByteArray;
import flash.utils.Timer;

import mx.events.RSLEvent;
import mx.utils.SHA256;

[ExcludeClass]

/**
 *  @private
 *  Cross-domain RSL Item Class.
 * 
 *  The rsls are typically located on a different host than the loader. 
 *  There are signed and unsigned Rsls, both have a digest to confirm the 
 *  correct rsl is loaded.
 *  Signed Rsls are loaded by setting the digest of the URLRequest.
 *  Unsigned Rsls are check using actionScript to calculate a sha-256 hash of 
 *  the loaded bytes and compare them to the expected digest.
 * 
 */
public class CrossDomainRSLItem extends RSLItem
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    private var rslUrls:Array;  // first url is the primary url in the url parameter, others are failovers
    private var policyFileUrls:Array; // optional policy files, parallel array to rslUrls
    private var digests:Array;      // option rsl digest, parallel array to rslUrls
    private var isSigned:Array;     // each entry is a boolean value. "true" if the rsl in the parallel array is signed
    private var hashTypes:Array;     //  type of hash used to create the digest
    private var urlIndex:int = 0;   // index into url being loaded in rslsUrls and other parallel arrays
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
    * Create a cross-domain RSL item to load.
    * 
    * @param rslUrls Array of Strings, may not be null. Each String is the url of an RSL to load.
    * @param policyFileUrls Array of Strings, may not be null. Each String contains the url of an
    *                       policy file which may be required to allow the RSL to be read from another
    *                       domain. An empty string means there is no policy file specified.
    * @param digests Array of Strings, may not be null. A String contains the value of the digest
    *                computed by the hash in the corresponding entry in the hashTypes Array. An empty
    *                string may be provided for unsigned RSLs to loaded them without verifying the digest.
    *                This is provided as a development cycle convenience and should not be used in a
    *                production application.
    * @param hashTypes Array of Strings, may not be null. Each String identifies the type of hash
    *                  used to compute the digest. Currently the only valid value is SHA256.TYPE_ID.
    * @param hashTypes Array of boolean, may not be null. Each boolean value specifies if the RSL to be
    *                  loaded is a signed or unsigned RSL. If the value is true the RSL is signed. 
    *                  If the value is false the RSL is unsigned.
    */  
    public function CrossDomainRSLItem(rslUrls:Array,
                             policyFileUrls:Array, 
                             digests:Array,
                             hashTypes:Array,
                             isSigned:Array)
    {
        super(rslUrls[0]);
        
        this.rslUrls = rslUrls;
        this.policyFileUrls = policyFileUrls;
        this.digests = digests;
        this.hashTypes = hashTypes;
        this.isSigned = isSigned;
    }


    //--------------------------------------------------------------------------
    //
    //  Overridden Methods
    //
    //--------------------------------------------------------------------------
    
    
    /**
     * 
     * Load an RSL. 
    * 
    * @param progressHandler       receives ProgressEvent.PROGRESS events, may be null.
    * @param completeHandler       receives Event.COMPLETE events, may be null.
    * @param ioErrorHandler        receives IOErrorEvent.IO_ERROR events, may be null.
    * @param securityErrorHandler  receives SecurityErrorEvent.SECURITY_ERROR events, may be null.
    * @param rslErrorHandler       receives RSLEvent.RSL_ERROR events, may be null.
    * 
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


/*      
        // Debug loading of swf files

        trace("begin load of " + url);
                if (Security.sandboxType == Security.REMOTE)
        {
            trace(" in REMOTE sandbox");    
        }
        else if (Security.sandboxType == Security.LOCAL_TRUSTED)
        {
            trace(" in LOCAL_TRUSTED sandbox");                 
        }
        else if (Security.sandboxType == Security.LOCAL_WITH_FILE)
        {
            trace(" in LOCAL_WITH_FILE sandbox");                   
        }
        else if (Security.sandboxType == Security.LOCAL_WITH_NETWORK)
        {
            trace(" in LOCAL_WITH_NETWORK sandbox");                    
        }
*/

        urlRequest = new URLRequest(rslUrls[urlIndex]);
        var loader:URLLoader = new URLLoader();
        loader.dataFormat = URLLoaderDataFormat.BINARY;

        // We needs to listen to certain events.
            
        loader.addEventListener(
            ProgressEvent.PROGRESS, itemProgressHandler);
            
        loader.addEventListener(
            Event.COMPLETE, itemCompleteHandler);
            
        loader.addEventListener(
            IOErrorEvent.IO_ERROR, itemErrorHandler);
            
        loader.addEventListener(
            SecurityErrorEvent.SECURITY_ERROR, itemErrorHandler);

        if (policyFileUrls.length > urlIndex &&
            policyFileUrls[urlIndex] != "")
        {
            Security.loadPolicyFile(policyFileUrls[urlIndex]);
        }
        
        if (isSigned[urlIndex])
        {
            if (urlRequest.hasOwnProperty("digest"))
            {
                // load a signed rsl by specifying the digest
                urlRequest.digest = digests[urlIndex];
            }
            else if (hasFailover())             
            {
                loadFailover();
                return;
            }
            else
            {
                // B Feature: externalize error message
                var rslError:ErrorEvent = new ErrorEvent(RSLEvent.RSL_ERROR);
                rslError.text = "Flex Error #1002: Flash Player 9.0.60 and above is required to support signed RSLs. Problem occurred when trying to load the RSL " +
                                urlRequest.url + 
                                ".  Upgrade your Flash Player and try again.";
                super.itemErrorHandler(rslError);
                return;
            }
        }
        loader.load(urlRequest);
    }
    
    

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Complete the load of the cross-domain rsl by loading it into the current
     *  application domain. The load was started by loadCdRSL.
     * 
     *  @param - urlLoader from the complete event.
     * 
     *  @return - true if the load was completed successfully or unsuccessfully, 
     *            false if the load of a failover rsl was started
     */
    private function completeCdRslLoad(urlLoader:URLLoader):Boolean
    {
        // handle player bug #204244, complete event without data after an error
        if (urlLoader == null || urlLoader.data == null || ByteArray(urlLoader.data).bytesAvailable == 0)
        {
            return true;
        }
        
        // load the bytes into the current application domain.
        var loader:Loader = new Loader();
        var context:LoaderContext = new LoaderContext();
        context.applicationDomain = ApplicationDomain.currentDomain;
        context.securityDomain = null;
        
        // If the AIR flag is available then set it to true so we can
        // load the RSL without a security error.
        if ("allowLoadBytesCodeExecution" in context)
        {
            context["allowLoadBytesCodeExecution"] = true;
        }   
        
        // verify the digest, if any, is correct
        if (digests[urlIndex] != null && String(digests[urlIndex]).length > 0)
        {
            var verifiedDigest:Boolean = false;
            if (!isSigned[urlIndex])
            {
                // verify an unsigned rsl
                if (hashTypes[urlIndex] == SHA256.TYPE_ID)
                {
                    // get the bytes from the rsl and calculate the hash
                    var rslDigest:String = null;
                    if (urlLoader.data != null)
                    {
                        rslDigest = SHA256.computeDigest(urlLoader.data);
                    }

                    if (rslDigest == digests[urlIndex])
                    {
                        verifiedDigest = true;
                    }
                }
            }
            else
            {
                // signed rsls are verified by the player
                verifiedDigest = true;
            }           
            
            if (!verifiedDigest)
            {
                // failover to the next rsl, if one exists
                // no failover to load, all the rsls have failed to load
                // report an error.
                 // B Feature: externalize error message
                var hasFailover:Boolean = hasFailover();
                var rslError:ErrorEvent = new ErrorEvent(RSLEvent.RSL_ERROR);
                rslError.text = "Flex Error #1001: Digest mismatch with RSL " +
                                urlRequest.url + 
                                ". Redeploy the matching RSL or relink your application with the matching library.";
                itemErrorHandler(rslError);
                
                return !hasFailover;
            }
        }

        // load the rsl into memory
        loader.contentLoaderInfo.addEventListener(Event.COMPLETE, loadBytesCompleteHandler);
        loader.loadBytes(urlLoader.data, context);
        return true;
    }

  
    /**
    *  Does the current url being processed have a failover?
    * 
    * @return true if a failover url exists, false otherwise.
    */
    public function hasFailover():Boolean
    {
        return (rslUrls.length > (urlIndex + 1));
    }
    
    
    /**
    *  Load the next url from the list of failover urls.
    */
    public function loadFailover():void
    {
        // try to load the failover from the same node again
        if (urlIndex < rslUrls.length)
        {
            trace("Failed to load RSL " + rslUrls[urlIndex]);
            trace("Failing over to RSL " + rslUrls[urlIndex+1]);
            urlIndex++;        // move to failover url
            url = rslUrls[urlIndex];
            load(chainedProgressHandler,
                 chainedCompleteHandler,
                 chainedIOErrorHandler,
                 chainedSecurityErrorHandler,
                 chainedRSLErrorHandler);    
        }
    }
    


    //--------------------------------------------------------------------------
    //
    //  Overridden Event Handlers
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    override public function itemCompleteHandler(event:Event):void
    {
        // complete loading the cross-domain rsl by calling loadBytes.
        completeCdRslLoad(event.target as URLLoader);
    }
    
    /**
     *  @private
     */
    override public function itemErrorHandler(event:ErrorEvent):void
    {
        // if a failover exists, try to load it. Otherwise call super()
        // for default error handling.
        if (hasFailover())
        {
            trace(decodeURI(event.text));
            loadFailover();
        }
        else 
        {
            super.itemErrorHandler(event);
        }
    }
    
    
    /**
     * loader.loadBytes() has a complete event.
     * Done loading this rsl into memory. Call the completeHandler
     * to start loading the next rsl.
     * 
     *  @private
     */ 
    private function loadBytesCompleteHandler(event:Event):void
    {
        super.itemCompleteHandler(event);           
    }
}
}