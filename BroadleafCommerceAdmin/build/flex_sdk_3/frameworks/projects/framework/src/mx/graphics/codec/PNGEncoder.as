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

package mx.graphics.codec
{

import flash.display.BitmapData;
import flash.utils.ByteArray;

/**
 *  The PNGEncoder class converts raw bitmap images into encoded
 *  images using Portable Network Graphics (PNG) lossless compression.
 *
 *  <p>For the PNG specification, see http://www.w3.org/TR/PNG/</p>.
 */
public class PNGEncoder implements IImageEncoder
{
    include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
	 *  The MIME type for a PNG image.
     */
    private static const CONTENT_TYPE:String = "image/png";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function PNGEncoder()
    {
    	super();

		initializeCRCTable();
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
	 *  Used for computing the cyclic redundancy checksum
	 *  at the end of each chunk.
     */
    private var crcTable:Array;
    
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  contentType
	//----------------------------------

    /**
     *  The MIME type for the PNG encoded image.
     *  The value is <code>"image/png"</code>.
     */
    public function get contentType():String
    {
        return CONTENT_TYPE;
    }

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Converts the pixels of a BitmapData object
	 *  to a PNG-encoded ByteArray object.
     *
     *  @param bitmapData The input BitmapData object.
     *
     *  @return Returns a ByteArray object containing PNG-encoded image data.
     */
    public function encode(bitmapData:BitmapData):ByteArray
    {
        return internalEncode(bitmapData, bitmapData.width, bitmapData.height,
							  bitmapData.transparent);
    }

    /**
     *  Converts a ByteArray object containing raw pixels
	 *  in 32-bit ARGB (Alpha, Red, Green, Blue) format
	 *  to a new PNG-encoded ByteArray object.
	 *  The original ByteArray is left unchanged.
     *
     *  @param byteArray The input ByteArray object containing raw pixels.
	 *  This ByteArray should contain
	 *  <code>4 * width * height</code> bytes.
	 *  Each pixel is represented by 4 bytes, in the order ARGB.
	 *  The first four bytes represent the top-left pixel of the image.
	 *  The next four bytes represent the pixel to its right, etc.
	 *  Each row follows the previous one without any padding.
     *
     *  @param width The width of the input image, in pixels.
     *
     *  @param height The height of the input image, in pixels.
     *
     *  @param transparent If <code>false</code>, alpha channel information
	 *  is ignored but you still must represent each pixel 
     *  as four bytes in ARGB format.
     *
     *  @return Returns a ByteArray object containing PNG-encoded image data. 
     */
    public function encodeByteArray(byteArray:ByteArray, width:int, height:int,
									transparent:Boolean = true):ByteArray
    {
        return internalEncode(byteArray, width, height, transparent);
    }

    /**
	 *  @private
	 */
	private function initializeCRCTable():void
	{
        crcTable = [];

        for (var n:uint = 0; n < 256; n++)
        {
            var c:uint = n;
            for (var k:uint = 0; k < 8; k++)
            {
                if (c & 1)
                    c = uint(uint(0xedb88320) ^ uint(c >>> 1));
				else
                    c = uint(c >>> 1);
             }
            crcTable[n] = c;
        }
	}

    /**
	 *  @private
	 */
	private function internalEncode(source:Object, width:int, height:int,
									transparent:Boolean = true):ByteArray
    {
     	// The source is either a BitmapData or a ByteArray.
    	var sourceBitmapData:BitmapData = source as BitmapData;
    	var sourceByteArray:ByteArray = source as ByteArray;
    	
    	if (sourceByteArray)
    		sourceByteArray.position = 0;
    	
        // Create output byte array
        var png:ByteArray = new ByteArray();

        // Write PNG signature
        png.writeUnsignedInt(0x89504E47);
        png.writeUnsignedInt(0x0D0A1A0A);

        // Build IHDR chunk
        var IHDR:ByteArray = new ByteArray();
        IHDR.writeInt(width);
        IHDR.writeInt(height);
		IHDR.writeByte(8); // bit depth per channel
		IHDR.writeByte(6); // color type: RGBA
		IHDR.writeByte(0); // compression method
		IHDR.writeByte(0); // filter method
        IHDR.writeByte(0); // interlace method
        writeChunk(png, 0x49484452, IHDR);

        // Build IDAT chunk
        var IDAT:ByteArray = new ByteArray();
        for (var y:int = 0; y < height; y++)
        {
            IDAT.writeByte(0); // no filter

            var x:int;
            var pixel:uint;
            
			if (!transparent)
            {
                for (x = 0; x < width; x++)
                {
                    if (sourceBitmapData)
                    	pixel = sourceBitmapData.getPixel(x, y);
                   	else
             			pixel = sourceByteArray.readUnsignedInt();
					
					IDAT.writeUnsignedInt(uint(((pixel & 0xFFFFFF) << 8) | 0xFF));
                }
            }
            else
            {
                for (x = 0; x < width; x++)
                {
                    if (sourceBitmapData)
                   		pixel = sourceBitmapData.getPixel32(x, y);
                    else
						pixel = sourceByteArray.readUnsignedInt();
 
                    IDAT.writeUnsignedInt(uint(((pixel & 0xFFFFFF) << 8) |
												(pixel >>> 24)));
                }
            }
        }
        IDAT.compress();
        writeChunk(png, 0x49444154, IDAT);

        // Build IEND chunk
        writeChunk(png, 0x49454E44, null);

        // return PNG
        png.position = 0;
        return png;
    }

    /**
	 *  @private
	 */
	private function writeChunk(png:ByteArray, type:uint, data:ByteArray):void
    {
        // Write length of data.
        var len:uint = 0;
        if (data)
            len = data.length;
		png.writeUnsignedInt(len);
        
		// Write chunk type.
		var typePos:uint = png.position;
		png.writeUnsignedInt(type);
        
		// Write data.
		if (data)
            png.writeBytes(data);

        // Write CRC of chunk type and data.
		var crcPos:uint = png.position;
        png.position = typePos;
        var crc:uint = 0xFFFFFFFF;
        for (var i:uint = typePos; i < crcPos; i++)
        {
            crc = uint(crcTable[(crc ^ png.readUnsignedByte()) & uint(0xFF)] ^
					   uint(crc >>> 8));
        }
        crc = uint(crc ^ uint(0xFFFFFFFF));
        png.position = crcPos;
        png.writeUnsignedInt(crc);
    }
}

}
