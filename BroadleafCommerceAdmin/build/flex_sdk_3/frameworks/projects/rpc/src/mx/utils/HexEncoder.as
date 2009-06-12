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

package mx.utils
{

import flash.utils.ByteArray;


[ExcludeClass]

/**
 *  Documentation is not currently available.
 *  Ported to ActionScript from flex/messaging/util/Hex.java
 *  @private
 */
public class HexEncoder
{
    /**
     *  @private
     *  Set encodingStyle to this value to encode using upper case 'A'-'F'.
     */ 
    public static const UPPER_CASE:String = "upper";
    
    /**
     *  @private
     *  Set encodingStyle to this value to encode using lower case 'a'-'f'. 
     */
    public static const LOWER_CASE:String = "lower";
    
    /**
     *  @private
     *  The default encoding style for all HexEncoders.
     */
    public static var encodingStyle:String = UPPER_CASE;
    
    /**
     *  @private
     *  Constructor.
     */
    public function HexEncoder()
    {
        super();
        _buffers = [];
        _buffers.push([]);
    }

    /**
     *  @private
     *  The encoding style for this HexEncoder instance.
     *  If not set, the default static encodingStyle is used.
     */
    public var encodingStyle:String;

    /**
     *  @private
     */
    public function encode(data:ByteArray, offset:uint = 0, length:uint = 0):void
    {
        if (length == 0)
            length = data.length;

        if (offset < length)
        {
            data.position = offset;
        }
        
        var style:String = (this.encodingStyle != null) ? this.encodingStyle : HexEncoder.encodingStyle;
        // Validate style; coerce invalid values to UPPER.
        if (style != UPPER_CASE && style != LOWER_CASE)
            style = UPPER_CASE;

        var digits:Array = (style == UPPER_CASE) ? UPPER_CHAR_CODES : LOWER_CHAR_CODES;

        while (data.bytesAvailable > 0)
        {
            encodeBlock(data.readByte(), digits);
        } // while
    }

    /**
     *  @private
     */
    public function drain():String
    {
        var result:String = "";

        for (var i:uint = 0; i < _buffers.length; i++)
        {
            var buffer:Array = _buffers[i] as Array;
            result += String.fromCharCode.apply(null, buffer);
        }

        _buffers = [];
        _buffers.push([]);

        return result;
    }

    /**
     *  @private
     */
    public function flush():String
    {
        return drain();
    }

    /**
     *  @private
     */
    private function encodeBlock(_work:int, digits:Array):void
    {
        var currentBuffer:Array = _buffers[_buffers.length - 1] as Array;
        if (currentBuffer.length >= MAX_BUFFER_SIZE)
        {
            currentBuffer = [];
            _buffers.push(currentBuffer);
        }

        currentBuffer.push(digits[(_work & 0xF0) >>> 4]);
        currentBuffer.push(digits[(_work & 0x0F)]);
    }

    /**
     *  An Array of buffer Arrays. 
     *  @private
     */
    private var _buffers:Array;

    /**
     *  @private
     */
    private var _work:int = 0;

    /**
     * This value represents a safe number of characters (i.e. arguments) that
     * can be passed to String.fromCharCode.apply() without exceeding the AVM+
     * stack limit.
     * 
     * @private
     */
    public static const MAX_BUFFER_SIZE:uint = 32767;

    /*
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
     */
    private static const UPPER_CHAR_CODES:Array =
    [
        48, 49, 50, 51, 52, 53, 54, 55,
        56, 57, 65, 66, 67, 68, 69, 70,
    ];

    /*
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
     */
    private static const LOWER_CHAR_CODES:Array = 
    [
        48, 49, 50, 51, 52, 53, 54, 55,
        56, 57, 97, 98, 99, 100, 101, 102
    ];

}

}
