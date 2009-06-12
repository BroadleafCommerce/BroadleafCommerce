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

package mx.rpc.wsdl
{

import mx.logging.ILogger;
import mx.logging.Log;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.Fault;
import mx.rpc.soap.SOAPConstants;
import mx.rpc.xml.QualifiedResourceManager;
import mx.rpc.xml.Schema;
import mx.rpc.xml.SchemaConstants;
import mx.rpc.xml.SchemaManager;

[ResourceBundle("rpc")]

[ExcludeClass]

/**
 * Manages a WSDL top-level <code>definitions</code> element. WSDL definitions
 * may contain imports to other WSDL definitions. Only SOAP bindings are
 * supported.
 * 
 * @private
 */
public class WSDL
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * Constructs a WSDL from XML. Services and their operations are
     * parsed into object representations on the first call to
     * <code>getService()</code>, <code>getPort()</code> or
     * <code>getOperation()</code>.
     *
     * @param xml An XML document starting from the top-level WSDL 
     * <code>defintions</code> element.
     */
    public function WSDL(xml:XML)
    {
        super();
        _xml = xml;
        _log = Log.getLogger("mx.rpc.wsdl.WSDL");
        processNamespaces();
        processSchemas();
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var resourceManager:IResourceManager =
									ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    public function get schemaManager():SchemaManager
    {
        if (_schemaManager == null)
        {
            _schemaManager = new SchemaManager();
            _schemaManager.schemaConstants = schemaConstants;
        }

        return _schemaManager;
    }

    public function get schemaConstants():SchemaConstants
    {
        if (_schemaConstants == null)
            _schemaConstants = SchemaConstants.getConstants(xml);

        return _schemaConstants;
    }

    public function get soapConstants():SOAPConstants
    {
        if (_soapConstants == null)
            _soapConstants = SOAPConstants.getConstants(xml);
        
        return _soapConstants;
    }

    public function get targetNamespace():Namespace
    {
        return _targetNamespace;
    }

    public function get wsdlConstants():WSDLConstants
    {
        if (_wsdlConstants == null)
            _wsdlConstants = WSDLConstants.getConstants(xml);

        return _wsdlConstants;
    }

    /**
     * The raw XML representing the WSDL starting from the top-level
     * definitions element.
     */
    public function get xml():XML
    {
        return _xml;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /*
        FIXME:
        1. Validate that the targetNamespace matches the one defined on the
           import XML
        2. Also, check that the import being added does not cause a cyclic
           relationship.
    */
    public function addImport(targetNamespace:Namespace, wsdl:WSDL):void
    {
        if (importsManager == null)
            importsManager = new QualifiedResourceManager();

        importsManager.addResource(targetNamespace, wsdl);
    }

    public function addSchema(schema:Schema):void
    {
        schemaManager.addSchema(schema);
    }

    public function getOperation(operationName:String, serviceName:String = null, portName:String = null):WSDLOperation
    {
        var port:WSDLPort = getPort(serviceName, portName);
        var binding:WSDLBinding = port.binding;
        var portType:WSDLPortType = binding.portType;
        var operation:WSDLOperation = portType.getOperation(operationName);
        return operation;
    }

    /**
     * Locate a port for a service by name. If a serviceName is provided, a
     * service is first located, otherwise the first service is used by default.
     * Once a service has been selected, the search then considers the port.
     * If a portName is not provided, the first port is used by default.
     */
    public function getPort(serviceName:String = null, portName:String = null):WSDLPort
    {
        var port:WSDLPort;

        var service:WSDLService = getService(serviceName, portName);
        if (service != null)
        {
            if (portName == null)
                port = service.defaultPort;
            else
                port = service.getPort(portName);
        }

        if (port == null)
        {
			var message:String = resourceManager.getString(
				"rpc", "noServiceAndPort", [ serviceName, portName ]);
            throw new Fault("Client.NoSuchPort", message);
        }

        return port;
    }

    /**
     * Search for WSDL service and port by name. If a serviceName is not
     * provided the first service found will be selected by default. 
     */
    public function getService(serviceName:String = null, portName:String = null):WSDLService
    {
        var service:WSDLService;
        var port:WSDLPort;

        if (serviceMap != null && serviceName != null)
        {    
            service = serviceMap[serviceName];
            if (service != null)
            {
                if (portName != null)
                    port = service.getPort(portName);

                if (port == null)
                {
                    var tempService:WSDLService = parseService(serviceName, portName);
                    for each (var p:WSDLPort in tempService.ports)
                    {
                        service.addPort(p);
                    }
                }
            }
        }

        // We didn't find a service, so we parse to find one...
        if (service == null)
        {
            service = parseService(serviceName, portName);

			var message:String;
			var detail:String;

            if (service != null)
            {
                if (serviceMap == null)
                    serviceMap = {};

                serviceMap[service.name] = service;
            }
            else if (serviceName != null)
            {
				message = resourceManager.getString(
					"rpc", "noSuchServiceInWSDL", [ serviceName ]);
                throw new Fault("Client.NoSuchService", message);
            }
            else
            {
				message = resourceManager.getString(
					"rpc", "noServiceElement");
				detail = resourceManager.getString(
					"rpc", "noServiceElement.details", [ "" ]);
                throw new Fault("Server.NoServicesInWSDL", message, detail);
            }
        }

        return service;
    }

    public function getTypes(targetNamespace:Namespace):XML
    {
        var types:XML;

        // First, check current WSDL for types element
        var typesXMLList:XMLList = xml.elements(wsdlConstants.typesQName);
        if (typesXMLList.length() > 0)
        {
            // There should only be one types element.
            types = typesXMLList[0];
        }
        else if (importsManager != null)
        {
            // Check WSDL imports for types element
            var imports:Array = importsManager.getResourcesForNamespace(targetNamespace);
            for each (var childWSDL:WSDL in imports)
            {
                types = childWSDL.getTypes(targetNamespace);
                if (types != null)
                    break;
            }
        }

        return types;
    }

    /**
     * Search for a requested service in the definitions XML, including all
     * WSDL imports. This is the usual entry point to start parsing the WSDL as
     * only one service and one of its port will be used at any time.
     * <p>
     * If the service name is not specified, the first service is used by
     * default. If a service is located it is parsed and then the search
     * continues for the requested port, which is in turn parsed, and so forth.
     * </p>
     */
    private function parseService(serviceName:String = null, portName:String = null):WSDLService
    {
        var service:WSDLService;
        var serviceXML:XML;

        // First, look for the service in this WSDL
        var serviceXMLList:XMLList = xml.elements(wsdlConstants.serviceQName);
        for each (var x:XML in serviceXMLList)
        {
            if (serviceName == null)
            {
                // Select the first service if a service name wasn't specified
                serviceXML = x;
                serviceName = x.@name.toString();
                break;
            }
            else if (x.@name == serviceName)
            {
                serviceXML = x;
                break;
            }
        }

        if (serviceXML != null)
        {
            // We have the service, now look for the port
            service = new WSDLService(serviceName);

            var port:WSDLPort = parsePort(service, serviceXML, portName);

            if (port != null)
            {
                service.addPort(port);
            }
        }
        else if (importsManager != null)
        {
            // Otherwise, look for the service in our WSDL imports (assuming
            // the port must be in the same import as the service).
            var imports:Array = importsManager.getResources();
            for each (var childWSDL:WSDL in imports)
            {
                service = childWSDL.parseService(serviceName, portName);
                if (service != null)
                    break;
            }
        }

        return service;
    }

    /**
     * Search for a requested port in the given service XML. If a port
     * name was not specified, the first port is used by default.
     */
    private function parsePort(service:WSDLService, serviceXML:XML, portName:String = null):WSDLPort
    {
        var port:WSDLPort;
        var portXML:XML;

        var portXMLList:XMLList = serviceXML.elements(wsdlConstants.portQName);
        for each (var x:XML in portXMLList)
        {
            if (portName == null)
            {
                portXML = x;
                portName = x.@name.toString();
                break;
            }
            else if (x.@name == portName)
            {
                portXML = x;
                break;
            }
        }

        if (portXML != null)
        {
            // We have the port, now look for the location and binding
            port = new WSDLPort(portName, service);

            var soapAddressXMLList:XMLList;
            soapAddressXMLList = portXML.elements(wsdlConstants.soapAddressQName);
            if (soapAddressXMLList.length() > 0)
            {
                port.endpointURI = soapAddressXMLList[0].@location.toString();
            }

            var prefixedBindingName:String = portXML.@binding.toString();
            var bindingQName:QName = schemaManager.getQNameForPrefixedName(prefixedBindingName, portXML);

            var binding:WSDLBinding = parseBinding(bindingQName);

            if (binding != null)
            {
                port.binding = binding;
            }
            else
            {
				var message:String = resourceManager.getString(
					"rpc", "unrecognizedBindingName",
					[ bindingQName.localName, bindingQName.uri ]);
                throw new Fault("WSDL.UnrecognizedBindingName", message);
            }
        }

        return port;
    }

    /**
     * Search for a binding by QName. If a URI is not specified, the
     * targetNamespace is assumed. FIXME: We may need to consider whether
     * formElementDefault is set to qualified or not.
     */
    private function parseBinding(bindingQName:QName):WSDLBinding
    {
        var binding:WSDLBinding;
        var bindingXML:XML;

        // First, look for the binding in this WSDL
        var bindingXMLList:XMLList = xml.elements(wsdlConstants.bindingQName);
        for each (var x:XML in bindingXMLList)
        {
            if (x.@name == bindingQName.localName)
            {
                bindingXML = x;
                break;
            }
        }

        if (bindingXML != null)
        {
            // We have the binding, now look for the style, transport,
            // operation and portType information
            binding = new WSDLBinding(bindingQName.localName);

            // Look for the SOAP specific binding that controls the basic
            // structure of requests made to and from a WSDL operation, and
            // specifies the default style for operations. It does not make any
            // claims as to the encoding or format of the message.
            //
            //     <soap:binding transport="uri"? style="rpc|document"?>
            //
            var soapBindingXMLList:XMLList;
            soapBindingXMLList = bindingXML.elements(wsdlConstants.soapBindingQName);
            if (soapBindingXMLList.length() > 0)
            {
                var style:String = soapBindingXMLList[0].@style.toString();
                binding.style = style;

                var transport:String = soapBindingXMLList[0].@transport.toString();
                binding.transport = transport;
            }

            // The port type name must match the type specified on the binding.
            //
            //    <wsdl:binding name="nmtoken" type="qname">*
            //
            var prefixedTypeName:String = bindingXML.@type.toString();
            var portTypeQName:QName = schemaManager.getQNameForPrefixedName(prefixedTypeName, bindingXML);
            var portType:WSDLPortType = new WSDLPortType(portTypeQName.localName);
            binding.portType = portType;

            // We parse binding information for each operation and its
            // input, output and fault messages before processing the port type
            // so that we can establish encoding rules and whether subsets of
            // the parts have been declared for the Body.
            //
            //    <wsdl:operation name="nmtoken">*
            //
            var operationXMLList:XMLList = bindingXML.elements(wsdlConstants.operationQName);
            for each (var operationXML:XML in operationXMLList)
            {
                var operationName:String = operationXML.@name.toString();
                var operation:WSDLOperation = new WSDLOperation(operationName);
                operation.schemaManager = schemaManager;
                operation.namespaces = namespaces;
                operation.wsdlConstants = wsdlConstants;

                // Look for soapAction as we expect HTTP based SOAP binding
                // and any operation-specific style information.
                //
                //    <soap:operation soapAction="uri"? style="rpc|document"?>?
                //
                var soapOperationXMLList:XMLList = operationXML.elements(wsdlConstants.soapOperationQName);
                if (soapOperationXMLList.length() > 0)
                {
                    var soapAction:String = soapOperationXMLList[0].@soapAction.toString();
                    operation.soapAction = soapAction;
                    var opStyle:String = soapOperationXMLList[0].@style.toString();
                    if (opStyle == "")
                        operation.style = binding.style;
                    else
                        operation.style = opStyle;
                }

                // Record the encoding format each of the messages of this
                // operation. The SOAP Body, Fault and Header sections are
                // described.
                // 
                //     <soap:body parts="nmtokens"? use="literal|encoded"?
                //         encodingStyle="uri-list"? namespace="uri"?>
                //
                //     <soap:header message="qname" part="nmtoken" use="literal|encoded"
                //         encodingStyle="uri-list"? namespace="uri"?>*
                //

                var operationExtensionXMLList:XMLList;
                var operationExtensionXML:XML;
                var encoding:WSDLEncoding;
                var extensionXMLList:XMLList;
                var extensionXML:XML;
                var extensionName:String;
                var extensionMessage:WSDLMessage;

                // WSDL Input Message
                operationExtensionXMLList = operationXML.elements(wsdlConstants.inputQName);
                if (operationExtensionXMLList.length() > 0)
                {
                    operationExtensionXML = operationExtensionXMLList[0];
                    operation.inputMessage = new WSDLMessage();

                    // SOAP Body
                    extensionXMLList = operationExtensionXML.elements(wsdlConstants.soapBodyQName);
                    if (extensionXMLList.length() > 0)
                    {
                        extensionXML = extensionXMLList[0];
                        encoding = parseEncodingExtension(extensionXML);
                        operation.inputMessage.encoding = encoding;
                    }

                    // SOAP Header
                    extensionXMLList = operationExtensionXML.elements(wsdlConstants.soapHeaderQName);
                    for each (extensionXML in extensionXMLList)
                    {
                        extensionMessage = parseHeader(operationName, extensionXML);
                        operation.inputMessage.addHeader(extensionMessage);
                    }

                    // SOAP Header Fault
                    extensionXMLList = operationExtensionXML.elements(wsdlConstants.soapHeaderFaultQName);
                    for each (extensionXML in extensionXMLList)
                    {
                        extensionMessage = parseHeader(operationName, extensionXML);
                        operation.inputMessage.addHeaderFault(extensionMessage);
                    }
                }

                // WSDL Output Message
                operationExtensionXMLList = operationXML.elements(wsdlConstants.outputQName);
                if (operationExtensionXMLList.length() > 0)
                {
                    operationExtensionXML = operationExtensionXMLList[0];
                    operation.outputMessage = new WSDLMessage();

                    // SOAP Body
                    extensionXMLList = operationExtensionXML.elements(wsdlConstants.soapBodyQName);
                    if (extensionXMLList.length() > 0)
                    {
                        extensionXML = extensionXMLList[0];
                        encoding = parseEncodingExtension(extensionXML);
                        operation.outputMessage.encoding = encoding;
                    }

                    // SOAP Header
                    extensionXMLList = operationExtensionXML.elements(wsdlConstants.soapHeaderQName);
                    for each (extensionXML in extensionXMLList)
                    {
                        extensionMessage = parseHeader(operationName, extensionXML);
                        operation.outputMessage.addHeader(extensionMessage);
                    }

                    // SOAP Header Fault
                    extensionXMLList = operationExtensionXML.elements(wsdlConstants.soapHeaderFaultQName);
                    for each (extensionXML in extensionXMLList)
                    {
                        extensionMessage = parseHeader(operationName, extensionXML);
                        operation.outputMessage.addHeaderFault(extensionMessage);
                    }
                }

                // WSDL Fault Message
                //
                //     <soap:fault name="nmtoken" use="literal|encoded"
                //         encodingStyle="uri-list"? namespace="uri"?>
                //
                operationExtensionXMLList = operationXML.elements(wsdlConstants.faultQName);
                for each (operationExtensionXML in operationExtensionXMLList)
                {
                    // SOAP Fault
                    extensionXMLList = operationExtensionXML.elements(wsdlConstants.soapFaultQName);
                    if (extensionXMLList.length() > 0)
                    {
                        extensionXML = extensionXMLList[0];
                        var faultName:String = extensionXML.@["name"].toString();
                        extensionMessage = new WSDLMessage(faultName);
                        encoding = parseEncodingExtension(extensionXML, false, true);
                        extensionMessage.encoding = encoding;
                        operation.addFault(extensionMessage);
                    }
                }

                portType.addOperation(operation);
            }

            // Now that we've read the SOAP binding information for each
            // operation, we can find and parse the port type which is a
            // named set of abstract operations and the abstract messages
            // involved.
            parsePortType(portTypeQName, portType);
        }
        else if (importsManager != null)
        {
            // FIXME: USE QNAME URI TO LOOK UP IMPORT BY NAMESPACE!
            
            // Otherwise, look for the binding in our WSDL imports.
            var imports:Array = importsManager.getResources();
            for each (var childWSDL:WSDL in imports)
            {
                binding = childWSDL.parseBinding(bindingQName);
                if (binding != null)
                    break;
            }
        }

        return binding;
    }

    /**
     * Search for a portType by QName. If a URI is not specified, the
     * targetNamespace is assumed.
     */
    private function parsePortType(portTypeQName:QName, portType:WSDLPortType):Boolean
    {
        var portTypeXML:XML;
        var foundMatchingPortType:Boolean;

        // First, look for the portType in this WSDL
        var portTypeXMLList:XMLList = xml.elements(wsdlConstants.portTypeQName);
        for each (var x:XML in portTypeXMLList)
        {
            if (x.@name == portTypeQName.localName)
            {
                portTypeXML = x;
                break;
            }
        }

        if (portTypeXML != null)
        {
            foundMatchingPortType = true;

            var operationsXMLList:XMLList = portTypeXML.elements(wsdlConstants.operationQName);
            for each(var operationXML:XML in operationsXMLList)
            {
                // TODO: We may need to consider operation overloading!

                // The binding operation nmtoken is unique and must match an
                // operation defined in the associated portType. We've
                // previously parsed the binding, so this operation must be
                // located by name.
                var operationName:String = operationXML.@name.toString();
                var operation:WSDLOperation = portType.getOperation(operationName);

                // If we don't have the binding information for this operation,
                // skip it.
                if (operationName == null)
                {
                    _log.warn("An operation '{0}' was found in the port type but is missing binding information.", operationName);
                    continue;
                }

                var messageName:String;
                var messageQName:QName;
                var message:WSDLMessage;

                // Input Message
                //
                //     <wsdl:input name="nmtoken"? message="qname"/>
                //
                var inputXMLList:XMLList = operationXML.elements(wsdlConstants.inputQName);
                if (inputXMLList.length() > 0)
                {
                    // Reset the schema scope for each message
                    schemaManager.reset();

                    messageName = inputXMLList[0].@message.toString();
                    messageQName = schemaManager.getQNameForPrefixedName(messageName, inputXMLList[0]);
                    message = operation.inputMessage;
                    message.name = messageQName.localName;
                    parseMessage(message, messageQName, operationName, WSDLConstants.MODE_IN);
                }

                // Output Message
                //
                //    <wsdl:output name="nmtoken"? message="qname"/>
                //
                var outputXMLList:XMLList = operationXML.elements(wsdlConstants.outputQName);
                if (outputXMLList.length() > 0)
                {
                    // Reset the schema scope for each message
                    schemaManager.reset();

                    messageName = outputXMLList[0].@message.toString();
                    messageQName = schemaManager.getQNameForPrefixedName(messageName, outputXMLList[0]);
                    message = operation.outputMessage;
                    message.name = messageQName.localName;
                    parseMessage(message, messageQName, operationName, WSDLConstants.MODE_OUT);
                }

                // Fault Messages
                //
                //    <wsdl:fault name="nmtoken" message="qname"/>*
                //
                var faultsXMLList:XMLList = operationXML.elements(wsdlConstants.faultQName);
                for each (var faultXML:XML in faultsXMLList)
                {
                    // Reset the schema scope for each message
                    schemaManager.reset();

                    var faultName:String = faultXML.@name.toString();
                    message = operation.getFault(faultName);
                    if (message != null)
                    {
                        messageName = faultXML.@message.toString();
                        messageQName = schemaManager.getQNameForPrefixedName(messageName, faultXML);
                        parseMessage(message, messageQName, operationName, WSDLConstants.MODE_FAULT);
                    }
                }

                if (operation.style == SOAPConstants.DOC_STYLE)
                {
                    parseDocumentOperation(operation);
                }
            }
        }
        else if (importsManager != null)
        {
            // Then look for the portType in the imports...
            var imports:Array = importsManager.getResources();
            for each (var childWSDL:WSDL in imports)
            {
                foundMatchingPortType = childWSDL.parsePortType(portTypeQName, portType);
                if (foundMatchingPortType)
                    break;
            }
        }

        return foundMatchingPortType;
    }

    /**
     * Search for a message by QName. If a URI is not specified, the
     * targetNamespace is assumed.
     */
    private function parseMessage(message:WSDLMessage, messageQName:QName, operationName:String, mode:int):Boolean
    {
        var messageXML:XML;
        var foundMatchingMessage:Boolean;
        var encoding:WSDLEncoding = message.encoding;

        // First, look for the message in this WSDL
        var messageXMLList:XMLList = xml.elements(wsdlConstants.messageQName);
        for each (var x:XML in messageXMLList)
        {
            if (x.@name == messageQName.localName)
            {
                messageXML = x;
                break;
            }
        }

        if (messageXML != null)
        {
            foundMatchingMessage = true;

            // If the message is not named we default to the operation name
            // plus a standard suffix for input or output mode:
            if (message.name == null || message.name == "")
            {
                if (mode == WSDLConstants.MODE_IN)
                {
                    message.name = operationName + "Request";
                }
                else if (mode == WSDLConstants.MODE_OUT)
                {
                    message.name = operationName + "Response";
                }
                
                // FIXME: Handle soap fault message name if one wasn't provided
            }

            // Parse the message types
            var partXMLList:XMLList = messageXML.elements(wsdlConstants.partQName);
            for each (var partXML:XML in partXMLList)
            {
                var partName:String = partXML.@name;

                // We skip a part if the SOAP encoding extension defines a 
                // subset of parts and this part nmtoken isn't in the list...
                if (encoding != null && !encoding.hasPart(partName))
                {
                    continue;
                }

                var part:WSDLMessagePart = parseMessagePart(partXML);
                message.addPart(part);
            }
        }
        else if (importsManager != null)
        {
            // Then look for the message in the imports...
            var imports:Array = importsManager.getResources();
            for each (var childWSDL:WSDL in imports)
            {
                foundMatchingMessage = childWSDL.parseMessage(message, messageQName, operationName, mode);
                if (foundMatchingMessage)
                    break;
            }
        }

        return foundMatchingMessage;
    }

    /**
     * Returns a WSDL message part based on the name and looks for either an
     * element QName or type QName to determine which definition describes
     * this message part.
     */
    private function parseMessagePart(partXML:XML):WSDLMessagePart
    {
        var partName:String = partXML.@name;
        var partQName:QName = new QName("", partName);
        var part:WSDLMessagePart = new WSDLMessagePart(partQName);
        var partXMLElement:String = partXML.@element;
        var partXMLType:String = partXML.@type;

        // Consider type or element
        if (partXMLElement != "")
        {
            var elementName:String = partXML.@element;
            var elementQName:QName = schemaManager.getQNameForPrefixedName(elementName, partXML);
            part.element = elementQName;
        }
        else if (partXMLType != "")
        {
            var typeName:String = partXML.@type;
            var typeQName:QName = schemaManager.getQNameForPrefixedName(typeName, partXML);
            part.type = typeQName;
        }

        return part;
    }

    /**
     * Looks for the SOAP encoding extensions based on the type of WSDL
     * operation message. SOAP encoding is required for SOAP Body, Header
     * (header and headerfault) and Fault extensions.
     */
    private function parseHeader(operationName:String, headerXML:XML):WSDLMessage
    {
        var headerName:String = headerXML.@["part"].toString();
        var headerMessage:WSDLMessage = new WSDLMessage(headerName);
        var encoding:WSDLEncoding = parseEncodingExtension(headerXML, true);
        headerMessage.encoding = encoding;
        parseMessage(headerMessage, encoding.message, operationName, WSDLConstants.MODE_HEADER);
        return headerMessage;
    }

    /**
     * Looks for the SOAP encoding extensions based on the type of WSDL
     * operation message. SOAP encoding is required for SOAP Body, Header
     * (header and headerfault) and Fault extensions.
     */
    private function parseEncodingExtension(extensionXML:XML, isHeader:Boolean = false, isFault:Boolean = false):WSDLEncoding
    {
        var encoding:WSDLEncoding = new WSDLEncoding();
        encoding.useStyle = extensionXML.@["use"].toString();
        encoding.namespaceURI = extensionXML.@["namespace"].toString();
        encoding.encodingStyle = extensionXML.@["encodingStyle"].toString();

        if (isHeader)
        {
            var messageName:String = extensionXML.@["message"].toString();
            encoding.message = schemaManager.getQNameForPrefixedName(messageName, extensionXML);
            encoding.setParts(extensionXML.@["part"].toString());
        }
        else if (!isFault)
        {
            encoding.setParts(extensionXML.@["parts"].toString());
        }

        return encoding;
    }

    /**
     * A Document-Literal operation may make use of "wrapped" style. If so,
     * then the message parts should be replaced by the the individual wrapped
     * parts so that the SOAP encoders correctly bind ActionScript input params
     * to message params, and SOAP decoders correctly bind output params
     * to predictable ActionScript constructs. Essentially the wrappers should
     * be an encoding detail that is invisible to the Flex developer.
     */
    private function parseDocumentOperation(operation:WSDLOperation):void
    {
        var part:WSDLMessagePart;
        var element:XML;
        var elementTypeString:String;
        var elementType:QName;
        var complexTypes:XMLList;
        var complexType:XML;
        var attributes:XMLList;
        var sequences:XMLList;
        var sequence:XML;

        // Check whether this operation's input is literal and "wrapped".
        if (operation.inputMessage != null
            && operation.inputMessage.encoding != null
            && operation.inputMessage.encoding.useStyle == SOAPConstants.USE_LITERAL)
        {
            // A "wrapped" input message must:
            // 1. Have a single part.
            // 2. The part is an element.
            // 3. The element's name MUST be the same as the name of the operation.
            // 4. The element's complex type must have no attributes and define
            //    a sequence of zero or more elements (which serve as the input 
            //    params).
            var input:WSDLMessage = operation.inputMessage;
            if (input.parts != null && input.parts.length == 1)
            {
                // Reset the schema scope for each wrapped part
                schemaManager.reset();
                
                part = input.parts[0];
                if (part.element != null)
                {
                    element = schemaManager.getNamedDefinition(part.element, schemaConstants.elementTypeQName);
                    if (element != null && element.@["name"] == operation.name)
                    {
                        elementTypeString = element.@["type"];
                        if (elementTypeString != null && elementTypeString != "")
                        {
                            elementType = schemaManager.getQNameForPrefixedName(elementTypeString, element);
                            complexType = schemaManager.getNamedDefinition(elementType, schemaConstants.complexTypeQName);
                        }
                        else
                        {
                            complexTypes = element.elements(schemaConstants.complexTypeQName);
                            if (complexTypes.length() == 1)
                            {
                                complexType = complexTypes[0];
                            }
                        }

                        if (complexType != null)
                        {                    
                            attributes = complexType.elements(schemaConstants.attributeQName);
                            if (attributes.length() == 0)
                            {
                                sequences = complexType.elements(schemaConstants.sequenceQName);

                                // The sequence may be omitted entirely for
                                // zero-param operation signatures
                                if (complexType.elements().length() == 0 || sequences.length() == 1)
                                {
                                    input.isWrapped = true;
                                    input.wrappedQName = part.element;
                                    input.parts = [];

                                    if (sequences.length() == 1)
                                    {
                                        // Use the wrapped parts to redefine the
                                        // signature of the input message.
                                        sequence = sequences[0];
                                        var requestElements:XMLList = sequence.elements(schemaConstants.elementTypeQName);
                                        for each (var requestElement:XML in requestElements)
                                        {
                                            // FIXME: Should we skip wrapped parts
                                            // if the SOAP encoding extension defines
                                            // a subset?
                                            part = parseWrappedMessagePart(requestElement);
                                            input.addPart(part);
                                        }
                                    }
                                }
                            }
                        }
                    }                        
                }
            }
        }

        // Reset wrapped element's type variables
        complexType = null;
        elementTypeString = null;

        // Check whether this operation's output is literal and "wrapped".
        if (operation.outputMessage != null
            && operation.outputMessage.encoding != null
            && operation.outputMessage.encoding.useStyle == SOAPConstants.USE_LITERAL)
        {
            // A "wrapped" output message must:
            // 1. Have a single part.
            // 2. The part is an element.
            // 3. This element's complexType must have no attributes and define
            //    a sequence of zero or more elements (which serve as the output
            //    params).
            var output:WSDLMessage = operation.outputMessage;
            if (output.parts != null && output.parts.length == 1)
            {
                part = output.parts[0];
                if (part.element != null)
                {
                    // Reset the schema scope for each wrapped part
                    schemaManager.reset();

                    element = schemaManager.getNamedDefinition(part.element, schemaConstants.elementTypeQName);
                    if (element != null)
                    { 
                        elementTypeString = element.@["type"];
                        if (elementTypeString != null && elementTypeString != "")
                        {
                            elementType = schemaManager.getQNameForPrefixedName(elementTypeString, element);
                            complexType = schemaManager.getNamedDefinition(elementType, schemaConstants.complexTypeQName);
                        }
                        else
                        {
                            complexTypes = element.elements(schemaConstants.complexTypeQName);
                            if (complexTypes.length() == 1)
                            {
                                complexType = complexTypes[0];
                            }
                        }

                        if (complexType != null)
                        {  
                            attributes = complexType.elements(schemaConstants.attributeQName);
                            if (attributes.length() == 0)
                            {
                                sequences = complexType.elements(schemaConstants.sequenceQName);

                                // The sequence may be omitted entirely for
                                // zero-param operation signatures
                                if (complexType.elements().length() == 0 || sequences.length() == 1)
                                {
                                    output.isWrapped = true;
                                    output.wrappedQName = part.element;
                                    output.parts = [];

                                    if (sequences.length() == 1)
                                    {
                                        // Use the wrapped parts to redefine the
                                        // return type signature of the output
                                        // message (multiple output parts will
                                        // be returned as a map)
                                        sequence = sequences[0];
                                        var resultElements:XMLList = sequence.elements(schemaConstants.elementTypeQName);
                                        for each (var resultElement:XML in resultElements)
                                        {
                                            // FIXME: Should we skip wrapped parts
                                            // if the SOAP encoding extension defines
                                            // a subset?
                                            part = parseWrappedMessagePart(resultElement);
                                            output.addPart(part);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a WSDL message part based on the name and looks for either an
     * element QName or type QName to determine which definition describes
     * this message part.
     */
    private function parseWrappedMessagePart(elementXML:XML):WSDLMessagePart
    {
        // <element ref="..."> may be used to point to a top-level element definition
        var ref:QName;
        if (elementXML.attribute("ref").length() == 1)
        {
            ref = schemaManager.getQNameForPrefixedName(elementXML.@ref, elementXML, true);
            elementXML = schemaManager.getNamedDefinition(ref, schemaConstants.elementTypeQName);
            if (elementXML == null)
                throw new Error("Cannot resolve element definition for ref '" + ref + "'");
        }

        var partName:String = elementXML.@name;
        var partQName:QName = schemaManager.getQNameForElement(partName, elementXML.@form);
        var part:WSDLMessagePart = new WSDLMessagePart(partQName);

        // However, since this is a local element that acts like a
        // top-level element, we first check whether we need to
        // special case the element definition.
        var minOccurs:String = elementXML.@minOccurs;
        var maxOccurs:String = elementXML.@maxOccurs;

        // Furthermore, if minOccurs is set to 0, this part is optional
        if (minOccurs != "" && parseInt(minOccurs) == 0)
            part.optional = true;

        if (ref != null)
        {
            // We have a reference to a top level element definition
            part.element = ref;
        }
        else 
        {
            var partType:String = elementXML.@type;
            if (partType != "" || !elementXML.hasComplexContent())
            {
                // We have a reference to a type definition
                if (partType == null || partType == "")
                    part.type = schemaConstants.anyTypeQName;
                else 
                    part.type = schemaManager.getQNameForPrefixedName(partType, elementXML);

                if (minOccurs != "" || maxOccurs != "")
                {
                    part.definition = elementXML;
                }
            }
            else if (elementXML.hasComplexContent())
            {
                // We have an inline type definition
                part.definition = elementXML;
            }
        }

        // If we found our element by reference, we now release the schema scope
        if (ref != null)
            schemaManager.releaseScope();

        return part;
    }

    /**
     * Determines the WSDL and SOAP versions from the definitions and creates
     * a map of top level prefixes to namespaces.
     */
    private function processNamespaces():void
    {
        if (_xml != null)
        {
            // Record the targetNamespace
            var tns:String = _xml.@targetNamespace.toString();
            _targetNamespace = new Namespace(tns);

            // Initialize Namespaces
            namespaces = {};
            
            // Determine WSDL and SOAP Version
            var nsArray:Array = _xml.namespaceDeclarations();
            for each (var ns:Namespace in nsArray)
            {
                namespaces[ns.prefix] = ns;
            }

            // Initialize WSDL Constants
            _wsdlConstants = WSDLConstants.getConstants(_xml);
            _schemaConstants = SchemaConstants.getConstants(_xml);

            // Also record these top level namespaces with our SchemaManager
            schemaManager.addNamespaces(namespaces);
        }
    }

    private function processSchemas():void
    {
        var types:XML = getTypes(targetNamespace);
        if (types != null)
        {
            var schemas:XMLList = types.elements(schemaConstants.schemaQName);
            for each (var schemaXML:XML in schemas)
            {
                var schema:Schema = new Schema(schemaXML);
                addSchema(schema);
            }
        }
    }

    /**
     * Manages WSDL imports.
     */ 
    private var importsManager:QualifiedResourceManager;

    /**
     * Logs warnings encountered while parsing WSDL.
     */
    private var _log:ILogger;

    /**
     * Maps a namespace prefix (as a <code>String</code>) to a
     * <code>Namespace</code> (i.e. this helps to resolve a prefix to a URI).
     */
    private var namespaces:Object;

    /**
     * Provides a static cache of the constants for various versions of XSD.
     */
    private var _schemaConstants:SchemaConstants;

    /**
     * A map of target namespaces to XSD Schemas that describe the types 
     * used in this WSDL.
     */
    private var _schemaManager:SchemaManager;

    /**
     * Map to cache a WSDLService by service name. The cache is cleared 
     * when a new definitions element is set for the WSDL.
     */
    private var serviceMap:Object;

    private var _soapConstants:SOAPConstants;

    /**
     * WSDL target namespace.
     */
    private var _targetNamespace:Namespace;

    /**
     * Provides a static cache of the constants for various versions of WSDL.
     */
    private var _wsdlConstants:WSDLConstants;

    /**
     * The raw XML representing the WSDL definitions top level element.
     */
    private var _xml:XML;
}

}
