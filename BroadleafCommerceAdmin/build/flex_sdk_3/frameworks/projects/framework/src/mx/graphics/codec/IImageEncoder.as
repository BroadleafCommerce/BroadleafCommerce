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
 *  The IImageEncoder interface defines the interface
 *  that image encoders implement to take BitmapData objects,
 *  or ByteArrays containing raw ARGB pixels, as input
 *  and convert them to popular image formats such as PNG or JPEG.
 * 
 *  @see PNGEncoder
 *  @see JPEGEncoder
 */
public interface IImageEncoder
{
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  contentType
	//----------------------------------

    /**
     *  The MIME type for the image format that this encoder produces.
     */
    function get contentType():String;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Encodes a BitmapData object as a ByteArray.
     *
     *  @param bitmapData The input BitmapData object.
     *
     *  @return Returns a ByteArray object containing encoded image data. 
     */
    function encode(bitmapData:BitmapData):ByteArray;

    /**
     *  Encodes a ByteArray object containing raw pixels
	 *  in 32-bit ARGB (Alpha, Red, Green, Blue) format
	 *  as a new ByteArray object containing encoded image data.
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
     *  @param transparent If <code>false</code>,
	 *  alpha channel information is ignored.
     *
     *  @return Returns a ByteArray object containing encoded image data.
     */
    function encodeByteArray(byteArray:ByteArray, width:int, height:int,
							 transparent:Boolean = true):ByteArray;
}

}
