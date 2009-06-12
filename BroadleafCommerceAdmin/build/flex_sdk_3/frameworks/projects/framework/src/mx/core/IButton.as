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
 *  The IButton interface is a marker interface that indicates that a component
 *  acts as a button.
 */
public interface IButton extends IUIComponent
{
    /**
     *  @copy mx.controls.Button#emphasized
     */
    function get emphasized():Boolean;
    function set emphasized(value:Boolean):void;

    /**
     *  @copy mx.core.UIComponent#callLater()
     */
    function callLater(method:Function,
                              args:Array /* of Object */ = null):void
}

}
