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
 *  TextFieldAsset is a subclass of the flash.text.TextField class
 *  which represents TextField symbols that you embed in a Flex
 *  application from a SWF file produced by Flash.
 *  It implements the IFlexDisplayObject interface, which makes it
 *  possible for a TextFieldAsset to be displayed in an Image control,
 *  or to be used as a container background or a component skin.
 *
 *  <p>This class is included in Flex for completeness, so that any kind
 *  of symbol in a SWF file produced by Flash can be embedded
 *  in a Flex application.
 *  However, Flex applications do not typically use embedded TextFields.
 *  Refer to more commonly-used asset classes such as BitmapAsset
 *  for more information about how embedded assets work in Flex.</p>
 */
public class TextFieldAsset extends FlexTextField
                            implements IFlexAsset, IFlexDisplayObject
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
    public function TextFieldAsset()
    {
        super();

        // Remember initial size as our measured size.
        _measuredWidth = width;
        _measuredHeight = height;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  measuredHeight
    //----------------------------------

    /**
     *  @private
     *  Storage for the measuredHeight property.
     */
    private var _measuredHeight:Number;

    /**
     *  @inheritDoc
     */
    public function get measuredHeight():Number
    {
        return _measuredHeight;
    }

    //----------------------------------
    //  measuredWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for the measuredWidth property.
     */
    private var _measuredWidth:Number;

    /**
     *  @inheritDoc
     */
    public function get measuredWidth():Number
    {
        return _measuredWidth;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @inheritDoc
     */
    public function move(x:Number, y:Number):void
    {
        this.x = x;
        this.y = y;
    }

    /**
     *  @inheritDoc
     */
    public function setActualSize(newWidth:Number, newHeight:Number):void
    {
        width = newWidth;
        height = newHeight;
    }
}

}
