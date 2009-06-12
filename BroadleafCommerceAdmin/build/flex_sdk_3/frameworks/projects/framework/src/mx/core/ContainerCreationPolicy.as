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
 *  The ContainerCreationPolicy class defines the constant values
 *  for the <code>creationPolicy</code> property of the Container class.
 *
 *  @see mx.core.Container#creationPolicy
 */
public final class ContainerCreationPolicy
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Delay creating some or all descendants until they are needed.
	 *
	 *  <p>For example, if a navigator container such as a TabNavigator
	 *  has this <code>creationPolicy</code>, it will immediately create
	 *  all of its children, plus the descendants of the initially
	 *  selected child.
	 *  However, it will wait to create the descendants of the other children
	 *  until the user navigates to them.</p>
	 */
	public static const AUTO:String = "auto";
	
	/**
	 *  Immediately create all descendants.
	 *
	 *  <p>Avoid using this <code>creationPolicy</code> because
	 *  it increases the startup time of your application.
	 *  There is usually no good reason to create components at startup
	 *  which the user cannot see.
	 *  If you are using this policy so that you can "push" data into
	 *  hidden components at startup, you should instead design your
	 *  application so that the data is stored in data variables
	 *  and components which are created later "pull" in this data,
	 *  via databinding or an <code>initialize</code> handler.</p>
	 */
	public static const ALL:String = "all";
	
	/**
	 *  Add the container to a creation queue.
	 */
	public static const QUEUED:String = "queued";
	
	/**
	 *  Do not create any children.
	 *
	 *  <p>With this <code>creationPolicy</code>, it is the developer's
	 *  responsibility to programmatically create the children 
	 *  from the UIComponentDescriptors by calling
	 *  <code>createComponentsFromDescriptors()</code>
	 *  on the parent container.</p>
	 */
	public static const NONE:String = "none";
}

}
