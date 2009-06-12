////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

//--------------------------------------
//  Other metadata
//--------------------------------------

/**
 *  The FormItem container uses a FormItemLabel object to display the 
 *  label portion of the FormItem container.
 * 
 *  <p>The FormItemLabel class does not add any functionality to its superclass, Label. 
 *  Instead, its purpose is to let you set styles in a FormItemLabel type selector and 
 *  set styles that affect the labels in all FormItem containers.</p>
 * 
 *  <p><strong>Note:</strong> This class has been deprecated.  
 *  The recommended way to style a FormItem label is to use the 
 *  <code>labelStyleName</code> style property of the FormItem class.</p>
 *
 *  @see mx.containers.FormItem
 */
public class FormItemLabel extends Label 
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
    public function FormItemLabel() 
    {
        super();
    }
}

}
