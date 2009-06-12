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

package mx.messaging.channels.amfx
{

import flash.net.getClassByAlias;
import flash.utils.ByteArray;
import flash.utils.getDefinitionByName;
import flash.utils.IExternalizable;

import mx.logging.Log;
import mx.messaging.errors.ChannelError;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.HexDecoder;

[ResourceBundle("messaging")]

[ExcludeClass]

/**
 * Decodes an AMFX packet into an ActionScript Object graph.
 * Headers and the result body are accessed from the returned
 * AMFXResult.
 * @private
 */
public class AMFXDecoder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    public function AMFXDecoder()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    // Class variables
    // 
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Storage for the resourceManager getter.
	 *  This gets initialized on first access,
	 *  not at static initialization time, in order to ensure
	 *  that the Singleton registry has already been initialized.
	 */
	private static var _resourceManager:IResourceManager;
	
	/**
	 *  @private
     *  A reference to the object which manages
     *  all of the application's localized resources.
     *  This is a singleton instance which implements
     *  the IResourceManager interface.
	 */
	private static function get resourceManager():IResourceManager
	{
		if (!_resourceManager)
			_resourceManager = ResourceManager.getInstance();

		return _resourceManager;
	}

    //--------------------------------------------------------------------------
    //
    // Public Methods
    // 
    //--------------------------------------------------------------------------
    
    public function decode(xml:XML):AMFXResult
    {
        if (xml)
        {
            var context:AMFXContext = new AMFXContext();
            context.log = Log.getLogger("mx.messaging.channels.amfx.AMFXDecoder");
            return decodePacket(xml, context);
        }
        else
        {
            return null;
        }
    }

    //--------------------------------------------------------------------------
    //
    // Static Methods
    // 
    //--------------------------------------------------------------------------

    private static function decodePacket(xml:XML, context:AMFXContext):AMFXResult
    {
        var result:AMFXResult = new AMFXResult();

        var message:String;
		
		var name:String = xml.localName();
        if (name == "amfx")
        {
            var v:String = xml.attribute("ver").toString();
            var version:uint = uint(v);

            if (supportedVersion(version))
            {
                var h:XMLList = xml.child("header");
                if (h)
                {
                    result.headers = decodeHeaders(h, context);
                }

                var body:XML = xml.body[0];

                if (body)
                {
                    result.result = decodeBody(body, context);
                }
                else
                {
					message = resourceManager.getString(
						"messaging", "noAMFXBody");
                    throw new ChannelError(message);
                }
            }
            else
            {
				message = resourceManager.getString(
					"messaging", "unsupportedAMFXVersion", [ version ]);
                throw new ChannelError(message);
            }
        }
        else
        {
			message = resourceManager.getString(
				"messaging", "noAMFXNode")
            throw new ChannelError(message);
        }

        return result;
    }

    private static function decodeHeaders(xmlList:XMLList, context:AMFXContext):Array
    {
        var headers:Array = [];

        for (var i:uint = 0; i < xmlList.length(); i++)
        {
            var h:XML = xmlList[i];
            var name:String = h.attribute("name").toString();

            var header:AMFXHeader = new AMFXHeader();
            header.name = name;

            var temp:String = h.attribute("mustUnderstand").toString();
            header.mustUnderstand = (temp == "true");

            var children:XMLList = h.children();
            if (children.length() > 0)
            {
                header.content = decodeValue(children[0], context);
            }
            else
            {
                header.content = null;
            }
            
            headers[i] = header;
        }

        return headers;
    }

    private static function decodeBody(xml:XML, context:AMFXContext):Object
    {
        var result:Object;
        var children:XMLList = xml.children();

        if (children.length() == 1)
        {
            result = decodeValue(children[0], context);
        }
        else if (children.length() > 1)
        {
            result = [];
            for (var i:uint = 0; i < children.length(); i++)
            {
                result[i] = decodeValue(children[i], context);
            }
        }

        return result;
    }

    public static function decodeValue(xml:XML, context:AMFXContext):Object
    {
        var result:Object;
        var name:String = xml.localName();

        if (name == "string")
        {
            result = decodeString(xml, context);
        }
        else if (name == "true")
        {
            result = true;
        }
        else if (name == "false")
        {
            result = false;
        }
        else if (name == "array")
        {
            result = decodeArray(xml, context);
        }
        else if (name == "object")
        {
            result = decodeObject(xml, context);
        }
        else if (name == "ref")
        {
            result = decodeRef(xml, context);
        }
        else if (name == "double")
        {
            var n:String = xml.text().toString();
            result = Number(n);
        }
        else if (name == "int")
        {
            var i:String = xml.text().toString();
            result = int(i);
        }
        else if (name == "null")
        {
            result = null;
        }
        else if (name == "date")
        {
            result = decodeDate(xml, context);
        }
        else if (name == "xml")
        {
            var x:String = xml.text().toString();
            // If we had CDATA, restore any escaped close CDATA "]]>" tokens
            if (hasEscapedCloseCDATA(xml))
            {
                x = x.replace(REGEX_CLOSE_CDATA, "]]>");
            }

            result = new XML(x);
        }
        else if (name == "bytearray")
        {        
            result = decodeByteArray(xml);
        }
        else if (name == "undefined")
        {
            result = undefined;
        }

        return result;
    }

    private static function decodeArray(xml:XML, context:AMFXContext):Array
    {
        var array:Array = [];

        // Remember Array
        context.addObject(array);

        var entries:XMLList = xml.*;
        if (entries)
        {
            for (var i:uint = 0; i < entries.length(); i++)
            {
                var x:XML = entries[i];
                var prop:Object;
                if (x.localName() == "item")
                {
                    var propName:String = x.attribute("name").toString();
                    prop = decodeValue(x.*[0], context);
                    array[propName] = prop;
                }
                else
                {
                    prop = decodeValue(x, context);
                    array.push(prop);
                }
            }
        }

        return array;
    }

    private static function decodeByteArray(xml:XML):ByteArray
    {
        var str:String = xml.text().toString();

        var decoder:HexDecoder = new HexDecoder();
        decoder.decode(str);
        return decoder.drain();
    }

    private static function decodeDate(xml:XML, context:AMFXContext):Date
    {
        var d:String = xml.text().toString();
        var time:Number = new Number(d);
        var result:Date = new Date();
        result.setTime(time);

        // Remember Date object
        context.addObject(result);

        return result;
    }

    private static function decodeObject(xml:XML, context:AMFXContext):Object
    {
        var result:Object;
        var className:String = xml.attribute("type").toString();

        if (className)
        {
            try
            {
                var classType:Class = null;
                try
                {
                    classType = getClassByAlias(className);
                }
                catch(e1:Error)
                {
                    if (!classType)
                        classType = getDefinitionByName(className) as Class;
                }

                result = new classType();
            }
            catch(e:Error)
            {
                if (context.log)
                    context.log.warn("Error instantiating class: {0}. Reverting to anonymous Object.", className);

                result = {};
            }
        }
        else
        {
            className = "Object";
            result = {};
        }

        // Remember Object
        context.addObject(result);

        var entries:XMLList = xml.*;

        if (entries && entries.length() > 0)
        {
            var traits:Object;
            var tx:XML = entries[0];
			var message:String;

            if (tx.localName() == "traits")
            {
                traits = decodeTraits(tx, className, context);
                delete entries[0];
            }

            if (!traits)
            {
				message = resourceManager.getString(
					"messaging", "AMFXTraitsNotFirst")
                throw new ChannelError(message);
            }

            if (traits.externalizable)
            {
                if (result is IExternalizable)
                {
                    var ext:IExternalizable = IExternalizable(result);
                    tx = entries[0];
                    if (tx.localName() == "bytearray")
                    {
                        var ba:ByteArray = decodeByteArray(tx);

                        try
                        {
                            ext.readExternal(ba);
                        }
                        catch(e:Error)
                        {
							message = resourceManager.getString(
								"messaging", "errorReadingIExternalizable",
								[ (e.message+e.getStackTrace()) ]);
                            throw new ChannelError(message);
                        }
                    }
                }
                else
                {
					message = resourceManager.getString(
						"messaging", "notImplementingIExternalizable",
						[ className ]);
                    throw new ChannelError(message);
                }
            }
            else
            {
                for (var i:uint = 0; i < entries.length(); i++)
                {
                    var propName:String = traits.properties[i];
                    var propValue:* = decodeValue(entries[i], context);

                    try
                    {
                        result[propName] = propValue;
                    }
                    catch(e2:Error)
                    {
                        if (context.log != null)
                            context.log.warn("Cannot set property '{0}' on type '{1}'.", propName, className);
                    }
                }
            }
        }
        return result;
    }

    private static function decodeRef(xml:XML, context:AMFXContext):Object
    {
        var result:*;

		var message:String;

        var idAttr:String = xml.attribute("id").toString();
        if (idAttr)
        {
            var ref:int = parseInt(idAttr);

            if (!isNaN(ref))
            {
                result = context.getObject(ref);
            }

            if (result === undefined)
            {
				message = resourceManager.getString(
					"messaging", "unknownReference", [ idAttr ]);
                throw new ChannelError(message);
            }
        }
        else
        {
			message = resourceManager.getString(
				"messaging", "referenceMissingId");
            throw new ChannelError(message);
        }

        return result;
    }

    private static function decodeString(xml:XML, context:AMFXContext, isTrait:Boolean = false):String
    {
        var str:String;

        var refAttr:String = xml.attribute("id").toString();
        if (refAttr)
        {
            var ref:uint = uint(refAttr);
            if (!isNaN(ref))
            {
                str = context.getString(ref);
            }

            if (!str)
            {
				var message:String = resourceManager.getString(
					"messaging", "unknownStringReference", [ refAttr ]);
                throw new ChannelError(message);
            }
        }
        else
        {
            str = xml.text().toString();

            // If we had CDATA, restore any escaped close CDATA "]]>" tokens
            // Note that trait names won't have CDATA sections... so no need to check them
            if (!isTrait && hasEscapedCloseCDATA(xml))
            {
                str = str.replace(REGEX_CLOSE_CDATA, "]]>");
            }

            //Remember string
            context.addString(str);
        }

        return str;
    }

    private static function decodeTraits(xml:XML, className:String, context:AMFXContext):Object
    {
        var traits:Object;

        var refAttr:String = xml.attribute("id").toString();
        if (refAttr)
        {
            var ref:uint = uint(refAttr);
            if (!isNaN(ref))
            {
                traits = context.getTraitInfo(ref);
            }

            if (!traits)
            {
				var message:String = resourceManager.getString(
					"messaging", "unknownTraitReference", [ refAttr ]);
                throw new ChannelError(message);
            }
        }
        else
        {
            traits = {};
            traits.name = className;
            traits.alias = className;
            traits.properties = [];
            traits.externalizable = false;

            var ext:String = xml.attribute("externalizable").toString();
            if (ext == "true")
            {
                traits.externalizable = true;
            }

            var nodes:XMLList = xml.*;
            if (nodes)
            {
                for (var i:uint = 0; i < nodes.length(); i++)
                {
                    traits.properties[i] = decodeString(nodes[i], context, true);
                }
            }

            //Remember traits
            context.addTraitInfo(traits);
        }

        return traits;
    }

    private static function hasEscapedCloseCDATA(xml:XML):Boolean
    {
        var s:String = xml.toXMLString();

        if (s.indexOf("]]>") != -1)
        {
            return s.indexOf("]]&gt;") != -1;
        }
        else
        {
            return false;
        }
    }

    private static function supportedVersion(ver:uint):Boolean
    {
        for (var i:uint = 0; i < SUPPORTED_VERSIONS.length; i++)
        {
            if (ver == SUPPORTED_VERSIONS[i])
                return true;
        }

        return false;
    }

    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------

    private static const SUPPORTED_VERSIONS:Array = [3];
    private static const REGEX_CLOSE_CDATA:RegExp = new RegExp("]]&gt;", "g");
}

}
