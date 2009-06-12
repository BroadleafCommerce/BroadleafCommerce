////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls.colorPickerClasses
{

import mx.collections.ArrayList;
import mx.collections.IList;

[ExcludeClass]

/**
 *  @private
 */
public class WebSafePalette
{
    include "../../core/Version.as";

	//--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function WebSafePalette()
    {
		super();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    public function getList():IList /* of Number */
    {
		// Dynamically generate the default websafe color palette.
        var dp:IList = new ArrayList();
        
		var n:Number = 0;
        
		var spacer:Number = 0x000000;
        
		var c1:Array = [ 0x000000, 0x333333, 0x666666, 0x999999,
						 0xCCCCCC, 0xFFFFFF, 0xFF0000, 0x00FF00,
						 0x0000FF, 0xFFFF00, 0x00FFFF, 0xFF00FF ];
        
		var ra:Array = [ "00", "00", "00", "00", "00", "00",
						 "33", "33", "33", "33", "33", "33",
						 "66", "66", "66", "66", "66", "66" ];
        
		var rb:Array = [ "99", "99", "99", "99", "99", "99",
						 "CC", "CC", "CC", "CC", "CC", "CC",
						 "FF", "FF", "FF", "FF", "FF", "FF" ];
        
		var g:Array = [ "00", "33", "66", "99", "CC", "FF",
						"00", "33", "66", "99", "CC", "FF",
						"00", "33", "66", "99", "CC", "FF" ];
        
		var b:Array = [ "00", "33", "66", "99", "CC", "FF",
						"00", "33", "66", "99", "CC", "FF" ];

        for (var x:int = 0; x < 12; x++)
        {
            for (var j:int = 0; j < 20; j++)
            {
                var item:Number;
                
				if (j == 0)
                {
                    item = c1[x];
                    
                }
                else if (j == 1)
                {
                    item = spacer;
                }
                else
                {
                    var r:String;
                    if (x < 6)
                        r = ra[j - 2];
                    else
                        r = rb[j - 2];
                    item = Number("0x" + r + g[j - 2] + b[x]);
                }
                
				dp.addItem(item);
                n++;
            }
        }

        return dp;
    }
}

}
