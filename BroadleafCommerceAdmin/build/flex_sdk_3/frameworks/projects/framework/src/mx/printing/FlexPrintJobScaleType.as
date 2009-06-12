////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.printing
{

/**
 *  Values for the <code>scaleType</code> property
 *  of the FlexPrintJob.addObject() method parameter.
 * 
 *  @see FlexPrintJob#addObject()
 */
public final class FlexPrintJobScaleType
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Scales the object to fill at least one page completely; 
	 *  that is, it selects the larger of the MATCH_WIDTH or MATCH_HEIGHT 
	 *  scale types.
	 */
	public static const FILL_PAGE:String = "fillPage";
	
	/**
	 *  Scales the object to fill the available page height. 
	 *  If the resulting object width exceeds the page width, the output 
	 *  spans multiple pages.
	 */
	public static const MATCH_HEIGHT:String = "matchHeight";

	/**
	 *  Scales the object to fill the available page width. 
	 *  If the resulting object height exceeds the page height, the output 
	 *  spans multiple pages.
	 */
	public static const MATCH_WIDTH:String = "matchWidth";
	
	/**
	 *  Does not scale the output. 
	 *  The printed page has the same dimensions as the object on the screen. 
	 *  If the object height, width, or both dimensions exceed the page width 
	 *  or height, the output spans multiple pages.
	 */
	public static const NONE:String = "none";

	/**
	 *  Scales the object to fit on a single page, filling one dimension; 
	 *  that is, it selects the smaller of the MATCH_WIDTH or MATCH_HEIGHT 
	 *  scale types.
	 */
	public static const SHOW_ALL:String = "showAll";
}

}

