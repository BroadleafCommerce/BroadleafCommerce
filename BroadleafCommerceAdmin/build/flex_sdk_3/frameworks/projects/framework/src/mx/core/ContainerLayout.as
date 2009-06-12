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

package mx.core
{
	
/**
 *  The ContainerLayout class defines the constant values
 *  for the <code>layout</code> property of container classes.
 *
 *  @see mx.containers.Panel#layout
 *  @see mx.core.Application#layout
 */
public final class ContainerLayout
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Use absolute layout for the contents of this container.
	 *  You are responsible for explicitly specifying the position
	 *  of each child.
	 *
	 *  <p>The easiest way to do this is to specify
	 *  the <code>x</code>, <code>y</code>, <code>width</code>,
	 *  and <code>height</code> of each child.</p>
	 *
	 *  <p>The <code>width</code> and <code>height</code> can be specified
	 *  as a percentage value in MXML.
	 *  (In ActionScript you have to set the <code>percentWidth</code>
	 *  and <code>percentHeight</code> properties.)</p>
	 *
	 *  <p>If you don't specify the <code>width</code> or
	 *  <code>percentWidth</code> for a child, 
	 *  then its <code>measuredWidth</code>, as automatically determined
	 *  by its <code>measure()</code> method, will be used.
	 *  The same applies for its height.</p>
	 *
	 *  <p>As an alternative way of doing layout, you can use the anchor
	 *  styles <code>left</code>, <code>top</code>, <code>right </code>,
	 *  <code>bottom</code>, <code>horizontalCenter</code>,
	 *  and <code>verticalCenter</code> on children to anchor them to
	 *  the sides or the center of a container.</p>
	 *
	 *  <p>When you use absolute layout, the container's
	 *  <code>paddingLeft</code>, <code>paddingTop</code>,
	 *  <code>paddingRight</code>, <code>paddingBottom</code>,
	 *  <code>horizontalGap</code>, <code>verticalGap</code>,
	 *  <code>horizontalAlign</code>, and<code>verticalAlign</code>
	 *  styles are ignored.</p> 
	 */
	public static const ABSOLUTE:String = "absolute";
	
	/**
	 *  Use vertical layout for the contents of this container.
	 *  The container will automatically place its children in a single column.
	 *
	 *  <p>If you don't specify the <code>width</code> or
	 *  <code>percentWidth</code> for a child, 
	 *  then its <code>measuredWidth</code>, as automatically determined
	 *  by its <code>measure()</code> method, is used.
	 *  The same applies for its height.</p>
	 *
	 *  <p>You can control the spacing between children
	 *  with the <code>verticalGap</code> style,
	 *  and the alignment of the children
	 *  with the <code>horizontalAlign</code> style.
	 *  The <code>paddingLeft</code>, <code>paddingTop</code>,
	 *  <code>paddingRight</code>, and <code>paddingBottom</code> styles
	 *  control the space between the border of the container
	 *  and the children.</p>
	 */
	public static const VERTICAL:String = "vertical";

	/**
	 *  Use horizontal layout for the contents of this container.
	 *  The container will automatically place its children in a single row.
	 *
	 *  <p>If you don't specify the <code>width</code> or
	 *  <code>percentWidth</code> for a child, 
	 *  then its <code>measuredWidth</code>, as automatically determined
	 *  by its <code>measure()</code> method, is used.
	 *  The same applies for its height.</p>
	 *
	 *  <p>You can control the spacing between children
	 *  with the <code>horizontalGap</code> style,
	 *  and the alignment of the children
	 *  with the <code>verticalAlign</code> style.
	 *  The <code>paddingLeft</code>, <code>paddingTop</code>,
	 *  <code>paddingRight</code>, and <code>paddingBottom</code> styles
	 *  control the space between the border of the container
	 *  and the children.</p>
	 */
	public static const HORIZONTAL:String = "horizontal";
}

}
