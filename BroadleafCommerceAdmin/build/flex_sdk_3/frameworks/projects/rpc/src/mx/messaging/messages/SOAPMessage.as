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

[RemoteClass(alias="flex.messaging.messages.SOAPMessage")]

/**
 *  SOAPMessages are similar to HTTPRequestMessages. However,
 *  they always contain a SOAP XML envelope request body
 *  that will always be sent using HTTP POST.
 *  They also allow a SOAP action to be specified.
 */
public class SOAPMessage extends HTTPRequestMessage
{
    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------

    /**
     *  The HTTP header that stores the SOAP action for the SOAPMessage.
     */
    public static const SOAP_ACTION_HEADER:String = "SOAPAction";    
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Constructs an uninitialized SOAPMessage.
     */
    public function SOAPMessage()
    {
        super();
        method = "POST";
        contentType = CONTENT_TYPE_SOAP_XML;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  Provides access to the name of the remote method/operation that
     *  will be called.
     *
     *  @return Returns the name of the remote method/operation that 
     *  will be called.
     */
    public function getSOAPAction():String
    {
        return (httpHeaders != null) ? httpHeaders[SOAP_ACTION_HEADER] : null;
    }

    /**
     *  @private
     */
    public function setSOAPAction(value:String):void
    {
        if (value != null)
        {
            if (value.indexOf('"') < 0)
            {
                var str:String = '"';
                str += value;
                str += '"';
                value = str.toString();
            }

            if (httpHeaders == null)
                httpHeaders = {};

            httpHeaders[SOAP_ACTION_HEADER] = value;
        }
    }

}

}
