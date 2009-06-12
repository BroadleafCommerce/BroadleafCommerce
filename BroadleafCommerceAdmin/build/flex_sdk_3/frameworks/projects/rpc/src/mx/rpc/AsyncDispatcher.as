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

package mx.rpc
{

import flash.events.TimerEvent;
import flash.events.TimerEvent;
import flash.utils.Timer;

[ExcludeClass]
/**
 *  This class provides a mechanism for dispatching a method asynchronously.
 *  @private
 */
public class AsyncDispatcher
{
    /**
     *  @private
     */
    public function AsyncDispatcher(method:Function, args:Array, delay:Number)
    {
        super();
        _method = method;
        _args = args;
        _timer = new Timer(delay);
        _timer.addEventListener(TimerEvent.TIMER, timerEventHandler);
        _timer.start();
    }

    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------

    private function timerEventHandler(event:TimerEvent):void
    {
        _timer.stop();
        _timer.removeEventListener(TimerEvent.TIMER, timerEventHandler);
        // This call may throw so do not put code after it
        _method.apply(null, _args);
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    private var _method:Function;
    private var _args:Array;
    private var _timer:Timer;
}

}
