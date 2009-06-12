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

import mx.resources.ResourceManager;

[ResourceBundle("core")]

/** 
 *  This class controls the backward-compatibility of the framework.
 *  With every new release, some aspects of the framework such as behaviors, 
 *  styles, and default settings, are changed which can affect your application.
 *  By setting the <code>compatibilityVersion</code> property, the behavior can be changed
 *  to match previous releases.
 *  This is a 'global' flag; you cannot apply one version to one component or group of components
 *  and a different version to another component or group of components.
 */
public class FlexVersion 
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /** 
     *  The current released version of the Flex SDK, encoded as a uint.
     */
    public static const CURRENT_VERSION:uint = 0x03000000;

    /** 
     *  The <code>compatibilityVersion</code> value of Flex 3.0,
     *  encoded numerically as a <code>uint</code>.
     *  Code can compare this constant against
     *  the <code>compatibilityVersion</code>
     *  to implement version-specific behavior.
     */
    public static const VERSION_3_0:uint = 0x03000000;

    /** 
     *  The <code>compatibilityVersion</code> value of Flex 2.0.1,
     *  encoded numerically as a <code>uint</code>.
     *  Code can compare this constant against
     *  the <code>compatibilityVersion</code>
     *  to implement version-specific behavior.
     */
    public static const VERSION_2_0_1:uint = 0x02000001;

    /** 
     *  The <code>compatibilityVersion</code> value of Flex 2.0,
     *  encoded numerically as a <code>uint</code>.
     *  Code can compare this constant against
     *  the <code>compatibilityVersion</code>
     *  to implement version-specific behavior.
     */
    public static const VERSION_2_0:uint = 0x02000000;

    /**
     *  A String passed as a parameter
     *  to the <code>compatibilityErrorFunction()</code> method
     *  if the compatibility version has already been set.
     */
    public static const VERSION_ALREADY_SET:String = "versionAlreadySet";
        // Also used as resource string, so be careful changing it.

    /**
     *  A String passed as a parameter
     *  to the <code>compatibilityErrorFunction()</code> method 
     *  if the compatibility version has already been read.
     */
    public static const VERSION_ALREADY_READ:String = "versionAlreadyRead";
        // Also used as resource string, so be careful changing it.

    //--------------------------------------------------------------------------
    //
    //  Class properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  compatibilityErrorFunction
    //----------------------------------

    /**
     *  @private
     *  Storage for the compatibilityErrorFunction property.
     */
    private static var _compatibilityErrorFunction:Function;

    /** 
     *  A function that gets called when the compatibility version
     *  is set more than once, or set after it has been read.
     *  If this function is not set, the SDK throws an error.
     *  If set, File calls this function, but it is
     *  up to the developer to decide how to handle the call.
     *  This function will also be called if the function is set more than once.     
     *  The function takes two parameters: the first is a <code>uint</code>
     *  which is the version that was attempted to be set; the second
     *  is a string that is the reason it failed, either
     *  <code>VERSION_ALREADY_SET</code> or <code>VERSION_ALREADY_READ</code>.
     */  
    public static function get compatibilityErrorFunction():Function
    {
        return _compatibilityErrorFunction;
    }

    /**
     *  @private
     */
    public static function set compatibilityErrorFunction(value:Function):void   
    {
        _compatibilityErrorFunction = value;
    }

    //----------------------------------
    //  compatibilityVersion
    //----------------------------------

    /**
     *  @private
     *  Storage for the compatibilityVersion property.
     */
    private static var _compatibilityVersion:uint = CURRENT_VERSION;

    /**
     *  @private
     */
    private static var compatibilityVersionChanged:Boolean = false;

    /**
     *  @private
     */
    private static var compatibilityVersionRead:Boolean = false;

    /** 
     *  The current version that the framework maintains compatibility for.  
     *  This defaults to <code>CURRENT_VERSION</code>.
     *  It can be changed only once; changing it a second time
     *  results in a call to the <code>compatibilityErrorFunction()</code> method
     *  if it exists, or results in a runtime error. 
     *  Changing it after the <code>compatibilityVersion</code> property has been read results in an error
     *  because code that is dependent on the version has already run.
     *  There are no notifications; the assumption is that this is set only once, and this it is set
     *  early enough that no code that depends on it has run yet.
     *
     *  @default FlexVersion.CURRENT_VERSION
     */
    public static function get compatibilityVersion():uint
    {
        compatibilityVersionRead = true;

        return _compatibilityVersion;
    }

    /**
     *  @private
     */
    public static function set compatibilityVersion(value:uint):void
    {
        if (value == _compatibilityVersion)
            return;

        var s:String;
        
        if (compatibilityVersionChanged)
        {
            if (compatibilityErrorFunction == null)
            {
                s = ResourceManager.getInstance().getString(
                    "core", VERSION_ALREADY_SET);           
                throw new Error(s);
            }
            else
                compatibilityErrorFunction(value, VERSION_ALREADY_SET);
        }

        if (compatibilityVersionRead)
        {
            if (compatibilityErrorFunction == null)
            {
                s = ResourceManager.getInstance().getString(
                    "core", VERSION_ALREADY_READ);          
                throw new Error(s);
            }
            else
                compatibilityErrorFunction(value, VERSION_ALREADY_READ);
        }

        _compatibilityVersion = value;
        compatibilityVersionChanged = true;
    }

    //----------------------------------
    //  compatibilityVersionString
    //----------------------------------

    /** 
     *  The compatibility version, as a string of the form "X.X.X".
     *  This is a pass-through to the <code>compatibilityVersion</code>
     *  property, which converts the number to and from a more
     *  human-readable String version.
     */
    public static function get compatibilityVersionString():String
    {
        var major:uint = (compatibilityVersion >> 24) & 0xFF;
        var minor:uint = (compatibilityVersion >> 16) & 0xFF;
        var update:uint = compatibilityVersion & 0xFFFF;
        
        return major.toString() + "." +
               minor.toString() + "." +
               update.toString();
    }

    /**
     *  @private
     */
    public static function set compatibilityVersionString(value:String):void
    {
        var pieces:Array = value.split(".");

        var major:uint = parseInt(pieces[0]);
        var minor:uint = parseInt(pieces[1]);
        var update:uint = parseInt(pieces[2]);

        compatibilityVersion = (major << 24) + (minor << 16) + update;
    }

    /** 
     *  @private
     *  A back door for changing the compatibility version.
     *  This is provided for FlexBuilder's Design View,
     *  which needs to be able to change compatibility mode. 
     *  In general, we won't support late changes to compatibility, 
     *  because the framework won't watch for changes.  
     *  Design View will need to set this early during initialization.
     */
    mx_internal static function changeCompatibilityVersionString(
                                        value:String):void
    {
        var pieces:Array = value.split(".");

        var major:uint = parseInt(pieces[0]);
        var minor:uint = parseInt(pieces[1]);
        var update:uint = parseInt(pieces[2]);

        _compatibilityVersion = (major << 24) + (minor << 16) + update;
    }
}

}
