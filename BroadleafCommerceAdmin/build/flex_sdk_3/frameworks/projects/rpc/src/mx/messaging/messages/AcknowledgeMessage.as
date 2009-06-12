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

package mx.messaging.messages
{

import flash.utils.IDataInput;
import flash.utils.IDataOutput;

[RemoteClass(alias="flex.messaging.messages.AcknowledgeMessage")]

/**
 *  An AcknowledgeMessage acknowledges the receipt of a message that 
 *  was sent previously.
 *  Every message sent within the messaging system must receive an
 *  acknowledgement.
 */
public class AcknowledgeMessage extends AsyncMessage implements ISmallMessage
{
    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Header name for the error hint header.
     *  Used to indicate that the acknowledgement is for a message that
     *  generated an error.
     */
    public static const ERROR_HINT_HEADER:String = "DSErrorHint";
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Constructs an instance of an AcknowledgeMessage with an empty body and header.
     */
    public function AcknowledgeMessage()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    //
    // Overridden Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    override public function getSmallMessage():IMessage
    {
        var o:Object = this;
        if (o.constructor == AcknowledgeMessage)
            return new AcknowledgeMessageExt(this);
        return null;
    }

    /**
     * @private
     */
    override public function readExternal(input:IDataInput):void
    {
        super.readExternal(input);

        var flagsArray:Array = readFlags(input);
        for (var i:uint = 0; i < flagsArray.length; i++)
        {
            var flags:uint = flagsArray[i] as uint;
            var reservedPosition:uint = 0;

            // For forwards compatibility, read in any other flagged objects
            // to preserve the integrity of the input stream...
            if ((flags >> reservedPosition) != 0)
            {
                for (var j:uint = reservedPosition; j < 6; j++)
                {
                    if (((flags >> j) & 1) != 0)
                    {
                        input.readObject();
                    }
                }
            }
        }
    }

    /**
     * @private
     */
    override public function writeExternal(output:IDataOutput):void
    {
        super.writeExternal(output);

        var flags:uint = 0;
        output.writeByte(flags);
    }
    
}

}
