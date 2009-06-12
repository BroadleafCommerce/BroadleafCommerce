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

package mx.messaging.channels.amfx
{

[ExcludeClass]

/**
 * An AMFX request or response packet can contain headers.
 *
 * A Header must have a name, can be marked with a mustUnderstand
 * boolean flag (the default is false), and the content can be any
 * Object.
 * @private
 */
public class AMFXHeader
{
    public var name:String;
    public var mustUnderstand:Boolean;
    public var content:Object;

    public function AMFXHeader()
    {
        super();
    }
}

}
