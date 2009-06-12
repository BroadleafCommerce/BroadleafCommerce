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

package mx.utils
{

import flash.display.LoaderInfo;
import flash.system.Security;

  /**
   *  The LoaderUtil class defines a utility method for use with Flex RSLs.
   */
    public class LoaderUtil
    {
    /**
     *  The root URL of a cross-domain RSL contains special text 
     *  appended to the end of the URL. 
     *  This method normalizes the URL specified in the specified LoaderInfo instance 
     *  to remove the appended text, if present. 
     *  Classes accessing <code>LoaderInfo.url</code> should call this method 
     *  to normalize the URL before using it.
     *
     *  @param loaderInfo A LoaderInfo instance.
     *
     *  @return A normalized <code>LoaderInfo.url</code> property.
     */
    public static function normalizeURL(loaderInfo:LoaderInfo):String
    {
        var url:String = loaderInfo.url;
        var results:Array = url.split("/[[DYNAMIC]]/");
        
        return results[0];
    }

    }
}