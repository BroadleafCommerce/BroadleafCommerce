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

package mx.controls.buttonBarClasses
{

import mx.controls.Button;
import mx.core.UITextFormat;
import mx.core.mx_internal;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 *  The ButtonBarButton class is for internal use.
 */
public class ButtonBarButton extends Button
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
	 *  Constructor.
     */
    public function ButtonBarButton()
    {
        super();
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var inLayoutContents:Boolean = false;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: UIComponent
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function determineTextFormatFromStyles():UITextFormat
    {
        if (inLayoutContents && selected)
            return textField.getUITextFormat();
        else
            return super.determineTextFormatFromStyles();
    }

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Button
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */
    override mx_internal function layoutContents(unscaledWidth:Number,
												 unscaledHeight:Number,
												 offset:Boolean):void
    {
        // Fix for bug 122684:
        // layoutContents() internally calls measureText(), which calls
        // determineTextFormatFromStyles() to get the UITextFormat object.
        // For a selected button, the textField's text styles can differ from
        // the button's text styles. So we need to return the right
        // UITextFormat in determineTextFormatFromStyles()
        inLayoutContents = true;
        super.layoutContents(unscaledWidth, unscaledHeight, offset);
        inLayoutContents = false;
    }
}

}
