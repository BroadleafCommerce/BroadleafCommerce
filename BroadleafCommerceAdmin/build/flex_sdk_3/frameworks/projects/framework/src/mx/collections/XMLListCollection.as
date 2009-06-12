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

package mx.collections
{

[DefaultProperty("source")]

/**
 *  The XMLListCollection class provides collection functionality to
 *  an XMLList object and makes available some of the methods of
 *  the native XMLList class.
 *
 *  @mxml
 * 
 *  <p>The <code>&lt;mx:XMLListCollection&gt;</code> tag inherits all
 *  the attributes of its superclass, and adds the following attributes:</p>
 *
 *  <pre>
 *  &lt;mx:XMLListCollection
 *  <b>Properties</b>
 *  source="null"
 *  /&gt;
 *  </pre>
 * 
 *  @see XMLList
 *  @see XML
 */
public class XMLListCollection extends ListCollectionView
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     *  Constructor.
     *
     *  <p>Creates a new XMLListCollection object
     *  using the specified XMLList object.</p>
     * 
     *  @param source The XMLList object containing the data to be represented
     *                by the XMLListCollection object.
     */
    public function XMLListCollection(source:XMLList = null)
    {
        super();

        this.source = source;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  source
    //----------------------------------

    [Inspectable(category="General")]
    [Bindable("listChanged")] //superclass will fire this
    
    /**
     *  The underlying XMLList for this collection.
     *  The XMLListCollection object does not represent any changes that
     *  you make directly to the source XMLList object.
     *  Always use the XMLListCollection methods to modify the collection.
     */
    public function get source():XMLList
    {
        return list ? XMLListAdapter(list).source : null;
    }

    /**
     *  @private
     */
    public function set source(s:XMLList):void
    {
        if (list)
            XMLListAdapter(list).source = null;
        
        list = new XMLListAdapter(s);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Calls the <code>attribute()</code> method of each XML object in the
     *  XMLList and returns an XMLList of the results, which
     *  match the given <code>attributeName</code>.
     *
     *  @param attributeName The attribute that you want to match in the XML
     *                       objects of the XMLList.
     *
     *  @return The XMLList of matching XML objects.
     *
     *  @see XML#attribute()
     */
    public function attribute(attributeName:Object):XMLList
    {
        return execXMLListFunction(
            function(obj:Object):XMLList
            {
                return obj.attribute(attributeName);
            }
        );
    }

    /**
     *  Calls the <code>attributes()</code> method of each XML object in the
     *  XMLList object and returns an XMList of attributes for each XML object.
     *
     *  @return The XMLList containing the resulting XML objects, listing
     *          the attributes.
     *
     *  @see XML#attributes()
     */
    public function attributes():XMLList
    {
        return execXMLListFunction(
            function(obj:Object):XMLList
            {
                return obj.attributes();
            }
        );
    }

    /**
     *  Calls the <code>child()</code> method of each XML object in the XMLList
     *  and returns an XMLList containing the children of with the specified property
     *  name, in order.
     *
     *  @param propertyName The propery to match.
     *
     *  @return An XMLList of matching children of the XML objects in the
     *          original XMLList.
     *
     *  @see XML#child()
     */
    public function child(propertyName:Object):XMLList
    {
        return execXMLListFunction(
            function(obj:Object):XMLList
            {
                return obj.child(propertyName);
            }
        );
    }

    /**
     *  Calls the children() method of each XML object in the XMLList and
     *  returns an XMLList containing the results.
     *
     *  @return An XMLList of children of the XML objects in the original XMLList.
     *
     *  @see XML#children()
     */
    public function children():XMLList
    {
        return execXMLListFunction(
            function(obj:Object):XMLList
            {
                return obj.children();
            }
        );
    }

    /**
     *  Returns a deep copy of the XMLList object.
     *
     *  @return The copy of the XMLList object.
     */
    public function copy():XMLList
    {
        return execXMLListFunction(
            function(obj:Object):XMLList
            {
                // sometimes the obj can be of type XML
                return XMLList(obj.copy());
            }
        );
    }

    /**
     *  Calls the <code>descendants()</code> method of each XML object in the
     *  XMLList and returns an XMLList containing the results.
     *  The <code>name</code> parameter is passed to the XML object's 
     *  <code>descendants()</code> method. 
     *  If you do not specify a parameter, the string "~~" is passed to the
     *  <code>descendants()</code> method.
     *
     *  @param name The name of the elements to match.
     *
     *  @return XMLList of the matching descendents (children, grandchildren,
     *  etc.) of the XML objects in the original XMLList.
     *
     *  @see XML#descendants()
     */
    public function descendants(name:Object="*"):XMLList
    {
        return execXMLListFunction(
            function(obj:Object):XMLList
            {
                return obj.descendants(name);
            }
        );
    }

    /**
     *  Calls the <code>elements()</code> method of each XML object in the XMLList.
     *  The <code>name</code> parameter is passed to the XML object's 
     *  <code>elements()</code> method. 
     *  If you do not specify a parameter, the string "~~" is passed to the
     *  <code>elements()</code> method.
     *
     *  @param name The name of the elements to match.
     *
     *  @return XMLList of the matching child elements of the XML objects in the
     *          original XMLList.
     *
     *  @see XML#elements()
     */
    public function elements(name:String="*"):XMLList
    {
        return execXMLListFunction(
            function(obj:Object):XMLList
            {
                return obj.elements(name);
            }
        );
    }

    /**
     * Calls the <code>text()</code> method of each XML object in
     *  the XMLList and returns an XMLList containing the results.
     *  
     *  @return The XMLList that contains the result.
     *
     *  @see XML#text()
     */
    public function text():XMLList
    {
        return execXMLListFunction(
            function(obj:Object):XMLList
            {
                return obj.text();
            }
        );
    }

    /**
     *  Returns a string representation of the XMLList by calling the
     *  <code>toString()</code> method for each XML object in the XMLList.
     *  If the <code>prettyPrinting</code> property of the <code>XML</code>
     *  class is set to <code>true</code>, then the results for each XML object
     *  in the XMLList are separated by the return character.
     *  Otherwise, if <code>prettyPrinting</code> is set to <code>false</code>,
     *  then the results are simply concatenated, without separating return characters.
     *
     *  <p>The <code>toString()</code> method strips out the following
     *  in the returned string for an XML object that has simple content:
     *  the start tag, attributes, namespace declarations, and the end tag.
     *  Use the <code>toXMLString()</code> method if you want to preserve these
     *  in the returned string for XML objects with simple content.</p>
     * 
     *  @return The string representation of the XMLList object.
     *  
     *  @see XML#prettyPrinting
     */
    override public function toString():String
    {
        if (!localIndex)
        {
            return source.toString();
        }
        else
        {
            var str:String = "";

            for (var i:int = 0; i < localIndex.length; i++)
            {
                if (i > 0)
                    str += "\n"; // this matches how XML works

                str += localIndex[i].toString();
            }

            return str;
        }
    }

    /**
     *  Returns a string representation of the XMLList by calling the
     *  <code>toXMLString()</code> method for each XML object in the XMLList.
     *  If the <code>prettyPrinting</code> property of the <code>XML</code>
     *  class is set to <code>true</code>, then the results for each XML object
     *  in the XMLList are separated by the return character.
     *  Otherwise, if <code>prettyPrinting</code> is set to <code>false</code>,
     *  then the results are concatenated, without separating return
     *  characters.
     *
     *  <p>The <code>toXMLString()</code> method preserves the
     *  following in the returned string for an XML object that has simple content:
     *  the start tag, attributes, namespace declarations, and the end tag.
     *  Use the <code>toString()</code> method if you want to strip these from
     *  the returned string for XML objects with simple content.</p>
     *
     *  @return The string representation of the XMLList.
     *
     *  @see XML#prettyPrinting
     */
    public function toXMLString():String
    {
        if (!localIndex)
        {
            return source.toXMLString();
        }
        else
        {
            var str:String = "";

            for (var i:int = 0; i < localIndex.length; i++)
            {
                if (i > 0)
                    str += "\n"; // this matches how XML works

                str += localIndex[i].toXMLString();
            }

            return str;
        }
    }

    //--------------------------------------------------------------------------
    //
    // Internal methods
    //
    //--------------------------------------------------------------------------

    /**
     * XMLList doesn't allow you to do myList[funcName](arg) because
     * it will attempt to lookup a node named funcName instead (despite
     * the fact that it should be a callProperty()).  So instead
     * the above methods will build little functions that call the
     * right thing, but the looping logic has been factored into here.
     */
    private function execXMLListFunction(func:Function):XMLList
    {
        if (!localIndex)
        {
            return func(source);
        }
        else
        {
            //create an XML object that will be a host for all the results
            //go through each child in the order specified by the index
            //and append the XMLList that results from a call on it to the
            //return object.
            var length:int = localIndex.length;
            var ret:XMLList = <></>;
            for (var i:int = 0; i < length; i++)
            {
                var xml:Object = localIndex[i];
                ret += func(xml);
            }
            return ret;
        }
    }

}

}
