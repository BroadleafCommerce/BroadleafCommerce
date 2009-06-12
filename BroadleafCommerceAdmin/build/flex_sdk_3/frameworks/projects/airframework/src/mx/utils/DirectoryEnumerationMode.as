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

[ExcludeClass]

/**
 *  @private
 *
 *  The DirectoryEnumerationMode class defines the constant values
 *  for the <code>enumerationMode</code> property
 *  of the DirectoryEnumeration class.
 *
 *  @see mx.utils.DirectoryEnumeration#enumerationMode
 * 
 *  @playerversion AIR 1.1
 */
public final class DirectoryEnumerationMode
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Specifies to show files but not directories.
     */
    public static const FILES_ONLY:String = "filesOnly";
    
    /**
     *  Specifies to show directories but not files.
     */
    public static const DIRECTORIES_ONLY:String = "directoriesOnly";
    
    /**
     *  Specifies to show files first, then directories.
     */
    public static const FILES_FIRST:String = "filesFirst";
    
    /**
     *  Specifies to show directories, then files.
     */
    public static const DIRECTORIES_FIRST:String = "directoriesFirst";
    
    /**
     *  Specifies to show both files and directories, mixed together.
     */
     public static const FILES_AND_DIRECTORIES:String = "filesAndDirectories";
}

}
