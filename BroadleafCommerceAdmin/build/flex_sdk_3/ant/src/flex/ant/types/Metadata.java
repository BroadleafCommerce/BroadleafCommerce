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

package flex.ant.types;

import flex.ant.config.ConfigString;
import flex.ant.config.NestedAttributeElement;
import flex.ant.config.OptionSpec;
import flex.ant.config.OptionSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicElement;
import org.apache.tools.ant.types.Commandline;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public final class Metadata implements OptionSource, DynamicElement
{
    private static OptionSpec ldSpec = new OptionSpec("metadata", "localized-description");
    private static OptionSpec ltSpec = new OptionSpec("metadata", "localized-title");

    private static OptionSpec coSpec = new OptionSpec("metadata", "contributor");
    private static OptionSpec crSpec = new OptionSpec("metadata", "creator");
    private static OptionSpec laSpec = new OptionSpec("metadata", "language");
    private static OptionSpec puSpec = new OptionSpec("metadata", "publisher");

    private final ConfigString date;
    private final ConfigString description;
    private final ConfigString title;

    private final ArrayList nestedAttribs;

    public Metadata ()
    {
        date = new ConfigString(new OptionSpec("metadata", "date"));
        description = new ConfigString(new OptionSpec("metadata", "description"));
        title = new ConfigString(new OptionSpec("metadata", "title"));

        nestedAttribs = new ArrayList();
    }

    /*=======================================================================*
     *  Attributes                                                           *
     *=======================================================================*/

    public void setDate(String value)
    {
        date.set(value);
    }

    public void setDescription(String value)
    {
        description.set(value);
    }

    public void setTitle(String value)
    {
        title.set(value);
    }

    /*=======================================================================*
     *  Nested Elements
     *=======================================================================*/

    public NestedAttributeElement createContributor()
    {
        return createElem("name", coSpec);
    }

    public NestedAttributeElement createCreator()
    {
        return createElem("name", crSpec);
    }

    public NestedAttributeElement createLanguage()
    {
        return createElem("code", laSpec);
    }

    public NestedAttributeElement createPublisher()
    {
        return createElem("name", puSpec);
    }

    public Object createDynamicElement(String name)
    {
        /*
         * Name is checked against getAlias() because both of these options
         * have prefixes. We don't want to allow something like:
         *
         * <metadata>
         *   <metadata.localized-title title="foo" lang="en" />
         * </metadata>
         */
        if (ldSpec.matches(name)) {
            return createElem(new String[] { "text", "lang" }, ldSpec);
        }
        else if (ltSpec.matches(name)) {
            return createElem(new String[] { "title", "lang" }, ltSpec);
        }
        else {
            throw new BuildException("Invalid element: " + name);
        }
    }

    private NestedAttributeElement createElem(String attrib, OptionSpec spec)
    {
        NestedAttributeElement e = new NestedAttributeElement(attrib, spec);
        nestedAttribs.add(e);
        return e;
    }

    private NestedAttributeElement createElem(String[] attribs, OptionSpec spec)
    {
        NestedAttributeElement e = new NestedAttributeElement(attribs, spec);
        nestedAttribs.add(e);
        return e;
    }

    /*=======================================================================*
     *  OptionSource interface                                               *
     *=======================================================================*/

    public void addToCommandline(Commandline cmdl)
    {
        date.addToCommandline(cmdl);
        description.addToCommandline(cmdl);
        title.addToCommandline(cmdl);

        Iterator it = nestedAttribs.iterator();

        while (it.hasNext())
            ((OptionSource) it.next()).addToCommandline(cmdl);
    }

} //End of Metadata
