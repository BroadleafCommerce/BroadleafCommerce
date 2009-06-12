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
 *  The IProgrammaticSkin interface defines the interface that skin classes must implement 
 *  if they use the <code>name</code> property skin interface. 
 */
public interface IProgrammaticSkin
{
    /**
     *  @copy mx.skins.ProgrammaticSkin#validateNow()
     */
    function validateNow():void;

    /**
     *  @copy mx.skins.ProgrammaticSkin#validateDisplayList()
     */
    function validateDisplayList():void;
}

}