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

package mx.rpc.xml
{

import flash.utils.ByteArray;
import flash.utils.getQualifiedClassName;

import mx.collections.ArrayCollection;
import mx.collections.IList;
import mx.logging.ILogger;
import mx.logging.Log;
import mx.utils.DescribeTypeCache;
import mx.utils.object_proxy;
import mx.utils.ObjectProxy;
import mx.utils.URLUtil;
import mx.utils.XMLUtil;

[ExcludeClass]

/**
 * Decodes an XML document to an object graph based on XML Schema definitions.
 * 
 * @private
 */ 
public class XMLDecoder extends SchemaProcessor implements IXMLDecoder
{
    public function XMLDecoder()
    {
        super();
        log = Log.getLogger("mx.rpc.xml.XMLDecoder");
        typeRegistry = SchemaTypeRegistry.getInstance();
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * When makeObjectsBindable is set to true, anonymous Objects and Arrays
     * are wrapped to make them bindable. Objects are wrapped with
     * <code>mx.utils.ObjectProxy</code> and Arrays are wrapped with
     * <code>mx.collections.ArrayCollection</code>.
     */
    public function get makeObjectsBindable():Boolean
    {
        return _makeObjectsBindable;
    }

    public function set makeObjectsBindable(value:Boolean):void
    {
        _makeObjectsBindable = value;
    }

    /**
     * When recordXSIType is set to true, if an encoded complexType
     * has an <code>xsi:type</code> attribute the type information will be
     * recorded on the decoded instance if it is strongly typed and implements
     * <code>mx.rpc.xml.IXMLSchemaInstance</code> or is an anonymous
     * <code>mx.utils.ObjectProxy</code>. This type information can be used
     * to post process the decoded objects and identify which concrete
     * implementation of a potentially abstract type was used.
     * The default is false.
     */
    public function get recordXSIType():Boolean
    {
        return _recordXSIType;
    }

    public function set recordXSIType(value:Boolean):void
    {
        _recordXSIType = value;
    }

    /**
     * Maps XML Schema types by QName to ActionScript Classes in order to 
     * create strongly typed objects when decoding content.
     */
    public function get typeRegistry():SchemaTypeRegistry
    {
        return _typeRegistry;
    }

    public function set typeRegistry(value:SchemaTypeRegistry):void
    {
        _typeRegistry = value;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     * Decodes an XML document to an ActionScript object.
     * 
     * @param xml The XML instance to decode to an ActionScript object. 
     * This may be an XML instance, an XMLList of length 1 or a String that is
     * valid XML.
     * @param name The QName of an XML Schema <code>element</code> that
     * describes how to decode the value, or the name to be used for the
     * decoded value when a type parameter is also specified.
     * @param type The QName of an XML Schema <code>simpleType</code> or
     * <code>complexType</code> definition that describes how to decode the
     * value.
     * @param definition If neither a top level element nor type exists in the
     * schema to describe how to decode this value, a custom element definition
     * can be provided.
     */
    public function decode(xml:*, name:QName = null, type:QName = null, definition:XML = null):*
    {        
        // Validate xml argument and save the root document to decode multi-refs.
        if (!(xml is XML) && !(xml is XMLList) && !(xml is String))
            throw new ArgumentError("The xml argument must be of type XML, XMLList or String.");
        
        if (xml is XML)
        {
            document = xml;
        }
        else if (xml is XMLList)
        {
           if (XMLList(xml).length() != 1) // XMLList of length 1 can be treated as XML
               throw new ArgumentError("The xml argument must have a length of 1 when passed as an XMLList.");

           document = XMLList(xml)[0];
        }
        else if (xml is String)
        {
            try
            {
                document = new XML(xml);
            }
            catch(e:Error)
            {
                throw new ArgumentError("The xml argument does not contain valid xml. " + xml);
            }
        }
        
        // Pre-process encoded XML content
        preProcessXML(document);

        // FIXME: Handle generic name == null case with some default decoding?

        var content:*;

        if (type != null)
        {
            // Decoding is described directly from a type definition
            content = createContent(type);
            decodeType(type, content, name, document);
        }
        else
        {
            // Decoding is based on an element definition, either a custom one
            // was provided or we look one up for the given name...
            var elementDefinition:XML = definition;
            if (elementDefinition == null)
            {
                elementDefinition = schemaManager.getNamedDefinition(name, constants.elementTypeQName);
            }

            if (elementDefinition == null)
            {
                content = createContent(type);
                decodeType(constants.anyTypeQName, content, name, document);
            }
            else
            {
                content = decodeElementTopLevel(elementDefinition, name, document);
                // If passed-in definition was null, but elementDefinition != null,
                // we must have gotten the definition from the schemaManager.
                // We should release the scope in this case.
                if (definition == null)
                    schemaManager.releaseScope();
            }
        }

        // If necessary, unwrap content from its proxy wrapper
        if (content is ContentProxy)
            content = ContentProxy(content).object_proxy::content;

        return content;
    }

    /**
     * All content:
     *     (annotation?, (element | any)*)
     * 
     * <ul>
     * <li>maxOccurs for 'all' itself must be 1, minOccurs can be 0 or 1</li>
     * <li>maxOccurs for element declarations must be 1, minOccurs can be
     * 0 or 1</li>
     * <li>'all' does not contain other groups and it must not appear in other
     * structure groups - it must be at the top level of a complexType.</li>
     * </ul>
     * 
     * @private
     */
    public function decodeAll(definition:XML, parent:*, name:QName, valueElements:XMLList,
                                context:DecodingContext=null, isRequired:Boolean=true):Boolean
    {
        if (context == null)
            context = new DecodingContext();
//      else
            // TODO: Do we want to throw an error? If context was not null, it
            // means this all group is not at the top level of a complex type

        var minOccurs:uint = getMinOccurs(definition);
        var allElements:XMLList = definition.elements();
        var hasSiblings:Boolean = allElements.length() > 1 || context.hasContextSiblings;
        var requireChild:Boolean = isRequired && minOccurs > 0;

        // groupSatisfied should be true even if there are no elements to
        // process in the cases when we don't require any child elements to be
        // there.
        var groupSatisfied:Boolean = !requireChild;

        for each (var childDefinition:XML in allElements)
        {
            // all elements can appear in any order but no more than once each
            context.index = 0;
            context.anyIndex = 0;

            if (childDefinition.name() == constants.annotationQName)
            {
                // <annotation>
                groupSatisfied = true; // just move on
            }
            else if (childDefinition.name() == constants.elementTypeQName)
            {
                // <element>
                if (!decodeGroupElement(childDefinition, parent, valueElements, context, requireChild, hasSiblings))
                    break;
            }
            else if (childDefinition.name() == constants.anyQName)
            {
                // <any>
                if (!decodeAnyElement(childDefinition, parent, name, valueElements, context, requireChild))
                    break;
            }
            groupSatisfied = true;
        }

        return groupSatisfied;
    }

    public function decodeAnyType(parent:*, name:QName, valueElements:XMLList):void
    {
        for each (var elementVal:XML in valueElements)
        {
            var propertyName:QName = elementVal.name() as QName;
            var propertyVal:*;
            
            // First, check if an xsi:type attribute has been provided
            // on this element...
            var xsiType:QName = getXSIType(elementVal);
            if (xsiType != null)
            {
                propertyVal = createContent();
                decodeType(schemaManager.schemaDatatypes.anyTypeQName, propertyVal, propertyName, elementVal);
            }
            // Otherwise, we decode with anyType.
            else
            {
                propertyVal = marshallBuiltInType(schemaManager.schemaDatatypes.anyTypeQName, parent, propertyName, elementVal);
            }

            setValue(parent, propertyName, propertyVal, xsiType);
        }
    }



    /**
     * Decodes <any> elements from the context of a model group. The valueElements
     * list contains all top-level values for all elements in the current model
     * group. This method picks up the first undecoded value and 
     * 
     * Any content:
     *    (annotation?)
     * @private
     */
    public function decodeAnyElement(definition:XML, parent:*, name:QName, valueElements:XMLList,
                                context:DecodingContext = null, isRequired:Boolean = true):Boolean
    {
        if (context == null)
            context = new DecodingContext();
        var includedNamespaces:Array;
        var allowTargetNamespace:Boolean = true;
        var targetNamespace:String;
        var maxOccurs:uint = getMaxOccurs(definition);
        var minOccurs:uint = getMinOccurs(definition);

        if (definition != null)
        {
            var processContents:String = definition.@["processContents"];

            // Check for namespace rules
            var namespacesString:String = definition.@["namespace"];
            if (namespacesString != "" && namespacesString != "##any")
            {
                // Resolve current targetNamespace
                if (schemaManager.currentSchema != null
                    && schemaManager.currentSchema.targetNamespace != null)
                    targetNamespace = schemaManager.currentSchema.targetNamespace.uri;

                if (namespacesString == "##other")
                {
                    allowTargetNamespace = false;
                }
                else
                {
                    if (namespacesString.indexOf("##targetNamespace") >= 0)
                        namespacesString = namespacesString.replace("##targetNamespace", targetNamespace);

                    includedNamespaces = namespacesString.split(" ");
                }
            }
        }

        var applicableValues:XMLList = getApplicableValues(parent, valueElements, null, context, maxOccurs);

        for each (var element:XML in applicableValues)
        {
            var propertyName:QName = element.name() as QName;

            // Loop through the applicable elements and verify that each
            // one satisfies the constraints of the definition. If we reach
            // an element that does not fit the constraints, we stop decoding
            // and assume the remaining elements will be handled by another
            // definition.

            // If ##other was specified, we don't allow the targetNamespace
            if (!allowTargetNamespace
                && URLUtil.urisEqual(propertyName.uri, targetNamespace))
            {
                break;
            }

            // Otherwise, we check the property has an allowed namespace
            if (includeNamespace(propertyName.uri, includedNamespaces))
            {
                var any:*;
                var elementVal:*;

                if (processContents == "skip")
                    elementVal = element.toXMLString();
                else
                    elementVal = element;

                decodeAnyType(parent, propertyName, new XMLList(elementVal));

                // If this is the first element decoded with an <any> definition,
                // we save its index so that we can 
                if (context.anyIndex < 0)
                    context.anyIndex = context.index + 0;
                // We are done with this element, so increment the context counter.
                context.index++;
            }
        }
        // FIXME: return based on minOccurs
        return true;
    }

    /**
     * Decodes any attributes using the XML schema rules for attribute
     * wildcards.
     * 
     * FIXME: This needs further investigation of the XML schema spec for
     * wildcard rules and constraints.
     * 
     * @private
     */
    public function decodeAnyAttribute(definition:XML, parent:*, value:* = undefined, restriction:XML = null):void
    {
        // FIXME: honor restrictions for attributes

        if (value !== undefined && value is XML)
        {
            var xml:XML = value as XML;

            // Honor wildcard namespace definitions
            var includedNamespaces:Array;
            var allowTargetNamespace:Boolean = true;
            var targetNamespace:String;

            var namespacesString:String = definition.@["namespace"];
            if (namespacesString != "" && namespacesString != "##any")
            {
                // Resolve current targetNamespace
                if (schemaManager.currentSchema != null
                    && schemaManager.currentSchema.targetNamespace != null)
                    targetNamespace = schemaManager.currentSchema.targetNamespace.uri;

                if (namespacesString == "##other")
                {
                    allowTargetNamespace = false;
                }
                else
                {
                    if (namespacesString.indexOf("##targetNamespace") >= 0)
                        namespacesString = namespacesString.replace("##targetNamespace", targetNamespace);

                    includedNamespaces = namespacesString.split(" ");
                }
            }

            var attributes:XMLList = xml.attributes();
            for each (var attribute:XML in attributes)
            {
                var attributeName:QName = attribute.name() as QName;

                // If ##other was specified, we don't allow the targetNamespace
                if (!allowTargetNamespace && URLUtil.urisEqual(attributeName.uri, targetNamespace))
                    continue;

                // Otherwise, we check the property has an allowed namespace
                if (includeNamespace(attributeName.uri, includedNamespaces))
                {
                    var attributeValue:* = getAttribute(value, attributeName);
                    if (attributeValue != null)
                        setAttribute(parent, attributeName, attributeValue);
                }
            }
        }
    }


    /**
     * An attribute must be based on a simple type and thus will have simple
     * content encoded as a String.
     *
     * This function is used to encode an <code>attribute</code> that may be
     * named and registered as a top-level <code>schema</code> definition or
     * in-line from a <code>complexType</code>, <code>extension</code> or
     * <restriction> of either a <code>complexType</code> or
     * <code>simpleType</code>, or <code>attributeGroup</code>
     * definition in any aforementioned parent component.
     * 
     * If the <code>attribute</code> points to a named definition using a 
     * <code>ref</code> attribute, the reference is resolved to provide the
     * real definition of the attribute. If the reference cannot be resolved,
     * an error is thrown.
     * 
     * If the attribute defines a <code>fixed</code> constraint then any value
     * provided is ignored and the fixed value is used instead. If a value is
     * not provided and the attribute defines a <code>default</code>, the
     * default is used for the encoded attribute. Otherwise if an attribute is
     * marked as <code>optional</code> and a value is not provided it will be
     * skipped.
     *
     * @param parent The parent instance to which these attributes will be added.
     * @param definition The XML schema definition of the attribute.
     * @param value An object with a property name that matches the resolved
     * attribute name. The property value will be used as the encoded attribute
     * value.
     * 
     * FIXME: Attributes are expected to be simple values and must be ultimately
     * representable as a String. If a complex value is passed to this method
     * should we assume that we're always looking for a property with the same
     * name as the attribute? We may need to because if we have a ref then the
     * name is not known immediately...
     * 
     * @private
     */
    public function decodeAttribute(definition:XML, parent:*, value:* = undefined, restriction:XML = null):void
    {
        // <attribute ref="..."> may be used to point to a top-level attribute definition
        var ref:QName;
        if (definition.attribute("ref").length() == 1)
        {
            ref = schemaManager.getQNameForPrefixedName(definition.@ref, definition, true);
            definition = schemaManager.getNamedDefinition(ref, constants.attributeQName);

            if (definition == null)
                throw new Error("Cannot resolve attribute definition for '" + ref + "'");
        }

        // FIXME: Check restriction for prohibited attribute definitions too
        var attributeNameString:String = definition.@name.toString();
        var attributeUse:String = definition.attribute("use").toString();
        if (attributeUse != "prohibited")
        {
            var attributeName:QName = schemaManager.getQNameForAttribute(attributeNameString, getAttributeFromNode("form", definition));

            var attributeFixed:String = getAttributeFromNode("fixed", definition);
            if (attributeFixed != null)
            {
                value = attributeFixed;
            }
            else
            {
                value = getAttribute(value, attributeName);

                if (value === undefined)
                {
                    var attributeDefault:String = getAttributeFromNode("default", definition);
                    if (attributeDefault != null)
                        value = attributeDefault;
                }
            }

            var attributeValue:*;
            if (value !== undefined)
            {
                var typeDefinition:XML;
                attributeValue = createContent();

                // An <attribute> may declare a type="QName" attribute which
                // refers to either a built-in schema type or a previously
                // declared <simpleType>
                var typeName:String = getAttributeFromNode("type", definition);
                var attributeType:QName;
                if (typeName != null)
                    attributeType = schemaManager.getQNameForPrefixedName(definition.@type, definition);
                else
                    attributeType = schemaManager.schemaDatatypes.anySimpleTypeQName;

                if (attributeType != null)
                {
                    if (isBuiltInType(attributeType))
                    {
                        var result:* = marshallBuiltInType(attributeType, attributeValue, attributeName, value, restriction);
                        setValue(attributeValue, attributeName, result);
                    }
                    else
                    {
                        // <simpleType>
                        typeDefinition = schemaManager.getNamedDefinition(attributeType, constants.simpleTypeQName);
                        if (typeDefinition != null)
                            decodeSimpleType(typeDefinition, attributeValue, attributeName, value, restriction);
                        else
                            throw new Error("Cannot find simpleType " + attributeType + " for attribute " + attributeName);

                        // Then release the scope after we've found the attribute type
                        schemaManager.releaseScope();
                    }
                }
                else
                {            
                    // Otherwise, an <attribute> may define a single anonymous
                    // <simpleType> child in-line
                    typeDefinition = getSingleElementFromNode(definition, constants.simpleTypeQName);
                    if (typeDefinition != null)
                    {
                        decodeSimpleType(typeDefinition, attributeValue, attributeName, value, restriction);
                    }
                    else if (value != null)
                    {
                        // Finally, in the absence of type information we
                        // just get the attribute value as a String without
                        // restriction
                        attributeValue = value;
                    }
                }
            }

            // FIXME: Should we enforce use="required"?
            if (attributeValue != null)
            {
                setAttribute(parent, attributeName, attributeValue);
            }
        }

        // If we found our attribute by reference, we now release the schema scope
        if (ref != null)
            schemaManager.releaseScope();
    }

    /**
     * An <code>attributeGroup</code> definition may include a number of
     * <code>attribute</code> or <code>attributeGroup</code> children, all of
     * which ultimately combine to form a flat group of attributes for some
     * type. It may also specify <code>anyAttribute</code> which expands
     * the definition to accept attributes based on more general criteria
     * (such excluding or including attributes on namespace).
     *
     * This function is used to encode an <code>attributeGroup</code> that may 
     * be named and registered as a top-level <code>schema</code> definition or
     * in-line from a <code>complexType</code>, <code>extension</code> or
     * <restriction> of either a <code>complexType</code> or
     * <code>simpleType</code>, or even another <code>attributeGroup</code>
     * definition in any aforementioned parent component.
     * 
     * If the <code>attributeGroup</code> points to a named definition using a
     * ref attribute, the reference is resolved to provide the real definition
     * of the attributeGroup. If the reference cannot be resolved, an error is
     * thrown.
     * 
     * @param parent The parent instance to which these attributes will be added.
     * @param definition The XML schema definition of the attributeGroup.
     * @param value An object with property names that match the resolved
     * attribute names in the group. The property values will be used as the
     * encoded attribute values. This argument may be omitted if each attribute
     * in the group has a fixed or default value.
     * 
     * @private
     */
    public function decodeAttributeGroup(definition:XML, parent:*, value:* = undefined, restriction:XML = null):void
    {
        // <attributeGroup ref="..."> may be used to point to a top-level
        // attributeGroup definition which must first be resolved.
        var ref:QName;
        if (definition.attribute("ref").length() == 1)
        {
            ref = schemaManager.getQNameForPrefixedName(definition.@ref, definition, true);
            definition = schemaManager.getNamedDefinition(ref, constants.attributeGroupQName);

            if (definition == null)
                throw new Error("Cannot resolve attributeGroup definition for '" + ref + "'");
        }

        // <attribute>
        var attributes:XMLList = definition.elements(constants.attributeQName);
        for each (var attribute:XML in attributes)
        {
            decodeAttribute(attribute, parent, value, restriction);
        }

        // <attributeGroup>
        var attributeGroups:XMLList = definition.elements(constants.attributeGroupQName);
        for each (var attributeGroup:XML in attributeGroups)
        {
            decodeAttributeGroup(attributeGroup, parent, value, restriction);
        }

        // <anyAttribute>
        var anyAttribute:XML = getSingleElementFromNode(definition, constants.anyAttributeQName);
        if (anyAttribute != null)
        {
            decodeAnyAttribute(anyAttribute, parent, value, restriction);
        }

        // If we found our attributeGroup by reference, we now release the schema scope
        if (ref != null)
            schemaManager.releaseScope();
    }

    /**
     * choice:
     *    (annotation?, (element | group | choice | sequence | any)*)
     * 
     * @param context A DecodingContext instance. Used to keep track
     * of the index of the element being processed in the current model
     * group.
     * 
     * @private
     */
    public function decodeChoice(definition:XML, parent:*, name:QName, valueElements:XMLList,
                                context:DecodingContext = null, isRequired:Boolean = true):Boolean
    {
        if (context == null)
            context = new DecodingContext();
        var maxOccurs:uint = getMaxOccurs(definition);
        var minOccurs:uint = getMinOccurs(definition);

        // If maxOccurs is 0 this choice must not be present.
        if (maxOccurs == 0)
            return false;
        // If minOccurs == 0 the choice is optional so it can be omitted if
        // a value was not provided.
        if (valueElements == null && minOccurs == 0)
            return true;

        var choiceElements:XMLList = definition.elements();
        // If no elements in the choice definition, we can say the choice was
        // satisfied no matter what value is provided.
        if (choiceElements.length() == 0)
            return true;

        var choiceSatisfied:Boolean;
        var lastIndex:uint;
        var choiceOccurs:uint;

        for (choiceOccurs = 0; choiceOccurs < maxOccurs; choiceOccurs++)
        {
            lastIndex = context.index + 0;
            choiceSatisfied = false;
            //We loop through the possible choices until one of them consumes
            //at least one of the valueElements. However, any of the choiceElements
            //with minOccurs=0 could potentially satisfy the choice.
            for each (var childDefinition:XML in choiceElements)
            {
                if (childDefinition.name() == constants.elementTypeQName)
                {
                    // <element>
                    choiceSatisfied ||= decodeGroupElement(childDefinition, parent, valueElements, context, false);
                    if (context.index > lastIndex) break;
                }
                else if (childDefinition.name() == constants.sequenceQName)
                {
                    // <sequence>
                    choiceSatisfied ||= decodeSequence(childDefinition, parent, name, valueElements, context, false);
                    if (context.index > lastIndex) break;
                }
                else if (childDefinition.name() == constants.groupQName)
                {
                    // <group>
                    choiceSatisfied ||= decodeGroupReference(childDefinition, parent, name, valueElements, context, false);
                    if (context.index > lastIndex) break;
                }
                else if (childDefinition.name() == constants.choiceQName)
                {
                    // <choice>
                    choiceSatisfied ||= decodeChoice(childDefinition, parent, name, valueElements, context, false);
                    if (context.index > lastIndex) break;
                }
                else if (childDefinition.name() == constants.anyQName)
                {
                    // <any>
                    choiceSatisfied ||= decodeAnyElement(childDefinition, parent, name, valueElements, context, false);
                    if (context.index > lastIndex) break;
                }
            }
            if (!choiceSatisfied)
            {
                break;
            }
        }

        if (choiceOccurs < minOccurs)
        {
            if (isRequired && strictOccurenceBounds)
                throw new Error("Value supplied for choice "+ name.toString() +" occurs " +
                    choiceOccurs + " times which falls short of minOccurs " + minOccurs + ".");
            else
                return false;
        }

        return true;
     }


    /**
     * Derivation by restriction takes an existing type as the base and creates
     * a new type by limiting its allowed content to a subset of that allowed
     * by the base type. Derivation by extension takes an existing type as the
     * base and creates a new type by adding to its allowed content.
     * 
     * complexContent:
     * (annotation?, (restriction | extension))
     * 
     * @private
     */
    public function decodeComplexContent(definition:XML, parent:*, name:QName, value:*, context:DecodingContext):void
    {
        var childDefinition:XML = getSingleElementFromNode(definition, constants.extensionQName, constants.restrictionQName);

        if (childDefinition.name() == constants.extensionQName)
        {
            decodeComplexExtension(childDefinition, parent, name, value, context);
        }
        else if (childDefinition.name() == constants.restrictionQName)
        {
            decodeComplexRestriction(childDefinition, parent, name, value);
        }
    }
    

    /**    
     * complexContent:
     *   extension:
     *     (annotation?, ((group | all | choice | sequence)?, ((attribute | attributeGroup)*, anyAttribute?), (assert | report)*))
     * 
     * @private
     */
    public function decodeComplexExtension(definition:XML, parent:*, name:QName, value:*, context:DecodingContext=null):void
    {
        if (context == null)
            context = new DecodingContext();

        // We need to remember that the base and the extension definitions are
        // really sibling elements, although they are not visible from each other's
        // definitions.
        context.hasContextSiblings = true;    

        var baseName:String = getAttributeFromNode("base", definition);
        if (baseName == null)
            throw new Error ("A complexContent extension must declare a base type.");

        var baseType:QName = schemaManager.getQNameForPrefixedName(baseName, definition);

        // complexContent base type must be a complexType
        var baseDefinition:XML = schemaManager.getNamedDefinition(baseType, constants.complexTypeQName);
        if (baseDefinition == null)
            throw new Error("Cannot find base type definition '" + baseType + "'");

        // FIXME: Should we care if base type is marked final?

        // First encode all of the properties of the base type
        decodeComplexType(baseDefinition, parent, name, value, null, context);

        // Then release the scope of the base type definition
        schemaManager.releaseScope();


        var childElements:XMLList = definition.elements();
        var valueElements:XMLList = new XMLList();
        if (value is XML)
            valueElements = (value as XML).elements();
        else if (value is XMLList)
            valueElements = value;

        for each (var childDefinition:XML in childElements)
        {
            if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                decodeSequence(childDefinition, parent, name, valueElements, context);
            }
            else if (childDefinition.name() == constants.groupQName)
            {
                // <group>
                decodeGroupReference(childDefinition, parent, name, valueElements, context);
            }
            else if (childDefinition.name() == constants.allQName)
            {
                // <all>
                decodeAll(childDefinition, parent, name, valueElements, context);
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                decodeChoice(childDefinition, parent, name, valueElements, context);
            }
            else if (childDefinition.name() == constants.attributeQName)
            {
                // <attribute>
                decodeAttribute(childDefinition, parent, value);
            }
            else if (childDefinition.name() == constants.attributeGroupQName)
            {
                // <attributeGroup>
                decodeAttributeGroup(childDefinition, parent, value);
            }
            else if (childDefinition.name() == constants.anyAttributeQName)
            {
                // <anyAttribute>
                decodeAnyAttribute(childDefinition, parent, value);
            }
        }
    }
    
    /**    
     * complexContent:
     *   restriction:
     *     (annotation?, (group | all | choice | sequence)?, ((attribute | attributeGroup)*, anyAttribute?), (assert | report)*)
     * 
     * @private
     */
    public function decodeComplexRestriction(restriction:XML, parent:*, name:QName, value:*):void
    {
        var baseName:String = getAttributeFromNode("base", restriction);
        if (baseName == null)
            throw new Error ("A complexContent restriction must declare a base type.");

        var baseType:QName = schemaManager.getQNameForPrefixedName(baseName, restriction);

        // FIXME: Validate complex restriction based on the base type definition
        // complexContent base type must be a complexType
        // var baseDefinition:XML = schemaManager.getNamedDefinition(baseType, constants.complexTypeQName);
        // if (baseDefinition == null)
        //    throw new Error("Cannot find base type definition '" + baseType + "'");

        // FIXME: Should we care if base type is marked final?

        var childElements:XMLList = restriction.elements();
        var valueElements:XMLList = new XMLList();
        if (value is XML)
            valueElements = (value as XML).elements();
        else if (value is XMLList)
            valueElements = value;
            
        for each (var childDefinition:XML in childElements)
        {
            if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                decodeSequence(childDefinition, parent, name, valueElements);
            }
            else if (childDefinition.name() == constants.groupQName)
            {
                // <group>
                decodeGroupReference(childDefinition, parent, name, valueElements);
            }
            else if (childDefinition.name() == constants.allQName)
            {
                // <all>
                decodeAll(childDefinition, parent, name, valueElements);
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                decodeChoice(childDefinition, parent, name, valueElements);
            }
            else if (childDefinition.name() == constants.attributeQName)
            {
                // <attribute>
                decodeAttribute(childDefinition, parent, value);
            }
            else if (childDefinition.name() == constants.attributeGroupQName)
            {
                // <attributeGroup>
                decodeAttributeGroup(childDefinition, parent, value);
            }
            else if (childDefinition.name() == constants.anyAttributeQName)
            {
                // <anyAttribute>
                decodeAnyAttribute(childDefinition, parent, value);
            }
        }
    }
    /**
     * @private
     */
    public function decodeComplexType(definition:XML, parent:*, name:QName, value:*,
                    restriction:XML=null, context:DecodingContext=null):void
    {
        if (parent is ContentProxy)
            ContentProxy(parent).object_proxy::isSimple = false;

        var childElements:XMLList = definition.elements();
        var valueElements:XMLList = new XMLList();
        if (value is XML)
            valueElements = (value as XML).elements();
        else if (value is XMLList)
            valueElements = value;

        // FIXME: Investigate if we need to support "base" attribute on
        // complexType as short-cut as seen in some examples...

        for each (var childDefinition:XML in childElements)
        {
            if (childDefinition.name() == constants.simpleContentQName)
            {
                // <simpleContent>
                decodeSimpleContent(childDefinition, parent, name, value, restriction);
            }
            else if (childDefinition.name() == constants.complexContentQName)
            {
                // <complexContent>
                decodeComplexContent(childDefinition, parent, name, value, context);
            }
            else if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                decodeSequence(childDefinition, parent, name, valueElements, context);
            }
            else if (childDefinition.name() == constants.groupQName)
            {
                // <group>
                decodeGroupReference(childDefinition, parent, name, valueElements, context);
            }
            else if (childDefinition.name() == constants.allQName)
            {
                // <all>
                decodeAll(childDefinition, parent, name, valueElements, context);
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                decodeChoice(childDefinition, parent, name, valueElements, context);
            }
            else if (childDefinition.name() == constants.attributeQName)
            {
                // <attribute>
                decodeAttribute(childDefinition, parent, value, restriction);
            }
            else if (childDefinition.name() == constants.attributeGroupQName)
            {
                // <attributeGroup>
                decodeAttributeGroup(childDefinition, parent, value, restriction);
            }
            else if (childDefinition.name() == constants.anyAttributeQName)
            {
                // <anyAttribute>
                decodeAnyAttribute(childDefinition, parent, value, restriction);
            }
        }
    }
    
    
    
    /**
     * Used to decode a local element definition. This element may also simply
     * refer to a top level element.
     * 
     * Element content:
     * (annotation?, ((simpleType | complexType)?, (unique | key | keyref)*))
     * 
     * FIXME: Support substitutionGroup, block and redefine?
     * FIXME: Do we care about abstract or final?
     * 
     * FIXME: Remove isRequired if not necessary...
     * 
     * @private
     */
    public function decodeGroupElement(definition:XML, parent:*, valueElements:XMLList,
                                context:DecodingContext = null, isRequired:Boolean = true, hasSiblings:Boolean = true):Boolean
    {
        if (context == null)
        	context = new DecodingContext();

        // <element minOccurs="..." maxOccurs="..."> occur on the local element,
        // not on a referent, so we capture this information first.
        var maxOccurs:uint = getMaxOccurs(definition);
        var minOccurs:uint = getMinOccurs(definition);

        // <element ref="..."> may be used to point to a top-level element definition
        var ref:QName;
        if (definition.attribute("ref").length() == 1)
        {
            ref = schemaManager.getQNameForPrefixedName(definition.@ref, definition, true);
            definition = schemaManager.getNamedDefinition(ref, constants.elementTypeQName);
            if (definition == null)
                throw new Error("Cannot resolve element definition for ref '" + ref + "'");
        }

        // If the maximum occurence is 0 this element must not be present.
        if (maxOccurs == 0)
            return true;

        var elementName:String = definition.@name.toString();
        var elementQName:QName = schemaManager.getQNameForElement(elementName, getAttributeFromNode("form", definition));

		// Now that we've resolved the real element name, get the applicable
        // values from the given valueElements
        var applicableValues:XMLList = getApplicableValues(parent, valueElements,
                    elementQName, context, maxOccurs);

		// If we have a single xml node with that name and it has the xsi:nil
		// attribute set to true, we set a null 
        if (applicableValues.length() == 1 && isXSINil(applicableValues[0]))
        {
            setValue(parent, elementQName, null);
            context.index++;
            return true;
        }

        // If maxOccurs > 1 we always create an array, even if it will be empty or null.
        if (maxOccurs > 1)
        {
            // If we have a type, provide this when creating an array in case
            // a custom strongly typed collection class has been registered.
            var typeAttribute:String = getAttributeFromNode("type", definition);
            var typeQName:QName;
            if (typeAttribute != null)
            {
                typeQName = schemaManager.getQNameForPrefixedName(typeAttribute, definition);
            }

            var emptyArray:* = createIterableValue(typeQName);

            // If this is not the only property in the definition, we assign the
            // array on a named property on the parent.
            if (hasSiblings)
            {
                setValue(parent, elementQName, emptyArray, typeQName);
            }
            else
            {
                // If this is a "wrapped array", the iterable value should be assigned
                // as the value of the parent itself. However, we only replace
                // the parent if it hasn't been created already. (It could be
                // created if the parent QName has a registered collectionClass
                // in the SchemaTypeRegistry - see bug FB-11399).
                if (!(parent is ContentProxy && parent.object_proxy::content != undefined))
                {
                    setValue(parent, null, emptyArray, typeQName);
                }
            }
        }

        // If minOccurs == 0 the element is optional so we can omit it if
        // a value was not provided.
        if (applicableValues.length() == 0)
        {
            if (minOccurs == 0)
            	return true;
            else
            	return false;
        }

        var element:*;

        // We treat maxOccurs="1" as a special case and not check the
        // occurence because we need to pass through values to SOAP
        // encoded Arrays which do not rely on minOccurs/maxOccurs
        if (maxOccurs == 1)
        {
            element = decodeElementTopLevel(definition, elementQName, parseValue(elementQName, applicableValues));
            setValue(parent, elementQName, element);
            context.index++;
        }
        else if (maxOccurs > 1)
        {
            // If maxOccurs is greater than 1 then we would expect an
            // Array of values
            if (applicableValues.length() < minOccurs)
            {
                if (strictOccurenceBounds)
                    throw new Error("Value supplied for element '" + elementQName +
                        "' occurs " + applicableValues.length() + " times which falls short of minOccurs " +
                        minOccurs + ".");
                else
                    return false;
            }

            if (applicableValues.length() > maxOccurs)
            {
                if (strictOccurenceBounds)
                    throw new Error("Value supplied for element of type '" + elementQName +
                        "' occurs " + applicableValues.length() + " times which exceeds maxOccurs " +
                        maxOccurs + ".");
                else
                    return false;
            }
            
            var elementOccurs:uint;
            for (elementOccurs = 0; elementOccurs < maxOccurs
                                 && elementOccurs < applicableValues.length();
                                 elementOccurs++)
            {
                var item:XML = applicableValues[elementOccurs];
                
                element = decodeElementTopLevel(definition, elementQName, item);
                setValue(parent, elementQName, element);
                context.index++;
            }
        }

        // If we found our element by reference, we now release the schema scope
        if (ref != null)
            schemaManager.releaseScope();
        
        return true;
    }

    /**
     * Element content:
     * (annotation?, ((simpleType | complexType)?, (unique | key | keyref)*))
     * 
     * @private
     */
    public function decodeElementTopLevel(definition:XML, elementQName:QName, value:*):*
    {
        var content:*;

        // <element nillable="..."> may be set to true to allow for elements to
        // be present but encode a nil value.
        var nillable:Boolean = definition.@nillable.toString() == "true" ? true : false;
        if (nillable && value == null)
        {
            return value;
        }

        // An <element fixed="..."> restriction ignores a supplied value. The
        // fixed attribute content will be used to encode the element.
        var fixedValue:String = getAttributeFromNode("fixed", definition);
        if (fixedValue != null)
            value = fixedValue;

        // <element default="..." may be used in the absence of a value.
        var defaultValue:String = getAttributeFromNode("default", definition);
        if (!nillable && value == null)
            value = defaultValue;

        // If we're still null by now we must set null.
        if (value == null)
        {
            return value;
        }

        var processedElement:*;

        // Check for a simple def first, falling back to complex type handling
        // and then default handling.
        var attributeValue:String = getAttributeFromNode("type", definition);
        if (attributeValue != null)
        {
            var typeQName:QName = schemaManager.getQNameForPrefixedName(attributeValue, definition);
            content = createContent(typeQName);
            decodeType(typeQName, content, elementQName, value);
            return content;
        }
        // Next, check if the element has an in-line <complexType> or
        // <simpleType> definition.
        else if (definition.hasComplexContent())
        {
            var typeDefinition:XML = getSingleElementFromNode(definition,
                                        constants.complexTypeQName,
                                        constants.simpleTypeQName);

            content = createContent();

            // FIXME: If the value was fixed or default, we're assuming that
            // the element complexType does not contain any elements in its
            // definition, i.e. effectively a type with simple content.
            // Is this acceptable?

            if (typeDefinition.name() == constants.complexTypeQName)
            {
                // <complexType>
                decodeComplexType(typeDefinition, content, elementQName, value);
            }
            else if (typeDefinition.name() == constants.simpleTypeQName)
            {
                // <simpleType>
                decodeSimpleType(typeDefinition, content, elementQName, value);
            }

            // FIXME: Support unique, key, keyref, field, selector
            return content;
        }
        else
        {
            // The default for fixed or default values is the built-in
            // string type, otherwise the built-in anyType is used
            var typeQName2:QName;
            if (fixedValue != null || defaultValue != null)
                typeQName2 = schemaManager.schemaDatatypes.stringQName;
            else
                typeQName2 = constants.anyTypeQName;

            // FIXME: Support <element substitutionGroup="...">

            content = createContent(typeQName2);
            decodeType(typeQName2, content, elementQName, value);
            return content;
        }
    }
    
    /**
     * The <code>group</code> element allows partial (or complete) content
     * models to be reused in complex types.
     * 
     * group:
     * (annotation?, (all | choice | sequence)?)
     * 
     * @private
     */
    public function decodeGroupReference(definition:XML, parent:*, name:QName, valueElements:XMLList,
                                context:DecodingContext = null, isRequired:Boolean = true):Boolean
    {
        // <group ref="..."> must be used to point to a top-level group definition
        var ref:QName;
        if (definition.attribute("ref").length() == 1)
        {
            ref = schemaManager.getQNameForPrefixedName(definition.@ref, definition, true);
            definition = schemaManager.getNamedDefinition(ref, constants.groupQName);

            if (definition == null)
                throw new Error("Cannot resolve group definition for '" + ref + "'");
        }
        else
        {
        	// FIXME: consider looking for inline definition if ref is not provided.
            throw new Error("A group reference element must have the ref attribute.");
        }

        var groupElements:XMLList = definition.elements();
        var groupSatisfied:Boolean = false;
        for each (var childDefinition:XML in groupElements)
        {
            if (childDefinition.name() == constants.allQName)
            {
                // <all>
                groupSatisfied = decodeAll(childDefinition, parent, name, valueElements, context, isRequired);
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                groupSatisfied = decodeChoice(childDefinition, parent, name, valueElements, context, isRequired);
            }
            else if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                groupSatisfied = decodeSequence(childDefinition, parent, name, valueElements, context, isRequired);
            }
        }
        // We found our group by reference, so we must release the schema scope
        schemaManager.releaseScope();
        return groupSatisfied;
    }

    /**
     * sequence:
     *    (annotation?, (element | group | choice | sequence | any)*)
     * 
     * @private
     */
    public function decodeSequence(definition:XML, parent:*, name:QName, valueElements:XMLList,
                                    context:DecodingContext = null, isRequired:Boolean = true):Boolean
    {
        if (context == null)
            context = new DecodingContext();
        var maxOccurs:uint = getMaxOccurs(definition);
        var minOccurs:uint = getMinOccurs(definition);

        // If maxOccurs is 0 this sequence must not be present.
        if (maxOccurs == 0)
            return false;
        // If minOccurs == 0 the sequence is optional so it can be omitted if
        // a value was not provided.
        if ((valueElements == null || valueElements.length() == 0) && minOccurs == 0)
            return true;

        var sequenceElements:XMLList = definition.elements();
        var sequenceOccurs:uint;
        
        // hasSiblings is passed down to decodeGroupElement to control whether
        // a maxOccurs > 1 element should be decoded as a nested array, or should
        // replace the parent. This handles the "wrapped array" special case,
        // where a sequence of one element with maxoccurs > 1 is meant to represent
        // an array, and not an object with a single property which is an array.
        // However, even if the current sequence definition matches the convention,
        // we should avoid assigning the array as the parent object if the sequence
        // context allows other elements outside this sequence to be siblings of
        // the array (such as elements defined in the base type of an extension).
        var hasSiblings:Boolean = sequenceElements.length() > 1 || context.hasContextSiblings;
        
        // If there are no children in the sequence definition, we don't need
        // to do anything. Any content satisfies the sequence.
        if (sequenceElements.length() == 0)
            return true;
        
        for (sequenceOccurs = 0; sequenceOccurs < maxOccurs; sequenceOccurs++)
        {
            // We loop through the children of the sequence definition. We require
            // all child definitions to be satisfied, unless we have reached minOccurs
            // or the sequence itself doesn't need to be satisfied.
            var requireChild:Boolean = isRequired && sequenceOccurs < minOccurs;
            var sequenceSatisfied:Boolean = true;
            var lastIndex:uint = context.index + 0; // + 0 to get a different instance of uint

            for each (var childDefinition:XML in sequenceElements)
            {
            	sequenceSatisfied = false;
				if (childDefinition.name() == constants.annotationQName)
				{
					// <annotation>
					sequenceSatisfied = true; // just move on
				}
                if (childDefinition.name() == constants.elementTypeQName)
                {
                    // <element>
                    if (!decodeGroupElement(childDefinition, parent, valueElements, context, requireChild, hasSiblings))
                        break;
                }
                else if (childDefinition.name() == constants.groupQName)
                {
                    // <group>
                    if (!decodeGroupReference(childDefinition, parent, name, valueElements, context, requireChild))
                        break;
                }
                else if (childDefinition.name() == constants.choiceQName)
                {
                    // <choice>
                    if (!decodeChoice(childDefinition, parent, name, valueElements, context, requireChild))
                        break;
                }
                else if (childDefinition.name() == constants.sequenceQName)
                {
                    // <sequence>
                    if (!decodeSequence(childDefinition, parent, name, valueElements, context, requireChild))
                        break;
                }
                else if (childDefinition.name() == constants.anyQName)
                {
                    // <any>
                    if (!decodeAnyElement(childDefinition, parent, name, valueElements, context, requireChild))
                        break;
                }
                sequenceSatisfied = true;
            }
            // If the sequence had to be satisfied, but wasn't, throw an error.
            if (!sequenceSatisfied && requireChild)
            {
                if (strictOccurenceBounds)
                    throw new Error("Cannot find value for definition " + childDefinition.toXMLString() + " in sequence.");
                else
                    return false;
            }
            // If no more valueElements were consumed, it means no more valueElements
            // will be consumed no matter how much we keep looping. So we break from
            // the loop.
            if (lastIndex == context.index)
            {
                // If the sequence was satisfied by no elements being consumed, it means that
                // empty content is a valid occurence of the sequence. If we are still short of
                // the sequence's minOccurs, we can assume the sequence occured minOccurs times.
                if (sequenceSatisfied)
                {
                    // Since sequenceOccurs is 0-based, it would be the actual occurence count if
                    // the loop exits normally (i.e. passes through sequenceOccurs++ one final time
                    // when the loop condition is no longer true). Because we break from the loop,
                    // we need sequenceOccurs++ to have the accurate value.
                    sequenceOccurs++;
                    sequenceOccurs = sequenceOccurs > minOccurs ? sequenceOccurs : minOccurs;
                }
                break;
            }
        }

        if (sequenceOccurs < minOccurs)
        {
            if (isRequired && strictOccurenceBounds)
                throw new Error("Value supplied for sequence "+ name.toString() +" occurs " +
                    sequenceOccurs + " times which falls short of minOccurs " + minOccurs + ".");
            else
                return false;
        }

        return true;
    }
    


    /**
     * <code>simpleContent</code> specifies that the content will be simple text
     * only, that is it conforms to a simple type and will not contain elements,
     * although it may also define attributes.
     * 
     * A simpleContent must be defined with an extension or a restriction. An
     * extension specifies the attribute definitions that are to be added to the
     * type and the base attribute specifies from which simple data type this
     * custom type is defined. A restriction for simpleContent is less common,
     * although it may be used to prohibit attributes in derived types also
     * with simpleContent.
     * 
     * simpleContent
     *     (annotation?, (restriction | extension))
     * 
     * @private
     */
    public function decodeSimpleContent(definition:XML, parent:*, name:QName, value:*, restriction:XML = null):void
    {
        var childDefinition:XML = getSingleElementFromNode(definition, constants.extensionQName, constants.restrictionQName);


        if (parent is ContentProxy)
            ContentProxy(parent).object_proxy::isSimple = true;


        if (childDefinition != null)
        {
            var baseName:String = getAttributeFromNode("base", childDefinition);
            if (baseName == null)
                throw new Error ("A simpleContent extension or restriction must declare a base type.");

            var baseType:QName = schemaManager.getQNameForPrefixedName(baseName, childDefinition);

            if (!isBuiltInType(baseType))
            {
                var baseDefinition:XML = schemaManager.getNamedDefinition(baseType,
                        constants.complexTypeQName, constants.simpleTypeQName);
                if (baseDefinition == null)
                    throw new Error("Cannot find base type definition '" + baseType + "'");

                // We found our baseType by name so we now release the schema scope
                schemaManager.releaseScope();
            }

            // FIXME: Should we care if base type is marked final?
            var simpleValue:*;

            // <extension>
            if (childDefinition.name() == constants.extensionQName)
            {
                // simpleContent base type must be a simpleType or a complexType
                // that ultimately has simpleContent (FIXME: we currently don't verify the latter)
                if (isBuiltInType(baseType))
                {
                    simpleValue = getSimpleValue(value, name);
                    var result:* = marshallBuiltInType(baseType, parent, name, simpleValue, restriction);
                    setSimpleValue(parent, name, result, baseType);
                }
                else
                {
                    decodeType(baseType, parent, name, value, restriction);
                }

                var extensions:XMLList = childDefinition.elements();
                for each (var extensionChild:XML in extensions)
                {
                    if (extensionChild.name() == constants.attributeQName)
                    {
                        // <attribute>
                        decodeAttribute(extensionChild, parent, value, restriction);
                    }
                    else if (extensionChild.name() == constants.attributeGroupQName)
                    {
                        // <attributeGroup>
                        decodeAttributeGroup(extensionChild, parent, value, restriction);
                    }
                    else if (extensionChild.name() == constants.anyAttributeQName)
                    {
                        // <anyAttribute>
                        decodeAnyAttribute(extensionChild, parent, value, restriction);
                    }
                }
            }
            // <restriction>
            else if (childDefinition.name() == constants.restrictionQName)
            {
                simpleValue = getSimpleValue(value, name);
                decodeSimpleRestriction(childDefinition, parent, name, simpleValue);
            }
        }
    }

    /**
     * A <code>simpleType</code> may declare a list of space separated
     * simple content for a single value.
     * 
     * <list
     *     id = ID
     *     itemType = QName >
     *     Content: (annotation?, simpleType?)
     * </list>
     * 
     * @private
     */
    public function decodeSimpleList(definition:XML, parent:*, name:QName, value:*, restriction:XML = null):void
    {
        var itemTypeAttribute:String = definition.@itemType;
        
        var itemTypeQName:QName;
        var itemDefinition:XML;
        
        // The simple type of each item in the list can be specified by
        // either an itemType attribute, or a simpleType inline definition.
        if (itemTypeAttribute != "")
            itemTypeQName = schemaManager.getQNameForPrefixedName(itemTypeAttribute, definition);
        else
            itemDefinition = getSingleElementFromNode(definition, constants.simpleTypeQName);
        
        var listValue:String = "";

        if (!TypeIterator.isIterable(value))
            value = [value];

        var iter:TypeIterator = new TypeIterator(value);
        while (iter.hasNext())
        {
            var item:* = iter.next();
            var tempValue:* = createContent();
            
            if (itemTypeQName != null)
                decodeType(itemTypeQName, tempValue, name, item, restriction);
            else
                decodeSimpleType(itemDefinition, tempValue, name, item, restriction);
                
            var encodedItem:String = tempValue != null ? tempValue.toString() : "";
            listValue = listValue.concat(encodedItem);
            
            if (iter.hasNext())
                listValue = listValue.concat(" ");
        }

        setValue(parent, name, listValue, itemTypeQName);
    }

    /**
     * simpleType:
     *   restriction: (annotation?, (simpleType?,
     *       (minExclusive | minInclusive | maxExclusive | maxInclusive |
     *       totalDigits | fractionDigits | maxScale | minScale | length |
     *       minLength | maxLength | enumeration | whiteSpace | pattern)*))
     * 
     * @private
     */
    public function decodeSimpleRestriction(restriction:XML, parent:*, name:QName, value:*):void
    {
        var simpleTypeDefinition:XML = getSingleElementFromNode(restriction, constants.simpleTypeQName);
        if (simpleTypeDefinition != null)
        {
            decodeSimpleType(simpleTypeDefinition, parent, name, value, restriction);
        }
        else
        {
            var baseName:String = getAttributeFromNode("base", restriction);
            var baseType:QName = schemaManager.getQNameForPrefixedName(baseName, restriction);
            decodeType(baseType, parent, name, value, restriction);
        }
    }

    /**
     * <simpleType
     *     final = (#all | List of (list | union | restriction | extension))
     *     id = ID
     *     name = NCName>
     *     Content: (annotation?, (restriction | list | union))
     * </simpleType>
     * 
     * @private
     */
    public function decodeSimpleType(definition:XML, parent:*, name:QName, value:*, restriction:XML = null):void
    {
        var definitionChild:XML = getSingleElementFromNode(definition,
                            constants.restrictionQName,
                            constants.listQName,
                            constants.unionQName);

        if (definitionChild.name() == constants.restrictionQName)
        {    
            // <restriction>
            decodeSimpleRestriction(definitionChild, parent, name, value);
        }
        else if (definitionChild.name() == constants.listQName)
        {
            // <list>
            decodeSimpleList(definitionChild, parent, name, value, restriction);
        }
        else if (definitionChild.name() == constants.listQName)
        {
            // <union>
            decodeSimpleUnion(definitionChild, parent, name, value, restriction);
        }
    }

    /**
     * <union
     *     id = ID
     *     memberTypes = List of QName >
     *     Content: (annotation?, simpleType*)
     * </union>
     * 
     * FIXME: This needs a lot of work.
     * 
     * @private
     */
    public function decodeSimpleUnion(definition:XML, parent:*, name:QName, value:*, restriction:XML = null):void
    {
        var memberList:String = getAttributeFromNode("memberTypes", definition);
        var memberArray:Array = memberList.split(" ");
        var type:QName;
        var args:*;

        //as the memeber types can contain simple data types like xsd:string or tns:address
        for (var i:int = 0; i < memberArray.length; i++)
        {
            var prefixedName:String = memberArray[i];
            var simpleType:QName = schemaManager.getQNameForPrefixedName(prefixedName, definition);

            if (!isBuiltInType(simpleType))
            {
                args = getValue(value, simpleType);
                if (args !== undefined)
                {
                    type = simpleType;
                    break;
                }
            }
        }

        if (!type)
        {
            type = schemaManager.schemaDatatypes.stringQName;
        }

        var result:* = marshallBuiltInType(type, parent, name, value, restriction);
        setValue(parent, name, result, type);
    }




    /**
     * @private
     */
    public function decodeType(type:QName, parent:*, name:QName, value:*, restriction:XML = null):void
    {
        if (isBuiltInType(type))
        {
            var result:* = marshallBuiltInType(type, parent, name, value, restriction);
            setValue(parent, name, result, type);
        }
        else
        {
            var definition:XML = schemaManager.getNamedDefinition(type,
                constants.complexTypeQName, constants.simpleTypeQName);

            if (definition == null)
                throw new Error("Cannot find definition for type '" + type + "'");

            // Short circuit if value is xsi:nil
            if (isXSINil(value))
            {
                setValue(parent, name, null, type);
                return;
            }

            var definitionType:QName = definition.name() as QName;
            if (definitionType == constants.complexTypeQName)
            {
                // <complexType>
                decodeComplexType(definition, parent, name, value, restriction);
            }
            else if (definitionType == constants.simpleTypeQName)
            {
                // <simpleType>
                decodeSimpleType(definition, parent, name, value, restriction);
            }
            else
            {
                throw new Error("Invalid type definition " + definitionType);
            }

            // We found our type by name so we finally release the schema scope
            schemaManager.releaseScope();
        }

        // Record XSI type information for ObjectProxy or implementations of
        // IXMLSchemaInstance 
        setXSIType(parent, type);
    }

    /**
     * This function controls the marshalling of XML values into
     * ActionScript values for a <code>simpleType</code>.
     * 
     * All simple types are derived from a built-in schema simple types, with
     * the parent being <code>allSimpleTypes</code>. Simple types must not
     * have complex content as they are also used to describe the values of
     * attributes.
     * 
     * If this method is called with the schema <code>anyType</code> and a
     * value with complex content it redirects to decodeAny to handle generic
     * complex types without a type definition.
     * 
     * @private
     */
    public function marshallBuiltInType(type:QName, parent:*, name:QName, value:*, restriction:XML = null):*
    {
        if (type == constants.anyTypeQName && !isSimpleValue(value))
        {
            var content:* = createContent();
            if (content is ContentProxy)
                ContentProxy(content).object_proxy::isSimple = false; // The XML value has complex content.
            var valueList:XMLList;// = new XMLList(value);
            if (value is XML)
                valueList = value.elements();
            else
                valueList = new XMLList(value);
            decodeAnyType(content, name, valueList);
            return content;
        }
        else
        {
            return schemaManager.unmarshall(value, type, restriction);
        }
    }

    /**
     * Resets the decoder to its initial state, including resetting any 
     * Schema scope to the top level and releases the current XML document by
     * setting it to null.
     */
    override public function reset():void
    {
        super.reset();
        document = null;
    }

    /**
     * @private
     */
    public function getAttribute(parent:*, name:*):*
    {
        var result:*;
        var attribute:XMLList;
        if (parent is XML)
        {
            attribute = XML(parent).attribute(name);
            result = parseValue(name, attribute);
        }
        else if (parent is XMLList)
        {
            attribute = XMLList(parent).attribute(name);
            result = parseValue(name, attribute);
        }

        return result;
    }
    
    /**
     * @private
     */
    public function hasAttribute(parent:*, name:*):Boolean
    {
        return (getAttribute(parent, name) !== undefined);
    }

    /**
     * @private
     */
    public function setAttribute(parent:*, name:*, value:*):void
    {
        if (parent is ContentProxy)
        {
            var parentProxy:ContentProxy = parent as ContentProxy;
            if (parentProxy.object_proxy::isSimple)
            {
                var existingContent:* = parentProxy.object_proxy::content;
                if (!(existingContent is SimpleContent))
                {
                    var simpleContent:SimpleContent = new SimpleContent(existingContent);
                    parentProxy.object_proxy::content = simpleContent;
                }
                parentProxy.object_proxy::isSimple = false;
            }
        }

        setValue(parent, name, value);
    }

    /**
     * @private
     */
    public function getProperties(value:*):Array
    {
        var properties:Array = [];
        var elements:XMLList;

        if (value is XML)
        {
            elements = XML(value).elements();
        }
        else if (value is XMLList)
        {
            elements = XMLList(value).elements();
        }

        if (elements != null)
        {
            for each (var element:XML in elements)
            {
                properties.push(element.name());
            }
        }

        return properties;
    }

    /**
     * @private
     */
    public function createContent(type:QName = null):*
    {
        var content:* = undefined;

        // If we have a QName, check for a registered Class to use to create
        // a shell for the content, otherwise the default anonymous object shell
        // will be used (i.e. Object or ObjectProxy depending on the value
        // for makeObjectsBindable.
        if (type != null && typeRegistry != null)
        {
            var c:Class = typeRegistry.getClass(type);
            if (c == null)
                c = typeRegistry.getCollectionClass(type);
            if (c != null)
            {
                try
                {
                    content = new c();
                }
                catch(e:Error)
                {
                    var className:String = getQualifiedClassName(c);
                    log.debug("Unable to create new instance of Class '{0}' for type '{1}'.", className, type);
                }
            }
        }

        return new ContentProxy(content, makeObjectsBindable);
    }

    /**
     * @private
     */
    public function isSimpleValue(value:*):Boolean
    {
        if (value is XML)
        {
            return XML(value).hasSimpleContent();
        }
        else if (value is String || value is Number || value is Boolean
            || value is Date || value is int || value is uint
            || value is ByteArray)
        {
            return true;
        }

        return false;
    }

    /**
     * If the parent only contains simple content, then that content is
     * returned as the value, otherwise getValue is called.
     * 
     * @private
     */
    public function getSimpleValue(parent:*, name:*):*
    {
        if (parent is XML)
        {
            var xml:XML = parent as XML;
            if (xml.hasSimpleContent())
            {
                return xml.toString();
            }
        }
        else if (parent is XMLList)
        {
            var list:XMLList = parent as XMLList;
            if (list.hasSimpleContent())
            {
                return list.toString();
            }
        }

        return getValue(parent, name);
    }

   /**
    * @private
    */
    public function setSimpleValue(parent:*, name:*, value:*, type:Object=null):void
    {
        if (parent is ContentProxy)
        {
            var parentProxy:ContentProxy = parent as ContentProxy;
            if (parentProxy.object_proxy::isSimple)
            {
                parentProxy.object_proxy::content = value;
                return;
            }
        }

        setValue(parent, name, value, type)
    }

    /**
     * Assuming the parent is XML, the decoder looks for child element(s) with
     * the given name. If a single child element exists that contains simple
     * content, the simple content is returned unwrapped.
     * @see #parseValue
     */
    public function getValue(parent:*, name:*, index:Number = -1):*
    {
        var result:*;
        var qname:QName;
        if (name is QName)
        {
            qname = name as QName; 
            if (qname.uri == null || qname.uri == "")
                name = qname.localName;
        }

        var elements:XMLList;
        if (parent is XML)
        {
            elements = XML(parent).elements(name);
            if (elements.length() > 0)
                result = parseValue(name, elements);
        }
        else if (parent is XMLList)
        {
            elements = XMLList(parent).elements(name);
            if (elements.length() > 0)
                result = parseValue(name, elements);
        }
        else if (parent is ContentProxy)
        {
            // We can only use the public AS3 namespace, so just use the 
            // local name to find the property.
            if (qname != null)
                name = qname.localName;

            // We can hit this block while decoding an RPC encoded array of any
            // type when the instances in the Array are not simple values and
            // no type definition is available to drive decoding.
            result = (parent as ContentProxy).hasOwnProperty(name) ? parent[name] : undefined;
        }
        else if (!isSimpleValue(parent))
        {
            result = parent[name];
        }

        return result;
    }


    /**
     * @private
     */
    public function hasValue(parent:*, name:*):Boolean
    {
        return (getValue(parent, name) !== undefined);
    }

    /**
     * @private
     */
    public function setValue(parent:*, name:*, value:*, type:Object=null):void
    {
        if (parent != null)
        {
            // Unwrap any proxied values
            if (value is ContentProxy)
                value = ContentProxy(value).object_proxy::content;

            var existingValue:*;

            // We already have an array of values, so just add to this list
            if (TypeIterator.isIterable(parent))
            {
                TypeIterator.push(parent, value);
            }
            else if (name != null)
            {
                var propertyName:String;
                if (name is ContentProxy)
                    name = ContentProxy(name).object_proxy::content;

                if (name is QName)
                    propertyName = QName(name).localName;
                else
                    propertyName = Object(name).toString();


                if (parent is ContentProxy && ContentProxy(parent).object_proxy::isSimple)
                {
                    existingValue = ContentProxy(parent).object_proxy::content;
                }
                else 
                {
                    if (Object(parent).hasOwnProperty(propertyName))
                        existingValue = getExistingValue(parent, propertyName);
                        
                    else if (Object(parent).hasOwnProperty("_" + propertyName))
                        existingValue = getExistingValue(parent, "_" + propertyName);
                }

                // FIXME: How would we handle building up an Array of null
                // values from a sequence? If the type was * then it would
                // allow undefined to be checked, but this is a rare type
                // for users to declare... perhaps more context is needed
                // here.
                if (existingValue != null)
                {
                    existingValue = promoteValueToArray(existingValue, type);
                    TypeIterator.push(existingValue, value);
                    value = existingValue;
                }

                try
                {
                    if (parent is ContentProxy && ContentProxy(parent).object_proxy::isSimple)
                    {
                        ContentProxy(parent).object_proxy::content = value;
                    }
                    else
                    {
                        try
                        {
                            parent[propertyName] = value;
                        }
                        catch(e:Error)
                        {
                            parent["_"+propertyName] = value;
                        }
                    }
                }
                catch(e:Error)
                {
                    log.warn("Unable to set property '{0}' on parent.", propertyName);
                }
            }
            // If not an array, and without a name, we assume this may be the
            // first of potentially many items, or perhaps it is the second
            // item requiring us to promote the the existing item to an array.
            else if (parent is ContentProxy)
            {
                var proxyParent:ContentProxy = parent as ContentProxy;
                existingValue = proxyParent.object_proxy::content;
                if (existingValue !== undefined)
                {
                    existingValue = promoteValueToArray(existingValue, type);
                    proxyParent.object_proxy::content = existingValue;
                    TypeIterator.push(existingValue, value);
                    value = existingValue;
                }

                proxyParent.object_proxy::content = value;
            }
        }
    }



    /**
     * If an array value is required (as when decoding an element
     * with maxOccurs > 1), we need to create an empty Array or the
     * appropriate instance of IList. The optional parameter unwrap
     * can be set to true to indicate that the array should be the
     * parent object itself, and not a property on the parent.
     * 
     * @param type Optional. The XML Schema type for the property
     * that holds this iterable value. The SchemaTypeRegistry will be checked
     * to see if a custom collection Class has been registered for that
     * type.
     * 
     * @private
     */
    public function createIterableValue(type:Object=null):*
    {
        var value:*;

        try
        {
            if (type != null)
            {
                var c:Class = typeRegistry.getCollectionClass(type);
                if (c != null)
                    value = new c();
            }
        }
        catch (e1:Error)
        {
            log.debug("Error while resolving custom collection type for '{0}'.\nError: '{1}'.", type, e1);
        }

        try
        {
            // If we didn't have a registered Class for the XML Schema type
            // fall back to our default behavior for iterable types...
            if (value == null)
            {
                if (makeObjectsBindable)
                {
                    value = new listClass();
                }
                else
                {
                    value = [];
                }
            }
        }
        catch(e2:Error)
        {
            log.warn("Unable to create instance of '{0}'.", listClass);
        }
        return value;
	}

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    // 
    //--------------------------------------------------------------------------


    /**
    * Returns the appropriate values from the list of encoded elements in a
    * model group. In the base case, start from the current index and return
    * consequent elements with the given name. If context.anyIndex > -1 an <any>
    * definition has already decoded some values, so we start at anyIndex until
    * we find a value by the same name, possibly one that has already been
    * decoded. If that is the case, the previously decoded value is removed from
    * the parent.
    * @private
    */
    protected function getApplicableValues(parent:*, valueElements:XMLList, name:QName,
                                    context:DecodingContext, maxOccurs:uint):XMLList
    {
        var applicableValues:XMLList = new XMLList();
        var startIndex:uint = context.index;
        var skipAhead:Boolean = false;

        // context.anyIndex indicates that values were previously decoded with
        // an <any> definition. If we are looking for a named element, we start
        // at that index and are allowed to skip non-applicable elements. If
        // name == null, however, we are looking up values for another <any>
        // definition, so we should start where the previous one left off.
        if (context.anyIndex > -1 && name != null)
        {
            startIndex = context.anyIndex;
            skipAhead = true;
        }

        for (var i:uint = startIndex; i < valueElements.length(); i++)
        {
            // We only need up to maxOccurs applicable values.
            if (applicableValues.length() == maxOccurs)
                break;
            
            // If no name to match was passed, we take the next element, regardless
            // of name. If name is unqualified we match local names only.
        	if (name == null || valueElements[i].name() == name
                || ((name.uri == "" || name.uri == null)
                    && name.localName == valueElements[i].name().localName))
        	{
        		applicableValues += valueElements[i];

        		// If this is already decoded, we need to remove it from the parent.
        		if (i < context.index)
        		    parent[name.localName] = null;

        		// Once we found a matching element, we are only looking for
        		// consequent elements and we can't skip ahead.
        		skipAhead = false;
        	}
        	else if (skipAhead == false)
        	{
        		break;
        	}
        }
        return applicableValues;
    }


    /**
     * Tests whether a given namespace is included in a wildcard definition. If
     * no restrictions are provided the default behavior is to include all
     * namespaces.
     */
    protected function includeNamespace(namespaceURI:String, includedNamespaces:Array = null):Boolean
    {
        if (includedNamespaces != null)
        {
            var matchFound:Boolean = false;
            for each (var definedURI:String in includedNamespaces)
            {
                if (definedURI == "##local" && namespaceURI == null)
                {
                    return true;
                }

                if (URLUtil.urisEqual(namespaceURI, definedURI))
                {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    /**
     * This method primarily exists to give subclasses a chance to post-process
     * returned value(s) before the decoder processes them. This particular
     * implementation checks to see if the value is not a list of values (i.e.
     * it existed as a single child element) and contains only simple content -
     * if so, the simple content is returned unwrapped.
     * 
     * @private
     */
    protected function parseValue(name:*, value:XMLList):*
    {
        var result:* = value;
        
        // We unwrap simple content and get the value as a String
        if (value.hasSimpleContent())
        {
            if (isXSINil(value))
                result = null;
            else
                result = value.toString();
        }
        // Otherwise, as a convenience we unwrap an XMLList containing only one
        // XML node...
        else if (value.length() == 1)
        {
            result = value[0];
        }

        return result;
    }

    protected function isXSINil(value:*):Boolean
    {
        if (value != null)
        {
            var nilAttribute:String = "false";

            if (value is XML)
                nilAttribute = XML(value).attribute(constants.nilQName).toString();
            else if (value is XMLList)
                nilAttribute = XMLList(value).attribute(constants.nilQName).toString();

            if (nilAttribute == "true")
                return true;
        }

        return false;
    }

    /**
     * This function determines whether a given value is already iterable
     * and if not, wraps the value in a suitable iterable implementation. If
     * the value needs to be wrapped, makeObjectsBindable set to false will
     * just wrap the value in an Array where as makeObjectsBindable set to true
     * will wrap the value in the current listClass implementation, which
     * by default is an ArrayCollection.
     * 
     * @param value The value to promote to an iterable type, such as an Array.
     * @param type Optional. The XML Schema type for the property that
     * will hold this iterable value.
     * 
     * @private
     */
    protected function promoteValueToArray(value:*, type:Object=null):*
    {
        if (!TypeIterator.isIterable(value))
        {
            var array:* = createIterableValue(type);
            TypeIterator.push(array, value);
            value = array;
        }
        return value;
    }

    /**
     * Search for an XSI type attribute on an XML value.
     * 
     * @private
     */
    protected function getXSIType(value:*):QName
    {
        var xsiType:QName;
        var xml:XML;

        if (value is XML)
            xml = value as XML;
        else if (value is XMLList && value.length() == 1)
            xml = value[0];

        if (xml != null)
        {
            var xsi:String = XMLUtil.getAttributeByQName(xml, constants.typeAttrQName).toString();
            if (xsi != null && xsi != "")
                xsiType = schemaManager.getQNameForPrefixedName(xsi, xml);
        }

        return xsiType;
    }

    /**
     * We record the qualified type used for anonymous objects wrapped in
     * ObjectProxy or for strongly typed objects that implement
     * IXMLSchemaInstance.
     * 
     * @private
     */
    protected function setXSIType(value:*, type:QName):void
    {
        if (value != null && recordXSIType)
        {
            if (value is ContentProxy)
                value = value.object_proxy::content;

            if (value != null)
            {
                if (value is ObjectProxy)
                    ObjectProxy(value).object_proxy::type = type;
                else if (value is IXMLSchemaInstance)
                    IXMLSchemaInstance(value).xsiType = type;
            }
        }
    }

    /**
     * @private
     */
    protected function getExistingValue(parent:*, propertyName:String):*
    {
        var existingValue:*;

        var object:Object = Object(parent);

        if (parent is ContentProxy)
            object = ContentProxy(parent).object_proxy::content;

        if (object is ObjectProxy)
            object = ObjectProxy(object).object_proxy::object;

        // If we have an Object, all properties are dynamic and thus existing
        // values could be promoted to an Array...
        var className:String = getQualifiedClassName(object);
        if (className == "Object")
        {
            existingValue = parent[propertyName];
        }
        // ... otherwise we'd need to check that the property is capable of
        // being promoted to an Array (or already is an Array) first. If not,
        // we will just let the  decoder override any existing value.
        else
        {
            var classInfo:XML = DescribeTypeCache.describeType(object).typeDescription;
            var properties:XMLList = classInfo..accessor.(@access == "readwrite" && @name == propertyName) + classInfo..variable.(@name == propertyName);
            if (properties.length() > 0)
            {
                var property:XML = properties[0];
                var propertyType:String = property.@type;
                var tempValue:* = parent[propertyName];
                if (propertyType == "Object" || propertyType == "*" || TypeIterator.isIterable(tempValue))
                    existingValue = tempValue;
            }
        }

        return existingValue;
    }

    /**
     * Noop. Method exists to give a chance to subclasses to pre-process the
     * encoded XML.
     * @private
     */
    protected function preProcessXML(root:XML):void
    {
    }

    /**
     * The <code>mx.collections.IList</code> implementation to use when decoding
     * an array of values and <code>makeObjectsBindable</code> is set to true.
     * The default is <code>mx.collections.ArrayCollection</code>.
     */
    public static var listClass:Class = mx.collections.ArrayCollection;

    /**
     * The current, top level XML document to decode.
     */
    protected var document:XML;
    private var log:ILogger;
    private var _makeObjectsBindable:Boolean;
    private var _recordXSIType:Boolean;
    private var _typeRegistry:SchemaTypeRegistry;
}

}
