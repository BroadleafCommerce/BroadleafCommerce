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

package
{

/**
 *  @private
 *  This class is used to link additional classes into rpc.swc
 *  beyond those that are found by dependecy analysis starting
 *  from the classes specified in manifest.xml.
 */
internal class RPCClasses
{
import mx.messaging.AbstractMessageStore; AbstractMessageStore;
import mx.messaging.Consumer; Consumer;
import mx.messaging.MultiTopicProducer; MultiTopicProducer;
import mx.messaging.MultiTopicConsumer; MultiTopicConsumer;
import mx.messaging.config.ServerConfig; ServerConfig;
import mx.messaging.channels.HTTPChannel; HTTPChannel;
import mx.messaging.channels.AMFChannel; AMFChannel;
import mx.messaging.channels.StreamingAMFChannel; StreamingAMFChannel;
import mx.messaging.channels.StreamingHTTPChannel; StreamingHTTPChannel;
import mx.messaging.channels.SecureHTTPChannel; SecureHTTPChannel;
import mx.messaging.channels.SecureAMFChannel; SecureAMFChannel;
import mx.messaging.channels.SecureStreamingAMFChannel; SecureStreamingAMFChannel;
import mx.messaging.channels.SecureStreamingHTTPChannel; SecureStreamingHTTPChannel;
import mx.messaging.management.Attribute; Attribute;
import mx.messaging.management.ObjectName; ObjectName;
import mx.messaging.management.ObjectInstance; ObjectInstance;
import mx.messaging.management.MBeanInfo; MBeanInfo;
import mx.messaging.management.MBeanFeatureInfo; MBeanFeatureInfo;
import mx.messaging.management.MBeanAttributeInfo; MBeanAttributeInfo;
import mx.messaging.management.MBeanOperationInfo; MBeanOperationInfo;
import mx.messaging.management.MBeanParameterInfo; MBeanParameterInfo;
import mx.messaging.management.MBeanConstructorInfo; MBeanConstructorInfo;
import mx.messaging.messages.AcknowledgeMessageExt; AcknowledgeMessageExt;
import mx.messaging.messages.AsyncMessageExt; AsyncMessageExt;
import mx.rpc.Responder; Responder;
import mx.rpc.remoting.mxml.RemoteObject; RemoteObject;
import mx.rpc.soap.mxml.WebService; WebService;
import mx.rpc.http.mxml.HTTPService; HTTPService;
import mx.rpc.AsyncToken; AsyncToken;
import mx.rpc.AsyncRequest; AsyncRequest;
import mx.rpc.AsyncResponder; AsyncResponder;
import mx.rpc.xml.NamespaceUtil; NamespaceUtil;
import mx.utils.HashUtil; HashUtil;
import mx.utils.HexDecoder; HexDecoder;
import mx.utils.HexEncoder; HexEncoder;
import mx.utils.Translator; Translator;
}

}
