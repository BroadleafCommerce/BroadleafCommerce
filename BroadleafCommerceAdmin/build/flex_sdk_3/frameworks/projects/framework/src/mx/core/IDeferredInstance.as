////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

/**
 *  The IDeferredInstance interface defines the Flex deferred instance factory interface.
 *  An implementation of IDeferredInstance creates a particular instance value
 *  when the <code>getInstance()</code> method is first called, and returns a reference to that value
 *  when the <code>getInstance()</code> method is called subsequently.
 *
 *  <p>The Flex compiler performs the following automatic coercions when it
 *  encounters MXML that assigns a value to a property with the
 *  IDeferredInstance type:</p>
 *
 *  <ol>
 *      <li>If you assign a property of type IDeferredInstance a value that is
 *          an MXML child tag representing a class, such as a component
 *          tag, the compiler creates an IDeferredInstance implementation
 *          whose <code>getInstance()</code> method returns an instance
 *          of the class, configured as specified in the MXML code.
 *          The following example shows this format; in this example, 
 *          MyComp is a custom component that has a variable called 
 *          myDeferredInstanceProperty of type IDeferredInstance. The compiler
 *          generates an IDeferredInstance1 implementation whose
 *          <code>getInstance()</code> method returns an instance of the
 *          Label class, with its text property set to
 *          &quot;This is a deferred label&quot;:
 *          <pre>
 *          &lt;MyComp&gt;
 *              &lt;myDeferredInstanceProperty&gt;
 *                  &lt;Label text=&quot;This is a deferred label&quot;/&gt;
 *              &lt;/myDeferredInstanceProperty&gt;
 *          &lt;/MyComp&gt;</pre>
 *      </li>
 *      <li>If you assign a text string to a property of type IDeferredInstance,
 *          the compiler interprets the string as a fully qualified class name,
 *          and creates an
 *          IDeferredInstance implementation whose <code>getInstance()</code>
 *          method returns a new instance of the specified class.
 *          The specified class must have a constructor with no arguments.
 *          The following example shows this format; in this example, the compiler
 *          generates an IDeferredInstance1 implementation whose
 *          <code>getInstance()</code> method returns an instance of the
 *          MyClass class:
 *          <pre>
 *          &lt;MyComp myDeferredInstanceProperty="myPackage.MyClass/&gt;</pre>
 *      </li>
 *  </ol>
 *
 *  <p>Use the IDeferredInstance interface when an ActionScript class defers
 *  the instantiation of a property value.
 *  You cannot use IDeferredInstance if an ActionScript class requires
 *  multiple instances of the same value.
 *  In those situations, use the IFactory interface.</p>
 *  
 *  <p>The states.AddChild class includes a <code>childFactory</code>
 *  property that is of type IDeferredInstance.</p>
 * 
 *  @see mx.states.AddChild
 */
public interface IDeferredInstance
{
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Creates an instance Object from a class or function,
     *  if the instance does not yet exist.
     *  
     *  @return The instance Object.
     */
    function getInstance():Object;
}

}
