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

/**
 *  The IStateClient interface defines the interface that 
 *  components must implement to support view states.
 */
public interface IStateClient
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  currentState
    //----------------------------------

    /**
     *  The current view state.
     */
    function get currentState():String;
    
    /**
     *  @private
     */
    function set currentState(value:String):void;
}

}
