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

package mx.rpc.xml
{

[ExcludeClass]

/**
 * This internal utility class is used by XMLDecoder, to store some properties
 * relevant to the current context, such as which is the current element from
 * an XMLList of values, which elements were deserialized by <any> definitions, etc.
 *
 * @private
 */
public class DecodingContext
{
    
    public function DecodingContext() {}
    
    public var index:int = 0;
    
    public var hasContextSiblings:Boolean = false;

    public var anyIndex:int = -1;
}

}