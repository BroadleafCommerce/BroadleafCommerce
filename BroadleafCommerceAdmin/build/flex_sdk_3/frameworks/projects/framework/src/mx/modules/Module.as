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

package mx.modules
{

import mx.core.LayoutContainer;

[Frame(factoryClass="mx.core.FlexModuleFactory")]

/**
 *  The base class for MXML-based dynamically-loadable modules. You extend this
 *  class in MXML by using the <code>&lt;mx:Module&gt;</code> tag in an MXML file, as the
 *  following example shows:
 *  
 *  <PRE>
 *  &lt;?xml version="1.0"?&gt;
 *  &lt;!-- This module loads an image. --&gt;
 *  &lt;mx:Module  width="100%" height="100%" xmlns:mx="http://www.adobe.com/2006/mxml"&gt;
 *  
 *    &lt;mx:Image source="trinity.gif"/&gt;
 *  
 *  &lt;/mx:Module&gt;
 *  </PRE>
 *  
 *  <p>Extending the Module class in ActionScript is the same as using the <code>&lt;mx:Module&gt;</code> tag
 *  in an MXML file. You extend this class if your module interacts with the framework. 
 *  To see an example of an ActionScript class that extends the Module class, create an MXML 
 *  file with the root tag of <code>&lt;mx:Module&gt;</code>. When you compile this file, 
 *  set the value of the <code>keep-generated-actionscript</code> compiler option to <code>true</code>.
 *  The Flex compiler stores the generated ActionScript class in a directory called generated, which
 *  you can look at.</p>
 *  
 *  <p>If your module does not include any framework code, you can create a class that extends 
 *  the ModuleBase class. If you use the ModuleBase class, your module will typically be smaller than 
 *  if you create a module that is based on the Module class because it does not have any framework 
 *  class dependencies.</p>
 *  
 *  @see mx.modules.ModuleBase
 */
public class Module extends LayoutContainer
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Module()
    {
        super();
    }
}

}
