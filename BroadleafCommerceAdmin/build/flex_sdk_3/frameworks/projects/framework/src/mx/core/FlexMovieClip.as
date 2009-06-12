////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

import flash.display.MovieClip;
import mx.utils.NameUtil;

/**
 *  FlexMovieClip is a subclass of the Player's MovieClip class.
 *  It overrides the <code>toString()</code> method
 *  to return a string indicating the location of the object
 *  within the hierarchy of DisplayObjects in the application.
 */
public class FlexMovieClip extends MovieClip
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Constructor.
	 *
	 *  <p>Sets the <code>name</code> property to a string
	 *  returned by the <code>createUniqueName()</code>
	 *  method of the mx.utils.NameUtils class.</p>
	 *
	 *  <p>This string is the name of the object's class concatenated
	 *  with an integer that is unique within the application,
	 *  such as <code>"FlexMovieClip14"</code>.</p>
	 *
	 *  @see flash.display.DisplayObject#name
	 *  @see mx.utils.NameUtil#createUniqueName()
     */
    public function FlexMovieClip()
	{
		super();

		try
		{
			name = NameUtil.createUniqueName(this);
		}
		catch(e:Error)
		{
			// The name assignment above can cause the RTE
			//   Error #2078: The name property of a Timeline-placed
			//   object cannot be modified.
			// if this class has been associated with an asset
			// that was created in the Flash authoring tool.
			// The only known case where this is a problem is when
			// an asset has another asset PlaceObject'd onto it and
			// both are embedded separately into a Flex application.
			// In this case, we ignore the error and toString() will
			// use the name assigned in the Flash authoring tool.
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

    /**
	 *  Returns a string indicating the location of this object
	 *  within the hierarchy of DisplayObjects in the Application.
	 *  This string, such as <code>"MyApp0.HBox5.FlexMovieClip14"</code>,
	 *  is built by the <code>displayObjectToString()</code> method
	 *  of the mx.utils.NameUtils class from the <code>name</code>
	 *  property of the object and its ancestors.
	 *  
	 *  @return A String indicating the location of this object
	 *  within the DisplayObject hierarchy. 
	 *
	 *  @see flash.display.DisplayObject#name
	 *  @see mx.utils.NameUtil#displayObjectToString()
     */
    override public function toString():String
	{
		return NameUtil.displayObjectToString(this);
	}
}

}
