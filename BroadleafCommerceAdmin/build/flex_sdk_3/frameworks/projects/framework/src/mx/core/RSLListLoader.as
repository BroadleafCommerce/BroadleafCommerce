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

import flash.events.IEventDispatcher;
import flash.events.Event;

[ExcludeClass]

/**
 *  @private
 *  Utility class for loading a list of RSLs.
 *
 *  <p>A list of cross-domain RSLs and a list of regular RSLs
 *  can be loaded using this utility.</p>
 */
public class RSLListLoader
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
     *  @param rslList Array of RSLs to load.
	 *  Each entry in the array is of type RSLItem or CdRSLItem.
     *  The RSLs will be loaded from index 0 to the end of the array.
     */
    public function RSLListLoader(rslList:Array) 
    {
    	super();
        this.rslList = rslList;
    }
    
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 *  The index of the RSL being loaded.
	 */
	private var currentIndex:int = 0;
    
    /**
	 *  @private
	 *  The list of RSLs to load.
	 *  Each entry is of type RSLNode or CdRSLNode.
	 */
	private var rslList:Array = [];
    
    /**
	 *  @private
	 *  Supplied by caller.
	 */
	private var chainedProgressHandler:Function;
    
    /**
	 *  @private
	 *  Supplied by caller.
	 */
	private var chainedCompleteHandler:Function;
    
    /**
	 *  @private
	 *  Supplied by caller.
	 */
	private var chainedIOErrorHandler:Function;
    
    /**
	 *  @private
	 *  Supplied by caller.
	 */
	private var chainedSecurityErrorHandler:Function;
    
    /**
	 *  @private
	 *  Supplied by caller.
	 */
	private var chainedRSLErrorHandler:Function;
 
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Loads the RSLs in this list object.
     * 
     *  <p>This function accepts listeners to observe events
	 *  that result from loading an RSL.
     *  The function must be of the form required by a listener function 
     *  passed to <code>IEventDispatcher.addEventListener();</code>
     * 
     *  When an event is received, the RSL that generated
	 *  this event can be found by calling
	 *  <code>rslListLoader.getItem(getIndex());</code></p>
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
	    
	    currentIndex = -1; // so first loaded item will be index 0
	    loadNext();
         
    }
    
    /**
     *  Increments the current index and loads the next RSL.
     */
    private function loadNext():void
    {
        if (!isDone())
        {
            currentIndex++;

	        // Load the current RSL.
	        if (currentIndex < rslList.length)
	        {
	            // Load rsl and have the RSL loader chain the
	            // events our internal events handler or the chained
	            // events handler if we don't care about them.
	            rslList[currentIndex].load(chainedProgressHandler,
	                                       listCompleteHandler,
	                                       listIOErrorHandler,
	                                       listSecurityErrorHandler,
	                                       chainedRSLErrorHandler);
	        }
        }
    }
    
    
    /**
     *  Gets an RSL from the list of RSLs.
     * 
     *  @param index A zero-based index into the list of RSLs.
     * 
     *  @return The current item at <code>index</code> in the list,
	 *  or <code>null</code> if there is no item at that index.
     */
    public function getItem(index:int):RSLItem
    {
        if (index < 0 || index >= rslList.length)
            return null;
        
        return rslList[index];    
    }
    
    
    /**
     *  Gets the index of the currently loading RSL.
     * 
     *  @return The index of the currently loading RSL.
     */
    public function getIndex():int
    {
        return currentIndex;
    }
    
    
   /**
    *  Gets the total number of RSLs in this object.
	*  When the load() method is called the RSLs will be loaded.
    *  
    *  @return The total number of RSLs in this object
    */
    public function getItemCount():int
    {
        return rslList.length;    
    }
    
    /**
     *  Tests if all the RSLs are done loading.
     * 
     *  @return <code>true</code> if all the RSLs have been loaded,
	 *  <code>false</code> otherwise.
     */
    public function isDone():Boolean
    {
        return currentIndex >= rslList.length;
    }
   	
	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 */
	private function listCompleteHandler(event:Event):void
    {
        // Pass event to external listener.
        if (chainedCompleteHandler != null)
            chainedCompleteHandler(event);

        // Load the next RSL.
        loadNext();
    }
    
    /**
	 *  @private
	 */
    private function listIOErrorHandler(event:Event):void
    {
        // Pass event to external listener.
        if (chainedIOErrorHandler != null)
            chainedIOErrorHandler(event);
    }

    /**
	 *  @private
	 */
    private function listSecurityErrorHandler(event:Event):void
    {
        // Pass event to external listener.
        if (chainedSecurityErrorHandler != null)
            chainedSecurityErrorHandler(event);
    }    
}

}
