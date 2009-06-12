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

package mx.messaging
{

/**
 * This is the class used for elements of the ArrayCollection subscriptions property in the 
 * MultiTopicConsumer property.
 */
public class SubscriptionInfo 
{
    /** 
     * The subtopic - if null, represents a subscription for messages directed to the
     * destination with no subtopic.
     */
    public var subtopic:String;

    /**
     * The selector.  If null, indicates all messages should be sent.
     */
    public var selector:String;

    /** Builds a new SubscriptionInfo with the specified subtopic and selector.
     *
     *  @param st The subtopic for the subscription. If null, represents a subscription
     *  for messages directed to the destination with no subtopic.
     *
     *  @param sel The selector. If null, inidcates all messages should be sent.
     */
    public function SubscriptionInfo(st:String, sel:String)
    {
        subtopic = st;
        selector = sel;
    }
}

}
