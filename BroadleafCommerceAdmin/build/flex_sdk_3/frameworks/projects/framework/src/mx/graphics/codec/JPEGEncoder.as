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
 *  The JPEGEncoder class converts raw bitmap images into encoded
 *  images using Joint Photographic Experts Group (JPEG) compression.
 *
 *  For information about the JPEG algorithm, see the document
 *  http://www.opennet.ru/docs/formats/jpeg.txt by Cristi Cuturicu.
 */
public class JPEGEncoder implements IImageEncoder
{
    include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static const CONTENT_TYPE:String = "image/jpeg";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param quality A value between 0.0 and 100.0. 
     *  The smaller the <code>quality</code> value, 
     *  the smaller the file size of the resultant image. 
     *  The value does not affect the encoding speed.
     *. Note that even though this value is a number between 0.0 and 100.0, 
     *  it does not represent a percentage. 
	 *  The default value is 50.0.
     */
    public function JPEGEncoder(quality:Number = 50.0)
    {
    	super();
    	
        if (quality <= 0.0)
            quality = 1.0;

        if (quality > 100.0)
            quality = 100.0;

        var sf:int = 0;
        if (quality < 50.0)
            sf = int(5000 / quality);
        else
            sf = int(200 - quality * 2);

        // Create tables
        initHuffmanTbl();
        initCategoryNumber();
        initQuantTables(sf);
    }

	//--------------------------------------------------------------------------
	//
	//  Constants
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 */
    private const std_dc_luminance_nrcodes:Array =
		[ 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 ];
    
    /**
	 *  @private
	 */
	private const std_dc_luminance_values:Array =
		[ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ];
    
    /**
	 *  @private
	 */
    private const std_dc_chrominance_nrcodes:Array =
		[ 0, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 ];
    
    /**
	 *  @private
	 */
	private const std_dc_chrominance_values:Array =
		[ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ];
    
    /**
	 *  @private
	 */
	private const std_ac_luminance_nrcodes:Array =
		[ 0, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 0x7D ];
    
    /**
	 *  @private
	 */
	private const std_ac_luminance_values:Array =
	[
        0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12,
        0x21, 0x31, 0x41, 0x06, 0x13, 0x51, 0x61, 0x07,
        0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xA1, 0x08,
        0x23, 0x42, 0xB1, 0xC1, 0x15, 0x52, 0xD1, 0xF0,
        0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0A, 0x16,
        0x17, 0x18, 0x19, 0x1A, 0x25, 0x26, 0x27, 0x28,
        0x29, 0x2A, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
        0x3A, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49,
        0x4A, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59,
        0x5A, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69,
        0x6A, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79,
        0x7A, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89,
        0x8A, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98,
        0x99, 0x9A, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7,
        0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6,
        0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5,
        0xC6, 0xC7, 0xC8, 0xC9, 0xCA, 0xD2, 0xD3, 0xD4,
        0xD5, 0xD6, 0xD7, 0xD8, 0xD9, 0xDA, 0xE1, 0xE2,
        0xE3, 0xE4, 0xE5, 0xE6, 0xE7, 0xE8, 0xE9, 0xEA,
        0xF1, 0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF8,
        0xF9, 0xFA
    ];

    /**
	 *  @private
	 */
	private const std_ac_chrominance_nrcodes:Array =
		[ 0, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 0x77 ];
    
    /**
	 *  @private
	 */
	private const std_ac_chrominance_values:Array =
	[
        0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21,
        0x31, 0x06, 0x12, 0x41, 0x51, 0x07, 0x61, 0x71,
        0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91,
        0xA1, 0xB1, 0xC1, 0x09, 0x23, 0x33, 0x52, 0xF0,
        0x15, 0x62, 0x72, 0xD1, 0x0A, 0x16, 0x24, 0x34,
        0xE1, 0x25, 0xF1, 0x17, 0x18, 0x19, 0x1A, 0x26,
        0x27, 0x28, 0x29, 0x2A, 0x35, 0x36, 0x37, 0x38,
        0x39, 0x3A, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48,
        0x49, 0x4A, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58,
        0x59, 0x5A, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
        0x69, 0x6A, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78,
        0x79, 0x7A, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87,
        0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95, 0x96,
        0x97, 0x98, 0x99, 0x9A, 0xA2, 0xA3, 0xA4, 0xA5,
        0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4,
        0xB5, 0xB6, 0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3,
        0xC4, 0xC5, 0xC6, 0xC7, 0xC8, 0xC9, 0xCA, 0xD2,
        0xD3, 0xD4, 0xD5, 0xD6, 0xD7, 0xD8, 0xD9, 0xDA,
        0xE2, 0xE3, 0xE4, 0xE5, 0xE6, 0xE7, 0xE8, 0xE9,
        0xEA, 0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF8,
        0xF9, 0xFA
    ];

    /**
	 *  @private
	 */
    private const ZigZag:Array =
	[
         0,  1,  5,  6, 14, 15, 27, 28,
         2,  4,  7, 13, 16, 26, 29, 42,
         3,  8, 12, 17, 25, 30, 41, 43,
         9, 11, 18, 24, 31, 40, 44, 53,
        10, 19, 23, 32, 39, 45, 52, 54,
        20, 22, 33, 38, 46, 51, 55, 60,
        21, 34, 37, 47, 50, 56, 59, 61,
        35, 36, 48, 49, 57, 58, 62, 63
    ];

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 *  Initialized by initHuffmanTbl() in constructor.
	 */
    private var YDC_HT:Array;

    /**
	 *  @private
	 *  Initialized by initHuffmanTbl() in constructor.
	 */
    private var UVDC_HT:Array;

    /**
	 *  @private
	 *  Initialized by initHuffmanTbl() in constructor.
	 */
    private var YAC_HT:Array;

    /**
	 *  @private
	 *  Initialized by initHuffmanTbl() in constructor.
	 */
    private var UVAC_HT:Array;

    /**
	 *  @private
	 *  Initialized by initCategoryNumber() in constructor.
	 */
	private var category:Array = new Array(65535);

    /**
	 *  @private
	 *  Initialized by initCategoryNumber() in constructor.
	 */
    private var bitcode:Array = new Array(65535);
    
    /**
	 *  @private
	 *  Initialized by initQuantTables() in constructor.
	 */
    private var YTable:Array = new Array(64);

    /**
	 *  @private
	 *  Initialized by initQuantTables() in constructor.
	 */
    private var UVTable:Array = new Array(64);

    /**
	 *  @private
	 *  Initialized by initQuantTables() in constructor.
	 */
    private var fdtbl_Y:Array = new Array(64);

    /**
	 *  @private
	 *  Initialized by initQuantTables() in constructor.
	 */
    private var fdtbl_UV:Array = new Array(64);

    /**
	 *  @private
	 *  The output ByteArray containing the encoded image data.
	 */
    private var byteout:ByteArray;

    /**
	 *  @private
	 */
    private var bytenew:int = 0;

    /**
	 *  @private
	 */
    private var bytepos:int = 7;

    /**
	 *  @private
	 */
    private var DU:Array = new Array(64);

    /**
	 *  @private
	 */
    private var YDU:Array = new Array(64);
    
    /**
	 *  @private
	 */
	private var UDU:Array = new Array(64);
    
    /**
	 *  @private
	 */
	private var VDU:Array = new Array(64);

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  contentType
	//----------------------------------

    /**
     *  The MIME type for the JPEG encoded image. 
     *  The value is <code>"image/jpeg"</code>.
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
     *  Converts the pixels of BitmapData object
	 *  to a JPEG-encoded ByteArray object.
     *
     *  @param bitmapData The input BitmapData object.
     *
     *  @return Returns a ByteArray object containing JPEG-encoded image data.
     */
    public function encode(bitmapData:BitmapData):ByteArray
    {
        return internalEncode(bitmapData, bitmapData.width, bitmapData.height,
							  bitmapData.transparent);
    }

    /**
     *  Converts a ByteArray object containing raw pixels
	 *  in 32-bit ARGB (Alpha, Red, Green, Blue) format
	 *  to a new JPEG-encoded ByteArray object. 
	 *  The original ByteArray is left unchanged.
	 *  Transparency is not supported; however you still must represent
	 *  each pixel as four bytes in ARGB format.
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
     *  @return Returns a ByteArray object containing JPEG-encoded image data. 
     */
    public function encodeByteArray(byteArray:ByteArray, width:int, height:int,
									transparent:Boolean = true):ByteArray
    {
        return internalEncode(byteArray, width, height, transparent);
    }

	//--------------------------------------------------------------------------
	//
	//  Methods: Initialization
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 *  Initializes the Huffman tables YDC_HT, UVDC_HT, YAC_HT, and UVAC_HT.
	 */
    private function initHuffmanTbl():void
    {
        YDC_HT = computeHuffmanTbl(std_dc_luminance_nrcodes,
								   std_dc_luminance_values);

        UVDC_HT = computeHuffmanTbl(std_dc_chrominance_nrcodes,
									std_dc_chrominance_values);

        YAC_HT = computeHuffmanTbl(std_ac_luminance_nrcodes,
								   std_ac_luminance_values);

        UVAC_HT = computeHuffmanTbl(std_ac_chrominance_nrcodes,
									std_ac_chrominance_values);
    }

    /**
	 *  @private
	 */
    private function computeHuffmanTbl(nrcodes:Array, std_table:Array):Array
    {
        var codevalue:int = 0;
        var pos_in_table:int = 0;
        
		var HT:Array = [];
        
		for (var k:int = 1; k <= 16; k++)
        {
            for (var j:int = 1; j <= nrcodes[k]; j++)
            {
                HT[std_table[pos_in_table]] = new BitString();
                HT[std_table[pos_in_table]].val = codevalue;
                HT[std_table[pos_in_table]].len = k;
                
				pos_in_table++;
				codevalue++;
            }

            codevalue *= 2;
        }

        return HT;
    }

    /**
	 *  @private
	 *  Initializes the category and bitcode arrays.
	 */
    private function initCategoryNumber():void
    {
        var nr:int;
        
		var nrlower:int = 1;
        var nrupper:int = 2;
        
		for (var cat:int = 1; cat <= 15; cat++)
        {
            // Positive numbers
            for (nr = nrlower; nr < nrupper; nr++)
            {
                category[32767 + nr] = cat;
                
				bitcode[32767 + nr] = new BitString();
                bitcode[32767 + nr].len = cat;
                bitcode[32767 + nr].val = nr;
            }

            // Negative numbers
            for (nr = -(nrupper - 1); nr <= -nrlower; nr++)
            {
                category[32767 + nr] = cat;
                
				bitcode[32767 + nr] = new BitString();
                bitcode[32767 + nr].len = cat;
                bitcode[32767 + nr].val = nrupper - 1 + nr;
            }

            nrlower <<= 1;
            nrupper <<= 1;
        }
    }

    /**
	 *  @private
	 *  Initializes YTable, UVTable, fdtbl_Y, and fdtbl_UV.
	 */
    private function initQuantTables(sf:int):void
    {
        var i:int = 0;
        var t:Number;

        var YQT:Array =
		[
            16, 11, 10, 16,  24,  40,  51,  61,
            12, 12, 14, 19,  26,  58,  60,  55,
            14, 13, 16, 24,  40,  57,  69,  56,
            14, 17, 22, 29,  51,  87,  80,  62,
            18, 22, 37, 56,  68, 109, 103,  77,
            24, 35, 55, 64,  81, 104, 113,  92,
            49, 64, 78, 87, 103, 121, 120, 101,
            72, 92, 95, 98, 112, 100, 103,  99
        ];

        for (i = 0; i < 64; i++)
		{
            t = Math.floor((YQT[i] * sf + 50)/100);
            if (t < 1)
                t = 1;
            else if (t > 255)
                t = 255;
            YTable[ZigZag[i]] = t;
        }

        var UVQT:Array =
		[
            17, 18, 24, 47, 99, 99, 99, 99,
            18, 21, 26, 66, 99, 99, 99, 99,
            24, 26, 56, 99, 99, 99, 99, 99,
            47, 66, 99, 99, 99, 99, 99, 99,
            99, 99, 99, 99, 99, 99, 99, 99,
            99, 99, 99, 99, 99, 99, 99, 99,
            99, 99, 99, 99, 99, 99, 99, 99,
            99, 99, 99, 99, 99, 99, 99, 99
        ];

        for (i = 0; i < 64; i++)
		{
            t = Math.floor((UVQT[i] * sf + 50) / 100);
            if (t < 1)
                t = 1;
            else if (t > 255)
                t = 255;
            UVTable[ZigZag[i]] = t;
        }

        var aasf:Array =
		[
            1.0, 1.387039845, 1.306562965, 1.175875602,
            1.0, 0.785694958, 0.541196100, 0.275899379
        ];

        i = 0;
        for (var row:int = 0; row < 8; row++)
        {
            for (var col:int = 0; col < 8; col++)
            {
                fdtbl_Y[i] =
					(1.0 / (YTable [ZigZag[i]] * aasf[row] * aasf[col] * 8.0));
                
				fdtbl_UV[i] =
					(1.0 / (UVTable[ZigZag[i]] * aasf[row] * aasf[col] * 8.0));
                
				i++;
            }
        }
    }

	//--------------------------------------------------------------------------
	//
	//  Methods: Core processing
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 */
	private function internalEncode(source:Object, width:int, height:int,
									transparent:Boolean = true):ByteArray
    {
    	// The source is either a BitmapData or a ByteArray.
    	var sourceBitmapData:BitmapData = source as BitmapData;
    	var sourceByteArray:ByteArray = source as ByteArray;
    	
        // Initialize bit writer
        byteout = new ByteArray();
        bytenew = 0;
        bytepos = 7;

        // Add JPEG headers
        writeWord(0xFFD8); // SOI
        writeAPP0();
        writeDQT();
        writeSOF0(width, height);
        writeDHT();
        writeSOS();

        // Encode 8x8 macroblocks
        var DCY:Number = 0;
        var DCU:Number = 0;
        var DCV:Number = 0;
        bytenew = 0;
        bytepos = 7;

        for (var ypos:int = 0; ypos < height; ypos += 8)
        {
            for (var xpos:int = 0; xpos < width; xpos += 8)
            {
                RGB2YUV(sourceBitmapData, sourceByteArray, xpos, ypos, width, height);
                
				DCY = processDU(YDU, fdtbl_Y, DCY, YDC_HT, YAC_HT);
                DCU = processDU(UDU, fdtbl_UV, DCU, UVDC_HT, UVAC_HT);
                DCV = processDU(VDU, fdtbl_UV, DCV, UVDC_HT, UVAC_HT);
            }
        }

        // Do the bit alignment of the EOI marker
        if (bytepos >= 0)
        {
            var fillbits:BitString = new BitString();
            fillbits.len = bytepos + 1;
            fillbits.val = (1 << (bytepos + 1)) - 1;
            writeBits(fillbits);
        }

        // Add EOI
		writeWord(0xFFD9);
        
		return byteout;
    }

    /**
	 *  @private
	 */
    private function RGB2YUV(sourceBitmapData:BitmapData,
							 sourceByteArray:ByteArray,
							 xpos:int, ypos:int,
							 width:int, height:int):void
    {
        var k:int = 0; // index into 64-element block arrays
        
		for (var j:int = 0; j < 8; j++)
        {
	        var y:int = ypos + j;
	        if (y >= height)
	        	y = height - 1;

            for (var i:int = 0; i < 8; i++)
            {
		        var x:int = xpos + i;
		        if (x >= width)
		        	x = width - 1;

		        var pixel:uint;
		        if (sourceBitmapData)
		        {
		            pixel = sourceBitmapData.getPixel32(x, y);
		        }
		        else
		        {
		            sourceByteArray.position = 4 * (y * width + x);
		            pixel = sourceByteArray.readUnsignedInt();
		        }

                var r:Number = Number((pixel >> 16) & 0xFF);
                var g:Number = Number((pixel >> 8) & 0xFF);
                var b:Number = Number(pixel & 0xFF);

                YDU[k] =  0.29900 * r + 0.58700 * g + 0.11400 * b - 128.0;
                UDU[k] = -0.16874 * r - 0.33126 * g + 0.50000 * b;
                VDU[k] =  0.50000 * r - 0.41869 * g - 0.08131 * b;
                
				k++;
            }
        }
    }

    /**
	 *  @private
	 */
    private function processDU(CDU:Array, fdtbl:Array, DC:Number,
							   HTDC:Array, HTAC:Array):Number
    {
        var EOB:BitString = HTAC[0x00];
        var M16zeroes:BitString = HTAC[0xF0];
        var i:int;

        var DU_DCT:Array = fDCTQuant(CDU, fdtbl);
        
		// ZigZag reorder
        for (i = 0; i < 64; i++)
        {
            DU[ZigZag[i]] = DU_DCT[i];
        }

        var Diff:int = DU[0] - DC;
		DC = DU[0];
        
		// Encode DC
        if (Diff == 0)
        {
            writeBits(HTDC[0]); // Diff might be 0
        }
        else
        {
            writeBits(HTDC[category[32767 + Diff]]);
            writeBits(bitcode[32767 + Diff]);
        }
        
		// Encode ACs
        var end0pos:int = 63;
        for (; (end0pos > 0) && (DU[end0pos] == 0); end0pos--)
        {
        };

        // end0pos = first element in reverse order != 0
        if (end0pos == 0)
        {
            writeBits(EOB);
            return DC;
        }

        i = 1;
        while (i <= end0pos)
        {
            var startpos:int = i;
            for (; (DU[i] == 0) && (i <= end0pos); i++)
            {
            }
            var nrzeroes:int = i - startpos;

            if (nrzeroes >= 16)
            {
                for (var nrmarker:int = 1; nrmarker <= nrzeroes / 16; nrmarker++)
                {
                    writeBits(M16zeroes);
                }
                nrzeroes = int(nrzeroes & 0xF);
            }

            writeBits(HTAC[nrzeroes * 16 + category[32767 + DU[i]]]);
            writeBits(bitcode[32767 + DU[i]]);
            
			i++;
        }

        if (end0pos != 63)
            writeBits(EOB);

        return DC;
    }

    /**
	 *  @private
	 */
	private function fDCTQuant(data:Array, fdtbl:Array):Array
    {
        // Pass 1: process rows.
        var dataOff:int = 0;
        var i:int;
        for (i = 0; i < 8; i++)
        {
            var tmp0:Number = data[dataOff + 0] + data[dataOff + 7];
            var tmp7:Number = data[dataOff + 0] - data[dataOff + 7];
            var tmp1:Number = data[dataOff + 1] + data[dataOff + 6];
            var tmp6:Number = data[dataOff + 1] - data[dataOff + 6];
            var tmp2:Number = data[dataOff + 2] + data[dataOff + 5];
            var tmp5:Number = data[dataOff + 2] - data[dataOff + 5];
            var tmp3:Number = data[dataOff + 3] + data[dataOff + 4];
            var tmp4:Number = data[dataOff + 3] - data[dataOff + 4];

            // Even part
            var tmp10:Number = tmp0 + tmp3;	// phase 2
            var tmp13:Number = tmp0 - tmp3;
            var tmp11:Number = tmp1 + tmp2;
            var tmp12:Number = tmp1 - tmp2;

            data[dataOff + 0] = tmp10 + tmp11; // phase 3
            data[dataOff + 4] = tmp10 - tmp11;

            var z1:Number = (tmp12 + tmp13) * 0.707106781; // c4
            data[dataOff + 2] = tmp13 + z1; // phase 5
            data[dataOff + 6] = tmp13 - z1;

            // Odd part
            tmp10 = tmp4 + tmp5; // phase 2
            tmp11 = tmp5 + tmp6;
            tmp12 = tmp6 + tmp7;

            // The rotator is modified from fig 4-8 to avoid extra negations.
            var z5:Number = (tmp10 - tmp12) * 0.382683433; // c6
            var z2:Number = 0.541196100 * tmp10 + z5; // c2 - c6
            var z4:Number = 1.306562965 * tmp12 + z5; // c2 + c6
            var z3:Number = tmp11 * 0.707106781; // c4

            var z11:Number = tmp7 + z3; // phase 5
            var z13:Number = tmp7 - z3;

            data[dataOff + 5] = z13 + z2; // phase 6
            data[dataOff + 3] = z13 - z2;
            data[dataOff + 1] = z11 + z4;
            data[dataOff + 7] = z11 - z4;

            dataOff += 8; // advance pointer to next row
        }

        // Pass 2: process columns.
        dataOff = 0;
        for (i = 0; i < 8; i++)
        {
            tmp0 = data[dataOff +  0] + data[dataOff + 56];
            tmp7 = data[dataOff +  0] - data[dataOff + 56];
            tmp1 = data[dataOff +  8] + data[dataOff + 48];
            tmp6 = data[dataOff +  8] - data[dataOff + 48];
            tmp2 = data[dataOff + 16] + data[dataOff + 40];
            tmp5 = data[dataOff + 16] - data[dataOff + 40];
            tmp3 = data[dataOff + 24] + data[dataOff + 32];
            tmp4 = data[dataOff + 24] - data[dataOff + 32];

            // Even par
            tmp10 = tmp0 + tmp3; // phase 2
            tmp13 = tmp0 - tmp3;
            tmp11 = tmp1 + tmp2;
            tmp12 = tmp1 - tmp2;

            data[dataOff +  0] = tmp10 + tmp11; // phase 3
            data[dataOff + 32] = tmp10 - tmp11;

            z1 = (tmp12 + tmp13) * 0.707106781; // c4
            data[dataOff + 16] = tmp13 + z1; // phase 5
            data[dataOff + 48] = tmp13 - z1;

            // Odd part
            tmp10 = tmp4 + tmp5; // phase 2
            tmp11 = tmp5 + tmp6;
            tmp12 = tmp6 + tmp7;

            // The rotator is modified from fig 4-8 to avoid extra negations.
            z5 = (tmp10 - tmp12) * 0.382683433; // c6
            z2 = 0.541196100 * tmp10 + z5; // c2 - c6
            z4 = 1.306562965 * tmp12 + z5; // c2 + c6
            z3 = tmp11 * 0.707106781; // c4

            z11 = tmp7 + z3; // phase 5 */
            z13 = tmp7 - z3;

            data[dataOff + 40] = z13 + z2; // phase 6
            data[dataOff + 24] = z13 - z2;
            data[dataOff +  8] = z11 + z4;
            data[dataOff + 56] = z11 - z4;

            dataOff++; // advance pointer to next column
        }

        // Quantize/descale the coefficients
        for (i = 0; i < 64; i++)
        {
            // Apply the quantization and scaling factor
			// and round to nearest integer
            data[i] = Math.round((data[i] * fdtbl[i]));
        }

        return data;
    }

	//--------------------------------------------------------------------------
	//
	//  Methods: Output
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 */
    private function writeBits(bs:BitString):void
    {
        var value:int = bs.val;
        var posval:int = bs.len - 1;
        while (posval >= 0)
        {
            if (value & uint(1 << posval) )
            {
                bytenew |= uint(1 << bytepos);
            }
            posval--;
            bytepos--;
            if (bytepos < 0)
            {
                if (bytenew == 0xFF)
                {
                    writeByte(0xFF);
                    writeByte(0);
                }
                else
                {
                    writeByte(bytenew);
                }
                bytepos = 7;
                bytenew = 0;
            }
        }
    }

    /**
	 *  @private
	 */
    private function writeByte(value:int):void
    {
        byteout.writeByte(value);
    }

    /**
	 *  @private
	 */
    private function writeWord(value:int):void
    {
        writeByte((value >> 8) & 0xFF);
        writeByte(value & 0xFF);
    }

    /**
	 *  @private
	 */
    private function writeAPP0():void
    {
        writeWord(0xFFE0);	// marker
        writeWord(16);		// length
        writeByte(0x4A);	// J
        writeByte(0x46);	// F
        writeByte(0x49);	// I
        writeByte(0x46);	// F
        writeByte(0);		// = "JFIF",'\0'
        writeByte(1);		// versionhi
        writeByte(1);		// versionlo
        writeByte(0);		// xyunits
        writeWord(1);		// xdensity
        writeWord(1);		// ydensity
        writeByte(0);		// thumbnwidth
        writeByte(0);		// thumbnheight
    }

    /**
	 *  @private
	 */
    private function writeDQT():void
    {
        writeWord(0xFFDB);	// marker
        writeWord(132);     // length
        writeByte(0);
        var i:int;

        for (i = 0; i < 64; i++)
        {
            writeByte(YTable[i]);
        }

        writeByte(1);

        for (i = 0; i < 64; i++)
        {
            writeByte(UVTable[i]);
        }
    }

    /**
	 *  @private
	 */
    private function writeSOF0(width:int, height:int):void
    {
        writeWord(0xFFC0);	// marker
        writeWord(17);		// length, truecolor YUV JPG
        writeByte(8);		// precision
        writeWord(height);
        writeWord(width);
        writeByte(3);		// nrofcomponents
        writeByte(1);		// IdY
        writeByte(0x11);	// HVY
        writeByte(0);		// QTY
        writeByte(2);		// IdU
        writeByte(0x11);	// HVU
        writeByte(1);		// QTU
        writeByte(3);		// IdV
        writeByte(0x11);	// HVV
        writeByte(1);		// QTV
    }

    /**
	 *  @private
	 */
    private function writeDHT():void
    {
        var i:int;

        writeWord(0xFFC4); // marker
        writeWord(0x01A2); // length

        writeByte(0); // HTYDCinfo
        for (i = 0; i < 16; i++)
        {
            writeByte(std_dc_luminance_nrcodes[i + 1]);
        }
        for (i = 0; i <= 11; i++)
        {
            writeByte(std_dc_luminance_values[i]);
        }

        writeByte(0x10); // HTYACinfo
        for (i = 0; i < 16; i++)
        {
            writeByte(std_ac_luminance_nrcodes[i + 1]);
        }
        for (i = 0; i <= 161; i++)
        {
            writeByte(std_ac_luminance_values[i]);
        }

        writeByte(1); // HTUDCinfo
        for (i = 0; i < 16; i++)
        {
            writeByte(std_dc_chrominance_nrcodes[i + 1]);
        }
        for (i = 0; i <= 11; i++)
        {
            writeByte(std_dc_chrominance_values[i]);
        }

        writeByte(0x11); // HTUACinfo
        for (i = 0; i < 16; i++)
        {
            writeByte(std_ac_chrominance_nrcodes[i + 1]);
        }
        for (i = 0; i <= 161; i++)
        {
            writeByte(std_ac_chrominance_values[i]);
        }
    }

    /**
	 *  @private
	 */
    private function writeSOS():void
    {
        writeWord(0xFFDA);	// marker
        writeWord(12);		// length
        writeByte(3);		// nrofcomponents
        writeByte(1);		// IdY
        writeByte(0);		// HTY
        writeByte(2);		// IdU
        writeByte(0x11);	// HTU
        writeByte(3);		// IdV
        writeByte(0x11);	// HTV
        writeByte(0);		// Ss
        writeByte(0x3f);	// Se
        writeByte(0);		// Bf
    }
}

}

class BitString
{
	/**
     *  Constructor.
     */
    public function BitString()
    {
    	super();	
    }
    
	/**
     *  @private
     */
    public var len:int = 0;

	/**
     *  @private
     */
    public var val:int = 0;
}
