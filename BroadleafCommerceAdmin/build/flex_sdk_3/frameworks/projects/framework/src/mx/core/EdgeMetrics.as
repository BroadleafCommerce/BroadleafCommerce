////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

/**
 *  The EdgeMetrics class specifies the thickness, in pixels,
 *  of the four edge regions around a visual component.
 *
 *  <p>The following Flex properties have values that are EdgeMetrics
 *  objects:</p>
 *
 *  <ul>
 *  <li>The <code>borderMetrics</code> property of the mx.core.Container and
 *  mx.skins.Border classes includes only the border in the calculations 
 *  of the property values of the EdgeMetrics object.</li>
 *
 *  <li>The <code>viewMetrics</code> property of the mx.core.Container
 *  class, and of subclasses of the Container class, includes possible 
 *  scrollbars and non-content elements -- such as a Panel container's
 *  header area and the area for a ControlBar component -- in the calculations
 *  of the  property values of the EdgeMetrics object.</li>
 *
 *  <li>The <code>viewMetricsAndPadding</code> property of the
 *  mx.core.Container class includes the items listed for the
 *  <code>viewMetrics</code> property, plus the any areas defined by 
 *  the margins of the container in the calculations of the 
 *  property values of the EdgeMetrics object.</li>
 *  </ul>
 *
 *  <p>These three properites all return a reference to the same
 *  EdgeMetrics object that the Container is using for its measurement
 *  and layout; they do not return a copy of this object.
 *  If you need a copy, call the <code>clone()</code> method.</p>
 *
 *  @see mx.core.Container
 *  @see mx.skins.Border
 *  @see mx.containers.Panel
 */
public class EdgeMetrics
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  An EdgeMetrics object with a value of zero for its
	 *  <code>left</code>, <code>top</code>, <code>right</code>,
	 *  and <code>bottom</code> properties.
	 */
	public static const EMPTY:EdgeMetrics = new EdgeMetrics(0, 0, 0, 0);

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  @param left The width, in pixels, of the left edge region.
	 *
	 *  @param top The height, in pixels, of the top edge region.
	 *
	 *  @param right The width, in pixels, of the right edge region.
	 *
	 *  @param bottom The height, in pixels, of the bottom edge region.
	 */
	public function EdgeMetrics(left:Number = 0, top:Number = 0,
								right:Number = 0, bottom:Number = 0)
	{
		super();

		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  bottom
	//----------------------------------

	/**
	 *  The height, in pixels, of the bottom edge region.
	 */
	public var bottom:Number;

	//----------------------------------
	//  left
	//----------------------------------

	/**
	 *  The width, in pixels, of the left edge region.
	 */
	public var left:Number;

	//----------------------------------
	//  right
	//----------------------------------

	/**
	 *  The width, in pixels, of the right edge region.
	 */
	public var right:Number;

	//----------------------------------
	//  top
	//----------------------------------

	/**
	 *  The height, in pixels, of the top edge region.
	 */
	public var top:Number;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Returns a copy of this EdgeMetrics object.
	 */
	public function clone():EdgeMetrics
	{
		return new EdgeMetrics(left, top, right, bottom);
	}
}

}
