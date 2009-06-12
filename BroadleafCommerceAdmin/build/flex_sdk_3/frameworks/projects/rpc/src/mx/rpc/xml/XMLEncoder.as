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
import flash.utils.Dictionary;
import mx.collections.ArrayCollection;
import mx.utils.DescribeTypeCache;
import mx.utils.object_proxy;
import mx.utils.ObjectProxy;
import mx.utils.ObjectUtil;
import mx.collections.IList;

[ExcludeClass]

/**
 * Encodes an ActionScript Object graph to XML based on an XML Schema.
 * 
 * @private
 */
public class XMLEncoder extends SchemaProcessor implements IXMLEncoder
{
    public function XMLEncoder()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Encodes an ActionScript value as XML.
     * 
     * @param value The ActionScript value to encode as XML.
     * @param name The QName of an XML Schema <code>element</code> that
     * describes how to encode the value, or the name to be used for the
     * encoded XML node when a type parameter is also specified.
     * @param type The QName of an XML Schema <code>simpleType</code> or
     * <code>complexType</code> definition that describes how to encode the
     * @param definition If neither a top level element nor type exists in the
     * schema to describe how to encode this value, a custom element definition
     * can be provided.
     */
    public function encode(value:*, name:QName = null, type:QName = null, definition:XML = null):XMLList
    {
        var result:XMLList = new XMLList();
        var content:XML;

        // FIXME: Handle generic name == null case with some default encoding?
        if (name == null)
            name = new QName("", "root");

        if (type != null)
        {
            content = encodeXSINil(null, name, value);
            if (content == null)
            {
                // If encodeXSINil didn't create content, we do now.
                content = createElement(name);

                // However, value can still be null if the element wasn't
                // allowed to be nillable.
                // FIXME: should we skip null or always create content with xsi:nil?
                if (value == null)
                    setValue(content, null);
                else
                    encodeType(type, content, name, value);
            }
        }
        else
        {
            var elementDefinition:XML = definition;
            var mustReleaseScope:Boolean = false;
            if (elementDefinition == null)
            {
                elementDefinition = schemaManager.getNamedDefinition(name, constants.elementTypeQName);
                // If we found a definition through the schemaManager, the relevant
                // schema was pushed in scope, so we must remember to release it.
                if (elementDefinition != null)
                    mustReleaseScope = true;
            }

            // Encoding is based on an element definition, either a custom one
            // was provided or we looked one up for the given name. If no definition
            // was found, encodeElementTopLevel will encode with anyType.
            content = encodeElementTopLevel(elementDefinition, name, value);

            if (mustReleaseScope)
                schemaManager.releaseScope();
        }

        if (content != null)
            result += content;

        return result;
    }



    /**
     * All content:
     *     (annotation?, (element | any)*)
     * 
     * FIXME: This needs work, right now it treats all as a sequence.
     * @private
     */
    public function encodeAll(definition:XML, parent:XMLList, name:QName, value:*, isRequired:Boolean = true):Boolean
    {
        return encodeSequence(definition, parent, name, value, isRequired);
    }



    /**
     * Encodes any complex object values as attributes using the XML schema
     * rules for attribute wildcards.
     * 
     * FIXME: This needs further investigation of the XML schema spec for
     * wildcard rules and constraints.
     * 
     * @private
     */
    public function encodeAnyAttribute(definition:XML, parent:XML, name:QName, value:* = undefined, restriction:XML = null):void
    {
        // FIXME: honor restrictions for attributes

        if (value !== undefined)
        {
            if (!isSimpleValue(value) && !(value is Array))
            {
                // FIXME: De we consider namespace filter attributes for
                // encoding? Also consider skipping reserved attribute names
                // like xmlns etc? What about non-public namespace properties?
                for (var propertyName:Object in getProperties(value))
                {
                    // Only add wildcard attributes if property has not already been added
                    if (!hasAttribute(value, propertyName) && !hasValue(value, propertyName))
                    {
                        var attributeValue:* = getAttribute(value, propertyName);

                        if (attributeValue != null)
                            setAttribute(parent, propertyName, attributeValue);
                    }
                }
            }
        }
    }



    /**
     * Encodes elements based on wildcard rules.
     * 
     * Any content:
     *    (annotation?)
     * @private
     * 
     */
    public function encodeAnyElement(definition:XML, siblings:XMLList, name:QName, value:*, isRequired:Boolean=true, encodedVals:Dictionary=null):Boolean
    {
        // encodeAnyElement is never called with null value
//        if (value == null)
//            return false;
        
        var maxOccurs:uint = getMaxOccurs(definition);
        var minOccurs:uint = getMinOccurs(definition);

        if (isSimpleValue(value))
        {
        	var item:XML = createElement(name);
        	setValue(item, value);
            appendValue(siblings, item);
        }
        else if (value is XML || value is XMLList)
        {
            // If we have XML or XMLList, just append it to the siblings list.
            appendValue(siblings, value);
        }
        else
        {
            // We keep a dictionary of values we have encountered on this stack
            // in order to detect cyclic references.
            if (encodedVals == null)
                encodedVals = new Dictionary(true);

            // Work around AS problem with QName values in Dictionary. Since
            // QNames cannot possibly create cyclic references, it's OK not to
            // keep track of them.
            if (!(value is QName))
            {
                if (encodedVals[value] != null)
                    throw new Error("Cannot encode complex structure. Cyclic references detected.");
                encodedVals[value] = true;
            }
            
            if (value is Array || value is IList)
            {
            	// FIXME: Check for maxOccurs and minOccurs.
                if (value is IList)
                    value = IList(value).toArray();

                for each (var arrValue:* in value as Array)
                {
                    // Since this is an array, we don't need check for nillable
                    // or isRequired. We must always create a node to preserve
                    // array indexes.
					var arrayItem:* = createElement(name);
					
					if (arrValue == null)
					{
					    // Directly set xsi:nil
        	            setValue(arrayItem, null);
					}
					else if (arrValue != null)
                    {
                        var arrayChildren:XMLList = new XMLList();
                        encodeAnyElement(definition, arrayChildren, name, arrValue, isRequired, encodedVals);
                        if (isSimpleValue(arrValue))
                        {
                            // Don't double wrap simple values.
                            arrayItem = arrayChildren[0];
                        }
                        else
                        {
                            setValue(arrayItem, arrayChildren);
                        }
                    }
                    appendValue(siblings, arrayItem);
                }
            }
            else
            {
                for each (var objProperty:Object in getProperties(value))
                {
                    var propValue:* = getValue(value, objProperty);
                    var propQName:QName = new QName(name.uri, objProperty);
                    
                    // Only encode if property hasn't been encoded yet.
                    // FIXME: When coming from a group context, if the any element
                    // is not last in the definition, we will end up encoding subsequent
                    // named elements twice. We need all sibling names from the definition
                    // of the group to properly do this.
                    if (!containsNodeByName(siblings, propQName))
                    {
                        var propItem:XML = encodeXSINil(definition, propQName, propValue);
                        
                        if (propItem != null)
                        {
                            appendValue(siblings, propItem);
                        }
                        else if (propValue != null)
                        {   
                            var propChildren:XMLList = new XMLList();
                            encodeAnyElement(definition, propChildren, propQName, propValue, isRequired, encodedVals);
                            appendValue(siblings, propChildren);
                        }
                    }
                }
                
            }
            delete encodedVals[value];
        }
        
        // FIXME: figure out isRequired
        return true;
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
    public function encodeAttribute(definition:XML, parent:XML, name:QName, value:* = undefined, restriction:XML = null):void
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
                value = getValue(value, attributeName);

                if (value === undefined)
                {
                    var attributeDefault:String = getAttributeFromNode("default", definition);
                    if (attributeDefault != null)
                        value = attributeDefault;
                }
            }

            var tempElement:*;
            var attributeFound:Boolean;
            if (value !== undefined)
            {
                var typeDefinition:XML;
                // we just need a temporary wrapper to pass down as
                // the parent XML for the encodeSimpleType calls
                tempElement = <temp/>;

                

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
                        tempElement.appendChild(schemaManager.marshall(value, attributeType, restriction));
                    }
                    else
                    {
                        // <simpleType>
                        typeDefinition = schemaManager.getNamedDefinition(attributeType, constants.simpleTypeQName);
                        if (typeDefinition != null)
                            encodeSimpleType(typeDefinition, tempElement, attributeName, value, restriction);
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
                        encodeSimpleType(typeDefinition, tempElement, attributeName, value, restriction);
                    }
                    else if (value != null)
                    {
                        // Finally, in the absence of type information we
                        // just get the attribute value as a String without
                        // restriction
                        tempElement.appendChild(value.toString());
                    }
                }
            }

            // FIXME: Should we enforce use="required"?
            if (tempElement !== undefined)
            {
                setAttribute(parent, attributeName, tempElement);
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
    public function encodeAttributeGroup(definition:XML, parent:XML, name:QName, value:* = undefined, restriction:XML = null):void
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
        for each (var attributeDefinition:XML in attributes)
        {
            encodeAttribute(attributeDefinition, parent, name, value, restriction);
        }

        // <attributeGroup>
        var attributeGroups:XMLList = definition.elements(constants.attributeGroupQName);
        for each (var attributeGroup:XML in attributeGroups)
        {
            encodeAttributeGroup(attributeGroup, parent, name, value, restriction);
        }

        // <anyAttribute>
        var anyAttribute:XML = getSingleElementFromNode(definition, constants.anyAttributeQName);
        if (anyAttribute != null)
        {
            encodeAnyAttribute(anyAttribute, parent, name, value, restriction);
        }

        // If we found our attributeGroup by reference, we now release the schema scope
        if (ref != null)
            schemaManager.releaseScope();
    }

    /**
     * choice:
     *    (annotation?, (element | group | choice | sequence | any)*)
     * 
     * @private
     */
    public function encodeChoice(definition:XML, parent:XMLList, name:QName, value:*, isRequired:Boolean = true):Boolean
    {
        var maxOccurs:uint = getMaxOccurs(definition);
        var minOccurs:uint = getMinOccurs(definition);

        // If maxOccurs is 0 this choice must not be present.
        // If minOccurs == 0 the choice is optional so it can be omitted if
        // a value was not provided.
        if (maxOccurs == 0)
            return false;
        if (value == null && minOccurs == 0)
            return true;

		var choiceElements:XMLList = definition.elements();
		var choiceSatisfied:Boolean = true;
		var lastIndex:uint;
        var choiceOccurs:uint;

        // We don't enforce occurs bounds on the choice element itself. Since all
        // child elements of the choice definition would be properties on the
        // value object, simply looping through the choice children once would
        // encode the values that apply to each of the child elements.

        // An empty choice is satisfied by default, but if there are choiceElements
        // we need to start out with choiceSatisfied = false
        if (choiceElements.length() > 0)
            choiceSatisfied = false;

		for each (var childDefinition:XML in choiceElements)
		{
			if (childDefinition.name() == constants.elementTypeQName)
			{
                // <element>
                choiceSatisfied = encodeGroupElement(childDefinition, parent,
                    name, value, false) || choiceSatisfied;
            }
            else if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                choiceSatisfied = encodeSequence(childDefinition, parent,
                    name, value, false) || choiceSatisfied;
            }
            else if (childDefinition.name() == constants.groupQName)
            {
                // <group>
                choiceSatisfied = encodeGroupReference(childDefinition, parent,
                    name, value, false) || choiceSatisfied;
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                choiceSatisfied = encodeChoice(childDefinition, parent,
                    name, value, false) || choiceSatisfied;
            }
            else if (childDefinition.name() == constants.anyQName)
            {
                // <any>
				choiceSatisfied = encodeAnyElement(childDefinition, parent,
				    name, value, false) || choiceSatisfied;
            }
		}

        return choiceSatisfied;
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
    public function encodeComplexContent(definition:XML, parent:XML, name:QName, value:*):void
    {
        var childDefinition:XML = getSingleElementFromNode(definition, constants.extensionQName, constants.restrictionQName);

        if (childDefinition.name() == constants.extensionQName)
        {
            encodeComplexExtension(childDefinition, parent, name, value);
        }
        else if (childDefinition.name() == constants.restrictionQName)
        {
            encodeComplexRestriction(childDefinition, parent, name, value);
        }
    }
    

    /**    
     * complexContent:
     *   extension:
     *     (annotation?, ((group | all | choice | sequence)?, ((attribute | attributeGroup)*, anyAttribute?), (assert | report)*))
     * 
     * @private
     */
    public function encodeComplexExtension(definition:XML, parent:XML, name:QName, value:*):void
    {
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
        encodeComplexType(baseDefinition, parent, name, value);

        // Then release the scope of the base type definition
        schemaManager.releaseScope();

        var childDefinitions:XMLList = definition.elements();

        // Start a separate XMLList for the child elements defined in this extension.
        // Extension attributes are still encoded directly on the parent.
        var extChildren:XMLList = new XMLList();
        for each (var childDefinition:XML in childDefinitions)
        {
            if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                encodeSequence(childDefinition, extChildren, name, value);
            }
            else if (childDefinition.name() == constants.groupQName)
            {
                // <group>
                encodeGroupReference(childDefinition, extChildren, name, value);
            }
            else if (childDefinition.name() == constants.allQName)
            {
                // <all>
                encodeAll(childDefinition, extChildren, name, value);
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                encodeChoice(childDefinition, extChildren, name, value);
            }
            else if (childDefinition.name() == constants.attributeQName)
            {
                // <attribute>
                encodeAttribute(childDefinition, parent, name, value);
            }
            else if (childDefinition.name() == constants.attributeGroupQName)
            {
                // <attributeGroup>
                encodeAttributeGroup(childDefinition, parent, name, value);
            }
            else if (childDefinition.name() == constants.anyAttributeQName)
            {
                // <anyAttribute>
                encodeAnyAttribute(childDefinition, parent, name, value);
            }
        }

        // We need to add the extension elements to the parent node. However,
        // we need to handle the case where a value fits both the base and the
        // extension definitions (strictly speaking that's illegal schema, but
        // it's used in some cases). We need to keep the values encoded with the
        // extension definition, so we delete any values with the same names that
        // we got from encoding the base definition.
        for each (var extension:XML in extChildren)
        {
            // Delete anything already encoded during base type processing, which
            // matches the full qualified name of this extension element.
            delete parent[extension.name()];
            // Also delete unqualified elements with the same local name, since
            // <any> in the base definition would encode with local names only.
            delete parent[new QName("", extension.name().localName)];
            delete parent[new QName(null, extension.name().localName)];
        }
        setValue(parent, extChildren);
    }
    
    /**    
     * complexContent:
     *   restriction:
     *     (annotation?, (group | all | choice | sequence)?, ((attribute | attributeGroup)*, anyAttribute?), (assert | report)*)
     * 
     * @private
     */
    public function encodeComplexRestriction(restriction:XML, parent:XML, name:QName, value:*):void
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

        var childDefinitions:XMLList = restriction.elements();
        var children:XMLList = parent.elements();
        for each (var childDefinition:XML in childDefinitions)
        {
            if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                encodeSequence(childDefinition, children, name, value);
            }
            else if (childDefinition.name() == constants.groupQName)
            {
                // <group>
                encodeGroupReference(childDefinition, children, name, value);
            }
            else if (childDefinition.name() == constants.allQName)
            {
                // <all>
                encodeAll(childDefinition, children, name, value);
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                encodeChoice(childDefinition, children, name, value);
            }
            else if (childDefinition.name() == constants.attributeQName)
            {
                // <attribute>
                encodeAttribute(childDefinition, parent, name, value, restriction);
            }
            else if (childDefinition.name() == constants.attributeGroupQName)
            {
                // <attributeGroup>
                encodeAttributeGroup(childDefinition, parent, name, value, restriction);
            }
            else if (childDefinition.name() == constants.anyAttributeQName)
            {
                // <anyAttribute>
                encodeAnyAttribute(childDefinition, parent, name, value, restriction);
            }
        }
        parent.setChildren(children);
    }

    public function encodeComplexType(definition:XML, parent:XML, name:QName, value:*, restriction:XML = null):void
    {
        var childElements:XMLList = definition.elements();

        var children:XMLList = new XMLList();
        // FIXME: Investigate if we need to support "base" attribute on
        // complexType as short-cut as seen in some examples...

        for each (var childDefinition:XML in childElements)
        {
            if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                encodeSequence(childDefinition, children, name, value);
            }
            else if (childDefinition.name() == constants.simpleContentQName)
            {
                // <simpleContent>
                encodeSimpleContent(childDefinition, parent, name, value, restriction);
            }
            else if (childDefinition.name() == constants.complexContentQName)
            {
                // <complexContent>
                encodeComplexContent(childDefinition, parent, name, value);
            }
            else if (childDefinition.name() == constants.groupQName)
            {
                // <group>
                encodeGroupReference(childDefinition, children, name, value);
            }
            else if (childDefinition.name() == constants.allQName)
            {
                // <all>
                encodeAll(childDefinition, children, name, value);
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                encodeChoice(childDefinition, children, name, value);
            }
            else if (childDefinition.name() == constants.attributeQName)
            {
                // <attribute>
                encodeAttribute(childDefinition, parent, name, value, restriction);
            }
            else if (childDefinition.name() == constants.attributeGroupQName)
            {
                // <attributeGroup>
                encodeAttributeGroup(childDefinition, parent, name, value, restriction);
            }
            else if (childDefinition.name() == constants.anyAttributeQName)
            {
                // <anyAttribute>
                encodeAnyAttribute(childDefinition, parent, name, value, restriction);
            }
        }
        setValue(parent, children);
    }
    

    /**
     * Used to encode a local element definition (inside a model group).
     * Handles restrictions on omittability and occurence counts in the
     * context of the parent model group.
     * Delegates actual encoding to encodeElementTopLevel once all the
     * context around the element is known.
     * 
     * @param definition The XML Schema definition of the local element.
     * @param parent The XMLList of values encoded in the current level. The
     * new encoded node should be appended to this XMLList.
     * @param name The QName to be used for the encoded XML node.
     * @param value The ActionScript value to encode as XML.
     * @param isRequired A flag indicating wether the element should meet
     * its local occurence bounds. For example, the local element may have
     * minOccurs=1, but be only one of many elements in a choice group, in
     * which case it is valid not to satisfy the minOccurs requirement.
     * 
     * @return Wether or not the value provided 
     * 
     * FIXME: Support substitutionGroup, block and redefine?
     * FIXME: Do we care about abstract or final?
     * @private
     */
    public function encodeGroupElement(definition:XML, siblings:XMLList, name:QName, value:*, isRequired:Boolean = true):Boolean
    {
        // <element minOccurs="..." maxOccurs="..."> occur on the local element,
        // not on a referent, so we capture this information first.
        var maxOccurs:uint = getMaxOccurs(definition);
        var minOccurs:uint = getMinOccurs(definition);

        isRequired = isRequired && minOccurs > 0;

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

        // Now that we've resolved the real element name, look for the element's
        // value on the provided value.
        var elementValue:* = getValue(value, elementQName);
        var encodedElement:XML;

        // If minOccurs == 0 the element is optional so we can omit it if
        // a value was not provided.
        if (elementValue == null)
        {
            encodedElement = encodeElementTopLevel(definition, elementQName, elementValue);
            if (encodedElement != null)
                appendValue(siblings, encodedElement);

            // if required, but no value was encoded, the definition is not
            // satisfied
            if (isRequired  && encodedElement == null)
                return false;

            // Otherwise we can return true
            return true;
        }


        
        
        // We treat maxOccurs="1" as a special case and not check the
        // occurence because we need to pass through values to SOAP
        // encoded Arrays which do not rely on minOccurs/maxOccurs
        if (maxOccurs == 1)
        {
            encodedElement = encodeElementTopLevel(definition, elementQName, elementValue);
            if (encodedElement != null)
            {
                appendValue(siblings, encodedElement);
            }
            // ...else we just skip the element as a value wasn't provided.
        }
        else if (maxOccurs > 1)
        {
            var valueOccurence:uint = getValueOccurence(elementValue);

            // If maxOccurs is greater than 1 then we would expect an
            // Array of values
            if (valueOccurence < minOccurs)
            {
                throw new Error("Value supplied for element '" + elementQName +
                    "' occurs " + valueOccurence + " times which falls short of minOccurs " +
                    minOccurs + ".");
            }

            if (valueOccurence > maxOccurs)
            {
                throw new Error("Value supplied for element of type '" + elementQName +
                    "' occurs " + valueOccurence + " times which exceeds maxOccurs " +
                    maxOccurs + ".");
            }

            // Promote non-iterable values to an Array to handle the MXML
            // single-child property case where the compiler doesn't promote
            // a property to an Array until two items are present.
            if (!TypeIterator.isIterable(elementValue))
                elementValue = [elementValue];

            // Encode element based on occurence within the bounds of
            // minOccurs and maxOccurs
            var iter:TypeIterator = new TypeIterator(elementValue);
            
            for (var i:uint = 0; i < maxOccurs && i < valueOccurence; i++)
            {
                var item:*;
                if (iter.hasNext())
                {
                    item = iter.next();
                }
                else if (i > minOccurs)
                {
                    break;
                }

                encodedElement = encodeElementTopLevel(definition, elementQName, item);
                // encodedElement is null if encodeXSINil inside encodeElementTopLevel
                // was not allowed to create element with xsi:nil for a null or undefined
                // value. We must still force xsi:nil, because we are encoding an array
                // and we need to preserve the index.
                if (encodedElement == null)
                {
                    encodedElement = createElement(elementQName);
                    setValue(encodedElement, null);
                }
                appendValue(siblings, encodedElement);
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
    public function encodeElementTopLevel(definition:XML, elementQName:QName, value:*):XML
    {
        // Let encodeXSINil create an element if null, fixed, or default value
        // must be used.
        var element:XML = encodeXSINil(definition, elementQName, value);
        
        // If ecnodeXSINil created an element, we are done.
        if (element != null)
            return element;
        // If value was null, but element wasn't created, it must be omitted.
        else if (value == null)
            return null;

        // Otherwise, just create the element with the given QName. This starts off
        // a new tree of encoded values with this top level element as the root.
        element = createElement(elementQName);
        
        // Check for a simple def first, falling back to complex type handling
        // and then default handling.
        var typeAttribute:String = getAttributeFromNode("type", definition);
        if (typeAttribute != null)
        {
            var typeQName:QName = schemaManager.getQNameForPrefixedName(typeAttribute, definition);
            encodeType(typeQName, element, elementQName, value);
        }
        // Next, check if the element has an in-line <complexType> or
        // <simpleType> definition.
        else if (definition != null && definition.hasComplexContent())
        {
            var typeDefinition:XML = getSingleElementFromNode(definition,
                                        constants.complexTypeQName,
                                        constants.simpleTypeQName);

            if (typeDefinition.name() == constants.complexTypeQName)
            {
                // <complexType>
                encodeComplexType(typeDefinition, element, elementQName, value);
            }
            else if (typeDefinition.name() == constants.simpleTypeQName)
            {
                // <simpleType>
                encodeSimpleType(typeDefinition, element, elementQName, value);
            }

            // FIXME: Support unique, key, keyref, field, selector
        }
        else
        {
            // FIXME: Support <element substitutionGroup="...">
            encodeType(constants.anyTypeQName, element, elementQName, value);
        }
        return element;
    }
    
    /**
     * The <code>group</code> element allows partial (or complete) content
     * models to be reused in complex types. When used inside a choice,
     * sequence, complexType, extension or restriction element, it must 
     * have a ref attribute, specifying the name of a global definition
     * of a named model group.
     * 
     * group:
     * (annotation?, (all | choice | sequence)?)
     * 
     * @private
     */
    public function encodeGroupReference(definition:XML, parent:XMLList, name:QName, value:*, isRequired:Boolean = true):Boolean
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
            throw new Error("A group reference element must have the ref attribute");
        }

        var groupElements:XMLList = definition.elements();
        var groupSatisfied:Boolean = false;
        for each (var childDefinition:XML in groupElements)
        {
            if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                groupSatisfied = encodeSequence(childDefinition, parent, name, value, isRequired);
            }
            else if (childDefinition.name() == constants.allQName)
            {
                // <all>
                groupSatisfied = encodeAll(childDefinition, parent, name, value, isRequired);
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                groupSatisfied = encodeChoice(childDefinition, parent, name, value, isRequired);
            }
        }
        // We found our group by reference, we now release the schema scope
        schemaManager.releaseScope();
        return groupSatisfied;
    }

    /**
     * sequence:
     *    (annotation?, (element | group | choice | sequence | any)*)
     * 
     * @private
     */
    public function encodeSequence(definition:XML, siblings:XMLList, name:QName, value:*, isRequired:Boolean=true):Boolean
    {
        var maxOccurs:uint = getMaxOccurs(definition);
        var minOccurs:uint = getMinOccurs(definition);

        // If maxOccurs is 0 this sequence must not be present.
        // If minOccurs == 0 the sequence is optional so it can be omitted if
        // a value was not provided.
        if (maxOccurs == 0)
            return true;
        if (value == null && minOccurs == 0)
            return true;

		// Note that we can't enforce occurence count on the sequence element
		// itself. Since the value is an ActionScript object, any named element
		// in the sequence should correspond to a named property on the object. 

        var sequenceElements:XMLList = definition.elements();
        // We loop through the children of the sequence definition. We require
        // all child definitions to be satisfied, unless the sequence itself
        // doesn't need to be satisfied.
        var requireChild:Boolean = isRequired && minOccurs > 0;
        var sequenceSatisfied:Boolean = true;
		
		for each (var childDefinition:XML in sequenceElements)
		{
			sequenceSatisfied = false;
			if (childDefinition.name() == constants.elementTypeQName)
			{
                // <element>
                if (!encodeGroupElement(childDefinition, siblings, name, value, isRequired))
                	break;
            }
            else if (childDefinition.name() == constants.groupQName)
            {
                // <group>
                if (!encodeGroupReference(childDefinition, siblings, name, value, isRequired))
                	break;
            }
            else if (childDefinition.name() == constants.choiceQName)
            {
                // <choice>
                if (!encodeChoice(childDefinition, siblings, name, value, isRequired))
                	break;
            }
            else if (childDefinition.name() == constants.sequenceQName)
            {
                // <sequence>
                if (!encodeSequence(childDefinition, siblings, name, value, isRequired))
                	break;
            }
            else if (childDefinition.name() == constants.anyQName)
            {
                // <any>
				if (!encodeAnyElement(childDefinition, siblings, name, value, isRequired))
					break;
            }
            sequenceSatisfied = true;
		}

		return sequenceSatisfied || !isRequired;
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
    public function encodeSimpleContent(definition:XML, parent:XML, name:QName, value:*, restriction:XML = null):void
    {
        var childDefinition:XML = getSingleElementFromNode(definition, constants.extensionQName, constants.restrictionQName);

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
                    setValue(parent, schemaManager.marshall(simpleValue, baseType, restriction));
                }
                else
                {
                    encodeType(baseType, parent, value, restriction);
                }

                var extensions:XMLList = childDefinition.elements();
                for each (var extensionChild:XML in extensions)
                {
                    if (extensionChild.name() == constants.attributeQName)
                    {
                        // <attribute>
                        encodeAttribute(extensionChild, parent, name, value, restriction);
                    }
                    else if (extensionChild.name() == constants.attributeGroupQName)
                    {
                        // <attributeGroup>
                        encodeAttributeGroup(extensionChild, parent, name, value, restriction);
                    }
                    else if (extensionChild.name() == constants.anyAttributeQName)
                    {
                        // <anyAttribute>
                        encodeAnyAttribute(extensionChild, parent, name, value, restriction);
                    }
                }
            }
            // <restriction>
            else if (childDefinition.name() == constants.restrictionQName)
            {
                simpleValue = getSimpleValue(value, name);
                encodeSimpleRestriction(childDefinition, parent, name, simpleValue);
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
    public function encodeSimpleList(definition:XML, parent:XML, name:QName, value:*, restriction:XML = null):void
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
            var tempElement:* = <temp/>;

            // Lists cannot encode null values, since separators are collapsed.
            if (item == null)
                continue;

            if (itemTypeQName != null)
                encodeType(itemTypeQName, tempElement, name, item, restriction);
            else
                encodeSimpleType(itemDefinition, tempElement, name, item, restriction);

            listValue = listValue.concat(tempElement.toString());
            if (iter.hasNext())
                listValue = listValue.concat(" ");
        }

        setValue(parent, listValue);
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
    public function encodeSimpleRestriction(restriction:XML, parent:XML, name:QName, value:*):void
    {
        var simpleTypeDefinition:XML = getSingleElementFromNode(restriction, constants.simpleTypeQName);
        if (simpleTypeDefinition != null)
        {
            encodeSimpleType(simpleTypeDefinition, parent, name, value, restriction);
        }
        else
        {
            var baseName:String = getAttributeFromNode("base", restriction);
            var baseType:QName = schemaManager.getQNameForPrefixedName(baseName, restriction);
            
            // FIXME: handle anyType
            encodeType(baseType, parent, name, value, restriction);
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
    public function encodeSimpleType(definition:XML, parent:XML, name:QName, value:*, restriction:XML = null):void
    {
        var definitionChild:XML = getSingleElementFromNode(definition,
                            constants.restrictionQName,
                            constants.listQName,
                            constants.unionQName);

        if (definitionChild.name() == constants.restrictionQName)
        {    
            // <restriction>
            encodeSimpleRestriction(definitionChild, parent, name, value);
        }
        else if (definitionChild.name() == constants.listQName)
        {
            // <list>
            encodeSimpleList(definitionChild, parent, name, value, restriction);
        }
        else if (definitionChild.name() == constants.listQName)
        {
            // <union>
            encodeSimpleUnion(definitionChild, parent, name, value, restriction);
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
    public function encodeSimpleUnion(definition:XML, parent:XML, name:QName, value:*, restriction:XML = null):void
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

        setValue(parent, schemaManager.marshall(value, type, restriction));
    }

    /**
     * Allow instance specific overrides for concrete type information as
     * abstract complexTypes may require a concrete xsi:type definition.
     * 
     * @param parent A reference to the parent XML. Must not be null.
     *
     * @private
     */
    public function encodeType(type:QName, parent:XML, name:QName, value:*, restriction:XML = null):void
    {
        var xsiType:QName = getXSIType(value);
        if (xsiType != null)
            type = xsiType;

        var definition:XML = schemaManager.getNamedDefinition(type,
            constants.complexTypeQName, constants.simpleTypeQName);
            

        if (isBuiltInType(type))
        {
            if (type == constants.anyTypeQName && !isSimpleValue(value))
            {
                var children:XMLList = new XMLList();
                encodeAnyElement(definition, children, name, value);
                setValue(parent, children);
            }
            else
            {
                setValue(parent, schemaManager.marshall(value, type, restriction));
            }
            
            deriveXSIType(parent, type, value);
        }
        else
        {
            if (definition == null)
                throw new Error("Cannot find definition for type '" + type + "'");

            var definitionType:QName = definition.name() as QName;
            if (definitionType == constants.complexTypeQName)
            {
                // <complexType>
                encodeComplexType(definition, parent, name, value, restriction);
            }
            else if (definitionType == constants.simpleTypeQName)
            {
                // <simpleType>
                encodeSimpleType(definition, parent, name, value, restriction);
            }
            else
            {
                throw new Error("Invalid type definition " + definitionType);
            }
        }
        // If we found our type definition by name we release the schema scope.
        if (definition != null)
            schemaManager.releaseScope();
    }


    /**
     * Sets the xsi:nil attribute when necessary
     * 
     * @param definition The Schema definition of the expected type. If
     * nillable is strictly enforced, this definition must explicitly
     * specify nillable=true.
     * 
     * @param name The name of the element to be created
     * 
     * @param value The value to check
     * 
     * @return content The element where xsi:nil was set, or null if xsi:nil was
     * not set.
     */
    public function encodeXSINil(definition:XML, name:QName, value:*, isRequired:Boolean = true):XML
    {
        // Check for nillable in the definition only if strictNillability is true.
        // Otherwise assume nillable=true.
        var nillable:Boolean = true;
        if (strictNillability)
        {
            if (definition != null)
                nillable = definition.@nillable.toString() == "true" ? true : false;
            else
            	nillable = false; //XML schema default for nillable
        }
        
        var item:XML;

        // <element fixed="...">
        // Fixed is forbidden when nillable="true". We enforce that only if
        // strictNillability==true. Otherwise we take the fixed value if it
        // is provided.
        var fixedValue:String = getAttributeFromNode("fixed", definition);
        if (!(strictNillability && nillable) && fixedValue != null)
        {
            item = createElement(name);
            setValue(item, schemaManager.marshall(fixedValue, schemaManager.schemaDatatypes.stringQName));
            return item;
        }
        
        // After we are done with fixed, which can replace even a non-null value,
        // we only care about cases where value is null, so we can return otherwise.
        if (value != null)
            return null;

        // <element default="...">
        var defaultValue:String = getAttributeFromNode("default", definition);
        if (value == null && defaultValue != null)
        {
            item = createElement(name);
            setValue(item, schemaManager.marshall(defaultValue, schemaManager.schemaDatatypes.stringQName));
            return item;
        }

        // If null or undefined, and nillable, we set xsi:nil="true"
        // and return the element 
        if (nillable && value === null && isRequired == true)
        {
            item = createElement(name);
        	setValue(item, null);
            return item;
        }

        return null;
    }


    /**
     * @private
     */
    public function getAttribute(parent:*, name:*):*
    {
        return getValue(parent, name);
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
    public function setAttribute(parent:XML, name:*, value:*):void
    {
        if (value != null)
            parent.@[name] = value.toString();
    }

    /**
     * @private
     */
    public function getProperties(value:*):Array
    {
        var classInfo:Object = ObjectUtil.getClassInfo(value as Object, null, {includeReadOnly:false});
        return classInfo.properties;
    }

    /**
     * Returns a single XML node with the given name
     * 
     * @private
     */
    public function createElement(name:*):XML
    {
        var element:XML;
        var elementName:QName;
        if (name is QName)
            elementName = name as QName;
        else
            elementName = new QName("", name.toString());
    
        element = <{elementName.localName} />;
        if (elementName.uri != null && elementName.uri.length > 0)
        {
            var prefix:String = schemaManager.getOrCreatePrefix(elementName.uri);
            var ns:Namespace = new Namespace(prefix, elementName.uri);
            element.setNamespace(ns);
        }
        return element;
    }


    /**
     * @private
     */
    public function getSimpleValue(parent:*, name:*):*
    {
        var simpleValue:* = getValue(parent, name);

        // Support legacy _value property for simpleContent
        if (simpleValue === undefined)
            simpleValue = getValue(parent, "_value");

        return simpleValue;
    }

    /**
     * Determines whether a value should be representable as a single, simple
     * value, otherwise the object is regarded as "complex" and contains
     * child values referenced by index or name.
     * 
     * @private
     */
    public function isSimpleValue(value:*):Boolean
    {
        if (value is String || value is Number || value is Boolean
            || value is Date || value is int || value is uint
            || value is ByteArray)
        {
            return true;
        }

        return false;
    }

    /**
     * @private
     */
    public function getValue(parent:*, name:*):*
    {
        var value:*;
		
		if (parent is XML || parent is XMLList)
        {
            var node:XMLList = parent[name];
            if (node.length() > 0)
                value = node;
        }
        else if (TypeIterator.isIterable(parent))
        {
            // We may have an associative Array
            if (parent.hasOwnProperty(name) && parent[name] !== undefined)
            {
                value = resolveNamedProperty(parent, name);
            }
            else
            {
                // Otherwise, we just return the value as this may be for an
                // ArrayOfSomeType that needs special casing to map directly 
                // to an Array without a wrapper type
                value = parent;
            }
        }
        else if (!isSimpleValue(parent))
        {
            // We only support the public namespace for now
            if (name is QName)
                name = QName(name).localName;

                value = resolveNamedProperty(parent, name);
        }
        else
        {
            // FIXME: Shouldn't this be an error condition?
            value = parent;
        }

        return value;
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
    public function containsNodeByName(list:XMLList, name:QName, strict:Boolean=false):Boolean
    {
        var currentURI:String = schemaManager.currentSchema.targetNamespace.uri;
        for each (var node:XML in list)
        {
            if (strict || (name.uri != "" && name.uri != null))
            {
                // If we need strict comparisons, or if name is qualified, and
                // not in the default namespace, we match the full QName. However,
                // elements already contained in the encoded XMLList could be
                // unqualified, so to match them against a qualified name, we use
                // the target namespace of the current schema used in encoding,
                // which will be the namespace the unqualified elements assume.
                if (node.name().uri == "" && currentURI == name.uri)
                {
                    //compare by localName
                    if (node.name().localName == name.localName)
                        return true;
                }
                else if (node.name() == name)
                {
                    return true;
                }
            }
            else
            {
                // If we only have a localName, and don't need strict comparisons,
                // look for any node with that localName, regardless of namespace.
                if (node.name().localName == name.localName)
                    return true;
            }
        }
        return false;
    }


    /**
     * Looks up value by name on a complex parent object, considering that the
     * name might have to be prepended with an underscore.
     * @private
     */
    public function resolveNamedProperty(parent:*, name:*):*
    {
        var value:*;
        var fallbackName:String = null;

        if (!isSimpleValue(parent))
        {
            try
            {
                value = parent[name];
                // If a value by this name is not defined on a dynamic object,
                // try looking up with an underscore.
                if (value === undefined)
                    fallbackName = "_" + name.toString();
            }
            catch (e:Error)
            {
                // If a property with that name doesn't exist on a non-dynamic
                // object, an error will be thrown. We should still try the
                // fallback name.
                fallbackName = "_" + name.toString();
                
            }

            if (fallbackName != null && parent.hasOwnProperty(fallbackName))
                value = parent[fallbackName];
        }

        return value;
    }



    /**
     * Assigns value to an XML node.
     * 
     * @param parent The node to assign to. Must be either XML or XMLList.
     * If XMLList, it must contain at least one XML element. The value is
     * assigned on the last element in the list. If XML, the value is assigned
     * directly on parent.
     * @param value The value to assign on the parent. If null, the xsi:nil
     * attribute is set on the parent. If XML or XMLList, the value is appended
     * as child node(s) on the parent. Otherwise the string representation of the
     * value is appended as a text node. A value that is explicitly undefined is
     * skipped.
     * 
     * @private
     */
    public function setValue(parent:*, value:*):void
    {
        
        if (value !== undefined)
        {
            var currentChild:XML;
            if (parent is XML)
                currentChild = parent as XML;
            else if (parent is XMLList && parent.length() > 0)
                currentChild = parent[parent.length()-1];
                
            if (currentChild != null)
            {
                if (value === null)
                {
                	// set xsi:nil attribute if value is specifically null.
                    currentChild.@[schemaManager.schemaConstants.nilQName] = "true";
                    currentChild.addNamespace(constants.xsiNamespace);
                }
                else if (value is XML || value is XMLList)
                {
                    currentChild.appendChild(value);
                }
                else if (value !== undefined)
                {   
                    // Everything else is treated as simple content, except
                    // for undefined, which is skipped.
                    currentChild.appendChild(xmlSpecialCharsFilter(Object(value)));
                }
            }
        }
    }

    /**
     * Appends a value (or list of values) directly as
     * members of the parent XMLList. Effectively merges
     * two XMLLists.
     * 
     * @private
     */
    public function appendValue(parent:XMLList, value:*):void
    {
        parent[parent.length()] = value;
    }

    /**
     * Checks to see whether a value defines a custom XSI type to be used
     * during encoding, otherwise the default type is returned.
     */
    protected function getXSIType(value:*):QName
    {
        var xsiType:QName;

        // Allow IXMLSchemaInstance or ObjectProxy to override XSI type
        // information, if provided...
        if (value != null)
        {
            if (value is ObjectProxy && value.object_proxy::type != null)
            {
                xsiType = value.object_proxy::type;
            }
            else if (value is IXMLSchemaInstance && IXMLSchemaInstance(value).xsiType != null)
            {
                xsiType = IXMLSchemaInstance(value).xsiType;
            }
        }

        return xsiType;
    }

    /**
     * Record custom XSI type information for this XML node by adding an
     * xsi:type attribute with the value set to the qualified type name.
     */
    protected function setXSIType(parent:XML, type:QName):void
    {
        var namespaceURI:String = type.uri;
        var prefix:String = schemaManager.getOrCreatePrefix(namespaceURI);
        var prefixNamespace:Namespace = new Namespace(prefix, namespaceURI);
        parent.addNamespace(prefixNamespace);
        parent.@[constants.getXSIToken(constants.typeAttrQName)] = prefix + ":" + type.localName;
    }
    
    /**
     * @private
     */
    protected function deriveXSIType(parent:XML, type:QName, value:*):void
    {
    }
    
    /**
     * @private
     * Default implementation of xmlSpecialCharsFilter. Escapes "&" and "<".
     */
    private function escapeXML(value:Object):String
    {
        var str:String = value.toString();
        str = str.replace(/&/g, "&amp;").replace(/</g, "&lt;");
        return str;
    }
    
    
    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * 
     */
    public function get strictNillability():Boolean
    {
        return _strictNillability;
    }

    /**
     * 
     */
    public function set strictNillability(strict:Boolean):void
    {
        _strictNillability = strict;
    }


    /**
     * Function to be used for escaping XML special characters in simple content.
     * Returns default implementation in this class.
     */
    public function get xmlSpecialCharsFilter():Function
    {
        return _xmlSpecialCharsFilter;
    }

    /**
     * 
     */
    public function set xmlSpecialCharsFilter(func:Function):void
    {
        if (func != null)
            _xmlSpecialCharsFilter = func;
        else
            // If setting to null, we revert to built-in default.
            _xmlSpecialCharsFilter = escapeXML;
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    private var _strictNillability:Boolean = false;
    private var _xmlSpecialCharsFilter:Function = escapeXML;

}

}
