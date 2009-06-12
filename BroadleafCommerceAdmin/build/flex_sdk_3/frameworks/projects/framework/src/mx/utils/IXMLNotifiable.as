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

package mx.utils
{

[ExcludeClass]

/**
 *  @private
 */
public interface IXMLNotifiable
{
    function xmlNotification(currentTarget:Object,
                             type:String,
                             target:Object,
                             value:Object,
                             detail:Object):void;
}

}