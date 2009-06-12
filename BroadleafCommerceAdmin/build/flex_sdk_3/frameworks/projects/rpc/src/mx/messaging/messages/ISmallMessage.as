////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.messaging.messages 
{

/**
 * A marker interface that is used to indicate that an IMessage has an
 * alternative smaller form for serialization.
 * @private
 */
public interface ISmallMessage extends IMessage
{
    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * This method must be implemented by subclasses that have a "small" form,
     * typically achieved through the use of
     * <code>flash.utils.IExternalizable</code>. If a small form is not
     * available this method should return null.
     *
     * @return Returns An alternative representation of an
     * flex.messaging.messages.IMessage so that the serialized form
     * is smaller than the regular message.
     */
    function getSmallMessage():IMessage;
}

}