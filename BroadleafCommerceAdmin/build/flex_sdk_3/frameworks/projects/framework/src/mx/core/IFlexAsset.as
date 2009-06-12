////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

/**
 *  IFlexAsset is a marker interface with the following meaning:
 *  if a class declares that it implements IFlexAsset,
 *  then that class represents an asset -- such as a bitmap, a font,
 *  or a sound -- that has been embedded in a Flex application.
 *  This interface does not define any properties or methods that the
 *  class must actually implement.
 *
 *  <p>The player uses ActionScript classes to represent
 *  embedded assets as well as executable ActionScript code.
 *  When you embed an asset in a Flex application, the MXML compiler
 *  autogenerates a class to represent it, and all such classes
 *  declare that they implement IFlexAsset so that they can be
 *  distinguished from the code classes.</p>
 */
public interface IFlexAsset
{
}

}
